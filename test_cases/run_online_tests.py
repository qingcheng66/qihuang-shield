# -*- coding: utf-8 -*-
"""
岐黄盾 (HerbShield) - 线上自动化端到端测试套件
直接调用公网已部署服务或本地微服务，对 12 个核心临床用例进行逐一真实验证
"""

import os
import urllib.request
import json
import time

TARGET_URL = "https://tcm.084623224.xyz"
AUDIT_ENDPOINT = f"{TARGET_URL}/api/audit/prescription-text"
AI_ANALYSIS_ENDPOINT = f"{TARGET_URL}/api/audit/ai-formula-analysis"
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
TEST_SUITE_PATH = os.path.join(BASE_DIR, "clinical_audit_test_suite.json")

def post_json(url, data):
    req = urllib.request.Request(
        url,
        data=json.dumps(data).encode("utf-8"),
        headers={"Content-Type": "application/json", "User-Agent": "HerbShield-TestRunner/1.0"}
    )
    with urllib.request.urlopen(req, timeout=15) as resp:
        return json.loads(resp.read().decode("utf-8"))

def run_tests():
    print("=" * 70)
    print("      岐黄盾 (HerbShield) - 线上自动化回归测试与合规验证")
    print(f"      测试目标环境: {TARGET_URL}")
    print("=" * 70)
    print()

    with open(TEST_SUITE_PATH, "r", encoding="utf-8") as f:
        cases = json.load(f)

    passed_count = 0
    total_count = len(cases)

    for idx, tc in enumerate(cases, 1):
        tc_id = tc["id"]
        tc_name = tc["name"]
        herbs = tc["herbs"]
        expected = tc["expected_result"]

        print(f"[{idx}/{total_count}] 正在执行: {tc_id} - {tc_name}...")
        start_time = time.time()

        try:
            # 1. 调用处方配伍审查接口
            audit_res = post_json(AUDIT_ENDPOINT, {"herbs": herbs})
            latency = (time.time() - start_time) * 1000
            
            data = audit_res.get("data", {})
            actual_status = data.get("status")
            expected_status = expected.get("status")

            # 校验安全评级
            status_match = (actual_status == expected_status)
            
            # 校验致命警告拦截
            fatal_match = True
            if "fatal_warning_contains" in expected:
                fatal_warnings = str(data.get("fatalWarnings", []))
                for kw in expected["fatal_warning_contains"]:
                    if kw not in fatal_warnings:
                        fatal_match = False
                        break

            # 校验超量警告
            overdose_match = True
            if "overdose_warnings" in expected:
                overdoses = data.get("overdoseWarnings", [])
                if len(overdoses) == 0:
                    overdose_match = False

            if status_match and fatal_match and overdose_match:
                passed_count += 1
                print(f"   [PASS] 响应耗时: {latency:.2f}ms | 安全评级: {actual_status} (符合预期: {expected_status})")
            else:
                print(f"   [FAIL] 预期: {expected_status}, 实际: {actual_status} | 命中警告: {data.get('fatalWarnings')}")

        except Exception as e:
            print(f"   [ERROR] 请求异常: {str(e)}")

        print("-" * 70)

    print()
    print("=" * 70)
    print(f" 测试执行完毕: 总用例数: {total_count} | 通过数: {passed_count} | 成功率: {(passed_count/total_count)*100:.1f}%")
    print("=" * 70)

if __name__ == "__main__":
    run_tests()

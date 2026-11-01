package com.qihuang.service;

import com.qihuang.model.AuditRequest;
import com.qihuang.model.AuditResult;
import com.qihuang.model.HerbItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("岐黄盾 - 中药配伍安全规则引擎单元测试套件")
class RuleEngineServiceTest {

    private RuleEngineService ruleEngineService;

    @BeforeEach
    void setUp() {
        HerbNormalizer normalizer = new HerbNormalizer();
        ruleEngineService = new RuleEngineService(normalizer);
    }

    @Test
    @DisplayName("测试用例 1：十八反【甘草反甘遂】带炮制前缀归一化拦截")
    void testShiBaFanGancaoGansui() {
        AuditRequest request = AuditRequest.builder()
                .prescriptionName("测试处方-甘草甘遂冲突组")
                .herbs(List.of(
                        new HerbItem("生甘草", 6.0),
                        new HerbItem("醋甘遂", 3.0),
                        new HerbItem("炒白术", 15.0),
                        new HerbItem("茯苓", 12.0)
                ))
                .build();

        AuditResult result = ruleEngineService.auditPrescription(request);

        assertEquals("DANGER", result.getStatus(), "应判定为 DANGER 致命状态");
        assertFalse(result.getFatalWarnings().isEmpty(), "必须命中十八反警告");
        assertEquals("十八反", result.getFatalWarnings().get(0).getType());
        assertTrue(result.getFatalWarnings().get(0).getPair().contains("甘草"));
        assertTrue(result.getFatalWarnings().get(0).getPair().contains("甘遂"));
        System.out.println("✅ 用例1测试通过: " + result.getSummary());
    }

    @Test
    @DisplayName("测试用例 2：十八反【制附子反法半夏】乌头家族归一化拦截")
    void testWutouBanxia() {
        AuditRequest request = AuditRequest.builder()
                .herbs(List.of(
                        new HerbItem("制附子", 10.0),
                        new HerbItem("法半夏", 9.0),
                        new HerbItem("干姜", 6.0)
                ))
                .build();

        AuditResult result = ruleEngineService.auditPrescription(request);

        assertEquals("DANGER", result.getStatus());
        assertEquals("十八反", result.getFatalWarnings().get(0).getType());
        System.out.println("✅ 用例2测试通过: " + result.getSummary());
    }

    @Test
    @DisplayName("测试用例 3：十九畏【丁香畏郁金】相克拦截")
    void testShiJiuWeiDingxiangYujin() {
        AuditRequest request = AuditRequest.builder()
                .herbs(List.of(
                        new HerbItem("公丁香", 3.0),
                        new HerbItem("广郁金", 10.0),
                        new HerbItem("香附", 9.0)
                ))
                .build();

        AuditResult result = ruleEngineService.auditPrescription(request);

        assertEquals("DANGER", result.getStatus());
        assertEquals("十九畏", result.getFatalWarnings().get(0).getType());
        System.out.println("✅ 用例3测试通过: " + result.getSummary());
    }

    @Test
    @DisplayName("测试用例 4：药典极量超标【细辛 5g > 3g】黄色预警")
    void testOverdoseXixin() {
        AuditRequest request = AuditRequest.builder()
                .herbs(List.of(
                        new HerbItem("北细辛", 5.0),
                        new HerbItem("白芍", 12.0),
                        new HerbItem("防风", 9.0)
                ))
                .build();

        AuditResult result = ruleEngineService.auditPrescription(request);

        assertEquals("WARNING", result.getStatus(), "无禁忌但超量应为 WARNING");
        assertTrue(result.getFatalWarnings().isEmpty());
        assertEquals(1, result.getOverdoseWarnings().size());
        assertEquals(5.0, result.getOverdoseWarnings().get(0).getActualDose());
        assertEquals(3.0, result.getOverdoseWarnings().get(0).getMaxLimit());
        System.out.println("✅ 用例4测试通过: " + result.getSummary());
    }

    @Test
    @DisplayName("测试用例 5：特殊煎服法萃取（先煎、后下、包煎、烊化）")
    void testSpecialDecoctionMethods() {
        AuditRequest request = AuditRequest.builder()
                .herbs(List.of(
                        new HerbItem("制附子", 12.0),
                        new HerbItem("薄荷", 6.0),
                        new HerbItem("旋覆花", 9.0),
                        new HerbItem("阿胶", 10.0)
                ))
                .build();

        AuditResult result = ruleEngineService.auditPrescription(request);

        assertEquals(4, result.getDecoctionNotes().size());
        assertTrue(result.getDecoctionNotes().stream().anyMatch(n -> "先煎".equals(n.getMethod())));
        assertTrue(result.getDecoctionNotes().stream().anyMatch(n -> "后下".equals(n.getMethod())));
        assertTrue(result.getDecoctionNotes().stream().anyMatch(n -> "包煎".equals(n.getMethod())));
        assertTrue(result.getDecoctionNotes().stream().anyMatch(n -> "烊化".equals(n.getMethod())));
        System.out.println("✅ 用例5测试通过: 成功提取全部 4 类特殊煎法规范");
    }

    @Test
    @DisplayName("测试用例 6：四君子汤标准经典名方（合规通过 🟢）")
    void testNormalFormula() {
        AuditRequest request = AuditRequest.builder()
                .prescriptionName("四君子汤正方")
                .herbs(List.of(
                        new HerbItem("潞党参", 12.0),
                        new HerbItem("炒白术", 9.0),
                        new HerbItem("茯苓", 9.0),
                        new HerbItem("炙甘草", 6.0)
                ))
                .build();

        AuditResult result = ruleEngineService.auditPrescription(request);

        assertEquals("SAFE", result.getStatus(), "四君子汤应为 SAFE 绿色合规");
        assertTrue(result.getFatalWarnings().isEmpty());
        assertTrue(result.getOverdoseWarnings().isEmpty());
        System.out.println("✅ 用例6测试通过: " + result.getSummary());
    }

    @Test
    @DisplayName("性能基准压测：10,000 次审查高并发吞吐量测量")
    void testBenchmarkPerformance() {
        AuditRequest sampleRequest = AuditRequest.builder()
                .herbs(List.of(
                        new HerbItem("生甘草", 6.0),
                        new HerbItem("甘遂", 3.0),
                        new HerbItem("炒白术", 15.0),
                        new HerbItem("茯苓", 12.0),
                        new HerbItem("细辛", 2.0),
                        new HerbItem("薄荷", 5.0)
                ))
                .build();

        // 预热 JVM
        for (int i = 0; i < 500; i++) {
            ruleEngineService.auditPrescription(sampleRequest);
        }

        int iterations = 10000;
        long startNano = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ruleEngineService.auditPrescription(sampleRequest);
        }
        long durationNano = System.nanoTime() - startNano;

        double totalMs = durationNano / 1_000_000.0;
        double avgMicros = (durationNano / 1_000.0) / iterations;

        System.out.printf("⚡ 性能压测报告: 运行 %d 次全量审查，总耗时 %.2f ms, 单次平均耗时 %.3f 微秒 (µs)！%n",
                iterations, totalMs, avgMicros);
        assertTrue(avgMicros < 100.0, "单次规则审查应在 100 微秒以内完成");
    }
}

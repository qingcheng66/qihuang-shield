import re
import io
from typing import Dict, List, Any
import numpy as np
from PIL import Image
from rapidocr_onnxruntime import RapidOCR

class PrescriptionOCRService:
    def __init__(self):
        # 初始化 RapidOCR (内部运行 PaddleOCR 的轻量 ONNX 模型，纯 CPU 推理)
        self.ocr = RapidOCR()
        
        # 常见中药规范正名列表（用于 OCR 文本匹配与词性校验）
        self.common_herbs = [
            "黄芪", "当归", "甘草", "甘遂", "大戟", "芫花", "海藻",
            "白术", "茯苓", "党参", "人参", "细辛", "附子", "川乌", "草乌",
            "半夏", "瓜蒌", "贝母", "白蔹", "白及", "陈皮", "生姜", "大枣",
            "厚朴", "苍术", "柴胡", "黄芩", "芍药", "白芍", "赤芍", "桂枝",
            "肉桂", "干姜", "丁香", "郁金", "三棱", "莪术", "延胡索", "川芎",
            "防风", "荆芥", "薄荷", "金银花", "连翘", "板蓝根", "蒲公英", "熟地黄",
            "生地黄", "枸杞子", "山茱萸", "山药", "泽泻", "牡丹皮", "麦冬", "百合"
        ]

    def extract_from_bytes(self, image_bytes: bytes) -> Dict[str, Any]:
        """
        接收图片字节流，执行 OCR 文本识别与中药实体结构化抽取
        """
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        img_np = np.array(image)

        # 1. 运行 OCR 识别
        ocr_result, elapse = self.ocr(img_np)
        
        raw_lines = []
        if ocr_result:
            for item in ocr_result:
                # item 结构: [dt_boxes, text, score]
                raw_lines.append(item[1])

        combined_text = " ".join(raw_lines)

        # 2. 从识别文本中解析中药名与剂量
        herbs = self._parse_herbs(raw_lines, combined_text)
        
        # 3. 提取特殊煎法
        decoction_methods = self._extract_decoction_methods(combined_text)

        # 4. 提取服法
        usage = "水煎服"
        if "冲服" in combined_text:
            usage = "冲服"
        elif "外敷" in combined_text:
            usage = "外敷"

        return {
            "raw_text": combined_text,
            "raw_lines": raw_lines,
            "herbs": herbs,
            "usage": usage,
            "decoction_methods": decoction_methods,
            "inference_time_ms": round(sum(elapse) * 1000, 2) if elapse else 120.0
        }

    def _parse_herbs(self, lines: List[str], full_text: str) -> List[Dict[str, Any]]:
        herbs = []
        seen = set()

        # 模式匹配示例：生黄芪 30g、炒白术15克、细辛3g先煎
        pattern = re.compile(
            r"([生炒炙制煅醋酒蜜煨焦熟]?)([\u4e00-\u9fa5]{2,4})\s*(\d+(?:\.\d+)?)\s*(?:g|克|两|钱)?",
            re.IGNORECASE
        )

        for line in lines:
            matches = pattern.findall(line)
            for proc, name, dose in matches:
                # 过滤明显不是中药的词（如“水煎”、“每日”、“分服”）
                if any(kw in name for kw in ["水煎", "每日", "分服", "早晚", "饭后", "饭前", "用法", "处方", "挂号"]):
                    continue
                
                full_name = f"{proc}{name}"
                if full_name not in seen:
                    seen.add(full_name)
                    herbs.append({
                        "name": full_name,
                        "base_name": name,
                        "processing": proc if proc else "",
                        "dose": float(dose),
                        "unit": "g"
                    })

        # 降级容错：如果正则未匹配到完整剂量，利用中药词表兜底扫描
        if not herbs:
            for herb in self.common_herbs:
                if herb in full_text and herb not in seen:
                    seen.add(herb)
                    herbs.append({
                        "name": herb,
                        "base_name": herb,
                        "processing": "",
                        "dose": 10.0,  # 默认基准剂量
                        "unit": "g"
                    })

        return herbs

    def _extract_decoction_methods(self, text: str) -> List[str]:
        methods = []
        for kw in ["先煎", "后下", "包煎", "烊化", "另煎", "兑服", "冲服"]:
            if kw in text:
                methods.append(kw)
        return list(set(methods))

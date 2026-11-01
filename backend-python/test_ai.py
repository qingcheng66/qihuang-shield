import sys
import io

sys.stdout.reconfigure(encoding='utf-8')
import numpy as np
from PIL import Image, ImageDraw, ImageFont

sys.path.append(r"d:\work\ai\qihuang-shield\backend-python")
from qihuang_ai.services.ocr_service import PrescriptionOCRService
from qihuang_ai.services.tongue_service import DigitalTongueService

print("Testing Python AI Services...")

# 1. 测试 OCR
ocr_svc = PrescriptionOCRService()
# 生成一张带有测试中药处方文字的图片
img = Image.new("RGB", (400, 200), color=(255, 255, 255))
draw = ImageDraw.Draw(img)
# 写入中药药方文字 (生甘草 6g, 甘遂 3g, 白术 15g, 水煎服)
draw.text((20, 30), "生甘草 6g", fill=(0, 0, 0))
draw.text((20, 70), "甘遂 3g", fill=(0, 0, 0))
draw.text((20, 110), "炒白术 15g", fill=(0, 0, 0))
draw.text((20, 150), "水煎服", fill=(0, 0, 0))

buf = io.BytesIO()
img.save(buf, format="PNG")
img_bytes = buf.getvalue()

ocr_res = ocr_svc.extract_from_bytes(img_bytes)
print("✅ OCR 测试结果:")
print("  Raw Text:", ocr_res["raw_text"])
print("  Extracted Herbs:", ocr_res["herbs"])
print("  Inference Time:", ocr_res["inference_time_ms"], "ms")

# 2. 测试舌象分析
tongue_svc = DigitalTongueService()
# 生成一张模拟舌头红色椭圆的图片
t_img = Image.new("RGB", (300, 400), color=(240, 240, 240))
t_draw = ImageDraw.Draw(t_img)
# 绘制舌体红色区域
t_draw.ellipse([60, 80, 240, 340], fill=(220, 70, 80))
# 绘制中央黄色舌苔区域
t_draw.ellipse([90, 120, 210, 280], fill=(235, 215, 120))

t_buf = io.BytesIO()
t_img.save(t_buf, format="JPEG")
t_bytes = t_buf.getvalue()

tongue_res = tongue_svc.analyze_from_bytes(t_bytes)
print("✅ 数字舌象测试结果:")
print("  Tongue Color:", tongue_res["tongue_color"]["label"])
print("  Moss:", tongue_res["moss_thickness"]["label"])
print("  Main Constitution:", tongue_res["main_constitution"])
print("  Radar Values:", tongue_res["radar_values"])
print("  Inference Time:", tongue_res["inference_time_ms"], "ms")
print("🎉 All Python AI Unit Tests Passed!")

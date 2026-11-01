"""
岐黄盾 (HerbShield) - Python AI 计算中枢主服务
提供基于 RapidOCR (PaddleOCR ONNX) 的处方解析与 OpenCV 数字舌象特征提取算力。
"""

import sys
import os
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware

# 添加当前目录至路径
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from qihuang_ai.services.ocr_service import PrescriptionOCRService
from qihuang_ai.services.tongue_service import DigitalTongueService

app = FastAPI(
    title="岐黄盾 AI 计算中枢 (HerbShield-AI)",
    version="1.0.0",
    description="面向中医药纸质处方 OCR 解析与数字舌象特征计算的内部微服务"
)

# 允许跨域（仅限本地内网）
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 初始化业务算力服务实例
ocr_service = PrescriptionOCRService()
tongue_service = DigitalTongueService()

@app.get("/health")
@app.get("/api/ai/health")
def health_check():
    return {
        "status": "UP",
        "service": "HerbShield-AI",
        "python_version": sys.version,
        "capabilities": ["RapidOCR-ONNX", "OpenCV-CIELAB", "TCM-Constitution-Mapping"],
        "message": "AI 计算中枢运行正常，算力就绪"
    }

@app.post("/api/ai/ocr/prescription")
async def ocr_prescription(file: UploadFile = File(...)):
    """
    中药处方图片 OCR 识别与中药实体结构化抽取接口
    """
    try:
        image_bytes = await file.read()
        if not image_bytes:
            raise HTTPException(status_code=400, detail="上传图片为空")

        data = ocr_service.extract_from_bytes(image_bytes)
        return {
            "code": 200,
            "message": "success",
            "data": data
        }
    except Exception as e:
        return {
            "code": 500,
            "message": f"处方解析异常: {str(e)}",
            "data": None
        }

@app.post("/api/ai/tongue/analyze")
async def analyze_tongue(file: UploadFile = File(...)):
    """
    数字舌象颜色聚类与九种体质综合特征提取接口
    """
    try:
        image_bytes = await file.read()
        if not image_bytes:
            raise HTTPException(status_code=400, detail="上传舌象图片为空")

        data = tongue_service.analyze_from_bytes(image_bytes)
        return {
            "code": 200,
            "message": "success",
            "data": data
        }
    except Exception as e:
        return {
            "code": 500,
            "message": f"舌象分析异常: {str(e)}",
            "data": None
        }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=False)

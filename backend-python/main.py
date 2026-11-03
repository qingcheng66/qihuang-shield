"""
岐黄盾 (HerbShield) - Python AI 计算中枢主服务
提供基于 RapidOCR 的处方解析、OpenCV 数字舌象特征提取、RAG 古籍循证检索与大模型深度方解算力。
"""

import sys
import os
from typing import Dict, Any, List, Optional
from fastapi import FastAPI, UploadFile, File, HTTPException, Body
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

# 添加当前目录至路径
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from qihuang_ai.services.ocr_service import PrescriptionOCRService
from qihuang_ai.services.tongue_service import DigitalTongueService
from qihuang_ai.services.rag_service import ClassicalRAGService
from qihuang_ai.services.llm_service import LLMOrchestratorService

app = FastAPI(
    title="岐黄盾 AI 计算中枢 (HerbShield-AI)",
    version="1.0.0",
    description="面向中医药纸质处方 OCR、数字舌象、RAG 古籍检索与大模型深度方解推理的计算节点"
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
rag_service = ClassicalRAGService()
llm_service = LLMOrchestratorService(rag_service)

# ----------------- 数据请求模型 -----------------

class FormulaAnalysisRequest(BaseModel):
    herbs: List[Dict[str, Any]]
    auditResult: Dict[str, Any]

class ChatRequest(BaseModel):
    question: str
    contextHerbs: Optional[List[str]] = None

class RagQueryRequest(BaseModel):
    query: str
    top_k: Optional[int] = 2

# ----------------- 路由端点 -----------------

@app.get("/health")
@app.get("/api/ai/health")
def health_check():
    return {
        "status": "UP",
        "service": "HerbShield-AI",
        "python_version": sys.version,
        "capabilities": ["RapidOCR-ONNX", "OpenCV-CIELAB", "TCM-RAG-Classics", "LLM-Orchestration"],
        "has_online_llm_key": llm_service.has_active_api_key(),
        "active_model": llm_service.model,
        "message": "AI 计算中枢运行正常，各模块算力就绪"
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

@app.post("/api/ai/rag/query")
async def query_rag(req: RagQueryRequest):
    """
    古籍药典 RAG 知识检索切片召回接口
    """
    docs = rag_service.search_classics(req.query, top_k=req.top_k or 2)
    return {
        "code": 200,
        "message": "success",
        "data": docs
    }

@app.post("/api/ai/llm/formula-analysis")
async def analyze_formula(req: FormulaAnalysisRequest):
    """
    大模型 + RAG 深度方解推演接口（君臣佐使剖析、现代药理透视、替代处方建议）
    """
    result = await llm_service.generate_formula_deep_analysis(req.herbs, req.auditResult)
    return {
        "code": 200,
        "message": "success",
        "data": result
    }

@app.post("/api/ai/llm/chat")
async def chat_tcm(req: ChatRequest):
    """
    中医药用药安全与辨证调养智能伴聊问答接口
    """
    reply = await llm_service.chat_consultation(req.question, req.contextHerbs)
    return {
        "code": 200,
        "message": "success",
        "data": {
            "reply": reply
        }
    }

if __name__ == "__main__":
    import uvicorn
    port = int(os.getenv("PORT", "8001"))
    uvicorn.run("main:app", host="127.0.0.1", port=port, reload=False)

# 岐黄盾 (HerbShield) —— 跨服务 API 契约设计

本文档定义前端、Java 业务中枢与 Python AI 计算中枢之间的交互协议。

---

## 一、 通信拓扑

```
[ Frontend (Vue3) ]  ───(HTTPS / JSON)───>  [ Java (Spring Boot :8080) ]
                                                       │
                                      (Internal HTTP)  ▼
                                            [ Python (FastAPI :8000) ]
```

*   **外部客户端**（手机/PC）统一只与 **Java 业务中枢 (8080)** 交互。
*   **Python AI 服务 (8000)** 作为内部计算节点，仅暴露给本机内网（`127.0.0.1:8000`）。

---

## 二、 Java -> Python 内部计算接口协议

### 1. 处方/中药包装 OCR 识别与结构化提取
*   **Method**: `POST`
*   **Endpoint**: `http://127.0.0.1:8000/api/ai/ocr/prescription`
*   **Content-Type**: `multipart/form-data`
*   **Request**:
    *   `file`: 图片二进制数据 (JPG/PNG)
*   **Response (JSON)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "raw_text": "生白术 15g 炒枳实 10g 甘草 6g 甘遂 3g 先煎 水煎服",
    "herbs": [
      { "name": "白术", "processing": "生", "dose": 15.0, "unit": "g" },
      { "name": "枳实", "processing": "炒", "dose": 10.0, "unit": "g" },
      { "name": "甘草", "processing": "", "dose": 6.0, "unit": "g" },
      { "name": "甘遂", "processing": "", "dose": 3.0, "unit": "g" }
    ],
    "usage": "水煎服",
    "decoction_methods": ["先煎"]
  }
}
```

---

### 2. 数字舌象 AI 分割与特征分类
*   **Method**: `POST`
*   **Endpoint**: `http://127.0.0.1:8000/api/ai/tongue/analyze`
*   **Content-Type**: `multipart/form-data`
*   **Request**:
    *   `file`: 舌象图片二进制数据 (JPG/PNG)
*   **Response (JSON)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tongue_detected": true,
    "confidence": 0.96,
    "tongue_color": {
      "label": "红舌",
      "scores": { "淡白": 0.05, "淡红": 0.20, "红": 0.70, "绛红": 0.05 }
    },
    "moss_thickness": {
      "label": "黄腻苔",
      "scores": { "薄白": 0.10, "白厚": 0.15, "黄腻": 0.75 }
    },
    "has_tooth_marks": false,
    "tooth_mark_score": 0.12
  }
}
```

---

### 3. 古籍 RAG 语义循证检索
*   **Method**: `POST`
*   **Endpoint**: `http://127.0.0.1:8000/api/ai/rag/query`
*   **Content-Type**: `application/json`
*   **Request**:
```json
{
  "query": "甘草 甘遂 十八反",
  "top_k": 3
}
```
*   **Response (JSON)**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "source": "《本草纲目》· 草部",
      "content": "甘草反甘遂、大戟、海藻、芫花。相反者，激而生变，不可同用。",
      "score": 0.92
    }
  ]
}
```

---

## 三、 Frontend -> Java 核心业务接口

| 接口地址 | 请求方式 | 接口描述 |
| :--- | :--- | :--- |
| `/api/audit/prescription-image` | `POST (Multipart)` | 上传处方图：Java 转发 Python OCR，并经 Java 规则引擎执行配伍安全审计，返回终检报告。 |
| `/api/audit/prescription-text` | `POST (JSON)` | 直接输入中药药味数组进行禁忌与剂量审计。 |
| `/api/tongue/diagnose` | `POST (Multipart)` | 上传舌象图片：Java 转发 Python 视觉分析，结合体质知识库返回养生建议。 |
| `/api/graph/herb/{name}` | `GET` | 查询指定中药的知识图谱节点与关联方剂/性味归经。 |
| `/api/graph/overview` | `GET` | 获取 ECharts 力导向知识图谱全景数据。 |

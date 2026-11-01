# 岐黄盾 (HerbShield) —— 基于异构微服务的中医药用药安全哨兵与方剂智能辅助平台

> **参赛全称**：南京中医药大学第十九届“挑战杯”大学生课外学术科技作品竞赛（科技发明制作 B 类 - 信息技术组）  
> **项目代号**：HerbShield / QihuangShield  
> **架构设计**：Vue 3 PWA 前端 + Spring Boot 3 业务中枢 + FastAPI Python AI 异构微服务  
> **线上发布域名**：`https://tcm.084623224.xyz` (Cloudflare Tunnel 隧道映射)

---

## 一、 系统架构概览与转发调度设计

```text
                     ┌──────────────────────────────────────────────┐
                     │          前端展现层 (Vue 3 + PWA)            │
                     │  - 移动端：拍照取景引导、辨证自测、PWA全屏离线 │
                     │  - PC端：本草方剂力导向动态知识图谱大屏      │
                     └──────────────────────┬───────────────────────┘
                                            │ HTTP / JSON (端口 8080)
                                            ▼
                     ┌──────────────────────────────────────────────┐
                     │       Java 业务与规则中枢 (Spring Boot 3)     │
                     │  - 【调度中心】统管前端接入与 Python AI 转发 │
                     │  - 【安全底座】十八反/十九畏/超量规则引擎    │
                     │  - 【知识服务】ECharts 图谱全景与药典考据接口│
                     │  - 【健康探针】集群微服务健康状态动态聚合    │
                     └──────────────────────┬───────────────────────┘
                                            │ 本地高速内网调用 (127.0.0.1:8000)
                                            ▼
                     ┌──────────────────────────────────────────────┐
                     │          Python AI 计算中枢 (FastAPI)         │
                     │  - 【OCR 引擎】RapidOCR (ONNX CPU) 处方解析  │
                     │  - 【视觉分析】OpenCV CIELAB/HSV 舌象特征分割 │
                     │  - 【体质映射】九种中医体质倾向雷达数值计算  │
                     │  - 纯无状态计算，算完即走，高并发不阻塞业务   │
                     └──────────────────────────────────────────────┘
```

---

## 二、 工程各子系统开发与测试状态 (100% 完成)

| 子系统 | 技术栈 | 核心职责 | 测试与验证状态 |
| :--- | :--- | :--- | :--- |
| **`frontend/`** | Vue 3 + TypeScript + Vite + Tailwind + Vant + ECharts + PWA | 处方质检工作台、数字舌象取景器、力导向知识图谱、法定禁忌字典 | **通过** (`npm run build` 耗时 15.13s，PWA Service Worker 离线缓存就绪) |
| **`backend-java/`** | Spring Boot 3.3.4 + JDK 21 LTS + Maven + Fastjson2 | 毫秒级集合碰撞规则引擎、知识图谱考据服务、跨服务 AI 调度中枢 (`AiServiceClient`) | **通过** (`mvn test` 10 个测试用例 100% 通过，10,000 次压测平均 34.6 微秒/方) |
| **`backend-python/`** | FastAPI + Python 3.12 + RapidOCR (PaddleOCR ONNX) + OpenCV 5.0 | 处方图片字符抽取实体对齐、舌色 CIELAB 聚类、舌苔 HSV 分割、九维体质映射 | **通过** (`python test_ai.py` 图像合成与推理 100% 通过，无第三方收费云依赖) |

---

## 三、 跨服务转发与业务调度管道实现

1. **处方图像全流程质检管道 (`POST /api/audit/prescription-image`)**:
   - 前端上传处方图片至 Java 业务中枢 (`:8080`)。
   - Java `AiServiceClient` 构造 `multipart/form-data` 请求转发至 Python AI 节点 (`127.0.0.1:8000/api/ai/ocr/prescription`)。
   - Python RapidOCR ONNX 执行文字切分与实体抽取，返回中药名、炮制方法与剂量数值。
   - Java 自动将抽取的实体灌入 `RuleEngineService`，利用 Set 集合相交碰撞算法在 1 毫秒内完成十八反、十九畏、超量及先煎后下判定。
   - Java 组装全流程识别文本、药味标签与终检报告，一步到位返回前端。

2. **数字舌象特征计算与体质调养管道 (`POST /api/tongue/diagnose`)**:
   - 前端相机拍照后上传至 Java 网关。
   - Java 转发至 Python 节点 (`127.0.0.1:8000/api/ai/tongue/analyze`)，完成舌体 ROI 扣取、CIELAB 舌色分析与苔色计算。
   - 映射九种中医体质雷达评分，结合 Java 知识库附加辨证食疗与穴位按揉建议。

3. **微服务集群健康探针 (`GET /api/health`)**:
   - 动态检测 Java 自身及 Python 算力节点网络状态，前端顶栏实时渲染微服务心跳脉冲指示灯。

---

## 四、 本地一键启动指南

在项目根目录下双击运行 `start-all.bat`，即可一键同时拉起三大异构服务：

```cmd
D:\work\ai\qihuang-shield\start-all.bat
```

也可以分别在三个终端独立启动：

```bash
# 终端 1: 启动 Python AI 算力服务 (:8000)
cd backend-python
python main.py

# 终端 2: 启动 Java 业务中枢与规则引擎 (:8080)
cd backend-java
mvn spring-boot:run

# 终端 3: 启动前端开发服务器 (:3000)
cd frontend
npm run dev
```

启动完成后访问：
- **前端工作台**：`http://localhost:3000`
- **Java API 网关探针**：`http://localhost:8080/api/health`
- **Python AI 算力文档**：`http://127.0.0.1:8000/docs`

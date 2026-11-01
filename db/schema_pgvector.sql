-- ====================================================================
-- 岐黄盾 (HerbShield) - PostgreSQL + pgvector 统一关系与高维向量混合数据架构
-- 适用于：生产环境 (Ubuntu 24.04 + PostgreSQL 16 + pgvector)
-- ====================================================================

-- 1. 启用 pgvector 向量扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. 《中国药典》中草药基准词典表
CREATE TABLE IF NOT EXISTS tcm_herb_dict (
    id SERIAL PRIMARY KEY,
    herb_name VARCHAR(64) NOT NULL UNIQUE,       -- 药材正名 (规范名)
    pinyin VARCHAR(128),                         -- 汉语拼音
    aliases JSONB DEFAULT '[]'::jsonb,           -- 炮制别名、地方名、俗名列表
    nature VARCHAR(64),                          -- 四气 (寒/热/温/凉/平)
    flavor VARCHAR(64),                          -- 五味 (辛/甘/酸/苦/咸)
    meridians VARCHAR(128),                      -- 归经 (归肺、脾经等)
    toxicity_level VARCHAR(32) DEFAULT '无毒',   -- 毒性等级 (无毒/微毒/有毒/大毒)
    max_dosage NUMERIC(6, 2),                    -- 《中国药典》最高单剂极量 (g)
    dosage_range VARCHAR(64),                    -- 药典推荐常规剂量 (如 "3~9g")
    efficacy TEXT,                               -- 功效主治
    contraindications TEXT,                      -- 使用禁忌 (包括配伍禁忌说明)
    decoction_method VARCHAR(128) DEFAULT '常规水煎', -- 煎煮法 (常规水煎/先煎/后下/包煎/烊化/另煎)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_herb_name ON tcm_herb_dict(herb_name);
CREATE INDEX IF NOT EXISTS idx_herb_toxicity ON tcm_herb_dict(toxicity_level);

-- 3. 经典名方与配伍构型表 (国家中医药管理局法定经典名方)
CREATE TABLE IF NOT EXISTS tcm_formula_catalog (
    id SERIAL PRIMARY KEY,
    formula_name VARCHAR(128) NOT NULL UNIQUE,   -- 方剂名称
    source_book VARCHAR(128) NOT NULL,           -- 出处古籍 (如《伤寒论》《脾胃论》)
    author VARCHAR(64),                          -- 创方医家 (如 张仲景, 李东垣)
    category VARCHAR(64),                        -- 治法分类 (解表剂/泻下剂/清热剂/温里剂/补益剂等)
    composition JSONB NOT NULL,                  -- 药味构成与标准剂量 (JSONB 数组)
    indications TEXT,                            -- 主治病机与证候
    monarch_herb VARCHAR(64),                    -- 君药
    minister_herb VARCHAR(128),                  -- 臣药
    assistant_herb VARCHAR(128),                 -- 佐药
    guide_herb VARCHAR(64),                      -- 使药
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_formula_name ON tcm_formula_catalog(formula_name);
CREATE INDEX IF NOT EXISTS idx_formula_category ON tcm_formula_catalog(category);

-- 4. 处方安全审查审计流水表 (医疗合规可回溯存证核心)
CREATE TABLE IF NOT EXISTS prescription_audit_record (
    id BIGSERIAL PRIMARY KEY,
    audit_serial_no VARCHAR(64) NOT NULL UNIQUE, -- 审计流水号 (如 HS-20261101-ABCD)
    raw_text TEXT NOT NULL,                      -- 处方原文 (OCR 或手动输入文本)
    detected_herbs JSONB NOT NULL,               -- 识别解析出的药材列表及剂量
    safety_status VARCHAR(32) NOT NULL,          -- 安全状态 (PASS: 合规 / WARN: 剂量预警 / INTERCEPTED: 红线拦截)
    contraindications JSONB DEFAULT '[]'::jsonb, -- 触发的十八反/十九畏/毒性红线详情
    ai_analysis JSONB,                           -- 大模型深层方解与君臣佐使推演结果
    rag_citations JSONB,                         -- 检索命中的古籍文献切片引用
    client_ip VARCHAR(64),                       -- 请求客户端 IP
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_serial ON prescription_audit_record(audit_serial_no);
CREATE INDEX IF NOT EXISTS idx_audit_status ON prescription_audit_record(safety_status);
CREATE INDEX IF NOT EXISTS idx_audit_created ON prescription_audit_record(created_at DESC);

-- 5. 中医体质辨识报告表 (九型体质测评记录)
CREATE TABLE IF NOT EXISTS constitution_assessment_record (
    id BIGSERIAL PRIMARY KEY,
    user_identifier VARCHAR(64),                 -- 匿名用户或终端设备标识
    scores JSONB NOT NULL,                       -- 九种体质得分 (平和/气虚/阳虚/阴虚/痰湿/湿热/血瘀/气郁/特禀)
    primary_type VARCHAR(64) NOT NULL,           -- 主要体质
    tendency_types VARCHAR(128),                 -- 兼夹倾向体质
    conditioning_plan JSONB,                     -- 辨体调理方案与药膳食疗建议
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_constitution_primary ON constitution_assessment_record(primary_type);
CREATE INDEX IF NOT EXISTS idx_constitution_created ON constitution_assessment_record(created_at DESC);

-- 6. 中医药古籍与药理文献高维语义向量表 (pgvector 核心)
CREATE TABLE IF NOT EXISTS tcm_corpus_vector (
    id SERIAL PRIMARY KEY,
    chunk_key VARCHAR(64) UNIQUE NOT NULL,       -- 切片唯一标识 (如 kb_shibafan_01)
    source_book VARCHAR(128) NOT NULL,           -- 经典著作 (如《本草纲目》《金匮要略》)
    chapter VARCHAR(128),                        -- 卷部章节
    title VARCHAR(256) NOT NULL,                 -- 条目论述标题
    keywords JSONB DEFAULT '[]'::jsonb,          -- 语义关键词
    content TEXT NOT NULL,                       -- 古籍原文与释义
    modern_mechanism TEXT,                       -- 现代分子药理毒理学机制
    substitute_advice TEXT,                      -- 安全替代配伍处方建议
    embedding vector(1536),                      -- 1536 维语义向量嵌入 (可自适应 768 / 1024 维)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 创建 pgvector 高效 HNSW 向量近似最近邻索引 (余弦距离)
-- 注意：当表中拥有向量数据后建立 HNSW 索引性能最佳
CREATE INDEX IF NOT EXISTS idx_corpus_vector_hnsw 
ON tcm_corpus_vector USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);

CREATE INDEX IF NOT EXISTS idx_corpus_source ON tcm_corpus_vector(source_book);

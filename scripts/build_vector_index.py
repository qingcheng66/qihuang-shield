"""
岐黄盾 (HerbShield) - 中医药知识库多源语义切片与本地向量索引构建器
整合：
1. 10 个现代分子药理与配伍禁忌切片 (data/classics_rag_corpus.json)
2. 55 首传世经典名方切片 (data/formulas_dataset.json)
3. 307 味《中国药典》临床中药材切片 (data/herbs_dataset.json)

执行：
- 结构化数据语义自然语言化 (Semantic Sentence Construction)
- 提取关键词供 BM25/稀疏检索使用
- 调用本地 BAAI/bge-small-zh-v1.5 模型计算 512 维高维向量
- 输出 data/unified_rag_chunks.json 与 data/rag_embeddings.npy
"""

import os
import json
import time
import numpy as np
from fastembed import TextEmbedding

ROOT_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_DIR = os.path.join(ROOT_DIR, "data")

def build_chunks():
    chunks = []

    # 1. 载入 10 个现代分子药理与配伍禁忌切片
    classics_path = os.path.join(DATA_DIR, "classics_rag_corpus.json")
    if os.path.exists(classics_path):
        with open(classics_path, "r", encoding="utf-8") as f:
            classics_data = json.load(f)
        for item in classics_data:
            cid = item.get("id", f"rag_{len(chunks)}")
            title = item.get("title", "")
            source = item.get("source", "")
            kws = item.get("keywords", [])
            content = item.get("content", "")
            mech = item.get("modern_mechanism", "")
            sub = item.get("substitute_advice", "")

            semantic_text = (
                f"【条目论述】{title}。出处典籍：{source}。"
                f"配伍禁忌与古籍考据：{content} "
                f"现代分子生物化学毒理机制：{mech} "
                f"临床调优替代用药方案：{sub}"
            )

            chunks.append({
                "chunk_id": cid,
                "category": "toxic_interaction",
                "title": title,
                "source": source,
                "keywords": kws,
                "semantic_text": semantic_text,
                "content": content,
                "modern_mechanism": mech,
                "substitute_advice": sub
            })

    # 2. 载入 55 首经典名方
    formulas_path = os.path.join(DATA_DIR, "formulas_dataset.json")
    if os.path.exists(formulas_path):
        with open(formulas_path, "r", encoding="utf-8") as f:
            formulas_data = json.load(f)
        for item in formulas_data:
            fname = item.get("formula_name", "")
            cid = f"formula_{fname}"
            src = f"{item.get('source_book', '')} ({item.get('author', '历代医家')})"
            cat = item.get("category", "")
            comp_list = item.get("composition", [])
            comp_str = "、".join([f"{c['name']}{c.get('dose', '')}{c.get('unit', 'g')}({c.get('role', '药')})" for c in comp_list])
            herb_names = [c["name"] for c in comp_list]
            indications = item.get("indications", "")
            monarch = item.get("monarch_herb", "")
            minister = item.get("minister_herb", "")

            semantic_text = (
                f"【经典名方】{fname}，出自{src}，治法属于{cat}。"
                f"组方配伍药味：{comp_str}。"
                f"主治病机与证候表现：{indications}。"
                f"君臣配伍核心：以{monarch}为君药，{minister}为臣药协同起效。"
            )

            kws = [fname, cat] + herb_names + [monarch, minister]
            kws = list(set([k for k in kws if k]))

            chunks.append({
                "chunk_id": cid,
                "category": "classic_formula",
                "title": f"经典名方 · {fname}",
                "source": src,
                "keywords": kws,
                "semantic_text": semantic_text,
                "content": f"【方名】{fname}（{cat}）。主治：{indications}。方药组成：{comp_str}",
                "modern_mechanism": f"全方围绕{monarch}、{minister}配伍协同，治法属于{cat}，主治{indications}。",
                "substitute_advice": "临床临证加减宜遵循原方君臣佐使主攻方向，辨明虚实寒热表里。",
                "formula_name": fname,
                "composition": comp_list
            })

    # 3. 载入 307 味药典药材
    herbs_path = os.path.join(DATA_DIR, "herbs_dataset.json")
    if os.path.exists(herbs_path):
        with open(herbs_path, "r", encoding="utf-8") as f:
            herbs_data = json.load(f)
        for item in herbs_data:
            hname = item.get("herb_name", "")
            cid = f"herb_{hname}"
            pinyin = item.get("pinyin", "")
            nature = item.get("nature", "")
            flavor = item.get("flavor", "")
            meridians = item.get("meridians", "")
            toxicity = item.get("toxicity_level", "无毒")
            max_dose = item.get("max_dosage", 0.0)
            dose_range = item.get("dosage_range", "")
            efficacy = item.get("efficacy", "")
            contra = item.get("contraindications", "")
            decoct = item.get("decoction_method", "常规水煎")

            semantic_text = (
                f"【药典中药材】{hname}（拼音：{pinyin}）。"
                f"性味与归经：{nature}，{flavor}；{meridians}。"
                f"功能主治与功效：{efficacy}。"
                f"中国药典推荐用量：{dose_range}，法定单剂极量上限：{max_dose}g。"
                f"毒性分级：{toxicity}。"
                f"配伍禁忌与用药警戒：{contra}。"
                f"特殊煎煮与服用法：{decoct}。"
            )

            kws = [hname, pinyin, nature, flavor, toxicity]
            if "先煎" in decoct or "后下" in decoct or "包煎" in decoct or "烊化" in decoct:
                kws.append(decoct)
            if "十八反" in contra:
                kws.append("十八反")
            if "十九畏" in contra:
                kws.append("十九畏")

            chunks.append({
                "chunk_id": cid,
                "category": "herb_dict",
                "title": f"中药考据 · {hname}",
                "source": "《中华人民共和国药典》(2020年版 一部)",
                "keywords": kws,
                "semantic_text": semantic_text,
                "content": f"{hname}：{nature}，{flavor}；{meridians}。功效：{efficacy}。药典用量：{dose_range}（极量：{max_dose}g）。",
                "modern_mechanism": f"药理特性：归{meridians}，具{efficacy}之功。毒性级别为{toxicity}，临床极量严格限制为{max_dose}g。",
                "substitute_advice": f"配伍注意：{contra}。特殊煎法：{decoct}。",
                "herb_name": hname,
                "max_dosage": max_dose,
                "toxicity_level": toxicity
            })

    print(f"成功构建结构化原子语义切片：共 {len(chunks)} 个 Chunk")
    print(f"- 现代药理与配伍禁忌专题: 10 个")
    print(f"- 历代经典名方: 55 个")
    print(f"- 中国药典中药材: 307 个")
    return chunks

def vectorize_and_save(chunks):
    out_json = os.path.join(DATA_DIR, "unified_rag_chunks.json")
    out_npy = os.path.join(DATA_DIR, "rag_embeddings.npy")

    print(f"正在保存切片元数据至 {out_json} ...")
    with open(out_json, "w", encoding="utf-8") as f:
        json.dump(chunks, f, ensure_ascii=False, indent=2)

    texts = [c["semantic_text"] for c in chunks]
    print(f"开始加载本地 BAAI/bge-small-zh-v1.5 向量模型并计算 {len(texts)} 条语义嵌入...")
    t0 = time.time()
    embedding_model = TextEmbedding(model_name="BAAI/bge-small-zh-v1.5")

    embeddings = list(embedding_model.embed(texts))
    emb_array = np.array(embeddings, dtype=np.float32)

    # L2 归一化，方便后续直接用点积计算余弦相似度
    norms = np.linalg.norm(emb_array, axis=1, keepdims=True)
    norms[norms == 0] = 1.0
    normalized_embs = emb_array / norms

    print(f"向量化完成！耗时: {time.time() - t0:.2f}s, 矩阵维度: {normalized_embs.shape}")
    np.save(out_npy, normalized_embs)
    print(f"成功持久化向量索引至 {out_npy} (文件大小: {os.path.getsize(out_npy)/1024:.1f} KB)")

if __name__ == "__main__":
    c = build_chunks()
    vectorize_and_save(c)

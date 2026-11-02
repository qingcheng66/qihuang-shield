package com.qihuang.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qihuang.entity.FormulaCatalogEntity;
import com.qihuang.entity.HerbDictEntity;
import com.qihuang.model.HerbDetail;
import com.qihuang.model.KnowledgeGraphData;
import com.qihuang.repository.FormulaCatalogRepository;
import com.qihuang.repository.HerbDictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 中医药知识图谱与古籍本草专论服务
 * 优先基于数据库 (PostgreSQL / H2) 300+ 药材与 50+ 名方动态生成力导向图谱，同时具备离线内存兜底
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeGraphService {

    private final HerbNormalizer herbNormalizer;
    private final HerbDictRepository herbDictRepository;
    private final FormulaCatalogRepository formulaCatalogRepository;

    private static final Map<String, HerbDetail> HERB_DATABASE = new HashMap<>();

    static {
        HERB_DATABASE.put("甘草", HerbDetail.builder()
                .name("甘草")
                .pinyin("Gancao")
                .natureFlavor("甘，平")
                .meridian("归心、肺、脾、胃经")
                .efficacy("补脾益气，清热解毒，祛痰止咳，缓急止痛，调和诸药")
                .dosageRange("2~10g")
                .maxDosage(10.0)
                .contraindications("十八反：严禁与甘遂、大戟、海藻、芫花同用；湿盛中满者忌服")
                .decoctionMethod("常规水煎，调和百药，多作使药")
                .classicFormulas(List.of("四君子汤", "补中益气汤", "甘草泻心汤", "芍药甘草汤"))
                .pharmacopoeiaExcerpt("《中国药典》一部收载：豆科植物甘草 Glycyrrhiza uralensis Fisch. 的干燥根和根茎。具有肾上腺皮质激素样抗炎与抗过敏作用，抑制胃酸分泌。")
                .build());

        HERB_DATABASE.put("甘遂", HerbDetail.builder()
                .name("甘遂")
                .pinyin("Gansui")
                .natureFlavor("苦，寒；有毒")
                .meridian("归肺、肾、大肠经")
                .efficacy("泻水逐饮，消肿散结")
                .dosageRange("0.5~1.5g (入丸散)")
                .maxDosage(1.5)
                .contraindications("十八反：严禁与甘草配伍！孕妇禁用，体弱虚羸者忌服")
                .decoctionMethod("多入丸散服，不宜入煎剂")
                .classicFormulas(List.of("十枣汤", "大陷胸汤", "舟车丸"))
                .pharmacopoeiaExcerpt("《神农本草经》：主水肿腹满，下水，破积聚痰饮。现代药理证实其含二萜类刺激性毒素，能剧烈刺激肠黏膜导致充血坏死。")
                .build());

        HERB_DATABASE.put("黄芪", HerbDetail.builder()
                .name("黄芪")
                .pinyin("Huangqi")
                .natureFlavor("甘，微温")
                .meridian("归肺、脾经")
                .efficacy("补气升阳，固表止汗，利水消肿，生津养血，行滞通痹，托毒排脓，敛疮生肌")
                .dosageRange("9~30g")
                .maxDosage(60.0)
                .contraindications("表实邪盛、内有积滞、阴虚阳亢者慎服")
                .decoctionMethod("常规水煎")
                .classicFormulas(List.of("补中益气汤", "当归补血汤", "玉屏风散", "防己黄芪汤"))
                .pharmacopoeiaExcerpt("《神农本草经》列为上品：主痈疽久败疮，排脓止痛，补虚，小儿百病。增强网状内皮系统吞噬功能，促进抗体形成。")
                .build());
    }

    /**
     * 生成力导向知识图谱全貌数据 (优先从数据库动态装配，若空则优雅降级)
     */
    public KnowledgeGraphData getOverviewGraph() {
        List<KnowledgeGraphData.CategoryItem> categories = List.of(
                KnowledgeGraphData.CategoryItem.builder().name("中药材").itemStyle(Map.of("color", "#10b981")).build(),
                KnowledgeGraphData.CategoryItem.builder().name("经典名方").itemStyle(Map.of("color", "#f43f5e")).build(),
                KnowledgeGraphData.CategoryItem.builder().name("性味归经").itemStyle(Map.of("color", "#f59e0b")).build(),
                KnowledgeGraphData.CategoryItem.builder().name("治法分类").itemStyle(Map.of("color", "#8b5cf6")).build(),
                KnowledgeGraphData.CategoryItem.builder().name("禁忌红线").itemStyle(Map.of("color", "#ef4444")).build()
        );

        List<KnowledgeGraphData.NodeItem> nodes = new ArrayList<>();
        List<KnowledgeGraphData.LinkItem> links = new ArrayList<>();
        Set<String> addedNodeIds = new HashSet<>();

        // 尝试从数据库动态装载名方与药材构型
        List<FormulaCatalogEntity> formulas = formulaCatalogRepository.findAll();

        if (formulas != null && !formulas.isEmpty()) {
            // 1. 挂载治法分类节点 (Category 3)
            Map<String, String> categoryNodeMap = new HashMap<>();
            for (FormulaCatalogEntity f : formulas) {
                String cat = f.getCategory();
                if (cat != null && !cat.isBlank() && !categoryNodeMap.containsKey(cat)) {
                    String catId = "cat_" + cat;
                    categoryNodeMap.put(cat, catId);
                    nodes.add(createNode(catId, cat, 32.0, 3, "治法分类：" + cat));
                    addedNodeIds.add(catId);
                }
            }

            // 2. 挂载方剂节点 (Category 1) 并与治法分类连线
            int formulaLimit = Math.min(formulas.size(), 35);
            for (int i = 0; i < formulaLimit; i++) {
                FormulaCatalogEntity f = formulas.get(i);
                String fId = "f_" + f.getId();
                String desc = f.getFormulaName() + " (" + f.getSourceBook() + ") - " + (f.getIndications() != null ? f.getIndications() : "");
                nodes.add(createNode(fId, f.getFormulaName(), 42.0, 1, desc));
                addedNodeIds.add(fId);

                // 连线到治法分类
                if (f.getCategory() != null && categoryNodeMap.containsKey(f.getCategory())) {
                    links.add(createLink(categoryNodeMap.get(f.getCategory()), fId, null,
                            Map.of("color", "#8b5cf6", "width", 1.5), null));
                }

                // 解析方剂组成 (JSON)，连线到药材节点
                try {
                    JSONArray compArr = JSON.parseArray(f.getComposition());
                    if (compArr != null) {
                        for (int j = 0; j < compArr.size(); j++) {
                            JSONObject cObj = compArr.getJSONObject(j);
                            String hName = cObj.getString("name");
                            String role = cObj.getString("role");
                            Double dose = cObj.getDouble("dose");
                            String hCanonical = herbNormalizer.normalize(hName);
                            String hId = "herb_" + hCanonical;

                            if (!addedNodeIds.contains(hId)) {
                                nodes.add(createNode(hId, hCanonical, 28.0, 0, "中药正名: " + hCanonical));
                                addedNodeIds.add(hId);
                            }

                            // 连线：方剂 -> 药材
                            String edgeLabel = role != null ? role : "配伍";
                            if (dose != null) edgeLabel += " " + dose + "g";
                            links.add(createLink(fId, hId, edgeLabel,
                                    Map.of("color", "#10b981", "width", 1.8),
                                    Map.of("color", "#047857")));
                        }
                    }
                } catch (Exception ignored) {}
            }

            // 3. 补充重点相克药材节点以确保十八反/十九畏高亮连线存在
            List<String> coreConflictHerbs = List.of("甘草", "甘遂", "大戟", "芫花", "附子", "半夏", "瓜蒌", "贝母", "白及", "白蔹", "人参", "细辛", "芍药", "藜芦", "丁香", "郁金");
            for (String ch : coreConflictHerbs) {
                String chId = "herb_" + ch;
                if (!addedNodeIds.contains(chId)) {
                    nodes.add(createNode(chId, ch, 30.0, 0, "配伍审查核心药材: " + ch));
                    addedNodeIds.add(chId);
                }
            }

            // 4. 严禁配伍冲突红线 (十八反 / 十九畏) 动态连线
            addConflictEdgeIfExists(links, addedNodeIds, "甘草", "甘遂", "【十八反】甘草反甘遂");
            addConflictEdgeIfExists(links, addedNodeIds, "甘草", "大戟", "【十八反】甘草反大戟");
            addConflictEdgeIfExists(links, addedNodeIds, "甘草", "芫花", "【十八反】甘草反芫花");
            addConflictEdgeIfExists(links, addedNodeIds, "附子", "半夏", "【十八反】附子反半夏");
            addConflictEdgeIfExists(links, addedNodeIds, "附子", "瓜蒌", "【十八反】附子反瓜蒌");
            addConflictEdgeIfExists(links, addedNodeIds, "附子", "贝母", "【十八反】附子反贝母");
            addConflictEdgeIfExists(links, addedNodeIds, "附子", "白及", "【十八反】附子反白及");
            addConflictEdgeIfExists(links, addedNodeIds, "人参", "藜芦", "【十八反】诸参反藜芦");
            addConflictEdgeIfExists(links, addedNodeIds, "细辛", "藜芦", "【十八反】细辛反藜芦");
            addConflictEdgeIfExists(links, addedNodeIds, "芍药", "藜芦", "【十八反】芍药反藜芦");
            addConflictEdgeIfExists(links, addedNodeIds, "丁香", "郁金", "【十九畏】丁香畏郁金");

            return KnowledgeGraphData.builder()
                    .categories(categories)
                    .nodes(nodes)
                    .links(links)
                    .build();
        }

        // 若数据库为空（兜底防御），返回默认精炼图谱
        nodes.add(createNode("1", "补中益气汤", 45.0, 1, "李东垣《脾胃论》：甘温除热，补中益气，升阳举陷。"));
        nodes.add(createNode("2", "黄芪", 32.0, 0, "补气升阳，固表止汗。为君药。"));
        nodes.add(createNode("3", "甘草", 28.0, 0, "调和诸药，补脾益气。使药。"));
        nodes.add(createNode("4", "甘遂", 26.0, 0, "泻水逐饮，消肿散结。大毒。"));
        links.add(createLink("1", "2", "君药 15g", Map.of("color", "#10b981", "width", 2), null));
        links.add(createLink("1", "3", "使药 6g", Map.of("color", "#10b981", "width", 2), null));
        links.add(createConflictLink("3", "4", "【十八反】甘草反甘遂"));

        return KnowledgeGraphData.builder()
                .categories(categories)
                .nodes(nodes)
                .links(links)
                .build();
    }

    private void addConflictEdgeIfExists(List<KnowledgeGraphData.LinkItem> links, Set<String> nodeIds, String h1, String h2, String label) {
        String id1 = "herb_" + h1;
        String id2 = "herb_" + h2;
        if (nodeIds.contains(id1) && nodeIds.contains(id2)) {
            links.add(createConflictLink(id1, id2, label));
        }
    }

    /**
     * 根据中药名称考据药典明细 (优先查询数据库，无缝对接 307 味药典词典)
     */
    public HerbDetail getHerbDetail(String herbName) {
        if (herbName == null || herbName.isBlank()) {
            return null;
        }
        String canonical = herbNormalizer.normalize(herbName);

        // 优先从数据库中获取《中国药典》官方元数据
        Optional<HerbDictEntity> entityOpt = herbDictRepository.findByHerbName(canonical);
        if (entityOpt.isPresent()) {
            HerbDictEntity e = entityOpt.get();
            String natureFlavor = (e.getNature() != null ? e.getNature() : "") + "，" + (e.getFlavor() != null ? e.getFlavor() : "");
            double maxDose = e.getMaxDosage() != null ? e.getMaxDosage().doubleValue() : 15.0;

            return HerbDetail.builder()
                    .name(e.getHerbName())
                    .pinyin(e.getPinyin() != null ? e.getPinyin() : "")
                    .natureFlavor(natureFlavor)
                    .meridian(e.getMeridians() != null ? e.getMeridians() : "暂无记载")
                    .efficacy(e.getEfficacy() != null ? e.getEfficacy() : "详见《中国药典》")
                    .dosageRange(e.getDosageRange() != null ? e.getDosageRange() : "3~10g")
                    .maxDosage(maxDose)
                    .contraindications(e.getContraindications() != null ? e.getContraindications() : "遵医嘱配伍")
                    .decoctionMethod(e.getDecoctionMethod() != null ? e.getDecoctionMethod() : "常规水煎")
                    .classicFormulas(findFormulasForHerb(canonical))
                    .pharmacopoeiaExcerpt("《中华人民共和国药典》(2020年版 一部) 权威收载：" + e.getEfficacy() + "。毒性级别：" + e.getToxicityLevel() + "，推荐用量：" + e.getDosageRange())
                    .build();
        }

        return HERB_DATABASE.getOrDefault(canonical, HerbDetail.builder()
                .name(canonical)
                .natureFlavor("详见《中国药典》")
                .meridian("多经归属")
                .efficacy("本草专论中收载中药")
                .dosageRange("3~10g")
                .maxDosage(15.0)
                .contraindications("遵医嘱配伍")
                .decoctionMethod("常规水煎")
                .classicFormulas(List.of("经典名方收录"))
                .pharmacopoeiaExcerpt("中药安全哨兵知识图谱持续扩充收录中。")
                .build());
    }

    private List<String> findFormulasForHerb(String herbName) {
        List<String> list = new ArrayList<>();
        try {
            List<FormulaCatalogEntity> all = formulaCatalogRepository.findAll();
            for (FormulaCatalogEntity f : all) {
                if (f.getComposition() != null && f.getComposition().contains(herbName)) {
                    list.add(f.getFormulaName());
                    if (list.size() >= 5) break;
                }
            }
        } catch (Exception ignored) {}
        return list.isEmpty() ? List.of("经方配伍收载") : list;
    }

    /**
     * 获取系统全部法定规则列表（用于字典页全量展示）
     */
    public Map<String, Object> getAllRulesDetail() {
        Map<String, Object> res = new HashMap<>();

        // 十八反三组
        List<Map<String, Object>> shiBaFanList = List.of(
                Map.of(
                        "name", "甘草反组",
                        "leader", "甘草",
                        "conflicts", List.of("甘遂", "大戟", "海藻", "芫花"),
                        "quote", "《本草纲目》：甘草反甘遂、大戟、海藻、芫花。相反者，激而生变，不可同用。",
                        "mechanism", "甘草酸抑制肠道 P-糖蛋白外排泵，使甘遂、芫花等二萜毒性成分体内蓄积量剧增，诱发剧烈水泻与心脏停搏。"
                ),
                Map.of(
                        "name", "乌头反组 (川乌/草乌/附子)",
                        "leader", "附子 / 川乌 / 草乌",
                        "conflicts", List.of("半夏", "瓜蒌", "贝母", "白蔹", "白及"),
                        "quote", "《儒门事亲》：本草明言十八反，半蒌贝蔹及攻乌。",
                        "mechanism", "乌头碱水解产物与半夏、贝母等生物碱成分协同兴奋迷走神经并诱发严重心室纤颤。"
                ),
                Map.of(
                        "name", "藜芦反组",
                        "leader", "藜芦",
                        "conflicts", List.of("人参", "党参", "沙参", "丹参", "玄参", "苦参", "细辛", "芍药"),
                        "quote", "《神农本草经》：诸参辛芍叛藜芦。",
                        "mechanism", "藜芦含大量甾体生物碱，与人参皂苷及芍药苷同服显著抑制延髓呼吸中枢并恶化休克。"
                )
        );

        // 十九畏
        List<Map<String, Object>> shiJiuWeiList = List.of(
                Map.of("pair", "硫黄畏朴硝", "desc", "硫黄与朴硝同用，生成毒性硫化物"),
                Map.of("pair", "水银畏砒霜", "desc", "剧毒汞砷化合物，同用脏器迅速衰竭"),
                Map.of("pair", "狼毒畏密陀僧", "desc", "铅毒与根茎毒素叠加致中毒性肝炎"),
                Map.of("pair", "巴豆畏牵牛", "desc", "剧烈峻下泻水，同用致脱水休克与肠穿孔"),
                Map.of("pair", "丁香畏郁金", "desc", "芳香开窍与清心凉血相抵，减效增燥"),
                Map.of("pair", "川乌草乌畏犀角", "desc", "大热与大寒相反激变，损伤心阳"),
                Map.of("pair", "芒硝畏三棱", "desc", "剧烈破血与软坚相撞，诱发出血"),
                Map.of("pair", "官桂畏赤石脂", "desc", "肉桂温阳散寒被石脂固涩吸附减效"),
                Map.of("pair", "人参畏五灵脂", "desc", "人参益气与五灵脂活血化瘀相争，减弱补虚疗效")
        );

        // 药典单剂极量红线
        List<Map<String, Object>> dosageLimits = List.of(
                Map.of("herb", "细辛", "limit", "3g", "law", "《中国药典》2020版一部", "warning", "含黄樟醚及马兜铃酸，超量抑制呼吸中枢与肾蓄积"),
                Map.of("herb", "附子 / 制附子", "limit", "15g", "law", "《中国药典》2020版一部", "warning", "双酯型乌头碱致心律失常与房室传导阻滞，必须文火先煎"),
                Map.of("herb", "巴豆", "limit", "0.3g", "law", "《中国药典》2020版一部", "warning", "含巴豆油及巴豆毒素，致剧烈腐蚀性胃肠炎"),
                Map.of("herb", "马钱子", "limit", "0.6g", "law", "《中国药典》2020版一部", "warning", "士的宁中枢强毒性，导致角弓反张与强直性惊厥"),
                Map.of("herb", "甘遂", "limit", "1.5g", "law", "《中国药典》2020版一部", "warning", "二萜刺激性树脂致急性水样泻脱水"),
                Map.of("herb", "洋金花", "limit", "0.6g", "law", "《中国药典》2020版一部", "warning", "东莨菪碱致中枢抗胆碱综合征、幻觉昏迷")
        );

        // 特殊煎服法
        List<Map<String, Object>> decoctions = List.of(
                Map.of("method", "先煎", "herbs", "生石膏、磁石、代赭石、制附子、生川乌", "reason", "矿物贝壳质坚难溶需延长溶出；乌头类需高温水解消除毒性。文火先煎30~60分钟。"),
                Map.of("method", "后下", "herbs", "薄荷、砂仁、沉香、钩藤、大黄", "reason", "含挥发油或受热易破坏成分，在群药煎毕前5~10分钟下锅。"),
                Map.of("method", "包煎", "herbs", "辛夷、旋覆花、车前子、滑石、蒲黄", "reason", "含绒毛刺激咽喉引起剧烈咳嗽，或细小种子黏锅底焦糊，需用纱布袋包好入煎。"),
                Map.of("method", "烊化", "herbs", "阿胶、鹿角胶、龟甲胶、蜂蜜", "reason", "胶质浓稠黏腻，同煎易黏锅糊底且破坏药效，需隔水加热溶化或用药汁冲服。")
        );

        res.put("shiBaFan", shiBaFanList);
        res.put("shiJiuWei", shiJiuWeiList);
        res.put("dosageLimits", dosageLimits);
        res.put("decoctions", decoctions);

        return res;
    }

    private KnowledgeGraphData.NodeItem createNode(String id, String name, double size, int category, String detail) {
        return KnowledgeGraphData.NodeItem.builder()
                .id(id)
                .name(name)
                .symbolSize(size)
                .category(category)
                .detail(detail)
                .build();
    }

    private KnowledgeGraphData.LinkItem createLink(String source, String target, String label, Map<String, Object> lineStyle, Map<String, Object> labelStyle) {
        Map<String, Object> l = new HashMap<>();
        if (label != null) {
            l.put("show", true);
            l.put("formatter", label);
            if (labelStyle != null) {
                l.putAll(labelStyle);
            }
        }
        return KnowledgeGraphData.LinkItem.builder()
                .source(source)
                .target(target)
                .label(l.isEmpty() ? null : l)
                .lineStyle(lineStyle)
                .build();
    }

    private KnowledgeGraphData.LinkItem createConflictLink(String source, String target, String label) {
        Map<String, Object> lineStyle = Map.of(
                "color", "#ef4444",
                "width", 3,
                "type", "dashed"
        );
        Map<String, Object> labelStyle = Map.of(
                "show", true,
                "formatter", label,
                "color", "#ef4444"
        );
        return KnowledgeGraphData.LinkItem.builder()
                .source(source)
                .target(target)
                .lineStyle(lineStyle)
                .label(labelStyle)
                .build();
    }
}

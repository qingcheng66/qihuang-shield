package com.qihuang.service;

import com.qihuang.model.HerbDetail;
import com.qihuang.model.KnowledgeGraphData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 中医药知识图谱与古籍本草专论服务
 */
@Service
@RequiredArgsConstructor
public class KnowledgeGraphService {

    private final HerbNormalizer herbNormalizer;

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
                .pharmacopoeiaExcerpt("《本草纲目》：黄芪为补药之长，故名。黄芪多糖具有增强机体免疫力、抗衰老、抗疲劳及调节心血管血压作用。")
                .build());

        HERB_DATABASE.put("当归", HerbDetail.builder()
                .name("当归")
                .pinyin("Danggui")
                .natureFlavor("甘、辛，温")
                .meridian("归肝、心、脾经")
                .efficacy("补血活血，调经止痛，润肠通便")
                .dosageRange("6~12g")
                .maxDosage(15.0)
                .contraindications("湿盛中满、大便溏泄者慎服")
                .decoctionMethod("常规水煎")
                .classicFormulas(List.of("当归补血汤", "四物汤", "补中益气汤", "生化汤"))
                .pharmacopoeiaExcerpt("《本草备要》：血虚能补，血枯能润，血滞能通。主要活性成分为藁本内酯、阿魏酸，具有促进造血、抑制血小板聚集与抗血栓功效。")
                .build());

        HERB_DATABASE.put("细辛", HerbDetail.builder()
                .name("细辛")
                .pinyin("Xixin")
                .natureFlavor("辛，温；有小毒")
                .meridian("归心、肺、肾经")
                .efficacy("祛风散寒，通窍止痛，温肺化饮")
                .dosageRange("1~3g (严格遵守《中国药典》)")
                .maxDosage(3.0)
                .contraindications("十八反：严禁与藜芦配伍！气虚多汗、阴虚阳亢头痛者禁用。“细辛不过钱”。")
                .decoctionMethod("严格控制煎煮剂量，不可超 3g")
                .classicFormulas(List.of("小青龙汤", "麻黄附子细辛汤", "九味羌活汤"))
                .pharmacopoeiaExcerpt("《中国药典》：根和根茎。挥发油含黄樟醚、甲基丁香酚。超量黄樟醚对中枢神经先兴奋后麻痹，且含痕量马兜铃酸成分，具有蓄积性肾毒性。")
                .build());

        HERB_DATABASE.put("附子", HerbDetail.builder()
                .name("附子")
                .pinyin("Fuzi")
                .natureFlavor("辛、甘，大热；有毒")
                .meridian("归心、肾、脾经")
                .efficacy("回阳救逆，补火助阳，散寒止痛")
                .dosageRange("3~15g")
                .maxDosage(15.0)
                .contraindications("十八反：严禁与半夏、瓜蒌、贝母、白蔹、白及配伍！孕妇禁用，阴虚阳盛者忌服")
                .decoctionMethod("【先煎】文火单独先煎 30~60 分钟至口尝无麻舌麻唇感，破坏剧毒双酯型乌头碱")
                .classicFormulas(List.of("四逆汤", "真武汤", "附子理中汤", "麻黄附子细辛汤"))
                .pharmacopoeiaExcerpt("《本草汇言》：附子，回阳气，散阴寒之第一品也。乌头碱毒性强烈，长时间文火先煎可使其水解为毒性极低的单酯型乌头原碱。")
                .build());

        HERB_DATABASE.put("半夏", HerbDetail.builder()
                .name("半夏")
                .pinyin("Banxia")
                .natureFlavor("辛，温；有毒")
                .meridian("归脾、胃、肺经")
                .efficacy("燥湿化痰，降逆止呕，消痞散结")
                .dosageRange("3~9g")
                .maxDosage(9.0)
                .contraindications("十八反：反乌头类（附子、川乌、草乌）。阴虚燥咳、津伤口渴、出血者忌服")
                .decoctionMethod("药用多为姜半夏、法半夏，消减刺激性针晶毒性")
                .classicFormulas(List.of("二陈汤", "小半夏汤", "半夏泻心汤", "温胆汤"))
                .pharmacopoeiaExcerpt("《中国药典》：草酸钙针晶及凝集素为主要刺激性致毒成分，现代炮制结合生姜与明矾浸煮可显著解毒。")
                .build());

        HERB_DATABASE.put("人参", HerbDetail.builder()
                .name("人参")
                .pinyin("Renshen")
                .natureFlavor("甘、微苦，微温")
                .meridian("归脾、肺、心、肾经")
                .efficacy("大补元气，复脉固脱，补脾益肺，生津养血，安神益智")
                .dosageRange("3~9g (急救脱证可至15~30g)")
                .maxDosage(10.0)
                .contraindications("十八反：反藜芦！十九畏：畏五灵脂！不宜与茶、萝卜同服")
                .decoctionMethod("【另煎】单味文火另煎 2~3 小时兑服")
                .classicFormulas(List.of("四君子汤", "补中益气汤", "生脉饮", "独参汤"))
                .pharmacopoeiaExcerpt("《神农本草经》列为上品。人参总皂苷（Rg1, Rb1）显著增强中枢抗疲劳与心肌收缩力。")
                .build());

        HERB_DATABASE.put("藜芦", HerbDetail.builder()
                .name("藜芦")
                .pinyin("Lilu")
                .natureFlavor("辛、苦，寒；有毒")
                .meridian("归肺、胃、肝经")
                .efficacy("涌吐风痰，杀虫")
                .dosageRange("0.3~0.6g (入丸散)")
                .maxDosage(0.6)
                .contraindications("十八反：反诸参（人参、党参、沙参、丹参、玄参、苦参）、细辛、芍药！体虚失血者禁用")
                .decoctionMethod("外用为主，内服极慎")
                .classicFormulas(List.of("三圣散"))
                .pharmacopoeiaExcerpt("《中国药典》：含甾体生物碱，易诱发剧烈恶心呕吐与心律失常降压。")
                .build());

        HERB_DATABASE.put("白芍", HerbDetail.builder()
                .name("白芍")
                .pinyin("Baishao")
                .natureFlavor("苦、酸，微寒")
                .meridian("归肝、脾经")
                .efficacy("养血调经，敛阴止汗，柔肝止痛，平抑肝阳")
                .dosageRange("6~15g")
                .maxDosage(15.0)
                .contraindications("十八反：反藜芦！虚寒腹泻者慎服")
                .decoctionMethod("常规水煎")
                .classicFormulas(List.of("四物汤", "芍药甘草汤", "痛泻要方"))
                .pharmacopoeiaExcerpt("《日华子本草》：主治胁肋疼痛、泻痢腹痛。芍药苷具有镇痛解痉抗炎保护肝细胞活性。")
                .build());

        HERB_DATABASE.put("丁香", HerbDetail.builder()
                .name("丁香")
                .pinyin("Dingxiang")
                .natureFlavor("辛，温")
                .meridian("归脾、胃、肺、肾经")
                .efficacy("温中降逆，补肾助阳")
                .dosageRange("1~3g")
                .maxDosage(3.0)
                .contraindications("十九畏：畏郁金！热病及阴虚内热者忌服")
                .decoctionMethod("常规水煎")
                .classicFormulas(List.of("丁香柿蒂汤"))
                .pharmacopoeiaExcerpt("《本草纲目》：丁香辛烈，暖脾胃而行气。所含丁香酚具广谱抑菌与健胃驱气功效。")
                .build());

        HERB_DATABASE.put("郁金", HerbDetail.builder()
                .name("郁金")
                .pinyin("Yujin")
                .natureFlavor("辛、苦，寒")
                .meridian("归肝、心、肺经")
                .efficacy("活血止痛，行气解郁，清心凉血，利胆退黄")
                .dosageRange("5~12g")
                .maxDosage(12.0)
                .contraindications("十九畏：畏丁香！孕妇慎用")
                .decoctionMethod("常规水煎")
                .classicFormulas(List.of("菖蒲郁金汤", "白金丸"))
                .pharmacopoeiaExcerpt("《药性论》：主女人宿血气块，结癖疼痛。姜黄素类成分具保肝利胆与调脂抗氧化作用。")
                .build());
    }

    /**
     * 获取全景知识图谱数据
     */
    public KnowledgeGraphData getOverviewGraph() {
        List<KnowledgeGraphData.CategoryItem> categories = List.of(
                KnowledgeGraphData.CategoryItem.builder().name("中药材").itemStyle(Map.of("color", "#10b981")).build(),
                KnowledgeGraphData.CategoryItem.builder().name("经典名方").itemStyle(Map.of("color", "#f43f5e")).build(),
                KnowledgeGraphData.CategoryItem.builder().name("性味归经").itemStyle(Map.of("color", "#f59e0b")).build(),
                KnowledgeGraphData.CategoryItem.builder().name("功效分类").itemStyle(Map.of("color", "#06b6d4")).build()
        );

        List<KnowledgeGraphData.NodeItem> nodes = new ArrayList<>();
        List<KnowledgeGraphData.LinkItem> links = new ArrayList<>();

        // 节点列表
        nodes.add(createNode("1", "黄芪", 48, 0, "《中国药典》甘，微温。归肺、脾经。功效：补气升阳，固表止汗，利水消肿。"));
        nodes.add(createNode("2", "当归", 42, 0, "《中国药典》甘、辛，温。归肝、心、脾经。补血活血，调经止痛。"));
        nodes.add(createNode("3", "甘草", 48, 0, "《中国药典》甘，平。归心、肺、脾、胃经。调和诸药。反甘遂、大戟、海藻、芫花！"));
        nodes.add(createNode("4", "甘遂", 34, 0, "《中国药典》苦，寒；有毒。泻水逐饮。十八反：严禁与甘草配伍！"));
        nodes.add(createNode("5", "细辛", 34, 0, "辛，温；有小毒。祛风散寒，通窍止痛。常用极量不超3g。“细辛不过钱”。"));
        nodes.add(createNode("6", "制附子", 40, 0, "辛、甘，大热；有毒。回阳救逆，散寒止痛。【先煎30~60分钟】。反半夏、瓜蒌、贝母。"));
        nodes.add(createNode("7", "半夏", 36, 0, "辛，温；有毒。燥湿化痰，降逆止呕。十八反：反乌头、附子！"));
        nodes.add(createNode("8", "人参", 46, 0, "大补元气，复脉固脱。反藜芦，畏五灵脂。宜文火另煎兑服。"));
        nodes.add(createNode("9", "藜芦", 34, 0, "辛、苦，寒；有毒。涌吐风痰。十八反：反人参、细辛、芍药等诸参！"));
        nodes.add(createNode("10", "白芍", 38, 0, "养血调经，敛阴止汗，柔肝止痛。十八反：反藜芦。"));
        nodes.add(createNode("11", "丁香", 34, 0, "温中降逆，补肾助阳。十九畏：丁香畏郁金。"));
        nodes.add(createNode("12", "郁金", 34, 0, "活血止痛，行气解郁。十九畏：郁金畏丁香。"));

        // 方剂节点
        nodes.add(createNode("20", "补中益气汤", 56, 1, "李东垣《脾胃论》名方。主治脾虚气陷证。君药黄芪，臣以当归、白术，使以甘草。"));
        nodes.add(createNode("21", "当归补血汤", 50, 1, "李东垣名方。黄芪一两、当归二钱 (5:1)，气旺生血经典名方。"));
        nodes.add(createNode("22", "四君子汤", 52, 1, "《太平惠民和剂局方》治脾胃气虚基础方。人参、白术、茯苓、甘草。"));
        nodes.add(createNode("23", "四逆汤", 50, 1, "张仲景《伤寒论》回阳救逆第一方。附子、干姜、炙甘草。"));

        // 归经/功效节点
        nodes.add(createNode("30", "归肺、脾经", 36, 2, "定向调节肺主气与脾统血运化系统。"));
        nodes.add(createNode("31", "归肝、肾经", 36, 2, "定向调节肝藏血疏泄与肾藏精元阴元阳。"));
        nodes.add(createNode("32", "归心、胃经", 36, 2, "定向调节心主血脉与胃受纳水谷。"));
        nodes.add(createNode("40", "补气升阳", 36, 3, "益气固表、提托中气。"));
        nodes.add(createNode("41", "补血活血", 36, 3, "滋养阴血、化瘀止痛。"));

        // 关系连线
        links.add(createLink("1", "20", "君药", null, null));
        links.add(createLink("2", "20", "佐药", null, null));
        links.add(createLink("3", "20", "使药", null, null));

        links.add(createLink("1", "21", "主药(5:1)", null, null));
        links.add(createLink("2", "21", "辅药", null, null));

        links.add(createLink("8", "22", "君药", null, null));
        links.add(createLink("3", "22", "使药", null, null));

        links.add(createLink("6", "23", "君药(先煎)", null, null));
        links.add(createLink("3", "23", "使药", null, null));

        links.add(createLink("1", "30", "归经", null, null));
        links.add(createLink("1", "40", "功效", null, null));
        links.add(createLink("2", "31", "归经", null, null));
        links.add(createLink("2", "41", "功效", null, null));

        // 🚨 严禁配伍冲突红线 (十八反 / 十九畏)
        links.add(createConflictLink("3", "4", "【十八反】甘草反甘遂"));
        links.add(createConflictLink("6", "7", "【十八反】附子反半夏"));
        links.add(createConflictLink("8", "9", "【十八反】诸参反藜芦"));
        links.add(createConflictLink("5", "9", "【十八反】细辛反藜芦"));
        links.add(createConflictLink("10", "9", "【十八反】芍药反藜芦"));
        links.add(createConflictLink("11", "12", "【十九畏】丁香畏郁金"));

        return KnowledgeGraphData.builder()
                .categories(categories)
                .nodes(nodes)
                .links(links)
                .build();
    }

    /**
     * 根据中药名称考据药典明细
     */
    public HerbDetail getHerbDetail(String herbName) {
        if (herbName == null || herbName.isBlank()) {
            return null;
        }
        String canonical = herbNormalizer.normalize(herbName);
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

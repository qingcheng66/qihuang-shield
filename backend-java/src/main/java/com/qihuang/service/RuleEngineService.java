package com.qihuang.service;

import com.qihuang.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 岐黄盾 - 中医药配伍安全规则审查核心引擎
 * 采用纯内存 Set 集合交叉碰撞与极量字典比对，实现 100% 毫秒级零延迟审计。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final HerbNormalizer normalizer;

    // ================= 规则数据定义 =================

    /** 十八反规则模型定义 */
    private record IncompatibilityRule(
            String groupName,
            Set<String> setA,
            Set<String> setB,
            String quote,
            String mechanism
    ) {}

    /** 十九畏规则模型定义 */
    private record AntagonismRule(
            String herbA,
            String herbB,
            String quote,
            String mechanism
    ) {}

    /** 极量限制模型定义 */
    private record DosageLimitRule(
            String herb,
            double maxLimit,
            String riskDesc
    ) {}

    /** 特殊煎服法模型定义 */
    private record DecoctionRule(
            Set<String> herbs,
            String method,
            String instructions
    ) {}

    private static final List<IncompatibilityRule> SHI_BA_FAN = new ArrayList<>();
    private static final List<AntagonismRule> SHI_JIU_WEI = new ArrayList<>();
    private static final Map<String, DosageLimitRule> DOSAGE_LIMITS = new HashMap<>();
    private static final List<DecoctionRule> DECOCTION_RULES = new ArrayList<>();

    static {
        initShiBaFan();
        initShiJiuWei();
        initDosageLimits();
        initDecoctionRules();
    }

    /** 1. 初始化十八反规则 */
    private static void initShiBaFan() {
        // 第一组：甘草 反 甘遂、大戟、海藻、芫花
        SHI_BA_FAN.add(new IncompatibilityRule(
                "甘草反甘遂组",
                Set.of("甘草"),
                Set.of("甘遂", "大戟", "海藻", "芫花"),
                "《本草纲目》草部：“甘草反甘遂、大戟、海藻、芫花。相反者，激而生变，不可同用。”",
                "甘草主要活性成分甘草酸抑制肠道 P-糖蛋白外排泵，导致甘遂/大戟/芫花中二萜毒性成分体内蓄积量剧增，诱发严重胃肠炎与心脏早搏。"
        ));

        // 第二组：乌头 (川乌、草乌、附子) 反 半夏、瓜蒌、贝母、白蔹、白及
        SHI_BA_FAN.add(new IncompatibilityRule(
                "乌头反半夏瓜蒌贝母组",
                Set.of("乌头", "附子"),
                Set.of("半夏", "瓜蒌", "贝母", "白蔹", "白及"),
                "《神农本草经》：“乌头反半夏、瓜蒌、贝母、白蔹、白及。”",
                "乌头中双酯型乌头碱具有极强的心脏与神经毒性，与半夏等同煎时可加速乌头碱溶出或延缓降解，大幅增加急性乌头碱中毒风险。"
        ));

        // 第三组：藜芦 反 诸参 (人参、党参、沙参、丹参、玄参、苦参)、细辛、芍药
        SHI_BA_FAN.add(new IncompatibilityRule(
                "藜芦反诸参细辛芍药组",
                Set.of("藜芦"),
                Set.of("人参", "党参", "沙参", "丹参", "玄参", "苦参", "细辛", "芍药"),
                "《儒门事亲》：“诸参辛芍叛藜芦。”",
                "藜芦生物碱毒性剧烈，与诸参配伍不仅产生严重胃肠道痉挛催吐反应，且两药协同加剧低血压与中枢抑制。"
        ));
    }

    /** 2. 初始化十九畏规则 */
    private static void initShiJiuWei() {
        registerAntagonism("硫黄", "芒硝", "《医学正传》：“硫黄原是火中精，朴硝一见便相争。”", "硫黄与芒硝同用易生成有毒硫化物，损伤脾胃与黏膜。");
        registerAntagonism("水银", "砒霜", "《十九畏歌》：“水银莫与砒霜见。”", "两剧毒药物相合毒性剧增，有极高肝肾功能衰竭风险。");
        registerAntagonism("狼毒", "密陀僧", "《十九畏歌》：“狼毒最怕密陀僧。”", "铅化合物与二萜类相恶，加重中枢与消化系统毒性。");
        registerAntagonism("巴豆", "牵牛子", "《十九畏歌》：“巴豆性烈最为急，偏与牵牛不顺情。”", "峻下逐水药物相配，易致严重脱水、电解质紊乱及肠麻痹。");
        registerAntagonism("丁香", "郁金", "《十九畏歌》：“丁香莫与郁金见。”", "气味相反，丁香温中降逆与郁金凉血破瘀药效相互抵消。");
        registerAntagonism("乌头", "犀角", "《十九畏歌》：“川乌草乌不顺犀。”", "大热与大寒药相恶削弱疗效，且增加机体代谢负荷。");
        registerAntagonism("附子", "犀角", "《十九畏歌》：“川乌草乌不顺犀（附子同理）。”", "附子大热与水牛角/犀角大寒相激，影响心肌电生理。");
        registerAntagonism("芒硝", "三棱", "《十九畏歌》：“芒硝三棱最为劣。”", "咸寒软坚与苦平破血相合，易引起胃肠剧烈阵痛与出血倾向。");
        registerAntagonism("肉桂", "赤石脂", "《十九畏歌》：“官桂善能调冷气，若逢石脂便相欺。”", "赤石脂吸附肉桂挥发油，使肉桂温阳补火药效大幅降低。");
        registerAntagonism("人参", "五灵脂", "《十九畏歌》：“人参最怕五灵脂。”", "人参补气与五灵脂活血散瘀性味相反，削弱补益之力。");
    }

    private static void registerAntagonism(String a, String b, String quote, String mechanism) {
        SHI_JIU_WEI.add(new AntagonismRule(a, b, quote, mechanism));
    }

    /** 3. 初始化中国药典极量限制 (单剂煎剂上限) */
    private static void initDosageLimits() {
        DOSAGE_LIMITS.put("细辛", new DosageLimitRule("细辛", 3.0, "“细辛不过钱”。药典常用极量为 3.0g，超量挥发油黄樟醚可致中枢麻痹，且含微量马兜铃酸蓄积肾毒性。"));
        DOSAGE_LIMITS.put("附子", new DosageLimitRule("附子", 15.0, "制附子常用极量为 15.0g。含乌头碱心脏毒性，大剂量使用时必须久煎 1~2 小时。"));
        DOSAGE_LIMITS.put("乌头", new DosageLimitRule("乌头", 3.0, "川乌/草乌剧毒，常用量 1.5~3g，超量易致致死性室性心律失常。"));
        DOSAGE_LIMITS.put("朱砂", new DosageLimitRule("朱砂", 0.5, "朱砂含硫化汞，常用极量 0.5g。忌火煅，不宜过量久服，防汞中毒。"));
        DOSAGE_LIMITS.put("全蝎", new DosageLimitRule("全蝎", 6.0, "有毒动物药，常用极量 6.0g，过量可引起头晕、呼吸减慢等中毒反应。"));
        DOSAGE_LIMITS.put("马钱子", new DosageLimitRule("马钱子", 0.6, "剧毒中药，常用极量为 0.3~0.6g，超量直接诱发强直性惊厥与呼吸衰竭。"));
    }

    /** 4. 初始化特殊煎服法规则 */
    private static void initDecoctionRules() {
        DECOCTION_RULES.add(new DecoctionRule(
                Set.of("附子", "乌头", "生石膏", "磁石", "鳖甲", "龟甲", "水牛角", "犀角", "龙骨", "牡蛎"),
                "先煎",
                "矿物、介壳类有效成分不易煎出，或乌头类有毒中药需降解毒性，需文火单独先煎 30~60 分钟后再下群药。"
        ));

        DECOCTION_RULES.add(new DecoctionRule(
                Set.of("薄荷", "砂仁", "沉香", "豆蔻", "肉桂"),
                "后下",
                "含有挥发性芳香成分或受热易破坏药效，宜在出锅前 5~10 分钟放入煎煮。"
        ));

        DECOCTION_RULES.add(new DecoctionRule(
                Set.of("旋覆花", "车前子", "辛夷", "蒲黄", "海金沙"),
                "包煎",
                "含细小绒毛易刺激咽喉黏膜，或黏性易糊锅底，需用医用纱布袋包扎后入锅煎煮。"
        ));

        DECOCTION_RULES.add(new DecoctionRule(
                Set.of("阿胶", "鹿角胶", "龟甲胶", "蜂蜜"),
                "烊化",
                "胶质药物直接同煎易粘锅焦化，应在煎好的汤药中趁热倒入搅拌完全融化（烊化）后温服。"
        ));
    }

    // ================= 核心审查调度算法 =================

    /**
     * 对处方进行全量安全审计
     */
    public AuditResult auditPrescription(AuditRequest request) {
        long startTime = System.currentTimeMillis();
        List<HerbItem> herbs = request.getHerbs() == null ? Collections.emptyList() : request.getHerbs();

        List<ConflictWarning> fatalWarnings = new ArrayList<>();
        List<OverdoseWarning> overdoseWarnings = new ArrayList<>();
        List<DecoctionNote> decoctionNotes = new ArrayList<>();

        // 1. 建立归一化映射表：Map<标准名, List<原始HerbItem>>
        Map<String, List<HerbItem>> canonicalMap = new HashMap<>();
        for (HerbItem item : herbs) {
            String norm = normalizer.normalize(item.getName());
            if (!norm.isEmpty()) {
                canonicalMap.computeIfAbsent(norm, k -> new ArrayList<>()).add(item);
            }
        }

        Set<String> presentHerbs = canonicalMap.keySet();

        // 2. 十八反排查
        for (IncompatibilityRule rule : SHI_BA_FAN) {
            // 找出处方中属于 setA 的中药
            List<String> matchedA = findMatches(presentHerbs, rule.setA());
            // 找出处方中属于 setB 的中药
            List<String> matchedB = findMatches(presentHerbs, rule.setB());

            if (!matchedA.isEmpty() && !matchedB.isEmpty()) {
                for (String herbA : matchedA) {
                    for (String herbB : matchedB) {
                        String origA = canonicalMap.get(herbA).get(0).getName();
                        String origB = canonicalMap.get(herbB).get(0).getName();

                        fatalWarnings.add(ConflictWarning.builder()
                                .type("十八反")
                                .pair(List.of(herbA, herbB))
                                .originalNames(List.of(origA, origB))
                                .quote(rule.quote())
                                .mechanism(rule.mechanism())
                                .severity("FATAL")
                                .build());
                    }
                }
            }
        }

        // 3. 十九畏排查
        for (AntagonismRule rule : SHI_JIU_WEI) {
            if (presentHerbs.contains(rule.herbA()) && presentHerbs.contains(rule.herbB())) {
                String origA = canonicalMap.get(rule.herbA()).get(0).getName();
                String origB = canonicalMap.get(rule.herbB()).get(0).getName();

                fatalWarnings.add(ConflictWarning.builder()
                        .type("十九畏")
                        .pair(List.of(rule.herbA(), rule.herbB()))
                        .originalNames(List.of(origA, origB))
                        .quote(rule.quote())
                        .mechanism(rule.mechanism())
                        .severity("HARMFUL")
                        .build());
            }
        }

        // 4. 药典极量超标排查
        for (Map.Entry<String, List<HerbItem>> entry : canonicalMap.entrySet()) {
            String canonicalName = entry.getKey();
            if (DOSAGE_LIMITS.containsKey(canonicalName)) {
                DosageLimitRule limitRule = DOSAGE_LIMITS.get(canonicalName);
                // 累计同味药总剂量
                double totalDose = entry.getValue().stream()
                        .mapToDouble(h -> h.getDose() != null ? h.getDose() : 0.0)
                        .sum();

                if (totalDose > limitRule.maxLimit()) {
                    overdoseWarnings.add(OverdoseWarning.builder()
                            .herb(entry.getValue().get(0).getName())
                            .actualDose(totalDose)
                            .maxLimit(limitRule.maxLimit())
                            .unit("g")
                            .warningText(limitRule.riskDesc())
                            .build());
                }
            }
        }

        // 5. 特殊煎服法提醒排查
        for (DecoctionRule decRule : DECOCTION_RULES) {
            for (String decHerb : decRule.herbs()) {
                if (presentHerbs.contains(decHerb)) {
                    HerbItem item = canonicalMap.get(decHerb).get(0);
                    decoctionNotes.add(DecoctionNote.builder()
                            .herb(item.getName())
                            .method(decRule.method())
                            .instructions(decRule.instructions())
                            .build());
                }
            }
        }

        // 6. 综合判定状态
        String status;
        String summary;
        if (!fatalWarnings.isEmpty()) {
            status = "DANGER";
            summary = String.format("处方拦截：检测到 %d 处中药法定配伍禁忌（依法严禁同方）！", fatalWarnings.size());
        } else if (!overdoseWarnings.isEmpty()) {
            status = "WARNING";
            summary = String.format("风险警告：检测到 %d 处药典极量超标中药！", overdoseWarnings.size());
        } else {
            status = "SAFE";
            summary = "处方合规：未发现法定配伍禁忌与超量风险，符合七情配伍规范。";
        }

        long executionTime = System.currentTimeMillis() - startTime;

        return AuditResult.builder()
                .status(status)
                .summary(summary)
                .herbCount(herbs.size())
                .fatalWarnings(fatalWarnings)
                .overdoseWarnings(overdoseWarnings)
                .decoctionNotes(decoctionNotes)
                .auditTimestamp(System.currentTimeMillis())
                .executionTimeMs(executionTime)
                .build();
    }

    private List<String> findMatches(Set<String> presentHerbs, Set<String> targetSet) {
        List<String> matches = new ArrayList<>();
        for (String herb : presentHerbs) {
            if (targetSet.contains(herb)) {
                matches.add(herb);
            }
        }
        return matches;
    }
}

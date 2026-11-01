package com.qihuang.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 中药名称规范化与别名归一化工具
 * 解决处方中包含炮制前缀（如“炙甘草”、“制附子”、“炒白术”）导致的规则匹配失效问题。
 */
@Component
public class HerbNormalizer {

    private static final Map<String, String> ALIAS_MAP = new HashMap<>();

    static {
        // 甘草族
        register("甘草", "生甘草", "炙甘草", "甘草片", "蜜甘草", "粉甘草");

        // 乌头族（川乌、草乌、附子统属乌头类十八反范畴）
        register("乌头", "川乌", "生川乌", "制川乌", "草乌", "生草乌", "制草乌", "乌头");
        register("附子", "制附子", "生附子", "黑顺片", "白附片", "附片", "熟附子");

        // 半夏族
        register("半夏", "生半夏", "法半夏", "姜半夏", "清半夏", "半夏曲");

        // 瓜蒌族
        register("瓜蒌", "全瓜蒌", "栝楼", "瓜蒌仁", "瓜蒌皮", "瓜蒌根", "天花粉");

        // 贝母族
        register("贝母", "川贝母", "浙贝母", "川贝", "浙贝", "土贝母");

        // 参族
        register("人参", "生晒参", "红参", "高丽参", "野山参", "吉林参");
        register("党参", "潞党参", "炒党参");
        register("沙参", "南沙参", "北沙参");
        register("丹参", "生丹参", "酒丹参");
        register("玄参", "元参");
        register("苦参", "苦参片");

        // 细辛族
        register("细辛", "北细辛", "辽细辛");

        // 芍药族
        register("芍药", "白芍", "生白芍", "炒白芍", "酒白芍", "赤芍", "生赤芍");

        // 白术族
        register("白术", "生白术", "炒白术", "焦白术");

        // 肉桂族
        register("肉桂", "官桂", "油桂", "桂心");

        // 芒硝族
        register("芒硝", "玄明粉", "朴硝", "风化硝");

        // 犀角族
        register("犀角", "水牛角", "水牛角片", "水牛角粉");

        // 丁香族
        register("丁香", "公丁香", "母丁香", "丁香花");

        // 郁金族
        register("郁金", "广郁金", "川郁金", "黄郁金", "温郁金");

        // 硫黄族
        register("硫黄", "硫磺", "生硫黄");

        // 砒霜族
        register("砒霜", "白砒", "红砒", "砒石");

        // 狼毒族
        register("狼毒", "白狼毒", "黑狼毒");

        // 密陀僧族
        register("密陀僧", "陀僧");

        // 巴豆族
        register("巴豆", "巴豆霜", "巴豆仁");

        // 牵牛子族
        register("牵牛子", "黑丑", "白丑", "二丑", "黑牵牛", "白牵牛");

        // 三棱族
        register("三棱", "荆三棱", "醋三棱");

        // 赤石脂族
        register("赤石脂");

        // 五灵脂族
        register("五灵脂", "灵脂", "炒五灵脂", "醋五灵脂");
    }

    private static void register(String canonical, String... aliases) {
        for (String alias : aliases) {
            ALIAS_MAP.put(alias, canonical);
        }
        ALIAS_MAP.put(canonical, canonical);
    }

    /**
     * 将输入的药材名称归一化为其标准正名
     * 例如："生甘草" -> "甘草", "制附子" -> "附子"
     */
    public String normalize(String rawName) {
        if (rawName == null || rawName.trim().isEmpty()) {
            return "";
        }
        String clean = rawName.trim()
                .replaceAll("[(（].*?[)）]", "") // 剔除括号附注如 (先煎)
                .replaceAll("[0-9.一二三四五六七八九十g克两钱]+$", ""); // 剔除末尾剂量

        // 1. 直接命中别名表
        if (ALIAS_MAP.containsKey(clean)) {
            return ALIAS_MAP.get(clean);
        }

        // 2. 去除常见炮制前缀再试
        String stripped = clean.replaceAll("^(生|炒|炙|制|煅|醋|酒|蜜|煨|焦|熟)", "");
        if (ALIAS_MAP.containsKey(stripped)) {
            return ALIAS_MAP.get(stripped);
        }

        // 3. 包含匹配降级
        for (Map.Entry<String, String> entry : ALIAS_MAP.entrySet()) {
            if (clean.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return stripped;
    }

    /**
     * 判断某药材是否归属于“乌头类”（川乌、草乌、附子均归属乌头类反药）
     */
    public boolean isWutouFamily(String canonicalName) {
        return "乌头".equals(canonicalName) || "附子".equals(canonicalName);
    }
}

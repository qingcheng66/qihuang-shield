package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 配伍禁忌冲突警报 (十八反 / 十九畏)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConflictWarning {
    /** 禁忌类型：十八反 / 十九畏 */
    private String type;

    /** 冲突药材对 (如 ["甘草", "甘遂"]) */
    private List<String> pair;

    /** 药材原始名称 (如 ["生甘草", "醋甘遂"]) */
    private List<String> originalNames;

    /** 古籍文献依据 */
    private String quote;

    /** 现代药理/毒理机制说明 */
    private String mechanism;

    /** 危害等级：FATAL (致命/剧毒) / HARMFUL (减效/损伤) */
    @Builder.Default
    private String severity = "FATAL";
}

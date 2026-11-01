package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 处方配伍审查综合评估报告
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditResult {
    /** 综合安全状态：SAFE (安全通过 🟢) / WARNING (存在风险 🟡) / DANGER (致命禁忌 🔴) */
    private String status;

    /** 状态简要描述 */
    private String summary;

    /** 处方中识别出的有效中药味数 */
    private Integer herbCount;

    /** 致命配伍禁忌列表 (十八反、十九畏) */
    @Builder.Default
    private List<ConflictWarning> fatalWarnings = new ArrayList<>();

    /** 剂量超标警告列表 (细辛、附子等极量) */
    @Builder.Default
    private List<OverdoseWarning> overdoseWarnings = new ArrayList<>();

    /** 特殊煎服法操作提示 (先煎、后下等) */
    @Builder.Default
    private List<DecoctionNote> decoctionNotes = new ArrayList<>();

    /** 审查完成时间戳 */
    @Builder.Default
    private Long auditTimestamp = System.currentTimeMillis();

    /** 质检规则引擎耗时 (ms) */
    private Long executionTimeMs;
}

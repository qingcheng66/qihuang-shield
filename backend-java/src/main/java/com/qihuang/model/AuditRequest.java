package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 处方配伍审查请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditRequest {
    /** 待审查处方中的药材列表 */
    private List<HerbItem> herbs;

    /** 处方名称（可选，如：补中益气汤加减） */
    private String prescriptionName;

    /** 用药人群属性（ADULT: 成人, PREGNANT: 孕妇, CHILD: 儿童） */
    @Builder.Default
    private String patientType = "ADULT";
}

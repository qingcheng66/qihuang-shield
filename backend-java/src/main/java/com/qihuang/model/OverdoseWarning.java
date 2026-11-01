package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 药典极量超标警告
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverdoseWarning {
    /** 药材标准名 */
    private String herb;

    /** 处方实际剂量 */
    private Double actualDose;

    /** 《中国药典》规定极量上限 */
    private Double maxLimit;

    /** 剂量单位 */
    @Builder.Default
    private String unit = "g";

    /** 毒性风险警示说明 */
    private String warningText;
}

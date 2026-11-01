package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 处方中的单味中药条目
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HerbItem {
    /** 药材原始名称（如：炙甘草、炒白术、细辛） */
    private String name;

    /** 剂量数值 */
    private Double dose;

    /** 计量单位（默认为 g） */
    @Builder.Default
    private String unit = "g";

    /** 炮制方法（如：生、炒、炙、醋制、酒制） */
    private String processing;

    /** 特殊煎服法（如：先煎、后下、包煎、烊化） */
    private String decoctionMethod;

    public HerbItem(String name, Double dose) {
        this.name = name;
        this.dose = dose;
        this.unit = "g";
    }
}

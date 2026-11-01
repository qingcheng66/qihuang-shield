package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 中药药典考据明细数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HerbDetail {
    private String name;
    private String pinyin;
    private String natureFlavor;      // 性味: 如 "甘，平"
    private String meridian;          // 归经: 如 "归心、肺、脾、胃经"
    private String efficacy;          // 功效: 如 "补脾益气，清热解毒，祛痰止咳，缓急止痛，调和诸药"
    private String dosageRange;       // 常用剂量: 如 "2~10g"
    private Double maxDosage;         // 药典极量 (g)
    private String contraindications; // 配伍禁忌: 如 "十八反：反甘遂、大戟、海藻、芫花"
    private String decoctionMethod;   // 煎服法说明
    private List<String> classicFormulas; // 经典名方
    private String pharmacopoeiaExcerpt; // 药典原论/古籍引文
}

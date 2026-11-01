package com.qihuang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱全景图数据模型 (适配 ECharts Graph 系列)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeGraphData {
    private List<CategoryItem> categories;
    private List<NodeItem> nodes;
    private List<LinkItem> links;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem {
        private String name;
        private Map<String, Object> itemStyle;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeItem {
        private String id;
        private String name;
        private double symbolSize;
        private int category;
        private String detail;
        private Map<String, Object> itemStyle;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkItem {
        private String source;
        private String target;
        private Map<String, Object> label;
        private Map<String, Object> lineStyle;
    }
}

package com.qihuang.service;

import com.qihuang.model.HerbDetail;
import com.qihuang.model.KnowledgeGraphData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("知识图谱与古籍本草专论服务测试")
public class KnowledgeGraphServiceTest {

    private KnowledgeGraphService knowledgeGraphService;

    @BeforeEach
    void setUp() {
        HerbNormalizer herbNormalizer = new HerbNormalizer();
        knowledgeGraphService = new KnowledgeGraphService(herbNormalizer);
    }

    @Test
    @DisplayName("测试知识图谱全景数据生成")
    void testGetOverviewGraph() {
        KnowledgeGraphData graph = knowledgeGraphService.getOverviewGraph();
        assertNotNull(graph);
        assertFalse(graph.getCategories().isEmpty());
        assertTrue(graph.getNodes().size() >= 10, "节点数应当大于等于10");
        assertTrue(graph.getLinks().size() >= 10, "关系数应当大于等于10");

        // 验证是否存在十八反连线
        boolean hasConflict = graph.getLinks().stream()
                .anyMatch(link -> link.getLineStyle() != null && "#ef4444".equals(link.getLineStyle().get("color")));
        assertTrue(hasConflict, "图谱中应当包含十八反/十九畏高危红线");
    }

    @Test
    @DisplayName("测试中药考据明细查询")
    void testGetHerbDetail() {
        HerbDetail detail = knowledgeGraphService.getHerbDetail("炙甘草");
        assertNotNull(detail);
        assertEquals("甘草", detail.getName());
        assertTrue(detail.getContraindications().contains("甘遂"));
        assertNotNull(detail.getPharmacopoeiaExcerpt());

        // 测试细辛极量
        HerbDetail xixin = knowledgeGraphService.getHerbDetail("北细辛");
        assertNotNull(xixin);
        assertEquals("细辛", xixin.getName());
        assertEquals(3.0, xixin.getMaxDosage());
    }

    @Test
    @DisplayName("测试全量规则字典返回")
    void testGetAllRulesDetail() {
        Map<String, Object> rules = knowledgeGraphService.getAllRulesDetail();
        assertNotNull(rules);
        assertTrue(rules.containsKey("shiBaFan"));
        assertTrue(rules.containsKey("shiJiuWei"));
        assertTrue(rules.containsKey("dosageLimits"));
        assertTrue(rules.containsKey("decoctions"));
    }
}

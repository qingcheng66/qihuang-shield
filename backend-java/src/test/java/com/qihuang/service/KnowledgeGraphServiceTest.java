package com.qihuang.service;

import com.qihuang.model.HerbDetail;
import com.qihuang.model.KnowledgeGraphData;
import com.qihuang.repository.FormulaCatalogRepository;
import com.qihuang.repository.HerbDictRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("知识图谱与古籍本草专论服务测试")
public class KnowledgeGraphServiceTest {

    private KnowledgeGraphService knowledgeGraphService;
    private HerbDictRepository herbDictRepository;
    private FormulaCatalogRepository formulaCatalogRepository;

    @BeforeEach
    void setUp() {
        HerbNormalizer herbNormalizer = new HerbNormalizer();
        herbDictRepository = Mockito.mock(HerbDictRepository.class);
        formulaCatalogRepository = Mockito.mock(FormulaCatalogRepository.class);

        Mockito.when(herbDictRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(formulaCatalogRepository.findAll()).thenReturn(Collections.emptyList());

        knowledgeGraphService = new KnowledgeGraphService(herbNormalizer, herbDictRepository, formulaCatalogRepository);
    }

    @Test
    @DisplayName("测试知识图谱全景数据生成 (含兜底防御)")
    void testGetOverviewGraph() {
        KnowledgeGraphData graph = knowledgeGraphService.getOverviewGraph();
        assertNotNull(graph);
        assertFalse(graph.getCategories().isEmpty());
        assertTrue(graph.getNodes().size() >= 4, "节点数应当大于等于4");
        assertTrue(graph.getLinks().size() >= 3, "关系数应当大于等于3");

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

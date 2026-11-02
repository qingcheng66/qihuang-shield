package com.qihuang.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.qihuang.entity.FormulaCatalogEntity;
import com.qihuang.entity.HerbDictEntity;
import com.qihuang.repository.FormulaCatalogRepository;
import com.qihuang.repository.HerbDictRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库启动自动装载器 (DataInitializerRunner)
 * 系统初次启动时，自动装载 300+ 味《中国药典》中草药基准数据与 55 首古代经典名方
 * 支持本地 H2 持久化与生产 PostgreSQL 双模无缝兼容
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializerRunner implements ApplicationRunner {

    private final HerbDictRepository herbDictRepository;
    private final FormulaCatalogRepository formulaCatalogRepository;
    private final ResourceLoader resourceLoader;

    @Override
    public void run(ApplicationArguments args) {
        try {
            initHerbsData();
            initFormulasData();
        } catch (Exception e) {
            log.error("自动化数据库种子数据装载异常: {}", e.getMessage(), e);
        }
    }

    private void initHerbsData() {
        long count = herbDictRepository.count();
        if (count > 0) {
            log.info("【数据库检查】中药典籍词典表已存在 {} 条数据，跳过初始装载", count);
            return;
        }

        log.info("【数据库初始化】正在从权威中药数据集 (herbs_dataset.json) 自动灌入基准数据...");
        String jsonContent = loadJsonContent("classpath:data/herbs_dataset.json", "file:data/herbs_dataset.json");
        if (jsonContent == null || jsonContent.isBlank()) {
            log.warn("未找到 herbs_dataset.json 数据源，跳过中药基准装载");
            return;
        }

        JSONArray array = JSON.parseArray(jsonContent);
        List<HerbDictEntity> entities = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String herbName = obj.getString("herb_name");
            if (herbName == null || herbName.isBlank()) continue;

            Double maxDose = obj.getDouble("max_dosage");
            BigDecimal maxDosageBd = (maxDose != null) ? BigDecimal.valueOf(maxDose) : null;

            JSONArray aliasesArr = obj.getJSONArray("aliases");
            String aliasesJson = (aliasesArr != null) ? aliasesArr.toJSONString() : "[]";

            HerbDictEntity entity = HerbDictEntity.builder()
                    .herbName(herbName)
                    .pinyin(obj.getString("pinyin"))
                    .aliases(aliasesJson)
                    .nature(obj.getString("nature"))
                    .flavor(obj.getString("flavor"))
                    .meridians(obj.getString("meridians"))
                    .toxicityLevel(obj.getString("toxicity_level"))
                    .maxDosage(maxDosageBd)
                    .dosageRange(obj.getString("dosage_range"))
                    .efficacy(obj.getString("efficacy"))
                    .contraindications(obj.getString("contraindications"))
                    .decoctionMethod(obj.getString("decoction_method"))
                    .build();

            entities.add(entity);
        }

        herbDictRepository.saveAll(entities);
        log.info("🎉 【数据库初始化完成】成功将 {} 味《中国药典》临床中药材灌入数据库！", entities.size());
    }

    private void initFormulasData() {
        long count = formulaCatalogRepository.count();
        if (count > 0) {
            log.info("【数据库检查】古代经典名方目录已存在 {} 首方剂，跳过初始装载", count);
            return;
        }

        log.info("【数据库初始化】正在从古代经典名方数据集 (formulas_dataset.json) 自动灌入基准数据...");
        String jsonContent = loadJsonContent("classpath:data/formulas_dataset.json", "file:data/formulas_dataset.json");
        if (jsonContent == null || jsonContent.isBlank()) {
            log.warn("未找到 formulas_dataset.json 数据源，跳过方剂目录装载");
            return;
        }

        JSONArray array = JSON.parseArray(jsonContent);
        List<FormulaCatalogEntity> entities = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String formulaName = obj.getString("formula_name");
            if (formulaName == null || formulaName.isBlank()) continue;

            JSONArray compArr = obj.getJSONArray("composition");
            String compJson = (compArr != null) ? compArr.toJSONString() : "[]";

            FormulaCatalogEntity entity = FormulaCatalogEntity.builder()
                    .formulaName(formulaName)
                    .sourceBook(obj.getString("source_book"))
                    .author(obj.getString("author"))
                    .category(obj.getString("category"))
                    .composition(compJson)
                    .indications(obj.getString("indications"))
                    .monarchHerb(obj.getString("monarch_herb"))
                    .ministerHerb(obj.getString("minister_herb"))
                    .assistantHerb(obj.getString("assistant_herb"))
                    .guideHerb(obj.getString("guide_herb"))
                    .build();

            entities.add(entity);
        }

        formulaCatalogRepository.saveAll(entities);
        log.info("🎉 【数据库初始化完成】成功将 {} 首国家法定经典名方灌入数据库！", entities.size());
    }

    private String loadJsonContent(String classpathLocation, String fileLocation) {
        try {
            Resource res = resourceLoader.getResource(classpathLocation);
            if (res.exists()) {
                try (InputStream is = res.getInputStream()) {
                    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            res = resourceLoader.getResource(fileLocation);
            if (res.exists()) {
                try (InputStream is = res.getInputStream()) {
                    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            log.warn("读取数据文件失败 ({}, {}): {}", classpathLocation, fileLocation, e.getMessage());
        }
        return null;
    }
}

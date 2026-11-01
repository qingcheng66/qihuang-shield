package com.qihuang.repository;

import com.qihuang.entity.FormulaCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormulaCatalogRepository extends JpaRepository<FormulaCatalogEntity, Long> {
    Optional<FormulaCatalogEntity> findByFormulaName(String formulaName);
}

package com.qihuang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 经典名方与方剂元数据实体
 */
@Entity
@Table(name = "tcm_formula_catalog", indexes = {
        @Index(name = "idx_formula_name", columnList = "formula_name"),
        @Index(name = "idx_formula_category", columnList = "category")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulaCatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "formula_name", nullable = false, unique = true, length = 128)
    private String formulaName;

    @Column(name = "source_book", nullable = false, length = 128)
    private String sourceBook;

    @Column(name = "author", length = 64)
    private String author;

    @Column(name = "category", length = 64)
    private String category;

    @Column(name = "composition", columnDefinition = "TEXT", nullable = false)
    private String composition;

    @Column(name = "indications", columnDefinition = "TEXT")
    private String indications;

    @Column(name = "monarch_herb", length = 64)
    private String monarchHerb;

    @Column(name = "minister_herb", length = 128)
    private String ministerHerb;

    @Column(name = "assistant_herb", length = 128)
    private String assistantHerb;

    @Column(name = "guide_herb", length = 64)
    private String guideHerb;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

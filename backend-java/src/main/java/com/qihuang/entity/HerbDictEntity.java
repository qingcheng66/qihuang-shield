package com.qihuang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 《中国药典》中草药基准词典实体
 */
@Entity
@Table(name = "tcm_herb_dict", indexes = {
        @Index(name = "idx_herb_name", columnList = "herb_name"),
        @Index(name = "idx_herb_toxicity", columnList = "toxicity_level")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HerbDictEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "herb_name", nullable = false, unique = true, length = 64)
    private String herbName;

    @Column(name = "pinyin", length = 128)
    private String pinyin;

    @Column(name = "aliases", columnDefinition = "TEXT")
    private String aliases;

    @Column(name = "nature", length = 64)
    private String nature;

    @Column(name = "flavor", length = 64)
    private String flavor;

    @Column(name = "meridians", length = 128)
    private String meridians;

    @Column(name = "toxicity_level", length = 32)
    private String toxicityLevel;

    @Column(name = "max_dosage", precision = 6, scale = 2)
    private BigDecimal maxDosage;

    @Column(name = "dosage_range", length = 64)
    private String dosageRange;

    @Column(name = "efficacy", columnDefinition = "TEXT")
    private String efficacy;

    @Column(name = "contraindications", columnDefinition = "TEXT")
    private String contraindications;

    @Column(name = "decoction_method", length = 128)
    private String decoctionMethod;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

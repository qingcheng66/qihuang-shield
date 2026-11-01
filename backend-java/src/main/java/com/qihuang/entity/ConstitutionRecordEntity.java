package com.qihuang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 中医体质辨识与健康档案实体
 */
@Entity
@Table(name = "constitution_assessment_record", indexes = {
        @Index(name = "idx_constitution_primary", columnList = "primary_type"),
        @Index(name = "idx_constitution_created", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstitutionRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_identifier", length = 64)
    private String userIdentifier;

    @Column(name = "scores", columnDefinition = "TEXT", nullable = false)
    private String scores;

    @Column(name = "primary_type", nullable = false, length = 64)
    private String primaryType;

    @Column(name = "tendency_types", length = 128)
    private String tendencyTypes;

    @Column(name = "conditioning_plan", columnDefinition = "TEXT")
    private String conditioningPlan;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

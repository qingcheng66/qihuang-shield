package com.qihuang.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 处方审查审计留痕实体
 */
@Entity
@Table(name = "prescription_audit_record", indexes = {
        @Index(name = "idx_audit_serial", columnList = "audit_serial_no"),
        @Index(name = "idx_audit_status", columnList = "safety_status"),
        @Index(name = "idx_audit_created", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "audit_serial_no", nullable = false, unique = true, length = 64)
    private String auditSerialNo;

    @Column(name = "raw_text", columnDefinition = "TEXT", nullable = false)
    private String rawText;

    @Column(name = "detected_herbs", columnDefinition = "TEXT", nullable = false)
    private String detectedHerbs;

    @Column(name = "safety_status", nullable = false, length = 32)
    private String safetyStatus;

    @Column(name = "contraindications", columnDefinition = "TEXT")
    private String contraindications;

    @Column(name = "ai_analysis", columnDefinition = "TEXT")
    private String aiAnalysis;

    @Column(name = "rag_citations", columnDefinition = "TEXT")
    private String ragCitations;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

package com.qihuang.repository;

import com.qihuang.entity.PrescriptionAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionAuditRepository extends JpaRepository<PrescriptionAuditEntity, Long> {
    Optional<PrescriptionAuditEntity> findByAuditSerialNo(String auditSerialNo);
}

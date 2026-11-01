package com.qihuang.repository;

import com.qihuang.entity.ConstitutionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstitutionRecordRepository extends JpaRepository<ConstitutionRecordEntity, Long> {
    List<ConstitutionRecordEntity> findByUserIdentifierOrderByCreatedAtDesc(String userIdentifier);
}

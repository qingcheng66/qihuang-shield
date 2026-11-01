package com.qihuang.repository;

import com.qihuang.entity.HerbDictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HerbDictRepository extends JpaRepository<HerbDictEntity, Long> {
    Optional<HerbDictEntity> findByHerbName(String herbName);
}

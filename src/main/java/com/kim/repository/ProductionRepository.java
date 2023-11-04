package com.kim.repository;

import com.kim.entity.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductionRepository extends JpaRepository<Production, Long>, QuerydslPredicateExecutor<Production>, ProductionRepositoryCustom {

    Page<Production> findByOrderId(Long id, Pageable pageable);

    List<Production> findByOrderIdIs(Long id);

    Page<Production> findByLotIdContaining(String keyword, Pageable pageable);

    Page<Production> findByStartTimeBetween(LocalDateTime StartTime, LocalDateTime endTime, Pageable pageable);


    List<Production> findByIdIs(Long id);

    List<Production> findByLotIdContaining(String keyword);

    List<Production> findByStartTimeBetween(LocalDateTime StartTime, LocalDateTime endTime);

}

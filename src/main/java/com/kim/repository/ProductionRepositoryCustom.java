package com.kim.repository;


import com.kim.dto.ProductionSearchDto;

import com.kim.entity.Production;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionRepositoryCustom {

    Page<Production> getProductionPage(ProductionSearchDto productionSearchDto, Pageable pageable);
}

package com.kim.repository;

import com.kim.dto.ProductionSearchDto;
import com.kim.entity.Production;
import com.kim.entity.QProduction;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ProductionRepositoryCustomImpl implements ProductionRepositoryCustom{

    private JPAQueryFactory queryFactory;

    public ProductionRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QProduction.production.startTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("lotId", searchBy)){
            return QProduction.production.lotId.like("%" + searchQuery + "%");
        }
        return null;
    }
    @Override
    public Page<Production> getProductionPage(ProductionSearchDto productionSearchDto, Pageable pageable) {
        QueryResults<Production> results = queryFactory
                .selectFrom(QProduction.production)
                .where(regDtsAfter(productionSearchDto.getSearchDateType()),
                        searchByLike(productionSearchDto.getSearchBy(),
                                productionSearchDto.getSearchQuery()))
                .orderBy(QProduction.production.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Production> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}

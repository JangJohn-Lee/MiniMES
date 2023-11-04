package com.kim.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductionSearchDto {

    private String searchBy;

    private String searchQuery = "";

    private String searchDateType;


}

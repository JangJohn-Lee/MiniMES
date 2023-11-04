package com.kim.dto;

import com.kim.entity.MatOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class MatOrderDto {

    private Long id;

    private String matId;   //제품

    private LocalDateTime orderDate; // 발주일

    private LocalDateTime comDate;  // 입고일

    private int matNum;   // 수량

    private static ModelMapper modelMapper = new ModelMapper();


    public MatOrder createMatOrder(){
        return modelMapper.map(this, MatOrder.class);
    }
    public static MatOrderDto of(MatOrder matOrder) {
        return modelMapper.map(matOrder,MatOrderDto.class);
    }
}

package com.kim.entity;


import com.kim.dto.MatOrderDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name="matOrdertest2")
public class MatOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matId;

    private LocalDateTime orderDate;

    private LocalDateTime comDate;

    private int matNum;




    public static MatOrder createMatOrder(MatOrderDto matOrderDto){
        MatOrder matOrder = new MatOrder();
        matOrder.setMatId(matOrderDto.getMatId());
        matOrder.setOrderDate(matOrderDto.getOrderDate());
        matOrder.setComDate(matOrderDto.getComDate());
        matOrder.setMatNum(matOrderDto.getMatNum());

        return matOrder;
    }

}

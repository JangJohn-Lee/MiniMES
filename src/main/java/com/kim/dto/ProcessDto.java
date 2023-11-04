package com.kim.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ProcessDto {

    private Long id;

    private String name;

    private String lotId;

    private boolean status;

    private LocalDateTime endTime;

    private double amountHour;

    private int leadTime;

    private int maxAmount;

    private static ModelMapper modelMapper = new ModelMapper();

    public Process createProcess(){
        return modelMapper.map(this, Process.class);
    }

    public static ProcessDto of(Process process) {
        return modelMapper.map(process,ProcessDto.class);
    }
}

package com.kim.controller;

import com.kim.constant.Status;
import com.kim.dto.OrdersDto;
import com.kim.entity.Orders;
import com.kim.entity.Production;
import com.kim.repository.OrdersRepository;
import com.kim.repository.ProceRepository;
import com.kim.repository.ProductionRepository;
import com.kim.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Integer.parseInt;


@Controller
public class ShipmentController {

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderService orderService;


    @GetMapping("/shipment")
    public String shipment(Model model, @PageableDefault(page=0, /*페이징되는 게시글 수 변경*/size=5, sort="id", direction= Sort.Direction.ASC) Pageable pageable){
        orderService.comCheck();


        Page<Orders> list = ordersRepository.findByStatus(Status.COMPLETE, pageable);


        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = list.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage =  Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage+9, list.getTotalPages());


        model.addAttribute("list", list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);




        return "shipment/shipment";
    }

    @PostMapping("/shipment")
    public String shipment2(@RequestParam String type, @RequestParam String keyword, Model model, @PageableDefault(page=0, /*페이징되는 게시글 수 변경*/size=5, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        orderService.comCheck();
        System.out.println("type: " + type);
        System.out.println("keyword: " + keyword);

        Page<Orders> list = ordersRepository.findByStatus(Status.COMPLETE, pageable);

        if(type.equals("option1")){

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            list = orderService.ordersSearchList6(id,Status.COMPLETE,pageable);

        }else if(type.equals("option2")){

            System.out.println("2번");
            list = orderService.ordersSearchList7(keyword, Status.COMPLETE,pageable);


        }else if(type.equals("option3")){

            System.out.println("3번");
            list = orderService.ordersSearchList8(keyword, Status.COMPLETE,pageable);


        }else if(type.equals("option4")){

            System.out.println("4번");
            int day = parseInt(keyword,10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            list = orderService.ordersSearchList9(start, end, Status.COMPLETE,pageable);

        }else if(type.equals("option5")){

            int day = parseInt(keyword, 10);
            LocalDateTime comDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = comDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = comDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(comDate);
            System.out.println(start);
            System.out.println(end);
            list = orderService.ordersSearchList10(start, end, Status.COMPLETE,pageable);

        }


        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = list.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 9, list.getTotalPages());

        System.out.println("type: " + type);
        System.out.println("keyword: " + keyword);

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);


        return "shipment/shipment";
    }

    @GetMapping(value="/shipment/search{page}{type}{keyword}")
    public String shipment3(@RequestParam(required = false) String keyword,@RequestParam(required = false) String type, Model model, @PageableDefault( /*페이징되는 게시글 수 변경*/size=5, sort="id", direction=Sort.Direction.ASC) Pageable pageable){

        orderService.comCheck();
        System.out.println("여기로 오나");
        System.out.println("type: "+type);
        System.out.println("keyword: "+keyword);

        Page<Orders> list = ordersRepository.findByStatus(Status.COMPLETE, pageable);

        if(type.equals("option1")){

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            list = orderService.ordersSearchList6(id,Status.COMPLETE,pageable);

        }else if(type.equals("option2")){

            System.out.println("2번");
            list = orderService.ordersSearchList7(keyword, Status.COMPLETE,pageable);


        }else if(type.equals("option3")){

            System.out.println("3번");
            list = orderService.ordersSearchList8(keyword, Status.COMPLETE,pageable);


        }else if(type.equals("option4")){

            System.out.println("4번");
            int day = parseInt(keyword,10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            list = orderService.ordersSearchList9(start, end, Status.COMPLETE,pageable);

        }else if(type.equals("option5")){

            int day = parseInt(keyword, 10);
            LocalDateTime comDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = comDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = comDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(comDate);
            System.out.println(start);
            System.out.println(end);
            list = orderService.ordersSearchList10(start, end, Status.COMPLETE,pageable);

        }


        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = list.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage =  Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage+9, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        return "shipment/shipment";
    }



}

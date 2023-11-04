package com.kim.controller;

import com.kim.constant.Status;
import com.kim.dto.OrdersDto;
import com.kim.entity.Orders;
import com.kim.entity.Production;
import com.kim.repository.OrdersRepository;
import com.kim.repository.ProceRepository;
import com.kim.repository.ProductionRepository;
import com.kim.service.*;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Integer.parseInt;


@Controller
public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;


    @Autowired
    private BOMService bomService;
    @Autowired
    private MatService matService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MoniteringService moniteringService;
    @Autowired
    private ProductionService productionService;
    @Autowired
    private ProductionRepository productionRepository;
    @Autowired
    private ProceRepository proceRepository;
    @Autowired
    private MatOrderService matOrderService;


    //제품수주
    @GetMapping("/orders")
    public String order1(Model model){

        model.addAttribute("ordersDto",new OrdersDto());
        return "orders/orders";
    }
    @PostMapping("/orders")
    public String getOrder(Model model, @Validated OrdersDto ordersDto , BindingResult bindingResult){
        System.out.println(1);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "입력값이 올바르지 않습니다.");
            System.out.println(2);
            return "redirect:/";

        }

        try {
            System.out.println(ordersDto);
            Orders orders = Orders.createOrder(ordersDto);

            System.out.println(3);
            orderService.saveOrder(orders);
            System.out.println(orders);
//            orderDto = orderService.getorderDtl(order.getId());



        } catch (Exception e) {
            System.out.println(4);
            return "redirect:/orders";
        }

        System.out.println(5);
        return  "redirect:/orders2";
    }

    //주문조회
    @GetMapping("/orders2")
    public String order2(Model model, @PageableDefault(page=0, /*페이징되는 게시글 수 변경*/size=5, sort="id", direction= Sort.Direction.DESC) Pageable pageable){

        System.out.println("여기 아닌데");


        Page<Orders> list = orderService.ordersList(pageable);


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




        return "orders/orders2";
    }

    @PostMapping(value={"/orders2","/orders2/{page}"})
    public String order3(@RequestParam String type, @RequestParam String keyword, Model model, @PageableDefault(page=0, /*페이징되는 게시글 수 변경*/size=5, sort="id", direction=Sort.Direction.DESC) Pageable pageable){

        System.out.println("type: "+type);
        System.out.println("keyword: "+keyword);

        Page<Orders> list = orderService.ordersList(pageable);

        if(type.equals("option1")){

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            list = orderService.ordersSearchList1(id, pageable);

        }else if(type.equals("option2")){

            System.out.println("2번");
            list = orderService.ordersSearchList2(keyword, pageable);


        }else if(type.equals("option3")){

            System.out.println("3번");
            list = orderService.ordersSearchList3(keyword, pageable);


        }else if(type.equals("option4")){

            System.out.println("4번");
            int day = parseInt(keyword,10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            list = orderService.ordersSearchList4(start, end, pageable);

        }


        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = list.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage =  Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage+9, list.getTotalPages());

        System.out.println("type: "+type);
        System.out.println("keyword: "+keyword);

        model.addAttribute("list", list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("type",type);
        model.addAttribute("keyword",keyword);


        return "orders/orders2";
    }
    @GetMapping(value="/orders2/search{page}{type}{keyword}")
    public String order5(@RequestParam(required = false) String keyword,@RequestParam(required = false) String type, Model model, @PageableDefault( /*페이징되는 게시글 수 변경*/size=5, sort="id", direction=Sort.Direction.DESC) Pageable pageable){


        System.out.println("여기로 오나");
        System.out.println("type: "+type);
        System.out.println("keyword: "+keyword);

        Page<Orders> list = orderService.ordersList(pageable);

        if(type.equals("option1")){

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            list = orderService.ordersSearchList1(id, pageable);

        }else if(type.equals("option2")){

            System.out.println("2번");
            list = orderService.ordersSearchList2(keyword, pageable);


        }else if(type.equals("option3")){

            System.out.println("3번");
            list = orderService.ordersSearchList3(keyword, pageable);


        }else if(type.equals("option4")){

            System.out.println("4번");
            int day = parseInt(keyword,10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            list = orderService.ordersSearchList4(start, end, pageable);

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


        return "orders/orders2";
    }

    // 상세페이지
    @GetMapping("/orders/{id}")
    public String order3(Model model, @PathVariable Long id){
        List<Production> productionList = productionRepository.findByOrderIdIs(id);
        OrdersDto ordersDto = orderService.getorderDtl(id);

        model.addAttribute("list",productionList);




        model.addAttribute("id", ordersDto.getId());
        model.addAttribute("orderFrom", ordersDto.getOrderFrom());
        model.addAttribute("product", ordersDto.getProduct());
        model.addAttribute("box", ordersDto.getBox());
        model.addAttribute("orderDate", ordersDto.getOrderDate());
        model.addAttribute("comDate", ordersDto.getComDate());

        if(ordersDto.getStatus() == null){
            ordersDto.setStatus(Status.PREORDER);
        }
        model.addAttribute("status", ordersDto.getStatus());
        if(ordersDto.getStatus().equals(Status.PREORDER)){
            model.addAttribute("stat",true);
        }else{
            model.addAttribute("stat",false);
        }


        return "orders/orders3";
    }



    @GetMapping(value="/orders/updateOrders/{id}")
    public String updateOrders(@PathVariable("id") Long id, Model model){
        try{

            OrdersDto ordersDto = orderService.getorderDtl(id);
            System.out.println(ordersDto);
            model.addAttribute("ordersDto", ordersDto);
            model.addAttribute("orderDate",ordersDto.getOrderDate());
            model.addAttribute("comDate", ordersDto.getComDate());
            model.addAttribute("status", ordersDto.getStatus());




        }catch(EntityNotFoundException e) {
            model.addAttribute("errorMessage", "보드 불러오기 실패");
            model.addAttribute("ordersDto", new OrdersDto());
            return "orders/orders4";
        }
        return "orders/orders4";
    }

    @PostMapping(value="/orders/updateOrders/")
    public String updateOrders2(@Validated OrdersDto ordersDto, BindingResult bindingResult, Model model)throws Exception{
        Long id = ordersDto.getId();
        if(bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "바인드에서 문제");
            return "orders/orders4";
        }
        try {
            orderService.updateOrders(ordersDto);
        }catch (Exception e){
            model.addAttribute("errorMessage", "게시물 수정 중 에러 발생");
            return "orders/orders4";
        }
        return "redirect:/orders/"+id;
    }

    @GetMapping(value="/orders/deleteOrders/{id}")
    public String deleteOrders(@PathVariable("id") Long id){

        System.out.println(1);
        ordersRepository.deleteById(id);
        System.out.println(2);

        return "redirect:/orders2";
    }

    @GetMapping(value="/orders/confirmOrders/{id}")
    public String confirmOrders(@PathVariable("id") Long id) throws Exception {

        System.out.println(1);
        OrdersDto ordersDto = orderService.getorderDtl(id);
        ordersDto.setStatus(Status.STANDBY);
        orderService.updateOrders2(ordersDto);
        System.out.println(2);


        LocalDateTime startTime = productionService.workTimeStart(proceRepository.findByIdIs(1L).getEndTime());
        if(startTime == null){
            startTime = LocalDateTime.now();
        }

        if(ordersDto.getProduct().equals("양배추즙")){
            LocalDateTime endTime = productionService.calCab(ordersDto.getBox(),startTime,ordersDto.getId());
        }else if(ordersDto.getProduct().equals("흑마늘즙")){
            LocalDateTime endTime = productionService.calGal(ordersDto.getBox(),startTime,ordersDto.getId());
        }else if(ordersDto.getProduct().equals("석류젤리스틱")){
            LocalDateTime endTime = productionService.calPom(ordersDto.getBox(),startTime,ordersDto.getId());
        }else if(ordersDto.getProduct().equals("매실젤리스틱")){
            LocalDateTime endTime = productionService.calPlu(ordersDto.getBox(),startTime,ordersDto.getId());
        }

        return "redirect:/orders/"+id;
    }


    @GetMapping(value="/orders/cancleConfirmOrders/{id}")
    public String cancleConfirmOrders(@PathVariable("id") Long id) throws Exception {

        System.out.println(1);
        OrdersDto ordersDto = orderService.getorderDtl(id);
        ordersDto.setStatus(Status.PREORDER);
        orderService.updateOrders2(ordersDto);
        System.out.println(2);

        return "redirect:/orders/"+id;
    }
/*
    @GetMapping(value="/orders/comOrderTest/{id}")
    public String comOrderTest(@PathVariable("id") Long id) throws Exception {

        System.out.println(1);
        OrdersDto ordersDto = orderService.getorderDtl(id);
        ordersDto.setStatus(Status.COMPLETE);
        orderService.updateOrders2(ordersDto);
        System.out.println(2);

        return "redirect:/orders/"+id;
    }
*/

}


package com.kim.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kim.entity.Proce;
import com.kim.entity.Production;
import com.kim.repository.ProceRepository;
import com.kim.repository.ProductionRepository;
import com.kim.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;


@Controller
public class ProcessController {

    @Autowired
    private ProductionService productionService;
    @Autowired
    private ProductionRepository productionRepository;
    @Autowired
    private ProceRepository proceRepository;


    @GetMapping("/process")
    public String process(Model model) {

        List<Proce> proceList = proceRepository.findAll();

        model.addAttribute("list",proceList);
        model.addAttribute("now",LocalDateTime.now());



        return "process/process";
    }


    @GetMapping("/process2")
    public String process2(Model model, @PageableDefault(page = 0, /*페이징되는 게시글 수 변경*/size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {


        Page<Production> list = productionRepository.findAll(pageable);


        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = list.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 9, list.getTotalPages());


        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageable", pageable);

        return "process/process2";
    }

    @PostMapping(value = {"/process2", "/process2/{page}"})
    public String process2(@RequestParam String type, @RequestParam String keyword, Model model, @PageableDefault(page = 0, /*페이징되는 게시글 수 변경*/size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        System.out.println("type: " + type);
        System.out.println("keyword: " + keyword);

        Page<Production> list = productionRepository.findAll(pageable);

        if (type.equals("option1")) {

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            list = productionRepository.findByOrderId(id, pageable);

        } else if (type.equals("option2")) {

            System.out.println("2번");
            list = productionRepository.findByLotIdContaining(keyword, pageable);


        } else if (type.equals("option3")) {

            int day = parseInt(keyword, 10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            list = productionRepository.findByStartTimeBetween(start, end, pageable);

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



        return "process/process2";
    }

    @GetMapping(value = "/process2/page/search{page}{type}{keyword}")
    public String process3(@RequestParam(required = false) String keyword, @RequestParam(required = false) String type, Model model, @PageableDefault( /*페이징되는 게시글 수 변경*/size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {


        System.out.println("여기로 오나");
        System.out.println("type: " + type);
        System.out.println("keyword: " + keyword);

        Page<Production> list = productionRepository.findAll(pageable);


        if (type.equals("option1")) {

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            list = productionService.productionSearchList1(id, pageable);

        } else if (type.equals("option2")) {

            System.out.println("2번");
            list = productionService.productionSearchList2(keyword, pageable);


        } else if (type.equals("option3")) {

            int day = parseInt(keyword, 10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            list = productionService.productionSearchList3(start, end, pageable);

        }


        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = list.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 9, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageable", pageable);

        return "process/process2";
    }

    @GetMapping(value = "/process2/{day}")
    public ResponseEntity<String> process4(@PathVariable(required = false) int day, Model model) {
        System.out.println("day: " + day);

        LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
        LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
        List<Production> list = productionService.productionSearchList6(start, end);

        // 각 Production 객체의 정보를 문자열로 가져옴
        List<String> productionInfos = list.stream()
                .map(p -> p.getLotId() + " " + p.getStartTime().toLocalTime())
                .collect(Collectors.toList());

        // 모델에 데이터 추가
        for (int i = 0; i < productionInfos.size(); i++) {
            model.addAttribute(String.valueOf((char)('a' + i)), productionInfos.get(i));
        }

        // JSON 응답 데이터 생성
        Map<String, String> responseData = new HashMap<>();
        for (int i = 0; i < productionInfos.size(); i++) {
            responseData.put(String.valueOf((char)('a' + i)), productionInfos.get(i));
        }
        ObjectMapper obj = new ObjectMapper();
        String jsonResponse;
        try {
            jsonResponse = obj.writeValueAsString(responseData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(jsonResponse);
    }
}

package com.kim.controller;

import com.kim.constant.Status;
import com.kim.dto.MatDto;
import com.kim.dto.OrdersDto;
import com.kim.dto.ProductDto;
import com.kim.entity.*;
import com.kim.repository.*;
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

import static com.kim.calculator.orderVolume;
import static java.lang.Integer.parseInt;


@Controller
public class InventoryController {

    @Autowired
    private MatRepository matRepository;

    @Autowired
    private MatOrderRepository matOrderRepository;

    @Autowired
    private ProductRepository productRepository;


    @GetMapping("/inventory")
    public String inventory1(Model model) {

        List<MatOrder> matOrderList = matOrderRepository.findAll();


        model.addAttribute("matOrderList", matOrderList);


        return "inventory/inventory";
    }


    @GetMapping("/inventory2")
    public String inventory2(Model model) {
        int cab = 0;
        List<Mat> matList1 = matRepository.findByMatName("양배추");
        for(int i = 0; i<matList1.size(); i++){
            cab += matList1.get(i).getMatNum();
        }
        int gal = 0;
        List<Mat> matList2 = matRepository.findByMatName("흑마늘");
        for(int i = 0; i<matList2.size(); i++){
            gal += matList2.get(i).getMatNum();
        }
        int pom = 0;
        List<Mat> matList3 = matRepository.findByMatName("석류");
        for(int i = 0; i<matList3.size(); i++){
            pom += matList3.get(i).getMatNum();
        }
        int plu = 0;
        List<Mat> matList4 = matRepository.findByMatName("매실");
        for(int i = 0; i<matList4.size(); i++){
            plu += matList4.get(i).getMatNum();
        }
        int pau = 0;
        List<Mat> matList5 = matRepository.findByMatName("파우치");
        for(int i = 0; i<matList5.size(); i++){
            pau += matList5.get(i).getMatNum();
        }
        int stick = 0;
        List<Mat> matList6 = matRepository.findByMatName("스틱파우치");
        for(int i = 0; i<matList6.size(); i++){
            stick += matList6.get(i).getMatNum();
        }
        int col = 0;
        List<Mat> matList7 = matRepository.findByMatName("콜라겐");
        for(int i = 0; i<matList7.size(); i++){
            col += matList7.get(i).getMatNum();
        }
        int box = 0;
        List<Mat> matList8 = matRepository.findByMatName("박스");
        for(int i = 0; i<matList8.size(); i++){
            box += matList8.get(i).getMatNum();
        }

//        Mat mat = matRepository.findById(1L);

        List<Product> products = productRepository.findAll();


        model.addAttribute("cab",cab);
        model.addAttribute("gal",gal);
        model.addAttribute("pom",pom);
        model.addAttribute("plu",plu);
        model.addAttribute("pau",pau);
        model.addAttribute("stick",stick);
        model.addAttribute("col",col);
        model.addAttribute("box",box);
        model.addAttribute("list",products);


        return "inventory/inventory2";
    }

}


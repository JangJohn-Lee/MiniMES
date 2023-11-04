package com.kim.controller;

import com.kim.constant.Status;
import com.kim.entity.Orders;
import com.kim.entity.Production;
import com.kim.repository.OrdersRepository;
import com.kim.repository.ProceRepository;
import com.kim.repository.ProductionRepository;
import com.kim.service.OrderService;
import com.kim.service.ProductionService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Integer.parseInt;


@Controller
public class ExcelController {

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProceRepository proceRepository;
    @Autowired
    private ProductionRepository productionRepository;
    @Autowired
    private ProductionService productionService;


    //공정 Excel
    @GetMapping("/excel/download2{type}{keyword}")
    public void excelDownload2(HttpServletResponse response, @RequestParam(required = false) String type,@RequestParam(required = false) String keyword) throws IOException {
//        Workbook wb = new HSSFWorkbook();
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("첫번째 시트");
        System.out.println("type: " +type);
        System.out.println("keyword: "+keyword);
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header

        row = sheet.createRow(rowNum++);
        if(type.equals("option3")){
            cell = row.createCell(0);
            cell.setCellValue(keyword+"일 예정 작업");
        }else if(type.equals("option2")){
            cell = row.createCell(0);
            cell.setCellValue(keyword+" 예정 작업");
        }else {
            cell = row.createCell(0);
            cell.setCellValue(LocalDateTime.now().toLocalDate().getDayOfWeek()+"일 이후 예정 작업");
        }

        cell = row.createCell(1);
        cell.setCellValue("수주 ID");
        cell = row.createCell(2);
        cell.setCellValue("공정명");
        cell = row.createCell(3);
        cell.setCellValue("예상시작시간");


        List<Production> comList = productionRepository.findAll();


        if(type.equals("option1")){

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            comList = productionService.productionSearchList4(id);

        }else if(type.equals("option2")){

            System.out.println("2번");
            comList = productionService.productionSearchList5(keyword);


        }else if(type.equals("option3")){

            int day = parseInt(keyword,10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            comList = productionService.productionSearchList6(start, end);

        }

        System.out.println(comList);
        System.out.println(1);
        // Body
        for (int i=0; i<comList.size(); i++) {
            System.out.println(2);
            System.out.println(comList.get(i).toString());
            Production production = comList.get(i);
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell = row.createCell(1);
            cell.setCellValue(production.getOrderId());
            cell = row.createCell(2);
            cell.setCellValue(production.getLotId());
            cell = row.createCell(3);
            cell.setCellValue(production.getStartTime().toString());

            System.out.println(3);
        }

        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
//        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
        response.setHeader("Content-Disposition", "attachment;filename=WorkList.xlsx");

        // Excel File Output
        wb.write(response.getOutputStream());
        wb.close();
    }




    //출하 Excel
    @GetMapping("/excel/download{type}{keyword}")
    public void excelDownload(HttpServletResponse response, @RequestParam(required = false) String type,@RequestParam(required = false) String keyword) throws IOException {
//        Workbook wb = new HSSFWorkbook();
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("첫번째 시트");
        System.out.println("type: " +type);
        System.out.println("keyword: "+keyword);
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("주문자명");
        cell = row.createCell(2);
        cell.setCellValue("제품");
        cell = row.createCell(3);
        cell.setCellValue("수량");
        cell = row.createCell(4);
        cell.setCellValue("발주일");
        cell = row.createCell(5);
        cell.setCellValue("출하일");

        List<Orders> comList = ordersRepository.findByStatus(Status.COMPLETE);

        if(type.equals("option1")){

            System.out.println("1번");
            Long id = Long.parseLong(keyword);
            comList = orderService.ordersSearchList11(Status.COMPLETE, id);

        }else if(type.equals("option2")){

            System.out.println("2번");
            comList = ordersRepository.findByStatusAndOrderFromContaining(Status.COMPLETE,keyword);


        }else if(type.equals("option3")){

            System.out.println("3번");
            comList = ordersRepository.findByStatusAndProductContaining(Status.COMPLETE, keyword);


        }else if(type.equals("option4")){

            System.out.println("4번");
            int day = parseInt(keyword,10);
            LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(orderDate);
            System.out.println(start);
            System.out.println(end);
            comList = ordersRepository.findByStatusAndOrderDateBetween(Status.COMPLETE,start,end);

        }else if(type.equals("option5")){

            int day = parseInt(keyword, 10);
            LocalDateTime comDate = LocalDateTime.now().withDayOfMonth(day);
            LocalDateTime start = comDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = comDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
            System.out.println(comDate);
            System.out.println(start);
            System.out.println(end);
            comList = ordersRepository.findByStatusAndComDateBetween(Status.COMPLETE,start,end);

        }

        System.out.println(comList);
        System.out.println(1);
        // Body
        for (int i=0; i<comList.size(); i++) {
            System.out.println(2);
            System.out.println(comList.get(i).toString());
            Orders order = comList.get(i);
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(order.getId());
            cell = row.createCell(1);
            cell.setCellValue(order.getOrderFrom());
            cell = row.createCell(2);
            cell.setCellValue(order.getProduct());
            cell = row.createCell(3);
            cell.setCellValue(order.getBox());
            cell = row.createCell(4);
            cell.setCellValue(order.getOrderDate().toLocalDate().toString());

            cell = row.createCell(5);
            cell.setCellValue(order.getComDate());
            System.out.println(3);
        }

        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
//        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
        response.setHeader("Content-Disposition", "attachment;filename=shipment.xlsx");

        // Excel File Output
        wb.write(response.getOutputStream());
        wb.close();
    }




    @GetMapping("/excel/cal/{day}")
    public void excelDownload2(HttpServletResponse response ,@PathVariable(required = false) int day) throws IOException {
//        Workbook wb = new HSSFWorkbook();
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("첫번째 시트");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;
        System.out.println("day: "+day);
        // Header

        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue(day+"일의 작업 일정");
        cell = row.createCell(1);
        cell.setCellValue("ID");
        cell = row.createCell(2);
        cell.setCellValue("공정명");
        cell = row.createCell(3);
        cell.setCellValue("작업수량");
        cell = row.createCell(4);
        cell.setCellValue("시작시간");
        cell = row.createCell(5);
        cell.setCellValue("완료시간");


        LocalDateTime orderDate = LocalDateTime.now().withDayOfMonth(day);
        LocalDateTime start = orderDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = orderDate.withHour(23).withMinute(59).withSecond(59).withNano(999);
        System.out.println(orderDate);
        System.out.println(start);
        System.out.println(end);
        List<Production> comList =productionRepository.findByStartTimeBetween(start, end);

        System.out.println(comList);
        System.out.println(1);
        // Body
        for (int i=0; i<comList.size(); i++) {
            System.out.println(2);
            System.out.println(comList.get(i).toString());
            Production production = comList.get(i);
            row = sheet.createRow(rowNum++);
            cell = row.createCell(1);
            cell.setCellValue(production.getId());
            cell = row.createCell(2);
            cell.setCellValue(production.getLotId());
            cell = row.createCell(3);
            cell.setCellValue(production.getMatInput());
            cell = row.createCell(4);
            cell.setCellValue(production.getStartTime().getHour()+":"+production.getStartTime().getMinute());
            cell = row.createCell(5);
            cell.setCellValue(production.getEndTime().getHour()+":"+production.getEndTime().getMinute());

            System.out.println(3);
        }

        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
//        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
        response.setHeader("Content-Disposition", "attachment;filename=TodaysWork.xlsx");

        // Excel File Output
        wb.write(response.getOutputStream());
        wb.close();
    }



}


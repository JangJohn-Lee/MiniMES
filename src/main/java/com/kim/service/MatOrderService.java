package com.kim.service;

import com.kim.constant.Status;
import com.kim.dto.MatDto;
import com.kim.dto.MatOrderDto;
import com.kim.dto.OrdersDto;
import com.kim.entity.Mat;
import com.kim.entity.MatOrder;
import com.kim.entity.Product;
import com.kim.repository.MatOrderRepository;
import com.kim.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MatOrderService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MatService matService;
    @Autowired
    private MatOrderRepository matOrderRepository;


    public MatOrder saveMatOrder(MatOrder matOrder) {
        matOrderRepository.save(matOrder);
        return matOrder;
    }


    public void orderReq(Long id) throws Exception {

        OrdersDto order = orderService.getorderDtl(id);
        Product product = productRepository.findByProduct(order.getProduct());

//        BOM bom = bomRepository.findByProduct(order.getProduct());  --안씀
        List<Mat> matCab = matService.findMat("양배추");
        int totalCab = 0;
        for(int i = 0; i<matCab.size();i++){
            totalCab += matCab.get(i).getMatNum();
        }


        System.out.println(1);
        System.out.println();
        System.out.println("matCab: "+matCab);
        System.out.println();
        System.out.println();


        List<Mat> matGal = matService.findMat("흑마늘");
        int totalGal = 0;
        for(int i = 0; i<matGal.size();i++){
            totalGal += matGal.get(i).getMatNum();
        }
        System.out.println("여기1");
        List<Mat> matPom = matService.findMat("석류");
        int totalPom = 0;
        for(int i = 0; i<matPom.size();i++){
            totalPom += matPom.get(i).getMatNum();
        }

        System.out.println("여기2");
        List<Mat> matPlu = matService.findMat("매실");
        int totalPlu = 0;
        for(int i = 0; i<matPlu.size();i++){
            totalPlu += matPlu.get(i).getMatNum();
        }

        System.out.println("여기3");
        List<Mat> matCol = matService.findMat("콜라겐");
        int totalCol = 0;
        for(int i = 0; i<matCol.size();i++){
            totalCol += matCol.get(i).getMatNum();
        }
        System.out.println("여기4");

        List<Mat> matPau = matService.findMat("파우치");
        int totalPau = 0;
        for(int i = 0; i<matPau.size();i++){
            totalPau += matPau.get(i).getMatNum();
        }
        System.out.println("여기5");

        List<Mat> matStick = matService.findMat("스틱파우치");
        int totalStick = 0;
        for(int i = 0; i<matStick.size();i++){
            totalStick += matStick.get(i).getMatNum();
        }
        System.out.println("여기6");

        List<Mat> matBox = matService.findMat("박스");
        int totalBox = 0;
        for(int i = 0; i<matBox.size();i++){
            totalBox += matBox.get(i).getMatNum();
        }
        System.out.println("여기7");
        System.out.println(matCab);
        System.out.println(matGal);
        System.out.println(matCol);
        System.out.println(matPau);
        System.out.println(matPlu);
        System.out.println(matPom);
        System.out.println(matStick);
        System.out.println(matBox);
        System.out.println("==================");

        int quantityToProduce = order.getBox() - product.getNum();
        // 양배추
        double productionCapacityCab = totalCab * 2 * 0.8 / 0.08 / 30;
        // 흑마늘
        double productionCapacityGal = totalGal * 4 * 0.6 / 0.02 / 30;
        // 석류 젤리
        double productionCapacityPom = totalPom / 0.005 / 25;
        // 매실 젤리
        double productionCapacityPlu = totalPlu / 0.005 / 25;
        // 콜라겐
        double productionCapacityCol = totalCol / 0.002 / 25;
        // 파우치
        double productionCapacityPau = totalPau;
        //스틱 파우치
        double productionCapacityStick = totalStick;
        // 포장 박스
        double productionCapacityBox = totalBox; // 가진 box - 완제품

        boolean a = false;
        boolean b = false;
        boolean c = false;
        boolean d = false;

        if (order.getBox() > product.getNum()) {
            System.out.println("양배추 들어옴");
            if (order.getProduct().equals("양배추즙") || order.getProduct().equals("흑마늘즙")) {
                if (order.getProduct().equals("양배추즙")) {

                    System.out.println("양배추 들어옴");
                    // 양배추즙
                    a = producible(order, matCab, productionCapacityCab, product, quantityToProduce);
                } else {
                    // 흑마늘즙
                    a = producible(order, matGal, productionCapacityGal, product, quantityToProduce);
                }
                // 파우치 가진거
                System.out.println("파우치 들어옴");
                b = producible(order, matPau, productionCapacityPau, product, quantityToProduce * 30);

                c = producible(order, matBox, productionCapacityBox, product, quantityToProduce);

                if(a == b == c == true){
                    System.out.println("수주 스테이터스");
                    order.setStatus(Status.PROCESSING);
                    orderService.updateOrders2(order);
                }

            } else {
                // 젤리 자재 확인 없으면 발주
                if (order.getProduct().equals("석류젤리스틱")) {
                    a = producible(order, matPom, productionCapacityPom, product, quantityToProduce);
                } else {
                    a = producible(order, matPlu, productionCapacityPlu, product, quantityToProduce);
                }

                // 콜라겐 자재 확인 없으면 발주
                b = producible(order, matCol, productionCapacityCol, product, quantityToProduce);

                // 파우치 자재 확인 없으면 발주
                c = producible(order, matStick, productionCapacityStick, product, quantityToProduce * 25);

                // 박스 자재 확인 없으면 발주
                d = producible(order, matBox, productionCapacityBox, product, quantityToProduce);

                if(a == b == c == d == true){
                    order.setStatus(Status.PROCESSING);
                    orderService.updateOrders2(order);
                }

            }

            // 이전 발주 건 조회하고 자재발주 수량이 오버 되는지 체크 해야됨

        } else { // 완제품이 충분할경우
            System.out.println("출하");
        }
        System.out.println(3);


    }
    public boolean producible(OrdersDto order, List<Mat> mat, double productionCapacity, Product product, int quantityToProduce) {
        boolean a = true;

        if (productionCapacity >= quantityToProduce) { // 여기 수정함
            int total = 0;
            for(int i = 0; i<mat.size();i++){
                total += mat.get(i).getMatNum();
            }

            System.out.println(mat.get(0).getMatName() + " 자재 충분 / 자재량: " + total);

        } else {
            List<MatOrderDto> orderMat = orderVolume(order, mat, product);
            System.out.println("==============================");
            System.out.println(orderMat);
            System.out.println("==============================");

            for(int i = 0; i<orderMat.size();i++){
                saveMatOrder(orderMat.get(i).createMatOrder());
            }


            a = false;
        }
        return a;
    }


    public List<MatOrderDto> orderVolume(OrdersDto order, List<Mat> mat, Product product) {

        int total=0;
        for(int i = 0; i<mat.size();i++){
            total+=mat.get(i).getMatNum();
        }
        System.out.println("total: "+total);
        System.out.println("mat: "+mat);

        MatOrderDto matOrderDto = new MatOrderDto();
        List<MatOrderDto> orderQuantityList = new ArrayList<>();
        LocalDateTime orderTime = LocalDateTime.now(); // 원자재 발주 넣은 시간

        double orderQuantity;




        switch (mat.get(0).getMatName()) {

            case "양배추":
                orderQuantity = (order.getBox() - product.getNum()) * 1.5; // 양배추즙 1box = 양배추 1.5kg

                orderQuantity -= total; // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 1000) * 1000; // 최소 주문량 1000kg 단위

                MatDto matDto1 = new MatDto();
                matDto1.setMatName("양배추");
                matDto1.setMatNum((int) orderQuantity);
                Mat mat1 = Mat.createMat(matDto1);
                matService.saveMat(mat1);

                matOrderDto.setMatId(String.format("양배추"));
                matOrderDto.setOrderDate(LocalDateTime.now());

                break;

            case "흑마늘":
                orderQuantity = (order.getBox() - product.getNum()) * 0.25; // 흑마늘즙 1box = 흑마늘 0.25kg

                orderQuantity -= total; // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 10) * 10; // 최소 주문량 10kg 단위

                MatDto matDto2 = new MatDto();
                matDto2.setMatName("흑마늘");
                matDto2.setMatNum((int) orderQuantity);
                Mat mat2 = Mat.createMat(matDto2);
                matService.saveMat(mat2);

                matOrderDto.setMatId(String.format("흑마늘"));
                matOrderDto.setOrderDate(LocalDateTime.now());
                break;

            case "석류":


                matOrderDto.setMatId(String.format("석류"));
                matOrderDto.setOrderDate(LocalDateTime.now());

                orderQuantity = (order.getBox() - product.getNum()) * 0.125; // 젤리스틱 1box = 원자재(석류, 매실) 0.125kg

                orderQuantity -= total; // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 5) * 5;

                MatDto matDto3 = new MatDto();
                matDto3.setMatName("석류");
                matDto3.setMatNum((int) orderQuantity);
                Mat mat3 = Mat.createMat(matDto3);
                matService.saveMat(mat3);


                break;

            case "매실":

                matOrderDto.setMatId(String.format("매실"));
                matOrderDto.setOrderDate(LocalDateTime.now());

                orderQuantity = (order.getBox() - product.getNum()) * 0.125; // 젤리스틱 1box = 원자재(석류, 매실) 0.125kg

                orderQuantity -= total; // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 5) * 5;

                MatDto matDto4 = new MatDto();
                matDto4.setMatName("매실");
                matDto4.setMatNum((int) orderQuantity);
                Mat mat4 = Mat.createMat(matDto4);
                matService.saveMat(mat4);
                break;



            case "콜라겐":



                matOrderDto.setMatId(String.format("콜라겐"));
                matOrderDto.setOrderDate(LocalDateTime.now());

                orderQuantity = (order.getBox() - product.getNum()) * 0.125; // 젤리스틱 1box = 원자재(석류, 매실) 0.125kg

                orderQuantity -= total; // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 5) * 5;

                MatDto matDto5 = new MatDto();
                matDto5.setMatName("콜라겐");
                matDto5.setMatNum((int) orderQuantity);
                Mat mat5 = Mat.createMat(matDto5);
                matService.saveMat(mat5);
                break;

            case "파우치":
            case "스틱파우치":

                if (mat.get(0).getMatName().equals("파우치")) {
                    matOrderDto.setMatId(String.format("파우치"));
                    matOrderDto.setOrderDate(LocalDateTime.now());
                    orderQuantity = (order.getBox() - product.getNum()) * 30;


                } else {
                    matOrderDto.setMatId(String.format("스틱파우치"));
                    matOrderDto.setOrderDate(LocalDateTime.now());
                    orderQuantity = (order.getBox() - product.getNum()) * 25;



                }
                orderQuantity -= total;
                orderQuantity = Math.ceil(orderQuantity / 1000) * 1000;

                if (mat.get(0).getMatName().equals("파우치")) {
                    MatDto matDto6 = new MatDto();
                    matDto6.setMatName("파우치");
                    matDto6.setMatNum((int) orderQuantity);
                    Mat mat6 = Mat.createMat(matDto6);
                    matService.saveMat(mat6);
                }
                else {
                    MatDto matDto6 = new MatDto();
                    matDto6.setMatName("스틱파우치");
                    matDto6.setMatNum((int) orderQuantity);
                    Mat mat6 = Mat.createMat(matDto6);
                    matService.saveMat(mat6);

                }
                break;

            default:
                // 박스
                orderQuantity = order.getBox() - product.getNum();
                orderQuantity -= total;
                orderQuantity = Math.ceil(orderQuantity / 500) * 500;


                MatDto matDto7 = new MatDto();
                matDto7.setMatName("박스");
                matDto7.setMatNum((int) orderQuantity);
                Mat mat7 = Mat.createMat(matDto7);
                matService.saveMat(mat7);

                matOrderDto.setMatId(String.format("박스"));
                matOrderDto.setOrderDate(LocalDateTime.now());
                break;
        }

        // 양배추 최소 발주량 1ton
        if ((mat.get(0).getMatName().equals("양배추") || mat.get(0).getMatName().equals("파우치") || mat.get(0).getMatName().equals("스틱파우치")) && orderQuantity <= 1000) { // 수정
            matOrderDto.setMatNum(1000);
            matOrderDto.setComDate(orderDelivery(mat, orderTime));
            orderQuantityList.add(matOrderDto);
        } else if (mat.get(0).getMatName().equals("흑마늘") && orderQuantity <= 10) {
            matOrderDto.setMatNum(10);
            matOrderDto.setComDate(orderDelivery(mat, orderTime));
            orderQuantityList.add(matOrderDto);
        } else if ((mat.get(0).getMatName().equals("석류") || mat.get(0).getMatName().equals("매실")) && orderQuantity <= 5) {
            matOrderDto.setMatNum(5);
            matOrderDto.setComDate(orderDelivery(mat, orderTime));
            orderQuantityList.add(matOrderDto);
        } else if (mat.get(0).getMatName().equals("box") && orderQuantity <= 500) {
            matOrderDto.setMatNum(500);
            matOrderDto.setComDate(orderDelivery(mat, orderTime));
            orderQuantityList.add(matOrderDto);
        } else {

            boolean stop = false;
            double orderMax = orderQuantity;
            int minimumOrder;

            // 최대 발주량 초과
            while (!stop) {

                // 파우치 스틱
                if (mat.get(0).getMatName().equals("양배추") || mat.get(0).getMatName().equals("흑마늘")) {
                    minimumOrder = 5000;
                } else if (mat.get(0).getMatName().equals("석류") || mat.get(0).getMatName().equals("매실") || mat.get(0).getMatName().equals("콜라겐")) {
                    minimumOrder = 500;
                } else if (mat.get(0).getMatName().equals("box")) {  // 박스
                    minimumOrder = 10000;
                } else {
                    minimumOrder = 1000000;
                }

                if (orderMax > minimumOrder) {
                    orderMax -= minimumOrder;
                    matOrderDto.setMatNum(minimumOrder);
                    matOrderDto.setComDate(orderDelivery(mat, orderTime));
//                    matOrderDto.setOrderDate(orderTime);
                    orderQuantityList.add(matOrderDto);
                    orderTime = matOrderDto.getComDate();
                    matOrderDto = new MatOrderDto();
                } else if (0 < orderMax) {
                    matOrderDto.setMatNum((int) orderMax);
                    matOrderDto.setComDate(orderDelivery(mat, orderTime));
//                    matOrderDto.setOrderDate(orderTime);
                    orderQuantityList.add(matOrderDto);
                    orderMax -= orderMax;
                } else {
                    stop = true;
                }

            }
        }
        return orderQuantityList;
    }
    public static LocalDateTime orderDelivery(List<Mat> mat, LocalDateTime orderTime) {
        // 입고는 월, 수, 금 오전 10:00 창고에 도착

        System.out.println("발주일: " + orderTime);
        LocalDateTime deliveryDate;

        if (mat.get(0).getMatName().equals("양배추") || mat.get(0).getMatName().equals("흑마늘")) {

            if (orderTime.getHour() < 12) {
                // 12시 이전 주문건
                // 1=월 ... 7=일
                deliveryDate = getLocalDateTimeBefore2Day(orderTime);

            } else {
                // 12시 이후 주문건
                deliveryDate = getLocalDateTimeAfter2Day(orderTime);

            }
        } else {
            if (orderTime.getHour() < 15) {
                // 15 : 00 이전 주문건
                if (mat.get(0).getMatName().equals("석류") || mat.get(0).getMatName().equals("매실") || mat.get(0).getMatName().equals("콜라겐")) {
                    if (orderTime.getDayOfWeek().getValue() == 2) {
                        deliveryDate = orderTime.toLocalDate().plusDays(3).atTime(10, 0);
                    } else if (orderTime.getDayOfWeek().getValue() == 1) {
                        deliveryDate = orderTime.toLocalDate().plusDays(4).atTime(10, 0);
                    } else if (orderTime.getDayOfWeek().getValue() == 4 || orderTime.getDayOfWeek().getValue() == 6) {
                        deliveryDate = orderTime.toLocalDate().plusDays(6).atTime(10, 0);
                    } else {
                        deliveryDate = orderTime.toLocalDate().plusDays(5).atTime(10, 0);
                    }

                } else {
                    // 포장지
                    deliveryDate = getLocalDateTimeBefore2Day(orderTime);
                }
            } else {
                if (mat.get(0).getMatName().equals("석류") || mat.get(0).getMatName().equals("매실") || mat.get(0).getMatName().equals("콜라겐")) {

                    if (orderTime.getDayOfWeek().getValue() == 1) {
                        deliveryDate = orderTime.toLocalDate().plusDays(4).atTime(10, 0);
                    } else if (orderTime.getDayOfWeek().getValue() == 7) {
                        deliveryDate = orderTime.toLocalDate().plusDays(5).atTime(10, 0);
                    } else if (orderTime.getDayOfWeek().getValue() == 3 || orderTime.getDayOfWeek().getValue() == 5) {
                        deliveryDate = orderTime.toLocalDate().plusDays(7).atTime(10, 0);
                    } else {
                        deliveryDate = orderTime.toLocalDate().plusDays(6).atTime(10, 0);
                    }

                } else {
                    // 포장지
                    deliveryDate = getLocalDateTimeAfter2Day(orderTime);
                }
            }

        }

        return deliveryDate;
    }

    public static LocalDateTime getLocalDateTimeAfter2Day(LocalDateTime orderTime) {
        LocalDateTime deliveryDate;
        if (orderTime.getDayOfWeek().getValue() == 2 || orderTime.getDayOfWeek().getValue() == 7) {
            // 화요일 일요일
            deliveryDate = orderTime.toLocalDate().plusDays(3).atTime(10, 0);
        } else if (orderTime.getDayOfWeek().getValue() == 1 || orderTime.getDayOfWeek().getValue() == 6) {
            // 월요일 토요일
            deliveryDate = orderTime.toLocalDate().plusDays(4).atTime(10, 0);
        } else if (orderTime.getDayOfWeek().getValue() == 3 || orderTime.getDayOfWeek().getValue() == 5) {
            // 수요일 금요일
            deliveryDate = orderTime.toLocalDate().plusDays(5).atTime(10, 0);
        } else {
            // 목요일
            deliveryDate = orderTime.toLocalDate().plusDays(6).atTime(10, 0);
        }
        return deliveryDate;
    }


    public static LocalDateTime getLocalDateTimeBefore2Day(LocalDateTime orderTime) {
        LocalDateTime deliveryDate;
        if (orderTime.getDayOfWeek().getValue() == 1 || orderTime.getDayOfWeek().getValue() == 3) {
            // 월요일 수요일
            deliveryDate = orderTime.toLocalDate().plusDays(2).atTime(10, 0);
        } else if (orderTime.getDayOfWeek().getValue() == 2 || orderTime.getDayOfWeek().getValue() == 7) {
            // 화요일 일요일
            deliveryDate = orderTime.toLocalDate().plusDays(3).atTime(10, 0);
        } else if (orderTime.getDayOfWeek().getValue() == 4 || orderTime.getDayOfWeek().getValue() == 6) {
            // 목요일 토요일
            deliveryDate = orderTime.toLocalDate().plusDays(4).atTime(10, 0);
        } else {
            // 금요일
            deliveryDate = orderTime.toLocalDate().plusDays(5).atTime(10, 0);
        }
        return deliveryDate;
    }
}

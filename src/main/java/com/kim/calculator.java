package com.kim;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.temporal.ChronoUnit;

import com.kim.dto.MatDto;
import com.kim.dto.OrdersDto;
import com.kim.dto.ProductDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kim.constant.Status.STANDBY;

public class calculator {

    public static void main(String[] args) {

        OrdersDto order = new OrdersDto();
        ProductDto product = new ProductDto();
        MatDto mat = new MatDto();
        MatDto matPau = new MatDto();
        MatDto matBox = new MatDto();
        MatDto matCol = new MatDto();
        MatDto matStick = new MatDto();
        List<MatDto> matDTOList = new ArrayList<>();

        // 수주
        order.setId(1L);
        order.setOrderFrom("테스트 주문자");
        order.setProduct("흑마늘즙");
        order.setBox(100);
        order.setStatus(STANDBY);
        order.setOrderDate(LocalDateTime.now());

        // 원자재 재고
        mat.setMatName("흑마늘");
        mat.setMatNum(1);

        matPau.setMatName("파우치");
        matPau.setMatNum(0);

        matStick.setMatName("스틱파우치");
        matStick.setMatNum(0);

        matBox.setMatName("box");
        matBox.setMatNum(0);

        matCol.setMatName("콜라겐");
        matCol.setMatNum(0);


        matDTOList.add(mat); // 원자재
        matDTOList.add(matPau); // 파우치
        matDTOList.add(matBox); // 박스
        matDTOList.add(matCol); // 콜라겐


        // 완제품 재고
        product.setProduct(order.getProduct());
        product.setNum(10);

        System.out.println("==================================");
        System.out.println("수주 제품: " + order.getProduct());
        System.out.println("수주량: " + order.getBox());
        System.out.println("==================================");
        System.out.println("완제품 재고: " + product.getNum());

        // 생산 해야되는 수량
        int quantityToProduce = order.getBox() - product.getNum();
        // 양배추
        double productionCapacityCab = matDTOList.get(0).getMatNum() * 2 * 0.8 / 0.08 / 30;
        // 흑마늘
        double productionCapacityGal = matDTOList.get(0).getMatNum() * 4 * 0.6 / 0.02 / 30;
        // 젤리
        double productionCapacityJel = matDTOList.get(0).getMatNum() / 0.005 / 25;
        // 콜라겐
        double productionCapacityCol = matDTOList.get(3).getMatNum() / 0.002 / 25;
        // 파우치
        double productionCapacityPau = matPau.getMatNum();
        // 스틱 파우치
        double productionCapacityStick = matStick.getMatNum();
        // 포장 박스
        double productionCapacityBox = matBox.getMatNum() - product.getNum(); // 가진 box - 완제품


        // 수주량 > 완재품 재고
        if (order.getBox() > product.getNum()) {

            System.out.println(mat.getMatName() + "재고: " + (int)mat.getMatNum() + "kg");

            if (order.getProduct().equals("양배추즙") || order.getProduct().equals("흑마늘즙")) {
                if (order.getProduct().equals("양배추즙")) {
                    // 양배추즙
                    producible(order, mat, productionCapacityCab, product, quantityToProduce);
                } else {
                    // 흑마늘즙
                    producible(order, mat, productionCapacityGal, product, quantityToProduce);
                }
                // 파우치 가진거
                producible(order, matPau, productionCapacityPau, product, quantityToProduce * 30);

                producible(order, matBox, productionCapacityBox, product, quantityToProduce);

            } else {
                // 젤리 자재 확인 없으면 발주
                producible(order, mat, productionCapacityJel, product, quantityToProduce);

                // 콜라겐 자재 확인 없으면 발주
                producible(order, matCol, productionCapacityCol, product, quantityToProduce);

                // 파우치 자재 확인 없으면 발주
                producible(order, matStick, productionCapacityStick, product, quantityToProduce * 25);

                // 박스 자재 확인 없으면 발주
                producible(order, matBox, productionCapacityBox, product, quantityToProduce);
            }

            // 이전 발주 건 조회하고 자재발주 수량이 오버 되는지 체크 해야됨

        } else { // 완제품이 충분할경우
            System.out.println("출하");
        }

        System.out.println("==================================");

    }

    public static void producible(OrdersDto order, MatDto mat, double productionCapacity, ProductDto product, int quantityToProduce) {
        if (productionCapacity >= quantityToProduce) { // 여기 수정함
            System.out.println(mat.getMatName() + " 자재 충분 / 자재량: " + (int) mat.getMatNum());

        } else {
            List<Integer> orderMat = orderVolume(order, mat, product);
            for (int i : orderMat) {
                System.out.println(mat.getMatName() + " 발주량 " + i + "kg");
            }
            LocalDateTime deliveryDate = orderDelivery(mat);
            System.out.println("예상 입고일: " + deliveryDate);
        }
    }

    public static List<Integer> orderVolume(OrdersDto order, MatDto mat, ProductDto product) {

        List<Integer> orderQuantityList = new ArrayList<>();

        double orderQuantity;

        switch (mat.getMatName()) {

            case "양배추":
                orderQuantity = (order.getBox() - product.getNum()) * 1.5; // 양배추즙 1box = 양배추 1.5kg

                orderQuantity -= mat.getMatNum(); // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 1000) * 1000; // 최소 주문량 1000kg 단위

                break;

            case "흑마늘":
                orderQuantity = (order.getBox() - product.getNum()) * 0.25; // 흑마늘즙 1box = 흑마늘 0.25kg

                orderQuantity -= mat.getMatNum(); // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 10) * 10; // 최소 주문량 10kg 단위

                break;

            case "석류":
            case "매실":
            case "콜라겐":
                orderQuantity = (order.getBox() - product.getNum()) * 0.125; // 젤리스틱 1box = 원자재(석류, 매실) 0.125kg

                orderQuantity -= mat.getMatNum(); // 수주건에 대한 원자재 필요량 - 현재 원자재 재고량

                orderQuantity = Math.ceil(orderQuantity / 5) * 5;
                break;

            case "파우치":
            case "스틱파우치":

                if (mat.getMatName().equals("파우치")) {
                    orderQuantity = (order.getBox() - product.getNum()) * 30;
                } else {
                    orderQuantity = (order.getBox() - product.getNum()) * 25;
                }
                orderQuantity -= mat.getMatNum();
                orderQuantity = Math.ceil(orderQuantity / 1000) * 1000;

                break;

            default:
                // 박스
                orderQuantity = order.getBox() - product.getNum();
                orderQuantity -= mat.getMatNum();
                orderQuantity = Math.ceil(orderQuantity / 500) * 500;
                break;
        }


        // 양배추 최소 발주량 1ton
        if (mat.getMatName().equals("양배추") && orderQuantity <= 1000) {
            // 발주량이 1000보다 작을 때
            orderQuantity = 1000;
            orderQuantityList.add((int) orderQuantity);
        } else if (mat.getMatName().equals("흑마늘") && orderQuantity <= 10) {
            // 흑마늘 발주량이 10보다 작을 때
            orderQuantity = 10;
            orderQuantityList.add((int) orderQuantity);
        } else if ((mat.getMatName().equals("석류") || mat.getMatName().equals("매실")) && orderQuantity <= 5) {
            orderQuantity = 5;
            orderQuantityList.add((int) orderQuantity);
        } else if ((mat.getMatName().equals("파우치") || mat.getMatName().equals("스틱파우치")) && orderQuantity <= 1000) {
            orderQuantity = 1000;
            orderQuantityList.add((int) orderQuantity);
        } else if (mat.getMatName().equals("box") && orderQuantity <= 500) {
            orderQuantity = 500;
            orderQuantityList.add((int) orderQuantity);
        } else {

            boolean stop = false;
            double orderMax = orderQuantity;
            while (!stop) {

                switch (mat.getMatName()) {

                    case "양배추":
                    case "흑마늘":
                        if (orderMax > 5000) {
                            orderMax -= 5000;
                            orderQuantityList.add(5000);
                        } else if (0 < orderMax) {
                            orderQuantityList.add((int) orderMax);
                            orderMax -= orderMax;
                        } else {
                            stop = true;
                        }
                        break;

                    case "석류":
                    case "매실":
                    case "콜라겐":
                        if (orderMax > 500) {
                            orderMax -= 500;
                            orderQuantityList.add(500);
                        } else if (0 < orderMax) {
                            orderQuantityList.add((int) orderMax);
                            orderMax -= orderMax;
                        } else {
                            stop = true;
                        }
                        break;

                    case "box":
                        // 박스
                        if (orderMax > 10000) {
                            orderMax -= 10000;
                            orderQuantityList.add(10000);
                        } else if (0 < orderMax) {
                            orderQuantityList.add((int) orderMax);
                            orderMax -= orderMax;
                        } else {
                            stop = true;
                        }

                        break;

                    default:
                        // 파우치 스틱
                        if (orderMax > 1000000) {
                            orderMax -= 1000000;
                            orderQuantityList.add(1000000);
                        } else if (0 < orderMax) {
                            orderQuantityList.add((int) orderMax);
                            orderMax -= orderMax;
                        } else {
                            stop = true;
                        }
                        break;
                }
            }
        }

        return orderQuantityList;
    }

    public static LocalDateTime orderDelivery(MatDto mat) {
        // 입고는 월, 수, 금 오전 10:00 창고에 도착

        LocalDateTime orderTime = LocalDateTime.now(); // 원자재 발주 넣은 시간
        System.out.println("발주일: " + orderTime);
        LocalDateTime deliveryDate;

        if (mat.getMatName().equals("양배추") || mat.getMatName().equals("흑마늘")) {

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
                if (mat.getMatName().equals("석류") || mat.getMatName().equals("매실") || mat.getMatName().equals("콜라겐")) {
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
                if (mat.getMatName().equals("석류") || mat.getMatName().equals("매실") || mat.getMatName().equals("콜라겐")) {

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

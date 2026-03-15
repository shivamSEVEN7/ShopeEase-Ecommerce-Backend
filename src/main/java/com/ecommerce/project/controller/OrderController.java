package com.ecommerce.project.controller;

import com.cashfree.pg.model.PaymentEntity;
import com.ecommerce.project.dto.*;
import com.ecommerce.project.model.OrderStatus;
import com.ecommerce.project.service.CashFreeService;
import com.ecommerce.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    CashFreeService cashFreeService;
//    @PostMapping("/order")
//    public ResponseEntity<OrderDTO> orderProducts(@RequestBody OrderRequestDTO orderRequestDTO) {
//        OrderDTO order = orderService.placeOrder(orderRequestDTO.getAddressId(), orderRequestDTO.getPaymentMethod());
//        return new ResponseEntity<>(order, HttpStatus.CREATED);
//    }
    @PostMapping("/orders/create")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody OrderRequestDTO orderRequest) {
        return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.OK);
    }
    @PostMapping("/orders/payment/success")
    public String verifyPaymentAndConfirmOrder(@RequestParam PayUResponse payUResponse) {
        System.out.println("Payment is succesfull");
        return null; // Replace with actual implementation
    }
    @PostMapping("/orders/payment/failure")
    public String paymentFiled(@RequestParam Map<String, String> response) {
        System.out.println("Payment is Failed");
        return null; // Replace with actual implementation
    }

    @GetMapping("/orders/status")
    public OrderStatus getOrderStatus(@RequestParam String orderId){
        return orderService.getOrderStatus(orderId);
    }

    @GetMapping("/orders")
    public ResponseEntity<UserOrdersResponse> getUserOrders(@RequestParam(defaultValue = "0", name = "offset", required = false) int offset ,
                                                            @RequestParam(defaultValue = "8", name = "limit", required = false) int limit,
                                                            @RequestParam(defaultValue = "createdAt", name = "sortBy", required = false) String sortBy,
                                                            @RequestParam(defaultValue = "desc", name = "sortOrder", required = false) String sortOrder){

    return new ResponseEntity<>(orderService.getUserOrders(offset, limit, sortBy, sortOrder), HttpStatus.OK) ;
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderByOrderId(@PathVariable String orderId){
        return new ResponseEntity<>(orderService.getOrderByOrderId(orderId), HttpStatus.OK) ;
    }



}

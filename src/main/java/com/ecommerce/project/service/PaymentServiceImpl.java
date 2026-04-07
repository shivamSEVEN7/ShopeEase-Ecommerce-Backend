package com.ecommerce.project.service;

import com.cashfree.pg.model.PaymentEntity;
import com.ecommerce.project.dto.PaymentDTO;
import com.ecommerce.project.dto.PaymentInitiationDTO;
import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.OrderRepo;
import com.ecommerce.project.repositories.PaymentRepo;
import com.ecommerce.project.repositories.ProductRepo;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepo paymentRepository;
    @Autowired
    OrderRepo orderRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CashFreeService cashFreeService;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ProductRepo productRepo;

    @Override
    public String initiateOnlinePayment(Order order, String paymentMode) {
        String paymentSessionId = cashFreeService.createOrder(order.getTotalAmount(), order.getOrderId());
        Payment payment = new Payment(order.getTotalAmount(), PaymentStatus.INITIATED, order.getOrderId(), order);
//        payment.setPaymentMethod(paymentMethod);
//        if(paymentMethod.equals("COD")){
//            payment.setPaymentStatus("UNPAID");
//        }else{
//            payment.setPaymentStatus("INITIATED");
//        }

        payment = paymentRepository.save(payment);
        order.getPayment().add(payment);
        orderRepository.save(order);
//        Map<String, String> params = new HashMap<>();
//        params.put("amount", String.valueOf(payment.getAmount()));
//        params.put("productinfo", "Test Product Info");
//        params.put("firstname", order.getShippingAddress().getName());
//        params.put("email", order.getEmail());
//        params.put("phone", authUtil.loggedInUser().getMobileNumber());

        return paymentSessionId;
    }

    @Override
    public Order initiateCodPayment(Order order, String paymentMode) {
        Payment payment = new Payment(order.getTotalAmount(), PaymentStatus.UNPAID, order.getOrderId(), order);
        payment = paymentRepository.save(payment);
        order.getPayment().add(payment);
        orderRepository.save(order);
        return order;
    }

    @Override
    public void updatePaymentSuccess(String payload) {
        JsonObject data = JsonParser.parseString(payload)
                .getAsJsonObject()
                .getAsJsonObject("data");
        JsonObject payment = data
                .getAsJsonObject("payment");
        JsonObject order = data.getAsJsonObject("order");
        String referenceOrderId = order.get("order_id").getAsString();
        String cashfreeOrderStatus = cashFreeService.fetchOrderStatus(referenceOrderId);
        Order order1 = orderRepository.findByOrderId(referenceOrderId);
        if(order1.getStatus() != OrderStatus.CONFIRMED){
            order1.setStatus(OrderStatus.CONFIRMED);
            List<OrderItem> orderItems = order1.getOrderItems();
            for (OrderItem item : orderItems){
                Product product =  item.getProduct();
                product.setQuantity(product.getQuantity()-item.getQuantity());
                productRepo.save(product);
            }
        }
        String cfPaymentId = payment.get("cf_payment_id").getAsString();
        String cfPaymentMessage = payment.get("payment_message").getAsString();
        String paymentMethod = payment.get("payment_group").getAsString();
        String bankReference = payment.get("bank_reference").getAsString();
        String paymentTime = payment.get("payment_time").getAsString();
        Payment payment1 = paymentRepository.findFirstByReferenceOrderId(referenceOrderId);
        PaymentMethod method = switch (paymentMethod) {
            case "credit_card", "debit_card" -> PaymentMethod.CARD;
            case "net_banking" -> PaymentMethod.NET_BANKING;
            case "upi" -> PaymentMethod.UPI;
            case "wallet" -> PaymentMethod.WALLET;
            case "pay_later" -> PaymentMethod.PAY_LATER;
            default -> PaymentMethod.OTHERS;
        };
        if (payment1.getCashfreePaymentId() == null) {
            payment1.setCashfreePaymentId(cfPaymentId);
            payment1.setCashfreePaymentMessage(cfPaymentMessage);
            payment1.setMethod(method);
            payment1.setBankReference(bankReference);
            payment1.setPaymentTime(OffsetDateTime.parse(paymentTime));
            payment1.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment1);
        } else {
            Payment newPayment = new Payment(cfPaymentId, bankReference, method, payment1.getAmount(), cfPaymentMessage, PaymentStatus.PAID, order1, OffsetDateTime.parse(paymentTime), referenceOrderId);
            paymentRepository.save(newPayment);
        }

    }

    @Override
    public void updatePaymentSuccess(PaymentEntity paymentEntity, Order order) {
        PaymentMethod method = switch (paymentEntity.getPaymentGroup()) {
            case "credit_card", "debit_card" -> PaymentMethod.CARD;
            case "net_banking" -> PaymentMethod.NET_BANKING;
            case "upi" -> PaymentMethod.UPI;
            case "wallet" -> PaymentMethod.WALLET;
            case "pay_later" -> PaymentMethod.PAY_LATER;
            default -> PaymentMethod.OTHERS;
        };
        if (order.getPayment().size() > 1) {
            Payment newPayment = new Payment(paymentEntity.getCfPaymentId(), paymentEntity.getBankReference(), method, paymentEntity.getOrderAmount().doubleValue(), paymentEntity.getPaymentMessage(), PaymentStatus.PAID, order, OffsetDateTime.parse(paymentEntity.getPaymentTime()), order.getOrderId());
            paymentRepository.save(newPayment);
        }
        else {
            if(order.getPayment().getFirst().getStatus().equals(PaymentStatus.INITIATED)){
                Payment payment1 = order.getPayment().getFirst();
                payment1.setCashfreePaymentId(paymentEntity.getCfPaymentId());
                payment1.setCashfreePaymentMessage(paymentEntity.getPaymentMessage());
                payment1.setMethod(method);
                payment1.setBankReference(paymentEntity.getBankReference());
                payment1.setPaymentTime(OffsetDateTime.parse(paymentEntity.getPaymentTime()));
                payment1.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment1);
            }
            else{
                Payment newPayment = new Payment(paymentEntity.getCfPaymentId(), paymentEntity.getBankReference(), method, paymentEntity.getOrderAmount().doubleValue(), paymentEntity.getPaymentMessage(), PaymentStatus.PAID, order, OffsetDateTime.parse(paymentEntity.getPaymentTime()), order.getOrderId());
                paymentRepository.save(newPayment);
            }
        }
    }


    @Override
    public void updatePaymentFailure(String payload, PaymentStatus paymentStatus) {
        JsonObject data = JsonParser.parseString(payload)
                .getAsJsonObject()
                .getAsJsonObject("data");
        JsonObject payment = data
                .getAsJsonObject("payment");
        JsonObject order = data.getAsJsonObject("order");
        String referenceOrderId = order.get("order_id").getAsString();
        String cashfreeOrderStatus = cashFreeService.fetchOrderStatus(referenceOrderId);
        Order order1 = orderRepository.findByOrderId(referenceOrderId);
        if(cashfreeOrderStatus.equals("ACTIVE")){
            order1.setStatus(OrderStatus.PAYMENT_FAILED);
            orderRepository.save(order1);
        }
        String cfPaymentId = payment.get("cf_payment_id").getAsString();
        String cfPaymentMessage = payment.get("payment_message").getAsString();
        String paymentMethod = payment.get("payment_group").getAsString();
        String bankReference = payment.get("bank_reference").getAsString();
        String paymentTime = payment.get("payment_time").getAsString();
        Payment payment1 = paymentRepository.findFirstByReferenceOrderId(referenceOrderId);
        PaymentMethod method = switch (paymentMethod) {
            case "credit_card", "debit_card" -> PaymentMethod.CARD;
            case "net_banking" -> PaymentMethod.NET_BANKING;
            case "upi" -> PaymentMethod.UPI;
            case "wallet" -> PaymentMethod.WALLET;
            case "pay_later" -> PaymentMethod.PAY_LATER;
            default -> PaymentMethod.OTHERS;
        };
        if (payment1.getCashfreePaymentId() == null) {
            payment1.setCashfreePaymentId(cfPaymentId);
            payment1.setCashfreePaymentMessage(cfPaymentMessage);
            payment1.setMethod(method);
            payment1.setBankReference(bankReference);
            payment1.setPaymentTime(OffsetDateTime.parse(paymentTime));
            payment1.setStatus(paymentStatus);
            paymentRepository.save(payment1);
        } else {
            Payment newPayment = new Payment(cfPaymentId, bankReference, method, payment1.getAmount(), cfPaymentMessage, paymentStatus, order1, OffsetDateTime.parse(paymentTime), referenceOrderId);
            paymentRepository.save(newPayment);
        }

    }

    @Override
    public Payment getLastPaymentAttempt(String referenceOrderId){
        Payment payment =  paymentRepository.findFirstByReferenceOrderIdOrderByCreatedAtDesc(referenceOrderId).orElse(null);
        return payment;
    }
}

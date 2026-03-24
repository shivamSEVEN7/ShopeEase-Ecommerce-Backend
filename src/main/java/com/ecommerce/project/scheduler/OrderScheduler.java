package com.ecommerce.project.scheduler;

import com.cashfree.pg.model.PaymentEntity;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.OrderRepo;
import com.ecommerce.project.repositories.PaymentRepo;
import com.ecommerce.project.repositories.ScheduledTaskRepo;
import com.ecommerce.project.service.CashFreeService;
import com.ecommerce.project.service.OrderService;
import jakarta.annotation.PostConstruct;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrderScheduler {
    @Autowired
    OrderRepo orderRepository;
    @Autowired
    CashFreeService cashFreeService;
    @Autowired
    private ThreadPoolTaskScheduler scheduler;
    @Autowired
    private ScheduledTaskRepo taskRepo;
    @Autowired
    private OrderRepo OrderRepository;
    @Autowired
    private PaymentRepo paymentRepository;

    Logger logger = LoggerFactory.getLogger(OrderScheduler.class);

    @PostConstruct
    public void init() {
        taskRepo.findAllByStatus("PENDING").forEach((task) ->
        { long delay = Duration.between(LocalDateTime.now(), task.getRunAt()).toMillis(); if (delay < 0) delay = 0;
        scheduler.schedule(() -> runTask(task.getId()), new java.util.Date(System.currentTimeMillis() + delay));});
    }

    @Scheduled(fixedRate = 60000) //10 Minutes
    @SchedulerLock(name = "processPendingOrders", lockAtLeastFor = "20s", lockAtMostFor = "10m")
    public void processPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING_PAYMENT);
        for (Order order : pendingOrders) {
            OffsetDateTime orderCreationTime = order.getCreatedAt();
            OffsetDateTime currentTime = OffsetDateTime.now();
            if (orderCreationTime.isBefore(currentTime.minusMinutes(30))) {
                if (cashFreeService.fetchOrderStatus(order.getOrderId()).equals("PAID")) {
                    logger.info("Confirming Order " + order.getOrderId());
                    order.setStatus(OrderStatus.CONFIRMED);
                    orderRepository.save(order);
                    PaymentEntity successfulPayment = cashFreeService.fetchSuccessfulPayment(order.getOrderId());
                    PaymentMethod method = switch (successfulPayment.getPaymentGroup()) {
                        case "credit_card", "debit_card" -> PaymentMethod.CARD;
                        case "net_banking" -> PaymentMethod.NET_BANKING;
                        case "upi" -> PaymentMethod.UPI;
                        case "wallet" -> PaymentMethod.WALLET;
                        case "pay_later" -> PaymentMethod.PAY_LATER;
                        default -> PaymentMethod.OTHERS;
                    };
                    if (order.getPayment().size() > 1) {
                        Payment newPayment = new Payment(successfulPayment.getCfPaymentId(), successfulPayment.getBankReference(), method, successfulPayment.getOrderAmount().doubleValue(), successfulPayment.getPaymentMessage(), PaymentStatus.PAID, order, OffsetDateTime.parse(successfulPayment.getPaymentTime()), order.getOrderId());
                        paymentRepository.save(newPayment);
                    }
                    else {
                        if(order.getPayment().getFirst().getStatus().equals(PaymentStatus.INITIATED)){
                            Payment payment1 = order.getPayment().getFirst();
                            payment1.setCashfreePaymentId(successfulPayment.getCfPaymentId());
                            payment1.setCashfreePaymentMessage(successfulPayment.getPaymentMessage());
                            payment1.setMethod(method);
                            payment1.setBankReference(successfulPayment.getBankReference());
                            payment1.setPaymentTime(OffsetDateTime.parse(successfulPayment.getPaymentTime()));
                            payment1.setStatus(PaymentStatus.PAID);
                            paymentRepository.save(payment1);
                        }
                        else{
                            Payment newPayment = new Payment(successfulPayment.getCfPaymentId(), successfulPayment.getBankReference(), method, successfulPayment.getOrderAmount().doubleValue(), successfulPayment.getPaymentMessage(), PaymentStatus.PAID, order, OffsetDateTime.parse(successfulPayment.getPaymentTime()), order.getOrderId());
                            paymentRepository.save(newPayment);
                        }
                    }

                }
                else {
                    order.setStatus(OrderStatus.PAYMENT_FAILED);
                }
            }
        }
    }

    public void scheduleOrderCheck(Long orderId, LocalDateTime runAt) {
        ScheduledTask task = new ScheduledTask();
        task.setOrderId(orderId);
        task.setRunAt(runAt);
        task.setStatus("PENDING");
        taskRepo.save(task);
        long delay = Duration.between(LocalDateTime.now(), runAt).toMillis();
        scheduler.schedule(() -> runTask(task.getId()),
                new java.util.Date(System.currentTimeMillis() + delay));
    }

    private void runTask(Long taskId) {
        ScheduledTask task = taskRepo.findById(taskId).orElse(null);
        if (task == null || !"PENDING".equals(task.getStatus())) return;

        Order order = orderRepository.findById(task.getOrderId()).orElse(null);
        if(order.getStatus().equals(OrderStatus.PENDING_PAYMENT) || order.getStatus().equals(OrderStatus.PAYMENT_FAILED) ){
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        task.setStatus("COMPLETED");
        taskRepo.save(task);
    }
}
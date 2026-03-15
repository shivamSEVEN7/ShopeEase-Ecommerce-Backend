package com.ecommerce.project.service;

import com.ecommerce.project.dto.*;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.*;
import com.ecommerce.project.scheduler.OrderScheduler;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    AuthUtil authUtil;
    @Autowired
    CartRepo cartRepo;
    @Autowired
    CartItemRepo cartItemRepo;
    @Autowired
    AddressRepo addressRepository;
    @Autowired
    ShippingAddressRepo shippingAddressRepo;
    @Autowired
    OrderItemRepo orderItemRepository;
    @Autowired
    PaymentService paymentService;
    @Autowired
    OrderRepo orderRepository;
    @Autowired
    ProductRepo productRepository;
    @Autowired
    PaymentRepo paymentRepository;
    @Autowired
    CartService cartService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    OrderScheduler orderScheduler;
    @Autowired
    private CashFreeService cashFreeService;
    @Value("${image.base.url}")
    private String imagePath;

    @Override
    @Transactional
    public CreateOrderResponse placeOrder(OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepo.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", emailId, "email");
        }
        Address address = addressRepository.findById(orderRequestDTO.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", orderRequestDTO.getAddressId(), "addressId"));
        ShippingAddress shippingAddress = modelMapper.map(address, ShippingAddress.class);
        String orderId = generateOrderId();
        List<OrderItem> orderItems = new ArrayList<>();
        Order order = new Order(orderId, orderRequestDTO.getPaymentMode().equalsIgnoreCase("Cash On Delivery")  ? OrderStatus.CONFIRMED :  OrderStatus.PENDING_PAYMENT, 0.0,  orderItems, emailId);
        shippingAddress.setOrder(order);

        order.setUser(authUtil.loggedInUser());
        orderRepository.save(order);
        shippingAddressRepo.save(shippingAddress);
        List<OrderItemRequestDTO> orderItemDTOS = orderRequestDTO.getItems();
        for(OrderItemRequestDTO orderItem : orderItemDTOS){
            CartItem cartItem =  cartItemRepo.findCartItemByCartIdAndProductId(cart.getCartId(), orderItem.getProductId());
            Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> new ResourceNotFoundException("productId", orderItem.getProductId(), "Product"));
            OrderItem orderItem1 = new OrderItem(order, orderId, product, cartItem.getQuantity(), product.getPrice(), product.getSpecialPrice(), product.getPrice() * cartItem.getQuantity() , cartItem.getQuantity() * product.getSpecialPrice(), Math.round((cartItem.getQuantity() * (product.getPrice()-product.getSpecialPrice())) * 100.0) / 100.0 );
            orderItemRepository.save(orderItem1);
            order.setTotalAmount(Math.round((order.getTotalAmount() + orderItem1.getTotalAmountAtPurchase()) * 100.0) / 100.0);
            order.setSubTotal(Math.round((order.getSubTotal() + orderItem1.getPriceAtPurchase()) * 100.0) / 100.0);
            order.setDiscount(Math.round((order.getDiscount() + orderItem1.getDiscount()) * 100.0) / 100.0);
            orderItems.add(orderItem1);
        }
        cartService.cleanupCart(orderItemDTOS);
        if(order.getTotalAmount() < 500){
            order.setShipping(50);
            order.setTotalAmount(order.getTotalAmount() + 50);
        }
        orderScheduler.scheduleOrderCheck(order.getId(), order.getCreatedAt().plusHours(6).toLocalDateTime());
        if(orderRequestDTO.getPaymentMode().equalsIgnoreCase("Cash On Delivery")){
           Order updatedOrder =  paymentService.initiateCodPayment(order, orderRequestDTO.getPaymentMode());
            return new CreateOrderResponse(null, orderId);
        }

        String paymentSessionId = paymentService.initiateOnlinePayment(order, orderRequestDTO.getPaymentMode());
        return new CreateOrderResponse(paymentSessionId, orderId);
    }

    @Override
    public OrderStatus getOrderStatus(String orderId){
       Order order =  orderRepository.findByOrderIdAndUser(orderId, authUtil.loggedInUser());
       if(order==null){
           throw new AccessDeniedException("You are not allowed to view this order");
       }
       if(cashFreeService.fetchOrderStatus(orderId).equals("PAID")){
           if(order.getStatus() != OrderStatus.CONFIRMED){
               order.setStatus(OrderStatus.CONFIRMED);
               orderRepository.save(order);
               paymentService.updatePaymentSuccess(cashFreeService.fetchSuccessfulPayment(orderId), order);
               return OrderStatus.CONFIRMED;
           }
           else {
               return OrderStatus.CONFIRMED;
           }
       }
       return OrderStatus.PAYMENT_FAILED;

    }

    @Override
    public UserOrdersResponse getUserOrders(int offset, int limit, String sortBy, String sortOrder) {
        Sort sortingDetails = sortOrder.equals("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(offset/limit, limit, sortingDetails);
        Page<Order> userOrdersPage =  orderRepository.findAllByUser(authUtil.loggedInUser(), pageable);
        List<Order> userOrders = userOrdersPage.getContent();
        List<OrderDTO> userOrdersDto =  userOrders.stream()
                .map(userOrder -> {OrderDTO dto = modelMapper.map(userOrder, OrderDTO.class);
                    dto.setPayment(modelMapper.map(paymentService.getLastPaymentAttempt(userOrder.getOrderId()), PaymentDTO.class));

                    dto.getOrderItems().forEach(orderItem1 -> {orderItem1.getProduct().setImage(imagePath + orderItem1.getProduct().getImage());});
                    return dto;})
                .toList();
        return new UserOrdersResponse(userOrdersDto, new OffsetPaginationDetails(offset, limit, userOrdersPage.getTotalElements(), userOrdersPage.isLast()));
    }

    @Override
    public OrderDTO getOrderByOrderId(String orderId) {
        Order order = orderRepository.findByOrderIdAndUser(orderId, authUtil.loggedInUser());
        if(order==null){
            throw new ResourceNotFoundException("Order", orderId, "orderId");
        }
        Payment payment;
        if(order.getPayment().size() ==1){
            payment = order.getPayment().get(0);
        }
        else {
          payment =  paymentRepository.findFirstByReferenceOrderIdOrderByPaymentTimeDesc(order.getOrderId());
        }
        OrderDTO orderDTO =  modelMapper.map(order, OrderDTO.class);
        orderDTO.setPayment(modelMapper.map(payment, PaymentDTO.class));
        orderDTO.getOrderItems().forEach(orderItem1 -> orderItem1.getProduct().setImage(imagePath + orderItem1.getProduct().getImage()));
        return orderDTO;
    }
   @Override
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", orderId, "orderId"));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    public boolean isPaymentPending(Long orderId) {
        if(orderRepository.getById(orderId).getStatus().equals(OrderStatus.PAYMENT_FAILED) || orderRepository.getById(orderId).getStatus().equals(OrderStatus.PENDING_PAYMENT)){
            return true;
        }
        return false;
    }

    public String generateOrderId() {
        final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final SecureRandom random = new SecureRandom();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());
        StringBuilder randomPart = new StringBuilder(6);
        for (int i = 0; i <6; i++) {
            int randomIndex = random.nextInt(CHAR_SET.length());
            randomPart.append(CHAR_SET.charAt(randomIndex));
        }
        return "ORD-" + datePart + "-" + randomPart;
    }

}

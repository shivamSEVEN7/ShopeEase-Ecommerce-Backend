package com.ecommerce.project.service;

import com.ecommerce.project.dto.CartDTO;
import com.ecommerce.project.dto.CartItemDTO;
import com.ecommerce.project.dto.OrderItemRequestDTO;
import com.ecommerce.project.dto.ProductDTO;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Image;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.repositories.CartItemRepo;
import com.ecommerce.project.repositories.CartRepo;
import com.ecommerce.project.repositories.ProductRepo;
import com.ecommerce.project.utility.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{
    @Autowired
    CartRepo cartRepository;
    @Autowired
    ProductRepo productRepository;
    @Autowired
    CartItemRepo cartItemRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AuthUtil authUtil;
    @Value("${image.base.url}")
    String imageBaseUrl;
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUser(authUtil.loggedInUser())
                .orElse(createCart());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setPriceAtAdd(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        cartRepository.save(cart);
        cart.getCartItems().add(newCartItem);

        CartDTO cartDTO = getUserCart();
        return cartDTO;
    }
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.size() == 0) {
            throw new APIException("No cart exists");
        }
       return carts.stream().map(cart -> modelMapper.map(cart,CartDTO.class)).toList();
    }

    @Override
    public CartDTO getUserCart() {
        Cart cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(cart==null){
            return modelMapper.map(createCart(), CartDTO.class);
        }
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.sort(Comparator.comparing(CartItem::getCreatedAt));
        cartDTO.setCartItems(cartItems.stream().map(ci -> {CartItemDTO cartItemDTO =  modelMapper.map(ci, CartItemDTO.class);
        for(Image image : ci.getProduct().getImages()){
            if(image.getIsPrimary()){
                cartItemDTO.getProduct().setImage(image.getUrl());
            }
        }

            return cartItemDTO;}).toList());

        Double price = cartItems.stream().mapToDouble(cartItem1 -> cartItem1.getProduct().getPrice()*cartItem1.getQuantity()).sum();
        cartDTO.setPrice(price);
        Double total = Math.round(cartItems.stream().mapToDouble(cartItem1 -> cartItem1.getProduct().getSpecialPrice() * cartItem1.getQuantity()).sum() * 100.0) / 100.0;
        cartDTO.setShipping(total<500?50.0:0.0);
        cartDTO.setTotalAmount(total>500?total:total + 50.0);
        Double discount = price-total;

        cartDTO.setDiscount(Math.round(discount * 100.0) / 100.0);
        return  cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, int quantity) {

        Cart userCart = cartRepository.findByUser(authUtil.loggedInUser()).orElseThrow(() -> new ResourceNotFoundException("Cart", null, "cartId"));
        Long cartId  = userCart.getCartId();

        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cartId, productId) ;
        int newQuantity = cartItem.getQuantity() + quantity;
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId, "productId"));


        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < newQuantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }
        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative.");
        }
        if (newQuantity == 0){
            deleteProductFromCart(productId);
        } else {
            cartItem.setQuantity(newQuantity);
        }


        CartItem updatedItem = cartItemRepository.save(cartItem);

        CartDTO cartDTO = getUserCart();

        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO deleteProductFromCart(Long productId) {
        Cart cart = cartRepository.findByUser(authUtil.loggedInUser()) .orElseThrow(() -> new ResourceNotFoundException("Cart", authUtil.loggedInUserId(), "cartId"));
        CartItem cartItem = cartItemRepository.findCartItemByCartIdAndProductId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", productId, "productId" );
        }
        cart.getCartItems().remove(cartItem);
        cartItemRepository.deleteCartItemByProductIdAndCartId(cart.getCartId(), productId);

        return getUserCart();
    }

    @Override
    @Transactional
    public void cleanupCart(List<OrderItemRequestDTO> orderItemDTOS) {
        for(OrderItemRequestDTO orderItemDTO : orderItemDTOS){
            deleteProductFromCart(orderItemDTO.getProductId());
        }
    }


    private Cart createCart() {
        Cart userCart  = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }

}

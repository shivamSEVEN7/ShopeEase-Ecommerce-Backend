package com.ecommerce.project.model;

public enum Permissions {
    USER_VIEW,
    USER_MANAGE,
    PRODUCT_MANAGE,   // Add, update, delete any product
    ORDER_VIEW,       // View all orders
    CATEGORY_MANAGE,  // Add, update, delete categories

    // Customer permissions
    ORDER_CREATE,
    REVIEW_CREATE,
    PROFILE_UPDATE,

    // Seller permissions
    PRODUCT_CREATE,   // Add products
    PRODUCT_UPDATE,   // Edit own products
    INVENTORY_MANAGE,

    // Guest permissions
    PRODUCT_VIEW,
    CATEGORY_VIEW;
}

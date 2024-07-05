package com.vedasole.ekartecommercebackend.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class AppConstant {

    @AllArgsConstructor
    @Getter
    public enum RELATIONS {

        CATEGORY("Category"),
        CATEGORIES("Categories"),
        PRODUCT("Product"),
        PRODUCTS("Products"),
        CUSTOMER("Customer"),
        CUSTOMERS("Customers"),
        SHOPPING_CART("ShoppingCart"),
        SHOPPING_CARTS("ShoppingCarts"),
        ORDER("Order"),
        ORDERS("Orders"),
        USER("User"),
        USERS("Users");

        private final String value;

    }

    @AllArgsConstructor
    @Getter
    public enum OrderStatus {
        ORDER_CREATED("ORDER_CREATED"),
        ORDER_PLACED("ORDER_PLACED"),
        ORDER_FAILED("ORDER_FAILED"),
        ORDER_DISPATCHED("ORDER_DISPATCHED"),
        ORDER_SHIPPED("ORDER_SHIPPED"),
        ORDER_DELIVERED("ORDER_DELIVERED"),
        ORDER_COMPLETED("ORDER_COMPLETED");

        private final String name;

    }

    @AllArgsConstructor
    @Getter
    public enum Role {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        private final String value;
    }

}

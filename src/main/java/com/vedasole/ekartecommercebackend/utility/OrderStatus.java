package com.vedasole.ekartecommercebackend.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {

    ORDER_PLACED("ORDER_PLACED"),
    ORDER_FAILED("ORDER_FAILED"),
    ORDER_DISPATCHED("ORDER_DISPATCHED"),
    ORDER_SHIPPED("ORDER_SHIPPED"),
    ORDER_DELIVERED("ORDER_DELIVERED"),
    ORDER_COMPLETED("ORDER_COMPLETED");

    private final String name;

}

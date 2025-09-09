package com.empresa.demo.service;

import com.empresa.demo.model.Order;

import java.math.BigDecimal;

public interface OrderService {
    BigDecimal calculateDiscount(Order order);
}

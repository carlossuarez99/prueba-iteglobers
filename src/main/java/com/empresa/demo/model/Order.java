package com.empresa.demo.model;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Order {
    private BigDecimal total;
    private boolean vip;
}

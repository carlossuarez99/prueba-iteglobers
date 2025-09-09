package com.empresa.demo.servicio.impl;

import com.empresa.demo.exeptions.DiscountCalculationException;
import com.empresa.demo.model.Order;
import com.empresa.demo.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateDiscount_NoDiscount_ForTotal500() {
        Order order = mock(Order.class);
        when(order.getTotal()).thenReturn(BigDecimal.valueOf(500));
        when(order.isVip()).thenReturn(false);

        var result = orderService.calculateDiscount(order);

        assertEquals(BigDecimal.ZERO, result, "El descuento para una orden de 500 debe ser 0");
    }

    @Test
    void testCalculateDiscount_Discount150_ForTotal1500_NotVIP() {
        Order order = mock(Order.class);
        when(order.getTotal()).thenReturn(BigDecimal.valueOf(1500));
        when(order.isVip()).thenReturn(false);

        BigDecimal result = orderService.calculateDiscount(order);

        assertEquals(BigDecimal.valueOf(150.0), result, "El descuento para una orden de 1500 (no VIP) debe ser 150");
    }

    @Test
    void testCalculateDiscount_Discount45_ForTotal900_VIP() {
        Order order = mock(Order.class);
        when(order.getTotal()).thenReturn(BigDecimal.valueOf(900));
        when(order.isVip()).thenReturn(true);

        BigDecimal result = orderService.calculateDiscount(order);

        assertEquals(BigDecimal.valueOf(45.00).setScale(2), result.setScale(2), "El descuento para una orden de 900 (VIP) debe ser 45");
    }


    @Test
    void testCalculateDiscount_Discount600_ForTotal3000_VIP() {
        Order order = mock(Order.class);
        when(order.getTotal()).thenReturn(BigDecimal.valueOf(3000));
        when(order.isVip()).thenReturn(true);

        BigDecimal result = orderService.calculateDiscount(order);

        assertEquals(BigDecimal.valueOf(600.0), result, "El descuento para una orden de 3000 (VIP) debe ser 600 (tope 20%)");
    }

    @Test
    void testCalculateDiscount_ThrowsException_WhenTotalIsNull() {
        Order order = mock(Order.class);
        when(order.getTotal()).thenReturn(null);

        DiscountCalculationException thrown = assertThrows(DiscountCalculationException.class, () -> {
            orderService.calculateDiscount(order);
        });

        assertEquals("El total de la orden no puede ser nulo.", thrown.getMessage());
    }

}

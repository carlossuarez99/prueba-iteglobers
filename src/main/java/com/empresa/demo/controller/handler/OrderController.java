package com.empresa.demo.controller.handler;

import com.empresa.demo.exeptions.DiscountCalculationException;
import com.empresa.demo.model.Order;
import com.empresa.demo.model.generic.ApiResponse;
import com.empresa.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/calculate-discount")
    public ResponseEntity<ApiResponse<Object>> calculateDiscount(@RequestBody Order order) {
            var descuento = orderService.calculateDiscount(order);
            ApiResponse<Object> response = ApiResponse.success(descuento);
            return ResponseEntity.ok(response);
    }
}

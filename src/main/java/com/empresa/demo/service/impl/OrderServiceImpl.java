package com.empresa.demo.service.impl;

import com.empresa.demo.util.ValidationUtils;
import com.empresa.demo.exeptions.DiscountCalculationException;
import com.empresa.demo.model.Order;
import com.empresa.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal DESCUENTO_PORCENTAJE = BigDecimal.valueOf(0.1);
    private static final BigDecimal DESCUENTO_VIP_PORCENTAJE = BigDecimal.valueOf(0.05);
    private static final BigDecimal DESCUENTO_MAXIMO_PORCENTAJE = BigDecimal.valueOf(0.2);
    private static final BigDecimal LIMITE_DESCUENTO = BigDecimal.valueOf(1000);

    @Override
    public BigDecimal calculateDiscount(Order order) {
        log.info("Iniciando cálculo de descuento para la orden con total: {}", order.getTotal());

        ValidationUtils.validateNotNull(order, "La orden no puede ser nula." , 400);
        ValidationUtils.validateNotNull(order.getTotal(), "El total de la orden no puede ser nulo.", 403);

        try{
            // Descuento no VIP
            BigDecimal discount = Optional.of(order.getTotal())
                    .filter(total -> total.compareTo(LIMITE_DESCUENTO) > 0)
                    .map(this::calculateDiscountByTotal)
                    .orElse(BigDecimal.ZERO);

            // Descuento VIP
            var finalDiscount = discount;
            discount = Optional.of(order.isVip())
                    .filter(vip -> vip)
                    .map(vip -> applyVIPDiscount(order.getTotal(), finalDiscount))
                    .orElse(discount);

            discount = applyMaximumDiscount(order.getTotal(), discount, order.isVip());

            log.info("Cálculo de descuento finalizado. Descuento aplicado: {}", discount);
            return discount;
        }catch (IllegalArgumentException ex) {
            throw new DiscountCalculationException("Error de parametros" , ex.getCause(), 400);
        }catch (Exception e){
            throw new DiscountCalculationException("Error inesperado" , e.getCause(), 500);
        }
    }

    private BigDecimal calculateDiscountByTotal(BigDecimal total) {
         return total.multiply(DESCUENTO_PORCENTAJE);
    }

    private BigDecimal applyVIPDiscount(BigDecimal total, BigDecimal currentDiscount) {
        var discountVip = total.multiply(DESCUENTO_VIP_PORCENTAJE);
        var discountTotal = currentDiscount.add(discountVip);
        log.debug("Descuento adicional por VIP: {}", discountTotal);
        return discountTotal;
    }

    private BigDecimal applyMaximumDiscount(BigDecimal total, BigDecimal currentDiscount, boolean isVip) {
        BigDecimal descuentoMaximo;

        if (isVip) {
            if (total.compareTo(BigDecimal.valueOf(3000)) >= 0) {
                return total.multiply(BigDecimal.valueOf(0.2));
            } else {
                return total.multiply(BigDecimal.valueOf(0.05));
            }
        } else {
            descuentoMaximo = total.multiply(DESCUENTO_MAXIMO_PORCENTAJE);
        }

        if (currentDiscount.compareTo(descuentoMaximo) > 0) {
            currentDiscount = descuentoMaximo;
        }

        return currentDiscount;
    }


}

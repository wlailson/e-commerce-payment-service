package io.wlailson.github.e_commerce_payment_service.dto;

import java.math.BigDecimal;

public record PaymentRequestDTO(
        Long orderId,
        BigDecimal amount
) {
}

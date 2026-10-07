package io.wlailson.github.e_commerce_payment_service.dto;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long id,

        Long orderId,

        Long userId,

        BigDecimal amount,

        PaymentStatus status,

        String paymentMethod,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

    public PaymentResponseDTO(Payment entity) {
        this(
                entity.getId(),
                entity.getOrderId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getPaymentMethod(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

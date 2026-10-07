package io.wlailson.github.e_commerce_payment_service.message;

import io.wlailson.github.e_commerce_payment_service.dto.PaymentResponseDTO;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentCreatedMessage(
        Long id,
        Long orderId,
        Long userId,
        BigDecimal price,
        PaymentStatus status,
        LocalDateTime created,
        LocalDateTime updated
) {

    public PaymentCreatedMessage(PaymentResponseDTO dto) {
        this(
                dto.id(),
                dto.orderId(),
                dto.userId(),
                dto.amount(),
                dto.status(),
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}

package io.wlailson.github.e_commerce_payment_service.dto;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "PaymentResponse", description = "Representação de um pagamento.")
public record PaymentResponseDTO(
        @Schema(description = "Identificador do pagamento.", example = "501")
        Long id,

        @Schema(description = "Identificador do pedido associado.", example = "1001")
        Long orderId,

        @Schema(description = "Identificador do usuário proprietário do pagamento.", example = "42")
        Long userId,

        @Schema(description = "Valor do pagamento.", example = "49.90")
        BigDecimal amount,

        @Schema(description = "Estado atual do pagamento. Valores: PENDING, APPROVED, REFUSED, CANCELLED ou REFUNDED.",
                example = "APPROVED")
        PaymentStatus status,

        @Schema(description = "Método de pagamento utilizado, quando disponível.", example = "CARD")
        String paymentMethod,

        @Schema(description = "Data e hora de criação do pagamento.", example = "2026-10-07T14:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Data e hora da última atualização do pagamento.", example = "2026-10-07T14:30:00")
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

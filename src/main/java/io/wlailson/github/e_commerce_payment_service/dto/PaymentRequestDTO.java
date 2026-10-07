package io.wlailson.github.e_commerce_payment_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "PaymentRequest", description = "Dados necessários para solicitar a criação de um pagamento.")
public record PaymentRequestDTO(
        @Schema(description = "Identificador do pedido associado ao pagamento.", example = "1001",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Long orderId,

        @Schema(description = "Valor do pagamento.", example = "49.90",
                requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal amount
) {
}

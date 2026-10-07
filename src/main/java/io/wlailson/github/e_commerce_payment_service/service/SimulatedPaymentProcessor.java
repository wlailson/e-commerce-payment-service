package io.wlailson.github.e_commerce_payment_service.service;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SimulatedPaymentProcessor implements PaymentProcessor {

    private final PaymentSimulatorProperties properties;

    @Override
    public PaymentStatus process(Payment payment) {
        BigDecimal amount = Objects.requireNonNull(payment.getAmount(), "Payment amount is required");
        BigDecimal declineAboveAmount = properties.declineAboveAmount();

        if (declineAboveAmount != null && amount.compareTo(declineAboveAmount) > 0) {
            return PaymentStatus.REFUSED;
        }

        return PaymentStatus.APPROVED;
    }
}

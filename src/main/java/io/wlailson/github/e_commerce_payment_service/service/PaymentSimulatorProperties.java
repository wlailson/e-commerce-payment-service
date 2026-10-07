package io.wlailson.github.e_commerce_payment_service.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "payment.simulator")
public record PaymentSimulatorProperties(BigDecimal declineAboveAmount) {
}

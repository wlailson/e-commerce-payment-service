package io.wlailson.github.e_commerce_payment_service.service;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;

public interface PaymentProcessor {

    PaymentStatus process(Payment payment);
}

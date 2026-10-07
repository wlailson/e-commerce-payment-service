package io.wlailson.github.e_commerce_payment_service.service;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatedPaymentProcessorTest {

    @Nested
    class ApprovePayments {

        @Test
        void approvesPaymentWhenDeclineLimitIsNotConfigured() {
            SimulatedPaymentProcessor processor =
                    new SimulatedPaymentProcessor(new PaymentSimulatorProperties(null));

            assertEquals(PaymentStatus.APPROVED, processor.process(payment("1000000.00")));
        }

        @Test
        void approvesPaymentAtConfiguredLimit() {
            SimulatedPaymentProcessor processor =
                    new SimulatedPaymentProcessor(new PaymentSimulatorProperties(new BigDecimal("100.00")));

            assertEquals(PaymentStatus.APPROVED, processor.process(payment("100.00")));
        }
    }

    @Nested
    class RefusePayments {

        @Test
        void refusesPaymentAboveConfiguredLimit() {
            SimulatedPaymentProcessor processor =
                    new SimulatedPaymentProcessor(new PaymentSimulatorProperties(new BigDecimal("100.00")));

            assertEquals(PaymentStatus.REFUSED, processor.process(payment("100.01")));
        }
    }

    private Payment payment(String amount) {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(amount));
        return payment;
    }
}

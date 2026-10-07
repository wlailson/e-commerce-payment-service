package io.wlailson.github.e_commerce_payment_service.repository;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16.0");

    @Autowired
    private PaymentRepository paymentRepository;

    @Nested
    class Persistence {

        @Test
        void savesPaymentAndGeneratesId() {
            Payment saved = paymentRepository.saveAndFlush(payment(20L, 10L));

            assertThat(saved.getId()).isNotNull();
            assertThat(paymentRepository.findById(saved.getId()))
                    .get()
                    .extracting(Payment::getOrderId, Payment::getUserId, Payment::getAmount, Payment::getStatus)
                    .containsExactly(10L, 20L, new BigDecimal("19.99"), PaymentStatus.APPROVED);
        }
    }

    @Nested
    class UserScopedQueries {

        @Test
        void findsPaymentOnlyWhenItBelongsToRequestedUser() {
            Payment payment = paymentRepository.saveAndFlush(payment(20L, 10L));

            assertThat(paymentRepository.findByUserIdAndId(20L, payment.getId())).isPresent();
            assertThat(paymentRepository.findByUserIdAndId(21L, payment.getId())).isEmpty();
        }

        @Test
        void returnsOnlyPaymentsOwnedByRequestedUser() {
            paymentRepository.save(payment(20L, 10L));
            paymentRepository.save(payment(21L, 11L));
            paymentRepository.flush();

            Page<Payment> results = paymentRepository.findAllByUserId(20L, PageRequest.of(0, 10));

            assertThat(results.getContent()).hasSize(1);
            assertThat(results.getContent().getFirst().getUserId()).isEqualTo(20L);
            assertThat(results.getContent().getFirst().getOrderId()).isEqualTo(10L);
            assertThat(results.getTotalElements()).isEqualTo(1);
        }

        @Test
        void appliesPageableToUserPayments() {
            paymentRepository.save(payment(20L, 10L));
            paymentRepository.save(payment(20L, 11L));
            paymentRepository.save(payment(20L, 12L));
            paymentRepository.flush();

            Page<Payment> firstPage =
                    paymentRepository.findAllByUserId(20L, PageRequest.of(0, 2));
            Page<Payment> secondPage =
                    paymentRepository.findAllByUserId(20L, PageRequest.of(1, 2));

            assertThat(firstPage.getContent()).hasSize(2);
            assertThat(firstPage.getTotalElements()).isEqualTo(3);
            assertThat(firstPage.getTotalPages()).isEqualTo(2);
            assertThat(secondPage.getContent()).hasSize(1);
            assertThat(secondPage.getNumber()).isEqualTo(1);
        }
    }

    private Payment payment(Long userId, Long orderId) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(new BigDecimal("19.99"));
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }
}

package io.wlailson.github.e_commerce_payment_service.service;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentRequestDTO;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentResponseDTO;
import io.wlailson.github.e_commerce_payment_service.message.KafkaTopics;
import io.wlailson.github.e_commerce_payment_service.message.PaymentCreatedMessage;
import io.wlailson.github.e_commerce_payment_service.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final KafkaTopics kafkaTopics = mock(KafkaTopics.class);
    private final KafkaProducerService kafkaProducerService = mock(KafkaProducerService.class);
    private final PaymentProcessor paymentProcessor = mock(PaymentProcessor.class);
    private final PaymentService service =
            new PaymentService(repository, kafkaTopics, kafkaProducerService, paymentProcessor);

    @Nested
    class CreatePayment {

        @Test
        void persistsApprovedPaymentAndPublishesCreatedEvent() {
            when(kafkaTopics.created()).thenReturn("payment-created-event");
            when(paymentProcessor.process(any(Payment.class))).thenAnswer(invocation -> {
                Payment payment = invocation.getArgument(0);
                assertEquals(PaymentStatus.PENDING, payment.getStatus());
                assertEquals(20L, payment.getUserId());
                assertEquals(10L, payment.getOrderId());
                return PaymentStatus.APPROVED;
            });
            when(repository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment payment = invocation.getArgument(0);
                payment.setId(1L);
                return payment;
            });

            PaymentResponseDTO response = service.insertPayment(
                    20L, new PaymentRequestDTO(10L, new BigDecimal("19.99")));

            assertEquals(1L, response.id());
            assertEquals(10L, response.orderId());
            assertEquals(20L, response.userId());
            assertEquals(new BigDecimal("19.99"), response.amount());
            assertEquals(PaymentStatus.APPROVED, response.status());
            verify(paymentProcessor).process(any(Payment.class));
            verify(kafkaProducerService).send(
                    eq("payment-created-event"),
                    argThat(message -> message instanceof PaymentCreatedMessage created
                            && created.status() == PaymentStatus.APPROVED));
        }

        @Test
        void persistsRefusedPaymentAndPublishesItsFinalStatus() {
            when(kafkaTopics.created()).thenReturn("payment-created-event");
            when(paymentProcessor.process(any(Payment.class))).thenReturn(PaymentStatus.REFUSED);
            when(repository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PaymentResponseDTO response = service.insertPayment(
                    20L, new PaymentRequestDTO(10L, new BigDecimal("100.01")));

            assertEquals(PaymentStatus.REFUSED, response.status());
            verify(kafkaProducerService).send(
                    eq("payment-created-event"),
                    argThat(message -> message instanceof PaymentCreatedMessage created
                            && created.status() == PaymentStatus.REFUSED));
        }
    }

    @Nested
    class FindPayment {

        @Test
        void returnsPaymentOwnedByUser() {
            Payment payment = payment(1L, 10L, 20L);
            when(repository.findByUserIdAndId(20L, 1L)).thenReturn(Optional.of(payment));

            PaymentResponseDTO response = service.findPaymentById(20L, 1L);

            assertEquals(1L, response.id());
            assertEquals(10L, response.orderId());
            assertEquals(20L, response.userId());
            verify(repository).findByUserIdAndId(20L, 1L);
        }

        @Test
        void throwsWhenPaymentDoesNotBelongToUserOrDoesNotExist() {
            when(repository.findByUserIdAndId(20L, 99L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> service.findPaymentById(20L, 99L));
            verify(repository).findByUserIdAndId(20L, 99L);
        }
    }

    @Nested
    class FindAllPayments {

        @Test
        void mapsUserPaymentsToResponsePage() {
            Payment payment = payment(1L, 10L, 20L);
            PageRequest pageable = PageRequest.of(0, 10);
            when(repository.findAllByUserId(20L, pageable))
                    .thenReturn(new PageImpl<>(List.of(payment), pageable, 1));

            Page<PaymentResponseDTO> responsePage = service.findAllPayments(20L, pageable);

            assertEquals(1, responsePage.getTotalElements());
            assertEquals(0, responsePage.getNumber());
            assertEquals(10, responsePage.getSize());
            assertEquals(1L, responsePage.getContent().getFirst().id());
            assertEquals(20L, responsePage.getContent().getFirst().userId());
            verify(repository).findAllByUserId(20L, pageable);
            verify(kafkaProducerService, never()).send(any(), any());
        }
    }

    private Payment payment(Long id, Long orderId, Long userId) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(new BigDecimal("19.99"));
        payment.setStatus(PaymentStatus.APPROVED);
        return payment;
    }
}

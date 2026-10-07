package io.wlailson.github.e_commerce_payment_service.service;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentRequestDTO;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentResponseDTO;
import io.wlailson.github.e_commerce_payment_service.message.KafkaTopics;
import io.wlailson.github.e_commerce_payment_service.message.PaymentCreatedMessage;
import io.wlailson.github.e_commerce_payment_service.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final KafkaTopics kafkaTopics;
    private final KafkaProducerService kafkaProducerService;
    private final PaymentProcessor paymentProcessor;

    @Transactional
    public PaymentResponseDTO insertPayment(Long userId, PaymentRequestDTO request) {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setUserId(userId);
        payment.setAmount(request.amount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        payment.setStatus(paymentProcessor.process(payment));

        PaymentResponseDTO response = new PaymentResponseDTO(repository.save(payment));

        kafkaProducerService.send(
                kafkaTopics.created(),
                new PaymentCreatedMessage(response)
        );

        return response;
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO findPaymentById(Long userId, Long paymentId) {
        Payment payment = repository.findByUserIdAndId(userId, paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + paymentId));
        return new PaymentResponseDTO(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> findAllPayments(Long userId, Pageable pageable) {
        return repository.findAllByUserId(userId, pageable)
                .map(PaymentResponseDTO::new);
    }
}

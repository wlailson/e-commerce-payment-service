package io.wlailson.github.e_commerce_payment_service.repository;

import io.wlailson.github.e_commerce_payment_service.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAllByUserId(Long userId, Pageable pageable);

    Optional<Payment> findByUserIdAndId(Long userId, Long paymentId);
}

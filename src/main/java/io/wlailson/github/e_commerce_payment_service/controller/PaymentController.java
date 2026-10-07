package io.wlailson.github.e_commerce_payment_service.controller;

import io.wlailson.github.e_commerce_payment_service.dto.PaymentRequestDTO;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentResponseDTO;
import io.wlailson.github.e_commerce_payment_service.service.AuthenticatedUser;
import io.wlailson.github.e_commerce_payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final AuthenticatedUser authenticatedUser;
    private final PaymentService service;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> insertPayment(@RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = service.insertPayment(authenticatedUser.getUserId(), request);
        URI location = URI.create("/payments/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> findPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(service.findPaymentById(authenticatedUser.getUserId(), paymentId));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentResponseDTO>> findAllPayments(Pageable pageable) {
        return ResponseEntity.ok(service.findAllPayments(authenticatedUser.getUserId(), pageable));
    }

}

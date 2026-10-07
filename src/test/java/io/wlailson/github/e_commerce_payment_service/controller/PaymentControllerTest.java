package io.wlailson.github.e_commerce_payment_service.controller;

import io.wlailson.github.e_commerce_payment_service.domain.PaymentStatus;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentRequestDTO;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentResponseDTO;
import io.wlailson.github.e_commerce_payment_service.service.AuthenticatedUser;
import io.wlailson.github.e_commerce_payment_service.service.PaymentService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentControllerTest {

    private final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
    private final PaymentService service = mock(PaymentService.class);
    private final PaymentController controller = new PaymentController(authenticatedUser, service);

    @Nested
    class CreatePayment {

        @Test
        void returnsCreatedLocationAndUsesAuthenticatedUserId() {
            PaymentRequestDTO request = new PaymentRequestDTO(10L, new BigDecimal("19.99"));
            PaymentResponseDTO response = response(1L);
            when(authenticatedUser.getUserId()).thenReturn(20L);
            when(service.insertPayment(20L, request)).thenReturn(response);

            ResponseEntity<PaymentResponseDTO> result = controller.insertPayment(request);

            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertSame(response, result.getBody());
            assertEquals("/payments/1", result.getHeaders().getLocation().getPath());
            verify(service).insertPayment(20L, request);
        }
    }

    @Nested
    class FindPayment {

        @Test
        void returnsPaymentForAuthenticatedUser() {
            PaymentResponseDTO response = response(1L);
            when(authenticatedUser.getUserId()).thenReturn(20L);
            when(service.findPaymentById(20L, 1L)).thenReturn(response);

            ResponseEntity<PaymentResponseDTO> result = controller.findPaymentById(1L);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertSame(response, result.getBody());
            verify(service).findPaymentById(20L, 1L);
        }
    }

    @Nested
    class FindAllPayments {

        @Test
        void returnsPageForAuthenticatedUser() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<PaymentResponseDTO> response = Page.empty(pageable);
            when(authenticatedUser.getUserId()).thenReturn(20L);
            when(service.findAllPayments(20L, pageable)).thenReturn(response);

            ResponseEntity<Page<PaymentResponseDTO>> result = controller.findAllPayments(pageable);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertSame(response, result.getBody());
            verify(service).findAllPayments(20L, pageable);
        }
    }

    private PaymentResponseDTO response(Long id) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentResponseDTO(id, 10L, 20L, new BigDecimal("19.99"),
                PaymentStatus.PENDING, "CARD", now, now);
    }
}

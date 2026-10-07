package io.wlailson.github.e_commerce_payment_service.controller;

import io.wlailson.github.e_commerce_payment_service.dto.PaymentRequestDTO;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentResponseDTO;
import io.wlailson.github.e_commerce_payment_service.service.KafkaProducerService;
import io.wlailson.github.e_commerce_payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class PaymentControllerIT extends AbstractIntegrationTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        when(jwtDecoder.decode("integration-token")).thenReturn(
                Jwt.withTokenValue("integration-token")
                        .header("alg", "none")
                        .claim("userId", 20L)
                        .build());
    }

    @Nested
    class CreateAndReadPayment {

        @Test
        void createsPaymentAndReadsItFromPostgres() {
            PaymentRequestDTO request = new PaymentRequestDTO(10L, new BigDecimal("19.99"));

            PaymentResponseDTO created = client.post()
                    .uri("/payments")
                    .headers(headers -> headers.setBearerAuth("integration-token"))
                    .body(request)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().valueMatches(HttpHeaders.LOCATION, "/payments/\\d+")
                    .expectBody(PaymentResponseDTO.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(created).isNotNull();
            assertThat(created.id()).isNotNull();
            assertThat(paymentRepository.findById(created.id()))
                    .get()
                    .satisfies(payment -> {
                        assertThat(payment.getOrderId()).isEqualTo(10L);
                        assertThat(payment.getUserId()).isEqualTo(20L);
                        assertThat(payment.getAmount()).isEqualByComparingTo("19.99");
                    });

            client.get()
                    .uri("/payments/{paymentId}", created.id())
                    .headers(headers -> headers.setBearerAuth("integration-token"))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PaymentResponseDTO.class)
                    .value(response -> {
                        assertThat(response.id()).isEqualTo(created.id());
                        assertThat(response.userId()).isEqualTo(20L);
                        assertThat(response.orderId()).isEqualTo(10L);
                    });
        }
    }
}

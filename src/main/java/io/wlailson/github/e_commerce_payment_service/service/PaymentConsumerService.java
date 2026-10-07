package io.wlailson.github.e_commerce_payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumerService {

    private final PaymentService paymentService;


}

package io.wlailson.github.e_commerce_payment_service.config;

import io.wlailson.github.e_commerce_payment_service.service.PaymentSimulatorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PaymentSimulatorProperties.class)
public class PaymentSimulatorConfiguration {
}

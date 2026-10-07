package io.wlailson.github.e_commerce_payment_service.message;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topics")
public record KafkaTopics(
        String created,
        String updated
) {
}

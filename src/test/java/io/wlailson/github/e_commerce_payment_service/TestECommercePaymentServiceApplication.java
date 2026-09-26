package io.wlailson.github.e_commerce_payment_service;

import org.springframework.boot.SpringApplication;

public class TestECommercePaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ECommercePaymentServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

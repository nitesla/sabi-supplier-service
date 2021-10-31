package com.sabi.supplier.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.sabi.framework")
@EntityScan(basePackages = {"com.sabi.suppliers.core.models"})
@SpringBootApplication
public class SabiSupplierServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SabiSupplierServiceApplication.class, args);
	}

}

package com.sabisupplier.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.sabi")
@EntityScan(basePackages = {"com.sabisupplierscore.models"})
@SpringBootApplication
public class SabiSupplierServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SabiSupplierServiceApplication.class, args);
	}

}

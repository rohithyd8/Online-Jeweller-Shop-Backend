package com.jewelleryshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title="Online-Jewellery-Shop",
		description="SLK Assignment",
		version="v1"
		))
public class OnlineJewelleryShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineJewelleryShopApplication.class, args);
	}

}

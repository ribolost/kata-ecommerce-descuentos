package com.ribolost.pruebastecnicas.kataecommerce;

import com.ribolost.pruebastecnicas.kataecommerce.shared.seed.DiscountPolicySeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KataecommerceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(KataecommerceApplication.class, args);
		context.getBean(DiscountPolicySeeder.class).seed();
	}

}

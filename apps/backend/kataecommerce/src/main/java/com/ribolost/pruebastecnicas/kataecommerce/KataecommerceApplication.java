package com.ribolost.pruebastecnicas.kataecommerce;

import com.ribolost.pruebastecnicas.kataecommerce.shared.seed.DiscountPolicySeeder;
import com.ribolost.pruebastecnicas.kataecommerce.shared.seed.ProductSeeder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class KataecommerceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(KataecommerceApplication.class, args);
		context.getBean(ProductSeeder.class).seed();
		context.getBean(DiscountPolicySeeder.class).seed();
	}

}

package es.juventudcomunista.redroja.cjcgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CjcGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(CjcGatewayApplication.class, args);
	}

}

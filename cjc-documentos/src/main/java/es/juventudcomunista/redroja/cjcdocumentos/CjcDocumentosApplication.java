package es.juventudcomunista.redroja.cjcdocumentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = "es.juventudcomunista.redroja.cjccommonutils.service")
@ComponentScan(basePackages = {"es.juventudcomunista.redroja.cjcdocumentos","es.juventudcomunista.redroja.cjccommonutils"})
public class CjcDocumentosApplication {

	public static void main(String[] args) {
		SpringApplication.run(CjcDocumentosApplication.class, args);
	}

}

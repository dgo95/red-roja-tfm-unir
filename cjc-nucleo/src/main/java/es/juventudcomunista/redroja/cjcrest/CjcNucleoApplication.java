package es.juventudcomunista.redroja.cjcrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "es.juventudcomunista.redroja.cjccommonutils.service")
@ComponentScan(basePackages = {"es.juventudcomunista.redroja"})
@EnableScheduling
public class CjcNucleoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CjcNucleoApplication.class, args);
    }

}

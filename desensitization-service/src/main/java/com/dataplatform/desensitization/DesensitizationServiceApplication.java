package com.dataplatform.desensitization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DesensitizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesensitizationServiceApplication.class, args);
    }
}

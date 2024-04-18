package com.example.cmscreditaccountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CmsCreditAccountServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmsCreditAccountServiceApplication.class, args);
	}

}

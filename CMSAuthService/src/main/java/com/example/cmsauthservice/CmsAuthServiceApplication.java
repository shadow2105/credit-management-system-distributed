package com.example.cmsauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CmsAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmsAuthServiceApplication.class, args);
	}

}

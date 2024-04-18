package com.example.cmseurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CmsEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsEurekaServerApplication.class, args);
    }

}

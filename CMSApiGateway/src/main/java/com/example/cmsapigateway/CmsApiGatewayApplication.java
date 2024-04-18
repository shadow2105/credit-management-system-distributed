package com.example.cmsapigateway;

import com.example.cmsapigateway.configuration.LoadBalancerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;

/*
 * https://spring.io/guides/gs/service-registration-and-discovery
 * "A client that both registers itself with the registry and uses the Spring Cloud DiscoveryClient abstraction
 * to interrogate the registry for its own host and port.
 * The @EnableDiscoveryClient activates the Netflix Eureka DiscoveryClient implementation.
 * (There are other implementations for other service registries, such as Hashicorp’s Consul or Apache Zookeeper)"
 */
@SpringBootApplication
@EnableDiscoveryClient
/* Scenario 1 (used here): All microservices use the same load balancing strategy -
 * Global Load Balancer (Ribbon) configuration defined in API Gateway application
 * using @RibbonClients
 */

/* Scenario 2: Different microservices use the different load balancing strategies -
 * Individual Load Balancer (Ribbon) configuration defined in each microservice application
 * using @RibbonClient
 */

// Netflix Ribbon provides client-side load balancing as one of its features, and it can be used in
// conjunction with service discovery for dynamic service location and load balancing;
// otherwise explicitly specify a list of servers to be used for load balancing
@RibbonClients({
    @RibbonClient(name = "cms-auth-service", configuration = LoadBalancerConfiguration.class),
    @RibbonClient(name = "cms-credit-account-service", configuration = LoadBalancerConfiguration.class),
    @RibbonClient(name = "cms-credit-statement-service", configuration = LoadBalancerConfiguration.class),
    @RibbonClient(name = "cms-transaction-service", configuration = LoadBalancerConfiguration.class),
    @RibbonClient(name = "cms-user-details-service", configuration = LoadBalancerConfiguration.class)
})
//@RibbonClients(defaultConfiguration = LoadBalancerConfiguration.class)
public class CmsApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmsApiGatewayApplication.class, args);
    }

}

package com.example.cmsapigateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class RouteConfiguration {

    /*
     * ROLE_CUSTOMER Services ----------------------------
     * Credit Account Service
     *      - GET - /api/v1/customer/{customer-id}/credit-accounts ( view all accounts )
     *      - GET - /api/v1/customer/{customer-id}/credit-accounts/{credit-account-id} ( view specific/selected account )
     *      - GET - /api/v1/customer/{customer-id}/credit-accounts/new ( view form to open/request new account )
     *      - POST - /api/v1/customer/{customer-id}/credit-accounts ( submit form to open/request new account )
     *      - PUT/PATCH - ( cannot update account; only transactions update accounts - see CDC)
     *      - DELETE - ( cannot delete account; only request for deletion )
     *
     * Credit Statement Service
     *      - GET - /api/v1/customer/{customer-id}/credit-statements
     *          ( view all credit statements for the customer; or filter by ?account-number= )
     *
     *      - GET - /api/v1/customer/{customer-id}/credit-statements/credit-accounts/{credit-account-id}
     *          ( view all credit statements for a credit account belonging to the customer )
     *
     *      - GET - /api/v1/customer/{customer-id}/credit-statements/generate ( submit form to generate credit statement )
     *
     * Transaction Service
     *      - GET - /api/v1/customer/{customer-id}/transactions
     *          ( view all transactions for the customer; or filter by ?account-number= )
     *
     *      - GET - /api/v1/customer/{customer-id}/transactions/credit-accounts/{credit-account-id}
     *          ( view all transactions for a credit account belonging to the customer )
     *
     *      - GET - /api/v1/customer/{customer-id}/transactions/make ( view form to enter transaction details )
     *      - POST - /api/v1/customer/{customer-id}/transactions ( submit form to make a transaction )
     *
     *      * also see Stripe API to simulate credit payments
     *
     * ROLE_ADMIN Services ----------------------------
     * User Details Service
     *      - GET - /api/v1/admin/users ( view all users (accounts) signed up for the application;  with authority APP_CMS)
     *      - GET - /api/v1/admin/users/{user-id}/edit ( view form to update user account - like add authority to use new app )
     *      - GET - /api/v1/admin/users/{user-id}/profile ( view user profile )
     *      - GET - /api/v1/admin/users/{user-id}/profile/edit ( view form to update user profile - like name upon request)
     *      - PUT/PATCH - /api/v1/admin/users/{user-id} ( submit form to update user account )
     *      - PUT/PATCH - /api/v1/admin/users/{user-id}/profile ( submit form to update user profile )
     *      - DELETE - /api/v1/admin/users/{user-id} ( delete user account and profile upon request)
     *
     * Customer Credit Accounts Service (interface to approve or decline credit account closing/deletion requests)
     */
    @Bean
    public RouterFunction<ServerResponse> customRouteLocator() {
        return route("resource")
                .add(route(path("/auth/**"), http())
                        .filter(lb("cms-auth-service")))
                .add(route(path("/api/v1/customer/*/credit-accounts/**"), http())
                        .filter(lb("cms-credit-account-service")))
                .add(route(path("/api/v1/customer/*/credit-statements/**"), http())
                        .filter(lb("cms-credit-statement-service")))
                .add(route(path("/api/v1/customer/*/transactions/**"), http())
                        .filter(lb("cms-transaction-service")))
                .add(route(path("/api/v1/admin/users/**"), http())
                        .filter(lb("cms-user-details-service"))
                )
                .filter(tokenRelay())
                .build();
    }
}

/*
* The below configuration doesn't work -
* returns 404 when matching GET - http://127.0.0.1:8080/api/v1/customer/shadow.ar2199/credit-accounts
* returns 200 when matching GET - http://127.0.0.1:8080/api/v1/customer/shadow.ar2199/transactions/credit-accounts
*
*
* */

//@Bean
//public RouterFunction<ServerResponse> customRouteLocator() {
//        return route("resource")
//                .route(path("/auth/**"), http())
//                .filter(lb("cms-auth-service"))
//                .route(path("/api/v1/customer/*/credit-accounts/**"), http())
//                .filter(lb("cms-credit-account-service"))
//                .route(path("/api/v1/customer/*/credit-statements/**"), http())
//                .filter(lb("cms-credit-statement-service"))
//                .route(path("/api/v1/customer/*/transactions/**"), http())
//                .filter(lb("cms-transaction-service"))
//                .route(path("/api/v1/admin/users/**"), http())
//                .filter(lb("cms-user-details-service"))
//                .filter(tokenRelay())
//                .build();
//}
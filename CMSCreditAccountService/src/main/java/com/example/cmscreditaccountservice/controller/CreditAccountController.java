package com.example.cmscreditaccountservice.controller;

import com.example.cmscreditaccountservice.controller.exception.BadRequestException;
import com.example.cmscreditaccountservice.controller.exception.ResourceCreationFailedException;
import com.example.cmscreditaccountservice.controller.exception.ResourceNotFoundException;
import com.example.cmscreditaccountservice.controller.exception.UnexpectedException;
import com.example.cmscreditaccountservice.domain.CreditAccount;
import com.example.cmscreditaccountservice.dto.AllCreditAccountsResponseDto;
import com.example.cmscreditaccountservice.dto.CreditAccountOpeningRequestDto;
import com.example.cmscreditaccountservice.dto.SelectedCreditAccountResponseDto;
import com.example.cmscreditaccountservice.service.CreditAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/customer")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    public CreditAccountController(CreditAccountService creditAccountService) {
        this.creditAccountService = creditAccountService;
    }

    // - GET - /api/v1/customer/{customer-id}/credit-accounts ( view all accounts )
    @GetMapping(value="/{customer-id}/credit-accounts", produces = { "application/json" })
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public AllCreditAccountsResponseDto getAllCreditAccountsForCustomer(@PathVariable("customer-id") String customerId) {

        AllCreditAccountsResponseDto allCreditAccountsResponseDto;
        try {
            allCreditAccountsResponseDto = creditAccountService
                    .getCreditAccountsByCustomerId(customerId);
        }
        catch (Exception e) {
            throw new UnexpectedException("Unable to fetch Credit Accounts. Please try again later.");
        }


        /*
        "_links": {
            "self": { "href": "/api/v1/customer/{customer-id}/credit-accounts/{credit-account-id}" }
        }
         */
        allCreditAccountsResponseDto.getCreditAccounts()
                .forEach(creditAccount -> {

                    Link selfLink = linkTo(methodOn(CreditAccountController.class)
                            .getSelectedCreditAccountForCustomer(customerId, creditAccount.getAccountId()))
                            .withSelfRel();

                    creditAccount.add(selfLink);
        });

        /*
        "_links": {
            "self": { "href": "/api/v1/customer/{customer-id}/credit-accounts" }
        }
         */
        Link selfLink = linkTo(methodOn(CreditAccountController.class)
                .getAllCreditAccountsForCustomer(customerId))
                .withSelfRel();

        allCreditAccountsResponseDto.add(selfLink);

        return allCreditAccountsResponseDto;
    }

    // - GET - /api/v1/customer/{customer-id}/credit-accounts/{credit-account-id} ( view specific/selected account )
    @GetMapping(value="/{customer-id}/credit-accounts/{credit-account-id}",
            produces = { "application/json" })
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public SelectedCreditAccountResponseDto getSelectedCreditAccountForCustomer(@PathVariable("customer-id") String customerId,
                                                                             @PathVariable("credit-account-id") String creditAccountId) {

        SelectedCreditAccountResponseDto selectedCreditAccountResponseDto;
        try {
            selectedCreditAccountResponseDto = creditAccountService
                    .getCreditAccountByCustomerIdAndCreditAccountId(customerId, creditAccountId);;
        }
        catch (Exception e) {
            throw new UnexpectedException("Unable to fetch Credit Account. Please try again later.");
        }

        if (selectedCreditAccountResponseDto == null) {
            throw new ResourceNotFoundException(
                    "Credit account not found or does not exist for the customer '" + customerId + "'.");
        }

        /*
        "_links": {
            "self": { "href": "/api/v1/customer/{customer-id}/credit-accounts/{credit-account-id}" },
            "transactions": { "href": "/api/v1/customer/{customer-id}/transactions/credit-accounts/{credit-account-id}" },
            "accountStatements": { "href": "/api/v1/customer/{customer-id}/credit-statements/credit-accounts/{credit-account-id}" },
            "allCreditAccounts": { "href": "/api/v1/customer/{customer-id}/credit-accounts" },
        }
         */
        Link selfLink = linkTo(methodOn(CreditAccountController.class)
                .getSelectedCreditAccountForCustomer(customerId, creditAccountId))
                .withRel("self");

        selectedCreditAccountResponseDto.add(selfLink);

        // This link/endpoint requires an API call to 'cms-transaction-service' to get the transactions of the specified
        // credit account for the customer (tight coupling to another service) or,
        // redirect to api gateway

        // "transactions": { "href": "/api/v1/customer/{customer-id}/transactions?account-number=value" },
        /*UriComponents transactionsLink = linkTo(CreditAccountController.class).slash(customerId)
                .toUriComponentsBuilder()
                .path("/transactions")
                .queryParam("account-number", selectedCreditAccountResponseDto.getAccountNumber())
                .build();*/

        URI transactionsLink = linkTo(CreditAccountController.class).slash(customerId)
                .toUriComponentsBuilder()
                .pathSegment("transactions", "credit-accounts", "{credit-account-id}")
                .build(creditAccountId);

        selectedCreditAccountResponseDto.add(Link.of(String.valueOf(transactionsLink), "transactions"));

        // This link/endpoint requires an API call to 'cms-credit-statement-service' to get the credit statements of the
        // specified credit account for the customer (tight coupling to another service) or,
        // redirect to api gateway

        // "creditStatements": { "href": "/api/v1/customer/{customer-id}/credit-statements?account-number=value" }
        /*UriComponents creditStatementsLink = linkTo(CreditAccountController.class).slash(customerId)
                .toUriComponentsBuilder()
                .path("/credit-statements")
                .queryParam("account-number", selectedCreditAccountResponseDto.getAccountNumber())
                .build();*/

        URI creditStatementsLink = linkTo(CreditAccountController.class).slash(customerId)
                .toUriComponentsBuilder()
                .pathSegment("credit-statements", "credit-accounts", "{credit-account-id}")
                .build(creditAccountId);

        selectedCreditAccountResponseDto.add(Link.of(String.valueOf(creditStatementsLink),
                "creditStatements"));

        Link allCreditAccountsLink = linkTo(methodOn(CreditAccountController.class)
                .getAllCreditAccountsForCustomer(customerId))
                .withRel("allCreditAccounts");

        selectedCreditAccountResponseDto.add(allCreditAccountsLink);

        return selectedCreditAccountResponseDto;
    }

    /* - GET - /api/v1/customer/{id}/credit-accounts/new ( view form to open/request new account )
     * Not required here; generated on the Client Side
     */

    // - POST - /api/v1/customer/{customer-id}/credit-accounts ( submit form to open/request new account )
    @PostMapping(value="/{customer-id}/credit-accounts", produces = { "application/json" })
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public ResponseEntity<Map<String, Object>> openCreditAccountForCustomer(
            @PathVariable("customer-id") String customerId,
            @Valid @RequestBody CreditAccountOpeningRequestDto creditAccountOpeningRequestDto,
            BindingResult bindingResult,
            HttpServletRequest request) throws BadRequestException {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid Input! Unable to process the request to open a Credit Account.");
        }

        CreditAccount addedCreditAccount;
        try {
            addedCreditAccount = creditAccountService
                    .addCreditAccountByCustomerId(customerId, creditAccountOpeningRequestDto);
        }
        catch (Exception e) {
            throw new ResourceCreationFailedException("Unable to open credit account. Please try again later.");
        }

        // If Gross Annual Household Income is less than threshold
        if (addedCreditAccount == null) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Credit Account opening failed!");

            return ResponseEntity.ok(responseBody);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Credit Account opened successfully!");

        String uri = request.getRequestURI() + "/" + addedCreditAccount.getId();
        Map<String, Object> links = new HashMap<>();
        links.put("allCreditAccounts", linkTo(methodOn(CreditAccountController.class)
                .getAllCreditAccountsForCustomer(customerId)).withRel("allCreditAccounts").getHref());

        //links.put("allCreditAccounts", Collections.singletonMap("href", "/api/v1/customers/{customer-id}/credit-accounts"));
        responseBody.put("_links", links);

        return ResponseEntity.created(URI.create(uri)).body(responseBody);
    }
}

/* POST - /api/v1/customer/{customer-id}/credit-accounts ( submit form to open/request new account )
{
    "message": "Credit Account created successfully",
    "_links": {
            "allCreditAccounts": { "href": "/api/v1/customer/{customer-id}/credit-accounts" }
        }
    }
}
 */



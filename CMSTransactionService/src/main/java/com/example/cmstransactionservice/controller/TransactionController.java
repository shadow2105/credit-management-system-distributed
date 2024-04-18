package com.example.cmstransactionservice.controller;

import com.example.cmstransactionservice.controller.exception.ResourceCreationFailedException;
import com.example.cmstransactionservice.controller.exception.UnexpectedException;
import com.example.cmstransactionservice.domain.Transaction;
import com.example.cmstransactionservice.dto.AllTransactionsResponseDto;
import com.example.cmstransactionservice.dto.SelectedCreditAccountTransactionsResponseDto;
import com.example.cmstransactionservice.dto.TransactionRequestDto;
import com.example.cmstransactionservice.service.TransactionAccessValidator;
import com.example.cmstransactionservice.service.TransactionService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/customer")
public class TransactionController {

    private static final String DEFAULT_PAGE_SIZE = "10";
    private final TransactionService transactionService;
    private final TransactionAccessValidator transactionAccessValidator;

    public TransactionController(TransactionService transactionService, TransactionAccessValidator transactionAccessValidator) {
        this.transactionService = transactionService;

        this.transactionAccessValidator = transactionAccessValidator;
    }

    // - GET - /api/v1/customer/{customer-id}/transactions
    // ( view all transactions for the customer; or filter by ?creditAccountId= )
    @GetMapping(value="/{customer-id}/transactions", produces = { "application/json" })
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public AllTransactionsResponseDto getAllTransactionsForCustomer(@PathVariable("customer-id") String customerId,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
                                                                    @RequestParam(required = false) String creditAccountId,
                                                                    @RequestParam(required = false) Character type,
                                                                    @RequestParam(required = false) String month,
                                                                    @RequestParam(required = false) String year) {

        AllTransactionsResponseDto allTransactionsResponseDto;
        try {
            allTransactionsResponseDto = transactionService
                    .getTransactionsByCustomerId(customerId, page, size, creditAccountId, type, month, year);
        }
        catch (Exception e) {
            //System.out.println(e.getMessage());
            throw new UnexpectedException("Unable to fetch Transactions. Please try again later.");
        }

        /*
        "_links": {
            "self": { "href": "/api/v1/customer/{customer-id}/transactions" }
        }
         */
        /*Link selfLink = linkTo(methodOn(TransactionController.class)
                .getAllTransactionsForCustomer(customerId, page, size, null, null, null, null))
                .withSelfRel();
        allTransactionsResponseDto.add(selfLink);*/

        URI selfLink = linkTo(TransactionController.class).slash(customerId)
                .toUriComponentsBuilder()
                .path("/transactions")
                .queryParam("page", page)
                .queryParam("size", size)
                .build().toUri();

        allTransactionsResponseDto.add(Link.of(String.valueOf(selfLink), "self"));

        return allTransactionsResponseDto;
    }

    // - GET - /api/v1/customer/{customer-id}/transactions/credit-accounts/{credit-account-id}
    // ( view all transactions for a credit account belonging to the customer )
    /*
     * This approach uses Joins.
     *
     * Joins can often be optimized by the database engine itself, especially if appropriate indexes are in place.
     * Single query execution can retrieve both the account and transaction information in a single round trip to the database.
     *
     * Complex join queries can sometimes lead to slower performance, especially if the join involves large tables or
     * non-optimized query plans.
     */
    @GetMapping(value="/{customer-id}/transactions/credit-accounts/{credit-account-id}",
            produces = { "application/json" })
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public SelectedCreditAccountTransactionsResponseDto getSelectedCreditAccountTransactionsForCustomer(
            @PathVariable("customer-id") String customerId,
            @PathVariable("credit-account-id") String creditAccountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) Character type,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year) {

        SelectedCreditAccountTransactionsResponseDto selectedCreditAccountTransactionsResponseDto;
        try {
            selectedCreditAccountTransactionsResponseDto = transactionService
                    .getTransactionsByCustomerIdAndCreditAccountId(customerId, creditAccountId, page, size, type,
                            month, year);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new UnexpectedException("Unable to fetch Transactions. Please try again later.");
        }

        /*
        "_links": {
            "self": { "href": "/api/v1/customer/{customer-id}/transactions/credit-accounts/{credit-account-id}" }
            "creditAccount": { "href": "/api/v1/customer/{customer-id}/credit-accounts/{credit-account-id}" }
            "allTransactions": { "href": "/api/v1/customer/{customer-id}/transactions" }
        }
         */
        /*Link selfLink = linkTo(methodOn(TransactionController.class)
                .getSelectedCreditAccountTransactionsForCustomer(customerId, creditAccountId, page, size, null,
                        null, null))
                .withRel("self");*/

        URI selfLink = linkTo(TransactionController.class).slash(customerId)
                .toUriComponentsBuilder()
                .pathSegment("transactions", "credit-accounts", "{credit-account-id}")
                .queryParam("page", page)
                .queryParam("size", size)
                .build(creditAccountId);

        selectedCreditAccountTransactionsResponseDto.add(Link.of(String.valueOf(selfLink), "self"));

        // This link/endpoint requires an API call to 'cms-credit-account-service' to get the specified credit account
        // for the customer (tight coupling to another service) or,
        // requires a creation of endpoint locally to get the specified credit account (with only the data required by
        // this service) or,
        // redirect to api gateway
        URI creditAccountLink = linkTo(TransactionController.class).slash(customerId)
                .toUriComponentsBuilder()
                .pathSegment("credit-accounts", "{credit-account-id}")
                .build(creditAccountId);

        selectedCreditAccountTransactionsResponseDto
                .add(Link.of(String.valueOf(creditAccountLink), "creditAccount"));

        /*Link allTransactionsLink = linkTo(methodOn(TransactionController.class)
                .getAllTransactionsForCustomer(customerId, page, size, null, null, null, null))
                .withRel("allTransactions");*/

        URI allTransactionsLink = linkTo(TransactionController.class).slash(customerId)
                .toUriComponentsBuilder()
                .path("/transactions")
                .queryParam("page", page)
                .queryParam("size", size)
                .build().toUri();

        selectedCreditAccountTransactionsResponseDto.add(Link.of(String.valueOf(allTransactionsLink),
                "allTransactions"));

        return selectedCreditAccountTransactionsResponseDto;
    }

    /*
     * Alternative approach to view all transactions for a credit account belonging to the customer
     * This approach -
     *
     * May result in simpler SQL queries generated by Spring Data JPA compared to complex joins,
     * leading to potentially faster execution times.
     *
     * May require additional database queries to check the credit account entity, resulting in multiple round trips to
     * the database. Performance can degrade if the number of accounts associated with a customer is large, as each
     * account needs to be individually checked.
     *
     * Adding the customer ID directly to the transaction table violates the principles of the Third Normal Form (3NF)
     * of database normalization. In Third Normal Form (3NF), every non-prime attribute (attribute that is not part of
     * any candidate key)of a table must be dependent on the primary key, and there should be no transitive dependencies.
     *
     * A transaction (id) can transitively determine a customer (id) through a credit account (id)? Which implies,
     * customer (id) would transitively depend on transaction (id) via a credit account (id)

    @GetMapping("/api/v1/transactions/credit-accounts/{credit-account-id}")
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and " +
            "@transactionAccessValidator.hasAccessToCreditAccount(#creditAccountId)")
    public SelectedCreditAccountTransactionsResponseDto getSelectedCreditAccountTransactionsForCustomer(
            @PathVariable("credit-account-id") String creditAccountId) {
        // implementation to retrieve transactions by credit account
    }
     */

    /* - GET - /api/v1/customer/{customer-id}/transactions/make ( view form to enter transaction details )
     * Not required here; generated on the Client Side
     */

    // - POST - /api/v1/customer/{customer-id}/transactions ( submit form to make a transaction )
    @PostMapping(value="/{customer-id}/transactions", produces = { "application/json" })
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public ResponseEntity<Map<String, Object>> processTransactionForCustomer(
            @PathVariable("customer-id") String customerId,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto,
            BindingResult bindingResult) throws BadRequestException {

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors()
                    .stream()
                    //.map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .forEach(error -> System.out.println(error.getDefaultMessage()));
                    //.collect(Collectors.toList());
            throw new BadRequestException("Invalid Input! Unable to process the transaction.");
        }

        Transaction processedTransaction;
        try {
            processedTransaction = transactionService
                    .addTransactionByCustomerId(customerId, transactionRequestDto);
        }
        catch (Exception e) {
            //System.out.println(e.getMessage());
            throw new ResourceCreationFailedException("Unable to process the transaction. Please try again later.");
        }

        // If closing balance (credit account balance after adding "credit" transaction amount) is greater
        // than the account's credit limit
        if (processedTransaction == null) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Transaction failed!");

            return ResponseEntity.ok(responseBody);
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Transaction processed successfully!");

        Map<String, Object> links = new HashMap<>();
        /*links.put("allTransactions", linkTo(methodOn(TransactionController.class)
                .getAllTransactionsForCustomer(customerId, 0, Integer.parseInt(DEFAULT_PAGE_SIZE), null,
                        null, null, null))
                .withRel("allTransactions").getHref());*/

        links.put("allTransactions", linkTo(TransactionController.class).slash(customerId)
                .toUriComponentsBuilder()
                .path("/transactions")
                .queryParam("page", 0)
                .queryParam("size", Integer.parseInt(DEFAULT_PAGE_SIZE))
                .build().toUri());

        //links.put("allTransactions", Collections.singletonMap("href", "/api/v1/customers/{customer-id}/transactions"));
        responseBody.put("_links", links);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }
}

/* POST - /api/v1/customer/{customer-id}/transactions ( submit form to make a transaction )
{
    "message": "Transaction processed successfully!",
    "_links": {
            "allTransactions": { "href": "/api/v1/customer/{customer-id}/transactions" }
        }
    }
}
 */



package com.example.cmscreditstatementservice.controller;

import com.example.cmscreditstatementservice.controller.exception.BadRequestException;
import com.example.cmscreditstatementservice.controller.exception.ResourceNotFoundException;
import com.example.cmscreditstatementservice.controller.exception.UnexpectedException;
import com.example.cmscreditstatementservice.domain.CreditStatementDocument;
import com.example.cmscreditstatementservice.dto.AllCreditStatementsResponseDto;
import com.example.cmscreditstatementservice.dto.CreditStatementRequestDto;
import com.example.cmscreditstatementservice.dto.SelectedCreditAccountCreditStatementsResponseDto;
import com.example.cmscreditstatementservice.service.CreditStatementAccessValidator;
import com.example.cmscreditstatementservice.service.CreditStatementService;
import jakarta.validation.Valid;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/v1/customer")
public class CreditStatementController {

    private static final String DEFAULT_PAGE_SIZE = "5";
    private final CreditStatementService creditStatementService;
    private final CreditStatementAccessValidator creditStatementAccessValidator;

    public CreditStatementController(CreditStatementService creditStatementService,
                                     CreditStatementAccessValidator creditStatementAccessValidator) {
        this.creditStatementService = creditStatementService;
        this.creditStatementAccessValidator = creditStatementAccessValidator;
    }

    // - GET - /api/v1/customer/{customer-id}/credit-statements
    // ( view all credit statements for the customer; or filter by ?creditAccountId= )
    @GetMapping(value = "/{customer-id}/credit-statements", produces = {"application/json"})
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public AllCreditStatementsResponseDto getAllCreditStatementsForCustomer(@PathVariable("customer-id") String customerId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
                                                                            @RequestParam(required = false) String creditAccountId,
                                                                            @RequestParam(required = false) String month,
                                                                            @RequestParam(required = false) String year) {

        AllCreditStatementsResponseDto allCreditStatementsResponseDto;
        try {
            allCreditStatementsResponseDto = creditStatementService
                    .getCreditStatementsByCustomerId(customerId, page, size, creditAccountId, month, year);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            throw new UnexpectedException("Unable to fetch Credit Statements. Please try again later.");
        }

        /*
        "_links": {
            "self": { "href": "/api/v1/customer/{customer-id}/credit-statements" }
        }
         */
        /*Link selfLink = linkTo(methodOn(CreditStatementController.class)
                .getAllCreditStatementsForCustomer(customerId, page, size, null, null, null))
                .withSelfRel();
        allCreditStatementsResponseDto.add(selfLink);*/

        URI selfLink = linkTo(CreditStatementController.class).slash(customerId)
                .toUriComponentsBuilder()
                .path("/credit-statements")
                .queryParam("page", page)
                .queryParam("size", size)
                .build().toUri();

        allCreditStatementsResponseDto.add(Link.of(String.valueOf(selfLink), "self"));

        return allCreditStatementsResponseDto;
    }

    // - GET - /api/v1/customer/{customer-id}/credit-statements/credit-accounts/{credit-account-id}
    // ( view all credit statements for a credit account belonging to the customer )
    /*
     * This approach uses Joins.
     *
     * Joins can often be optimized by the database engine itself, especially if appropriate indexes are in place.
     * Single query execution can retrieve both the account and statement information in a single round trip to the database.
     *
     * Complex join queries can sometimes lead to slower performance, especially if the join involves large tables or
     * non-optimized query plans.
     */
    @GetMapping(value = "/{customer-id}/credit-statements/credit-accounts/{credit-account-id}",
            produces = {"application/json"})
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public SelectedCreditAccountCreditStatementsResponseDto getSelectedCreditAccountCreditStatementsForCustomer(
            @PathVariable("customer-id") String customerId,
            @PathVariable("credit-account-id") String creditAccountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year) {

        SelectedCreditAccountCreditStatementsResponseDto selectedCreditAccountCreditStatementsResponseDto;
        try {
            selectedCreditAccountCreditStatementsResponseDto = creditStatementService
                    .getCreditStatementsByCustomerIdAndCreditAccountId(customerId, creditAccountId, page, size, month, year);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new UnexpectedException("Unable to fetch Credit Statements. Please try again later.");
        }

        /*
        "_links": {
            "self": { "href": "/api/v1/customer/{customer-id}/credit-statements/credit-accounts/{credit-account-id}" }
            "creditAccount": { "href": "/api/v1/customer/{customer-id}/credit-accounts/{credit-account-id}" }
            "allCreditStatements": { "href": "/api/v1/customer/{customer-id}/credit-statements" }
        }
         */

        /*Link selfLink = linkTo(methodOn(CreditStatementController.class)
                .getSelectedCreditAccountCreditStatementsForCustomer(customerId, creditAccountId, page, size, null, null))
                .withRel("self");*/

        URI selfLink = linkTo(CreditStatementController.class).slash(customerId)
                .toUriComponentsBuilder()
                .pathSegment("credit-statements", "credit-accounts", "{credit-account-id}")
                .queryParam("page", page)
                .queryParam("size", size)
                .build(creditAccountId);

        selectedCreditAccountCreditStatementsResponseDto.add(Link.of(String.valueOf(selfLink), "self"));

        // This link/endpoint requires an API call to 'cms-credit-account-service' to get the specified credit account
        // for the customer (tight coupling to another service) or,
        // requires a creation of endpoint locally to get the specified credit account (with only the data required by
        // this service) or,
        // redirect to api gateway
        URI creditAccountLink = linkTo(CreditStatementController.class).slash(customerId)
                .toUriComponentsBuilder()
                .pathSegment("credit-accounts", "{credit-account-id}")
                .build(creditAccountId);

        selectedCreditAccountCreditStatementsResponseDto
                .add(Link.of(String.valueOf(creditAccountLink), "creditAccount"));


        /*Link allCreditStatementsLink = linkTo(methodOn(CreditStatementController.class)
                .getAllCreditStatementsForCustomer(customerId, page, size, null, null, null))
                .withRel("allCreditStatements");*/

        URI allCreditStatementsLink = linkTo(CreditStatementController.class).slash(customerId)
                .toUriComponentsBuilder()
                .path("/credit-statements")
                .queryParam("page", page)
                .queryParam("size", size)
                .build().toUri();

        selectedCreditAccountCreditStatementsResponseDto.add(Link.of(String.valueOf(allCreditStatementsLink),
                "allCreditStatements"));

        return selectedCreditAccountCreditStatementsResponseDto;
    }

    /*
     * Alternative approach to view all credit statements for a credit account belonging to the customer
     * This approach -
     *
     * May result in simpler SQL queries generated by Spring Data JPA compared to complex joins,
     * leading to potentially faster execution times.
     *
     * May require additional database queries to check the credit account entity, resulting in multiple round trips to
     * the database. Performance can degrade if the number of accounts associated with a customer is large, as each
     * account needs to be individually checked.
     *
     * Adding the customer ID directly to the credit_statements table violates the principles of the Third Normal Form (3NF)
     * of database normalization. In Third Normal Form (3NF), every non-prime attribute (attribute that is not part of
     * any candidate key)of a table must be dependent on the primary key, and there should be no transitive dependencies.
     *
     * A credit statement (id) can transitively determine a customer (id) through a credit account (id)? Which implies,
     * customer (id) would transitively depend on credit statement (id) via a credit account (id)

    @GetMapping("/api/v1/credit-statements/credit-accounts/{credit-account-id}")
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and " +
            "@creditStatementAccessValidator.hasAccessToCreditAccount(#creditAccountId)")
    public SelectedCreditAccountCreditStatementsResponseDto getSelectedCreditAccountCreditStatementsForCustomer(
            @PathVariable("credit-account-id") String creditAccountId) {
        // implementation to retrieve credit statements by credit account
    }
     */

    // - GET - /api/v1/customer/{customer-id}/credit-statements/generate ( submit form to generate/fetch credit statement )
    @GetMapping(value = "/{customer-id}/credit-statements/generate", produces = {"application/json"})
    @PreAuthorize("hasAuthority('APP_CMS') and hasAuthority('ROLE_CUSTOMER') and #customerId == authentication.name")
    public ResponseEntity<Map<String, Object>> getRequestedCreditStatementForCustomer(
            @PathVariable("customer-id") String customerId,
            @Valid @RequestBody CreditStatementRequestDto creditStatementRequestDto,
            BindingResult bindingResult) throws BadRequestException {

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors()
                    .stream()
                    //.map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .forEach(error -> System.out.println(error.getDefaultMessage()));
            //.collect(Collectors.toList());
            throw new BadRequestException("Invalid Input! Unable to fetch the credit statement.");
        }

        CreditStatementDocument retrievedCreditStatementDocument;
        try {
            // this fetches statement from MongoDB
            retrievedCreditStatementDocument = creditStatementService
                    .getRequestedCreditStatementByCustomerId(customerId, creditStatementRequestDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new UnexpectedException("Unable to fetch Credit Statement. Please try again later.");
        }

        if (retrievedCreditStatementDocument == null) {
            throw new ResourceNotFoundException("No Credit Statement exists for the selected Credit Account in this date range.");
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Credit Statement fetched successfully!");
        responseBody.put("creditStatement", retrievedCreditStatementDocument);

        Map<String, Object> links = new HashMap<>();

        links.put("download", linkTo(CreditStatementController.class)
                .toUriComponentsBuilder()
                .path("/not-implemented")
                .build().toUri());

        //links.put("download", Collections.singletonMap("href", "/api/v1/customer/not-implemented"));
        responseBody.put("_links", links);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}

/* GET - /api/v1/customer/{customer-id}/credit-statements/generate ( submit form to generate/fetch credit statement )
{
    "message": "Credit Statement fetched successfully!",
    "creditStatement": {
            "value"
        }
    "_links": {
            "download": { "href": "/api/v1/customer/not-implemented" }
        }
    }
}
 */
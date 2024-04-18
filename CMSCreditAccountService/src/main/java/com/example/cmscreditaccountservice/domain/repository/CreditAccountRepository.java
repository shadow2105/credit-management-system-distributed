package com.example.cmscreditaccountservice.domain.repository;

import com.example.cmscreditaccountservice.domain.CreditAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditAccountRepository extends CrudRepository<CreditAccount, UUID> {

    /*
     * By including the customerId parameter, the methods ensures that only credit accounts belonging to a specific
     * customer can be retrieved. This helps enforce access control and prevents unauthorized users from accessing
     * sensitive account information.
     */

    // view specific/selected account by customerId
    Optional<CreditAccount> findByIdAndCustomerId(UUID id, String customerId);

    // view all accounts by customerId ordered by createdAt
    List<CreditAccount> findAllByCustomerIdOrderByCreatedAt(String customerId);

    // open/request new account
    // CreditAccount save(CreditAccount creditAccount);

    boolean existsByAccountNumber(String accountNumber);

}

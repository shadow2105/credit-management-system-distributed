package com.example.cmstransactionservice.domain.repository;

import com.example.cmstransactionservice.domain.CreditAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditAccountRepository extends CrudRepository<CreditAccount, UUID> {

    // view specific/selected account by customerId
    Optional<CreditAccount> findByIdAndCustomerId(UUID id, String customerId);

    // view all accounts by customerId ordered by createdAt
    List<CreditAccount> findAllByCustomerIdOrderByCreatedAt(String customerId);

    boolean existsByIdAndCustomerId(UUID id, String customerId);
}

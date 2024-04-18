package com.example.cmscreditstatementservice.domain.repository;

import com.example.cmscreditstatementservice.domain.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// CrudRepository doesn't have getReferenceById method
// https://vladmihalcea.com/spring-data-jpa-findbyid/
// https://stackoverflow.com/questions/39741102/how-to-beautifully-update-a-jpa-entity-in-spring-data
public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {

    // view specific/selected account by customerId
    Optional<CreditAccount> findByIdAndCustomerId(UUID id, String customerId);

    // view all accounts by customerId ordered by createdAt
    List<CreditAccount> findAllByCustomerIdOrderByCreatedAt(String customerId);

    boolean existsByIdAndCustomerId(UUID id, String customerId);
}

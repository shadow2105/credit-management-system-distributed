package com.example.cmscreditstatementservice.domain.repository;

import com.example.cmscreditstatementservice.domain.CreditStatementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CreditStatementDocumentRepository extends MongoRepository<CreditStatementDocument, String> {

    Optional<CreditStatementDocument>
    findByCustomerIdAndAccountDetails_AccountNumberAndCreditStatementDateMonthAndCreditStatementDateYear(
            String customerId,
            String accountNumber,
            int month,
            int year
            );
}

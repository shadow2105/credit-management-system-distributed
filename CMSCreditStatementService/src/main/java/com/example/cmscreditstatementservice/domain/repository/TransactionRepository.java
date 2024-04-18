package com.example.cmscreditstatementservice.domain.repository;

import com.example.cmscreditstatementservice.domain.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {

    // view all transactions by creditAccountId between startDateTime and endDateTime ordered by transactionDateTime
    /*@Query("""
            SELECT t FROM Transaction t
            WHERE t.creditAccount = :creditAccount
            AND t.transactionDateTime BETWEEN :startDateTime AND :endDateTime
            ORDER BY t.transactionDateTime
            """)
    List<Transaction> findAllByCreditAccountAndTransactionDateTimeBetweenOrderByTransactionDateTime(
            @Param("creditAccount") CreditAccount creditAccount,
            @Param("startDate") Timestamp startDateTime,
            @Param("endDate") Timestamp endDateTime);*/

    /*List<Transaction> findAllByCreditAccountAndTransactionDateTimeBetweenOrderByTransactionDateTime(
            CreditAccount creditAccount, Timestamp startDateTime, Timestamp endDateTime);*/

    List<Transaction> findAllByCreditAccount_IdAndTransactionDateTimeBetweenOrderByTransactionDateTime(
            UUID creditAccountIs, Timestamp startDateTime, Timestamp endDateTime);

}

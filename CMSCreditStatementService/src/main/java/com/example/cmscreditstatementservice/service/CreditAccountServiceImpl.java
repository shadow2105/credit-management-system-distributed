package com.example.cmscreditstatementservice.service;

import com.example.cmscreditstatementservice.domain.CreditAccount;
import com.example.cmscreditstatementservice.domain.CreditStatementSchedule;
import com.example.cmscreditstatementservice.domain.repository.CreditAccountRepository;
import com.example.cmscreditstatementservice.dto.AllCreditAccountsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CreditAccountServiceImpl implements CreditAccountService {

    private static final int MIN_DAYS_AFTER_ACCOUNT_CREATION_FOR_STATEMENT = 15;

    private static final int LAST_DAY_OF_MONTH_FOR_STATEMENT = 27;

    private final CreditAccountRepository creditAccountRepository;

    public CreditAccountServiceImpl(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AllCreditAccountsResponseDto getCreditAccountsByCustomerId(String customerId) {
        AllCreditAccountsResponseDto allCreditAccountsResponseDto = new AllCreditAccountsResponseDto();

        // Fetch persisted Customer CreditAccount(s) from data source
        List<CreditAccount> retrievedCreditAccounts = creditAccountRepository
                .findAllByCustomerIdOrderByCreatedAt(customerId);

        // Map each CreditAccount to AllCreditAccountsResponseDto.CreditAccountDetails
        List<AllCreditAccountsResponseDto.CreditAccountDetails> creditAccounts = retrievedCreditAccounts
                .stream()
                .map(retrievedCreditAccount -> {
                    AllCreditAccountsResponseDto.CreditAccountDetails creditAccount = new
                            AllCreditAccountsResponseDto.CreditAccountDetails();

                    creditAccount.setAccountId(retrievedCreditAccount.getId().toString());
                    creditAccount.setAccountNumber((retrievedCreditAccount.getAccountNumber()));
                    creditAccount.setCreditLimit(retrievedCreditAccount.getCreditLimit());
                    creditAccount.setCurrentBalance(retrievedCreditAccount.getCurrentBalance());
                    creditAccount.setCreditAvailable((retrievedCreditAccount.getCreditLimit()
                            .subtract(retrievedCreditAccount.getCurrentBalance())));

                    return creditAccount;
                })
                .toList();

        // Construct AllCreditAccountsResponseDto object
        allCreditAccountsResponseDto.setCustomerId(customerId);
        allCreditAccountsResponseDto.setCreditAccounts(creditAccounts);

        return allCreditAccountsResponseDto;
    }

    @Override
    @Transactional
    public void processCreditAccountMessage(String message) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> data = objectMapper.readValue(message, new TypeReference<>() {
        });

        Map<String, Object> payload = (Map<String, Object>) data.get("payload");

        /*
         * op field often indicates the type of operation that was performed on the data. Common values:
         *   "c" for "create" or "insert"
         *   "u" for "update"
         *   "d" for "delete"
         */
        String op = (String) payload.get("op");
        //System.out.println("/n" + op);

        String after = objectMapper.writeValueAsString(payload.get("after"));
        CreditAccount creditAccount = objectMapper.readValue(after, CreditAccount.class);
        //System.out.println("\n" + creditAccount.toString());

        if (op.equals("c")) {
            // Create an entry in the scheduling table for newly inserted Credit Account
            CreditStatementSchedule creditStatementSchedule = new CreditStatementSchedule();

            int statementDay = scheduleStatementDay(creditAccount.getCreatedAt());

            creditStatementSchedule.setStatementDayOfMonth(statementDay);

            creditAccount.addCreditStatementSchedule(creditStatementSchedule);

            creditAccountRepository.save(creditAccount);
        }
        else if (op.equals("u")) {
            CreditAccount retrievedCreditAccount = creditAccountRepository.getReferenceById(creditAccount.getId());

            retrievedCreditAccount.setCurrentBalance(creditAccount.getCurrentBalance());
            retrievedCreditAccount.setUpdatedAt(creditAccount.getUpdatedAt());
            retrievedCreditAccount.setUpdatedBy(creditAccount.getUpdatedBy());
        }
        else {
            System.out.println("Unhandled Operation : " + op);
        }
    }

    // LocalDateTime objects are immutable, ensuring thread safety and reducing the risk of unintended side effects
    //
    // LocalDateTime is designed to be thread-safe, making it suitable for use in multi-threaded environments without
    // requiring external synchronization.
    private int scheduleStatementDay(Timestamp createdAt) {

        LocalDateTime statementDateTime = createdAt.toLocalDateTime()
                .plusDays(MIN_DAYS_AFTER_ACCOUNT_CREATION_FOR_STATEMENT);

        // If the statement day is greater than LAST_DAY_OF_MONTH_FOR_STATEMENT,
        // set it to LAST_DAY_OF_MONTH_FOR_STATEMENT
        return Math.min(statementDateTime.getDayOfMonth(), LAST_DAY_OF_MONTH_FOR_STATEMENT);
    }

    // Calendar objects are mutable. This mutability can lead to unexpected behavior if not handled carefully.
    //
    // Calendar instances are not inherently thread-safe. If multiple threads access and modify the same Calendar
    // instance concurrently, it can result in data inconsistency or unexpected behavior.
    /*private int scheduleStatementDay(Timestamp createdAt) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createdAt.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 15);

        int statementDay = calendar.get(Calendar.DAY_OF_MONTH);

        // If the statement day is greater than 27, set it to 27
        return Math.min(statementDay, 27);
    }*/
}

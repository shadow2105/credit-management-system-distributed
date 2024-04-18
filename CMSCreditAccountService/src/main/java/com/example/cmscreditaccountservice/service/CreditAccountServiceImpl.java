package com.example.cmscreditaccountservice.service;

import com.example.cmscreditaccountservice.Utility;
import com.example.cmscreditaccountservice.domain.CreditAccount;
import com.example.cmscreditaccountservice.domain.CreditCard;
import com.example.cmscreditaccountservice.domain.repository.CreditAccountRepository;
import com.example.cmscreditaccountservice.dto.AllCreditAccountsResponseDto;
import com.example.cmscreditaccountservice.dto.CreditAccountOpeningRequestDto;
import com.example.cmscreditaccountservice.dto.SelectedCreditAccountResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreditAccountServiceImpl implements CreditAccountService {

    private static final String ISSUER_IDENTIFICATION_NUMBER = "402105";

    private static final BigDecimal THRESHOLD_INCOME = BigDecimal.valueOf(10000);
    private static final BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(20)
            .setScale(2, RoundingMode.HALF_UP);

    private static final int DEFAULT_CREDIT_CARD_EXPIRY = 4;
    private final CreditAccountRepository creditAccountRepository;

    public CreditAccountServiceImpl(CreditAccountRepository creditAccountRepository) {
        this.creditAccountRepository = creditAccountRepository;
    }

    // https://stackoverflow.com/questions/44984781/what-are-advantages-of-using-transactionalreadonly-true
    // https://vladmihalcea.com/spring-transactional-annotation/
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

        // Construct AllCreditAccountsResponseDto.Totals object
        AllCreditAccountsResponseDto.Totals totals = new AllCreditAccountsResponseDto.Totals();
        BigDecimal totalCreditLimit = calculateTotalCreditLimit(retrievedCreditAccounts);
        BigDecimal totalCurrentBalance = calculateTotalCurrentBalance(retrievedCreditAccounts);

        totals.setTotalCreditLimit(totalCreditLimit);
        totals.setTotalCurrentBalance(totalCurrentBalance);
        totals.setTotalCreditAvailable(totalCreditLimit.subtract(totalCurrentBalance));

        // Construct AllCreditAccountsResponseDto object
        allCreditAccountsResponseDto.setCustomerId(customerId);
        allCreditAccountsResponseDto.setCreditAccounts(creditAccounts);
        allCreditAccountsResponseDto.setTotals(totals);

        return allCreditAccountsResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SelectedCreditAccountResponseDto getCreditAccountByCustomerIdAndCreditAccountId(String customerId,
                                                                                           String creditAccountId) {
        SelectedCreditAccountResponseDto selectedCreditAccountResponseDto = new SelectedCreditAccountResponseDto();

        // Fetch persisted Customer CreditAccount(s) from data source
        Optional<CreditAccount> creditAccountOptional = creditAccountRepository
                .findByIdAndCustomerId(UUID.fromString(creditAccountId), customerId);

        if (creditAccountOptional.isEmpty()) {
            return null;
        }

        // Construct SelectedCreditAccountResponseDto object
        CreditAccount retrievedCreditAccount = creditAccountOptional.get();

        selectedCreditAccountResponseDto.setCustomerId(customerId);
        selectedCreditAccountResponseDto.setAccountId(retrievedCreditAccount.getId().toString());
        selectedCreditAccountResponseDto.setAccountNumber(retrievedCreditAccount.getAccountNumber());
        selectedCreditAccountResponseDto.setCreditLimit(retrievedCreditAccount.getCreditLimit());
        selectedCreditAccountResponseDto.setCurrentBalance(retrievedCreditAccount.getCurrentBalance());
        selectedCreditAccountResponseDto.setCreditAvailable(retrievedCreditAccount.getCreditLimit()
                .subtract(retrievedCreditAccount.getCurrentBalance()));

        return selectedCreditAccountResponseDto;
    }

    @Override
    @Transactional
    public CreditAccount addCreditAccountByCustomerId(String customerId,
                                                      CreditAccountOpeningRequestDto creditAccountOpeningRequestDto) {

        String email = creditAccountOpeningRequestDto.getEmail();
        BigDecimal grossAnnualHouseholdIncome = creditAccountOpeningRequestDto.getGrossAnnualHouseholdIncome();
        if (grossAnnualHouseholdIncome.compareTo(THRESHOLD_INCOME) < 0) {
            //sendEmailAboutAccountStatus(email, content);

            // simulating account opening process
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Processing account opening request. This may take a moment.");
            }

            return null;
        }

        int maxAttempts = 100;
        int attempts = 0;
        String accountNumber;

        do {
            accountNumber = generateAccountNumber();
            attempts++;
        } while (creditAccountRepository.existsByAccountNumber(accountNumber) && attempts < maxAttempts);

        if (attempts == maxAttempts) {
            throw new RuntimeException("Unable to generate a unique account number after " + maxAttempts + " attempts.");
        }


        // Build Credit Account
        CreditAccount creditAccount = new CreditAccount();
        creditAccount.setCustomerId(customerId);
        creditAccount.setAccountNumber(accountNumber);
        creditAccount.setCreditLimit(generateInitialCreditLimit(grossAnnualHouseholdIncome));
        creditAccount.setCurrentBalance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        creditAccount.setInterestRate(DEFAULT_INTEREST_RATE);

        creditAccount.setCreatedBy(customerId);
        creditAccount.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Add a Credit Card with Credit Account
        CreditCard creditCard = new CreditCard();
        creditCard.setCvv(Utility.generateRandomNumericString(3));
        creditCard.setExpiryDate(calculateCreditCardExpiryDate());

        creditAccount.addCreditCard(creditCard);

        // simulating account opening process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Processing account opening request. This may take a moment.");
        }
        return creditAccountRepository.save(creditAccount);
    }

    /*
    * Generates a sixteen-digit number
    * First fifteen digits are determined by the issuing institution
    * Last digit, called the check digit, is mathematically determined based on all other digits using -
    * Luhn Algorithm or modulo-10 Algorithm
        1. Start from the rightmost digit (i.e. check digit)
        2. Multiply every second digit by 2 (i.e. digit at even positions)
        3. If the result in step 2 is more than one digit, add them up
        4. Add the resulting digits to digits at the odd positions (total + x)
        5. Check digit (x) should be calculated accordingly, so that the final total is divisible by 10
    */
    private String generateAccountNumber() {
        String nextNineDigits = Utility.generateRandomNumericString(9);
        String firstFifteenDigits = ISSUER_IDENTIFICATION_NUMBER + nextNineDigits;

        int sum = 0;
        for (int i = 0; i < firstFifteenDigits.length(); i++) {

            // Max value of currentDigit is 9
            int currentDigit = Character.getNumericValue(firstFifteenDigits.charAt(i));
            if (i % 2 == 0) {
                // Max value of (currentDigit * 2) is 18
                currentDigit = currentDigit * 2;

                if (currentDigit > 9) {
                    // int leftDigit = currentDigit / 10;
                    // int rightDigit = currentDigit % 10;
                    currentDigit = (currentDigit / 10) + (currentDigit % 10);
                }
            }

            sum += currentDigit;
        }

        // Round up sum to the next multiple of ten
        int roundedSum = ((sum + 9) / 10) * 10;
        int checkDigit = roundedSum - sum;

        return  firstFifteenDigits + checkDigit;
    }

    private BigDecimal generateInitialCreditLimit(BigDecimal grossAnnualHouseholdIncome) {

        // Round up Gross Annual Household Income to the next multiple m of thousand
        int roundedIncome = ((grossAnnualHouseholdIncome.intValue() + 999) / 1000 ) * 1000;

        // Initial Credit Limit is one-tenth of the Gross Annual Household Income rounded up to the next thousand
        return BigDecimal.valueOf(0.1 * roundedIncome).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCreditLimit(List<CreditAccount> creditAccounts) {

        if (creditAccounts.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return creditAccounts.stream()
                // Credit Limits have a scale of 2; see {@link #generateInitialCreditLimit(BigDecimal)}
                .map(CreditAccount::getCreditLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalCurrentBalance(List<CreditAccount> creditAccounts) {

        if (creditAccounts.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return creditAccounts.stream()
                // Current Balances have a scale of 2;
                // see {@link #addCreditAccountByCustomerId(String, CreditAccountOpeningRequestDto)}
                .map(CreditAccount::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Date calculateCreditCardExpiryDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.YEAR, DEFAULT_CREDIT_CARD_EXPIRY);

        return new Date(calendar.getTimeInMillis());
    }

    private void sendEmailAboutAccountStatus(String email, String content) {
        // Not implemented
    }

}

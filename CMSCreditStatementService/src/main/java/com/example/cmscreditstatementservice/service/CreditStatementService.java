package com.example.cmscreditstatementservice.service;

import com.example.cmscreditstatementservice.domain.CreditStatementDocument;
import com.example.cmscreditstatementservice.dto.AllCreditStatementsResponseDto;
import com.example.cmscreditstatementservice.dto.CreditStatementRequestDto;
import com.example.cmscreditstatementservice.dto.SelectedCreditAccountCreditStatementsResponseDto;

public interface CreditStatementService {
    AllCreditStatementsResponseDto getCreditStatementsByCustomerId(String customerId, int page, int size,
                                                               String creditAccountId,
                                                               String month,
                                                               String year);

    SelectedCreditAccountCreditStatementsResponseDto getCreditStatementsByCustomerIdAndCreditAccountId(String customerId,
                                                                                                   String creditAccountId,
                                                                                                   int page, int size,
                                                                                                   String month,
                                                                                                   String year);

    CreditStatementDocument getRequestedCreditStatementByCustomerId(String customerId,
                                                                    CreditStatementRequestDto creditStatementRequestDto);

    void generateCreditStatementsDaily();
}

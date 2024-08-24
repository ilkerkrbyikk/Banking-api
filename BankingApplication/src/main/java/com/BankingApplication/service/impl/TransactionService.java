package com.BankingApplication.service.impl;

import com.BankingApplication.dto.TransactionDto;
import com.BankingApplication.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}

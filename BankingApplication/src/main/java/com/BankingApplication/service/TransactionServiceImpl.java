package com.BankingApplication.service;

import com.BankingApplication.dto.TransactionDto;
import com.BankingApplication.entity.Transaction;
import com.BankingApplication.repository.TransactionRepository;
import com.BankingApplication.service.impl.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("Success")
                .build();

        transactionRepository.save(transaction);
    }
}

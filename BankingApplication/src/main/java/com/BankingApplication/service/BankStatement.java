package com.BankingApplication.service;

import com.BankingApplication.entity.Transaction;
import com.BankingApplication.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class BankStatement {

    private TransactionRepository transactionRepository;

    //!Çalışmıyor

//    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate){
//
//
//        List<Transaction> transactionList = transactionRepository.findAll()
//                .stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
//                .filter(transaction -> transaction.getCreateAt().isEqual(LocalDateTime.parse(startDate))).
//                filter(transaction -> transaction.getCreateAt().isEqual());
//
//    }

}

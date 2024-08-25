package com.BankingApplication.controller;

import com.BankingApplication.entity.Transaction;
import com.BankingApplication.service.BankStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bankStatement")
@RequiredArgsConstructor
public class TransactionController {

    private BankStatement bankStatement;

    //! Bankstatementdaki generateStatement çalışmadığı için çalışmıyor.
//    @GetMapping
//    private List<Transaction> generateBankStatement(@RequestParam String accountNumber, @RequestParam String startDate,
//                                                    @RequestParam String endDate){
//
//        return bankStatement.generateStatement(accountNumber,startDate,endDate);
//
//    }


}

package com.BankingApplication.utils;

import lombok.Data;

import java.time.Year;
@Data
public class AccountUtils {

    public static final String ACCOUNT_EXIST_CODE = "001";
    public static final String ACCOUNT_EXIST_MESSAGE = "This account already exist.";
    public static final String ACCOUNT_CREATING_MESSAGE = "Account has been created succesfully.";
    public static final String ACCOUNT_CREATING_CODE = "002";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE= "User with the provided account number does not exist.";
    public static final String ACCOUNT_FOUND_MESSAGE = "";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "004";
    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance.";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been successfully debited.";
    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE= "Transfer successfull.";





    public static String generateAccountNumber(){
        Year currentYear = Year.now(); //* güncel yılı veriyor.

        int min = 100000;
        int max = 999999;

        int randNumber = (int) Math.floor((Math.random() * (max-min + 1 ) + min));

        //? Math.floor() = aşağıdaki en yakın sayıya yuarlıyor. Başındaki intde garanti olsun diye IDE ekletti.

        String year = String.valueOf(currentYear); //* yılı stringe çevirdim.

        String randomNumber = String.valueOf(randNumber); //* random sayıyı stringe çevirdim.

        StringBuilder accountNumber = new StringBuilder();


        return accountNumber.append(year).append(randomNumber).toString();
    }

}

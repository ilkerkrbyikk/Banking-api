package com.BankingApplication.service;

import com.BankingApplication.config.JwtTokenProvider;
import com.BankingApplication.dto.*;
import com.BankingApplication.entity.Role;
import com.BankingApplication.entity.User;
import com.BankingApplication.repository.TransactionRepository;
import com.BankingApplication.repository.UserRepository;
import com.BankingApplication.service.impl.EmailService;
import com.BankingApplication.service.impl.TransactionService;
import com.BankingApplication.service.impl.UserService;
import com.BankingApplication.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    private EmailService emailService;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    private JwtTokenProvider jwtTokenProvider;


    @Override
    public BankResponse createAccount(UserRequest userRequest) {
         //* Hesap açıp dbye kaydetme kodları yazıyor.

        //* Db'^de mevcut hesap var mı diye kontrol ediyorum.
        if (userRepository.existsByEmail((userRequest.getEmail()))) {
            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        // Mapper yazılabilir cok karışık ve uzun duruyor.
        User user = User.builder()
                        .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO) // * bakiyeye direkt 0 yazmak yerine bigdecimal zero vermek daha sağlıklı
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Role.ROLE_USER)
                .build();

        User createdUser = userRepository.save(user);
        //* Email gönderelim.
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(createdUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations. Your account has been succesfully created.")
                .build();


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATING_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATING_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(createdUser.getAccountBalance())
                        .accountNumber(createdUser.getAccountNumber())
                        .accountName(createdUser.getFirstName() + " " +
                                createdUser.getLastName() + " " + createdUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User founderUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());

        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .accountInfo(AccountInfo.builder()
                        .accountName(founderUser.getFirstName() + " " + founderUser.getLastName())
                        .accountBalance(founderUser.getAccountBalance())
                        .accountNumber(enquiryRequest.getAccountNumber())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're logged in!")
                .recipient(loginDto.getEmail())
                .messageBody("You logged into your account. bla bla bla......")
                .build();

        return BankResponse.builder()
                .responseMessage(null)
                .responseCode("Login success.")
                .build();

    }
    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return foundUser.getFirstName()+ " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userToCredit);

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("Credit")
                .amount(creditDebitRequest.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                        .accountNumber(creditDebitRequest.getAccountNumber())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest creditDebitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());

        //* big decimal olan balance ve amountu integera çevirdim ki hata almayım.
        int availableBalance = Integer.parseInt(userToDebit.getAccountBalance().toString());
        int debitAmount = Integer.parseInt(creditDebitRequest.getAmount().toString());

        if(availableBalance < debitAmount){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getAmount()));
            userRepository.save(userToDebit);

            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("Credit")
                    .amount(creditDebitRequest.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(creditDebitRequest.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {
        boolean isSourceAccountExist =
            userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());
        if(!isSourceAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        //* security yazılınca bu olmasada olur ama yinede olsun şimdilik.
        if (!isDestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceUserAccount = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());

        /* compareTo sadece 3 sonuç sunuyor.
           1,0,-1. büyükse 1 kücükse -1 eşitse 0 gibi.
         */
        if(transferRequest.getAmount().compareTo(sourceUserAccount.getAccountBalance()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceUserAccount.setAccountBalance(sourceUserAccount.getAccountBalance().subtract(transferRequest.getAmount()));
        userRepository.save(sourceUserAccount);
        //!Email service gönderme kodları yazılacak.

        User destinationAccount = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(transferRequest.getAmount()));
        userRepository.save(destinationAccount);
        //!Email service gönderme kodları yazılacak.

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(sourceUserAccount.getFirstName() + " " + sourceUserAccount.getLastName())
                        .accountNumber(null)
                        .build())
                .build();
    }
}

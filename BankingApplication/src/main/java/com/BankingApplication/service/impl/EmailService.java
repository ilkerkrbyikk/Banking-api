package com.BankingApplication.service.impl;

import com.BankingApplication.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}

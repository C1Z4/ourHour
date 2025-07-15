package com.ourhour.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final EmailSenderService emailSenderService;

    @Value("${spring.service.base-url-email}")
    private String serviceBaseUrl;

    @Transactional
    public void sendPwdLinkEmail(String email) {


    }

}

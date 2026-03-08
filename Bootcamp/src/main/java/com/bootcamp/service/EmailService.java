package com.bootcamp.service;

import com.bootcamp.entity.product.Product;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

public interface EmailService {

    @Async
    void sendAccountLockEmail(String recipient);

    @Async
    void sendSellerRegisteredEmail(String recipient);

    @Async
    void sendActivateAccountMail(String email, Date date);

    @Async
    void AccountActivatedSuccesfullyMail(String email);

    @Async
    void sendActivateProductMail(Product product);

    @Async
    void AccountDeactivatedSuccesfullyMail(String email);

    @Async
    void sendForgotPasswordMail(String email, Date date);

    @Async
    void PasswordUpdatedMail(String email);

    @Async
    void productActivatedMail(String email,Product product);

    @Async
    void productDeactivatedMail(String email,Product product);
}

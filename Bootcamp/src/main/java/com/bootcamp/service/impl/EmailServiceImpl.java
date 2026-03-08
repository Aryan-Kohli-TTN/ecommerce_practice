package com.bootcamp.service.impl;

import com.bootcamp.auditing.Auditing;
import com.bootcamp.enums.TokenType;
import com.bootcamp.entity.email.EmailType;
import com.bootcamp.entity.email.EmailDetails;
import com.bootcamp.entity.product.Product;
import com.bootcamp.exception.email.EmailSendingException;
import com.bootcamp.repository.email.EmailRepository;
import com.bootcamp.service.EmailService;
import com.bootcamp.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final JWTService jwtService;
    private final static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Value("${spring.mail.username}")
    String sender;

    private final EmailRepository emailRepository;



    private void saveEmailDetails(String recipient, String body,
                                  String subject, EmailType emailType, boolean isSuccess) {
        EmailDetails emailDetails = EmailDetails.builder()
                .emailType(emailType).sender(sender).productId(null).auditing(new Auditing())
                .isSuccess(isSuccess).recipient(recipient).subject(subject).build();
        emailRepository.save(emailDetails);
    }
    private void saveEmailDetailsWithProduct(String recipient, String body,
                                             String subject, EmailType emailType, boolean isSuccess, UUID productId) {
        EmailDetails emailDetails = EmailDetails.builder()
                .emailType(emailType).sender(sender).auditing(new Auditing()).productId(productId)
                .isSuccess(isSuccess).recipient(recipient).subject(subject).build();
        emailRepository.save(emailDetails);
    }

    @Async
    @Override
    public void sendAccountLockEmail(String recipient) {
        String body = "YOUR ACCOUNT IS LOCKED.";
        String subject = "ACCOUNT LOCKED | SHOPKART";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(recipient);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetails(recipient, body, subject, EmailType.ACCOUNT_LOCKED, true);
        } catch (Exception e) {
            logger.error("Error while sending account lock email to: {}", recipient);
            saveEmailDetails(recipient, body, subject, EmailType.ACCOUNT_LOCKED, false);
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void sendSellerRegisteredEmail(String recipient) {
        String body = "You have successfully registered on Shopkart." +
                " Your account will be activated shortly once the verification is done.";
        String subject = "REGISTERED SUCCESSFULLY | SHOPKART";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(recipient);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetails(recipient, body, subject, EmailType.SELLER_REGISTERED, true);
        } catch (Exception e) {
            logger.error("Error while sending seller registration email to: {}", recipient);
            saveEmailDetails(recipient, body, subject, EmailType.SELLER_REGISTERED, false);
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void sendActivateAccountMail(String email, Date date) {
        String token = jwtService.generateToken(email, TokenType.ACTIVATION_TOKEN, date);
        String body = "Activate your account by clicking here: http://localhost:8080/api/customer/activate/account?token=" + token;
        String subject = "ACTIVATE YOUR ACCOUNT | ShopKart";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetails(email, body, subject, EmailType.CUSTOMER_ACTIVATE_ACCOUNT, true);
            logger.debug("Activation mail token generated for email: {}", email);
        } catch (Exception e) {
            logger.error("Error while sending activation mail to: {}", email);
            saveEmailDetails(email, body, subject, EmailType.CUSTOMER_ACTIVATE_ACCOUNT, false);
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void AccountActivatedSuccesfullyMail(String email) {
        String body = "YOUR ACCOUNT HAS BEEN ACTIVATED";
        String subject = "ACCOUNT ACTIVATED | ShopKart";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetails(email, body, subject, EmailType.USER_ACCOUNT_ACTIVATED, true);
            logger.debug("Successfully  Account Activated sent email  : {}", email);
        } catch (Exception e) {
            logger.error("Error while sending account activation success mail to: {}", email);
            saveEmailDetails(email, body, subject, EmailType.USER_ACCOUNT_ACTIVATED, false);
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void sendActivateProductMail(Product product) {
        String body = "New Product is Listed "
                + "\n The details are as follows : "
                + "\n ProductId : " + product.getId()
                + "\n NAME : " + product.getProductName()
                + "\n BRAND: " + product.getProductBrand()
                + "\n CATEGORY: " + product.getCategory().getCategoryName()
                + "\n CATEGORYId: " + product.getCategory().getId()
                + "\n SELLER :  " + product.getSeller().getEmail()
                + "\n DESCRIPTION : " + product.getProductDescription()
                + "\n kindly update the product ";
        String subject = "ACTIVATE PRODUCT | ShopKart";
        //currently my admin is sending mail to itself
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(sender);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetailsWithProduct(sender, body, subject, EmailType.ACTIVATE_PRODUCT, true,product.getId());
            logger.debug("Actiavet  product  sent email  : {}", sender);
        } catch (Exception e) {
            logger.error("Error while sending product activate mail to: {}", sender);
            saveEmailDetailsWithProduct(sender, body, subject, EmailType.ACTIVATE_PRODUCT, false,product.getId());
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void AccountDeactivatedSuccesfullyMail(String email) {
        String body = "YOUR ACCOUNT HAS BEEN DEACTIVATED";
        String subject = "ACCOUNT DEACTIVATED | ShopKart";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetails(email, body, subject, EmailType.USER_ACCOUNT_DEACTIVATED, true);
            logger.debug("USER DEACTIVATED MAIL SENT  : {}", email);
        } catch (Exception e) {
            logger.error("Error while sending account deactivated mail to: {}", email);
            saveEmailDetails(email, body, subject, EmailType.USER_ACCOUNT_DEACTIVATED, false);
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void sendForgotPasswordMail(String email, Date date) {
        String token = jwtService.generateToken(email, TokenType.FORGOT_PASSWORD_TOKEN, date);
        String body = "Generate new password token: " + token;
        String subject = "CREATE NEW PASSWORD | ShopKart";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetails(email, body, subject, EmailType.FORGOT_PASSWORD_GENERATE, true);
            logger.debug("forgot password MAIL SENT  : {}", email);
        } catch (Exception e) {
            logger.error("Error while sending forgot password mail to: {}", email);
            saveEmailDetails(email, body, subject, EmailType.FORGOT_PASSWORD_GENERATE, false);
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void PasswordUpdatedMail(String email) {
        String subject = "Password Updated | ShopKart";
        String body = "Your password has been updated successfully";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetails(email, body, subject, EmailType.PASSWORD_UPDATED, true);
            logger.debug("password updated MAIL SENT  : {}", email);
        } catch (Exception e) {
            logger.error("Error while sending password updated mail to: {}", email);
            saveEmailDetails(email, body, subject, EmailType.PASSWORD_UPDATED, false);
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void productActivatedMail(String email, Product product) {
        String subject = "PRODUCT ACTIVATED | ShopKart";
        String body = "Your Product is activated successfully "
                + "\n The details are as follows : "
                + "\n ProductId : " + product.getId()
                + "\n NAME : " + product.getProductName()
                + "\n BRAND: " + product.getProductBrand()
                + "\n CATEGORY: " + product.getCategory().getCategoryName()
                + "\n CATEGORYId: " + product.getCategory().getId()
                + "\n SELLER :  " + product.getSeller().getEmail()
                + "\n DESCRIPTION : " + product.getProductDescription()
                + "\n";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetailsWithProduct(email, body, subject, EmailType.PRODUCT_ACTIVATED, true,product.getId());
            logger.debug("product activated MAIL SENT  : {}", email);
        } catch (Exception e) {
            logger.error("Error while sending product activated mail to: {}", email);
            saveEmailDetailsWithProduct(email, body, subject, EmailType.PRODUCT_ACTIVATED, false,product.getId());
            throw new EmailSendingException();
        }
    }

    @Async
    @Override
    public void productDeactivatedMail(String email, Product product) {
        String body = "Your Product is deactivated successfully "
                + "\n The details are as follows : "
                + "\n ProductId : " + product.getId()
                + "\n NAME : " + product.getProductName()
                + "\n BRAND: " + product.getProductBrand()
                + "\n CATEGORY: " + product.getCategory().getCategoryName()
                + "\n CATEGORYId: " + product.getCategory().getId()
                + "\n SELLER :  " + product.getSeller().getEmail()
                + "\n DESCRIPTION : " + product.getProductDescription()
                + "\n";
        String subject = "PRODUCT DEACTIVATED | ShopKart";
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            saveEmailDetailsWithProduct(email, body, subject, EmailType.PRODUCT_DEACTIVATED, true,product.getId());
            logger.debug("product deactivated MAIL SENT  : {}", email);
        } catch (Exception e) {
            logger.error("Error while sending product deactivated mail to : {}", email);
            saveEmailDetailsWithProduct(email, body, subject, EmailType.PRODUCT_DEACTIVATED, false,product.getId());
            throw new EmailSendingException();
        }
    }
}

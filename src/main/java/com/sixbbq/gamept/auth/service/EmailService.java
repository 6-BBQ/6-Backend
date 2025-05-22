package com.sixbbq.gamept.auth.service;

import com.sixbbq.gamept.auth.entity.EmailVerification;
import com.sixbbq.gamept.auth.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    public EmailService(JavaMailSender mailSender, EmailVerificationRepository emailVerificationRepository) {
        this.mailSender = mailSender;
        this.emailVerificationRepository = emailVerificationRepository;

    }

    @Transactional
    public void sendVerificationEmail(String email) {
        // 6자리 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // 기존 인증 코드가 있다면 삭제
        emailVerificationRepository.deleteById(email);

        // 새 인증 코드 저장 (5분 유효)
        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .verificationCode(verificationCode)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        emailVerificationRepository.save(emailVerification);

        // 이메일 발송
        sendEmail(email, verificationCode);
    }

    @Transactional
    public boolean verifyCode(String email, String code) {
        return emailVerificationRepository.findByEmailAndVerificationCode(email, code)
                .map(verification -> {
                    if (verification.getExpiresAt().isAfter(LocalDateTime.now())) {
                        verification.setVerified(true);
                        emailVerificationRepository.save(verification);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmailAndVerified(email, true)
                .isPresent();
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private void sendEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("RPGPT 이메일 인증 코드");
        message.setText(
                "RPGPT 회원가입을 위한 이메일 인증 코드입니다.\n\n" +
                        "인증 코드: " + verificationCode + "\n\n" +
                        "이 코드는 5분간 유효합니다."
        );

        mailSender.send(message);
    }
}
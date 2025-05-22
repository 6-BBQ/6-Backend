package com.sixbbq.gamept.auth.repository;

import com.sixbbq.gamept.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);
    Optional<EmailVerification> findByEmailAndVerified(String email, boolean verified);
}

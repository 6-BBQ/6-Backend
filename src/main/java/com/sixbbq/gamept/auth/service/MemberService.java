package com.sixbbq.gamept.auth.service;

import com.sixbbq.gamept.auth.dto.LoginDto;
import com.sixbbq.gamept.auth.dto.SignupDto;
import com.sixbbq.gamept.auth.entity.Member;
import com.sixbbq.gamept.auth.repository.MemberRepository;
import com.sixbbq.gamept.metrics.support.AuthMetricsRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthMetricsRecorder authMetricsRecorder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, EmailService emailService, AuthMetricsRecorder authMetricsRecorder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authMetricsRecorder = authMetricsRecorder;
    }

    // 회원가입
    @Transactional
    public Member signup(SignupDto signupDto) {
        System.out.println("🚀 MemberService.signup() 메서드 진입");
        try {
            // 이메일 인증 확인
            if (!emailService.isEmailVerified(signupDto.getEmail())) {
                System.out.println("❌ 이메일 인증 미완료");
                        authMetricsRecorder.counterSignupFailure("이메일 인증 미완료");
                throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
            }

            // 아이디 중복 체크
            if (memberRepository.existsById(signupDto.getUserId())) {
                System.out.println("❌ 아이디 중복");
                        authMetricsRecorder.counterSignupFailure("아이디 중복");
                throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
            }

            // 이메일 중복 체크
            if (memberRepository.existsByEmail(signupDto.getEmail())) {
                System.out.println("❌ 이메일 중복");
                        authMetricsRecorder.counterSignupFailure("이메일 중복");
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }

            // 비밀번호 일치 여부 확인
            if (signupDto.getPasswordConfirm() != null &&
                    !signupDto.getPassword().equals(signupDto.getPasswordConfirm())) {
                System.out.println("❌ 비밀번호 불일치");
                        authMetricsRecorder.counterSignupFailure("비밀번호 불일치");
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            // 회원 엔티티 생성 및 저장
            Member member = new Member();
            member.setUserId(signupDto.getUserId());
            member.setEmail(signupDto.getEmail());
            member.setPassword(passwordEncoder.encode(signupDto.getPassword()));
            member.setNickname(signupDto.getNickname());

            Member savedMember = memberRepository.save(member);
            System.out.println("✅ 회원가입 성공 - 메트릭 기록 시작");

                    // 회원가입 성공 메트릭 기록
                    authMetricsRecorder.counterSignupSuccess();

            System.out.println("✅ 회원가입 성공 - 메트릭 기록 완료");
            return savedMember;

        } catch (IllegalArgumentException e) {
            // 이미 메트릭이 기록된 경우는 다시 기록하지 않음
            throw e;
        } catch (Exception e) {
            // 예상치 못한 오류
            System.out.println("❌ 시스템 오류");
                    authMetricsRecorder.counterSignupFailure("시스템 오류");
            throw e;
        }
    }
    // 로그인
//    @Transactional(readOnly = true)
//    public Member login(LoginDto loginDto) {
//        Member member = memberRepository.findById(loginDto.getUserId())
//                .orElse(null);
//
//        // 회원이 존재하고 비밀번호가 일치하는 경우
//        if (member != null && passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
//            return member;
//        }
//
//        return null;
//    }

    // ID로 회원 조회
    @Transactional(readOnly = true)
    public Member findById(String userId) {
        return memberRepository.findById(userId).orElse(null);
    }

    // 이메일 중복체크
    @Transactional(readOnly = true)
    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }
}
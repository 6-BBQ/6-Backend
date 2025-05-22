package com.sixbbq.gamept.auth.repository;

import com.sixbbq.gamept.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByEmail(String email);
}
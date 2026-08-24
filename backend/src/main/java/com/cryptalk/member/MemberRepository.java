package com.cryptalk.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmailIgnoreCase(String email);
}

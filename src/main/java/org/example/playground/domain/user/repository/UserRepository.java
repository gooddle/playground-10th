package org.example.playground.domain.user.repository;

import org.example.playground.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //쿼리문 대충 SELECT *(컬럼전체) FROM users WHERE email = ? (매개변수로 받는 이메일 값)
    Optional<User> findByEmail(String email);
}

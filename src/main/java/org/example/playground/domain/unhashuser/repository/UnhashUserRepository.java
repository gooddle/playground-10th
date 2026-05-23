package org.example.playground.domain.unhashuser.repository;

import org.example.playground.domain.unhashuser.model.UnhashUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnhashUserRepository extends JpaRepository<UnhashUser, Long> {
    Optional<UnhashUser> findByEmail(String email);
    Optional<UnhashUser> findByEmailAndPassword(String email, String password);
}

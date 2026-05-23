package org.example.playground.domain.shauser.repository;

import org.example.playground.domain.shauser.model.ShaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShaUserRepository extends JpaRepository<ShaUser, Long> {
    Optional<ShaUser> findByEmail(String email);
    Optional<ShaUser> findByEmailAndPassword(String email, String password);

    // DB에서 SHA2 해싱 + 식별 동시 처리 (평문 비밀번호를 넘기면 DB가 해싱)
    @Query(value = """
            SELECT *
            FROM sha_users
            WHERE email    = :email
              AND password = SHA2(:password, 256)
            """, nativeQuery = true)
    Optional<ShaUser> findByEmailWithDbHash(@Param("email") String email,
                                            @Param("password") String password);
}

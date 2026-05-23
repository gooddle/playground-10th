package org.example.playground.domain.unhashuser.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.unhashuser.constant.UnhashUserRole;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name = "unhash_users")
public class UnhashUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role = UnhashUserRole.NORMAL.name();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

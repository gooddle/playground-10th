package org.example.playground.domain.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.user.constant.UserRole;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name ="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name ="password")
    private String password;

    @Column(name = "role")
    private String role = UserRole.NORMAL.name();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

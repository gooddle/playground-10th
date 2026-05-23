package org.example.playground.domain.shauser.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.playground.domain.shauser.constant.ShaUserRole;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity(name = "sha_users")
public class ShaUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role = ShaUserRole.NORMAL.name();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

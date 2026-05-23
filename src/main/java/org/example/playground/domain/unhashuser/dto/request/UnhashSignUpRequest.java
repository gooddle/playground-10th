package org.example.playground.domain.unhashuser.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnhashSignUpRequest {
    private String email;
    private String password;
}

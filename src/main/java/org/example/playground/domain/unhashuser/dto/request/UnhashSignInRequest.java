package org.example.playground.domain.unhashuser.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnhashSignInRequest {
    private String email;
    private String password;
}

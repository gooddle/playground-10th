package org.example.playground.domain.shauser.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShaSignInRequest {
    private String email;
    private String password;
}

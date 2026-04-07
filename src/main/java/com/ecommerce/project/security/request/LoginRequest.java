package com.ecommerce.project.security.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String identifier;
    private String password;
    private Boolean rememberMe;

}

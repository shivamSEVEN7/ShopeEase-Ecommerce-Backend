package com.ecommerce.project.security.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String name;
    private String username;
    private String email;
    private String mobileNumber;
    private String gender;
    private String password;
}

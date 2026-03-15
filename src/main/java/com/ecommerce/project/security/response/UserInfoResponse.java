package com.ecommerce.project.security.response;

import com.ecommerce.project.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Getter
@Setter
public class UserInfoResponse {
    private long userId;
    private String username;
    private String email;
    private List<?> roles;

    public UserInfoResponse(long userId, String username, String email, List<?> roles) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

}

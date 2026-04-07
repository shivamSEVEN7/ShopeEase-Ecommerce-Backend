package com.ecommerce.project.security.response;

import com.ecommerce.project.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private UserDTO userDetails;
    private String username;
    private String accessToken;
//    private String refreshToken;
//    private String sessionId;
    private List<String> roles;
    private OffsetDateTime expiresAt;


}

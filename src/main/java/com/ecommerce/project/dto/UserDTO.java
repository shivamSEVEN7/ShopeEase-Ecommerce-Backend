package com.ecommerce.project.dto;

import com.ecommerce.project.model.AccountStatus;
import com.ecommerce.project.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private long userId;
    private String name;
    private Gender gender;
    private String username;
    private String email;
    private String mobileNumber;
    private AccountStatus accountStatus;
    private List<SessionInfo> activeSessions = new ArrayList<>();
}

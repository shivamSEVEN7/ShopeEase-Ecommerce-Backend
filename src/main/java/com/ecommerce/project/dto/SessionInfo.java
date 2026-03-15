package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfo {
    private String id;
    private String deviceType;
    private String deviceInfo;
    private String ipAddress;
    private OffsetDateTime loginTime;
    private Boolean currentSession;
}

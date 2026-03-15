package com.ecommerce.project.utility;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua_parser.*;

@Component
public class ClientInfoUtil {
    @Autowired
    Parser parser;
    public String getClientIpAddress(HttpServletRequest request){
        String ipAddress = request.getHeader("X-Forwarded-For"); // Common for proxies
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr(); // Fallback
        }
        return ipAddress;
    }

    public String getClientDeviceInfo(HttpServletRequest request){
        String userAgentString = request.getHeader("User-Agent");
        Client client = parser.parse(userAgentString);
        // --- Browser Info ---
        UserAgent userAgent = client.userAgent;
        String browserName = userAgent.family;
        String browserVersion = userAgent.major != null ? userAgent.major : "";
        // --- OS Info
        OS os = client.os;
        String osName = os.family;
        if (os.major != null) {
            osName += " " + os.major;
        }
        if (os.minor != null) {
            osName += "." + os.minor;
        }
        String finalBrowserString = (browserName + " " + browserVersion).trim();
        String finalOsString = osName.trim();
        return String.format("%s on %s", finalBrowserString, finalOsString);
    }

    public String getDeviceType(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        Client client = parser.parse(userAgentString);
        String osFamily = client.os.family;
        String deviceFamily = client.device.family;
        if (deviceFamily != null) {
            if ("Spider".equals(deviceFamily)) {
                return "Bot";
            }
            if (deviceFamily.contains("Tablet") || "iPad".equals(deviceFamily)) {
                return "Tablet";
            }
        }
        if (osFamily != null) {
            if ("Android".equals(osFamily) || "iOS".equals(osFamily) || "Windows Phone".equals(osFamily)) {
                return "Mobile";
            }
        }
        return "Desktop";
    }

}

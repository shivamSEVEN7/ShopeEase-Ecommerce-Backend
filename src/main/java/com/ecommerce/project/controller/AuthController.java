package com.ecommerce.project.controller;

import com.ecommerce.project.dto.SessionInfo;
import com.ecommerce.project.dto.UserDTO;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.RefreshTokenRepo;
import com.ecommerce.project.repositories.RoleRepo;
import com.ecommerce.project.repositories.UserRepo;
import com.ecommerce.project.security.jwt.JwtUtil;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.LoginResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.service.UserDetailsImpl;
import com.ecommerce.project.service.EmailService;
import com.ecommerce.project.service.OtpService;
import com.ecommerce.project.service.RefreshTokenService;
import com.ecommerce.project.utility.ClientInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController()
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    ClientInfoUtil clientInfoUtil;
    @Autowired
    RefreshTokenRepo refreshTokenRepo;
    @Value("${jwt.expiration}")
    private long jwtExpiry;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;


   @PostMapping("/refresh")
   ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, @CookieValue(value = "sessionId", required = false) String sessionId){
       RefreshToken oldRefreshToken = refreshTokenService.validateRefreshToken(refreshToken, sessionId);
       if(oldRefreshToken==null){
           System.out.println("deleting tokens");
           ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", null)
                   .httpOnly(true)
                   .secure(false)
                   .path("/")
                   .maxAge(0)
                   .sameSite("lax")
                   .build();
           ResponseCookie sessionIdCookie = ResponseCookie.from("sessionId", null)
                   .httpOnly(true)
                   .secure(false)           // ensure HTTPS in prod
                   .path("/")
                   .maxAge(0)
                   .sameSite("lax")
                   .build();
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString(),  HttpHeaders.SET_COOKIE, sessionIdCookie.toString())
                   .body(Map.of("error", "Unauthorized", "message", "Invalid token"));
       }

           String rawRefreshToken = UUID.randomUUID().toString();
           RefreshToken newRefreshToken = refreshTokenService.refreshToken(oldRefreshToken, rawRefreshToken);
       List<String> roles = newRefreshToken.getUser().getRoles().stream().map(role -> role.getRoleName().toString()).toList();
           String newJwtToken = jwtUtil.generateToken(newRefreshToken.getUser().getUsername());
       UserDTO userDTO = modelMapper.map(newRefreshToken.getUser(), UserDTO.class);
       List<RefreshToken> refreshTokens = refreshTokenService.fetchRefreshTokensOfUser(newRefreshToken.getUser().getUserId());
       for(RefreshToken refreshToken1 : refreshTokens){
           userDTO.getActiveSessions().add(new SessionInfo(refreshToken1.getSessionId(), refreshToken1.getDeviceType(), refreshToken1.getDeviceInfo(), refreshToken1.getIpAddress(), refreshToken1.getCreatedAt(), sessionId.equals(refreshToken1.getSessionId())));
       }
           ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", rawRefreshToken)
                   .httpOnly(true)
                   .secure(false)
                   .path("/")
                   .maxAge(Duration.between(Instant.now(), newRefreshToken.getExpiry()))
                   .sameSite("lax")
                   .build();


                return ResponseEntity.ok()
               .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
               .body(new LoginResponse(userDTO, newRefreshToken.getUser().getUsername(),newJwtToken, roles, Instant.now().plusMillis(jwtExpiry)));
   }

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        try{
           Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
           User user= ((UserDetailsImpl) authentication.getPrincipal()).getUser();
           if(user.getAccountStatus() == AccountStatus.UNVERIFIED){
               return ResponseEntity.status(HttpStatus.FORBIDDEN)
                       .body(Map.of(
                               "success", false,
                               "message", "Your account is not verified"
                       ));
           }
            String token = jwtUtil.generateToken(loginRequest.getUsername());
           String rawRefreshToken = UUID.randomUUID().toString();
           RefreshToken refreshToken = refreshTokenService.generateRefreshToken(rawRefreshToken, loginRequest.getUsername(), request);
           List<String> roles = refreshToken.getUser().getRoles().stream().map(role -> role.getRoleName().toString()).toList();
           refreshTokenRepo.save(refreshToken);
           ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", rawRefreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.between(Instant.now(), refreshToken.getExpiry()))
                    .sameSite("lax")
                    .build();
           ResponseCookie sessionIdCookie = ResponseCookie.from("sessionId", refreshToken.getSessionId())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.between(Instant.now(), refreshToken.getExpiry()))
                    .sameSite("lax")
                    .build();
            UserDTO userDTO = modelMapper.map(refreshToken.getUser(), UserDTO.class);
            List<RefreshToken> refreshTokens = refreshTokenService.fetchRefreshTokensOfUser(user.getUserId());
            for(RefreshToken refreshToken1 : refreshTokens){
                userDTO.getActiveSessions().add(new SessionInfo(refreshToken1.getSessionId(), refreshToken1.getDeviceType(), refreshToken1.getDeviceInfo(), refreshToken1.getIpAddress(), refreshToken1.getCreatedAt(), refreshToken.getSessionId().equals(refreshToken1.getSessionId())));
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString(), HttpHeaders.SET_COOKIE, sessionIdCookie.toString())
                    .body(new LoginResponse(userDTO , loginRequest.getUsername(),token, roles, Instant.now().plusMillis(jwtExpiry)));
        }
        catch (Exception e){
            System.out.println("Exception Occurred in Login " + e);
            throw e;
        }
    }
    @PostMapping("/signup")
    ResponseEntity<?> signUp(@Valid @RequestBody SignupRequest signupRequest){

        String name = signupRequest.getName();
        String username = signupRequest.getUsername();
        String email = signupRequest.getEmail();
        String password  = signupRequest.getPassword();
        String mobileNumber = signupRequest.getMobileNumber();
        Gender gender = signupRequest.getGender().equalsIgnoreCase("male") ? Gender.MALE : Gender.FEMALE;
        if(userRepo.existsByUsername(username)){
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);
        } else if (userRepo.existsByEmail(email)) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }
        else if (userRepo.existsByMobileNumber(mobileNumber)) {
            return new ResponseEntity<>("Mobile Number already exists", HttpStatus.BAD_REQUEST);
        }
        User newUser = new User(name, gender, username, email, mobileNumber, passwordEncoder.encode(password));
        newUser.getRoles().add(roleRepo.findByRoleName(RoleName.CUSTOMER));

        User savedUser = userRepo.save(newUser);
        String verificationToken = otpService.generateVerificationToken(signupRequest.getEmail());
        emailService.sendVerificationMail(signupRequest.getEmail(), "Verification Mail", "http://localhost:5173/verify?token=" + verificationToken);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, @CookieValue(value = "sessionId", required = false) String sessionId) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)             // delete cookie
                .sameSite("lax")
                .build();
        ResponseCookie sessionIdCookie = ResponseCookie.from("sessionId", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("lax")
                .build();
        refreshTokenService.invalidateRefreshToken(refreshToken, sessionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString(), HttpHeaders.SET_COOKIE, sessionIdCookie.toString())
                .body(Map.of("message", "logged out"));
    }

    @PostMapping("/sessions/{sessionId}/logout")
    public ResponseEntity<?> logoutFromOtherDevice(@PathVariable String sessionId) {
        refreshTokenService.invalidateRefreshTokenOtherDevice(sessionId);
        return ResponseEntity.ok()
                .body(Map.of("message", "logged out"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam String token){
        System.out.println("Token is " + token);
        String email = otpService.verifyVerificationToken(token);
        if(email!=null){
            User user = userRepo.getByEmail(email);
            user.setAccountStatus(AccountStatus.VERIFIED);
            userRepo.save(user);
            System.out.println("Account Verified Successfully for " + email);
            return ResponseEntity.ok().body(Map.of("success", "true", "message", "Account verified Successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("success", "false"));
    }

    @GetMapping("/user")
    ResponseEntity<?> currentUserInfo(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println(authentication.getAuthorities());
        return new ResponseEntity<>(new UserInfoResponse(userDetails.getUserId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getAuthorities().stream().toList()), HttpStatus.OK);
    }
    @PostMapping("/role")
    ResponseEntity<?> createRole(){
        Role savedRole  = roleRepo.save(new Role(RoleName.CUSTOMER));
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/hello")
    ResponseEntity<String> hello(){
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}

package com.ecommerce.project.controller;

import com.ecommerce.project.dto.SessionInfo;
import com.ecommerce.project.dto.UserDTO;
import com.ecommerce.project.model.*;
import com.ecommerce.project.repositories.RefreshTokenRepo;
import com.ecommerce.project.repositories.RoleRepo;
import com.ecommerce.project.repositories.SessionRepo;
import com.ecommerce.project.repositories.UserRepo;
import com.ecommerce.project.security.jwt.JwtUtil;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.LoginResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.service.UserDetailsImpl;
import com.ecommerce.project.service.*;
import com.ecommerce.project.utility.AuthUtil;
import com.ecommerce.project.utility.ClientInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    AuthUtil authUtil;
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
    @Value("${refreshtoken.expiration}")
    private long refreshTokenExpiration;
    @Value(("${jwt.expiration}"))
    private long jwtExpiration;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OtpService otpService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SessionRepo sessionRepo;
    @Autowired
    private HmacService hmacService;
    @Autowired
    private SessionService sessionService;
    @Value("${frontend.url}")
    private String frontEnd;
    @Value("${refreshtoken.shortexpiration}")
    private long refreshTokenShortExpiration;

   @PostMapping("/refresh")
   ResponseEntity<?> issueTokens(@CookieValue(value = "refreshToken", required = false) String refreshToken, @CookieValue(value = "sessionId", required = false) String sessionId, HttpServletRequest request){
       String hashedOldRefreshToken = null;
       if(refreshToken!=null){
           hashedOldRefreshToken = hmacService.hash(refreshToken);
       }
       if(refreshTokenService.validateRefreshToken(hashedOldRefreshToken,sessionId)){
           String newRawRefreshToken = UUID.randomUUID().toString();
           RefreshToken newRefreshToken = null;
            Optional<RefreshToken> oldRefreshTokenOptional = refreshTokenRepo.findByHashedRefreshToken(hashedOldRefreshToken);
            Session session = sessionRepo.findBySessionId(sessionId);
            RefreshToken oldRefreshToken = oldRefreshTokenOptional.get();
           if (oldRefreshToken.isUsed()) {
               if (oldRefreshToken.isWithinGracePeriod()) {
                   newRefreshToken = refreshTokenService.rotateRefreshTokensWithinGrace(oldRefreshToken, newRawRefreshToken);
               } else {
                   System.out.println("Token Reuse Detected");
               }
           }
           else{
               newRefreshToken = refreshTokenService.rotateRefreshTokens(oldRefreshToken, newRawRefreshToken);
           }
           User user = newRefreshToken.getUser();
           List<String> roles = user.getRoles().stream().map(role -> role.getRoleName().toString()).toList();
           String newJwtToken = jwtUtil.generateToken(user.getUsername());
           UserDTO userDTO = modelMapper.map(newRefreshToken.getUser(), UserDTO.class);
           List<Session> activeSessions = sessionRepo.findByUserUserIdAndActiveTrue(user.getUserId());
           for(Session session1 : activeSessions){
               userDTO.getActiveSessions().add(new SessionInfo(session1.getSessionId(), session1.getDeviceType(), session1.getDeviceInfo(), session1.getIpAddress(), session1.getCreatedAt(), session1.getSessionId().equals(sessionId)));
           }
           ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRawRefreshToken)
                   .httpOnly(true)
                   .secure(false)
                   .path("/")
                   .maxAge(session.isRememberMe()?Duration.between(OffsetDateTime.now(), newRefreshToken.getExpiry()):Duration.ofSeconds(-1))
                   .sameSite("lax")
                   .build();
           ResponseCookie sessionIdCookie = ResponseCookie.from("sessionId", newRefreshToken.getSession().getSessionId())
                   .httpOnly(true)
                   .secure(false)
                   .path("/")
                   .maxAge(session.isRememberMe()?Duration.between(OffsetDateTime.now(), newRefreshToken.getExpiry()):Duration.ofSeconds(-1))
                   .sameSite("lax")
                   .build();
           return ResponseEntity.ok()
                   .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                   .header(HttpHeaders.SET_COOKIE, sessionIdCookie.toString())
                   .body(new LoginResponse(
                           userDTO,
                           user.getUsername(),
                           newJwtToken,
                           roles,
                           OffsetDateTime.now().plusMinutes(jwtExpiration/60000)
                   ));
       }
           System.out.println("Inalid Refresh token found now logout");
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

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        try{
           Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));
           User user= ((UserDetailsImpl) authentication.getPrincipal()).getUser();
           if(user.getAccountStatus() == AccountStatus.UNVERIFIED){
               return ResponseEntity.status(HttpStatus.FORBIDDEN)
                       .body(Map.of(
                               "success", false,
                               "message", "Your account is not verified"
                       ));
           }
            Session session = Session.builder()
                    .sessionId(UUID.randomUUID().toString())
                    .user(userRepo.findByUsername(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username not found")))
                    .deviceType(clientInfoUtil.getDeviceType(request))
                    .deviceInfo(clientInfoUtil.getClientDeviceInfo(request))
                    .ipAddress(clientInfoUtil.getClientIpAddress(request))
                    .active(true)
                    .rememberMe(loginRequest.getRememberMe())
                    .expiry(OffsetDateTime.now().plusMinutes(loginRequest.getRememberMe()?refreshTokenExpiration:refreshTokenShortExpiration))
                    .build();
            sessionRepo.save(session);
           String token = jwtUtil.generateToken(user.getUsername());
           String rawRefreshToken = UUID.randomUUID().toString();
           RefreshToken refreshToken = refreshTokenService.generateRefreshToken(rawRefreshToken, user.getUsername(), session);
           List<String> roles = refreshToken.getUser().getRoles().stream().map(role -> role.getRoleName().toString()).toList();
           refreshTokenRepo.save(refreshToken);
           ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", rawRefreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(loginRequest.getRememberMe()?Duration.between(OffsetDateTime.now(), refreshToken.getExpiry()):Duration.ofSeconds(-1))
                    .sameSite("lax")
                    .build();
           ResponseCookie sessionIdCookie = ResponseCookie.from("sessionId", session.getSessionId())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                   .maxAge(loginRequest.getRememberMe()?Duration.between(OffsetDateTime.now(), refreshToken.getExpiry()):Duration.ofSeconds(-1))
                   .sameSite("lax")
                    .build();
            UserDTO userDTO = modelMapper.map(refreshToken.getUser(), UserDTO.class);
            List<Session> activeSessions = sessionRepo.findByUserUserIdAndActiveTrueAndExpiryAfter(user.getUserId(), OffsetDateTime.now());
            for(Session session1 : activeSessions){
                userDTO.getActiveSessions().add(new SessionInfo(session1.getSessionId(), session1.getDeviceType(), session1.getDeviceInfo(), session1.getIpAddress(), session1.getCreatedAt(), session1.getSessionId().equals(session.getSessionId())));
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, sessionIdCookie.toString())
                    .body(new LoginResponse(
                            userDTO,
                            user.getUsername(),
                            token,
                            roles,
                            OffsetDateTime.now().plusMinutes(jwtExpiration/60000)
                    ));
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
        emailService.sendVerificationMail(signupRequest.getEmail(), "Verification Mail", frontEnd + "/verify?token=" + verificationToken);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            @CookieValue(value = "sessionId", required = false) String sessionId) {

        // Delete refreshToken cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        // Delete sessionId cookie
        ResponseCookie sessionIdCookie = ResponseCookie.from("sessionId", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        // Invalidate session safely
        if (sessionId != null && !sessionId.isEmpty() && sessionService.validateSessionId(refreshToken, sessionId)) {
            sessionService.invalidateSession(sessionId);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, sessionIdCookie.toString())
                .body(Map.of("message", "logged out"));
    }

    @PostMapping("/sessions/{sessionId}/logout")
    @Transactional
    public ResponseEntity<?> logoutFromOtherDevice(@PathVariable String sessionId) {
                Session sessionToBeDeleted = sessionRepo.findBySessionId(sessionId);
                if(sessionToBeDeleted.getUser().getUserId() == authUtil.loggedInUserId()){
                    sessionService.invalidateSession(sessionId);
                }
        return ResponseEntity.ok()
                .body(Map.of("message", "logged out"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam String token){

        String email = otpService.verifyVerificationToken(token);
        if(email!=null){
            User user = userRepo.getByEmail(email);
            user.setAccountStatus(AccountStatus.VERIFIED);
            userRepo.save(user);

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

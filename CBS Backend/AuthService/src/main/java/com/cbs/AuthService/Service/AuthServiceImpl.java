package com.cbs.AuthService.Service;

import com.cbs.AuthService.AuthDto.ApiResponseDto;
import com.cbs.AuthService.AuthDto.AuthRequest;
import com.cbs.AuthService.AuthDto.PasswordDto;
import com.cbs.AuthService.Entity.AuthEntity;
import com.cbs.AuthService.Exception.DoesNotExistException;
import org.springframework.http.HttpStatus;
import com.cbs.AuthService.JWT.JwtHelper;
import com.cbs.AuthService.JWT.JwtResponse;
import com.cbs.AuthService.Repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuthServiceImpl implements IAuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager manager;
    private final UserDetailsService userDetailsService;
    private final JwtHelper jwtHelper;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(AuthenticationManager manager, UserDetailsService userDetailsService,
                           JwtHelper jwtHelper, AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.manager = manager;
        this.userDetailsService = userDetailsService;
        this.jwtHelper = jwtHelper;
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public JwtResponse loginUser(AuthRequest authRequest) {

        Optional<AuthEntity> auth = authRepository.findByEmail(authRequest.getEmail());

        if (auth.isEmpty()) {
            System.out.println(auth);
            logger.error("User not found in repository after successful authentication for email: {}", authRequest.getEmail());
            throw new DoesNotExistException("No User found with this Email. Please Register!!!");
        }
        this.doAuthenticate(authRequest.getEmail(), authRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER"); // fallback if no role found
        String token = jwtHelper.generateToken(userDetails, role);
        logger.info("Token has been generated for user: {}", userDetails.getUsername());
        return new JwtResponse(token, auth.get().getUserName(), auth.get().getEntityId(), role);
    }

    @Override
    public ApiResponseDto<String> changePassword(long userId, PasswordDto passwordDto) {
        Optional<AuthEntity> optionalUser = authRepository.findByEntityId(userId);
        if (optionalUser.isEmpty()) {
            logger.warn("Password change failed: User with ID {} not found.", userId);
            return new ApiResponseDto<>("User not found", HttpStatus.NOT_FOUND, LocalDateTime.now(), null);
        }
        AuthEntity existingUser = optionalUser.get();
        if (!passwordEncoder.matches(passwordDto.getPasswordhash(), existingUser.getPasswordHash())) {
            logger.warn("Password change failed for user {}: Current password is wrong.", existingUser.getEmail());
            return new ApiResponseDto<>("Current password is wrong", HttpStatus.UNAUTHORIZED, LocalDateTime.now(), null);
        }
        try {
            existingUser.setPasswordHash(passwordEncoder.encode(passwordDto.getChangedPassword()));
            authRepository.save(existingUser);
            logger.info("Password updated successfully for user: {}", existingUser.getEmail());
            return new ApiResponseDto<>("Password updated !!!", HttpStatus.OK, LocalDateTime.now(), null);
        } catch (Exception e) {
            logger.error("Failed to update password for user {} due to an internal error: {}", existingUser.getEmail(), e.getMessage(), e);
            return new ApiResponseDto<>("Failed to update password due to an internal error", HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), null);
        }
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication authentication = manager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Authentication successful for user: {}", email);
        } catch (BadCredentialsException e) {
            logger.error("Authentication FAILED for user: {} - Reason: Invalid Username or Password.", email);
            throw new BadCredentialsException("Incorrect Password  !!");
        } catch (AuthenticationException e) {
            logger.error("Authentication FAILED for user: {} - General Authentication Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}
package com.hostel.management.auth.service;

import com.hostel.management.auth.dto.LoginRequest;
import com.hostel.management.auth.dto.LoginResponse;
import com.hostel.management.common.enums.Role;
import com.hostel.management.security.jwt.JwtService;
import com.hostel.management.security.user.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;



@Service
public class AuthService
{
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthService(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    private LoginResponse mapsToLoginResponse(Role role, String token)
    {
        return LoginResponse.builder()
                .role(role)
                .message("Successfully logged in")
                .token(token)
                .build();
    }

    //Earlier login before creating the authentication system with jwt
//    public LoginResponse login(LoginRequest loginRequest)
//    {
//
//        switch(loginRequest.getRole())
//        {
//
//            case ADMIN:
//                throw new UnsupportedOperationException("Not supported yet.");
//
//            case WARDEN:
//                throw new UnsupportedOperationException("Not supported yet.");
//            case STUDENT:
//                Student student = studentRepository.findById(loginRequest.getUsername())
//                        .orElseThrow(()-> new UserNotFoundException("Student not found"));
//                if(student.getAccountStatus()== AccountStatus.PROFILE_PENDING)
//                {
//                    String password = student.getPassword();
//                    if(!passwordEncoder.matches(loginRequest.getPassword(), password))
//                    {
//                        throw new InvalidCredentialsException("Invalid password");
//                    }
//                    String token = jwtService.generateToken(loginRequest.getUsername(),loginRequest.getRole());
//                    return mapsToLoginResponse(loginRequest.getRole(), token);
//
//                }
//                else
//                {
//                    throw new AccountNotActivatedException("For activation account go to warden");
//                }
//        }
//        return new LoginResponse();
//
//    }

    // after creating the jwt authentication the login now
    public LoginResponse login(LoginRequest loginRequest)
    {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword()));

        CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(customUser.getUsername(),customUser.getRole());

        return mapsToLoginResponse(customUser.getRole(),token);
    }
}

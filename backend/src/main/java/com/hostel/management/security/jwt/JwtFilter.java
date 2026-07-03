package com.hostel.management.security.jwt;

import com.hostel.management.auth.exception.UserNotFoundException;
import com.hostel.management.security.service.CustomUserDetailsService;
import com.hostel.management.student.entity.Student;
import com.hostel.management.student.repository.StudentRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter
{
    private final  JwtService jwtService;
    private  final CustomUserDetailsService customUserDetailsService;

    public JwtFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        System.out.println("JwtFilter Executed");
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }
        String jwt = authHeader.substring(7);
        if(SecurityContextHolder.getContext().getAuthentication() != null)
        {
            filterChain.doFilter(request,response);
            return;
        }
        String username = jwtService.extractUsername(jwt);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if(!jwtService.isTokenValid(jwt,userDetails))
        {
            filterChain.doFilter(request,response);
            return;
        }

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
        System.out.println("JwtFilter Executed");
    }
}


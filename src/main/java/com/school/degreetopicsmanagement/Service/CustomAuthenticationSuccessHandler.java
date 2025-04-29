package com.school.degreetopicsmanagement.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.school.degreetopicsmanagement.Model.User;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final EmailService emailService;

    private final UserRepository userRepository;

    @Autowired
    public CustomAuthenticationSuccessHandler(EmailService emailService, UserRepository userRepository ) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = userRepository.findByUsername(authentication.getName());
        String username = authentication.getName(); // Get the username

        System.out.println("Username: " + username);

        if (user.getUseTwoFa() != null && user.getUseTwoFa() == 1) {
            String email = user.getEmail(); // Get user's email
            String code = generateVerificationCode(); // Generate verification code

            System.out.println("verification code: " + code);

            emailService.sendVerificationCode(email, code);

// Store the code in a session attribute or a database
            request.getSession().setAttribute("code", code);

// Retrieve the authenticated user's roles from the SecurityContextHolder
            authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

// Store the roles in the session attribute
            request.getSession().setAttribute("authorities", authorities);

// Create an empty collection of authorities
            Collection<GrantedAuthority> emptyAuthorities = Collections.emptyList();

            // Create a new Authentication object with the same principal and credentials, but with empty authorities
            Authentication newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), emptyAuthorities);

            // Set the new Authentication object back to the security context
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // Redirect to a page where the user can enter the code
            response.sendRedirect("/verifyCode");
        } else {
            response.sendRedirect("/");
        }
    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        System.out.println("this method is reached");
//        User user = userRepository.findByUsername(authentication.getName());
//        System.out.println("User details: " + user);
//        response.sendRedirect("/");
//    }

    private String generateVerificationCode() {
        // Generate a random 6-digit code
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // Generates a random number between 100000 and 999999
        return String.valueOf(code);
    }
}
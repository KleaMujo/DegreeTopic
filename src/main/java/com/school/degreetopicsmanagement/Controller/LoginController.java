package com.school.degreetopicsmanagement.Controller;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DataObjects.UserDTO;
import com.school.degreetopicsmanagement.Model.Role;
import com.school.degreetopicsmanagement.Model.User;
import com.school.degreetopicsmanagement.Repository.AuthorityRepository;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class LoginController {

    private final BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    public LoginController(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login(HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication != null) {
            response.sendRedirect("/");
        }
         return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }


    @PostMapping("/register")
    @ResponseBody
    public void handleRegisterUser(@RequestBody UserDTO userDTO) {
        System.out.println("Po");
        User user = new User();
        user.setUsername(userDTO.getUsernameDTO());
        user.setPassword(passwordEncoder.encode(userDTO.getPasswordDTO()));
        user.setEmail(userDTO.getEmailDTO());
        user.setRole(userDTO.getRoleDTO());
        user.setEnabled(true);
        user.setUseTwoFa(1);
        Role role = new Role();
        role.setUsername(userDTO.getUsernameDTO());
        role.setAuthority("ROLE_USER");
        role.setUser(user);

// Initialize the list and add the authority
        List<Role> authorities = new ArrayList<>();
        authorities.add(role);
        user.setRoles(authorities);

// Save user and authority
        userRepository.save(user);
        authorityRepository.save(role);

    }

    @GetMapping("/verifyCode")
    public ModelAndView verifyCode(ModelAndView modelAndView) {

        modelAndView.setViewName("verifyCode");
        return modelAndView;
    }

    @PostMapping("/verify2fa")
    @ResponseBody
    public boolean verify2fa(@RequestParam(value = "enteredCode") String enteredCode, HttpServletRequest httpServletRequest, Authentication authentication) {

        if (enteredCode.equals(httpServletRequest.getSession().getAttribute("code"))) {

            httpServletRequest.getSession().setAttribute("code", null);

            // Retrieve the authorities from the session
            Collection<? extends GrantedAuthority> authorities = (Collection<? extends GrantedAuthority>) httpServletRequest.getSession().getAttribute("authorities");

            // Create a new Authentication object with the same principal and credentials and old roles
            Authentication newAuth = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);

            // Set the new Authentication object back to the security context
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {

        return "access-denied";
    }

}

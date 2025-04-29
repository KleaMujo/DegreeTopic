package com.school.degreetopicsmanagement.Security;

import com.school.degreetopicsmanagement.Repository.UserRepository;
import com.school.degreetopicsmanagement.Service.CustomAuthenticationSuccessHandler;
import com.school.degreetopicsmanagement.Service.EmailService;
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepository userRepository;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource, BCryptPasswordEncoder passwordEncoder) {
        return new BcryptJdbcUserDetailsManager(dataSource, passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        System.out.println("the security filter chain is ok, so the authenticateTheUser is ok ");
        http

                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                .and()
                .authorizeRequests()
                .antMatchers("/register", "/login", "/home").permitAll()  // Ensure /register and /login are publicly accessible
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/authenticateTheUser")
                .failureHandler(authenticationFailureHandler())
                .successHandler(new CustomAuthenticationSuccessHandler(emailService, userRepository))  // Custom success handler
                .permitAll()
                .and()
                .logout()  // Allow public access to logout
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                .and()
                // Redirect to /403 on access denied

                .csrf().disable()
                .sessionManagement(session -> session
                        .sessionFixation().newSession()
                );;  // Disable CSRF protection (use with caution, only for non-stateful, stateless applications)

        System.out.println("after this we go to CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler with method onAuthenticationSuccess?");

    }


    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}


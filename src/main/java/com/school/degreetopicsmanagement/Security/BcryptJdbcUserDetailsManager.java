package com.school.degreetopicsmanagement.Security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;

public class BcryptJdbcUserDetailsManager extends JdbcUserDetailsManager {

    private final BCryptPasswordEncoder passwordEncoder;

    public BcryptJdbcUserDetailsManager(DataSource dataSource, BCryptPasswordEncoder passwordEncoder) {
        super(dataSource);
        this.passwordEncoder = passwordEncoder;
    }


}

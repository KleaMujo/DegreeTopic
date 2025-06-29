package com.school.degreetopicsmanagement.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.school.degreetopicsmanagement.Model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Long countByRole(String role);

    List<User> findAllByRole (String role);
}

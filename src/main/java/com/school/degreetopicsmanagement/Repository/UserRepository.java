package com.school.degreetopicsmanagement.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.school.degreetopicsmanagement.Model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}

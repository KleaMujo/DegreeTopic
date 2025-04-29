package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Role, Long> {
}

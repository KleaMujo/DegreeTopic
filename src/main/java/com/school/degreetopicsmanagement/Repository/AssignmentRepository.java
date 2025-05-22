package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.Assignment;
import com.school.degreetopicsmanagement.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a WHERE a.teacherId = :userId ")
    List<Assignment> findByTeacherId(@Param("userId") Long userId);

}

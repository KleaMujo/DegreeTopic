package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.Assignment;
import com.school.degreetopicsmanagement.Model.AssignmentAnswer;
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

    @Query("SELECT a FROM Assignment a WHERE a.studentId = :userId ")
    List<Assignment> findAllByStudentId(@Param("userId") Long userId);

    @Query("SELECT a FROM Assignment a WHERE a.studentId = :studentId ORDER BY a.date DESC")
    List<Assignment> findAllByStudentIdOrderByDateDesc(@Param("studentId") Long studentId);


    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.teacherId = :userId")
    long countByTeacherId(@Param("userId") Long userId);


    // Count how many assignments a teacher gave to a specific student
    int countByTeacherIdAndStudentId(Long teacherId, Long studentId);



}

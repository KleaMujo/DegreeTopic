package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.Assignment;
import com.school.degreetopicsmanagement.Model.AssignmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentAnswerRepository extends JpaRepository<AssignmentAnswer,Long> {

    @Query("SELECT a FROM AssignmentAnswer a WHERE a.studentId = :userId ")
    List<AssignmentAnswer> findByStudentId(@Param("userId") Long userId);

    @Query("SELECT a FROM AssignmentAnswer a WHERE a.assignmentId = :assignmentId ")
    AssignmentAnswer findByAssignmendId(@Param("assignmentId") Long assignmentId);

    List<AssignmentAnswer> findByAssignmentIdIn(List<Long> assignmentIds);

    AssignmentAnswer findByAssignmentId(Long assignmentId);

    @Query("SELECT a FROM AssignmentAnswer a WHERE a.assignmentId = :assignmentId ")
    List <AssignmentAnswer> findAllByAssignmentId(@Param("assignmentId") Long assignmentId);


    int countByStudentId(Long studentId);



}


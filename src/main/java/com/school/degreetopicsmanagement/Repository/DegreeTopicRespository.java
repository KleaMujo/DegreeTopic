package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.DegreeTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import com.school.degreetopicsmanagement.Model.*;
@Repository
public interface DegreeTopicRespository extends JpaRepository<DegreeTopic, Long> {

    @Query("SELECT d FROM DegreeTopic d WHERE d.teacher.id = :teacherId")
    List<DegreeTopic> findAllByTeacherId(@Param("teacherId") Long teacherId);

    long countByTeacherAndStudentIsNotNull(User teacher);

    long countByTeacher(User teacher);

    @Query("SELECT dt.title, COUNT(dt.student) FROM DegreeTopic dt WHERE dt.teacher.id = :teacherId GROUP BY dt.title")
    Map<String, Long> countStudentsPerTopic(@Param("teacherId") Long teacherId);

    List<DegreeTopic> findByTeacher(User teacher);

}

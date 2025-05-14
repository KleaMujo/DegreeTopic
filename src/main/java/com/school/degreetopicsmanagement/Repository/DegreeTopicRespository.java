package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.DegreeTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DegreeTopicRespository extends JpaRepository<DegreeTopic, Long> {

    @Query("SELECT d FROM DegreeTopic d WHERE d.teacher.id = :teacherId")
    List<DegreeTopic> findAllByTeacherId(@Param("teacherId") Long teacherId);

}

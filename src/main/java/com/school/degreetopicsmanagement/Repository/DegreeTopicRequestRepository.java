package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DegreeTopicRequestRepository extends JpaRepository<DegreeTopicRequest, Long> {

    List<DegreeTopicRequest> findAllByDegreeTopicIn(List<DegreeTopic> degreeTopics);

    @Query("SELECT d FROM DegreeTopicRequest d WHERE d.student = :studentId AND d.status = :status")
    DegreeTopicRequest findByStudentIdAndStatus(@Param("studentId") User studentId, @Param("status") String status);


    @Query("SELECT r FROM DegreeTopicRequest r WHERE r.status = :status AND r.degreeTopic.teacher.id = :teacherId")
    List<DegreeTopicRequest> findActiveByTeacherId(@Param("status") String status, @Param("teacherId") Long teacherId);

    @Query("SELECT r.degreeTopic.id, COUNT(r) FROM DegreeTopicRequest r WHERE r.status = 'ACTIVE' GROUP BY r.degreeTopic.id")
    List<Object[]> countActiveRequestsByDegreeTopic();


}

package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.Assignment;
import com.school.degreetopicsmanagement.Model.DegreeTopic;
import com.school.degreetopicsmanagement.Model.DegreeTopicRequest;
import com.school.degreetopicsmanagement.Model.User;
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





}

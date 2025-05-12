package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.DegreeTopic;
import com.school.degreetopicsmanagement.Model.DegreeTopicRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DegreeTopicRequestRepository extends JpaRepository<DegreeTopicRequest, Long> {

    List<DegreeTopicRequest> findAllByDegreeTopicIn(List<DegreeTopic> degreeTopics);
}

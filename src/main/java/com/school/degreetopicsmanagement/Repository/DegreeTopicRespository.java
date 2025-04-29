package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.DegreeTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DegreeTopicRespository extends JpaRepository<DegreeTopic, Long> {
}

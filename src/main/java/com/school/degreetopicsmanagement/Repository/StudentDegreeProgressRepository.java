package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.StudentDegreeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDegreeProgressRepository extends JpaRepository<StudentDegreeProgress, Integer> {

}

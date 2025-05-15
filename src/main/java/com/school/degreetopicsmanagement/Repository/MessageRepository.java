package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query("SELECT a FROM Message a WHERE a.user = :userId ")
    Message findByUserId(@Param("userId") Long userId);


}

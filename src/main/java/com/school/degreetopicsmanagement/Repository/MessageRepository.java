package com.school.degreetopicsmanagement.Repository;

import com.school.degreetopicsmanagement.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query("SELECT a FROM Message a WHERE a.messageTo = :userId ")
    List<Message> findAllByUserId(@Param("userId") String userId);

    @Query("SELECT m FROM Message m WHERE m.messageTo = :username ORDER BY m.date DESC")
    List<Message> findAllByMessageTo(@Param("username") String username);


    @Query("SELECT m FROM Message m WHERE m.messageFrom = :username ORDER BY m.date DESC")
    List<Message> findAllByMessageFrom(@Param("username") String username);


}

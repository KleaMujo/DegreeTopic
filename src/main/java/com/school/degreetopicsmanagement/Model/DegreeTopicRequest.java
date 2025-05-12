package com.school.degreetopicsmanagement.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="degreeTopicRequest")
public class DegreeTopicRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "degree_topic_id")
    private DegreeTopic degreeTopic;

    @Column(name="status")
    private String status ;

    @Column(name="requestedAt")
    private LocalDateTime requestedAt ;

 }

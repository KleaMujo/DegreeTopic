package com.school.degreetopicsmanagement.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
@Table(name="assignment_answer")
public class AssignmentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name="student_id")
    private Long studentId;

    @Column(name="date")
    private Date date;

    @Column(name="assignment_title")
    private String assignmentTitle;

    @Lob
    @Column(name="assignment_description", columnDefinition = "TEXT")
    private String assignmentDescription;

    @Column(name="file_name")
    private String fileName;

    @Column(name="assignment_id")
    private Long assignmentId;

}

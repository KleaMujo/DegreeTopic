package com.school.degreetopicsmanagement.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DegreeTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    // Many DegreeTopics are supervised by one teacher (User)
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;


    @OneToOne
    @JoinColumn(name = "student_id", unique = true)
    private User student;

}

package com.school.degreetopicsmanagement.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="teacher_id")
    private Long teacherId;

    @Column(name="student_id")
    private Long studentId;

    @Column(name="points")
    private Integer points;

    @Column(name="date")
    private Date date;

    @Column(name="assignment_title")
    private String assignmentTitle;

    @Column(name="assignment_description", columnDefinition = "TEXT")
    private String assignmentDescription;

    @Column(name="file_name")
    private String fileName;


    @Column(name="link_text")
    private String linkText;

    @Column(name="link_url")
    private String linkUrl;

    @Column(name="degreeTopic")
    private String degreeTopic;

    public String getPropertyPicturesPath(){
        return "/attachments/" + getDegreeTopic() + "/" + this.fileName;
    }




//    @ManyToOne
//    @JoinColumn(name="degree_topic_id")
//    private DegreeTopic degreeTopic;

}

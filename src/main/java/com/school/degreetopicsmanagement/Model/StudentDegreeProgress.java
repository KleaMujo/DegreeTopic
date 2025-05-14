//package com.school.degreetopicsmanagement.Model;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class StudentDegreeProgress {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "student_id")
//    private User student;
//
//    @ManyToOne
//    @JoinColumn(name = "degree_topic_id")
//    private DegreeTopic degreeTopic;
//
//
//    // Status of this part for the student
//    @Column(name = "status")
//    private String status; // e.g., "In Process", "Working", "Completed"
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//}

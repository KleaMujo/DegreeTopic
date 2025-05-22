package com.school.degreetopicsmanagement.DataObjects;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {
        private Long teacherId;
        private Long studentId;
        private Integer points;
        private String assignmentTitle;
        private String assignmentDescription;
        private String fileName;
        private Date date;
        private String linkText;
        private String linkUrl;


}

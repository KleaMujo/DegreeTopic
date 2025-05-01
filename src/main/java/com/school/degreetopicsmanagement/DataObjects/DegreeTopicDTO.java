package com.school.degreetopicsmanagement.DataObjects;

import com.school.degreetopicsmanagement.Model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DegreeTopicDTO {
    private String titleDTO;
    private String descriptionDTO;
    private User student;
}

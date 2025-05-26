package com.school.degreetopicsmanagement.DataObjects;

import com.school.degreetopicsmanagement.Model.DegreeTopic;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessageDTO {

    private String messageDTO;
    private String studentDTO;
    private DegreeTopic degreeTopic; // the topic associated with the student

}

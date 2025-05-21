package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.DataObjects.AssignmentDto;
import com.school.degreetopicsmanagement.DataObjects.MessageDTO;
import com.school.degreetopicsmanagement.Model.*;
import com.school.degreetopicsmanagement.Repository.AssignmentRepository;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRespository;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class AssignmentController {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DegreeTopicRespository degreeTopicRespository;

    @GetMapping(value="/assignment")
    public ModelAndView assignment(ModelAndView modelAndView, HttpServletRequest httpServletRequest) {
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        List<DegreeTopic> degreeTopicList = degreeTopicRespository.findAllByTeacherId(user.getId());
        List<User> studentNames = new ArrayList<>();
        for (DegreeTopic degreeTopic : degreeTopicList) {
            List<DegreeTopicRequest> degreeTopicRequests = degreeTopic.getDegreeTopicRequests();
            for (DegreeTopicRequest degreeTopicRequest : degreeTopicRequests) {
                User std = degreeTopicRequest.getStudent();
                studentNames.add(std);
                modelAndView.addObject("studentNames", studentNames);
            }
        }
        modelAndView.setViewName("Teacher/assignment");
        return modelAndView;
    }

    @PostMapping(value = "/createAssignment")
    @ResponseBody
    public void createAssignment(@RequestBody AssignmentDto assignmentDto  ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        Assignment assignment = new Assignment();
        assignment.setTeacherId(user.getId());
        assignment.setStudentId(assignmentDto.getStudentId());
        assignment.setPoints(assignmentDto.getPoints());
        assignment.setAssignmentTitle(assignmentDto.getAssignmentTitle());
        assignment.setAssignmentDescription(assignmentDto.getAssignmentDescription());
        assignment.setFileName(assignment.getFileName());
        assignment.setDate(new Date());
        assignmentRepository.save(assignment);


    }
}

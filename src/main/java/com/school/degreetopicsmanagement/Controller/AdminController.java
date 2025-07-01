package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.Model.*;
import com.school.degreetopicsmanagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller

public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    DegreeTopicRespository degreeTopicRespository;
    @Autowired
    DegreeTopicRequestRepository degreeTopicRequestRepository;
    @Autowired
    private AssignmentAnswerRepository assignmentAnswerRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;

    @GetMapping(value = "/AllTopics")
    public ModelAndView allTopicsPage( ModelAndView modelAndView) {

        List<DegreeTopic> degreeTopics = degreeTopicRespository.findAll();
        long totalTopics = degreeTopicRespository.count();
        long totalProfessors = userRepository.countByRole("professor");

        modelAndView.addObject("degreeTopics", degreeTopics);
        modelAndView.addObject("totalTopics", totalTopics);
        modelAndView.addObject("totalProfessors", totalProfessors);

        modelAndView.setViewName("Admin/allTopics");
        return modelAndView;
    }

    @GetMapping(value = "/AllRequests")
    public ModelAndView allRequestsPage( ModelAndView modelAndView) {

        List<DegreeTopicRequest> degreeTopicRequests = degreeTopicRequestRepository.findAll();
        long totalRequests = degreeTopicRequestRepository.count();
        long totalStudents = userRepository.countByRole("student");
        int active= 0;
        int pending= 0;

        for(DegreeTopicRequest degreeTopicRequest : degreeTopicRequests) {
            if (degreeTopicRequest.getStatus().equals("ACTIVE") ) {
                active++;
            } else {
                pending++;
            }

        }
        modelAndView.addObject("degreeTopicRequests", degreeTopicRequests);
        modelAndView.addObject("totalRequests", totalRequests);
        modelAndView.addObject("totalStudents", totalStudents);
        modelAndView.addObject("active", active);
        modelAndView.addObject("pending", pending);

        modelAndView.setViewName("Admin/AllRequests");

        return modelAndView;
    }

    @GetMapping(value = "/listOfTopicDegrees")
    public ModelAndView listOfTopicDegrees( ModelAndView modelAndView) {

        List<DegreeTopicRequest> degreeTopicRequests = degreeTopicRequestRepository.findAll();
        long totalRequests = degreeTopicRequestRepository.count();
        long totalStudents = userRepository.countByRole("student");




        modelAndView.addObject("degreeTopicRequests", degreeTopicRequests);
        modelAndView.addObject("totalRequests", totalRequests);
        modelAndView.addObject("totalStudents", totalStudents);

        Integer currentYear = LocalDate.now().getYear();
        modelAndView.addObject("year",currentYear);
        modelAndView.setViewName("Admin/listOfTopicDegrees");

        return modelAndView;
    }

    @GetMapping("/assignments")
    public ModelAndView assignmentsPage( ModelAndView modelAndView) {
        List <Assignment> assignment = assignmentRepository.findAll();

        modelAndView.addObject("assignments", assignment);
        modelAndView.setViewName("Admin/assignments");
        return modelAndView;


    }

    @GetMapping("/degreeTopic")
    public ModelAndView degreeTopicPage(@RequestParam(value = "id") Long id, ModelAndView modelAndView) {
        DegreeTopic degreeTopic = degreeTopicRespository.findById(id).get();
        modelAndView.addObject("degreeTopic", degreeTopic);

        List<Assignment> assignments = assignmentRepository.findAllByDegreeId(degreeTopic.getTitle());

        // Map Assignment -> AssignmentAnswer
        Map<Assignment, AssignmentAnswer> assignmentAnswerMap = new LinkedHashMap<>();
        for (Assignment a : assignments) {
            AssignmentAnswer answer = assignmentAnswerRepository.findByAssignmentId(a.getId());
            String degreeTopicFolderName = a.getDegreeTopic();
            String getPropertyPicturesPath =  "/attachments/" + degreeTopicFolderName+ "/" + answer.getFileName();
            assignmentAnswerMap.put(a, answer); // answer can be null, which is fine
            modelAndView.addObject("getPropertyPicturesPath", getPropertyPicturesPath);

        }

        modelAndView.addObject("assignmentAnswerMap", assignmentAnswerMap);
        modelAndView.setViewName("Admin/degreeTopicAssignment");
        return modelAndView;
    }

}

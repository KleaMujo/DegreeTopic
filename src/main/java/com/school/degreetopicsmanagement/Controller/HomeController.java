package com.school.degreetopicsmanagement.Controller;

import javax.servlet.http.HttpServletRequest;

import com.school.degreetopicsmanagement.Model.DegreeTopic;
import com.school.degreetopicsmanagement.Model.DegreeTopicRequest;
import com.school.degreetopicsmanagement.Model.User;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRequestRepository;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRespository;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    DegreeTopicRespository degreeTopicRespository;
    @Autowired
    DegreeTopicRequestRepository degreeTopicRequestRepository;
    public HomeController(UserRepository userRepository, DegreeTopicRespository degreeTopicRespository) {
        this.userRepository = userRepository;
        this.degreeTopicRespository = degreeTopicRespository;
    }

    @GetMapping("/")
    public String homePage(HttpServletRequest httpServletRequest) {

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(authenticatedUser.getUsername());
            if ("student".equals(user.getRole())) {

                return "/Student/studentDashboard";
            } else if ("professor".equals(user.getRole())) {

                return "/Teacher/professorDashboard";
            }

        }
        return "login";

    }

    @GetMapping(value = "/sidebarStudent")
    public ModelAndView sidebarStudent(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        modelAndView.setViewName("Student/sidebarStudent");
        return modelAndView;
    }

    @GetMapping(value = "/studentList")
    public ModelAndView studentList(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            if (user.getRole().equals("student")) {
                modelAndView.addObject("student", user);
            }
        }
        List<DegreeTopic> degreeTopics = degreeTopicRespository.findAll();
        modelAndView.addObject("degreeTopics", degreeTopics);
        modelAndView.setViewName("studentList");
        return modelAndView;
    }

    @GetMapping(value = "/topicsStudent")
    public ModelAndView topicsStudent(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        modelAndView.addObject("studentId", user.getId());
        List<DegreeTopic> degreeTopics = degreeTopicRespository.findAll();
        modelAndView.addObject("degreeTopics", degreeTopics);

        modelAndView.setViewName("Student/degreeListStudent");
        return modelAndView;
    }


    @GetMapping(value = "/showAllStudentsChosenTopic")
    public ModelAndView showAllStudentsChosenTopic(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        //id e teacherit t loguar
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());
        System.out.println("id e teacher te log " + user.getId());

        List<DegreeTopic> degreeTopicList = user.getDegreeTopics();
        List<DegreeTopicRequest> degreeTopicRequests = degreeTopicRequestRepository.findAllByDegreeTopicIn(degreeTopicList);
        modelAndView.addObject("degreeTopicRequests", degreeTopicRequests);

        modelAndView.setViewName("Teacher/showAllStudentsChosenTopic");
        return modelAndView;

    }
}


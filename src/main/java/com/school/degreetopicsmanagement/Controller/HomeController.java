package com.school.degreetopicsmanagement.Controller;

import javax.servlet.http.HttpServletRequest;

import com.school.degreetopicsmanagement.Model.DegreeTopic;
import com.school.degreetopicsmanagement.Model.User;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRespository;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final DegreeTopicRespository degreeTopicRespository;

    public HomeController(UserRepository userRepository, DegreeTopicRespository degreeTopicRespository) {
        this.userRepository = userRepository;
        this.degreeTopicRespository = degreeTopicRespository;
    }

    @GetMapping("/")
    public String homePage(HttpServletRequest httpServletRequest) {

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            return "home";
        } else {
            return "login";
        }
    }

    @GetMapping(value = "/studentList")
    public ModelAndView studentList(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        List<User> userList = userRepository.findAll();
         for(User user : userList) {
             if(user.getRole().equals("student")) {
                 modelAndView.addObject("student", user);
             }
         }
          List<DegreeTopic> degreeTopics = degreeTopicRespository.findAll();
          modelAndView.addObject("degreeTopics", degreeTopics);
         modelAndView.setViewName("studentList");
        return modelAndView;
    }

    @GetMapping(value="/topicsStudent")
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





}


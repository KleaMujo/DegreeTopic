package com.school.degreetopicsmanagement.Controller;

import javax.servlet.http.HttpServletRequest;

import com.school.degreetopicsmanagement.DataObjects.DegreeRequestDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ModelAndView homePage(HttpServletRequest httpServletRequest) {

        ModelAndView modelAndView = new ModelAndView();

        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(authenticatedUser.getUsername());

            if ("student".equals(user.getRole())) {
                modelAndView.setViewName("/Student/studentDashboard");
                return modelAndView;
            } else if ("professor".equals(user.getRole())) {
                List<DegreeTopic> degreeTopicList = degreeTopicRespository.findAllByTeacherId(user.getId());

                List<DegreeRequestDTO> requestsList = new ArrayList<>();

                for (DegreeTopic degreeTopic : degreeTopicList) {
                    List<DegreeTopicRequest> degreeTopicRequests = degreeTopic.getDegreeTopicRequests();
                    System.out.println(degreeTopicRequests.size() + " size");

                    for (DegreeTopicRequest degreeTopicRequest : degreeTopicRequests) {
                        String studentName = degreeTopicRequest.getStudent().getUsername();
                        String degreeTitle = degreeTopic.getTitle();
                        String status = degreeTopicRequest.getStatus();
                        System.out.println(studentName + " std username -> " + degreeTitle);

                        requestsList.add(new DegreeRequestDTO(studentName, degreeTitle,status));
                    }
                }

                modelAndView.addObject("requests", requestsList);
            }

                modelAndView.setViewName("/Teacher/professorDashboard");
                return modelAndView;
            }


        modelAndView.setViewName("login");
        return modelAndView;
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
        List<DegreeTopic> allTopics = degreeTopicRespository.findAll();
        List<DegreeTopic> filteredTopics = allTopics.stream()
                .filter(topic -> topic.getDegreeTopicRequests().stream()
                        .noneMatch(request -> "CHOOSEN".equalsIgnoreCase(request.getStatus())))
                .collect(Collectors.toList());


        modelAndView.addObject("studentId", user.getId());
        modelAndView.addObject("degreeTopics", filteredTopics);
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

        User user1 = userRepository.findById(user.getId()).get();
        modelAndView.addObject("useri", user1.getUsername());
        List<DegreeTopic> degreeTopicList = user.getDegreeTopics();
        List<DegreeTopicRequest> degreeTopicRequests = degreeTopicRequestRepository.findAllByDegreeTopicIn(degreeTopicList);
        modelAndView.addObject("degreeTopicRequests", degreeTopicRequests);

        modelAndView.setViewName("Teacher/showAllStudentsChosenTopic");
        return modelAndView;

    }
}


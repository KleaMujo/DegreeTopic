package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.DataObjects.DegreeRequestDTO;
import com.school.degreetopicsmanagement.DataObjects.DegreeTopicDTO;
import com.school.degreetopicsmanagement.DataObjects.MessageDTO;
import com.school.degreetopicsmanagement.Model.DegreeTopic;
import com.school.degreetopicsmanagement.Model.DegreeTopicRequest;
import com.school.degreetopicsmanagement.Model.Message;
import com.school.degreetopicsmanagement.Model.User;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRequestRepository;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRespository;
import com.school.degreetopicsmanagement.Repository.MessageRepository;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DegreeTopicController {

 @Autowired
 DegreeTopicRespository degreeTopicRespository;

 @Autowired
 UserRepository userRepository;

 @Autowired
 DegreeTopicRequestRepository degreeTopicRequestRepository;

    @GetMapping(value="/addDegreeTopic")
    public ModelAndView  addDegreeTopic(ModelAndView modelAndView, HttpServletRequest httpServletRequest){
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

        modelAndView.setViewName("Teacher/addDegreeTopic");
     return modelAndView;
    }

    @PostMapping(value="/addDegreeTopic")
    @ResponseBody
    public void addDegreeTopic(@RequestBody DegreeTopicDTO degreeTopicDTO){

        DegreeTopic degreeTopic = new DegreeTopic();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());
        System.out.println(user.getRole() + " role - "  + user.getId());
        degreeTopic.setTitle(degreeTopicDTO.getTitleDTO());
        degreeTopic.setDescription(degreeTopicDTO.getDescriptionDTO());
        degreeTopic.setTeacher(user);
         degreeTopicRespository.save(degreeTopic);
    }

    @PostMapping(value="/addStatusActive")
    @ResponseBody
    public void addStatusActive(@RequestParam(value="id")Long id){
        DegreeTopicRequest degreeTopicRequest = degreeTopicRequestRepository.findById(id).get();
        System.out.println(degreeTopicRequest.getId() + " id");
        degreeTopicRequest.setStatus("ACTIVE");
        degreeTopicRequestRepository.save(degreeTopicRequest);
    }


    @GetMapping(value="/showAllDegreeTopic")
    public ModelAndView  showAllDegreeTopic(ModelAndView modelAndView){
        List<DegreeTopic> degreeTopics = degreeTopicRespository.findAll();
         modelAndView.addObject("degreeTopics", degreeTopics);
         modelAndView.setViewName("degreeTopicList");
         return modelAndView;
    }

    @PostMapping(value = "/addStudentToDegree")
    @ResponseBody
    public void addStudentToDegree(@RequestParam(value = "id") Long id) {
        DegreeTopic degreeTopic = degreeTopicRespository.findById(id).orElseThrow();
        System.out.println(degreeTopic.getId() + " d");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        DegreeTopicRequest request = new DegreeTopicRequest();
        request.setDegreeTopic(degreeTopic);
        request.setStudent(user);
        request.setStatus("PENDING");
        request.setRequestedAt(LocalDateTime.now());

        degreeTopicRequestRepository.save(request);

    }

    @GetMapping(value = "viewNotificationsProfessor")
    public ModelAndView  viewNotificationsProfessor(ModelAndView modelAndView,HttpServletRequest httpServletRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

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
        modelAndView.setViewName("/Teacher/viewNotificationsProfessor");

        return modelAndView;
    }

    @Autowired
    MessageRepository messageRepository;

    @PostMapping(value = "/addMessage")
    @ResponseBody
    public void addMessage(@RequestBody MessageDTO messageDTO ) {

        Message message = new Message();
        message.setMessage(messageDTO.getMessageDTO());
        message.setUser(messageDTO.getUserDTO());
        messageRepository.save(message);

    }

    @PostMapping(value = "/editMessage")
    @ResponseBody
    public void editMessage(@RequestBody MessageDTO messageDTO ,@RequestParam(value = "id") Long id ) {

        Message message = messageRepository.findByUserId(id);
        message.setMessage(messageDTO.getMessageDTO());
        message.setUser(messageDTO.getUserDTO());
        messageRepository.save(message);

    }

    @GetMapping(value="/createParts")
    public ModelAndView  topicPart(ModelAndView modelAndView,HttpServletRequest httpServletRequest){
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());
        modelAndView.setViewName("Teacher/createParts");
        return modelAndView;
    }


}

package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.DataObjects.DegreeTopicDTO;
import com.school.degreetopicsmanagement.Model.DegreeTopic;
import com.school.degreetopicsmanagement.Model.User;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRespository;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class DegreeTopicController {

 @Autowired
 DegreeTopicRespository degreeTopicRespository;

 @Autowired
 UserRepository userRepository;


    @GetMapping(value="/addDegreeTopic")
    public ModelAndView  addDegreeTopic(ModelAndView modelAndView){
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

    @GetMapping(value="/showAllDegreeTopic")
    public ModelAndView  showAllDegreeTopic(ModelAndView modelAndView){
          List<DegreeTopic> degreeTopics = degreeTopicRespository.findAll();
         modelAndView.addObject("degreeTopics", degreeTopics);
         modelAndView.setViewName("degreeTopicList");
         return modelAndView;
    }


}

package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.DataObjects.DegreeRequestDTO;
import com.school.degreetopicsmanagement.DataObjects.DegreeTopicDTO;
import com.school.degreetopicsmanagement.DataObjects.MessageDTO;
import com.school.degreetopicsmanagement.Model.*;
import com.school.degreetopicsmanagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DegreeTopicController {

    @Autowired
    DegreeTopicRespository degreeTopicRespository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DegreeTopicRequestRepository degreeTopicRequestRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentAnswerRepository assignmentAnswerRepository;

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
    public ModelAndView viewNotificationsProfessor(ModelAndView modelAndView, HttpServletRequest httpServletRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

        List<DegreeTopic> degreeTopicList = degreeTopicRespository.findAllByTeacherId(user.getId());
        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (DegreeTopic degreeTopic : degreeTopicList) {
            List<DegreeTopicRequest> degreeTopicRequests = degreeTopic.getDegreeTopicRequests();
            for (DegreeTopicRequest degreeTopicRequest : degreeTopicRequests) {
                User student = degreeTopicRequest.getStudent();
                DegreeTopic degreeTopic1 = degreeTopicRequest.getDegreeTopic();

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setStudentDTO(student.getUsername());
                messageDTO.setDegreeTopic(degreeTopic1);
                messageDTO.setStudentDTO(student.getUsername());
                messageDTOList.add(messageDTO);
            }
        }

        modelAndView.addObject("messageDTOList", messageDTOList);
        modelAndView.setViewName("/Teacher/viewNotificationsProfessor");

        return modelAndView;
    }


    @Autowired
    MessageRepository messageRepository;

    @PostMapping(value = "/addMessage")
    @ResponseBody
    public void addMessage(@RequestBody MessageDTO messageDTO ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        System.out.println(messageDTO.getStudentDTO()  + " s");
        Message message = new Message();
        message.setMessage(messageDTO.getMessageDTO());
        message.setUser(messageDTO.getStudentDTO());
        message.setDate(new Date());
        message.setTeacherName(user.getUsername());

        messageRepository.save(message);

    }

    @PostMapping(value = "/editMessage")
    @ResponseBody
    public void editMessage(@RequestBody MessageDTO messageDTO ,@RequestParam(value = "id") Long id ) {

        Message message = messageRepository.findByUserId(id);
        message.setMessage(messageDTO.getMessageDTO());
        message.setUser(messageDTO.getStudentDTO());
        messageRepository.save(message);

    }

    @GetMapping(value="/createParts")
    public ModelAndView  topicPart(ModelAndView modelAndView,HttpServletRequest httpServletRequest){
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
            modelAndView.addObject("user",user.getUsername());
        }

        Map<String, List<Assignment>> assignmentsByDegree = new HashMap<>();

        List<Assignment> assignments = assignmentRepository.findByTeacherId(user.getId());

        Map<Long, AssignmentAnswer> assignmentAnswers = new HashMap<>();

        for (Assignment assignment : assignments) {
            Long studentId = assignment.getStudentId();
            User user1 = userRepository.findById(studentId).get();
            DegreeTopicRequest request = degreeTopicRequestRepository.findByStudentIdAndStatus(user1, "ACTIVE");

            if (request != null) {
                DegreeTopic degree = request.getDegreeTopic();
                assignmentsByDegree.computeIfAbsent(degree.getTitle(), k -> new ArrayList<>()).add(assignment);
            }

            AssignmentAnswer assignmentAnswer = assignmentAnswerRepository.findByAssignmendId(assignment.getId());
            if (assignmentAnswer != null) {
                assignmentAnswers.put(assignment.getId(), assignmentAnswer);
            }
        }

        modelAndView.addObject("assignmentsByDegree", assignmentsByDegree);
        modelAndView.addObject("assignmentAnswers", assignmentAnswers);





        modelAndView.setViewName("Teacher/createParts");
        return modelAndView;
    }


    @GetMapping(value="editDegreeTopic")
    public ModelAndView  editDegreeTopic(@RequestParam(value="id")Long degreeTopicId, ModelAndView modelAndView,HttpServletRequest httpServletRequest){
            DegreeTopic degreeTopic = degreeTopicRespository.findById(degreeTopicId).get();
            modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());
            modelAndView.addObject("degreeTopic", degreeTopic);
            modelAndView.setViewName("/Teacher/editDegreeTopic");
            return modelAndView;
    }


    @PostMapping(value = "/deleteDegreeTopic")
    public String  deleteDegreeTopic(@RequestParam(value = "id") Long degreeTopicId) {
        System.out.println(" DegreeTopic me id " + degreeTopicId + " u fshi ");
        degreeTopicRespository.deleteById(degreeTopicId);
        return "success";

    }


}

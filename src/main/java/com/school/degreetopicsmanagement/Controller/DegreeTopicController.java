package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.DataObjects.DegreeRequestDTO;
import com.school.degreetopicsmanagement.DataObjects.DegreeTopicDTO;
import com.school.degreetopicsmanagement.DataObjects.MessageDTO;
import com.school.degreetopicsmanagement.Model.*;
import com.school.degreetopicsmanagement.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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


    @GetMapping("/dashboard")
    public ModelAndView dashboard(ModelAndView modelAndView, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User currentUser = userRepository.findByUsername(authenticatedUser.getUsername());

        long totalTopics = degreeTopicRespository.countByTeacher(currentUser);

        long totalAssignments = assignmentRepository.countByTeacherId(currentUser.getId());
        System.out.println("po");

        List<DegreeTopic> degreeTopics = degreeTopicRespository.findAllByTeacher(currentUser);

        List<Assignment> assignments = assignmentRepository.findByTeacherId(currentUser.getId());

        List<AssignmentAnswer> answers = assignmentAnswerRepository.findByAssignmentIdIn(
                assignments.stream().map(Assignment::getId).collect(Collectors.toList())
        );

        Map<Long, AssignmentAnswer> assignmentAnswersMap = answers.stream()
                .collect(Collectors.toMap(AssignmentAnswer::getAssignmentId, Function.identity()));

        modelAndView.addObject("degreeTopics", degreeTopics);
        modelAndView.addObject("assignments", assignments);
        modelAndView.addObject("assignmentAnswers", assignmentAnswersMap);

        modelAndView.addObject("totalTopics", totalTopics);
         modelAndView.addObject("totalAssignments", totalAssignments);

        modelAndView.addObject("currentPath", request.getRequestURI());




        modelAndView.setViewName("Teacher/dashboard");
        return modelAndView;
    }


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

        // Fetch messages where professor is sender or recipient
        List<Message> messageTo = messageRepository.findAllByMessageTo(user.getUsername());
        List<Message> messageFrom = messageRepository.findAllByMessageFrom(user.getUsername());

        // Combine all messages
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(messageTo);
        allMessages.addAll(messageFrom);

        Map<String, List<Message>> groupedMessages = allMessages.stream()
                .filter(msg -> msg.getMessageFrom() != null && msg.getMessageTo() != null) // Filter out invalid messages
                .sorted(Comparator.comparing(Message::getDate).reversed())
                .collect(Collectors.groupingBy(msg ->
                                msg.getMessageFrom().equals(user.getUsername())
                                        ? msg.getMessageTo()
                                        : msg.getMessageFrom(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));


        List<DegreeTopic> degreeTopicList = degreeTopicRespository.findAllByTeacherId(user.getId());
        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (DegreeTopic degreeTopic : degreeTopicList) {
            List<DegreeTopicRequest> degreeTopicRequests = degreeTopic.getDegreeTopicRequests();
            for (DegreeTopicRequest degreeTopicRequest : degreeTopicRequests) {
                User student = degreeTopicRequest.getStudent();
                DegreeTopic degreeTopic1 = degreeTopicRequest.getDegreeTopic();

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setMessageToDTO(student.getUsername());
                messageDTO.setDegreeTopic(degreeTopic1);
                 messageDTOList.add(messageDTO);
            }
        }

        modelAndView.addObject("messageDTOList", messageDTOList);

        // Pass grouped messages to the view
        modelAndView.addObject("groupedMessages", groupedMessages);
        modelAndView.setViewName("/Teacher/viewNotificationsProfessor");

        return modelAndView;
    }



    @Autowired
    MessageRepository messageRepository;

    @PostMapping(value = "/addMessage")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addMessage(@RequestBody MessageDTO messageDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        Message message = new Message();
        message.setMessage(messageDTO.getMessageDTO());
        message.setMessageTo(messageDTO.getMessageToDTO());
        message.setDate(new Date());
        message.setMessageFrom(user.getUsername());

        messageRepository.save(message);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Message saved successfully");
        return ResponseEntity.ok(response);
    }

//    @PostMapping(value = "/editMessage")
//    @ResponseBody
//    public void editMessage(@RequestBody MessageDTO messageDTO ,@RequestParam(value = "id") Long id ) {
//
//        Message message = messageRepository.findByUserId(id);
//        message.setMessage(messageDTO.getMessageDTO());
//        message.setMessageTo(messageDTO.getMessageToDTO());
//        messageRepository.save(message);
//
//    }

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





    @PostMapping("/deleteDegreeTopic")
    public ResponseEntity<String> deleteDegreeTopic(@RequestParam("id") Long degreeTopicId) {
        if (!degreeTopicRespository.existsById(degreeTopicId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("DegreeTopic me id " + degreeTopicId + " nuk ekziston.");
        }

        degreeTopicRespository.deleteById(degreeTopicId);
        return ResponseEntity.ok("DegreeTopic me id " + degreeTopicId + " u fshi me sukses.");
    }



    @GetMapping(value="notifications")
    public ModelAndView  notifications(  ModelAndView modelAndView, HttpServletRequest httpServletRequest ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

        // Fetch messages where professor is sender or recipient
        List<Message> messageTo = messageRepository.findAllByMessageTo(user.getUsername());
        List<Message> messageFrom = messageRepository.findAllByMessageFrom(user.getUsername());

        // Combine all messages
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(messageTo);
        allMessages.addAll(messageFrom);

        Map<String, List<Message>> groupedMessages = allMessages.stream()
                .filter(msg -> msg.getMessageFrom() != null && msg.getMessageTo() != null) // Filter out invalid messages
                .sorted(Comparator.comparing(Message::getDate).reversed())
                .collect(Collectors.groupingBy(msg ->
                                msg.getMessageFrom().equals(user.getUsername())
                                        ? msg.getMessageTo()
                                        : msg.getMessageFrom(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));


        List<DegreeTopic> degreeTopicList = degreeTopicRespository.findAllByTeacherId(user.getId());
        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (DegreeTopic degreeTopic : degreeTopicList) {
            List<DegreeTopicRequest> degreeTopicRequests = degreeTopic.getDegreeTopicRequests();
            for (DegreeTopicRequest degreeTopicRequest : degreeTopicRequests) {
                User student = degreeTopicRequest.getStudent();
                DegreeTopic degreeTopic1 = degreeTopicRequest.getDegreeTopic();

                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setMessageToDTO(student.getUsername());
                messageDTO.setDegreeTopic(degreeTopic1);
                messageDTOList.add(messageDTO);
            }
        }

        modelAndView.addObject("messageDTOList", messageDTOList);
        List <DegreeTopic> degreeTopic = degreeTopicRespository.findAll();

        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());
        modelAndView.addObject("user", user);
        modelAndView.addObject("degreeTopic", degreeTopic);
        // Pass grouped messages to the view
        modelAndView.addObject("groupedMessages", groupedMessages);

        modelAndView.setViewName("/Student/notifications");
        return modelAndView;
    }


}

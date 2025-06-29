package com.school.degreetopicsmanagement.Controller;

import javax.servlet.http.HttpServletRequest;

import com.school.degreetopicsmanagement.DataObjects.DegreeRequestDTO;
import com.school.degreetopicsmanagement.DataObjects.StudentAssignmentSummary;
import com.school.degreetopicsmanagement.DataObjects.TopicFillSummary;
 import com.school.degreetopicsmanagement.Model.*;
import com.school.degreetopicsmanagement.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

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

    public HomeController(UserRepository userRepository, DegreeTopicRespository degreeTopicRespository) {
        this.userRepository = userRepository;
        this.degreeTopicRespository = degreeTopicRespository;
    }

    @Autowired
    private AssignmentRepository assignmentRepository;

    @GetMapping("/")
    public ModelAndView homePage(HttpServletRequest httpServletRequest) {

        ModelAndView modelAndView = new ModelAndView();


        if (httpServletRequest.isUserInRole("ROLE_USER")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
                modelAndView.setViewName("login");
                return modelAndView;
            }

            UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(authenticatedUser.getUsername());

            if (user == null || user.getRole() == null) {
                modelAndView.setViewName("login");
                return modelAndView;
            }

            System.out.println("userii " + user.getRole());

            if ("student".equals(user.getRole())) {
                DegreeTopicRequest degreeTopicRequest = degreeTopicRequestRepository.findByStudentIdAndStatus(user, "ACTIVE");

                DegreeTopic topic = (degreeTopicRequest != null) ? degreeTopicRequest.getDegreeTopic() : null;
                Long studentId = user.getId();

                if (topic == null) {
                    modelAndView.addObject("noTopicMessage", "You have not chosen any degree topic yet.");
                } else {
                    String topicTitle = topic.getTitle();

                    List<Assignment> allAssignments = Optional.ofNullable(assignmentRepository.findAllByStudentId(studentId)).orElse(Collections.emptyList());
                    int totalAssignments = allAssignments.size();

                    List<AssignmentAnswer> submittedAnswers = Optional.ofNullable(assignmentAnswerRepository.findByStudentId(studentId)).orElse(Collections.emptyList());
                    int submittedCount = submittedAnswers.size();

                    List<Message> messages = (topic.getTeacher() != null)
                            ? Optional.ofNullable(messageRepository.findAllByMessageFrom(topic.getTeacher().getUsername())).orElse(Collections.emptyList())
                            : Collections.emptyList();
                    int totalMessages = messages.size();

                    int progress = totalAssignments > 0 ? (submittedCount * 100) / totalAssignments : 0;

                    modelAndView.addObject("degreeTopic", topic);
                    modelAndView.addObject("totalAssignments", totalAssignments);
                    modelAndView.addObject("submittedAssignments", submittedCount);
                    modelAndView.addObject("assignmentAnswers", submittedAnswers);
                    modelAndView.addObject("totalMessages", totalMessages);
                    modelAndView.addObject("progressPercent", progress);
                }

                modelAndView.setViewName("/Student/studentDashboard");
                return modelAndView;

            } else if ("professor".equals(user.getRole())) {

                long assignedStudents = 0L;
                long totalTopics = degreeTopicRespository.countByTeacher(user);

                List<Assignment> assignments = assignmentRepository.findByTeacherId(user.getId());
                if (assignments != null) {
                    for (Assignment a : assignments) {
                        if (a != null) {
                            List<AssignmentAnswer> assignmentAnswer = assignmentAnswerRepository.findAllByAssignmentId(a.getId());
                            if (assignmentAnswer != null) {
                                assignedStudents += assignmentAnswer.size();
                            }
                        }
                    }
                }

                long totalAssignments = assignmentRepository.countByTeacherId(user.getId());
                modelAndView.addObject("totalTopics", totalTopics);
                modelAndView.addObject("assignedStudents", assignedStudents);
                modelAndView.addObject("totalAssignments", totalAssignments);

                List<DegreeTopicRequest> requests = degreeTopicRequestRepository.findActiveByTeacherId("active", user.getId());
                if (requests == null) requests = new ArrayList<>();

                Map<Long, StudentAssignmentSummary> summaryMap = new HashMap<>();
                for (DegreeTopicRequest req : requests) {
                    if (req != null && req.getDegreeTopic() != null && req.getStudent() != null &&
                            req.getDegreeTopic().getTeacher() != null &&
                            user.getId().equals(req.getDegreeTopic().getTeacher().getId())) {

                        Long studentId = req.getStudent().getId();
                        String studentName = req.getStudent().getUsername();

                        int totalAssignments1 = assignmentRepository.countByTeacherIdAndStudentId(user.getId(), studentId);
                        int totalResponses = assignmentAnswerRepository.countByStudentId(studentId);

                        summaryMap.put(studentId, new StudentAssignmentSummary(studentId, studentName, totalAssignments1, totalResponses));
                    }
                }
                modelAndView.addObject("summaryMap", summaryMap);

                // Active Count Map
                Map<Long, Integer> activeCountMap = new HashMap<>();
                for (DegreeTopicRequest request : requests) {
                    if (request != null && request.getDegreeTopic() != null) {
                        Long topicId = request.getDegreeTopic().getId();
                        activeCountMap.put(topicId, activeCountMap.getOrDefault(topicId, 0) + 1);
                    }
                }

                List<TopicFillSummary> topicSummaries = new ArrayList<>();
                List<DegreeTopic> degreeTopicList = degreeTopicRespository.findAllByTeacherId(user.getId());
                if (degreeTopicList != null) {
                    for (DegreeTopic topic : degreeTopicList) {
                        if (topic != null) {
                            int active = activeCountMap.getOrDefault(topic.getId(), 0);
                            topicSummaries.add(new TopicFillSummary(topic.getTitle(), active));
                        }
                    }
                }
                modelAndView.addObject("topicSummaries", topicSummaries);

                // Interest Count Map
                Map<Long, Integer> interestCountMap = new HashMap<>();
                for (DegreeTopicRequest req : requests) {
                    if (req != null && req.getDegreeTopic() != null) {
                        Long topicId = req.getDegreeTopic().getId();
                        interestCountMap.put(topicId, interestCountMap.getOrDefault(topicId, 0) + 1);
                    }
                }

                List<TopicFillSummary> interestSummaries = new ArrayList<>();
                List<DegreeTopic> degreeTopics = degreeTopicRespository.findAllByTeacherId(user.getId());
                if (degreeTopics != null) {
                    for (DegreeTopic topic : degreeTopics) {
                        if (topic != null) {
                            int count = interestCountMap.getOrDefault(topic.getId(), 0);
                            interestSummaries.add(new TopicFillSummary(topic.getTitle(), count));
                        }
                    }
                }

                modelAndView.addObject("interestSummaries", interestSummaries);
                modelAndView.setViewName("/Teacher/professorDashboard");
                return modelAndView;
            } else if ("admin".equals(user.getRole())) {
                System.out.println("aaa");
                List<DegreeTopic> degreeTopics = degreeTopicRespository.findAll();

                List<DegreeTopicRequest> degreeTopicRequests = degreeTopicRequestRepository.findAllByDegreeTopicIn(degreeTopics);

                long totalTopics = degreeTopicRespository.count();
                long totalRequests = degreeTopicRequestRepository.count();
                long totalStudents = userRepository.countByRole("student");
                long totalProfessors = userRepository.countByRole("professor");

                modelAndView.addObject("totalTopics", totalTopics);
                modelAndView.addObject("totalRequests", totalRequests);
                modelAndView.addObject("totalStudents", totalStudents);
                modelAndView.addObject("totalProfessors", totalProfessors);

                List<User> studentUsers =  userRepository.findAllByRole("student");
                List<User> professorsUsers =  userRepository.findAllByRole("professor");

                Integer currentYear = LocalDate.now().getYear();
                modelAndView.addObject("year",currentYear);
                modelAndView.addObject("studentUsers", studentUsers);
                modelAndView.addObject("professorsUsers", professorsUsers);
                modelAndView.addObject("degreeTopics", degreeTopics);
                modelAndView.addObject("degreeTopicRequests", degreeTopicRequests);

                modelAndView.setViewName("/Admin/adminDashboard");
            }
            System.out.println("User has unknown role: " + user.getRole());
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
                        .noneMatch(request -> "ACTIVE".equalsIgnoreCase(request.getStatus())))
                .collect(Collectors.toList());
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

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

    @GetMapping(value = "/myDegrees")
    public ModelAndView myDegrees(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        //id e teacherit t loguar
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());
        System.out.println("id e teacher te log " + user.getId());

        User user1 = userRepository.findById(user.getId()).get();
        modelAndView.addObject("useri", user1.getUsername());
        List<DegreeTopic> degreeTopicList = user.getDegreeTopics();
        modelAndView.addObject("degreeTopicList", degreeTopicList);

        modelAndView.setViewName("Teacher/myDegrees");
        return modelAndView;

    }


    @GetMapping(value = "/viewNotificationsStudent")
    public ModelAndView viewNotificationsStudent(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        modelAndView.addObject("studentId", user.getId());

        modelAndView.setViewName("Student/viewNotificationsStudent");
        return modelAndView;
    }


}


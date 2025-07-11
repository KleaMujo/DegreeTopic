package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.DataObjects.AssignmentDto;
import com.school.degreetopicsmanagement.Model.*;
import com.school.degreetopicsmanagement.Repository.*;
import io.github.classgraph.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;



@Controller
public class AssignmentController {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DegreeTopicRespository degreeTopicRespository;

    @Autowired
    DegreeTopicRequestRepository degreeTopicRequestRepository;

    @GetMapping(value="/assignment")
    public ModelAndView assignment(ModelAndView modelAndView, HttpServletRequest httpServletRequest) {
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        List<DegreeTopic> degreeTopicList = degreeTopicRespository.findAllByTeacherId(user.getId());
        Map<User, String> studentDegreeMap = new HashMap<>();

        for (DegreeTopic degreeTopic : degreeTopicList) {
            List<DegreeTopicRequest> degreeTopicRequests = degreeTopic.getDegreeTopicRequests();
            for (DegreeTopicRequest degreeTopicRequest : degreeTopicRequests) {
                if ("ACTIVE".equals(degreeTopicRequest.getStatus())) {
                    User student = degreeTopicRequest.getStudent();
                    DegreeTopicRequest request = degreeTopicRequestRepository.findByStudentIdAndStatus(student, "ACTIVE");

                     if (request != null) {
                        studentDegreeMap.put(student, request.getDegreeTopic().getTitle());
                    }
                }
            }
        }

        modelAndView.addObject("studentDegreeMap", studentDegreeMap);

        modelAndView.setViewName("Teacher/assignment");
        return modelAndView;
    }


    @PostMapping(value = "/createAssignment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> createAssignment(
            @RequestPart("assignmentDto") AssignmentDto assignmentDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        // Base directory
        Path baseUploadPath = Paths.get("/home/data/DegreeTopic/");


        String degreeTopicFolderName = assignmentDto.getDegreeTopic();

         degreeTopicFolderName = degreeTopicFolderName.replaceAll("[:\\\\/*?|<>]", "-");

         Path uploadPath = baseUploadPath.resolve(degreeTopicFolderName);


        String fileName = null;

        if (file != null && !file.isEmpty()) {
            fileName = StringUtils.cleanPath(file.getOriginalFilename());

            try {

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }


                Path filePath = uploadPath.resolve(fileName);


                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("File upload failed: " + e.getMessage());
            }
        }


        Assignment assignment = new Assignment();
        assignment.setTeacherId(user.getId());
        assignment.setStudentId(assignmentDto.getStudentId());
        assignment.setAssignmentTitle(assignmentDto.getAssignmentTitle());
        assignment.setAssignmentDescription(assignmentDto.getAssignmentDescription());
        assignment.setFileName(fileName);
        assignment.setDate(assignmentDto.getDate());
        assignment.setLinkUrl(assignmentDto.getLinkUrl());
        assignment.setLinkText(assignmentDto.getLinkText());
        assignment.setDegreeTopic(assignmentDto.getDegreeTopic());


        assignmentRepository.save(assignment);

        return ResponseEntity.ok("Assignment created successfully.");
    }


    @GetMapping(value = "/editAssignment")
    public ModelAndView editAssignment(@RequestParam(value = "id") Long assignmentId,ModelAndView modelAndView, HttpServletRequest httpServletRequest) {
        Assignment assignment = assignmentRepository.findById(assignmentId).get();
        User user = userRepository.findById(assignment.getStudentId()).get();
        modelAndView.addObject("assignment", assignment);
        modelAndView.addObject("user", user.getUsername());
        modelAndView.setViewName("Teacher/editAssignment");
        return modelAndView;
    }

    @PostMapping(value = "/deleteAssignment")
    public String  deleteAssignment(@RequestParam(value = "id") Long assignmentId) {
        System.out.println("Assignment me id " + assignmentId + " u fshi ");
        assignmentRepository.deleteById(assignmentId);
        return "success";

     }

    @PostMapping(value = "/editAssignment", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity<String> editAssignment(
            @RequestParam(value = "id") Long assignmentId,
            @RequestPart("assignmentDto") AssignmentDto assignmentDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Assignment not found"));
        System.out.println("Assignment id " + assignmentId);

        if (file != null && !file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path uploadPath = Paths.get("/home/data/DegreeTopic/");
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                assignment.setFileName(fileName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("File upload failed: " + e.getMessage());
            }
        }

        assignment.setAssignmentTitle(assignmentDto.getAssignmentTitle());
        assignment.setAssignmentDescription(assignmentDto.getAssignmentDescription());
        assignment.setDate(assignmentDto.getDate());  // note you used 'dueDate' in JS, use it consistently
        assignment.setLinkUrl(assignmentDto.getLinkUrl());
        assignment.setLinkText(assignmentDto.getLinkText());

        System.out.println("Assignment Description: " + assignment.getAssignmentDescription());

        assignmentRepository.save(assignment);

        return ResponseEntity.ok("Assignment edited and file saved successfully.");
    }



    @GetMapping(value = "/viewAssignments")
    public ModelAndView viewAssignments(HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        modelAndView.addObject("currentPath", httpServletRequest.getRequestURI());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        List<Assignment> assignments = assignmentRepository.findAllByStudentIdOrderByDateDesc(user.getId());
        Map<Long, String> assignmentStatuses = new HashMap<>(); // key = assignmentId, value = "On time" / "Late"

        for (Assignment assignment : assignments) {
            List<AssignmentAnswer> answers = assignmentAnswerRepository.findAllByAssignmentId(assignment.getId());
            if (!answers.isEmpty()) {
                AssignmentAnswer answer = answers.get(0); // assuming one answer per assignment
                String status = answer.getDate().before(assignment.getDate()) || answer.getDate().equals(assignment.getDate())
                        ? "Answered on time" : "Answered late";
                assignmentStatuses.put(assignment.getId(), status);
            } else {
                assignmentStatuses.put(assignment.getId(), "Not answered");
            }
        }

        modelAndView.addObject("assignments", assignments);
        modelAndView.addObject("assignmentStatuses", assignmentStatuses);
        modelAndView.addObject("studentId", user.getId());
        modelAndView.setViewName("Student/viewAssignments");
        return modelAndView;
    }


    @GetMapping(value = "/assignmentAnswer")
    public ModelAndView assignmentAnswer( @RequestParam(value="assignmentId")Long id, HttpServletRequest httpServletRequest, ModelAndView modelAndView) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        Assignment assignment = assignmentRepository.findById(id).get();

        modelAndView.addObject("assignment", assignment);
        modelAndView.addObject("studentId", user.getId());

        List<AssignmentAnswer> as = assignmentAnswerRepository.findByStudentId(user.getId());
        modelAndView.addObject("Answerassignmemts", as );

        modelAndView.setViewName("Student/assignmentAnswer");
        return modelAndView;
    }

    @Autowired
    private AssignmentAnswerRepository assignmentAnswerRepository;

    @PostMapping(value = "/assignmentAnswer", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity<String> assignmentAnswer(
            @RequestParam(value = "assignmentId") Long assignmentId,
            @RequestPart("assignmentDto") AssignmentDto assignmentDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        System.out.println("Assignment id " + assignmentId);

        Path baseUploadPath = Paths.get("/home/data/DegreeTopic/");



        String degreeTopicFolderName = assignmentDto.getDegreeTopic();

        degreeTopicFolderName = degreeTopicFolderName.replaceAll("[:\\\\/*?|<>]", "-");

        Path uploadPath = baseUploadPath.resolve(degreeTopicFolderName);


        String fileName = null;  // inicializo me null

        if (file != null && !file.isEmpty()) {
            fileName = StringUtils.cleanPath(file.getOriginalFilename());

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("File upload failed: " + e.getMessage());
            }
        }

        Date dueDateItWas = assignment.getDate();
        Date today = new Date();
        String status;
        if (dueDateItWas.before(today)) {
            status = "Answered late";
        } else if (dueDateItWas.after(today)) {
            status = "Answered on time";
        } else {
            status = "Pending Submission";
        }

        AssignmentAnswer assignmentAnswer = new AssignmentAnswer();
        assignmentAnswer.setAssignmentTitle(assignmentDto.getAssignmentTitle());
        assignmentAnswer.setAssignmentDescription(assignmentDto.getAssignmentDescription());
        assignmentAnswer.setDate(new Date());
        assignmentAnswer.setStudentId(user.getId());
        assignmentAnswer.setAssignmentId(assignment.getId());
        assignmentAnswer.setFileName(fileName);  // do të jetë null nëse nuk ka file
        assignmentAnswer.setStatus(status);

        assignmentAnswerRepository.save(assignmentAnswer);

        return ResponseEntity.ok("Assignment edited and file saved successfully.");
    }




    @GetMapping(value="/assignmentAnswers")
    public ModelAndView assignmentAnswers (@RequestParam(value="id")Long id,ModelAndView modelAndView){

        AssignmentAnswer assignmentAnswer = assignmentAnswerRepository.findById(id).get();
        modelAndView.addObject("assignmentAnswer", assignmentAnswer);

        Assignment assignment = assignmentRepository.findById(assignmentAnswer.getAssignmentId()).get();
        modelAndView.addObject("assignment", assignment);
        String degreeTopicFolderName = assignment.getDegreeTopic();
        String getPropertyPicturesPath =  "/attachments/" + degreeTopicFolderName+ "/" + assignmentAnswer.getFileName();
        modelAndView.addObject("getPropertyPicturesPath", getPropertyPicturesPath);


        modelAndView.setViewName("Student/assignmentAnswers1");
        return modelAndView;
    }

    @GetMapping("/assignmentAnswers/pdf/{id}")
    public void generatePdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
        AssignmentAnswer assignmentAnswer = assignmentAnswerRepository.findById(id).get();
        Assignment assignment = assignmentRepository.findById(assignmentAnswer.getAssignmentId()).get();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=assignment_"+id+".pdf");

        // Use a PDF library like iText or Apache PDFBox to generate the PDF
        // This would involve creating the PDF document programmatically
        // ...
    }






}

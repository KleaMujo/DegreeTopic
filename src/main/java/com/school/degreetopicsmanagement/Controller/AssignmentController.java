package com.school.degreetopicsmanagement.Controller;

import com.school.degreetopicsmanagement.DataObjects.AssignmentDto;
import com.school.degreetopicsmanagement.Model.*;
import com.school.degreetopicsmanagement.Repository.AssignmentRepository;
import com.school.degreetopicsmanagement.Repository.DegreeTopicRespository;
import com.school.degreetopicsmanagement.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class AssignmentController {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DegreeTopicRespository degreeTopicRespository;

    @GetMapping(value="/assignment")
    public ModelAndView assignment(ModelAndView modelAndView, HttpServletRequest httpServletRequest) {
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
        }
        modelAndView.setViewName("Teacher/assignment");
        return modelAndView;
    }


    @PostMapping(value = "/createAssignment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> createAssignment(
            @RequestPart("assignmentDto") AssignmentDto assignmentDto,
            @RequestPart("file") MultipartFile file) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path uploadPath = Paths.get("/home/data/DegreeTopic/");

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

        Assignment assignment = new Assignment();
        assignment.setTeacherId(user.getId());
        assignment.setStudentId(assignmentDto.getStudentId());

        assignment.setAssignmentTitle(assignmentDto.getAssignmentTitle());
        assignment.setAssignmentDescription(assignmentDto.getAssignmentDescription());
        assignment.setFileName(fileName); // Save just the file name
        assignment.setDate(new Date());
        assignment.setLinkUrl(assignmentDto.getLinkUrl());
        assignment.setLinkText(assignmentDto.getLinkText());

        assignmentRepository.save(assignment);

        return ResponseEntity.ok("Assignment created and file saved successfully.");
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(authenticatedUser.getUsername());

        List<Assignment> assignments = assignmentRepository.findAllByStudentIdOrderByDateDesc(user.getId());
        modelAndView.addObject("assignments", assignments);


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


        modelAndView.setViewName("Student/assignmentAnswer");
        return modelAndView;
    }

}

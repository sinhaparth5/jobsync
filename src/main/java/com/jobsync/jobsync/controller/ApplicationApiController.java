package com.jobsync.jobsync.controller;

import com.jobsync.jobsync.model.JobApplication;
import com.jobsync.jobsync.model.User;
import com.jobsync.jobsync.repository.JobApplicationRepository;
import com.jobsync.jobsync.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/applications")
public class ApplicationApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationApiController.class);
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public ApplicationApiController(JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getApplications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        List<JobApplication> applications = isAdmin
                ? jobApplicationRepository.findAll()
                : jobApplicationRepository.findByUserId(user.getId());
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getApplication(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Application not found: " + id));
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !application.getUser().getUsername().equals(username)) {
            logger.warn("User {} attempted to access application {} not owned by them", username, id);
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(application);
    }

    @PostMapping
    public ResponseEntity<JobApplication> createApplication(@RequestBody JobApplication application) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        application.setUser(user);
        JobApplication savedApplication = jobApplicationRepository.save(application);
        logger.info("Created job application: {} for user: {}", savedApplication.getId(), username);
        return ResponseEntity.ok(savedApplication);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplication> updateApplication(@PathVariable String id, @RequestBody JobApplication application) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        JobApplication existingApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Application not found: " + id));
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !existingApplication.getUser().getUsername().equals(username)) {
            logger.warn("User {} attempted to update application {} not owned by them", username, id);
            return ResponseEntity.status(403).build();
        }
        existingApplication.setCompany(application.getCompany());
        existingApplication.setRole(application.getRole());
        existingApplication.setStatus(application.getStatus());
        existingApplication.setDeadline(application.getDeadline());
        JobApplication updatedApplication = jobApplicationRepository.save(existingApplication);
        logger.info("Updated job application: {} by user: {}", id, username);
        return ResponseEntity.ok(updatedApplication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Application not found: " + id));
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !application.getUser().getUsername().equals(username)) {
            logger.warn("User {} attempted to delete application {} not owned by them", username, id);
            return ResponseEntity.status(403).build();
        }
        jobApplicationRepository.deleteById(id);
        logger.info("Deleted job application: {} by user: {}", id, username);
        return ResponseEntity.noContent().build();
    }
}

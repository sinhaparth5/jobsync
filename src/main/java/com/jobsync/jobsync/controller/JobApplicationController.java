package com.jobsync.jobsync.controller;

import com.jobsync.jobsync.model.JobApplication;
import com.jobsync.jobsync.model.User;
import com.jobsync.jobsync.repository.JobApplicationRepository;
import com.jobsync.jobsync.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class JobApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(JobApplicationController.class);
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final String UPLOAD_DIR = "uploads/resumes/";

    public JobApplicationController(JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            logger.error("Failed to create uploads directory: {}", UPLOAD_DIR,e);
        }
    }

    @GetMapping("/applications")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/applications/new")
    public String showApplicationForm(Model model) {
        model.addAttribute("application", new JobApplication());
        return "application-form";
    }

    @PostMapping("/applications")
    public String saveApplication(@Valid @ModelAttribute("application") JobApplication application, BindingResult result,
                                  @RequestParam("resume") MultipartFile resume) {
        if (result.hasErrors()) {
            logger.error("Validation errors for job application form: {}", result.getAllErrors());
            return "application-form";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            logger.warn("Unauthenticated user attempted to save application");
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        if (!resume.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + resume.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.write(filePath, resume.getBytes());
                application.setResumePath(fileName);
            } catch (IOException e) {
                logger.error("Failed to upload resume for application: {}", application.getCompany(), e);
                result.rejectValue("resumePath", "upload.failed", "Failed to upload resume");
                return "application-form";
            }
        }
        application.setUser(user);
        jobApplicationRepository.save(application);
        logger.info("Saved job application for user: {}, company: {}", username, application.getCompany());
        return "redirect:/dashboard";
    }

    @GetMapping("/applications/edit")
    public String showEditApplicationForm(@RequestParam("id") String id, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            logger.warn("Unauthenticated user attempted to edit application");
            return "redirect:/login";
        }
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Application not found: {}", id);
                    return new IllegalStateException("Application not found: " + id);
                });
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin && !application.getUser().getUsername().equals(username)) {
            logger.warn("User {} attempted to edit application {} not owned by them", username, id);
            return "redirect:/dashboard";
        }
        model.addAttribute("application", application);
        return "application-form";
    }

    @PostMapping("/applications/edit")
    public String updateApplication(
            @Valid @ModelAttribute("application") JobApplication application,
            BindingResult result,
            @RequestParam("id") String id,
            @RequestParam("resume") MultipartFile resume
    ) {
        if (result.hasErrors()) {
            logger.error("Validation errors in job application edit form: {}", result.getAllErrors());
            return "application-form";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            logger.warn("Unauthenticated user attempted to update application");
            return "redirect:/login";
        }
        JobApplication existingApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Application not found: {}", id);
                    return new IllegalStateException("Application not found: " + id);
                });
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin && !existingApplication.getUser().getUsername().equals(username)) {
            logger.warn("User {} attempted to update application {} not owned by them", username, id);
            return "redirect:/dashboard";
        }
        // Handle file upload
        if (!resume.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + resume.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.write(filePath, resume.getBytes());
                // Delete old resume if exists
                if (existingApplication.getResumePath() != null) {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIR, existingApplication.getResumePath()));
                }
                existingApplication.setResumePath(fileName);
            } catch (IOException e) {
                logger.error("Failed to upload resume for application: {}", application.getCompany(), e);
                result.rejectValue("resumePath", "upload.failed", "Failed to upload resume");
                return "application-form";
            }
        }
        existingApplication.setCompany(application.getCompany());
        existingApplication.setRole(application.getRole());
        existingApplication.setStatus(application.getStatus());
        existingApplication.setDeadline(application.getDeadline());
        jobApplicationRepository.save(existingApplication);
        logger.info("Updated job application: {} by user: {}", id, username);
        return "redirect:/dashboard";
    }

    @GetMapping("/applications/delete")
    public String deleteApplication(@RequestParam("id") String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {
            logger.warn("Unauthenticated user attempted to delete application");
            return "redirect:/login";
        }
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Application not found: {}", id);
                    return new IllegalStateException("Application not found: " + id);
                });
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin && !application.getUser().getUsername().equals(username)) {
            logger.warn("User {} attempted to delete application {} not owned by them", username, id);
            return "redirect:/dashboard";
        }
        // Delete resume file if exists
        if (application.getResumePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR, application.getResumePath()));
            } catch (IOException e) {
                logger.error("Failed to delete resume file: {}", application.getResumePath(), e);
            }
        }
        jobApplicationRepository.deleteById(id);
        logger.info("Deleted job application: {} by user: {}", id, username);
        return "redirect:/dashboard";
    }
}

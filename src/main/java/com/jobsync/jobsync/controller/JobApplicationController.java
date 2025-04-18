package com.jobsync.jobsync.controller;

import com.jobsync.jobsync.model.JobApplication;
import com.jobsync.jobsync.model.User;
import com.jobsync.jobsync.repository.JobApplicationRepository;
import com.jobsync.jobsync.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;

@Controller
public class JobApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(JobApplicationController.class);
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public JobApplicationController(JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
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
    public String saveApplication(@Valid @ModelAttribute("application") JobApplication application, BindingResult result) {
        if (result.hasErrors()) {
            return "application-form";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        application.setUser(user);
        jobApplicationRepository.save(application);
        logger.info("Saved job application for user: {}", username);
        return "redirect:/dashboard";
    }
}

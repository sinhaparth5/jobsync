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
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AnalyticsController {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public AnalyticsController(JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/analytics")
    public String showAnalytics(Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                logger.warn("Unauthenticated user accessing analytics");
                return "redirect:/login";
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", username);
                        return new IllegalStateException("User not found: " + username);
                    });

            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            List<JobApplication> applications = isAdmin
                    ? jobApplicationRepository.findAll()
                    : jobApplicationRepository.findByUserId(user.getId());

            // Calculate the status counts
            Map<String, Long> statusCounts = new HashMap<>();
            statusCounts.put("Applied", 0L);
            statusCounts.put("Interviewing", 0L);
            statusCounts.put("Rejected", 0L);
            statusCounts.put("Offered", 0L);

            for (JobApplication app: applications) {
                String status = app.getStatus();
                statusCounts.put(status, statusCounts.getOrDefault(status, 0L) + 1);
            }

            model.addAttribute("statusCounts", statusCounts);
            return "analytics";
        } catch (Exception e) {
            logger.error("Error rendering analytics for user: {}", SecurityContextHolder.getContext().getAuthentication().getName(), e);
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "error";
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jobsync.jobsync.controller;

import java.util.List;

import com.jobsync.jobsync.model.TechnicalNote;
import com.jobsync.jobsync.model.User;
import com.jobsync.jobsync.repository.TechnicalNoteRepository;
import com.jobsync.jobsync.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.jobsync.jobsync.model.JobApplication;
import com.jobsync.jobsync.repository.JobApplicationRepository;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author parth
 */

@Controller
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final JobApplicationRepository jobApplicationRepository;
    private final TechnicalNoteRepository technicalNoteRepository;
    private final UserRepository userRepository;

    public DashboardController(JobApplicationRepository jobApplicationRepository, TechnicalNoteRepository technicalNoteRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.technicalNoteRepository = technicalNoteRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(name = "appPage", defaultValue = "0") int appPage,
                                @RequestParam(name = "notePage", defaultValue = "0") int notePage,
                                @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
                                @RequestParam(name = "appSearch", required = false) String appSearch,
                                @RequestParam(name = "appStatus", required = false) String appStatus,
                                @RequestParam(name = "noteSearch", required = false) String noteSearch,
                                @RequestParam(name = "noteCategory", required = false) String noteCategory,
                                Model model) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                logger.warn("Unauthenticated user accessing dashboard");
                return "redirect:/login";
            }
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + username));

            // Job Applications
            Pageable appPageable = PageRequest.of(appPage, pageSize);
            Page<JobApplication> appPageResult = jobApplicationRepository.findByUserIdWithSearchAndFilter(
                    user.getId(), appSearch, appStatus, appPageable
            );
            model.addAttribute("applications", appPageResult.getContent());
            model.addAttribute("appPage", appPageResult);
            model.addAttribute("appSearch", appSearch);
            model.addAttribute("appStatus", appStatus);

            // Technical Notes
            Pageable notePageable = PageRequest.of(notePage, pageSize);
            Page<TechnicalNote> notePageResult = technicalNoteRepository.findByUserIdWithSearchAndFilter(
                    user.getId(), noteSearch, noteCategory, notePageable
            );
            model.addAttribute("notes", notePageResult.getContent());
            model.addAttribute("notePage", notePageResult);
            model.addAttribute("noteSearch", noteSearch);
            model.addAttribute("noteCategory", noteCategory);

            return "dashboard";
        } catch (Exception e) {
            logger.error("Error rendering dashboard for user: {}", SecurityContextHolder.getContext().getAuthentication().getName(), e);
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "error";
        }
    }
}

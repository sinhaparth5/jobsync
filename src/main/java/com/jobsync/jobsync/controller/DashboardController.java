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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.jobsync.jobsync.model.JobApplication;
import com.jobsync.jobsync.repository.JobApplicationRepository;

/**
 *
 * @author parth
 */

@Controller
public class DashboardController {
    private final JobApplicationRepository jobApplicationRepository;
    private final TechnicalNoteRepository technicalNoteRepository;
    private final UserRepository userRepository;

    public DashboardController(JobApplicationRepository jobApplicationRepository, TechnicalNoteRepository technicalNoteRepository, UserRepository userRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.technicalNoteRepository = technicalNoteRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        List<JobApplication> applications = jobApplicationRepository.findByUserId(user.getId());
        List<TechnicalNote> notes = technicalNoteRepository.findByUserId(user.getId());
        model.addAttribute("applications", applications);
        model.addAttribute("notes", notes);
        return "dashboard";
    }
}

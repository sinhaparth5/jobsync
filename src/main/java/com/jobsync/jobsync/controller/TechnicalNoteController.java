package com.jobsync.jobsync.controller;


import com.jobsync.jobsync.model.TechnicalNote;
import com.jobsync.jobsync.model.User;
import com.jobsync.jobsync.repository.TechnicalNoteRepository;
import com.jobsync.jobsync.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TechnicalNoteController {
    private final TechnicalNoteRepository technicalNoteRepository;
    private final UserRepository userRepository;

    public TechnicalNoteController(TechnicalNoteRepository technicalNoteRepository, UserRepository userRepository) {
        this.technicalNoteRepository = technicalNoteRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/notes/new")
    public String showNoteForm(Model model) {
        model.addAttribute("note", new TechnicalNote());
        return "technical-note-form";
    }

    @PostMapping("/notes")
    public String saveNote(@Valid @ModelAttribute("note") TechnicalNote note, BindingResult result) {
        if (result.hasErrors()) {
            return "technical-note-form";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        note.setUser(user);
        technicalNoteRepository.save(note);
        return "redirect:/dashboard";
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jobsync.jobsync.model;

import java.time.LocalDate;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author parth
 */

@Entity
@Table(name="job_application")
public class JobApplication {
    @Id
    @Column(name="id", length=26, nullable=false, updatable=false)
    private String id;

    @NotBlank(message = "Company is required")
    @Column(nullable=false)
    private String company;

    @NotBlank(message = "Role is required")
    @Column(nullable=false)
    private String role;

    @NotBlank(message = "Status is required")
    @Column(nullable=false)
    private String status;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "resume_path")
    private String resumePath;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    private static final ULID ulidGenerator = new ULID();

    public JobApplication() {}
    public JobApplication(String company, String role, String status, LocalDate deadline, String resumePath, User user) {
        this.company = company;
        this.role = role;
        this.status = status;
        this.deadline = deadline;
        this.resumePath = resumePath;
        this.user = user;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = ulidGenerator.nextULID();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

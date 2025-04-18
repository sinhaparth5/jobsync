/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jobsync.jobsync.model;

import java.util.List;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author parth
 */

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", length=26, nullable=false, updatable=false)
    private String id;

    @NotBlank(message = "Username is required")
    @Column(unique=true, nullable=false)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable=false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique=true, nullable=false)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TechnicalNote> technicalNotes;

    @Transient
    private static final ULID ulidGenerator = new ULID();

    public User() {}
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
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
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<JobApplication> getJobApplications() { return jobApplications; }
    public void setJobApplications(List<JobApplication> jobApplications) { this.jobApplications = jobApplications; }
    public List<TechnicalNote> getTechnicalNotes() { return technicalNotes; }
    public void setTechnicalNotes(List<TechnicalNote> technicalNotes) { this.technicalNotes = technicalNotes; }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jobsync.jobsync.model;


import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author parth
 */

@Entity
@Table(name = "technical_notes")
public class TechnicalNote {
    @Id
    @Column(name = "id", length = 26, nullable = false, updatable = false)
    private String id;

    @NotBlank(message = "Topic is required")
    @Column(nullable = false)
    private String topic;

    @NotBlank(message = "Content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Transient
    private static final ULID ulidGenerator = new ULID();

    public TechnicalNote() {}
    public TechnicalNote(String topic, String content, String category, User user) {
        this.topic = topic;
        this.content = content;
        this.category = category;
        this.user = user;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = ulidGenerator.nextULID();
        }
    }

    // Getter and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() {return category; }
    public void setCategory(String category) { this.category = category; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.jobsync.jobsync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobsync.jobsync.model.JobApplication;

/**
 *
 * @author parth
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {
    List<JobApplication> findByUserId(String userId);
}

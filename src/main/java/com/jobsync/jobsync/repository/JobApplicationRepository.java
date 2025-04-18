/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.jobsync.jobsync.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobsync.jobsync.model.JobApplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author parth
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {
    List<JobApplication> findByUserId(String userId);

    @Query("SELECT j FROM JobApplication j WHERE j.user.id = :userId " +
        "AND (:search IS NULL OR j.company LIKE %:search% OR j.role LIKE %:search%) " +
            "AND (:status IS NULL OR j.status = :status)")
    Page<JobApplication> findByUserIdWithSearchAndFilter(
            @Param("userId") String userId,
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );
}

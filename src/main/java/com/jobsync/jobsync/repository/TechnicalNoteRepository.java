/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.jobsync.jobsync.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobsync.jobsync.model.TechnicalNote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author parth
 */
public interface TechnicalNoteRepository extends JpaRepository<TechnicalNote, String> {
    List<TechnicalNote> findByUserId(String userId);

    @Query("SELECT n FROM TechnicalNote n WHERE n.user.id = :userId " +
        "AND (:search IS NULL OR n.topic LIKE %:search% OR n.category LIKE %:search%) " +
            "AND (:category IS NULL OR n.category = :category)"
    )
    Page<TechnicalNote> findByUserIdWithSearchAndFilter(
            @Param("userId") String userId,
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable
    );
}

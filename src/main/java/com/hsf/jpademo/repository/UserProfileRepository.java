package com.hsf.jpademo.repository;

import com.hsf.jpademo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * =========================================================================================
 * REPOSITORY QUẢN LÝ THỰC THỂ USERPROFILE
 * =========================================================================================
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}

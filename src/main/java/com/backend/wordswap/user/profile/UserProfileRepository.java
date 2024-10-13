package com.backend.wordswap.user.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.wordswap.user.profile.entity.UserProfileModel;

public interface UserProfileRepository extends JpaRepository<UserProfileModel, Long>{

}

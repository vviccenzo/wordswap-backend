package com.backend.wordswap.user;

import com.backend.wordswap.user.entity.UserModel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    UserModel findByUserCode(String userCode);

    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByEmail(String email);

}

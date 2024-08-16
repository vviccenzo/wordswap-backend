package com.backend.wordswap.user;

import com.backend.wordswap.user.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    UserModel findByUserCode(String userCode);

    UserModel findByUsername(String username);

}

package com.backend.wordswap.user.factory;

import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class UserFactory {

    public static UserModel createModelFromDto(UserCreateDTO dto) {
        UserModel model = new UserModel();
        model.setUsername(dto.getUsername());
        model.setEmail(dto.getEmail());
        model.setPassword(dto.getPassword());
        model.setCreationDate(LocalDate.now());

        return model;
    }

    public static UserModel createModelFromDto(UserUpdateDTO dto, UserModel model) {
        model.setUsername(dto.getUsername());
        model.setPassword(dto.getPassword());

        return model;
    }
    
    public static List<UserDTO> buildList(List<UserModel> friends) {
    	return friends.stream().map(model -> new UserDTO(model.getId(), model.getUsername(), model.getCreationDate())).toList();
    }
}

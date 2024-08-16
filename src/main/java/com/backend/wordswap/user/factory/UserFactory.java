package com.backend.wordswap.user.factory;

import com.backend.wordswap.user.dto.UserCreateDTO;
import com.backend.wordswap.user.dto.UserDTO;
import com.backend.wordswap.user.dto.UserUpdateDTO;
import com.backend.wordswap.user.entity.UserModel;

import java.time.LocalDate;
import java.util.List;

public class UserFactory {

    public UserModel createModelFromDto(UserCreateDTO dto) {
        UserModel model = new UserModel();
        model.setUsername(dto.getUsername());
        model.setEmail(dto.getEmail());
        model.setPassword(dto.getPassword());
        model.setCreationDate(LocalDate.now());

        return model;
    }

    public UserModel createModelFromDto(UserUpdateDTO dto, UserModel model) {
        model.setUsername(dto.getUsername());
        model.setPassword(dto.getPassword());

        return model;
    }
    
    public static List<UserDTO> buildList(List<UserModel> friends) {
    	return friends.stream().map(model -> new UserDTO(model.getId(), model.getUsername(), model.getCreationDate())).toList();
    }
}

package com.backend.wordswap.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.user.profile.entity.UserProfileModel;

@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@EqualsAndHashCode(callSuper = true)
public class UserModel extends GenericModel {

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "user_code", unique = true)
	private String userCode;

	@Column(name = "creation_date")
	private LocalDate creationDate;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private UserProfileModel userProfile;
	
}

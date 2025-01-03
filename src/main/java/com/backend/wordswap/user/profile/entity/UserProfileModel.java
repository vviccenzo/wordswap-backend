package com.backend.wordswap.user.profile.entity;

import java.time.LocalDate;

import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.user.entity.UserModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UserProfileModel extends GenericModel {

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
	private UserModel user;

	@Column(name = "content")
	private byte[] content;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "update_date")
	private LocalDate updateDate;

	@Override
	public String toString() {
		return "UserProfileModel{" + "id=" + getId() + ", fileName='" + fileName + '\'' + ", updateDate=" + updateDate
				+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		UserProfileModel that = (UserProfileModel) obj;
		return getId() != null && getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}
}

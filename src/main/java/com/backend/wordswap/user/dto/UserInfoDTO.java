package com.backend.wordswap.user.dto;

import java.util.Arrays;
import java.util.Objects;

public record UserInfoDTO(Long id, byte[] profilePic, String name, String bio, String userCode) {

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserInfoDTO that = (UserInfoDTO) o;
		return Objects.equals(id, that.id) && Arrays.equals(profilePic, that.profilePic)
				&& Objects.equals(name, that.name) && Objects.equals(bio, that.bio)
				&& Objects.equals(userCode, that.userCode);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(id, name, bio, userCode);
		result = 31 * result + Arrays.hashCode(profilePic);
		return result;
	}

	@Override
	public String toString() {
		return "UserInfoDTO{" + "id=" + id + ", profilePic=" + Arrays.toString(profilePic) + ", name='" + name + '\''
				+ ", bio='" + bio + '\'' + ", userCode='" + userCode + '\'' + '}';
	}
}

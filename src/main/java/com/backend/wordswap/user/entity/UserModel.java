package com.backend.wordswap.user.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.friendshipRequest.entity.FriendshipRequestModel;
import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.user.profile.entity.UserProfileModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserModel extends GenericModel {

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "user_code", unique = true)
	private String userCode;

	@Column(name = "name")
	private String name;

	@Column(name = "bio")
	private String bio;

	@Column(name = "creation_date")
	private LocalDate creationDate;

	@OneToOne(mappedBy = "user", orphanRemoval = true)
	private UserProfileModel userProfile;

	@Column(name = "role")
	private UserRole role;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id"))
	private List<UserModel> friends = new ArrayList<>();

	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<FriendshipRequestModel> receivedFriendshipRequests = new ArrayList<>();

	@OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<FriendshipRequestModel> sentFriendshipRequests = new ArrayList<>();

	@ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
	private List<ConversationModel> conversations = new ArrayList<>();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		UserModel that = (UserModel) obj;
		return getId() != null && getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (this.role == UserRole.ADMIN) {
			return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
		} else {
			return List.of(new SimpleGrantedAuthority("ROLE_USER"));
		}
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
}

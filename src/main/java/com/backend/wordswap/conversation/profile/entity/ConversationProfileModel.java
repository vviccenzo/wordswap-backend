package com.backend.wordswap.conversation.profile.entity;

import java.time.LocalDate;

import com.backend.wordswap.conversation.entity.ConversationModel;
import com.backend.wordswap.generic.entity.GenericModel;

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
@Table(name = "conversation_profile")
public class ConversationProfileModel extends GenericModel {

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "conversation_id", referencedColumnName = "id", nullable = true)
	private ConversationModel conversation;

	@Column(name = "content")
	private byte[] content;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "update_date")
	private LocalDate updateDate;

	@Override
	public String toString() {
		return "ConversationProfileModel{" + "id=" + getId() + ", fileName='" + fileName + '\'' + ", updateDate=" + updateDate
				+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ConversationProfileModel that = (ConversationProfileModel) obj;
		return getId() != null && getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}
}

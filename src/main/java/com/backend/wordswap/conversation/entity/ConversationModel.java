package com.backend.wordswap.conversation.entity;

import com.backend.wordswap.generic.entity.GenericModel;
import com.backend.wordswap.message.entity.MessageModel;
import com.backend.wordswap.user.entity.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversation")
@EqualsAndHashCode(callSuper = true)
public class ConversationModel extends GenericModel {

	@ManyToOne
	@JoinColumn(name = "initiator")
	private UserModel userInitiator;

	@ManyToOne
	@JoinColumn(name = "recipient")
	private UserModel userRecipient;

	@OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<MessageModel> messages = new ArrayList<>();

	@Column(name = "created_date")
	private LocalDate createdDate;

}

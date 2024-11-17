package com.backend.wordswap.message.entity;

import java.time.LocalDate;

import com.backend.wordswap.generic.entity.GenericModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_images")
@EqualsAndHashCode(callSuper = true)
public class MessageImageModel extends GenericModel {

    @OneToOne
    @JoinColumn(name = "message_id", unique = true, nullable = false)
    private MessageModel message;

	@Lob
	@Column(name = "content", nullable = false)
	private byte[] content;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "upload_date", nullable = false)
	private LocalDate uploadDate;

}

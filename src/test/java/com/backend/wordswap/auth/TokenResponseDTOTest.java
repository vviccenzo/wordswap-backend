package com.backend.wordswap.auth;

import org.junit.jupiter.api.Test;

import com.backend.wordswap.auth.token.definition.TokenResponseDTO;

import static org.assertj.core.api.Assertions.assertThat;

class TokenResponseDTOTest {

	@Test
	void testTokenResponseDTO() {
		TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(true);

		assertThat(tokenResponseDTO.isValid()).isTrue();

		TokenResponseDTO invalidTokenResponseDTO = new TokenResponseDTO(false);

		assertThat(invalidTokenResponseDTO.isValid()).isFalse();
	}

	@Test
	void testTokenResponseDTO_Equality() {
		TokenResponseDTO dto1 = new TokenResponseDTO(true);
		TokenResponseDTO dto2 = new TokenResponseDTO(true);

		assertThat(dto1).isEqualTo(dto2);

		TokenResponseDTO dto3 = new TokenResponseDTO(false);
		assertThat(dto1).isNotEqualTo(dto3);
	}

	@Test
	void testTokenResponseDTO_HashCode() {
		TokenResponseDTO dto1 = new TokenResponseDTO(true);
		TokenResponseDTO dto2 = new TokenResponseDTO(true);

		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

		TokenResponseDTO dto3 = new TokenResponseDTO(false);
		assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
	}

	@Test
	void testTokenResponseDTO_ToString() {
		TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(true);
		assertThat(tokenResponseDTO.toString()).isEqualTo("TokenResponseDTO[isValid=true]");
	}
}

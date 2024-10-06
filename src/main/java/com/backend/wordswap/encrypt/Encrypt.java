package com.backend.wordswap.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Encrypt {
	
	Encrypt() {
	}

	@Value("${secret.key}")
	private static String secretKey;

	@Value("${aes.ecb}")
	private static String aesEcb;

	public static String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

		SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");
		Cipher cipher = Cipher.getInstance(aesEcb);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public static String decrypt(String encryptedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
		byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

		SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");
		Cipher cipher = Cipher.getInstance(aesEcb);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}
}

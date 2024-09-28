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

import lombok.experimental.UtilityClass;

@UtilityClass
public class Encrypt {

	private static final String SECRET_KEY = "03gNpJHDjKQzwe4U";

	private static final String AES_ECB = "AES/ECB/PKCS5Padding";

	public static String encrypt(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

		SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");
		Cipher cipher = Cipher.getInstance(AES_ECB);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public static String decrypt(String encryptedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
		byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

		SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");
		Cipher cipher = Cipher.getInstance(AES_ECB);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}
}

package com.claro.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public final class AESEncryption {
	
	private static final String AES = "AES";
	private static final String AES_INSTANCE = "AES/CBC/PKCS5PADDING";
	
	private AESEncryption() {
		
	}

	public static String encrypt(String cuenta, String key) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
		Cipher cipher = Cipher.getInstance(AES_INSTANCE);
		cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);
		byte[] encrypted = cipher.doFinal(cuenta.getBytes());
		return Base64.encodeBase64String(encrypted);
	}
	
	public static String decrypt(String cuentaEncrypted, String key) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
		Cipher cipher = Cipher.getInstance(AES_INSTANCE);
		cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);
		byte[] decrypt = cipher.doFinal(Base64.decodeBase64(cuentaEncrypted.getBytes()));
		return new String(decrypt);
	}

}

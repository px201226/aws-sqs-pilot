package com.example.awssqspilot.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UuidUtils {

	public static String generateUuid() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest salt = MessageDigest.getInstance("SHA-256");
		salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
		return bytesToHex(salt.digest());
	}

	public static String bytesToHex(byte[] raw) {
		if ( raw == null ) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(Character.forDigit((b & 0xF0) >> 4, 16))
					.append(Character.forDigit((b & 0x0F), 16));
		}
		return hex.toString();
	}
}

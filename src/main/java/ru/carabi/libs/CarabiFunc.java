package ru.carabi.libs;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author sasha
 */
public class CarabiFunc {
	private static final String configkey = "Carab!", salt = "EventerKOD", pepper = "#Test~";
	public static final Charset defaultCharset = Charset.forName("UTF-8");
	public static String getRandomString(int length) {
		byte[] bytes = new byte[length];
		for (int i=0; i<length; i++) {
			bytes[i] = (byte) Math.round(Math.random() * 127);
		}
		String string = DatatypeConverter.printBase64Binary(bytes);
		return string.substring(0, length);
	}
	
	public static String decrypt(String encrypted, String configkey, String salt, String pepper) throws GeneralSecurityException {
		byte[] input = DatatypeConverter.parseBase64Binary(encrypted);
		String secretKey = configkey + salt;
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(defaultCharset), "AES");
		String iv = salt + pepper;
		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(defaultCharset));
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		byte[] decrypted = new byte[cipher.getOutputSize(input.length)];
		int dec_len = cipher.update(input, 0, input.length, decrypted, 0);
		dec_len += cipher.doFinal(decrypted, dec_len);
		return new String(decrypted, defaultCharset).trim();
	}
	
	public static String decrypt(String encrypted) throws GeneralSecurityException {
		return decrypt(encrypted, configkey, salt, pepper);
	}
	
	public static String encrypt (String data, String configkey, String salt, String pepper) throws GeneralSecurityException {
		byte[] input = data.getBytes(CarabiFunc.defaultCharset);
		String secretKey = configkey + salt;
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(defaultCharset), "AES");
		String iv = salt + pepper;
		IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(defaultCharset));
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] encrypted= new byte[cipher.getOutputSize(input.length)];
		int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
		enc_len += cipher.doFinal(encrypted, enc_len);
		return DatatypeConverter.printBase64Binary(encrypted);
	}
	
	public static String encrypt (String data) throws GeneralSecurityException {
		return encrypt(data, configkey, salt, pepper);
	}

	public static void setCookie(BindingProvider bindingProvider) {
		Map responseHeaders = (Map)bindingProvider.getResponseContext().get(MessageContext.HTTP_RESPONSE_HEADERS);
		final Object cookies = responseHeaders.get("Set-cookie");
		if (cookies == null) {
			return;
		}
		for (Object header: new ArrayList((Collection) cookies)) {
			String cookie = header.toString();
			//JSESSIONID=916513d9c09ef99036f90d938172; Path=/carabiserver-web; HttpOnly
			StringTokenizer tokenizer = new StringTokenizer(cookie, ";");
			String value = tokenizer.nextToken();
			Map map = new HashMap();
			map.put("Cookie", Collections.singletonList(value));
			bindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, map);
		}
	}
}

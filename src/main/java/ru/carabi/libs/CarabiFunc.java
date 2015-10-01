package ru.carabi.libs;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

/**
 *
 * @author sasha
 */
public class CarabiFunc {
	private static final String configkey = "Carab!", salt = "EventerKOD", pepper = "#Test~";
	//время в секундах, после которого считаем, что пользователь отключился
	//(а не связь порвалась, например). С этим же интервалом обновляем список онлайн-пользователей.
	public static final int onlineControlTimeout = 60; 
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
	
	private static final Map<Character, String> translit = new HashMap<>();
	
	static {
		translit.put('А', "A");
		translit.put('Б', "B");
		translit.put('В', "V");
		translit.put('Г', "G");
		translit.put('Д', "D");
		translit.put('Е', "JE");
		translit.put('Ё', "JO");
		translit.put('Ж', "J");
		translit.put('З', "Z");
		translit.put('И', "I");
		translit.put('Й', "IJ");
		translit.put('К', "K");
		translit.put('Л', "L");
		translit.put('М', "M");
		translit.put('Н', "N");
		translit.put('О', "O");
		translit.put('П', "P");
		translit.put('Р', "R");
		translit.put('С', "S");
		translit.put('Т', "T");
		translit.put('У', "U");
		translit.put('Ф', "F");
		translit.put('Х', "H");
		translit.put('Ц', "TC");
		translit.put('Ч', "CH");
		translit.put('Ш', "SH");
		translit.put('Щ', "SCH");
		translit.put('Ъ', "`");
		translit.put('Ы', "Y");
		translit.put('Ь', "`");
		translit.put('Э', "E");
		translit.put('Ю', "JU");
		translit.put('Я', "JA");

		translit.put('а', "a");
		translit.put('б', "b");
		translit.put('в', "v");
		translit.put('г', "g");
		translit.put('д', "d");
		translit.put('е', "je");
		translit.put('ё', "jo");
		translit.put('ж', "j");
		translit.put('з', "z");
		translit.put('и', "i");
		translit.put('й', "ij");
		translit.put('к', "k");
		translit.put('л', "l");
		translit.put('м', "m");
		translit.put('н', "n");
		translit.put('о', "o");
		translit.put('п', "p");
		translit.put('р', "r");
		translit.put('с', "s");
		translit.put('т', "t");
		translit.put('у', "u");
		translit.put('ф', "f");
		translit.put('х', "h");
		translit.put('ц', "tc");
		translit.put('ч', "ch");
		translit.put('ш', "sh");
		translit.put('щ', "sch");
		translit.put('ъ', "`");
		translit.put('ы', "y");
		translit.put('ь', "`");
		translit.put('э', "e");
		translit.put('ю', "ju");
		translit.put('я', "ja");
	}
	
	public static String cyrillicToAscii(String cyrillicStr) {
		
		StringBuilder result = new StringBuilder();
		for (char litera: cyrillicStr.toCharArray()) {
			if (translit.containsKey(litera)) {
				result.append(translit.get(litera));
			} else {
				result.append(litera);
			}
		}
		return result.toString();
	}
}

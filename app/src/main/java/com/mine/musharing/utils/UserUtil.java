package com.mine.musharing.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import com.mine.musharing.bases.User;

/**
 * <h1>用户库</h1>
 *
 * 用来处理有关用户编码问题的工具
 */
public class UserUtil {

	/**
	 * 代表添加新成员的特殊用户对象
	 */
	public final static User addMemberSign = new User("add_member", "添加朋友", "https://cdn.pixabay.com/photo/2016/03/21/05/05/plus-1270001_1280.png");

	/**
	 * 计算 password 用 userNameBase64 加盐后 md5 加密值 (hmac)
	 * @param nameBase64 Base64编码后的用户名
	 * @param password_text 原始密码字符串
	 * @return 加密后的密码十六进制字符串表示
	 */
	public static String encryptPassword(String nameBase64, String password_text) {
		Mac mac = null;
		try {
			mac = Mac.getInstance("HmacMD5");
			SecretKeySpec secret_key = new SecretKeySpec(nameBase64.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
			mac.init(secret_key);
			byte[] result = mac.doFinal(password_text.getBytes(StandardCharsets.UTF_8));
			return  Utility.bytesToHex(result);		// 转化为十六进制字符串串表示
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取用户名的 Base64 编码
	 * @param name_text 原始用户名字符串
	 * @return Base64编码后的用户名字符串表示
	 */
	public static String encodeName(String name_text) {
		byte[] data = name_text.getBytes(StandardCharsets.UTF_8);
		String base64 = Base64.encodeToString(data, Base64.DEFAULT);
		return base64;
	}

	/**
	 * 解码名字的 Base64
	 * @param name_base64 Base64编码后的用户名字符串表示
	 * @return 解码后的用户名字符串
	 */
	public static String decodeName(String name_base64) {
		byte[] data = Base64.decode(name_base64, Base64.DEFAULT);
		String text = new String(data, StandardCharsets.UTF_8);
		return text;
	}

}
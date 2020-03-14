package com.mine.musharing.utils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <h1>请求库</h1>
 * <p>把 app 中需要请求（阻塞式）的方法整合到一起。</p>
 * <em>调用时注意线程问题，不要在 主/UI 线程中调用</em>
 */
public class RequestUtil {

	/**
	 * 服务器地址
	 */
	public static final String SERVER_URL = "http://39.107.75.19:5000";			// Service
//	public static final String SERVER_URL = "http://192.168.43.214:5000";		// local debug for back-end via emulator
//	public static final String SERVER_URL = "http://192.168.11.222:5000";		// another local address

	public static final String FORUM_LOOKAROUND_URL = "http://39.107.75.19:1080/musharing-forums/forum/musharing-lookaround/";
	public static final String FORUM_FEEDBACK_URL = "http://39.107.75.19:1080/musharing-forums/forum/musharing-feedback/";

	/**
	 * Unexpected Json
	 *
	 * <p>Return this when something wrong about network</p>
	 */
	private static final String NETWORK_ERROR_JSON = "{\"error\":\""+ ParseUtil.ResponseError.NETWORK_ERROR +"\",\"debug\":\"Network Error\"}";

	/**
	 * 注册的网络请求
	 * @param nameEncoded Base64编码后的用户名
	 * @param passwordEncrypted 加密后的密码
	 * @param img 用户头像Url
	 * @return response json
	 */
	public static String register(String nameEncoded, String passwordEncrypted, String img) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("name", nameEncoded)
				.add("password", passwordEncrypted)
				.add("img", img)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/register")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 登录的网络请求
	 * @param nameEncoded Base64编码后的用户名
	 * @param passwordEncrypted 加密后的密码
	 * @return response json
	 */
	public static String login(String nameEncoded, String passwordEncrypted) {
		OkHttpClient client = new OkHttpClient.Builder()
				.retryOnConnectionFailure(true)
				.build();
		RequestBody requestBody = new FormBody.Builder()
				.add("name", nameEncoded)
				.add("password", passwordEncrypted)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/login")
				.post(requestBody)
				.addHeader("Connection", "close")
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * ## 查询指定用户所处Room中成员名单的网络请求
	 * @param uid 要查询的用户的uid
	 * @return response json
	 */
	public static String members(String uid) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/members")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 拉其他用户加入自己所在 Room 的网络请求
	 * @param uid 当前用户uid
	 * @param targetName 欲拉取的用户名
	 * @return response json
	 */
	public static String attend(String uid, String targetName) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.add("target_name", targetName)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/attend")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 发送消息到Room中的网络请求
	 * @param uid 当前用户uid
	 * @param msg 消息json
	 * @return response json
	 */
	public static String send(String uid, String msg) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.add("msg", msg)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/send")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 接收消息的网络请求
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String receive(String uid) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/receive")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 离开Room的网络请求
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String leave(String uid) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/leave")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 退出登录的网络请求
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String logout(String uid) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/logout")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * ## 获取 categories (可选的 playlist) 的网络请求
	 * @param uid 用户的uid
	 * @return response json
	 */
	public static String category(String uid) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/category")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * ## 获取指定 Playlist 的网络请求
	 * @param uid 用户的uid
	 * @param playlistId 欲获取的 playlist 的 id
	 * @return response json
	 */
	public static String playlist(String uid, String playlistId) {
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = new FormBody.Builder()
				.add("from_uid", uid)
				.add("playlist_id", playlistId)
				.build();
		Request request = new Request.Builder()
				.url(SERVER_URL + "/playlist")
				.post(requestBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 接收通知的网络请求
	 *
	 * ⚠️注意，与其他的请求不同，这个方法是 GET！
	 *
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String notice(String uid) {
		OkHttpClient client = new OkHttpClient();

		// 拼接url＋queryString
		HttpUrl.Builder urlBuilder = HttpUrl.parse(SERVER_URL + "/notice").newBuilder();
		urlBuilder.addQueryParameter("from_uid", uid);

		Request request = new Request.Builder()
				.url(urlBuilder.build())
				.get()
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (response.isSuccessful() && response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return NETWORK_ERROR_JSON;
	}

	/**
	 * 获取 chatbot 回复的网络请求
	 *
	 * @param statement 要让机器人回复的话
	 * @return response body
	 */
	public static String chatbot(String statement) {
		// TODO: implement chatbot request
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
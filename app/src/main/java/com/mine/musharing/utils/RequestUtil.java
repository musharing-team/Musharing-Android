package com.mine.musharing.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	public static final String SERVER_URL = "http://39.107.75.19:5000";            // Service
//	public static final String SERVER_URL = "http://192.168.43.214:5000";		// local debug for back-end via emulator
//	public static final String SERVER_URL = "http://192.168.11.222:5000";		// another local address

	public static final String FORUM_LOOKAROUND_URL = "http://39.107.75.19:1080/musharing-forums/forum/musharing-lookaround/";
	public static final String FORUM_FEEDBACK_URL = "http://39.107.75.19:1080/musharing-forums/forum/musharing-feedback/";

	public static final String CHATBOT_API_URL = "http://39.107.75.19/api/chatbot";

	/**
	 * Unexpected Json
	 *
	 * <p>Return this when something wrong about network</p>
	 */
	private static final String NETWORK_ERROR_JSON = "{\"error\":\"" + ParseUtil.ResponseError.NETWORK_ERROR + "\",\"debug\":\"Network Error\"}";

	/**
	 * 注册的网络请求
	 * @param nameEncoded Base64编码后的用户名
	 * @param passwordEncrypted 加密后的密码
	 * @param img 用户头像Url
	 * @return response json
	 */
	public static String register(String nameEncoded, String passwordEncrypted, String img) {
		String url = SERVER_URL + "/register";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("name", nameEncoded);
		postFormBody.put("password", passwordEncrypted);
		postFormBody.put("img", img);

		return post(url, postFormBody);
	}

	/**
	 * 登录的网络请求
	 * @param nameEncoded Base64编码后的用户名
	 * @param passwordEncrypted 加密后的密码
	 * @return response json
	 */
	public static String login(String nameEncoded, String passwordEncrypted) {
//		OkHttpClient client = new OkHttpClient.Builder()
//				.retryOnConnectionFailure(true)
//				.build();
//		RequestBody requestBody = new FormBody.Builder()
//				.add("name", nameEncoded)
//				.add("password", passwordEncrypted)
//				.build();
//		Request request = new Request.Builder()
//				.url(SERVER_URL + "/login")
//				.post(requestBody)
//				.addHeader("Connection", "close")
//				.build();
//		return requestResponse(client, request);

		String url = SERVER_URL + "/login";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("name", nameEncoded);
		postFormBody.put("password", passwordEncrypted);

		return post(url, postFormBody);
	}

	/**
	 * ## 查询指定用户所处Room中成员名单的网络请求
	 * @param uid 要查询的用户的uid
	 * @return response json
	 */
	public static String members(String uid) {
		String url = SERVER_URL + "/members";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);

		return post(url, postFormBody);
	}

	/**
	 * 拉其他用户加入自己所在 Room 的网络请求
	 * @param uid 当前用户uid
	 * @param targetName 欲拉取的用户名
	 * @return response json
	 */
	public static String attend(String uid, String targetName) {
		String url = SERVER_URL + "/attend";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);
		postFormBody.put("target_name", targetName);

		return post(url, postFormBody);
	}

	/**
	 * 发送消息到Room中的网络请求
	 * @param uid 当前用户uid
	 * @param msg 消息json
	 * @return response json
	 */
	public static String send(String uid, String msg) {
		String url = SERVER_URL + "/send";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);
		postFormBody.put("msg", msg);

		return post(url, postFormBody);
	}

	/**
	 * 接收消息的网络请求
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String receive(String uid) {
		String url = SERVER_URL + "/receive";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);

		return post(url, postFormBody);
	}

	/**
	 * 离开Room的网络请求
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String leave(String uid) {
		String url = SERVER_URL + "/leave";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);

		return post(url, postFormBody);
	}

	/**
	 * 退出登录的网络请求
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String logout(String uid) {
		String url = SERVER_URL + "/logout";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);

		return post(url, postFormBody);
	}

	/**
	 * ## 获取 categories (可选的 playlist) 的网络请求
	 * @param uid 用户的uid
	 * @return response json
	 */
	public static String category(String uid) {
		String url = SERVER_URL + "/category";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);

		return post(url, postFormBody);
	}

	/**
	 * ## 获取指定 Playlist 的网络请求
	 * @param uid 用户的uid
	 * @param playlistId 欲获取的 playlist 的 id
	 * @return response json
	 */
	public static String playlist(String uid, String playlistId) {
		String url = SERVER_URL + "/playlist";

		Map<String, String> postFormBody = new HashMap<>();
		postFormBody.put("from_uid", uid);
		postFormBody.put("playlist_id", playlistId);

		return post(url, postFormBody);
	}

	/**
	 * 接收通知的网络请求
	 *
	 * GET 请求
	 *
	 * @param uid 当前用户uid
	 * @return response json
	 */
	public static String notice(String uid) {
		String url = SERVER_URL + "/notice";

		Map<String, String> queryParameter = new HashMap<>();
		queryParameter.put("from_uid", uid);

		return get(url, queryParameter);
	}

	/**
	 * 获取 chatbot 回复的网络请求
	 * <p>
	 * GET 请求
	 *
	 * @param statement 要让机器人回复的话
	 * @return response body
	 */
	public static String chatbot(String statement) {
		String url = CHATBOT_API_URL + "/chatbot/get_response";

		Map<String, String> queryParameter = new HashMap<>();
		queryParameter.put("chat", statement);

		return get(url, queryParameter);
	}

	/**
	 * get is a method request a GET, returns the response body
	 *
	 * @param url            GET url
	 * @param queryParameter GET Query Parameter
	 * @return response body toString if success or NETWORK_ERROR_JSON else
	 */
	private static String get(String url, Map<String, String> queryParameter) {
		OkHttpClient client = new OkHttpClient();

		// 拼接url＋queryString
		HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
		for (String key : queryParameter.keySet()) {
			urlBuilder.addQueryParameter(key, queryParameter.get(key));
		}

		Request request = new Request.Builder()
				.url(urlBuilder.build())
				.get()
				.build();
		return requestResponse(client, request);
	}

	/**
	 * post is a method request a POST, returns the
	 *
	 * @param url          POST url
	 * @param postFormBody POST Form Body
	 * @return response body toString if success or NETWORK_ERROR_JSON else
	 */
	private static String post(String url, Map<String, String> postFormBody) {
		OkHttpClient client = new OkHttpClient();

		FormBody.Builder requestBodyBuilder = new FormBody.Builder();
		for (String key : postFormBody.keySet()) {
			requestBodyBuilder.add(key, postFormBody.get(key));
		}
		RequestBody requestBody = requestBodyBuilder.build();

		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		return requestResponse(client, request);
	}

	/**
	 * requestResponse is a method exec a OkHttp request and returns the response
	 *
	 * @param client  OkHttpClient of a request
	 * @param request Request
	 * @return response body toString if success or NETWORK_ERROR_JSON else
	 */
	private static String requestResponse(OkHttpClient client, Request request) {
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
}
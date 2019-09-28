package com.mine.musharing.utils;

import java.io.IOException;

import okhttp3.FormBody;
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
	public static final String SERVER_URL = "http://39.107.75.19:5000";
	// public static final String SERVER_URL = "http://10.0.2.2:5000";		// local debug for back-end via emulator

	/**
	 * 注册的网络请求
	 * @param nameEncoded Base64编码后的用户名
	 * @param passwordEncrypted 加密后的密码
	 * @param img 用户头像Url
	 * @return <pre>response json:
	 * 	- `{uid, name, img}`: 成功注册，返回用户uid、name、img
	 *
	 * 	- `{"error": "UserNameError"}`: 用户已存在，注册失败
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

	/**
	 * 登录的网络请求
	 * @param nameEncoded Base64编码后的用户名
	 * @param passwordEncrypted 加密后的密码
	 * @return <pre>response json:
	 * 	- `{uid, name, img}`: 成功登录，返回用户uid、name、img
	 *
	 * 	- `{"error": "UserNameError"}`: 不存在的用户名
	 * 	- `{"error": "PasswordError"}`: 密码错误
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

	/**
	 * ## 查询指定用户所处Room中成员名单的网络请求
	 * @param uid 要查询的用户的uid
	 * @return <pre>response json:
	 * 	- `{"members": [{uid, name, img}, {...}, ...]}`: 请求成功，返回由成员用户uid、name、img组成的Array
	 *
	 * 	- `{"error": "UidError"}`: 发起用户uid不存在
	 * 	- `{"error": "UserNotLogin"}`: 发起用户登录状态为False
	 * 	- `{"error": "UserNotInGroup"}`: 发起用户未处于Room中
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

	/**
	 * 拉其他用户加入自己所在Room的网络请求
	 * @param uid 当前用户uid
	 * @param targetName 欲拉取的用户名
	 * @return <pre>response json:
	 * 	- `{"successful": "attend"}`: 请求成功
	 *
	 * 	- `{"error": "UidError"}`: 发起用户uid不存在
	 * 	- `{"error": "UserNotLogin"}`: 发起用户登录状态为False
	 * 	- `{"error": "UserNotInGroup"}`: 发起用户未处于Room中
	 *
	 * 	- `{"error": "UserNameError"}`: 目标用户名不存在
	 * 	- `{"error": "TargetUserNotLogin"}`: 目标用户登录状态为False
	 * 	- `{"error": "TargetUserInGroup"}`: 目标用户已身处某Room
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

	/**
	 * 发送消息到Room中的网络请求
	 * @param uid 当前用户uid
	 * @param msg 消息json
	 * @return <pre>response json:
 	 * 	- `{"successful": "sent"}`: 请求成功
 	 *
	 * 	- `{"error": "UidError"}`: 发起用户uid不存在
	 * 	- `{"error": "UserNotLogin"}`: 发起用户登录状态为False
	 * 	- `{"error": "UserNotInGroup"}`: 发起用户未处于Room中
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

	/**
	 * 接收消息的网络请求
	 * @param uid 当前用户uid
	 * @return <pre>response json:
	 * 	- `{"messages": ["MsgJson", "...", ...]}`: 请求成功，返回由Msg.toString()得到的json格式字符串组成的Array
	 *
	 * 	- `{"error": "UidError"}`: 发起用户uid不存在
	 * 	- `{"error": "UserNotLogin"}`: 发起用户登录状态为False
	 * 	- `{"error": "UserNotInGroup"}`: 发起用户未处于Room中
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

	/**
	 * 离开Room的网络请求
	 * @param uid 当前用户uid
	 * @return <pre>response json:
	 * 	- `{"successful": 'left'}`: 请求成功
	 *
	 * 	- `{"error": "UidError"}`: 发起用户uid不存在
	 * 	- `{"error": "UserNotLogin"}`: 发起用户登录状态为False
	 * 	- `{"error": "UserNotInGroup"}`: 发起用户未处于Room中
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

	/**
	 * 退出登录的网络请求
	 * @param uid 当前用户uid
	 * @return <pre>response json:
	 * 	- `{"successful": 'logout'}`: 请求成功
	 *
	 * 	- `{"error": "UidError"}`: 发起用户uid不存在
	 * 	- `{"error": "UserNotLogin"}`: 发起用户登录状态为False
	 * 	- `{"error": "UserNotInGroup"}`: 发起用户未处于Room中
	 *
	 * 	- `{"error": "Unexpected"}`: 未知错误
	 * 	</pre>
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
			if (response.body() != null) {
				return response.body().string();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"error\": \"Unexpected\"}";
	}

}
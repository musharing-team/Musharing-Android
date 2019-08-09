package com.mine.musharing.utils;

import com.google.gson.Gson;
import com.mine.musharing.bases.Msg;
import com.mine.musharing.bases.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>解析库</h1>
 *
 * <p>对请求得到的响应的解析工具集合</p>
 */
public class ParseUtil {

    /**
     * <h2>服务错误</h2>
     *
     * 在解析过程中发现响应中含有"error"字段时，抛出携带详细错误信息的这种错误
     */
    public static class ResponseError extends RuntimeException {
        public ResponseError(String message) {
            super(message);
        }
    }

    /**
     * <h2>解析 登录/注册 的结果</h2>
     *
     * 二者在请求成功时返回数据样式相同，共用一个解析函数
     *
     * @param responseText 请求的响应文本
     * @return 对应操作的User对象，或null(表示请求失败)
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static User loginAndRegisterResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);

            if (jsonObject.has("uid")) {
                String uid = jsonObject.getString("uid");
                String name = UserUtil.decodeName(jsonObject.getString("name"));
                String img = jsonObject.getString("img");

                return new User(uid, name, img);

            } else if (jsonObject.has("error")) {
                String errorInfo = jsonObject.getString("error");
                throw new ResponseError(errorInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <h2>解析 请求Room成员 的结果</h2>
     *
     * @param responseText 请求的响应文本
     * @return <p>当前所在Room成员列表({@code List<User>})</p>
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static List<User> memberResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            if (jsonObject.has("members")) {
                JSONArray members = jsonObject.getJSONArray("members");
                List<User> memberList = new ArrayList<>();

                for (int i = 0; i < members.length(); i++) {
                    JSONObject memberJSONObject = members.getJSONObject(i);

                    String uid = memberJSONObject.getString("uid");
                    String name = UserUtil.decodeName(memberJSONObject.getString("name"));
                    String img = memberJSONObject.getString("img");

                    User member = new User(uid, name, img);
                    memberList.add(member);
                }

                return memberList;

            } else if (jsonObject.has("error")) {
                String errorInfo = jsonObject.getString("error");
                throw new ResponseError(errorInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <h2>解析 获取新消息 的结果</h2>
     *
     * @param responseText 请求的响应文本
     * @return <p>新消息列表({@code List<Msg>})</p>
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static List<Msg> receiveResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            if (jsonObject.has("messages")) {
                Gson gson = new Gson();
                JSONArray newMessages = jsonObject.getJSONArray("messages");
                List<Msg> newMsgList = new ArrayList<>();

                for (int i = 0; i < newMessages.length(); i++) {
                    Msg msg = gson.fromJson((String) newMessages.get(i), Msg.class);
                    newMsgList.add(msg);
                }

                return newMsgList;

            } else if (jsonObject.has("error")) {
                String errorInfo = jsonObject.getString("error");
                throw new ResponseError(errorInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <h2>解析 添加Room成员/发送消息/离开Room/退出登录 的结果</h2>
     *
     * 这些请求响应相对简单且相似，成功时都是返回一个{@code `{"successful": "..."}`}，共用一个解析函数
     *
     * @param responseText 请求的响应文本
     * @return 一个代表成功的字符串，或null(表示请求失败)
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static String simpleResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            if (jsonObject.has("successful")) {
                String succesful = jsonObject.getString("successful");
                return succesful;

            } else if (jsonObject.has("error")) {
                String errorInfo = jsonObject.getString("error");
                throw new ResponseError(errorInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

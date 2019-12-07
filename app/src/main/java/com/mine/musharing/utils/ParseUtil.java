package com.mine.musharing.utils;

import com.google.gson.Gson;
import com.mine.musharing.models.Category;
import com.mine.musharing.models.Msg;
import com.mine.musharing.models.Music;
import com.mine.musharing.models.Notice;
import com.mine.musharing.models.Playlist;
import com.mine.musharing.models.User;

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
     * 在解析过程中发现响应中含有"error"字段时，抛出携带详细错误信息的这种错误。
     *
     * 同时，存放了所有可能的错误码。
     */
    public static class ResponseError extends RuntimeException {

        public ResponseError(String message) {
            super(message);
        }

        public static final String NETWORK_ERROR = "network_error";

        public static final String UNEXPECTED = "unexpected";

        public static final String FROM_NOT_EXIST = "from_not_exist";
        public static final String FROM_NOT_LOGIN = "from_not_login";
        public static final String FROM_NOT_IN_ROOM = "from_not_in_room";

        public static final String TARGET_NOT_EXIST = "target_not_exist";
        public static final String TARGET_NOT_LOGIN = "target_not_login";
        public static final String TARGET_IN_ROOM = "target_in_room";

        public static final String FAIL_TO_GET_INDEX = "fail_to_get_index";
        public static final String PLAYLIST_NOT_EXIST = "playlist_not_exist";

        public static final String NAME_OCCUPIED = "name_occupied";
        public static final String WRONG_NAME = "wrong_name";
        public static final String WRONG_PASSWORD = "wrong_password";
    }

    /**
     * <h2>解析 login, register 的结果</h2>
     *
     * 二者在请求成功时返回数据样式相同，共用一个解析函数
     *
     * @param responseText 请求的响应文本
     * @return 对应操作的User对象，或null(表示请求失败)
     * @throws ResponseError 当响应 json 中包含 error 字段时，抛出携带错误信息的 ResponseError
     */
    public static User loginAndRegisterResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);

            if (jsonObject.has("response")) {
                JSONObject responseObject = jsonObject.getJSONObject("response");

                String uid = responseObject.getString("uid");
                String name = UserUtil.decodeName(responseObject.getString("name"));
                String img = responseObject.getString("img");

                return new User(uid, name, img);

            } else if (jsonObject.has("error")) {
                String errorCode = jsonObject.getJSONObject("error").getString("error");
                throw new ResponseError(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new ResponseError(ResponseError.UNEXPECTED);
    }

    /**
     * <h2>解析 请求 Room 成员 的结果</h2>
     *
     * @param responseText 请求的响应文本
     * @return <p>当前所在Room成员列表({@code List<User>})</p>
     * @throws ResponseError 当响应 json 中包含 error 字段时，抛出携带错误信息的 ResponseError
     */
    public static List<User> memberResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);

            if (jsonObject.has("response")) {

                JSONArray members = jsonObject.getJSONObject("response").getJSONArray("members");
                List<User> memberList = new ArrayList<>();

                for (int i = 0; i < members.length(); i++) {
                    JSONObject m = members.getJSONObject(i);

                    String uid = m.getString("uid");
                    String name = UserUtil.decodeName(m.getString("name"));
                    String img = m.getString("img");

                    User member = new User(uid, name, img);
                    memberList.add(member);
                }

                return memberList;

            } else if (jsonObject.has("error")) {
                String errorCode = jsonObject.getJSONObject("error").getString("error");
                throw new ResponseError(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new ResponseError(ResponseError.UNEXPECTED);
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
            if (jsonObject.has("response")) {
                Gson gson = new Gson();
                JSONArray newMessages = jsonObject.getJSONObject("response").getJSONArray("messages");
                List<Msg> newMsgList = new ArrayList<>();

                for (int i = 0; i < newMessages.length(); i++) {
                    Msg msg = gson.fromJson((String) newMessages.get(i), Msg.class);
                    newMsgList.add(msg);
                }

                return newMsgList;

            } else if (jsonObject.has("error")) {
                String errorCode = jsonObject.getJSONObject("error").getString("error");
                throw new ResponseError(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new ResponseError(ResponseError.UNEXPECTED);
    }

    /**
     * <h2>解析 attend, send, leave, logout 的结果</h2>
     *
     * 这些请求响应相对简单且相似，成功时 response 都是 {@code `{"success": "..."}`}，共用一个解析函数
     *
     * @param responseText 请求的响应文本
     * @return 一个代表成功的字符串，或null(表示请求失败)
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static String simpleResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            
            if (jsonObject.has("response")) {
                return jsonObject.getJSONObject("response").getString("success");

            } else if (jsonObject.has("error")) {
                String errorCode = jsonObject.getJSONObject("error").getString("error");
                throw new ResponseError(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new ResponseError(ResponseError.UNEXPECTED);
    }

    /**
     * <h2>解析 获取 category 的结果</h2>
     *
     * @param responseText 请求的响应文本
     * @return <p>category 列表({@code List<Category>})</p>
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static List<Category> categoryResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            
            if (jsonObject.has("response")) {
                JSONArray categories = jsonObject.getJSONObject("response").getJSONArray("categories");
                List<Category> categoryList = new ArrayList<>();

                // 把 categories 中的各条数据变成独立的 json 字符串，交给 gson 解析成 Category 对象：
                Gson gson = new Gson();
                for (int i = 0; i < categories.length(); i++) {
                    Category category = gson.fromJson(categories.getJSONObject(i).toString(), Category.class);
                    categoryList.add(category);
                }

                return categoryList;

            } else if (jsonObject.has("error")) {
                String errorCode = jsonObject.getJSONObject("error").getString("error");
                throw new ResponseError(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new ResponseError(ResponseError.UNEXPECTED);
    }

    /**
     * <h2>解析对服务器的 Playlist 请求结果</h2>
     *
     * @param responseText 请求的响应文本
     * @return <p>解析出的 Playlist 对象</p>
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static Playlist playlistResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);

            if (jsonObject.has("response")) {
                JSONObject contentJson = jsonObject.getJSONObject("response");

                return playlistContentParse(contentJson.toString());

            } else if (jsonObject.has("error")) {
                String errorCode = jsonObject.getJSONObject("error").getString("error");
                throw new ResponseError(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new ResponseError(ResponseError.UNEXPECTED);
    }

    /**
     * <h2>解析 Playlist Msg 的 Content</h2>
     *
     * <em>注意，这个不是解析 response 的！这个是负责解析 playlist request 的 response 里的内容，以及 playlist msg 里的 content 的！</em>
     *
     * @param content 收到的 Playlist 的 content
     * @return 解析出的 Playlist 对象
     * @throws JSONException 解析 Json 出错
     */
    public static Playlist playlistContentParse(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            Gson gson = new Gson();
            Playlist playlist = new Playlist();

            playlist.setId(jsonObject.getString("id"));
            playlist.setSize(jsonObject.getInt("size"));

            JSONArray musicArrayJson = jsonObject.getJSONArray("music_list");
            List<Music> musicList = new ArrayList<>();
            for (int i = 0; i < musicArrayJson.length(); i++) {
                String musicString = musicArrayJson.getString(i);
                Music music = gson.fromJson(musicString, Music.class);
                musicList.add(music);
            }
            playlist.setMusicList(musicList);
            playlist.setId(String.valueOf(musicList.hashCode()));
            playlist.setContent(content);

            return playlist;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <h2>解析 获取新通知 的结果</h2>
     *
     * @param responseText 请求的响应文本
     * @return <p>新消息列表({@code List<Notice>})</p>
     * @throws ResponseError 当响应json中包含error字段时，抛出携带错误信息的ResponseError
     */
    public static List<Notice> noticeResponseParse(String responseText) throws ResponseError {
        try {
            JSONObject jsonObject = new JSONObject(responseText);
            if (jsonObject.has("response")) {
                Gson gson = new Gson();
                JSONArray newNotices = jsonObject.getJSONObject("response").getJSONArray("notices");
                List<Notice> newNoticeList = new ArrayList<>();

                for (int i = 0; i < newNotices.length(); i++) {
                    Notice notice = gson.fromJson((String) newNotices.get(i), Notice.class);
                    newNoticeList.add(notice);
                }

                return newNoticeList;

            } else if (jsonObject.has("error")) {
                String errorCode = jsonObject.getJSONObject("error").getString("error");
                throw new ResponseError(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new ResponseError(ResponseError.UNEXPECTED);
    }
}

package com.mine.musharing.requestTasks;

import com.mine.musharing.bases.User;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

/**
 * <h1>登录的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(User result)}，把登录成功得到的本用户User对象传出给listener
 */
public class LoginTask extends RequestTask<User> {

    public LoginTask(RequestTaskListener<User> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText =  RequestUtil.login(strings[0], strings[1]);   // name, password
            User user = ParseUtil.loginAndRegisterResponseParse(responseText);
            if (user != null) {
                listener.onSuccess(user);
                return RequestTask.REQUEST_SUCCESSFUL;
            }
        } catch (ParseUtil.ResponseError e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
        return RequestTask.REQUEST_FAILED;
    }
}

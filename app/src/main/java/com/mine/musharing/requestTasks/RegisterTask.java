package com.mine.musharing.requestTasks;

import com.mine.musharing.models.User;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

/**
 * <h1>注册的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(User result)}，把注册成功得到的新用户信息(封装成 User 对象)传出给listener
 */
public class RegisterTask extends RequestTask<User> {

    public RegisterTask(RequestTaskListener<User> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText =  RequestUtil.register(strings[0], strings[1], strings[2]);   // name, password, img
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

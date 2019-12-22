package com.mine.musharing.requestTasks;

import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

/**
 * <h1>获取 chatbot 回复的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(String result)}，把机器人回复的字符串传出给listener
 */
public class ChatbotTask extends RequestTask<String> {
    public ChatbotTask(RequestTaskListener<String> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText = RequestUtil.chatbot(strings[0]);   // your statement for reply
            String reply = ParseUtil.chatbotResponseParse(responseText);
            if (reply != null) {
                listener.onSuccess(reply);
                return RequestTask.REQUEST_SUCCESSFUL;
            }
        } catch (ParseUtil.ResponseError e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
        return RequestTask.REQUEST_FAILED;
    }
}

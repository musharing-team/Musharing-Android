package com.mine.musharing.requestTasks;

import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

/**
 * <h1>发送消息到当前Room的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(String result)}，把一个标志请求成功得的字符串传出给listener
 */
public class SendTask extends RequestTask<String> {

    public SendTask(RequestTaskListener<String> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText = RequestUtil.send(strings[0], strings[1]);   // uid, msg.toString()
            String successful = ParseUtil.simpleResponseParse(responseText);
            if (successful != null) {
                listener.onSuccess(successful);
                return RequestTask.REQUEST_SUCCESSFUL;
            }
        } catch (ParseUtil.ResponseError e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
        return RequestTask.REQUEST_FAILED;
    }
}

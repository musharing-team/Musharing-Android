package com.mine.musharing.requestTasks;

import com.mine.musharing.models.Msg;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>获取Room中新消息的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(List<Msg> result)}，把请求成员成功得到的新消息列表(List<Msg>)传出给listener
 */
public class ReceiveTask extends RequestTask<List<Msg>> {

    public ReceiveTask(RequestTaskListener<List<Msg>> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText =  RequestUtil.receive(strings[0]);    // uid
            List<Msg> newMsgList = ParseUtil.receiveResponseParse(responseText);
            if (newMsgList == null) {
                newMsgList = new ArrayList<>();
            }

            listener.onSuccess(newMsgList);
            return RequestTask.REQUEST_SUCCESSFUL;

        } catch (ParseUtil.ResponseError e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
        return RequestTask.REQUEST_FAILED;
    }
}

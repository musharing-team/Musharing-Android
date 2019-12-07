package com.mine.musharing.requestTasks;

import com.mine.musharing.models.Notice;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

import java.util.ArrayList;
import java.util.List;

public class NoticeTask extends RequestTask<List<Notice>> {

    public NoticeTask(RequestTaskListener<List<Notice>> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText =  RequestUtil.notice(strings[0]);    // uid
            List<Notice> newNoticeList = ParseUtil.noticeResponseParse(responseText);
            if (newNoticeList == null) {
                newNoticeList = new ArrayList<>();
            }

            listener.onSuccess(newNoticeList);
            return RequestTask.REQUEST_SUCCESSFUL;

        } catch (ParseUtil.ResponseError e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
        return RequestTask.REQUEST_FAILED;
    }
}

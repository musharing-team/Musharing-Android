package com.mine.musharing.requestTasks;

import android.text.TextUtils;

import com.mine.musharing.bases.User;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;
import com.mine.musharing.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>获取Room中的成员名单的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(List<User> result)}，把请求成员成功得到的成员列表(List<User>)传出给listener
 */
public class MemberTask extends RequestTask<List<User>> {

    public MemberTask(RequestTaskListener<List<User>> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText =  RequestUtil.members(strings[0]);    // uid
            List<User> memberList = ParseUtil.memberResponseParse(responseText);
            if (memberList == null) {
                memberList = new ArrayList<>();
            }
            // 在最后一个放加入新成员的特殊块
            memberList.add(UserUtil.addMemberSign);

            listener.onSuccess(memberList);
            return RequestTask.REQUEST_SUCCESSFUL;

        } catch (ParseUtil.ResponseError e) {
            // 处理 UserNotInGroup 的特殊情况
            if (TextUtils.equals(ParseUtil.ResponseError.FROM_NOT_IN_ROOM, e.getMessage())) {
                List<User> memberList = new ArrayList<>();
                // 在最后一个放加入新成员的特殊块
                memberList.add(UserUtil.addMemberSign);

                listener.onSuccess(memberList);
                return RequestTask.REQUEST_SUCCESSFUL;
            } else {
                e.printStackTrace();
                listener.onFailed(e.getMessage());
            }
        }
        return RequestTask.REQUEST_FAILED;
    }
}

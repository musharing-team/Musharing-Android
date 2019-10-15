package com.mine.musharing.requestTasks;

import com.mine.musharing.bases.Category;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>获取 播放列表的目录(被叫做 categoryList 😂) 的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(List<Category> result)}，把获取到的播放列表目录(the list (i.e. index) of playlists)出给listener
 */
public class CategoriesTask extends RequestTask<List<Category>> {

    public CategoriesTask(RequestTaskListener<List<Category>> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText =  RequestUtil.category(strings[0]);    // uid
            List<Category> categoryList = ParseUtil.categoryResponseParse(responseText);
            if (categoryList == null) {
                categoryList = new ArrayList<>();
            }

            listener.onSuccess(categoryList);
            return RequestTask.REQUEST_SUCCESSFUL;

        } catch (ParseUtil.ResponseError e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
        return RequestTask.REQUEST_FAILED;
    }
}

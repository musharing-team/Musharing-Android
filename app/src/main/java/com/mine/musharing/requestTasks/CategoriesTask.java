package com.mine.musharing.requestTasks;

import com.mine.musharing.bases.Category;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

import java.util.ArrayList;
import java.util.List;

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

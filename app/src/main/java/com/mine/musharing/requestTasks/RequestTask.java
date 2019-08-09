package com.mine.musharing.requestTasks;

import android.os.AsyncTask;

/**
 * <h1>请求抽象类</h1>
 *
 * <p>所有相关请求的超类，继承自{@code AsyncTask}，实现非抽象方法onPreExecute、onPostExecute，需子类实现抽象方法doInBackground</p>
 * @param <Result> listener的{@code onSuccess(Result result)} 要接收的参数类型，即请求后解析response最终得到的所需数据类型
 */
public abstract class RequestTask<Result> extends AsyncTask<String, String, String> {

    /**
     * 代表请求成功的字符串
     */
    public static final String REQUEST_SUCCESSFUL = "successful";

    /**
     * 代表请求失败的字符串
     */
    public static final String REQUEST_FAILED = "failed";

    /**
     * 用来监听请求并完成与主线程通信、UI更新的RequestTaskListener
     */
    public RequestTaskListener<Result> listener;

    public RequestTask() {
    }

    public RequestTask(RequestTaskListener<Result> listener) {
        this.listener = listener;
    }

    /**
     * 在开始请求前自动调用，在UI线程，调用listener中的onStart，更新UI，
     */
    @Override
    protected void onPreExecute() {
        listener.onStart();
    }

    /**
     * 请求结束时自动调用，在UI线程，调用listener中的onFinish，更新UI
     * @param s 表示结果的成功与否的字符串，REQUEST_SUCCESSFUL or REQUEST_FAILED
     */
    @Override
    protected void onPostExecute(String s) {
        listener.onFinish(s);
    }

    /**
     * <h3>请求和解析响应的具体逻辑</h3><br/>
     * <em>不在UI线程！调用listener设计更新UI要注意runOnUiThread！</em><br/>
     * 在得到想要的结果后调用{@code listener.onSuccess(Result result)}<br/>
     * 或是在解析失败后调用{@code listener.onFailed(String error)}
     * @param strings 请求需要的参数
     * @return 字符串s：表示结果的成功与否({@code REQUEST_SUCCESSFUL} or {@code REQUEST_FAILED})
     */
    @Override
    protected abstract String doInBackground(String... strings);
}

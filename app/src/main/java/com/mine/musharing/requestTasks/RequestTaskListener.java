package com.mine.musharing.requestTasks;

/**
 * <h1>Request Tasks Listener Interface</h1>
 * @param <Result> the type of onSuccess's param, which is the expected type of a request
 */
public interface RequestTaskListener<Result> {

    void onStart();

    void onSuccess(Result result);

    void onFailed(String error);

    void onFinish(String s);
}

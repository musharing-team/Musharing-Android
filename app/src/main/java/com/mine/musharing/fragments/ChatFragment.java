package com.mine.musharing.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.audio.HotLineRecorder;
import com.mine.musharing.audio.PlayAsyncer;
import com.mine.musharing.bases.Msg;
import com.mine.musharing.bases.User;
import com.mine.musharing.recyclerViewAdapters.MsgAdapter;
import com.mine.musharing.requestTasks.ReceiveTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.requestTasks.SendTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;

/**
 * <h1>聊天碎片</h1>
 * 提供聊天功能，界面包括房间中的消息列表以及输入、发送框
 */
public class ChatFragment extends Fragment {

    private View chatFragmentView;

    private RecyclerView msgRecyclerView;

    private FloatingActionButton sendButton;

    private FloatingActionButton moreInputButton;

    private ImageButton hotLineButton;

    private HotLineRecorder hotLineRecorder;

    private User user;

    private List<Msg> mMsgList = new ArrayList<>();

    private MsgAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get User
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        } else {
            Toast.makeText(getContext(), "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        // Inflate the layout for this fragment
        chatFragmentView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Set the hotLineRecorder
        hotLineRecorder = HotLineRecorder.getInstance();
        hotLineRecorder.setUser(user);

        // Buttons and their onClick
        sendButton = chatFragmentView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this::sendOnClick);

        moreInputButton = chatFragmentView.findViewById(R.id.more_input_button);
        moreInputButton.setOnClickListener(this::moreInputOnClick);

        hotLineButton = chatFragmentView.findViewById(R.id.hot_line_button);
        hotLineButton.setOnTouchListener(this::hotLineOnTouch);

        // message recycler view
        msgRecyclerView = chatFragmentView.findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(user, mMsgList);
        msgRecyclerView.setAdapter(adapter);

        return chatFragmentView;
    }

    /**
     * 点击发送按钮的事件
     */
    public void sendOnClick(View view) {
        EditText editText = chatFragmentView.findViewById(R.id.input_text);
        String content = editText.getText().toString();
        if (!"".equals(content)) {
            // TODO(b04) Different kinds of Msg
            Msg msg = new Msg(Msg.TYPE_TEXT, user, content);

            new SendTask(new RequestTaskListener<String>() {
                @Override
                public void onStart() {}

                @Override
                public void onSuccess(String s) {
                    getActivity().runOnUiThread(() -> {
                        editText.setText("");
                    });
                }

                @Override
                public void onFailed(String error) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFinish(String s) {}

            }).execute(user.getUid(), msg.toString());
        }
    }

    /**
     * 点击更多输入的事件
     */
    public void moreInputOnClick(View view) {
        Toast.makeText(getContext(), "未完成的功能", Toast.LENGTH_SHORT).show();
    }

    /**
     * 按住 HotLine 的事件
     */
    public boolean hotLineOnTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "HotLine button -> down");
                try {
                    hotLineRecorder.startRecord();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    hotLineRecorder.reset();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "HotLine button -> up");
                try {
                    hotLineRecorder.stopRecord();
                    hotLineRecorder.publishRecord();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    hotLineRecorder.reset();
                }

                break;

        }
        return false;
    }

    /**
     * 显示一条消息
     * @param msg
     */
    public void showTextMsg(Msg msg) {
        mMsgList.add(0, msg);
        adapter.notifyItemInserted(0); // 有新消息,刷新显示
        msgRecyclerView.scrollToPosition(0);   // 移动到最新一条消息
    }

    /**
     * 移除过多的消息
     */
    public void clearSurplusMsgs(int retainCount) {
        int removed = 0;
        while (mMsgList.size() > retainCount) {
            mMsgList.remove(mMsgList.size() - 1);
            removed++;
        }
        adapter.notifyItemRangeRemoved(retainCount + 1, removed);
    }
}

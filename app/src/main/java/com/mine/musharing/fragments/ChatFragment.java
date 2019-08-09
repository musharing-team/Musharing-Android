package com.mine.musharing.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.asyncPlayer.PlayAsyncer;
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

import de.hdodenhof.circleimageview.CircleImageView;

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

    private User user;

    private List<Msg> mMsgList = new ArrayList<>();

    private MsgAdapter adapter;

    private Timer mTimer;

    private TimerTask refreshMsgTimerTask;

    private static final long REFRESH_PERIOD = 2000;

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

        // Buttons and their onClick
        sendButton = chatFragmentView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(this::sendOnClick);

        moreInputButton = chatFragmentView.findViewById(R.id.more_input_button);
        moreInputButton.setOnClickListener(this::moreInputOnClick);

        // message recycler view
        msgRecyclerView = chatFragmentView.findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(user, mMsgList);
        msgRecyclerView.setAdapter(adapter);

        // Refresh the message list periodically
        mTimer = new Timer();
        refreshMsgTimerTask = new TimerTask() {
            @Override
            public void run() {
                refreshMsgs();
            }
        };
        mTimer.schedule(refreshMsgTimerTask, 0, REFRESH_PERIOD);

        return chatFragmentView;
    }

    private void refreshMsgs() {

        new ReceiveTask(new RequestTaskListener<List<Msg>>() {
            @Override
            public void onStart() {}

            @Override
            public void onSuccess(List<Msg> newMsgs) {
                if (!newMsgs.isEmpty()) {
                    Log.d(TAG, "ChatFragment refreshMsgs: new Msgs: " + newMsgs);
                }
                getActivity().runOnUiThread(() -> {
                    for (Msg msg : newMsgs) {
                        switch (msg.getType()) {
                            case Msg.TYPE_TEXT:
                                mMsgList.add(msg);
                                // TODO:(b04) 控制消息列表大小
                                adapter.notifyItemInserted(mMsgList.size() - 1); // 有新消息,刷新显示
                                msgRecyclerView.scrollToPosition(mMsgList.size() - 1);   // 移动到最后一条消息
                                break;
                            case Msg.TYPE_PLAYER_ASYNC:
                                PlayAsyncer.getInstance().handleAsyncMsg(msg);
                                break;
                        }
                    }
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

        }).execute(user.getUid());
    }

    public void moreInputOnClick(View view) {
        Toast.makeText(getContext(), "未完成的功能", Toast.LENGTH_SHORT).show();
    }

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
                        refreshMsgs();
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

    @Override
    public void onDestroy() {
        if (refreshMsgTimerTask != null) {
            refreshMsgTimerTask.cancel();
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroy();
    }
}

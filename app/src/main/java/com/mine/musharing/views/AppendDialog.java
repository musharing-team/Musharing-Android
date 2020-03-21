package com.mine.musharing.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mine.musharing.R;
import com.mine.musharing.fragments.RoomFragment;
import com.mine.musharing.requestTasks.AttendTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.utils.ParseUtil;

public class AppendDialog extends Dialog {
    private Context context;

    private RoomFragment parentRoomFragment;

    private String uid;

    public AppendDialog(Context context, RoomFragment parent, String uid) {
        super(context);
        parentRoomFragment = parent;
        this.uid = uid;
        this.setTitle("添加朋友");
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        build();
    }

    private void build() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.append_dialog, null);

        SearchView friendSearchView = view.findViewById(R.id.friend_search_view);
        friendSearchView.setIconified(false);
        friendSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                attendRequest(uid, s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        ((RelativeLayout) view.findViewById(R.id.my_qr)).setOnClickListener(v -> myQrCode());

        ((RelativeLayout) view.findViewById(R.id.face_to_face)).setOnClickListener(v -> faceToFace());

        ((RelativeLayout) view.findViewById(R.id.attend_chatbot)).setOnClickListener(v -> attendChatbot());

        ((Button) view.findViewById(R.id.cancel_append_button)).setOnClickListener(v -> dismiss());
        
        setContentView(view);
    }

    /**
     * 点击我的二维码
     */
    private void myQrCode() {
        // TODO: Implement this
        Toast.makeText(context, "未完成的功能", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    /**
     * 点击面对面建群
     */
    private void faceToFace() {
        // TODO: Implement this
        Toast.makeText(context, "未完成的功能", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    /**
     * 点击添加机器人
     */
    private void attendChatbot() {
        attendRequest(uid, "chatbot");
    }

    /**
     * 发送添加人的请求
     *
     * @param uid
     * @param targetNameEncoded
     */
    private void attendRequest(String uid, String targetNameEncoded) {

        new AttendTask(new RequestTaskListener<String>() {
            @Override
            public void onStart() {
                // progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(String s) {
                parentRoomFragment.refreshMemberList();
            }

            @Override
            public void onFailed(String error) {
                parentRoomFragment.getActivity().runOnUiThread(() -> {
                    String readableError;
                    switch (error) {
                        case ParseUtil.ResponseError.TARGET_IN_ROOM:
                            readableError = "你的朋友正在别的房间中，不可以打扰ta哦。";
                            break;
                        case ParseUtil.ResponseError.TARGET_NOT_LOGIN:
                            readableError = "你的朋友没有登录，联系不上ta。";
                            break;
                        case ParseUtil.ResponseError.TARGET_NOT_EXIST:
                            readableError = "在 Musharing 家族中没有这个人。。。";
                            break;
                        case ParseUtil.ResponseError.FROM_NOT_LOGIN:
                            readableError = "请先登录。";
                            break;
                        case ParseUtil.ResponseError.FROM_NOT_EXIST:
                            readableError = "错误！发起用户不存在。";
                            break;
                        default:
                            readableError = "出错啦，请稍后再试TAT";
                    }
                    Toast.makeText(parentRoomFragment.getActivity(), readableError, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {
                dismiss();
            }
        }).execute(uid, targetNameEncoded);
    }
}

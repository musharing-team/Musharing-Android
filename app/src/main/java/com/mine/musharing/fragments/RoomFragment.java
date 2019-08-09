package com.mine.musharing.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.bases.User;
import com.mine.musharing.recyclerViewAdapters.MemberAdapter;
import com.mine.musharing.requestTasks.AttendTask;
import com.mine.musharing.requestTasks.MemberTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.utils.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.mine.musharing.utils.UserUtil.addMemberSign;

/**
 * <h1>房间碎片</h1>
 * 显示、添加Room中的成员
 */
public class RoomFragment extends Fragment {

    private List<User> mMemberList = new ArrayList<>();

    private View view;

    private RecyclerView membersRecycleView;

    private ProgressBar progressBar;

    private MemberAdapter memberAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private User user;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_room, container, false);

        // get User
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        } else {
            Toast.makeText(getContext(), "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        // Swipe Refresh
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshMemberList());

        // Set ProgressBar
        progressBar = view.findViewById(R.id.attend_room_progressbar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        initMemberRecycleView();
        refreshMemberList();

        return view;
    }

    private void initMemberRecycleView() {
        membersRecycleView = view.findViewById(R.id.room_members_list);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        membersRecycleView.setLayoutManager(layoutManager);
        memberAdapter = new MemberAdapter(mMemberList);
        memberAdapter.setOnItemClickListener(((v, member) -> {
            // 此处进行监听事件的处理
            Toast.makeText(getContext(), member.getName(), Toast.LENGTH_SHORT).show();
            // 点击 addMember
            if (addMemberSign.equals(member)) {
                addMember();
            }
        }));
        membersRecycleView.setAdapter(memberAdapter);
    }

    private void refreshMemberList() {
        // mMemberList.clear();

        new MemberTask(new RequestTaskListener<List<User>>() {
            @Override
            public void onStart() {
                getActivity().runOnUiThread(()-> {
                    // progressBar.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(true);
                });
            }

            @Override
            public void onSuccess(List<User> users) {
                Log.d(TAG, "onSuccess: users: " + users);
                getActivity().runOnUiThread(() -> {
                    /* 注意：不能 `mMemberList = users;`
                    [参考1](https://www.cnblogs.com/xesam/archive/2012/05/31/2529056.html)
                    [参考2](https://blog.csdn.net/gufeichn/article/details/54409171)
                     */
                    mMemberList.clear();
                    mMemberList.addAll(users);

                    Log.d(TAG, "onSuccess: mMemberList" + mMemberList);
                    memberAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onSuccess: notified");
                });
                Log.d(TAG, "onSuccess: afterRunOnUiThread: " + mMemberList);
            }

            @Override
            public void onFailed(String error) {
                Log.d(TAG, "onFailed: error");
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {
                getActivity().runOnUiThread(() -> {
                    // progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        }).execute(user.getUid());

    }

    private void addMember() {
        String uid = user.getUid();

        final EditText editText = new EditText(getActivity());
        editText.setTextSize(30);
        final CardView layout = new CardView(getActivity());
        layout.setRadius(32);
        layout.setCardElevation(2);
        layout.setCardBackgroundColor(Color.WHITE);
        layout.addView(editText);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(getActivity());
        inputDialog.setTitle("请输入您朋友的用户名：").setView(layout);
        inputDialog.setPositiveButton("确定", (dialog, which) -> {
            String targetName = editText.getText().toString();
            if (TextUtils.isEmpty(targetName)) {
                return;
            } else {
                String targetNameEncoded = UserUtil.encodeName(targetName);
                attendRequest(uid, targetNameEncoded);
            }
        });

        inputDialog.show();

    }

    private void attendRequest(String uid, String targetNameEncoded) {

        new AttendTask(new RequestTaskListener<String>() {
            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(String s) {
                refreshMemberList();
            }

            @Override
            public void onFailed(String error) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {
                progressBar.setVisibility(View.GONE);
            }
        }).execute(uid, targetNameEncoded);
    }

    public List<User> getmMemberList() {
        return mMemberList;
    }
}

package com.mine.musharing.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.activities.LookaroundActivity;
import com.mine.musharing.activities.MusicChatActivity;
import com.mine.musharing.activities.SettingActivity;
import com.mine.musharing.models.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    private User user;

    private int[] buttonCardIds = {R.id.me_lookaround, R.id.me_collection, R.id.me_setting, R.id.me_feedback, R.id.me_leave};

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View meFragmentView = inflater.inflate(R.layout.fragment_me, container, false);

        // get User
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        } else {
            Toast.makeText(getContext(), "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        initView(meFragmentView);

        // Inflate the layout for this fragment
        return meFragmentView;
    }

    private void initView(View view) {
        ImageView meImg = view.findViewById(R.id.me_img);
        Glide.with(this).load(user.getImgUrl()).into(meImg);

        TextView meName = view.findViewById(R.id.me_name);
        meName.setText(user.getName());

        for (int i: buttonCardIds) {
            CardView b = view.findViewById(i);
            b.setOnClickListener(this::meFragmentButtonOnClick);
        }
    }

    public void meFragmentButtonOnClick(View view) {
        switch (view.getId()) {
            case R.id.me_setting:
                Intent intent1 = new Intent(getContext(), SettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.me_lookaround:
                Intent intent2 = new Intent(getContext(), LookaroundActivity.class);
                startActivity(intent2);
                break;
            case R.id.me_leave:
                ((MusicChatActivity)getActivity()).leaveRoom();
                break;
            default:
                Toast.makeText(getContext(), "未完成的功能", Toast.LENGTH_SHORT).show();
        }
    }

}

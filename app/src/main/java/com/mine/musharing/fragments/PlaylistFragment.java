package com.mine.musharing.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mine.musharing.Demo;
import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.User;
import com.mine.musharing.recyclerViewAdapters.MusicAdapter;
import com.mine.musharing.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>播放列表碎片</h1>
 * 显示、编辑当前播放列表
 */
public class PlaylistFragment extends Fragment {

    private User user;

    private Playlist playlist;

    private RecyclerView recyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager layoutManager;

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

        initPlaylist();

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        view = inflaterPlaylist(view);
        return  view;
    }

    private View inflaterPlaylist(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.music_list_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MusicAdapter(playlist.getMusicList());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    private void initPlaylist() {
        playlist = new Playlist(user);
        playlist.setMusicList(Demo.testMusicList);
        playlist.commit();
        // TODO: actually implement
    }

    public Playlist getPlaylist() {
        return playlist;
    }
}

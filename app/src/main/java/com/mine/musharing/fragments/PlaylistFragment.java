package com.mine.musharing.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mine.musharing.Demo;
import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.activities.RoomPlaylistActivity;
import com.mine.musharing.audio.MusicListHolder;
import com.mine.musharing.bases.Msg;
import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.SerializableList;
import com.mine.musharing.bases.User;
import com.mine.musharing.recyclerViewAdapters.MusicAdapter;
import com.mine.musharing.recyclerViewAdapters.SimpleItemTouchHelperCallback;
import com.mine.musharing.requestTasks.PlaylistTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.requestTasks.SendTask;
import com.mine.musharing.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;

/**
 * <h1>播放列表碎片</h1>
 * 显示、编辑当前播放列表
 */
public class PlaylistFragment extends Fragment {

    private User user;

    private String categoryId;

    private RecyclerView recyclerView;

    private MusicAdapter mAdapter;

    private List<Music> musicList = new ArrayList<>();

    private Playlist playlist;

    private RecyclerView.LayoutManager layoutManager;

    private ItemTouchHelper mItemTouchHelper;

    private SwipeRefreshLayout swipeRefreshLayout;

    public FloatingActionButton commitPlaylistButton;

    private Timer timer;

    private TimerTask refreshTask;

    private final static int REFRESH_PERIOD = 2000;

    private final int QUERY_PERIOD = 5;

    private int refreshCount = QUERY_PERIOD + 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get Date
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            playlist = (Playlist) getArguments().getSerializable("playlist");
            musicList = (List<Music>) getArguments().getSerializable("musiclist");
        } else {
            Toast.makeText(getContext(), "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.sparkBlueDark);
        swipeRefreshLayout.setOnRefreshListener(this::refreshPlaylist);

        commitPlaylistButton = view.findViewById(R.id.commit_playlist_button);
        commitPlaylistButton.setOnClickListener(this::commitPlayListOnClick);

        view = inflaterPlaylist(view);

        // refreshPlaylist();

        timer = new Timer();
        refreshTask = new TimerTask() {
            @Override
            public void run() {
                refreshPlaylist();
            }
        };
        timer.schedule(refreshTask, 0, REFRESH_PERIOD);

        return  view;
    }

    private View inflaterPlaylist(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.music_list_recycler_view);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MusicAdapter(musicList);
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void refreshPlaylist() {
        if (refreshCount++ > QUERY_PERIOD) {
            MusicListHolder.getInstance().queryPlaylist();
            refreshCount = 0;
        }
        if (MusicListHolder.getInstance().receiveFlag) {
            MusicListHolder.getInstance().refreshMsgs();
        }
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void commitPlayListOnClick(View view) {
        commitPlaylist();
    }

    private void commitPlaylist() {
        MusicListHolder musicListHolder = MusicListHolder.getInstance();
        musicListHolder.setPlaylist(playlist);
        musicListHolder.setUser(user);
        musicListHolder.postPlaylist();

        Intent intent = new Intent(getContext(), RoomPlaylistActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtra("data", bundle);
        startActivity(intent);
        getActivity().finish();
    }
}

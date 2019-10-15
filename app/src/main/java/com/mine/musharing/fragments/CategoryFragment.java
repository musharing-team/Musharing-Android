package com.mine.musharing.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.activities.PlaylistActivity;
import com.mine.musharing.bases.Category;
import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.SerializableList;
import com.mine.musharing.bases.User;
import com.mine.musharing.recyclerViewAdapters.CategoryAdapter;
import com.mine.musharing.requestTasks.CategoriesTask;
import com.mine.musharing.requestTasks.PlaylistTask;
import com.mine.musharing.requestTasks.RequestTaskListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * 显示可选的播放列表目录的 Fragment
 */
public class CategoryFragment extends Fragment {

    private List<Category> mCategoryList = new ArrayList<>();

    private View view;

    private RecyclerView categoriesRecycleView;

    private CategoryAdapter categoryAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private User user;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, container, false);

        // get User
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        } else {
            Toast.makeText(getContext(), "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        // Swipe Refresh
        swipeRefreshLayout = view.findViewById(R.id.category_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.sparkBlueDark);
        swipeRefreshLayout.setOnRefreshListener(this::refreshCategoryList);

        initCategoryRecycleView();
        refreshCategoryList();

        return view;
    }

    private void initCategoryRecycleView() {
        categoriesRecycleView = view.findViewById(R.id.category_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        categoriesRecycleView.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(mCategoryList);
        categoryAdapter.setOnItemClickListener(((v, category) -> {

            new PlaylistTask(new RequestTaskListener<Playlist>() {
                @Override
                public void onStart() {
                    // TODO：加载等待时的 UI 反馈，B站小电视之类的玩意儿
                }

                @Override
                public void onSuccess(Playlist s) {
                    getActivity().runOnUiThread(() -> {
                        Intent intent = new Intent(getContext(), PlaylistActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        bundle.putSerializable("category", category);
                        bundle.putSerializable("playlist", s);

                        SerializableList<Music> music = new SerializableList<>();
                        music.addAll(s.getMusicList());
                        bundle.putSerializable("musiclist", music);

                        Log.d(TAG, "onSuccess: playlist: " + s);

                        intent.putExtra("data", bundle);
                        startActivity(intent);
                    });
                }

                @Override
                public void onFailed(String error) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onFinish(String s) {
                    // TODO: UI 反馈
                }
            }).execute(user.getUid(), category.getId());

        }));
        categoriesRecycleView.setAdapter(categoryAdapter);
    }

    /**
     * 刷新（重新请求）目录
     */
    private void refreshCategoryList() {
        new CategoriesTask(new RequestTaskListener<List<Category>>() {
            @Override
            public void onStart() {
                getActivity().runOnUiThread(()-> {
                    swipeRefreshLayout.setRefreshing(true);
                });
            }

            @Override
            public void onSuccess(List<Category> categories) {
                getActivity().runOnUiThread(() -> {
                    mCategoryList.clear();
                    mCategoryList.addAll(categories);

                    categoryAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailed(String error) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {
                getActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        }).execute(user.getUid());

    }

}

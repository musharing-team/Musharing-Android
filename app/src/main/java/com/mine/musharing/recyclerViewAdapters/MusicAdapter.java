package com.mine.musharing.recyclerViewAdapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * PlaylistFragment 中 播放列表RecycleView 的 Adapter
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Music> musicList = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        View musicView;
        ImageView musicImage;
        TextView musicTitle;
        TextView musicFrom;

        public ViewHolder(View view) {
            super(view);
            musicView = view;
            musicImage = view.findViewById(R.id.music_image);
            musicTitle = view.findViewById(R.id.music_title);
            musicFrom = view.findViewById(R.id.music_from);
        }

        //选中item，改变背景颜色
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        //放开item，改变背景颜色
        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public MusicAdapter(List<Music> list) {
        musicList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.music_item, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        // 监听点击事件
        // 点击了整个 item view
        viewHolder.musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                // Music music = musicList.get(position);
                // TODO: intent to play activity
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Music music = musicList.get(i);
        // 专辑图片
        Glide.with(viewHolder.musicView).load(music.getAlbumImageUrl()).into(viewHolder.musicImage);
        // 曲目名
        viewHolder.musicTitle.setText(music.getName());
        // 音乐来源
        String from = music.getArtist() + " - " + music.getAlbum();
        viewHolder.musicFrom.setText(from);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    //交换item
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Music prev = musicList.remove(fromPosition);
        musicList.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    //删除item
    @Override
    public void onItemDismiss(int position) {
        musicList.remove(position);
        notifyItemRemoved(position);
    }
}
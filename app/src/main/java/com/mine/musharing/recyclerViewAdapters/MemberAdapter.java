package com.mine.musharing.recyclerViewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.models.User;
import com.mine.musharing.utils.Utility;

import java.util.List;

/**
 * RoomFragment 中 Room内成员RecycleView 的 Adapter
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{

    private Context mContext;

    private List<User> mMemberList;

    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            imageView = view.findViewById(R.id.member_image);
            textView = view.findViewById(R.id.member_name);
        }
    }

    public MemberAdapter(List<User> memberList) {
        mMemberList = memberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.member_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            User member = mMemberList.get(position);

            if(onItemClickListener != null){
                onItemClickListener.OnItemClick(v, member);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        // Log.d(TAG, "onBindViewHolder: mMemberList: " + mMemberList);
        User member = mMemberList.get(i);
        viewHolder.cardView.setCardBackgroundColor(Utility.randomCardColor());
        Glide.with(mContext).load(member.getImgUrl()).into(viewHolder.imageView);
        viewHolder.textView.setText(member.getName());
    }

    @Override
    public int getItemCount() {
        return mMemberList.size();
    }


    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法
         *
         * @param view 点击的item的视图
         * @param member 点击的item的数据
         */
        public void OnItemClick(View view, User member);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}

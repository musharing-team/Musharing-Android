package com.mine.musharing.recyclerViewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.models.Category;
import com.mine.musharing.utils.Utility;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * CategoryFragment 中 Categories RecycleView 的 Adapter
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private Context mContext;

    private List<Category> mCategoryList;

    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            imageView = view.findViewById(R.id.category_image);
            textView = view.findViewById(R.id.category_title);
        }
    }

    public CategoryAdapter(List<Category> categories) {
        mCategoryList = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Category category = mCategoryList.get(position);

            if(onItemClickListener != null){
                onItemClickListener.OnItemClick(v, category);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: mCategoryList: " + mCategoryList);
        Category category = mCategoryList.get(i);
        viewHolder.cardView.setCardBackgroundColor(Utility.randomCardColor());
        Glide.with(mContext).load(category.getImage()).into(viewHolder.imageView);
        viewHolder.textView.setText(category.getTitle());
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
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
        public void OnItemClick(View view, Category member);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}

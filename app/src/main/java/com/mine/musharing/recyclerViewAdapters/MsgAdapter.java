package com.mine.musharing.recyclerViewAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.models.Msg;
import com.mine.musharing.models.User;
import com.mine.musharing.utils.Utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ChatFragment 中 Room内消息RecycleView 的 Adapter
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private User selfUser;

    private List<Msg> msgList;

    public static final int SINGLE_LINE_LONG = 15;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout othersLayout;
        LinearLayout selfLayout;

        CircleImageView othersImg;
        CircleImageView selfImg;

        TextView othersMsg;
        TextView selfMsg;

        public ViewHolder(View view) {
            super(view);

            othersLayout = view.findViewById(R.id.message_others_layout);
            selfLayout = view.findViewById(R.id.message_self_layout);

            othersImg = view.findViewById(R.id.others_img);
            selfImg = view.findViewById(R.id.self_img);

            othersMsg = view.findViewById(R.id.others_msg);
            selfMsg = view.findViewById(R.id.self_msg);
        }
    }

    public MsgAdapter(User user, List<Msg> msgs) {
        selfUser = user;
        msgList = msgs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.msg_item, viewGroup, false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = msgList.get(position);
        if (selfUser.getUid().equals(msg.getFromUid())) {
            // 自己发的的消息
            holder.selfLayout.setVisibility(View.VISIBLE);
            holder.othersLayout.setVisibility(View.GONE);

            Glide.with(holder.itemView).load(msg.getFromImg()).into(holder.selfImg);
            // String content = Utility.formatText(msg.getContent(), SINGLE_LINE_LONG);
            String content = msg.getContent();
            holder.selfMsg.setText(content);
        } else {
            // 别人发出的消息
            holder.selfLayout.setVisibility(View.GONE);
            holder.othersLayout.setVisibility(View.VISIBLE);

            Glide.with(holder.itemView).load(msg.getFromImg()).into(holder.othersImg);
            // String content = Utility.formatText(msg.getContent(), SINGLE_LINE_LONG);
            String content = msg.getContent();
            holder.othersMsg.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}

package com.mine.musharing.recyclerViewAdapters;

public interface ItemTouchHelperAdapter {
    //拖动item的回调
    void onItemMove(int fromPosition, int toPosition);
    //滑动item后删除的回调
    void onItemDismiss(int position);
}

package com.mine.musharing.recyclerViewAdapters;

/**
 * 实现可拖动、滑动删除的 RecycleView 用的，不需要更改
 */
public interface ItemTouchHelperAdapter {
    //拖动item的回调
    void onItemMove(int fromPosition, int toPosition);
    //滑动item后删除的回调
    void onItemDismiss(int position);
}

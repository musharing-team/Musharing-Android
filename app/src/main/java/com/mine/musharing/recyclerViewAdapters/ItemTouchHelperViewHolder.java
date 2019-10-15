package com.mine.musharing.recyclerViewAdapters;

/**
 * 实现可拖动、滑动删除的 RecycleView 用的，不需要更改
 */
public interface ItemTouchHelperViewHolder {
    //item选中的时候的回调
    void onItemSelected();
    //item放开的时候的回调
    void onItemClear();
}

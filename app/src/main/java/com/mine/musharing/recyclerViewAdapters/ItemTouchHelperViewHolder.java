package com.mine.musharing.recyclerViewAdapters;

public interface ItemTouchHelperViewHolder {
    //item选中的时候的回调
    void onItemSelected();
    //item放开的时候的回调
    void onItemClear();
}

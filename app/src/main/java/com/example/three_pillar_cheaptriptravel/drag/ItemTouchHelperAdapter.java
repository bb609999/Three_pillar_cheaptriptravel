package com.example.three_pillar_cheaptriptravel.drag;

/**
 * Created by Administrator on 18/3/2018.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}

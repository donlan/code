package dong.lan.code.utils;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import dong.lan.code.Interface.ItemTouchListener;


public class MyItemTouchHelper extends ItemTouchHelper.Callback{




    private ItemTouchListener touchListener;


    public MyItemTouchHelper(ItemTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public void setTouchListener(ItemTouchListener listener){
        this.touchListener = listener;
    }


    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }


    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.DOWN | ItemTouchHelper.UP;
        int swipeFlag = ItemTouchHelper.END ;

        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        touchListener.onItemMoved(viewHolder.getPosition(),target.getPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        touchListener.onItemSwiped(viewHolder.getPosition());
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return super.getSwipeThreshold(viewHolder);
    }
}

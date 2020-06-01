package cn.poco.widget.recycle;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by admin on 2017/12/15.
 */

public class EntryPageItemDecoration extends RecyclerView.ItemDecoration {
    int mSpace;
    public EntryPageItemDecoration(int mSpace){
        this.mSpace = mSpace;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == 0){
            outRect.left = 0;
        }else{
            outRect.left = mSpace;
        }
//        outRect.right = mSpace;



    }


}

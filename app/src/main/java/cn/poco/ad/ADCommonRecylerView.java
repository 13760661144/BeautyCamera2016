package cn.poco.ad;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


public class ADCommonRecylerView extends RecyclerView {
    public ADCommonRecylerView(Context context) {
        super(context);
    }

    public ADCommonRecylerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ADCommonRecylerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void ScrollToCenter(int position)
    {
        if(this.getLayoutManager() instanceof LinearLayoutManager && ((LinearLayoutManager) this.getLayoutManager()).getOrientation() == LinearLayoutManager.HORIZONTAL)
        {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.getLayoutManager();
            View tempView = linearLayoutManager.findViewByPosition(position);
           if(tempView != null)
           {
               int left = tempView.getLeft();
               int shouldLeft = (int) ((this.getWidth() - tempView.getWidth())/2f);
               int dis = left - shouldLeft;
               this.smoothScrollBy(dis,0);
           }
        }
    }
}

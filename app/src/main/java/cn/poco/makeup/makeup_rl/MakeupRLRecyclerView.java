package cn.poco.makeup.makeup_rl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;

import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.recycle.RecommendDragContainer;


public class MakeupRLRecyclerView extends RecommendDragContainer {
    public MakeupRLRecyclerView(@NonNull Context context, AbsDragAdapter adapter) {
        super(context, adapter);
    }
    @Override
    protected void initUi(RecyclerView recyclerView) {
        super.initUi(recyclerView);
        LayoutParams params = (LayoutParams) recyclerView.getLayoutParams();
        params.height = ShareData.PxToDpi_xhdpi(162);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = ShareData.PxToDpi_xhdpi(70);
        recyclerView.setLayoutParams(params);
    }

}

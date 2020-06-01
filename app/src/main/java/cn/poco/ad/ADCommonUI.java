package cn.poco.ad;


import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class ADCommonUI {


    public static ADCommonRecylerView makeRecyclerView1(Context context, ArrayList<ADCommonAdapter.ADBaseItemData> ress, ADCommonAdapter.OnClickCB cb, final int firstItemLefMagin, final int otherItemMagin)
    {
        ADCommonRecylerView recyclerView = new ADCommonRecylerView(context);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ADCommonAdapter adCommonAdapter = new ADCommonAdapter(context,ress,cb);
        recyclerView.setAdapter(adCommonAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = linearLayoutManager.getPosition(view);
                if(position == 0)
                {
                    outRect.left = firstItemLefMagin;
                }
                else
                {
                    outRect.left = otherItemMagin;
                }
            }
        });
        return recyclerView;
    }

}

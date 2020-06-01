package cn.poco.ad65;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.poco.ad.abs.ADAbsAdapter;
import cn.poco.ad.abs.ADAbsBottomFrWithRY;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsConfig;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class AD65BottomFr2 extends ADAbsBottomFrWithRY {
    public AD65BottomFr2(@NonNull Context context) {
        super(context);
    }

    @Override
    public AbsConfig getConfig() {
        return new AbsConfig() {
            @Override
            public void InitData() {
                def_item_w = ShareData.PxToDpi_xhdpi(150);
                def_item_h = ShareData.PxToDpi_xhdpi(150);
                def_parent_center_x = (int) (ShareData.m_screenWidth/2f);
            }

            @Override
            public void ClearAll() {

            }
        };
    }

    @Override
    public ADAbsAdapter getAdapter() {
        AD65ADAdapter ad65ADAdapter = new AD65ADAdapter(m_config);
        ad65ADAdapter.SetData(getItemInfos());
        ad65ADAdapter.setOnItemClickListener(new AbsAdapter.OnItemClickListener() {
            @Override
            public void OnItemDown(AbsAdapter.ItemInfo info, int index) {

            }

            @Override
            public void OnItemUp(AbsAdapter.ItemInfo info, int index) {

            }

            @Override
            public void OnItemClick(AbsAdapter.ItemInfo info, int index) {
                if(m_cb != null)
                {
                    ((ADAbsBottomFrWithRY.ClickCallBack)m_cb).onItemClick(info,index);
                }
            }
        });
        return ad65ADAdapter;
    }

    public ArrayList<ADAbsAdapter.ADItemInfo> getItemInfos()
    {
        ArrayList<ADAbsAdapter.ADItemInfo> itemInfos = new ArrayList<>();
//        ADAbsAdapter.ADItemInfo info = new ADAbsAdapter.ADItemInfo();
//        info.m_res = R.drawable.ad65_frame_thumb1;
//        itemInfos.add(info);
//
//        info = new ADAbsAdapter.ADItemInfo();
//        info.m_res = R.drawable.ad65_frame_thumb2;
//        itemInfos.add(info);
//
//        ADAbsAdapter.ADNullItem info2 = new ADAbsAdapter.ADNullItem();
//        info2.m_res = R.drawable.ad65_frame_thumb3;
//        itemInfos.add(info2);
        return itemInfos;
    }

    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getLayoutManager().getPosition(view);
                if(position == 0)
                {
                    outRect.left = ShareData.PxToDpi_xhdpi(25);
                }
                else
                {
                    outRect.left = ShareData.PxToDpi_xhdpi(25);
                }
            }
        };
    }

       public void SetselectIndex(int index)
    {
        if(m_adAdapter != null)
        {
            m_adAdapter.SetSelectByIndex(index);
        }
    }


    private class AD65ADAdapter extends ADAbsAdapter
    {

        public AD65ADAdapter(AbsConfig m_config) {
            super(m_config);
        }

        @Override
        protected BaseItem initItem(Context context) {
            return new AD65Item(context,m_config);
        }

        @Override
        public BaseItem initNullItem() {
            return new AD65Item(getContext(),m_config);
        }
    }

    private class AD65Item extends BaseItem
    {
        private ImageView m_img;
        private int m_selectColor = Color.GRAY;
        private int m_normolColor = 0;
        public AD65Item(@NonNull Context context,AbsConfig config) {
            super(context);
            initUI();
        }

        private void initUI()
        {
            m_img = new ImageView(getContext());
            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            fl.gravity = Gravity.CENTER;
            m_img.setLayoutParams(fl);
            m_img.setPadding(ShareData.PxToDpi_xhdpi(5),ShareData.PxToDpi_xhdpi(5),ShareData.PxToDpi_xhdpi(5),ShareData.PxToDpi_xhdpi(5));
            this.addView(m_img);
        }

        @Override
        public void onSelected() {
            m_img.setBackgroundColor(m_selectColor);
        }

        @Override
        public void onUnSelected() {
            m_img.setBackgroundColor(m_normolColor);
        }

        @Override
        public void onClick() {

        }

        @Override
        public void SetData(AbsAdapter.ItemInfo info, int index) {
            if(info != null)
            {
                Glide.with(getContext()).load(((ADAbsAdapter.ADItemInfo)info).m_res).into(m_img);
            }
        }
    }
}

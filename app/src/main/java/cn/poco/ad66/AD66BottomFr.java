package cn.poco.ad66;

import android.content.Context;
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

public class AD66BottomFr extends ADAbsBottomFrWithRY
{
    public AD66BottomFr(@NonNull Context context) {
        super(context);
    }

    @Override
    public AbsConfig getConfig() {
        return new AbsConfig() {
            @Override
            public void InitData() {
                def_item_w = ShareData.PxToDpi_xhdpi(120);
                def_item_h = ShareData.PxToDpi_xhdpi(160);
                def_parent_center_x = (int) (ShareData.m_screenWidth/2f);
            }

            @Override
            public void ClearAll() {

            }
        };
    }


    @Override
    protected void addUI() {
        super.addUI();
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) m_recyclerView.getLayoutParams();
        fl.gravity = Gravity.CENTER;
        m_recyclerView.setLayoutParams(fl);
    }

    @Override
    public ADAbsAdapter getAdapter() {
        AD66Adapter adAdapter = new AD66Adapter(m_config);
        adAdapter.setOnItemClickListener(new AbsAdapter.OnItemClickListener() {
            @Override
            public void OnItemDown(AbsAdapter.ItemInfo info, int index) {

            }

            @Override
            public void OnItemUp(AbsAdapter.ItemInfo info, int index) {

            }

            @Override
            public void OnItemClick(AbsAdapter.ItemInfo info, int index) {
                if(m_cb != null && m_cb instanceof ClickCallBack)
                {
                    ClickCallBack clickCallBack = (ClickCallBack) m_cb;
                    clickCallBack.onItemClick(info,index);
                }
            }
        });
        return adAdapter;
    }

    public void setItemInfos(ArrayList<ADAbsAdapter.ADItemInfo> itemInfos)
    {
        if(itemInfos != null && itemInfos.size() > 0)
        {
            m_adAdapter.SetData(itemInfos);
        }
    }

    public void SetselectIndex(int index)
    {
        if(m_adAdapter != null)
        {
            m_adAdapter.SetSelectByIndex(index);
        }
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
                    outRect.left = ShareData.PxToDpi_xhdpi(15);
                }
                else
                {
                    outRect.left = ShareData.PxToDpi_xhdpi(15);
                }

                if(m_adAdapter != null && position == (m_adAdapter.getItemCount() - 1))
                {
                     outRect.right = ShareData.PxToDpi_xhdpi(15);
                }
            }
        };
    }

    private class AD66Adapter extends ADAbsAdapter {
        public AD66Adapter(AbsConfig m_config) {
            super(m_config);
        }

        @Override
        public BaseItem initNullItem() {
            return new AD66NullItem(m_parent.getContext());
        }

        @Override
        protected BaseItem initItem(Context context) {
            return new AD66NorItem(m_parent.getContext());
        }

        private class AD66NorItem extends BaseItem {

            private ImageView m_img;
            private AD66Model.AD66Item m_itemInfo;
            public AD66NorItem(@NonNull Context context) {
                super(context);
                initUI();
            }

            private void initUI()
            {
                m_img = new ImageView(getContext());
                LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.TOP;
                m_img.setLayoutParams(fl);
                this.addView(m_img);
            }

            @Override
            public void onSelected() {
                if(m_itemInfo != null)
                {
                    Glide.with(getContext()).load(m_itemInfo.m_selectRes).into(m_img);
                }
            }

            @Override
            public void onUnSelected() {
                if(m_itemInfo != null)
                {
                    Glide.with(getContext()).load(m_itemInfo.m_res).into(m_img);
                }
            }

            @Override
            public void onClick() {

            }

            @Override
            public void SetData(AbsAdapter.ItemInfo info, int index) {
                m_itemInfo = (AD66Model.AD66Item) info;
                if(m_itemInfo != null)
                {
                    Glide.with(getContext()).load(m_itemInfo.m_res).into(m_img);
                }
            }
        }

        private class AD66NullItem extends BaseItem {

            private ImageView m_img;
            private ADNullItem m_itemInfo;
            public AD66NullItem(@NonNull Context context) {
                super(context);
                initUI();
            }

            private void initUI()
            {
                m_img = new ImageView(getContext());
                LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                fl.gravity = Gravity.TOP;
                m_img.setLayoutParams(fl);
                this.addView(m_img);
            }

            @Override
            public void onSelected() {
                if(m_itemInfo != null)
                {
                    Glide.with(getContext()).load(m_itemInfo.m_selectRes).into(m_img);
                }
            }

            @Override
            public void onUnSelected() {
                if(m_itemInfo != null)
                {
                    Glide.with(getContext()).load(m_itemInfo.m_res).into(m_img);
                }
            }

            @Override
            public void onClick() {

            }

            @Override
            public void SetData(AbsAdapter.ItemInfo info, int index) {
                m_itemInfo = (ADNullItem) info;
                if(m_itemInfo != null)
                {
                    Glide.with(getContext()).load(m_itemInfo.m_res).into(m_img);
                }
            }
        }
    }
}

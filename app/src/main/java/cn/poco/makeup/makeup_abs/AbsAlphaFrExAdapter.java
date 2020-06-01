package cn.poco.makeup.makeup_abs;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.widget.recycle.RecommendDragContainer;
import cn.poco.widget.recycle.RecommendExAdapter;
import my.beautyCamera.R;

/**
 * 包含调节透明度ui的可展开的Item
 */
public abstract class AbsAlphaFrExAdapter extends BaseExAdapter {
    protected boolean mAlphaFrIsShow = false;
    protected BaseItemWithAlphaFrContainer.MySeekBarCB mProgressChangeListener;
    protected BaseItemWithAlphaFrContainer.AnimaChangeCallBack m_ChangceCB;
    public AbsAlphaFrExAdapter(AbsExConfig itemConfig) {
        super(itemConfig);
        init();
    }

    protected void init()
    {
        mProgressChangeListener = new BaseItemWithAlphaFrContainer.MySeekBarCB() {
            @Override
            public void onSeekBarShowStart(MySeekBar mySeekBar) {
                m_parent.setLayoutFrozen(false);

                if(m_parent.getParent() instanceof RecommendDragContainer)
                {
                    RecommendDragContainer recyclerViewContainer = (RecommendDragContainer) m_parent.getParent();
                    recyclerViewContainer.setUIEnable(false);
                }
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onAlphaFrShowStart(mySeekBar);
                }
            }

            @Override
            public void onSeekBarLayoutFinish(MySeekBar mySeekBar) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onAlphaFrFinishLayout(mySeekBar);
                }
                if(mAlphaFrIsShow)
                {
                    m_parent.setLayoutFrozen(true);
                }
                else
                {
                    m_parent.setLayoutFrozen(false);
                }

                if(m_parent.getParent() instanceof RecommendDragContainer)
                {
                    RecommendDragContainer recyclerViewContainer = (RecommendDragContainer) m_parent.getParent();
                    recyclerViewContainer.setUIEnable(true);
                }
            }

            @Override
            public void onProgressChanged(MySeekBar seekBar, int progress) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onProgressChanged(seekBar,progress);
                }
            }

            @Override
            public void onStartTrackingTouch(MySeekBar seekBar) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(MySeekBar seekBar) {
                ItemAllCallBack itemAllCallBack = (ItemAllCallBack) m_onItemClickListener;
                if(itemAllCallBack != null)
                {
                    itemAllCallBack.onStopTrackingTouch(seekBar);
                }
            }
        };

        m_ChangceCB = new BaseItemWithAlphaFrContainer.AnimaChangeCallBack() {
            @Override
            public void change(View view, float value, boolean isOpen) {
                if(view != null)
                {
                    if(isOpen)
                    {
                        if(view != null)
                        {
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                            int curLeft = lp.leftMargin;
                            AbsAlphaConfig config = (AbsAlphaConfig) m_config;
                            int animValue = (int) ((config.m_alphaFr_Item_left - curLeft)*value);
                            lp.leftMargin = curLeft + animValue;
                            int curRight = lp.rightMargin;
                            if(curRight > 0)
                            {
                                int rightAnimValue = (int) (curRight*(1 - value));
                                lp.rightMargin = rightAnimValue;
                            }
                            view.setLayoutParams(lp);
                        }
                        scrollByLeft2(view,value);
                    }
                    else
                    {
                        if(view != null)
                        {
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                            int curLeft = lp.leftMargin;
                            AbsAlphaConfig config = (AbsAlphaConfig) m_config;
                            int normolSub_l = config.def_sub_l;
                            if(m_currentSubSel == 1)
                            {
                                normolSub_l = config.def_sub_l + config.def_sub_padding_l;
                            }
//                            int normolSub_l = getAlphaExInitLeftMargin(false,m_currentSel,m_currentSubSel);
                            int animValue = (int) ((curLeft - normolSub_l)*value);
                            lp.leftMargin = curLeft - animValue;

                            if(view.getParent() != null)
                            {
                                int finishSubIndex = ((BaseItemWithAlphaFrContainer) view.getParent()).getChildCount() - 2;
                                if(m_currentSubSel == finishSubIndex)
                                {
                                    int animRightMargin = (int) (lp.rightMargin + (config.def_sub_padding_r - lp.rightMargin)*value);
//                                    int animRightMargin = (int) (lp.rightMargin + (getAlphaExinitRightMargin(isOpen,m_currentSel,m_currentSubSel,true) - lp.rightMargin)*value);
                                    lp.rightMargin = animRightMargin;
                                }
                                view.setLayoutParams(lp);
                            }
                            scrollByCenter(view);
                        }
                    }
                }
            }
        };
    }


    @Override
    protected void onSubClick(BaseItem subItem, int position) {
        if(subItem instanceof BaseItemWithAlphaFrMode)
        {
            BaseItemWithAlphaFrMode targetItem = (BaseItemWithAlphaFrMode) subItem;
            BaseItemWithAlphaFrContainer targetParent = (BaseItemWithAlphaFrContainer) targetItem.getParent();
            if(targetItem.IsSelected() && targetParent != null)
            {
                targetParent.setChangeCB(m_ChangceCB);
                targetParent.setOnProgressChangeListener(mProgressChangeListener);
                if(!mAlphaFrIsShow)
                {
                    mAlphaFrIsShow = true;
                    targetItem.onOpenAlphaFr();
                    targetParent.openAlphaFr(m_currentSubSel);
                }
                else
                {
                    targetItem.onCloseAlphaFr();
                    mAlphaFrIsShow = false;
                    targetParent.closeAlphaFr();
                }
                return;
            }
        }
        super.onSubClick(subItem, position);
    }

    @Override
    protected void scrollByCenter(final View view) {
        if (view != null && m_parent != null) {
            int[] dst = new int[2];
            view.getLocationOnScreen(dst);
            //当前view移去中间的距离
            float viewCenter = dst[0] + view.getWidth() / 2f;
            float center = m_parent.getWidth() / 2f;
            float offset = viewCenter - center;
            m_parent.smoothScrollBy((int)offset, 0);
        }
    }

    //当前调节透明度ui是否显示
    public boolean AlphaIsShow()
    {
        return mAlphaFrIsShow;
    }


    //关闭调节透明度的ui
    public void onCloseAlphaFr()
    {
        if(mAlphaFrIsShow)
        {
            BaseItemWithAlphaFrContainer container = (BaseItemWithAlphaFrContainer) getVisibleItem(m_currentSel);
            if(container != null)
            {
                container.closeAlphaFr();
                mAlphaFrIsShow = false;
                if(m_currentSubSel > -1)
                {
                    if(m_currentSubSel > -1 && m_currentSubSel < container.getChildCount())
                    {
                        IAlphaMode alphaMode = (IAlphaMode) container.getChildAt(m_currentSubSel);
                        alphaMode.onCloseAlphaFr();
                    }
                }
            }
        }
    }

    protected void scrollByLeft2(View view, float percent)
    {
        if(view != null && m_parent != null)
        {
            float center = m_parent.getLeft();
            int[] dst = new int[2];
            view.getLocationOnScreen(dst);
            float viewCenter = dst[0];
            AbsAlphaConfig config = (AbsAlphaConfig) m_config;
            float offset = viewCenter - center - config.m_alphaFr_Item_left;
            m_parent.smoothScrollBy((int)(offset * percent), 0);
        }
    }


    public interface ItemAllCallBack extends OnItemClickListener
    {
        void onAlphaFrShowStart(MySeekBar mySeekBar);

        void onAlphaFrFinishLayout(MySeekBar mySeekBar);

        void onProgressChanged(MySeekBar seekBar, int progress);

        void onStartTrackingTouch(MySeekBar seekBar);

        void onStopTrackingTouch(MySeekBar seekBar);
    }


    public static class NullViewHolder extends RecyclerView.ViewHolder
    {

        public NullViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class RLViewHolder extends RecyclerView.ViewHolder
    {

        public RLViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DownloadItemInfo2 extends ItemInfo
    {
        public static final int DOWNLOAD_ITEM_URI_2 = 0xFFFFF100;
        public int m_num;

        public DownloadItemInfo2()
        {
            m_uri = DOWNLOAD_ITEM_URI_2;
            m_uris = new int[]{DOWNLOAD_ITEM_URI_2};
            m_ids = new int[]{MY_ID++};
        }

        public void setData(Object[] logos,String[] names,int num)
        {
            this.m_logos = logos;
            this.m_names = names;
            this.m_num = num;
        }

        public void setNum(int num)
        {
            this.m_num = num;
        }
    }

    public static class NullItemInfo extends ItemInfo
    {
        public static final int NULL_ITEM_URI = 0xFFFFF101;
        public int m_color = 0xffffffff;

        public NullItemInfo()
        {
            m_uri = NULL_ITEM_URI;
            m_ids = new int[]{MY_ID++};
            m_logos = new Object[]{R.drawable.photofactory_makeup_item_null_out};
        }
    }

    public static class RecommendItemInfo extends ItemInfo
    {
        public static final int REC_ITEM_URI = 0xFFFFF101;
        public int m_bkColor;

        public RecommendItemInfo()
        {
            m_uri = REC_ITEM_URI;
            m_ids = new int[]{MY_ID++};
        }

        public void setData(int[] uris, Object[] logos, String[] names, int color)
        {
            m_uris = uris;
            m_logos = logos;
            m_names = names;
            m_bkColor = color;
        }
    }

    public static class ItemInfo extends RecommendExAdapter.ItemInfo
    {
        public int m_maskColor = -1;
    }
}

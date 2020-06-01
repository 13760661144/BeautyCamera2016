package cn.poco.makeup.makeup2;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import cn.poco.makeup.MySeekBar;
import cn.poco.recycleview.AbsExConfig;
import cn.poco.recycleview.BaseItem;
import cn.poco.recycleview.BaseItemContainer;
import cn.poco.widget.recycle.RecommendExAdapter;

public class Makeup2Adapter extends RecommendExAdapter {
    public Makeup2Adapter(AbsExConfig itemConfig) {
        super(itemConfig);
    }

    @Override
    protected BaseItemContainer initItem(Context context, AbsExConfig itemConfig) {

        return new Makeup2ListItem(context,itemConfig);
    }

    public void closeAlphaFr()
    {
        if(IsShowAlphaFr())
        {
            if(m_currentSel >= 0 && m_currentSubSel >= 0)
            {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) m_parent.getLayoutManager();

                BaseItemContainer baseItemContainer = (BaseItemContainer) linearLayoutManager.findViewByPosition(m_currentSel);
                if(baseItemContainer != null && m_currentSubSel < baseItemContainer.getChildCount())
                {
                    onSubClick((BaseItem) baseItemContainer.getChildAt(m_currentSubSel),m_currentSel);
                }
            }
        }
    }

    @Override
    protected void onSubClick(final BaseItem subItem, int position) {
        if(subItem instanceof Makeup2SubItem)
        {
            Makeup2SubItem tempItem = (Makeup2SubItem) subItem;

            if(tempItem.getIsSelect())
            {
                if(!mIsShowAlphaFr)
                {
                    final Makeup2ListItem item = (Makeup2ListItem)subItem.getParent();
                    item.setProgressChangeCB(new Makeup2ListItem.ProgressChangeCB() {
                        @Override
                        public void onProgressChange(MySeekBar seekBar, int progress) {
                            if(m_onItemClickListener != null)
                            {
                                OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                listener.onProgressChange(seekBar,progress);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(MySeekBar seekBar) {
                            if(m_onItemClickListener != null)
                            {
                                OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                listener.onStartTrackingTouch(seekBar);
                            }
                        }

                        @Override
                        public void onStopTrackingTouch(MySeekBar seekBar) {
                            if(m_onItemClickListener != null)
                            {
                                OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                listener.onStopTrackingTouch(seekBar);
                            }
                        }

                        @Override
                        public void onSeekBarStartShow(MySeekBar seekBar) {
                            m_parent.setLayoutFrozen(false);
                            if(m_onItemClickListener != null)
                            {
                                OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                listener.onSeekBarStartShow(seekBar);
                            }
                        }

                        @Override
                        public void onFinishLayoutAlphaFr(MySeekBar seekBar) {
                            if(m_onItemClickListener != null)
                            {
                                OnItemClickListener listener = (OnItemClickListener) m_onItemClickListener;
                                listener.onFinishLayoutAlphaFr(seekBar);
                            }

                            if(IsShowAlphaFr())
                            {
                                m_parent.setLayoutFrozen(true);
                            }
                            else
                            {
                                m_parent.setLayoutFrozen(false);
                            }
                        }
                    });
                    item.openAlphaFr(m_currentSubSel, new Makeup2ListItem.onChangeAlphaFrCB() {
                        @Override
                        public void change(float value) {
                            if(subItem != null)
                            {
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) subItem.getLayoutParams();
                                int curLeft = lp.leftMargin;
                                Makeup2ListConfig config = (Makeup2ListConfig) m_config;
                                int animValue = (int) ((config.def_alphafr_leftMargin - curLeft)*value);
                                lp.leftMargin = curLeft + animValue;
                                int curRight = lp.rightMargin;
                                if(curRight > 0)
                                {
                                    int rightAnimValue = (int) (curRight*(1 - value));
                                    lp.rightMargin = rightAnimValue;
                                }
                                subItem.setLayoutParams(lp);
                            }
                            scrollByLeft2(subItem,value);
                        }
                    });
                    ((Makeup2SubItem)subItem).onAlphaMode();
                    mIsShowAlphaFr = true;
                }
                else
                {
                    ((Makeup2SubItem)subItem).closeAlphaMode();
                    mIsShowAlphaFr = false;
                    Makeup2ListItem item = (Makeup2ListItem)subItem.getParent();
                    item.closeAlphaFr(new Makeup2ListItem.onChangeAlphaFrCB() {
                        @Override
                        public void change(float value) {
                            if(subItem != null)
                            {
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) subItem.getLayoutParams();
                                int curLeft = lp.leftMargin;
                                Makeup2ListConfig config = (Makeup2ListConfig) m_config;
                                int normolSub_l = config.def_sub_l;
                                if(m_currentSubSel == 1)
                                {
                                    normolSub_l = config.def_sub_l + config.def_sub_padding_l;
                                }
                                int animValue = (int) ((curLeft - normolSub_l)*value);
                                lp.leftMargin = curLeft - animValue;

                                if(subItem.getParent() != null)
                                {
                                    int finishSubIndex = ((Makeup2ListItem) subItem.getParent()).getChildCount() - 2;
                                    if(m_currentSubSel == finishSubIndex)
                                    {
                                        int animRightMargin = (int) (lp.rightMargin + (config.def_sub_padding_r - lp.rightMargin)*value);
                                        lp.rightMargin = animRightMargin;
                                    }
                                    subItem.setLayoutParams(lp);
                                }
                                scrollByCenter(subItem);
                            }
                        }
                    });
                }
            }
            else
            {
                super.onSubClick(subItem,position);
            }
        }
        else
        {
            super.onSubClick(subItem, position);
        }
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

    private boolean mIsShowAlphaFr = false;

    public boolean IsShowAlphaFr()
    {
        return mIsShowAlphaFr;
    }


    boolean invokeSuper;
    @Override
    public void CancelSelect()
    {
        if (invokeSuper) {
            super.CancelSelect();
            invokeSuper = false;
        } else {
            closeSubLayout();
        }
    }

   private void closeSubLayout () {
        int temp = m_currentSel;
        if(temp != -1)
        {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) m_parent.getLayoutManager();
            BaseItemContainer baseItemContainer = (BaseItemContainer) linearLayoutManager.findViewByPosition(temp);
            if(baseItemContainer != null)
            {
                if(baseItemContainer.isOpen)
                {
                    onClick(baseItemContainer.mGroupItem);
                    m_parent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            invokeSuper = true;
                            Makeup2Adapter.this.CancelSelect();
                        }
                    },300);
                }
                else
                {
                    invokeSuper = true;
                    Makeup2Adapter.this.CancelSelect();
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
            Makeup2ListConfig config = (Makeup2ListConfig) m_config;
            float offset = viewCenter - center - config.def_alphafr_leftMargin;
            m_parent.smoothScrollBy((int)(offset * percent), 0);
        }
    }

    public interface OnItemClickListener extends RecommendExAdapter.OnItemClickListener
    {
        public void onProgressChange(MySeekBar seekBar,int progress);

        public void onStartTrackingTouch(MySeekBar seekBar);

        public void onStopTrackingTouch(MySeekBar seekBar);

        public void onSeekBarStartShow(MySeekBar seekBar);

        public void onFinishLayoutAlphaFr(MySeekBar seekBar);
    }
}

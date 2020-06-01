package cn.poco.live.sticker.local;

import android.content.Context;
import android.graphics.Color;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.info.sticker.LabelLocalInfo;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.live.LivePageListener;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

import static android.os.Looper.myQueue;

/**
 * 直播 素材管理页
 * Created by Gxx on 2017/10/30.
 */

public class StickerMgrPage extends FrameLayout implements View.OnClickListener, StickerLocalMgr.DataListener
{
    public @interface SelectedIconType
    {
        int DO_NOT_SHOW = 1;
        int CHECK_ALL = 1 << 2;
        int SELECTED_NONE = 1 << 3;
    }

    public @interface DeleteIconType
    {
        int CLICKABLE = 1;
        int DO_NOT_CLICK = 1 << 2;
    }

    private PressedButton mBackView;
    private TextView mSelectedView;
    private RecyclerView mTabView;
    private ViewPager mSticker;
    private FrameLayout mDeleteView;

    private LivePageListener mPageListener;

    public StickerMgrPage(@NonNull Context context)
    {
        super(context);
        StickerLocalMgr.getInstance().init(context);
        setBackgroundColor(Color.WHITE);
        initView(context);
        initData();
    }

    public void setPageListener(LivePageListener listener)
    {
        mPageListener = listener;
    }

    public void ClearAll()
    {
        mPageListener = null;
        StickerLocalMgr.getInstance().ClearAll();
    }

    private void initView(Context context)
    {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(container, params);
        {
            FrameLayout topLayout = new FrameLayout(context);
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(80));
            container.addView(topLayout, ll);
            {
                mBackView = new PressedButton(context);
                mBackView.setOnClickListener(this);
                mBackView.setButtonImage(R.drawable.framework_back_btn, R.drawable.framework_back_btn, ImageUtils.GetSkinColor(), 0.5f);
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL;
                topLayout.addView(mBackView, params);

                TextView titleView = new TextView(getContext());
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                titleView.setTextColor(0xff333333);
                titleView.setText(R.string.sticker_pager_manager_material);
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                topLayout.addView(titleView, params);

                boolean isShowSelectedView = StickerLocalMgr.getInstance().isHadLocalRes(0);
                mSelectedView = new TextView(getContext());
                mSelectedView.setVisibility(isShowSelectedView ? VISIBLE : GONE);
                mSelectedView.setTag(isShowSelectedView ? SelectedIconType.CHECK_ALL : SelectedIconType.DO_NOT_SHOW);
                mSelectedView.setOnClickListener(this);
                mSelectedView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                mSelectedView.setTextColor(ImageUtils.GetSkinColor());
                mSelectedView.setText(R.string.material_manage_select_all);
                mSelectedView.setGravity(Gravity.CENTER);
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                params.rightMargin = CameraPercentUtil.WidthPxToPercent(28);
                topLayout.addView(mSelectedView, params);
            }

            mTabView = new RecyclerView(context);
            mTabView.setOverScrollMode(OVER_SCROLL_NEVER);
            ((SimpleItemAnimator) mTabView.getItemAnimator()).setSupportsChangeAnimations(false);
            mTabView.getItemAnimator().setChangeDuration(0);
            mTabView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(72));
            container.addView(mTabView, ll);

            mSticker = new ViewPager(context);
            mSticker.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {

                }

                @Override
                public void onPageSelected(int position)
                {
                    if (mTabView != null)
                    {
                        RecyclerView.Adapter adapter = mTabView.getAdapter();
                        if (adapter != null)
                        {
                            int labelSize = adapter.getItemCount();
                            if (labelSize > 0 && position >= 0 && position < labelSize)
                            {
                                int previous_index = StickerLocalMgr.getInstance().getSelectedLabelIndex();
                                LabelLocalInfo previous_info = StickerLocalMgr.getInstance().getLabelInfoByIndex(previous_index);

                                if (previous_info != null)
                                {
                                    previous_info.isSelected = false;
                                    adapter.notifyItemChanged(previous_index);
                                }

                                LabelLocalInfo current_info = StickerLocalMgr.getInstance().getLabelInfoByIndex(position);
                                if (current_info != null)
                                {
                                    current_info.isSelected = true;
                                    StickerLocalMgr.getInstance().updateSelectedLabelIndex(position);
                                    adapter.notifyItemChanged(position);
                                    onLabelScrollToSelected(position);
                                }
                            }
                        }
                    }

                    int selected_icon_status = SelectedIconType.DO_NOT_SHOW;
                    int delete_icon_status = DeleteIconType.DO_NOT_CLICK;

                    int data_size = StickerLocalMgr.getInstance().getStickerInfoArrSize(position);

                    if (data_size > 0)
                    {
                        if (StickerLocalMgr.getInstance().isHadLocalRes(position))
                        {
                            if (StickerLocalMgr.getInstance().isAllStickerSelected(position)) // 全选
                            {
                                selected_icon_status = StickerMgrPage.SelectedIconType.SELECTED_NONE;
                                delete_icon_status = DeleteIconType.CLICKABLE;
                            }
                            else if (StickerLocalMgr.getInstance().isSelectedNone(position)) // 全不选
                            {
                                selected_icon_status = StickerMgrPage.SelectedIconType.CHECK_ALL;
                            }
                            else
                            {
                                selected_icon_status = StickerMgrPage.SelectedIconType.CHECK_ALL;
                                delete_icon_status = DeleteIconType.CLICKABLE;
                            }
                        }
                    }
                    onChangeSelectedIconStatus(selected_icon_status);
                    onChangeDeleteIconAlpha(delete_icon_status);
                }

                @Override
                public void onPageScrollStateChanged(int state)
                {

                }
            });
            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(mSticker, ll);
        }

        mDeleteView = new FrameLayout(getContext());
        mDeleteView.setOnClickListener(this);
        mDeleteView.setTag(DeleteIconType.DO_NOT_CLICK);
        mDeleteView.setAlpha(0.1f);
        mDeleteView.setBackgroundResource(R.drawable.new_material4_delete);
        params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(270), CameraPercentUtil.WidthPxToPercent(76));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = CameraPercentUtil.WidthPxToPercent(12);
        addView(mDeleteView, params);
        {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(R.drawable.sticker_trash_white);
            params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(40);
            mDeleteView.addView(iv, params);

            TextView tv = new TextView(getContext());
            tv.setText(R.string.material_manage_delete);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setTextColor(Color.WHITE);
            params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(20);
            mDeleteView.addView(tv, params);
        }
    }

    private void initData()
    {
        LabelLocalAdapter adapter = new LabelLocalAdapter();
        ArrayList<LabelLocalInfo> data = StickerLocalMgr.getInstance().getLabelInfoArr();
        adapter.setDataListener(this);
        adapter.setData(data);
        if (mTabView != null)
        {
            mTabView.setAdapter(adapter);
        }

        if (data != null)
        {
            int pagerSize = data.size();
            StickerLocalPagerAdapter pagerAdapter = new StickerLocalPagerAdapter();
            pagerAdapter.updatePagerSize(pagerSize);
            pagerAdapter.setStickerHelper(this);
            if (mSticker != null)
            {
                mSticker.setAdapter(pagerAdapter);
            }
        }
    }

    @Override
    public void onSelectedLabel(int index)
    {
        if (index < StickerLocalMgr.getInstance().getLabelArrValidSize() && mSticker != null)
        {
            mSticker.setCurrentItem(index);
        }
    }

    @Override
    public void onLabelScrollToSelected(final int index)
    {
        if (mTabView != null)
        {
            RecyclerView.LayoutManager manager = mTabView.getLayoutManager();
            if(manager != null)
            {
                View view = manager.findViewByPosition(index);
                float center = mTabView.getWidth() / 2f;
                if(view != null)
                {
                    float viewCenter = view.getX() + view.getWidth() / 2f;
                    mTabView.smoothScrollBy((int)(viewCenter - center), 0);
                }
                else
                {
                    mTabView.smoothScrollToPosition(index);
                    myQueue().addIdleHandler(new MessageQueue.IdleHandler()
                    {
                        @Override
                        public boolean queueIdle()
                        {
                            myQueue().removeIdleHandler(this);
                            onLabelScrollToSelected(index);
                            return false;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onChangeSelectedIconStatus(int status)
    {
        mSelectedView.setTag(status);

        switch (status)
        {
            case SelectedIconType.DO_NOT_SHOW:// 隐藏
            {
                mSelectedView.setVisibility(GONE);
                break;
            }

            case SelectedIconType.CHECK_ALL:// 显示全选
            {
                mSelectedView.setText(R.string.material_manage_select_all);
                mSelectedView.setVisibility(VISIBLE);
                break;
            }

            case SelectedIconType.SELECTED_NONE:// 显示全不选
            {
                mSelectedView.setText(R.string.material_manage_cancel_select_all);
                mSelectedView.setVisibility(VISIBLE);
                break;
            }
        }
    }

    @Override
    public void onChangeDeleteIconAlpha(int status)
    {
        if (mDeleteView != null)
        {
            mDeleteView.setTag(status);
            switch (status)
            {
                case DeleteIconType.CLICKABLE:
                {
                    mDeleteView.setAlpha(1);
                    break;
                }

                case DeleteIconType.DO_NOT_CLICK:
                {
                    mDeleteView.setAlpha(0.1f);
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mBackView)
        {
            if (mPageListener != null)
            {
                mPageListener.onCloseStickerMgrPage();
            }
        }
        else if (v == mDeleteView)
        {
            int status = (int) mDeleteView.getTag();
            switch (status)
            {
                case DeleteIconType.CLICKABLE:
                {
                    StickerLocalMgr.getInstance().deleteSelectedSticker(getContext());

                    int selectedIndex = StickerLocalMgr.getInstance().getSelectedLabelIndex();
                    ArrayList<StickerInfo> list = StickerLocalMgr.getInstance().getStickerInfoArr(selectedIndex);
                    StickerLocalMgr.getInstance().getStickerInfoArrSize(selectedIndex);
                    if (list != null && list.size() > 0 && StickerLocalMgr.getInstance().isHadLocalRes(list))
                    {
                        onChangeSelectedIconStatus(StickerMgrPage.SelectedIconType.CHECK_ALL);
                    }
                    else
                    {
                        onChangeSelectedIconStatus(StickerMgrPage.SelectedIconType.DO_NOT_SHOW);
                    }

                    onChangeDeleteIconAlpha(DeleteIconType.DO_NOT_CLICK);

                    MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸管理页_删除);
                    break;
                }
            }
        }
        else if (v == mSelectedView)
        {
            int status = (int) mSelectedView.getTag();

            switch (status)
            {
                case SelectedIconType.CHECK_ALL://全选
                {
                    MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸管理页_全选);
                    StickerLocalMgr.getInstance().selectedAllSticker(true);
                    onChangeSelectedIconStatus(SelectedIconType.SELECTED_NONE);
                    onChangeDeleteIconAlpha(DeleteIconType.CLICKABLE);
                    break;
                }

                case SelectedIconType.SELECTED_NONE://全不选
                {
                    MyBeautyStat.onClickByRes(R.string.直播助手_贴纸页_贴纸管理页_全不选);
                    StickerLocalMgr.getInstance().selectedAllSticker(false);
                    onChangeSelectedIconStatus(SelectedIconType.CHECK_ALL);
                    onChangeDeleteIconAlpha(DeleteIconType.DO_NOT_CLICK);
                    break;
                }
            }
        }
    }
}

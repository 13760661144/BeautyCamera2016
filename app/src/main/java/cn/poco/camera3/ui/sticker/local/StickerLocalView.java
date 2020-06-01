package cn.poco.camera3.ui.sticker.local;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.camera3.cb.sticker.StickerLocalInnerListener;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StickerLocalMgr;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author Created by Gxx on 2017/10/30.
 */

public class StickerLocalView extends FrameLayout implements View.OnClickListener
{
    public @interface DeleteIconType
    {
        int CLICKABLE = 1;
        int DO_NOT_CLICK = 1 << 2;
    }

    private ViewPager mContentView;
    private FrameLayout mDeleteView;
    private StickerLocalPagerAdapter mAdapter;
    private StickerLocalInnerListener mHelper;

    public StickerLocalView(@NonNull Context context)
    {
        super(context);
        initView(context);
    }

    public void ClearAll()
    {
        mHelper = null;

        if (mDeleteView != null)
        {
            mDeleteView.setOnClickListener(null);
        }

        if (mAdapter != null)
        {
            mAdapter.setStickerHelper(null);
        }

        if (mContentView != null)
        {
            mContentView.clearOnPageChangeListeners();
            int size = mContentView.getChildCount();
            for (int index = 0;index<size;index++)
            {
                View v = mContentView.getChildAt(index);
                if (v != null && v instanceof StickerLocalPagerView)
                {
                    ((StickerLocalPagerView) v).ClearAll();
                }
            }

            mContentView.removeAllViews();
        }
    }

    private void initView(Context context)
    {
        mContentView = new ViewPager(context);
        mContentView.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(final int position)
            {
                int selected_icon_status = StickerMgrPage.SelectedIconType.DO_NOT_SHOW;
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
                setDeleteViewStatus(delete_icon_status);
                if (mHelper != null)
                {
                    mHelper.onChangeSelectedIconStatus(selected_icon_status);
                    mHelper.onStickerPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mContentView, params);

        View view = new View(getContext());
        view.setBackgroundColor(0xe6ffffff);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
        params.gravity = Gravity.BOTTOM;
        addView(view, params);

        mDeleteView = new FrameLayout(getContext());
        mDeleteView.setOnClickListener(this);
        mDeleteView.setTag(DeleteIconType.DO_NOT_CLICK);
        mDeleteView.setAlpha(0.1f);
        mDeleteView.setBackgroundResource(R.drawable.new_material4_delete);
        params = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(270), ShareData.PxToDpi_xhdpi(76));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = ShareData.PxToDpi_xhdpi(12);
        addView(mDeleteView, params);
        {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(R.drawable.sticker_trash_white);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.rightMargin = ShareData.PxToDpi_xhdpi(40);
            mDeleteView.addView(iv, params);

            TextView tv = new TextView(getContext());
            tv.setText(R.string.material_manage_delete);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            tv.setTextColor(Color.WHITE);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.leftMargin = ShareData.PxToDpi_xhdpi(20);
            mDeleteView.addView(tv, params);
        }

        initData();
    }

    private void initData()
    {
        mAdapter = new StickerLocalPagerAdapter();
        mAdapter.updatePagerSize(StickerLocalMgr.getInstance().getLabelArrValidSize());
        mContentView.setAdapter(mAdapter);
    }

    public void setStickerLocalDataHelper(StickerLocalInnerListener helper)
    {
        mHelper = helper;
        if (mAdapter != null)
        {
            mAdapter.setStickerHelper(helper);
        }
    }

    public void setDeleteViewStatus(int status)
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

    public void setCurrentItem(int index)
    {
        mContentView.setCurrentItem(index, true);
    }

    @Override
    public void onClick(View v)
    {
        if (v == mDeleteView)
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
                    if (mHelper != null)
                    {
                        if (list != null && list.size() > 0 && StickerLocalMgr.getInstance().isHadLocalRes(list))
                        {
                            mHelper.onChangeSelectedIconStatus(StickerMgrPage.SelectedIconType.CHECK_ALL);
                        }
                        else
                        {
                            mHelper.onChangeSelectedIconStatus(StickerMgrPage.SelectedIconType.DO_NOT_SHOW);
                        }
                    }

                    setDeleteViewStatus(DeleteIconType.DO_NOT_CLICK);
                    break;
                }
            }
        }
    }
}

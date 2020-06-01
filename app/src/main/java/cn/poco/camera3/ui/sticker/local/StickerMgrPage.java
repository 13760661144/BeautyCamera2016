package cn.poco.camera3.ui.sticker.local;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.cb.sticker.StickerLocalInnerListener;
import cn.poco.camera3.cb.sticker.StickerUIListener;
import cn.poco.camera3.info.sticker.LabelLocalInfo;
import cn.poco.camera3.mgr.StickerLocalMgr;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

/**
 * 素材管理页
 * Created by Gxx on 2017/10/30.
 */

public class StickerMgrPage extends LinearLayout implements StickerLocalInnerListener, View.OnClickListener
{
    private StickerUIListener mStickerUIController;

    public @interface SelectedIconType
    {
        int DO_NOT_SHOW = 1;
        int CHECK_ALL = 1 << 2;
        int SELECTED_NONE = 1 << 3;
    }

    private PressedButton mBackView;
    private TextView mSelectedView;
    private LabelLocalView mLabel;
    private StickerLocalView mSticker;

    public StickerMgrPage(@NonNull Context context)
    {
        super(context);
        StickerLocalMgr.getInstance().init(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        initView(context);
    }

    public void setStickerUIListener(StickerUIListener listener)
    {
        mStickerUIController = listener;
    }

    public void ClearAll()
    {
        if (mSticker != null)
        {
            mSticker.ClearAll();
            mSticker = null;
        }

        if (mLabel != null)
        {
            mLabel.ClearAll();
            mLabel = null;
        }

        mStickerUIController = null;
        removeAllViews();

        StickerLocalMgr.getInstance().ClearAll();
    }

    private void initView(Context context)
    {
        FrameLayout topLayout = new FrameLayout(context);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(80));
        addView(topLayout, params);
        {
            mBackView = new PressedButton(context);
            mBackView.setOnClickListener(this);
            mBackView.setButtonImage(R.drawable.framework_back_btn, R.drawable.framework_back_btn, ImageUtils.GetSkinColor(), 0.5f);
            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.CENTER_VERTICAL;
            topLayout.addView(mBackView, fp);

            TextView titleView = new TextView(getContext());
            fp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.CENTER;
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            titleView.setTextColor(0xff333333);
            titleView.setText(R.string.sticker_pager_manager_material);
            topLayout.addView(titleView, fp);

            boolean isShowSelectedView = StickerLocalMgr.getInstance().isHadLocalRes(0);
            mSelectedView = new TextView(getContext());
            mSelectedView.setVisibility(isShowSelectedView ? VISIBLE : GONE);
            mSelectedView.setTag(isShowSelectedView ? SelectedIconType.CHECK_ALL : SelectedIconType.DO_NOT_SHOW);
            mSelectedView.setOnClickListener(this);
            fp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
            fp.rightMargin = CameraPercentUtil.WidthPxToPercent(28);
            mSelectedView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            mSelectedView.setTextColor(ImageUtils.GetSkinColor());
            mSelectedView.setText(R.string.material_manage_select_all);
            mSelectedView.setGravity(Gravity.CENTER);
            topLayout.addView(mSelectedView, fp);
        }

        mLabel = new LabelLocalView(context);
        mLabel.setStickerLocalDataHelper(this);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mLabel, params);

        mSticker = new StickerLocalView(context);
        mSticker.setStickerLocalDataHelper(this);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mSticker, params);
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
    public void onStickerPageSelected(int index)
    {
        int last_selected_index = StickerLocalMgr.getInstance().getSelectedLabelIndex();

        LabelLocalInfo previous_info = StickerLocalMgr.getInstance().getLabelInfoByIndex(last_selected_index);
        if (previous_info != null)
        {
            previous_info.isSelected = false;
        }

        LabelLocalInfo info = StickerLocalMgr.getInstance().getLabelInfoByIndex(index);
        if (info != null)
        {
            info.isSelected = true;
            StickerLocalMgr.getInstance().updateSelectedLabelIndex(index);
        }

        mLabel.notifyLabelDataChange(last_selected_index);
        mLabel.notifyLabelDataChange(index);
        mLabel.scrollToCenter(index);
    }

    @Override
    public void onLabelScrollToSelected(int index)
    {
        if (mLabel != null)
        {
            mLabel.scrollToCenter(index);
        }
    }

    @Override
    public void onChangeSelectedIconStatus(int status)
    {
        setSelectedIconStatus(status);
    }

    private void setSelectedIconStatus(int status)
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
        if (mSticker != null)
        {
            mSticker.setDeleteViewStatus(status);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == mBackView)
        {
            if (mStickerUIController != null)
            {
                mStickerUIController.onCloseStickerMgrPage();
            }
        }
        else if (v == mSelectedView)
        {
            int status = (int) mSelectedView.getTag();

            switch (status)
            {
                case SelectedIconType.CHECK_ALL://全选
                {
                    StickerLocalMgr.getInstance().selectedAllSticker(true);
                    setSelectedIconStatus(SelectedIconType.SELECTED_NONE);
                    onChangeDeleteIconAlpha(StickerLocalView.DeleteIconType.CLICKABLE);
                    break;
                }

                case SelectedIconType.SELECTED_NONE://全不选
                {
                    StickerLocalMgr.getInstance().selectedAllSticker(false);
                    setSelectedIconStatus(SelectedIconType.CHECK_ALL);
                    onChangeDeleteIconAlpha(StickerLocalView.DeleteIconType.DO_NOT_CLICK);
                    break;
                }
            }
        }
    }
}

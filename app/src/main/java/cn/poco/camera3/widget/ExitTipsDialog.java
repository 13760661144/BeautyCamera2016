package cn.poco.camera3.widget;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 退出提示
 * Created by Gxx on 2018/2/2.
 */

public class ExitTipsDialog extends AlertDialogV1
{
    public static final int EXIT_CONFIRM = 0x1;
    public static final int EXIT_CANCEL = 0x2;

    private final boolean mIsChinese;
    private TextView mTipsView;
    private TextView mConfirmView;
    private TextView mCancelView;
    private View.OnTouchListener mOnAnimTouchListener;

    public ExitTipsDialog(Context context)
    {
        super(context);

        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
        initCB();
        initView(context);
    }

    private void initCB()
    {
        mOnAnimTouchListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (v == mConfirmView)
                {
                    if (mListener != null)
                    {
                        mListener.onClick(ExitTipsDialog.this, EXIT_CONFIRM);
                    }
                }
                else if (v == mCancelView)
                {
                    if (mListener != null)
                    {
                        mListener.onClick(ExitTipsDialog.this, EXIT_CANCEL);
                    }
                }

                return true;
            }
        };
    }

    private void initView(Context context)
    {
        LinearLayout contentView = new LinearLayout(context);
        contentView.setOrientation(LinearLayout.VERTICAL);

        mTipsView = new TextView(context);
        mTipsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 16 : 14);
        mTipsView.setText(R.string.ar_hide_pre_exit_tips);
        mTipsView.setTextColor(Color.BLACK);
        mTipsView.setTypeface(Typeface.DEFAULT);
        mTipsView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(520), CameraPercentUtil.HeightPxToPercent(160));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        contentView.addView(mTipsView, params);

        mCancelView = new TextView(context);
        mCancelView.setText(R.string.ar_hide_pre_exit_stay);
        setRadius(CameraPercentUtil.WidthPxToPercent(38));
        mCancelView.setBackgroundDrawable(getShapeDrawable(ImageUtils.GetSkinColor()));
        mCancelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
        mCancelView.setTextColor(Color.WHITE);
        mCancelView.setTypeface(Typeface.DEFAULT);
        mCancelView.setSingleLine(true);
        mCancelView.setGravity(Gravity.CENTER);
        mCancelView.setOnTouchListener(mOnAnimTouchListener);
        params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(420), CameraPercentUtil.HeightPxToPercent(80));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        contentView.addView(mCancelView, params);

        mConfirmView = new TextView(context);
        mConfirmView.setText(R.string.ar_hide_pre_exit);
        mConfirmView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
        mConfirmView.setTextColor(Color.GRAY);
        mConfirmView.setTypeface(Typeface.DEFAULT_BOLD);
        mConfirmView.setSingleLine(true);
        mConfirmView.setText(R.string.camera_clear_all_video_abandon);
        mConfirmView.setGravity(Gravity.CENTER);
        mConfirmView.setOnTouchListener(mOnAnimTouchListener);
        params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(420), CameraPercentUtil.HeightPxToPercent(120));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        contentView.addView(mConfirmView, params);

        addContentView(contentView);
        setRadius(CameraPercentUtil.WidthPxToPercent(20));
    }

    public void setText(String tips, String confirm, String cancel)
    {
        if (mTipsView != null)
        {
            mTipsView.setText(tips);
        }

        if (mConfirmView != null)
        {
            mConfirmView.setText(confirm);
        }

        if (mCancelView != null)
        {
            mCancelView.setText(cancel);
        }
    }
}

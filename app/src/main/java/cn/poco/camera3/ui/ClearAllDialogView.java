package cn.poco.camera3.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/31.
 */

public class ClearAllDialogView extends AlertDialogV1 implements View.OnClickListener, View.OnTouchListener
{
    public static final int VIEW_NOT_ABANDON = 1;
    public static final int VIEW_ABANDON = 2;
    private final boolean mIsChinese;

    private TextView mNoAbandonText;
    private TextView mAbandonText;

    public ClearAllDialogView(Context context)
    {
        super(context);
        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
        initView();
    }

    private void initView()
    {
        LinearLayout contentView = new LinearLayout(getContext());
        contentView.setOrientation(LinearLayout.VERTICAL);

        TextView abandonTips = new TextView(getContext());
        abandonTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 16 : 14);
        abandonTips.setTextColor(Color.BLACK);
        abandonTips.setTypeface(Typeface.DEFAULT);
        abandonTips.setSingleLine(true);
        abandonTips.setText(R.string.camera_clear_all_video_tips);
        abandonTips.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(520), CameraPercentUtil.HeightPxToPercent(160));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        contentView.addView(abandonTips, params);

        mAbandonText = new TextView(getContext());
        setRadius(CameraPercentUtil.WidthPxToPercent(38));
        mAbandonText.setBackgroundDrawable(getShapeDrawable(ImageUtils.GetSkinColor()));
        mAbandonText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
        mAbandonText.setTextColor(Color.WHITE);
        mAbandonText.setTypeface(Typeface.DEFAULT_BOLD);
        mAbandonText.setSingleLine(true);
        mAbandonText.setText(R.string.camera_clear_all_video_abandon);
        mAbandonText.setGravity(Gravity.CENTER);
        mAbandonText.setOnTouchListener(this);
        mAbandonText.setOnClickListener(this);
        params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(420), CameraPercentUtil.HeightPxToPercent(80));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        contentView.addView(mAbandonText, params);

        mNoAbandonText = new TextView(getContext());
        mNoAbandonText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
        mNoAbandonText.setTextColor(Color.GRAY);
        mNoAbandonText.setTypeface(Typeface.DEFAULT);
        mNoAbandonText.setSingleLine(true);
        mNoAbandonText.setText(R.string.camera_clear_all_video_not_abandon);
        mNoAbandonText.setGravity(Gravity.CENTER);
        mNoAbandonText.setOnTouchListener(this);
        mNoAbandonText.setOnClickListener(this);
        params = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(420), CameraPercentUtil.HeightPxToPercent(120));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        contentView.addView(mNoAbandonText, params);
        addContentView(contentView);

        setRadius(CameraPercentUtil.WidthPxToPercent(20));
    }

    @Override
    public void show()
    {
        super.show();
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View v)
    {
        if (mListener != null)
        {
            if (v == mNoAbandonText)
            {
                mListener.onClick(this, VIEW_NOT_ABANDON);
            }
            else if (v == mAbandonText)
            {
                mListener.onClick(this, VIEW_ABANDON);
            }
        }
    }

    @Override
    public void dismiss()
    {
        mListener = null;
        mAbandonText.setOnClickListener(null);
        mNoAbandonText.setOnClickListener(null);
        super.dismiss();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            v.setAlpha(0.5f);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
        {
            v.setAlpha(1f);
        }
        return false;
    }
}

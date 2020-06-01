package cn.poco.campaignCenter.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2016/12/15.
 */

public class PoorNetWorkNotification extends FrameLayout{
    private TextView mIndicationText;
    private LinearLayout mViewContainer;
    private ImageView mWarningIcon;

    public PoorNetWorkNotification(Context context) {
        super(context);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(13);
        bg.setColor(Color.WHITE);
        bg.setAlpha(245);
        this.setBackgroundDrawable(bg);
        initView();
    }

    private void initView() {
        mViewContainer = new LinearLayout(getContext());
        mViewContainer.setOrientation(LinearLayout.HORIZONTAL);
        mViewContainer.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewContainer.setLayoutParams(params);
        this.addView(mViewContainer);

        mWarningIcon = new ImageView(getContext());
        mWarningIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mWarningIcon.setImageResource(R.drawable.campaigncenter_network_warn_little);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWarningIcon.setLayoutParams(params1);
        mViewContainer.addView(mWarningIcon);

        mIndicationText = new TextView(getContext());
        mIndicationText.setTextColor(0xfff24949);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.leftMargin = ShareData.PxToDpi_xhdpi(4);
        mIndicationText.setLayoutParams(params2);
        mViewContainer.addView(mIndicationText);
    }

    public void setIndicationText(String text) {
        mIndicationText.setText(text);
    }




}

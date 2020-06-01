package cn.poco.campaignCenter.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;

/**
 * Created by Shine on 2017/1/9.
 */

public class RoundedBgCell extends FrameLayout {
    private ImageView mBg;
    private GradientDrawable mBgDrawable;
    private TextView mText;
    private String mTextContent;
    private int mWidth, mHeight, mRadius;

    public RoundedBgCell(Context context) {
        this(context, ShareData.PxToDpi_xhdpi(270));
    }

    public RoundedBgCell(Context context, int width) {
        this(context, width, null);
    }

    public RoundedBgCell(Context context, int width, String text) {
        this(context, width, ShareData.PxToDpi_xhdpi(74), text);
    }

    public RoundedBgCell(Context context, int width, int height, String text) {
        this(context, width, height, ShareData.PxToDpi_xhdpi(37), text);
    }

    public RoundedBgCell(Context context, int width, int height, int radius, String text) {
        super(context);
        this.mWidth = width;
        this.mHeight = height;
        this.mRadius = radius;
        this.mTextContent = text;
        initView(context);
    }


    private void initView(Context context) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mWidth, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mBg = new ImageView(context);
        mBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mBgDrawable = new GradientDrawable();
        mBgDrawable.setSize(mWidth, mHeight);
        mBgDrawable.setCornerRadius(mRadius);
        mBgDrawable.setShape(GradientDrawable.RECTANGLE);
        mBgDrawable.setColor(Color.RED);
        mBg.setBackgroundDrawable(mBgDrawable);
        mBg.setLayoutParams(layoutParams);
        this.addView(mBg);

        mText = new TextView(context);
        if (mTextContent != null) {
            mText.setText(mTextContent);
        }
        mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mText.setTextColor(0xFFFFFFFF);
        mText.setSingleLine(true);
        mText.setLines(1);
        mText.setMaxLines(1);
        mText.setGravity(Gravity.CENTER);
        mText.setEllipsize(TextUtils.TruncateAt.END);
        mText.setLayoutParams(layoutParams);
        this.addView(mText);
    }

    public void addSkin(int color) {
        mBgDrawable.setColor(color);
    }

    public void setText(CharSequence text) {
        if (text != null && mText != null) {
            mText.setText(text);
        }
    }

    public void setViewWidth(int width) {
        this.mWidth = width;
    }

    public void setViewHeight(int height) {
        this.mHeight = height;
    }

    public void setViewRadius(int radius) {
        this.mRadius = radius;
    }


}

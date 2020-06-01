package cn.poco.clip;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;

/**
 * @author lmx
 *         Created by lmx on 2016/12/2.
 */

public class ClipItemButton extends LinearLayout {

    public ImageView mImg;
    public TextView mTitle;

    public boolean isSelected = false;

    public ClipItemButton(Context context) {
        super(context);
        initUI();
    }

    private void initUI() {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.TRANSPARENT);
        setOnTouchListener(mOnTouchListener);
        LayoutParams lp;

        mImg = new ImageView(getContext());
        mImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        lp = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        this.addView(mImg, lp);

        mTitle = new TextView(getContext());
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11.0f);
        mTitle.setTextColor(0xb3000000);
        lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = ShareData.PxToDpi_xhdpi(18);
        this.addView(mTitle, lp);
    }

    public void InitData(@DrawableRes int imgResId, @NonNull String title, OnClickListener listener) {
        this.mImg.setImageResource(imgResId);
        this.mTitle.setText(title);
        this.listener = listener;
    }

    public void SetImgRes(@DrawableRes int resId, boolean isSelected) {
        this.isSelected = isSelected;
        mImg.setImageResource(resId);
    }

    public void SetTextColor(int color, boolean isSelected) {
        this.isSelected = isSelected;
        mTitle.setTextColor(/*isSelected ? 0xb3000000 : 0x26000000*/color);
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public OnClickListener listener;

    private OnTouchListener mOnTouchListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (listener != null) {
                listener.onClick(v);
            }
        }

        @Override
        public void onTouch(View v) {

        }

        @Override
        public void onRelease(View v) {

        }
    };
}

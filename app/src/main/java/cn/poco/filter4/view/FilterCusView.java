package cn.poco.filter4.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.StatusButton;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2016/12/8.
 */

public class FilterCusView extends LinearLayout {

    public StatusButton mBlurBtn;
    public StatusButton mDarkBtn;

    public FilterCusView(Context context) {
        super(context);
        initView(context);
    }


    private void initView(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(0x0000000);
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setPadding(ShareData.PxToDpi_xhdpi(30), 0, ShareData.PxToDpi_xhdpi(30), 0);

        LayoutParams lp;

        mBlurBtn = new StatusButton(context);
        mBlurBtn.setId(R.id.filter_btn_blur);
        mBlurBtn.SetData(R.drawable.beautify_blur_btn_out, ImageUtils.AddSkin(context, BitmapFactory.decodeResource(getResources(), R.drawable.beautify_blur_btn_over)), ImageView.ScaleType.CENTER_INSIDE);
        lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mBlurBtn.setLayoutParams(lp);
        mBlurBtn.setOnTouchListener(mOnAnimationClickListener);
        this.addView(mBlurBtn);

        mDarkBtn = new StatusButton(context);
        mDarkBtn.SetData(R.drawable.beautify_dark_btn_out, ImageUtils.AddSkin(context, BitmapFactory.decodeResource(getResources(), R.drawable.beautify_dark_btn_over)), ImageView.ScaleType.CENTER_INSIDE);
        mDarkBtn.setId(R.id.filter_btn_dark);
        lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = ShareData.PxToDpi_xhdpi(42);
        mDarkBtn.setLayoutParams(lp);
        mDarkBtn.setOnTouchListener(mOnAnimationClickListener);
        this.addView(mDarkBtn);
    }

    public interface ClickCallback {
        void onClick(View v);
    }

    public void SetDarkOver(boolean over) {
        if (over) {
            mDarkBtn.SetOver();
        } else {
            mDarkBtn.SetOut();
        }
    }

    public void SetBlurOver(boolean over) {
        if (over) {
            mBlurBtn.SetOver();
        } else {
            mBlurBtn.SetOut();
        }
    }

    private ClickCallback mCB;

    public void setmCB(ClickCallback cb) {
        this.mCB = cb;
    }

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mBlurBtn) {
                if (mCB != null) mCB.onClick(v);
            } else if (v == mDarkBtn) {
                if (mCB != null) mCB.onClick(v);
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

package cn.poco.beautifyEyes.Component.Cell;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import my.beautyCamera.R;

/**
 * Created by Shine on 2017/1/3.
 */

public class PinPointCell extends BaseBkCell{
    private ImageView mPinPointBtn;


    public PinPointCell(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mPinPointBtn = new ImageView(context);
        mPinPointBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPinPointBtn.setImageResource(R.drawable.beautify_fix_by_hand);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mPinPointBtn.setLayoutParams(params2);
        mBgContainer.addView(mPinPointBtn);
    }

    public ImageView getPinPointImage() {
        return mPinPointBtn;
    }





}

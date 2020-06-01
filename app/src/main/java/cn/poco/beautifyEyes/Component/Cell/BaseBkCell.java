package cn.poco.beautifyEyes.Component.Cell;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import my.beautyCamera.R;

/**
 * Created by Shine on 2017/1/3.
 */

public class BaseBkCell extends FrameLayout{
    protected FrameLayout mBgContainer;

    public BaseBkCell(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mBgContainer = new FrameLayout(context);
        mBgContainer.setBackgroundResource(R.drawable.beautify_white_circle_bg);
        FrameLayout.LayoutParams paramsBtn = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mBgContainer.setLayoutParams(paramsBtn);
        this.addView(mBgContainer);
    }



}

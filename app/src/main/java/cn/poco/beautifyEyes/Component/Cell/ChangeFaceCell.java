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

public class ChangeFaceCell extends BaseBkCell{
    private ImageView mChangeFaceBtn;

    public ChangeFaceCell(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mChangeFaceBtn = new ImageView(context);
        mChangeFaceBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mChangeFaceBtn.setImageResource(R.drawable.beautify_makeup_multiface_icon);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mChangeFaceBtn.setLayoutParams(params2);
        mBgContainer.addView(mChangeFaceBtn);
    }

    public ImageView getPinPointImage() {
        return mChangeFaceBtn;
    }

}

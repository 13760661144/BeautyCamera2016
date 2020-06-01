package cn.poco.campaignCenter.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import cn.poco.tianutils.ShareData;

/**
 * Created by Shine on 2016/12/15.
 */

public class CustomViewToast{
    private Toast mToast;
    private FrameLayout mToastViewContainer;
    private Context mContext;

    public CustomViewToast(Context context) {
        this.mContext = context;
        mToastViewContainer = new FrameLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mToastViewContainer.setLayoutParams(params);

        if (mToast == null) {
            mToast = new Toast(context);
        }

        mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, ShareData.PxToDpi_xhdpi(16));
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setView(mToastViewContainer);
    }

    public void show() {
        mToast.show();
    }

    public void setCustomView(View v) {
        mToastViewContainer.addView(v);
    }

    public void clear() {
        mToast.cancel();
    }


}

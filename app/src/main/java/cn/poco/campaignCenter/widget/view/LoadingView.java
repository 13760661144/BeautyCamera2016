package cn.poco.campaignCenter.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.tianutils.ShareData;
import cn.poco.utils.WaitAnimDialog;

/**
 * Created by Shine on 2017/1/11.
 */

public class LoadingView extends FrameLayout{
    private WaitAnimDialog.WaitAnimView mWaintAnimView;

    public LoadingView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mWaintAnimView = new WaitAnimDialog.WaitAnimView(context);
        mWaintAnimView.setBkColor(0x19000000);
        mWaintAnimView.setColor(Color.WHITE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(85), ShareData.PxToDpi_xhdpi(48), Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        layoutParams.topMargin = ShareData.PxToDpi_xhdpi(87);
        mWaintAnimView.setLayoutParams(layoutParams);
        this.addView(mWaintAnimView);
    }

}

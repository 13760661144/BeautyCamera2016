package cn.poco.featuremenu.cell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.featuremenu.widget.TopbarDefaultDrawable;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Simon Meng on 2017/10/13.
 * Guangzhou Beauty Information Technology Co.,Ltd
 */

public class NavigationCell extends FrameLayout{
    public ImageView mNavigationBackView;
    private TopbarDefaultDrawable mBackgroundDrawable;

    public NavigationCell(@NonNull Context context) {
        super(context);
        mBackgroundDrawable = new TopbarDefaultDrawable(0, ShareData.PxToDpi_xhdpi(45), ShareData.m_screenWidth, ShareData.PxToDpi_xhdpi(45), SysConfig.s_skinColor1, SysConfig.s_skinColor2);
        this.setBackgroundDrawable(mBackgroundDrawable);
        initView();
    }

    private void initView() {
        mNavigationBackView = new ImageView(getContext());
        mNavigationBackView.setImageResource(R.drawable.featuremenu_nagivation_icon);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(4);
        mNavigationBackView.setLayoutParams(params);
        this.addView(mNavigationBackView);
    }

    public void setNavigationClickListener(View.OnClickListener onClickListener) {
        mNavigationBackView.setOnClickListener(onClickListener);
    }

    public void changeSkin() {
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setGradientColor(SysConfig.s_skinColor1, SysConfig.s_skinColor2);
            mBackgroundDrawable.invalidateSelf();
        }
    }

}

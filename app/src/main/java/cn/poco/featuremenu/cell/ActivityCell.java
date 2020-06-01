package cn.poco.featuremenu.cell;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/9/7.
 */

public class ActivityCell extends FrameLayout {
    private Context mContext;
    private ImageView mLeftIconView, mRightIconView;
    private TextView mActivityView;
    private AnimationDrawable mSpeakerDrawable;

    public static boolean sUserCancel = false;

    public ActivityCell(@NonNull Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        this.setBackgroundColor(0xfff5f5f5);
        mLeftIconView = new ImageView(mContext);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        mLeftIconView.setLayoutParams(params);
        this.addView(mLeftIconView);
        mLeftIconView.setBackgroundResource(R.drawable.frame_animation);
        Drawable drawable = mLeftIconView.getBackground();
        if (drawable instanceof AnimationDrawable) {
            mSpeakerDrawable =(AnimationDrawable) drawable;
            mSpeakerDrawable.start();
            ImageUtils.AddSkin(drawable);
        }

        mActivityView = new TextView(mContext);
        mActivityView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mActivityView.setTextColor(0xff404040);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
        params.leftMargin = ShareData.PxToDpi_xxhdpi(120) + ShareData.PxToDpi_xhdpi(10);
        mActivityView.setLayoutParams(params);
        this.addView(mActivityView);

        mRightIconView = new ImageView(mContext);
        mRightIconView.setImageResource(R.drawable.featuremenu_adcancel_icon);
        mRightIconView.setPadding(ShareData.PxToDpi_xhdpi(5), ShareData.PxToDpi_xhdpi(5), ShareData.PxToDpi_xhdpi(5), ShareData.PxToDpi_xhdpi(5));
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        params.rightMargin = ShareData.PxToDpi_xhdpi(26);
        mRightIconView.setLayoutParams(params);
        this.addView(mRightIconView);
    }

    public void setOnCancelClickListener(OnClickListener listener) {
        this.mRightIconView.setOnClickListener(listener);
    }

    public void setBusinessActivityTitle(String title) {
        mActivityView.setText(title);
    }

    public void addSkin() {
        ImageUtils.AddSkin(mSpeakerDrawable);
    }

    public void resumeSpeakerAnimation() {
        if (mSpeakerDrawable != null && !mSpeakerDrawable.isRunning()) {
            mSpeakerDrawable.start();
        }
    }

    public void clear() {
       if (mSpeakerDrawable != null) {
           mSpeakerDrawable.stop();
       }
    }


}

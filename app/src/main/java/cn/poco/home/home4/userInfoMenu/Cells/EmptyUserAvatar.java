package cn.poco.home.home4.userInfoMenu.Cells;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import my.beautyCamera.R;

/**
 * Created by Shine on 2016/12/20.
 */

public class EmptyUserAvatar extends FrameLayout{
    public ImageView mBackground;
    public ImageView mAvatarIcon;

    public EmptyUserAvatar(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mBackground = new ImageView(context);
        mBackground.setImageResource(R.drawable.homepage_menu_login_bg);
        mBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBackground.setLayoutParams(params);
        this.addView(mBackground);

        mAvatarIcon = new ImageView(context);
        mAvatarIcon.setImageResource(R.drawable.homepage_menu_login);
        mAvatarIcon.setScaleType(ImageView.ScaleType.CENTER);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mAvatarIcon.setLayoutParams(params1);
        this.addView(mAvatarIcon);
    }
}

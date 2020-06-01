package cn.poco.featuremenu.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Simon Meng on 2017/10/17.
 * Guangzhou Beauty Information Technology Co.,Ltd
 */

public class BadgeView extends FrameLayout{
    public static final int NUMBER_BADGEVIEW = 0;
    public static final int TITLE_BADGEVIEW = 1;
    public static final int RED_DOT_TIPS = 2;
    private int mType;

    private ImageView mReddot;
    private TextView mContentView;

    public BadgeView(@NonNull Context context, int type) {
        super(context);
        mType = type;
        initView(context);
    }

    private void initView(Context context) {
        mContentView = new TextView(context);
        mContentView.setGravity(Gravity.CENTER);
        mContentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
        mContentView.setTextColor(0xffffffff);
        mContentView.setEllipsize(TextUtils.TruncateAt.END);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(34));
        mContentView.setLayoutParams(layoutParams);
        this.addView(mContentView);

        mReddot = new ImageView(context);
        mReddot.setScaleType(ImageView.ScaleType.CENTER_CROP);
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mReddot.setLayoutParams(layoutParams);
        this.addView(mReddot);

        if (mType == NUMBER_BADGEVIEW || mType == TITLE_BADGEVIEW) {
            mReddot.setVisibility(View.GONE);
        } else if (mType == RED_DOT_TIPS) {
            mContentView.setVisibility(View.GONE);
        }
    }

    public void setBadgeViewType(int type, String text) {
       if (type == NUMBER_BADGEVIEW || type == TITLE_BADGEVIEW) {
           mType = type;
           if (mContentView.getVisibility() != VISIBLE) {
               mContentView.setVisibility(VISIBLE);
           }
           mReddot.setVisibility(View.GONE);
           if (type == NUMBER_BADGEVIEW) {
               mContentView.setBackgroundResource(R.drawable.featuremenu_number_badge);
           } else if (type == TITLE_BADGEVIEW) {
               mContentView.setBackgroundResource(R.drawable.featuremenu_text_bg);
           }
           mContentView.setText(text);
       } else if (type == RED_DOT_TIPS) {
           mType = type;
           this.mContentView.setVisibility(View.GONE);
           if (this.mReddot.getVisibility() != VISIBLE) {
               this.mReddot.setVisibility(View.VISIBLE);
           }
           this.mReddot.setImageResource(R.drawable.featuremenu_reddot_tips);
       }
       this.getParent().requestLayout();
    }

    public int getBadgeType() {
       return mType;
    }


}

package cn.poco.campaignCenter.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2016/12/13.
 */

public class ViewPagerIndicator extends LinearLayout implements AutoSlideViewPager.IDataChange{
    private int mCount;
    private Context mContext;
    private int mLastIndex;
    private boolean mFirstLayout = true;

    public ViewPagerIndicator(Context context) {
        super(context);
        mContext = context;
    }

    public void setData(int count) {
        mCount = count;
        if (mFirstLayout) {
            initView(mContext);
            mFirstLayout = false;
        }
        ImageView currentActive = (ImageView) this.getChildAt(mLastIndex);
        if (currentActive != null) {
            currentActive.setImageResource(R.drawable.campaigncenter_indicator_selected);
        }
    }

    @SuppressWarnings("ResourceType")
    private void initView(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        int resId;
        for (int i = 0; i < mCount; i++) {
            ImageView circle = new ImageView(context);
            circle.setScaleType(ImageView.ScaleType.CENTER_CROP);
            resId = R.drawable.campaigncenter_indicator_unselected;
            circle.setImageResource(resId);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (i == 0 ? 0 : ShareData.PxToDpi_xhdpi(12));
            circle.setLayoutParams(params);
            this.addView(circle);
        }
    }

    @Override
    public void onDataChange(int activeIndex) {
        ImageView usedActiveChild = (ImageView) this.getChildAt(mLastIndex);
        if (usedActiveChild != null) {
            usedActiveChild.setImageResource(R.drawable.campaigncenter_indicator_unselected);
        }

        ImageView activeChild = (ImageView) this.getChildAt(activeIndex);
        if (activeChild != null) {
            activeChild.setImageResource(R.drawable.campaigncenter_indicator_selected);
        }
        mLastIndex = activeIndex;
    }
}

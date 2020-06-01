package cn.poco.featuremenu.cell;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.featuremenu.model.MenuFeature;
import cn.poco.featuremenu.widget.BadgeView;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/9/6.
 */

public class FeatureCell extends FrameLayout {
    private Context mContext;

    protected ImageView mIconView;
    protected TextView mFeatureTitle, mFeatureHint;

    private BadgeView mBadgeView;
    private FrameLayout mCellContainer;
    private LinearLayout mViewContainer;

    public FeatureCell(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        mCellContainer = new FrameLayout(mContext) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int topMargin = (this.getMeasuredHeight() - mViewContainer.getMeasuredHeight()) / 2 ;
                int rightMargin = (this.getMeasuredWidth() - (mIconView.getMeasuredWidth())) / 2;
                if (mBadgeView.getBadgeType() == BadgeView.TITLE_BADGEVIEW) {
                    ((LayoutParams)mBadgeView.getLayoutParams()).topMargin = topMargin - ShareData.PxToDpi_xhdpi(17);
                    ((LayoutParams)mBadgeView.getLayoutParams()).rightMargin = rightMargin - mBadgeView.getMeasuredWidth() + ShareData.PxToDpi_xhdpi(18);
                } else if (mBadgeView.getBadgeType() == BadgeView.RED_DOT_TIPS) {
                    ((LayoutParams)mBadgeView.getLayoutParams()).topMargin = topMargin + ShareData.PxToDpi_xhdpi(4);
                    ((LayoutParams)mBadgeView.getLayoutParams()).rightMargin = rightMargin - ShareData.PxToDpi_xhdpi(4);
                } else if (mBadgeView.getBadgeType() == BadgeView.NUMBER_BADGEVIEW) {
                    ((LayoutParams)mBadgeView.getLayoutParams()).topMargin = topMargin - ShareData.PxToDpi_xhdpi(9);
                    ((LayoutParams)mBadgeView.getLayoutParams()).rightMargin = rightMargin - mBadgeView.getMeasuredWidth() + ShareData.PxToDpi_xhdpi(18);
                }
            }
        };
        FrameLayout.LayoutParams paramsWhole = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mCellContainer.setLayoutParams(paramsWhole);
        this.addView(mCellContainer);

        mViewContainer = new LinearLayout(mContext);
        mViewContainer.setOrientation(LinearLayout.VERTICAL);
        mViewContainer.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mViewContainer.setLayoutParams(params);
        mCellContainer.addView(mViewContainer);

        mIconView = new ImageView(mContext);
        mIconView.setId(R.id.featuremenu_icon);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mIconView.setLayoutParams(linearParams);
        mViewContainer.addView(mIconView);

        mFeatureTitle = new TextView(mContext);
        mFeatureTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mFeatureTitle.setTextColor(0xff333333);
        linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(4);
        mFeatureTitle.setLayoutParams(linearParams);
        mViewContainer.addView(mFeatureTitle);

        mFeatureHint = new TextView(mContext);
        linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mFeatureHint.setLayoutParams(linearParams);
        mViewContainer.addView(mFeatureHint);
        mFeatureHint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        mFeatureHint.setTextColor(0xff999999);

        mBadgeView = new BadgeView(mContext, BadgeView.TITLE_BADGEVIEW);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
        mBadgeView.setLayoutParams(params);
        mCellContainer.addView(mBadgeView);
    }


    private MenuFeature mMenuFeature;
    public void setData(final MenuFeature menuFeature) {
        if (menuFeature != null) {
            mMenuFeature = menuFeature;
            if (!menuFeature.isPlaceHolder()) {
                this.mFeatureTitle.setText(menuFeature.getTitle());
                this.mIconView.setImageResource(menuFeature.getFeatureIconResId());
                if (!TextUtils.isEmpty(menuFeature.getDescribe())) {
                    this.mFeatureHint.setText(menuFeature.getDescribe());
                } else {
                    this.mFeatureHint.setText("");
                }

                StateListDrawable itemDrawable = new StateListDrawable();
                itemDrawable.addState(new int []{android.R.attr.state_pressed}, new ColorDrawable(0xfff0f0f0));
                itemDrawable.addState(new int[]{}, new ColorDrawable(Color.WHITE));
                this.setBackgroundDrawable(itemDrawable);
                addSkin();

                if (menuFeature.getBadgeTip() instanceof MenuFeature.TextBadge) {
                    setBadgeViewType(BadgeView.TITLE_BADGEVIEW);
                } else if (menuFeature.getBadgeTip() instanceof MenuFeature.NumberBadge) {
                    setBadgeViewType(BadgeView.NUMBER_BADGEVIEW);
                } else if (menuFeature.getBadgeTip() instanceof MenuFeature.RedDotBadge) {
                    setBadgeViewType(BadgeView.RED_DOT_TIPS);
                } else {
                    this.mBadgeView.setVisibility(View.GONE);
                }
            } else {
                this.mFeatureTitle.setText("");
                this.mIconView.setImageDrawable(null);
                this.mFeatureHint.setText("");
                this.setBackgroundColor(Color.WHITE);
            }
        }
    }

    public MenuFeature getData() {
        return mMenuFeature;
    }

    public void hideBadgeView() {
        if (mBadgeView.getVisibility() != View.GONE) {
            mBadgeView.setVisibility(View.GONE);
        }
        mMenuFeature.mShowBadge = false;
    }



    private void setBadgeViewType(int type) {
        if (mMenuFeature == null || mMenuFeature.isPlaceHolder()) {
            return;
        }

        if (mMenuFeature.mShowBadge) {
            MenuFeature.BadgeTip currentTip = mMenuFeature.getBadgeTip();
            if (!TextUtils.isEmpty(currentTip.badgeContent)) {
                if (this.mBadgeView.getVisibility() != View.VISIBLE) {
                    this.mBadgeView.setVisibility(View.VISIBLE);
                }
                this.mBadgeView.setBadgeViewType(type, currentTip.badgeContent);
            } else if (type == BadgeView.RED_DOT_TIPS) {
                if (this.mBadgeView.getVisibility() != View.VISIBLE) {
                    this.mBadgeView.setVisibility(View.VISIBLE);
                }
                this.mBadgeView.setBadgeViewType(type, null);
            }
        } else {
            if (this.mBadgeView.getVisibility() != View.GONE) {
                this.mBadgeView.setVisibility(View.GONE);
            }

        }
    }

    public void addSkin() {
        ImageUtils.AddSkin(this.getContext(), mIconView);
    }


}

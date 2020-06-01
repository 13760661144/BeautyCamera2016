package cn.poco.home.home4.userInfoMenu.Cells;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/7/24.
 */
public abstract class BaseMenuCell extends LinearLayout {
    public ImageView mIconImage;
    protected TextBadgeView mTextBadgeView;

    public BaseMenuCell(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }


    public void addSkin(Context context) {
        ImageUtils.AddSkin(context, mIconImage);
    }

    public void setTextAndIcon(int text, int leftIcon) {
        mTextBadgeView.menuText.setText(getResources().getString(text));
        mIconImage.setImageResource(leftIcon);
        mIconImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        setTextAndIcon(text, 0xff4c4c4c, -1, leftIcon);
    }


    public void setTextAndIcon(int text, int textColor, int textSize, int leftIcon) {
        mTextBadgeView.menuText.setText(getResources().getString(text));
        if (textColor != -1) {
            mTextBadgeView.menuText.setTextColor(textColor);
        }

        if (textSize != -1) {
            mTextBadgeView.menuText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        }
        mIconImage.setImageResource(leftIcon);
    }



    public void setBadgeIcon(int rightIcon) {
        mTextBadgeView.mBadgeIcon.setImageResource(rightIcon);
        mTextBadgeView.mBadgeIcon.setVisibility(View.VISIBLE);
    }

    public void setBadgeIconVisibility(int visibility) {
        mTextBadgeView.mBadgeIcon.setVisibility(visibility);
    }

    public void setRedDotIcon(int iconRes) {
        mTextBadgeView.mRedDot.setImageResource(iconRes);
        mTextBadgeView.mRedDot.setVisibility(View.VISIBLE);
    }

    public void setRedDotIconVisibility(int visibility) {
        mTextBadgeView.mRedDot.setVisibility(visibility);
    }

    protected static class TextBadgeView extends RelativeLayout {
        protected TextView menuText;
        protected ImageView mBadgeIcon;
        protected ImageView mRedDot;

        public TextBadgeView(Context context) {
            super(context);
            initView(context);
        }

        private void initView(Context context) {
            menuText = new TextView(context);
            menuText.setLines(1);
            menuText.setMaxLines(1);
            menuText.setSingleLine(true);
            menuText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            menuText.setTextColor(0xff333333);
            menuText.setIncludeFontPadding(false);
            menuText.setId(R.id.usermenu_text);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            menuText.setLayoutParams(params2);
            this.addView(menuText);

            mBadgeIcon = new ImageView(context);
            mBadgeIcon.setBaselineAlignBottom(true);
            mBadgeIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mBadgeIcon.setId(R.id.usermenu_badgeview);
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params3.addRule(RelativeLayout.RIGHT_OF, menuText.getId());
            params3.addRule(RelativeLayout.ABOVE, menuText.getId());
            params3.leftMargin = ShareData.PxToDpi_xhdpi(-10);
            params3.bottomMargin = ShareData.PxToDpi_xhdpi(-16);
            mBadgeIcon.setClickable(false);
            this.addView(mBadgeIcon, params3);
            mBadgeIcon.setVisibility(View.GONE);

            mRedDot = new ImageView(context);
            params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params3.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            params3.addRule(RelativeLayout.RIGHT_OF, menuText.getId());
            params3.leftMargin = ShareData.PxToDpi_xhdpi(14);
            mRedDot.setLayoutParams(params3);
            this.addView(mRedDot);
            mRedDot.setVisibility(View.GONE);
        }
    }
}


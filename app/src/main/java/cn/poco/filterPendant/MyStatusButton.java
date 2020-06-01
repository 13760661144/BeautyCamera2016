package cn.poco.filterPendant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2016/12/5.
 */

public class MyStatusButton extends FrameLayout {

    private ImageView mIconView;
    private ImageView mArrowView;
    private Bitmap mDefIcon;
    private Bitmap mIcon;
    private TextView mName;
    private ImageView mLine;
    private ImageView mNew;
    private boolean isCircleIcon = false;
    private boolean isSeletecd = true;
    private boolean isDown = false;

    private int mArrowViewRightMagin = 0;


    public MyStatusButton(Context context) {
        super(context);
        initialize(context);
    }

    public MyStatusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        ShareData.InitData(context);
        LayoutParams fl_lp;
        LinearLayout.LayoutParams ll_lp;

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.HORIZONTAL);
        fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl_lp.gravity = Gravity.CENTER;
        container.setLayoutParams(fl_lp);
        this.addView(container);

        mIconView = new ImageView(context);
        mIconView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ll_lp = new LinearLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(50), CameraPercentUtil.HeightPxToPercent(50));
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        mIconView.setLayoutParams(ll_lp);
        container.addView(mIconView);

        mName = new TextView(context);
        mName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.0f);
        mName.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
        ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        ll_lp.setMargins(CameraPercentUtil.WidthPxToPercent(5), 0, 0, 0);
        mName.setLayoutParams(ll_lp);
        container.addView(mName);

        mArrowView = new ImageView(context);
        mArrowView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mArrowView.setImageResource(R.drawable.beauty_status_btn_arrow);
        ImageUtils.AddSkin(getContext(), mArrowView);
        ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll_lp.gravity = Gravity.CENTER_VERTICAL;
        ll_lp.setMargins(CameraPercentUtil.WidthPxToPercent(5), 0, mArrowViewRightMagin = CameraPercentUtil.WidthPxToPercent(5), 0);
        mArrowView.setLayoutParams(ll_lp);
        container.addView(mArrowView);

        mNew = new ImageView(context);
        mNew.setImageResource(R.drawable.beautify4page_button_new);
        mNew.setVisibility(View.GONE);
        fl_lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl_lp.gravity = Gravity.LEFT | Gravity.TOP;
        fl_lp.topMargin = CameraPercentUtil.HeightPxToPercent(16);
        fl_lp.leftMargin = CameraPercentUtil.WidthPxToPercent(18);
        this.addView(mNew, fl_lp);

        mLine = new ImageView(context);
        mLine.setScaleType(ImageView.ScaleType.FIT_XY);
        mLine.setBackgroundColor(ImageUtils.GetSkinColor(0xffe75988));
        fl_lp = new LayoutParams(CameraPercentUtil.WidthPxToPercent(180), CameraPercentUtil.HeightPxToPercent(4));
        fl_lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mLine.setLayoutParams(fl_lp);
        this.addView(mLine);
        mLine.setVisibility(View.INVISIBLE);
    }

    public void setData(Bitmap defIcon, String name) {
        mDefIcon = defIcon;
        mIconView.setImageBitmap(defIcon);
        mName.setText(name);
    }

    public void setData(@DrawableRes int resId, @NonNull String name) {
        mDefIcon = ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), resId));
        mIconView.setImageBitmap(mDefIcon);
        mName.setText(name);
    }

    public void setName(@NonNull String name) {
        mName.setText(name);
    }

    public void setIcon(@DrawableRes int resId) {
        mDefIcon = ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), resId));
        mIconView.setImageBitmap(mDefIcon);
    }

    public void setCircleIcon(Bitmap icon) {
        if (mIcon != null && mIcon != mDefIcon) {
            mIcon.recycle();
            mIcon = null;
        }
        if (icon != null) {
            isCircleIcon = true;
            mIcon = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mIcon);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            paint.setAntiAlias(true);
            canvas.drawCircle(mIcon.getWidth() / 2, mIcon.getHeight() / 2, mIcon.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(icon, new Matrix(), paint);
            icon.recycle();
            icon = null;
        } else {
            mIcon = mDefIcon;
            isCircleIcon = false;
        }
        if (isCircleIcon) {
            ImageUtils.RemoveSkin(getContext(), mIconView);
        } else {
            if (isSeletecd()) {
                ImageUtils.AddSkin(getContext(), mIconView);
            } else {
                ImageUtils.AddSkinColor(getContext(), mIconView, 0xff737373);
            }
        }
        mIconView.setImageBitmap(mIcon);
    }

    public void setBtnStatus(boolean isSelected, boolean isDown) {
        this.isSeletecd = isSelected;
        this.isDown = isDown;
        if (isDown) {//收缩
            if (isSelected) {
                mLine.setVisibility(INVISIBLE);
                mArrowView.setVisibility(VISIBLE);

            } else {
                mLine.setVisibility(INVISIBLE);
                mArrowView.setVisibility(INVISIBLE);
            }
            mArrowView.animate().rotation(180).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).start();
        } else { //展开
            if (isSelected) {
                mArrowView.setVisibility(VISIBLE);
                mLine.setVisibility(VISIBLE);
            } else {
                mArrowView.setVisibility(INVISIBLE);
                mLine.setVisibility(INVISIBLE);
            }
            mArrowView.animate().rotation(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200).start();
        }

        if (isSelected) {
            if (isCircleIcon) {
                ImageUtils.RemoveSkin(getContext(), mIconView);
            } else {
                ImageUtils.AddSkin(getContext(), mIconView);
            }
            mName.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
        } else {
            if (isCircleIcon) {
                ImageUtils.RemoveSkin(getContext(), mIconView);
            } else {
                ImageUtils.AddSkinColor(getContext(), mIconView, 0xff737373);
            }
            mName.setTextColor(0xff737373);
        }
    }

    public boolean isDown() {
        return isDown;
    }

    public boolean isSeletecd() {
        return isSeletecd;
    }

    public void setNewStatus(boolean isShow) {
        mNew.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setArrowViewRightMargin(int rightMargin) {
        mArrowViewRightMagin = rightMargin;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mArrowView.getLayoutParams();
        lp.rightMargin = rightMargin;
        mArrowView.setLayoutParams(lp);
    }

    public void setLineWidth(int width) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mLine.getLayoutParams();
        lp.width = width;
        mLine.setLayoutParams(lp);
    }

    public boolean getNewStatusVisible() {
        return mNew.getVisibility() == VISIBLE;
    }

    public void releaseMem() {
        if (mDefIcon != null && !mDefIcon.isRecycled()) {
            mDefIcon.recycle();
            mDefIcon = null;
        }
        if (mIcon != null && !mIcon.isRecycled()) {
            mIcon.recycle();
            mIcon = null;
        }
    }

}

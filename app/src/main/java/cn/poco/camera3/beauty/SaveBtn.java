package cn.poco.camera3.beauty;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.ui.drawable.RoundRectDrawable;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2018-01-17.
 */

public class SaveBtn extends LinearLayout
{
    public boolean isSaved = false;
    public ImageView icon;
    public TextView txt;

    public Drawable mDrawable;

    public SaveBtn(@NonNull Context context)
    {
        super(context);
        initView();
    }

    private void initView()
    {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);

        icon = new ImageView(getContext());
        icon.setImageResource(R.drawable.ic_shape_unsaved_icon);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.rightMargin = CameraPercentUtil.WidthPxToPercent(2);
        this.addView(icon, layoutParams);

        txt = new TextView(getContext());
        txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        txt.setTextColor(0xff333333);
        txt.setText(R.string.beauty_selector_view_shape_unsaved_params);
        txt.setGravity(Gravity.CENTER);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.leftMargin = CameraPercentUtil.WidthPxToPercent(1);
        this.addView(txt, layoutParams);
    }

    /**
     * @param save true 已保存
     */
    public void setSaved(boolean save)
    {
        isSaved = save;
        txt.setText(save ? R.string.beauty_selector_view_shape_saved_params : R.string.beauty_selector_view_shape_unsaved_params);
        txt.setTextColor(save ? ImageUtils.GetSkinColor() : 0xff333333);
        if (save)
        {
            icon.setColorFilter(ImageUtils.GetSkinColor(), PorterDuff.Mode.SRC_IN);
        }
        else
        {
            icon.clearColorFilter();
        }
        icon.setImageResource(save ? R.drawable.ic_shape_saved_icon : R.drawable.ic_shape_unsaved_icon);
        changeDrawable();
        requestLayout();
    }


    private void changeDrawable()
    {
        if (mDrawable == null)
        {
            RoundRectDrawable drawable = new RoundRectDrawable();
            drawable.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(28), CameraPercentUtil.WidthPxToPercent(28));
            drawable.setColor(ImageUtils.GetColorAlpha(Color.WHITE, 0.96f));
            drawable.setShadowLayer(CameraPercentUtil.WidthPxToPercent(5), 0, CameraPercentUtil.WidthPxToPercent(2), ImageUtils.GetColorAlpha(Color.BLACK, 0.6f));
            setBackgroundDrawable(mDrawable = drawable);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSize = isSaved ? CameraPercentUtil.WidthPxToPercent(150) : CameraPercentUtil.WidthPxToPercent(125);
        }
        setMeasuredDimension(widthSize, MeasureSpec.getSize(heightMeasureSpec));
    }

    public boolean isSaved()
    {
        return isSaved;
    }
}

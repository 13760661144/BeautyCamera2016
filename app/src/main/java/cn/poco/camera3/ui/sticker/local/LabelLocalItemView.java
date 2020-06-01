package cn.poco.camera3.ui.sticker.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * @author Created by Gxx on 2017/10/30.
 */

public class LabelLocalItemView extends FrameLayout
{
    public LabelLocalItemView(@NonNull Context context)
    {
        super(context);
        initView(context);
    }

    private void initView(Context context)
    {
        View bottomLine = new View(context);
        bottomLine.setAlpha(0);
        bottomLine.setBackgroundColor(ImageUtils.GetSkinColor());
        bottomLine.setId(R.id.sticker_local_label_bottom_line);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(108), CameraPercentUtil.HeightPxToPercent(4));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(bottomLine, params);

        TextView label = new TextView(context);
        label.setId(R.id.sticker_local_label_text);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.leftMargin = CameraPercentUtil.WidthPxToPercent(28);
        params.rightMargin = CameraPercentUtil.WidthPxToPercent(28);
        addView(label, params);
    }
}

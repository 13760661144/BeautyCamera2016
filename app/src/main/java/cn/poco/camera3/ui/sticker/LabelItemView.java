package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.newSticker.PointCircle;
import my.beautyCamera.R;

/**
 * 贴纸标签
 * Created by Gxx on 2017/10/13.
 */

public class LabelItemView extends FrameLayout
{
    public @interface Type
    {
        int TYPE_IMAGE = 1;
        int TYPE_IMAGE_MGR = 1 << 1;
        int TYPE_TEXT = 1 << 2;
    }

    public LabelItemView(@NonNull Context context, int type)
    {
        this(context);
        initView(context, type);
    }

    private LabelItemView(@NonNull Context context)
    {
        super(context);
    }

    private void initView(Context context, int type)
    {
        View bottomLine = new View(context);
        bottomLine.setAlpha(0);
        bottomLine.setId(R.id.sticker_label_bottom_line);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(108), CameraPercentUtil.HeightPxToPercent(4));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        addView(bottomLine, params);

        if (type == Type.TYPE_IMAGE)
        {
            //带有icon
            ImageView logo = new ImageView(context);
            logo.setId(R.id.sticker_label_logo);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(16);
            params.gravity = Gravity.CENTER_VERTICAL;
            addView(logo, params);

            RelativeLayout itemView = initItemView(context);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(43);
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(28);
            addView(itemView, params);
        }
        else if (type == Type.TYPE_IMAGE_MGR)
        {
            //管理类型标签
            ImageView logo = new ImageView(context);
            logo.setId(R.id.sticker_label_logo);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(19);
            params.gravity = Gravity.CENTER_VERTICAL;
            addView(logo, params);

            RelativeLayout layout = initItemView(context);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(49);
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(30);
            addView(layout, params);
        }
        else if (type == Type.TYPE_TEXT)
        {
            RelativeLayout itemView = initItemView(context);
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = CameraPercentUtil.WidthPxToPercent(28);
            params.rightMargin = CameraPercentUtil.WidthPxToPercent(28);
            addView(itemView, params);
        }
    }

    private RelativeLayout initItemView(Context context)
    {
        RelativeLayout itemView = new RelativeLayout(context);

        TextView label = new TextView(context);
        label.setId(R.id.sticker_label_text);
        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        itemView.addView(label, params);

        //右上角tip红点
        PointCircle redPoint = new PointCircle(context);
        redPoint.setId(R.id.sticker_label_point_right_top);
        redPoint.setAlpha(0);
        redPoint.setColor(0xFFFF4747);
        params = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(12), CameraPercentUtil.WidthPxToPercent(12));
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.sticker_label_text);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.sticker_label_text);
        params.leftMargin = -CameraPercentUtil.WidthPxToPercent(5);
        itemView.addView(redPoint, params);

        return itemView;
    }
}

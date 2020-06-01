package cn.poco.campaignCenter.ui.cells;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;

/**
 * Created by admin on 2016/10/12.
 */

public class MenuBaseCell extends LinearLayout {
    private ImageView leftMenuIcon;
    private TextView menuText;

    public MenuBaseCell(Context context) {
        super(context);
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);

        leftMenuIcon = new ImageView(context);
        leftMenuIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.leftMargin = ShareData.PxToDpi_xhdpi(38);
        this.addView(leftMenuIcon, params1);

        menuText = new TextView(context);
        menuText.setLines(1);
        menuText.setMaxLines(1);
        menuText.setSingleLine(true);
        menuText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        menuText.setTextColor(Color.parseColor("#000000"));
        menuText.setAlpha(0.8f);

        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.leftMargin = ShareData.PxToDpi_xhdpi(30);
        this.addView(menuText, params2);

    }

    public void setTextAndIcon(int text, int leftIcon) {
        menuText.setText(getResources().getString(text));
        leftMenuIcon.setImageResource(leftIcon);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

}





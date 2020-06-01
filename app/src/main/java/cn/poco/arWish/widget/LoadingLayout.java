package cn.poco.arWish.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adnonstop.beautymall.views.loading.WaitAnimView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;

/**
 * Created by admin on 2018/2/1.
 */

public class LoadingLayout extends LinearLayout {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout();
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public LoadingLayout(Context context) {
        super(context);
        initLayout();
    }

    private TextView mText;

    private void initLayout() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        int padding = ShareData.PxToDpi_xhdpi(30);
        setPadding(padding, padding, padding, padding);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0x77000000);
        bg.setCornerRadius(padding / 2);
        setBackground(bg);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(150), ShareData.PxToDpi_xhdpi(90));
        WaitAnimView waitAnimView = new WaitAnimView(getContext());
        addView(waitAnimView, layoutParams);

        layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = ShareData.PxToDpi_xhdpi(20);
        mText = new TextView(getContext());
        mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mText.setTextColor(ImageUtils.GetSkinColor());
        addView(mText, layoutParams);
    }

    public void setMessage(String message) {
        mText.setText(message);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void dismiss() {
        setVisibility(GONE);
    }
}

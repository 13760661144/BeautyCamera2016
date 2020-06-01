package cn.poco.featuremenu.cell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Shine on 2017/9/7.
 */

public class LoadMoreStateCell extends FrameLayout{
    private TextView mText;

    public LoadMoreStateCell(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mText = new TextView(getContext());
        mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        mText.setTextColor(0xff999999);
        mText.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mText.setLayoutParams(params);
        this.addView(mText);
    }

    public void setText(String text) {
        mText.setText(text);
    }


}

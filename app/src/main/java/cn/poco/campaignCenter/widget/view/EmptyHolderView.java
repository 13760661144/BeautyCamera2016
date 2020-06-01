package cn.poco.campaignCenter.widget.view;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import my.beautyCamera.R;


/**
 * Created by Shine on 2016/12/14.
 */

public class EmptyHolderView extends FrameLayout{

    private TextView mIndicationText;
    private ImageView mWarningIcon;

    private LinearLayout mViewContainer;

    public EmptyHolderView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mViewContainer = new LinearLayout(context);
        mViewContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        mViewContainer.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mViewContainer.setLayoutParams(params);
        this.addView(mViewContainer);

        mWarningIcon = new ImageView(context);
        mWarningIcon.setImageResource(R.drawable.campaigncenter_network_warn_big);
        mWarningIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWarningIcon.setLayoutParams(params1);
        mViewContainer.addView(mWarningIcon);

        mIndicationText = new TextView(context);
        mIndicationText.setText(context.getString(R.string.poor_network2) + System.getProperty ("line.separator") + context.getString(R.string.check_network_configuration));
        mIndicationText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mIndicationText.setLayoutParams(params2);
        mViewContainer.addView(mIndicationText);
    }

    public void setOnEmptyViewClick(OnClickListener listener) {
        this.setOnClickListener(listener);
    }



}

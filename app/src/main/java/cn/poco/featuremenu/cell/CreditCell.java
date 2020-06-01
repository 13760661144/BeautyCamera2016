package cn.poco.featuremenu.cell;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/9/8.
 */

public class CreditCell extends LinearLayout{
    private ImageView mCreditIcon;
    private TextView mMyCredit;

    public CreditCell(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        this.setBackgroundResource(R.drawable.featuremenu_mycredit_bg);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);

        mCreditIcon = new ImageView(getContext());
        mCreditIcon.setImageResource(R.drawable.featuremenu_credit_icon);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mCreditIcon.setLayoutParams(params);
        this.addView(mCreditIcon);

        mMyCredit = new TextView(getContext());
        mMyCredit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        mMyCredit.setTextColor(Color.WHITE);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = ShareData.PxToDpi_xhdpi(5);
        mMyCredit.setLayoutParams(params);
        this.addView(mMyCredit);
    }

    public void setUserCredit(String credit) {
        mMyCredit.setText(getContext().getString(R.string.myCredit) + " " + credit);
    }




}

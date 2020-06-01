package cn.poco.setting;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class SettingArrowBtn extends RelativeLayout
{
	public SettingArrowBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public SettingArrowBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public SettingArrowBtn(Context context) {
		super(context);
		initialize(context);
	}
	
	ImageView mArrowIcon;
	TextView  mTxTitle;
	
	protected void initialize(Context context) {
		ShareData.InitData((Activity)context);//by cgf 2014-02-25
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		mArrowIcon = new ImageView(context);
		mArrowIcon.setImageResource(R.drawable.setting_arrow);
		addView(mArrowIcon, params);
		mArrowIcon.setId(R.id.setting_arrowbtn_arrowicon);
		
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.LEFT_OF, R.id.setting_arrowbtn_arrowicon);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		mTxTitle = new TextView(context);
		addView(mTxTitle, params);
		mTxTitle.setTextColor(0xff000000);
		//mTxTitle.setPadding(0, 0, ShareData.PxToDpi_hdpi(24), 0);
		mTxTitle.setPadding(0, 0, ShareData.PxToDpi_xhdpi(26), 0);
	}
	
	public void setText(String text) {
		mTxTitle.setText(text);
	}
	
	public void setTextColor(int color) {
		mTxTitle.setTextColor(color);
	}
}
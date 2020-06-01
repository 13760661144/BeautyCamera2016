package cn.poco.setting;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;

public class SettingItem extends RelativeLayout
{

	public SettingItem(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public SettingItem(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public SettingItem(Context context)
	{
		super(context);
		initialize(context);
	}

	public SettingItem(Context context, String text, View button)
	{
		super(context);
		initialize(context);
		setText(text);
		if(button != null)
		{
			setButton(button);
		}
	}

	TextView mTxTitle;
	RelativeLayout mButtonHolder;

	protected void initialize(Context context)
	{
		ShareData.InitData((Activity)context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		mTxTitle = new TextView(context);
		addView(mTxTitle, params);
		mTxTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
		mTxTitle.setTextColor(0xff333333);
		mTxTitle.setPadding(ShareData.PxToDpi_xhdpi(78), 0, 0, 0);

		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mButtonHolder = new RelativeLayout(context);
		addView(mButtonHolder, params);
		mButtonHolder.setPadding(0, 0, ShareData.PxToDpi_xhdpi(26), 0);

		params = new LayoutParams(ShareData.m_screenWidth * 8 / 9, ShareData.PxToDpi_xhdpi(1));
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		View line = new View(getContext());
		line.setBackgroundColor(0x19000000);
		addView(line, params);
	}

	public void setText(String strText)
	{
		mTxTitle.setText(strText);
	}

	public void setButton(View button)
	{
		mButtonHolder.removeAllViews();
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		mButtonHolder.addView(button, params);
	}
}

package cn.poco.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;

/**
 * Created by Raining on 2017/1/5.
 * 4.0UI按钮
 */

public class MyTextButton extends FrameLayout
{
	public ImageView mBk;
	public TextView mName;

	public MyTextButton(Context context)
	{
		super(context);

		Init();
	}

	public MyTextButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Init();
	}

	public MyTextButton(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		Init();
	}

	protected void Init()
	{
		FrameLayout.LayoutParams fl;

		mBk = new ImageView(getContext());
		mBk.setScaleType(ImageView.ScaleType.FIT_XY);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(mBk, fl);
		ImageUtils.AddSkin(getContext(), mBk);

		mName = new TextView(getContext());
		mName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		mName.setSingleLine();
		mName.setTextColor(0xffffffff);
		mName.setGravity(Gravity.CENTER);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		this.addView(mName, fl);
	}

	public void setBk(int res)
	{
		mBk.setImageResource(res);
	}

	public void setName(int res, float size, int color, boolean bold)
	{
		mName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
		mName.setText(res);
		mName.setTextColor(color);
		if(bold)
		{
			mName.getPaint().setFakeBoldText(true);
		}
		else
		{
			mName.getPaint().setFakeBoldText(false);
		}
	}
}

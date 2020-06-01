package cn.poco.beautify4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Raining on 2016/12/7.
 * 美化下面的按钮
 */

public class MyButton1 extends FrameLayout
{
	protected ImageView mImg;
	protected TextView mText;
	protected ImageView mNewIcon;
	protected int mRes;
	protected String mName;

	public MyButton1(Context context)
	{
		super(context);

		Init();
	}

	public MyButton1(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Init();
	}

	public MyButton1(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		Init();
	}

	protected void Init()
	{
		setClipToPadding(false);
		setPadding(ShareData.PxToDpi_xhdpi(20), 0, ShareData.PxToDpi_xhdpi(20), 0);
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		LinearLayout lfr = new LinearLayout(getContext());
		lfr.setOrientation(LinearLayout.VERTICAL);
		lfr.setGravity(Gravity.CENTER);
		fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		this.addView(lfr, fl);
		{
			mImg = new ImageView(getContext());
			ImageUtils.AddSkin(getContext(), mImg);
			ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER_HORIZONTAL;
			lfr.addView(mImg, ll);

			mText = new TextView(getContext());
			mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
			mText.setIncludeFontPadding(false);
			mText.setTextColor(0x70000000);
			ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_xhdpi(10);
			lfr.addView(mText, ll);
		}
	}

	public void SetNew(boolean flag, int deltaX, int deltaY)
	{
		if(mNewIcon != null)
		{
			this.removeView(mNewIcon);
			mNewIcon = null;
		}
		if(flag)
		{
			mNewIcon = new ImageView(getContext());
			mNewIcon.setImageResource(R.drawable.beautify4page_button_new);
			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.END | Gravity.TOP;
			fl.rightMargin = ShareData.PxToDpi_xhdpi(-12 + deltaX);
			fl.topMargin = deltaY;
			this.addView(mNewIcon, fl);
		}
	}

	public void SetData(int res, String name)
	{
		mRes = res;
		mName = name;

		mImg.setImageResource(res);
		mText.setText(name);
	}

	public int getRes()
	{
		return mRes;
	}

	public String getName()
	{
		return mName;
	}
}

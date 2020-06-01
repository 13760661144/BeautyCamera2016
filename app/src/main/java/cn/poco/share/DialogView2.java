package cn.poco.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2017/9/27.
 */

public class DialogView2 extends FrameLayout implements View.OnClickListener, View.OnTouchListener
{
	public static final int VIEW_BUTTON = 100;
	public static final int VIEW_EXIT = 101;

	private TextView mText;
	private TextView mButton;
	private TextView mExit;

	private boolean mIsChinese;
	private SharePage.DialogListener mListener;

	public DialogView2(Context context, Bitmap bg)
	{
		super(context);
		mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
		init(bg);
	}

	private void init(Bitmap bg)
	{
		setLayoutParams(new LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight));
		if(bg != null && !bg.isRecycled()) setBackgroundDrawable(new BitmapDrawable(getResources(), bg));
		else setBackgroundColor(0xb2ffffff);

		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		LinearLayout mainFrame = new LinearLayout(getContext());
		mainFrame.setBackgroundDrawable(getShapeDrawable(PercentUtil.WidthPxToPercent(20), Color.WHITE));
		mainFrame.setOrientation(LinearLayout.VERTICAL);
		fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(570), LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		addView(mainFrame, fl);
		{
			mText = new TextView(getContext());
			mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 16 : 14);
			mText.setTextColor(Color.BLACK);
			mText.setTypeface(Typeface.DEFAULT);
			mText.setGravity(Gravity.CENTER);
			mText.setLineSpacing(ShareData.PxToDpi_xhdpi(15), 1);
			ll = new LinearLayout.LayoutParams(PercentUtil.WidthPxToPercent(520), LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_xhdpi(61);
			mainFrame.addView(mText, ll);

			mButton = new TextView(getContext());
			mButton.setBackgroundDrawable(getShapeDrawable(PercentUtil.WidthPxToPercent(38), ImageUtils.GetSkinColor()));
			mButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
			mButton.setTextColor(Color.WHITE);
			mButton.setTypeface(Typeface.DEFAULT_BOLD);
			mButton.setSingleLine(true);
			mButton.setGravity(Gravity.CENTER);
			mButton.setOnTouchListener(this);
			mButton.setOnClickListener(this);
			ll = new LinearLayout.LayoutParams(PercentUtil.WidthPxToPercent(420), PercentUtil.HeightPxToPercent(80));
			ll.gravity = Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_xhdpi(35);
			mainFrame.addView(mButton, ll);

			mExit = new TextView(getContext());
			mExit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mIsChinese ? 13 : 12);
			mExit.setTextColor(Color.GRAY);
			mExit.setTypeface(Typeface.DEFAULT);
			mExit.setSingleLine(true);
			mExit.setText(R.string.camera_clear_all_video_not_abandon);
			mExit.setGravity(Gravity.CENTER);
			mExit.setPadding(ShareData.PxToDpi_xhdpi(30), 0, ShareData.PxToDpi_xhdpi(30), 0);
			mExit.setOnTouchListener(this);
			mExit.setOnClickListener(this);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, PercentUtil.HeightPxToPercent(90));
			ll.gravity = Gravity.CENTER_HORIZONTAL;
			mainFrame.addView(mExit, ll);
		}
	}

	public void setInfo(String text, String buttonText, SharePage.DialogListener onClickListener)
	{
		mListener = onClickListener;
		mText.setText(text);
		mButton.setText(buttonText);
	}

	private ShapeDrawable getShapeDrawable(int radius, int color)
	{
		float[] outerRadii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
		RoundRectShape round = new RoundRectShape(outerRadii, null, null);
		ShapeDrawable shape = new ShapeDrawable(round);
		shape.getPaint().setColor(color);
		shape.getPaint().setAntiAlias(true);
		shape.getPaint().setDither(true);
		shape.getPaint().setStyle(Paint.Style.FILL);
		return shape;
	}

	@Override
	public void onClick(View v)
	{
		if(mListener != null)
		{
			if(v == mButton)
			{
				mListener.onClick(VIEW_BUTTON);
			}
			else if(v == mExit)
			{
				mListener.onClick(VIEW_EXIT);
			}
		}
	}


	public void clean()
	{
		mListener = null;
		mButton.setOnClickListener(null);
		mButton.setOnTouchListener(null);
		mExit.setOnClickListener(null);
		mExit.setOnTouchListener(null);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			v.setAlpha(0.5f);
		}
		else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			v.setAlpha(1f);
		}
		return false;
	}
}

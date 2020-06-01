package cn.poco.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2016/12/27.
 */

public class DialogView extends FrameLayout
{
	private ImageView mIcon;
	private TextView mText;
	private TextView mDetermine;

	public DialogView(Context context, Bitmap bg)
	{
		super(context);
		init(bg);
	}

	private void init(Bitmap bg)
	{
		setLayoutParams(new LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight));
		if(bg != null && !bg.isRecycled()) setBackgroundDrawable(new BitmapDrawable(getResources(), bg));
		else setBackgroundColor(0xb2ffffff);

		FrameLayout.LayoutParams fl;

		FrameLayout mainFrame = new FrameLayout(getContext());
		mainFrame.setBackgroundResource(R.drawable.share_dialog_bg);
		fl = new LayoutParams(ShareData.PxToDpi_xhdpi(568), ShareData.PxToDpi_xhdpi(340));
		fl.gravity = Gravity.CENTER;
		addView(mainFrame, fl);
		{
			mIcon = new ImageView(getContext());
			fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = ShareData.PxToDpi_xhdpi(52);
			mainFrame.addView(mIcon, fl);

			mText = new TextView(getContext());
			mText.setTextColor(0xff333333);
			mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = ShareData.PxToDpi_xhdpi(172);
			mainFrame.addView(mText, fl);

			mDetermine = new TextView(getContext());
			mDetermine.setText("确定");
			mDetermine.setTextColor(0xffa0a0a0);
			mDetermine.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			TextPaint paint = mDetermine.getPaint();
			paint.setFakeBoldText(true);
			fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			fl.bottomMargin = ShareData.PxToDpi_xhdpi(22);
			mainFrame.addView(mDetermine, fl);
		}
	}

	public void setInfo(boolean result, String text, View.OnClickListener onClickListener)
	{
		if(result)
		{
			mIcon.setVisibility(View.VISIBLE);
			mIcon.setImageResource(R.drawable.sharepage_dialog_success);
		}
		else
		{
			mIcon.setVisibility(View.VISIBLE);
			mIcon.setImageResource(R.drawable.sharepage_dialog_fail);
		}
		mText.setText(text);
		mDetermine.setOnClickListener(onClickListener);
	}
}

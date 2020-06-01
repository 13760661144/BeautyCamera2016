package cn.poco.arWish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.lightApp06.RoundProgressBar;
import cn.poco.video.view.AutoRoundProgressBar;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2018/1/22.
 */

public class UploadRoundProgressView extends FrameLayout
{
	protected RoundProgressBar mBar;
	private int mProgress = 0;

	public UploadRoundProgressView(@NonNull Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		FrameLayout frameLayout = new FrameLayout(getContext());
		FrameLayout.LayoutParams fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		addView(frameLayout, fl);
		{
			mBar = new RoundProgressBar(getContext());
			mBar.setMax(100);
			mBar.setProgress(0);
			fl = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(200), CameraPercentUtil.HeightPxToPercent(200));
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			frameLayout.addView(mBar, fl);

			TextView textView = new TextView(getContext());
			textView.setSingleLine(true);
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.WHITE);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
			textView.setText(R.string.arwish_upload_video);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = CameraPercentUtil.HeightPxToPercent(50 + 200);
			frameLayout.addView(textView, fl);
		}


		TextView textView2 = new TextView(getContext());
		textView2.setGravity(Gravity.CENTER);
		textView2.setTextColor(Color.WHITE);
		textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		textView2.setLineSpacing(CameraPercentUtil.WidthPxToPercent(18), 1.0f);
		textView2.setText(R.string.arwish_upload_video_tips);
		fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		fl.bottomMargin = CameraPercentUtil.HeightPxToPercent(50);
		addView(textView2, fl);
	}

	public void setBackground(Bitmap bitmap)
	{
		if (bitmap != null && !bitmap.isRecycled())
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			{
				this.setBackground(new BitmapDrawable(getContext().getResources(), bitmap));
			}
			else
			{
				this.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
			}
		}
	}

	public void setProgress(int progress)
	{
		if(progress > 100) progress = 100;
		mProgress = progress;
		mBar.setProgress(mProgress);
	}

	public int getProgress()
	{
		return mProgress;
	}
}

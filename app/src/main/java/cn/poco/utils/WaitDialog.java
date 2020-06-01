package cn.poco.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import my.beautyCamera.R;

public class WaitDialog extends Dialog
{
	public WaitDialog(Context context)
	{
		super(context, R.style.waitDialog);
	}

	public WaitDialog(Context context, int theme)
	{
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		FrameLayout fr = new FrameLayout(getContext());
		fr.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
		setContentView(fr);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		ProgressBar wait = new ProgressBar(getContext());
		wait.setLayoutParams(fl);
		fr.addView(wait);

		this.setCancelable(false);

		super.onCreate(savedInstanceState);
	}
}

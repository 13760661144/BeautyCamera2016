package cn.poco.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.circle.utils.Utils;

import cn.poco.arWish.BeautyRefreshStyle.WaitAnimView;
import my.beautyCamera.R;


public class LoadingBar extends RelativeLayout
{
	
	private ProgressBar mProgressBar;
	private TextView mProgressText;
	private TextView mProgressInfo;
	private WaitAnimView mWaitAnimView;

	public LoadingBar(Context context) {
		super(context);
		
		RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(Utils.getRealPixel2(100), Utils.getRealPixel2(100));
		rparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		RelativeLayout progressBar = new RelativeLayout(context);
		GradientDrawable drawable = new GradientDrawable();
		drawable.setColor(0x80000000);
		drawable.setCornerRadius(Utils.getRealPixel2(100));
		progressBar.setBackgroundDrawable(drawable);
		addView(progressBar, rparams);
		progressBar.setId(Utils.generateViewId());

		rparams = new RelativeLayout.LayoutParams(Utils.getRealPixel2(90), Utils.getRealPixel2(90));
		rparams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mProgressBar = new ProgressBar(context);
		progressBar.addView(mProgressBar, rparams);

		rparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rparams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mWaitAnimView = new WaitAnimView(context);
		progressBar.addView(mWaitAnimView, rparams);
		mWaitAnimView.setVisibility(GONE);

		rparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rparams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mProgressText = new TextView(context);
		mProgressText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
		mProgressText.setTextColor(0xffffffff);
		progressBar.addView(mProgressText, rparams);

		rparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rparams.topMargin = Utils.getRealPixel2(5);
		rparams.addRule(RelativeLayout.BELOW, progressBar.getId());
		mProgressInfo = new TextView(context);
		mProgressInfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		mProgressInfo.setTextColor(0xff666666);
		mProgressInfo.setText("努力加载中");
		addView(mProgressInfo, rparams);
	}
	
	public void setTextColor(int color)
	{
		mProgressInfo.setTextColor(color);
	}
	
	public void setProgress(int progress)
	{
		if(progress != -1)
		{
			mProgressText.setText(progress + "%");
		}
		else
		{
			mProgressText.setText("");
		}
	}
	
	public void setText(String text)
	{
		mProgressInfo.setText(text);
	}

	public void setLoadMode(boolean isBeauty)
	{
		if(isBeauty)
		{
			mWaitAnimView.setVisibility(VISIBLE);
			mProgressBar.setVisibility(GONE);
		}else
		{
			mWaitAnimView.setVisibility(GONE);
			mProgressBar.setVisibility(VISIBLE);
		}
	}

	@Override
	protected void onVisibilityChanged(@NonNull View changedView, int visibility)
	{
		if(mWaitAnimView.getVisibility() == VISIBLE)
		{
			if (visibility != VISIBLE)
			{
				mWaitAnimView.stop();
			} else
			{
				mWaitAnimView.start();
			}
		}
		super.onVisibilityChanged(changedView, visibility);
	}

	private class ProgressBar extends View
	{
		public ProgressBar(Context context)
		{
			super(context);
			setBackgroundResource(R.drawable.loading_circle);
			//mBmpCircle = BitmapFactory.decodeResource(getResources(), R.drawable.loading_circle);
		}

		private float mAngle = 0;
		private volatile boolean mRunning = false;
		private Thread mThread = null;
		private volatile boolean mAttached = false;
		/*private Bitmap mBmpCircle;
		private PaintFlagsDrawFilter mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

		@Override
		public void draw(Canvas canvas)
		{
			int strokeWidth = Utils.getRealPixel2(5);
			Paint paintC = new Paint();
			paintC.setStyle(Paint.Style.STROKE);
			paintC.setStrokeWidth(strokeWidth);
			paintC.setAntiAlias(true);

			int x = getWidth() / 2;
			int y = getHeight() / 2;
			Matrix matrix = new Matrix();
			
			float scalex = (float)getWidth()/(float)mBmpCircle.getWidth();
			float scaley = (float)getHeight()/(float)mBmpCircle.getHeight();
			matrix.postRotate(mAngle, x, y);
			matrix.postScale(scalex, scaley);
			canvas.setDrawFilter(mDrawFilter);
			canvas.drawBitmap(mBmpCircle, matrix, paintC);
		}
*/
		public void start()
		{
			if(mRunning == false)
			{
				mRunning = true;
				// System.out.println("start");
				mThread = new Thread(mAnimRunnable);
				mThread.start();
			}
		}

		public void stop()
		{
			mRunning = false;
			if (mThread != null)
			{
				// System.out.println("stop");
				mThread.interrupt();
				mThread = null;
			}
		}
		
		private Runnable mRefreshRunnable = new Runnable()
		{

			@Override
			public void run() {
				setRotation(mAngle);
			}
			
		};

		private Runnable mAnimRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				while (mRunning)
				{
					try
					{
						Thread.sleep(50);
					} catch (InterruptedException e)
					{
						break;
					}
					mAngle += 18;
					post(mRefreshRunnable);
					//postInvalidate();
				}
				mRunning = false;
			}
		};

		@Override
		protected void onVisibilityChanged(View changedView, int visibility)
		{
			if(mAttached == true)
			{
				if (visibility != VISIBLE)
				{
					stop();
				} else
				{
					start();
				}
			}
			super.onVisibilityChanged(changedView, visibility);
		}

		@Override
		protected void onAttachedToWindow()
		{
			mAttached = true;
			if (getVisibility() == VISIBLE)
			{
				start();
			}
			super.onAttachedToWindow();
		}

		@Override
		protected void onDetachedFromWindow()
		{
			mAttached = false;
			stop();
			super.onDetachedFromWindow();
		}
	}

}

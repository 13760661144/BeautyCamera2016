package cn.poco.bootimg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Raining on 2016/12/23.
 * 跳过按钮
 */

public class SkipBtn extends LinearLayout
{
	protected CircleProgress mProgress;

	public SkipBtn(Context context)
	{
		super(context);

		Init();
	}

	public SkipBtn(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Init();
	}

	public SkipBtn(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		Init();
	}

	protected void Init()
	{
		setOrientation(LinearLayout.HORIZONTAL);
		setPadding(0, ShareData.PxToDpi_xhdpi(20), 0, ShareData.PxToDpi_xhdpi(20));

		LinearLayout.LayoutParams ll;

		mProgress = new CircleProgress(getContext());
		ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.CENTER;
		this.addView(mProgress, ll);

		TextView tex = new TextView(getContext());
		tex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		tex.setText(R.string.bootimgpage_skip_btn);
		tex.setTextColor(0xffa3a3a3);
		tex.setGravity(Gravity.CENTER);
		ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.CENTER;
		ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
		ll.rightMargin = ll.leftMargin;
		this.addView(tex, ll);

		ImageView arrow = new ImageView(getContext());
		arrow.setScaleType(ImageView.ScaleType.CENTER);
		arrow.setImageResource(R.drawable.bootimgpage_skip_btn_arrow);
		ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.CENTER;
		this.addView(arrow, ll);
	}

	public void SetSkipTime(int duration)
	{
		mProgress.Start(duration);
	}

	public static class CircleProgress extends View
	{
		protected long mStartTime;
		protected long mEndTime;
		protected int mDuration;

		protected int mW;
		protected int mH;

		public CircleProgress(Context context)
		{
			super(context);

			Init();
		}

		public CircleProgress(Context context, AttributeSet attrs)
		{
			super(context, attrs);

			Init();
		}

		public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr)
		{
			super(context, attrs, defStyleAttr);

			Init();
		}

		protected void Init()
		{
			CommonUtils.CancelViewGPU(this);
			mW = ShareData.PxToDpi_xhdpi(18);
			mH = mW;

			temp_paint.setAntiAlias(true);
			temp_paint.setFilterBitmap(true);
			temp_paint.setStyle(Paint.Style.FILL);
			temp_paint.setColor(0xffa3a3a3);

			temp_rect.set(0, 0, mW, mH);
		}

		public void Start(int duration)
		{
			mDuration = duration;
			mStartTime = System.currentTimeMillis();
			mEndTime = mStartTime + mDuration;
		}

		protected Paint temp_paint = new Paint();
		protected RectF temp_rect = new RectF();

		@Override
		protected void onDraw(Canvas canvas)
		{
			long currentTime = System.currentTimeMillis();
			if(currentTime <= mEndTime && mDuration > 0)
			{
				float d = currentTime - mStartTime;
				if(d >= 0)
				{
					float s = d / mDuration;
					int a = (int)(s * 360 + 0.5f);
					//开始角度，跨越的角度
					canvas.drawArc(temp_rect, a - 90, 360 - a, true, temp_paint);
				}
				this.invalidate();
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			setMeasuredDimension(mW, mH);
		}
	}
}

package cn.poco.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Raining on 2016/12/26.
 * 新loading
 */

public class WaitAnimDialog extends FullScreenDlg
{
	public WaitAnimView mView;

	public WaitAnimDialog(Activity activity)
	{
		super(activity, R.style.waitDialog);
	}

	@Override
	protected void Init(Activity activity)
	{
		super.Init(activity);

		this.setCancelable(false);

		mView = new WaitAnimView(activity);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		AddView(mView, fl);
	}


	public void SetGravity(int gravity, int offsetY)
	{
		RemoveView(mView);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = gravity;
		switch(gravity & Gravity.VERTICAL_GRAVITY_MASK)
		{
			case Gravity.BOTTOM:
				fl.bottomMargin = offsetY;
				break;

			case Gravity.TOP:
				fl.topMargin = offsetY;
				break;

			default:
				break;
		}
		AddView(mView, fl);
	}

	public void SetText(String tex)
	{
	}

    public static class WaitAnimView extends View
	{
		protected int LINE_X;
		protected int LINE_Y;
		protected int LINE_MAX;
		protected int LINE_MIN;
		protected int LINE_W;
		protected int LINE_GAP;
		protected int LINE_NUM;
		protected int W;
		protected int H;
		protected int ANIM_DELAY;
		protected int COLOR1;
		protected int COLOR2;
		protected int BK_COLOR;
		protected int RECT_R; //圆角矩形半径

		protected long mStartTime;
		protected long mCurrentTime;
		protected boolean mIsStop;
		protected boolean mIsGradient = true;

		public WaitAnimView(Context context)
		{
			super(context);

			Init();
		}

		public WaitAnimView(Context context, AttributeSet attrs)
		{
			super(context, attrs);

			Init();
		}

		public WaitAnimView(Context context, AttributeSet attrs, int defStyleAttr)
		{
			super(context, attrs, defStyleAttr);

			Init();
		}

		protected void Init()
		{
			CommonUtils.CancelViewGPU(this);

			W = ShareData.PxToDpi_xhdpi(86);
			H = ShareData.PxToDpi_xhdpi(48);
			LINE_W = ShareData.PxToDpi_xhdpi(6);
			LINE_GAP = ShareData.PxToDpi_xhdpi(4) + LINE_W;
			LINE_MAX = ShareData.PxToDpi_xhdpi(30) - LINE_W;
			LINE_MIN = ShareData.PxToDpi_xhdpi(12) - LINE_W;
			LINE_NUM = 6;
			LINE_X = (W - LINE_GAP * (LINE_NUM - 1)) / 2;
			LINE_Y = (H - LINE_MAX) / 2;
			ANIM_DELAY = 100;
			COLOR1 = ImageUtils.GetSkinColor1(0xffef629f);
			COLOR2 = ImageUtils.GetSkinColor2(0xffeeb2a3);
			RECT_R = H / 2;

			temp_paint.setAntiAlias(true);
			temp_paint.setColor(0xff000000);
			temp_paint.setStrokeCap(Paint.Cap.ROUND);
			temp_paint.setStrokeWidth(LINE_W);

			setBkColor(0xf0ffffff);

			temp_rect.set(0, 0, W, H);

			mStartTime = System.currentTimeMillis();
		}

		protected Paint temp_paint = new Paint();
		protected Paint temp_paint2 = new Paint();
		protected Paint temp_paint3 = new Paint();
		protected RectF temp_rect = new RectF();
		protected LinearGradient temp_gradient;

		@Override
		protected void onDraw(Canvas canvas)
		{
			if(!mIsStop)
			{
				mCurrentTime = System.currentTimeMillis();
			}

			if(mIsGradient)
			{
				//画线
				int x = LINE_X;
				int y = LINE_Y;
				int d = 0;
				for(int i = 0; i < LINE_NUM; i++)
				{
					DrawLine(canvas, x, y, d);
					x += LINE_GAP;
					d += ANIM_DELAY;
				}

				//画渐变
				if(temp_gradient == null)
				{
					temp_gradient = new LinearGradient(LINE_X - LINE_GAP / 2, LINE_Y + LINE_MAX / 2f, LINE_X + (LINE_NUM - 1) * LINE_GAP + LINE_GAP / 2, LINE_Y + LINE_MAX / 2f, new int[]{COLOR1, COLOR2}, null, Shader.TileMode.CLAMP);
					temp_paint2.setShader(temp_gradient);
					temp_paint2.setAntiAlias(true);
					temp_paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
				}
				canvas.drawRect(0, 0, W, H, temp_paint2);

				//画椭圆底图
				canvas.drawRoundRect(temp_rect, RECT_R, RECT_R, temp_paint3);
			}
			else
			{
				//画椭圆底图
				temp_paint3.reset();
				temp_paint3.setAntiAlias(true);
				temp_paint3.setColor(BK_COLOR);
				temp_paint3.setStyle(Paint.Style.FILL);
				canvas.drawRoundRect(temp_rect, RECT_R, RECT_R, temp_paint3);

				//画线
				int x = LINE_X;
				int y = LINE_Y;
				int d = 0;
				temp_paint.setColor(COLOR1);
				for(int i = 0; i < LINE_NUM; i++)
				{
					DrawLine(canvas, x, y, d);
					x += LINE_GAP;
					d += ANIM_DELAY;
				}
			}

			this.invalidate();
		}

		public void stop()
		{
			mIsStop = true;
		}

		/**
		 * 默认自动start,不需要手动调用
		 */
		public void start()
		{
			mIsStop = false;
		}

		/**
		 * 设置波浪颜色(纯色)
		 *
		 * @param color
		 */
		public void setColor(int color)
		{
			COLOR1 = color;
			temp_gradient = null;
			mIsGradient = false;
			this.invalidate();
		}

		/**
		 * 设置波浪颜色(渐变)
		 *
		 * @param color1
		 * @param color2
		 */
		public void setColor(int color1, int color2)
		{
			COLOR1 = color1;
			COLOR2 = color2;
			temp_gradient = null;
			mIsGradient = true;
			this.invalidate();
		}

		/**
		 * 设置背景颜色
		 *
		 * @param color
		 */
		public void setBkColor(int color)
		{
			BK_COLOR = color;
			temp_paint3.setAntiAlias(true);
			temp_paint3.setColor(BK_COLOR);
			temp_paint3.setStyle(Paint.Style.FILL);
			temp_paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
			this.invalidate();
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			setMeasuredDimension(W, H);
		}

		public int getW()
		{
			return W;
		}

		public int getH()
		{
			return H;
		}

		protected void DrawLine(Canvas canvas, float x, float y, int offset)
		{
			float d = ((mCurrentTime + offset - mStartTime) % 1000) / 1000f;
			float h = (float)(LINE_MIN + (LINE_MAX - LINE_MIN) * ((Math.sin(d * 2 * Math.PI) + 1) / 2f));
			y += (LINE_MAX - h) / 2f;
			canvas.drawLine(x, y, x, y + h, temp_paint);
		}
	}
}

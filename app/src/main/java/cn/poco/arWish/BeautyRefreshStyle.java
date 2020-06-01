package cn.poco.arWish;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.PullToRefreshLayout.RefreshState;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.CommonUtils;

/**
 * Created by Anson on 2018/1/22.
 */

public class BeautyRefreshStyle extends PullToRefreshLayout.StyleTemplate
{
	private WaitAnimView mWaitAnimView;// 头布局的进度条
	private Handler mHandler;

	public BeautyRefreshStyle(Context context)
	{
		super(context);
		mHandler = new Handler();
	}

	@Override
	public void addChildView(Context context, FrameLayout container)
	{
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout headerView = new LinearLayout(context);
		headerView.setPadding(0, CameraPercentUtil.WidthPxToPercent(20), 0, CameraPercentUtil.WidthPxToPercent(20));
		headerView.setOrientation(LinearLayout.HORIZONTAL);
		headerView.setVisibility(View.VISIBLE);
		headerView.setGravity(Gravity.CENTER);
		params.gravity = Gravity.CENTER;
		container.addView(headerView, params);

		LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lparams.gravity = Gravity.CENTER;
		mWaitAnimView = new WaitAnimView(context);
		headerView.addView(mWaitAnimView, params);

	}

	@Override
	public void onHeadStateChange(int state)
	{
		if (state == RefreshState.PULL)
		{ // 当前进入下拉
			mWaitAnimView.setVisibility(View.VISIBLE);
		} else if (state == RefreshState.RELEASE)
		{ // 当前松开刷新
			mWaitAnimView.setVisibility(View.VISIBLE);

		} else if (state == RefreshState.REFRESHING)
		{ // 当前正在刷新中
			mHandler.postDelayed(new Runnable()
			{

				@Override
				public void run()
				{
					mWaitAnimView.setVisibility(View.VISIBLE);
					mWaitAnimView.start();
				}
			}, 120);
		} else
		{ // 手动中断刷新
			mWaitAnimView.stop();
			mWaitAnimView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onFootStateChange(int state)
	{

	}

	@Override
	public void onRefresh()
	{
		mWaitAnimView.stop();
		mWaitAnimView.setVisibility(View.GONE);
	}

	@Override
	public void onFinish()
	{

	}

	@Override
	public void onStart()
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

			W = CameraPercentUtil.WidthPxToPercent(86);
			H = CameraPercentUtil.WidthPxToPercent(48);
			LINE_W = CameraPercentUtil.WidthPxToPercent(6);
			LINE_GAP = CameraPercentUtil.WidthPxToPercent(4) + LINE_W;
			LINE_MAX = CameraPercentUtil.WidthPxToPercent(30) - LINE_W;
			LINE_MIN = CameraPercentUtil.WidthPxToPercent(12) - LINE_W;
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

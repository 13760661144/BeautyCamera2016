package cn.poco.home.home4.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import cn.poco.home.home4.utils.PercentUtil;

import static cn.poco.home.home4.utils.PercentUtil.HeightPxToPercent;

/**
 * Created by lgd on 2017/3/24.
 */

public class RopeAnimationView extends View
{
	private static final String TAG = "RopeAnimationView";
	private boolean isIntercept = false;

	private int mode = MODE_SWING;          //正在哪种动画阶段
	private final static int MODE_PAUSE = 0;
	private final static int MODE_SWING = 1;
	private final static int MODE_THROW = 2;
	private final static int MODE_REBOUND = 3;

	private Point mTopVertex;       //绳子上顶点
	private Point mBottomVertex;    //绳子下顶点
	private Point mControlVertex;  //控制点,4分之3
	private Circle mCircle;
	private Path mRopePath;       //绳子 贝塞尔曲线
	private Paint mPaintCirCle;
	private Paint mPaintRope;

	private float mSwingDegrees = 0; //MODE_SWING 画板旋转角度。
	private float mReboundDegree = 0;  //MODE_REBOUND 回弹的角度，画板旋转角度。
	private float mControlHeight = 0;  //控制点的高度偏移量
	private float mControlWidth = 0;  //控制点的宽度偏移量，
	private float mThrowFactor = 1.3f;  //上抛时偏移量比，宽 = 高度*系数;
	private float mThrowHeight1;      //上抛高度
	private float mThrowHeight2;	  //坠落
	private float mThrowHeight3;      //向上弹
	private float mThrowHeight4;      //向下回弹

	public float mReboundMaxDegree;   //最大角度
	public float mReboundMaxLength;   //最大偏移量
	public static final float MAX_DEGREE = 20;
	public static final float MAX_LENGTH = PercentUtil.HeightPxToPercent(20);

	private ValueAnimator mSwingAnimator;
	private ValueAnimator mThrowAnimator;
	private ValueAnimator mReBoundAnimator;

	public RopeAnimationView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		mPaintCirCle = new Paint();
		mPaintCirCle.setAntiAlias(true);
		mPaintCirCle.setColor(Color.WHITE);
		mPaintCirCle.setStyle(Paint.Style.STROKE);
//		mPaintCirCle.setStrokeWidth(WidthPxToPercent(4));
		mPaintCirCle.setStrokeWidth(HeightPxToPercent(4));

		mPaintRope = new Paint();
		mPaintRope.setAntiAlias(true);
		mPaintRope.setColor(Color.WHITE);
		mPaintRope.setAlpha((int)(0.4f*255));
		mPaintRope.setStyle(Paint.Style.STROKE);
		mPaintRope.setStrokeWidth(HeightPxToPercent(2));
		mRopePath = new Path();

		initData();


		mSwingAnimator = new ValueAnimator();
		mSwingAnimator.setDuration(3000);
//		float[] degrees = new float[]{0,2,0,-2,0,4,0,-4,0,6,0,-6,0,4,0,-4,0,2,0,-2,0};
		float[] degrees = new float[]{0, 3, 0, -3, 0, 5, 0, -5, 0, 6, 0, -6, 0, 3, 0, -3, 0};
		mSwingAnimator.setFloatValues(degrees);
		mSwingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				mSwingDegrees = (float)animation.getAnimatedValue();
				invalidate();
			}
		});
		mSwingAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				mode = MODE_SWING;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				if(isIntercept)
				{
					initData();
					RopeAnimationView.this.invalidate();
				}
				else
				{
					mode = MODE_THROW;
					mThrowAnimator.start();
				}
			}
		});
		mThrowAnimator = new ValueAnimator();
		mThrowAnimator.setFloatValues(0, 10);  //归一化处理，4 段，向上抛，向下坠，回弹，下坠
		mThrowAnimator.setDuration(900);
		mThrowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{

			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				mControlWidth = 0;
				float f = (float)animation.getAnimatedValue();
				if(f <= 3)
				{
					mControlHeight = mThrowHeight1 * (f) / 3;
					mControlWidth = mControlHeight * mThrowFactor;
				}
				else if(f <= 6)
				{
					mControlHeight = mThrowHeight2 * (f - 3) / 3 + mThrowHeight1;
					mControlWidth = mThrowFactor * (mThrowHeight1 - mThrowHeight1 * (f - 3) / 3);
				}
				else if(f <= 9)
				{
					mControlHeight = mThrowHeight3 * (f - 6) / 3 + mThrowHeight2 + mThrowHeight1;
				}
				else if(f <= 10)
				{
					mControlHeight = mThrowHeight4 * (f - 9) + mThrowHeight3 + mThrowHeight2 + mThrowHeight1;
				}
				invalidate();
			}
		});
		mThrowAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				if(!isIntercept)
				{
					mSwingAnimator.start();
				}
			}
		});

		mReBoundAnimator = new ValueAnimator();
		mReBoundAnimator.setFloatValues(0, 11);
		mReBoundAnimator.setDuration(900);
		mReBoundAnimator.setInterpolator(new DecelerateInterpolator());
		mReBoundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float f = (float)animation.getAnimatedValue();
				if(f <= 2)
				{
					if(f <= 1)
					{
						//左拉
						mControlHeight = -mReboundMaxDegree * f;
						mControlWidth = -mReboundMaxDegree * f;
					}
					else
					{
						//左拉回弹
						mControlHeight = -Math.abs(mReboundMaxDegree * (f - 2));
						mControlWidth = -Math.abs(mReboundMaxDegree * (f - 2));
					}
					mReboundDegree = Math.abs(mReboundMaxDegree * f / 2);
				}
				else if(f <= 6)
				{
					if(f <= 5)
					{
						//右拉到0点
						mControlHeight = Math.abs(mReboundMaxDegree * (f - 2) /6);
						mControlWidth = Math.abs(mReboundMaxDegree * (f - 2) /6);
						mReboundDegree = mReboundMaxDegree - Math.abs(mReboundMaxDegree * (f - 2) / 3);
					}
					else
					{
						//0点右拉回弹
						mControlHeight = Math.abs(mReboundMaxDegree * (6 - f) / 2);
						mControlWidth = Math.abs(mReboundMaxDegree * (6 - f) / 2);
						mReboundDegree = -Math.abs(mReboundMaxDegree * (f - 5) / 4);
					}
				}
				else if(f <= 8)
				{
					if(f <= 7)
					{
						//左拉到0点
						mControlHeight = -Math.abs(mReboundMaxDegree * (f - 6) / 4);
						mControlWidth = -Math.abs(mReboundMaxDegree * (f - 6) / 4);
						mReboundDegree = -Math.abs(mReboundMaxDegree * (7 - f) / 4);
					}
					else
					{
						//0点左拉
						mControlHeight = -Math.abs(mReboundMaxDegree * (8 - f) / 4);
						mControlWidth = -Math.abs(mReboundMaxDegree * (8 - f) / 4);
						mReboundDegree = Math.abs(mReboundMaxDegree * (f - 7) / 3);
					}
				}
				//
				else if(f <= 10)
				{
					if(f <= 9)
					{
						//右拉到0点
						mControlHeight = -Math.abs(mReboundMaxDegree * (f - 9f) / 6);
						mControlWidth = -Math.abs(mReboundMaxDegree * (f - 9f) / 6);
						mReboundDegree = Math.abs(mReboundMaxDegree * (f - 9f)  / 3);
					}
					else
					{
						//0点右拉回弹
						mControlHeight = Math.abs(mReboundMaxDegree * (9f - f) /6 );
						mControlWidth = Math.abs(mReboundMaxDegree * (9f - f) / 6);
						mReboundDegree = -Math.abs(mReboundMaxDegree * (f - 9f)  / 6);
					}
				}
				else if(f <= 11)
				{
//					//右拉到0点
					mReboundDegree = -Math.abs(mReboundMaxDegree * (f - 11) / 6);
				}
				invalidate();
			}
		});
		mReBoundAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				RopeAnimationView.this.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						if(!isIntercept){
							mode = MODE_SWING;
							mSwingAnimator.start();
						}
					}
				}, 500);
			}

			@Override
			public void onAnimationStart(Animator animation)
			{
				mode = MODE_REBOUND;
			}

		});
	}

	public void initData()
	{
		mReboundMaxDegree = MAX_DEGREE;
		mReboundMaxLength = MAX_LENGTH;

		mReboundDegree = 0;
		mSwingDegrees = 0;
		mControlWidth = 0;
		mControlHeight = 0;

		if(mTopVertex == null)
		{
			mTopVertex = new Point();
		}
		mTopVertex.x = getWidth() / 2;
		mTopVertex.y = 0;

		if(mBottomVertex == null)
		{
			mBottomVertex = new Point();
		}
		mBottomVertex.x = getWidth() / 2;
//		mBottomVertex.y = ShareData.PxToDpi_xhdpi(100);
		mBottomVertex.y = HeightPxToPercent(67);

		if(mControlVertex == null)
		{
			mControlVertex = new Point();
		}
		mControlVertex.x = getWidth() / 2;
		mControlVertex.y = mBottomVertex.y * 2 / 4;

		if(mCircle == null)
		{
			mCircle = new Circle();
		}
		mCircle.radius = PercentUtil.HeightPxToPercent(21);  //23-4/2;
		mCircle.x = getWidth() / 2;
		mCircle.y = mBottomVertex.y + HeightPxToPercent(23);

		//向上抛
		mThrowHeight1 = (mTopVertex.y - mBottomVertex.y) * 2.0f / 5;
		//向下坠
		mThrowHeight2 = Math.abs(mThrowHeight1) * 2;
		//回弹
		mThrowHeight3 = -Math.abs(mThrowHeight1 * 1.4f);
		//回弹
		mThrowHeight4 = Math.abs(mThrowHeight1 * 0.4f);
	}


	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		switch(mode)
		{
			case MODE_SWING:
				drawSwing(canvas);
				break;
			case MODE_THROW:
				drawThrow(canvas);
				break;
			case MODE_REBOUND:
				drawReBound(canvas);
				break;
			case MODE_PAUSE:
			default:
				break;
		}
	}

	private void drawReBound(Canvas canvas)
	{
		canvas.translate(mTopVertex.x, 0);
		canvas.rotate(mReboundDegree);
		mRopePath.reset();
		mRopePath.moveTo(0, 0);
		mRopePath.quadTo(mControlWidth, mControlVertex.y + mControlHeight, 0, mBottomVertex.y);
		canvas.drawPath(mRopePath, mPaintRope);
		canvas.drawCircle(0, mCircle.y, mCircle.radius, mPaintCirCle);
		canvas.restore();
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		initData();
	}

	/**
	 * 旋转和平移画板，不用计算圆摆动
	 *
	 * @param canvas
	 */
	public void drawSwing(Canvas canvas)
	{
		canvas.translate(mTopVertex.x, 0);
		canvas.rotate(mSwingDegrees);
		mRopePath.reset();
		mRopePath.moveTo(0, 0);
		float x = 0.2f * (float)(Math.sin(Math.PI * mSwingDegrees / 180) * mControlVertex.y);
		float y = 0.2f * (float)(Math.cos(Math.PI * mSwingDegrees / 180) * mControlVertex.y);
		mRopePath.quadTo(x, y, 0, mBottomVertex.y);
		canvas.drawPath(mRopePath, mPaintRope);
		canvas.drawCircle(0, mCircle.y, mCircle.radius, mPaintCirCle);
		canvas.restore();
	}

	private void drawThrow(Canvas canvas)
	{
		canvas.translate(mTopVertex.x, 0);
		mRopePath.reset();
		mRopePath.moveTo(0, mTopVertex.y);
		mRopePath.quadTo(mControlWidth, mControlVertex.y + mControlHeight, mControlWidth / 2, mBottomVertex.y + mControlHeight * 2 / 3);
		canvas.drawPath(mRopePath, mPaintRope);
		canvas.drawCircle(mControlWidth / 2, mCircle.y + mControlHeight * 2 / 3, mCircle.radius, mPaintCirCle);
		canvas.restore();

//		mRopePath.reset();
//		mRopePath.moveTo(mTopVertex.x, mTopVertex.y);
//		mRopePath.quadTo(mControlVertex.x + mControlWidth, mControlVertex.y + mControlHeight, mBottomVertex.x + mControlWidth / 2, mBottomVertex.y + mControlHeight * 2 / 3);
//		canvas.drawPath(mRopePath, mPaintRope);
//		canvas.drawCircle(mCircle.x + mControlWidth / 2, mCircle.y + mControlHeight * 2 / 3, mCircle.radius, mPaintCirCle);
	}

	static class Point
	{
		float x;
		float y;
	}

	static class Circle
	{
		float x;
		float y;
		float radius;
	}

	public void setIntercept(boolean intercept)
	{
		isIntercept = intercept;
		if(isIntercept)
		{
			if(!mReBoundAnimator.isRunning())
			{
				mSwingAnimator.cancel();
				if(!mThrowAnimator.isRunning()){
					mode = MODE_REBOUND;
					initData();
					RopeAnimationView.this.invalidate();
				}
			}
		}
		else
		{
			if(!mReBoundAnimator.isRunning())
			{
				if(!mSwingAnimator.isRunning() && !mThrowAnimator.isRunning())
				{
					mode = MODE_SWING;
					mSwingAnimator.start();
				}
			}
		}
	}

	public void startReboundAnimation(float reBoundPercent)
	{
		//此时isIntercept为true;
		if(!mReBoundAnimator.isRunning() || (mReBoundAnimator.getAnimatedFraction()>=0.5f && mReBoundAnimator.isRunning()))
		{
			mSwingAnimator.cancel();
			mReBoundAnimator.cancel();
			mReBoundAnimator.cancel();
			mode = MODE_REBOUND;
			mReboundMaxDegree = MAX_DEGREE * reBoundPercent;
			mReboundMaxLength = MAX_LENGTH * reBoundPercent;
			mReBoundAnimator.start();
		}
	}

	public void onClose()
	{
		mReBoundAnimator.removeAllListeners();
		mSwingAnimator.removeAllListeners();
		mThrowAnimator.removeAllListeners();
		mReBoundAnimator.cancel();
		mSwingAnimator.cancel();
		mThrowAnimator.cancel();
	}


}

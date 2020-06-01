package cn.poco.home.home4.introAnimation;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;


/**
 * Created by lgd on 2016/11/12.
 * 两球粘性分离
 */

public class SegregateAnimator extends ValueAnimator
{
	public final static String TAG = "SegregateAnimator";
	public final static int DURATION = Config.DURATION_SEGREGATE;
	private final int mStopDistance;
	public int radius;
	private View mView;
	//分离的距离，距中心
	private int mDistance;
	private int mStartDistance;
	private int centerX;
	private int centerY;
	private CirCle topCirCle;
	private CirCle bottomCirCle;
	private Path mPath;
	public SegregateAnimator(View view)
	{
		mView = view;
		mPath = new Path();
		centerX = Config.CENTER_X;
		centerY = Config.CENTER_Y;
		mDistance = Config.DISTANCE_SEGREGATE;
		radius = Config.RADIUS_BIG_CIRCLE;
		mStartDistance = Config.DISTANCE_START_SEGREGATE;
		mStopDistance = Config.DISTANCE_STOP_SEGREGATE;
		topCirCle = new CirCle(centerX, centerY, radius);
		bottomCirCle = new CirCle(centerX, centerY, radius);

		initAnim();
	}


	private void initAnim()
	{
		this.setDuration(DURATION);
		this.setFloatValues(0, mDistance);
		this.addUpdateListener(new AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float curY = (Float)animation.getAnimatedValue();
				topCirCle.y = (int)(centerY - curY);
				bottomCirCle.y = (int)(centerY + curY);
				mView.postInvalidate();
			}
		});
	}

	public void draw(Canvas canvas, Paint paint)
	{
		drawCircle(topCirCle, canvas, paint);
		drawCircle(bottomCirCle, canvas, paint);
		drawBersal(canvas, paint);
	}

	private void drawCircle(CirCle circle, Canvas canvas, Paint paint)
	{
		canvas.drawCircle(circle.x, circle.y, circle.radius, paint);
	}

	//第一次计算交点
	boolean isFrist = true;
	private float startDx;
	private float startDy;
	private float startAnchorDx;
	private float pointDx;
	private float pointDy;
	private float anchorDx;

	private void drawBersal(Canvas canvas, Paint paint)
	{
		//y轴偏移量
		int offsetY = bottomCirCle.y - centerY;
		//开始绘制贝塞尔曲线
		if( offsetY >= mStartDistance)
		{
			if(isFrist)
			{
				//计算切点，dy,dx距到圆心的位置 ，用于后面计算4个切点
				pointDy = offsetY;
				startDy = pointDy;
				pointDx = (int)Math.sqrt(radius * radius - pointDy * pointDy);
				startDx = pointDx;
				//控制点
				anchorDx = centerX-pointDx;
				startAnchorDx = anchorDx;
				isFrist = false;
			}
			//切点，向内移动一半，d 越来越小
			pointDx = startDx - startDx/2.0f*(offsetY-startDy)/(mStopDistance-startDy);
			pointDy = (int)Math.sqrt(radius * radius - pointDx * pointDx);

			//控制点，加速向中心点移动，
			float factor = startDx*1.0f/pointDx;
			//控制点，加速向中心点移动，越来越大
//			float factor = accelerateInterpolator.getInterpolation((float)(startDx*1.0/pointDx));
//			float factor  = (float)Math.pow((startDx*1.0/pointDx), 1.001f);
			anchorDx = (radius-pointDx) * factor;

			//计算绘制点和控制点
			float anchorX1 = centerX - radius + anchorDx;
			float anchorX2 = centerX + radius - anchorDx;
			float anchorY = centerY;

			float startX1 = centerX - pointDx;
			float startY1 = topCirCle.y + pointDy;
			float endX1 = startX1;
			float endY1 = bottomCirCle.y - pointDy;

			float startX2 = centerX + pointDx;
			float startY2 = bottomCirCle.y - pointDy;
			float endX2 = startX2;
			float endY2 = topCirCle.y + pointDy;

			//超出界限
			if((startX1 + anchorX1) / 2 >= centerX || offsetY >= mStopDistance)
			{
				return;
			}
			mPath.reset();
			mPath.moveTo(startX1, startY1);
			mPath.quadTo(anchorX1, anchorY, endX1, endY1);
			mPath.lineTo(startX2, startY2);
			mPath.quadTo(anchorX2, anchorY, endX2, endY2);
			canvas.drawPath(mPath, paint);
		}
	}
}
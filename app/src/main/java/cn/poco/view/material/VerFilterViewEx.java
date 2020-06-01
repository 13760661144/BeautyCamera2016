package cn.poco.view.material;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.utils.PhotoMark;
import cn.poco.view.beauty.BeautyCommonViewEx;

/**
 * 滤镜
 * Created by admin on 2017/1/18.
 */

public class VerFilterViewEx extends BeautyCommonViewEx
{
	protected boolean mIsTouch = false;
	protected boolean mCompare = false;
	protected int mFilterAlpha = 100;
	protected Shape mWaterMark;
	protected RectF mWaterRect;
	protected RectF mTemWaterRect;
	protected boolean mDrawWaterMark = false;
	protected boolean mIsNonItem = true;

	private float mWaterMarkAlpha = 1f;

	private boolean mDoingAlphaAnim = false;

	private ValueAnimator mWaterMarkAlphaAnim = null;

	private ControlCallback mCB;
	private boolean mHadData;

	public VerFilterViewEx(Context context)
	{
		super(context);
		mHadData = SettingInfoMgr.GetSettingInfo(context).GetAddDateState();
	}

	public void setVerFilterCB(ControlCallback mCB)
	{
		this.mCB = mCB;
		SetOnControlListener(mCB);
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();

		mCallBack = null;
		if(mWaterMark != null && mWaterMark.m_bmp != null)
		{
			mWaterMark.m_bmp = null;
		}
		cancelWaterMarkAlphaAnim();
		mWaterMark = null;
		mWaterRect = null;
		mTemWaterRect = null;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// 画遮罩之类
		if(frame != null && frame.m_bmp != null && !frame.m_bmp.isRecycled() && !mCompare){
			canvas.save();
			canvas.translate(mCanvasX, mCanvasY);
			canvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setAlpha((int) ((float) mFilterAlpha / 100f * 255));
			canvas.drawBitmap(frame.m_bmp,frame.m_matrix,mPaint);
			canvas.restore();
		}

		DrawWaterMark(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (mDoingAlphaAnim) return true;

		return super.onTouchEvent(event);
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		mIsTouch = true;
		if(mDrawWaterMark && mTemWaterRect != null)
		{
			mTemWaterRect.setEmpty();
			mWaterMark.m_matrix.mapRect(mTemWaterRect, mWaterRect);
			getShowPos(mTemWaterRect, mTemWaterRect);
			mTemWaterRect.left -= 1;
			mTemWaterRect.top -= 1;
			mTemWaterRect.right += 1;
			mTemWaterRect.bottom += 1;
			if(mTemWaterRect.contains(mDownX, mDownY) && mCB != null)
			{
				mCB.OnClickWaterMask();
				return;
			}
		}
//		super.OddDown(event);
		if(mCB != null)
			mCB.OnFingerDown(event.getPointerCount());
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
//		super.OddMove(event);
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
//		super.OddUp(event);
		mIsTouch = false;
		if(mCB != null)
			mCB.OnFingerUp(event.getPointerCount());
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
//		super.EvenDown(event);
		mIsTouch = true;
		if(mCB != null)
			mCB.OnFingerDown(event.getPointerCount());
	}

	@Override
	protected void EvenMove(MotionEvent event)
	{
//		super.EvenMove(event);
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		if(mCancelTouch)
		{
			mIsTouch = false;
			if(mCB != null)
				mCB.OnFingerUp(event.getPointerCount());
		}
//		super.EvenUp(event);
	}

	protected void DrawWaterMark(Canvas canvas)
	{
		if(mDrawWaterMark && mWaterMark != null && mWaterMark.m_bmp != null && mTemWaterRect != null)
		{
			canvas.save();
			canvas.translate(mCanvasX, mCanvasY);
			mWaterMark.m_matrix.reset();
			mTemWaterRect.setEmpty();

			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setColor(0x80ffffff);

			RectF curImgLogicRect = getCurImgLogicRect();
			float s = Math.min(curImgLogicRect.width(), curImgLogicRect.height());

			if(!mIsNonItem)
			{
				float dx = PhotoMark.getLogoRight(s);
				float dy = PhotoMark.getLogoBottom(s, mHadData);
				float watermarkW = PhotoMark.getLogoW(s);
				float scale = watermarkW / mWaterRect.width();
				mWaterMark.m_matrix.postScale(scale,scale);
				mWaterMark.m_matrix.mapRect(mTemWaterRect, mWaterRect);
				float x = dx;
				float y = curImgLogicRect.height() - dy - mTemWaterRect.height();
				mWaterMark.m_matrix.postTranslate(x, y);
				mPaint.setColor(0xffffffff);
			}
			else
			{
				float scale = Math.min(CameraPercentUtil.WidthPxToPercent(70) / mWaterRect.width(), CameraPercentUtil.HeightPxToPercent(60)/ mWaterRect.height());
				mWaterMark.m_matrix.postScale(scale,scale);
				mWaterMark.m_matrix.mapRect(mTemWaterRect, mWaterRect);
				float x = CameraPercentUtil.WidthPxToPercent(24);
//				float y = curImgLogicRect.height() - mTemWaterRect.height() - CameraPercentUtil.HeightPxToPercent(24);
				float dy = PhotoMark.getLogoBottom(s, mHadData) + mWaterRect.height() + CameraPercentUtil.WidthPxToPercent(4);
				float y = curImgLogicRect.height() - dy;
				mWaterMark.m_matrix.postTranslate(x, y);
			}

			mPaint.setAlpha((int) (255 * mWaterMarkAlpha));

			canvas.drawBitmap(mWaterMark.m_bmp, mWaterMark.m_matrix, mPaint);
			canvas.restore();
		}
	}

	public void setDrawWaterMark(boolean drawWaterMask)
	{
		cancelWaterMarkAlphaAnim();
		syncScaling();
		mDrawWaterMark = drawWaterMask;
	}

	public boolean AddWaterMark(Bitmap waterMask, boolean isNonItem)
	{
		mIsNonItem = isNonItem;
		//无水印素材下，50% 透明度
		mWaterMarkAlpha = isNonItem ? 0.5f : 1f;
		return AddWaterMark(waterMask);
	}

	public boolean AddWaterMark(Bitmap waterMask)
	{
		if(waterMask != null && !waterMask.isRecycled())
		{
			if(mWaterMark == null)
			{
				mWaterMark = new Shape();
			}

			if(mWaterMark.m_bmp != null && !mWaterMark.m_bmp.isRecycled())
			{
				mWaterMark.m_bmp.recycle();
				mWaterMark.m_bmp = null;
			}
			mWaterMark.m_bmp = waterMask;

			if(mTemWaterRect == null)
			{
				mTemWaterRect = new RectF();
			}
			if(mWaterRect == null)
			{
				mWaterRect = new RectF();
			}
			mWaterRect.setEmpty();
			mWaterRect.set(0, 0, mWaterMark.m_bmp.getWidth(), mWaterMark.m_bmp.getHeight());
			invalidate();
			return true;
		}
		return false;
	}

	public void cancelWaterMarkAlphaAnim()
	{
		if (mWaterMarkAlphaAnim != null)
		{
			mWaterMarkAlphaAnim.removeAllListeners();
			mWaterMarkAlphaAnim.removeAllUpdateListeners();
			mWaterMarkAlphaAnim.cancel();
			mWaterMarkAlphaAnim = null;
			mWaterMarkAlpha = 1f;
			mDoingAlphaAnim = false;
			invalidate();
		}
	}

	public boolean isDoingWaterMarkAlphaAnim()
	{
		return mDoingAlphaAnim;
	}

	public void AddWaterMarkWithAnim(Bitmap waterMask, boolean isNonItem)
	{
		AddWaterMark(waterMask, isNonItem);
		mDoingAlphaAnim = true;
		//无水印素材下，50% 透明度
		if (isNonItem) {
			mWaterMarkAlphaAnim = ValueAnimator.ofFloat(0.5f, 0.2f, 0.5f, 0.2f, 0.5f);
		} else {
			mWaterMarkAlphaAnim = ValueAnimator.ofFloat(1f, 0.2f, 1f, 0.2f, 1f);
		}
		mWaterMarkAlphaAnim.setInterpolator(new LinearInterpolator());
		mWaterMarkAlphaAnim.setDuration(1200);
		mWaterMarkAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				mWaterMarkAlpha = (float) animation.getAnimatedValue();
				invalidate();
			}
		});
		mWaterMarkAlphaAnim.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				mDoingAlphaAnim = false;
			}
		});
		mWaterMarkAlphaAnim.start();
	}

	public void setFrame(Bitmap frameBmp)
	{
		frame.m_bmp = frameBmp;
		frame.m_matrix = img.m_matrix;
		invalidate();
	}

	public void setFrame(Bitmap bmp, boolean invalidate)
	{
		if (invalidate) {
			setFrame(bmp);
		} else {
			frame.m_bmp = bmp;
			frame.m_matrix = img.m_matrix;
		}
	}

	public void setCompare(Bitmap compareBmp, boolean compare)
	{
		img.m_bmp = compareBmp;
		mCompare = compare;
		mLockUI = !mCompare;
		invalidate();
	}

	public void setFilterAlpha(int alpha)
	{
		mFilterAlpha = alpha;
		invalidate();
	}

	public boolean getIsTouch()
	{
		return mIsTouch;
	}

	public boolean getIsCompare()
	{
		return mCompare;
	}

	public Bitmap getOrgImage()
	{
		return img.m_bmp;
	}

	public Bitmap getMaskBmp()
	{
		return frame.m_bmp;
	}

	public void SetUIEnabled(boolean uiEnabled)
	{
		LockUI(!uiEnabled);
	}

	public void setMaskImage(Bitmap frameBmp)
	{
		frame.m_bmp = frameBmp;
		frame.m_matrix = img.m_matrix;
		invalidate();
	}

	public void setMaskImage(Bitmap bmp, boolean invalidate)
	{
		if (invalidate) {
			setMaskImage(bmp);
		} else {
			frame.m_bmp = bmp;
			frame.m_matrix = img.m_matrix;
		}
	}

	public void setOrgImage(Bitmap bmp)
	{
		setImage(bmp);
	}

	public void setOrgImage(Bitmap bmp, boolean invalidate)
	{
		if (invalidate) {
			setOrgImage(bmp);
		} else {
			img.m_bmp = bmp;
		}
	}

	public interface ControlCallback extends BeautyCommonViewEx.ControlCallback
	{
		/**
		 * @param fingerCount 触发Down事件时的手指数量
		 */
		void OnFingerDown(int fingerCount);

		/**
		 * @param fingerCount 触发Up事件时的手指数量
		 */
		void OnFingerUp(int fingerCount);

		void OnClickWaterMask();
	}
}
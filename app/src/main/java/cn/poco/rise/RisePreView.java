package cn.poco.rise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.statistics.MyBeautyStat;
import my.beautyCamera.R;

public class RisePreView extends View
{
	private Paint mPaint;
	private Paint mTextPaint;
	private Bitmap mBmp; // 原图
	private Matrix mMatrix;

	private Matrix mAdjustMatrix;
	private Bitmap mAdjustBmp;

	private int mViewW;
	private int mViewH;

	// 记录每次图片拉伸的数据
	private ArrayList<RegionInfo> mData;
	private int mSelIndex;
	private RegionInfo mRegionInfo;

	private int mTouchDashedArea = DashedValidType.NO;

	private int mSelBKColor;
	private String mSelText;

	private CallBack mCB;
	private boolean mIsCompare;
	private boolean mShowDashed;

	private boolean mInit;
	private boolean isDown;
	private boolean mShowTipText;
	private float mSelAreaAlpha;
	private float mSelAreaTextAlpha;
	private boolean mDragSeekBar;
	private boolean hasChange;
	private float mDownY;

	public boolean isHasChange()
	{
		return hasChange;
	}

	interface DashedValidType
	{
		int NO = 1;
		int TOP_DASHED_AREA = 1 << 2;
		int BOTTOM_DASHED_AREA = 1 << 3;
		int DASH_ALL_AREA = 1 << 4;
	}

	public interface CallBack
	{
		void onTouchDashedAreaEnd();

		void onCanNotUnDo();

		void onCanUnDo();

		void onCanNotReDo();

		void onCanReDo();

		void onShowUnDoCtrl(boolean show);

		void onShowCompare(boolean show);
	}

	public RisePreView(Context context)
	{
		super(context);
		mInit = true;

		mSelAreaAlpha = 0.2f;
		mSelAreaTextAlpha = 1f;

		mShowDashed = true;
		mData = new ArrayList<>();
		mPaint = new Paint();

		mTextPaint = new Paint();
		mSelBKColor = ImageUtils.GetSkinColor();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, context.getResources().getDisplayMetrics()));
		mTextPaint.setTypeface(Typeface.DEFAULT);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mSelText = getContext().getString(R.string.rise_page_sel_area_text);

		mAdjustBmp = Bitmap.createBitmap(CameraPercentUtil.WidthPxToPercent(70), CameraPercentUtil.WidthPxToPercent(70), Bitmap.Config.ARGB_8888);
		Bitmap adjust_bk = BitmapFactory.decodeResource(getResources(), R.drawable.rise_adjust_bk);
		Bitmap adjust_logo = BitmapFactory.decodeResource(getResources(), R.drawable.rise_adjust_logo);
		adjust_logo = ImageUtils.AddSkin(context, adjust_logo);
		Canvas canvas = new Canvas(mAdjustBmp);
		mPaint.reset();
		mPaint.setFilterBitmap(true);
		mPaint.setAntiAlias(true);
		mAdjustMatrix = new Matrix();
		float scale = Math.max(mAdjustBmp.getWidth() * 1f / adjust_logo.getWidth(), mAdjustBmp.getHeight() * 1f / adjust_logo.getHeight());
		mAdjustMatrix.postScale(scale, scale);
		canvas.drawBitmap(adjust_bk, mAdjustMatrix, mPaint);
		canvas.drawBitmap(adjust_logo, mAdjustMatrix, mPaint);
		mShowTipText = true;
	}

	public void clearAll()
	{
		if(mAdjustBmp != null && !mAdjustBmp.isRecycled())
		{
			mAdjustBmp.recycle();
			mAdjustBmp = null;
		}
	}

	public void setOnListener(CallBack cb)
	{
		mCB = cb;
	}

	public void setBitmap(Bitmap bitmap)
	{
		mBmp = bitmap;
		if(mBmp != null && mRegionInfo != null)
		{
			mRegionInfo.setBitmap(mBmp);
			invalidate();
		}
	}

	public void setStretchDegree(float degree, boolean showDashed)
	{
		mShowTipText = false;
		mSelAreaAlpha = 0.2f;
		mSelAreaTextAlpha = 1f;

		mDragSeekBar = !showDashed;

		if(mRegionInfo != null && mRegionInfo.isValid())
		{
			hasChange = true;
			mRegionInfo.setStretchDegree(degree * 0.2f);
		}

		mShowDashed = showDashed;

		invalidate();
	}

	public void HideSelAreaTipText()
	{
		if(!isDown)
		{
			final float sel_area_alpha = mSelAreaAlpha;
			final float sel_area_text_alpha = mSelAreaTextAlpha;

			ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
			{
				@Override
				public void onAnimationUpdate(ValueAnimator animation)
				{
					float value = (float)animation.getAnimatedValue();
					mSelAreaAlpha = sel_area_alpha * value;
					mSelAreaTextAlpha = sel_area_text_alpha * value;

					if((isDown && mTouchDashedArea != DashedValidType.NO) || mDragSeekBar)
					{
						mSelAreaAlpha = 0.2f;
						mSelAreaTextAlpha = 1f;
						animation.removeAllUpdateListeners();
						animation.removeAllListeners();
						animation.cancel();
					}
					invalidate();
				}
			});
			animator.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					mShowTipText = false;
					mSelAreaAlpha = 0.2f;
					mSelAreaTextAlpha = 1f;
				}
			});
			animator.setDuration(400);
			animator.start();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mViewW = w;
		mViewH = h;

		if(mInit)
		{
			RegionInfo info = new RegionInfo(true);
			info.setTag(0);
			info.setPreViewWH(w, h);
			info.setBitmap(mBmp);
			mRegionInfo = info;
			mData.add(info);
		}
		mInit = false;
	}

	private void updateAllDashed(int dis)
	{
		if(mRegionInfo != null && mRegionInfo.isValid())
		{
			int bmp_top = mRegionInfo.getBmpPreTopLoc();
			int bmp_bot = mRegionInfo.getBmpPreBotLoc();

			int top = mRegionInfo.getPreDashedLoc(true);
			int bot = mRegionInfo.getPreDashedLoc(false);

			int tem_top = top + dis;
			int tem_bot = bot + dis;

			if(tem_top <= bmp_top)
			{
				dis = bmp_top - top;
			}
			else if(tem_bot >= bmp_bot)
			{
				dis = bmp_bot - bot;
			}

			mRegionInfo.setDashed(top + dis, bot + dis);
			invalidate();
		}
	}

	private void updateDashedPath(float y)
	{
		if(mRegionInfo != null && mRegionInfo.isValid())
		{
			int bmp_top = mRegionInfo.getBmpPreTopLoc();
			int bmp_bot = mRegionInfo.getBmpPreBotLoc();

			if(y < bmp_top)
			{
				y = bmp_top;
			}
			else if(y > bmp_bot)
			{
				y = bmp_bot;
			}

			int loc = (int)y;
			int top = mRegionInfo.getPreDashedLoc(true);
			int bot = mRegionInfo.getPreDashedLoc(false);

			switch(mTouchDashedArea)
			{
				case DashedValidType.TOP_DASHED_AREA:
				{
					if(loc >= bot)
					{
						mTouchDashedArea = DashedValidType.BOTTOM_DASHED_AREA;
						top = bot;
					}
					else
					{
						top = loc;
					}
					break;
				}

				case DashedValidType.BOTTOM_DASHED_AREA:
				{
					if(loc <= top)
					{
						mTouchDashedArea = DashedValidType.TOP_DASHED_AREA;
						bot = top;
					}
					else
					{
						bot = loc;
					}
					break;
				}

				case DashedValidType.NO:
				{
					return;
				}
			}

			mRegionInfo.setDashed(top, bot);
			invalidate();
		}
	}

	public int isPointInDashed(float x, float y)
	{
		if(mRegionInfo != null && mRegionInfo.isValid())
		{
			if(mRegionInfo.mTopDashedValidRect.contains(x, y))
			{
				MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_上标准线);
				return DashedValidType.TOP_DASHED_AREA;
			}
			else if(mRegionInfo.mBotDashedValidRect.contains(x, y))
			{
				MyBeautyStat.onClickByRes(R.string.美颜美图_增高页_主页面_下标准线);
				return DashedValidType.BOTTOM_DASHED_AREA;
			}
			else if(mRegionInfo.isTouchOnDashedValidArea(y))
			{
				return DashedValidType.DASH_ALL_AREA;
			}
		}

		return DashedValidType.NO;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				isDown = true;
				mTouchDashedArea = isPointInDashed(event.getX(), event.getY());
				if(mTouchDashedArea != DashedValidType.NO)
				{
					mShowTipText = true;
					mDownY = event.getY();
					if(mTouchDashedArea != DashedValidType.DASH_ALL_AREA)
					{
						updateDashedPath(event.getY());
					}
					else
					{
						invalidate();
					}
				}
				return true;
			}

			case MotionEvent.ACTION_MOVE:
			{
				if(mTouchDashedArea == DashedValidType.DASH_ALL_AREA)
				{
					updateAllDashed((int)(event.getY() - mDownY));
					mDownY = event.getY();
				}
				else
				{
					updateDashedPath(event.getY());
				}
				return true;
			}

			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			{
				isDown = false;
				mShowTipText = false;
				if(mTouchDashedArea == DashedValidType.NO) return true;
				mTouchDashedArea = DashedValidType.NO;
				if(mRegionInfo != null && mRegionInfo.isValid())
				{
					if(mRegionInfo.mStretchDegree != 0)
					{
						Bitmap bmp = mRegionInfo.getOutPutBitmap();

						RegionInfo info = new RegionInfo(false);
						info.setPreViewWH(mViewW, mViewH);
						info.setDashed(mRegionInfo.getPreDashedLoc(true), mRegionInfo.getPreDashedLoc(false));
						info.setBitmap(bmp);
						info.setStretchDegree(0);

						mRegionInfo.setDashedToOrg();
						mRegionInfo.setStretchDegree(0);

						if(mData != null)
						{
							if(mSelIndex != mData.size() - 1)
							{
								for(int i = mSelIndex + 1; i < mData.size(); )
								{
									mData.remove(i);
								}
								if(mCB != null)
								{
									mCB.onCanNotReDo();
								}
							}
							mData.add(info);
							if(mData.size() > 6)
							{
								mData.remove(0);
							}
							mSelIndex = mData.size() - 1;
						}
						mRegionInfo = info;

						if(mCB != null)
						{
							if(mData != null)
							{
								mCB.onShowUnDoCtrl(mData.size() > 1);
								mCB.onShowCompare(mRegionInfo.getTag() == null);

								if(mRegionInfo != null && mData.size() == 2)
								{
									mCB.onCanUnDo();
								}
							}
							mCB.onTouchDashedAreaEnd();
						}
					}
					invalidate();
				}
				return true;
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		drawCompareBmpToCanvas(canvas);
		drawContentToCanvas(canvas);
	}

	private void drawCompareBmpToCanvas(Canvas canvas)
	{
		if(mIsCompare)
		{
			mPaint.reset();
			mPaint.setFilterBitmap(true);
			mPaint.setAntiAlias(true);

			if(mMatrix == null)
			{
				mMatrix = new Matrix();
				float scale = Math.min(mViewW * 1f / mBmp.getWidth(), mViewH * 1f / mBmp.getHeight());
				mMatrix.reset();
				mMatrix.postScale(scale, scale);
				mMatrix.postTranslate((mViewW - mBmp.getWidth() * scale) / 2f, (mViewH - mBmp.getHeight() * scale) / 2f);
			}

			canvas.save();
			canvas.drawBitmap(mBmp, mMatrix, mPaint);
			canvas.restore();
		}
	}

	private void drawContentToCanvas(Canvas canvas)
	{
		if(mIsCompare) return;

		mPaint.reset();
		mPaint.setFilterBitmap(true);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(CameraPercentUtil.WidthPxToPercent(1));

		if(mRegionInfo != null && mRegionInfo.mBmp != null)
		{
			canvas.save();
			canvas.drawBitmap(mRegionInfo.mBmp, mRegionInfo.mBmpTopRect, mRegionInfo.mPreTopRect, mPaint);// TOP
			canvas.drawBitmap(mRegionInfo.mBmp, mRegionInfo.mBmpMidRect, mRegionInfo.mPreMidRect, mPaint);// MIDDLE
			canvas.drawBitmap(mRegionInfo.mBmp, mRegionInfo.mBmpBotRect, mRegionInfo.mPreBotRect, mPaint);// BOTTOM

			if(mShowDashed)
			{
				if(mShowTipText)
				{
					mPaint.setColor(mSelBKColor);
					mPaint.setAlpha((int)(255 * mSelAreaAlpha));
					mPaint.setStyle(Paint.Style.FILL);
					canvas.drawRect(mRegionInfo.mDashedRect, mPaint);
					mTextPaint.setAlpha((int)(255 * mSelAreaTextAlpha));
					Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
					float text_height = metrics.bottom - metrics.top;
					if(mRegionInfo.mDashedRect.height() > text_height + CameraPercentUtil.WidthPxToPercent(20))
					{
						float x = mViewW / 2f;
						float y = (mRegionInfo.mDashedRect.top + mRegionInfo.mDashedRect.bottom) / 2f + text_height / 2f - metrics.bottom;
						canvas.drawText(mSelText, x, y, mTextPaint);
					}
				}

				mPaint.reset();
				mPaint.setFilterBitmap(true);
				mPaint.setAntiAlias(true);
				mPaint.setColor(0xB3FFFFFF);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeWidth(CameraPercentUtil.WidthPxToPercent(2));
				canvas.drawPath(mRegionInfo.mTopDashedPath, mPaint);
				canvas.drawPath(mRegionInfo.mBotDashedPath, mPaint);

				mPaint.reset();
				mPaint.setFilterBitmap(true);
				mPaint.setAntiAlias(true);
				int top = mRegionInfo.getPreDashedLoc(true);
				int bot = mRegionInfo.getPreDashedLoc(false);
				mAdjustMatrix.reset();
				mAdjustMatrix.postTranslate(mViewW - CameraPercentUtil.WidthPxToPercent(122) - mAdjustBmp.getWidth() / 2f, top - mAdjustBmp.getHeight() / 2f);
				canvas.drawBitmap(mAdjustBmp, mAdjustMatrix, mPaint);
				mAdjustMatrix.reset();
				mAdjustMatrix.postTranslate(mViewW - CameraPercentUtil.WidthPxToPercent(122) - mAdjustBmp.getWidth() / 2f, bot - mAdjustBmp.getHeight() / 2f);
				canvas.drawBitmap(mAdjustBmp, mAdjustMatrix, mPaint);
			}
			canvas.restore();
		}
	}

	public void unDo()
	{
		if(mData != null)
		{
			mSelIndex -= 1;
			RegionInfo info = mData.get(mSelIndex);
			if(info != null)
			{
				if(mRegionInfo != null && mRegionInfo.isValid())
				{
					if(mRegionInfo.mStretchDegree != 0)
					{
						mRegionInfo.setStretchDegree(0);
					}
				}
				mRegionInfo = info;
				invalidate();
			}

			if(mCB != null)
			{
				mCB.onCanReDo();
				if(mSelIndex == 0)
				{
					mCB.onCanNotUnDo();
					if(mRegionInfo.getTag() != null)
					{
						mCB.onShowCompare(false);
					}
				}
			}
		}
	}

	public void reDo()
	{
		if(mData != null)
		{
			mSelIndex += 1;
			RegionInfo info = mData.get(mSelIndex);
			if(info != null)
			{
				if(mRegionInfo != null && mRegionInfo.isValid())
				{
					if(mRegionInfo.mStretchDegree != 0)
					{
						mRegionInfo.setStretchDegree(0);
					}
				}

				mRegionInfo = info;
				invalidate();
			}

			if(mCB != null)
			{
				mCB.onShowCompare(true);
				mCB.onCanUnDo();
				if(mSelIndex == mData.size() - 1)
				{
					mCB.onCanNotReDo();
				}
			}
		}
	}

	public void compare(boolean compare)
	{
		mIsCompare = compare;
		invalidate();
	}

	public Bitmap getOutPutBmp()
	{
		if(mRegionInfo != null && mRegionInfo.isValid())
		{
			mRegionInfo = mData.remove(mData.size() - 1);
			for(RegionInfo info : mData)
			{
				if(info != null)
				{
					if(info.mBmp != null && !info.mBmp.isRecycled())
					{
						info.mBmp.recycle();
						info.mBmp = null;
					}
				}
			}
			mData.clear();

			return mRegionInfo.getOutPutBitmap();
		}

		return null;
	}

	public float getImgHeight()
	{
		if(mRegionInfo != null && mRegionInfo.isValid())
		{
			return mRegionInfo.mPreTopRect.height() + mRegionInfo.mPreMidRect.height() + mRegionInfo.mPreBotRect.height();
		}

		return 0;
	}

	public void ClearAll()
	{
		for(RegionInfo info : mData)
		{
			if(info != null)
			{
				if(info.mBmp != null && !info.mBmp.isRecycled())
				{
					info.mBmp.recycle();
					info.mBmp = null;
				}
			}
		}
		mData.clear();
		mCB = null;

		if(mBmp != null && !mBmp.isRecycled())
		{
			mBmp.recycle();
		}
		mBmp = null;

		mRegionInfo = null;
	}
}

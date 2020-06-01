package cn.poco.rise;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import cn.poco.camera3.util.CameraPercentUtil;

/**
 * 记录增高每次操作数据
 * Created by Gxx on 2017/11/27.
 */

public class RegionInfo
{
	private DashedLoc mOrgPreDashedLoc;
	private DashedLoc mCurPreDashedLoc;

	private boolean mSetDefDashedDistance;

	Path mTopDashedPath;
	Path mBotDashedPath;
	Rect mDashedRect;

	private int mDashedValidArea;

	RectF mTopDashedValidRect;
	RectF mBotDashedValidRect;

	float mStretchDegree; // 拉伸程度

	// view preview w h
	private int mPreViewWidth;
	private int mPreViewHeight;

	// content preview w h
	private int mPreWidth;
	int mPreHeight;

	// bmp w h
	private int mBmpWidth;
	private int mBmpHeight;

	Bitmap mBmp;

	Rect mPreTopRect;
	Rect mPreMidRect;
	Rect mPreBotRect;

	Rect mBmpTopRect;
	Rect mBmpMidRect;
	Rect mBmpBotRect;

	private Object mTag;

	static class DashedLoc
	{
		int top;
		int bottom;
	}

	RegionInfo(boolean def_dashed_distance)
	{
		mSetDefDashedDistance = def_dashed_distance;

		mOrgPreDashedLoc = new DashedLoc();
		mCurPreDashedLoc = new DashedLoc();

		mTopDashedPath = new Path();
		mBotDashedPath = new Path();
		mDashedValidArea = CameraPercentUtil.WidthPxToPercent(30);

		initBmpRect();
		initPreRect();
		initDashedRect();
		initDashedValidRect();
	}

	public void setTag(Object tag)
	{
		mTag = tag;
	}

	public Object getTag()
	{
		return mTag;
	}

	public boolean isTouchOnDashedValidArea(float y)
	{
		float top = 0;
		float bottom = 0;
		if(mTopDashedValidRect != null)
		{
			top = mTopDashedValidRect.top;
		}

		if(mBotDashedValidRect != null)
		{
			bottom = mBotDashedValidRect.bottom;
		}

		return y >= top && y <= bottom;
	}

	public void setBitmap(Bitmap bmp)
	{
		mBmp = bmp;
		if(isValid())
		{
			init();
		}
	}

	void setStretchDegree(float degree)
	{
		mStretchDegree = degree;
		updateBmpPreData();
	}

	boolean isValid()
	{
		return mBmp != null;
	}

	private void init()
	{
		mBmpWidth = mBmp.getWidth();
		mBmpHeight = mBmp.getHeight();

		float scale = Math.min(mPreViewWidth * 1f / mBmpWidth, mPreViewHeight * 1f / mBmpHeight);

		mPreWidth = (int)(mBmpWidth * scale);
		mPreHeight = (int)(mBmpHeight * scale);

		if(mSetDefDashedDistance)
		{
			setDefDashed();
		}

		mSetDefDashedDistance = false;

		// 预览 rect 的 left、top、right、bottom
		int left = (mPreViewWidth - mPreWidth) / 2;
		int top = (mPreViewHeight - mPreHeight) / 2;
		int right = left + mPreWidth;
		int bottom = top + mPreHeight;

		mPreTopRect.set(left, top, right, mCurPreDashedLoc.top);
		mPreMidRect.set(left, mCurPreDashedLoc.top, right, mCurPreDashedLoc.bottom);
		mPreBotRect.set(left, mCurPreDashedLoc.bottom, right, bottom);

		int top_height_in_bmp = (int)(mBmpHeight * (mCurPreDashedLoc.top - (mPreViewHeight - mPreHeight) / 2) * 1f / mPreHeight);
		int bot_height_in_bmp = (int)(mBmpHeight * (mCurPreDashedLoc.bottom - (mPreViewHeight - mPreHeight) / 2) * 1f / mPreHeight);

		mBmpTopRect.set(0, 0, mBmpWidth, top_height_in_bmp);
		mBmpMidRect.set(0, top_height_in_bmp, mBmpWidth, bot_height_in_bmp);
		mBmpBotRect.set(0, bot_height_in_bmp, mBmpWidth, mBmpHeight);

		updateDashedPreData(mCurPreDashedLoc.top, mCurPreDashedLoc.bottom);
	}

	private void setDefDashed()
	{
		mOrgPreDashedLoc.top = mPreViewHeight / 2; // 图片预览区域 50%
		mOrgPreDashedLoc.bottom = (int)(mOrgPreDashedLoc.top + mPreHeight * 0.25f); // 图片预览区域 75%

		mCurPreDashedLoc.top = mOrgPreDashedLoc.top;
		mCurPreDashedLoc.bottom = mOrgPreDashedLoc.bottom;
	}

	void setDashedToOrg()
	{
		setDashed(mOrgPreDashedLoc.top, mOrgPreDashedLoc.bottom);
	}

	void setDashed(int top, int bot)
	{
		mCurPreDashedLoc.top = top;
		mCurPreDashedLoc.bottom = bot;

		if(mStretchDegree == 0)
		{
			mOrgPreDashedLoc.top = top;
			mOrgPreDashedLoc.bottom = bot;
		}

		updateDashedPreData(top, bot);
	}

	void setPreViewWH(int w, int h)
	{
		mPreViewHeight = h;
		mPreViewWidth = w;
	}

	int getPreDashedLoc(boolean top)
	{
		return mStretchDegree != 0 ? top ? mCurPreDashedLoc.top : mCurPreDashedLoc.bottom : top ? mOrgPreDashedLoc.top : mOrgPreDashedLoc.bottom;
	}

	private void updateBmpPreData()
	{
		int top_part_height_in_bmp = (int)(mBmpHeight * (mOrgPreDashedLoc.top - (mPreViewHeight - mPreHeight) / 2) * 1f / mPreHeight);
		int bot_part_height_in_bmp = (int)(mBmpHeight * (((mPreViewHeight + mPreHeight) / 2) - mOrgPreDashedLoc.bottom) * 1f / mPreHeight);

		mBmpTopRect.set(0, 0, mBmpWidth, top_part_height_in_bmp);
		mBmpMidRect.set(0, top_part_height_in_bmp, mBmpWidth, mBmpHeight - bot_part_height_in_bmp);
		mBmpBotRect.set(0, mBmpHeight - bot_part_height_in_bmp, mBmpWidth, mBmpHeight);

		int sel_part_height_in_bmp = mBmpHeight - top_part_height_in_bmp - bot_part_height_in_bmp;
		if(sel_part_height_in_bmp >= 400)
		{
			sel_part_height_in_bmp = 400;
		}
		int increase_height = (int)Math.ceil(Math.abs(sel_part_height_in_bmp * mStretchDegree));
		int new_bmp_height = mBmpHeight + increase_height * (mStretchDegree > 0 ? 1 : -1);

		float scale = Math.min(mPreViewWidth * 1f / mBmpWidth, mPreViewHeight * 1f / new_bmp_height);

		int pre_width = (int)(mBmpWidth * scale);
		int pre_height = (int)(new_bmp_height * scale);

		int temp_pre_top_dashed_loc = (mPreViewHeight - pre_height) / 2 + (int)(pre_width * top_part_height_in_bmp * 1f / mBmpWidth);
		int temp_pre_bot_dashed_loc = (mPreViewHeight + pre_height) / 2 - (int)(pre_width * bot_part_height_in_bmp * 1f / mBmpWidth);

		mCurPreDashedLoc.top = temp_pre_top_dashed_loc;
		mCurPreDashedLoc.bottom = temp_pre_bot_dashed_loc;

		int left = (mPreViewWidth - pre_width) / 2;
		int top = (mPreViewHeight - pre_height) / 2;
		int right = left + pre_width;
		int bottom = top + pre_height;

		mPreTopRect.set(left, top, right, temp_pre_top_dashed_loc);
		mPreMidRect.set(left, temp_pre_top_dashed_loc, right, temp_pre_bot_dashed_loc);
		mPreBotRect.set(left, temp_pre_bot_dashed_loc, right, bottom);

		updateDashedPreData(temp_pre_top_dashed_loc, temp_pre_bot_dashed_loc);
	}

	public int getBmpPreBotLoc()
	{
		return mPreBotRect != null ? mPreBotRect.bottom : 0;
	}

	public int getBmpPreTopLoc()
	{
		return mPreTopRect != null ? mPreTopRect.top : 0;
	}

	private void updateDashedPreData(int top, int bot)
	{
		initDashedRect();

		mTopDashedPath.reset();
		mTopDashedPath.moveTo(0, top);
		mTopDashedPath.lineTo(mPreViewWidth, top);

		mBotDashedPath.reset();
		mBotDashedPath.moveTo(0, bot);
		mBotDashedPath.lineTo(mPreViewWidth, bot);

		mDashedRect.set(0, top, mPreViewWidth, bot);

		mTopDashedValidRect.set(0, top - mDashedValidArea, mPreViewWidth, top + mDashedValidArea);
		mBotDashedValidRect.set(0, bot - mDashedValidArea, mPreViewWidth, bot + mDashedValidArea);
	}

	private void initDashedRect()
	{
		if(mDashedRect == null)
		{
			mDashedRect = new Rect();
		}
		mDashedRect.setEmpty();
	}

	private void initPreRect()
	{
		if(mPreTopRect == null)
		{
			mPreTopRect = new Rect();
		}

		if(mPreMidRect == null)
		{
			mPreMidRect = new Rect();
		}

		if(mPreBotRect == null)
		{
			mPreBotRect = new Rect();
		}

		mPreTopRect.setEmpty();
		mPreMidRect.setEmpty();
		mPreBotRect.setEmpty();
	}

	private void initBmpRect()
	{
		if(mBmpTopRect == null)
		{
			mBmpTopRect = new Rect();
		}

		if(mBmpMidRect == null)
		{
			mBmpMidRect = new Rect();
		}

		if(mBmpBotRect == null)
		{
			mBmpBotRect = new Rect();
		}

		mBmpTopRect.setEmpty();
		mBmpMidRect.setEmpty();
		mBmpBotRect.setEmpty();
	}

	private void initDashedValidRect()
	{
		if(mTopDashedValidRect == null)
		{
			mTopDashedValidRect = new RectF();
		}

		if(mBotDashedValidRect == null)
		{
			mBotDashedValidRect = new RectF();
		}

		mTopDashedValidRect.set(-1, -1, -1, -1);
		mBotDashedValidRect.set(-1, -1, -1, -1);
	}

	public Bitmap getOutPutBitmap()
	{
		int top_part_height_in_bmp = (int)(mBmpHeight * (mOrgPreDashedLoc.top - (mPreViewHeight - mPreHeight) / 2) * 1f / mPreHeight);
		int bot_part_height_in_bmp = (int)(mBmpHeight * (((mPreViewHeight + mPreHeight) / 2) - mOrgPreDashedLoc.bottom) * 1f / mPreHeight);

		mBmpTopRect.set(0, 0, mBmpWidth, top_part_height_in_bmp);
		mBmpMidRect.set(0, top_part_height_in_bmp, mBmpWidth, mBmpHeight - bot_part_height_in_bmp);
		mBmpBotRect.set(0, mBmpHeight - bot_part_height_in_bmp, mBmpWidth, mBmpHeight);

		int sel_part_height_in_bmp = mBmpHeight - top_part_height_in_bmp - bot_part_height_in_bmp;
		if(sel_part_height_in_bmp >= 400)
		{
			sel_part_height_in_bmp = 400;
		}
		int increase_height = (int)Math.ceil(Math.abs(sel_part_height_in_bmp * mStretchDegree));
		int new_bmp_height = mBmpHeight + increase_height * (mStretchDegree > 0 ? 1 : -1);

		Bitmap out = Bitmap.createBitmap(mBmpWidth, new_bmp_height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(out);

		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);

		Rect mid = new Rect(0, mBmpTopRect.height(), mBmpWidth, new_bmp_height - mBmpBotRect.height());
		Rect bot = new Rect(0, new_bmp_height - mBmpBotRect.height(), mBmpWidth, new_bmp_height);
		canvas.save();
		canvas.drawBitmap(mBmp, mBmpTopRect, mBmpTopRect, paint);
		canvas.drawBitmap(mBmp, mBmpMidRect, mid, paint);
		canvas.drawBitmap(mBmp, mBmpBotRect, bot, paint);
		canvas.restore();

		return out;
	}
}

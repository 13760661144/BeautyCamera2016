package cn.poco.PhotoPicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.model.PhotoInfo;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

public class ImageViewer extends View
{
	//private static final String TAG = "选图看大图";

	public ImageViewer(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public ImageViewer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public ImageViewer(Context context)
	{
		super(context);
		initialize(context);
	}

	protected static final int OFFSET_RATIO = 5;
	private ArrayList<CacheImage> mCachedImgs = new ArrayList<>();
	private int mCurSel = 0;
	private RectF mRcDefault;
	protected int mMinSize = 640;
	private ArrayList<PhotoInfo> mImages = new ArrayList<>();
	private OnLoadListener mLoadListener = null;
	private OnSwitchListener mOnSwitchListener = null;
	private OnClickListener mOnClickListener = null;
	private OnLongClickListener mOnLongClickListener = null;
	private OnScrollListener mOnScrollListener = null;
	private int mScaledMinimumFlingVelocity;
	private int mScaledMaximumFlingVelocity;
	private boolean mShowNoMoreTips = true;
	private float mMaxScale = 3;
	private int m_topMargin = 0;

	public interface OnLoadListener
	{
		void onStart(PhotoInfo img);

		void onProgress(int downloadedSize, int totalSize);

		void onEnd(PhotoInfo img, boolean success);
	}

	public interface OnSwitchListener
	{
		void onSwitch(PhotoInfo img, int index);
	}

	public static interface OnScrollListener
	{
		public void OnScroll(float disX, float disY);

		public void OnUP(float disX, float disY);
	}

	protected class CacheImage
	{
		public PhotoInfo imgInfo;
		public boolean loaded = false;
		public int width;
		public int height;
		public Bitmap bitmap;
		public String image;
		public RectF rcFrame = new RectF();
		public int index;
		public ProgressDrawer pgsDrawer = new ProgressDrawer(getContext());
	}

	private void initialize(Context context)
	{
		ShareData.InitData(context);
		mMinSize = ShareData.m_screenWidth;

		ViewConfiguration viewCfg = ViewConfiguration.get(context);
		mScaledMinimumFlingVelocity = viewCfg.getScaledMinimumFlingVelocity();
		mScaledMaximumFlingVelocity = viewCfg.getScaledMaximumFlingVelocity();
		setClickable(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setDrawingCacheEnabled(false);

		//TongJiUtils.onPageStart(getContext(), R.string.选图看大图);
	}

	public void setLoadListener(OnLoadListener l)
	{
		mLoadListener = l;
	}

	public void setSwitchListener(OnSwitchListener l)
	{
		mOnSwitchListener = l;
	}

	public void setImages(List<PhotoInfo> imgs)
	{
		PhotoInfo imgInfo = null;
		int count = imgs.size();
		for(int i = 0; i < count; i++)
		{
			imgInfo = imgs.get(i);
			if(!TextUtils.isEmpty(imgInfo.getImagePath()))
			{
				mImages.add(imgInfo);
			}
		}
		//setSel(0);
	}

	public void updateImages(List<PhotoInfo> imgs, int currentIndex)
	{
		mImages.clear();
		PhotoInfo imgInfo = null;
		int count = imgs.size();
		for(int i = 0; i < count; i++)
		{
			imgInfo = imgs.get(i);
			if(!TextUtils.isEmpty(imgInfo.getImagePath()))
			{
				mImages.add(imgInfo);
			}
		}

		int diff = currentIndex - mCurSel;

		for(CacheImage cacheImage : mCachedImgs)
		{
			cacheImage.index += diff;
		}

		mCurSel = currentIndex;
	}

	public void setImages(PhotoInfo[] imgs)
	{
		for(int i = 0; i < imgs.length; i++)
		{
			if(!TextUtils.isEmpty(imgs[i].getImagePath()))
			{
				mImages.add(imgs[i]);
			}
		}
		//setSel(0);
	}

	public void setImages(String[] imgs)
	{
		PhotoInfo imgInfo = null;
		String img = null;
		for(int i = 0; i < imgs.length; i++)
		{
			img = imgs[i];
			imgInfo = new PhotoInfo();
			if(!TextUtils.isEmpty(img))
			{
				imgInfo.setImagePath(img);
				mImages.add(imgInfo);
			}
		}
		//setSel(0);
	}

	public void showNoMoreTips(boolean show)
	{
		mShowNoMoreTips = show;
	}

	public void leave()
	{
		synchronized(mCachedImgs)
		{
			CacheImage img = null;
			for(int i = 0; i < mCachedImgs.size(); i++)
			{
				img = mCachedImgs.get(i);
				img.bitmap = null;
			}
		}
		mCachedImgs.clear();
		stopTweener();
		mStoping = true;
		//stopGif();
	}

	public void enter(int index, Bitmap bmp)
	{
		mStoping = false;
		setSel(index, bmp);
	}

	public void setSel(int index, Bitmap bmp)
	{
		if(index >= 0 && index < mImages.size())
		{
			//stopGif();

			mCurSel = index;
			mSelBmp = bmp;
			if(mW == 0 || mH == 0)
			{
				return;
			}
			mCurImg = cacheImage(index, bmp);
			cacheImage(index - 1, null);
			cacheImage(index + 1, null);
			mSelBmp = null;

			mCurImg.rcFrame.set(mRcDefault);
			mScale = 1.0f;
			if(mCurImg.bitmap != null)
			{
				mBmpW = mCurImg.bitmap.getWidth();
				mBmpH = mCurImg.bitmap.getHeight();
				resetRect(mCurImg.rcFrame, mCurImg.width, mCurImg.height);
				mScale = (float)mCurImg.rcFrame.width() / (float)mBmpW;
				mMaxScale = computeMaxScale(mBmpW, mBmpH);
//				if((mCurImg.imgInfo.getImagePath().endsWith(".gif") || mCurImg.imgInfo.getImagePath().endsWith(".GIF")) &&
//						mCurImg.width <= 1000 && mCurImg.height <= 1000)
//				{
//					if(mCurImg.image != null)
//					{
//						playGif(mCurImg.image);
//					}
//				}
			}
			mRcFrame = mCurImg.rcFrame;
			mRcFrameCache.set(mRcFrame);
			mRcReset.set(mRcFrameCache);
			postInvalidate();
			if(mOnSwitchListener != null)
			{
				mOnSwitchListener.onSwitch(mCurImg.imgInfo, index);
			}
		}
	}

	public void setCurBitmap(Bitmap bitmap)
	{
		if(bitmap != null && mCurImg != null)
		{
			if(mCurImg.bitmap != null)
			{
				//mCurImg.bitmap.recycle();
				mCurImg.bitmap = null;
			}
			mCurImg.bitmap = bitmap;
			mCurImg.width = mCurImg.bitmap.getWidth();
			mCurImg.height = mCurImg.bitmap.getHeight();
			mBmpW = mCurImg.width;
			mBmpH = mCurImg.height;
			resetRect(mCurImg.rcFrame, mCurImg.width, mCurImg.height);
			mRcFrame = mCurImg.rcFrame;
			mRcFrameCache.set(mRcFrame);
			mRcReset.set(mRcFrameCache);
			adjustRectY(mRcFrameCache);
			adjustRectX(mRcFrameCache);

			mScale = (float)mCurImg.rcFrame.width() / (float)mBmpW;
			mMaxScale = computeMaxScale(mBmpW, mBmpH);
			mPreScale = mScale;
			mPreOffset = mCurOffset;
			postInvalidate();
		}
	}

	public Bitmap getCurBitmap()
	{
		if(mCurImg != null)
		{
			return mCurImg.bitmap;
		}
		return null;
	}

	public PhotoInfo getCurImage()
	{
		if(mCurImg != null)
		{
			return mCurImg.imgInfo;
		}
		return null;
	}

	public RectF getCurCache()
	{
		if(mCurImg != null)
		{
			return mCurImg.rcFrame;
		}
		return null;
	}

	public void SetTopMargin(int margin)
	{
		m_topMargin = margin;
	}

	public ArrayList<PhotoInfo> getImages()
	{
		return mImages;
	}

	public int getCurSel()
	{
		return mCurSel;
	}

	public int getImageCount()
	{
		return mImages.size();
	}

	public void update()
	{
		if(mCurImg != null)
		{
			mCurImg.loaded = false;
			if(mCurImg.bitmap != null)
			{
				//mCurImg.bitmap.recycle();
				mCurImg.bitmap = null;
			}
		}
		setSel(mCurSel, null);
	}

	public void setOnClickListener(OnClickListener l)
	{
		mOnClickListener = l;
	}

	public void setOnLongClickListener(OnLongClickListener l)
	{
		mOnLongClickListener = l;
	}

	public void setOnScrollListener(OnScrollListener l)
	{
		mOnScrollListener = l;
	}

	public void delImage(int index)
	{
		if(index >= 0 && index < mImages.size())
		{
			mImages.remove(index);
			synchronized(mCachedImgs)
			{
				CacheImage img = null;
				for(int i = 0; i < mCachedImgs.size(); i++)
				{
					img = mCachedImgs.get(i);
					if(img != mCurImg && img.index > index)
					{
						img.index--;
					}
				}
				if(mCurImg != null)
				{
					mCachedImgs.remove(mCurImg);
				}
			}
			if(mCurSel >= mImages.size())
			{
				mCurSel = mImages.size() - 1;
			}
			if(mImages.size() > 0)
			{
				setSel(mCurSel, null);
			}
			else
			{
				mCurImg = null;
				mCurSel = 0;
				postInvalidate();
			}
		}
	}

	public void changeImage(PhotoInfo info, int index, Bitmap selBmp)
	{
		if(index >= 0 && index < mImages.size())
		{
			synchronized(mCachedImgs)
			{
				CacheImage img = null;
				for(int i = 0; i < mCachedImgs.size(); i++)
				{
					img = mCachedImgs.get(i);
					if(img.index == index)
					{
						/*if(img.bitmap != null)
						{
							img.bitmap.recycle();
						}*/
						img.bitmap = null;
						mCachedImgs.remove(i);
						mCurImg = null;
						break;
					}
				}
			}
			mCurSel = index;
			mImages.set(index, info);
			setSel(mCurSel, selBmp);
		}
	}

	public void changeImage(PhotoInfo info, int index)
	{
		changeImage(info, index, null);
	}

	public void addImage(PhotoInfo info, int index)
	{
		if(index >= mImages.size())
		{
			mImages.add(info);
		}
		else if(index <= 0)
		{
			mImages.add(0, info);
		}
		else
		{
			mImages.add(index, info);
		}
		mCurSel = index;
		update();
	}

	public void resetCache() {
		int index = mCurSel - 1;
		while (index >= 0 && !new File(mImages.get(index).getImagePath()).exists()) {
			index--;
		}
		cacheImage(index, null);
		mCurImg = cacheImage(mCurSel, null);
		index = mCurSel + 1;
		while (index < mImages.size() && !new File(mImages.get(index).getImagePath()).exists()) {
			index++;
		}
		cacheImage(index, null);
	}

	public void addImage2(PhotoInfo info, int index)
	{
		if(index >= mImages.size())
		{
			mImages.add(info);
		}
		else if(index <= 0)
		{
			mImages.add(0, info);
		}
		else
		{
			mImages.add(index, info);
		}
	}

	public void next(int index)
	{
		if(index >= 0 && index < mImages.size())
		{
			//stopGif();
			updateLeftRightPos();

			mCurSel = index;
			if(mW == 0 || mH == 0)
			{
				return;
			}

			if(mCurImg != null && mCurImg.bitmap != null)
			{
				if(mCurImg.bitmap.getWidth() > mMinSize || mCurImg.bitmap.getHeight() > mMinSize)
				{
					Bitmap temp = mCurImg.bitmap;
					mCurImg.bitmap = scaleBitmap(mCurImg.bitmap, mMinSize);
					if(temp != null && temp != mCurImg.bitmap)
					{
						//temp.recycle();
						temp = null;
					}
				}
			}

			resetCache();

			mBmpW = 0;
			mBmpH = 0;
			mScale = 1.0f;
			if(mCurImg.bitmap != null)
			{
				mBmpW = mCurImg.bitmap.getWidth();
				mBmpH = mCurImg.bitmap.getHeight();
				mScale = (float)mCurImg.rcFrame.width() / (float)mBmpW;
				mMaxScale = computeMaxScale(mBmpW, mBmpH);
			}
			mRcFrame = mCurImg.rcFrame;
			mRcFrameCache.set(mRcFrame);
			mRcReset.set(mRcFrameCache);
			if(mCurImg.bitmap != null)
			{
				resetRect(mRcReset, mCurImg.width, mCurImg.height);
//				if((mCurImg.imgInfo.getImagePath().endsWith(".gif") || mCurImg.imgInfo.getImagePath().endsWith(".GIF")) &&
//						mCurImg.width <= 1000 && mCurImg.height <= 1000)
//				{
//					if(mCurImg.image != null)
//					{
//						playGif(mCurImg.image);
//					}
//				}
			}

			adjustRectY(mRcFrameCache);
			adjustRectX(mRcFrameCache);
			startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART, 500, mSwitchListener);

			if(mOnSwitchListener != null)
			{
				mOnSwitchListener.onSwitch(mCurImg.imgInfo, index);
			}
		}
		else
		{
			if(mShowNoMoreTips)
			{
				showToast("已经没有了");
			}
		}
	}

	private Runnable mSwitchListener = new Runnable()
	{

		@Override
		public void run()
		{
			synchronized(mCachedImgs)
			{
				CacheImage img = null;
				for(int i = 0; i < mCachedImgs.size(); i++)
				{
					img = mCachedImgs.get(i);
					if(img != mCurImg && img.bitmap != null)
					{
						resetRect(img.rcFrame, img.width, img.height);
					}
				}
			}
		}

	};

	public void prev(int index)
	{
		if(index >= 0 && index < mImages.size())
		{
			//stopGif();
			updateLeftRightPos();

			mCurSel = index;
			if(mW == 0 || mH == 0)
			{
				return;
			}

			if(mCurImg != null && mCurImg.bitmap != null)
			{
				if(mCurImg.bitmap.getWidth() > mMinSize || mCurImg.bitmap.getHeight() > mMinSize)
				{
					Bitmap temp = mCurImg.bitmap;
					//mCurImg.bitmap = Utils.scaleBitmap(mCurImg.bitmap, mMinSize);
					mCurImg.bitmap = scaleBitmap(mCurImg.bitmap, mMinSize);
					if(temp != null)
					{
						//temp.recycle();
						temp = null;
					}
				}
			}

			resetCache();

			mBmpW = 0;
			mBmpH = 0;
			mScale = 1.0f;
			if(mCurImg.bitmap != null)
			{
				mBmpW = mCurImg.bitmap.getWidth();
				mBmpH = mCurImg.bitmap.getHeight();
				mScale = (float)mCurImg.rcFrame.width() / (float)mBmpW;
				mMaxScale = computeMaxScale(mBmpW, mBmpH);
			}
			mRcFrame = mCurImg.rcFrame;
			mRcFrameCache.set(mRcFrame);
			mRcReset.set(mRcFrameCache);
			if(mCurImg.bitmap != null)
			{
				resetRect(mRcReset, mCurImg.width, mCurImg.height);
//				if((mCurImg.imgInfo.getImagePath().endsWith(".gif") || mCurImg.imgInfo.getImagePath().endsWith(".GIF")) &&
//						mCurImg.width <= 1000 && mCurImg.height <= 1000)
//				{
//					if(mCurImg.image != null)
//					{
//						playGif(mCurImg.image);
//					}
//				}
			}
			adjustRectY(mRcFrameCache);
			adjustRectX(mRcFrameCache);
			startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART, 500, mSwitchListener);

			if(mOnSwitchListener != null)
			{
				mOnSwitchListener.onSwitch(mCurImg.imgInfo, index);
			}
		}
		else
		{
			if(mShowNoMoreTips)
			{
				showToast("已经没有了");
			}
		}
	}

	public void clear()
	{
		if(mToast1 != null)
		{
			mToast1.cancel();
			mToast1 = null;
		}
		if(mVTracker != null)
		{
			//mVTracker.recycle();
			mVTracker = null;
		}
		synchronized(mCachedImgs)
		{
			CacheImage img = null;
			for(int i = 0; i < mCachedImgs.size(); i++)
			{
				img = mCachedImgs.get(i);
				/*if(img.bitmap != null)
				{
					img.bitmap.recycle();
				}*/
				img.bitmap = null;
			}
		}
		stopTweener();
		mStoping = true;

		//TongJiUtils.onPageEnd(getContext(), R.string.选图看大图);
	}

	private float computeMaxScale(int bmpW, int bmpH)
	{
		float maxScale = 3;
		int w = (int)(maxScale * bmpW);
		int h = (int)(maxScale * bmpH);
		if(w < getWidth() && h < getHeight())
		{
			float r1 = (float)getWidth() / (float)bmpW * 1.1f;
			float r2 = (float)getHeight() / (float)bmpH * 1.1f;
			maxScale = r1 < r2 ? r1 : r2;
		}
		return maxScale;
	}

	private void updateLeftRightPos()
	{
		CacheImage img = getLeftImage();
		if(img != null)
		{
			float w = img.rcFrame.width();
			float offset1 = (mW - w) / 2;
			if(offset1 < 0) offset1 = 0;
			float offset2 = (mW - mRcFrame.width()) / 2;
			if(offset2 < 0) offset2 = 0;
			img.rcFrame.right = mRcFrame.left - offset2 - 10 - offset1;
			img.rcFrame.left = img.rcFrame.right - w;
		}
		img = getRightImage();
		if(img != null)
		{
			float w = img.rcFrame.width();
			float offset1 = (mW - w) / 2;
			if(offset1 < 0) offset1 = 0;
			float offset2 = (mW - mRcFrame.width()) / 2;
			if(offset2 < 0) offset2 = 0;
			img.rcFrame.left = mRcFrame.right + offset2 + 10 + offset1;
			img.rcFrame.right = img.rcFrame.left + w;
		}
	}

	private CacheImage getLeftImage()
	{
		CacheImage cacheImage;
		if (mCurSel-1 >= 0 && !new File(mImages.get(mCurSel-1).getImagePath()).exists()) {
//			cacheImage(mCurSel - 2, null);
			cacheImage = getCacheImage(mCurSel - 2);
		} else {
			cacheImage = getCacheImage(mCurSel - 1);
		}
		return cacheImage;
	}

	private CacheImage getRightImage()
	{
		CacheImage cacheImage;
		if (mCurSel+1 < mImages.size() && !new File(mImages.get(mCurSel + 1).getImagePath()).exists()) {
//			cacheImage(mCurSel + 2, null);
			cacheImage = getCacheImage(mCurSel + 2);
		} else {
			cacheImage = getCacheImage(mCurSel + 1);
		}
		return cacheImage;
	}

	protected CacheImage getCurCacheImage()
	{
		return getCacheImage(mCurSel);
	}

	protected CacheImage getCacheImage(int index)
	{
		if(index >= 0 && index < mImages.size())
		{
			synchronized(mCachedImgs)
			{
				CacheImage img = null;
				for(int i = 0; i < mCachedImgs.size(); i++)
				{
					img = mCachedImgs.get(i);
					if(img.index == index)
					{
						return img;
					}
				}
			}
		}
		return null;
	}

	private CacheImage cacheImage(int index, Bitmap bmp)
	{
		if(index < 0 || index >= mImages.size())
		{
			return null;
		}
		CacheImage img = null;
		synchronized(mCachedImgs)
		{
			PhotoInfo imgInfo = mImages.get(index);
			for(int i = 0; i < mCachedImgs.size(); i++)
			{
				img = mCachedImgs.get(i);
				if(img.index == index)
				{
					break;
				}
				img = null;
			}
			if(img != null)
			{
				mCachedImgs.remove(img);
				mCachedImgs.add(img);
			}
			else
			{
				if(mCachedImgs.size() >= 3)
				{
					mCachedImgs.remove(getRemoveIndex(mCurSel));
				}
				img = new CacheImage();
				img.imgInfo = imgInfo;
				img.index = index;
				if(bmp != null)
				{
					img.bitmap = bmp;
					img.width = bmp.getWidth();
					img.height = bmp.getHeight();
					img.loaded = true;
				}
				if(mRcDefault != null)
				{
					img.rcFrame.set(mRcDefault);
				}
				mCachedImgs.add(img);
			}
		}
		startLoader();
		if(img != null && img.imgInfo.getImagePath().startsWith("http"))
		{
			startNetImgLoader();
		}
		return img;
	}

	private int getRemoveIndex(int index) {
		int removeIndex = 0;
		for (CacheImage cacheImage : mCachedImgs) {
			if (Math.abs(cacheImage.index - index) > 1) {
				break;
			} else {
				removeIndex++;
			}
		}

		if (removeIndex >= 3) {
			removeIndex = 0;
		}

		return removeIndex;
	}

	private boolean mStoping = false;
	private boolean mCaching = false;

	private void startLoader()
	{
		if(mCaching == false)
		{
			mCaching = true;
			new Thread(mCacheRunnable).start();
		}
	}

	public static Bitmap scaleBitmap(Bitmap bmp, int size)
	{
		Bitmap out = null;

		if(bmp != null)
		{
			if(bmp.getWidth() <= size && bmp.getHeight() <= size)
			{
				out = bmp;
			}
			else
			{
				out = MakeBmp.CreateBitmap(bmp, size, size, -1, 0, Config.ARGB_8888);
			}
		}

		return out;
	}

	private Runnable mCacheRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			while(!mStoping)
			{
				boolean finish = true;
				CacheImage info = null;
				synchronized(mCachedImgs)
				{
					for(CacheImage itemInfo : mCachedImgs)
					{
						if(itemInfo.loaded == false && (itemInfo.imgInfo.getImagePath().startsWith("http") == false))
						{
							info = itemInfo;
							finish = false;
							break;
						}
					}
				}
				if(finish == true)
				{
					//加载大图
					if(mCurImg != null && mCurImg.bitmap != null && (mCurImg.width > mMinSize || mCurImg.height > mMinSize))
					{
						String image = mCurImg.image;

						if(image != null && image.startsWith("http") == false && (image.endsWith(".gif") || image.endsWith(".GIF")) == false)
						{
							CacheImage img = mCurImg;
							img.bitmap = decodeBigImage(image, 0.4f);
							if(img.bitmap != null)
							{
								if(mCurImg == img)
								{
									mBmpW = mCurImg.bitmap.getWidth();
									mBmpH = mCurImg.bitmap.getHeight();
									mScale = (float)mRcFrameCache.width() / (float)mBmpW;
									mMaxScale = computeMaxScale(mBmpW, mBmpH);
									mPreScale = mScale;
									mPreOffset = mCurOffset;
									postInvalidate();
								}
								else
								{
									img.bitmap = scaleBitmap(img.bitmap, mMinSize);

								}
							}
							System.gc();
							synchronized(mCachedImgs)
							{
								for(CacheImage itemInfo : mCachedImgs)
								{
									if(itemInfo != null && itemInfo.loaded == false)
									{
										info = itemInfo;
										finish = false;
										break;
									}
								}
							}
						}
					}
					if(finish == true)
					{
						break;
					}
				}
				if(info != null)
				{
					if(info.imgInfo != null)
					{
						if(info.bitmap == null)
						{
							info.bitmap = decodeFile(info);
						}
						if(info.bitmap == null)
						{
							if(info.imgInfo.getImagePath().startsWith("http"))
							{
								info.pgsDrawer.SetText("下载图片失败");
							}
							else
							{
								File file = new File(info.image);
								if(info.imgInfo.getImagePath().startsWith(SysConfig.GetSDCardPath()) && !file.exists())
								{
									info.pgsDrawer.SetText("图片已被删除");
								}
								else
								{
									info.pgsDrawer.SetText("加载图片失败");
								}
							}
						}
						if(info.bitmap != null)
						{
							if(info.index == mCurSel)
							{
								mBmpW = info.bitmap.getWidth();
								mBmpH = info.bitmap.getHeight();
								resetSize(mRcFrame, info.width, info.height);
								resetRect(mRcFrameCache, info.width, info.height);
								mScale = (float)mRcFrameCache.width() / (float)mBmpW;
								mMaxScale = computeMaxScale(mBmpW, mBmpH);
								mRcReset.set(mRcFrameCache);
								startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART);

//								if((mCurImg.imgInfo.getImagePath().endsWith(".gif") || mCurImg.imgInfo.getImagePath().endsWith(".GIF")) &&
//										info.width <= 1000 && info.height <= 1000)
//								{
//									playGif(info.image);
//								}
							}
							else
							{
								resetRect(info.rcFrame, info.width, info.height);
							}
						}
						System.gc();
					}
					info.loaded = true;
					if(info.index == mCurSel)
					{
						postInvalidate();
					}
				}
			}
			mCaching = false;
		}
	};

	//private boolean mNetImgCaching = false;

	private void startNetImgLoader()
	{
		/*if(mNetImgCaching == false)
		{
			new Thread(mNetImgCacheRunnable).start();
			mNetImgCaching = true;
		}*/
	}

	/*private Runnable mNetImgCacheRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			while(!mStoping)
			{
				boolean finish = true;
				CacheImage info = null;
				synchronized(mCachedImgs)
				{
					for(CacheImage itemInfo : mCachedImgs)
					{
						if(itemInfo.loaded == false && itemInfo.imgInfo.getImagePath().startsWith("http"))
						{
							info = itemInfo;
							finish = false;
							break;
						}
					}
				}
				if(finish == true)
				{
					if(mCurImg != null && mCurImg.bitmap != null && (mCurImg.width > mMinSize || mCurImg.height > mMinSize))
					{
						String image = mCurImg.image;

						if(image != null && mCurImg.imgInfo.getImagePath().startsWith("http") &&
								(image.endsWith(".gif") || image.endsWith(".GIF")) == false)
						{
							CacheImage img = mCurImg;
							img.bitmap = decodeBigImage(image, 0.4f);
							if(img.bitmap != null)
							{
								if(mCurImg == img)
								{
									mBmpW = mCurImg.bitmap.getWidth();
									mBmpH = mCurImg.bitmap.getHeight();
									mScale = (float)mRcFrameCache.width() / (float)mBmpW;
									mMaxScale = computeMaxScale(mBmpW, mBmpH);
									mPreScale = mScale;
									mPreOffset = mCurOffset;
									postInvalidate();
								}
								else
								{
									img.bitmap = scaleBitmap(img.bitmap, mMinSize);
								}
							}
							System.gc();
							synchronized(mCachedImgs)
							{
								for(CacheImage itemInfo : mCachedImgs)
								{
									if(itemInfo != null && itemInfo.loaded == false)
									{
										info = itemInfo;
										finish = false;
										break;
									}
								}
							}
						}
					}
					if(finish == true)
					{
						break;
					}
				}
				if(info != null)
				{
					if(info.imgInfo != null)
					{
						if(info.bitmap == null)
						{
							info.bitmap = decodeFile(info);
						}
						if(info.bitmap == null)
						{
							if(info.imgInfo.getImagePath().startsWith("http"))
							{
								info.pgsDrawer.SetText("下载图片失败");
							}
							else
							{
								info.pgsDrawer.SetText("加载图片失败");
							}
						}
						if(info.bitmap != null)
						{
							if(info.index == mCurSel)
							{
								mBmpW = info.bitmap.getWidth();
								mBmpH = info.bitmap.getHeight();
								resetSize(mRcFrame, info.width, info.height);
								resetRect(mRcFrameCache, info.width, info.height);
								mScale = (float)mRcFrameCache.width() / (float)mBmpW;
								mMaxScale = computeMaxScale(mBmpW, mBmpH);
								mRcReset.set(mRcFrameCache);
								startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART);

								if((mCurImg.imgInfo.getImagePath().endsWith(".gif") || mCurImg.imgInfo.getImagePath().endsWith(".GIF")) &&
										info.width <= 1000 && info.height <= 1000)
								{
									playGif(info.image);
								}
							}
							else
							{
								resetRect(info.rcFrame, info.width, info.height);
							}
						}
						System.gc();
					}
					info.loaded = true;
					if(info.index == mCurSel)
					{
						postInvalidate();
					}
				}
			}
			mNetImgCaching = false;
		}
	};*/

	protected Bitmap decodeFile(final CacheImage img)
	{
		String image = img.imgInfo.getImagePath();
		if(image != null)
		{
			img.image = image;
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			cn.poco.imagecore.Utils.DecodeFile(image, opts, true);
			img.width = opts.outWidth;
			img.height = opts.outHeight;
			opts.inJustDecodeBounds = false;
			int max = opts.outHeight > opts.outWidth ? opts.outHeight : opts.outWidth;
			opts.inSampleSize = max / mMinSize;
			Bitmap bmp = BitmapFactory.decodeFile(image, opts);
			if(bmp != null)
			{
				if(opts.outMimeType == null || opts.outMimeType.equals("image/jpeg"))
				{
					int[] imei = CommonUtils.GetImgInfo(image);
					int rotation = imei[0];
					if(rotation != 0)
					{
						if(rotation % 180 != 0)
						{
							img.width = opts.outHeight;
							img.height = opts.outWidth;
						}
						Matrix m = new Matrix();
						m.setRotate(rotation);
						Bitmap bmpTemp = bmp;
						bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
						//bmpTemp.recycle();
						bmpTemp = null;
					}
				}
			}
			if(bmp != null)
			{
				Bitmap temp = bmp;
				bmp = scaleBitmap(bmp, mMinSize);
				if(temp != null && temp != bmp)
				{
					//temp.recycle();
					temp = null;
				}
			}
			if(bmp == null)
			{
				//System.out.println("decode " + image + " fail");
			}
			return bmp;
		}
		return null;
	}

	public Bitmap decodeBigImage(String file, float maxMem)
	{
		int maxMemory = (int)(Runtime.getRuntime().maxMemory() * maxMem);
		int maxSize = (int)(Math.sqrt(maxMemory / 4) / 2);

		int limitSize = mMaxBitmapWidth < mMaxBitmapHeight ? mMaxBitmapWidth : mMaxBitmapHeight;
		if(limitSize > 0)
		{
			if(maxSize > limitSize)
			{
				maxSize = limitSize;
			}
		}

//		System.out.println("maxSize:" + maxSize);

		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		opt.inPreferredConfig = Config.RGB_565;
		BitmapFactory.decodeFile(file, opt);
		opt.inJustDecodeBounds = false;

		int bigOne = opt.outWidth > opt.outHeight ? opt.outWidth : opt.outHeight;
		int sampleSize = bigOne / maxSize;
		if(sampleSize == 0)
		{
			sampleSize = 1;
		}
		opt.inSampleSize = sampleSize;
		Bitmap bitmap = null;
		for(int i = 0; i < 4; i++)
		{
			try
			{
				bitmap = BitmapFactory.decodeFile(file, opt);
				break;
			}
			catch(OutOfMemoryError e)
			{
				opt.inSampleSize++;
			}
		}
		if(bitmap != null)
		{
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
//			System.out.println("bitmap width:" + w);
//			System.out.println("bitmap height:" + h);
			int big = w > h ? w : h;
			if(limitSize > 0 && big > limitSize)
			{
				bitmap = scaleBitmap(bitmap, limitSize);
			}
		}
		if(bitmap != null)
		{
			if(opt.outMimeType == null || opt.outMimeType.equals("image/jpeg"))
			{
				int[] imei = CommonUtils.GetImgInfo(file);
				int rotation = imei[0];
				if(rotation != 0)
				{
					Matrix m = new Matrix();
					m.setRotate(rotation);
					Bitmap bmpTemp = bitmap;
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
					//bmpTemp.recycle();
					bmpTemp = null;
				}
			}
		}
		return bitmap;
	}

	/*public static int getJpgRotation(String img)
	{
		if(img == null) return 0;
		ExifInterface exif;
		try
		{
			exif = new ExifInterface(img);
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch(ori)
			{
				case ExifInterface.ORIENTATION_ROTATE_90:
					return 90;
				case ExifInterface.ORIENTATION_ROTATE_180:
					return 180;
				case ExifInterface.ORIENTATION_ROTATE_270:
					return 270;
			}
		}
		catch(Exception e)
		{
		}
		return 0;
	}
	private Bitmap decodeUrlImage(String url)
	{
		InputStream inputStream = null;
		Bitmap bmp = null;
		try
		{
			URL _url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)_url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10*1000);
			conn.setReadTimeout(20*1000);
			conn.setDoOutput(true);
			conn.connect();
			if (conn.getResponseCode() == 200)
			{
				inputStream = conn.getInputStream();
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
		        BitmapFactory.decodeStream(inputStream, null, opts);
		        opts.inJustDecodeBounds = false;
				int max = opts.outHeight > opts.outWidth ? opts.outHeight : opts.outWidth;
				opts.inSampleSize = max / mMinSize;
				bmp = BitmapFactory.decodeStream(inputStream, null, opts);
				inputStream.close();
			}
			conn.disconnect();
		} catch (Exception e)
		{
			if(inputStream != null)
			{
				try{
					inputStream.close();
				} 
				catch (IOException e1) {
				}
				inputStream = null;
			}
		}
		return bmp;
	}*/

	private int mW = 0;
	private int mH = 0;
	private Bitmap mSelBmp = null;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		if(w > 0 && h > 0)
		{
			stopTweener();
			mW = w;
			mH = h;
			mScaleCenXBase = 0.5f;
			mScaleCenYBase = 0.5f;
			//
			int h1 = (int)(w * 0.7);
			if(h1 > h * 0.7)
			{
				h1 = (int)(h * 0.7);
			}
			int top = (h - h1) / 2;
			mRcDefault = new RectF(0, top, w, top + h1);

			setSel(mCurSel, mSelBmp);
			mSelBmp = null;
		}
	}

	private int mBmpW = 0;
	private int mBmpH = 0;

	private CacheImage mCurImg = null;
	private Paint mPaint = new Paint();
	private RectF mRcFrame = new RectF();
	private RectF mRcFrameCache = new RectF();
	private RectF mRcReset = new RectF();
	private float mScale = 1.0f;
	protected int mMaxBitmapWidth = 0;
	protected int mMaxBitmapHeight = 0;
	private boolean mInitializedBitmapSize = false;
	private PaintFlagsDrawFilter mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.save();
		if(mInitializedBitmapSize == false)
		{
			mInitializedBitmapSize = true;
			initCanvasMaxBitmapSize(canvas);
		}
		if(mW == 0 || mH == 0 || mCurImg == null) return;
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xff000000);
		canvas.setDrawFilter(mDrawFilter);
		if(mCurImg.bitmap != null)
		{
			canvas.drawBitmap(mCurImg.bitmap, null, mCurImg.rcFrame, mPaint);
		}
		if(mRcFrame.left > 0)
		{
			CacheImage img = getLeftImage();
			if(img != null)
			{
				float w = img.rcFrame.width();
				float offset1 = (mW - w) / 2;
				if(offset1 < 0) offset1 = 0;
				float offset2 = (mW - mRcFrame.width()) / 2;
				if(offset2 < 0) offset2 = 0;
				img.rcFrame.right = mRcFrame.left - offset2 - 10 - offset1;
				img.rcFrame.left = img.rcFrame.right - w;
				if(img.bitmap != null)
				{
					canvas.drawBitmap(img.bitmap, null, img.rcFrame, mPaint);
				}
			}
		}
		if(mRcFrame.right < mW)
		{
			CacheImage img = getRightImage();
			if(img != null)
			{
				float w = img.rcFrame.width();
				float offset1 = (mW - w) / 2;
				if(offset1 < 0) offset1 = 0;
				float offset2 = (mW - mRcFrame.width()) / 2;
				if(offset2 < 0) offset2 = 0;
				img.rcFrame.left = mRcFrame.right + offset2 + 10 + offset1;
				img.rcFrame.right = img.rcFrame.left + w;
				if(img.bitmap != null)
				{
					canvas.drawBitmap(img.bitmap, null, img.rcFrame, mPaint);
				}
			}
		}
		if(mCurImg.bitmap == null)
		{
			if(!mCurImg.loaded)
			{
				mCurImg.pgsDrawer.SetText("加载中...");
			}
			else
			{
				mCurImg.pgsDrawer.SetText("图片解析失败");
			}
			mCurImg.pgsDrawer.draw(canvas, (int)(mCurImg.rcFrame.left + mCurImg.rcFrame.width() / 2), (int)(mCurImg.rcFrame.top + mCurImg.rcFrame.height() / 2));
		}
		canvas.restore();
	}

	private void initCanvasMaxBitmapSize(Canvas canvas)
	{
		try
		{
			int w = 0;
			int h = 0;
			Method getMaximumBitmapHeight = Canvas.class.getMethod("getMaximumBitmapHeight");
			Method getMaximumBitmapWidth = Canvas.class.getMethod("getMaximumBitmapWidth");
			if(getMaximumBitmapHeight != null)
			{
				Object objh = getMaximumBitmapHeight.invoke(canvas, (Object[])null);
				if(objh != null)
				{
					h = (Integer)objh;
				}
			}
			if(getMaximumBitmapWidth != null)
			{
				Object objw = getMaximumBitmapWidth.invoke(canvas, (Object[])null);
				if(objw != null)
				{
					w = (Integer)objw;
				}
			}
			mMaxBitmapWidth = w;
			mMaxBitmapHeight = h;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private float mPreX = 0;
	private float mPreY = 0;
	private float mPreOffset = 0;
	private float mCurOffset = 0;
	private float mPreScale = 0;
	private float mScaleCenXBase;
	private float mScaleCenYBase;
	private RectF mScalePreRc = new RectF();
	private boolean mInflating = false;
	private boolean mMoving = false;
	private boolean mDown = false;
	private long mPreTime = 0;
	private long mTimeDoubleClick = 0;
	private long mCountDoubleClick = 0;
	private VelocityTracker mVTracker = null;
	private int mMoveSpace = getRealPixel2(10);

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if(mW == 0 || mH == 0 || mCurImg == null) return super.dispatchTouchEvent(event);
		if(mVTracker == null)
		{
			mVTracker = VelocityTracker.obtain();
		}
		mVTracker.addMovement(event);
		int action = event.getAction();
		float x = (int)event.getX();
		float y = (int)event.getY();
		if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_1_UP || action == MotionEvent.ACTION_POINTER_2_UP || action == MotionEvent.ACTION_CANCEL)
		{
			boolean handled = false;
			if(mMoving == true)
			{
				long timeOffset = System.currentTimeMillis() - mPreTime;
				mPreTime = System.currentTimeMillis();
				float distancex = x - mPreX;
				int index;
				if(Math.abs(distancex) > mW * 0.06)
				{
					if(timeOffset < 200)
					{
						if((mRcFrame.width() <= mW && mRcFrame.height() <= mH) || (mRcFrameCache.right <= mW && distancex < 0) || (mRcFrameCache.left >= 0 && distancex > 0))
						{
							if(distancex > 0)
							{
								index = mCurSel - 1;
								while (index >= 0 && !new File(mImages.get(index).getImagePath()).exists()) {
									index--;
								}
								if(index >= 0)
								{
									prev(index);
									handled = true;
								}
								else
								{
									if(mShowNoMoreTips)
									{
										showToast("已经没有了");
									}
								}
							}
							else
							{
								index = mCurSel + 1;
								while (index < mImages.size() && !new File(mImages.get(index).getImagePath()).exists()) {
									index++;
								}
								if(index < mImages.size())
								{
									next(index);
									handled = true;
								}
								else
								{
									if(mShowNoMoreTips)
									{
										showToast("已经没有了");
									}
								}
							}
						}
					}
					else
					{
						if(mRcFrame.right < mW / 2)
						{
							index = mCurSel + 1;
							while (index < mImages.size() && !new File(mImages.get(index).getImagePath()).exists()) {
								index++;
							}
							if(index < mImages.size())
							{
								next(index);
								handled = true;
							}
							else
							{
								if(mShowNoMoreTips)
								{
									showToast("已经没有了");
								}
							}
						}
						else if(mRcFrame.left >= mW / 2)
						{
							index = mCurSel - 1;
							while (index >= 0 && !new File(mImages.get(index).getImagePath()).exists()) {
								index--;
							}
							if(index >= 0)
							{
								prev(index);
								handled = true;
							}
							else
							{
								if(mShowNoMoreTips)
								{
									showToast("已经没有了");
								}
							}
						}
					}
				}
				if(handled == false)
				{
					mVTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
					int appendx = (int)mVTracker.getXVelocity() / 4;
					int appendy = (int)mVTracker.getYVelocity() / 4;
					if(mRcFrameCache.height() < mH) appendy = 0;
					if(Math.max(Math.abs(appendx), Math.abs(appendy)) > mScaledMinimumFlingVelocity)
					{
						/*appendx -= mScaledMinimumFlingVelocity;
						appendy -= mScaledMinimumFlingVelocity;
						if(appendx < 0) appendx = 0;
						if(appendy < 0) appendy = 0;*/
						mRcFrameCache.offset(appendx, appendy);
					}
					int type = TweenLite.EASE_OUT | TweenLite.EASING_QUART;
					if(mRcFrameCache.right < mW || mRcFrameCache.left > 0 || mRcFrameCache.top > 0 || mRcFrameCache.bottom < mH)
					{
						type = TweenLite.EASE_OUT | TweenLite.EASING_BACK;
					}
					adjustRectY(mRcFrameCache);
					adjustRectX(mRcFrameCache);
					startTweener(mRcFrameCache, type);
				}
				else
				{
					mCountDoubleClick = 0;
				}
			}
			if(mDown == true)
			{
				mCountDoubleClick++;
				if(mCountDoubleClick >= 2)
				{
					mCountDoubleClick = 0;
					if(System.currentTimeMillis() - mTimeDoubleClick < 500)
					{
						mHandler.removeCallbacks(mOnClickRunnable);
						boolean zoomIn = false;
						float scale = 1;
						if(mRcFrame.width() <= mW && mRcFrame.height() <= mH)
						{
							zoomIn = true;
							scale = (float)(mRcFrame.width() * 3) / (float)mBmpW;
							if(scale > mMaxScale)
							{
								scale = mMaxScale;
							}
						}
						if(zoomIn)
						{
							mScale = scale;

							x = x - mRcFrame.left;
							y = y - mRcFrame.top;
							mScaleCenXBase = (float)x / (float)mRcFrame.width();
							mScaleCenYBase = (float)y / (float)mRcFrame.height();

							int w = (int)(mScale * mBmpW);
							int h = (int)(mScale * mBmpH);
							mRcFrameCache.left = mRcFrame.left + (int)((mRcFrame.width() - w) * mScaleCenXBase);
							mRcFrameCache.right = mRcFrameCache.left + w;
							mRcFrameCache.top = mRcFrame.top + (int)((mRcFrame.height() - h) * mScaleCenYBase);
							mRcFrameCache.bottom = mRcFrameCache.top + h;

							adjustRectY(mRcFrameCache);
							adjustRectX(mRcFrameCache);

							startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART, 500, null);
						}
						else
						{
							resetRect(mRcFrameCache, mBmpW, mBmpH);
							mScale = (float)mRcFrameCache.width() / (float)mBmpW;
							startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART, 500, null);
						}
					}
				}
				else
				{
					if(System.currentTimeMillis() - mTimeDoubleClick < 500)
					{
						if(handled == false)
						{
							mHandler.postDelayed(mOnClickRunnable, 200);
						}
					}
				}
			}
			if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
			{
				float mDisX = event.getX() - mPreX;
				float mDisY = event.getY() - mPreX;
				if(Math.abs(mDisX) < Math.abs(mDisY))
				{
					if(mOnScrollListener != null)
					{
						mOnScrollListener.OnUP(mDisX / OFFSET_RATIO, mDisY / OFFSET_RATIO);
					}
				}
				if(mRcFrameCache.width() < mRcReset.width() || mRcFrameCache.height() < mRcReset.height())
				{
					mRcFrameCache.set(mRcReset);
					mScale = (float)mRcReset.width() / (float)mBmpW;
					startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART);
				}
				else if(mScale > mMaxScale)
				{
					mScale = mMaxScale;
					int w = (int)(mScale * mBmpW);
					int h = (int)(mScale * mBmpH);
					mRcFrameCache.left = mScalePreRc.left + (int)((mScalePreRc.width() - w) * mScaleCenXBase);
					mRcFrameCache.right = mRcFrameCache.left + w;
					mRcFrameCache.top = mScalePreRc.top + (int)((mScalePreRc.height() - h) * mScaleCenYBase);
					mRcFrameCache.bottom = mRcFrameCache.top + h;

					adjustRectY(mRcFrameCache);

					startTweener(mRcFrameCache, TweenLite.EASE_OUT | TweenLite.EASING_QUART, 500, null);
				}
			}
			mInflating = false;
			mMoving = false;
			invalidate();
			mDown = false;
			if(mVTracker != null)
			{
				//mVTracker.recycle();
				mVTracker = null;
			}
		}
		else if(action == MotionEvent.ACTION_DOWN)
		{
			invalidate();
			mInflating = false;
			mMoving = false;
			mDown = true;
			mPreX = x;
			mPreY = y;
			mHandler.removeCallbacks(mOnClickRunnable);
			if(mCountDoubleClick == 0 || System.currentTimeMillis() - mTimeDoubleClick >= 500)
			{
				mCountDoubleClick = 0;
				mTimeDoubleClick = System.currentTimeMillis();
			}
			mHandler.removeCallbacks(mOnLongClickRunnable);
			mHandler.postDelayed(mOnLongClickRunnable, 1000);
		}
		else if(action == MotionEvent.ACTION_POINTER_1_DOWN || action == MotionEvent.ACTION_POINTER_2_DOWN)
		{
			invalidate();
		}
		else if(action == MotionEvent.ACTION_MOVE)
		{
			mVTracker.addMovement(event);
			stopTweener();
			int pc = event.getPointerCount();
			if(pc == 1)
			{
				mInflating = false;
				float mDisX = x - mPreX;
				float mDisY = y - mPreY;
				if(Math.abs(mDisX) < 20 && Math.abs(mDisY) > 50 && Math.abs(mDisX) < Math.abs(mDisY))
				{
					if(mOnScrollListener != null)
					{
						mOnScrollListener.OnScroll(mDisX / OFFSET_RATIO, mDisY / OFFSET_RATIO);
					}
					return super.dispatchTouchEvent(event);
				}
				if(mMoving == true)
				{
					float offsetx = x - mPreX;
					float offsety = y - mPreY;
					if(Math.abs(offsetx) > mMoveSpace || Math.abs(offsety) > mMoveSpace)
					{
						mDown = false;
						mCountDoubleClick = 0;
					}
					//mPreTime = System.currentTimeMillis();

					//if(mCurImg.width > mW || mCurImg.height > mH)
					{
						mRcFrameCache.left = mScalePreRc.left + offsetx;
						mRcFrameCache.right = mScalePreRc.right + offsetx;
						mRcFrameCache.top = mScalePreRc.top + offsety;
						mRcFrameCache.bottom = mScalePreRc.bottom + offsety;

						if(mRcFrameCache.height() > mH)
						{
							if(mRcFrameCache.top >= mH * 0.1f)
							{
								mRcFrameCache.top = mH * 0.1f;
								mRcFrameCache.bottom = mRcFrameCache.top + mRcFrame.height();
							}
							if(mRcFrameCache.bottom <= mH - mH * 0.1f)
							{
								mRcFrameCache.bottom = mH - mH * 0.1f;
								mRcFrameCache.top = mRcFrameCache.bottom - mRcFrame.height();
							}
						}
						else
						{
							adjustRectY(mRcFrameCache);
						}
						mRcFrame.set(mRcFrameCache);
					}
				}
				if(mMoving == false)
				{
					float offsetx = x - mPreX;
					float offsety = y - mPreY;
					if(Math.abs(offsetx) > mMoveSpace || Math.abs(offsety) > mMoveSpace)
					{
						mDown = false;
						mCountDoubleClick = 0;
					}
					mPreX = x;
					mPreY = y;
					mMoving = true;
					mScalePreRc.set(mRcFrame);
					mPreTime = System.currentTimeMillis();
				}
			}
			else/* if(mCurImg.width > mW || mCurImg.height > mH)*/
			{
				mDown = false;
				mMoving = false;
				float x1 = (int)event.getX(0);
				float y1 = (int)event.getY(0);
				float x2 = (int)event.getX(1);
				float y2 = (int)event.getY(1);
				float xoffset = Math.abs(x1 - x2);
				float yoffset = Math.abs(y1 - y2);
				mCurOffset = xoffset > yoffset ? xoffset : yoffset;

				if(mInflating == true)
				{
					mScale = mPreScale * (float)mCurOffset / (float)mPreOffset;
					int w = (int)(mScale * mBmpW);
					int h = (int)(mScale * mBmpH);
					mRcFrameCache.left = mScalePreRc.left + (int)((mScalePreRc.width() - w) * mScaleCenXBase);
					mRcFrameCache.right = mRcFrameCache.left + w;
					mRcFrameCache.top = mScalePreRc.top + (int)((mScalePreRc.height() - h) * mScaleCenYBase);
					mRcFrameCache.bottom = mRcFrameCache.top + h;

					adjustRectY(mRcFrameCache);

					mRcFrame.set(mRcFrameCache);
				}
				if(mInflating == false)
				{
					mInflating = true;
					mPreOffset = mCurOffset;
					mPreScale = mScale;
					x1 = x1 < x2 ? x1 : x2;
					y1 = y1 < y2 ? y1 : y2;
					x = x1 + xoffset / 2 - mRcFrame.left;
					y = y1 + yoffset / 2 - mRcFrame.top;
					mScaleCenXBase = (float)x / (float)mRcFrame.width();
					mScaleCenYBase = (float)y / (float)mRcFrame.height();
					mScalePreRc.set(mRcFrame);
				}
			}
			invalidate();
		}
		return super.dispatchTouchEvent(event);
	}

	private Runnable mOnClickRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			if(mOnClickListener != null)
			{
				mOnClickListener.onClick(ImageViewer.this);
			}
		}
	};

	private Runnable mOnLongClickRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			if(mOnLongClickListener != null && mDown == true)
			{
				mOnLongClickListener.onLongClick(ImageViewer.this);
			}
		}
	};

	private void resetRect(RectF rc, int bmW, int bmH)
	{
		bmW = bmW * ShareData.m_screenWidth / 240;
		bmH = bmH * ShareData.m_screenWidth / 240;
		int realH = mH - m_topMargin;
		float r1 = (float)bmW / (float)bmH;
		float r2 = (float)mW / (float)realH;
		int w = 0;
		int h = 0;
		if(bmW < mW && bmH < realH)
		{
			w = bmW;
			h = bmH;
		}
		else
		{
			if(r1 > r2)
			{
				w = mW;
				h = (int)(mW / r1);
			}
			else
			{
				h = realH;
				w = (int)(realH * r1);
			}
		}
		rc.left = (mW - w) / 2;
		rc.right = rc.left + w;
		rc.top = m_topMargin + (realH - h) / 2;
		rc.bottom = rc.top + h;
	}

	private void resetSize(RectF rc, int bmW, int bmH)
	{
		bmW = bmW * ShareData.m_screenWidth / 240;
		bmH = bmH * ShareData.m_screenWidth / 240;
		float r1 = (float)bmW / (float)bmH;
		float r2 = (float)mW / (float)mH;
		int w = 0;
		int h = 0;
		if(bmW < mW && bmH < mH)
		{
			w = bmW;
			h = bmH;
		}
		else
		{
			if(r1 > r2)
			{
				w = mW;
				h = (int)(mW / r1);
			}
			else
			{
				h = mH;
				w = (int)(mH * r1);
			}
		}
		rc.right = rc.left + w;
		rc.top = (mH - h) / 2;
		rc.bottom = rc.top + h;
	}

	private void adjustRectY(RectF rc)
	{
		float h = rc.height();
		if(rc.height() < mH)
		{
			rc.top = (mH - h) / 2;
			rc.bottom = rc.top + h;
		}
		else
		{
			if(rc.top > 0)
			{
				rc.top = 0;
				rc.bottom = rc.top + h;
			}
			if(rc.bottom < mH)
			{
				rc.bottom = mH;
				rc.top = rc.bottom - h;
			}
		}
	}

	private void adjustRectX(RectF rc)
	{
		float w = rc.width();
		if(rc.width() < mW)
		{
			rc.left = (mW - w) / 2;
			rc.right = rc.left + w;
		}
		else
		{
			if(rc.left > 0)
			{
				rc.left = 0;
				rc.right = rc.left + w;
			}
			if(rc.right < mW)
			{
				rc.right = mW;
				rc.left = rc.right - w;
			}
		}
	}

	private TweenLite mTweenX1 = new TweenLite();
	private TweenLite mTweenY1 = new TweenLite();
	private TweenLite mTweenX2 = new TweenLite();
	private TweenLite mTweenY2 = new TweenLite();
	private boolean mTweenWorking = false;
	private Runnable mTweenerCallback = null;
	private Handler mHandler = new Handler();

	private void startTweener(RectF rect, int type)
	{
		float w = Math.abs(mRcFrame.left - rect.left);
		float h = Math.abs(mRcFrame.top - rect.top);
		int time = (int)Math.sqrt(w * w + h * h);
		if(time < 300) time = 300;
		if(time > 2000) time = 2000;
		startTweener(rect, type, time, null);
	}

	private void startTweener(RectF rect, int type, int time, Runnable cb)
	{
		mTweenerCallback = cb;
		mTweenX1.M1End();
		mTweenY1.M1End();
		mTweenX2.M1End();
		mTweenY2.M1End();
		mTweenX1.Init(mRcFrame.left, rect.left, time);
		mTweenY1.Init(mRcFrame.top, rect.top, time);
		mTweenX2.Init(mRcFrame.right, rect.right, time);
		mTweenY2.Init(mRcFrame.bottom, rect.bottom, time);
		mTweenX1.M1Start(type);
		mTweenY1.M1Start(type);
		mTweenX2.M1Start(type);
		mTweenY2.M1Start(type);
		if(mTweenWorking == false)
		{
			new Thread(mTweenRunnable).start();
			mTweenWorking = true;
		}
	}

	private void stopTweener()
	{
		mTweenX1.M1End();
		mTweenY1.M1End();
		mTweenX2.M1End();
		mTweenY2.M1End();
	}

	private Runnable mTweenRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			while(mTweenX1.M1IsFinish() == false && mTweenY1.M1IsFinish() == false && mTweenX2.M1IsFinish() == false && mTweenY2.M1IsFinish() == false)
			{
				mRcFrame.left = mTweenX1.M1GetPos();
				mRcFrame.top = mTweenY1.M1GetPos();
				mRcFrame.right = mTweenX2.M1GetPos();
				mRcFrame.bottom = mTweenY2.M1GetPos();
				postInvalidate();
				try
				{
					Thread.sleep(17);
				}
				catch(InterruptedException e)
				{
					break;
				}
			}
			mTweenWorking = false;
			if(mTweenerCallback != null)
			{
				mHandler.post(mTweenerCallback);
			}
		}
	};

	private Toast mToast1;
	private void showToast(String msg)
	{
		/*if(mToast1 == null)
		{
			mToast1 = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
			mToast1.setGravity(Gravity.CENTER, 0, 0);
		}
		mToast1.show();*/
	}

	//GIF播放支持
//	private GifDecoder mGifDecoder;
//
//	private void playGif(String img)
//	{
//		if(mLoadListener != null)
//		{
//			mHandler.post(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					if(mLoadListener != null)
//					{
//						mLoadListener.onStart(null);
//					}
//				}
//			});
//		}
//		if(mGifDecoder != null)
//		{
//			mGifDecoder.free();
//			mGifDecoder = null;
//		}
//		try
//		{
//			File file = new File(img);
//			if(file == null || !file.exists()) return;
//			FileInputStream fis = new FileInputStream(file);
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			byte[] buffer = new byte[10240];
//			int len = -1;
//			while((len = fis.read(buffer, 0, 10240)) != -1)
//			{
//				bos.write(buffer, 0, len);
//			}
//			fis.close();
//			GifParseListener gifAction = new GifParseListener();
//			mGifDecoder = new GifDecoder(bos.toByteArray(), gifAction);
//			mGifDecoder.setDataType(GifDecoder.FORMAT_JPG);
//			mGifDecoder.start();
//			gifAction.gifDecoder = mGifDecoder;
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	private void stopGif()
//	{
//		if(mGifDecoder != null)
//		{
//			mGifDecoder.free();
//			mGifDecoder = null;
//		}
//	}
//
//	private class GifParseListener implements GifAction
//	{
//		public GifDecoder gifDecoder = null;
//
//		@Override
//		public void parseOk(final boolean parseStatus, int frameIndex)
//		{
//			if(gifDecoder != mGifDecoder)
//			{
//				return;
//			}
//
//			if(mLoadListener != null && frameIndex == -1)
//			{
//				mHandler.post(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						if(mLoadListener != null)
//						{
//							mLoadListener.onEnd(null, parseStatus);
//						}
//					}
//				});
//			}
//			if(parseStatus == true)
//			{
//				startPlayGif();
//			}
//			else
//			{
//				stopGif();
//			}
//		}
//	}
//
//	private void startPlayGif()
//	{
//		if(mGifPlaying == false)
//		{
//			new Thread(mGifPlayRunnable).start();
//			mGifPlaying = true;
//		}
//	}
//
//	private boolean mGifPlaying = false;
//	private Runnable mGifPlayRunnable = new Runnable()
//	{
//
//		@Override
//		public void run()
//		{
//			if(mGifDecoder == null)
//			{
//				mGifPlaying = false;
//				return;
//			}
//			while(!mStoping)
//			{
//				if(mGifDecoder != null)
//				{
//					if(!mGifDecoder.isAlive())
//					{
//						GifDecoderFrame frame = mGifDecoder.next();
//						if(frame != null)
//						{
//							mCurImg.bitmap = frame.getImage();
//							if(mCurImg.bitmap != null)
//							{
//								postInvalidate();
//							}
//							SystemClock.sleep(frame.delay);
//						}
//						else
//						{
//							SystemClock.sleep(10);
//						}
//					}
//					else
//					{
//						SystemClock.sleep(10);
//					}
//				}
//				else
//				{
//					break;
//				}
//			}
//			mGifPlaying = false;
//		}
//	};

	private static class ProgressDrawer
	{
		protected int TEXT_SIZE;
		protected int TEXT_COLOR;

		protected Paint mPaint;
		protected String mText;
		protected float mTextW;

		public ProgressDrawer(Context context)
		{
			ShareData.InitData(context);

			TEXT_SIZE = ShareData.PxToDpi_xhdpi(28);
			TEXT_COLOR = ImageUtils.GetSkinColor(0xffe75988);

			mPaint = new Paint();
			mPaint.setTextSize(TEXT_SIZE);
			mPaint.setColor(TEXT_COLOR);
			mPaint.setAntiAlias(true);
			mPaint.setTextAlign(Paint.Align.CENTER);
		}

		public void SetText(String text)
		{
			mText = text;
			if(mText != null)
			{
				mTextW = mPaint.measureText(mText);
			}
			else
			{
				mTextW = 0;
			}
		}

		protected void draw(Canvas canvas, int x, int y)
		{
			if(mText != null)
			{
				canvas.drawText(mText, x, y, mPaint);
			}
		}
	}

	public static int getRealPixel2(int pxSrc)
	{
		return (int)(pxSrc * ShareData.m_screenWidth / 480);
	}
}

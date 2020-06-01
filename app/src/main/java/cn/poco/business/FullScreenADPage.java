package cn.poco.business;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.adnonstop.admasterlibs.data.AbsFullscreenAdRes;

import java.util.HashMap;

import cn.poco.banner.BannerCore3;
import cn.poco.business.site.FullScreenDisplayPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.home4.introAnimation.Config;
import cn.poco.home.site.HomePageSite;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;


/**
 * Created by lgd on 2016/11/24.
 */

public class FullScreenADPage extends IPage
{
	protected FullScreenDisplayPageSite m_site;
	private boolean mUiEnabled = true;
	private Path mPath;
	private int mScreenCenterX;
	private int mScreenCenterY;
	private int mCenterX;
	private int mCenterY;
	private int RADIUS;
	private int mRadius;
	private int mStartX;
	private int mStartY;
	private int mOffsetY;                //广告按钮到屏幕中心点的距离
	private ValueAnimator openPageAnimator;
	private ValueAnimator closePageAnimator;
	private final static int DURATION = 500;
	private final static float MIN_SCALE = 0.85f;
//	private View mBackBtn;
	private ImageView mBackBtn;
	private ADImageView mAdImageView;
	private AbsFullscreenAdRes m_res;

	private boolean isDoingAmn = false;
	public FullScreenADPage(Context context, BaseSite site)
	{
		super(context, site);
		m_site = (FullScreenDisplayPageSite)site;
		TongJiUtils.onPageStart(getContext(), R.string.全屏广告);
		initDate();
		initUI();
	}

	public void SetData(HashMap<String, Object> params)
	{
		if(params != null)
		{
			m_res = (AbsFullscreenAdRes)params.get(HomePageSite.BUSINESS_KEY);
		}
		if(m_res != null)
		{
			if(m_res.mPageAdm != null && m_res.mPageAdm.length > 0)
			{
				Bitmap bmp = cn.poco.imagecore.Utils.DecodeShowImage((Activity)getContext(), m_res.mPageAdm[0], 0, -1, MakeBmpV2.FLIP_NONE);
				if(bmp != null)
				{
					mAdImageView.setBitmap(bmp);
				}
			}
		}
	}

	@Override
	public void onBack()
	{
		m_site.OnBack(getContext());
	}


	private void initDate()
	{
		this.setClickable(true);
		mPath = new Path();
		mScreenCenterX = ShareData.m_screenWidth/2;
		mScreenCenterY = ShareData.m_screenHeight/2;
		RADIUS = (int)Math.sqrt(mScreenCenterX * mScreenCenterX + mScreenCenterY * mScreenCenterY);
//		mOffsetY = PercentUtil.HeightPxToPercent(46);
		mOffsetY = Config.AD_CENTER_BOTTOM_MARGIN;
		mStartX = mScreenCenterX;
		mStartY = mScreenCenterY - mOffsetY;
		mCenterX = mStartX;
		mCenterY = mStartY;
		openPageAnimator = ValueAnimator.ofInt(0, RADIUS);
		openPageAnimator.setDuration(DURATION);
		openPageAnimator.addUpdateListener(updateListener);
		openPageAnimator.setInterpolator(new DecelerateInterpolator());
		openPageAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				isDoingAmn = false;
				invalidate();
				mUiEnabled = true;
				if(onOpenPageCallback !=null){
					onOpenPageCallback.onOpenEnd();
				}
			}

			@Override
			public void onAnimationStart(Animator animation)
			{
				isDoingAmn = true;
				setWillNotDraw(false);
				FullScreenADPage.this.setScaleX(MIN_SCALE);
				FullScreenADPage.this.setScaleY(MIN_SCALE);
				FullScreenADPage.this.setAlpha(0f);
				mUiEnabled = false;
				if(onOpenPageCallback !=null){
					onOpenPageCallback.onOpenStart();
				}
			}
		});

		closePageAnimator = ValueAnimator.ofInt(RADIUS, 0);
		closePageAnimator.setDuration(DURATION);
		closePageAnimator.addUpdateListener(updateListener);
		closePageAnimator.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				isDoingAmn = false;
				invalidate();
				mUiEnabled = true;
				FullScreenADPage.this.clearAnimation();
				if(onClosePageCallBack !=null){
					onClosePageCallBack.onCloseEnd();
				}
			}

			@Override
			public void onAnimationStart(Animator animation)
			{
				isDoingAmn = true;
				mUiEnabled = false;
				if(onClosePageCallBack !=null){
					onClosePageCallBack.onCloseStart();
				}
			}
		});

	}

	private void initUI()
	{
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mAdImageView = new ADImageView(getContext());
//		mAdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mAdImageView.setOnClickListener(onClickListener);
		addView(mAdImageView, params);

		 params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		params.leftMargin = ShareData.PxToDpi_xhdpi(20);
//		params.topMargin = ShareData.PxToDpi_xhdpi(20);
		mBackBtn = new ImageView(getContext());
		mBackBtn.setPadding(ShareData.PxToDpi_xhdpi(25),ShareData.PxToDpi_xhdpi(25),ShareData.PxToDpi_xhdpi(25),ShareData.PxToDpi_xhdpi(25));
		mBackBtn.setImageResource(R.drawable.business_btn_back);
		mBackBtn.setOnClickListener(onClickListener);
		addView(mBackBtn, params);

//		mBackBtn = new View(getContext());
//		params = new LayoutParams(ShareData.PxToDpi_xxhdpi(170), ShareData.PxToDpi_xxhdpi(170));
//		params.gravity = Gravity.LEFT | Gravity.TOP;
//		this.addView(mBackBtn,params);
//		mBackBtn.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(mUiEnabled)
			{
				if(v == mBackBtn){
					m_site.OnBack(getContext());
				}else if(v == mAdImageView){
					String url = null;
					if(m_res != null)
					{
						url = m_res.mPageClick;
					}
					BannerCore3.ExecuteCommand(getContext(),url,new HomePageSite.CmdProc());
				}
			}
		}
	};

	private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener()
	{
		@Override
		public void onAnimationUpdate(ValueAnimator animation)
		{
			int val = (int)animation.getAnimatedValue();
			float percent = animation.getAnimatedFraction();
			mRadius = val;
			mCenterY = (int) (mStartY + percent * mOffsetY);         //Y向按钮中心偏移
			float alpha = (float)(1.0* mRadius / RADIUS);
			float scale = (MIN_SCALE +(mRadius)*(1-MIN_SCALE)/ RADIUS);
			if(onOpenPageCallback !=null){
				onOpenPageCallback.onPageFade(alpha);
			}
			if(onClosePageCallBack !=null){
				onClosePageCallBack.onPageFade(alpha);
			}
			FullScreenADPage.this.setScaleX(scale);
			FullScreenADPage.this.setScaleY(scale);
			FullScreenADPage.this.setAlpha(alpha);
			FullScreenADPage.this.invalidate();
		}
	};

	@Override
	public void onClose()
	{
		TongJiUtils.onPageEnd(getContext(), R.string.全屏广告);
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.全屏广告);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.全屏广告);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mScreenCenterX = w/2;
		mScreenCenterY = h/2;
		RADIUS = (int)Math.sqrt(mScreenCenterX * mScreenCenterX + mScreenCenterY * mScreenCenterY);
//		mOffsetY = PercentUtil.HeightPxToPercent(46);
		mOffsetY = Config.AD_CENTER_BOTTOM_MARGIN;
		mStartX = mScreenCenterX;
		mStartY = mScreenCenterY - mOffsetY;
		mCenterX = mStartX;
		mCenterY = mStartY;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(isDoingAmn) {
			mPath.reset();
			mPath.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CCW);
			canvas.clipPath(mPath); //裁剪区域
		}
	}

	public void startOpenAnimation()
	{
		openPageAnimator.start();
	}
	public void startCloseAnimation()
	{
		closePageAnimator.start();
	}


	public void setOnOpenPageCallback(OnOpenPageCallback onOpenPageCallback)
	{
		this.onOpenPageCallback = onOpenPageCallback;
	}

	private OnOpenPageCallback onOpenPageCallback;
	public interface OnOpenPageCallback
	{
		void onOpenStart();
		void onPageFade(float alpha);
		void onOpenEnd();
	}

	public void setOnClosePageCallBack(OnClosePageCallBack onClosePageCallBack)
	{
		this.onClosePageCallBack = onClosePageCallBack;
	}

	private OnClosePageCallBack onClosePageCallBack;
	public interface OnClosePageCallBack
	{
		void onCloseStart();
		void onPageFade(float alpha);
		void onCloseEnd();
	}
}

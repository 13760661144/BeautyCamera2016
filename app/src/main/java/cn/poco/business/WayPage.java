package cn.poco.business;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adnonstop.admasterlibs.data.AbsChannelAdRes;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.business.site.WayPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.site.HomePageSite;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MyTextButton;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * 拍一张/选一张页面
 */
public class WayPage extends IPage
{
	//private static final String TAG = "拍一张选一张";

	protected WayPageSite m_site;

	private ViewPager mViewPager;
	private boolean mViewPagerInitLayout = false;
	private PagerAdapter mPagerAdapter;
	private AdIntroImgView[] mViews;
	private Bitmap mDotOut;
	private Bitmap mDotOver;
	private LinearLayout mDotFr;
	private ImageView[] mDots;
	protected MyTextButton mBtnCamera;
	protected MyTextButton mBtnPhoto;
	private ImageView mBtnCancel;
	private TextView mTitle;
	private AbsChannelAdRes mChannelAdRes;
	private AbsChannelAdRes.SelPhotoPageData mRes;

	public WayPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (WayPageSite)site;

		Init();

		TongJiUtils.onPageStart(getContext(), R.string.商业_选一张拍一张);
	}

	protected void Init()
	{
		FrameLayout.LayoutParams fl;

		//BitmapDrawable bmpDraw = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.frame_bg));
		//bmpDraw.setTileModeX(TileMode.REPEAT);
		//bmpDraw.setTileModeY(TileMode.REPEAT);
		//setBackgroundDrawable(bmpDraw);
		setBackgroundColor(0xFFEDEDE9);

		mDotOut = BitmapFactory.decodeResource(getResources(), R.drawable.intropage_page_num_out);
		mDotOver = BitmapFactory.decodeResource(getResources(), R.drawable.intropage_page_num_over);

		FrameLayout topBar = new FrameLayout(getContext());
		//topBar.setBackgroundColor(0xffefefef);
		/*BitmapDrawable bmpDraw = (BitmapDrawable)getResources().getDrawable(R.drawable.business_top_bk);
		bmpDraw.setTileModeX(Shader.TileMode.REPEAT);
		topBar.setBackgroundDrawable(bmpDraw);*/
		topBar.setBackgroundColor(0xf4ffffff);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(topBar, fl);
		{
			mBtnCancel = new ImageView(getContext());
			mBtnCancel.setImageResource(R.drawable.framework_back_btn);
            ImageUtils.AddSkin(getContext(), mBtnCancel);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			topBar.addView(mBtnCancel, fl);
			mBtnCancel.setOnTouchListener(m_btnLst2);

			mTitle = new TextView(getContext());
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			mTitle.setTextColor(0xff333333);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			topBar.addView(mTitle, fl);
		}
		mViewPager = new ViewPager(getContext());
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xhdpi(90);
		fl.bottomMargin = ShareData.PxToDpi_xhdpi(100);
		this.addView(mViewPager, fl);
		mViewPager.setAdapter(mPagerAdapter = new PagerAdapter()
		{
			@Override
			public boolean isViewFromObject(View arg0, Object arg1)
			{
				return arg0 == arg1;
			}

			@Override
			public int getCount()
			{
				if(mRes != null && mRes.mAdm != null)
				{
					return mRes.mAdm.length;
				}
				return 0;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object)
			{
				if(container != null && object instanceof View)
				{
					container.removeView((View)object);
				}
				else
				{
					super.destroyItem(container, position, object);
				}
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position)
			{
				if(mViews != null && mViews.length > position)
				{
					View view = mViews[position];
					if(view != null)
					{
						container.addView(view);
						return view;
					}
				}
				return super.instantiateItem(container, position);
			}
		});
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int arg0)
			{
				if(mDots != null)
				{
					for(int i = 0; i < mDots.length; i++)
					{
						if(i == arg0)
						{
							mDots[i].setImageBitmap(mDotOver);
						}
						else
						{
							mDots[i].setImageBitmap(mDotOut);
						}
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}
		});

		mDotFr = new LinearLayout(getContext());
		mDotFr.setOrientation(LinearLayout.HORIZONTAL);
		mDotFr.setGravity(Gravity.CENTER);
		fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		fl.bottomMargin = ShareData.PxToDpi_xhdpi(200);
		this.addView(mDotFr, fl);

		FrameLayout bottomBar = new FrameLayout(getContext());
		/*bmpDraw = (BitmapDrawable)getResources().getDrawable(R.drawable.business_bottom_bk);
		if(bmpDraw != null)
		{
			bmpDraw.setTileModeX(Shader.TileMode.REPEAT);
		}
		bottomBar.setBackgroundDrawable(bmpDraw);*/
		bottomBar.setBackgroundColor(0xffffffff);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
		fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
		this.addView(bottomBar, fl);
		{
			int x = (ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(270) * 2) / 3;

			mBtnCamera = new MyTextButton(getContext());
			mBtnCamera.setBk(R.drawable.business_signup_btn_bk);
			mBtnCamera.setName(R.string.business_camera_btn_name, 14, 0xffffffff, false);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			fl.leftMargin = x;
			bottomBar.addView(mBtnCamera, fl);
			mBtnCamera.setOnTouchListener(m_btnLst2);

			mBtnPhoto = new MyTextButton(getContext());
			mBtnPhoto.setBk(R.drawable.business_signup_btn_bk);
			mBtnPhoto.setName(R.string.business_photo_btn_name, 14, 0xffffffff, false);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
			fl.rightMargin = x;
			bottomBar.addView(mBtnPhoto, fl);
			mBtnPhoto.setOnTouchListener(m_btnLst2);
		}
	}

	protected OnAnimationClickListener m_btnLst2 = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == mBtnCamera)
			{
				if(mRes != null)
				{
					Utils.UrlTrigger(getContext(), mRes.mTakePhotoTj);
				}
				TongJi2.AddCountByRes(getContext(), R.integer.商业_介绍页_拍一张);
				m_site.OnCamera(getContext());
			}
			else if(v == mBtnPhoto)
			{
				if(mRes != null)
				{
					Utils.UrlTrigger(getContext(), mRes.mPickPhotoTj);
				}
				TongJi2.AddCountByRes(getContext(), R.integer.商业_介绍页_从相册选);
				m_site.OnSelPhoto(getContext());
			}
			else if(v == mBtnCancel)
			{
				m_site.OnBack(getContext());
			}
		}

		@Override
		public void onTouch(View v)
		{
		}

		@Override
		public void onRelease(View v)
		{
		}
	};

	protected View.OnClickListener m_btnLst = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
            if(mViews != null && mViews.length > 0 && v == mViews[0])
            {
                if(mRes != null && mRes.mAdmClick != null && mRes.mAdmClick.length > 0)
                {
                    m_site.OnClickImg(getContext(), mRes.mAdmClick[0]);
                }
            }
        }
	};

	@Override
	public void onClose()
	{
		TongJiUtils.onPageEnd(getContext(), R.string.商业_选一张拍一张);

		super.onClose();
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.商业_选一张拍一张);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.商业_选一张拍一张);
		super.onResume();
	}

	/**
	 * @param params {@link HomePageSite#BUSINESS_KEY}:BusinessRes<br/>
	 *
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		mChannelAdRes = (AbsChannelAdRes)params.get(HomePageSite.BUSINESS_KEY);
		if(mChannelAdRes != null)
		{
			mRes = (AbsChannelAdRes.SelPhotoPageData)mChannelAdRes.GetPageData(AbsChannelAdRes.SelPhotoPageData.class);
		}

		if(mRes != null)
		{
			if(mRes.mAdm != null)
			{
				Bitmap bmp;
				int len = mRes.mAdm.length;
				mViews = new AdIntroImgView[len];
				for(int i = 0; i < len; i++)
				{
					mViews[i] = new AdIntroImgView(getContext());
					bmp = cn.poco.imagecore.Utils.DecodeShowImage((Activity)getContext(), mRes.mAdm[i], 0, 3.0f / 4.0f, MakeBmpV2.FLIP_NONE);
					if(bmp != null)
					{
						mViews[i].setImageBitmap(bmp);
						mViews[i].setOnClickListener(m_btnLst);

						if(!mViewPagerInitLayout)
						{
							mViewPagerInitLayout = true;
//							ViewGroup.LayoutParams layoutParams = mViewPager.getLayoutParams();
//							if(layoutParams != null)
//							{
//								layoutParams.width = ShareData.m_screenWidth;
//								layoutParams.height = layoutParams.width * bmp.getHeight() / bmp.getWidth();
//								mViewPager.setLayoutParams(layoutParams);
//							}

							if(len > 1)
							{
								mDots = new ImageView[len];
								for(int j = 0; j < len; j++)
								{
									mDots[j] = new ImageView(getContext());
									if(j == 0)
									{
										mDots[j].setImageBitmap(mDotOver);
									}
									else
									{
										mDots[j].setImageBitmap(mDotOut);
									}
									mDots[j].setPadding(ShareData.PxToDpi_xhdpi(8), 0, ShareData.PxToDpi_xhdpi(8), 0);
									LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
									mDotFr.addView(mDots[j], ll);
								}
							}
						}
					}
				}
				if(mPagerAdapter != null)
				{
					mPagerAdapter.notifyDataSetChanged();
				}
			}

			if(mRes.mTitle != null)
			{
				mTitle.setText(mRes.mTitle);
			}
		}
	}

	@Override
	public void onBack()
	{
		m_site.OnBack(getContext());
	}
}

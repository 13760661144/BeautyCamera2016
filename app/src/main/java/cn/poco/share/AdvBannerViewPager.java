package cn.poco.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsClickAdRes;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.util.ArrayList;

import cn.poco.adMaster.ShareAdBanner;
import cn.poco.adMaster.StickerAdBanner;
import cn.poco.advanced.ImageUtils;
import cn.poco.banner.BannerCore3;
import cn.poco.home.site.HomePageSite;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.NetState;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2017/7/17.
 */

public class AdvBannerViewPager extends FrameLayout
{
	public static final int PAGE_SHARE = 50001;
	public static final int PAGE_STICKER = 50002;
	public static final int PAGE_ADVERTISING = 50003;

	protected ViewPager m_viewPager;
//	protected LinearLayout m_pageNumber;
	protected MyPagerAdapter m_adapter;

//	private Bitmap m_pointColor;
//	private Bitmap m_pointWhite;
	private int m_page = 0;
	private boolean m_pageClose = false;
	private boolean m_pageScrolling = false;
	private boolean m_bannerUpdateComplete = false;
	private boolean m_firstShow = true;
	private boolean m_onPause = false;

	protected ShareAdBanner m_banner;
	private HomePageSite.CmdProc m_cmdProc;
	private ArrayList<AbsAdRes> m_advRes;

	private boolean m_startUp = false;
	private boolean m_autoPageRun = false;

	//广告定制内置banner
	private boolean m_advertising = false;
	private int m_advResId;
	private String m_advUrl;

	public AdvBannerViewPager(@NonNull Context context, HomePageSite.CmdProc cmdProc, int banner_type)
	{
		super(context);
		m_cmdProc = cmdProc;
		init();
//		makePointBitmap();
		initData(banner_type);
	}

	public AdvBannerViewPager(@NonNull Context context, HomePageSite.CmdProc cmdProc, int advResId, String advUrl)
	{
		super(context);
		m_cmdProc = cmdProc;
		m_advertising = true;
		m_advResId = advResId;
		m_advUrl = advUrl;
		init();
//		makePointBitmap();
		initData(PAGE_ADVERTISING);
	}

	public boolean isFirstShow()
	{
		return m_firstShow;
	}

	private void initData(int banner_type)
	{
		switch(banner_type)
		{
			case PAGE_SHARE:
				m_banner = new ShareAdBanner(getContext());
				m_banner.Run(new ShareAdBanner.Callback()
				{
					@Override
					public void ShowBanner(ArrayList<AbsAdRes> arr)
					{
						if(m_pageClose) return;
//						m_pageNumber.removeAllViews();
						if(arr != null && arr.size() > 0 && NetState.IsConnectNet(getContext()))
						{
							m_advRes = arr;
//							for(int i = 0; i < arr.size(); i++)
//							{
//								ImageView view = new ImageView(getContext());
//								if(i == 0) view.setImageBitmap(m_pointColor);
//								else view.setImageBitmap(m_pointWhite);
//								LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//								ll.gravity = Gravity.LEFT | Gravity.TOP;
//								if(i > 0) ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
//								m_pageNumber.addView(view, ll);
//							}

							if(m_adapter != null)
							{
								m_adapter.clean();
								m_adapter = null;
							}
							m_adapter = new MyPagerAdapter(getContext(), arr);
							m_viewPager.setAdapter(m_adapter);
							m_viewPager.setCurrentItem(0);
//							if(arr.size() <= 1) m_pageNumber.setVisibility(View.GONE);
//							else
//							{
//								if(m_firstShow) setVisibility(View.GONE);
//								m_pageNumber.setVisibility(View.VISIBLE);
//							}
							if(arr.size() > 1 && m_firstShow)
							{
								setVisibility(View.GONE);
								startAutoPage();
							}
						}
						else
						{
//							m_pageNumber.setVisibility(View.GONE);
							m_adapter = new MyPagerAdapter(getContext(), null);
							m_viewPager.setAdapter(m_adapter);
							m_viewPager.setCurrentItem(0);
						}
						m_bannerUpdateComplete = true;
						m_banner.Show();
					}
				});
				break;

			case PAGE_STICKER:
				m_banner = new StickerAdBanner(getContext());
				m_banner.Run(new ShareAdBanner.Callback()
				{
					@Override
					public void ShowBanner(ArrayList<AbsAdRes> arr)
					{
						if(m_pageClose) return;
//						m_pageNumber.removeAllViews();
						if(arr != null && arr.size() > 0 && NetState.IsConnectNet(getContext()))
						{
							m_advRes = arr;
//							for(int i = 0; i < arr.size(); i++)
//							{
//								ImageView view = new ImageView(getContext());
//								if(i == 0) view.setImageBitmap(m_pointColor);
//								else view.setImageBitmap(m_pointWhite);
//								LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//								ll.gravity = Gravity.LEFT | Gravity.TOP;
//								if(i > 0) ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
//								m_pageNumber.addView(view, ll);
//							}

							if(m_adapter != null)
							{
								m_adapter.clean();
								m_adapter = null;
							}
							m_adapter = new MyPagerAdapter(getContext(), arr);
							m_viewPager.setAdapter(m_adapter);
							m_viewPager.setCurrentItem(0);
//							if(arr.size() <= 1) m_pageNumber.setVisibility(View.GONE);
//							else
//							{
//								if(m_firstShow) setVisibility(View.GONE);
//								m_pageNumber.setVisibility(View.VISIBLE);
//							}
							if(arr.size() > 1 && m_firstShow)
							{
								setVisibility(View.GONE);
								startAutoPage();
							}
						}
						else
						{
//							m_pageNumber.setVisibility(View.GONE);
							m_adapter = new MyPagerAdapter(getContext(), null);
							m_viewPager.setAdapter(m_adapter);
							m_viewPager.setCurrentItem(0);
						}
						m_bannerUpdateComplete = true;
						m_banner.Show();
					}
				});
				break;

			case PAGE_ADVERTISING:
//				m_pageNumber.setVisibility(View.GONE);
				m_adapter = new MyPagerAdapter(getContext(), null);
				m_viewPager.setAdapter(m_adapter);
				m_viewPager.setCurrentItem(0);
				m_bannerUpdateComplete = true;
				break;
		}

	}

	private void init()
	{
		ShareData.InitData(getContext());

		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		LinearLayout mainFrame = new LinearLayout(getContext());
		mainFrame.setOrientation(LinearLayout.VERTICAL);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		addView(mainFrame, fl);
		{
			m_viewPager = new ViewPager(getContext());
			m_viewPager.addOnPageChangeListener(new MyPageChangeListener());
			m_viewPager.setPageMargin(ShareData.PxToDpi_xhdpi(-46));
			ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(300));
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			mainFrame.addView(m_viewPager, ll);

//			m_pageNumber = new LinearLayout(getContext());
//			m_pageNumber.setOrientation(LinearLayout.HORIZONTAL);
//			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//			ll.topMargin = ShareData.PxToDpi_xhdpi(33);
//			mainFrame.addView(m_pageNumber, ll);
		}
	}

	protected class MyPagerAdapter extends PagerAdapter
	{
		private ImageView[] viewList;
		private ArrayList<AbsAdRes> advList;
		private Bitmap white_bg;

		public MyPagerAdapter(Context context, ArrayList<AbsAdRes> list)
		{
			advList = list;
			viewList = null;

			if(m_advertising)
			{
				viewList = new ImageView[1];

				ImageView imageView = new ImageView(context);
				imageView.setPadding(ShareData.PxToDpi_xhdpi(35), 0, ShareData.PxToDpi_xhdpi(35), 0);
				FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				imageView.setLayoutParams(fl);
				imageView.setImageBitmap(makeAdvBannerBitmap(m_advResId));
				imageView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						if(m_advUrl != null && m_advUrl.length() > 0) BannerCore3.ExecuteCommand(getContext(), m_advUrl, m_cmdProc);
					}
				});

				viewList[0] = imageView;
			}
			else if(advList != null && advList.size() > 0)
			{
				int len = advList.size();
				viewList = new ImageView[len];

				Bitmap white = Bitmap.createBitmap(ShareData.PxToDpi_xhdpi(650), ShareData.PxToDpi_xhdpi(300), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(white);
				canvas.drawColor(Color.WHITE);
				white_bg = makeAdvBannerBitmap(white);

				for(int i = 0; i < len; i++)
				{
					ImageView imageView = new ImageView(context);
					imageView.setImageBitmap(white_bg);
					imageView.setPadding(ShareData.PxToDpi_xhdpi(35), 0, ShareData.PxToDpi_xhdpi(35), 0);
					FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					imageView.setLayoutParams(fl);

					AbsAdRes ad = advList.get(i);
					if(ad instanceof AbsClickAdRes)
					{
						final AbsClickAdRes res = (AbsClickAdRes)ad;
						if(res.url_adm != null && res.url_adm.length > 0 && res.url_adm[0] != null)
						{
							if(res.url_adm[0].endsWith(".gif"))
							{
								Glide.with(getContext()).load(res.url_adm[0]).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(new BitmapDrawable(getContext().getResources(), white_bg)).into(imageView);
							}
							else
							{
								Glide.with(getContext()).load(res.url_adm[0]).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(new BitmapDrawable(getContext().getResources(), white_bg)).transform(new GlideRoundTransform(getContext(), ShareData.PxToDpi_xhdpi(30))).into(imageView);
							}
							imageView.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									m_banner.Click(res, m_cmdProc);
									onStop();
								}
							});
						}
					}

					viewList[i] = imageView;
				}
			}
			else
			{
				viewList = new ImageView[1];

				ImageView imageView = new ImageView(context);
				imageView.setPadding(ShareData.PxToDpi_xhdpi(35), 0, ShareData.PxToDpi_xhdpi(35), 0);
				FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				imageView.setLayoutParams(fl);
				imageView.setImageBitmap(makeAdvBannerBitmap(R.drawable.share_default_banner));
				imageView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						BannerCore3.ExecuteCommand(getContext(), "beautyCamera://open=34", m_cmdProc);
					}
				});

				viewList[0] = imageView;
			}
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			if(viewList != null && viewList.length > position)
			{
				container.addView(viewList[position]);
				return viewList[position];
			}
			return null;
		}

		@Override
		public int getCount() {
			return viewList.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			if (object instanceof View) {
				((ViewPager) container).removeView((View)object);
			}
		}

		public void clean()
		{
			if(advList != null)
			{
				advList.clear();
				advList = null;
			}
		}
	}

	protected class MyPageChangeListener implements ViewPager.OnPageChangeListener
	{
		@Override
		public void onPageScrollStateChanged(int status)
		{
			if(status == 0) m_pageScrolling = false;
			else if(status == 1) m_pageScrolling = true;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}

		@Override
		public void onPageSelected(int page)
		{
//			if(m_pageNumber == null || m_pageNumber.getChildCount() <= 1) return;
			if(page == m_page) return;
//			((ImageView)m_pageNumber.getChildAt(m_page)).setImageBitmap(m_pointWhite);
//			((ImageView)m_pageNumber.getChildAt(page)).setImageBitmap(m_pointColor);
			m_page = page;
		}
	}

//	private void makePointBitmap()
//	{
//		m_pointColor = Bitmap.createBitmap(ShareData.PxToDpi_xhdpi(12), ShareData.PxToDpi_xhdpi(12), Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(m_pointColor);
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//		Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		int color = ImageUtils.GetSkinColor();
//		if(color != 0) paint.setColor(color);
//		else paint.setColor(0xffe75988);
//		paint.setAlpha(204);
//		float center = (float)ShareData.PxToDpi_xhdpi(12) / 2;
//		canvas.drawCircle(center, center, center, paint);
//
//		m_pointWhite = Bitmap.createBitmap(ShareData.PxToDpi_xhdpi(12), ShareData.PxToDpi_xhdpi(12), Bitmap.Config.ARGB_8888);
//		canvas = new Canvas(m_pointWhite);
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//		paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setColor(Color.WHITE);
//		paint.setAlpha(153);
//		canvas.drawCircle(center, center, center, paint);
//	}

	private Bitmap makeAdvBannerBitmap(Object res)
	{
		if(res == null) return null;
		Bitmap org;
		if(res instanceof Bitmap) org = (Bitmap)res;
		else org = MakeBmpV2.DecodeImage(getContext(), res, 0, -1, ShareData.PxToDpi_xhdpi(650), ShareData.PxToDpi_xhdpi(300), Bitmap.Config.ARGB_8888);
		if(org == null || org.isRecycled()) return null;
		Bitmap adv_pic = MakeBmpV2.CreateFixBitmapV2(org, 0, 0, MakeBmpV2.POS_LEFT, ShareData.PxToDpi_xhdpi(650), ShareData.PxToDpi_xhdpi(300), Bitmap.Config.ARGB_8888);
		if(adv_pic == null || adv_pic.isRecycled()) return null;
		adv_pic = cn.poco.tianutils.ImageUtils.MakeRoundBmp(adv_pic, ShareData.PxToDpi_xhdpi(30));
		return adv_pic;
	}

	public void autoPage()
	{
		m_startUp = true;
		startAutoPage();
	}

	private void startAutoPage()
	{
		if(!m_startUp || m_autoPageRun) return;

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					m_autoPageRun = true;
					while(!m_bannerUpdateComplete && !m_pageClose)
					{
						Thread.sleep(100);
					}
					if(m_pageClose)
					{
						m_autoPageRun = false;
						return;
					}
					if(m_advRes == null || m_advRes.size() <= 1)
					{
						m_autoPageRun = false;
						return;
					}

					if(m_firstShow && !m_pageScrolling)
					{
						m_firstShow = false;
						AdvBannerViewPager.this.post(new Runnable()
						{
							@Override
							public void run()
							{
								setVisibility(View.VISIBLE);
								if(!m_pageClose && m_viewPager != null && !m_pageScrolling && m_advRes != null && m_advRes.size() > 1)
								{
									TranslateAnimation translate = new TranslateAnimation(ShareData.PxToDpi_xhdpi(20), 0, 0, 0);
									translate.setDuration(360);
									translate.setAnimationListener(new Animation.AnimationListener()
									{
										@Override
										public void onAnimationStart(Animation animation){}

										@Override
										public void onAnimationEnd(Animation animation)
										{
											if(m_viewPager != null) m_viewPager.clearAnimation();
										}

										@Override
										public void onAnimationRepeat(Animation animation){}
									});
									m_viewPager.startAnimation(translate);
								}
							}
						});
						Thread.sleep(1000);
						if(m_pageClose)
						{
							m_autoPageRun = false;
							return;
						}
					}

					int count = 0;
					while(!m_pageClose && m_advRes != null && m_advRes.size() > 1)
					{
						Thread.sleep(100);
						if(!m_onPause) count += 100;
						if(m_pageScrolling) count = 0;
						if(count >= 5000)
						{
							count = 0;
							AdvBannerViewPager.this.post(new Runnable()
							{
								@Override
								public void run()
								{
									if(!m_bannerUpdateComplete || m_pageClose || m_viewPager == null || m_advRes == null || m_page == m_advRes.size() - 1 || m_pageScrolling || m_onPause)
										return;
									m_viewPager.setCurrentItem(m_page + 1, true);
								}
							});
						}
					}
					m_autoPageRun = false;
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void clean()
	{
		m_pageClose = true;
		if(m_viewPager != null)
		{
			m_viewPager.clearAnimation();
			m_viewPager.setAdapter(null);
			m_viewPager.addOnPageChangeListener(null);
			if(m_adapter != null)
			{
				m_adapter.clean();
				m_adapter = null;
			}
		}
//		if(m_pageNumber != null)
//		{
//			m_pageNumber.removeAllViews();
//		}
//		if(m_pointColor != null)
//		{
//			m_pointColor.recycle();
//			m_pointColor = null;
//		}
//		if(m_pointWhite != null)
//		{
//			m_pointWhite.recycle();
//			m_pointWhite = null;
//		}
		if(m_banner != null)
		{
			m_banner.Clear();
			m_banner = null;
		}
		if(m_advRes != null)
		{
			m_advRes.clear();
			m_advRes = null;
		}
		cleanGlide();
		System.gc();
	}

	public void onStop()
	{
		m_onPause = true;
	}

	public void onStart()
	{
		m_onPause = false;
		m_pageScrolling = false;
	}

	private void cleanGlide()
	{
		Glide.get(getContext()).clearMemory();
//		new Thread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				Glide.get(PocoCamera.main).clearDiskCache();
//			}
//		}).start();
	}

	public class GlideRoundTransform extends BitmapTransformation
	{
		private float radius = 0f;

		public GlideRoundTransform(Context context, int dp) {
			super(context);
			this.radius = dp;
		}

		@Override
		protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
			return roundCrop(pool, toTransform);
		}

		private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
			if (source == null) return null;

			Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
			if (result == null) result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(result);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.drawBitmap(source, 0, 0, null);

			return cn.poco.tianutils.ImageUtils.MakeRoundBmp(MakeBmpV2.CreateFixBitmapV2(result, 0, 0, MakeBmpV2.POS_H_CENTER | MakeBmpV2.POS_V_CENTER, ShareData.PxToDpi_xhdpi(650), ShareData.PxToDpi_xhdpi(300), Bitmap.Config.ARGB_8888), radius);
		}

		@Override
		public String getId() {
			return getClass().getName() + Math.round(radius);
		}
	}
}

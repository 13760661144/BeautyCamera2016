package cn.poco.home;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.site.IntroPage2Site;
import cn.poco.statistics.TongJiUtils;
import my.beautyCamera.R;

public class IntroPage2 extends IPage
{
	//private static final String TAG = "开机引导";

	protected IntroPage2Site m_site;

//	protected VideoPage m_videoPage;
	protected AnimGroup m_animGroup;
	protected Runnable m_completeLst;
	protected boolean m_hasAnimGroup = true;;

	public IntroPage2(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (IntroPage2Site)site;

		Init();

		TongJiUtils.onPageStart(getContext(), R.string.开机引导);
	}

	protected void Init()
	{
		//视频完成再显示介绍动画

		//显示视频
//		m_videoPage = new VideoPage(getContext());
//		m_videoPage.setCompleteListener(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				//移除视频
//				if(m_videoPage != null)
//				{
//					m_videoPage.setVisibility(View.GONE);
//					IntroPage2.this.post(new Runnable()
//					{
//						public void run()
//						{
//							if(m_videoPage != null)
//							{
//								IntroPage2.this.removeView(m_videoPage);
//								m_videoPage = null;
//							}
//						}
//					});
//				}

				if(m_hasAnimGroup)
				{
					//介绍动画(先隐藏)
					m_animGroup = new AnimGroup(getContext());
					m_animGroup.setCompleteListener(new Runnable()
					{
						@Override
						public void run()
						{

							if(m_completeLst != null)
							{
								m_completeLst.run();
							}

							if(m_site != null)
							{
								m_site.OnNext(getContext());
							}
						}
					});
					LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					m_animGroup.setLayoutParams(fl);
					IntroPage2.this.addView(m_animGroup);
					m_animGroup.StartFirstAnim();
				}
				else
				{
					if(m_completeLst != null)
					{
						m_completeLst.run();
					}

					if(m_site != null)
					{
						m_site.OnNext(getContext());
					}
				}
//			}
//		});
//		LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		m_videoPage.setLayoutParams(fl);
//		IntroPage2.this.addView(m_videoPage);
	}

	public void SetShowAnimGroup(boolean hasAnimGroup)
	{
		m_hasAnimGroup = hasAnimGroup;
	}

	public void setCompleteListener(Runnable listener)
	{
		m_completeLst = listener;
	}

//	@Override
//	public void onPause()
//	{
//		if(m_videoPage != null)
//		{
//			m_videoPage.onPause();
//		}
//	}
//
//	@Override
//	public void onResume()
//	{
//		if(m_videoPage != null)
//		{
//			m_videoPage.onResume();
//		}
//	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{

	}

	@Override
	public void onBack()
	{

	}

	@Override
	public void onClose()
	{
//		if(m_videoPage != null)
//		{
//			m_videoPage.onClose();
//			m_videoPage = null;
//		}
		if(m_animGroup != null)
		{
			IntroPage2.this.removeView(m_animGroup);
			m_animGroup.ClearAll();
			m_animGroup = null;
		}

		TongJiUtils.onPageEnd(getContext(), R.string.开机引导);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.开机引导);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.开机引导);
		super.onResume();
	}
}

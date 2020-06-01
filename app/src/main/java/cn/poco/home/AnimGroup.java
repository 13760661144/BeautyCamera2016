package cn.poco.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.StatusButton;
import my.beautyCamera.R;

import static cn.poco.home.home4.utils.PercentUtil.HeightPxxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.WidthPxxToPercent;

public class AnimGroup extends FrameLayout
{
	protected BaseAnimView[] m_views;
	protected ViewPager m_viewPage;
	protected LinearLayout m_pageNumCtrl;
	protected Bitmap m_dotOut;
	protected Bitmap m_dotOver;
	protected StatusButton[] m_pageNumArr;

	protected Runnable m_completeLst;

	public AnimGroup(Context context)
	{
		super(context);

		Init();
	}

	public AnimGroup(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Init();
	}

	public AnimGroup(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		Init();
	}

	protected void Init()
	{
		ShareData.InitData((Activity)getContext());

		LayoutParams fl;

		this.setBackgroundColor(0xffffffff);
		m_views = new BaseAnimView[3];

		m_dotOut = BitmapFactory.decodeResource(getResources(), R.drawable.home4_intro_dot_out);
		m_dotOver = BitmapFactory.decodeResource(getResources(), R.drawable.home4_intro_dot_over);
		m_pageNumArr = new StatusButton[m_views.length];
		for(int i = 0; i < m_pageNumArr.length; i++)
		{
			StatusButton temp = new StatusButton(getContext());
//			temp.SetData(m_dotOut, m_dotOver, ImageView.ScaleType.CENTER_INSIDE);
			temp.SetData(m_dotOut, m_dotOver, ImageView.ScaleType.FIT_XY);
			m_pageNumArr[i] = temp;
		}

		m_viewPage = new ViewPager(getContext());
		m_viewPage.setAdapter(new PagerAdapter()
		{
			@Override
			public boolean isViewFromObject(View arg0, Object arg1)
			{
				return arg0 == arg1;
			}

			@Override
			public int getCount()
			{
				if(m_views != null)
				{
					return m_views.length;
				}
				return 0;
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object)
			{
				if(container != null && object instanceof View)
				{
					//System.out.println("object " + object);
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
				if(m_views != null && m_views.length > position)
				{
					//System.out.println("container " + container);
					View view = m_views[position];
					if(view == null)
					{
						switch(position)
						{
							case 0:
								m_views[0] = new AnimView41(getContext());
								((AnimView41)m_views[0]).SetData(R.drawable.home4_intro_img1);
								//m_views[0] = new AnimView1(getContext());
								//if(m_start)
								//{
								//	m_views[0].Start();
								//}
								//System.out.println("view " + m_views[0]);
								break;
							case 1:
								m_views[1] = new AnimView41(getContext());
								((AnimView41)m_views[1]).SetData(R.drawable.home4_intro_img2);
								//m_views[1] = new AnimView2(getContext());
								//System.out.println("view " + m_views[1]);
								break;
							case 2:
								m_views[2] = new AnimView42(getContext());
								m_views[2].setCompleteListener(m_completeLst);

								//m_views[2] = new AnimView3(getContext());
								//System.out.println("view " + m_views[2]);
								break;
							case 3:
							default:
								break;
						}
						view = m_views[position];
					}
					if(view != null)
					{
						container.addView(view);
						return view;
					}
				}
				return super.instantiateItem(container, position);
			}
		});
		m_viewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}

			@Override
			public void onPageSelected(int arg0)
			{
				if(arg0 == m_pageNumArr.length - 1){
					//最后一个隐藏
					m_pageNumCtrl.setVisibility(View.GONE);
				}else{
					m_pageNumCtrl.setVisibility(View.VISIBLE);
				}
				if(m_pageNumArr != null)
				{
					for(int i = 0; i < m_pageNumArr.length; i++)
					{
						if(i == arg0)
						{
							if(m_views != null && m_views[i] != null)
							{
								m_views[i].Start();
							}
							m_pageNumArr[i].SetOver();
						}
						else
						{
							if(m_views != null && m_views[i] != null)
							{
								m_views[i].Stop();
							}
							m_pageNumArr[i].SetOut();
						}
					}
				}
			}
		}); fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		m_viewPage.setLayoutParams(fl);
		this.addView(m_viewPage);

		m_pageNumCtrl = new LinearLayout(getContext());
		m_pageNumCtrl.setOrientation(LinearLayout.HORIZONTAL);
		fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		fl.bottomMargin = HeightPxxToPercent(150);
		m_pageNumCtrl.setLayoutParams(fl);
		this.addView(m_pageNumCtrl);
		{
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(WidthPxxToPercent(33), WidthPxxToPercent(33));
			ll.leftMargin = WidthPxxToPercent(17);
			ll.rightMargin = ll.leftMargin;
			for(int i = 0; i < m_pageNumArr.length; i++)
			{
				StatusButton temp = m_pageNumArr[i];
				temp.setLayoutParams(ll);
				m_pageNumCtrl.addView(temp);
			}
		}

		m_pageNumArr[0].SetOver();
	}

	public void setCompleteListener(Runnable listener)
	{
		m_completeLst = listener;
	}

	protected boolean m_start = false;

	public void StartFirstAnim()
	{
		//第一次启动动画
		m_start = true;
		if(m_views != null && m_views[0] != null)
		{
			m_views[0].Start();
		}
	}

	public void ClearAll()
	{
		this.removeAllViews();
		if(m_views != null)
		{
			for(int i = 0; i < m_views.length; i++)
			{
				if(m_views[i] != null)
				{
					m_views[i].ClearAll();
				}
				m_views[i] = null;
			}
			m_views = null;
		}
	}
}

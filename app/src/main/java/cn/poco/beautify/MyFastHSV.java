package cn.poco.beautify;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.StatusButton;
import cn.poco.tsv.FastItemList;

public class MyFastHSV extends HorizontalScrollView
{
	public int def_dark_btn_res_out;
	public int def_dark_btn_res_over;
	public int def_blur_btn_res_out;
	public int def_blur_btn_res_over;
	public int def_btn_x;
	public int def_view_x;
	public int def_2btn_gap_size;

	protected boolean m_uiOk = false;
	protected boolean m_scrolling = false;
	protected boolean m_toCenter = false;

	public RelativeLayout m_fr;
	public FastItemList m_view;
	public StatusButton m_darkBtn;
	public StatusButton m_blurBtn;

	protected Callback m_lst;

	public MyFastHSV(Context context)
	{
		super(context);

		Init();
	}

	public MyFastHSV(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		Init();
	}

	public MyFastHSV(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		Init();
	}

	protected void Init()
	{
		this.setHorizontalScrollBarEnabled(false);

		ShareData.InitData((Activity)getContext());
	}

	public void SetShowCore(FastItemList view)
	{
		ClearAll();

		LayoutParams fl;
		LinearLayout.LayoutParams ll;
		RelativeLayout.LayoutParams rl;

		m_fr = new RelativeLayout(getContext())
		{
			@Override
			protected void onLayout(boolean changed, int left, int top, int right, int bottom)
			{
				super.onLayout(changed, left, top, right, bottom);

				if(changed)
				{
					m_uiOk = true;

					if(m_toCenter && !m_scrolling)
					{
						m_toCenter = false;

						LaunchScrollToCenter(false);
					}
				}
			}
		};
		fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		m_fr.setLayoutParams(fl);
		this.addView(m_fr);
		{
			LinearLayout btnFr = new LinearLayout(getContext());
			btnFr.setGravity(Gravity.CENTER);
			btnFr.setOrientation(LinearLayout.VERTICAL);
			rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			rl.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			rl.leftMargin = def_btn_x;
			btnFr.setLayoutParams(rl);
			m_fr.addView(btnFr);
			{
				m_blurBtn = new StatusButton(getContext());
				m_blurBtn.SetData(def_blur_btn_res_out, def_blur_btn_res_over, ImageView.ScaleType.CENTER);
				ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				ll.bottomMargin = def_2btn_gap_size;
				m_blurBtn.setLayoutParams(ll);
				btnFr.addView(m_blurBtn);
				m_blurBtn.setOnClickListener(m_btnListener);

				m_darkBtn = new StatusButton(getContext());
				m_darkBtn.SetData(def_dark_btn_res_out, def_dark_btn_res_over, ImageView.ScaleType.CENTER);
				ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				m_darkBtn.setLayoutParams(ll);
				btnFr.addView(m_darkBtn);
				m_darkBtn.setOnClickListener(m_btnListener);
			}

			m_view = view;
			rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			rl.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			rl.leftMargin = def_view_x;
			m_view.setLayoutParams(rl);
			m_fr.addView(m_view);
		}
	}

	protected OnClickListener m_btnListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(m_lst != null)
			{
				m_lst.onClick(v);
			}
		}
	};

	protected void LaunchScrollToCenter(boolean hasAnim)
	{
		if(m_view != null)
		{
			LaunchScrollToCenter(m_view.GetSelectIndex(), hasAnim);
		}
	}

	protected void LaunchScrollToCenter(int index, boolean hasAnim)
	{
		if(m_view != null)
		{
			if(index >= 0)
			{
				int itemW = m_view.def_item_left + m_view.def_item_width + m_view.def_item_right;
				int x = itemW * index + def_view_x - (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight() - itemW) / 2;
				int y = 0;
				if(hasAnim)
				{
					MyFastHSV.this.smoothScrollTo(x, y);
				}
				else
				{
					MyFastHSV.this.scrollTo(x, y);
				}
			}
			else
			{
				MyFastHSV.this.scrollTo(0, 0);
			}
		}
	}

	public void ScrollToCenter(final boolean hasAnim)
	{
		m_toCenter = true;
		m_scrolling = true;
		this.post(new Runnable()
		{
			@Override
			public void run()
			{
				m_scrolling = false;

				if(m_uiOk)
				{
					m_toCenter = false;

					LaunchScrollToCenter(hasAnim);
				}
			}
		});
	}

	public void ScrollToCenterEx(final boolean hasAnim)
	{
		m_toCenter = true;
		m_scrolling = true;
		this.post(new Runnable()
		{
			@Override
			public void run()
			{
				m_scrolling = false;

				if(m_uiOk)
				{
					m_toCenter = false;

					LaunchScrollToCenter(-1, hasAnim);
				}
			}
		});
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		if(m_view != null)
		{
			int tl = l - def_view_x;
			if(tl < 0)
			{
				tl = 0;
			}
			m_view.UpdateUI(getWidth(), tl);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		if(m_view != null)
		{
			int tl = this.getScrollX() - def_view_x;
			if(tl < 0)
			{
				tl = 0;
			}
			m_view.UpdateUI(w, tl);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		if(m_lst != null)
		{
			m_lst.onTouch(this, ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	public void ClearAll()
	{
		this.removeAllViews();

		if(m_view != null)
		{
			m_view.ClearAll();
			m_view = null;
		}

		m_lst = null;
	}

	public void AddDispatchTouchListener(Callback lst)
	{
		m_lst = lst;
	}

	public static interface Callback extends OnTouchListener, OnClickListener
	{

	}
}

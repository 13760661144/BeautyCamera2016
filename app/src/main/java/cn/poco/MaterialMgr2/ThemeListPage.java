package cn.poco.MaterialMgr2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.ThemeListPageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.BaseRes;
import cn.poco.resource.LockRes;
import cn.poco.resource.ResourceMgr;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.pullToRefresh.PullToRefreshListView1;
import my.beautyCamera.R;

/**
 * 主题页
 */
public class ThemeListPage extends IPage
{
	public static final int OVER_TIME = 6000;
	private static final String TAG = "主题列表";

	protected ThemeListPageSite m_site;

	private boolean m_uiEnabled;
	private ImageView m_backBtn;
	private LinearLayout m_manageBtn;
	private ImageView m_manageIcon;
	private TextView m_manageText;
	private TextView m_title;
	private PullToRefreshListView1 m_themeList;
	private BaseItemInfo m_curItemInfo;
	private ThemeListAdapter m_adapter;
	private int m_defID = -1;
	private OnPageCallback m_cb;

	//资源
	protected ArrayList<ThemeRes> m_orgThemeRes;
	private ArrayList<BaseItemInfo> m_themeRes;
	private boolean m_isRefreshing = false;
	private boolean m_hasRefreshUIComplete = false;	//是否收起刷新的UI

	public ThemeListPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (ThemeListPageSite)site;

		InitData();
		InitUI();

		TongJiUtils.onPageStart(getContext(), R.string.素材中心);
		MyBeautyStat.onPageStartByRes(R.string.素材商店_素材商店首页_主页面);
	}

	private void InitData()
	{
		ShareData.InitData(getContext());
		m_orgThemeRes = ThemeResMgr2.getInstance().GetAllResArr();
		m_themeRes = getThemeItemDatas();
		m_uiEnabled = true;
	}

	public ArrayList<BaseItemInfo> getThemeItemDatas()
	{
		ArrayList<BaseItemInfo> out = new ArrayList<BaseItemInfo>();
		BaseItemInfo itemInfo;
		ThemeRes res;
		if(m_orgThemeRes != null)
		{
			int size = m_orgThemeRes.size();
			for(int i = 0; i < size; i++)
			{
				res = m_orgThemeRes.get(i);
				if(res != null && !res.m_isHide)
				{
					itemInfo = new BaseItemInfo();
					itemInfo.m_name = res.m_name;
					itemInfo.m_themeRes = res;
					LockRes lockRes = MgrUtils.unLockTheme(res.m_id);
					if(lockRes != null && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE &&
							TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + res.m_id))
					{
						itemInfo.m_lock = true;
					}
					out.add(itemInfo);
				}
			}
		}
		return out;
	}

	private void InitUI()
	{
		FrameLayout.LayoutParams fl_lp;
		this.setBackgroundResource(R.drawable.login_tips_all_bk);

		//白色遮罩层
		View mask = new View(getContext());
		mask.setBackgroundColor(0x99ffffff);
		fl_lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask.setLayoutParams(fl_lp);
		addView(mask, 0);

		m_themeList = new PullToRefreshListView1(getContext(), ShareData.PxToDpi_xhdpi(20), 0);
		m_themeList.setDivider(new ColorDrawable(0x00000000));
		m_themeList.setCacheColorHint(0);
		ColorDrawable c = new ColorDrawable(0);
		c.setAlpha(0);
		m_themeList.setSelector(c);
		m_themeList.setDividerHeight(0);
		m_themeList.setVerticalScrollBarEnabled(false);
		m_themeList.setOnItemClickListener(mThemeListItemClickListener);
		m_themeList.setSelector(new ColorDrawable(Color.TRANSPARENT));
		m_themeList.setOnRefreshListener(new PullToRefreshListView1.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				m_hasRefreshUIComplete = false;
				if(m_isRefreshing == false)
				{
					m_isRefreshing = true;
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							ResourceMgr mgr = new ResourceMgr();
							mgr.ReloadCloudRes(getContext());
							((Activity)getContext()).runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									if(m_refreshCB != null)
									{
										m_refreshCB.OnFinish();
									}
								}
							});
						}
					}).start();
				}
				postDelayed(m_overTimeRunnable, OVER_TIME);
			}
		});

		m_adapter = new ThemeListAdapter(getContext());
		m_adapter.setDatas(m_themeRes);
		m_themeList.setAdapter(m_adapter);
		m_adapter.notifyDataSetChanged();
		fl_lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(96));
		fl_lp.topMargin = ShareData.PxToDpi_xhdpi(96);
		m_themeList.setLayoutParams(fl_lp);
		this.addView(m_themeList, 1);

		final FrameLayout captionBar = new FrameLayout(getContext());
		captionBar.setBackgroundColor(0xf4FFFFFF);
		fl_lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(96));
		fl_lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		captionBar.setLayoutParams(fl_lp);
		this.addView(captionBar);
		captionBar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(m_themeList != null)
				{
					m_themeList.smoothScrollToPosition(1);
				}
			}
		});

		m_backBtn = new ImageView(getContext());
		m_backBtn.setImageResource(R.drawable.framework_back_btn);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		captionBar.addView(m_backBtn, fl_lp);
		m_backBtn.setOnTouchListener(mOnClickListener);
		ImageUtils.AddSkin(getContext(), m_backBtn);

		m_manageBtn = new LinearLayout(getContext());
		m_manageBtn.setGravity(Gravity.CENTER);
		m_manageBtn.setOrientation(LinearLayout.HORIZONTAL);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		fl_lp.rightMargin = ShareData.PxToDpi_xhdpi(28);
		captionBar.addView(m_manageBtn, fl_lp);
		{
			LinearLayout.LayoutParams ll;
			m_manageIcon = new ImageView(getContext());
			m_manageIcon.setImageResource(R.drawable.new_material_manage_btn);
			ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			m_manageIcon.setLayoutParams(ll);
			m_manageBtn.addView(m_manageIcon);
			ImageUtils.AddSkin(getContext(), m_manageIcon);

			m_manageText = new TextView(getContext());
			m_manageText.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
			m_manageText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			m_manageText.setText(R.string.material_manage);
			ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
			m_manageText.setLayoutParams(ll);
			m_manageBtn.addView(m_manageText);
		}
		m_manageBtn.setOnTouchListener(mOnClickListener);

		m_title = new TextView(getContext());
		m_title.setText(R.string.material);
		m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		m_title.setTextColor(0xe6000000);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.CENTER;
		captionBar.addView(m_title, fl_lp);
	}

	private MgrUtils.MyRefreshCb m_refreshCB = new MgrUtils.MyRefreshCb(new MgrUtils.MyCB2()
	{
		@Override
		public void OnFinish()
		{
			m_isRefreshing = false;
			if(m_themeList != null && !m_hasRefreshUIComplete)
			{
				m_hasRefreshUIComplete = true;
				m_themeList.onPullDownRefreshComplete();
			}
			UpdateData();
		}
	});

	private Runnable m_overTimeRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			if(m_themeList != null && !m_hasRefreshUIComplete)
			{
				m_hasRefreshUIComplete = true;
				m_themeList.onPullDownRefreshComplete();
			}
		}
	};

	private OnAnimationClickListener mOnClickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(m_uiEnabled)
			{
				if(v == m_backBtn)
				{
					TongJi2.AddCountByRes(getContext(), R.integer.素材中心_返回);
					MyBeautyStat.onClickByRes(R.string.素材商店_素材商店首页_主页面_返回);
					m_site.OnBack(getContext());
				}
				else if(v == m_manageBtn)
				{
					TongJi2.AddCountByRes(getContext(), R.integer.素材中心_管理);
					MyBeautyStat.onClickByRes(R.string.素材商店_素材商店首页_主页面_素材管理);
					m_site.OnManagePageOpen(getContext());
				}
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

	private AdapterView.OnItemClickListener mThemeListItemClickListener = new AdapterView.OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if(position >= 2)
			{
				int realIndex = position - 2;
				openTheme(m_themeRes.get(realIndex));
			}
		}
	};

	public void UpdateData()
	{
		InitData();
		if(m_adapter != null)
		{
			m_adapter.setDatas(m_themeRes);
			m_adapter.notifyDataSetChanged();
		}
	}

	public void setBk(Bitmap bk)
	{
		setBackgroundColor(Color.TRANSPARENT);
		if(bk == null)
		{
			if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
			{
				this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
			}
			else
			{
				this.setBackgroundResource(R.drawable.login_tips_all_bk);
			}
		}
		else
		{
			this.setBackgroundDrawable(new BitmapDrawable(bk));
		}
	}


	/**
	 * @param params defID:int默认打开主题的id<br/>
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(params != null)
		{
			Object obj = params.get("defID");
			if(obj != null)
			{
				m_defID = (Integer)obj;
			}
		}
		if(m_themeRes != null && m_defID != -1)
		{
			int size = m_themeRes.size();
			BaseItemInfo info;
			for(int i = 0; i < size; i++)
			{
				info = m_themeRes.get(i);
				if(info != null && info.m_themeRes != null && info.m_themeRes.m_id == m_defID)
				{
					openTheme(info);
					break;
				}
			}
		}
	}

	public void SetPageCallback(OnPageCallback cb)
	{
		m_cb = cb;
	}

	public void UpdateThemeStyle(Bitmap bk)
	{
		if(bk == null)
		{
			if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
			{
				this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
			}
			else
			{
				this.setBackgroundResource(R.drawable.login_tips_all_bk);
			}
		}
		else
		{
			this.setBackgroundDrawable(new BitmapDrawable(bk));
		}
		if (m_backBtn != null) {
			ImageUtils.AddSkin(getContext(), m_backBtn);
		}
		if(m_manageIcon != null)
		{
			ImageUtils.AddSkin(getContext(), m_manageIcon);
		}
		if(m_manageText != null)
		{
			m_manageText.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		}
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		super.onPageResult(siteID, params);
		if(siteID == SiteID.THEME_INTRO && params != null)
		{
			Object obj = params.get("unlock");
			if(obj != null)
			{
				Boolean unLock = (Boolean)obj;
				if(!unLock && m_curItemInfo != null)
				{
					m_curItemInfo.m_lock = false;
				}
			}
		}
		this.requestFocus();
//		m_adapter.notifyDataSetChanged();
	}

	private void openTheme(BaseItemInfo info)
	{
		if(info == null) return;
		Utils.UrlTrigger(getContext(), info.m_themeRes.m_tjLink);
		TongJi2.AddCountById(Integer.toString(info.m_themeRes.m_tjId));

		m_curItemInfo = info;
		BaseRes res = info.m_themeRes;
		if(res != null)
		{
			m_defID = res.m_id;

			/*if(m_adapter != null)
			{
				m_adapter.releaseMem();
			}*/

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("theme_res", res);

//			if (info.m_themeRes.isOnlyFilter()) {
//				m_site.onFilterDetails(params);
//			} else {
				m_site.OpenThemeIntroPage(getContext(), params);
//			}
		}
	}

	@Override
	public void onBack()
	{
		if(m_cb != null)
		{
			m_cb.onBack();
		}
		else
		{
			TongJi2.AddCountByRes(getContext(), R.integer.素材中心_返回);
			MyBeautyStat.onClickByRes(R.string.素材商店_素材商店首页_主页面_返回);
			m_site.OnBack(getContext());
		}
	}

	@Override
	public void onClose()
	{
		super.onClose();
		this.setBackgroundColor(Color.CYAN);
		if(m_adapter != null)
		{
			m_adapter.releaseMem();
			m_adapter = null;
		}
		if(m_themeList != null)
		{
			m_themeList.releaseMem();
			m_themeList = null;
		}
		if(m_refreshCB != null)
		{
			m_refreshCB.ClearAll();
			m_refreshCB = null;
		}
		if(m_overTimeRunnable != null)
		{
			removeCallbacks(m_overTimeRunnable);
			m_overTimeRunnable = null;
		}

		TongJiUtils.onPageEnd(getContext(), R.string.素材中心);
		MyBeautyStat.onPageEndByRes(R.string.素材商店_素材商店首页_主页面);
	}

	public static interface OnPageCallback
	{
		public void onBack();
	}

	public class MyDoubleClickListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onDoubleTap(MotionEvent e)
		{
			if(m_themeList != null)
			{
				m_themeList.setSelection(1);
			}
			return super.onDoubleTap(e);
		}
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.素材中心);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.素材中心);
	}
}

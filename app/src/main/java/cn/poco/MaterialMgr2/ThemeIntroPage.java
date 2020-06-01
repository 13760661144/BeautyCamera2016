package cn.poco.MaterialMgr2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.ThemeIntroPageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.credits.Credit;
import cn.poco.filterManage.FilterItem;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FrameRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceMgr;
import cn.poco.resource.ThemeRes;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.pullToRefresh.PullToRefreshListView1;
import my.beautyCamera.R;

/**
 * 主题详情页
 */
public class ThemeIntroPage extends IPage
{
	//private static final String TAG = "主题详情";

	protected ThemeIntroPageSite m_site;
	private FrameLayout m_topBar;
	private ImageView m_backBtn;
	private TextView m_topicName; //主题名称

	private PullToRefreshListView1 m_listView;
	private BaseListAdapter m_listAdapter;
	private boolean m_isRefreshing = false;
	private boolean m_hasRefreshUIComplete = false;	//是否收起刷新的UI

	private FrameLayout m_bottomBar;
	private FrameLayout m_downloadAllBtn;
	private ImageView m_downloadAllBg;
	private ImageView m_downloadAllIcon;
	private TextView m_downloadAllText;

	private ThemeRes m_themeRes;

	private int m_topBarHeight;
	private int m_bottomBarHeight;

	private AlertDialog m_downloadFailedDlg; //下载失败提示对话框
	private AlertDialog m_checkDlg;
	private boolean m_isChecked = false;
	private int m_state;
	private boolean m_isAllDownload = false;

	private ArrayList<BaseItemInfo> m_infos;

	public ThemeIntroPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (ThemeIntroPageSite)site;

		initData();
		initUI();

		TongJiUtils.onPageStart(getContext(), R.string.素材中心_详情);
	}

	private void initData()
	{
		ShareData.InitData(getContext());
		m_topBarHeight = ShareData.PxToDpi_xhdpi(96);
		m_bottomBarHeight = ShareData.PxToDpi_xhdpi(100);

		if (DownloadMgr.getInstance() != null) {
			DownloadMgr.getInstance().AddDownloadListener(mDownloadListener);
		}
	}

	private void initUI()
	{
		if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
		{
			this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
		}
		else
		{
			this.setBackgroundResource(R.drawable.login_tips_all_bk);
		}
		FrameLayout.LayoutParams fl;

		//白色遮罩层
		View mask = new View(getContext());
		mask.setBackgroundColor(0xb2ffffff);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask.setLayoutParams(fl);
		addView(mask, 0);

		m_listView = new PullToRefreshListView1(getContext(), ShareData.PxToDpi_xhdpi(116), ShareData.PxToDpi_xhdpi(20));
		m_listView.setDivider(new ColorDrawable(0x00000000));
		m_listView.setCacheColorHint(0x00000000);
		m_listView.setVerticalScrollBarEnabled(false);
		m_listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		m_listView.setOnRefreshListener(new PullToRefreshListView1.OnRefreshListener()
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
				postDelayed(m_overTimeRunnable, ThemeListPage.OVER_TIME);
			}
		});
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenHeight - m_bottomBarHeight);
		m_listView.setLayoutParams(fl);
		this.addView(m_listView);

		m_listAdapter = new BaseListAdapter(getContext(), ShareData.m_screenWidth, ShareData.PxToDpi_xhdpi(88));
		m_listAdapter.setType(BaseListAdapter.THEME_INTRO);
		m_listAdapter.setDatas(m_infos);
		m_listAdapter.canClickItem(true);
		m_listAdapter.setOnBaseItemCallback(m_baseCB);
		m_listAdapter.setOnFilterItemClick(new FilterItem.OnFilterItemClick() {
			@Override
			public void onClickDetailItem(View view) {
				HashMap<String, Object> params = new HashMap<>();
				ThemeRes themeRes = null;
				for (int i = m_infos.size() - 1; i >= 0; i--) {
					if (m_infos.get(i).m_type == ResType.FILTER) {
						themeRes = m_infos.get(i).m_themeRes;
						break;
					}
				}
				params.put("theme_res", themeRes);
				m_site.onFilterDetails(getContext(), params);
			}
		});
		m_listView.setAdapter(m_listAdapter);
		m_listAdapter.notifyDataSetChanged();

		m_topBar = new FrameLayout(getContext());
		m_topBar.setBackgroundColor(0xf4ffffff);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, m_topBarHeight);
		fl.gravity = Gravity.TOP;
		this.addView(m_topBar, fl);
		{
			m_backBtn = new ImageView(getContext());
			m_backBtn.setImageResource(R.drawable.framework_back_btn);
			m_backBtn.setOnTouchListener(m_clickListener);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
			m_backBtn.setLayoutParams(fl);
			m_topBar.addView(m_backBtn);
			ImageUtils.AddSkin(getContext(), m_backBtn);

			m_topicName = new TextView(getContext());
			m_topicName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			m_topicName.setTextColor(0xe6000000);
			m_topicName.setGravity(Gravity.CENTER);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			m_topicName.setLayoutParams(fl);
			m_topBar.addView(m_topicName);
		}

		m_bottomBar = new FrameLayout(getContext());
		m_bottomBar.setBackgroundColor(0xf4ffffff);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, m_bottomBarHeight);
		fl.gravity = Gravity.BOTTOM;
		m_bottomBar.setLayoutParams(fl);
		this.addView(m_bottomBar);
		{
			m_downloadAllBtn = new FrameLayout(getContext());
			m_downloadAllBtn.setOnTouchListener(m_clickListener);
			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(270), ShareData.PxToDpi_xhdpi(76));
			fl.gravity = Gravity.CENTER;
			m_downloadAllBtn.setLayoutParams(fl);
			m_bottomBar.addView(m_downloadAllBtn);
			{
				m_downloadAllBg = new ImageView(getContext());
				m_downloadAllBg.setScaleType(ImageView.ScaleType.FIT_XY);
				m_downloadAllBg.setImageResource(R.drawable.new_material4_downloadall);
				ImageUtils.AddSkin(getContext(), m_downloadAllBg);
				fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(270), ShareData.PxToDpi_xhdpi(76));
				m_downloadAllBg.setLayoutParams(fl);
				m_downloadAllBtn.addView(m_downloadAllBg);

				LinearLayout layout = new LinearLayout(getContext());
				layout.setGravity(Gravity.CENTER);
				fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(270), ShareData.PxToDpi_xhdpi(76));
				fl.gravity = Gravity.CENTER;
				layout.setLayoutParams(fl);
				m_downloadAllBtn.addView(layout);
				{
					LinearLayout.LayoutParams ll;
					m_downloadAllIcon = new ImageView(getContext());
					m_downloadAllIcon.setImageResource(R.drawable.new_material4_download_icon);
					ll= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					m_downloadAllIcon.setLayoutParams(ll);
					layout.addView(m_downloadAllIcon);

					m_downloadAllText = new TextView(getContext());
					m_downloadAllText.setTextColor(Color.WHITE);
					m_downloadAllText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					m_downloadAllText.setText(R.string.material_download_all);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.leftMargin = ShareData.PxToDpi_xhdpi(8);
					m_downloadAllText.setLayoutParams(ll);
					layout.addView(m_downloadAllText);
				}
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.download_failed_title);
		builder.setMessage(R.string.download_failed_content);
		builder.setPositiveButton(R.string.know, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				m_downloadFailedDlg.dismiss();
			}
		});
		m_downloadFailedDlg = builder.create();

		builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.download_failed_title);
		builder.setMessage(R.string.net_weak_tip);
		builder.setPositiveButton(R.string.know, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(m_checkDlg != null)
				{
					m_checkDlg.dismiss();
				}
			}
		});
		m_checkDlg = builder.create();
	}

	private OnAnimationClickListener m_clickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == m_backBtn)
			{
				onBack();
			}
			else if(v == m_downloadAllBtn)
			{
				if(m_infos != null)
				{
					m_isAllDownload = true;

					boolean flag = false;
					int size = m_infos.size();
					for(int i = 0; i < size; i ++)
					{
						BaseItemInfo info = m_infos.get(i);
						View view = m_listView.findViewWithTag(info.m_uri);
						if(info.m_state == BaseItemInfo.CONTINUE || info.m_state == BaseItemInfo.PREPARE)
						{
							if(info.m_lock && TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + info.m_themeRes.m_id))
							{
								unLock(view, info);
								break;
							}
							info.m_lock = false;
							flag = true;
							downloadGroupMgr(view, info);
						}
					}
					if(flag)
					{
						MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.全部, false, m_themeRes.m_tjId + "");
						m_state = BaseItemInfo.LOADING;
						setDownloadBtnState(m_state);
					}
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

	public void checkPageDownloadState()
	{
		if(null == m_infos)
			return;
		int count = m_infos.size();
		if(count == 0)
		{
			m_state = BaseItemInfo.PREPARE;
			setDownloadBtnState(m_state);
			return;
		}
		int flag = -1;
		int state1 = 0; //下载中
		int state2 = 0; //下载完成
		BaseItemInfo itemInfo;
		for(int i = 0; i < count; i++)
		{
			itemInfo = m_infos.get(i);
			itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
			if (itemInfo.m_ids != null && itemInfo.m_ids.length > 0) {
				itemInfo.m_progress = 100 * MgrUtils.getM_completeCount() / itemInfo.m_ids.length;
			}
			flag = itemInfo.m_state;
			if(flag == BaseItemInfo.LOADING)
				state1++;
			else if(flag == BaseItemInfo.COMPLETE)
			{
				state2++;
			}
		}
		if(state1 != 0 && ((state1 + state2) == count))
		{
			m_state = BaseItemInfo.LOADING;
			if(m_themeRes != null && MgrUtils.unLockTheme(m_themeRes.m_id) != null)
			{
				TagMgr.SetTag(getContext(), Tags.THEME_UNLOCK + m_themeRes.m_id);
			}
			setDownloadBtnState(m_state);
			return;
		}
		else if(state2 == count)
		{
			m_state = BaseItemInfo.COMPLETE;
			if(m_themeRes != null && MgrUtils.unLockTheme(m_themeRes.m_id) != null)
			{
				TagMgr.SetTag(getContext(), Tags.THEME_UNLOCK + m_themeRes.m_id);
			}
			String params = Credit.APP_ID + Credit.THEME + m_themeRes.m_id;
			Credit.CreditIncome(params, MyFramework2App.getInstance().getApplicationContext(), R.integer.积分_首次使用新素材);
			setDownloadBtnState(m_state);
			return;
		}
		else
		{
			m_state = BaseItemInfo.PREPARE;
			setDownloadBtnState(m_state);
			return;
		}
	}

	private void setDownloadBtnState(int state)
	{
		if(state == BaseItemInfo.LOADING)
		{
			m_downloadAllBg.setVisibility(GONE);
			m_downloadAllBtn.setOnTouchListener(null);
			m_downloadAllBtn.setBackgroundColor(0x00ffffff);
			m_downloadAllIcon.setVisibility(GONE);
			m_downloadAllText.setTextColor(0xff07c34e);
			m_downloadAllText.setText(R.string.material_downloading);
			LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams)m_downloadAllText.getLayoutParams();
			ll.leftMargin = ShareData.PxToDpi_xhdpi(16);
		}
		else if(state == BaseItemInfo.COMPLETE)
		{
			m_downloadAllBg.setVisibility(GONE);
			m_downloadAllIcon.setVisibility(VISIBLE);
			m_downloadAllBtn.setOnTouchListener(null);
			m_downloadAllBtn.setBackgroundColor(0x00ffffff);
			m_downloadAllIcon.setImageResource(R.drawable.new_material4_downloaded_icon);
			m_downloadAllText.setTextColor(0xff07c34e);
			m_downloadAllText.setText(R.string.material_download_complete);
			LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams)m_downloadAllText.getLayoutParams();
			ll.leftMargin = ShareData.PxToDpi_xhdpi(16);
		}
		else
		{
			m_downloadAllBg.setVisibility(VISIBLE);
			m_downloadAllIcon.setVisibility(VISIBLE);
			m_downloadAllBtn.setOnTouchListener(m_clickListener);
			m_downloadAllIcon.setImageResource(R.drawable.new_material4_download_icon);
			m_downloadAllText.setTextColor(Color.WHITE);
			m_downloadAllText.setText(R.string.material_download_all);
			LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams)m_downloadAllText.getLayoutParams();
			ll.leftMargin = ShareData.PxToDpi_xhdpi(8);
		}
	}

	/**
	 * 检查网络状态
	 */
	private void checkNetState()
	{
		if(m_state != BaseItemInfo.COMPLETE && m_checkDlg != null)
		{
			if(!isNetConnected(getContext()))
			{
				m_checkDlg.show();
				m_isChecked = true;
				return;
			}
		}
	}

	/**
	 * 检查网络状态
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if(info != null)
		{
			return info.isAvailable();
		}
		return false;
	}

	protected UnLockMgr m_unlockView;
	private void unLock(final View view, final BaseItemInfo info)
	{
		TongJi2.AddCountByRes(getContext(), R.integer.素材中心_解锁);
		if(m_unlockView != null && m_unlockView.IsRecycle())
		{
			m_unlockView = null;
		}
		if(m_unlockView == null)
		{
			m_unlockView = new UnLockMgr(getContext(), MgrUtils.unLockTheme(info.m_themeRes.m_id), new UnLockMgr.Callback()
			{
				@Override
				public void UnlockSuccess(BaseRes res)
				{
					if(m_isAllDownload)
					{
						m_clickListener.onAnimationClick(m_downloadAllBtn);
						return;
					}
					TagMgr.SetTag(getContext(), Tags.THEME_UNLOCK + res.m_id);
					TongJi2.AddCountByRes(getContext(), R.integer.素材中心_解锁_成功);
					Toast.makeText(getContext(), R.string.unlock_success, Toast.LENGTH_SHORT).show();
					info.m_lock = false;
					if(info != null)
					{
						MgrUtils.AddDownloadTj(info.m_type, info.m_themeRes);
					}
					downloadGroupMgr(view, info);
				}

				@Override
				public void OnCloseBtn()
				{

				}

				@Override
				public void OnBtn(int state)
				{

				}

				@Override
				public void OnClose()
				{
					m_unlockView.OnCancel(true);
				}

				@Override
				public void OnLogin()
				{
					m_site.OnLogin(getContext());
				}
			});
			m_unlockView.Create();
			m_unlockView.SetBk(CommonUtils.GetScreenBmp((Activity)getContext(), ShareData.m_screenWidth / 4, ShareData.m_screenHeight / 4), true);
			m_unlockView.Show(ThemeIntroPage.this);
		}
	}

	public void downloadGroupMgr(View view, BaseItemInfo info)
	{
		if(null == info)
			return;
		info.m_state = BaseItemInfo.LOADING;
		if(view != null)
		{
			if (view instanceof FilterItem) {
				((FilterItem)view).setDownloadBtnState(BaseItemInfo.LOADING, info.m_progress);
			} else if(view instanceof BaseItem){
				((BaseItem)view).setDownloadBtnState(BaseItemInfo.LOADING, info.m_progress);
			}
		}

		checkPageDownloadState();
		if(info.m_ress == null || info.m_ress.size() < info.m_ids.length)
		{
			IDownload[] ress = new IDownload[info.m_ids.length + 1];
			int[] ressIds = new int[info.m_ids.length];
			for(int i = 0; i < info.m_ids.length; i++)
			{
				BaseRes baseRes = new FrameRes();
				baseRes.m_id = info.m_ids[i];
				baseRes.m_type = BaseRes.TYPE_NETWORK_URL;
				ress[i] = baseRes;
				ressIds[i] = baseRes.m_id;
			}
			ress[info.m_ids.length] = info.m_themeRes;
			AbsDownloadMgr.DownloadGroupInfo myInfo = DownloadMgr.getInstance().DownloadRes(ress, false, m_downloadCB);
			info.m_downloadID = myInfo.m_id;
			m_downloadCB.setDatas(ressIds, info.m_type, info.m_themeRes.m_id, info.m_downloadID);
		}
		else
		{
			ArrayList<? extends BaseRes> itemInfos = info.m_ress;
			int len = itemInfos.size();
			IDownload[] ress;
			ress = new IDownload[len + 1];
			int[] ressIds = new int[len];
			for(int i = 0; i < len; i++)
			{
				BaseRes itemInfo = itemInfos.get(i);
				if(itemInfo != null)
				{
					ress[i] = itemInfo;
					ressIds[i] = itemInfo.m_id;
				}
			}
			//主题必须放最后
			ress[len] = info.m_themeRes; //主题也需要下载
			AbsDownloadMgr.DownloadGroupInfo myInfo = DownloadMgr.getInstance().DownloadRes(ress, false, m_downloadCB);
			info.m_downloadID = myInfo.m_id;
			m_downloadCB.setDatas(ressIds, info.m_type, info.m_themeRes.m_id, info.m_downloadID);
		}

	}

	private MgrUtils.MyRefreshCb m_refreshCB = new MgrUtils.MyRefreshCb(new MgrUtils.MyCB2()
	{
		@Override
		public void OnFinish()
		{
			m_isRefreshing = false;
			if(m_listView != null && !m_hasRefreshUIComplete)
			{
				m_hasRefreshUIComplete = true;
				m_listView.onPullDownRefreshComplete();
			}
			updatePageInfos(m_themeRes);
		}
	});

	private Runnable m_overTimeRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			if(m_listView != null && !m_hasRefreshUIComplete)
			{
				m_hasRefreshUIComplete = true;
				m_listView.onPullDownRefreshComplete();
			}
		}
	};



	private BaseItem.OnBaseItemCallback m_baseCB = new BaseItem.OnBaseItemCallback()
	{
		@Override
		public void OnDownload(View view, BaseItemInfo info, boolean clickDownloadBtn)
		{
			if(info.m_lock && TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + info.m_themeRes.m_id))
			{
				unLock(view, info);
				return;
			}
			if(info.m_ress == null || info.m_ress.size() == 0)
			{
				if(info.m_ids != null && info.m_ids.length > 0)
				{
					if(m_isChecked == false)
					{
						checkNetState();
					}
				}
			}
			info.m_lock = false;

			if(info != null)
			{
				MgrUtils.AddDownloadTj(info.m_type, info.m_themeRes);
			}
			downloadGroupMgr(view, info);
		}

		@Override
		public void OnUse(BaseItemInfo info, int index)
		{
			if(info != null && info.m_ress != null && info.m_ress.size() > index)
			{
				HashMap<String, Object> params = new HashMap<>();
				params.put("type", info.m_type);
				int uri = info.m_ress.get(index).m_id;
				if(info.m_type == ResType.DECORATE)
				{
					uri = info.m_themeRes.m_id;
				}
				params.put("id", uri);
				m_site.OnResourceUse(getContext(), params);
			}
		}

		@Override
		public void OnCheck(BaseItemInfo info)
		{

		}
	};

	private MgrUtils.MyDownloadCB m_downloadCB = new MgrUtils.MyDownloadCB(new MgrUtils.MyCB()
	{
		@Override
		public void OnFail(int downloadId, IDownload res)
		{
		}

		@Override
		public void OnGroupFailed(int downloadId, IDownload[] resArr)
		{
			m_isAllDownload = false;
			BaseItemInfo info = null;
			if(m_infos != null)
			{
				int size = m_infos.size();
				for(int i = 0; i < size; i ++)
				{
					if(m_infos.get(i).m_downloadID == downloadId)
					{
						info = m_infos.get(i);
						break;
					}
				}
			}
			if(info != null)
			{
				if(info.m_progress > 0 && info.m_progress < info.m_ress.size())
				{
					info.m_state = BaseItemInfo.CONTINUE;
				}
				else
				{
					info.m_state = BaseItemInfo.PREPARE;
				}
				if(m_downloadFailedDlg != null)
				{
					m_downloadFailedDlg.show();
				}
				checkPageDownloadState();
				View view = m_listView.findViewWithTag(info.m_uri);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).setDownloadBtnState(info.m_state, 0);
				} else if (view instanceof FilterItem) {
					((FilterItem)view).setDownloadBtnState(info.m_state, 0);
				}
			}
		}

		@Override
		public void OnComplete(int downloadId, IDownload res)
		{
		}

		@Override
		public void OnProgress(int downloadId, IDownload[] resArr, int progress)
		{
			BaseItemInfo info = null;
			if(m_infos != null)
			{
				int size = m_infos.size();
				for(int i = 0; i < size; i ++)
				{
					if(m_infos.get(i).m_downloadID == downloadId)
					{
						info = m_infos.get(i);
						break;
					}
				}
			}
			if(info != null)
			{
				info.m_state = BaseItemInfo.LOADING;
				info.m_progress = progress;

				View view = m_listView.findViewWithTag(info.m_uri);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).setDownloadBtnState(info.m_state, progress);
				} else if (view instanceof FilterItem) {
					((FilterItem)view).setDownloadBtnState(info.m_state, progress);
				}
			}
		}

		@Override
		public void OnGroupComplete(int downloadId, IDownload[] resArr)
		{
			BaseItemInfo info = null;
			if(m_infos != null)
			{
				int size = m_infos.size();
				for(int i = 0; i < size; i ++)
				{
					if(m_infos.get(i).m_downloadID == downloadId)
					{
						info = m_infos.get(i);
						break;
					}
				}
			}
			if(info != null)
			{
				info.m_state = BaseItemInfo.COMPLETE;
				info.m_progress = resArr.length;
				checkPageDownloadState();
				String params = "";
				switch(info.m_type)
				{
					case FRAME:
						params = Credit.APP_ID + Credit.FRAME + info.m_ids[0];
						break;
					case DECORATE:
						params = Credit.APP_ID + Credit.DECORATE + info.m_ids[0];
						break;
					case MAKEUP_GROUP:
						params = Credit.APP_ID + Credit.MAKEUP + info.m_ids[0];
						break;
					case GLASS:
						params = Credit.APP_ID + Credit.GLASS + info.m_ids[0];
						break;
					case MOSAIC:
						params = Credit.APP_ID + Credit.MOSAIC + info.m_ids[0];
						break;
					case BRUSH:
						params = Credit.APP_ID + Credit.BRUSH + info.m_ids[0];
						break;
					case FILTER:
						params = Credit.APP_ID + Credit.FILTER + info.m_ids[0];
						break;
				}

				Credit.CreditIncome(params, MyFramework2App.getInstance().getApplicationContext(), R.integer.积分_首次使用新素材);
				params = Credit.APP_ID + Credit.THEME + info.m_themeRes.m_id;
				Credit.CreditIncome(params, MyFramework2App.getInstance().getApplicationContext(), R.integer.积分_首次使用新素材);
				View view = m_listView.findViewWithTag(info.m_uri);
//				Log.i("themeIntro", "view: " + view);
//				Log.i("themeIntro", "info.m_uri: " + info.m_uri);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).setDownloadBtnState(info.m_state, info.m_progress);
				} else if (view instanceof FilterItem) {
					((FilterItem)view).setDownloadBtnState(info.m_state, info.m_progress);
				}
			}
		}
	});

	private AbsDownloadMgr.DownloadListener mDownloadListener = new AbsDownloadMgr.DownloadListener() {
		@Override
		public void OnDataChange(int resType, int downloadId, IDownload[] resArr) {
			if(resType == ResType.FILTER.GetValue())
			{
				BaseItemInfo info = null;
				BaseItemInfo info1 = null;
				if(m_infos != null)
				{
					int size = m_infos.size();
					for(int i = 0; i < size; i ++)
					{
						if(m_infos.get(i).m_type.GetValue() == resType)
						{
							info1 = m_infos.get(i);
						}
						if(m_infos.get(i).m_downloadID == downloadId)
						{
							info = m_infos.get(i);
							break;
						}
					}

					if(info == null && m_downloadCB != null && info1 != null)
					{
						info1.m_downloadID = downloadId;
						m_downloadCB.OnGroupComplete(downloadId, resArr);
					}
				}
			}
		}
	};

	/**
	 * @param params
	 * theme_res ThemeRes
	 * cur_item ThemeItem(返回时用到)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void SetData(HashMap<String, Object> params)
	{
		Object o;
		if(params != null)
		{
			o = params.get("theme_res");
			if(o != null)
			{
				m_themeRes = (ThemeRes)o;
			}
			/*o = params.get("cur_item");
			if(o != null)
			{
				m_themeItem = (ThemeItem)o;
			}*/
		}
		if(m_themeRes != null)
		{
			m_topicName.setText(m_themeRes.m_name);

			updatePageInfos(m_themeRes);

			m_listAdapter.setOnBaseItemCallback(m_baseCB);
			checkNetState();
		}
	}

	protected void updatePageInfos(ThemeRes res)
	{
		if(m_infos != null)
		{
			m_infos.clear();
			m_infos = null;
		}
		if(res != null)
		{
			m_infos = MgrUtils.GetThemeInfos(getContext(), res);
			checkPageDownloadState();
			m_listAdapter.setDatas(m_infos);
			m_listAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClose()
	{
		super.onClose();
		this.setBackgroundColor(0xff000000);
		if(m_downloadCB != null)
		{
			m_downloadCB.ClearAll();
			m_downloadCB = null;
		}
		if(m_listView != null)
		{
			int len = m_listView.getChildCount();
			for(int i = 1; i < len; i ++)
			{
				View view = m_listView.getChildAt(i);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).releaseMem();
				}
			}
			m_listView.setAdapter(null);
			m_listView.releaseMem();
			m_listView = null;
		}
		if(m_listAdapter != null)
		{
			m_listAdapter.ReleaseMem();
			m_listAdapter = null;
			m_baseCB = null;
		}
		if(m_infos != null)
		{
			m_infos.clear();
			m_infos = null;
		}
		if(m_downloadFailedDlg != null)
		{
			m_downloadFailedDlg.dismiss();
			m_downloadFailedDlg = null;
		}
		if(m_checkDlg != null)
		{
			m_checkDlg.dismiss();
			m_checkDlg = null;
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
		this.removeAllViews();
		this.clearFocus();

		if (DownloadMgr.getInstance() != null) {
			DownloadMgr.getInstance().RemoveDownloadListener(mDownloadListener);
		}

		TongJiUtils.onPageEnd(getContext(), R.string.素材中心_详情);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.素材中心_详情);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.素材中心_详情);
		super.onResume();
	}

	@Override
	public void onBack()
	{
		if(m_unlockView != null && !m_unlockView.IsRecycle())
		{
			m_unlockView.OnCancel(true);
			return;
		}
		HashMap<String, Object> params = new HashMap<>();
		boolean unLock = false;
		if(MgrUtils.unLockTheme(m_themeRes.m_id) != null &&
				TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + m_themeRes.m_id))
		{
			unLock = true;
		}
		params.put("unlock", unLock);
		m_site.OnBack(getContext(), params);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		super.onPageResult(siteID, params);
		if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW)
		{
			if(m_unlockView != null)
			{
				m_unlockView.UpdateCredit();
			}
		}
		if(siteID == SiteID.ALBUM)
		{
			checkPageDownloadState();
			m_listAdapter.notifyDataSetChanged();
		}
	}
}

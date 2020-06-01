package cn.poco.MaterialMgr2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.credits.Credit;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.BrushResMgr2;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameRes;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.IDownload;
import cn.poco.resource.LockRes;
import cn.poco.resource.MakeupComboResMgr2;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.resource.ResType;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * 下载更多页
 */
public class DownloadMorePage extends IPage
{
	//private static final String TAG = "下载更多";

	protected DownloadMorePageSite m_site;

	private ListView m_downloadList;
	private FrameLayout m_topBar;
	private ImageView m_backBtn;
	private TextView m_groupName;
	private TextView m_prompt;
	private LinearLayout m_manageBtn;
	private ImageView m_manageIcon;
	private TextView m_manageText;

	private int m_topBarHeight;
	private int m_leftMargin;
	private ResType m_type;
	private ArrayList<BaseItemInfo> m_infos;
	private BaseListAdapter m_adapter;
	private AlertDialog m_downloadFailedDlg;    //下载失败提示对话框

	private boolean m_isDelete = false;

	public DownloadMorePage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (DownloadMorePageSite)site;
		InitData();
		InitUI();

		TongJiUtils.onPageStart(getContext(), R.string.下载更多);
	}

	private void InitData()
	{
		ShareData.InitData((Activity)getContext());
		m_topBarHeight = ShareData.PxToDpi_xhdpi(96);
		m_leftMargin = ShareData.PxToDpi_xhdpi(20);
	}

	private void InitUI()
	{
		FrameLayout.LayoutParams fl;
		if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
		{
			this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
		}
		else
		{
			this.setBackgroundResource(R.drawable.login_tips_all_bk);
		}
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});

		//白色遮罩层
		View mask = new View(getContext());
		mask.setBackgroundColor(0x99ffffff);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask.setLayoutParams(fl);
		addView(mask, 0);

		m_downloadList = new ListView(getContext());
		m_downloadList.setVerticalScrollBarEnabled(false);
		m_downloadList.setDivider(new ColorDrawable(0x00000000));
		m_downloadList.setDividerHeight(ShareData.PxToDpi_xhdpi(20));
		m_downloadList.setCacheColorHint(0x00000000);
		m_downloadList.setSelector(new ColorDrawable(Color.TRANSPARENT));
//		m_downloadList.setPadding(0, m_topBarHeight, 0, 0);
		fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth - 2 * m_leftMargin, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP;
		fl.leftMargin = m_leftMargin;
		m_downloadList.setLayoutParams(fl);
		this.addView(m_downloadList);

		m_adapter = new BaseListAdapter(getContext(), ShareData.m_screenWidth - 2 * m_leftMargin, ShareData.PxToDpi_xhdpi(70));
		m_adapter.setDatas(m_infos);
		m_adapter.showLock(true);
		m_adapter.canDrag(true);
		m_adapter.showCheckBox(false);
		m_adapter.canClickItem(true);
		m_adapter.canClickDownload(false);
		m_adapter.setOnBaseItemCallback(m_baseCB);
		m_downloadList.setAdapter(m_adapter);

		m_topBar = new FrameLayout(getContext());
		m_topBar.setBackgroundColor(0xf4FFFFFF);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, m_topBarHeight);
		fl.gravity = Gravity.TOP;
		m_topBar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(m_downloadList != null)
				{
					m_downloadList.smoothScrollToPosition(0);
				}
			}
		});
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

			m_groupName = new TextView(getContext());
			m_groupName.setTextColor(0xe6000000);
			m_groupName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			m_groupName.setGravity(Gravity.CENTER);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
			m_groupName.setLayoutParams(fl);
			m_topBar.addView(m_groupName);

			m_manageBtn = new LinearLayout(getContext());
			m_manageBtn.setGravity(Gravity.CENTER);
			m_manageBtn.setOrientation(LinearLayout.HORIZONTAL);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
			fl.rightMargin = ShareData.PxToDpi_xhdpi(28);
			m_topBar.addView(m_manageBtn, fl);
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
			m_manageBtn.setOnTouchListener(m_clickListener);
		}

		m_prompt = new TextView(getContext());
		m_prompt.setVisibility(View.GONE);
		m_prompt.setTextColor(0x99000000);
		m_prompt.setText(R.string.material_download_none_tip);
		m_prompt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		m_prompt.setGravity(Gravity.CENTER);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		m_prompt.setLayoutParams(fl);
		this.addView(m_prompt);

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
	}

	private OnAnimationClickListener m_clickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == m_manageBtn)
			{
				HashMap<String, Object> params = new HashMap<>();
				params.put("type", m_type);
				m_site.OnManagePageOpen(getContext(), params);
			}
			else if(v == m_backBtn)
			{
				onBack();
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

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		super.onPageResult(siteID, params);
		if(siteID == SiteID.RES_MANAGE && params != null)
		{
			Object o = params.get("is_delete");
			if(o != null)
			{
				m_isDelete = (Boolean)o;
			}
			if(m_isDelete)
			{
				m_infos = readRess(m_type);
				if(null != m_infos && m_infos.size() > 0)
				{
					m_adapter.setDatas(m_infos);
					m_prompt.setVisibility(View.GONE);
				}
				else
				{
					m_prompt.setVisibility(View.VISIBLE);
				}
				m_adapter.notifyDataSetChanged();
			}
		}
		if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW)
		{
			if(m_unlockView != null)
			{
				m_unlockView.UpdateCredit();
			}
		}
	}

	/**
	 * @param params type ResType
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(params != null)
		{
			if(params.get("type") != null)
			{
				m_type = (ResType)params.get("type");
			}
		}

		if(m_type != null)
		{
			m_infos = readRess(m_type);
			if(null != m_infos && m_infos.size() > 0)
			{
				m_adapter.setOnBaseItemCallback(m_baseCB);
				m_adapter.setDatas(m_infos);
				m_adapter.notifyDataSetChanged();

				m_prompt.setVisibility(View.GONE);
			}
			else
			{
				m_prompt.setVisibility(View.VISIBLE);
			}
			switch(m_type)
			{
				case DECORATE:
				{
					m_groupName.setText(R.string.material_decorate);
					break;
				}
				case FRAME:
				{
					m_groupName.setText(R.string.material_frame);
					break;
				}
				case FRAME2:
				{
					m_groupName.setText(R.string.material_frame2);
					break;
				}
				case MAKEUP_GROUP:
				{
					m_groupName.setText(R.string.material_makeup);
					break;
				}
				case MOSAIC:
				{
					m_groupName.setText(R.string.material_mosaic);
					break;
				}
				case GLASS:
				{
					m_groupName.setText(R.string.material_glass);
					break;
				}
				case BRUSH:
				{
					m_groupName.setText(R.string.brushpage_brush);
					break;
				}
			}
		}
	}

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
			HashMap<String, Object> params = new HashMap<>();
			params.put(DownloadMorePageSite.DOWNLOAD_MORE_TYPE, info.m_type);
			int uri = -1;
			if(info.m_type == ResType.DECORATE)
			{
				uri = info.m_themeRes.m_id;
			}
			else
			{
				if(index >= 0 && index < info.m_ress.size())
				{
					uri = info.m_ress.get(index).m_id;
				}
			}
			params.put(DownloadMorePageSite.DOWNLOAD_MORE_ID, uri);
			m_site.OnResourceUse(getContext(), params);
		}

		@Override
		public void OnCheck(BaseItemInfo info)
		{

		}
	};

	public void downloadGroupMgr(View view, BaseItemInfo info)
	{
		if(null == info) return;
		info.m_state = BaseItemInfo.LOADING;
		if(view != null)
		{
			((BaseItem)view).setDownloadBtnState(BaseItemInfo.LOADING, info.m_progress);
		}
		if(info.m_ress == null || info.m_ress.size() < info.m_ids.length) // FIXME: 2017/4/11 info.m_ids 为空
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

	private MgrUtils.MyDownloadCB m_downloadCB = new MgrUtils.MyDownloadCB(new MgrUtils.MyCB()
	{

		@Override
		public void OnFail(int downloadId, IDownload res)
		{
		}

		@Override
		public void OnGroupFailed(int downloadId, IDownload[] resArr)
		{
			BaseItemInfo info = null;
			if(m_infos != null)
			{
				int size = m_infos.size();
				for(int i = 0; i < size; i++)
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
				View view = m_downloadList.findViewWithTag(info.m_uri);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).setDownloadBtnState(info.m_state, 0);
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
				for(int i = 0; i < size; i++)
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

				View view = m_downloadList.findViewWithTag(info.m_uri);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).setDownloadBtnState(info.m_state, progress);
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
				for(int i = 0; i < size; i++)
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
				String params = "";
				switch(info.m_type)
				{
					case FRAME:
					case FRAME2:
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
				}
				Credit.CreditIncome(params, MyFramework2App.getInstance().getApplicationContext(), R.integer.积分_首次使用新素材);
				MgrUtils.AddThemeCredit(getContext(), info.m_themeRes);
				View view = m_downloadList.findViewWithTag(info.m_uri);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).setDownloadBtnState(info.m_state, info.m_progress);
				}
			}
		}
	});

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
					TongJi2.AddCountByRes(getContext(), R.integer.素材中心_解锁_成功);
					if (info != null)
					{
						info.m_lock = false;
					}
					if (m_adapter != null)
					{
						m_adapter.notifyDataSetChanged();
					}
					if (m_baseCB != null)
					{
						m_baseCB.OnDownload(view, info, true);
					}
					Toast.makeText(getContext(), R.string.unlock_success, Toast.LENGTH_SHORT).show();
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
			m_unlockView.Show(DownloadMorePage.this);
		}
	}

	private ArrayList<BaseItemInfo> readRess(ResType type)
	{
		ArrayList<BaseItemInfo> out = new ArrayList<BaseItemInfo>();

		ArrayList<ThemeRes> themeRess = ThemeResMgr2.getInstance().GetAllResArr();

		ThemeRes themeRes;
		ArrayList<? extends BaseRes> ress = null;
		if(themeRess != null && themeRess.size() > 0)
		{
			int themeLen = themeRess.size();
			for(int i = 0; i < themeLen; i++)
			{
				themeRes = themeRess.get(i);
				if(themeRes != null && !themeRes.m_isHide)
				{
					switch(type)
					{
						case FRAME:
						{
							ress = FrameResMgr2.getInstance().GetResArr2(themeRes.m_frameIDArr, false);
							break;
						}
						case FRAME2:
						{
							ress = FrameExResMgr2.getInstance().GetResArr(themeRes.m_sFrameIDArr, false);
							break;
						}
						case MAKEUP_GROUP:
						{
							ress = MakeupComboResMgr2.getInstance().GetResArr(themeRes.m_makeupIDArr);
							break;
						}
						case DECORATE:
						{
							ress = DecorateResMgr2.getInstance().GetResArr2(themeRes.m_decorateIDArr, false);
							break;
						}
						case MOSAIC:
						{
							ress = MosaicResMgr2.getInstance().GetResArr(themeRes.m_mosaicIDArr);
							break;
						}
						case GLASS:
						{
							ress = GlassResMgr2.getInstance().GetResArr(themeRes.m_glassIDArr);
							break;
						}
						case BRUSH:
						{
							ress = BrushResMgr2.getInstance().GetResArr(themeRes.m_brushIDArr, false);
							break;
						}
					}
					setListInfos(out, ress, themeRes, type);
				}
			}
		}
		return out;
	}

	private void setListInfos(ArrayList<BaseItemInfo> out, ArrayList<? extends BaseRes> ress, ThemeRes themeRes, ResType type)
	{
		if(null == ress || ress.size() == 0) return;
		int state = MgrUtils.checkGroupDownloadState(ress, null);
		if(state != BaseItemInfo.COMPLETE)
		{
			BaseItemInfo itemInfo = new BaseItemInfo(themeRes, type);
			itemInfo.m_name = themeRes.m_name;
			itemInfo.m_uri = themeRes.m_id;
			itemInfo.m_ress = new ArrayList<BaseRes>();
			itemInfo.m_ress.addAll(ress);
			itemInfo.m_state = state;
			itemInfo.m_progress = MgrUtils.getM_completeCount() / ress.size() * 100;
			LockRes lockRes = MgrUtils.unLockTheme(themeRes.m_id);
			if(lockRes != null && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE && TagMgr.CheckTag(getContext(), Tags.THEME_UNLOCK + themeRes.m_id))
			{
				itemInfo.m_lock = true;
			}
			itemInfo.isAllShow = false;
			out.add(itemInfo);
		}
	}

	@Override
	public void onClose()
	{
		super.onClose();
		this.setBackgroundColor(Color.CYAN);
		if(m_downloadCB != null)
		{
			m_downloadCB.ClearAll();
			m_downloadCB = null;
		}
		if(m_downloadList != null)
		{
			int len = m_downloadList.getChildCount();
			for(int i = 1; i < len; i++)
			{
				View view = m_downloadList.getChildAt(i);
				if(view != null && view instanceof BaseItem)
				{
					((BaseItem)view).releaseMem();
				}
			}
			m_downloadList.setAdapter(null);
		}
		if(m_adapter != null)
		{
			m_adapter.ReleaseMem();
			m_adapter = null;
		}
		if(m_infos != null)
		{
			m_infos.clear();
			m_infos = null;
		}

		TongJiUtils.onPageEnd(getContext(), R.string.下载更多);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.下载更多);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.下载更多);
		super.onResume();
	}

	@Override
	public void onBack()
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put(DownloadMorePageSite.DOWNLOAD_MORE_DEL, m_isDelete);
		params.put(DownloadMorePageSite.DOWNLOAD_MORE_TYPE, m_type);
		m_site.OnBack(getContext(), params);
	}

}

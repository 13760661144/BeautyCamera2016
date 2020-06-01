package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.ManagePageIntroSite;
import cn.poco.MaterialMgr2.site.ManagePageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.filterManage.FilterManagePage;
import cn.poco.filterManage.site.FilterManageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.BrushResMgr2;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.GroupRes;
import cn.poco.resource.MakeupComboResMgr2;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.resource.ResType;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * 素材中心管理页
 */
public class ManagePage extends IPage
{
	//private static final String TAG = "素材管理";
	private static final int MANAGE = 0;
	private static final int MANAGE_INSTRO = 1;
	private static final int MANAGE_FILTER_INSTRO = 2;

	private ManagePageSite m_site;

	private ImageView m_backBtn;
	private TextView m_topBar;

	//从"下载更多"进来的时候用
	private int m_curSelModule = MANAGE;
	private ResType m_type;//"下载更多"所在的分类
	private boolean m_isDelete = false;//"下载更多"所在的分类是否有删除过

	private LinearLayout m_container;
	private ManageItem m_makeupItem;
	private ManageItem m_decorateItem;
	private ManageItem m_frameItem;
	private ManageItem m_simpleFrameItem;
	private ManageItem m_glassItem;
	private ManageItem m_mosaicItem;
	private ManageItem m_brushItem;
	private ManageItem m_filterItem;

	private boolean m_uiEnabled = true;

	public ManagePage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (ManagePageSite)site;
		initUI();

		TongJiUtils.onPageStart(getContext(), R.string.素材中心_管理);
		MyBeautyStat.onPageStartByRes(R.string.素材商店_素材管理_主页面);
	}

	private void initUI()
	{
		ShareData.InitData(getContext());
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});

//		this.setBackgroundResource(R.drawable.new_material_bk);
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});
		if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
		{
			this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath, null)));
		}
		else
		{
			this.setBackgroundResource(R.drawable.login_tips_all_bk);
		}
		FrameLayout.LayoutParams fl_lp;
		//白色遮罩层
		View mask = new View(getContext());
		mask.setBackgroundColor(0x99ffffff);
		fl_lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask.setLayoutParams(fl_lp);
		addView(mask, 0);

		ScrollView mScrollView = new ScrollView(getContext());
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.CENTER_HORIZONTAL;
		mScrollView.setLayoutParams(fl_lp);
		this.addView(mScrollView);

		m_container = new LinearLayout(getContext());
		m_container.setOrientation(LinearLayout.VERTICAL);
		m_container.setPadding(0, ShareData.PxToDpi_xhdpi(116), 0, 0);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.CENTER_HORIZONTAL;
		m_container.setLayoutParams(fl_lp);
		mScrollView.addView(m_container);

		ArrayList<GroupRes> tempDatas = MakeupComboResMgr2.getInstance().GetDownloadedGroupResArr();
		Object icon = R.drawable.new_material4_manage_icon_makeup;
		m_makeupItem = addItemView(ResType.MAKEUP_GROUP, getResources().getString(R.string.material_makeup), icon, tempDatas, R.integer.素材中心_管理_彩妆);

		tempDatas = FilterResMgr2.getInstance().GetDownloadedGroupResArr(getContext());
		icon = R.drawable.new_material4_manage_icon_filter;
		m_filterItem = addItemView(ResType.FILTER, getResources().getString(R.string.material_filter), icon, tempDatas, R.integer.素材中心_管理_滤镜);

		tempDatas = FrameResMgr2.getInstance().GetDownloadedGroupResArr();
		icon = R.drawable.new_material4_manage_icon_frame;
		m_frameItem = addItemView(ResType.FRAME, getResources().getString(R.string.material_frame), icon, tempDatas, R.integer.素材中心_管理_相框);

		tempDatas = FrameExResMgr2.getInstance().GetDownloadedGroupResArr(getContext());
		icon = R.drawable.new_material4_manage_icon_simple_frame;
		m_simpleFrameItem = addItemView(ResType.FRAME2, getResources().getString(R.string.material_frame2), icon, tempDatas, R.integer.素材中心_管理_简约边框);

		tempDatas = DecorateResMgr2.getInstance().GetDownloadedGroupResArr();
		icon = R.drawable.new_material4_manage_icon_decorate;
		m_decorateItem = addItemView(ResType.DECORATE, getResources().getString(R.string.material_decorate), icon, tempDatas, R.integer.素材中心_管理_贴图);

		tempDatas = GlassResMgr2.getInstance().GetDownloadedGroupResArr();
		icon = R.drawable.new_material4_manage_icon_glass;
		m_glassItem = addItemView(ResType.GLASS, getResources().getString(R.string.material_glass), icon, tempDatas, R.integer.素材中心_管理_毛玻璃);

		tempDatas = MosaicResMgr2.getInstance().GetDownloadedGroupResArr();
		icon = R.drawable.new_material4_manage_icon_mosaic;
		m_mosaicItem = addItemView(ResType.MOSAIC, getResources().getString(R.string.material_mosaic), icon, tempDatas, R.integer.素材中心_管理_马赛克);

		tempDatas = BrushResMgr2.getInstance().GetDownloadedGroupResArr(getContext());
		icon = R.drawable.new_material4_manage_icon_brush;
		m_brushItem = addItemView(ResType.BRUSH, getResources().getString(R.string.material_brush), icon, tempDatas, R.integer.素材中心_管理_指尖魔法);
		m_brushItem.mLine.setVisibility(GONE);

		FrameLayout captionBar = new FrameLayout(getContext());
		captionBar.setBackgroundColor(0xf4FFFFFF);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, ShareData.PxToDpi_xhdpi(96));
		fl_lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		captionBar.setLayoutParams(fl_lp);
		this.addView(captionBar);

		m_backBtn = new ImageView(getContext());
		m_backBtn.setImageResource(R.drawable.framework_back_btn);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		captionBar.addView(m_backBtn, fl_lp);
		m_backBtn.setOnTouchListener(mOnClickListener);
		ImageUtils.AddSkin(getContext(), m_backBtn);

		m_topBar = new TextView(getContext());
		m_topBar.setText(R.string.material_manage_title);
		m_topBar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		m_topBar.setTextColor(0xe6000000);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.CENTER;
		captionBar.addView(m_topBar, fl_lp);
	}

	public ManageItem addItemView(ResType tag, String describe, Object icon, ArrayList<GroupRes> datas)
	{
		return addItemView(tag, describe, icon, datas, 0);
	}

	public ManageItem addItemView(ResType tag, String describe, Object icon, ArrayList<GroupRes> datas, int tongJiId)
	{
		ManageItem out = new ManageItem(getContext());
		out.setUI(tag, describe, icon);
		out.setData(datas);
		out.setTongJiId(tongJiId);
		LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		m_container.addView(out, ll_lp);
		out.mItemContainer.setOnTouchListener(mOnClickListener);
		return out;
	}

	protected void SetViewState(View v, boolean isOpen, boolean hasAnimation, Animation.AnimationListener lst)
	{
		if (v == null)
			return;
		v.clearAnimation();

		int start;
		int end;
		if (isOpen)
		{
			v.setVisibility(View.VISIBLE);

			start = 1;
			end = 0;
		}
		else
		{
			v.setVisibility(View.GONE);

			start = 0;
			end = 1;
		}

		if (hasAnimation)
		{
			AnimationSet as;
			TranslateAnimation ta;
			as = new AnimationSet(true);
			ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			ta.setDuration(350);
			as.addAnimation(ta);
			v.startAnimation(as);
			as.setAnimationListener(lst);
		}
	}

	private FilterManagePage m_filterManagePage;
	protected void OpenFilterManagePage(HashMap<String, Object> params)
	{
		if(m_filterManagePage != null)
		{
			removeView(m_filterManagePage);
			m_filterManagePage.onClose();
			m_filterManagePage = null;
		}
		m_curSelModule = MANAGE_FILTER_INSTRO;
		m_filterManagePage = new FilterManagePage(getContext(), m_filterManageSite);
		m_filterManagePage.SetData(params);
		addView(m_filterManagePage);

		//下载更多进来去掉动画
		if(m_type == null)
		{
			m_uiEnabled = false;
			SetViewState(m_filterManagePage, true, true, new Animation.AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{

				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					m_uiEnabled = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{

				}
			});
		}
	}

	private FilterManageSite m_filterManageSite = new FilterManageSite()
	{
		@Override
		public void onBack(Context context, final HashMap<String, Object> params)
		{
			if(m_type == null)
			{
				m_uiEnabled = false;
				SetViewState(m_filterManagePage, false, true, new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{

					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						m_uiEnabled = true;

						if(m_filterManagePage != null)
						{
							ManagePage.this.removeView(m_filterManagePage);
							m_filterManagePage.onClose();
							m_filterManagePage = null;
						}
						onPageResult(SiteID.FILTER_MANAGE, params);
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{

					}
				});
			}
			else
			{
				onPageResult(SiteID.FILTER_MANAGE, params);
			}
		}
	};

	private ManageIntroPage m_manageIntroPage;
	protected void OpenManageIntroPage(HashMap<String, Object> params)
	{
		if(m_manageIntroPage != null)
		{
			removeView(m_manageIntroPage);
			m_manageIntroPage.onClose();
			m_manageIntroPage = null;
		}
		m_curSelModule = MANAGE_INSTRO;
		m_manageIntroPage = new ManageIntroPage(getContext(), m_masterSite);
		m_manageIntroPage.SetData(params);
		addView(m_manageIntroPage);

		//下载更多进来去掉动画
		if(m_type == null)
		{
			m_uiEnabled = false;
			SetViewState(m_manageIntroPage, true, true, new Animation.AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{

				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					m_uiEnabled = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{

				}
			});
		}
	}

	private ManagePageIntroSite m_masterSite = new ManagePageIntroSite()
	{
		@Override
		public void OnBack(Context context, final HashMap<String, Object> params)
		{
			if(m_type == null)
			{
				m_uiEnabled = false;
				SetViewState(m_manageIntroPage, false, true, new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{

					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						m_uiEnabled = true;

						if(m_manageIntroPage != null)
						{
							ManagePage.this.removeView(m_manageIntroPage);
							m_manageIntroPage.onClose();
							m_manageIntroPage = null;
						}
						onPageResult(SiteID.RES_MANAGE_INTRO, params);
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{

					}
				});
			}
			else
			{
				onPageResult(SiteID.RES_MANAGE_INTRO, params);
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
				ViewParent parent = v.getParent();
				if(v == m_backBtn)
				{
					onBack();
				} else if (parent == m_filterItem) {
					TongJi2.AddCountByRes(getContext(), ((ManageItem)parent).mTongJiId);
					MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_滤镜管理);
					OpenFilterManagePage(null);
				} else {
					if (parent instanceof ManageItem) {
						TongJi2.AddCountByRes(getContext(), ((ManageItem)parent).mTongJiId);
						switch(((ManageItem)parent).mTag)
						{
							case FRAME:
							{
								MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_相框管理);
								break;
							}
							case FRAME2:
							{
								MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_简约管理);
								break;
							}
							case MAKEUP_GROUP:
							{
								MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_彩妆管理);
								break;
							}
							case DECORATE:
							{
								MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_贴图管理);
								break;
							}
							case GLASS:
							{
								MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_毛玻璃管理);
								break;
							}
							case MOSAIC:
							{
								MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_马赛克管理);
								break;
							}
							case BRUSH:
							{
								MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_指尖魔法管理);
								break;
							}
						}

						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("datas", ((ManageItem)parent).mDatas);
						params.put("type", ((ManageItem)parent).mTag);
						if(m_type != null)
						{
							params.put("hasAnim", false);
						}
						OpenManageIntroPage(params);
//						m_site.OnManageIntroPage(params);
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

	@Override
	public void onClose()
	{
		TongJiUtils.onPageEnd(getContext(), R.string.素材中心_管理);
		MyBeautyStat.onPageEndByRes(R.string.素材商店_素材管理_主页面);
		this.setBackgroundColor(Color.CYAN);
		if(m_filterManagePage != null)
		{
			ManagePage.this.removeView(m_filterManagePage);
			m_filterManagePage.onClose();
			m_filterManagePage = null;
		}
		if(m_manageIntroPage != null)
		{
			ManagePage.this.removeView(m_manageIntroPage);
			m_manageIntroPage.onClose();
			m_manageIntroPage = null;
		}
		super.onClose();
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.素材中心_管理);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.素材中心_管理);
		super.onResume();
	}

	/**
	 * 从下载更多进来需要传参数
	 * @param params
	 * type ResType
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(params != null)
		{
			Object o = params.get("type");
			if(o != null)
			{
				m_type = (ResType)o;
				switch(m_type)
				{
					case FRAME:
					{
						mOnClickListener.onAnimationClick(m_frameItem.mItemContainer);
						break;
					}
					case FRAME2:
					{
						mOnClickListener.onAnimationClick(m_simpleFrameItem.mItemContainer);
						break;
					}
					case DECORATE:
					{
						mOnClickListener.onAnimationClick(m_decorateItem.mItemContainer);
						break;
					}
					case MAKEUP_GROUP:
					{
						mOnClickListener.onAnimationClick(m_makeupItem.mItemContainer);
						break;
					}
					case MOSAIC:
					{
						mOnClickListener.onAnimationClick(m_mosaicItem.mItemContainer);
						break;
					}
					case GLASS:
					{
						mOnClickListener.onAnimationClick(m_glassItem.mItemContainer);
						break;
					}
					case BRUSH:
					{
						mOnClickListener.onAnimationClick(m_brushItem.mItemContainer);
						break;
					}
					case FILTER: {
						mOnClickListener.onAnimationClick(m_filterItem.mItemContainer);
						break;
					}
				}
			}
		}

	}

	@Override
	public void onBack()
	{
		if(m_uiEnabled)
		{
			if(m_curSelModule == MANAGE_INSTRO)
			{
				if(m_manageIntroPage != null)
				{
					m_manageIntroPage.onBack();
					return;
				}
			}
			if(m_curSelModule == MANAGE_FILTER_INSTRO)
			{
				if(m_filterManagePage != null)
				{
					m_filterManagePage.onBack();
					return;
				}
			}
			MyBeautyStat.onClickByRes(R.string.素材商店_素材管理_主页面_返回);
			HashMap<String, Object> params = new HashMap<>();
			params.put("is_delete", m_isDelete);
			m_site.OnBack(getContext(), params);
		}
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		super.onPageResult(siteID, params);
		if(siteID == SiteID.RES_MANAGE_INTRO && params != null)
		{
			m_curSelModule = MANAGE;
			Object o = params.get("is_delete");
			if(o != null)
			{
				boolean is_delete = (Boolean)o;
				o = params.get("type");
				ResType type = ResType.THEME;
				if(o != null)
				{
					type = (ResType)o;
					if(type == m_type)
					{
						m_isDelete = is_delete;
					}
				}

				if(m_type != null)
				{
					onBack();
					return;
				}

				if(is_delete)
				{
					switch(type)
					{
						case FRAME:
						{
							ArrayList<GroupRes> tempDatas = FrameResMgr2.getInstance().GetDownloadedGroupResArr();
							m_frameItem.setData(tempDatas);
							break;
						}
						case FRAME2:
						{
							ArrayList<GroupRes> tempDatas = FrameExResMgr2.getInstance().GetDownloadedGroupResArr(getContext());
							m_simpleFrameItem.setData(tempDatas);
							break;
						}
						case DECORATE:
						{
							ArrayList<GroupRes> tempDatas = DecorateResMgr2.getInstance().GetDownloadedGroupResArr();
							m_decorateItem.setData(tempDatas);
							break;
						}
						case MAKEUP_GROUP:
						{
							ArrayList<GroupRes> tempDatas = MakeupComboResMgr2.getInstance().GetDownloadedGroupResArr();
							m_makeupItem.setData(tempDatas);
							break;
						}
						case MOSAIC:
						{
							ArrayList<GroupRes> tempDatas = MosaicResMgr2.getInstance().GetDownloadedGroupResArr();
							m_mosaicItem.setData(tempDatas);
							break;
						}
						case GLASS:
						{
							ArrayList<GroupRes> tempDatas = GlassResMgr2.getInstance().GetDownloadedGroupResArr();
							m_glassItem.setData(tempDatas);
							break;
						}
						case BRUSH:
						{
							ArrayList<GroupRes> tempDatas = BrushResMgr2.getInstance().GetDownloadedGroupResArr(getContext());
							m_brushItem.setData(tempDatas);
							break;
						}
					}
				}
			}
		} else if (siteID == SiteID.FILTER_MANAGE && params != null) {
			if (params.containsKey("is_delete")) {
				ArrayList<GroupRes> tempDatas = FilterResMgr2.getInstance().GetDownloadedGroupResArr(getContext());
				m_filterItem.setData(tempDatas);
			}
		}
	}
}

package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.ManagePageIntroSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.home4.Home4Page;
import cn.poco.resource.BrushResMgr2;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.GroupRes;
import cn.poco.resource.MakeupComboResMgr2;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.resource.ResType;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * 素材管理详情页
 */
public class ManageIntroPage extends IPage
{
	//private static final String TAG = "素材管理详情";

	private ManagePageIntroSite m_site;

	private ArrayList<BaseItemInfo> m_infos;
	private ResType m_type = ResType.THEME;
	private ImageView m_backBtn;
	private TextView m_title;
	private TextView m_selectAllBtn;

	private LinearLayout m_deleteBtn;
	private ImageView m_deleteIcon;
	private TextView m_deleteText;

	private TextView m_tip;//没有已下载文字提示

	private ListView m_listView;
	private int m_leftMargin;
	private BaseListAdapter m_listAdapter;
	private boolean m_isDelete;
	private boolean m_uiEnabled = true;

	public ManageIntroPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (ManagePageIntroSite)site;
		initUI();

		TongJiUtils.onPageStart(getContext(), R.string.素材中心_管理_详情);
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
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		});
		FrameLayout.LayoutParams fl_lp;
		View mask = new View(getContext());
		mask.setBackgroundColor(0x99ffffff);
		fl_lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mask.setLayoutParams(fl_lp);
		addView(mask, 0);

		int bottomBarHeight = ShareData.PxToDpi_xhdpi(100);
		m_leftMargin = ShareData.PxToDpi_xhdpi(20);

		FrameLayout captionBar = new FrameLayout(getContext());
		captionBar.setBackgroundColor(0xf4FFFFFF);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(96));
		fl_lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		captionBar.setLayoutParams(fl_lp);
		this.addView(captionBar);
		//防止点击事件穿透下层
		captionBar.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// TODO Auto-generated method stub
				return true;
			}
		});

		m_backBtn = new ImageView(getContext());
		m_backBtn.setImageResource(R.drawable.framework_back_btn);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		captionBar.addView(m_backBtn, fl_lp);
		m_backBtn.setOnTouchListener(m_btnLst);
		ImageUtils.AddSkin(getContext(), m_backBtn);

		m_title = new TextView(getContext());
		m_title.setText("");
		m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		m_title.setTextColor(0xe6000000);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.CENTER;
		captionBar.addView(m_title, fl_lp);

		m_selectAllBtn = new TextView(getContext());
		m_selectAllBtn.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
		m_selectAllBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		m_selectAllBtn.setText(R.string.material_manage_select_all);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		fl_lp.rightMargin = ShareData.PxToDpi_xhdpi(28);
		m_selectAllBtn.setLayoutParams(fl_lp);
		captionBar.addView(m_selectAllBtn);
		m_selectAllBtn.setOnTouchListener(m_btnLst);

		FrameLayout bottomBar = new FrameLayout(getContext());
		bottomBar.setBackgroundColor(0xf4ffffff);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, bottomBarHeight);
		fl_lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		bottomBar.setLayoutParams(fl_lp);
		this.addView(bottomBar);
		//防止点击事件穿透下层
		bottomBar.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return true;
			}
		});

		m_deleteBtn = new LinearLayout(getContext());
		m_deleteBtn.setAlpha(0.1f);
		m_deleteBtn.setBackgroundResource(R.drawable.new_material4_delete);
		m_deleteBtn.setGravity(Gravity.CENTER);
		m_deleteBtn.setOnTouchListener(m_btnLst);
		fl_lp = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(270), ShareData.PxToDpi_xhdpi(76));
		fl_lp.gravity = Gravity.CENTER;
		m_deleteBtn.setLayoutParams(fl_lp);
		bottomBar.addView(m_deleteBtn);
		{
			m_deleteIcon = new ImageView(getContext());
			m_deleteIcon.setImageResource(R.drawable.new_material4_delete_icon);
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			m_deleteIcon.setLayoutParams(ll);
			m_deleteBtn.addView(m_deleteIcon);

			m_deleteText = new TextView(getContext());
			m_deleteText.setTextColor(Color.WHITE);
			m_deleteText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			m_deleteText.setText(R.string.material_manage_delete);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ll.leftMargin = ShareData.PxToDpi_xhdpi(8);
			m_deleteText.setLayoutParams(ll);
			m_deleteBtn.addView(m_deleteText);
		}

		m_listView = new ListView(getContext());
		m_listView.setVerticalScrollBarEnabled(false);
		m_listView.setCacheColorHint(0x00000000);
		m_listView.setDivider(new ColorDrawable(0x00000000));
		m_listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		m_listView.setDividerHeight(ShareData.PxToDpi_xhdpi(20));
//		m_listView.setPadding(0, ShareData.PxToDpi_xhdpi(96), 0, 0);
		fl_lp = new FrameLayout.LayoutParams(ShareData.m_screenWidth - 2 * m_leftMargin, ShareData.m_screenHeight - bottomBarHeight - ShareData.PxToDpi_xhdpi(20));
		fl_lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		m_listView.setLayoutParams(fl_lp);
		addView(m_listView, 1);

		m_tip = new TextView(getContext());
		m_tip.setTextColor(0x99000000);
		m_tip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f);
		m_tip.setText(R.string.material_manage_none_tip);
		fl_lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl_lp.gravity = Gravity.CENTER;
		m_tip.setLayoutParams(fl_lp);
		addView(m_tip);
		updateTVState();

		m_listAdapter = new BaseListAdapter(getContext(), ShareData.m_screenWidth - 2 * m_leftMargin, ShareData.PxToDpi_xhdpi(70));
		m_listAdapter.setType(BaseListAdapter.OTHER);
		m_listAdapter.setDatas(m_infos);
		m_listAdapter.showCheckBox(true);
		m_listAdapter.canDrag(true);
		m_listAdapter.setOnBaseItemCallback(null);
		m_listView.setAdapter(m_listAdapter);
		m_listAdapter.notifyDataSetChanged();
	}

	private BaseItem.OnBaseItemCallback m_baseCB = new BaseItem.OnBaseItemCallback()
	{
		@Override
		public void OnDownload(View view, BaseItemInfo info, boolean clickDownloadBtn)
		{

		}

		@Override
		public void OnUse(BaseItemInfo info, int index)
		{

		}

		@Override
		public void OnCheck(BaseItemInfo info)
		{
			updateDeleteBtnStatus();
			updateSelectAllBtnStatus();
		}
	};

	public void updateTVState()
	{
		if(m_infos == null || m_infos.size() == 0)
		{
//			m_selectAllBtn.setAlpha(0.5f);
			m_selectAllBtn.setVisibility(GONE);
			m_tip.setVisibility(View.VISIBLE);
		}
		else
		{
//			m_selectAllBtn.setAlpha(1f);
			m_selectAllBtn.setVisibility(VISIBLE);
			m_tip.setVisibility(GONE);
		}
	}

	private OnAnimationClickListener m_btnLst = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == m_backBtn)
			{
				onBack();
				switch(m_type)
				{
					case FRAME:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_相框管理_主页面_相框管理_返回);
						break;
					}
					case FRAME2:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_简约边框管理_主页面_简约管理_返回);
						break;
					}
					case MAKEUP_GROUP:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_彩妆管理_主页面_彩妆管理_返回);
						break;
					}
					case DECORATE:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_贴图管理_主页面_贴图管理_返回);
						break;
					}
					case GLASS:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_毛玻璃管理_主页面_毛玻璃管理_返回);
						break;
					}
					case MOSAIC:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_马赛克管理_主页面_马赛克管理_返回);
						break;
					}
					case BRUSH:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_指尖魔法管理_主页面_指尖魔法管理_返回);
						break;
					}
				}
			}
			else if(v == m_selectAllBtn)
			{
				boolean isAllSelected = true;
				if(m_infos != null)
				{
					int size = m_infos.size();
					for(int i = 0; i < size; i++)
					{
						if(m_infos.get(i).m_isChecked == false)
						{
							isAllSelected = false;
						}
					}
				}
				if(isAllSelected)
				{
					if(m_infos != null)
					{
						int size = m_infos.size();
						for(int i = 0; i < size; i++)
						{
							m_infos.get(i).m_isChecked = false;
						}
					}
				}
				else
				{
					if(m_infos != null)
					{
						int size = m_infos.size();
						for(int i = 0; i < size; i++)
						{
							m_infos.get(i).m_isChecked = true;
						}
					}
				}
				switch(m_type)
				{
					case FRAME:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_相框管理_主页面_相框管理_全选);
						break;
					}
					case FRAME2:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_简约边框管理_主页面_简约管理_全选);
						break;
					}
					case MAKEUP_GROUP:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_彩妆管理_主页面_彩妆管理_全选);
						break;
					}
					case DECORATE:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_贴图管理_主页面_贴图管理_全选);
						break;
					}
					case GLASS:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_毛玻璃管理_主页面_毛玻璃管理_全选);
						break;
					}
					case MOSAIC:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_马赛克管理_主页面_马赛克管理_全选);
						break;
					}
					case BRUSH:
					{
						MyBeautyStat.onClickByRes(R.string.素材商店_指尖魔法管理_主页面_指尖魔法管理_全选);
						break;
					}
				}
				m_listAdapter.notifyDataSetChanged();
				updateDeleteBtnStatus();
				updateSelectAllBtnStatus();
			}
			else if(v == m_deleteBtn)
			{
				ArrayList<BaseItemInfo> selectedDatas = getSelectedDatas();

				if(selectedDatas != null && selectedDatas.size() > 0)
				{
					switch(m_type)
					{
						case MAKEUP_GROUP:
						{
							GroupRes res;
							for(BaseItemInfo itemData : selectedDatas)
							{
								res = new GroupRes();
								res.m_themeRes = itemData.m_themeRes;
								MakeupComboResMgr2.getInstance().DeleteGroupRes(getContext(), res);
							}
							MyBeautyStat.onClickByRes(R.string.素材商店_彩妆管理_主页面_彩妆管理_删除);
							break;
						}
						case DECORATE:
						{
							GroupRes res;
							for(BaseItemInfo itemData : selectedDatas)
							{
								res = new GroupRes();
								res.m_themeRes = itemData.m_themeRes;
								DecorateResMgr2.getInstance().DeleteGroupRes(getContext(), res);
							}
							MyBeautyStat.onClickByRes(R.string.素材商店_贴图管理_主页面_贴图管理_删除);
							break;
						}
						case FRAME:
						{
							GroupRes res;
							for(BaseItemInfo itemData : selectedDatas)
							{
								res = new GroupRes();
								res.m_themeRes = itemData.m_themeRes;
								FrameResMgr2.getInstance().DeleteGroupRes(getContext(), res);
							}
							MyBeautyStat.onClickByRes(R.string.素材商店_相框管理_主页面_相框管理_删除);
							break;
						}
						case FRAME2:
						{
							GroupRes res;
							for(BaseItemInfo itemData : selectedDatas)
							{
								res = new GroupRes();
								res.m_themeRes = itemData.m_themeRes;
								FrameExResMgr2.getInstance().DeleteGroupRes(getContext(), res);
							}
							MyBeautyStat.onClickByRes(R.string.素材商店_简约边框管理_主页面_简约管理_删除);
							break;
						}
						case MOSAIC:
						{
							GroupRes res;
							for(BaseItemInfo itemData : selectedDatas)
							{
								res = new GroupRes();
								res.m_themeRes = itemData.m_themeRes;
								MosaicResMgr2.getInstance().DeleteGroupRes(getContext(), res);
							}
							MyBeautyStat.onClickByRes(R.string.素材商店_马赛克管理_主页面_马赛克管理_删除);
							break;
						}
						case GLASS:
						{
							GroupRes res;
							for(BaseItemInfo itemData : selectedDatas)
							{
								res = new GroupRes();
								res.m_themeRes = itemData.m_themeRes;
								GlassResMgr2.getInstance().DeleteGroupRes(getContext(), res);
							}
							MyBeautyStat.onClickByRes(R.string.素材商店_毛玻璃管理_主页面_毛玻璃管理_删除);
							break;
						}
						case BRUSH:
						{
							GroupRes res;
							for(BaseItemInfo itemData : selectedDatas)
							{
								res = new GroupRes();
								res.m_themeRes = itemData.m_themeRes;
								BrushResMgr2.getInstance().DeleteGroupRes(getContext(), res);
							}
							MyBeautyStat.onClickByRes(R.string.素材商店_指尖魔法管理_主页面_指尖魔法管理_删除);
							break;
						}
						default:
							break;
					}
					m_infos.removeAll(selectedDatas);
					m_listAdapter.notifyDataSetChanged();
					m_deleteBtn.setAlpha(0.1f);
					updateTVState();
					m_isDelete = true;
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

	public ArrayList<BaseItemInfo> getSelectedDatas()
	{
		ArrayList<BaseItemInfo> out = new ArrayList<BaseItemInfo>();
		for(BaseItemInfo itemData : m_infos)
		{
			if(itemData.m_isChecked == true)
			{
				out.add(itemData);
			}
		}
		return out;
	}

	public void updateDeleteBtnStatus()
	{
		ArrayList<BaseItemInfo> ress = getSelectedDatas();
		if(ress != null && ress.size() > 0)
		{
			m_deleteBtn.setAlpha(1f);
		}
		else
		{
			m_deleteBtn.setAlpha(0.1f);
		}
	}

	public void updateSelectAllBtnStatus()
	{
		ArrayList<BaseItemInfo> ress = getSelectedDatas();
		if(ress != null && ress.size() == m_infos.size() && ress.size() > 0)
		{
			m_selectAllBtn.setText(R.string.material_manage_cancel_select_all);
		}
		else
		{
			m_selectAllBtn.setText(R.string.material_manage_select_all);
		}
	}

	/**
	 *
	 * @param params
	 * datas ArrayList<GroupRes>
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
						m_title.setText(R.string.material_frame);
						MyBeautyStat.onPageStartByRes(R.string.素材商店_相框管理_主页面);
						break;
					}
					case FRAME2:
					{
						m_title.setText(R.string.material_frame2);
						MyBeautyStat.onPageStartByRes(R.string.素材商店_简约边框管理_主页面);
						break;
					}
					case MAKEUP_GROUP:
					{
						m_title.setText(R.string.material_makeup);
						MyBeautyStat.onPageStartByRes(R.string.素材商店_彩妆管理_主页面);
						break;
					}
					case DECORATE:
					{
						m_title.setText(R.string.material_decorate);
						MyBeautyStat.onPageStartByRes(R.string.素材商店_贴图管理_主页面);
						break;
					}
					case GLASS:
					{
						m_title.setText(R.string.material_glass);
						MyBeautyStat.onPageStartByRes(R.string.素材商店_毛玻璃管理_主页面);
						break;
					}
					case MOSAIC:
					{
						m_title.setText(R.string.material_mosaic);
						MyBeautyStat.onPageStartByRes(R.string.素材商店_马赛克管理_主页面);
						break;
					}
					case BRUSH:
					{
						m_title.setText(R.string.material_brush);
						MyBeautyStat.onPageStartByRes(R.string.素材商店_指尖魔法管理_主页面);
						break;
					}
				}
			}
			o = params.get("datas");
			if(o != null)
			{
				m_infos = getPageDatas((ArrayList<GroupRes>)o);
			}
			updateTVState();
		}
		if(m_infos != null)
		{
			m_listAdapter.setOnBaseItemCallback(m_baseCB);
			m_listAdapter.setDatas(m_infos);
			m_listAdapter.notifyDataSetChanged();
		}
	}

	private ArrayList<BaseItemInfo> getPageDatas(ArrayList<GroupRes> res)
	{
		ArrayList<BaseItemInfo> out = new ArrayList<BaseItemInfo>();
		if(res != null && res.size() > 0)
		{
			int size = res.size();
			BaseItemInfo itemInfo;
			GroupRes groupRes;
			for(int i = 0; i < size; i ++)
			{
				groupRes = res.get(i);
				itemInfo = new BaseItemInfo(groupRes.m_themeRes, m_type);
				itemInfo.m_name = groupRes.m_themeRes.m_name;
				itemInfo.m_uri = groupRes.m_themeRes.m_id;
				itemInfo.m_ress = new ArrayList<>();
				if(groupRes.m_ress != null)
				{
					itemInfo.m_ress.addAll(groupRes.m_ress);
				}
				itemInfo.isAllShow = false;
				out.add(itemInfo);
			}
		}
		return out;
	}

	@Override
	public void onBack()
	{
		if(m_uiEnabled)
		{
			m_uiEnabled = false;
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("is_delete", m_isDelete);
			params.put("type", m_type);
			switch(m_type)
			{
				case FRAME:
				{
					MyBeautyStat.onPageEndByRes(R.string.素材商店_相框管理_主页面);
					break;
				}
				case FRAME2:
				{
					MyBeautyStat.onPageEndByRes(R.string.素材商店_简约边框管理_主页面);
					break;
				}
				case MAKEUP_GROUP:
				{
					MyBeautyStat.onPageEndByRes(R.string.素材商店_彩妆管理_主页面);
					break;
				}
				case DECORATE:
				{
					MyBeautyStat.onPageEndByRes(R.string.素材商店_贴图管理_主页面);
					break;
				}
				case GLASS:
				{
					MyBeautyStat.onPageEndByRes(R.string.素材商店_毛玻璃管理_主页面);
					break;
				}
				case MOSAIC:
				{
					MyBeautyStat.onPageEndByRes(R.string.素材商店_马赛克管理_主页面);
					break;
				}
				case BRUSH:
				{
					MyBeautyStat.onPageEndByRes(R.string.素材商店_指尖魔法管理_主页面);
					break;
				}
			}
			m_site.OnBack(getContext(), params);
		}
	}

	@Override
	public void onClose()
	{
		super.onClose();
		this.setBackgroundColor(Color.CYAN);
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
		}
		if(m_listAdapter != null)
		{
			m_listAdapter.ReleaseMem();
			m_listAdapter = null;
		}
		if(m_infos != null)
		{
			m_infos.clear();
			m_infos = null;

		}
		m_baseCB = null;

		switch(m_type)
		{
			case FRAME:
			{
				MyBeautyStat.onPageEndByRes(R.string.素材商店_相框管理_主页面);
				break;
			}
			case FRAME2:
			{
				MyBeautyStat.onPageEndByRes(R.string.素材商店_简约边框管理_主页面);
				break;
			}
			case MAKEUP_GROUP:
			{
				MyBeautyStat.onPageEndByRes(R.string.素材商店_彩妆管理_主页面);
				break;
			}
			case DECORATE:
			{
				MyBeautyStat.onPageEndByRes(R.string.素材商店_贴图管理_主页面);
				break;
			}
			case GLASS:
			{
				MyBeautyStat.onPageEndByRes(R.string.素材商店_毛玻璃管理_主页面);
				break;
			}
			case MOSAIC:
			{
				MyBeautyStat.onPageEndByRes(R.string.素材商店_马赛克管理_主页面);
				break;
			}
			case BRUSH:
			{
				MyBeautyStat.onPageEndByRes(R.string.素材商店_指尖魔法管理_主页面);
				break;
			}
		}

		TongJiUtils.onPageEnd(getContext(), R.string.素材中心_管理_详情);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.素材中心_管理_详情);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.素材中心_管理_详情);
		super.onResume();
	}
}

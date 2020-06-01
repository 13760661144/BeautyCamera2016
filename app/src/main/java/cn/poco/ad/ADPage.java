package cn.poco.ad;

/**
 * 商业通用流程
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adnonstop.admasterlibs.data.AbsChannelAdRes;
import com.adnonstop.admasterlibs.data.ActAnimationInfo;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.ad.ImageHandler.RestoreMsg;
import cn.poco.ad.site.ADPageSite;
import cn.poco.advanced.AdvancedResMgr;
import cn.poco.advanced.BeautifyViewV3;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.beautify.EffectType;
import cn.poco.camera.RotationImg2;
import cn.poco.display.CoreViewV3;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.graphics.ShapeEx;
import cn.poco.home.site.HomePageSite;
import cn.poco.image.filter;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DecorateRes;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.FrameRes;
import cn.poco.resource.FrameResMgr2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.AnimationDialog;
import cn.poco.tianutils.AnimationDialog.AnimFrameData;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.StatusButton;
import cn.poco.tsv.FastDynamicListV2;
import cn.poco.tsv.FastHSV;
import cn.poco.tsv.FastItemList;
import cn.poco.utils.CommonUI;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

public class ADPage extends IPage
{
	protected ADPageSite m_site;

	private HandlerThread mImageThread;
	private ImageHandler mImageHandler;
	private UIHandler mUIHandler;

	private AbsChannelAdRes mBusinessRes;
	private AbsChannelAdRes.AbsPageData mCurrentPageData;

	private boolean mUiEnabled;

	private Object mOrgInfo;
	private Bitmap mOrgBmp;
	private FrameLayout mViewFr;
	private int mViewFrW, mViewFrH;
	private ADCoreView mView;
	private int mViewW, mViewH;

	private boolean isBeautifyPage;

	private FastHSV mFrameList;
	private ADCommonRecylerView mFrameList1;
	private int mFrameUri;

	private ADCommonRecylerView mPendantList1;
	private Toast m_maxTip; //装饰过多提醒
	private StatusButton m_deleteBtn; //装饰删除按钮

	private ImageView mCancelBtn;
	private ImageView mOkBtn;

	private WaitAnimDialog m_waitDlg;

	private ArrayList<FastDynamicListV2.ItemInfo> mFrameListRes;
	private ArrayList<FastDynamicListV2.ItemInfo> mPendantListRes;
	private ArrayList<ADCommonAdapter.ADBaseItemData> mFrameListRes1;
	private ArrayList<ADCommonAdapter.ADBaseItemData> mPendantListRes1;

	private int DEF_IMG_SIZE;

	//返回编辑
	private boolean isBackFromShare;
	private static ShapeEx s_img;
	private static boolean isFrameUri;
	private static int s_frameUri;
	private static ArrayList<ShapeEx> s_pendantArr;
	private static AbsChannelAdRes.AbsPageData sPageData;

	private int m_bottomAllLayoutHeight;
	private int m_bottomBarHeight;
	private int m_bottomResListHeight;
	private LinearLayout m_bottomFr;
	private FrameLayout m_bottomList;

	public ADPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (ADPageSite)site;

		TongJiUtils.onPageStart(getContext(), R.string.通用商业);
	}

	public void InitData()
	{
		filter.deleteAllCacheFilterFile();

		ShareData.InitData(getContext());
		mViewFrW = ShareData.m_screenWidth;
		mViewFrH = (int) (mViewFrW*4/3f);
		mViewW = mViewFrW;
		mViewH = mViewFrH;

		m_bottomAllLayoutHeight = ShareData.PxToDpi_xhdpi(320);
		m_bottomBarHeight = ShareData.PxToDpi_xhdpi(88);
		m_bottomResListHeight = m_bottomAllLayoutHeight - m_bottomBarHeight;

		mUiEnabled = false;
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());
		isBeautifyPage = false;

		mUIHandler = new UIHandler();
		mImageThread = new HandlerThread("my_handler_thread");
		mImageThread.start();
		mImageHandler = new ImageHandler(mImageThread.getLooper(), getContext(), mUIHandler);

		this.setBackgroundColor(0xFFEDEDE9);
		m_waitDlg = new WaitAnimDialog((Activity)getContext());
		SetWaitUI(true, "");
	}

	public void InitUI()
	{
		mViewFr = new FrameLayout(getContext());
		LayoutParams fl_lp = new LayoutParams(LayoutParams.MATCH_PARENT, mViewFrH);
		fl_lp.gravity = Gravity.LEFT | Gravity.TOP;
		int center = (int) ((ShareData.m_screenHeight - m_bottomBarHeight - mViewFrH)/2f);
		fl_lp.topMargin = center;
		mViewFr.setLayoutParams(fl_lp);
		this.addView(mViewFr);

		m_bottomFr = new LinearLayout(getContext());
		m_bottomFr.setOrientation(LinearLayout.VERTICAL);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		fl.bottomMargin = -m_bottomResListHeight;
		m_bottomFr.setLayoutParams(fl);
		this.addView(m_bottomFr);

		FrameLayout mBottomBar = new FrameLayout(getContext());
		fl = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,m_bottomBarHeight);
		mBottomBar.setLayoutParams(fl);
		mBottomBar.setBackgroundColor(0xe6ffffff);
		m_bottomFr.addView(mBottomBar);
		{
			mCancelBtn= new ImageView(getContext());
			mCancelBtn.setImageResource(R.drawable.beautify_cancel);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			fl.leftMargin = ShareData.PxToDpi_xhdpi(22);
			mCancelBtn.setLayoutParams(fl);
			mCancelBtn.setOnTouchListener(mOnClickListener2);
			mBottomBar.addView(mCancelBtn);

			mOkBtn = new ImageView(getContext());
			mOkBtn.setImageResource(R.drawable.beautify_ok);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
			fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
			mOkBtn.setLayoutParams(fl);
			mOkBtn.setOnTouchListener(mOnClickListener2);
			mBottomBar.addView(mOkBtn);
			ImageUtils.AddSkin(getContext(),mOkBtn);
		}

		m_bottomList = new FrameLayout(getContext());
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,m_bottomResListHeight);
		m_bottomList.setLayoutParams(ll);
		m_bottomFr.addView(m_bottomList);

	}

	public FrameRes getFrameByUri(int uri)
	{
		FrameRes out = null;
		if(mFrameListRes1 != null)
		{
			int len = mFrameListRes1.size();
			for(int i = 0; i < len; i++)
			{
				if(mFrameListRes1.get(i).m_id == uri)
				{
					out = (FrameRes)mFrameListRes1.get(i).m_ex;
				}
			}
		}
		return out;
	}

	public int getEffectType(String defaultColor)
	{
		int outType = EffectType.EFFECT_LITTLE;

		if(defaultColor != null && !defaultColor.equals(""))
		{
			switch(defaultColor)
			{
				case "细节":
					outType = EffectType.EFFECT_NEWBEE;
					break;
				case "自然":
					outType = EffectType.EFFECT_NATURE;
					break;
				case "嫩白":
					outType = EffectType.EFFECT_DEFAULT;
					break;
				case "亮白":
					outType = EffectType.EFFECT_MIDDLE;
					break;
				case "轻微":
					outType = EffectType.EFFECT_LITTLE;
					break;
				case "梦幻":
					outType = EffectType.EFFECT_MOON;
					break;
				case "无":
					outType = EffectType.EFFECT_NONE;
					break;
				case "朦胧":
					outType = EffectType.EFFECT_MOONLIGHT;
					break;
				case "净白":
					outType = EffectType.EFFECT_CLEAR;
					break;
			}
		}
		return outType;
	}

	protected boolean mUiEnabled2 = true;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if(mUiEnabled2)
		{
			return super.onInterceptTouchEvent(ev);
		}
		else
		{
			return true;
		}
	}

	public void showAnimation()
	{
		SetWaitUI(false, "");
		mUiEnabled2 = false;
		AnimationDialog animDialog = new AnimationDialog((Activity)getContext(), new AnimationDialog.Callback()
		{
			@Override
			public void OnAnimationEnd()
			{
				mUiEnabled2 = true;
			}

			@Override
			public void OnClick()
			{
				
			}
		});

		if(mCurrentPageData instanceof AbsChannelAdRes.ColorPageData)
		{
			ArrayList<AnimFrameData> mAnimFrameDatas = new ArrayList<>();
			AnimFrameData mAnimFrameData;
			ActAnimationInfo anim = ((AbsChannelAdRes.ColorPageData)mCurrentPageData).mAnim;
			if(anim != null)
			{
				for(ActAnimationInfo.ActAnimationFrame actAnimFrame : anim.frames)
				{
					mAnimFrameData = new AnimFrameData(actAnimFrame.img, actAnimFrame.time, actAnimFrame.stop);
					mAnimFrameDatas.add(mAnimFrameData);
				}
				animDialog.SetData_xhdpi(mAnimFrameDatas);
				String align = anim.align;
				animDialog.SetGravity(align.equals("center") ? Gravity.CENTER : Gravity.TOP | Gravity.CENTER_HORIZONTAL);
			}
		}

		animDialog.show();
	}

	/**
	 * 默认到高级美化
	 *
	 * @param infos
	 */
	public void setAdvancedPage(HashMap<String, Object> infos)
	{
		InitData();
		Message mMsg = mImageHandler.obtainMessage();
		mMsg.what = ImageHandler.MSG_ADVANCED_PAGE;
		mMsg.obj = infos.get("imgs");
		if(mCurrentPageData instanceof AbsChannelAdRes.BeautyPageData)
		{
			mMsg.arg1 = getEffectType(((AbsChannelAdRes.BeautyPageData)mCurrentPageData).mColor);
			mMsg.arg2 = ((AbsChannelAdRes.BeautyPageData)mCurrentPageData).mAlpha;
		}
		else
		{
			mMsg.arg1 = getEffectType("轻微");
			mMsg.arg2 = 80;
		}
		mImageHandler.sendMessage(mMsg);
	}

	/**
	 * 保存到高级美化
	 *
	 * @param bmp
	 */
	public void saveToAdvancedPage(Bitmap bmp)
	{
		String pic = null;
		try
		{
			pic = Utils.SaveImg(getContext(), bmp, null, 100);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		SetWaitUI(false, "");

		RotationImg2 mTempRotationImg = new RotationImg2();
		mTempRotationImg.m_orgPath = pic;
		mTempRotationImg.m_img = pic;
		mTempRotationImg.m_degree = 0;
		m_site.OpenAdvBeauty(new RotationImg2[]{mTempRotationImg},getContext());
	}

	private void SetModuleUI(final AbsChannelAdRes.AbsPageData dstPage)
	{
		if(dstPage != null)
		{
			if(dstPage instanceof AbsChannelAdRes.FramePageData)
			{
				InitFrameUI((AbsChannelAdRes.FramePageData)dstPage);
			}
			else if(dstPage instanceof AbsChannelAdRes.DecoratePageData)
			{
				InitPendantUI((AbsChannelAdRes.DecoratePageData)dstPage);
			}
		}
	}

	public OnAnimationClickListener mOnClickListener2 = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(mUiEnabled)
			{
				if(v == mCancelBtn)
				{
					s_img = null;
					s_frameUri = -1;
					if(s_pendantArr != null)
					{
						s_pendantArr.clear();
					}
					sPageData = null;
					onBack();
				}
				else if(v == mOkBtn)
				{
					if(isBeautifyPage)
					{
						isBeautifyPage = false;
						if(mCurrentPageData != null)
						{
							AbsChannelAdRes.AbsPageData pageData = mBusinessRes.GetNext(mCurrentPageData.getClass());
							if(pageData instanceof AbsChannelAdRes.DecoratePageData)
							{
								mCurrentPageData = pageData;
								SetModuleUI(mCurrentPageData);
								SetSelItemByUri(((AbsChannelAdRes.DecoratePageData)mCurrentPageData).mDefaultSel);
							}
							else if(pageData instanceof AbsChannelAdRes.FramePageData)
							{
								mCurrentPageData = pageData;
								SetModuleUI(mCurrentPageData);
								SetSelItemByUri(((AbsChannelAdRes.FramePageData)mCurrentPageData).mDefaultSel);
							}
							else if(pageData instanceof AbsChannelAdRes.BeautyPageData)
							{
								saveToAdvancedPage(mView.GetOutputBmp());
							}
							else
							{
								saveBmp();
							}
						}
						else
						{
							saveBmp();
						}
					}
					else
					{
						if(mCurrentPageData instanceof AbsChannelAdRes.FramePageData)
						{
							AbsChannelAdRes.AbsPageData pageData = mBusinessRes.GetNext(mCurrentPageData.getClass());
							if(pageData instanceof AbsChannelAdRes.DecoratePageData)
							{
								mCurrentPageData = pageData;
								SetModuleUI(mCurrentPageData);
								SetSelItemByUri(((AbsChannelAdRes.DecoratePageData)mCurrentPageData).mDefaultSel);
							}
							else if(pageData instanceof AbsChannelAdRes.BeautyPageData)
							{
								saveToAdvancedPage(mView.GetOutputBmp());
							}
							else
							{
								saveBmp();
							}
						}
						else if(mCurrentPageData instanceof AbsChannelAdRes.DecoratePageData)
						{
							AbsChannelAdRes.AbsPageData pageData = mBusinessRes.GetNext(mCurrentPageData.getClass());
							if(pageData instanceof AbsChannelAdRes.FramePageData)
							{
								Bitmap pendantBmp = mView.GetOutputBmp2();
								mView.SetImg(mOrgInfo,pendantBmp);
								mView.CreateViewBuffer();
								mView.DelAllPendant();
								mView.UpdateUI();
								mCurrentPageData = pageData;
								SetModuleUI(mCurrentPageData);
								SetSelItemByUri(((AbsChannelAdRes.FramePageData)mCurrentPageData).mDefaultSel);
							}
							else if(pageData instanceof AbsChannelAdRes.BeautyPageData)
							{
								saveToAdvancedPage(mView.GetOutputBmp());
							}
							else
							{
								saveBmp();
							}
						}
						else
						{
							saveBmp();
						}
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

	public OnClickListener mOnClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if(mUiEnabled)
			{
				if(v == m_deleteBtn)
				{
					if(mView != null)
					{
						mView.DelPendant();
						mView.UpdateUI();
					}
				}
			}
		}
	};

	public void saveBmp()
	{
		//返回编辑
		s_img = mView.m_img;
		s_frameUri = mFrameUri;
		sPageData = mCurrentPageData;

		//修改保存接口
		Bitmap bmp = mView.GetOutputBmp();
		if(mView.m_pendantArr != null && mView.m_pendantArr.size() > 0)
		{
			s_pendantArr = (ArrayList<ShapeEx>)mView.m_pendantArr.clone();
			int len = s_pendantArr.size();
			ShapeEx temp;
			for(int i = 0; i < len; i++)
			{
				temp = s_pendantArr.get(i);
				if(temp.m_bmp != null)
				{
					temp.m_bmp.recycle();
					temp.m_bmp = null;
				}
			}
		}
		m_site.OnSave(bmp,getContext());
	}

	BeautifyViewV3.ControlCallback mCtrlInterface = new BeautifyViewV3.ControlCallback()
	{
		@Override
		public Bitmap MakeShowImg(Object info, int frW, int frH)
		{
			
			return null;
		}

		@Override
		public Bitmap MakeOutputImg(Object info, int outW, int outH)
		{
			
			return null;
		}

		@Override
		public Bitmap MakeShowFrame(Object info, int frW, int frH)
		{
			
			return null;
		}

		@Override
		public Bitmap MakeOutputFrame(Object info, int outW, int outH)
		{
			
			return null;
		}

		@Override
		public Bitmap MakeShowBK(Object info, int frW, int frH)
		{
			
			return null;
		}

		@Override
		public Bitmap MakeOutputBK(Object info, int outW, int outH)
		{
			
			return null;
		}

		@Override
		public Bitmap MakeShowPendant(Object info, int frW, int frH)
		{
			Bitmap out = null;

			out = cn.poco.imagecore.Utils.DecodeImage(getContext(), ((DecorateRes)info).m_res, 0, -1, frW * 2 / 3, frH * 2 / 3);

			return out;
		}

		@Override
		public Bitmap MakeOutputPendant(Object info, int outW, int outH)
		{
			
			return null;
		}

		@Override
		public void SelectPendant(int index)
		{
			if(mUiEnabled && mCurrentPageData instanceof AbsChannelAdRes.DecoratePageData && m_deleteBtn != null)
			{
				if(index >= 0)
				{
					m_deleteBtn.SetOver();
				}
				else
				{
					m_deleteBtn.SetOut();
				}
			}

		}

		@Override
		public void TouchImage(boolean isTouch)
		{
			
		}

		@Override
		public void DeletePendant(ShapeEx pendant)
		{
			
		}
	};

	private void InitFrameUI(AbsChannelAdRes.FramePageData pageData)
	{
		mFrameUri = -1;

		if(mPendantList1 != null)
		{
			m_deleteBtn.setVisibility(View.GONE);
			mPendantList1.setVisibility(View.GONE);
		}
		InitFrameList(pageData);

		if(mFrameList1 != null)
		{
			mView.SetOperateMode(BeautifyViewV3.MODE_FRAME);
			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			mFrameList1.setLayoutParams(fl);
			m_bottomList.addView(mFrameList1);
			ShowBottomResList(true);
			mFrameList.ScrollToCenter(ShareData.m_screenWidth, false);
			mUiEnabled = true;
			isFrameUri = true;//返回编辑用
		}
		reLayoutViewFr();
	}


	private void ShowBottomResList(boolean flag)
	{
		if(m_bottomFr != null)
		{
			if(flag)
			{
				FrameLayout.LayoutParams fl = (LayoutParams) m_bottomFr.getLayoutParams();
				fl.bottomMargin = 0;
				m_bottomFr.setLayoutParams(fl);
			}
			else
			{
				FrameLayout.LayoutParams fl = (LayoutParams) m_bottomFr.getLayoutParams();
				fl.bottomMargin = -m_bottomResListHeight;
				m_bottomFr.setLayoutParams(fl);
			}
		}
	}

	private ArrayList<FastDynamicListV2.ItemInfo> getFrames(AbsChannelAdRes.FramePageData pageData)
	{
		ArrayList<FastDynamicListV2.ItemInfo> out = new ArrayList<>();

		if(pageData != null)
		{
			if(!pageData.mMustChoose)
			{
				//添加"无"item
				FastDynamicListV2.ItemInfo wu = new FastDynamicListV2.ItemInfo();
				wu.m_logo = R.drawable.photofactory_sp_null;
				wu.m_name = "无相框";
				wu.m_uri = 0;
				out.add(wu);
			}

			ArrayList<FrameRes> infoArr = FrameResMgr2.getInstance().GetResArr2(pageData.mIds, false);
			FrameRes temp;
			FastDynamicListV2.ItemInfo tempInfo;
			int len = infoArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = infoArr.get(i);
				if(temp != null)
				{
					tempInfo = new FastDynamicListV2.ItemInfo();
					tempInfo.m_logo = temp.m_thumb;
					tempInfo.m_name = temp.m_name;
					tempInfo.m_uri = temp.m_id;
					tempInfo.m_ex = temp;
					switch(temp.m_type)
					{
						case BaseRes.TYPE_NETWORK_URL:
							//没下载,排队中,下载中
							tempInfo.m_style = FastDynamicListV2.ItemInfo.Style.NEED_DOWNLOAD;
							break;
						case BaseRes.TYPE_LOCAL_RES:
						case BaseRes.TYPE_LOCAL_PATH:
						default:
							tempInfo.m_style = FastDynamicListV2.ItemInfo.Style.NORMAL;
							break;
					}
					out.add(tempInfo);
				}
			}
		}

		return out;
	}

	private void InitFrameList(AbsChannelAdRes.FramePageData pageData)
	{
		mFrameListRes = getFrames(pageData);
		mFrameListRes1 = switchData(mFrameListRes);
		mFrameList = MakeFrameList(mFrameListRes, false);
		mFrameList1 = ADCommonUI.makeRecyclerView1(getContext(),mFrameListRes1,m_cb,ShareData.PxToDpi_xhdpi(32),ShareData.PxToDpi_xhdpi(18));
	}

	private ArrayList<ADCommonAdapter.ADBaseItemData> switchData(ArrayList<FastDynamicListV2.ItemInfo> arrs)
	{
		ArrayList<ADCommonAdapter.ADBaseItemData> outs = new ArrayList<>();
		ADCommonAdapter.ADItem1.ADItem1Data data = null;
		if(arrs != null && arrs.size() > 0)
		{
			for(int i = 0; i < arrs.size(); i++)
			{
				FastDynamicListV2.ItemInfo itemData = arrs.get(i);
				if(itemData != null)
				{
					data = new ADCommonAdapter.ADItem1.ADItem1Data();
					data.m_id = itemData.m_uri;
					data.m_res = itemData.m_logo;
					data.m_name = itemData.m_name;
					data.m_bkColor = 0xffe75988;
					data.m_ex = itemData.m_ex;
					data.m_imgShowHeight = ShareData.PxToDpi_xhdpi(140);
					data.m_imgShowWidth = ShareData.PxToDpi_xhdpi(140);
					outs.add(data);
				}
			}
		}
		return outs;
	}

	private FastHSV MakeFrameList(ArrayList<FastDynamicListV2.ItemInfo> resInfoArr, boolean isShowTitle)
	{
		FastHSV out = CommonUI.MakeFastDynamicList1((Activity)getContext(), resInfoArr, false, m_listCallback);

		return out;
	}

	private void InitPendantList(AbsChannelAdRes.DecoratePageData pageData)
	{
		mPendantListRes = getPendantRes(pageData);
		mPendantListRes1 = switchData(mPendantListRes);
		mPendantList1 = ADCommonUI.makeRecyclerView1(getContext(),mPendantListRes1,m_cb,ShareData.PxToDpi_xhdpi(10),ShareData.PxToDpi_xhdpi(26));
	}

	private ArrayList<FastDynamicListV2.ItemInfo> getPendantRes(AbsChannelAdRes.DecoratePageData pageData)
	{
		ArrayList<FastDynamicListV2.ItemInfo> out = new ArrayList<FastDynamicListV2.ItemInfo>();

		if(pageData != null)
		{
			ArrayList<DecorateRes> res = DecorateResMgr2.getInstance().GetResArr2(pageData.mIds, false);
			int len = res.size();
			DecorateRes temp;
			FastDynamicListV2.ItemInfo tempInfo;
			for(int i = 0; i < len; i++)
			{
				temp = res.get(i);
				if(AdvancedResMgr.GetResIndex(out, temp.m_id) < 0)
				{
					tempInfo = new FastDynamicListV2.ItemInfo();
					tempInfo.m_logo = temp.m_thumb;
					tempInfo.m_name = temp.m_name;
					tempInfo.m_uri = temp.m_id;
					tempInfo.m_ex = temp;
					//if(!temp.isAvailable())
					//{
					//	tempInfo.m_style = FastDynamicListV2.ItemInfo.Style.NEED_DOWNLOAD;
					//}
					out.add(tempInfo);
				}
			}
		}

		return out;
	}

	private void InitPendantUI(AbsChannelAdRes.DecoratePageData pageData)
	{
		mFrameUri = -1;
		if(mFrameList1 != null)
		{
			mFrameList1.setVisibility(View.GONE);
		}
		//删除
		m_deleteBtn = new StatusButton(getContext());
		m_deleteBtn.SetData(R.drawable.photofactory_eidt_delete_out, R.drawable.photofactory_eidt_delete_over, ScaleType.CENTER_INSIDE);
		LayoutParams fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.TOP | Gravity.RIGHT;
		fl.topMargin = ShareData.PxToDpi_xhdpi(20);
		fl.rightMargin = ShareData.PxToDpi_xhdpi(20);
		m_deleteBtn.setLayoutParams(fl);
		this.addView(m_deleteBtn);
		m_deleteBtn.setOnClickListener(mOnClickListener);

		InitPendantList(pageData);

		if(mPendantList1 != null)
		{
			mView.SetOperateMode(BeautifyViewV3.MODE_PENDANT);
			fl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			mPendantList1.setLayoutParams(fl);
			m_bottomList.addView(mPendantList1);
			ShowBottomResList(true);
			mUiEnabled = true;
		}
		reLayoutViewFr();
	}

	private FastItemList.ControlCallback m_listCallback = new FastItemList.ControlCallback()
	{
		@Override
		public void OnItemDown(FastItemList list, FastItemList.ItemInfo info, int index)
		{
			
		}

		@Override
		public void OnItemUp(FastItemList list, FastItemList.ItemInfo info, int index)
		{
			
		}

		@Override
		public void OnItemClick(FastItemList list, FastItemList.ItemInfo info, int index)
		{
			if(mUiEnabled)
			{
				if(info instanceof FastDynamicListV2.ItemInfo)
				{
					switch(((FastDynamicListV2.ItemInfo)info).m_style)
					{
						case NORMAL:
						{
							SetSelItemByUri(info.m_uri);
							break;
						}

						/*case NEED_DOWNLOAD:
						{
							((FastDynamicListV2.ItemInfo)info).m_style = FastDynamicListV2.ItemInfo.Style.WAIT;
							if(m_module == AdvancedModuleType.CARD)
							{
								CardUpdate.getInstance().pushDownloadTask(info.m_uri);
							}
							else
							{
								FrameUpdate.getInstance().pushDownloadTask(info.m_uri);
							}
							((FastDynamicListV2)list).SetItemStyleByUri(info.m_uri, FastDynamicListV2.ItemInfo.Style.WAIT);
							break;
						}*/

						default:
							break;
					}
				}
			}
		}
	};

	ADCommonAdapter.OnClickCB m_cb = new ADCommonAdapter.OnClickCB() {
		@Override
		public void clickItem(ADCommonAdapter.ADBaseItemData data,int index) {
			if(mUiEnabled)
			{
				if(data instanceof ADCommonAdapter.ADBaseItemData)
				{
					SetSelItemByUri(data.m_id);
					ScroollToCenter(data.m_index);
				}
			}
		}
	};

	private void ScroollToCenter(int position)
	{
		if(mCurrentPageData instanceof AbsChannelAdRes.FramePageData)
		{
			if(mFrameList1 != null)
			{
				mFrameList1.ScrollToCenter(position);
			}
		}
	}

	private void SendFrameMsg(Object frameInfo)
	{
		ImageHandler.FrameMsg msgInfo = new ImageHandler.FrameMsg();
		msgInfo.m_w = mOrgBmp.getWidth();
		msgInfo.m_h = mOrgBmp.getHeight();
		msgInfo.m_frameInfo = frameInfo;

		Message msg = mImageHandler.obtainMessage();
		msg.obj = msgInfo;
		msg.what = ImageHandler.MSG_UPDATE_FRAME;
		mImageHandler.sendMessage(msg);
	}

	protected void SetSelItemByUri(int uri)
	{
		if(mCurrentPageData instanceof AbsChannelAdRes.DecoratePageData)
		{
			if(uri == 0)
			{
				mView.DelAllPendant();
			}
			else
			{
				DecorateRes info = null;
				if(mPendantListRes1 != null)
				{
					for(int i = 0; i < mPendantListRes1.size(); i++)
					{
						if(mPendantListRes1.get(i).m_id == uri)
						{
							Object ex = mPendantListRes1.get(i).m_ex;
							info = (DecorateRes)ex;
							break;
						}
					}
				}
				if(info != null)
				{
					int index = mView.AddPendant(info, null);
					if(index < 0)
					{
						ShowMaxTip();
					}
					else
					{
						//调整装饰尺寸
						ShapeEx tempItem = mView.m_pendantArr.get(index);
						if(ShareData.m_screenWidth <= 480)
						{
							tempItem.SetScaleXY(0.6f, 0.6f);
						}
						else if(ShareData.m_screenWidth <= 720)
						{
							tempItem.SetScaleXY(0.8f, 0.8f);
						}

						mView.SetSelPendant(index);
						mCtrlInterface.SelectPendant(index);
					}
				}
			}
			mView.UpdateUI();
		}
		else if(mCurrentPageData instanceof AbsChannelAdRes.FramePageData)
		{
			if(mFrameList1 != null)
			{
				ADCommonAdapter adCommonAdapter = (ADCommonAdapter) mFrameList1.getAdapter();
				adCommonAdapter.SetSelectByUri(uri);
				adCommonAdapter.notifyDataSetChanged();
			}
			if(uri == 0)
			{
				mFrameUri = uri;
				mView.SetFrame2(null);
				//图片位置重置(一定要在SetFrame后)
				mView.m_img.m_x = mView.m_viewport.m_x;
				mView.m_img.m_y = mView.m_viewport.m_y;
				mView.m_img.m_scaleX = mView.m_img.DEF_SCALE;
				mView.m_img.m_scaleY = mView.m_img.DEF_SCALE;
				mView.m_img.m_degree = 0;

				mView.SetBkColor(0x00FFFFFF);

				mView.UpdateUI();
			}
			else if(mFrameUri != uri)
			{
				mFrameUri = uri;
				FrameRes info = FrameResMgr2.getInstance().GetRes(uri);
				if(info != null)
				{
					SendFrameMsg(info);
				}
			}
		}
	}

	/**
	 * 更新边框背景,UI显示等
	 *
	 * @param info
	 * @param fThumb
	 */
	private void UpdateUI2Frame(Object info, Bitmap fThumb)
	{
		if(info instanceof FrameRes)
		{
			FrameRes finfo = (FrameRes)info;
			if(!AdvancedResMgr.IsNull(finfo.f_bk))
			{
				mView.SetBkBmp(finfo.f_bk, null);
			}
			else
			{
				mView.SetBkColor(finfo.m_bkColor);
			}
		}
	}

	private void ShowMaxTip()
	{
		if(m_maxTip == null)
		{
			m_maxTip = Toast.makeText(getContext(), "装饰数量已超过内存使用上限", Toast.LENGTH_SHORT);
			m_maxTip.setGravity(Gravity.CENTER, 0, 0);
		}

		m_maxTip.show();
	}

	private void SetWaitUI(boolean flag, String str)
	{
		if(flag)
		{
			if(str == null)
			{
				str = "";
			}

			if(m_waitDlg != null)
			{
				m_waitDlg.show();
			}
		}
		else
		{
			if(m_waitDlg != null)
			{
				m_waitDlg.hide();
			}
		}
	}

	@Override
	public void onBack()
	{
		m_site.OnBack(getContext());
	}

	/**
	 * 没用的参数不必传进来，使用时先判断参数是否为null
	 * imgs : RotationImg[] / ImageFile2
	 * {@link HomePageSite#BUSINESS_KEY}:ChannelAdRes
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		mBusinessRes = (AbsChannelAdRes)params.get(HomePageSite.BUSINESS_KEY);
		mCurrentPageData = mBusinessRes.GetFirst();
		if(mCurrentPageData != null && !(mCurrentPageData instanceof AbsChannelAdRes.BeautyPageData))
		{
			if(!isBackFromShare)
			{
				InitData();
				InitUI();
				if(mView != null)
				{
					mViewFr.removeView(mView);
				}

				mOrgInfo = params.get("imgs");

				final Bitmap outBitmap = ImageUtils.MakeBmp(getContext(), mOrgInfo, DEF_IMG_SIZE, DEF_IMG_SIZE);
				{
					//加毛玻璃背景
					Bitmap bk = BeautifyResMgr2.MakeBkBmp(outBitmap, ShareData.m_screenWidth, ShareData.m_screenHeight);
					setBackgroundDrawable(new BitmapDrawable(getResources(), bk));
				}

				int w = ShareData.m_screenWidth;
				int h = (int) (w*4/3f);
				mView = new ADCoreView(getContext(), w + 2, h + 2);
				mView.def_rotation_res = R.drawable.photofactory_pendant_rotation;
				mView.InitData(mCtrlInterface);
				mView.SetOperateMode(CoreViewV3.MODE_IMAGE);
				LayoutParams fl_lp = new LayoutParams(w + 2, h + 2);
				fl_lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
				mView.setLayoutParams(fl_lp);
				mView.SetImg(mOrgInfo, outBitmap);
				mView.CreateViewBuffer();
				mView.UpdateUI();
				mViewFr.addView(mView);

				//美化前动画
				int delayTime = 0;
				if(mCurrentPageData instanceof AbsChannelAdRes.ColorPageData)
				{
					isBeautifyPage = true;
					ActAnimationInfo anim = ((AbsChannelAdRes.ColorPageData)mCurrentPageData).mAnim;
					if(anim != null && anim.frames.size() > 0)
					{
						String animPlace = anim.place;
						if(animPlace != null && animPlace.equals("before_beautify"))
						{
							int temp = 0;
							for(ActAnimationInfo.ActAnimationFrame actAnimFrame : anim.frames)
							{
								if(actAnimFrame != null)
								{
									temp = actAnimFrame.time + temp;
								}
							}
							delayTime = temp;
							showAnimation();
						}
					}
				}
				else
				{
					mOnClickListener2.onAnimationClick(mOkBtn);
					SetModuleUI(mCurrentPageData);
				}

				ADPage.this.postDelayed(new Runnable() {
					@Override
					public void run() {
						Message mMsg = mImageHandler.obtainMessage();
						mMsg.what = ImageHandler.MSG_INIT;
						mMsg.obj = outBitmap.copy(Config.ARGB_8888, true);
						if(mCurrentPageData instanceof AbsChannelAdRes.ColorPageData)
						{
							mMsg.arg1 = getEffectType(((AbsChannelAdRes.ColorPageData)mCurrentPageData).mColor);
							mMsg.arg2 = ((AbsChannelAdRes.ColorPageData)mCurrentPageData).mAlpha;
						}
						else
						{
							mMsg.arg1 = getEffectType("轻微");
							mMsg.arg2 = 80;
						}
						String ver = SysConfig.GetAppVer(getContext());
						if(ver != null && ver.equals("88.8.8"))
						{
							//显示颜色透明度的toast
							Toast.makeText(getContext(), "效果：" + mMsg.arg1 + ",百分比：" + mMsg.arg2, Toast.LENGTH_LONG).show();

						}
						mImageHandler.sendMessage(mMsg);
					}
				},delayTime);
			}
			else
			{
				//返回编辑
				InitData();
				InitUI();
				if(mView != null)
				{
					mViewFr.removeView(mView);
				}

				mOrgInfo = params.get("imgs");

				Bitmap outBitmap = ImageUtils.MakeBmp(getContext(), mOrgInfo, DEF_IMG_SIZE, DEF_IMG_SIZE);
				{
					//加毛玻璃背景
					Bitmap bk = BeautifyResMgr2.MakeBkBmp(outBitmap, ShareData.m_screenWidth, ShareData.m_screenHeight);
					setBackgroundDrawable(new BitmapDrawable(getResources(), bk));
				}

				mView = new ADCoreView(getContext(), mViewW, mViewH);
				mView.def_rotation_res = R.drawable.photofactory_pendant_rotation;
				mView.InitData(mCtrlInterface);
				mView.SetOperateMode(CoreViewV3.MODE_IMAGE);
				LayoutParams fl_lp = new LayoutParams(mViewW, mViewH);
				mView.setLayoutParams(fl_lp);
				s_img.m_bmp = outBitmap;
				mView.SetImg2(s_img);
				mView.CreateViewBuffer();
				mView.UpdateUI();
				mViewFr.addView(mView);

				if(sPageData != null)
				{
					mCurrentPageData = sPageData;
					sPageData = null;
					SetModuleUI(mCurrentPageData);
					if(mCurrentPageData instanceof AbsChannelAdRes.FramePageData)
					{
						if(mFrameList != null && mFrameList.m_view != null)
						{
							mFrameUri = s_frameUri;
							mFrameList.m_view.SetSelectByUri(mFrameUri);
							mFrameList.ScrollToCenter(true);
						}
					}
				}

				String color = "轻微";
				int alpha = 80;
				if(mCurrentPageData instanceof AbsChannelAdRes.ColorPageData)
				{
					color = ((AbsChannelAdRes.ColorPageData)mCurrentPageData).mColor;
					alpha = ((AbsChannelAdRes.ColorPageData)mCurrentPageData).mAlpha;
				}
				else if(mCurrentPageData instanceof AbsChannelAdRes.FramePageData)
				{
					color = ((AbsChannelAdRes.FramePageData)mCurrentPageData).mColor;
					alpha = ((AbsChannelAdRes.FramePageData)mCurrentPageData).mAlpha;
				}
				else if(mCurrentPageData instanceof AbsChannelAdRes.DecoratePageData)
				{
					color = ((AbsChannelAdRes.DecoratePageData)mCurrentPageData).mColor;
					alpha = ((AbsChannelAdRes.DecoratePageData)mCurrentPageData).mAlpha;
				}
				else if(mCurrentPageData instanceof AbsChannelAdRes.BeautyPageData)
				{
					color = ((AbsChannelAdRes.BeautyPageData)mCurrentPageData).mColor;
					alpha = ((AbsChannelAdRes.BeautyPageData)mCurrentPageData).mAlpha;
				}
				RestoreMsg restoreMsg = new RestoreMsg();
				restoreMsg.m_orgBmp = outBitmap.copy(Config.ARGB_8888, true);
				restoreMsg.m_effect = getEffectType(color);
				restoreMsg.m_alpha = alpha;
				if(mFrameUri != 0)
				{
					if(isFrameUri)
					{
						FrameRes info = getFrameByUri(mFrameUri);
						if(info != null)
						{
							restoreMsg.m_w = outBitmap.getWidth();
							restoreMsg.m_h = outBitmap.getHeight();
							restoreMsg.m_frameInfo = info;
						}
					}
				}

				Message mMsg = mImageHandler.obtainMessage();
				mMsg.what = ImageHandler.MSG_RESTORE;
				mMsg.obj = restoreMsg;
				mImageHandler.sendMessage(mMsg);
			}
		}
		else
		{
			setAdvancedPage(params);
		}
	}

	@Override
	public void onClose()
	{
		mUiEnabled = false;

		mImageThread.quit();
		mImageThread = null;

		if(mOrgBmp != null)
		{
			mOrgBmp.recycle();
			mOrgBmp = null;
		}
		if(mView != null)
		{
			mViewFr.removeView(mView);
			mView.ReleaseMem();
		}
		if(mFrameList != null)
		{
			mViewFr.removeView(mFrameList);
			mFrameList.ClearAll();
		}
		if(m_waitDlg != null)
		{
			m_waitDlg.dismiss();
			m_waitDlg = null;
		}

		TongJiUtils.onPageEnd(getContext(), R.string.通用商业);
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.通用商业);
		super.onResume();
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.通用商业);
		super.onPause();
	}

	private void reLayoutViewFr()
	{
		if(mViewFr != null)
		{
			FrameLayout.LayoutParams fl = (LayoutParams) mViewFr.getLayoutParams();
			if(fl != null && fl.topMargin != 0)
			{
				fl.topMargin = 0;
				if((ShareData.m_screenHeight - m_bottomResListHeight) < mViewFrH)
				{
					fl.height = (ShareData.m_screenHeight - m_bottomResListHeight);
				}
				mViewFr.setLayoutParams(fl);
			}
		}
	}

	private class UIHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case ImageHandler.MSG_INIT:
				{
					mOrgBmp = (Bitmap)msg.obj;
					if(mView.m_img != null)
					{
						mView.m_img.m_bmp = mOrgBmp;
					}
					mView.UpdateUI();

					SetWaitUI(false, "");
					//美化后动画
					if(isBeautifyPage)
					{
						if(mCurrentPageData instanceof AbsChannelAdRes.ColorPageData)
						{
							ActAnimationInfo anim = ((AbsChannelAdRes.ColorPageData)mCurrentPageData).mAnim;
							if(anim != null && anim.frames.size() > 0)
							{
								if(anim.place.equals("after_beautify"))
								{
									showAnimation();
								}
							}
						}
					}
					else
					{
						//选中默认
						if(mCurrentPageData instanceof AbsChannelAdRes.FramePageData)
						{
							SetSelItemByUri(((AbsChannelAdRes.FramePageData)mCurrentPageData).mDefaultSel);
						}
						else if(mCurrentPageData instanceof AbsChannelAdRes.DecoratePageData)
						{
							SetSelItemByUri(((AbsChannelAdRes.DecoratePageData)mCurrentPageData).mDefaultSel);
						}
					}
					mUiEnabled = true;

					break;
				}
				case ImageHandler.MSG_UPDATE_FRAME:
				{
					ImageHandler.FrameMsg params = (ImageHandler.FrameMsg)msg.obj;
					msg.obj = null;

					if(params.m_frameInfo != null)
					{
						mView.SetFrame3(params.m_frameInfo, params.m_fThumb);
						UpdateUI2Frame(params.m_frameInfo, params.m_fThumb);
					}
					mView.UpdateUI();

					SetWaitUI(false, "");
					mUiEnabled = true;
					break;
				}
				case ImageHandler.MSG_ADVANCED_PAGE:
				{
					SetWaitUI(false, "");
					if(msg.obj instanceof String)
					{
						RotationImg2 img = Utils.Path2ImgObj((String)msg.obj);
						m_site.OpenAdvBeauty(new RotationImg2[]{img},getContext());
					}
					break;
				}
				case ImageHandler.MSG_RESTORE:
				{
					RestoreMsg params = (RestoreMsg)msg.obj;

					mOrgBmp = params.m_beautifyBmp;
					if(mView.m_img != null)
					{
						mView.m_img.m_bmp = mOrgBmp;
					}

					if(params.m_frameInfo != null)
					{
						mView.SetFrame(params.m_frameInfo, params.m_frameBmp);
						UpdateUI2Frame(params.m_frameInfo, params.m_frameBmp);
					}

					if(s_pendantArr != null && s_pendantArr.size() > 0)
					{
						int len = s_pendantArr.size();
						for(int i = 0; i < len; i++)
						{
							ShapeEx info = s_pendantArr.get(i);
							if(info.m_ex != null && info.m_ex instanceof DecorateRes)
							{
								Bitmap tempBitmap = mCtrlInterface.MakeShowPendant(info.m_ex, mView.m_origin.m_w, mView.m_origin.m_h);
								if(tempBitmap != null)
								{
									info.m_bmp = tempBitmap;
									mView.AddPendant2(info);
								}
							}
						}
					}

					mView.UpdateUI();

					SetWaitUI(false, "");
					mUiEnabled = true;
					break;
				}
				default:
					break;
			}
		}
	}

}

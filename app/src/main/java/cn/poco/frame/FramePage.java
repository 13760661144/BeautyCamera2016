package cn.poco.frame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.advanced.AdvancedResMgr;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.credits.Credit;
import cn.poco.display.CoreViewV3;
import cn.poco.face.FaceDataV2;
import cn.poco.filterPendant.ColorChangeLayoutV2;
import cn.poco.filterPendant.ColorItemInfo;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.frame.site.FramePageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.BaseExAdapter;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FrameExRes;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameRes;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.GroupRes;
import cn.poco.resource.IDownload;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ResourceUtils;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.widget.recycle.RecommendAdapter;
import cn.poco.widget.recycle.RecommendConfig;
import cn.poco.widget.recycle.RecommendDragContainer;
import cn.poco.widget.recycle.RecommendExAdapter;
import cn.poco.widget.recycle.RecommendExConfig;
import my.beautyCamera.R;

/**
 * 边框页
 *
 * @since v4.0
 */
public class FramePage extends IPage
{
	//private static final String TAG = "相框";
	private int DEF_IMG_SIZE;

	private FramePageSite mSite;

	private boolean mUiEnabled;
	private boolean mQuit;

	private HandlerThread mImageThread;
	private FrameHandler mMainHandler;

	private Bitmap mOrgBmp;
	private int mFrameUri;
	private int mSelFrameType;
	private static final int TYPE_THEME = 1;
	private static final int TYPE_SIMPLE = 2;
	private int mFrameColor = 0xffffffff;
	private int mFrameColorIndex = -1;
	private int mFrW;
	private int mFrH;
	private FrameView mView;
	private FrameLayout mBottomFr;
	private boolean isFold = false;
	private int mBottomLayoutHeight;


	private LinearLayout mBtnFr;
	private MyStatusButton mThemeBtn;
	private MyStatusButton mSimpleBtn;
	private FrameLayout mColorIcon;
	private ImageView mCancelBtn;
	private ImageView mOkBtn;
	private FrameLayout mBottomBar;

	private boolean mIsThemeFrame = true;
	private int mBottomBarHeight;

	private ArrayList<RecommendExAdapter.ItemInfo> mThemeFrameInfo;
	private ArrayList<RecommendAdapter.ItemInfo> mSimpleFrameInfo;
	private RecommendExAdapter mThemeFrameAdapter;
	private RecommendAdapter mSimpleFrameAdapter;
	private RecommendDragContainer mThemeFrameList;
	private RecommendDragContainer mSimpleFrameList;
	private RecommendExConfig mThemeFrameConfig;
	private RecommendConfig mSimpleFrameConfig;


	private RecomDisplayMgr mRecomView;

	private ColorChangeLayoutV2 mColorPalette;
	private ArrayList<ColorItemInfo> mColorArr;
	private boolean isColorPaleteeShow = false;

	//ui anim
	private float m_currImgH = 0f;
	private int m_imgH = 0;
	private int m_viewH = 0;
	private int m_viewTopMargin;
	private static final int SHOW_VIEW_ANIM = 300;

	public FramePage(Context context, BaseSite site)
	{
		super(context, site);

		mSite = (FramePageSite)site;
		InitData();
		InitUI();

		MyBeautyStat.onPageStartByRes(R.string.美颜美图_相框页面_主页面);
		TongJiUtils.onPageStart(getContext(), R.string.相框);
	}

	protected void InitData()
	{
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

		mBottomLayoutHeight = ShareData.PxToDpi_xhdpi(232);
		mBottomBarHeight = ShareData.PxToDpi_xhdpi(88);
		mFrW = ShareData.m_screenWidth;
		mFrH = ShareData.m_screenHeight - mBottomBarHeight - mBottomLayoutHeight;

		UIHandler mUIHandler = new UIHandler();
		mImageThread = new HandlerThread("frame_handler_thread");
		mImageThread.start();
		mMainHandler = new FrameHandler(mImageThread.getLooper(), getContext(), mUIHandler);

		mThemeFrameConfig = new RecommendExConfig();
		mSimpleFrameConfig = new RecommendConfig();

		mUiEnabled = false;

		if(DownloadMgr.getInstance() != null)
		{
			DownloadMgr.getInstance().AddDownloadListener(mDownloadLst);
		}
	}

	protected void InitUI()
	{
		LayoutParams fl;

		int viewW = mFrW + 2;
		int viewH = ShareData.m_screenHeight - mBottomBarHeight;

		mView = new FrameView(getContext(), viewW, viewH);
		mView.InitData(mCtrlInterface);
		mView.SetOperateMode(FrameView.MODE_FRAME);

		viewH = mFrH;

		fl = new LayoutParams(viewW, viewH);
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		this.addView(mView, 0, fl);

		mBottomFr = new FrameLayout(getContext());
		fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(mBottomFr, fl);
		{
			//颜色编辑
			mColorIcon = new FrameLayout(getContext());
			mColorIcon.setBackgroundResource(R.drawable.beautify_white_circle_bg);
			{
				ImageView colorIcon = new ImageView(getContext());
				colorIcon.setImageResource(R.drawable.advanced_beautify_frame_color_palette);
				ImageUtils.AddSkin(getContext(), colorIcon);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER;
				mColorIcon.addView(colorIcon, fl);
			}
			mColorIcon.setOnTouchListener(mOnAnimationClickListener);
			mColorIcon.setVisibility(View.GONE);
			fl = new LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(80));
			fl.gravity = Gravity.RIGHT | Gravity.BOTTOM;
			fl.setMargins(0, 0, ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(28));
			fl.bottomMargin = mBottomBarHeight + mBottomLayoutHeight;
			mBottomFr.addView(mColorIcon, fl);

			mBottomBar = new FrameLayout(getContext());
			mBottomBar.setBackgroundColor(0xe6ffffff);
			fl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBottomBarHeight);
			fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			fl.bottomMargin = mBottomLayoutHeight;
			mBottomBar.setLayoutParams(fl);
			mBottomFr.addView(mBottomBar);
			{
				mCancelBtn = new ImageView(getContext());
				mCancelBtn.setImageResource(R.drawable.beautify_cancel);
				mCancelBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
				mCancelBtn.setLayoutParams(fl);
				mBottomBar.addView(mCancelBtn);
				mCancelBtn.setOnTouchListener(mOnAnimationClickListener);

				mOkBtn = new ImageView(getContext());
				mOkBtn.setImageResource(R.drawable.beautify_ok);
				mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
				ImageUtils.AddSkin(getContext(), mOkBtn);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
				mOkBtn.setLayoutParams(fl);
				mBottomBar.addView(mOkBtn);
				mOkBtn.setOnTouchListener(mOnAnimationClickListener);

				mBtnFr = new LinearLayout(getContext());
				mBtnFr.setOrientation(LinearLayout.HORIZONTAL);
				fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.CENTER;
				mBtnFr.setLayoutParams(fl);
				mBottomBar.addView(mBtnFr);
				mBtnFr.setOnClickListener(mOnClickListener);
				{
					LinearLayout.LayoutParams ll;
					mThemeBtn = new MyStatusButton(getContext());
					mThemeBtn.setData(ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.frame_theme_icon)), getContext().getString(R.string.framepage_theme));
					mThemeBtn.setBtnStatus(mIsThemeFrame, isFold);
					mThemeBtn.setOnClickListener(mOnClickListener);
					ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
					ll.gravity = Gravity.CENTER_VERTICAL;
					ll.rightMargin = ShareData.PxToDpi_xhdpi(14);
					mBtnFr.addView(mThemeBtn, ll);

					mSimpleBtn = new MyStatusButton(getContext());
					mSimpleBtn.setData(ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.frame_simple_icon)), getContext().getString(R.string.framepage_simple));
					mSimpleBtn.setBtnStatus(!mIsThemeFrame, isFold);
					mSimpleBtn.setOnClickListener(mOnClickListener);
					ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
					ll.gravity = Gravity.CENTER_VERTICAL;
					ll.setMargins(ShareData.PxToDpi_xhdpi(40 + 14), 0, 0, 0);
					mBtnFr.addView(mSimpleBtn, ll);
				}
			}

			//素材区域
			mSimpleFrameInfo = FrameResMgr.GetSimpleFrameRes((Activity)getContext());
			mThemeFrameInfo = FrameResMgr.GetThemeFrameRes((Activity)getContext());

			mThemeFrameAdapter = new RecommendExAdapter(mThemeFrameConfig);
			mSimpleFrameAdapter = new RecommendAdapter(mSimpleFrameConfig);
			mThemeFrameAdapter.SetData(mThemeFrameInfo);
			mSimpleFrameAdapter.SetData(mSimpleFrameInfo);
			mThemeFrameAdapter.setOnItemClickListener(mThemeFrameListCB);
			mThemeFrameAdapter.setOnDragCallBack(mThemeFrameListDragCB);
			mSimpleFrameAdapter.setOnItemClickListener(mSimpleFrameListCB);
			mSimpleFrameAdapter.setOnDragCallBack(mSimpleFrameListDragCB);

			mThemeFrameList = new RecommendDragContainer(getContext(), mThemeFrameAdapter);
			mSimpleFrameList = new RecommendDragContainer(getContext(), mSimpleFrameAdapter);


			mSimpleFrameList.setVisibility(View.GONE);
			fl = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			mBottomFr.addView(mSimpleFrameList, fl);

			fl = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			mBottomFr.addView(mThemeFrameList, fl);
		}

		mRecomView = new RecomDisplayMgr(getContext(), new RecomDisplayMgr.Callback()
		{
			@Override
			public void UnlockSuccess(BaseRes res)
			{

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

			}

			@Override
			public void OnLogin()
			{
				if(mSite != null)
				{
					mSite.OnLogin(getContext());
				}
			}
		});
		mRecomView.Create(this);
	}

	protected class UIHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case FrameHandler.MSG_UPDATE_FRAME:
				{
					FrameHandler.FrameMsg params = (FrameHandler.FrameMsg)msg.obj;
					msg.obj = null;

					if(mView != null)
					{
						if(params.m_frameInfo != null)
						{
							mView.SetFrame(params.m_frameInfo, params.m_fThumb, params.m_rect, params.m_resetPos);
							UpdateUI2Frame(params.m_frameInfo, params.m_fThumb);
						}
						mView.UpdateUI();
					}
					mUiEnabled = true;
					break;
				}

				default:
					break;
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
		Object bk = null;
		int bkColor = 0xffffffff;

		if(info instanceof FrameRes)
		{
			FrameRes fInfo = (FrameRes)info;

			bk = fInfo.f_bk;
			bkColor = fInfo.m_bkColor;
		}

		if(!AdvancedResMgr.IsNull(bk))
		{
			mView.SetBkBmp(bk, null);
		}
		else
		{
			mView.SetBkColor(bkColor);
		}
	}

		private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v == mThemeBtn)
			{
				mSelFrameType = TYPE_THEME;
				mThemeBtn.setNewStatus(false);
//				hideColorIconAnim();
				if(mIsThemeFrame)
				{
					isFold = !isFold;
					mThemeBtn.setBtnStatus(true, !mThemeBtn.isDown());
					mSimpleBtn.setBtnStatus(false, !mSimpleBtn.isDown());
					SetViewState(isFold);
					SetBottomFrState(isFold);
					MyBeautyStat.onClickByRes(isFold ? R.string.美颜美图_相框页面_主页面_主题tab_收回 : R.string.美颜美图_相框页面_主页面_主题tab_展开);
				}
				else
				{
					mSimpleFrameList.setVisibility(View.GONE);
					mThemeFrameList.setVisibility(View.VISIBLE);
					boolean oldIsFold = isFold;
					isFold = false;
					mThemeBtn.setBtnStatus(true, false);
					mSimpleBtn.setBtnStatus(false, false);
					if(oldIsFold)
					{
						SetViewState(isFold);
						SetBottomFrState(isFold);
					}
					mIsThemeFrame = true;
					MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_主题tab_展开);
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_相框_主题);
				}
			}
			else if(v == mSimpleBtn)
			{
				mSelFrameType = TYPE_SIMPLE;
//				showColorIconAnim();
				if(!mIsThemeFrame)
				{
					isFold = !isFold;
					mThemeBtn.setBtnStatus(false, !mThemeBtn.isDown());
					mSimpleBtn.setBtnStatus(true, !mSimpleBtn.isDown());
					SetViewState(isFold);
					SetBottomFrState(isFold);
					MyBeautyStat.onClickByRes(isFold ? R.string.美颜美图_相框页面_主页面_简约tab_收回 : R.string.美颜美图_相框页面_主页面_简约tab_展开);
				}
				else
				{
					mSimpleFrameList.setVisibility(View.VISIBLE);
					mThemeFrameList.setVisibility(View.GONE);
					boolean oldIsFold = isFold;
					isFold = false;
					mThemeBtn.setBtnStatus(false, false);
					mSimpleBtn.setBtnStatus(true, false);
					if(oldIsFold)
					{
						SetViewState(isFold);
						SetBottomFrState(isFold);
					}
					mIsThemeFrame = false;
					MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_简约tab_展开);
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_相框_简约);
				}
			}
			else if(v == mBtnFr)
			{
				if(mIsThemeFrame)
				{
					mOnClickListener.onClick(mThemeBtn);
				}
				else
				{
					mOnClickListener.onClick(mSimpleBtn);
				}
			}
		}
	};

	private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(v == mCancelBtn)
			{
				if(mRecomView != null && mRecomView.IsShow())
				{
					mRecomView.OnCancel(true);
					return;
				}
				MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_取消);
				TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_相框_取消);
				if(mFrameUri != 0)
				{
					showExitDialog();
				}
				else
				{
					cancel();
				}
			}
			else if(v == mOkBtn)
			{
				//统计
				if(mView.m_frame != null)
				{
					final Object obj = mView.m_frame.m_ex;
					if(obj instanceof BaseRes )
					{
						TongJi2.AddCountById(((BaseRes)obj).m_tjId + "");
						String param = Credit.APP_ID + Credit.FRAME + ((BaseRes)obj).m_id;
						Credit.CreditIncome(param, getContext(), R.integer.积分_首次使用新素材);

						if (obj instanceof FrameRes)
						{
							MyBeautyStat.onUseFrame(String.valueOf(((FrameRes) obj).m_tjId));
						}
						else if (obj instanceof FrameExRes)
						{
							MyBeautyStat.onUseSimpleFrame(String.valueOf(((FrameExRes) obj).m_tjId), mFrameColorIndex);
						}
					}
				}
				MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_确认);
				TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_相框_确认);
				if(mSite != null)
				{
					if(mFrameUri != 0)
					{
						Bitmap bmp = mView.GetOutputBmp();
						HashMap<String, Object> params = new HashMap<>();
						params.put("img", bmp);
						params.putAll(getBackAnimParam());
						mSite.OnSave(getContext(), params);
					}
					else
					{
						cancel();
					}
				}
			}
			else if(v == mColorIcon)
			{
				if(mColorArr == null)
				{
					mColorArr = FrameResMgr.GetColorPaletteArr();
				}
				if(mColorPalette == null)
				{
					mColorPalette = new ColorChangeLayoutV2(getContext());
					mColorPalette.SetData(mColorArr);
					LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(320));
					fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
					FramePage.this.addView(mColorPalette, fl);
					mColorPalette.setmItemOnClickListener(mColorPaletteLst);
				}
				mColorPalette.setSelectedColor(mFrameColor);
				mFrameColorIndex = mColorPalette.getSelectedPosition();
				SetColorPaletteState(isColorPaleteeShow, true);
				isColorPaleteeShow = !isColorPaleteeShow;
				if (isColorPaleteeShow)
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_背景颜色);
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

//    private CloudAlbumDialog mBackHintDialog;
//
//    private void showExitDialog() {
//        if (mBackHintDialog == null) {
//            mBackHintDialog = new CloudAlbumDialog(getContext(),
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            ImageUtils.AddSkin(getContext(), mBackHintDialog.getOkButtonBg());
//            mBackHintDialog.setCancelButtonText(R.string.cancel)
//                    .setOkButtonText(R.string.ensure)
//                    .setMessage(R.string.confirm_back)
//                    .setListener(new CloudAlbumDialog.OnButtonClickListener() {
//                        @Override
//                        public void onOkButtonClick() {
//                            mBackHintDialog.dismiss();
//                            cancel();
//                        }
//
//                        @Override
//                        public void onCancelButtonClick() {
//                            mBackHintDialog.dismiss();
//                        }
//                    });
//        }
//        mBackHintDialog.show();
//    }

	private void cancel()
	{
		if(mSite != null)
		{
			HashMap<String, Object> params = new HashMap<>();
			params.put("img", mOrgBmp);
			params.putAll(getBackAnimParam());
			mSite.OnBack(getContext(), params);
		}
	}

	protected ColorChangeLayoutV2.OnColorItemClickListener mColorPaletteLst = new ColorChangeLayoutV2.OnColorItemClickListener()
	{
		@Override
		public void onDownClick()
		{
			if(mColorPalette != null)
			{
				mColorPalette.setVisibility(View.GONE);
			}
			SetColorPaletteState(true, true);
			mBottomFr.setVisibility(View.VISIBLE);
			isColorPaleteeShow = false;
		}

		@Override
		public void onColorItemClick(int position, Object result)
		{
			if(result instanceof ColorItemInfo)
			{
				mFrameColorIndex = position;
				FrameExRes res = FrameExResMgr2.getInstance().GetRes(mFrameUri);
				if(res != null)
				{
					//发送处理消息
					SendFrameMsg(res, false, ((ColorItemInfo)result).m_color);
					//TongJi2.AddCountById(Integer.toString(info.m_tjId));
					//TongJi2.add_using_count("美化/边框/" + info.m_name);
					if(mColorPalette != null)
					{
						mColorPalette.setSelectedPosition(((ColorItemInfo)result).m_position);
					}
				}
			}
		}
	};

	private AbsDragAdapter.OnDragCallBack mThemeFrameListDragCB = new AbsDragAdapter.OnDragCallBack() {
		@Override
		public void onItemDelete(AbsDragAdapter.ItemInfo info, int position) {
			RecommendExAdapter.ItemInfo itemInfo = (RecommendExAdapter.ItemInfo) info;
			ThemeRes themeRes = ThemeResMgr2.getInstance().GetRes(itemInfo.m_uri);
			GroupRes res = new GroupRes();
			res.m_themeRes = themeRes;
			res.m_ress = new ArrayList<>();
			for (int i = 1; i < itemInfo.m_uris.length; i++) {
				res.m_ress.add(cn.poco.resource.FrameResMgr2.getInstance().GetRes(itemInfo.m_uris[i]));
			}
			cn.poco.resource.FrameResMgr2.getInstance().DeleteGroupRes(getContext(),res);

			((RecommendExAdapter.DownloadItemInfo)mThemeFrameInfo.get(0)).setNum(cn.poco.resource.FrameResMgr2.getInstance().GetNoDownloadCount());
			mThemeFrameAdapter.notifyItemChanged(0);

			RecommendExAdapter.RecommendItemInfo recommendInfo = FrameResMgr.getThemeRecommendInfo(getContext());
			int recommendIndex = mThemeFrameAdapter.GetIndex((RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI));
			if(recommendInfo != null && recommendIndex >=0 ) {
                mThemeFrameInfo.set(recommendIndex, recommendInfo);
                mThemeFrameAdapter.notifyItemChanged(recommendIndex);
			}
		}

		@Override
		public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition) {
			AbsAdapter.ItemInfo itemInfo = mThemeFrameInfo.get(fromPosition);
			int fromPos = fromPosition;
			int toPos = toPosition;
			if (itemInfo != null) {
				fromPos = ResourceUtils.HasId(FrameResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
			}
			itemInfo = mThemeFrameInfo.get(toPosition);
			if (itemInfo != null) {
				toPos = ResourceUtils.HasId(FrameResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
			}
			if(fromPos >=0 && toPos >=0){
				ResourceUtils.ChangeOrderPosition(FrameResMgr2.getInstance().GetOrderArr(), fromPos, toPos);
				FrameResMgr2.getInstance().SaveOrderArr();
			}
		}

		@Override
		public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position) {
			int out = ResourceUtils.HasItem(ThemeResMgr2.getInstance().sync_GetLocalRes(getContext(), null),itemInfo.m_uri);
			if(out >=0 ){
				mThemeFrameList.setCanDelete(false);
			}else{
				mThemeFrameList.setCanDelete(true);
			}
		}

		@Override
		public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position) {

		}

		@Override
		public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position) {

		}
	};

	private AbsDragAdapter.OnDragCallBack mSimpleFrameListDragCB = new AbsDragAdapter.OnDragCallBack() {
		@Override
		public void onItemDelete(AbsDragAdapter.ItemInfo info, int position) {

		}

		@Override
		public void onItemMove(AbsDragAdapter.ItemInfo info, int fromPosition, int toPosition) {
			AbsAdapter.ItemInfo itemInfo = mSimpleFrameInfo.get(fromPosition);
			int fromPos = fromPosition;
			int toPos = toPosition;
			if (itemInfo != null) {
				fromPos = ResourceUtils.HasId(FrameExResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
			}
			itemInfo = mSimpleFrameInfo.get(toPosition);
			if (itemInfo != null) {
				toPos = ResourceUtils.HasId(FrameExResMgr2.getInstance().GetOrderArr(), itemInfo.m_uri);
			}
			if(fromPos >=0 && toPos >=0){
				ResourceUtils.ChangeOrderPosition(FrameExResMgr2.getInstance().GetOrderArr(), fromPos, toPos);
				FrameExResMgr2.getInstance().SaveOrderArr();
			}
		}

		@Override
		public void onDragStart(AbsDragAdapter.ItemInfo itemInfo, int position) {
			mSimpleFrameList.setCanDelete(false);
//			BaseRes res = cn.poco.resource.ThemeResMgr.GetLocalResArr().get(itemInfo.m_uri);
//			if(res != null){
//				mSimpleFrameList.setCanDelete(false);
//			}
		}

		@Override
		public void onDragEnd(AbsDragAdapter.ItemInfo itemInfo, int position) {

		}

		@Override
		public void onLongClick(AbsDragAdapter.ItemInfo itemInfo, int position) {

		}
	};

	private RecommendExAdapter.OnItemClickListener mThemeFrameListCB = new RecommendExAdapter.OnItemClickListener()
	{
		@Override
		public void OnItemDown(AbsAdapter.ItemInfo info, int index)
		{

		}

		@Override
		public void OnItemUp(AbsAdapter.ItemInfo info, int index)
		{

		}

		@Override
		public void OnItemClick(AbsAdapter.ItemInfo info, int index)
		{
		}

		@Override
		public void OnItemDown(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
		{
		}

		@Override
		public void OnItemUp(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
		{
		}

		@Override
		public void OnItemClick(BaseExAdapter.ItemInfo info, int parentIndex, int subIndex)
		{
			switch(info.m_uris[0])
			{
				case RecommendExAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI:
					//下载更多
					TongJi2.AddCountByRes(getContext(), R.integer.美化_边框_下载更多);
					openDownloadMorePage(ResType.FRAME);
					break;
				case RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI:
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_主题相框_推荐位);
					OpenRecommendView((ArrayList<RecommendRes>)((RecommendExAdapter.ItemInfo)info).m_ex, ResType.FRAME.GetValue());
					//推荐位统计
					break;
				default:
					if(subIndex > 0)
					{
						//选中子项
						int selUri = ((RecommendExAdapter.ItemInfo)info).m_uris[subIndex];
						if(mFrameUri != selUri)
						{
							hideColorIconAnim();

							mSimpleFrameAdapter.CancelSelect();
							if(selUri == 0)
							{
								mView.SetFrame2(null);
								//图片位置重置(一定要在SetFrame后)
								mView.m_img.m_x = mView.m_viewport.m_x;
								mView.m_img.m_y = mView.m_viewport.m_y;
								mView.m_img.m_scaleX = mView.m_img.DEF_SCALE;
								mView.m_img.m_scaleY = mView.m_img.DEF_SCALE;
								mView.m_img.m_degree = 0;
								mView.UpdateUI();
							}
							else
							{
								FrameRes res = cn.poco.resource.FrameResMgr2.getInstance().GetRes(selUri);
								if(res != null)
								{
									//发送处理消息
									SendFrameMsg(res, true, 0);
								}
							}
							mFrameUri = selUri;
							mSelFrameType = TYPE_THEME;
							hideColorIconAnim();
						}
					}
			}
		}
	};

	private RecommendAdapter.OnItemClickListener mSimpleFrameListCB = new RecommendAdapter.OnItemClickListener()
	{
		@Override
		public void OnItemDown(AbsAdapter.ItemInfo info, int index)
		{

		}

		@Override
		public void OnItemUp(AbsAdapter.ItemInfo info, int index)
		{

		}

		@Override
		public void OnItemClick(AbsAdapter.ItemInfo info, int index)
		{
			if(mUiEnabled)
			{
				switch(info.m_uri)
				{
					case RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI:
						TongJi2.AddCountByRes(getContext(), R.integer.美化_边框_下载更多);
						TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_相框_更多素材);
						openDownloadMorePage(ResType.FRAME2);
						break;
					case RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI:
						//推荐位统计
						TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_白色相框_推荐位);
						OpenRecommendView((ArrayList<RecommendRes>)((RecommendAdapter.ItemInfo)info).m_ex, ResType.FRAME2.GetValue());
						break;
					default:
						int uri = ((RecommendAdapter.ItemInfo)info).m_uri;
						if(uri != mFrameUri)
						{
							showColorIconAnim();

							mThemeFrameAdapter.CancelSelect();

							mFrameUri = uri;
							FrameExRes res = FrameExResMgr2.getInstance().GetRes(mFrameUri);
							mSelFrameType = TYPE_SIMPLE;
							showColorIconAnim();
							//发送处理消息
							SendFrameMsg(res, true, 0);
							//TongJi2.AddCountById(Integer.toString(info.m_tjId));
							//TongJi2.add_using_count("美化/边框/" + info.m_name);
						}
						break;
				}
			}
		}
	};


	protected void OpenRecommendView(ArrayList<RecommendRes> ress, int type)
	{
		if (type == ResType.FRAME.GetValue())
		{
			MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_主题相框推荐位);
		}
		else if (type == ResType.FRAME2.GetValue())
		{
			MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_白色相框推荐位);
		}
		//推荐位
		RecommendRes recommendRes = null;
		if(ress != null && ress.size() > 0)
		{
			recommendRes = ress.get(0);
		}
		if(recommendRes != null && mRecomView != null)
		{
			mRecomView.SetBk(CommonUtils.GetScreenBmp((Activity)getContext(), ShareData.m_screenWidth / 8, ShareData.m_screenHeight / 8), true);
			mRecomView.SetDatas(recommendRes, type);
			mRecomView.Show();
		}
	}

	private void openDownloadMorePage(ResType resType)
	{
		MyBeautyStat.onClickByRes(R.string.美颜美图_相框页面_主页面_更多素材);
		if (mSite != null)
		{
			mSite.OpenDownloadMore(getContext(), resType);
		}
	}

	protected void SendFrameMsg(Object frameInfo, boolean resetPos, int color)
	{
		if(frameInfo != null)
		{
			if(color != 0)
			{
				mFrameColor = color;
			}
			else
			{
				if(frameInfo instanceof FrameExRes)
				{
					mFrameColor = ((FrameExRes)frameInfo).mMaskColor;
				}
			}
			if(frameInfo instanceof FrameExRes && ((FrameExRes)frameInfo).m_id == FrameExResMgr2.FRAME_WHITE_ID)
			{
				if(mView.m_frame != null && mView.m_frame.m_ex instanceof FrameExRes && ((FrameExRes)mView.m_frame.m_ex).m_id == FrameExResMgr2.FRAME_WHITE_ID && !resetPos)
				{
					mView.SetBkColor(mFrameColor);
					mView.UpdateUI();
				}
				else
				{
					mView.SetFrame2(null);
					mView.SetBkColor(mFrameColor);
					mView.SetWhiteFrame(frameInfo);
					mView.UpdateUI();
				}
			}
			else
			{
				mUiEnabled = false;
				FrameHandler.FrameMsg msgInfo = new FrameHandler.FrameMsg();
				msgInfo.m_w = mOrgBmp.getWidth();
				msgInfo.m_h = mOrgBmp.getHeight();
				msgInfo.m_frameInfo = frameInfo;
				msgInfo.m_color = mFrameColor;
				msgInfo.m_resetPos = resetPos;

				Message msg = mMainHandler.obtainMessage();
				msg.obj = msgInfo;
				msg.what = FrameHandler.MSG_UPDATE_FRAME;
				mMainHandler.sendMessage(msg);
			}
		}
	}

	private void setSelect(int uri)
	{
		if(mSelFrameType == TYPE_THEME)
		{
			mThemeFrameAdapter.SetSelectByUri(uri);
		}
		else if(mSelFrameType == TYPE_SIMPLE)
		{
			mSimpleFrameAdapter.SetSelectByUri(uri);
		}
	}

	public DownloadMgr.DownloadListener mDownloadLst = new AbsDownloadMgr.DownloadListener()
	{
		@Override
		public void OnDataChange(int resType, int downloadId, IDownload[] resArr)
		{
			if(resArr != null && ((BaseRes)resArr[0]).m_type == BaseRes.TYPE_LOCAL_PATH)
			{
				if(resType == ResType.FRAME.GetValue() || resType == ResType.FRAME2.GetValue())
				{
					boolean theme = false;
					boolean simple = false;
					for(IDownload temp : resArr)
					{
						if(temp instanceof FrameExRes)
						{
							simple = true;
						}
						else if(temp instanceof FrameRes)
						{
							theme = true;
						}
					}

					if(mIsThemeFrame)
					{
						if(simple)
						{
							mSimpleBtn.setNewStatus(true);
						}
						ArrayList<RecommendExAdapter.ItemInfo> dst = FrameResMgr.GetThemeFrameRes((Activity)getContext());

						if(dst.size() > mThemeFrameInfo.size())
						{
							mThemeFrameAdapter.notifyItemDownLoad(dst.size() - mThemeFrameInfo.size());
						}
						mThemeFrameInfo = dst;
						mThemeFrameAdapter.SetData(mThemeFrameInfo);
						mThemeFrameAdapter.notifyDataSetChanged();
					}
					else
					{
						if(theme)
						{
							mThemeBtn.setNewStatus(true);
						}
						ArrayList<RecommendAdapter.ItemInfo> dst = FrameResMgr.GetSimpleFrameRes((Activity)getContext());
						if(dst.size() > mSimpleFrameInfo.size())
						{
							mSimpleFrameAdapter.notifyItemDownLoad(dst.size() - mSimpleFrameInfo.size());
						}
						mSimpleFrameInfo = dst;
						mSimpleFrameAdapter.SetData(mSimpleFrameInfo);
						mSimpleFrameAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	};

	protected TweenLite m_tween = new TweenLite();

	protected void SetViewState(boolean isFold)
	{
		int mFrH = ShareData.m_screenHeight - mBottomBarHeight;
		int mFrH2 = mFrH - mBottomLayoutHeight;
		int start;
		int end;
		if(isFold)
		{
			start = mFrH2;
			end = mFrH;
		}
		else
		{
			start = mFrH;
			end = mFrH2;
		}
		m_tween.Init(start, end, 300);
		m_tween.M1Start(TweenLite.EASING_LINEAR | TweenLite.EASE_OUT);
		UpdateViewHeight();
	}

	protected void UpdateViewHeight()
	{
		if(mView != null && m_tween != null)
		{
			LayoutParams fl = new LayoutParams(mFrW + 2, (int)m_tween.M1GetPos());
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
			mView.setLayoutParams(fl);
			if(!m_tween.M1IsFinish())
			{
				this.postDelayed(mViewAnimRunnable, 1);
			}
		}
	}

	private Runnable mViewAnimRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			UpdateViewHeight();
		}
	};

	protected void SetBottomFrState(boolean isFold)
	{
		mBottomFr.clearAnimation();
		int start;
		int end;
		start = isFold ? 0 : mBottomLayoutHeight;
		end = isFold ? mBottomLayoutHeight : 0;
		ObjectAnimator object = ObjectAnimator.ofFloat(mBottomFr, "translationY", start, end);
		object.setDuration(300);
		object.setInterpolator(new AccelerateDecelerateInterpolator());
		object.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationStart(Animator animation)
			{
				mUiEnabled = false;
			}

			@Override
			public void onAnimationEnd(Animator animation)
			{
				mUiEnabled = true;
			}
		});
		object.start();
	}

	protected void SetColorPaletteState(boolean isFold, boolean hasAnim)
	{
		if(mColorPalette != null)
		{
			mColorPalette.clearAnimation();

			int start;
			int end;
			if(isFold)
			{
				mColorPalette.setVisibility(View.GONE);
				start = 0;
				end = 1;
			}
			else
			{
				mColorPalette.setVisibility(View.VISIBLE);
				start = 1;
				end = 0;
			}

			if(hasAnim)
			{
				mUiEnabled = false;
				AnimationSet as;
				TranslateAnimation ta;
				as = new AnimationSet(true);
				ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end);
				ta.setDuration(350);
				as.addAnimation(ta);
				as.setAnimationListener(new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{
					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						mUiEnabled = true;
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{
					}
				});
				mColorPalette.startAnimation(as);
			}
		}
	}

	protected FrameView.ControlCallback mCtrlInterface = new FrameView.ControlCallback()
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
			return null;
		}

		@Override
		public Bitmap MakeOutputPendant(Object info, int outW, int outH)
		{
			return null;
		}

		@Override
		public void SelectPendant(int index)
		{

		}
	};

	@Override
	public void onClose()
	{
		mUiEnabled = false;
		mQuit = true;

		clearExitDialog();

		if(mImageThread != null)
		{
			mImageThread.quit();
			mImageThread = null;
		}

		if(mRecomView != null)
		{
			mRecomView.ClearAllaa();
			mRecomView = null;
		}

		if(mView != null)
		{
			this.removeView(mView);
			if(mView.m_img != null)
			{
				mView.m_img.m_bmp = null;
			}
			mView.ReleaseMem();
			mView = null;
		}
		if(DownloadMgr.getInstance() != null)
		{
			DownloadMgr.getInstance().RemoveDownloadListener(mDownloadLst);
		}

		MyBeautyStat.onPageEndByRes(R.string.美颜美图_相框页面_主页面);
		TongJiUtils.onPageEnd(getContext(), R.string.相框);
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.相框);
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.相框);
		super.onResume();
	}

	/**
	 * @param params imgs : RotationImg[] / Bitmap
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		resetData();
		if(params != null)
		{
			Object o;
			o = params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
			if(o != null && o instanceof Integer)
			{
				mFrameUri = (int)params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
			}

			o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);
			if(o != null && o instanceof Integer)
			{
				m_imgH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
			if(o != null && o instanceof Integer)
			{
				m_viewH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
			if(o != null && o instanceof Integer)
			{
				m_viewTopMargin = (int)o;
			}

			if(params.get("imgs") != null)
			{
				SetImg(params.get("imgs"));
			}
		}

		if(mIsThemeFrame)
		{
			mThemeFrameList.setVisibility(View.VISIBLE);
			mSimpleFrameList.setVisibility(View.GONE);
		}
		else
		{
			mThemeFrameList.setVisibility(View.GONE);
			mSimpleFrameList.setVisibility(View.VISIBLE);
		}
		if(mFrameUri != 0)
		{
			int tempUri = mFrameUri;
			mFrameUri = 0;
			SetSelUri(tempUri);
		}
		else
		{
			if(mIsThemeFrame)
			{
				mThemeFrameAdapter.SetSelectByUri(FrameResMgr.GetThemeFirstUri(mThemeFrameInfo),false,false, true);
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if(!mUiEnabled || mQuit)
		{
			return true;
		}
		else
		{
			return super.onInterceptTouchEvent(ev);
		}
	}

	private void resetData()
	{
		FaceDataV2.ResetData();//清除人脸识别数据
	}


	public void SetImg(Object params)
	{
		if(params != null)
		{
			if(params instanceof RotationImg2[])
			{
				mOrgBmp = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), ((RotationImg2[])params)[0].m_img, ((RotationImg2[])params)[0].m_degree, -1, ((RotationImg2[])params)[0].m_flip, DEF_IMG_SIZE, DEF_IMG_SIZE);
			}
			else if(params instanceof Bitmap)
			{
				this.mOrgBmp = (Bitmap)params;
			}

			if(mOrgBmp != null)
			{
				SetBmp(mOrgBmp);
			}
		}
	}

	public void SetBmp(Bitmap bmp)
	{
		mOrgBmp = bmp;

		mView.SetImg(null, mOrgBmp);
		mView.SetLayoutMode(CoreViewV3.LAYOUT_MODE_MATCH_PARENT);
		mView.CreateViewBuffer();
		ShowStartAnim();
		mUiEnabled = true;
	}

	private void ShowStartAnim()
	{
		if(m_viewH > 0 && m_imgH > 0)
		{
			int tempStartY = (int)(ShareData.PxToDpi_xhdpi(90) + (m_viewH - mFrH) / 2f);
			float scaleX = mFrW * 1f / (float)mOrgBmp.getWidth();
			float scaleY = (mFrH - 2) * 1f / (float)mOrgBmp.getHeight();
			m_currImgH = mOrgBmp.getHeight() * Math.min(scaleX, scaleY);
			float scaleH = m_imgH / m_currImgH;
			ShowViewAnim(mView, tempStartY, 0, scaleH, 1f, SHOW_VIEW_ANIM);
		}
	}

	private HashMap<String, Object> getBackAnimParam()
	{
		HashMap<String, Object> params = new HashMap<>();
		float imgHeight = m_imgH;
		float viewHeight = ShareData.m_screenHeight - mBottomBarHeight - mBottomLayoutHeight;
		float topMargin = (mView.getHeight() - viewHeight) / 2f;
		if(topMargin > 0f)
		{
			imgHeight = mView.m_img.m_h * mView.m_img.m_scaleY;
		}
		params.put(Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN, topMargin);
		params.put(Beautify4Page.PAGE_BACK_ANIM_IMG_H, imgHeight);
		return params;
	}

	private void ShowViewAnim(final View view, int startY, int endY, float startScale, float endScale, int duration)
	{
		if(view != null)
		{
			AnimatorSet animatorSet = new AnimatorSet();
			ObjectAnimator object1 = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale);
			ObjectAnimator object2 = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale);
			ObjectAnimator object3 = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
			ObjectAnimator object4 = ObjectAnimator.ofFloat(mBottomFr, "translationY", mBottomBarHeight + mBottomLayoutHeight, 0);
			animatorSet.setDuration(duration);
			animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
			animatorSet.playTogether(object1, object2, object3, object4);
			animatorSet.addListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					mUiEnabled = true;
					view.clearAnimation();
					mBottomFr.clearAnimation();
				}
			});
			animatorSet.start();
		}
	}

	private void showColorIconAnim()
	{
		if(mColorIcon.getVisibility() == GONE)
		{
			mColorIcon.setScaleX(0);
			mColorIcon.setScaleY(0);
			mColorIcon.animate().scaleX(1).scaleY(1).setDuration(200).setListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationStart(Animator animation)
				{
					mColorIcon.setVisibility(VISIBLE);
				}
			}).start();
		}
	}

	private void hideColorIconAnim()
	{
		if(mColorIcon.getVisibility() == VISIBLE)
		{
			mColorIcon.setScaleX(1);
			mColorIcon.setScaleY(1);
			mColorIcon.animate().scaleX(0).scaleY(0).setDuration(200).setListener(new AnimatorListenerAdapter()
			{
				@Override
				public void onAnimationEnd(Animator animation)
				{
					mColorIcon.setVisibility(GONE);
				}
			}).start();
		}
	}

	private void SetSelUri(final int uri)
	{
		int temp_index = 0;
		boolean isTheme = true;
		final int[] is = RecommendExAdapter.GetSubIndexByUri(mThemeFrameInfo, uri);

		if(is[0] < 0 && is[1] <= 0)
		{
			temp_index = RecommendAdapter.GetIndex(mSimpleFrameInfo, uri);
			if(temp_index >= 0)
			{
				isTheme = false;
			}
			else
			{
				if(!mIsThemeFrame)
				{
					isTheme = false;
				}
			}
		}
		if(mIsThemeFrame)
		{
			if(isTheme)
			{
				if(is[0] >= 0 && is[1] > 0)
				{
					if(uri != RecommendExAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && uri != RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
					{
						mThemeFrameAdapter.SetSelectByIndex(is[0], is[1]);
					}

				}
			}
			else
			{
				mOnClickListener.onClick(mSimpleBtn);
				if(temp_index >= 0)
				{
					if(uri != RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && uri != RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
					{
						mSimpleFrameAdapter.SetSelectByIndex(temp_index);
					}
				}
			}
		}
		else
		{
			if(isTheme)
			{
				mOnClickListener.onClick(mThemeBtn);
				if(is[0] >= 0 && is[1] > 0)
				{
					if(uri != RecommendExAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && uri != RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
					{
						mThemeFrameAdapter.SetSelectByIndex(is[0], is[1]);
					}

				}
			}
			else
			{
				if(temp_index >= 0)
				{
					if(uri != RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && uri != RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
					{
						mSimpleFrameAdapter.SetSelectByIndex(temp_index);
					}
				}
			}
		}
	}

	@Override
	public void onBack()
	{
		if(mColorPalette != null && mColorPalette.getVisibility() == View.VISIBLE)
		{
			mColorPaletteLst.onDownClick();
		}
		else if(mOnAnimationClickListener != null)
		{
			mOnAnimationClickListener.onAnimationClick(mCancelBtn);
		}
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		if(siteID == SiteID.DOWNLOAD_MORE || siteID == SiteID.THEME_INTRO)
		{
			if(params != null)
			{
				boolean del = false;
				{
					Object obj = params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL);
					if(obj instanceof Boolean)
					{
						del = (Boolean)obj;
					}
				}
				if(del)
				{
//					int tempUri = mFrameUri;
//					mFrameUri = 0;
					if(mIsThemeFrame)
					{
						mThemeFrameInfo = FrameResMgr.GetThemeFrameRes((Activity)getContext());
						mThemeFrameAdapter.SetData(mThemeFrameInfo);
						mThemeFrameAdapter.CancelSelect();
						mThemeFrameAdapter.setOpenIndex(-1);
						mThemeFrameAdapter.notifyDataSetChanged();
//						mThemeFrameAdapter.SetSelectByUri(mFrameUri);
					}
					else
					{
						hideColorIconAnim();
						mSimpleFrameInfo = FrameResMgr.GetSimpleFrameRes((Activity)getContext());
						mSimpleFrameAdapter.SetData(mSimpleFrameInfo);
//						mSimpleFrameAdapter.SetSelectByUri(mFrameUri);
						mSimpleFrameAdapter.CancelSelect();
						mSimpleFrameAdapter.notifyDataSetChanged();
					}

					//素材被删除后，暂时用
//					mView.SetFrame2(null);
//					mView.UpdateUI();
				}

				if(params.get(DataKey.BEAUTIFY_DEF_SEL_URI) != null)
				{
					int uri = (int)params.get(DataKey.BEAUTIFY_DEF_SEL_URI);
					if(del && uri == 0) return;//删除素材并且没有指定素材后，沿用原素材id
					// 指定uri素材
					if(uri != 0)
					{
						setSelect(uri);
					}
				}
			}
		}
		else if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL)
		{
			if(mRecomView != null)
			{
				mRecomView.UpdateCredit();
			}
		}
	}


	private CloudAlbumDialog mExitDialog;

	private void showExitDialog()
	{
		if (mExitDialog == null)
		{
			mExitDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
			mExitDialog.setCancelable(true).setCancelButtonText(R.string.cancel).setOkButtonText(R.string.ensure).setMessage(R.string.confirm_back).setListener(new CloudAlbumDialog.OnButtonClickListener()
			{
				@Override
				public void onOkButtonClick()
				{
					if (mExitDialog != null)
					{
						mExitDialog.dismiss();
					}
					cancel();
				}

				@Override
				public void onCancelButtonClick()
				{
					if (mExitDialog != null)
					{
						mExitDialog.dismiss();
					}
				}
			});
		}
		mExitDialog.show();
	}

	private void clearExitDialog()
	{
		if (mExitDialog != null)
		{
			mExitDialog.dismiss();
			mExitDialog.setListener(null);
			mExitDialog = null;
		}
	}
}

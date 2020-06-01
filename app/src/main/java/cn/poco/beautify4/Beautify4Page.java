package cn.poco.beautify4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsChannelAdRes;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.MaterialMgr2.site.ThemeIntroPageSite;
import cn.poco.PhotoPicker.LocalPhotoAdapter;
import cn.poco.acne.AcnePage;
import cn.poco.acne.site.AcneSite;
import cn.poco.adMaster.HomeAd;
import cn.poco.advanced.ImageUtils;
import cn.poco.album.PhotoStore;
import cn.poco.album.model.PhotoInfo;
import cn.poco.album.utils.ListItemDecoration;
import cn.poco.banner.BannerCore3;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.beautify4.adapter.MyAdapter;
import cn.poco.beautify4.site.Beautify4PageSite;
import cn.poco.beautify4.view.EdgeGradientView;
import cn.poco.beautify4.view.MyButton1;
import cn.poco.beautify4.view.MyButton2;
import cn.poco.beautify4.view.MyImageViewer;
import cn.poco.beautify4.view.RecommendDialog;
import cn.poco.beautifyEyes.page.BigEyesPage;
import cn.poco.beautifyEyes.page.BrightEyesPage;
import cn.poco.beautifyEyes.site.BigEyesSite;
import cn.poco.beautifyEyes.site.BrightEyesSite;
import cn.poco.beauty.BeautyPage;
import cn.poco.beauty.site.BeautySite;
import cn.poco.brush.BrushPage;
import cn.poco.brush.site.BrushPageSite;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.ImageFile2;
import cn.poco.camera.RotationImg2;
import cn.poco.clip.ClipPage;
import cn.poco.clip.site.ClipPageSite;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.eyeBag.EyeBagPage;
import cn.poco.eyeBag.site.EysBagSite;
import cn.poco.face.FaceDataV2;
import cn.poco.filter4.FilterPage;
import cn.poco.filter4.WatermarkItem;
import cn.poco.filter4.site.Filter4PageSite;
import cn.poco.filterPendant.FilterPendantPage;
import cn.poco.filterPendant.site.FilterPendantPageSite;
import cn.poco.frame.FramePage;
import cn.poco.frame.site.FramePageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.site.HomePageSite;
import cn.poco.image.PocoDetector;
import cn.poco.makeup.MakeupPage;
import cn.poco.makeup.makeup2.MakeupSPage;
import cn.poco.makeup.site.MakeupPageSite;
import cn.poco.makeup.site.MakeupSPageSite;
import cn.poco.mosaic.MosaicPage;
import cn.poco.mosaic.site.MosaicPageSite;
import cn.poco.nose.NosePage;
import cn.poco.nose.site.NoseSite;
import cn.poco.noseAndtooth.ShrinkNosePage;
import cn.poco.noseAndtooth.WhiteTeethPage;
import cn.poco.noseAndtooth.site.ShrinkNoseSite;
import cn.poco.noseAndtooth.site.WhiteTeethPageSite;
import cn.poco.pendant.PendantPage;
import cn.poco.pendant.site.PendantSite;
import cn.poco.pendant.view.GarbageView;
import cn.poco.photoview.PhotosViewPager;
import cn.poco.prompt.PopupMgr;
import cn.poco.resource.BannerRes;
import cn.poco.resource.BannerResMgr2;
import cn.poco.resource.BrushResMgr2;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.resource.ResType;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.rise.RisePage;
import cn.poco.rise.site.RisePageSite;
import cn.poco.slim.SlimPage;
import cn.poco.slim.site.SlimSite;
import cn.poco.smile.SmilePage;
import cn.poco.smile.site.SmileSite;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.FolderMgr;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.UndoRedoDataMgr;
import cn.poco.utils.FileUtil;
import cn.poco.utils.MemoryTipDialog;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.PhotoMark;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.widget.AlertDialogV1;
import cn.poco.widget.SdkOutDatedDialog;
import my.beautyCamera.R;

/**
 * Created by Raining on 2016/11/15.
 * 4.0美化页面<br>
 * 处理完成后才加记录<br>
 */

public class Beautify4Page extends IPage
{
	private boolean mIsUsePreView = true;
	protected Beautify4PageSite mSite;
	protected boolean mUiEnabled;//界面控制总开关
	protected boolean mQuit;//是否退出
	protected boolean mCmdEnabled; //消息开关
	protected UiMode mUiMode;
	protected UIHandler mUiHandler;
	protected HandlerThread mImageThread;
	protected Beautify4Handler mImageHandler;

	protected int mTopBarFrHeight;
	protected int mViewW;
	protected int mViewH;
	protected int mBottomFrHeight;

	protected View mView;
	protected FrameLayout mTopBarFr;
	protected ImageView mBackBtn;
	protected ImageView mSaveBtn;
	protected ImageView mUndoBtn;
	protected ImageView mRedoBtn;

	private FrameLayout mBottomLayout;
	private MyButton2 mClipButton;
	private MyButton2 mFaceButton;
	private MyButton2 mBeautifyButton;
	private FrameLayout mBottomBar;
	private ImageView mSelectedClip;
	private ImageView mSelectedFace;
	private ImageView mSelectedBeautify;
	private View mDivide;

	protected ArrayList<MyAdapter.MyItem> mRecyclerViewData2;
	protected MyAdapter mMyAdapter2;
	protected RecyclerView mRecyclerView2;

	protected ArrayList<MyAdapter.MyItem> mRecyclerViewData3;
	protected MyAdapter mMyAdapter3;
	protected RecyclerView mRecyclerView3;

	private PhotoStore mPhotoStore;
	private String mFolderName;
	protected UiMode mDefOpenPage = UiMode.NORMAL;
	protected int mDefSelUri;
	protected Object mOrgImgInfo;
	protected Object mCurImgInfo;
	protected boolean mOnlyOnePic;
	protected boolean mAddDate;
	protected final ArrayList<PhotoInfo> mAllImages = new ArrayList<>();
	protected int mCurImgIndex = -1;
	protected final static int UNDO_REDO_SIZE = 20;
	public static final HashMap<String, UndoRedoDataMgr> sCacheDatas = new HashMap<>(); //撤销重做数据
	public static final Object CACHE_THREAD_LOCK = new Object();

	protected PopupMgr m_popupView;

	protected FrameLayout mDeleteBgLayout;
	protected GarbageView mDeleteIcon;

	private RecommendDialog mRecommendDialog;

	private AnimatorSet mCloseAnimator;
	private AnimatorSet mOpenAnimator;

	private AnimatorSet mOpenFaceListAnimator;
	private AnimatorSet mCloseFaceListAnimator;

	private AnimatorSet mOpenBeautifyListAnimator;
	private AnimatorSet mCloseBeautifyListAnimator;

	private AnimatorSet mChangeFaceListAnimator;
	private AnimatorSet mChangeBeautifyListAnimator;

	private ImageView mCompareView;
	private Bitmap mTempCompareBmp = null;

	private int mSelected = 0;

	private EdgeGradientView mBgView;

	private boolean mComparing = false;
	private boolean mShowExitDialog = true;//是否弹出退出确认框
	private boolean isModify = false;

	private boolean hasShowSdkOutDateDialog = false;
	private LocalPhotoAdapter mPhotoAdapter;

	private CloudAlbumDialog mExitDialog;

	public static UndoRedoDataMgr GetCache(String path)
	{
		synchronized(CACHE_THREAD_LOCK)
		{
			return sCacheDatas.get(path);
		}
	}

	protected int DEF_IMG_SIZE;
	protected Bitmap mBmp;
	protected Bitmap mBkBmp;
	private SoftReference<Bitmap> mOriginBitmap;

	private HashMap<String, Object> mPageAnimParams;
	public static final String PAGE_ANIM_IMG_H = "imgh";
	public static final String PAGE_ANIM_VIEW_H = "viewh";
	public static final String PAGE_ANIM_VIEW_TOP_MARGIN = "viewTopMargin";
	public static final String PAGE_BACK_ANIM_IMG_H = "back_img_h";
	public static final String PAGE_BACK_ANIM_VIEW_TOP_MARGIN = "back_view_top_margin";

	public boolean mHasWaterMark;//是否有水印
	public int mWaterMarkId = -1;

	private boolean mSaveToShare = false;

	/**
	 * New提示
	 */
	private boolean mShowFaceTip;
	private boolean mShowZenggaoTip;

	private String mChannelValue;

	public Beautify4Page(Context context, BaseSite site)
	{
		super(context, site);

		TongJiUtils.onPageStart(getContext(), R.string.美化);
		MyBeautyStat.onPageStartByRes(R.string.美颜美图_照片预览页_主页面);

		mSite = (Beautify4PageSite)site;

		InitData();
		InitUI();
	}

	protected void InitData()
	{
		ShareData.InitData(getContext());

		mWaterMarkId = WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext());

		// 获取图片的最大边长
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

		mUiMode = UiMode.NORMAL;
		mTopBarFrHeight = ShareData.PxToDpi_xhdpi(90);
		mBottomFrHeight = ShareData.PxToDpi_xhdpi(230);
		mViewW = ShareData.m_screenWidth;
		mViewH = ShareData.m_screenHeight - mTopBarFrHeight - mBottomFrHeight;

		// new tip
		mShowFaceTip = TagMgr.CheckTag(getContext(), Tags.FACE_CLICK_FLAG);
		mShowZenggaoTip = TagMgr.CheckTag(getContext(), Tags.ZENGGAO_CLICK_FLAG);

		mRecyclerViewData2 = new ArrayList<>();
		UiMode[] arr = BtnOrderMgr.ReadList2(getContext());
		for(UiMode mode : arr)
		{
			switch(mode)
			{
				case MEIYAN:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.MEIYAN, R.drawable.beautify4page_meiyan_btn_out, getResources().getString(R.string.beautify4page_meiyan_btn), false));
					break;
				case SHOUSHEN:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.SHOUSHEN, R.drawable.beautify4page_shoushen_btn_out, getResources().getString(R.string.beautify4page_shoushen_btn), false));
					break;
				case QUDOU:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.QUDOU, R.drawable.beautify4page_qudou_btn_out, getResources().getString(R.string.beautify4page_qudou_btn), false));
					break;
				case DAYAN:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.DAYAN, R.drawable.beautify4page_dayan_btn_out, getResources().getString(R.string.beautify4page_dayan_btn), false));
					break;
				case QUYANDAI:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.QUYANDAI, R.drawable.beautify4page_quyandai_btn_out, getResources().getString(R.string.beautify4page_quyandai_btn), false));
					break;
				case LIANGYAN:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.LIANGYAN, R.drawable.beautify4page_liangyan_btn_out, getResources().getString(R.string.beautify4page_liangyan_btn), false));
					break;
				case ZENGGAO:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.ZENGGAO, R.drawable.beautify4page_zenggao_btn_out, getResources().getString(R.string.beautify4page_zenggao_btn), mShowZenggaoTip));
					break;
				case WEIXIAO:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.WEIXIAO, R.drawable.beautify4page_weixiao_btn_out, getResources().getString(R.string.beautify4page_weixiao_btn), false));
					break;
				case MEIYA:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.MEIYA, R.drawable.beautify4page_meiya_btn_out, getResources().getString(R.string.beautify4page_meiya_btn), false));
					break ;
				case SHOUBI:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.SHOUBI, R.drawable.beautify4page_shoubi_btn_out, getResources().getString(R.string.beautify4page_shoubi_btn), false));
					break;
				case GAOBILIANG:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.GAOBILIANG, R.drawable.beautify4page_gaobiliang_btn_out, getResources().getString(R.string.beautify4page_gaobiliang_btn), false));
					break;
				case YIJIANMENGZHUANG:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.YIJIANMENGZHUANG, R.drawable.beautify4page_yijianmengzhuang_btn_out, getResources().getString(R.string.beautify4page_yijianmengzhuang_btn), false));
					break;
				case CAIZHUANG:
					mRecyclerViewData2.add(new MyAdapter.MyItem(UiMode.CAIZHUANG, R.drawable.beautify4page_caizhuang_btn_out, getResources().getString(R.string.beautify4page_caizhuang_btn), false));
					break;
			}
		}

		mRecyclerViewData3 = new ArrayList<>();
		arr = BtnOrderMgr.ReadList3(getContext());
		for(UiMode mode : arr)
		{
			switch(mode)
			{
				case LVJING:
					mRecyclerViewData3.add(new MyAdapter.MyItem(UiMode.LVJING, R.drawable.beautify4page_lvjing_btn_out, getResources().getString(R.string.beautify4page_lvjing_btn), false));
					break;
				case XIANGKUANG:
					mRecyclerViewData3.add(new MyAdapter.MyItem(UiMode.XIANGKUANG, R.drawable.beautify4page_xiangkuang_btn_out, getResources().getString(R.string.beautify4page_xiangkuang_btn), false));
					break;
				case TIETU:
					mRecyclerViewData3.add(new MyAdapter.MyItem(UiMode.TIETU, R.drawable.beautify4page_tietu_btn_out, getResources().getString(R.string.beautify4page_tietu_btn), false));
					break;
				case MAOBOLI:
					mRecyclerViewData3.add(new MyAdapter.MyItem(UiMode.MAOBOLI, R.drawable.beautify4page_maoboli_btn_out, getResources().getString(R.string.beautify4page_maoboli_btn), false));
					break;
				case MASAIKE:
					mRecyclerViewData3.add(new MyAdapter.MyItem(UiMode.MASAIKE, R.drawable.beautify4page_masaike_btn_out, getResources().getString(R.string.beautify4page_masaike_btn), false));
					break;
				case ZHIJIANMOFA:
					mRecyclerViewData3.add(new MyAdapter.MyItem(UiMode.ZHIJIANMOFA, R.drawable.beautify4page_zhijianmofa_btn_out, getResources().getString(R.string.beautify4page_zhijianmofa_btn), false));
					break;
				case PINTU:
					mRecyclerViewData3.add(new MyAdapter.MyItem(UiMode.PINTU, R.drawable.beautify4page_pintu_btn_out, getResources().getString(R.string.beautify4page_pintu_btn), false));
					break;
			}
		}

		mPhotoStore = PhotoStore.getInstance(getContext());

		mUiHandler = new UIHandler();
		mImageThread = new HandlerThread("beautify4_img_thread");
		mImageThread.start();
		mImageHandler = new Beautify4Handler(mImageThread.getLooper(), getContext(), mUiHandler);

		mCmdEnabled = true;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mView instanceof MyImageViewer) {
			mViewH = mView.getHeight();
		}else if (mView instanceof PhotosViewPager) {
			mViewH = mView.getHeight();
		}
	}

	@SuppressWarnings("all")
	protected void InitUI()
	{
		//初始化UI
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		mBgView = new EdgeGradientView(getContext());
		fl = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mBgView, fl);

		mTopBarFr = new FrameLayout(getContext());
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mTopBarFrHeight);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mTopBarFr, fl);
		{
			mBackBtn = new ImageView(getContext());
			mBackBtn.setImageResource(R.drawable.framework_back_btn);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
			mTopBarFr.addView(mBackBtn, fl);
			ImageUtils.AddSkin(getContext(), mBackBtn);
			mBackBtn.setOnTouchListener(mBtnListener);

			mSaveBtn = new ImageView(getContext());
			mSaveBtn.setImageResource(R.drawable.framework_share_btn);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
			fl.rightMargin = ShareData.PxToDpi_xhdpi(5);
			mTopBarFr.addView(mSaveBtn, fl);
			ImageUtils.AddSkin(getContext(), mSaveBtn);
			mSaveBtn.setOnTouchListener(mBtnListener);

			LinearLayout undoRedoFr = new LinearLayout(getContext());
			undoRedoFr.setOrientation(LinearLayout.HORIZONTAL);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			mTopBarFr.addView(undoRedoFr, fl);
			{
				mUndoBtn = new ImageView(getContext());
				mUndoBtn.setScaleType(ImageView.ScaleType.CENTER);
				ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER;
				undoRedoFr.addView(mUndoBtn, ll);
				mUndoBtn.setOnTouchListener(mBtnListener);

				mRedoBtn = new ImageView(getContext());
				mRedoBtn.setScaleType(ImageView.ScaleType.CENTER);
				ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(56);
				undoRedoFr.addView(mRedoBtn, ll);
				mRedoBtn.setOnTouchListener(mBtnListener);
			}
		}
		mTopBarFr.setVisibility(View.GONE);

		initBottomLayout();

		mCompareView = new ImageView(getContext());
		mCompareView.setPadding(0, ShareData.PxToDpi_xhdpi(10), ShareData.PxToDpi_xhdpi(10), 0);
		mCompareView.setImageResource(R.drawable.beautify_compare);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END;
		params.rightMargin = ShareData.PxToDpi_xhdpi(20 - 10);
		params.topMargin = mTopBarFrHeight + ShareData.PxToDpi_xhdpi(48 - 10);
		addView(mCompareView, params);
		mCompareView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mView instanceof MyImageViewer) {
					MyImageViewer imageViewer = (MyImageViewer) mView;
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_对比按钮);
							if (mTempCompareBmp == null && mCurImgInfo != null) {
								mUiEnabled = false;
								mComparing = true;
								String path = GetImgPath(mCurImgInfo);
								if (path != null) {
									synchronized (CACHE_THREAD_LOCK) {
										UndoRedoDataMgr cacheData = sCacheDatas.get(path);
										if (cacheData != null && cacheData.CanUndo()) {
											mTempCompareBmp = imageViewer.getCurBitmap();

											Bitmap bmp;
											if (mOriginBitmap != null && mOriginBitmap.get() != null) {
												bmp = mOriginBitmap.get();
											} else {
												Object img = Utils.Path2ImgObj(GetImgPath(cacheData.getOrigin()));
												bmp = ImageUtils.MakeBmp(getContext(), img, mViewW, mViewH);
												mOriginBitmap = new SoftReference<>(bmp);
											}

											imageViewer.setCurBitmap(bmp);
										}
									}
								}
							}
							break;

						case MotionEvent.ACTION_POINTER_DOWN:
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							if (mTempCompareBmp != null) {
								imageViewer.setCurBitmap(mTempCompareBmp);
								mTempCompareBmp = null;
							}
							mComparing = false;
							mUiEnabled = true;
							break;

						default:
							break;
					}
				}
				if (mView instanceof PhotosViewPager) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_对比按钮);
							if (mTempCompareBmp == null && mCurImgInfo != null) {
								mUiEnabled = false;
								mComparing = true;
								String path = GetImgPath(mCurImgInfo);
								if (path != null) {
									synchronized (CACHE_THREAD_LOCK) {
										UndoRedoDataMgr cacheData = sCacheDatas.get(path);
										if (cacheData != null && cacheData.CanUndo()) {
											mTempCompareBmp = mPhotoAdapter.getCurBitmap();
											Bitmap bmp;
											if (mOriginBitmap != null && mOriginBitmap.get() != null) {
												bmp = mOriginBitmap.get();
											} else {
												Object img = Utils.Path2ImgObj(GetImgPath(cacheData.getOrigin()));
												bmp = ImageUtils.MakeBmp(getContext(), img, mViewW, mViewH);
												mOriginBitmap = new SoftReference<>(bmp);
											}
											mPhotoAdapter.setCurBitmap(bmp);
										}
									}
								}
							}
							break;

						case MotionEvent.ACTION_POINTER_DOWN:
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							if (mTempCompareBmp != null) {
								mPhotoAdapter.setCurBitmap(mTempCompareBmp);
								mTempCompareBmp = null;
							}
							mComparing = false;
							mUiEnabled = true;
							break;

						default:
							break;
					}
				}
				return true;
			}
		});
		mCompareView.setVisibility(INVISIBLE);
	}

	private void initAnimator() {

		mOpenFaceListAnimator = new AnimatorSet();
		ObjectAnimator alpha = ObjectAnimator.ofFloat(mRecyclerView2, "alpha", 0, 1);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(mRecyclerView2, "translationY", ShareData.PxToDpi_hdpi(15), 0);
		ObjectAnimator bgAlpha = ObjectAnimator.ofFloat(mBgView, "bottomAlpha", 1, 0.7f);
		mOpenFaceListAnimator.play(alpha).with(translationY).with(bgAlpha);
		mOpenFaceListAnimator.setDuration(100);
		mOpenFaceListAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mOpenFaceListAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mUiEnabled = true;
			}

			@Override
			public void onAnimationStart(Animator animation) {
				mRecyclerView2.setVisibility(VISIBLE);
			}
		});

		mCloseFaceListAnimator = new AnimatorSet();
		alpha = ObjectAnimator.ofFloat(mRecyclerView2, "alpha", 1, 0);
		translationY = ObjectAnimator.ofFloat(mRecyclerView2, "translationY", 0, ShareData.PxToDpi_hdpi(15));
		bgAlpha = ObjectAnimator.ofFloat(mBgView, "bottomAlpha", 0.7f, 1);
		mCloseFaceListAnimator.play(alpha).with(translationY).with(bgAlpha);
		mCloseFaceListAnimator.setDuration(100);
		mCloseFaceListAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mCloseFaceListAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mRecyclerView2.setVisibility(INVISIBLE);
				mRecyclerView2.scrollToPosition(0);
				mRecyclerView2.setTranslationY(0);
			}
		});

		mOpenBeautifyListAnimator = new AnimatorSet();
		alpha = ObjectAnimator.ofFloat(mRecyclerView3, "alpha", 0, 1);
		translationY = ObjectAnimator.ofFloat(mRecyclerView3, "translationY", ShareData.PxToDpi_hdpi(15), 0);
		bgAlpha = ObjectAnimator.ofFloat(mBgView, "bottomAlpha", 1, 0.7f);
		mOpenBeautifyListAnimator.play(alpha).with(translationY).with(bgAlpha);
		mOpenBeautifyListAnimator.setDuration(100);
		mOpenBeautifyListAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mOpenBeautifyListAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mUiEnabled = true;
			}

			@Override
			public void onAnimationStart(Animator animation) {
				mRecyclerView3.setVisibility(VISIBLE);
			}
		});

		mCloseBeautifyListAnimator = new AnimatorSet();
		alpha = ObjectAnimator.ofFloat(mRecyclerView3, "alpha", 1, 0);
		translationY = ObjectAnimator.ofFloat(mRecyclerView3, "translationY", 0, ShareData.PxToDpi_hdpi(15));
		bgAlpha = ObjectAnimator.ofFloat(mBgView, "bottomAlpha", 0.7f, 1);
		mCloseBeautifyListAnimator.play(alpha).with(translationY).with(bgAlpha);
		mCloseBeautifyListAnimator.setDuration(100);
		mCloseBeautifyListAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mCloseBeautifyListAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mRecyclerView3.setVisibility(INVISIBLE);
				mRecyclerView3.scrollToPosition(0);
				mRecyclerView3.setTranslationY(0);
			}
		});

		mCloseAnimator = new AnimatorSet();
		float scale = ShareData.PxToDpi_xhdpi(50) * 1f / ShareData.PxToDpi_xhdpi(82);

		float y = ShareData.PxToDpi_xhdpi(100) - (ShareData.PxToDpi_xhdpi(82) * (1 - scale) / 2);

		ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(mClipButton, "scaleX", 1, scale);
		ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(mClipButton, "scaleY", 1, scale);
		ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(mFaceButton, "scaleX", 1, scale);
		ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(mFaceButton, "scaleY", 1, scale);
		ObjectAnimator scaleX3 = ObjectAnimator.ofFloat(mBeautifyButton, "scaleX", 1, scale);
		ObjectAnimator scaleY3 = ObjectAnimator.ofFloat(mBeautifyButton, "scaleY", 1, scale);

		ObjectAnimator y1 = ObjectAnimator.ofFloat(mClipButton, "translationY", 0, y);
		ObjectAnimator y2 = ObjectAnimator.ofFloat(mFaceButton, "translationY", 0, y);
		ObjectAnimator y3 = ObjectAnimator.ofFloat(mBeautifyButton, "translationY", 0, y);
		y1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float fraction = animation.getAnimatedFraction();
				if (fraction > 0.9 && mBottomBar.getVisibility() != VISIBLE) {
					mBottomBar.setVisibility(VISIBLE);
					if (mUiMode == UiMode.FACE) {
						mOpenFaceListAnimator.start();
					} else if (mUiMode == UiMode.BEAUTIFY) {
						mOpenBeautifyListAnimator.start();
					}
				}
			}
		});

		mCloseAnimator.play(scaleX1).with(scaleY1)
				.with(scaleX2).with(scaleY2)
				.with(scaleX3).with(scaleY3)
				.with(y1).with(y2).with(y3);
		mCloseAnimator.setDuration(200);
		mCloseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mCloseAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				if (mUiMode == UiMode.FACE) {
					mSelectedFace.setImageResource(R.drawable.beautify_page_face_selected);
				} else if (mUiMode == UiMode.BEAUTIFY){
					mSelectedBeautify.setImageResource(R.drawable.beautify_page_beautify_selected);
				} else {
					mUiEnabled = true;
				}
				mClipButton.setVisibility(INVISIBLE);
				mFaceButton.setVisibility(INVISIBLE);
				mBeautifyButton.setVisibility(INVISIBLE);
				mDivide.setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationStart(Animator animation) {
				mUiEnabled = false;
			}
		});

		mOpenAnimator = new AnimatorSet();

		scaleX1 = ObjectAnimator.ofFloat(mClipButton, "scaleX", scale, 1);
		scaleY1 = ObjectAnimator.ofFloat(mClipButton, "scaleY", scale, 1);
		scaleX2 = ObjectAnimator.ofFloat(mFaceButton, "scaleX", scale, 1);
		scaleY2 = ObjectAnimator.ofFloat(mFaceButton, "scaleY", scale, 1);
		scaleX3 = ObjectAnimator.ofFloat(mBeautifyButton, "scaleX", scale, 1);
		scaleY3 = ObjectAnimator.ofFloat(mBeautifyButton, "scaleY", scale, 1);

		y1 = ObjectAnimator.ofFloat(mClipButton, "translationY", y, 0);
		y2 = ObjectAnimator.ofFloat(mFaceButton, "translationY", y, 0);
		y3 = ObjectAnimator.ofFloat(mBeautifyButton, "translationY", y, 0);
		y1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float fraction = animation.getAnimatedFraction();
				if (fraction > 0.2f && mBottomBar.getVisibility() == VISIBLE) {
					mBottomBar.setVisibility(INVISIBLE);
				}
			}
		});

		mOpenAnimator.play(scaleX1).with(scaleY1)
				.with(scaleX2).with(scaleY2)
				.with(scaleX3).with(scaleY3)
				.with(y1).with(y2).with(y3);
		mOpenAnimator.setDuration(200);
		mOpenAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mOpenAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (mSelected == 1) {
					mSelectedFace.setImageResource(R.drawable.beautify_page_face);
				} else if (mSelected == 2) {
					mSelectedBeautify.setImageResource(R.drawable.beautify_page_beautify);
				}
				mUiMode = UiMode.NORMAL;
				mUiEnabled = true;
			}

			@Override
			public void onAnimationStart(Animator animation) {
				mUiEnabled = false;
				mDivide.setVisibility(INVISIBLE);
				mClipButton.setVisibility(VISIBLE);
				mFaceButton.setVisibility(VISIBLE);
				mBeautifyButton.setVisibility(VISIBLE);
				if (mSelected == 1) {
					mCloseFaceListAnimator.start();
				} else if (mSelected == 2) {
					mCloseBeautifyListAnimator.start();
				}
			}
		});

		mChangeFaceListAnimator = new AnimatorSet();
		ObjectAnimator translationX = ObjectAnimator.ofFloat(mRecyclerView2, "translationX", ShareData.PxToDpi_xhdpi(25), 0);
		alpha = ObjectAnimator.ofFloat(mRecyclerView2, "alpha", 0, 1);
		alpha.setInterpolator(new LinearInterpolator());
		mChangeFaceListAnimator.play(translationX).with(alpha);
		mChangeFaceListAnimator.setDuration(400);
//		mChangeFaceListAnimator.setInterpolator(new LinearInterpolator());
		mChangeFaceListAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mUiEnabled = false;
				mSelectedFace.setImageResource(R.drawable.beautify_page_face_selected);
				mSelectedBeautify.setImageResource(R.drawable.beautify_page_beautify);
				mRecyclerView2.scrollToPosition(0);
				mRecyclerView2.setVisibility(VISIBLE);
				mRecyclerView3.setVisibility(INVISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mUiEnabled = true;
			}
		});

		mChangeBeautifyListAnimator = new AnimatorSet();
		translationX = ObjectAnimator.ofFloat(mRecyclerView3, "translationX", ShareData.PxToDpi_xhdpi(30), 0);
		alpha = ObjectAnimator.ofFloat(mRecyclerView3, "alpha", 0, 1);
		alpha.setInterpolator(new LinearInterpolator());
		mChangeBeautifyListAnimator.play(translationX).with(alpha);
		mChangeBeautifyListAnimator.setDuration(400);
//		mChangeBeautifyListAnimator.setInterpolator(new LinearInterpolator());
		mChangeBeautifyListAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mUiEnabled = false;
				mSelectedFace.setImageResource(R.drawable.beautify_page_face);
				mSelectedBeautify.setImageResource(R.drawable.beautify_page_beautify_selected);
				mRecyclerView2.setVisibility(INVISIBLE);
				mRecyclerView3.scrollToPosition(0);
				mRecyclerView3.setVisibility(VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mUiEnabled = true;
			}
		});
	}

	protected boolean mIsShare = true;

	protected void UpdateShareBtnStyle()
	{
		boolean isShare = mIsShare;
		if(mCurImgInfo != null)
		{
			String path = GetImgPath(mCurImgInfo);
			if(path != null)
			{
				UndoRedoDataMgr mgr = sCacheDatas.get(path);
				if(mgr != null && mgr.GetCurrentIndex() > 0)
				{
					isShare = false;
				}
				else
				{
					if(FolderMgr.getInstance().IsCachePath(path))
					{
						isShare = false;
					}
					else
					{
						isShare = true;
					}
				}
			}
		}
		if(isShare != mIsShare)
		{
			mIsShare = isShare;
			if(mIsShare)
			{
				mSaveBtn.setImageResource(R.drawable.framework_share_btn);
			}
			else
			{
				mSaveBtn.setImageResource(R.drawable.framework_save_btn);
			}
		}
	}

	protected void initBottomLayout() {
		if (mBottomLayout == null) {
			LayoutParams params;

			mBottomLayout = new FrameLayout(getContext());
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mBottomFrHeight);
			params.gravity = Gravity.BOTTOM;
			addView(mBottomLayout, params);
			{
				mClipButton = new MyButton2(getContext());
				mClipButton.image.setImageResource(R.drawable.beautify_page_clip_normal);
				mClipButton.text.setText(R.string.beautify4page_clip_btn);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.leftMargin = ShareData.PxToDpi_xhdpi(66);
				params.topMargin = ShareData.PxToDpi_xhdpi(63);
				mBottomLayout.addView(mClipButton, params);
				mClipButton.setOnTouchListener(mBtnListener);

				mFaceButton = new MyButton2(getContext());
				mFaceButton.image.setImageResource(R.drawable.beautify_page_face_normal);
				mFaceButton.text.setText(R.string.beautify4page_face_btn);
				mFaceButton.setNew(mShowFaceTip);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER_HORIZONTAL;
				params.topMargin = ShareData.PxToDpi_xhdpi(63);
				mBottomLayout.addView(mFaceButton, params);
				mFaceButton.setOnTouchListener(mBtnListener);

				mBeautifyButton = new MyButton2(getContext());
				mBeautifyButton.image.setImageResource(R.drawable.beautify_page_beautify_normal);
				mBeautifyButton.text.setText(R.string.beautify4page_beautify_btn);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.END;
				params.rightMargin = ShareData.PxToDpi_xhdpi(66);
				params.topMargin = ShareData.PxToDpi_xhdpi(63);
				mBottomLayout.addView(mBeautifyButton, params);
				mBeautifyButton.setOnTouchListener(mBtnListener);

				mDivide = new View(getContext());
				mDivide.setBackgroundColor(Color.BLACK);
				mDivide.setAlpha(0.06f);
				params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
				params.gravity = Gravity.BOTTOM;
				params.bottomMargin = ShareData.PxToDpi_xhdpi(79);
				mBottomLayout.addView(mDivide, params);
				mDivide.setVisibility(INVISIBLE);

				mBottomBar = new FrameLayout(getContext());
				mBottomBar.setBackgroundColor(Color.WHITE);
				params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(79));
				params.gravity = Gravity.BOTTOM;
				mBottomLayout.addView(mBottomBar, params);
				{
					mSelectedClip = new ImageView(getContext());
					mSelectedClip.setImageResource(R.drawable.beautify_page_clip);
					mSelectedClip.setScaleType(ImageView.ScaleType.CENTER);
					mSelectedClip.setPadding(ShareData.PxToDpi_xhdpi(40), 0, ShareData.PxToDpi_xhdpi(40), 0);
					ImageUtils.AddSkin(getContext(), mSelectedClip);
					params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
					params.gravity = Gravity.CENTER_VERTICAL;
					params.leftMargin = ShareData.PxToDpi_xhdpi(62);
					mBottomBar.addView(mSelectedClip, params);
					mSelectedClip.setOnTouchListener(mBtnListener);

					mSelectedFace = new ImageView(getContext());
					mSelectedFace.setImageResource(R.drawable.beautify_page_face);
					mSelectedFace.setScaleType(ImageView.ScaleType.CENTER);
					mSelectedFace.setPadding(ShareData.PxToDpi_xhdpi(40), 0, ShareData.PxToDpi_xhdpi(40), 0);
					ImageUtils.AddSkin(getContext(), mSelectedFace);
					params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
					params.gravity = Gravity.CENTER;
					mBottomBar.addView(mSelectedFace, params);
					mSelectedFace.setOnTouchListener(mBtnListener);

					mSelectedBeautify = new ImageView(getContext());
					mSelectedBeautify.setImageResource(R.drawable.beautify_page_beautify);
					mSelectedBeautify.setScaleType(ImageView.ScaleType.CENTER);
					mSelectedBeautify.setPadding(ShareData.PxToDpi_xhdpi(40), 0, ShareData.PxToDpi_xhdpi(40), 0);
					ImageUtils.AddSkin(getContext(), mSelectedBeautify);
					params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
					params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
					params.rightMargin = ShareData.PxToDpi_xhdpi(63);
					mBottomBar.addView(mSelectedBeautify, params);
					mSelectedBeautify.setOnTouchListener(mBtnListener);
				}
				mBottomBar.setVisibility(INVISIBLE);
			}
			initFaceListLayout();
			initBeautifyListLayout();

			initAnimator();
		}
	}

	protected void initFaceListLayout() {
		if (mMyAdapter2 == null) {
			mMyAdapter2 = new MyAdapter();
			mMyAdapter2.setData(mRecyclerViewData2);
			mMyAdapter2.setCallback(new MyAdapter.MyListener()
			{
				@Override
				public void onClick(int res)
				{
					if (!mUiEnabled)
						return;

					switch(res)
					{
						case R.drawable.beautify4page_meiyan_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_美颜);
								SetUIMode(mUiMode, UiMode.MEIYAN, true);
								mUiMode = UiMode.MEIYAN;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_shoushen_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_瘦脸瘦身);
								SetUIMode(mUiMode, UiMode.SHOUSHEN, true);
								mUiMode = UiMode.SHOUSHEN;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_qudou_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_祛痘);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_祛痘);
								SetUIMode(mUiMode, UiMode.QUDOU, true);
								mUiMode = UiMode.QUDOU;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_dayan_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_大眼);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_大眼);
								SetUIMode(mUiMode, UiMode.DAYAN, true);
								mUiMode = UiMode.DAYAN;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_quyandai_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_祛眼袋);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_祛眼袋);
								SetUIMode(mUiMode, UiMode.QUYANDAI, true);
								mUiMode = UiMode.QUYANDAI;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_liangyan_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_亮眼);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_亮眼);
								SetUIMode(mUiMode, UiMode.LIANGYAN, true);
								mUiMode = UiMode.LIANGYAN;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_zenggao_btn_out:
							if (ImgIsOk4UI()) {
								mUiEnabled = false;
								if (mShowZenggaoTip)
								{
									TagMgr.SetTag(getContext(), Tags.ZENGGAO_CLICK_FLAG);
									int index = MyAdapter.getIndex(mRecyclerViewData2, res);
									if(index >= 0)
									{
										mRecyclerViewData2.get(index).mShowNew = false;
										mMyAdapter2.notifyDataSetChanged();
									}

									mShowZenggaoTip = false;
								}
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_增高);
								SetUIMode(mUiMode, UiMode.ZENGGAO, true);
								mUiMode = UiMode.ZENGGAO;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_weixiao_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_微笑);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_微笑);
								SetUIMode(mUiMode, UiMode.WEIXIAO, true);
								mUiMode = UiMode.WEIXIAO;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_meiya_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_美牙);
								SetUIMode(mUiMode, UiMode.MEIYA, true);
								mUiMode = UiMode.MEIYA;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_shoubi_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦鼻);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_瘦鼻);
								SetUIMode(mUiMode, UiMode.SHOUBI, true);
								mUiMode = UiMode.SHOUBI;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_gaobiliang_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_高鼻梁);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_高鼻梁);
								SetUIMode(mUiMode, UiMode.GAOBILIANG, true);
								mUiMode = UiMode.GAOBILIANG;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_yijianmengzhuang_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.一键萌妆);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_一键萌妆);
								SetUIMode(mUiMode, UiMode.YIJIANMENGZHUANG, true);
								mUiMode = UiMode.YIJIANMENGZHUANG;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_caizhuang_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_彩妆);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_美颜美形菜单_彩妆);
								SetUIMode(mUiMode, UiMode.CAIZHUANG, true);
								mUiMode = UiMode.CAIZHUANG;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;
					}
				}

				@Override
				public void onLongClick(MyButton1 btn)
				{

				}

				@Override
				public boolean isLongPressDragEnabled()
				{
					return mUiEnabled;
				}

				@Override
				public void onMove()
				{
					BtnOrderMgr.SaveOrder(getContext(), true, mRecyclerViewData2);
				}

				@Override
				public void onSwiped()
				{
					BtnOrderMgr.SaveOrder(getContext(), true, mRecyclerViewData2);
				}
			});
			mRecyclerView2 = new RecyclerView(getContext());
			mRecyclerView2.setOverScrollMode(OVER_SCROLL_NEVER);
			mRecyclerView2.setPadding(ShareData.PxToDpi_xhdpi(24), 0, ShareData.PxToDpi_xhdpi(24), 0);
			mRecyclerView2.setClipToPadding(false);
			mRecyclerView2.addItemDecoration(new ListItemDecoration(ShareData.PxToDpi_xhdpi(22), ListItemDecoration.HORIZONTAL));
			LinearLayoutManager llm = new LinearLayoutManager(getContext());
			llm.setOrientation(LinearLayout.HORIZONTAL);
			mRecyclerView2.setLayoutManager(llm);
			mRecyclerView2.setAdapter(mMyAdapter2);
			mRecyclerView2.setItemAnimator(new DefaultItemAnimator());
			LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = ShareData.PxToDpi_xhdpi(28);
			mBottomLayout.addView(mRecyclerView2, params);
//			new MyAdapter.DefaultItemTouchHelper(mMyAdapter2).attachToRecyclerView(mRecyclerView2);
			mRecyclerView2.setVisibility(INVISIBLE);
		}
	}

	protected void initBeautifyListLayout() {
		if (mMyAdapter3 == null) {
			mMyAdapter3 = new MyAdapter();
			mMyAdapter3.setData(mRecyclerViewData3);
			mMyAdapter3.setCallback(new MyAdapter.MyListener()
			{
				@Override
				public void onClick(int res)
				{
					if (!mUiEnabled)
						return;

					switch(res)
					{
						case R.drawable.beautify4page_lvjing_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								if(FilterResMgr2.getInstance().m_hasNewRes)
								{
									FilterResMgr2.getInstance().ClearOldId(getContext());
									int index = MyAdapter.getIndex(mRecyclerViewData3, res);
									if(index >= 0)
									{
										mRecyclerViewData3.get(index).mShowNew = false;
										mMyAdapter3.notifyDataSetChanged();
									}
								}
								TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_滤镜);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_素材美化菜单_滤镜);
								SetUIMode(mUiMode, UiMode.LVJING, true);
								mUiMode = UiMode.LVJING;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_xiangkuang_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								if(FrameResMgr2.getInstance().m_hasNewRes || FrameExResMgr2.getInstance().m_hasNewRes)
								{
									FrameResMgr2.getInstance().ClearOldId(getContext());
									FrameExResMgr2.getInstance().ClearOldId(getContext());
									int index = MyAdapter.getIndex(mRecyclerViewData3, res);
									if(index >= 0)
									{
										mRecyclerViewData3.get(index).mShowNew = false;
										mMyAdapter3.notifyDataSetChanged();
									}
								}

								TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_相框);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_素材美化菜单_相框);
								SetUIMode(mUiMode, UiMode.XIANGKUANG, true);
								mUiMode = UiMode.XIANGKUANG;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_tietu_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								if(DecorateResMgr2.getInstance().m_hasNewRes)
								{
									DecorateResMgr2.getInstance().ClearOldId(getContext());
									int index = MyAdapter.getIndex(mRecyclerViewData3, res);
									if(index >= 0)
									{
										mRecyclerViewData3.get(index).mShowNew = false;
										mMyAdapter3.notifyDataSetChanged();
									}
								}

								TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_贴图);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_素材美化菜单_贴图);
								SetUIMode(mUiMode, UiMode.TIETU, true);
								mUiMode = UiMode.TIETU;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_maoboli_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								if(GlassResMgr2.getInstance().m_hasNewRes)
								{
									GlassResMgr2.getInstance().ClearOldId(getContext());
									int index = MyAdapter.getIndex(mRecyclerViewData3, res);
									if(index >= 0)
									{
										mRecyclerViewData3.get(index).mShowNew = false;
										mMyAdapter3.notifyDataSetChanged();
									}
								}

								TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_毛玻璃);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_素材美化菜单_毛玻璃);
								SetUIMode(mUiMode, UiMode.MAOBOLI, true);
								mUiMode = UiMode.MAOBOLI;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_masaike_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								if(MosaicResMgr2.getInstance().m_hasNewRes)
								{
									MosaicResMgr2.getInstance().ClearOldId(getContext());
									int index = MyAdapter.getIndex(mRecyclerViewData3, res);
									if(index >= 0)
									{
										mRecyclerViewData3.get(index).mShowNew = false;
										mMyAdapter3.notifyDataSetChanged();
									}
								}

								TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_马赛克);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_素材美化菜单_马赛克);
								SetUIMode(mUiMode, UiMode.MASAIKE, true);
								mUiMode = UiMode.MASAIKE;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;

						case R.drawable.beautify4page_zhijianmofa_btn_out:
							if(ImgIsOk4UI())
							{
								mUiEnabled = false;
								if(BrushResMgr2.getInstance().m_hasNewRes)
								{
									BrushResMgr2.getInstance().ClearOldId(getContext());
									int index = MyAdapter.getIndex(mRecyclerViewData3, res);
									if(index >= 0)
									{
										mRecyclerViewData3.get(index).mShowNew = false;
										mMyAdapter3.notifyDataSetChanged();
									}
								}

								TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_指尖魔法);
								MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_素材美化菜单_指尖魔法);
								SetUIMode(mUiMode, UiMode.ZHIJIANMOFA, true);
								mUiMode = UiMode.ZHIJIANMOFA;
								SetModeData(mBmp, null);
								mBmp = null;
							}
							break;
						case R.drawable.beautify4page_pintu_btn_out:

							TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_拼图);
							MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_素材美化菜单_拼图);
							int state = JaneEntry.getAppState(getContext());
							String tip = TagMgr.GetTagValue(getContext(), Tags.PUZZLES_JANE_DLG_NEED_SHOW, "");
							boolean show = TextUtils.isEmpty(tip);
							if (state == JaneEntry.SUCCESS && !show) {
								JaneEntry.gotoJane(getContext(), GetImgPath(mCurImgInfo), false);
							} else {
								if (mRecommendDialog != null) {
									if (mRecommendDialog.isShow()) {
										mRecommendDialog.dismiss();
									}
									mRecommendDialog = null;
								}

								int text = R.string.goto_download;
								boolean showTip = false;
								if (state == JaneEntry.SUCCESS) {
									text = R.string.goto_use;
									showTip = true;
								}

								mRecommendDialog = new RecommendDialog(getContext())
										.setPositive(text, new OnClickListener() {
											@Override
											public void onClick(View v) {
												JaneEntry.gotoJane(getContext(), GetImgPath(mCurImgInfo), true);
												if (mRecommendDialog != null) {
													mRecommendDialog.dismiss();
													mRecommendDialog = null;
												}
											}
										})
										.setNegative(R.string.cancel, new OnClickListener() {
											@Override
											public void onClick(View v) {
												TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_拼图_返回);
												MyBeautyStat.onClickByRes(R.string.美颜美图_拼图页面_主页面_返回);
												if (mRecommendDialog != null) {
													mRecommendDialog.dismiss();
													mRecommendDialog = null;
												}
											}
										})
										.setOnCheckChangeListener(new RecommendDialog.OnCheckChangeListener() {
											@Override
											public void onCheck(View v, boolean check) {
												TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_拼图_不再提示);
												MyBeautyStat.onClickByRes(R.string.美颜美图_拼图页面_主页面_不再提示);
												if (check) {
													TagMgr.SetTagValue(getContext(), Tags.PUZZLES_JANE_DLG_NEED_SHOW, "true");
												} else {
													TagMgr.SetTagValue(getContext(), Tags.PUZZLES_JANE_DLG_NEED_SHOW, "");
												}
											}
										})
										.showTip(showTip)
										.setDialogBackgroungListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化_拼图_返回);
												MyBeautyStat.onClickByRes(R.string.美颜美图_拼图页面_主页面_返回);
												if (mRecommendDialog != null) {
													mRecommendDialog.dismiss();
													mRecommendDialog = null;
												}
											}
										});

								mRecommendDialog.show(Beautify4Page.this);
							}
							break;
					}
				}

				@Override
				public void onLongClick(MyButton1 btn)
				{

				}

				@Override
				public boolean isLongPressDragEnabled()
				{
					return mUiEnabled;
				}

				@Override
				public void onMove()
				{
					BtnOrderMgr.SaveOrder(getContext(), false, mRecyclerViewData3);
				}

				@Override
				public void onSwiped()
				{
					BtnOrderMgr.SaveOrder(getContext(), false, mRecyclerViewData3);
				}
			});

			mRecyclerView3 = new RecyclerView(getContext());
			mRecyclerView3.setOverScrollMode(OVER_SCROLL_NEVER);
			mRecyclerView3.setPadding(ShareData.PxToDpi_xhdpi(24), 0, ShareData.PxToDpi_xhdpi(24), 0);
			mRecyclerView3.setClipToPadding(false);
			mRecyclerView3.addItemDecoration(new ListItemDecoration(ShareData.PxToDpi_xhdpi(22), ListItemDecoration.HORIZONTAL));
			LinearLayoutManager llm = new LinearLayoutManager(getContext());
			llm.setOrientation(LinearLayout.HORIZONTAL);
			mRecyclerView3.setLayoutManager(llm);
			mRecyclerView3.setAdapter(mMyAdapter3);
			mRecyclerView3.setItemAnimator(new DefaultItemAnimator());
			LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.topMargin = ShareData.PxToDpi_xhdpi(28);
			mBottomLayout.addView(mRecyclerView3, params);
//			new MyAdapter.DefaultItemTouchHelper(mMyAdapter3).attachToRecyclerView(mRecyclerView3);
			mRecyclerView3.setVisibility(INVISIBLE);
		}
	}

	protected boolean checkIsReLoad(final int index)
	{
		boolean isReadLoad = false;
		int mapIndex = mPhotoStore.mapFromCacheIndex(index);
		if (mPhotoStore.shouldReloadData(mFolderName, mapIndex))
		{
			String currentPath = mAllImages.get(index).getImagePath();

			mAllImages.clear();
			List<PhotoInfo> temp = mPhotoStore.getPhotoInfos(mFolderName, mapIndex);
			mAllImages.addAll(temp);
			mCurImgIndex = 0;

			int size = mAllImages.size();
			PhotoInfo photoInfo;
			for (int i = size / 2, j = i + 1; i >= 0 || j < size; i--, j++)
			{
				if (i >= 0)
				{
					photoInfo = mAllImages.get(i);
					if (photoInfo.getImagePath().equals(currentPath))
					{
						mCurImgIndex = i;
						break;
					}
				}

				if (j < size)
				{
					photoInfo = mAllImages.get(j);
					if (photoInfo.getImagePath().equals(currentPath))
					{
						mCurImgIndex = j;
						break;
					}
				}
			}
			isReadLoad = true;
		} else
		{
			mCurImgIndex = mPhotoStore.mapToCacheIndex(mapIndex);
		}
		return isReadLoad;
	}

	protected void InitMainView()
	{
		if(mView == null)
		{
			if(mIsUsePreView)
			{
				if(!mOnlyOnePic)
				{
					checkIsReLoad(mCurImgIndex);
				}
				PhotosViewPager viewPager = new PhotosViewPager(getContext());
				mView = viewPager;
				mPhotoAdapter = new LocalPhotoAdapter(viewPager, mViewW + 2, mViewH);
				mPhotoAdapter.setData(mAllImages);
				viewPager.setAdapter(mPhotoAdapter);
				mPhotoAdapter.setSwitchListener(new LocalPhotoAdapter.SwitchListener()
				{
					@Override
					public void onSwitch(Object data, int index)
					{

					}

					@Override
					public void onTotalSwitch(Object data, int index)
					{
						PhotoInfo img = (PhotoInfo) data;
						if (!mOnlyOnePic)
						{
							mBmp = null;
							mOriginBitmap = null;
							mCurImgInfo = img;
							if (index != mCurImgIndex)
							{
								// 清除人脸检测数据
								FaceDataV2.ResetData();
							}

							UpdateUndoRedoBtnState();
							UpdateShareBtnStyle();
							if (checkIsReLoad(index))
							{
								mPhotoAdapter.updateImages(mAllImages, mCurImgIndex);

							}

						} else
						{
							UpdateUndoRedoBtnState();
							UpdateShareBtnStyle();
						}
					}
				});
			}else
			{
				mView = new MyImageViewer(getContext());
				((MyImageViewer)mView).setSwitchListener(new MyImageViewer.OnSwitchListener()
				{
					@Override
					public void onSwitch(PhotoInfo img, int index)
					{
						if(!mOnlyOnePic)
						{
							mBmp = null;
							mOriginBitmap = null;

							mCurImgInfo = img;

//							final int originIndex = index;

							if(index != mCurImgIndex)
							{
								// 清除人脸检测数据
								FaceDataV2.ResetData();
							}

//							index = mPhotoStore.mapFromCacheIndex(index);

							UpdateUndoRedoBtnState();
							UpdateShareBtnStyle();

							if(checkIsReLoad(index))
							{
								((MyImageViewer) mView).updateImages(mAllImages, mCurImgIndex);
							}
						}
						else
						{
							UpdateUndoRedoBtnState();
							UpdateShareBtnStyle();
						}
					}
				});
				((MyImageViewer)mView).setImages(mAllImages);
			}
			FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(mViewW + 2, ViewGroup.LayoutParams.MATCH_PARENT);
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
			fl.topMargin = mTopBarFrHeight;
			fl.bottomMargin = mBottomFrHeight;
			this.addView(mView, 1, fl);

			doPageBackAnim();
		}
	}

	@Override
	public void onResume() {
		if (mView instanceof MyImageViewer && !mOnlyOnePic) {
			// 缓存
			mPhotoStore.getPhotoInfos(mFolderName, mCurImgIndex);
			((MyImageViewer)mView).resetCache();
		}
		if (mView instanceof PhotosViewPager && !mOnlyOnePic) {
			if(checkIsReLoad(mCurImgIndex))
			{
				// 当前显示的图片是否被删除
				String curPath = mAllImages.get(mCurImgIndex).getImagePath();
				if (curPath != null && new File(curPath).exists())
				{
					mCurImgIndex = mPhotoStore.getPhotoInfoIndex(mFolderName, curPath);
				} else
				{
					mCurImgIndex = 0;
				}
				List<PhotoInfo> temp = mPhotoStore.getPhotoInfos(mFolderName, mCurImgIndex);
				mCurImgIndex = mPhotoStore.mapToCacheIndex(mCurImgIndex);
				mAllImages.clear();
				mAllImages.addAll(temp);
				mPhotoAdapter.upDataInfo(mAllImages, true, mCurImgIndex);
			}
		}

		switch (mUiMode) {
			case MEIYAN:
			case SHOUSHEN:
			case QUDOU:
			case DAYAN:
			case QUYANDAI:
			case LIANGYAN:
			case ZENGGAO:
			case WEIXIAO:
			case MEIYA:
			case SHOUBI:
			case GAOBILIANG:
			case YIJIANMENGZHUANG:
			case CAIZHUANG:
			case LVJING:
			case XIANGKUANG:
			case TIETU:
			case MAOBOLI:
			case MASAIKE:
			case ZHIJIANMOFA:
			case PINTU:
				if (mView instanceof IPage) {
					((IPage)mView).onResume();
				}
				break;
			default:
				TongJiUtils.onPageResume(getContext(), R.string.美化);
				break;
		}
	}

	@Override
	public void onPause() {
		switch (mUiMode) {
			case MEIYAN:
			case SHOUSHEN:
			case QUDOU:
			case DAYAN:
			case QUYANDAI:
			case LIANGYAN:
			case ZENGGAO:
			case WEIXIAO:
			case MEIYA:
			case SHOUBI:
			case GAOBILIANG:
			case YIJIANMENGZHUANG:
			case CAIZHUANG:
			case LVJING:
			case XIANGKUANG:
			case TIETU:
			case MAOBOLI:
			case MASAIKE:
			case ZHIJIANMOFA:
			case PINTU:
				if (mView instanceof IPage) {
					((IPage)mView).onPause();
				}
				break;
			default:
				TongJiUtils.onPagePause(getContext(), R.string.美化);
				break;
		}
	}

	protected void SetTopBarState(boolean isShow, boolean hasAnimation)
	{
		mTopBarFr.clearAnimation();

		int start;
		int end;
		if(isShow)
		{
			mTopBarFr.setVisibility(View.VISIBLE);
			start = -1;
			end = 0;
		}
		else
		{
			mTopBarFr.setVisibility(View.GONE);
			start = 0;
			end = -1;
		}

		if(hasAnimation)
		{
			AnimationSet as;
			TranslateAnimation ta;
			as = new AnimationSet(true);
			ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end);
			ta.setDuration(360);
			as.addAnimation(ta);
			mTopBarFr.startAnimation(as);

			if (isShow) {
				String path = GetImgPath(mCurImgInfo);
				if (path != null) {
					synchronized (CACHE_THREAD_LOCK) {
						UndoRedoDataMgr cacheData = sCacheDatas.get(path);
						if (cacheData != null && cacheData.CanUndo()) {
							setScaleAnim(mCompareView, false, 350);
						}
					}
				}
			} else {
				setScaleAnim(mCompareView, true, 350);
			}
		}
	}

	/**
	 * @param type 0：左右/1：上下
	 */
	private void SetBottomFr0State(int type, boolean isShow, boolean hasAnimation)
	{
		//效率不行,全部无动画
//		if(type == 1)
//		{
//			hasAnimation = false;
//		}

		if(mBottomLayout != null)
		{
			mBottomLayout.clearAnimation();

			int start;
			int end;
			if(isShow)
			{
				mBottomLayout.setVisibility(View.VISIBLE);
				switch(type)
				{
					case 0:
						start = -1;
						end = 0;
						break;

					default:
						start = 1;
						end = 0;
						break;
				}
			}
			else
			{
				mBottomLayout.setVisibility(View.GONE);
				switch(type)
				{
					case 0:
						start = 0;
						end = -1;
						break;

					default:
						start = 0;
						end = 1;
						break;
				}
			}

			if(hasAnimation)
			{
				AnimationSet as;
				TranslateAnimation ta;
				as = new AnimationSet(true);
				switch(type)
				{
					case 0:
						ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
						break;

					default:
						ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end);
						break;
				}
				ta.setDuration(360);
				as.addAnimation(ta);
				as.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						mUiEnabled = true;
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				mBottomLayout.startAnimation(as);
			}
		}
	}

	protected void onWaterMarkParams(HashMap<String, Object> params)
    {
        if(params != null)
        {
            Object obj = params.get("has_water_mark");
            if(obj != null && obj instanceof Boolean)
            {
                mHasWaterMark = (Boolean) obj;
            }

            obj = params.get("water_mark_id");
            if(obj != null && obj instanceof Integer)
            {
                mWaterMarkId = (Integer) obj;
            }
        }
    }

	private void onChannelParams(HashMap<String, Object> params) {

		if (params != null) {
			Object obj = params.get("business_channel_value");
			if (obj instanceof String) {
				mChannelValue = (String)obj;
			} else {
				mChannelValue = null;
			}
		}
	}

	protected void SaveCache(HashMap<String, Object> params)
	{
		isModify = true;
		mUiEnabled = false;
		Object o = params.get("img");
		if (o instanceof Bitmap)
		{
			mBmp = (Bitmap) params.get("img");
		}
		else if (o instanceof String)
		{
			RotationImg2 img = Utils.Path2ImgObj((String) o);
			mBmp = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), img.m_img, img.m_degree, -1, img.m_flip, -1, -1);
		}

		Beautify4Handler.CmdMsg cmd = new Beautify4Handler.CmdMsg();
		cmd.m_thumb = mBmp;
		cmd.m_info = mCurImgInfo;

		Message msg = mImageHandler.obtainMessage();
		msg.obj = cmd;
		msg.what = Beautify4Handler.MSG_CACHE;
		mImageHandler.sendMessage(msg);
	}

	private void onPageBackAnim(HashMap<String, Object> params) {
		if (params != null && mPageAnimParams != null) {
			mPageAnimParams.put(PAGE_BACK_ANIM_IMG_H, params.get(PAGE_BACK_ANIM_IMG_H));
			mPageAnimParams.put(PAGE_BACK_ANIM_VIEW_TOP_MARGIN, params.get(PAGE_BACK_ANIM_VIEW_TOP_MARGIN));
		}
	}

	protected ClipPageSite mClipPageSite = new ClipPageSite()
	{
		@Override
		public void OnBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.NORMAL, true);
			mUiMode = UiMode.NORMAL;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.NORMAL, true);
				mUiMode = UiMode.NORMAL;
				SetModeData(mBmp, null);
			}

			//清除人脸数据
			FaceDataV2.ResetData();
		}
	};

	protected String mLancomeResId;
	protected BeautySite mBeautySite = new BeautySite()
	{
		@Override
		public void OnBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			if(params != null && params.containsKey("show_business_banner")){
                mLancomeResId = (String) params.get("show_business_banner");
			}

			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected SlimSite mSlimSite = new SlimSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected AcneSite mAcneSite = new AcneSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}

			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected EysBagSite mEyeBagSite = new EysBagSite() {

		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params) {
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected BrightEyesSite mBrightEyesSite = new BrightEyesSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected BigEyesSite mBigEyesSite = new BigEyesSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected RisePageSite mRisePageSite = new RisePageSite() {
		@Override
		public void onBack(Context context, HashMap<String, Object> params) {
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params) {
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected SmileSite mSmileSite = new SmileSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected WhiteTeethPageSite mWhiteTeethSite = new WhiteTeethPageSite() {

		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected ShrinkNoseSite mShrinkNoseSite = new ShrinkNoseSite() {

		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected NoseSite mNoseSite = new NoseSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected MakeupSPageSite mMakeupSPageSite = new MakeupSPageSite() {

		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);
			onWaterMarkParams(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	private boolean m_isYSLChoose = false;
	protected MakeupPageSite mMakeupPageSite = new MakeupPageSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.FACE, true);
			mUiMode = UiMode.FACE;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);
			if(params.get("yslchoose") != null)
			{
				m_isYSLChoose = (boolean) params.get("yslchoose");
			}

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.FACE, true);
				mUiMode = UiMode.FACE;
				SetModeData(mBmp, null);
			}
		}
	};

	protected Filter4PageSite mFilter4PageSite = new Filter4PageSite()
	{
		@Override
		public void OnBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
			mUiMode = UiMode.BEAUTIFY;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);
			onWaterMarkParams(params);
			onChannelParams(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
				mUiMode = UiMode.BEAUTIFY;
				SetModeData(mBmp, null);
			}
		}
	};

	protected FramePageSite mFramePageSite = new FramePageSite()
	{
		@Override
		public void OnBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
			mUiMode = UiMode.BEAUTIFY;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
				mUiMode = UiMode.BEAUTIFY;
				SetModeData(mBmp, null);
			}

			//清除人脸数据
			FaceDataV2.ResetData();
		}
	};

	protected PendantSite mPendantSite = new PendantSite()
	{
		@Override
		public void onBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
			mUiMode = UiMode.BEAUTIFY;
			SetModeData(mBmp, null);
		}

		@Override
		public void onSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
				mUiMode = UiMode.BEAUTIFY;
				SetModeData(mBmp, null);
			}
		}
	};

	protected FilterPendantPageSite mFilterPendantPageSite = new FilterPendantPageSite()
	{
		@Override
		public void OnBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
			mUiMode = UiMode.BEAUTIFY;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
				mUiMode = UiMode.BEAUTIFY;
				SetModeData(mBmp, null);
			}
		}
	};

	protected MosaicPageSite mMosaicPageSite = new MosaicPageSite()
	{
		@Override
		public void OnBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
			mUiMode = UiMode.BEAUTIFY;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
				mUiMode = UiMode.BEAUTIFY;
				SetModeData(mBmp, null);
			}
		}
	};

	protected BrushPageSite mBrushPageSite = new BrushPageSite()
	{
		@Override
		public void OnBack(Context context, HashMap<String, Object> params)
		{
			if (mSaveToShare) {
				mSite.OnBack(context);
				return;
			}

			if(params != null)
			{
				mBmp = (Bitmap)params.get("img");
				onPageBackAnim(params);
			}
			SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
			mUiMode = UiMode.BEAUTIFY;
			SetModeData(mBmp, null);
		}

		@Override
		public void OnSave(Context context, HashMap<String, Object> params)
		{
			onPageBackAnim(params);
			SaveCache(params);

			if (!mSaveToShare) {
				SetUIMode(mUiMode, UiMode.BEAUTIFY, true);
				mUiMode = UiMode.BEAUTIFY;
				SetModeData(mBmp, null);
			}
		}
	};

	protected View MakeModuleView(UiMode mode)
	{
		View out = null;

		switch(mode)
		{
			case CLIP:
				out = new ClipPage(getContext(), mClipPageSite);
				break;

			case MEIYAN:
				out = new BeautyPage(getContext(), mBeautySite);
				break;

			case SHOUSHEN:
				out = new SlimPage(getContext(), mSlimSite);
				break;

			case QUDOU:
				out = new AcnePage(getContext(), mAcneSite);
				break;

			case DAYAN:
				out = new BigEyesPage(getContext(), mBigEyesSite);
				break;

			case QUYANDAI:
				out = new EyeBagPage(getContext(), mEyeBagSite);
				break;

			case LIANGYAN:
				out = new BrightEyesPage(getContext(), mBrightEyesSite);
				break;

			case ZENGGAO:
				out = new RisePage(getContext(), mRisePageSite);
				break;

			case WEIXIAO:
				out = new SmilePage(getContext(), mSmileSite);
				break;

			case MEIYA:
				out = new WhiteTeethPage(getContext(), mWhiteTeethSite);
				break;

			case SHOUBI:
				out = new ShrinkNosePage(getContext(), mShrinkNoseSite);
				break;

			case GAOBILIANG:
				out = new NosePage(getContext(), mNoseSite);
				break;

			case YIJIANMENGZHUANG:
				out = new MakeupSPage(getContext(), mMakeupSPageSite);
				break;
			case CAIZHUANG:
				out = new MakeupPage(getContext(), mMakeupPageSite);
				break;

			case LVJING:
				out = new FilterPage(getContext(), mFilter4PageSite);
				break;

			case XIANGKUANG:
				out = new FramePage(getContext(), mFramePageSite);
				break;

			case TIETU:
				out = new PendantPage(getContext(), mPendantSite);
				break;

			case MAOBOLI:
				out = new FilterPendantPage(getContext(), mFilterPendantPageSite);
				break;

			case MASAIKE:
				out = new MosaicPage(getContext(), mMosaicPageSite);
				break;

			case ZHIJIANMOFA:
				out = new BrushPage(getContext(), mBrushPageSite);
				break;

			case PINTU:
				break;

			default:
				out = new MyImageViewer(getContext());
				break;
		}

		return out;
	}

	protected void InitClipUi()
	{
		mView = MakeModuleView(UiMode.CLIP);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitMeiyanUi()
	{
		mView = MakeModuleView(UiMode.MEIYAN);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitShouShenUi()
	{
		mView = MakeModuleView(UiMode.SHOUSHEN);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitQudouUi()
	{
		mView = MakeModuleView(UiMode.QUDOU);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitQuyandaiUi()
	{
		mView = MakeModuleView(UiMode.QUYANDAI);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitLiangyanUi()
	{
		mView = MakeModuleView(UiMode.LIANGYAN);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitZenggaoUi() {
		mView = MakeModuleView(UiMode.ZENGGAO);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitDayanUi()
	{
		mView = MakeModuleView(UiMode.DAYAN);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitWeixiaoUi()
	{
		mView = MakeModuleView(UiMode.WEIXIAO);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitMeiyaUi() {
		mView = MakeModuleView(UiMode.MEIYA);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitShoubiUi() {
		mView = MakeModuleView(UiMode.SHOUBI);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitGaobiliangUi()
	{
		mView = MakeModuleView(UiMode.GAOBILIANG);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitYiJianMengZhuangUi() {
		mView = MakeModuleView(UiMode.YIJIANMENGZHUANG);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitCaizhuangUi()
	{
		mView = MakeModuleView(UiMode.CAIZHUANG);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitLvjingUi()
	{
		mView = MakeModuleView(UiMode.LVJING);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitXiangkuangUi()
	{
		mView = MakeModuleView(UiMode.XIANGKUANG);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitTietuUi()
	{
		mView = MakeModuleView(UiMode.TIETU);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitMaoboliUi()
	{
		mView = MakeModuleView(UiMode.MAOBOLI);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitMasaikeUi()
	{
		mView = MakeModuleView(UiMode.MASAIKE);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitZhijianmofaUi()
	{
		mView = MakeModuleView(UiMode.ZHIJIANMOFA);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(mView, 1, fl);
	}

	protected void InitPintuUi()
	{
	}

	protected void ClearMainView()
	{
		if(mView instanceof MyImageViewer)
		{
			mView.clearAnimation();
			((MyImageViewer)mView).clear();
			((MyImageViewer)mView).setSwitchListener(null);
			this.removeView(mView);
			mView = null;
		}
		if(mView instanceof PhotosViewPager)
		{
			mView.clearAnimation();
			mPhotoAdapter.onClose();
			this.removeView(mView);
			mView = null;
		}
	}

	protected void ClearPage()
	{
		if(mView != null)
		{
			if(mView instanceof IPage)
			{
				((IPage)mView).onClose();
			}
			this.removeView(mView);
			mView = null;
		}
	}

	protected void ClearClipUi()
	{
		ClearPage();

//		mSelected = 0;
//		mDivide.setVisibility(INVISIBLE);
//		mBottomBar.setVisibility(INVISIBLE);
//
//		mSelectedFace.setImageResource(R.drawable.beautify_page_face);
//		mSelectedBeautify.setImageResource(R.drawable.beautify_page_beautify);
//
//		mClipButton.setScaleX(1);
//		mClipButton.setScaleY(1);
//		mClipButton.setTranslationY(0);
//		mClipButton.setVisibility(VISIBLE);
//
//		mFaceButton.setScaleX(1);
//		mFaceButton.setScaleY(1);
//		mFaceButton.setTranslationY(0);
//		mFaceButton.setVisibility(VISIBLE);
//
//		mBeautifyButton.setScaleX(1);
//		mBeautifyButton.setScaleY(1);
//		mBeautifyButton.setTranslationY(0);
//		mBeautifyButton.setVisibility(VISIBLE);
//
//		mRecyclerView2.setVisibility(INVISIBLE);
//		mRecyclerView3.setVisibility(INVISIBLE);
//
//		mBgView.setBottomAlpha(1f);
	}

	protected void ClearMeiyanUi()
	{
		ClearPage();
	}

	protected void ClearShouShenUi()
	{
		ClearPage();
	}

	protected void ClearQudouUi()
	{
		ClearPage();
	}

	protected void ClearQuyandaiUi()
	{
		ClearPage();
	}

	protected void ClearLiangyanUi()
	{
		ClearPage();
	}

	protected void ClearZenggaoUi() {
		ClearPage();
	}

	protected void ClearDayanUi()
	{
		ClearPage();
	}

	protected void ClearWeixiaoUi()
	{
		ClearPage();
	}

	protected void ClearMeiyaUi() {
		ClearPage();
	}

	protected void ClearShoubiUi() {
		ClearPage();
	}

	protected void ClearGaobiliangUi()
	{
		ClearPage();
	}

	protected void ClearYiJianMengZhuangUi() {
		ClearPage();
	}

	protected void ClearCaizhuangUi()
	{
		ClearPage();
	}

	protected void ClearLvjingUi()
	{
		ClearPage();
	}

	protected void ClearXiangkuangUi()
	{
		ClearPage();
	}

	protected void ClearTietuUi()
	{
		ClearPage();
	}

	protected void ClearMaoboliUi()
	{
		ClearPage();
	}

	protected void ClearMasaikeUi()
	{
		ClearPage();
	}

	protected void ClearZhijianmofaUi()
	{
		ClearPage();
	}

	protected void ClearPintuUi()
	{
		ClearPage();
	}

	protected void ClearUI(final UiMode oldSel, final UiMode newSel, boolean hasAnimation)
	{
		switch(oldSel)
		{
			case NORMAL:
				if(!IsSamePage(oldSel, newSel))
				{
					SetTopBarState(false, hasAnimation);
					ClearMainView();

					SetBottomFr0State(1, false, hasAnimation);
				}
				else
				{
					SetBottomFr0State(0, false, hasAnimation);
				}
				break;

			case FACE:
				if(!IsSamePage(oldSel, newSel))
				{
					SetTopBarState(false, hasAnimation);
					ClearMainView();

					SetBottomFr0State(1, false, hasAnimation);
				} else {
					SetBottomFr0State(0, false, hasAnimation);
				}
				break;

			case BEAUTIFY:
				if(!IsSamePage(oldSel, newSel))
				{
					SetTopBarState(false, hasAnimation);
					ClearMainView();

					SetBottomFr0State(1, false, hasAnimation);
				} else {
					SetBottomFr0State(0, false, hasAnimation);
				}
				break;

			case CLIP:
				ClearClipUi();
				break;

			case MEIYAN:
				ClearMeiyanUi();
				break;

			case SHOUSHEN:
				ClearShouShenUi();
				break;

			case QUDOU:
				ClearQudouUi();
				break;

			case QUYANDAI:
				ClearQuyandaiUi();
				break;

			case LIANGYAN:
				ClearLiangyanUi();
				break;

			case ZENGGAO:
				ClearZenggaoUi();
				break;

			case DAYAN:
				ClearDayanUi();
				break;

			case WEIXIAO:
				ClearWeixiaoUi();
				break;

			case MEIYA:
				ClearMeiyaUi();
				break;

			case SHOUBI:
				ClearShoubiUi();
				break;

			case GAOBILIANG:
				ClearGaobiliangUi();
				break;

			case YIJIANMENGZHUANG:
				ClearYiJianMengZhuangUi();
				break;

			case CAIZHUANG:
				ClearCaizhuangUi();
				break;

			case LVJING:
				ClearLvjingUi();
				break;

			case XIANGKUANG:
				ClearXiangkuangUi();
				break;

			case TIETU:
				ClearTietuUi();
				break;

			case MAOBOLI:
				ClearMaoboliUi();
				break;

			case MASAIKE:
				ClearMasaikeUi();
				break;

			case ZHIJIANMOFA:
				ClearZhijianmofaUi();
				break;

			case PINTU:
				break;

			default:
				break;
		}
	}

	protected boolean IsSamePage(final UiMode oldPage, final UiMode newPage)
	{
		boolean out = false;

		if(oldPage == newPage || (oldPage == UiMode.NORMAL || oldPage == UiMode.FACE || oldPage == UiMode.BEAUTIFY) && (newPage == UiMode.NORMAL || newPage == UiMode.FACE || newPage == UiMode.BEAUTIFY))
		{
			out = true;
		}

		return out;
	}

	protected void UpdateUI(final UiMode oldPage, final UiMode newPage, boolean hasAnimation)
	{
		if(oldPage != null)
		{
			ClearUI(oldPage, newPage, hasAnimation);
		}
		if(newPage != null)
		{
			switch(newPage)
			{
				case NORMAL:
					InitMainView();

					if(IsSamePage(oldPage, newPage))
					{
						SetTopBarState(true, false);
						SetBottomFr0State(0, true, hasAnimation);
					}
					else
					{
						SetTopBarState(true, hasAnimation);
						SetBottomFr0State(1, true, hasAnimation);
					}
					break;

				case FACE:
					InitMainView();

					if(IsSamePage(oldPage, newPage))
					{
						SetTopBarState(true, false);
						SetBottomFr0State(0, true, hasAnimation);
					}
					else
					{
						SetTopBarState(true, hasAnimation);
						SetBottomFr0State(1, true, hasAnimation);
					}
					break;

				case BEAUTIFY:
					InitMainView();

					if(IsSamePage(oldPage, newPage))
					{
						SetTopBarState(true, false);
						SetBottomFr0State(0, true, hasAnimation);
					}
					else
					{
						SetTopBarState(true, hasAnimation);
						SetBottomFr0State(1, true, hasAnimation);
					}
					break;

				case CLIP:
					InitClipUi();
					break;

				case MEIYAN:
					InitMeiyanUi();
					break;

				case SHOUSHEN:
					InitShouShenUi();
					break;

				case QUDOU:
					InitQudouUi();
					break;

				case QUYANDAI:
					InitQuyandaiUi();
					break;

				case LIANGYAN:
					InitLiangyanUi();
					break;

				case ZENGGAO:
					InitZenggaoUi();
					break;

				case DAYAN:
					InitDayanUi();
					break;

				case WEIXIAO:
					InitWeixiaoUi();
					break;

				case MEIYA:
					InitMeiyaUi();
					break;

				case SHOUBI:
					InitShoubiUi();
					break;

				case GAOBILIANG:
					InitGaobiliangUi();
					break;

				case YIJIANMENGZHUANG:
					InitYiJianMengZhuangUi();
					break;

				case CAIZHUANG:
					InitCaizhuangUi();
					break;

				case LVJING:
					InitLvjingUi();
					break;

				case XIANGKUANG:
					InitXiangkuangUi();
					break;

				case TIETU:
					InitTietuUi();
					break;

				case MAOBOLI:
					InitMaoboliUi();
					break;

				case MASAIKE:
					InitMasaikeUi();
					break;

				case ZHIJIANMOFA:
					InitZhijianmofaUi();
					break;

				case PINTU:
					InitPintuUi();
					break;

				default:
					break;
			}
		}
	}

	protected void SetUIMode(final UiMode oldSel, final UiMode newSel, final boolean hasAnimation)
	{
		if(oldSel != newSel)
		{
			if(mView != null && mView instanceof MyImageViewer)
			{
				MyImageViewer imageViewer = (MyImageViewer)mView;
				RectF rect = imageViewer.getCurCache();
				if(rect != null)
				{
					float height = rect.bottom - rect.top;
					float width = rect.right - rect.left;
					if(height > 0 && width > 0)
					{
						if(mPageAnimParams == null)
						{
							mPageAnimParams = new HashMap<>();
						}
						else
						{
							mPageAnimParams.clear();
						}
						mPageAnimParams.put(PAGE_ANIM_IMG_H, (int)height);
						mPageAnimParams.put(PAGE_ANIM_VIEW_H, mViewH);
						mPageAnimParams.put(PAGE_ANIM_VIEW_TOP_MARGIN, mTopBarFrHeight);
					}
				}
			}

			if(mView != null && mView instanceof PhotosViewPager)
			{
				float height = mView.getHeight();
				float width = mView.getWidth();
				if(height > 0 && width > 0)
				{
					if(mPageAnimParams == null)
					{
						mPageAnimParams = new HashMap<>();
					}
					else
					{
						mPageAnimParams.clear();
					}
					mPageAnimParams.put(PAGE_ANIM_IMG_H, (int)height);
					mPageAnimParams.put(PAGE_ANIM_VIEW_H, mViewH);
					mPageAnimParams.put(PAGE_ANIM_VIEW_TOP_MARGIN, mTopBarFrHeight);
				}
			}

			UpdateUI(oldSel, newSel, hasAnimation);

			//动画
			if(newSel != null)
			{
				switch(newSel)
				{
					default:
						break;
				}
			}
		}
	}

	protected boolean ImgIsOk()
	{
		boolean out = false;

		if(mBmp != null)
		{
			out = true;
		}
		else
		{
			String path = GetImgPath(mCurImgInfo);
			if(path != null)
			{
				out = ImageUtils.AvailableImg(path);
			}
		}

		return out;
	}

	protected boolean ImgIsOk4UI()
	{
		boolean out = ImgIsOk();
		if(!out)
		{
			ShowImgErr();
		}
		return out;
	}

	protected void ShowImgErr()
	{
		Toast.makeText(getContext().getApplicationContext(), R.string.beautify4page_img_err, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @param bmp 只有在首页用到,其他页面用null
	 */
	protected void SetModeData(Bitmap bmp, HashMap<String, Object> ex)
	{
		if(mView instanceof MyImageViewer)
		{
			((MyImageViewer)mView).leave();
			((MyImageViewer)mView).enter(mCurImgIndex, bmp);
		}
		if(mView instanceof PhotosViewPager)
		{
			((PhotosViewPager) mView).setCurrentItem(mCurImgIndex);
			mPhotoAdapter.setCurBitmap(bmp);
		}

		else if(mView instanceof IPage)
		{
			if(mBmp == null)
			{
				String path = GetImgPath(mCurImgInfo);
				if(path != null)
				{
					Object obj = null;
					UndoRedoDataMgr mgr = sCacheDatas.get(path);
					if(mgr != null)
					{
						obj = mgr.GetCurrentData();
					}
					if(obj != null)
					{
						path = GetImgPath(obj);
					}
					RotationImg2 img = Utils.Path2ImgObj(path);
					mBmp = cn.poco.imagecore.Utils.DecodeFinalImage(getContext(), img.m_img, img.m_degree, -1, img.m_flip, -1, -1);
				}
			}
			if(mBmp != null)
			{
				HashMap<String, Object> params = new HashMap<>();
				if(mPageAnimParams != null)
				{
					params.putAll(mPageAnimParams);
					mPageAnimParams.put("doBackAnim", true);
				}
				if(ex != null)
				{
					params.putAll(ex);
				}
				params.put("imgs", mBmp);
				params.put("goto_save", mSaveToShare);
				params.put("bgimg", mBkBmp);
				params.put("add_date", mAddDate);
				params.put("has_water_mark", mHasWaterMark);
				params.put("water_mark_id", mWaterMarkId);
				((IPage)mView).SetData(params);
			}
			else
			{
				String path = GetImgPath(mCurImgInfo);
				if(path != null)
				{
					Object obj = null;
					UndoRedoDataMgr mgr = sCacheDatas.get(path);
					if(mgr != null)
					{
						obj = mgr.GetCurrentData();
					}
					if(obj != null)
					{
						path = GetImgPath(obj);
					}
					RotationImg2 img = Utils.Path2ImgObj(path);
					HashMap<String, Object> params = new HashMap<>();
					if(mPageAnimParams != null)
					{
						params.putAll(mPageAnimParams);
						mPageAnimParams.put("doBackAnim", true);
					}
					if(ex != null)
					{
						params.putAll(ex);
					}
					params.put("imgs", new RotationImg2[]{img});
					((IPage)mView).SetData(params);
				}
			}
		}
	}

	protected void UpdateUndoRedoBtnState()
	{
		SetBtnState(mCurImgInfo);
	}

	/**
	 * 设置撤销的按钮状态
	 *
	 * @param info
	 */
	protected void SetBtnState(Object info)
	{
		if(info != null)
		{
			String path = GetImgPath(info);
			if(path != null)
			{
				synchronized(CACHE_THREAD_LOCK)
				{
					UndoRedoDataMgr cacheData = sCacheDatas.get(path);
					if(cacheData != null)
					{
						if(cacheData.CanRedo())
						{
							mRedoBtn.setImageResource(R.drawable.beautify4page_redo_btn1);
							ImageUtils.AddSkin(getContext(), mRedoBtn);
						}
						else
						{
							mRedoBtn.setImageResource(R.drawable.beautify4page_redo_btn2);
							ImageUtils.RemoveSkin(getContext(), mRedoBtn);
						}
						if(cacheData.CanUndo())
						{
							mUndoBtn.setImageResource(R.drawable.beautify4page_undo_btn1);
							ImageUtils.AddSkin(getContext(), mUndoBtn);
							setScaleAnim(mCompareView, false, 100);
						}
						else
						{
							mUndoBtn.setImageResource(R.drawable.beautify4page_undo_btn2);
							ImageUtils.RemoveSkin(getContext(), mUndoBtn);
							setScaleAnim(mCompareView, true, 100);
						}
					}
					else
					{
						mUndoBtn.setImageResource(R.drawable.beautify4page_undo_btn2);
						ImageUtils.RemoveSkin(getContext(), mUndoBtn);
						mRedoBtn.setImageResource(R.drawable.beautify4page_redo_btn2);
						ImageUtils.RemoveSkin(getContext(), mRedoBtn);
						setScaleAnim(mCompareView, true, 100);
					}
				}
			}
		}
	}

	protected synchronized String GetImgPath(Object img)
	{
		String out = null;

		if(img instanceof PhotoInfo)
		{
			out = ((PhotoInfo)img).getImagePath();
		}
		else if(img instanceof RotationImg2[])
		{
			RotationImg2 img2 = ((RotationImg2[])img)[0];
			out = img2.m_orgPath;
			if (!FileUtil.isFileExists(out) && img2.m_img != null) {
				out = img2.m_img.toString();
			}
		}
		else if(img instanceof RotationImg2)
		{
			out = ((RotationImg2)img).m_orgPath;
		}
		else if(img instanceof String)
		{
			out = (String)img;
		}
		else if(img instanceof ImageFile2)
		{
			out = ((ImageFile2)img).SaveImg2(getContext())[0].m_orgPath;
		}

		return out;
	}

	protected class UIHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(mCmdEnabled)
			{
				switch(msg.what)
				{
					case Beautify4Handler.MSG_CACHE:
					{
						Beautify4Handler.CmdMsg params = (Beautify4Handler.CmdMsg)msg.obj;
						msg.obj = null;
						if(params.m_tempPath != null && params.m_info != null)
						{
							final Object info = params.m_info;
							String path = GetImgPath(info);
							if(path != null)
							{
								synchronized(CACHE_THREAD_LOCK)
								{
									UndoRedoDataMgr mgr = sCacheDatas.get(path);
									if(mgr == null)
									{
										mgr = new UndoRedoDataMgr(UNDO_REDO_SIZE, true, null);
										mgr.SetDeleteIndex(1);
										mgr.AddData(info instanceof PhotoInfo ? ((PhotoInfo)info).Clone() : info);
										sCacheDatas.put(path, mgr);
									}
									mgr.AddData(params.m_tempPath);
								}
								UpdateUndoRedoBtnState();

								UpdateShareBtnStyle();
							}
						}
						mUiEnabled = true;

						if (mSaveToShare && !TextUtils.isEmpty(params.m_tempPath)) {
							OnSave();
						} else {
							ShowPopup(BannerResMgr2.B22);
						}
						break;
					}
				}
			}
		}
	}

	protected void ShowPopup(String pos)
	{
		if(m_popupView != null && m_popupView.IsRecycle())
		{
			m_popupView = null;
		}
		if(m_popupView == null)
		{
			m_popupView = new PopupMgr(getContext(), pos, true, new PopupMgr.Callback()
			{
				@Override
				public void OnCloseBtn()
				{
				}

				@Override
				public void OnClose()
				{
				}

				@Override
				public void OnBtn()
				{
				}

				@Override
				public void OnJump(PopupMgr view, BannerRes res)
				{
					if(mUiEnabled && res != null)
					{
						BannerCore3.ExecuteCommand(getContext(), res.m_cmdStr, mSite.mCmdProc);
					}
				}
			});
			if(m_popupView.CanShow())
			{
				m_popupView.Create();
				m_popupView.SetBk(mBkBmp);
				m_popupView.Show(Beautify4Page.this);
			}
			else
			{
				m_popupView.ClearAll();
				m_popupView = null;
			}
		}
	}

	/**
	 * imgs : {@link PhotoInfo} / {@link RotationImg2}[] / {@link ImageFile2}<br>
	 * {@link DataKey#BEAUTIFY_DEF_OPEN_PAGE} Integer<br>
	 * {@link DataKey#BEAUTIFY_DEF_SEL_URI} Integer<br>
	 * only_one_pic : Boolean 可为null,只显示一张图片<br>
	 * index: 图片下标<br>
	 * folder_name: 文件夹名字<br>
	 * add_date: 添加日期<br>
	 * goto_save: Boolean 保存时跳分享
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		//复制为本地对象
		HashMap<String, Object> myParams = (HashMap<String, Object>)params.clone();
		mSite.m_inParams.remove(DataKey.BEAUTIFY_DEF_OPEN_PAGE);
		myParams.putAll(mSite.m_myParams);
		mSite.m_myParams.clear();

		mOrgImgInfo = myParams.get("imgs");
		mCurImgInfo = mOrgImgInfo;
		if(mCurImgInfo instanceof ImageFile2)
		{
			mCurImgInfo = ((ImageFile2)mCurImgInfo).SaveImg2(getContext());
		}

		Object obj = myParams.get(DataKey.BEAUTIFY_DEF_OPEN_PAGE);
		if(obj != null && obj instanceof Integer)
		{
			mDefOpenPage = UiMode.GetType((Integer)obj);
		}

		obj = myParams.get(DataKey.BEAUTIFY_DEF_SEL_URI);
		if(obj != null && obj instanceof Integer)
		{
			mDefSelUri = (Integer)obj;
		}

		//android.os.Debug.waitForDebugger();
		obj = myParams.get("only_one_pic");
		if(obj != null && obj instanceof Boolean)
		{
			mOnlyOnePic = (Boolean)obj;
		}

		obj = myParams.get("add_date");
		if(obj != null && obj instanceof Boolean)
		{
			mAddDate = (Boolean)obj;
		}

		obj = myParams.get("has_water_mark");
		if(obj != null && obj instanceof Boolean)
		{
			mHasWaterMark = (Boolean) obj;
		}

		obj = myParams.get("water_mark_id");
		if(obj != null && obj instanceof Integer)
		{
			mWaterMarkId = (Integer) obj;
		}

		obj = myParams.get("index");
		if(obj != null && obj instanceof Integer)
		{
			mCurImgIndex = (Integer)obj;
		}

		obj = myParams.get("folder_name");
		if(obj != null && obj instanceof String)
		{
			mFolderName = (String)obj;
		}

		obj = myParams.get("goto_save");
		if (obj instanceof Boolean) {
			mSaveToShare = (Boolean) obj;
		}

		obj = myParams.get("show_exit_dialog");
		if (obj instanceof Boolean)
		{
			mShowExitDialog = (Boolean) obj;
		}

		boolean isBack = false;
		obj = myParams.get("is_back");
		String backImagePath = null;
		if(obj != null && (Boolean)obj)
		{
			isBack = true;
			PhotoStore.getInstance(getContext()).clearCache();

			obj = myParams.get("back_index");
			if(obj != null && obj instanceof Integer)
			{
				mCurImgIndex = (Integer)obj;
			}

			obj = myParams.get("back_img");
			if (obj instanceof String) {
				backImagePath = (String) obj;
			}
		}

//		GetPhotoInfo(mCurImgInfo);
		if(mCurImgIndex < 0 || mOnlyOnePic)
		{
			if (isBack) {
				mCurImgInfo = mPhotoStore.getPhotoInfo(mCurImgIndex);
			}
			mAllImages.add(GetPhotoInfo(mCurImgInfo));
			mCurImgIndex = 0;
		}
		else
		{
			mAllImages.addAll(mPhotoStore.getPhotoInfos(mFolderName, mCurImgIndex));

			if (!isBack && mOrgImgInfo instanceof RotationImg2[]) {
				String path = ((RotationImg2[])mOrgImgInfo)[0].m_orgPath;

				PhotoInfo photoInfo;
				// 由于根据图片路径获取的下标index和根据下标index获取图片数据之间可能存在误差，需要修正
				int size = mAllImages.size();
				for (int i = size / 2, j = i + 1; i >= 0 || j < size; i--, j++) {
					if (i >= 0) {
						photoInfo = mAllImages.get(i);
						if (photoInfo.getImagePath().equals(path)) {
							mCurImgIndex = i;
							mCurImgInfo = photoInfo;
							break;
						}
					}

					if (j < size) {
						photoInfo = mAllImages.get(j);
						if (photoInfo.getImagePath().equals(path)) {
							mCurImgIndex = j;
							mCurImgInfo = photoInfo;
							break;
						}
					}
				}
			} else {

				if (backImagePath != null) {
					// 由于某些手机屏幕截图会在系统相册前面，导致分享返回"back_index=0"时图片错乱
					mCurImgIndex = mPhotoStore.getPhotoInfoIndex(mFolderName, backImagePath);
				}

				mCurImgInfo = mPhotoStore.getPhotoInfo(mCurImgIndex); //不能删后退时拿真实数据
				mCurImgIndex = mPhotoStore.mapToCacheIndex(mCurImgIndex);
			}
		}

		Object img = null;
		String path = GetImgPath(mCurImgInfo);
		if(path != null)
		{
			synchronized(CACHE_THREAD_LOCK)
			{
				UndoRedoDataMgr mgr = sCacheDatas.get(path);
				if(mgr != null)
				{
					img = mgr.GetCurrentData();
				}
			}
		}
		if(img == null)
		{
			img = mCurImgInfo;
			if(img instanceof PhotoInfo)
			{
				img = Utils.Path2ImgObj(((PhotoInfo)img).getImagePath());
			}
		}
		Bitmap bmp = ImageUtils.MakeBmp(getContext(), img, mViewW, mViewH);
		{
			if (bmp != null)
			{
				Bitmap temp = Bitmap.createBitmap(bmp);
				if(temp != null)
				{
					Canvas canvas = new Canvas(temp);
					canvas.drawColor(0xFFFFFFFF, PorterDuff.Mode.DST_ATOP);
					if(temp.getWidth() < 10 || temp.getHeight() < 10)
					{
						//修复图片太小黑色的bug
						canvas.drawColor(0xffffffff);
					}
				}
				//加毛玻璃背景
				mBkBmp = BeautifyResMgr2.MakeBkBmp(temp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0xe5f0f0f0);
				this.setBackgroundDrawable(new BitmapDrawable(getResources(), mBkBmp));
			}
		}

		if(mView != null)
		{
			this.removeView(mView);
		}
		if(mDefOpenPage != null)
		{
			mBottomLayout.setVisibility(INVISIBLE);
			SetUIMode(null, mDefOpenPage, false);
			mUiMode = mDefOpenPage;
		}
		else
		{
			SetUIMode(null, UiMode.NORMAL, false);
			mUiMode = UiMode.NORMAL;
		}

		HashMap<String, Object> ex = new HashMap<>();
		ex.put(DataKey.BEAUTIFY_DEF_SEL_URI, mDefSelUri);
		SetModeData(bmp, ex);

		UpdateUndoRedoBtnState();
		UpdateShareBtnStyle();
//		setScaleAnim(mCompareView, true, 100);
		mUiEnabled = true;

		if(mDefOpenPage != null && mDefOpenPage == UiMode.NORMAL)
		{
			ShowPopup(BannerResMgr2.B21);
		}

		final MemoryTipDialog dialog = MemoryTipDialog.shouldShowMemoryDialog(getContext(), Utils.SDCARD_WARNING);
		if (dialog != null) {
			dialog.setPositiveClickListener(new MemoryTipDialog.OnDialogClick() {
				@Override
				public void onClick(AlertDialogV1 dialogV1) {
					dialog.dismiss();
					Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
					getContext().startActivity(intent);
				}
			});
			dialog.show();
		}
	}

	private long mLastDuration = 350;

	private void setScaleAnim(final View view, boolean hide, long duration) {
		view.animate().cancel();
		if (hide && view.getVisibility() == VISIBLE) {
			view.animate().scaleX(0).scaleY(0).setDuration(duration).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					view.setVisibility(INVISIBLE);
				}
			});
		} else if (!hide) {
			if (view.getVisibility() != VISIBLE) {
				view.setScaleX(0);
				view.setScaleY(0);
				view.setVisibility(VISIBLE);
				view.animate().scaleX(1).scaleY(1).setDuration(duration).setListener(null);
			} else {
				long realDuration = (long)((1 - view.getScaleX()) * mLastDuration);
				view.animate().scaleX(1).scaleY(1).setDuration(realDuration).setListener(null);
			}
		}
		mLastDuration = duration;
	}

	protected PhotoInfo GetPhotoInfo(Object obj)
	{
		PhotoInfo out = null;

		if(obj instanceof String)
		{
			obj = Utils.Path2ImgObj((String)obj);
		}
		else if(obj instanceof RotationImg2[])
		{
			obj = ((RotationImg2[])obj)[0];
		}

		if(obj instanceof RotationImg2)
		{
			out = new PhotoInfo();
			if(((RotationImg2)obj).m_orgPath != null)
			{
				out.setImagePath(((RotationImg2)obj).m_orgPath);
			}
			else if(((RotationImg2)obj).m_img instanceof String)
			{
				out.setImagePath((String)((RotationImg2)obj).m_img);
			}
			out.setRotation(((RotationImg2)obj).m_degree);
		}
		else if(obj instanceof PhotoInfo)
		{
			out = (PhotoInfo)obj;
		}

		return out;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if((!mComparing && !mUiEnabled) || mQuit)
		{
			return true;
		}
		else
		{
			return super.onInterceptTouchEvent(ev);
		}
	}

	/**
	 * 当什么都没操作调用此保存函数
	 *
	 * @return
	 */
	protected boolean NoChangeSave(Object curImg)
	{
		boolean success = false;

		String curPath = GetImgPath(curImg);
		if(curPath != null && ImageUtils.AvailableImg(curPath))
		{
			//如果是缓存图片就复制到系统相册
			if(FolderMgr.getInstance().IsCachePath(curPath))
			{
				String path = null;
				try
				{
					path = Utils.MakeSavePhotoPath(getContext(), GetImgScaleWH(curPath));
                    //添加水印 & 日期
                    if(mHasWaterMark || mAddDate)
                    {
                        Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile(curPath, null, true);
                        if(mHasWaterMark && mWaterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                        {
                            WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(mWaterMarkId);
                            if(watermarkItem != null && watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                            {
                                if(bmp != null)
                                {
                                    PhotoMark.drawWaterMarkLeft(bmp, MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mAddDate);
                                }
                            }
                        }

                        if(mAddDate)
                        {
                            if(bmp != null)
                            {
                                PhotoMark.drawDataLeft(bmp);
                            }
                        }

                        if(bmp != null && !bmp.isRecycled())
						{
							Utils.SaveImg(getContext(), bmp, path, 100);
						}
						else
						{
							FileUtils.copyFile(new File(curPath), new File(path));
						}
                    }
					else
					{
						FileUtils.copyFile(new File(curPath), new File(path));
					}
					Utils.FileScan(getContext(), path);
				}
				catch(Throwable e)
				{
					e.printStackTrace();

					path = null;
				}
				if(path != null)
				{
					mUiEnabled = false;
					RotationImg2 img = Utils.Path2ImgObj(path);
					OnSave(img, 0, true);
					success = true;
				}
			}
			else
			{
				mUiEnabled = false;
				RotationImg2 img = Utils.Path2ImgObj(curPath);
				OnSave(img, mPhotoStore.mapFromCacheIndex(mCurImgIndex), false);
				success = true;
			}
		}

		return success;
	}

	public interface SaveCallback
	{
		void OnComplete(boolean state);
	}

	protected void SaveOneImg(final SaveCallback cb)
	{
		final String curPath = GetImgPath(mCurImgInfo);
		boolean availableImg = ImageUtils.AvailableImg(curPath);
		UndoRedoDataMgr mgr = sCacheDatas.get(curPath);

		if(availableImg && (mgr == null || mgr.GetCurrentIndex() == 0))
		{
			TongJi2.AddCountByRes(getContext(), R.integer.修图_分享);
			MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_分享);
			boolean success = NoChangeSave(mCurImgInfo);
			cb.OnComplete(success);
		}
		else if(mgr != null && mgr.GetCurrentIndex() > 0)
		{
			TongJi2.AddCountByRes(getContext(), R.integer.修图_保存并分享);
			MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_保存并分享);
			final String curPath2 = GetImgPath(mgr.GetCurrentData());
			mUiEnabled = false;
			final WaitAnimDialog dlg = new WaitAnimDialog((Activity) getContext());
			dlg.show();
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					String path = null;
					try
					{
						path = Utils.MakeSavePhotoPath(getContext(), GetImgScaleWH(curPath2));
                        //添加水印 & 日期
                        if(mHasWaterMark || mAddDate)
                        {
                            Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile(curPath2, null, true);
                            if(mHasWaterMark && mWaterMarkId != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                            {
                                WatermarkItem watermarkItem = WatermarkResMgr2.getInstance().GetWaterMarkById(mWaterMarkId);
                                if(watermarkItem != null && watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                                {
                                    if(bmp != null)
                                    {
                                        PhotoMark.drawWaterMarkLeft(bmp, MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888), mAddDate);
                                    }
                                }
                            }

                            if(mAddDate)
                            {
                                if(bmp != null)
                                {
                                    PhotoMark.drawDataLeft(bmp);
                                }
                            }

                            if(bmp != null && !bmp.isRecycled())
							{
								Utils.SaveImg(getContext(), bmp, path, 100);
							}
							else
							{
								FileUtils.copyFile(new File(curPath2), new File(path));
							}
                        }
						else
						{
							FileUtils.copyFile(new File(curPath2), new File(path));
						}
						Utils.FileScan(getContext(), path);
					}
					catch(Throwable e)
					{
						e.printStackTrace();

						path = null;
					}
					final String okPath = path;
					if(okPath != null)
					{
						Beautify4Page.this.post(new Runnable()
						{
							@Override
							public void run()
							{
								dlg.dismiss();

								//保存后清理记录
								sCacheDatas.remove(curPath);

								mUiEnabled = false;
								RotationImg2 img = Utils.Path2ImgObj(okPath);
								OnSave(img, 0, true);
								cb.OnComplete(true);
							}
						});
					}
					else
					{
						Beautify4Page.this.post(new Runnable()
						{
							@Override
							public void run()
							{
								dlg.dismiss();

								mUiEnabled = true;
								cb.OnComplete(false);
							}
						});
					}
				}
			}).start();
		}
		else
		{
			cb.OnComplete(false);
		}
	}

	/**
	 * @param backIndex 后退用的index
	 */
	protected void OnSave(RotationImg2 img, int backIndex, boolean hasSave)
	{
		if(img != null)
		{
			//保存后退用的index
			mSite.m_myParams.put("is_back", true);
			mSite.m_myParams.put("back_index", backIndex);
			if (img.m_orgPath != null) {
				mSite.m_myParams.put("back_img", img.m_orgPath);
			}
			if (backIndex == 0 && hasSave) {
				mSite.m_myParams.put("folder_name", null);
			}

			HashMap<String, Object> params = new HashMap<>();
			if(!TextUtils.isEmpty(mLancomeResId))
			{
				params.put("show_business_banner",X);
			}
			if(m_isYSLChoose)
			{
				Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071802937/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
			}
			params.put("img", img);
			if(!hasSave)
			{
				params.put("not_save", true);
			}
			if(mSaveToShare)
			{
				params.put("hide_button",true);
			}
			if (!TextUtils.isEmpty(mChannelValue))
			{
				AbsAdRes adRes = HomeAd.GetOneHomeRes(getContext(), mChannelValue);
				if(adRes instanceof AbsChannelAdRes)
				{
					params.put(HomePageSite.BUSINESS_KEY, adRes);
					params.put(HomePageSite.POST_STR_KEY, HomePageSite.makePostVar(getContext(), adRes.mAdId));
				}

				mChannelValue = null;
			}

			//记录 拍照-预览-美颜美化-保存的闪关灯模式，用于继续拍照流程
			if (mSite.m_inParams != null)
			{
				if (mSite.m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_FLASH_MODE))
				{
					params.put(CameraSetDataKey.KEY_CAMERA_FLASH_MODE, mSite.m_inParams.get(CameraSetDataKey.KEY_CAMERA_FLASH_MODE));
				}
				if (mSite.m_inParams.containsKey(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK))
				{
					params.put(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK, mSite.m_inParams.get(CameraSetDataKey.KEY_CAMERA_FRONT_SPLASH_MASK));
				}
			}
			mSite.OnSave(getContext(), params);
		}
	}

	protected void OnSave()
	{
		SaveOneImg(new SaveCallback()
		{
			@Override
			public void OnComplete(boolean state)
			{
				if(!state)
				{
					Toast.makeText(getContext(), R.string.beautify4page_save_fail, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 获取图片的w/h比例
	 */
	public static float GetImgScaleWH(String path)
	{
		float out = 0;
		if(path != null)
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			if(opts.outHeight != 0)
			{
				out = (float)opts.outWidth / (float)opts.outHeight;
			}
		}
		return out;
	}

	protected OnAnimationClickListener mBtnListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(Beautify4Page.this.mUiEnabled)
			{
				if(v == mBackBtn)
				{
					if(m_popupView != null && m_popupView.IsShow())
					{
						m_popupView.OnCancel(true);
					}
					else
					{
						TongJi2.AddCountByRes(getContext(), R.integer.修图_返回);
						MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_返回选图);
						if (mShowExitDialog && isModify) {
							showExitDialog();
						} else {
							mSite.OnBack(getContext());
						}
					}
				}
				else if(v == mSaveBtn)
				{
					OnSave();
				}
				else if(v == mClipButton || v == mSelectedClip)
				{
					if(ImgIsOk4UI())
					{
						mUiEnabled = false;
						TongJi2.AddCountByRes(getContext(), R.integer.修图_编辑);
						MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_编辑);
						SetUIMode(mUiMode, UiMode.CLIP, true);
						mUiMode = UiMode.CLIP;
						SetModeData(mBmp, null);
						mBmp = null;
					}
				}
				else if(v == mFaceButton)
				{
					boolean sdkIsValid = PocoDetector.detectFaceSdkIsValid(new Date());

					if (!hasShowSdkOutDateDialog && !sdkIsValid) {
						showSDKOutDateDialog();
					} else {
						onClickFaceButton();
					}

				} else if (v == mSelectedFace) {
					boolean sdkIsValid = PocoDetector.detectFaceSdkIsValid(new Date());

					if (!hasShowSdkOutDateDialog && !sdkIsValid) {
						showSDKOutDateDialog();
					} else {
						onClickSelectdFaceButton();
					}

				} else if(v == mBeautifyButton)
				{
					mUiEnabled = false;
					TongJi2.AddCountByRes(getContext(), R.integer.修图_素材美化);
					MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_素材美化);
					mSelected = 2;
					mUiMode = UiMode.BEAUTIFY;
					mCloseAnimator.start();
				} else if (v == mSelectedBeautify) {
					mUiEnabled = false;
					if (mSelected == 2) {
						mOpenAnimator.start();
					} else {
						mSelected = 2;
						mUiMode = UiMode.BEAUTIFY;
						mChangeBeautifyListAnimator.start();
					}

				} else if (v == mUndoBtn) {
					if (mCurImgInfo != null && mView instanceof MyImageViewer) {
						String path = GetImgPath(mCurImgInfo);
						if (path != null) {
							FaceDataV2.ResetData();
							synchronized (CACHE_THREAD_LOCK) {
								UndoRedoDataMgr cacheData = sCacheDatas.get(path);
								if (cacheData != null && cacheData.CanUndo()) {

									TongJi2.AddCountByRes(getContext(), R.integer.修图_撤销);
									MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_撤销);
									mBmp = null;

									Object img = Utils.Path2ImgObj(GetImgPath(cacheData.Undo()));
									Bitmap bmp = ImageUtils.MakeBmp(getContext(), img, mViewW, mViewH);
									((MyImageViewer)mView).setCurBitmap(bmp);

									UpdateUndoRedoBtnState();
									UpdateShareBtnStyle();
								}
							}
						}
					}
					if (mCurImgInfo != null && mView instanceof PhotosViewPager) {
						String path = GetImgPath(mCurImgInfo);
						if (path != null) {
							FaceDataV2.ResetData();
							synchronized (CACHE_THREAD_LOCK) {
								UndoRedoDataMgr cacheData = sCacheDatas.get(path);
								if (cacheData != null && cacheData.CanUndo()) {

									TongJi2.AddCountByRes(getContext(), R.integer.修图_撤销);
									MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_撤销);
									mBmp = null;

									Object img = Utils.Path2ImgObj(GetImgPath(cacheData.Undo()));
									Bitmap bmp = ImageUtils.MakeBmp(getContext(), img, mViewW, mViewH);
									mPhotoAdapter.setCurBitmap(bmp);

									UpdateUndoRedoBtnState();
									UpdateShareBtnStyle();
								}
							}
						}
					}
				} else if (v == mRedoBtn) {
					if (mCurImgInfo != null && mView instanceof MyImageViewer) {
						String path = GetImgPath(mCurImgInfo);
						if (path != null) {
							synchronized (CACHE_THREAD_LOCK) {
								UndoRedoDataMgr cacheData = sCacheDatas.get(path);
								if (cacheData != null && cacheData.CanRedo()) {

									TongJi2.AddCountByRes(getContext(), R.integer.修图_重做);
									MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_重做);
									mBmp = null;

									Object img = Utils.Path2ImgObj(GetImgPath(cacheData.Redo()));
									Bitmap bmp = ImageUtils.MakeBmp(getContext(), img, mViewW, mViewH);
									((MyImageViewer)mView).setCurBitmap(bmp);

									UpdateUndoRedoBtnState();
									UpdateShareBtnStyle();
								}
							}
						}
					}
					if (mCurImgInfo != null && mView instanceof PhotosViewPager) {
						String path = GetImgPath(mCurImgInfo);
						if (path != null) {
							synchronized (CACHE_THREAD_LOCK) {
								UndoRedoDataMgr cacheData = sCacheDatas.get(path);
								if (cacheData != null && cacheData.CanRedo()) {

									TongJi2.AddCountByRes(getContext(), R.integer.修图_重做);
									MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_重做);
									mBmp = null;

									Object img = Utils.Path2ImgObj(GetImgPath(cacheData.Redo()));
									Bitmap bmp = ImageUtils.MakeBmp(getContext(), img, mViewW, mViewH);
									mPhotoAdapter.setCurBitmap(bmp);

									UpdateUndoRedoBtnState();
									UpdateShareBtnStyle();

									UpdateUndoRedoBtnState();
									UpdateShareBtnStyle();
								}
							}
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

	/**
	 * 单击美颜美形按钮
	 */
	private void onClickFaceButton() {
		if (mShowFaceTip) {
			TagMgr.SetTag(getContext(), Tags.FACE_CLICK_FLAG);
			mFaceButton.setNew(false);
			mShowFaceTip = false;
		}
		mUiEnabled = false;
		TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形);
		MyBeautyStat.onClickByRes(R.string.美颜美图_照片预览页_主页面_美颜美形);
		mUiMode = UiMode.FACE;
		mSelected = 1;
		mCloseAnimator.start();
	}

	private void onClickSelectdFaceButton() {
		if (mShowFaceTip) {
			TagMgr.SetTag(getContext(), Tags.FACE_CLICK_FLAG);
			mFaceButton.setNew(false);
			mShowFaceTip = false;
		}
		mUiEnabled = false;
		if (mSelected == 1) {
			mOpenAnimator.start();
		} else {
			mSelected = 1;
			mUiMode = UiMode.FACE;
			mChangeFaceListAnimator.start();
		}
	}

	private void doPageBackAnim()
	{
//		if(mView != null && mView instanceof MyImageViewer)
		if(mView != null && (mView instanceof MyImageViewer || mView instanceof PhotosViewPager))
		{
			if(mPageAnimParams != null && mPageAnimParams.containsKey("doBackAnim"))
			{
				int imgH = 0, viewTopMargin = 0;
				Object o;

				o = mPageAnimParams.get(PAGE_ANIM_IMG_H);
				if (o instanceof Integer) {
					imgH = (int)o;
				}

				o = mPageAnimParams.get(PAGE_ANIM_VIEW_TOP_MARGIN);
				if (o instanceof Integer) {
					viewTopMargin = (int)o;
				}

				float backImgH = 0;
				float backViewTopMargin = 0;
				o = mPageAnimParams.get(PAGE_BACK_ANIM_IMG_H);
				if (o instanceof Float) {
					backImgH = (Float) o;
				}
				o = mPageAnimParams.get(PAGE_BACK_ANIM_VIEW_TOP_MARGIN);
				if (o instanceof Float) {
					backViewTopMargin = (Float) o;
				}

				mPageAnimParams.clear();

				if (backImgH == 0 && backViewTopMargin == 0) {
					float fromY = -(/*mViewH - imgH + */viewTopMargin) * 1.0f / mViewH;
					TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, 0.0f);
					translateAnimation.setDuration(350);
					translateAnimation.setFillAfter(true);
					mView.startAnimation(translateAnimation);
				} else {
					float scale;
					if (imgH != 0) {
						scale = backImgH / imgH;
					} else {
						scale = 1;
					}
					float translationY = backViewTopMargin - viewTopMargin;
					AnimatorSet set = new AnimatorSet();
					ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mView, "scaleX", scale, 1);
					ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mView, "scaleY", scale, 1);
					ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mView, "translationY", translationY, 0);
					set.play(scaleXAnimator).with(scaleYAnimator).with(translationAnimator);
					set.setDuration(300);
					set.start();
				}
			}
		}
	}

	@Override
	public void onBack()
	{
		if (mRecommendDialog != null && mRecommendDialog.isShow()) {
			mRecommendDialog.dismiss();
			mRecommendDialog = null;
			return;
		}

		switch(mUiMode)
		{
			case CLIP:
			case MEIYAN:
			case SHOUSHEN:
			case QUDOU:
			case DAYAN:
			case QUYANDAI:
			case LIANGYAN:
			case ZENGGAO:
			case WEIXIAO:
			case MEIYA:
			case SHOUBI:
			case GAOBILIANG:
			case YIJIANMENGZHUANG:
			case CAIZHUANG:
			case LVJING:
			case XIANGKUANG:
			case TIETU:
			case MAOBOLI:
			case MASAIKE:
			case ZHIJIANMOFA:
			case PINTU:
				if(mView instanceof IPage)
				{
					((IPage)mView).onBack();
				}
				break;

			default:
				mBtnListener.onAnimationClick(mBackBtn);
				break;
		}
	}

	@Override
	public void onClose()
	{
		mUiEnabled = false;
		mQuit = true;

		if(m_popupView != null)
		{
			m_popupView.ClearAll();
			m_popupView = null;
		}

		if(mImageThread != null)
		{
			mImageThread.quit();
			mImageThread = null;
		}

		if (mRecommendDialog != null) {
			if (mRecommendDialog.isShow()) {
				mRecommendDialog.dismiss();
			}
			mRecommendDialog = null;
		}
		clearExitDialog();
		ClearMainView();
		ClearPage();

		mOriginBitmap = null;

		// 清除人脸检测数据
		FaceDataV2.ResetData();

		TongJiUtils.onPageEnd(getContext(), R.string.美化);
		MyBeautyStat.onPageEndByRes(R.string.美颜美图_照片预览页_主页面);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		if(siteID == SiteID.DOWNLOAD_MORE || siteID == SiteID.THEME_INTRO)
		{
			if(params != null)
			{
				ResType type;
				if(siteID == SiteID.DOWNLOAD_MORE)
				{
					type = (ResType)params.get(DownloadMorePageSite.DOWNLOAD_MORE_TYPE);
				}
				else
				{
					type = (ResType)params.get(ThemeIntroPageSite.TYPE);
				}
				int uri = 0;
				if(siteID == SiteID.DOWNLOAD_MORE)
				{
					Object obj = params.get(DownloadMorePageSite.DOWNLOAD_MORE_ID);
					if(obj instanceof Integer)
					{
						uri = (Integer)obj;
					}
				}
				else
				{
					Object obj = params.get(ThemeIntroPageSite.ID);
					if(obj instanceof Integer)
					{
						uri = (Integer)obj;
					}
				}
				/*boolean del = false;
				{
					Object obj = params.get(DownloadMorePageSite.DOWNLOAD_MORE_DEL);
					if(obj instanceof Boolean)
					{
						del = (Boolean)obj;
					}
				}*/
				HashMap<String, Object> newParams = new HashMap<>();
				newParams.putAll(params);
				newParams.put(DataKey.BEAUTIFY_DEF_SEL_URI, uri);
				switch(type)
				{
					case BRUSH:
						if(mView instanceof BrushPage)
						{
							((BrushPage)mView).onPageResult(siteID, newParams);
						}
						break;

					case FRAME:
					case FRAME2:
 						if(mView instanceof FramePage)
						{
							((FramePage)mView).onPageResult(siteID, newParams);
						}
						break;

					case DECORATE:
						if(mView instanceof PendantPage)
						{
							((PendantPage)mView).onPageResult(siteID, newParams);
						}
						break;

					case GLASS:
						if(mView instanceof FilterPendantPage)
						{
							((FilterPendantPage)mView).onPageResult(siteID, newParams);
						}
						break;

					case MOSAIC:
						if(mView instanceof MosaicPage)
						{
							((MosaicPage)mView).onPageResult(siteID, newParams);
						}
						break;

					case MAKEUP_GROUP:
						if(mView instanceof MakeupPage)
						{
							((MakeupPage)mView).onPageResult(siteID, newParams);
						}
						break;
				}
			}
		}
		else if(siteID == SiteID.LOGIN || siteID == SiteID.REGISTER_DETAIL || siteID == SiteID.RESETPSW)
		{
			switch(mUiMode)
			{
				case XIANGKUANG:
				case MASAIKE:
				case MAOBOLI:
				case ZHIJIANMOFA:
				case TIETU:
				case YIJIANMENGZHUANG:
				case CAIZHUANG:
				case LVJING:
					if(mView instanceof IPage)
					{
						((IPage)mView).onPageResult(siteID, params);
					}
					break;

				default:
					break;
			}
		}
		else if(siteID == SiteID.CHANGEPOINT_PAGE)
		{
			switch(mUiMode)
			{
				case SHOUSHEN:
				case QUDOU:
				case DAYAN:
				case QUYANDAI:
				case LIANGYAN:
				case ZENGGAO:
				case WEIXIAO:
				case MEIYA:
				case SHOUBI:
				case GAOBILIANG:
				case YIJIANMENGZHUANG:
				case CAIZHUANG:
					if(mView instanceof IPage)
					{
						((IPage)mView).onPageResult(siteID, params);
					}
					break;

				default:
					break;
			}
		}
		else if ((siteID == SiteID.FILTER_DOWNLOAD_MORE || siteID == SiteID.FILTER_DETAIL) && mUiMode == UiMode.LVJING) {
			if(mView instanceof IPage)
			{
				((IPage)mView).onPageResult(siteID, params);
			}
		}
	}

	public void setSelect(int type, int uri)
	{
		UiMode mode = UiMode.GetType(type);
		if(mode != null && mUiEnabled)
		{
			switch(mUiMode)
			{
				case MEIYAN:
					mBeautySite.OnBack(getContext(), null);
					break;
				case SHOUSHEN:
					mSlimSite.onBack(getContext(), null);
					break;
				case QUDOU:
					mAcneSite.onBack(getContext(), null);
					break;
				case DAYAN:
					mBigEyesSite.onBack(getContext(), null);
					break;
				case QUYANDAI:
					mEyeBagSite.onBack(getContext(), null);
					break;
				case LIANGYAN:
					mBrightEyesSite.onBack(getContext(), null);
					break;
				case ZENGGAO:
					mRisePageSite.onBack(getContext(), null);
					break;
				case WEIXIAO:
					mSmileSite.onBack(getContext(), null);
					break;
				case MEIYA:
					mWhiteTeethSite.onBack(getContext(), null);
					break;
				case SHOUBI:
					mShrinkNoseSite.onBack(getContext(), null);
					break;
				case GAOBILIANG:
					mNoseSite.onBack(getContext(), null);
					break;
				case YIJIANMENGZHUANG:
					mMakeupSPageSite.onBack(getContext(), null);
					break;
				case CAIZHUANG:
					mMakeupPageSite.onBack(getContext(), null);
					break;
				case LVJING:
					mFilter4PageSite.OnBack(getContext(), null);
					break;
				case XIANGKUANG:
					mFramePageSite.OnBack(getContext(), null);
					break;
				case TIETU:
					mPendantSite.onBack(getContext(), null);
					break;
				case MAOBOLI:
					mFilter4PageSite.OnBack(getContext(), null);
					break;
				case MASAIKE:
					mMosaicPageSite.OnBack(getContext(), null);
					break;
				case ZHIJIANMOFA:
					mBrushPageSite.OnBack(getContext(), null);
					break;
				case PINTU:
					break;
			}
			SetUIMode(mUiMode, mode, false);
			mUiMode = mode;
			HashMap<String, Object> ex = new HashMap<>();
			ex.put(DataKey.BEAUTIFY_DEF_SEL_URI, uri);
			SetModeData(null, ex);

			UpdateUndoRedoBtnState();
			UpdateShareBtnStyle();
		}
	}

	private void showExitDialog()
	{
		if (mExitDialog == null)
		{
			MyBeautyStat.onPageStartByRes(R.string.美颜美图_修图退出确认_主页面);
			mExitDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
			mExitDialog.setCancelButtonText(R.string.cancel)
					.setOkButtonText(R.string.ensure)
					.setMessage(R.string.confirm_back)
					.setListener(new CloudAlbumDialog.OnButtonClickListener()
			{
				@Override
				public void onOkButtonClick()
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_修图确认退出_主页面_确认);
					MyBeautyStat.onPageEndByRes(R.string.美颜美图_修图退出确认_主页面);
					if (mExitDialog != null)
					{
						mExitDialog.dismiss();
					}
					if (mSite != null) mSite.OnBack(getContext());
				}

				@Override
				public void onCancelButtonClick()
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_修图确认退出_主页面_取消);
					MyBeautyStat.onPageEndByRes(R.string.美颜美图_修图退出确认_主页面);
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

	private void showSDKOutDateDialog() {

		final SdkOutDatedDialog dialog = new SdkOutDatedDialog((Activity)getContext());
		dialog.setCallback(new SdkOutDatedDialog.SdkDialogCallback() {
			@Override
			public void updateNow() {
				dialog.dismiss();
				CommonUtils.OpenBrowser(getContext(), "market://details?id=my.beautyCamera");
			}

			@Override
			public void updateLater() {
				onClickFaceButton();
			}
		});
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				onClickFaceButton();
			}
		});
		hasShowSdkOutDateDialog = true;
		dialog.show();
	}
}

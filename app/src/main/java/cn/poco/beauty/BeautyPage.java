package cn.poco.beauty;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.acne.view.CirclePanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.album.utils.ListItemDecoration;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.beautify.EffectType;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.beauty.adapter.BeautyAdapter;
import cn.poco.beauty.model.BeautyItem;
import cn.poco.beauty.site.BeautySite;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.beauty.view.ItemView;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceLocalData;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.image.filter;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import my.beautyCamera.R;

import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_IMG_H;
import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN;

/**
 * Created by: fwc
 * Date: 2016/12/20
 * 美颜界面
 */
@SuppressWarnings("all")
public class BeautyPage extends IPage {

	public static final int DEF_LIGHT_VALUE = 0;
	public static final int DEF_BLUR_VALUE = 0;
	public static final int DEF_HUE_VALUE = 50;

	private Context mContext;
	private BeautySite mSite;

	private int mFrameWidth;
	private int mFrameHeight;

	private int mTopBarHeight;
	private int mContentHeight;
	private int mImagePadding;
	private int mSeekBarHeight;
	private int mCircleRadius;

	private int DEF_IMG_SIZE;

	private WaitAnimDialog mWaitDialog;

	private BeautyCommonViewEx mBeautyView;
	private Object imgs;
	private Bitmap mBitmap;
	private Bitmap mTempCompareBmp = null;

	private FrameLayout mBottomLayout;

	private RelativeLayout mTopBarLayout;
	private ImageView mCancelView;
	private ImageView mOkView;

	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	private List<BeautyItem> mItems;
	private BeautyAdapter mBeautyAdapter;

	private FrameLayout mControlPanel;
	private ImageView mArrowBack;
	private ColorSeekBar mBuffingSeekBar;
	private ColorSeekBar mWhiteningSeekBar;
	private ColorSeekBar mComplexionSeekBar;

	private ItemView mSelectedItem;
	private ColorSeekBar mColorAlphaSeekBar;

	private Handler mUIHandler;
	private HandlerThread mHandlerThread;
	private BeautyHandler mBeautyHandler;

	private int mWhiteningValue; // 美白
	private int mBuffingValue; // 磨皮
	private int mComplexionValue; // 肤色

	private SparseIntArray mColorAlphaMap = new SparseIntArray();
	private int mType;
	private int mPosition = -1;

	private AnimatorSet mShowUserControl;
	private AnimatorSet mHideUserControl;

	private CirclePanel mCirclePanel1;
	private CirclePanel mCirclePanel2;
	private CirclePanel mCirclePanel3;
	private CirclePanel mCirclePanel4;

	private boolean mChange = false;
	private boolean mDoingAnimation = false;

	private AnimatorSet mShowSelectedItem;
	private AnimatorSet mHideSelectedItem;

	private int mStartLeft;
	private int mStartRight;

	private int mImgH;
	private int mViewH;
	private int mViewTopMargin;
	private static final int SHOW_VIEW_ANIM_TIME = 300;

	private int mStartY;
	private float mStartScale;

	private MyStatusButton mStatusButton;

	private boolean mDown = false;
	private AnimatorSet mDownAnimator;
	private AnimatorSet mUpAnimator;

	private ImageView mCompareView;

	private boolean mCompareViewEnable = true;
	private boolean mUiEnable = true;

	private CloudAlbumDialog mExitDialog;

	/**
	 * 美颜自动瘦脸
	 */
	private boolean mAutoShrinkFace = false;

	private boolean mGotoSave = false;

	public BeautyPage(Context context, BaseSite site) {
		super(context, site);
		TongJiUtils.onPageStart(context, R.string.美颜);
		MyBeautyStat.onPageStartByRes(R.string.美颜美图_美颜界面_主页面);

		mContext = context;
		mSite = (BeautySite)site;

		filter.deleteAllCacheFilterFile();

		initDatas();
		initViews();
		initListeners();
	}

	@Override
	public void SetData(HashMap<String, Object> params) {
		initParams(params);

		resetData();

		mBeautyView.setImage(mBitmap);

		if (params != null) {
			Object o = params.get(Beautify4Page.PAGE_ANIM_IMG_H);

			if (o instanceof Integer) {
				mImgH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_H);
			if (o instanceof Integer) {
				mViewH = (int)o;
			}

			o = params.get(Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
			if (o instanceof Integer) {
				mViewTopMargin = (int) o;
			}

			if (mImgH > 0 && mViewH > 0) {
				mStartY = (int)(mViewTopMargin + (mViewH - mFrameHeight) / 2f);
				float scaleX = (mFrameWidth-2) * 1f / mBitmap.getWidth();
				float scaleY = mFrameHeight * 1f / mBitmap.getHeight();
				mStartScale = mImgH / (mBitmap.getHeight() * Math.min(scaleX, scaleY));
				showViewAnim();
			} else {
				updateWaitDialog(true, getResources().getString(R.string.loading_img));
				setDatas();
			}
		}
	}

	private void showViewAnim() {

		mDoingAnimation = true;
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator object1 = ObjectAnimator.ofFloat(mBeautyView, "scaleX", mStartScale, 1);
		ObjectAnimator object2 = ObjectAnimator.ofFloat(mBeautyView, "scaleY", mStartScale, 1);
		ObjectAnimator object3 = ObjectAnimator.ofFloat(mBeautyView, "translationY", mStartY, 0);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mRecyclerView, "translationY", mContentHeight + mTopBarHeight, 0);
		ObjectAnimator yAnimator2 = ObjectAnimator.ofFloat(mTopBarLayout, "translationY", mContentHeight + mTopBarHeight, 0);
		animatorSet.setDuration(SHOW_VIEW_ANIM_TIME);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(object1).with(object2).with(object3).with(yAnimator).with(yAnimator2);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;

				updateWaitDialog(true, getResources().getString(R.string.loading_img));

				setDatas();
			}
		});
		animatorSet.start();

	}

	public void resetData() {
		if (TagMgr.CheckTag(mContext, Tags.USE_EFFECT_CLEAR) || !SettingInfoMgr.GetSettingInfo(getContext()).GetLastSaveColor()) {
			TagMgr.SetTag(mContext, Tags.USE_EFFECT_CLEAR);
			mType = EffectType.EFFECT_CLEAR;
		} else {
			mType = BeautifyResMgr2.GetDefColor(TagMgr.GetTagValue(getContext(), Tags.BEAUTY_COLOR));
		}
		int colorAlpha = TagMgr.GetTagIntValue(mContext, Tags.BEAUTY_COLOR_ALPHA, -1);

		//初始化所有颜色透明度
		mColorAlphaMap.clear();
		mColorAlphaMap.put(EffectType.EFFECT_AD62,50);
		mColorAlphaMap.put(EffectType.EFFECT_NATURE, BeautifyResMgr2.getColorTrans(getContext(), EffectType.EFFECT_NATURE));
		mColorAlphaMap.put(EffectType.EFFECT_DEFAULT, BeautifyResMgr2.getColorTrans(getContext(), EffectType.EFFECT_DEFAULT));
		mColorAlphaMap.put(EffectType.EFFECT_MIDDLE, BeautifyResMgr2.getColorTrans(getContext(), EffectType.EFFECT_MIDDLE));
		mColorAlphaMap.put(EffectType.EFFECT_NEWBEE, BeautifyResMgr2.getColorTrans(getContext(), EffectType.EFFECT_NEWBEE));
		mColorAlphaMap.put(EffectType.EFFECT_LITTLE, BeautifyResMgr2.getColorTrans(getContext(), EffectType.EFFECT_LITTLE));
		mColorAlphaMap.put(EffectType.EFFECT_MOONLIGHT, BeautifyResMgr2.getColorTrans(getContext(), EffectType.EFFECT_MOONLIGHT));
		mColorAlphaMap.put(EffectType.EFFECT_CLEAR, BeautifyResMgr2.getColorTrans(getContext(), EffectType.EFFECT_CLEAR));

		if (mType != EffectType.EFFECT_USER && colorAlpha >= 0) {
			mColorAlphaMap.put(mType, colorAlpha);
		}
		int whitening = DEF_LIGHT_VALUE;
		int buffing = DEF_BLUR_VALUE;
		int complexion = DEF_HUE_VALUE;
		String temp = TagMgr.GetTagValue(getContext(), Tags.BEAUTY_COLOR_LIGHT);

		if (temp != null && temp.length() > 0) {
			whitening = Integer.parseInt(temp);
		}
		temp = TagMgr.GetTagValue(getContext(), Tags.BEAUTY_COLOR_BLUR);
		if (temp != null && temp.length() > 0) {
			buffing = Integer.parseInt(temp);
		}
		temp = TagMgr.GetTagValue(getContext(), Tags.BEAUTY_COLOR_HUE);
		if (temp != null && temp.length() > 0) {
			complexion = Integer.parseInt(temp);
		}
		if (whitening >= 0 && buffing >= 0 && complexion >= 0) {
			mWhiteningValue = whitening;
			mBuffingValue = buffing;
			mComplexionValue = complexion;
		} else {
			mWhiteningValue = DEF_LIGHT_VALUE;
			mBuffingValue = DEF_BLUR_VALUE;
			mComplexionValue = DEF_HUE_VALUE;
		}
	}

	private void addBeautyItem(int type, String title) {
		BeautyItem item = new BeautyItem(type, title);

		if (mType == type) {
			item.select = true;
			mPosition = mItems.size();
		}
		mItems.add(item);
	}

	private void setDatas() {

		mItems = new ArrayList<>();
		addBeautyItem(EffectType.EFFECT_NONE, getResources().getString(R.string.beauty_effect_none));
//		boolean isAddLancome = false;
//		AbsAdRes lancomeAd = HomeAd.GetOneHomeRes(getContext(), ChannelValue.AD81_2);
//		if(lancomeAd != null ){
//			long curTime = System.currentTimeMillis();
//			if(curTime >= lancomeAd.mBeginTime && curTime <= lancomeAd.mEndTime ){
//				isAddLancome = true;
//			}
//		}
//		if(isAddLancome)
//		{
//			addBeautyItem(EffectType.EFFECT_AD62, "水嫩肌");
//		}
		addBeautyItem(EffectType.EFFECT_CLEAR, getResources().getString(R.string.beauty_effect_clear));
		addBeautyItem(EffectType.EFFECT_DEFAULT, getResources().getString(R.string.beauty_effect_default));
		addBeautyItem(EffectType.EFFECT_NEWBEE, getResources().getString(R.string.beauty_effect_newbee));
		addBeautyItem(EffectType.EFFECT_LITTLE, getResources().getString(R.string.beauty_effect_little));
		addBeautyItem(EffectType.EFFECT_MIDDLE, getResources().getString(R.string.beauty_effect_middle));
		addBeautyItem(EffectType.EFFECT_NATURE, getResources().getString(R.string.beauty_effect_nature));
		addBeautyItem(EffectType.EFFECT_MOONLIGHT, getResources().getString(R.string.beauty_effect_moonlight));
		addBeautyItem(EffectType.EFFECT_USER, getResources().getString(R.string.beauty_effect_user));

		for (int i = 0; i < mItems.size() - 1; i++) {
			mItems.get(i).thumb = mBitmap;
		}
		mBeautyAdapter = new BeautyAdapter(mContext, mItems);
		mBeautyAdapter.setOnItemCLickListener(mItemClickListener);
		mRecyclerView.setAdapter(mBeautyAdapter);

		BeautyHandler.BeautyMsg beautyMsg = new BeautyHandler.BeautyMsg();
		beautyMsg.type = mType;
		beautyMsg.orgBitmap = mBitmap;
		beautyMsg.colorAlpha = mColorAlphaMap.get(mType, 100);
		beautyMsg.whiteningValue = mWhiteningValue;
		beautyMsg.buffingValue = mBuffingValue;
		beautyMsg.complexionValue = mComplexionValue;
		beautyMsg.autoShrink = mAutoShrinkFace;

		Message msg = mBeautyHandler.obtainMessage();
		msg.what = BeautyHandler.MSG_INIT;
		msg.obj = beautyMsg;
		mBeautyHandler.sendMessage(msg);

		mWhiteningSeekBar.setProgress(mWhiteningValue);
		mBuffingSeekBar.setProgress(mBuffingValue);
		mComplexionSeekBar.setProgress(mComplexionValue);
	}

	private void initDatas() {

		mTopBarHeight = ShareData.PxToDpi_xhdpi(88);
		mContentHeight = ShareData.PxToDpi_xhdpi(232);
		mImagePadding = ShareData.PxToDpi_xhdpi(22);
		mSeekBarHeight = ShareData.PxToDpi_xhdpi(50);
		mCircleRadius = ShareData.PxToDpi_xhdpi(55);

		// 获取图片的最大边长
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(mContext);

		mFrameWidth = ShareData.m_screenWidth;
		mFrameWidth -= mFrameWidth % 2;
		mFrameHeight = ShareData.m_screenHeight - mTopBarHeight - mContentHeight;
		mFrameHeight -= mFrameHeight % 2;

		mFrameWidth += 2;

		mUIHandler = new UIHanlder();
		mHandlerThread = new HandlerThread("beauty_handler_thread");
		mHandlerThread.start();
		mBeautyHandler = new BeautyHandler(mHandlerThread.getLooper(), getContext(), mUIHandler);

		mAutoShrinkFace = SettingInfoMgr.GetSettingInfo(mContext).GetBeautyAutoThinface();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mFrameHeight = ShareData.m_screenHeight - mTopBarHeight - mContentHeight;
		mFrameHeight -= mFrameHeight % 2;
	}

	private void initViews() {

		LayoutParams params;
		mBeautyView = new BeautyCommonViewEx(getContext());
		mBeautyView.SetOnControlListener(null);

		params = new LayoutParams(mFrameWidth, ViewGroup.LayoutParams.MATCH_PARENT);
		params.bottomMargin = mTopBarHeight + mContentHeight;
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		addView(mBeautyView, params);

		mBottomLayout = new FrameLayout(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight + mContentHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mBottomLayout, params);
		{
			mTopBarLayout = new RelativeLayout(mContext);
			mTopBarLayout.setClickable(true);
			mTopBarLayout.setBackgroundColor(0xe6ffffff);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight);
			mBottomLayout.addView(mTopBarLayout, params);
			{
				RelativeLayout.LayoutParams params1;
				mCancelView = new ImageView(mContext);
				mCancelView.setImageResource(R.drawable.beautify_cancel);
				mCancelView.setPadding(mImagePadding, 0, mImagePadding, 0);
				params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
				mTopBarLayout.addView(mCancelView, params1);

				mOkView = new ImageView(mContext);
				mOkView.setImageResource(R.drawable.beautify_ok);
				mOkView.setPadding(mImagePadding, 0, mImagePadding, 0);
				ImageUtils.AddSkin(mContext, mOkView);
				params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
				params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mTopBarLayout.addView(mOkView, params1);

				mStatusButton = new MyStatusButton(mContext);
				mStatusButton.setData(R.drawable.beauty_color_sub_title, getResources().getString(R.string.beauty_sub_title));
				mStatusButton.setBtnStatus(true, false);
				params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
				params1.addRule(RelativeLayout.CENTER_IN_PARENT);
				mTopBarLayout.addView(mStatusButton, params1);
			}

			mRecyclerView = new RecyclerView(mContext);
			mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
			mRecyclerView.setLayoutManager(mLayoutManager);
			mRecyclerView.setPadding(ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(23), ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(23));
			mRecyclerView.setClipToPadding(false);
			mRecyclerView.addItemDecoration(new ListItemDecoration(ShareData.PxToDpi_xhdpi(17), ListItemDecoration.HORIZONTAL));
			// 解决闪屏问题
			((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContentHeight);
			params.gravity = Gravity.BOTTOM;
			mBottomLayout.addView(mRecyclerView, params);

			mControlPanel = new FrameLayout(mContext);
			mControlPanel.setClickable(true);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContentHeight);
			params.gravity = Gravity.BOTTOM;
			mBottomLayout.addView(mControlPanel, params);
			mControlPanel.setTranslationX(ShareData.m_screenWidth);
			{
				LayoutParams params1;
				LinearLayout linearLayout;
				LinearLayout.LayoutParams params2;

				mArrowBack = new ImageView(mContext);
				mArrowBack.setImageResource(R.drawable.beauty_color_btn_back);
				params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params1.gravity = Gravity.CENTER_VERTICAL;
				params1.leftMargin = ShareData.PxToDpi_xhdpi(12);
				mControlPanel.addView(mArrowBack, params1);

				String text = getResources().getString(R.string.beauty_def_buffing);
				int textWidth = 0;

				linearLayout = new LinearLayout(mContext);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mSeekBarHeight);
				params1.leftMargin = ShareData.PxToDpi_xhdpi(120);
				params1.topMargin = ShareData.PxToDpi_xhdpi(25);
				mControlPanel.addView(linearLayout, params1);
				{
					TextView textView = new TextView(mContext);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					textView.setTextColor(0xe6000000);
					textView.setText(R.string.beauty_def_buffing);
					textView.setGravity(Gravity.END);
					textWidth = (int)textView.getPaint().measureText(text) + 1;
					params2 = new LinearLayout.LayoutParams(textWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
					params2.gravity = Gravity.CENTER_VERTICAL;
					linearLayout.addView(textView, params2);

					mBuffingSeekBar = getDefaultSeekBar();
					params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					params2.leftMargin = PercentUtil.WidthPxToPercent(50);
					params2.rightMargin = PercentUtil.WidthPxToPercent(50);
					params2.gravity = Gravity.CENTER_VERTICAL;
					linearLayout.addView(mBuffingSeekBar, params2);
				}

				linearLayout = new LinearLayout(mContext);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mSeekBarHeight);
				params1.leftMargin = ShareData.PxToDpi_xhdpi(120);
				params1.topMargin = ShareData.PxToDpi_xhdpi(93);
				mControlPanel.addView(linearLayout, params1);
				{
					TextView textView = new TextView(mContext);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					textView.setTextColor(0xe6000000);
					textView.setText(R.string.beauty_def_whitening);
					textView.setGravity(Gravity.END);
					params2 = new LinearLayout.LayoutParams(textWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
					params2.gravity = Gravity.CENTER_VERTICAL;
					linearLayout.addView(textView, params2);

					mWhiteningSeekBar = getDefaultSeekBar();
					params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					params2.leftMargin = PercentUtil.WidthPxToPercent(50);
					params2.rightMargin = PercentUtil.WidthPxToPercent(50);
					params2.gravity = Gravity.CENTER_VERTICAL;
					linearLayout.addView(mWhiteningSeekBar, params2);
				}

				linearLayout = new LinearLayout(mContext);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				params1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mSeekBarHeight);
				params1.leftMargin = ShareData.PxToDpi_xhdpi(120);
				params1.topMargin = ShareData.PxToDpi_xhdpi(161);
				mControlPanel.addView(linearLayout, params1);
				{
					TextView textView = new TextView(mContext);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					textView.setTextColor(0xe6000000);
					textView.setText(R.string.beauty_def_complexion);
					textView.setGravity(Gravity.END);
					params2 = new LinearLayout.LayoutParams(textWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
					params2.gravity = Gravity.CENTER_VERTICAL;
					linearLayout.addView(textView, params2);

					mComplexionSeekBar = new ColorSeekBar(mContext);
					mComplexionSeekBar.setBackground(R.drawable.complexion_seekbar_bg);
					mComplexionSeekBar.setProgress(0);
					mComplexionSeekBar.setMax(100);
					mComplexionSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
					params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					params2.leftMargin = PercentUtil.WidthPxToPercent(50);
					params2.rightMargin = PercentUtil.WidthPxToPercent(50);
					params2.gravity = Gravity.CENTER_VERTICAL;
					linearLayout.addView(mComplexionSeekBar, params2);
				}
			}

			mSelectedItem = new ItemView(mContext);
			mSelectedItem.selected.setVisibility(VISIBLE);
			mSelectedItem.selectedIcon.setImageResource(R.drawable.filter_selected_back_icon);
			mSelectedItem.selectedIcon.setVisibility(VISIBLE);
			mSelectedItem.text.setVisibility(View.INVISIBLE);
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(146), ShareData.PxToDpi_xhdpi(187));
			params.leftMargin = ShareData.PxToDpi_xhdpi(30);
			params.bottomMargin = ShareData.PxToDpi_xhdpi(22);
			params.gravity = Gravity.BOTTOM;
			mBottomLayout.addView(mSelectedItem, params);
			mSelectedItem.setVisibility(INVISIBLE);

			mColorAlphaSeekBar = getDefaultSeekBar();
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(430), ViewGroup.LayoutParams.WRAP_CONTENT);
			params.leftMargin = ShareData.PxToDpi_xhdpi(240);
			params.gravity = Gravity.CENTER_VERTICAL;
			mBottomLayout.addView(mColorAlphaSeekBar, params);
			mColorAlphaSeekBar.setVisibility(INVISIBLE);
		}

		mCirclePanel1 = new CirclePanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
		params.gravity = Gravity.BOTTOM;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(248);
		addView(mCirclePanel1, params);

		mCirclePanel2 = new CirclePanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
		params.gravity = Gravity.BOTTOM;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(180);
		addView(mCirclePanel2, params);

		mCirclePanel3 = new CirclePanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
		params.gravity = Gravity.BOTTOM;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(112);
		addView(mCirclePanel3, params);

		mCirclePanel4 = new CirclePanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
		params.gravity = Gravity.BOTTOM;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(180);
		addView(mCirclePanel4, params);

		mCompareView = new ImageView(mContext);
		mCompareView.setPadding(0, ShareData.PxToDpi_xhdpi(10), ShareData.PxToDpi_xhdpi(20), 0);
		mCompareView.setImageResource(R.drawable.beautify_compare);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END;
		addView(mCompareView, params);
		mCompareView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mBeautyView != null && mCompareViewEnable) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_对比按钮);
							MyBeautyStat.onClickByRes(R.string.美颜美图_美颜界面_主页面_对比按钮);
							if (mBitmap != null && mTempCompareBmp == null) {
								mTempCompareBmp = mBeautyView.getImage();
								mBeautyView.setImage(mBitmap);
								mUiEnable = false;
								disableSeekBar(null);
								mColorAlphaSeekBar.setEnabled(false);
							}
							break;

						case MotionEvent.ACTION_UP:
							if (mTempCompareBmp != null) {
								mBeautyView.setImage(mTempCompareBmp);
								mTempCompareBmp = null;
							}
							mUiEnable = true;
							enableSeekBar(null);
							mColorAlphaSeekBar.setEnabled(true);
							break;

						default:
							break;
					}
				}
				return true;
			}
		});
		mCompareView.setVisibility(INVISIBLE);

		mWaitDialog = new WaitAnimDialog((Activity)mContext);
		mWaitDialog.SetGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, mTopBarHeight + mContentHeight + ShareData.PxToDpi_xhdpi(38));

		initAnimator();
	}

	private ColorSeekBar getDefaultSeekBar() {
		ColorSeekBar seekBar = new ColorSeekBar(mContext);
		seekBar.setMax(100);
		seekBar.setProgress(0);
		seekBar.setOnSeekBarChangeListener(mSeekBarListener);

		return seekBar;
	}

	private ColorSeekBar.OnSeekBarChangeListener mSeekBarListener = new ColorSeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(ColorSeekBar seekBar, int progress) {
			if (seekBar == mBuffingSeekBar) {
				showCircle1(seekBar, progress);
			} else if (seekBar == mWhiteningSeekBar) {
				showCircle2(seekBar, progress);
			} else if (seekBar == mComplexionSeekBar) {
				showCircle3(seekBar, progress);
			} else {
				showCircle4(seekBar, progress);
			}
		}

		@Override
		public void onStartTrackingTouch(ColorSeekBar seekBar) {
			disableSeekBar(seekBar);

			if (seekBar == mBuffingSeekBar) {
				showCircle1(seekBar, seekBar.getProgress());
			} else if (seekBar == mWhiteningSeekBar) {
				showCircle2(seekBar, seekBar.getProgress());
			} else if (seekBar == mComplexionSeekBar) {
				showCircle3(seekBar, seekBar.getProgress());
			} else {
				showCircle4(seekBar, seekBar.getProgress());
			}
		}

		@Override
		public void onStopTrackingTouch(ColorSeekBar seekBar) {
			if (seekBar == mBuffingSeekBar) {
				mCirclePanel1.hide();
				mBuffingValue = seekBar.getProgress();
			} else if (seekBar == mWhiteningSeekBar) {
				mCirclePanel2.hide();
				mWhiteningValue = seekBar.getProgress();
			} else if (seekBar == mComplexionSeekBar) {
				mCirclePanel3.hide();
				mComplexionValue = seekBar.getProgress();
			} else {
				mCirclePanel4.hide();
				mColorAlphaMap.put(mType, seekBar.getProgress());
			}

//			if (seekBar != mColorAlphaSeekBar) {
//				mItems.get(mPosition).select = false;
//				mBeautyAdapter.notifyItemChanged(mPosition);
//			}

			sendBeautyMessage();

			enableSeekBar(seekBar);
		}
	};

	//private boolean hasUseLancome = false;      //是否用了兰蔻，传给分享页
	private int lancomeIndex = 0;
	private boolean isShowingLancomeAd = false; //  是否正在显示商业
	private boolean isBeautyFinish = false;   //美颜效果是否完成      1 效果比动画快，延迟显示  2 效果比动画慢，显示loading
	private int lancomeResId[] = new int[]{R.drawable.beauty_effect_lancome_img1,R.drawable.beauty_effect_lancome_img2,R.drawable.beauty_effect_lancome_img3,R.drawable.beauty_effect_lancome_img4,R.drawable.beauty_effect_lancome_img5,R.drawable.beauty_effect_lancome_img6,R.drawable.beauty_effect_lancome_img7,R.drawable.beauty_effect_lancome_img8};
	private int lancomeDelay[] = new int[]{300,300,200,200,300,300,300,300};
	private void showLancomeAdAnim()
	{
		//hasUseLancome = true;
		mUiEnable = false;
		isShowingLancomeAd = true;
		isBeautyFinish = false;
		final ImageView imageView = new ImageView(getContext());
		imageView.setImageResource(lancomeResId[0]);
		LayoutParams fl = new LayoutParams(mFrameWidth, ViewGroup.LayoutParams.MATCH_PARENT);
		fl.bottomMargin = mTopBarHeight + mContentHeight;
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		addView(imageView,fl);
		Runnable LancomeRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				lancomeIndex++;
				if(lancomeIndex < lancomeResId.length){
					imageView.setImageResource(lancomeResId[lancomeIndex]);
					postDelayed(this, lancomeDelay[lancomeIndex]);
				}else{
					lancomeIndex = 0;
					removeView(imageView);
					isShowingLancomeAd = false;
					mUiEnable = true;
					if(!isBeautyFinish){
						updateWaitDialog(true, null);
					}
				}
			}
		};
		postDelayed(LancomeRunnable, lancomeDelay[lancomeIndex]);
	}

	private void disableSeekBar(ColorSeekBar seekBar) {

		if (seekBar == null) {
			mWhiteningSeekBar.setEnabled(false);
			mBuffingSeekBar.setEnabled(false);
			mComplexionSeekBar.setEnabled(false);
		}

		if (seekBar == mWhiteningSeekBar) {
			mBuffingSeekBar.setEnabled(false);
			mComplexionSeekBar.setEnabled(false);
		}

		if (seekBar == mBuffingSeekBar) {
			mWhiteningSeekBar.setEnabled(false);
			mComplexionSeekBar.setEnabled(false);
		}

		if (seekBar == mComplexionSeekBar) {
			mWhiteningSeekBar.setEnabled(false);
			mBuffingSeekBar.setEnabled(false);
		}
	}

	private void enableSeekBar(ColorSeekBar seekBar) {
		mWhiteningSeekBar.setEnabled(true);
		mBuffingSeekBar.setEnabled(true);
		mComplexionSeekBar.setEnabled(true);
	}

	private void showCircle1(ColorSeekBar seekBar, int progress) {
		int seekBarWidth = seekBar.getWidth();
		float circleX = ShareData.PxToDpi_xhdpi(145) + seekBar.getLeft() + progress / 100f * (seekBarWidth - mSeekBarHeight);
		float circleY = mCirclePanel1.getHeight() * 1.0f / 2 - ShareData.PxToDpi_xhdpi(3);
		mCirclePanel1.change(circleX, circleY, mCircleRadius);
		mCirclePanel1.setText(String.valueOf(progress));
		mCirclePanel1.show();
	}

	private void showCircle2(ColorSeekBar seekBar, int progress) {
		int seekBarWidth = seekBar.getWidth();
		float circleX = ShareData.PxToDpi_xhdpi(145) + seekBar.getLeft() + progress / 100f * (seekBarWidth - mSeekBarHeight);
		float circleY = mCirclePanel2.getHeight() * 1.0f / 2 - ShareData.PxToDpi_xhdpi(3);
		mCirclePanel2.change(circleX, circleY, mCircleRadius);
		mCirclePanel2.setText(String.valueOf(progress));
		mCirclePanel2.show();
	}

	private void showCircle3(ColorSeekBar seekBar, int progress) {
		int seekBarWidth = seekBar.getWidth();
		float circleX = ShareData.PxToDpi_xhdpi(145) + seekBar.getLeft() + progress / 100f * (seekBarWidth - mSeekBarHeight);
		float circleY = mCirclePanel3.getHeight() * 1.0f / 2 - ShareData.PxToDpi_xhdpi(3);
		mCirclePanel3.change(circleX, circleY, mCircleRadius);
		mCirclePanel3.setText(String.valueOf(progress - 50));
		mCirclePanel3.show();
	}

	private void showCircle4(ColorSeekBar seekBar, int progress) {
		int seekBarWidth = seekBar.getWidth();
		float circleX = mSeekBarHeight / 2 + seekBar.getLeft() + progress / 100f * (seekBarWidth - mSeekBarHeight);
		float circleY = mCirclePanel4.getHeight() * 1.0f / 2 - ShareData.PxToDpi_xhdpi(3);
		mCirclePanel4.change(circleX, circleY, mCircleRadius);
		mCirclePanel4.setText(String.valueOf(progress));
		mCirclePanel4.show();
	}

	private void initAnimator() {
		mShowUserControl = new AnimatorSet();
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(mRecyclerView, "translationX", 0, -ShareData.m_screenWidth);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(mControlPanel, "translationX", ShareData.m_screenWidth, 0);
		mShowUserControl.play(animator1).with(animator2);
		mShowUserControl.setInterpolator(new AccelerateDecelerateInterpolator());
		mShowUserControl.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;
			}
		});
		mShowUserControl.setDuration(400);

		mHideUserControl = new AnimatorSet();
		animator1 = ObjectAnimator.ofFloat(mRecyclerView, "translationX", -ShareData.m_screenWidth, 0);
		animator2 = ObjectAnimator.ofFloat(mControlPanel, "translationX", 0, ShareData.m_screenWidth);
		mHideUserControl.play(animator1).with(animator2);
		mHideUserControl.setInterpolator(new AccelerateDecelerateInterpolator());
		mHideUserControl.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;
			}
		});
		mHideUserControl.setDuration(400);

		mDownAnimator = new AnimatorSet();
		animator1 = ObjectAnimator.ofFloat(mBottomLayout, "translationY", 0, mContentHeight);
		ValueAnimator animator = ValueAnimator.ofInt(mContentHeight, 0);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				LayoutParams params = (LayoutParams) mBeautyView.getLayoutParams();
				params.bottomMargin = value + mTopBarHeight;
				mBeautyView.requestLayout();
			}
		});
		mDownAnimator.playTogether(animator, animator1);
		mDownAnimator.setDuration(300);
		mDownAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				mBeautyView.InitAnimDate(mFrameWidth, mFrameHeight, mFrameWidth, mFrameHeight + mContentHeight);
				mDoingAnimation = true;
				mStatusButton.setBtnStatus(true, true);
				mDown = true;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;
			}
		});

		mUpAnimator = new AnimatorSet();
		animator1 = ObjectAnimator.ofFloat(mBottomLayout, "translationY", mContentHeight, 0);
		animator = ValueAnimator.ofInt(0, mContentHeight);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer) animation.getAnimatedValue();
				LayoutParams params = (LayoutParams) mBeautyView.getLayoutParams();
				params.bottomMargin = value + mTopBarHeight;
				mBeautyView.requestLayout();
			}
		});
		mUpAnimator.playTogether(animator, animator1);
		mUpAnimator.setDuration(300);
		mUpAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				mBeautyView.InitAnimDate(mFrameWidth, mFrameHeight + mContentHeight, mFrameWidth, mFrameHeight);
				mDoingAnimation = true;
				mStatusButton.setBtnStatus(true, false);
				mDown = false;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mDoingAnimation = false;
			}
		});
	}

	private void initListeners() {

//		mBeautyView.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//
//				switch (event.getActionMasked()) {
//					case MotionEvent.ACTION_DOWN:
//						if (mBeautyView.m_img != null && mBitmap != null && mTempCompareBmp == null && mViewEnable) {
//							mTempCompareBmp = mBeautyView.m_img.m_bmp;
//							mBeautyView.m_img.m_bmp = mBitmap;
//							mCompareViewEnable = false;
//						}
//						break;
//					case MotionEvent.ACTION_POINTER_DOWN:
//					case MotionEvent.ACTION_UP:
//					case MotionEvent.ACTION_CANCEL:
//						if (mTempCompareBmp != null && mBeautyView.m_img != null && mViewEnable) {
//							mBeautyView.m_img.m_bmp = mTempCompareBmp;
//							mTempCompareBmp = null;
//						}
//						mCompareViewEnable = true;
//						break;
//
//					default:
//						break;
//				}
//
//				mBeautyView.UpdateUI();
//				return false;
//			}
//		});

		mRecyclerView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return !mUiEnable;
			}
		});

		mStatusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDoingAnimation || !mUiEnable) {
					return;
				}

				if (mDown) {
					TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_美颜_展开bar);
					MyBeautyStat.onClickByRes(R.string.美颜美图_美颜界面_主页面_展开bar);
					mUpAnimator.start();
				} else {
					TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_美颜_收回bar);
					MyBeautyStat.onClickByRes(R.string.美颜美图_美颜界面_主页面_收回bar);
					mDownAnimator.start();
				}
			}
		});

		mArrowBack.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}

				if (!mDoingAnimation) {
					mDoingAnimation = true;
					mHideUserControl.start();
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mOkView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}

				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_确认);
				MyBeautyStat.onClickByRes(R.string.美颜美图_美颜界面_主页面_确认);

				if (mGotoSave || mType != EffectType.EFFECT_NONE) {
					onSave();
				} else {
					if(mType == EffectType.EFFECT_NONE) {
						TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_无);
						TagMgr.SetTagValue(getContext(), Tags.BEAUTY_COLOR, Integer.toString(EffectType.EFFECT_CLEAR));
						MyBeautyStat.onUseBeautyEffect(MyBeautyStat.EffectType.none, 100);
					}
					cancel();
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mCancelView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}

				onCancel();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mSelectedItem.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}

				hideSelectedItem();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(mContext, R.string.美颜);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(mContext, R.string.美颜);
	}

	@Override
	public void onBack() {
		if (!mUiEnable) {
			return;
		}
		onCancel();
	}

	@Override
	public void onClose() {
		mHandlerThread.quit();
		mBeautyHandler.removeCallbacksAndMessages(null);
		mBeautyHandler.clear();
		mHandlerThread = null;

		mUIHandler.removeCallbacksAndMessages(null);

		if (mWaitDialog != null) {
			mWaitDialog.dismiss();
			mWaitDialog = null;
		}
		clearExitDialog();
		FaceLocalData.ClearData();

		TongJiUtils.onPageEnd(mContext, R.string.美颜);
		MyBeautyStat.onPageEndByRes(R.string.美颜美图_美颜界面_主页面);
	}

	private HashMap<String, Object> getBackAnimParam() {
		HashMap<String, Object> params = new HashMap<>();
		float imgH = mBeautyView.getImgHeight();
		params.put(PAGE_BACK_ANIM_IMG_H, imgH);
		float marginTop = (mBeautyView.getHeight() - mFrameHeight) / 2;
		params.put(PAGE_BACK_ANIM_VIEW_TOP_MARGIN, marginTop);
		return params;
	}

	/**
	 * 更新WaitDialog状态
	 *
	 * @param flag 是否显示
	 */
	private void updateWaitDialog(boolean flag, String title) {

		if (flag) {
			if (mWaitDialog != null) {
				mWaitDialog.show();
			}
		} else {
			if (mWaitDialog != null) {
				mWaitDialog.dismiss();
			}
		}
	}

	private void initParams(Map<String, Object> params) {
		imgs = params.get("imgs");
		if (imgs instanceof RotationImg2[]) {
			mBitmap = ImageUtils.MakeBmp(getContext(), imgs, mFrameWidth, mFrameHeight);
		} else if (imgs instanceof Bitmap) {
			mBitmap = (Bitmap)imgs;
		}

		Object o = params.get("goto_save");
		if (o instanceof Boolean) {
			mGotoSave = (boolean) o;
		}
	}


	private BeautyAdapter.OnItemClickListener mItemClickListener = new BeautyAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {

			if (mDoingAnimation || !mUiEnable) {
				return;
			}

			mType = mItems.get(position).type;
			if (mPosition != position || mType == EffectType.EFFECT_USER) {
				if (mPosition != -1) {
					mItems.get(mPosition).select = false;
				}
				mItems.get(position).select = true;

				mBeautyAdapter.notifyItemChanged(mPosition);
				mBeautyAdapter.notifyItemChanged(position);

				mPosition = position;

				if (mType == EffectType.EFFECT_USER) {
					if (!mDoingAnimation) {
						mDoingAnimation = true;
						mShowUserControl.start();
					}
				} else {
					if(mType == EffectType.EFFECT_AD62){
						showLancomeAdAnim();
						Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0070202409/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
					}
					scrollToCenter(mPosition);
					sendBeautyMessage();
				}
			} else if (mType != EffectType.EFFECT_NONE) {
				mSelectedItem.selectedText.setText(mItems.get(mPosition).title);
				mSelectedItem.image.setImageBitmap(mItems.get(mPosition).thumb);
				mColorAlphaSeekBar.setProgress(mColorAlphaMap.get(mType, 100));

				showSelectedItem();
			}
		}
	};

	private void scrollToCenter(int position) {
		View view = mLayoutManager.findViewByPosition(position);
		if (view != null) {
			float center = mRecyclerView.getWidth() / 2f;
			float viewCenter = view.getX() + view.getWidth() / 2f;
			mRecyclerView.smoothScrollBy((int)(viewCenter - center), 0);
		}
	}

	private void showSelectedItem() {

		if (mDoingAnimation) {
			return;
		}

		mDoingAnimation = true;

		mShowSelectedItem = new AnimatorSet();
		AnimatorSet.Builder builder;

		View view = mLayoutManager.findViewByPosition(mPosition);
		if (view != null) {
			mStartLeft = view.getLeft();
			mStartRight  = view.getRight();

			final int seekBarLeft = mColorAlphaSeekBar.getLeft();
			final int seekBarRight = mColorAlphaSeekBar.getRight();

			int end = mSelectedItem.getLeft();
			builder = mShowSelectedItem.play(ObjectAnimator.ofFloat(view, "translationX", 0, end - mStartLeft));

			int firstIndex = mLayoutManager.findFirstVisibleItemPosition();
			int lastIndex = mLayoutManager.findLastVisibleItemPosition();

			if (firstIndex < mPosition) {
				View leftView;
				for (int i = firstIndex; i < mPosition; i++) {
					leftView = mLayoutManager.findViewByPosition(i);
					if (leftView != null) {
						builder.with(ObjectAnimator.ofFloat(leftView, "translationX", 0, -mStartLeft));
					}
				}
			}

			if (lastIndex > mPosition) {

				View rightView;
				for (int i = mPosition + 1; i <= lastIndex; i++) {
					rightView = mLayoutManager.findViewByPosition(i);
					if (rightView != null) {
						builder.with(ObjectAnimator.ofFloat(rightView, "translationX", 0, mRecyclerView.getWidth() - mStartRight));
					}
				}
			}

			ValueAnimator widthAnimator = ValueAnimator.ofFloat(0, 1);
			widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float value = (Float) animation.getAnimatedValue();

					float moveLeft = (mStartRight - seekBarLeft) * value;
					float moveRight = (seekBarRight - mStartRight) * value;

					LayoutParams params = new LayoutParams((int)(moveLeft + moveRight), ViewGroup.LayoutParams.WRAP_CONTENT);
					params.leftMargin = (int)(seekBarLeft + (mStartRight - seekBarLeft) * (1 - value));
					params.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM;
					params.bottomMargin = ShareData.PxToDpi_xhdpi(41);
					mColorAlphaSeekBar.setLayoutParams(params);
				}
			});

			builder.with(widthAnimator);
			mShowSelectedItem.setInterpolator(new AccelerateDecelerateInterpolator());
			mShowSelectedItem.setDuration(300);
			mShowSelectedItem.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mDoingAnimation = false;
					mRecyclerView.setVisibility(INVISIBLE);
					mSelectedItem.setVisibility(VISIBLE);
				}

				@Override
				public void onAnimationStart(Animator animation) {
					mColorAlphaSeekBar.setVisibility(VISIBLE);
				}
			});
			mShowSelectedItem.start();
		}
	}

	private void hideSelectedItem() {
		if (mDoingAnimation) {
			return;
		}

		mDoingAnimation = true;

		mHideSelectedItem = new AnimatorSet();
		AnimatorSet.Builder builder;

		View view = mLayoutManager.findViewByPosition(mPosition);
		if (view != null) {

			builder = mHideSelectedItem.play(ObjectAnimator.ofFloat(view, "translationX", view.getTranslationX(), 0));

			int firstIndex = mLayoutManager.findFirstVisibleItemPosition();
			int lastIndex = mLayoutManager.findLastVisibleItemPosition();

			if (firstIndex < mPosition) {
				View leftView;
				for (int i = firstIndex; i < mPosition; i++) {
					leftView = mLayoutManager.findViewByPosition(i);
					if (leftView != null) {
						builder.with(ObjectAnimator.ofFloat(leftView, "translationX", leftView.getTranslationX(), 0));
					}
				}
			}

			if (lastIndex > mPosition) {

				View rightView;
				for (int i = mPosition + 1; i <= lastIndex; i++) {
					rightView = mLayoutManager.findViewByPosition(i);
					if (rightView != null) {
						builder.with(ObjectAnimator.ofFloat(rightView, "translationX", rightView.getTranslationX(), 0));
					}
				}
			}

			final int seekBarLeft = mColorAlphaSeekBar.getLeft();
			final int seekBarRight = mColorAlphaSeekBar.getRight();
			final int seekBarWidth = mColorAlphaSeekBar.getWidth();

			ValueAnimator widthAnimator = ValueAnimator.ofFloat(0, 1);
			widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float value = (Float) animation.getAnimatedValue();

					float moveLeft = (mStartRight - seekBarLeft) * value;
					float moveRight = (seekBarRight - mStartRight) * value;

					LayoutParams params = new LayoutParams(seekBarWidth - (int)(moveLeft + moveRight), ViewGroup.LayoutParams.WRAP_CONTENT);
					params.leftMargin = (int)(seekBarLeft + moveLeft);
					params.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM;
					params.bottomMargin = ShareData.PxToDpi_xhdpi(41);
					mColorAlphaSeekBar.setLayoutParams(params);
				}
			});
			builder.with(widthAnimator);
			mHideSelectedItem.setInterpolator(new AccelerateDecelerateInterpolator());
			mHideSelectedItem.setDuration(300);
			mHideSelectedItem.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mColorAlphaSeekBar.setVisibility(INVISIBLE);
					LayoutParams params = new LayoutParams(ShareData.PxToDpi_xhdpi(430), ViewGroup.LayoutParams.WRAP_CONTENT);
					params.leftMargin = ShareData.PxToDpi_xhdpi(240);
					params.gravity = Gravity.CENTER_VERTICAL | Gravity.BOTTOM;
					params.bottomMargin = ShareData.PxToDpi_xhdpi(41);
					mColorAlphaSeekBar.setLayoutParams(params);

					mDoingAnimation = false;
				}

				@Override
				public void onAnimationStart(Animator animation) {
					mRecyclerView.setVisibility(VISIBLE);
					mSelectedItem.setVisibility(INVISIBLE);
				}
			});
			mHideSelectedItem.start();
		}
	}

	private void sendBeautyMessage() {
		mChange = true;
		if(!isShowingLancomeAd)
		{
			updateWaitDialog(true, null);
		}
		BeautyHandler.BeautyMsg obj = new BeautyHandler.BeautyMsg();
		obj.orgBitmap = mBitmap;
		obj.type = mType;
		obj.colorAlpha = mColorAlphaMap.get(mType, 100);
		obj.whiteningValue = mWhiteningValue;
		obj.buffingValue = mBuffingValue;
		obj.complexionValue = mComplexionValue;
		obj.autoShrink = mAutoShrinkFace;

		Message message = mBeautyHandler.obtainMessage();
		message.what = BeautyHandler.MSG_BEAUTY;
		message.obj = obj;
		mBeautyHandler.sendMessage(message);
	}

	private class UIHanlder extends Handler {

		@Override
		public void handleMessage(Message msg) {
			BeautyHandler.BeautyMsg beautyMsg = (BeautyHandler.BeautyMsg)msg.obj;
			msg.obj = null;
			switch (msg.what) {
				case BeautyHandler.MSG_INIT:
					mBeautyView.setImage(beautyMsg.outBitmap);
					beautyMsg.outBitmap = null;
					updateWaitDialog(false, null);
					if (beautyMsg.type != EffectType.EFFECT_NONE) {
						mChange = true;
					}
					mRecyclerView.scrollToPosition(mPosition);
					mRecyclerView.post(new Runnable() {
						@Override
						public void run() {
							scrollToCenter(mPosition);
						}
					});
					break;
				case BeautyHandler.MSG_PUSH_THUMB:
					if (beautyMsg.type == EffectType.EFFECT_MOONLIGHT) {
						setCompareViewState();
					}
					changeAdapter(beautyMsg.thumb, beautyMsg.type);
					beautyMsg.thumb = null;
					break;
				case BeautyHandler.MSG_UPDATE_UI:
					final Bitmap out = beautyMsg.outBitmap;
					beautyMsg.outBitmap = null;
					mChange = beautyMsg.type != EffectType.EFFECT_NONE;
					if(isShowingLancomeAd)
					{
						//还在显示兰蔻动画，延迟显示
						isBeautyFinish = true;
						int delay = 0;
						for (int i = lancomeIndex; i < lancomeDelay.length; i++)
						{
							delay+= lancomeDelay[i];
						}
						postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								upDateUi(out);
							}
						},delay);
					}else{
						upDateUi(out);
					}
					break;

				case BeautyHandler.MSG_SAVE:
//					save(beautyMsg);
					break;
			}
		}
		private void upDateUi(Bitmap out)
		{
			if (mTempCompareBmp != null) {
				mTempCompareBmp = out;
			} else {
				mBeautyView.setImage(out);
			}
			updateWaitDialog(false, null);

			setCompareViewState();
		}
	}

	private void setCompareViewState() {

		if (mType != EffectType.EFFECT_NONE && mCompareView.getVisibility() != VISIBLE) {
			mCompareView.setScaleX(0);
			mCompareView.setScaleY(0);
			mCompareView.setVisibility(VISIBLE);
			mCompareView.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
		} else if (mType == EffectType.EFFECT_NONE && mCompareView.getVisibility() == VISIBLE) {
			mCompareView.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mCompareView.setVisibility(INVISIBLE);
				}
			});
		}
	}

	private void changeAdapter(Bitmap thumb, int type) {
		for (int i = 1; i < mItems.size() - 1; i++) {
			if (mItems.get(i).type == type) {
				mItems.get(i).thumb = thumb;
				mBeautyAdapter.notifyItemChanged(i);
				break;
			}
		}
	}

	private void onCancel() {
		TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_取消);
		MyBeautyStat.onClickByRes(R.string.美颜美图_美颜界面_主页面_取消);
		if (mChange) {
			showExitDialog();
		} else {
			cancel();
		}
	}


	private void showExitDialog()
	{
		if (mExitDialog == null)
		{
			mExitDialog = new CloudAlbumDialog(getContext(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
			mExitDialog.setCancelable(true)
					.setCancelButtonText(R.string.cancel)
					.setOkButtonText(R.string.ensure)
					.setMessage(R.string.confirm_back)
					.setListener(new CloudAlbumDialog.OnButtonClickListener()
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

	private void onSave() {

		//保存自定义
		if (mType == EffectType.EFFECT_USER) {
			TagMgr.SetTagValue(getContext(), Tags.BEAUTY_COLOR_LIGHT, Integer.toString(mWhiteningValue));
			TagMgr.SetTagValue(getContext(), Tags.BEAUTY_COLOR_BLUR, Integer.toString(mBuffingValue));
			TagMgr.SetTagValue(getContext(), Tags.BEAUTY_COLOR_HUE, Integer.toString(mComplexionValue));
		}

		if (SettingInfoMgr.GetSettingInfo(getContext()).GetLastSaveColor()) {
			if (mType == EffectType.EFFECT_NONE) {
				mType = EffectType.EFFECT_DEFAULT;
			}
			TagMgr.SetTagValue(getContext(), Tags.BEAUTY_COLOR, Integer.toString(mType));
			TagMgr.SetTagValue(getContext(), Tags.BEAUTY_COLOR_ALPHA, Integer.toString(mColorAlphaMap.get(mType, 100)));
		}

//		updateWaitDialog(true, getResources().getString(R.string.saving));
//
//		BeautyHandler.BeautyMsg beautyMsg = new BeautyHandler.BeautyMsg();
//		beautyMsg.view = (BeautyView)mBeautyView.Clone();
//		beautyMsg.size = DEF_IMG_SIZE;
//		Message sendMsg = mBeautyHandler.obtainMessage();
//		sendMsg.obj = beautyMsg;
//		sendMsg.what = BeautyHandler.MSG_SAVE;
//		mBeautyHandler.sendMessage(sendMsg);

		save(mBeautyView.getImage());

	}

	private void cancel() {
		HashMap<String, Object> params = new HashMap<>();
		params.putAll(getBackAnimParam());
		params.put("img", mBitmap);
		mSite.OnBack(getContext(), params);
	}

	private void save(Bitmap bitmap) {

		MyBeautyStat.EffectType effectType = null;

		switch (mType) {
			case EffectType.EFFECT_LITTLE:
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_轻微);
				effectType = MyBeautyStat.EffectType.qingwei;
				break;
			case EffectType.EFFECT_MIDDLE:
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_亮白);
				effectType = MyBeautyStat.EffectType.liangbai;
				break;
			case EffectType.EFFECT_DEFAULT:
//				TongJi2.AddCountByRes(getContext(), R.integer.美颜_嫩白);
				effectType = MyBeautyStat.EffectType.nenbai;
				break;
			case EffectType.EFFECT_NATURE:
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_自然);
				effectType = MyBeautyStat.EffectType.ziran;
				break;
			case EffectType.EFFECT_NEWBEE:
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_细节);
				effectType = MyBeautyStat.EffectType.xijie;
				break;
			case EffectType.EFFECT_NONE:
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_无);
				effectType = MyBeautyStat.EffectType.none;
				break;
			case EffectType.EFFECT_USER:
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_自定义);
				effectType = MyBeautyStat.EffectType.zidingyi;
				break;
			case EffectType.EFFECT_MOONLIGHT:
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美颜_效果_朦胧);
				effectType = MyBeautyStat.EffectType.menglong;
				break;
			case EffectType.EFFECT_CLEAR:
				effectType = MyBeautyStat.EffectType.jingbai;
				break;
			default:
				break;
		}

		if (effectType != null) {
			int alpha = mColorAlphaMap.get(mType, 0);
			MyBeautyStat.onUseBeautyEffect(effectType, alpha);
		}

		updateWaitDialog(false, null);

		mSite.m_myParams.put("is_back", true);

		HashMap<String, Object> tempParams = new HashMap<>();
//		if(hasUseLancome){
//			AbsAdRes absAd = HomeAd.GetOneHomeRes(getContext(), ChannelValue.AD81_2);
//			if(absAd != null )
//			{
//				tempParams.put("show_business_banner",absAd.mAdId);
//			}
//		}
		tempParams.putAll(getBackAnimParam());
		tempParams.put("img", bitmap);
		mSite.OnSave(getContext(), tempParams);
	}
}

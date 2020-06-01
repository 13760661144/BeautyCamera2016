package cn.poco.slim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
import java.util.Map;

import cn.poco.acne.view.CirclePanel;
import cn.poco.acne.view.UndoPanel;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.SonWindow;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.ui.FixPointView;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.common.LineData;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.filterPendant.MyStatusButton;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.makeup.ChangePointPage;
import cn.poco.slim.site.SlimSite;
import cn.poco.slim.view.FaceView;
import cn.poco.slim.view.SlimView;
import cn.poco.slim.view.ToolTipDialog;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.SlimViewEx;
import my.beautyCamera.R;

import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_IMG_H;
import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN;

/**
 * Created by: fwc
 * Date: 2016/12/23
 */
public class SlimPage extends IPage {

	private static final int AUTO_DEFAULT_VALUE = 10;

	private Context mContext;

	private SlimSite mSite;

	private int DEF_IMG_SIZE;

	private int mFrameWidth;
	private int mFrameHeight;
	private int mTopBarHeight;
	private int mContentHeight;
	private int mSeekBarHeight;
	private int mImagePadding;
	private int mCircleRadius;
	private int mSeekBarMargin;

	private Bitmap mBitmap;
	private Object imgs;
	private Bitmap mTempCompareBmp = null;

	private SlimViewEx mSlimView;

	private RelativeLayout mTopBarLayout;
	private ImageView mCancelView;
	private ImageView mOkView;

	private MyStatusButton mManualButton;
	private MyStatusButton mAutoButton;

	private int mSelectAction = 0;

	private FrameLayout mContentLayout;
	private ColorSeekBar mManualSeekBar;

	private CirclePanel mCirclePanel;

	private FaceView mOvalFace; // 瓜子脸
	private FaceView mAwlFace; //锤子脸
	private FaceView mEggFace; // 鹅蛋脸
	private FaceView mSelectedFace;
	private ImageView mBackView;
	private ColorSeekBar mAutoSeekBar;

	/**
	 * 用于标记选中哪个脸型
	 * -1: 没选中
	 * 0: 瓜子脸
	 * 1: 锥子脸
	 * 2: 鹅蛋脸
	 */
	private int mSelectFace = -1;

	private Handler mUIHandler;
	private HandlerThread mHandlerThread;
	private SlimHandler mSlimHandler;

	private WaitAnimDialog mWaitDialog;
	private UndoPanel mUndoPanel;

	public ArrayList<LineData> mSlimDatas = new ArrayList<>(); //手动瘦身
	public ArrayList<LineData> mSlimToolDatas = new ArrayList<>(); //瘦身工具

	protected ArrayList<LineData> mTempSlimDatas = new ArrayList<>();
	protected ArrayList<LineData> mTempSlimToolDatas = new ArrayList<>();

	private ActionInfo mActionInfo = new ActionInfo();

	private boolean mResetSlimSize;

	private SonWindow mSonWin;
	private ImageView mCompareView;

//	private ImageView mFixButton;
	private FixPointView mFixButton;
	private FrameLayout mToolButton;
	private ImageView mToolBg;
	private ImageView mToolView;
	private FrameLayout mToolButtonDefault;
	private ImageView mToolBgDefault;
	private ImageView mToolViewDefault;

	private boolean mShowTool = false;

	private boolean mChange = false;
	private CloudAlbumDialog mBackHintDialog;

	private TextView mMutipleFaceDetect;

	private FrameLayout mChangeFaceView;

	private Dialog mNoFaceDialog;

	private float mSeekBarStartX;
	private float mSeekBarEndX;

	private AnimatorSet mUpAnimator;
	private AnimatorSet mDownAnimator;

	private boolean mRunningAnimation = false;

	private int mLastMode = 1;

	private boolean mShowToolTip = false;

	private int mImgH;
	private int mViewH;
	private int mViewTopMargin;
	private static final int SHOW_VIEW_ANIM_TIME = 300;

	private int mStartY;
	private float mStartScale;

	private boolean mDown = false;

	private boolean mClickChangeFaceBtn = false;

	private boolean mUiEnable = true;

	private static final int DEFAULT_PROGRESS = 15;

	private boolean mGotoSave = false;

	public SlimPage(Context context, BaseSite site) {
		super(context, site);
		TongJiUtils.onPageStart(context, R.string.瘦脸瘦身);
		MyBeautyStat.onPageStartByRes(R.string.美颜美图_瘦脸瘦身页_主页面);

		mContext = context;
		mSite = (SlimSite)site;


		initDatas();
		initViews();
		setListeners();
	}

	private void initDatas() {
		DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

		mTopBarHeight = ShareData.PxToDpi_xhdpi(88);
		mContentHeight = ShareData.PxToDpi_xhdpi(232);
		mSeekBarHeight = ShareData.PxToDpi_xhdpi(50);
		mImagePadding = ShareData.PxToDpi_xhdpi(22);
		mCircleRadius = ShareData.PxToDpi_xhdpi(55);
		mSeekBarMargin = PercentUtil.WidthPxToPercent(80);

		mFrameWidth = ShareData.m_screenWidth;
		mFrameWidth -= mFrameWidth % 2;
		mFrameHeight = ShareData.m_screenHeight - mTopBarHeight - mContentHeight;
		mFrameHeight -= mFrameHeight % 2;

		mFrameWidth += 2;

		mUIHandler = new UIHandler();
		mHandlerThread = new HandlerThread("slim_handler_thread");
		mHandlerThread.start();
		mSlimHandler = new SlimHandler(mHandlerThread.getLooper(), getContext(), mUIHandler);

		String temp = TagMgr.GetTagValue(mContext, Tags.SLIM_IS_FIRST_USE_TOOL, "true");
		if ("true".equals(temp)) {
			mShowToolTip = true;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mFrameHeight = ShareData.m_screenHeight - mTopBarHeight - mContentHeight;
		mFrameHeight -= mFrameHeight % 2;
	}

	private void initViews() {

		LayoutParams params;

		mSlimView = new SlimViewEx(getContext());

		mSlimView.def_slim_tool_ab_btn_res = R.drawable.beauty_slim_view_path_out;
		mSlimView.def_slim_tool_r_btn_res = R.drawable.beauty_slim_view_rotate_out;
		mSlimView.def_color = Color.WHITE;
		mSlimView.def_stroke_width = ShareData.PxToDpi_xhdpi(2);
		if (mSlimView.def_stroke_width < 1) {
			mSlimView.def_stroke_width = 1;
		}
		mSlimView.SetOnControlListener(mCallback);
		params = new LayoutParams(mFrameWidth, ViewGroup.LayoutParams.MATCH_PARENT);
		params.bottomMargin = mTopBarHeight + mContentHeight;
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		addView(mSlimView, params);

		mTopBarLayout = new RelativeLayout(mContext);
		mTopBarLayout.setClickable(true);
		mTopBarLayout.setBackgroundColor(0xe6ffffff);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mTopBarHeight);
		params.gravity = Gravity.BOTTOM;
		params.bottomMargin = mContentHeight;
		addView(mTopBarLayout, params);
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

			LinearLayout linearLayout = new LinearLayout(mContext);
			params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params1.addRule(RelativeLayout.CENTER_IN_PARENT);
			mTopBarLayout.addView(linearLayout, params1);
			{
				mManualButton = new MyStatusButton(mContext);
				mManualButton.setBtnStatus(true, false);
				mManualButton.setData(R.drawable.beauty_slim_btn_manual, getResources().getString(R.string.slim_manual));
				LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(180), ViewGroup.LayoutParams.MATCH_PARENT);
				linearLayout.addView(mManualButton, params2);

				mAutoButton = new MyStatusButton(mContext);
				mAutoButton.setData(R.drawable.beauty_slim_btn_auto, getResources().getString(R.string.slim_auto));
				mAutoButton.setBtnStatus(false, false);
				params2 = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(180), ViewGroup.LayoutParams.MATCH_PARENT);
				params2.leftMargin = ShareData.PxToDpi_xhdpi(68);
				linearLayout.addView(mAutoButton, params2);
			}
		}

		mContentLayout = new FrameLayout(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContentHeight);
		params.gravity = Gravity.BOTTOM;
		addView(mContentLayout, params);
		{
			mManualSeekBar = getDefaultSeekBar(95);
			mManualSeekBar.setProgress(DEFAULT_PROGRESS);
//			setManualCircleSize(DEFAULT_PROGRESS);
			mSlimView.setManualCircleSize(DEFAULT_PROGRESS);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			params.leftMargin = params.rightMargin = mSeekBarMargin;
			mContentLayout.addView(mManualSeekBar, params);

			mSelectedFace = new FaceView(mContext);
			mSelectedFace.setVisibility(INVISIBLE);
			mSelectedFace.text.setText(R.string.slim_egg_face);
			mSelectedFace.face.setImageResource(R.drawable.beauty_slim_egg_face);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			params.leftMargin = ShareData.PxToDpi_xhdpi(59);
			mContentLayout.addView(mSelectedFace, params);

			mBackView = new ImageView(mContext);
			mBackView.setVisibility(INVISIBLE);
			mBackView.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(16), 0, ShareData.PxToDpi_xhdpi(16));
			mBackView.setImageResource(R.drawable.slim_select_face_back);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			mContentLayout.addView(mBackView, params);

			mAutoSeekBar = getDefaultSeekBar(100);
			mAutoSeekBar.setVisibility(INVISIBLE);
			mAutoSeekBar.setProgress(0);
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(476), ViewGroup.LayoutParams.WRAP_CONTENT);
			params.leftMargin = ShareData.PxToDpi_xhdpi(194);
			params.gravity = Gravity.CENTER_VERTICAL;
			mContentLayout.addView(mAutoSeekBar, params);

			mOvalFace = new FaceView(mContext);
			mOvalFace.setVisibility(INVISIBLE);
			mOvalFace.text.setText(R.string.slim_oval_face);
			mOvalFace.face.setImageResource(R.drawable.beauty_slim_oval_face);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL;
			params.leftMargin = ShareData.PxToDpi_xhdpi(100);
			mContentLayout.addView(mOvalFace, params);

			mAwlFace = new FaceView(mContext);
			mAwlFace.setVisibility(INVISIBLE);
			mAwlFace.text.setText(R.string.slim_awl_face);
			mAwlFace.face.setImageResource(R.drawable.beauty_slim_awl_face);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mContentLayout.addView(mAwlFace, params);

			mEggFace = new FaceView(mContext);
			mEggFace.setVisibility(INVISIBLE);
			mEggFace.text.setText(R.string.slim_egg_face);
			mEggFace.face.setImageResource(R.drawable.beauty_slim_egg_face);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
			params.rightMargin = ShareData.PxToDpi_xhdpi(100);
			mContentLayout.addView(mEggFace, params);
		}

		mCirclePanel = new CirclePanel(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(120));
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = ShareData.PxToDpi_xhdpi(180);
		addView(mCirclePanel, params);

		mWaitDialog = new WaitAnimDialog((Activity)mContext);
		mWaitDialog.SetGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, mTopBarHeight + mContentHeight + ShareData.PxToDpi_xhdpi(38));

		mCompareView = new ImageView(mContext);
		mCompareView.setPadding(0, ShareData.PxToDpi_xhdpi(10), ShareData.PxToDpi_xhdpi(20), 0);
		mCompareView.setImageResource(R.drawable.beautify_compare);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END;
		addView(mCompareView, params);
		mCompareView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mSlimView != null) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_对比按钮);
							MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_对比按钮);
							mUiEnable = false;
							mManualSeekBar.setEnabled(false);
							mAutoSeekBar.setEnabled(false);
							if (mBitmap != null && mTempCompareBmp == null) {
								mTempCompareBmp = mSlimView.getImage();
								mSlimView.setImage(mBitmap);
							}
							break;

						case MotionEvent.ACTION_UP:
							if (mTempCompareBmp != null) {
								mSlimView.setImage(mTempCompareBmp);
								mTempCompareBmp = null;
							}
							mUiEnable = true;
							mManualSeekBar.setEnabled(true);
							mAutoSeekBar.setEnabled(true);
							break;

						default:
							break;
					}
				}
				return true;
			}
		});
		mCompareView.setVisibility(INVISIBLE);

		initUndoCtrl();

		mFixButton = new FixPointView(mContext);
//		mFixButton.setScaleType(ImageView.ScaleType.CENTER);
//		mFixButton.setBackgroundResource(R.drawable.beautify_white_circle_bg);
//		mFixButton.setImageResource(R.drawable.beautify_fix_by_hand);
//		ImageUtils.AddSkin(mContext, mFixButton);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = ShareData.PxToDpi_xhdpi(24);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(24) + mContentHeight + mTopBarHeight;
		addView(mFixButton, params);
		mFixButton.setVisibility(GONE);

		mToolButton = new FrameLayout(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = ShareData.PxToDpi_xhdpi(120);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(24) + mContentHeight + mTopBarHeight;
		addView(mToolButton, params);
		{
			mToolBg = new ImageView(mContext);
			mToolBg.setImageResource(R.drawable.beauty_slim_btn_tool_bg);
			ImageUtils.AddSkin(mContext, mToolBg);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mToolButton.addView(mToolBg, params);

			mToolView = new ImageView(mContext);
			mToolView.setImageResource(R.drawable.beauty_slim_btn_tools);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mToolButton.addView(mToolView, params);
		}
		mToolButton.setVisibility(INVISIBLE);

		mToolButtonDefault = new FrameLayout(mContext);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.END;
		params.rightMargin = ShareData.PxToDpi_xhdpi(120);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(24) + mContentHeight + mTopBarHeight;
		addView(mToolButtonDefault, params);
		{
			mToolBgDefault = new ImageView(mContext);
			mToolBgDefault.setImageResource(R.drawable.beautify_white_circle_bg);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mToolButtonDefault.addView(mToolBgDefault, params);

			mToolViewDefault = new ImageView(mContext);
			mToolViewDefault.setImageResource(R.drawable.beauty_slim_btn_tools_default);
			ImageUtils.AddSkin(mContext, mToolViewDefault);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			mToolButtonDefault.addView(mToolViewDefault, params);
		}
		mToolButtonDefault.setVisibility(INVISIBLE);

		mToolButton.setTranslationX(ShareData.PxToDpi_xhdpi(104));
		mToolButtonDefault.setTranslationX(ShareData.PxToDpi_xhdpi(104));

		mChangeFaceView = new FrameLayout(mContext);
		mChangeFaceView.setBackgroundResource(R.drawable.beautify_white_circle_bg);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END | Gravity.BOTTOM;
		params.rightMargin = ShareData.PxToDpi_xhdpi(216);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(24) + mContentHeight + mTopBarHeight;
		addView(mChangeFaceView, params);
		{
			ImageView imageView = new ImageView(mContext);
			imageView.setImageResource(R.drawable.beautify_makeup_multiface_icon);
			ImageUtils.AddSkin(mContext, imageView);
			LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params1.gravity = Gravity.CENTER;
			mChangeFaceView.addView(imageView, params1);
		}

		mChangeFaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!mUiEnable) {
					return;
				}

				FaceLocalData faceLocalData = FaceLocalData.getInstance();
				if (faceLocalData != null && faceLocalData.m_faceNum > 1) {
					mClickChangeFaceBtn = true;
					mLastMode = mSlimView.getMode();
					mContentLayout.setVisibility(INVISIBLE);
					mTopBarLayout.setVisibility(INVISIBLE);
					mUndoPanel.hide();
					setCompareViewState(true);
					setScaleAnim(mToolButton, true);
					setScaleAnim(mToolButtonDefault, true);
					mFixButton.setVisibility(INVISIBLE);
					mChangeFaceView.setVisibility(INVISIBLE);
					mMutipleFaceDetect.setVisibility(View.VISIBLE);
					mSlimView.Restore();
				}
			}
		});

		mChangeFaceView.setVisibility(INVISIBLE);

		mSonWin = new SonWindow(getContext());
		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenWidth / 3);
		params.gravity = Gravity.TOP | Gravity.START;
		mSonWin.setLayoutParams(params);
		addView(mSonWin);

		mMutipleFaceDetect = new TextView(mContext);
		mMutipleFaceDetect.setText(R.string.bigeyes_multiple_face_detect);
		mMutipleFaceDetect.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		mMutipleFaceDetect.setTextColor(Color.parseColor("#000000"));
		mMutipleFaceDetect.setGravity(Gravity.CENTER);
		mMutipleFaceDetect.setBackgroundDrawable(getResources().getDrawable(R.drawable.beautifyeyes_multiple_indication));
		params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		params.bottomMargin = mTopBarHeight + mContentHeight + ShareData.PxToDpi_xhdpi(54);
		addView(mMutipleFaceDetect, params);
		mMutipleFaceDetect.setVisibility(View.INVISIBLE);

		initAnimator();
	}

	private void initAnimator() {
		ObjectAnimator contentAnimator = ObjectAnimator.ofFloat(mContentLayout, "translationY", 0, mContentHeight);
		ObjectAnimator topbarAnimator = ObjectAnimator.ofFloat(mTopBarLayout, "translationY", 0, mContentHeight);
		ObjectAnimator undoPanelAnimator = ObjectAnimator.ofFloat(mUndoPanel, "translationY", 0, mContentHeight);
		ObjectAnimator fixBtnAnimator = ObjectAnimator.ofFloat(mFixButton, "translationY", 0, mContentHeight);
		ObjectAnimator toolBtnAnimator = ObjectAnimator.ofFloat(mToolButton, "translationY", 0, mContentHeight);
		ObjectAnimator defaultToolBtnAnimator = ObjectAnimator.ofFloat(mToolButtonDefault, "translationY", 0, mContentHeight);
		ObjectAnimator changeFaceAnimator = ObjectAnimator.ofFloat(mChangeFaceView, "translationY", 0, mContentHeight);
		ValueAnimator slimAnimator = ValueAnimator.ofInt(mContentHeight, 0);
		slimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer)animation.getAnimatedValue();
				LayoutParams params = (LayoutParams)mSlimView.getLayoutParams();
				params.bottomMargin = value + mTopBarHeight;
				mSlimView.requestLayout();
			}
		});
		mDownAnimator = new AnimatorSet();
		mDownAnimator.play(contentAnimator).with(topbarAnimator).with(undoPanelAnimator).with(fixBtnAnimator).with(toolBtnAnimator).with(defaultToolBtnAnimator).with(changeFaceAnimator).with(slimAnimator);
		mDownAnimator.setDuration(400);
		mDownAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mDownAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				mRunningAnimation = false;
				mDown = true;

				if (mShowTool) {
					mSlimView.post(new Runnable() {
						@Override
						public void run() {
							mSlimView.setMode(SlimView.MODE_TOOL);
						}
					});

					if (mShowToolTip) {
						mShowToolTip = false;
						TagMgr.SetTagValue(getContext(), Tags.SLIM_IS_FIRST_USE_TOOL, "false");
						showToolTipDialog();
					}
				}
			}

			@Override
			public void onAnimationStart(Animator animation) {

				mRunningAnimation = true;

				mSlimView.InitAnimDate(mFrameWidth, mFrameHeight, mFrameWidth, mFrameHeight + mContentHeight);

				if (mSelectAction == 0) {
					mManualButton.setBtnStatus(true, true);
				} else {
					mAutoButton.setBtnStatus(true, true);
				}

				if (mShowTool) {
					mToolButtonDefault.setVisibility(INVISIBLE);
					mToolButton.setVisibility(VISIBLE);
					hideAutoButton();
					setUndoPanelState();
				}
			}
		});

		contentAnimator = ObjectAnimator.ofFloat(mContentLayout, "translationY", mContentHeight, 0);
		topbarAnimator = ObjectAnimator.ofFloat(mTopBarLayout, "translationY", mContentHeight, 0);
		undoPanelAnimator = ObjectAnimator.ofFloat(mUndoPanel, "translationY", mContentHeight, 0);
		fixBtnAnimator = ObjectAnimator.ofFloat(mFixButton, "translationY", mContentHeight, 0);
		toolBtnAnimator = ObjectAnimator.ofFloat(mToolButton, "translationY", mContentHeight, 0);
		defaultToolBtnAnimator = ObjectAnimator.ofFloat(mToolButtonDefault, "translationY", mContentHeight, 0);
		changeFaceAnimator = ObjectAnimator.ofFloat(mChangeFaceView, "translationY", mContentHeight, 0);
		slimAnimator = ValueAnimator.ofInt(0, mContentHeight);
		slimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (Integer)animation.getAnimatedValue();
				LayoutParams params = (LayoutParams)mSlimView.getLayoutParams();
				params.bottomMargin = value + mTopBarHeight;
				mSlimView.requestLayout();
			}
		});

		mUpAnimator = new AnimatorSet();
		mUpAnimator.play(contentAnimator).with(topbarAnimator).with(undoPanelAnimator).with(fixBtnAnimator).with(toolBtnAnimator).with(defaultToolBtnAnimator).with(changeFaceAnimator).with(slimAnimator);
		mUpAnimator.setDuration(400);
		mUpAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		mUpAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				mRunningAnimation = true;

				mSlimView.InitAnimDate(mFrameWidth, mFrameHeight + mContentHeight, mFrameWidth, mFrameHeight);

				mToolButtonDefault.setVisibility(VISIBLE);
				mToolButton.setVisibility(INVISIBLE);
				if (mSelectAction == 0) {
					showManualAction(true);
				} else {
					showAutoAction(true);
				}
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mRunningAnimation = false;
				mDown = false;
				if (mSelectAction == 0) {

				} else if (mSelectAction == 1 && FaceLocalData.getInstance() != null && FaceLocalData.getInstance().m_faceNum > 1) {
					mSlimView.DoSelFaceAnim();
				}
				mSlimView.LockUI(false);
			}
		});
	}

	/**
	 * 初始化控制按钮
	 */
	private void initUndoCtrl() {
		mUndoPanel = new UndoPanel(getContext());
		mUndoPanel.setCallback(new UndoPanel.Callback() {
			@Override
			public void onUndo() {
				if (!mUiEnable) {
					return;
				}

				ActionInfo.ActionType type = mActionInfo.getLast();
				if (type != null) {
					if (type == ActionInfo.ActionType.Manual) {
						int len = mSlimDatas.size();
						if (len > 0) {
							mTempSlimDatas.add(mSlimDatas.remove(len - 1));
							sendSlimMessage();
							if (mSlimDatas.isEmpty()) {
								mChange = false;
							}
						}
					} else {
						int len = mSlimToolDatas.size();
						if (len > 0) {
							mTempSlimToolDatas.add(mSlimToolDatas.remove(len - 1));
							sendSlimMessage();
							if (mSlimToolDatas.isEmpty()) {
								mChange = false;
							}
						}
					}
				} else {
					mSlimDatas.clear();
					mSlimToolDatas.clear();
				}
				updateUndoCtrl();
			}

			@Override
			public void onRedo() {
				if (!mUiEnable) {
					return;
				}

				ActionInfo.ActionType type = mActionInfo.getNext();
				if (type != null) {
					if (type == ActionInfo.ActionType.Manual) {
						int len = mTempSlimDatas.size();
						if (len > 0) {
							mSlimDatas.add(mTempSlimDatas.remove(len - 1));
							sendSlimMessage();
						}
					} else {
						int len = mTempSlimToolDatas.size();
						if (len > 0) {
							mSlimToolDatas.add(mTempSlimToolDatas.remove(len - 1));
							sendSlimMessage();
						}
					}
				} else {
					mTempSlimDatas.clear();
					mTempSlimToolDatas.clear();
				}
				updateUndoCtrl();
			}
		});

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		params.leftMargin = ShareData.PxToDpi_xhdpi(24);
		params.bottomMargin = ShareData.PxToDpi_xhdpi(24) + mContentHeight + mTopBarHeight;
		addView(mUndoPanel, params);
		mUndoPanel.setVisibility(INVISIBLE);
	}

	private void updateUndoCtrl() {

		if (mActionInfo.getCurIndex() >= 0 && mActionInfo.getCurIndex() <= mActionInfo.getSize() - 1) {
			if (!mUndoPanel.isCanUndo()) {
				mUndoPanel.setCanUndo(true);
			}
		} else {
			if (mUndoPanel.isCanUndo()) {
				mUndoPanel.setCanUndo(false);
			}
		}

		if (mActionInfo.getCurIndex() >= -1 && mActionInfo.getCurIndex() < mActionInfo.getSize() - 1) {
			if (!mUndoPanel.isCanRedo()) {
				mUndoPanel.setCanRedo(true);
			}
		} else {
			if (mUndoPanel.isCanRedo()) {
				mUndoPanel.setCanRedo(false);
			}
		}
	}

	private void setListeners() {

		mSlimView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return !mUiEnable && (mShowTool || mSelectAction == 0);
			}
		});

		mManualButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mRunningAnimation || !mUiEnable) {
					return;
				}

				if (mShowTool || mDown) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_手动瘦身_展开tab);
					MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_手动瘦身tab_展开tab);
					mSelectAction = 0;
					hideTool();
					return;
				}

				if (mSelectAction != 0) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_手动瘦身_展开tab);
					MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_手动瘦身tab_展开tab);
					showManualAction(false);
				} else if (!mRunningAnimation) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_手动瘦身_收回tab);
					MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_手动瘦身tab_收回tab);
					mDownAnimator.start();
				}
			}
		});

		mAutoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mRunningAnimation || !mUiEnable) {
					return;
				}

				if (mShowTool || mDown) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_自动瘦身_展开tab);
					MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_自动瘦身tab_展开tab);
					mSelectAction = 1;
					hideTool();
					return;
				}

				if (mSelectAction != 1) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_自动瘦身_展开tab);
					MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_自动瘦身tab_展开tab);
					showAutoAction(false);
				} else if (!mRunningAnimation) {
					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_自动瘦身_收回tab);
					MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_自动瘦身tab_收回tab);

					mDownAnimator.start();
				}
			}
		});

		mOvalFace.setOnTouchListener(onFaceClickListener);
		mAwlFace.setOnTouchListener(onFaceClickListener);
		mEggFace.setOnTouchListener(onFaceClickListener);

		mBackView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}
				closeSelectedFaceAnimation();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mSelectedFace.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}
				closeSelectedFaceAnimation();
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mFixButton.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}
				mFixButton.modifyStatus();
				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_手动定点);
				MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_手动定点);
				HashMap<String, Object> params = new HashMap<>();
				params.put("imgs", imgs);
				params.put("type", 5);
				params.put("index", FaceDataV2.sFaceIndex);
				mSite.openFixPage(getContext(), params);
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mToolButton.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}

				if (mShowTool) {
					hideTool();
				}
			}

			@Override
			public void onTouch(View v) {

			}

			@Override
			public void onRelease(View v) {

			}
		});

		mToolButtonDefault.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (!mUiEnable) {
					return;
				}

				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_瘦脸工具);
				MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_瘦脸工具);
				if (!mDown) {
					showTool();
				} else {
					mUpAnimator.start();
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

				if (mGotoSave || mChange) {

					TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_确认);
					MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_确认);

					boolean useHand = !mSlimDatas.isEmpty();
					int handValue = mManualSeekBar.getProgress() + 5;
					boolean useTool = !mSlimToolDatas.isEmpty();

					MyBeautyStat.FaceType faceType = null;
					if (mSelectFace == 0) {
						faceType = MyBeautyStat.FaceType.瓜子脸;
					} else if (mSelectFace == 1) {
						faceType = MyBeautyStat.FaceType.锥子脸;
					} else if (mSelectFace == 2) {
						faceType = MyBeautyStat.FaceType.鹅蛋脸;
					}
					int faceValue = mAutoSeekBar.getProgress();
					MyBeautyStat.onSaveSlim(useHand, handValue, useTool, faceType, faceValue);

					HashMap<String, Object> tempParams = new HashMap<>();
					tempParams.put("img", mSlimView.getImage());
					tempParams.putAll(getBackAnimParam());
					mSite.OnSave(getContext(), tempParams);

				} else {
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
	}

	private void showTool() {

		if (mRunningAnimation) {
			return;
		}
		mRunningAnimation = false;

		mShowTool = true;
		mDownAnimator.start();
	}

	private void showToolTipDialog() {
		ToolTipDialog dialog = new ToolTipDialog(mContext);
		dialog.show();
	}

	private void hideTool() {

		if (mRunningAnimation) {
			return;
		}
		mRunningAnimation = true;
		mShowTool = false;
		mUpAnimator.start();
	}

	private void showManualAction(boolean anim) {
		hideAutoButton();
		mSelectAction = 0;

		mManualButton.setBtnStatus(true, false);
		mAutoButton.setBtnStatus(false, false);

		mManualSeekBar.setVisibility(VISIBLE);
		mSelectFace = -1;
		mOvalFace.setVisibility(INVISIBLE);
		mAwlFace.setVisibility(INVISIBLE);
		mEggFace.setVisibility(INVISIBLE);

		mBackView.setVisibility(INVISIBLE);
		mSelectedFace.setVisibility(INVISIBLE);
		mAutoSeekBar.setVisibility(INVISIBLE);
		setUndoPanelState();

		mSlimView.setMode(SlimView.MODE_MANUAL);
	}

	private void showAutoAction(boolean anim) {

		mSelectAction = 1;

		mManualButton.setBtnStatus(false, false);
		mAutoButton.setBtnStatus(true, false);

		mOvalFace.setTranslationX(0);
		mAwlFace.setTranslationX(0);
		mEggFace.setTranslationX(0);

		mManualSeekBar.setVisibility(INVISIBLE);
		mOvalFace.setVisibility(VISIBLE);
		mAwlFace.setVisibility(VISIBLE);
		mEggFace.setVisibility(VISIBLE);
		mBackView.setVisibility(INVISIBLE);
		mSelectedFace.setVisibility(INVISIBLE);
		mAutoSeekBar.setVisibility(INVISIBLE);
		mUndoPanel.hide();

		mSlimView.setMode(SlimView.MODE_AUTO);

		initFace(anim);
	}

	private void showAutoButton(boolean hasNotAnim) {
		if (mFixButton.getVisibility() == VISIBLE) {
			return;
		}

		if (hasNotAnim) {
			mFixButton.setVisibility(VISIBLE);
			mChangeFaceView.setVisibility(VISIBLE);
			return;
		}

		mToolButton.animate().translationX(0).setDuration(400).setListener(null);
		mToolButtonDefault.animate().translationX(0).setDuration(400).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mRunningAnimation = false;
				mFixButton.setScaleX(0);
				mFixButton.setScaleY(0);
				mFixButton.setVisibility(VISIBLE);
				mFixButton.animate().scaleX(1).scaleY(1).setDuration(100);
				FaceLocalData faceLocalData = FaceLocalData.getInstance();
				if (faceLocalData != null && faceLocalData.m_faceNum > 1) {
					mChangeFaceView.setScaleX(0);
					mChangeFaceView.setScaleY(0);
					mChangeFaceView.setVisibility(VISIBLE);
					mChangeFaceView.animate().scaleX(1).scaleY(1).setDuration(100);
				}
			}

			@Override
			public void onAnimationStart(Animator animation) {
				mRunningAnimation = true;
			}
		});
	}

	private void hideAutoButton() {
		mFixButton.setVisibility(GONE);
		mChangeFaceView.setVisibility(GONE);
		mToolButton.animate().translationX(ShareData.PxToDpi_xhdpi(104)).setDuration(400).setListener(null);
		mToolButtonDefault.animate().translationX(ShareData.PxToDpi_xhdpi(104)).setDuration(400).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mRunningAnimation = false;
			}

			@Override
			public void onAnimationStart(Animator animation) {
				mRunningAnimation = true;
			}
		});
	}

	private ColorSeekBar getDefaultSeekBar(int maxValue) {
		ColorSeekBar seekBar = new ColorSeekBar(mContext);
		seekBar.setMax(maxValue);
		seekBar.setOnSeekBarChangeListener(mSeekBarListener);

		return seekBar;
	}

	private ColorSeekBar.OnSeekBarChangeListener mSeekBarListener = new ColorSeekBar.OnSeekBarChangeListener() {


		@Override
		public void onProgressChanged(ColorSeekBar seekBar, int progress) {
			showCircle(seekBar, progress);

			if (seekBar == mManualSeekBar) {
				setManualCircleSize(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(ColorSeekBar seekBar) {
			showCircle(seekBar, seekBar.getProgress());

			if (seekBar == mManualSeekBar) {
				TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_瘦脸瘦身_手动瘦身_滑动杆);
				setManualCircleSize(seekBar.getProgress());
			} else if (seekBar == mAutoSeekBar) {
				switch (mSelectFace) {
					case 0:
						TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_瘦脸瘦身_瓜子脸_滑动杆);
						break;
					case 1:
						TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_瘦脸瘦身_锥子脸_滑动杆);
						break;
					case 2:
						TongJi2.AddCountByRes(mContext, R.integer.修图_美颜美形_瘦脸瘦身_鹅蛋脸_滑动杆);
						break;
				}
			}
		}

		@Override
		public void onStopTrackingTouch(ColorSeekBar seekBar) {
			mCirclePanel.hide();
			mChange = true;
			if (seekBar == mAutoSeekBar) {
				// 处理
				FaceLocalData faceLocalData = FaceLocalData.getInstance();
				if (faceLocalData != null) {
					faceLocalData.m_faceLevelMap_multi[FaceDataV2.sFaceIndex].put(faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex], seekBar.getProgress());
					sendSlimMessage();
				}
			}
		}
	};

	private void setManualCircleSize(int value) {

		if (value < 0 || value > 100) {
			value = 100;
		}

		if (mSlimView != null) {
			float scale = (value / 2f + 100) / 100f;
			if (scale < 1) {
				scale = 1;
			} else if (scale > 1.5) {
				scale = 1.5f;
			}
			mSlimView.SetSlimDragRScale(scale);
		}
	}

	private OnTouchListener onFaceClickListener = new OnAnimationClickListener() {
		@Override
		public void onAnimationClick(View v) {

			if (!mUiEnable) {
				return;
			}

			int drawableId = 0, stringId = 0;

			FaceLocalData faceLocalData = FaceLocalData.getInstance();

			if (faceLocalData == null) return;

			if (v == mOvalFace && faceLocalData.m_faceType_multi != null && faceLocalData.m_faceType_multi.length > 0) {
				mSelectFace = 0;
				faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex] = FaceType.FACE_OVAL;

				stringId = R.string.slim_oval_face;
				drawableId = R.drawable.beauty_slim_oval_face;

				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_瓜子脸);
				MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_瓜子脸);

			} else if (v == mAwlFace && faceLocalData.m_faceType_multi != null && faceLocalData.m_faceType_multi.length > 0) {
				mSelectFace = 1;
				faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex] = FaceType.FACE_AWL;
				stringId = R.string.slim_awl_face;
				drawableId = R.drawable.beauty_slim_awl_face;

				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_锥子脸);
				MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_锥子脸);

			} else if (v == mEggFace && faceLocalData.m_faceType_multi != null && faceLocalData.m_faceType_multi.length > 0) {
				mSelectFace = 2;
				faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex] = FaceType.FACE_EGG;
				stringId = R.string.slim_egg_face;
				drawableId = R.drawable.beauty_slim_egg_face;

				TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_鹅蛋脸);
				MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_鹅蛋脸);
			}

			if (drawableId != 0 && stringId != 0) {
				mSelectedFace.face.setImageResource(drawableId);
				mSelectedFace.text.setText(stringId);
			}

			openSelectedFaceAnimation();
		}

		@Override
		public void onTouch(View v) {

		}

		@Override
		public void onRelease(View v) {

		}
	};

	private void openSelectedFaceAnimation() {

		if (mRunningAnimation) {
			return;
		}

		mRunningAnimation = true;

		ObjectAnimator xAnimator1 = null;
		ObjectAnimator xAnimator2 = null;
		ObjectAnimator xAnimator3 = null;

		if (mSelectFace == 0) {

			float startX = mOvalFace.getX();
			mSeekBarStartX = mOvalFace.getRight();
			float endX = ShareData.PxToDpi_xhdpi(59);

			float otherX = mAwlFace.getX();

			xAnimator1 = ObjectAnimator.ofFloat(mOvalFace, "translationX", 0, endX - startX);
			xAnimator2 = ObjectAnimator.ofFloat(mAwlFace, "translationX", 0, mFrameWidth - otherX);
			xAnimator3 = ObjectAnimator.ofFloat(mEggFace, "translationX", 0, mFrameWidth - otherX);

		} else if (mSelectFace == 1) {
			float startX = mAwlFace.getX();
			mSeekBarStartX = mAwlFace.getRight();
			float endX = ShareData.PxToDpi_xhdpi(59);

			xAnimator1 = ObjectAnimator.ofFloat(mAwlFace, "translationX", 0, endX - startX);
			xAnimator2 = ObjectAnimator.ofFloat(mOvalFace, "translationX", 0, -mOvalFace.getRight());
			xAnimator3 = ObjectAnimator.ofFloat(mEggFace, "translationX", 0, mFrameWidth - mEggFace.getLeft());

		} else if (mSelectFace == 2) {
			float startX = mEggFace.getX();
			mSeekBarStartX = mEggFace.getRight();
			float endX = ShareData.PxToDpi_xhdpi(59);

			xAnimator1 = ObjectAnimator.ofFloat(mEggFace, "translationX", 0, endX - startX);
			xAnimator2 = ObjectAnimator.ofFloat(mOvalFace, "translationX", 0, endX - startX);
			xAnimator3 = ObjectAnimator.ofFloat(mAwlFace, "translationX", 0, endX - startX);
		}

		final int seekBarLeft = mAutoSeekBar.getLeft();
		final int seekBarRight = mAutoSeekBar.getRight();

		ValueAnimator widthAnimator = ValueAnimator.ofFloat(0, 1);
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float)animation.getAnimatedValue();

				float moveLeft = (mSeekBarStartX - seekBarLeft) * value;
				float moveRight = (seekBarRight - mSeekBarStartX) * value;

				LayoutParams params = new LayoutParams((int)(moveLeft + moveRight), ViewGroup.LayoutParams.WRAP_CONTENT);
				params.leftMargin = (int)(seekBarLeft + (mSeekBarStartX - seekBarLeft) * (1 - value));
				params.gravity = Gravity.CENTER_VERTICAL;
				mAutoSeekBar.setLayoutParams(params);
			}
		});

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(xAnimator1).with(xAnimator2).with(xAnimator3).with(widthAnimator);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.setDuration(300);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {

				mOvalFace.setVisibility(INVISIBLE);
				mAwlFace.setVisibility(INVISIBLE);
				mEggFace.setVisibility(INVISIBLE);

				mBackView.setVisibility(VISIBLE);
				mSelectedFace.setVisibility(VISIBLE);

				mRunningAnimation = false;

//				FaceLocalData faceLocalData = FaceLocalData.getInstance();
//				int lastValue = faceLocalData.m_faceLevelMap_multi[FaceDataV2.sFaceIndex].get(faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex]);

				sendSlimMessage();

			}

			@Override
			public void onAnimationStart(Animator animation) {

				FaceLocalData faceLocalData = FaceLocalData.getInstance();
				int lastValue = faceLocalData.m_faceLevelMap_multi[FaceDataV2.sFaceIndex].get(faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex]);

				mAutoSeekBar.setProgress(lastValue);
				mAutoSeekBar.setVisibility(VISIBLE);
			}
		});
		animatorSet.start();
	}

	private void closeSelectedFaceAnimation() {

		if (mRunningAnimation) {
			return;
		}
		mRunningAnimation = true;

		ObjectAnimator xAnimator1;
		ObjectAnimator xAnimator2;
		ObjectAnimator xAnimator3;

		if (mSelectFace == 0) {
			mSeekBarEndX = mOvalFace.getRight();
		} else if (mSelectFace == 1) {
			mSeekBarEndX = mAwlFace.getRight();
		} else if (mSelectFace == 2) {
			mSeekBarEndX = mEggFace.getRight();
		}

		xAnimator1 = ObjectAnimator.ofFloat(mOvalFace, "translationX", mOvalFace.getTranslationX(), 0);
		xAnimator2 = ObjectAnimator.ofFloat(mAwlFace, "translationX", mAwlFace.getTranslationX(), 0);
		xAnimator3 = ObjectAnimator.ofFloat(mEggFace, "translationX", mEggFace.getTranslationX(), 0);

		final int seekBarLeft = mAutoSeekBar.getLeft();
		final int seekBarRight = mAutoSeekBar.getRight();
		final int seekBarWidth = mAutoSeekBar.getWidth();

		ValueAnimator widthAnimator = ValueAnimator.ofFloat(0, 1);
		widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float)animation.getAnimatedValue();

				float moveLeft = (mSeekBarEndX - seekBarLeft) * value;
				float moveRight = (seekBarRight - mSeekBarEndX) * value;

				LayoutParams params = new LayoutParams(seekBarWidth - (int)(moveLeft + moveRight), ViewGroup.LayoutParams.WRAP_CONTENT);
				params.leftMargin = (int)(seekBarLeft + moveLeft);
				params.gravity = Gravity.CENTER_VERTICAL;
				mAutoSeekBar.setLayoutParams(params);
			}
		});

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(xAnimator1).with(xAnimator2).with(xAnimator3).with(widthAnimator);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.setDuration(300);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mAutoSeekBar.setVisibility(INVISIBLE);
				LayoutParams params = new LayoutParams(ShareData.PxToDpi_xhdpi(476), ViewGroup.LayoutParams.WRAP_CONTENT);
				params.leftMargin = ShareData.PxToDpi_xhdpi(194);
				params.gravity = Gravity.CENTER_VERTICAL;
				mAutoSeekBar.setLayoutParams(params);

				mRunningAnimation = false;
			}

			@Override
			public void onAnimationStart(Animator animation) {
				mSelectFace = -1;
				mBackView.setVisibility(INVISIBLE);
				mSelectedFace.setVisibility(INVISIBLE);

				mOvalFace.setVisibility(VISIBLE);
				mAwlFace.setVisibility(VISIBLE);
				mEggFace.setVisibility(VISIBLE);
			}
		});
		animatorSet.start();
	}

	private void showCircle(ColorSeekBar seekBar, int progress) {
		int seekBarWidth = seekBar.getWidth();
		float max = seekBar == mManualSeekBar ? 95f : 100f;
		float circleX = seekBar.getLeft() + mSeekBarHeight / 2 + progress / max * (seekBarWidth - mSeekBarHeight);
		float circleY = mCirclePanel.getHeight() * 1.0f / 2 - ShareData.PxToDpi_xhdpi(3);
		mCirclePanel.change(circleX, circleY, mCircleRadius);
		if (seekBar == mManualSeekBar) {
			progress += 5;
		}
		mCirclePanel.setText(String.valueOf(progress));
		mCirclePanel.show();
	}

	@Override
	public void SetData(HashMap<String, Object> params) {
		initParams(params);

		mSlimView.setImage(mBitmap);

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
				mViewTopMargin = (int)o;
			}

			if (mImgH > 0 && mViewH > 0) {
				mStartY = (int)(mViewTopMargin + (mViewH - mFrameHeight) / 2f);
				float scaleX = (mFrameWidth - 2) * 1f / mBitmap.getWidth();
				float scaleY = mFrameHeight * 1f / mBitmap.getHeight();
				mStartScale = mImgH / (mBitmap.getHeight() * Math.min(scaleX, scaleY));
				showViewAnim();
			} else {
				setScaleAnim(mToolButtonDefault, false);
			}
		}
	}

	private void showViewAnim() {

		mRunningAnimation = true;
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator object1 = ObjectAnimator.ofFloat(mSlimView, "scaleX", mStartScale, 1);
		ObjectAnimator object2 = ObjectAnimator.ofFloat(mSlimView, "scaleY", mStartScale, 1);
		ObjectAnimator object3 = ObjectAnimator.ofFloat(mSlimView, "translationY", mStartY, 0);
		ObjectAnimator yAnimator = ObjectAnimator.ofFloat(mContentLayout, "translationY", mContentHeight + mTopBarHeight, 0);
		ObjectAnimator yAnimator2 = ObjectAnimator.ofFloat(mTopBarLayout, "translationY", mContentHeight + mTopBarHeight, 0);
		animatorSet.setDuration(SHOW_VIEW_ANIM_TIME);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		animatorSet.play(object1).with(object2).with(object3).with(yAnimator).with(yAnimator2);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mRunningAnimation = false;

				setScaleAnim(mToolButtonDefault, false);
			}
		});
		animatorSet.start();
	}

	private void initFace(boolean anim) {

		if (!FaceDataV2.CHECK_FACE_SUCCESS) {
			updateWaitDialog(true, null);

			Message message = mSlimHandler.obtainMessage();
			SlimHandler.SlimData data = new SlimHandler.SlimData();
			data.orgBitmap = mBitmap;
			message.obj = data;
			message.what = SlimHandler.MSG_INIT;
			mSlimHandler.sendMessage(message);
		} else {
			if (FaceLocalData.getInstance() == null) {
				//初始化人脸识别
				FaceLocalData.getNewInstance(FaceDataV2.FACE_POS_MULTI.length);
				setAutoDefaultValue();
			}

			if (FaceLocalData.getInstance().m_faceNum > 1) {
				// 多人检测
				if (FaceDataV2.sFaceIndex != -1) {
					mSlimView.m_faceIndex = FaceDataV2.sFaceIndex;
					if (!anim) {
						mSlimView.DoSelFaceAnim();
					}
					showAutoButton(false);
				} else {
					mContentLayout.setVisibility(INVISIBLE);
					mTopBarLayout.setVisibility(INVISIBLE);
					mUndoPanel.hide();
					setCompareViewState(true);
					setScaleAnim(mToolButton, true);
					setScaleAnim(mToolButtonDefault, true);
					mFixButton.setVisibility(INVISIBLE);
					mChangeFaceView.setVisibility(INVISIBLE);
					mMutipleFaceDetect.setVisibility(View.VISIBLE);

					mLastMode = SlimView.MODE_AUTO;
					mSlimView.m_faceIndex = -1;
					mSlimView.Restore();
				}
			} else {
				showAutoButton(false);
			}
			if(mFixButton != null)
			{
				mFixButton.showJitterAnimAccordingStatus();
			}
		}
	}

	private void initParams(Map<String, Object> params) {
		imgs = params.get("imgs");
		if (imgs instanceof RotationImg2[]) {
			//mBitmap = ImageUtils.MakeBmp(getContext(), imgs, mFrameWidth, mFrameHeight);
			mBitmap = ImageUtils.MakeBmp(getContext(), imgs, DEF_IMG_SIZE, DEF_IMG_SIZE);
		} else if (imgs instanceof Bitmap) {
			mBitmap = (Bitmap)imgs;
		}

		Object o = params.get("goto_save");
		if (o instanceof Boolean) {
			mGotoSave = (boolean) o;
		}
	}

	private void sendSlimMessage() {

		mChange = true;
		updateWaitDialog(true, null);

		SlimHandler.SlimData data = new SlimHandler.SlimData();
		data.orgBitmap = mBitmap;
		if (FaceLocalData.getInstance() != null) {
			data.faceLocalData = FaceLocalData.getInstance().Clone();
		}
		data.slimDatas = mSlimDatas;
		data.slimToolDatas = mSlimToolDatas;

		Message message = mSlimHandler.obtainMessage();
		message.what = SlimHandler.MSG_SLIM;
		message.obj = data;
		mSlimHandler.sendMessage(message);
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
				mWaitDialog.hide();
			}
		}
	}

	@Override
	public void onClose() {

		mSlimHandler.removeCallbacksAndMessages(null);
		mSlimHandler.clear();
		mHandlerThread.quit();
		mHandlerThread = null;

		mUIHandler.removeCallbacksAndMessages(null);

		if(mFixButton != null)
		{
			mFixButton.clearAll();
		}

		if (mBackHintDialog != null)
		{
			mBackHintDialog.dismiss();
			mBackHintDialog.setListener(null);
			mBackHintDialog = null;
		}

		if (mWaitDialog != null) {
			mWaitDialog.dismiss();
			mWaitDialog = null;
		}

		if (mNoFaceDialog != null) {
			mNoFaceDialog.dismiss();
			mNoFaceDialog = null;
		}

		if (mUpAnimator != null) {
			mUpAnimator.removeAllListeners();
			mUpAnimator.cancel();
		}

		if (mDownAnimator != null) {
			mDownAnimator.removeAllListeners();
			mDownAnimator.cancel();
		}

		FaceLocalData.ClearData();

		TongJiUtils.onPageEnd(mContext, R.string.瘦脸瘦身);
		MyBeautyStat.onPageEndByRes(R.string.美颜美图_瘦脸瘦身页_主页面);
	}

	@Override
	public void onResume() {
		TongJiUtils.onPageResume(mContext, R.string.瘦脸瘦身);
	}

	@Override
	public void onPause() {
		TongJiUtils.onPagePause(mContext, R.string.瘦脸瘦身);
	}

	@Override
	public void onBack() {

		if (!mUiEnable) {
			return;
		}

		if (mSelectFace != -1) {
			closeSelectedFaceAnimation();
		} else {
			onCancel();
		}
	}

	public void onCancel() {
		TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_瘦脸瘦身_取消);
		MyBeautyStat.onClickByRes(R.string.美颜美图_瘦脸瘦身页_主页面_取消);
		if (mChange) {
			if (mBackHintDialog == null) {
				mBackHintDialog = new CloudAlbumDialog(mContext, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ImageUtils.AddSkin(mContext, mBackHintDialog.getOkButtonBg());
				mBackHintDialog.setCancelButtonText(R.string.cancel).setOkButtonText(R.string.ensure).setMessage(R.string.confirm_back).setListener(new CloudAlbumDialog.OnButtonClickListener() {
					@Override
					public void onOkButtonClick() {
						mBackHintDialog.dismiss();
						cancel();
					}

					@Override
					public void onCancelButtonClick() {
						mBackHintDialog.dismiss();
					}
				});
			}
			mBackHintDialog.show();
		} else {
			cancel();
		}
	}

	private void cancel() {
		mUiEnable = false;
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", mBitmap);
		params.putAll(getBackAnimParam());
		mSite.onBack(getContext(), params);
	}

	private HashMap<String, Object> getBackAnimParam() {
		HashMap<String, Object> params = new HashMap<>();
		float imgH = mSlimView.getImgHeight();
		params.put(PAGE_BACK_ANIM_IMG_H, imgH);
		float marginTop = (mSlimView.getHeight() - mFrameHeight) / 2;
		params.put(PAGE_BACK_ANIM_VIEW_TOP_MARGIN, marginTop);
		return params;
	}


	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			SlimHandler.SlimData data = (SlimHandler.SlimData)msg.obj;
			msg.obj = null;
			switch (msg.what) {
				case SlimHandler.MSG_INIT:
					updateWaitDialog(false, null);

					if (!FaceDataV2.CHECK_FACE_SUCCESS) {
						// 人脸检测失败
						if (mNoFaceDialog == null) {
							mNoFaceDialog = CommonUI.MakeNoFaceHelpDlg((Activity)mContext, new OnClickListener() {
								@Override
								public void onClick(View v) {
									if(mFixButton != null)
									{
										mFixButton.modifyStatus();
									}
									mNoFaceDialog.dismiss();
									HashMap<String, Object> params = new HashMap<>();
									params.put("imgs", imgs);
									params.put("type", 5);
									params.put("index", 0);
									mSite.openFixPage(getContext(), params);
								}
							});
						}
						if (mUiEnable) {
							mNoFaceDialog.show();
						}
						return;
					} else if (FaceLocalData.getInstance().m_faceNum > 1) {

						if (FaceDataV2.sFaceIndex != -1) {
							mSlimView.m_faceIndex = FaceDataV2.sFaceIndex;
							mSlimView.DoSelFaceAnim();
							mChangeFaceView.setVisibility(VISIBLE);
						} else {
							mContentLayout.setVisibility(INVISIBLE);
							mTopBarLayout.setVisibility(INVISIBLE);
							mUndoPanel.hide();
							setCompareViewState(true);
							setScaleAnim(mToolButton, true);
							setScaleAnim(mToolButtonDefault, true);
							mFixButton.setVisibility(INVISIBLE);
							mChangeFaceView.setVisibility(INVISIBLE);
							mMutipleFaceDetect.setVisibility(View.VISIBLE);

							mLastMode = SlimView.MODE_AUTO;
							mSlimView.m_faceIndex = -1;
							mSlimView.Restore();
						}
						if(mFixButton != null)
						{
							mFixButton.showJitterAnimAccordingStatus();
						}
					} else {
						mSlimView.setMode(SlimView.MODE_AUTO);
						showAutoButton(false);
						if(mFixButton != null)
						{
							mFixButton.showJitterAnimAccordingStatus();
						}
					}

					setAutoDefaultValue();
					break;
				case SlimHandler.MSG_UPDATE_UI:
					if (data.outBitmap != null) {
						// 为了解决正在对比操作时刚好完成任务时出现的问题
						if (mTempCompareBmp != null) {
							mTempCompareBmp = data.outBitmap;
						} else {
							mSlimView.setImage(data.outBitmap);
						}
					}

					data.outBitmap = null;

					// 隐藏loading
					updateWaitDialog(false, null);

					setUndoPanelState();
					setCompareViewState(false);
					break;
			}
		}
	}

	private void setCompareViewState(boolean forceHide) {
		mCompareView.animate().cancel();

		int lastValue = 0;
		FaceLocalData faceLocalData = FaceLocalData.getInstance();
		if (faceLocalData != null && FaceDataV2.sFaceIndex >= 0) {
			lastValue = faceLocalData.m_faceLevelMap_multi[FaceDataV2.sFaceIndex].get(faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex], 0);
		}

		if ((forceHide || (mSlimDatas.isEmpty() && mSlimToolDatas.isEmpty() && lastValue <= 0)) && mCompareView.getVisibility() == VISIBLE) {
			mCompareView.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mCompareView.setVisibility(INVISIBLE);
				}
			});

		} else if (!(mSlimDatas.isEmpty() && mSlimToolDatas.isEmpty() && lastValue <= 0)) {
			if (mCompareView.getVisibility() != VISIBLE) {
				mCompareView.setScaleX(0);
				mCompareView.setScaleY(0);
				mCompareView.setVisibility(VISIBLE);
				mCompareView.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
			} else {
				mCompareView.setScaleX(1);
				mCompareView.setScaleY(1);
			}
		}
	}

	private void setScaleAnim(final View view, boolean hide) {
		view.animate().cancel();
		if (hide && view.getVisibility() == VISIBLE) {
			view.animate().scaleX(0).scaleY(0).setDuration(100).setListener(new AnimatorListenerAdapter() {
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
				view.animate().scaleX(1).scaleY(1).setDuration(100).setListener(null);
			} else {
				view.setScaleX(1);
				view.setScaleY(1);
			}
		}
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params) {

		boolean change = false;
		if (params != null) {
			Object o = params.get(ChangePointPage.ISCHANGE);
			if (o != null && o instanceof Boolean) {
				change = (Boolean)o;
			}

			if (siteID == SiteID.CHANGEPOINT_PAGE) {
				// 一开始没定点成功
				if (mFixButton.getVisibility() != VISIBLE) {
					showAutoButton(false);
					setAutoDefaultValue();
				}

				if (change && mChange) {
					sendSlimMessage();
				}
			}
		}
	}

	private SlimViewEx.ControlCallback mCallback = new SlimViewEx.ControlCallback() {

		@Override
		public void UpdateSonWin(Bitmap bmp, int x, int y) {
			if (mSonWin != null) {
				mSonWin.SetData(bmp, x, y);
			}
		}

		@Override
		public void OnFingerDown(int mode, int fingerCount) {
			if (fingerCount == 1) {
				if (mode == SlimView.MODE_MANUAL) {
					mUndoPanel.hide();
					setCompareViewState(true);
					setScaleAnim(mToolButtonDefault, true);
				} else if (mode == SlimView.MODE_TOOL) {
					mUndoPanel.hide();
					setCompareViewState(true);
					setScaleAnim(mToolButton, true);
				}
			} else {
				OnFingerUp(mode, 1);
			}
		}

		@Override
		public void OnFingerUp(int mode, int fingerCount) {
			if (fingerCount == 1) {
				if (mode == SlimView.MODE_MANUAL) {
					setUndoPanelState();
					setCompareViewState(false);
					setScaleAnim(mToolButtonDefault, false);
				} else if (mode == SlimView.MODE_TOOL) {
					setUndoPanelState();
					setCompareViewState(false);
					setScaleAnim(mToolButton, false);
				}
			}
		}

		@Override
		public void OnViewSizeChange() {

		}

		@Override
		public void OnClickSlimTool(float x1, float y1, float x2, float y2, float rw) {
			if (mResetSlimSize || mSlimToolDatas.size() <= 0) {
				mSlimToolDatas.add(new LineData(x1, y1, x2, y2, rw));
				mResetSlimSize = false;
			} else {
				LineData temp = mSlimToolDatas.get(mSlimToolDatas.size() - 1);
				mSlimToolDatas.add(new LineData(temp.m_x2, temp.m_y2, temp.m_x2 + x2 - x1, temp.m_y2 + y2 - y1, rw));
			}
			mTempSlimToolDatas.clear();
			mActionInfo.addRecord(ActionInfo.ActionType.Tool);
			updateUndoCtrl();

			sendSlimMessage();
		}

		@Override
		public void OnResetSlimTool(float rw) {
			mResetSlimSize = true;
		}

		@Override
		public void OnDragSlim(float x1, float y1, float x2, float y2, float rw) {
			mSlimDatas.add(new LineData(x1, y1, x2, y2, rw));
			mTempSlimDatas.clear();
			mActionInfo.addRecord(ActionInfo.ActionType.Manual);
			updateUndoCtrl();
			sendSlimMessage();
		}

		@Override
		public void OnSelFaceIndex(int index) {
			if (mSlimView != null) {

				mSlimView.m_faceIndex = index;
				FaceDataV2.sFaceIndex = mSlimView.m_faceIndex;

				mSlimView.setMode(mLastMode);

				mContentLayout.setVisibility(VISIBLE);
				mTopBarLayout.setVisibility(VISIBLE);
				setUndoPanelState();
				setCompareViewState(false);

				if (mShowTool) {
					setScaleAnim(mToolButtonDefault, true);
					setScaleAnim(mToolButton, false);
				} else {
					setScaleAnim(mToolButton, true);
					setScaleAnim(mToolButtonDefault, false);
				}

				showAutoButton(mClickChangeFaceBtn);
				mClickChangeFaceBtn = false;

				mMutipleFaceDetect.setVisibility(INVISIBLE);

				FaceLocalData faceLocalData = FaceLocalData.getInstance();
				int lastValue = faceLocalData.m_faceLevelMap_multi[FaceDataV2.sFaceIndex].get(faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex]);
				mAutoSeekBar.setProgress(lastValue);

				if (mSelectedFace.getVisibility() == VISIBLE) {
					int faceType = faceLocalData.m_faceType_multi[FaceDataV2.sFaceIndex];
					int stringId, drawableId;
					if (faceType == FaceType.FACE_OVAL) {
						stringId = R.string.slim_oval_face;
						drawableId = R.drawable.beauty_slim_oval_face;
						mSelectFace = 0;
						changeSelectedFace(drawableId, stringId);
					} else if (faceType == FaceType.FACE_AWL) {
						stringId = R.string.slim_awl_face;
						drawableId = R.drawable.beauty_slim_awl_face;
						mSelectFace = 1;
						changeSelectedFace(drawableId, stringId);
					} else if (faceType == FaceType.FACE_EGG) {
						stringId = R.string.slim_egg_face;
						drawableId = R.drawable.beauty_slim_egg_face;
						mSelectFace = 2;
						changeSelectedFace(drawableId, stringId);
					}
				}

				mSlimView.DoSelFaceAnim();
			}
		}

		@Override
		public void OnAnimFinish() {

		}
	};

	private void changeSelectedFace(int drawableId, int stringId) {

		mSelectedFace.face.setImageResource(drawableId);
		mSelectedFace.text.setText(stringId);

		float x1 = 0, x2 = 0, x3 = 0, startX, otherX;
		final float endX = ShareData.PxToDpi_xhdpi(59);
		if (mSelectFace == 0) {
			startX = mOvalFace.getLeft();
			otherX = mAwlFace.getLeft();
			x1 = endX - startX;
			x2 = x3 = mFrameWidth - otherX;
		} else if (mSelectFace == 1) {
			startX = mAwlFace.getLeft();
			otherX = mEggFace.getLeft();
			x1 = -mOvalFace.getRight();
			x2 = endX - startX;
			x3 = mFrameWidth - otherX;
		} else if (mSelectFace == 2) {
			startX = mEggFace.getLeft();
			x1 = x2 = x3 = endX - startX;
		}

		mOvalFace.setTranslationX(x1);
		mAwlFace.setTranslationX(x2);
		mEggFace.setTranslationX(x3);
	}

	private void setUndoPanelState() {
		if ((!mShowTool && mSelectAction == 1) || (mSlimToolDatas.isEmpty() && mTempSlimToolDatas.isEmpty() && mSlimDatas.isEmpty() && mTempSlimDatas.isEmpty())) {
			mUndoPanel.hide();
		} else {
			mUndoPanel.show();
		}
	}

	private void setAutoDefaultValue() {
		FaceLocalData faceLocalData = FaceLocalData.getInstance();
		for (int i = 0; i < faceLocalData.m_faceNum; i++) {
			faceLocalData.m_faceLevelMap_multi[i].put(FaceType.FACE_OVAL, AUTO_DEFAULT_VALUE);
			faceLocalData.m_faceLevelMap_multi[i].put(FaceType.FACE_AWL, AUTO_DEFAULT_VALUE);
			faceLocalData.m_faceLevelMap_multi[i].put(FaceType.FACE_EGG, AUTO_DEFAULT_VALUE);
		}
	}
}

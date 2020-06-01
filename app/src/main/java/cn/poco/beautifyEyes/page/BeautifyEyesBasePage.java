package cn.poco.beautifyEyes.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.beautify4.Beautify4Page;
import cn.poco.beautifyEyes.Component.Cell.ChangeFaceCell;
import cn.poco.beautifyEyes.Component.Widget.BeautifyTitleView;
import cn.poco.beautifyEyes.Component.Widget.LetterCenterView;
import cn.poco.beautifyEyes.Component.Widget.SeekbarLayout;
import cn.poco.beautifyEyes.site.BeautyBaseSite;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.beauty.view.ColorSeekBar;
import cn.poco.camera.RotationImg2;
import cn.poco.camera3.ui.FixPointView;
import cn.poco.cloudalbumlibs.utils.CloudAlbumDialog;
import cn.poco.face.FaceDataV2;
import cn.poco.face.FaceLocalData;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.makeup.ChangePointPage;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import cn.poco.view.beauty.BeautyCommonViewEx;
import my.beautyCamera.R;

import static cn.poco.advanced.ImageUtils.AddSkin;
import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_IMG_H;
import static cn.poco.beautify4.Beautify4Page.PAGE_BACK_ANIM_VIEW_TOP_MARGIN;
import static cn.poco.beautifyEyes.page.BeautifyEyesBasePage.LayoutStyle.CHANGE_BEAUTIFY_FACE;
import static cn.poco.beautifyEyes.page.BeautifyEyesBasePage.LayoutStyle.NONE;
import static cn.poco.beautifyEyes.page.BeautifyEyesBasePage.LayoutStyle.SHOWING_MULTI_PEOPLE;
import static cn.poco.beautifyEyes.page.BeautifyEyesBasePage.LayoutStyle.SHOWING_SINGLE_PEOPLE;
import static cn.poco.tianutils.ShareData.PxToDpi_xhdpi;
import static cn.poco.view.beauty.BeautyCommonViewEx.MODE_SEL_FACE;

/**
 * Created by Shine on 2016/12/2.
 */
public abstract class BeautifyEyesBasePage extends IPage {
    protected static class BeautyPageInfo {
        protected String tag;
        protected int checkType;
        protected String titleString;
        protected int cancelResId, confirmResId, beautifyIconResId;
        private int mIndex;
        private BeautifyModule mModule;
    }

    public enum BeautifyModule {
        NONE(-1),
        BIGEYES(0),
        DROPEYESBAG(1),
        BRIGHTEYES(2);

        int mIndex;
        BeautifyModule(int index) {
            this.mIndex = index;
        }

        public static BeautifyModule getModuleByIndex(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("the index is less than zero");
            }

            switch (index) {
                case 0 :
                    return BIGEYES;

                case 1 :
                    return DROPEYESBAG;

                case 2 :
                    return BRIGHTEYES;

                default: {
                    return NONE;
                }
            }

        }

    }

    public enum LayoutStyle {
        NONE,
        MULTI_PEOPLE_DETECTING,
        SHOWING_MULTI_PEOPLE,
        SHOWING_SINGLE_PEOPLE,
        CHANGE_BEAUTIFY_FACE
    }

    private String TAG_SHAPE;
    private int PINFACE_CHECK_TYPE;
    private int DEF_IMG_SIZE;

    // 进入时默认的美颜数据
    private static final int DEFAULT_BEAUTIFY_DATA = 30;
    private static final int NONE_VALUE = -1;

    private static final int SEKKBAR_THUMB_HALF_WIDTH = PxToDpi_xhdpi(35);
    protected int mCompareRightMargin = PxToDpi_xhdpi(20), mCompareTopMargin = PxToDpi_xhdpi(12);
    protected int mPinBtnRightMargin = PxToDpi_xhdpi(24), mPinBtnBottomMargin = PxToDpi_xhdpi(24);
    protected int mSeekbarLayoutHeight;
    protected int mBottomControlPanel = PxToDpi_xhdpi(320);
    protected int mProgressLetterRadius = PxToDpi_xhdpi(55);

    private Paint mColorPaint;
    private FrameLayout mViewContainer;
    protected BeautyCommonViewEx mBeautifyView;
    private FrameLayout mFeatureContainer;
    private TextView mMutipleFaceDetect;
    private ImageView mCompareBtn;
//    private PinPointCell mFixView;
    private FixPointView mFixView;
    private ChangeFaceCell mChangeFaceBtn;

    private FrameLayout mControlPanel;
    private FrameLayout mTopBar;
    private ImageView mCancelBtn, mConfirmBtn;
    private BeautifyTitleView mBeautifyTitleView;

    private SeekbarLayout mSeekBarLayout ;
    private LetterCenterView mLetterCenterView;

    protected WaitAnimDialog mWaitDlg;

    private Bitmap mOriginalBitmap;
    private BeautifyEyesHandler mBeautifyEyesHandler;
    private UIHandler mUIHandler ;
    private HandlerThread mProcessImageThread;

    private BeautyBaseSite mSite;
    private BeautifyModule mCurrentModule;
    private BeautifyModule mOriginBeautifyModule;

    private Bitmap m_bkBmp;
    private Bitmap mTempCompare;

    private int mThumbCenterX;

    private BeautyPageInfo mCurrentPageInfo;
    private boolean mUiEnable;
    private int mFaceLastIndex = NONE_VALUE;
    private LayoutStyle mCurrentLayoutStyle = NONE;
    private FullScreenDlg noFaceDetectDlg;

    private int mTopbarMargin, mViewHeight, mImgHeigth;
    private int mBeautifyViewWidthInit, mBeautifyViewHeightInit;
    private int mBeautifyViewWidthFinal, mBeautifyViewHeightFinal;

    private BeautifyEyesHandler.InitMsg info;

    private int mCurrentIndex = NONE_VALUE;

    private List<BeautyPageInfo> mBeautyPageInfoList = new ArrayList<>();
    private SparseArray<SparseArray<Integer>> mSeekBarProgressDictionary = new SparseArray();

    // 动画
    private int mInitAnimationDuration;
    private int mPageMoveAnimationDuration;


    private Context mContext;

    // Page的状态
    private static final int ON_BACK_PRESS = 1 << 0;
    private static final int FIRST_PIN_POINT = 1 << 1;
    private static final int FIRST_PICK_UP_FACE = 1 << 2;
    private static final int ZOOM_ANIMATION_END = 1 << 3;
    private static final int ALREADY_APPLAY_DEFAULT_EFFECT = 1 << 4;
    private int mPageStateFlags = 6;


    private boolean mChange = false;
    private CloudAlbumDialog mExitDialog;


    public BeautifyEyesBasePage(Context context, BeautyBaseSite site) {
        super(context, site);
        mContext = context;
        mSite = site;

        // 初始化需要用到的Handler
        mUIHandler = new UIHandler();
        mProcessImageThread = new HandlerThread("my_handler_thread");
        mProcessImageThread.start();
        mBeautifyEyesHandler = new BeautifyEyesHandler(mProcessImageThread.getLooper(), getContext(), mUIHandler);

        mCurrentIndex = implementData();
        initData();
        mCurrentPageInfo = mBeautyPageInfoList.get(mCurrentIndex);
        mCurrentModule = mCurrentPageInfo.mModule;
        int index = mCurrentIndex;
        mOriginBeautifyModule = BeautifyModule.getModuleByIndex(index);

        PINFACE_CHECK_TYPE = mCurrentPageInfo.checkType;
        TAG_SHAPE = mCurrentPageInfo.tag;
        setWillNotDraw(false);

        initView(context);
    }

    // 初始化相关数据
    private void initData() {
        BeautyPageInfo bigEyes = new BeautyPageInfo();
        bigEyes.beautifyIconResId = R.drawable.beautify_bigeyes_icon;
        bigEyes.cancelResId = R.drawable.beautify_cancel;
        bigEyes.confirmResId = R.drawable.beautify_ok;
        bigEyes.checkType = ChangePointPage.CHECK_TRHEE;
        bigEyes.titleString = getResources().getString(R.string.beautify4page_dayan_btn);
        bigEyes.tag = mContext.getString(R.string.大眼);
        bigEyes.mIndex = 0;
        bigEyes.mModule = BeautifyModule.BIGEYES;
        mBeautyPageInfoList.add(bigEyes);

        BeautyPageInfo dropEyesBag = new BeautyPageInfo();
        dropEyesBag.beautifyIconResId = R.drawable.beautify_remove_pouch;
        dropEyesBag.cancelResId = R.drawable.beautify_cancel;
        dropEyesBag.confirmResId = R.drawable.beautify_ok;
        dropEyesBag.checkType = ChangePointPage.CHECK_TRHEE;
        dropEyesBag.titleString = getResources().getString(R.string.beautify4page_quyandai_btn);
        bigEyes.tag = mContext.getString(R.string.祛眼袋);
        dropEyesBag.mIndex = 1;
        dropEyesBag.mModule = BeautifyModule.DROPEYESBAG;
        mBeautyPageInfoList.add(dropEyesBag);

        BeautyPageInfo brightEyes = new BeautyPageInfo();
        brightEyes.beautifyIconResId = R.drawable.beautify_brighteyes_icon;
        brightEyes.cancelResId = R.drawable.beautify_cancel;
        brightEyes.confirmResId = R.drawable.beautify_ok;
        brightEyes.checkType = ChangePointPage.CHECK_TRHEE;
        brightEyes.titleString = getResources().getString(R.string.beautify4page_liangyan_btn);
        brightEyes.tag = mContext.getString(R.string.亮眼);
        brightEyes.mIndex = 2;
        brightEyes.mModule = BeautifyModule.BRIGHTEYES;
        mBeautyPageInfoList.add(brightEyes);

        mColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mColorPaint.setStyle(Paint.Style.FILL);
        mColorPaint.setColor(0xe6f0f0f0);

        // 布局需要用到的数据
        mUiEnable = false;
        mBeautifyViewWidthInit = ShareData.m_screenWidth;
        mBeautifyViewWidthInit -= mBeautifyViewWidthInit % 2;
        mBeautifyViewHeightInit = ShareData.m_screenHeight - PxToDpi_xhdpi(320);
        mBeautifyViewHeightInit -= mBeautifyViewHeightInit % 2;
        mSeekbarLayoutHeight = PxToDpi_xhdpi(232);

        // 默认图片的尺寸
        DEF_IMG_SIZE = SysConfig.GetPhotoSize(getContext());

        // 动画相关数据
        mInitAnimationDuration = 300;
        mPageMoveAnimationDuration = 300;
    }


    @Override
    public void SetData(HashMap<String, Object> params) {
        Object v;
        //复制为本地对象
        HashMap<String, Object> myParams = (HashMap<String, Object>)params.clone();
        v = myParams.get("imgs");

        try
        {
            if(v == null) {
                RuntimeException ex = new RuntimeException(mCurrentPageInfo.tag + "received null params!");
                throw ex;
            }

            mTopbarMargin = checkParamsCorrectness(myParams, Beautify4Page.PAGE_ANIM_VIEW_TOP_MARGIN);
            mViewHeight = checkParamsCorrectness(myParams, Beautify4Page.PAGE_ANIM_VIEW_H);
            mImgHeigth = checkParamsCorrectness(myParams, Beautify4Page.PAGE_ANIM_IMG_H);

            // 传进来的参数有三种可能,分别是 RotationImg2[], RotationImg, Bitmap;
            if(v instanceof RotationImg2[]) {
                int len = ((RotationImg2[])v).length;
                if(len <= 0) {
                    RuntimeException ex = new RuntimeException(mCurrentPageInfo.tag + "--Input path num is 0!");
                    throw ex;
                }
                RotationImg2 temp;
                for(int i = 0; i < len; i++) {
                    temp = ((RotationImg2[])v)[i];
                    if(temp.m_img == null || !(new File((String)temp.m_img).exists())) {
                        RuntimeException ex = new RuntimeException(mCurrentPageInfo.tag + "--Input RotationImg[] has null element");
                        throw ex;
                    }
                }
                myParams.put("imgs", BeautifyResMgr2.CloneRotationImgArr((RotationImg2[])v));

            } else if (v instanceof RotationImg2) {
                RotationImg2 temp = (RotationImg2) v;
                if (temp.m_img == null || !(new File((String)temp.m_img)).exists()) {
                    RuntimeException ex = new RuntimeException(mCurrentPageInfo.tag + "--Input RotationImg path is null!");
                    throw ex;
                }
                myParams.put("imgs", temp.Clone());
            } else if (v instanceof Bitmap) {
                myParams.put("imgs", v);
            }
            setUpImage(myParams);
            startInitAnimation();
        }catch (Throwable t) {
            onExit();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mUiEnable) {
            return super.dispatchTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mColorPaint);
    }


    private int checkParamsCorrectness(Map data, String key) {
        Object object = data.get(key);
        if (object != null && object instanceof Integer) {
            return (Integer) object;
        }
        return 0;
    }

    protected void initView(final Context context) {
        mViewContainer = new FrameLayout(context);
        FrameLayout.LayoutParams paramsContainer = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewContainer.setLayoutParams(paramsContainer);
        this.addView(mViewContainer);

        mFeatureContainer = new FrameLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.m_screenHeight - mBottomControlPanel, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        mFeatureContainer.setLayoutParams(params);
        this.addView(mFeatureContainer);
        {
            mFixView = new FixPointView(context);
            FrameLayout.LayoutParams paramsBtn = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM);
            paramsBtn.rightMargin = mPinBtnRightMargin;
            paramsBtn.bottomMargin = mPinBtnBottomMargin;
            mFixView.setLayoutParams(paramsBtn);
            mFeatureContainer.addView(mFixView);
            mFixView.setVisibility(View.GONE);

            mChangeFaceBtn = new ChangeFaceCell(context);
            mChangeFaceBtn.setClickable(true);
            FrameLayout.LayoutParams changeParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM);
            changeParams.rightMargin = PxToDpi_xhdpi(120);
            changeParams.bottomMargin = PxToDpi_xhdpi(24);
            mChangeFaceBtn.setLayoutParams(changeParams);
            mFeatureContainer.addView(mChangeFaceBtn);
            mChangeFaceBtn.setVisibility(View.GONE);

            mMutipleFaceDetect = new TextView(context);
            mMutipleFaceDetect.setText(R.string.bigeyes_multiple_face_detect);
            mMutipleFaceDetect.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            mMutipleFaceDetect.setTextColor(Color.parseColor("#000000"));
            mMutipleFaceDetect.setGravity(Gravity.CENTER);
            mMutipleFaceDetect.setBackgroundDrawable(getResources().getDrawable(R.drawable.beautifyeyes_multiple_indication));
            FrameLayout.LayoutParams paramsMultiFace = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            paramsMultiFace.bottomMargin = PxToDpi_xhdpi(54);
            mMutipleFaceDetect.setLayoutParams(paramsMultiFace);
            mFeatureContainer.addView(mMutipleFaceDetect);
            mMutipleFaceDetect.setVisibility(View.GONE);
        }

        mCompareBtn = new ImageView(context);
        mCompareBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mCompareBtn.setImageResource(R.drawable.beautify_compare);
        FrameLayout.LayoutParams paramsCompare = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.TOP);
        paramsCompare.rightMargin = mCompareRightMargin;
        paramsCompare.topMargin = mCompareTopMargin;
        mCompareBtn.setLayoutParams(paramsCompare);
        this.addView(mCompareBtn);
        mCompareBtn.setVisibility(View.GONE);

        mControlPanel = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mBottomControlPanel, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        mControlPanel.setLayoutParams(layoutParams3);
        mViewContainer.addView(mControlPanel);
        mTopBar = new FrameLayout(context);
        mTopBar.setBackgroundColor(0xe6ffffff);
        FrameLayout.LayoutParams topBarParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxToDpi_xhdpi(88));
        mTopBar.setLayoutParams(topBarParams);
        mControlPanel.setVisibility(View.GONE);
        mControlPanel.addView(mTopBar);
        {
            mCancelBtn = new ImageView(context);
            mCancelBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mCancelBtn.setPadding(PxToDpi_xhdpi(22), 0, 0, 0);
            FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mCancelBtn.setLayoutParams(layoutParams4);
            mTopBar.addView(mCancelBtn);

            mBeautifyTitleView = new BeautifyTitleView(context, mCurrentIndex);
            List<BeautifyTitleView.TitleInfo> titleDataList = new ArrayList<>();
            titleDataList.add(new BeautifyTitleView.TitleInfo(mBeautyPageInfoList.get(0).titleString, mBeautyPageInfoList.get(0).beautifyIconResId));
            titleDataList.add(new BeautifyTitleView.TitleInfo(mBeautyPageInfoList.get(1).titleString, mBeautyPageInfoList.get(1).beautifyIconResId));
            titleDataList.add(new BeautifyTitleView.TitleInfo(mBeautyPageInfoList.get(2).titleString, mBeautyPageInfoList.get(2).beautifyIconResId));
            mBeautifyTitleView.setItem(titleDataList);
            FrameLayout.LayoutParams moduleParams = new FrameLayout.LayoutParams(ShareData.m_screenWidth - PxToDpi_xhdpi(100), ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            mBeautifyTitleView.setLayoutParams(moduleParams);
            mTopBar.addView(mBeautifyTitleView);

            mBeautifyTitleView.setDelegate(new BeautifyTitleView.BeautifyTitleViewDelegate() {
                @Override
                public void onArrowClick(boolean state) {
                    startPageAnimation(state);
                    if (state) {
                        StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).shrinkBar(mContext);
                    } else {
                        StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).unfoldedBar(mContext);
                    }
                }

                @Override
                public void onBeautifyModeChange(int index) {
                    if (mBeautyPageInfoList != null && mSeekBarProgressDictionary != null)
                    {
                        BeautyPageInfo info = mBeautyPageInfoList.get(index);
                        if (info != null)
                        {
                            mCurrentModule = info.mModule;
                        }
                        SparseArray<Integer> array = mSeekBarProgressDictionary.get(mCurrentIndex);
                        if (array != null)
                        {
                            array.put(mFaceLastIndex, mSeekBarLayout.getDisplaySeekbarProgress());
                        }
                        mCurrentIndex = index;

                        SparseArray<Integer> out = mSeekBarProgressDictionary.get(mCurrentIndex);
                        if (out != null)
                        {
                            int currentModeValue = out.get(mFaceLastIndex);
                            ((LayoutParams) mLetterCenterView.getLayoutParams()).leftMargin = calculateLetterXMargin(currentModeValue);
                            mLetterCenterView.setDrawText(String.valueOf(currentModeValue));
                            mControlPanel.requestLayout();
                        }
                    }
                }

                @Override
                public void onSwipeDirection(int direction, int srcIndex, int dstIndex) {
                    if (mSeekBarProgressDictionary != null)
                    {
                        SparseArray<Integer> list = mSeekBarProgressDictionary.get(dstIndex);
                        if (list != null)
                        {
                            int currentBarlevel = list.get(mFaceLastIndex);

                            if (direction == 0) {
                                mSeekBarLayout.startTransitionAnimation(true, currentBarlevel);
                            } else if (direction == 1){
                                mSeekBarLayout.startTransitionAnimation(false, currentBarlevel);
                            }
                        }
                    }
                }
            });

            mConfirmBtn = new ImageView(context);
            mConfirmBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mConfirmBtn.setPadding(0, 0, PxToDpi_xhdpi(22), 0);
            FrameLayout.LayoutParams layoutParams6 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            mConfirmBtn.setLayoutParams(layoutParams6);
            mTopBar.addView(mConfirmBtn);

            mCancelBtn.setImageResource(mCurrentPageInfo.cancelResId);
            mConfirmBtn.setImageResource(mCurrentPageInfo.confirmResId);
        }

        mSeekBarLayout = new SeekbarLayout(context);
        mSeekBarLayout.setBackgroundColor(0xE6f0f0f0);
        mSeekBarLayout.fillDisplaySeekbarData(DEFAULT_BEAUTIFY_DATA);
        FrameLayout.LayoutParams layoutParams7 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mSeekbarLayoutHeight, Gravity.BOTTOM);
        mSeekBarLayout.setLayoutParams(layoutParams7);
        mControlPanel.addView(mSeekBarLayout);
        {

            mSeekBarLayout.setUpProgressChangeListener(new ColorSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(ColorSeekBar seekBar, int progress) {
                    if (progress < 0 || progress > 100) {
                        progress = 100;
                    }
                    mLetterCenterView.setDrawText(String.valueOf(progress));
                    ((LayoutParams) mLetterCenterView.getLayoutParams()).leftMargin = calculateLetterXMargin(progress);
                    mControlPanel.requestLayout();
                }

                @Override
                public void onStartTrackingTouch(ColorSeekBar seekBar) {
                    if (mLetterCenterView.getVisibility() != VISIBLE) {
                        mLetterCenterView.setVisibility(View.VISIBLE);
                    }
                    StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).onClickSlidingBar(mContext);
                }

                @Override
                public void onStopTrackingTouch(ColorSeekBar seekBar) {
                    if (mLetterCenterView.getVisibility() != View.GONE) {
                        mLetterCenterView.setVisibility(View.GONE);
                    }
                    int progress = seekBar.getProgress();
                    mSeekBarLayout.updateLastDisplayValue(progress);

                    SparseArray<Integer> currentIndexData = mSeekBarProgressDictionary.get(mCurrentIndex);
                    if (currentIndexData == null) {
                        currentIndexData = new SparseArray<>();
                    }
                    currentIndexData.put(mFaceLastIndex, progress);

                    setBeautifyEffectData();
                    setWaitUI(true);
                    sendShapeMsg();
                    int compareVisibility = mCompareBtn.getVisibility();
                    boolean hasEffect = checkExistBeautifyEffect();
                    boolean needAnimation = compareVisibility == View.GONE || (compareVisibility == View.VISIBLE && !hasEffect);
                    if (needAnimation) {
                        startComparnBtnAnimation(progress, false);
                    }
                }
            });

            {
                mLetterCenterView = new LetterCenterView(getContext());
                FrameLayout.LayoutParams layoutParamsLetter = new FrameLayout.LayoutParams((mProgressLetterRadius * 2 + 5) , (mProgressLetterRadius * 2 + 5), Gravity.LEFT | Gravity.TOP);
                layoutParamsLetter.leftMargin = calculateLetterXMargin(DEFAULT_BEAUTIFY_DATA);
                layoutParamsLetter.topMargin = PxToDpi_xhdpi(24);
                mLetterCenterView.setDrawText(String.valueOf(DEFAULT_BEAUTIFY_DATA));
                mLetterCenterView.setLayoutParams(layoutParamsLetter);
                mControlPanel.addView(mLetterCenterView);
                mLetterCenterView.setVisibility(View.GONE);
            }

            mWaitDlg = new WaitAnimDialog((Activity) this.getContext());
            mWaitDlg.SetGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, mBottomControlPanel + PxToDpi_xhdpi(38));

            addSkin(context);
            initTouchListener();
        }
    }

    private void addSkin(Context context) {
        AddSkin(context, mConfirmBtn);
//        AddSkin(context, mFixView.getPinPointImage());
        AddSkin(context, mChangeFaceBtn.getPinPointImage());
    }

    private void initTouchListener() {
        mCancelBtn.setOnTouchListener(onAnimationClickListener);
        mConfirmBtn.setOnTouchListener(onAnimationClickListener);
        mCompareBtn.setOnTouchListener(onAnimationClickListener);
        mFixView.setOnTouchListener(onAnimationClickListener);
        mChangeFaceBtn.setOnTouchListener(onAnimationClickListener);
    }

    private void setLayoutStyle(LayoutStyle style) {
        mCurrentLayoutStyle = style;
        switch (style) {
            case CHANGE_BEAUTIFY_FACE:
                mPageStateFlags &= ~ZOOM_ANIMATION_END;
            case MULTI_PEOPLE_DETECTING: {
                if (mMutipleFaceDetect.getVisibility() != View.VISIBLE) {
                    mMutipleFaceDetect.setVisibility(View.VISIBLE);
                }

                if (mControlPanel.getVisibility() != View.GONE) {
                    mControlPanel.setVisibility(View.GONE);
                }

                if (mCompareBtn.getVisibility() != View.GONE ) {
                    mCompareBtn.setVisibility(View.GONE);
                }

                if (mFixView.getVisibility() != View.GONE) {
                    mFixView.setVisibility(View.GONE);
                }

                if (mChangeFaceBtn.getVisibility() != View.GONE) {
                    mChangeFaceBtn.setVisibility(View.GONE);
                }

                if (style == CHANGE_BEAUTIFY_FACE) {
                    mBeautifyView.Restore();
                } else {
                    mBeautifyView.m_faceIndex = -1;
                    mBeautifyView.setMode(MODE_SEL_FACE);
                }
                break;
            }

            case SHOWING_MULTI_PEOPLE: {
                if (mBeautifyView != null) {
                    mBeautifyView.m_showSelFaceRect = true;
                    mBeautifyView.DoSelFaceAnim();
                }

                if (mMutipleFaceDetect.getVisibility() != View.GONE) {
                    mMutipleFaceDetect.setVisibility(View.GONE);
                }

                if (mControlPanel.getVisibility() != View.VISIBLE) {
                    mControlPanel.setVisibility(View.VISIBLE);
                }


                if (mFixView.getVisibility() != View.VISIBLE) {
                    mFixView.setVisibility(View.VISIBLE);
                    getScaleAnimator(mFixView, 0, 1, null).start();
                }

                if (mChangeFaceBtn.getVisibility() != View.VISIBLE) {
                    mChangeFaceBtn.setVisibility(View.VISIBLE);
                    getScaleAnimator(mChangeFaceBtn, 0, 1, null).start();
                }



                break;
            }

            case SHOWING_SINGLE_PEOPLE: {

                if (mMutipleFaceDetect.getVisibility() != View.GONE) {
                    mMutipleFaceDetect.setVisibility(View.GONE);
                }

                if (mControlPanel.getVisibility() != View.VISIBLE) {
                    mControlPanel.setVisibility(View.VISIBLE);
                }


                if (mFixView.getVisibility() != View.VISIBLE) {
                    mFixView.setVisibility(View.VISIBLE);
                    getScaleAnimator(mFixView, 0, 1, null).start();
                }

                if (mChangeFaceBtn.getVisibility() != View.GONE) {
                    mChangeFaceBtn.setVisibility(View.GONE);
                }


                break;
            }

            default: {
                break;
            }
        }
    }

    //设置显示图片
    private void setUpImage(HashMap<String, Object> params) {
        mUiEnable = false;
        if (mBeautifyView != null) {
            this.removeView(mBeautifyView);
            mBeautifyView = null;
        }

        mBeautifyViewWidthFinal = mBeautifyViewWidthInit;
//        mBeautifyViewHeightFinal = mBeautifyViewWidthFinal * 4 / 3;
//        mBeautifyViewHeightFinal -= mBeautifyViewHeightFinal % 2;
//        if(mBeautifyViewHeightFinal > mBeautifyViewHeightInit)
//        {
//            mBeautifyViewHeightFinal = mBeautifyViewHeightInit;
//        }
        mBeautifyViewHeightFinal = mBeautifyViewHeightInit;

        //为了去白边
        mBeautifyViewWidthFinal += 2;
        mBeautifyView = new BeautyCommonViewEx(getContext());

        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(mBeautifyViewWidthFinal, mBeautifyViewHeightFinal, Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        mBeautifyView.setLayoutParams(params1);
        this.addView(mBeautifyView, 0);
        mBeautifyView.SetOnControlListener(mCallback);
        mBeautifyView.m_showSelFaceRect = false;

        // 发送初始化消息
        info = new BeautifyEyesHandler.InitMsg();
        Object object = params.get("imgs");

        info.m_w = DEF_IMG_SIZE;
        info.m_h = DEF_IMG_SIZE;

        if (object instanceof Bitmap) {
            info.mDisplayBitmap = (Bitmap) object;
        } else {
            info.m_imgs = object;
            //先显示图片
            BeautifyEyesHandler.initParams(getContext(), info);
        }

        if(info.mDisplayBitmap != null) {
            // 复制一张图片作为原图
            mOriginalBitmap = info.mDisplayBitmap.copy(Bitmap.Config.ARGB_8888, true);
            m_bkBmp = BeautifyResMgr2.MakeBkBmp(mOriginalBitmap, ShareData.m_screenWidth, ShareData.m_screenHeight);
            this.setBackgroundDrawable(new BitmapDrawable(getResources(), m_bkBmp));
            mBeautifyView.setImage(mOriginalBitmap);
        } else {
            RuntimeException ex = new RuntimeException(mCurrentPageInfo.tag + "--load img error!");
            throw ex;
        }

        mBeautifyView.LockUI(true);
    }

    //在动画结束之后，发送初始化信息到处理图片的Thread;
    private void sendInitMsg(BeautifyEyesHandler.InitMsg info) {
        Message msg = mBeautifyEyesHandler.obtainMessage();
        msg.what = BeautifyEyesHandler.MSG_INIT;
        msg.obj = info;
        mBeautifyEyesHandler.sendMessage(msg);
    }

    //发送进行面部处理的消息;
    private void sendShapeMsg() {
        BeautifyEyesHandler.CmdMsg cmd = new BeautifyEyesHandler.CmdMsg();
        cmd.m_faceLocalData = FaceLocalData.getInstance().Clone();
        if (mBeautifyEyesHandler != null && mBeautifyEyesHandler.mQueue != null)
        {
            mBeautifyEyesHandler.mQueue.AddItem(cmd);
            Message msg = mBeautifyEyesHandler.obtainMessage();
            msg.what = BeautifyEyesHandler.MSG_CYC_QUEUE;
            mBeautifyEyesHandler.sendMessage(msg);
        }
    }

    //根据情况是否显示正在加载的Dialog
    private void setWaitUI(boolean flag) {
        if(flag)
        {
            if(mWaitDlg != null)
            {
                mWaitDlg.show();
            }
        }
        else
        {
            if(mWaitDlg != null)
            {
                mWaitDlg.hide();
            }
        }
    }

    //更新页面
    private void refreshUI()
    {
        // 隐藏loading
        setWaitUI(false);
        mUiEnable = true;
    }


    private void mapFaceToBeautifyData(int size) {
        int faceNumber = size;
        for (int i = 0; i <= mBeautyPageInfoList.size() - 1; i++) {
            SparseArray<Integer> mode = new SparseArray<>();
            for (int j = 0; j <= faceNumber - 1; j++) {
                if (i == mCurrentIndex) {
                    mode.put(j, DEFAULT_BEAUTIFY_DATA);
                } else {
                    mode.put(j, 0);
                }
            }
            mSeekBarProgressDictionary.put(i, mode);
        }
    }


    //负责根据情况对页面UI进行操的Handler;
    private class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case BeautifyEyesHandler.MSG_INIT: {
                    if (msg.obj != null) {
                        BeautifyEyesHandler.InitMsg params = (BeautifyEyesHandler.InitMsg) msg.obj;
                        msg.obj = null;
                        boolean shouldJumpToDetectFace = params.mShouldJumpToDetectFace;
                        initDefaultBeautifyEffectData();

                        // 检测不到人脸
                        if (shouldJumpToDetectFace) {
                            // 先弹出检测不到人脸的提示
                            noFaceDetectDlg = CommonUI.MakeNoFaceHelpDlg((Activity) getContext(), new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(mFixView != null)
                                    {
                                        mFixView.modifyStatus();
                                    }
                                    noFaceDetectDlg .dismiss();
                                    HashMap<String ,Object> tempParams = new HashMap<>();
                                    tempParams.put("imgs", mOriginalBitmap);
                                    tempParams.put("type", PINFACE_CHECK_TYPE);
                                    tempParams.put("index", FaceDataV2.sFaceIndex);
                                    mSite.pinFaceChat(mContext, tempParams);
                                }
                            });
                            if ((mPageStateFlags & ON_BACK_PRESS) != ON_BACK_PRESS) {
                                noFaceDetectDlg.show();
                            }
                        } else {
                            if(mFixView != null)
                            {
                                mFixView.showJitterAnimAccordingStatus();
                            }
                            // 已经检测到人脸
                            displayLayoutDependOnStyle(params);
                            applyDefaultBeautifyEffectWhenSinglePerson();
                        }
                    }
                    break;
                }

                // 进行相应的美形操作之后, 更新界面
                case BeautifyEyesHandler.MSG_UPDATE_UI: {
                    if (msg.obj != null) {
                        BeautifyEyesHandler.CmdMsg params = (BeautifyEyesHandler.CmdMsg)msg.obj;
                        if (mCurrentLayoutStyle != SHOWING_MULTI_PEOPLE || (mPageStateFlags & ZOOM_ANIMATION_END) == ZOOM_ANIMATION_END) {
                            applyBeautifyEffect(params);
                        }

                        if (mWaitDlg != null && mWaitDlg.isShowing()) {
                            mWaitDlg.dismiss();
                        }

                        if ((mPageStateFlags & ALREADY_APPLAY_DEFAULT_EFFECT) != ALREADY_APPLAY_DEFAULT_EFFECT) {
                            startComparnBtnAnimation(DEFAULT_BEAUTIFY_DATA, true);
                            mPageStateFlags |= ALREADY_APPLAY_DEFAULT_EFFECT;
                        }
                        mChange = true;
                    }
                    break;
                }

                // 手动定点返回后,修正效果
                case BeautifyEyesHandler.MSG_ADJUST_EFFECT_UI : {
                    if (msg.obj != null) {
                        BeautifyEyesHandler.PinPointMsg params = (BeautifyEyesHandler.PinPointMsg)msg.obj;
                        msg.obj = null;

                        // 如果还没有布局，先进行界面布局
                        if (mCurrentLayoutStyle == NONE) {
                            displayLayoutDependOnStyle(params.mInitMsg);
                        }

                        // 根据最新的定点情况，修正美颜效果
                        if (FaceDataV2.CHECK_FACE_SUCCESS) {
                            applyBeautifyEffect(params.mCmdMsg);
                            if ((mPageStateFlags & ALREADY_APPLAY_DEFAULT_EFFECT) != ALREADY_APPLAY_DEFAULT_EFFECT) {
                                startComparnBtnAnimation(DEFAULT_BEAUTIFY_DATA, true);
                                mPageStateFlags |= ALREADY_APPLAY_DEFAULT_EFFECT;
                            }
                        }
                    }
                    break;
                }
                default: {

                }
            }
        }
    }

    //根据不同的情况显示不同的界面布局
    private void displayLayoutDependOnStyle(BeautifyEyesHandler.InitMsg params) {
        // 需要显示检测到多人的界面
        if (params.mShowMultifacedetect) {
            setLayoutStyle(LayoutStyle.MULTI_PEOPLE_DETECTING);
        } else {
            mBeautifyView.m_faceIndex = FaceDataV2.sFaceIndex;
            mFaceLastIndex = FaceDataV2.sFaceIndex;
            // 是否显示更换脸部的按钮
            if (params.mShowChangeface) {
                setLayoutStyle(LayoutStyle.SHOWING_MULTI_PEOPLE);
            } else {
                setLayoutStyle(SHOWING_SINGLE_PEOPLE);
            }

        }
        mBeautifyView.setImage(params.mDisplayBitmap);
        mBeautifyView.LockUI(false);
        refreshUI();
    }


    // 根据美颜效果，对图片进行更新
    private void applyBeautifyEffect(BeautifyEyesHandler.CmdMsg params) {
        if (params != null) {
            if(mTempCompare != null) {
                mTempCompare = params.mDisplayBitmap;
            } else {
                mBeautifyView.setImage(params.mDisplayBitmap);
            }
            refreshUI();
        }
    }


    @Override
    public void onBack() {
        if (mChange) {
            showExitDialog(new CloudAlbumDialog.OnButtonClickListener()
            {
                @Override
                public void onOkButtonClick()
                {
                    if (mExitDialog != null) mExitDialog.dismiss();
                    onExit();
                }

                @Override
                public void onCancelButtonClick()
                {
                    if (mExitDialog != null) mExitDialog.dismiss();
                }
            });
        } else {
            onExit();
        }
    }

    private BeautyCommonViewEx.ControlCallback mCallback = new BeautyCommonViewEx.ControlCallback() {
        @Override
        public void OnSelFaceIndex(int index) {
            FaceDataV2.sFaceIndex = index;
            if (mBeautifyView != null) {
                mBeautifyView.m_faceIndex = index;
                setLayoutStyle(LayoutStyle.SHOWING_MULTI_PEOPLE);
            }

            if (mFaceLastIndex != index && mSeekBarLayout.getDisplayColorSeekbar() != null && mSeekBarProgressDictionary != null) {
                mFaceLastIndex = index;
                int curValue = mSeekBarProgressDictionary.get(mCurrentIndex).get(mFaceLastIndex);
                mSeekBarLayout.fillDisplaySeekbarData(curValue);
            }
        }

        @Override
        public void OnAnimFinish() {
            if (mCurrentLayoutStyle != CHANGE_BEAUTIFY_FACE) {
                if ((mPageStateFlags & FIRST_PICK_UP_FACE) == FIRST_PICK_UP_FACE) {
                    applyDefaultBeautifyData();
                } else {
                    boolean hasEffect = checkExistBeautifyEffect();
                    if (hasEffect && mCompareBtn != null && mCompareBtn.getVisibility() != VISIBLE) {
                        mCompareBtn.setScaleX(1);
                        mCompareBtn.setScaleY(1);
                        mCompareBtn.setVisibility(View.VISIBLE);
                    }
                }

                mPageStateFlags |= ZOOM_ANIMATION_END;
                mPageStateFlags &= ~FIRST_PICK_UP_FACE;
            }
        }
    };

    private void applyDefaultBeautifyData() {
        if ((mPageStateFlags & ON_BACK_PRESS) != ON_BACK_PRESS) {
            setWaitUI(true);
            sendShapeMsg();
        }
    }

    private void applyDefaultBeautifyEffectWhenSinglePerson() {
        if (mCurrentLayoutStyle == SHOWING_SINGLE_PEOPLE) {
            if (mFaceLastIndex == NONE_VALUE) {
                mFaceLastIndex = 0;
            }
            applyDefaultBeautifyData();
        }
    }


    // 点击确定按钮之后, 释放原图内存，同时保存最新的图片，
    protected void saveBitmap() {
        mUiEnable = false;
        setWaitUI(true);
        clearOriginalBitmap();
    }

    // 释放原图内存
    private void clearOriginalBitmap() {
        if (mOriginalBitmap != null && mOriginalBitmap != mBeautifyView.getImage()) {
            mOriginalBitmap = null;
        }
        refreshUI();
        this.removeView(mBeautifyView);
        Bitmap bmp = mBeautifyView.getImage();
        mBeautifyView.setImage(null);
        HashMap<String, Object> tempParams = new HashMap<>();
        tempParams.put("img", bmp);
        tempParams.putAll(getBackAnimParam());
        mBeautifyEyesHandler.clearData();
        mSite.onSave(mContext, tempParams);
    }

    //用户点击取消之后，清除相关数据，并且释放内存
    protected void cancel() {
        mUiEnable = false;
        this.removeView(mBeautifyView);
        mBeautifyEyesHandler.clearData();
        onBackPassData();

        if (mBeautifyView != null &&  mBeautifyView.getImage() != null) {
            mBeautifyView.setImage(null);
        }
    }

    // 跳到定点页面
    protected void switchToPinFacePoint() {
        HashMap<String ,Object> tempParams = new HashMap<>();
        // 传原图
        tempParams.put("imgs", mOriginalBitmap);
        tempParams.put("type", PINFACE_CHECK_TYPE);
        tempParams.put("index", FaceDataV2.sFaceIndex);
        mSite.pinFaceChat(mContext, tempParams);
    }

    @Override
    public void onClose() {
        mUiEnable = false;
        mProcessImageThread.quit();
        mProcessImageThread = null;
        mBeautifyEyesHandler.removeCallbacksAndMessages(null);
        mUIHandler.removeCallbacksAndMessages(null);

        if(mFixView != null)
        {
            mFixView.clearAll();
        }
        this.removeAllViews();
        clearExitDialog();
        if (m_bkBmp != null) {
            m_bkBmp.recycle();
            m_bkBmp = null;
        }
        FaceLocalData.ClearData();

        if (mWaitDlg != null && mWaitDlg.isShowing()) {
            mWaitDlg.dismiss();
            mWaitDlg = null;
        }

        if (noFaceDetectDlg != null) {
            noFaceDetectDlg.dismiss();
            noFaceDetectDlg = null;
        }
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        if (siteID == SiteID.CHANGEPOINT_PAGE) {
            if ((mPageStateFlags & FIRST_PIN_POINT) == FIRST_PIN_POINT) {
                initDefaultBeautifyEffectData();
                mPageStateFlags &= ~FIRST_PIN_POINT;
            }

            BeautifyEyesHandler.PinPointMsg pinPointMsg = new BeautifyEyesHandler.PinPointMsg();
            BeautifyEyesHandler.InitMsg initMsg = new BeautifyEyesHandler.InitMsg();
            BeautifyEyesHandler.CmdMsg cmdMsg = new BeautifyEyesHandler.CmdMsg();
            cmdMsg.m_faceLocalData = FaceLocalData.getInstance().Clone();
            pinPointMsg.mInitMsg = initMsg;
            pinPointMsg.mCmdMsg = cmdMsg;

            HashMap<String, Object> data = (HashMap<String, Object>) params.clone();
            Object isChange = data.get(ChangePointPage.ISCHANGE);
            boolean isChangePoint = false;
            if (isChange instanceof Boolean) {
                isChangePoint = (Boolean) isChange;
            }

            // 在定点页面移动了定点
            if (isChangePoint) {
                setWaitUI(true);
                adjustBeautifyEffect();
                pinPointMsg.mInitMsg.mDisplayBitmap = mOriginalBitmap;
            } else {
                // 没有在定点页面确认定点,用回现在正在显示的图片
                pinPointMsg.mInitMsg.mDisplayBitmap = mBeautifyView.getImage();
            }
            sendPinPointMsg(pinPointMsg);
        }
    }

    // 发送脸部定点信息
    private void sendPinPointMsg(BeautifyEyesHandler.PinPointMsg pinPointMsg) {
        Message msg = mBeautifyEyesHandler.obtainMessage();
        msg.what = BeautifyEyesHandler.MSG_UPDATE_AFTER_PINPOINT;
        msg.obj = pinPointMsg;
        mBeautifyEyesHandler.sendMessage(msg);
    }


    private OnAnimationClickListener onAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
        }

        @Override
        public void onTouch(View v) {
            if (v == mConfirmBtn) {
                saveBitmap();
                SparseArray<Integer> bigEyesData = mSeekBarProgressDictionary.get(0);
                SparseArray<Integer> dropEyesBagData = mSeekBarProgressDictionary.get(1);
                SparseArray<Integer> brightEyesData = mSeekBarProgressDictionary.get(2);
                int bigEyesStatisticData = 0;
                int dropEyesBagStatisticData = 0;
                int brightEyesStatisticData = 0;
                if (bigEyesData != null) {
                    bigEyesStatisticData = bigEyesData.get(mFaceLastIndex);
                }

                if (dropEyesBagData != null) {
                    dropEyesBagStatisticData = dropEyesBagData.get(mFaceLastIndex);
                }

                if (brightEyesData != null) {
                    brightEyesStatisticData = brightEyesData.get(mFaceLastIndex);
                }
                StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).onClickConfirm(mContext, bigEyesStatisticData, dropEyesBagStatisticData, brightEyesStatisticData);
            } else if ( v == mCancelBtn) {
                if (mChange) {
                    showExitDialog(new CloudAlbumDialog.OnButtonClickListener()
                    {
                        @Override
                        public void onOkButtonClick()
                        {
                            if (mExitDialog != null) mExitDialog.dismiss();
                            StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).onClickCancel(mContext);
                            cancel();
                        }

                        @Override
                        public void onCancelButtonClick()
                        {
                            if (mExitDialog != null) mExitDialog.dismiss();
                        }
                    });
                } else {
                    StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).onClickCancel(mContext);
                    cancel();
                }
            } else if (v == mChangeFaceBtn) {
                setLayoutStyle(CHANGE_BEAUTIFY_FACE);
            } else if (v == mFixView) {
                mFixView.modifyStatus();
                StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).onClickPinPoint(mContext);
                switchToPinFacePoint();
            } else if (v == mCompareBtn) {
                StatisticHelper.getInstance(mOriginBeautifyModule, mCurrentModule).onClickCompareBtn(mContext);
                showBeautifyEffect();
            }
        }

        @Override
        public void onRelease(View v) {
            if (v == mCompareBtn) {
                restoreBeautifyEffect();
            }
        }
    };

    // 显示对比原图的效果
    private void showBeautifyEffect() {
        if (mBeautifyView != null) {
            if (mOriginalBitmap != null && mTempCompare == null) {
                mTempCompare = mBeautifyView.getImage();
                mBeautifyView.setImage(mOriginalBitmap);
            }
        }
    }

    // 恢复当前美颜效果
    private void restoreBeautifyEffect() {
        if (mBeautifyView != null) {
            if (mTempCompare != null) {
                mBeautifyView.setImage(mTempCompare);
                mTempCompare = null;
            }
        }
    }

    // 定点后修正图片效果
    private void adjustBeautifyEffect() {
        if (mFaceLastIndex == NONE_VALUE) {
            mFaceLastIndex = 0;
        }
        setBeautifyEffectData();
    }

    private boolean initDefaultBeautifyEffectData() {
        // 初始化人脸识别数据;
        if (FaceLocalData.getInstance() == null) {
            int faceNumberCount = 0;
            if (FaceDataV2.FACE_POS_MULTI != null) {
                faceNumberCount = FaceDataV2.FACE_POS_MULTI.length;
            }
            boolean isCountValid = faceNumberCount >= 0;
            if (isCountValid) {
                FaceLocalData.getInstance(faceNumberCount);
                for (int i = 0; i < faceNumberCount; i++) {
                    FaceLocalData.getInstance().m_bigEyeLevel_multi[i] = mCurrentModule == BeautifyModule.BIGEYES ? DEFAULT_BEAUTIFY_DATA : 0;
                    FaceLocalData.getInstance().m_brightEyeLevel_multi[i] = mCurrentModule == BeautifyModule.BRIGHTEYES ? DEFAULT_BEAUTIFY_DATA : 0;
                    FaceLocalData.getInstance().m_eyeBagsLevel_multi[i] = mCurrentModule == BeautifyModule.DROPEYESBAG ? DEFAULT_BEAUTIFY_DATA : 0;
                }
                mapFaceToBeautifyData(faceNumberCount);
            }
            return true;
        } else {
            return false;
        }
    }


    // 设置美颜效果的数据
    private void setBeautifyEffectData() {
        SparseArray<Integer> bigEyes = mSeekBarProgressDictionary.get(0);
        SparseArray<Integer> dropEyesBag = mSeekBarProgressDictionary.get(1);
        SparseArray<Integer> brightEyes = mSeekBarProgressDictionary.get(2);

        int bigEyesLevel = 0;
        int dropEyesBagLevel = 0;
        int brightEyesLevel = 0;

        if (bigEyes != null) {
            bigEyesLevel = bigEyes.get(mFaceLastIndex);
        }

        if (dropEyesBag != null) {
            dropEyesBagLevel = dropEyesBag.get(mFaceLastIndex);
        }

        if (brightEyes != null) {
            brightEyesLevel = brightEyes.get(mFaceLastIndex);
        }

        if (FaceLocalData.getInstance().m_bigEyeLevel_multi != null)
        {
            FaceLocalData.getInstance().m_bigEyeLevel_multi[FaceDataV2.sFaceIndex] = bigEyesLevel;
            FaceLocalData.getInstance().m_eyeBagsLevel_multi[FaceDataV2.sFaceIndex] = dropEyesBagLevel;
            FaceLocalData.getInstance().m_brightEyeLevel_multi[FaceDataV2.sFaceIndex] = brightEyesLevel;
        }
    }

    private void onBackPassData() {
        HashMap<String, Object> bitmapParams = new HashMap<>();
        bitmapParams.put("img", mOriginalBitmap);
        bitmapParams.putAll(getBackAnimParam());
        mSite.onBack(mContext, bitmapParams);
    }

    // 页面开始时的动画
    private void startInitAnimation() {
        if (mControlPanel.getVisibility() != View.VISIBLE) {
            mControlPanel.setVisibility(VISIBLE);
        }
        if (mImgHeigth > 0 && mViewHeight > 0) {
            int mStartY = (int)(mTopbarMargin + (mViewHeight - mBeautifyViewHeightFinal) / 2f);
            float scaleX = mBeautifyViewWidthInit * 1f / mOriginalBitmap.getWidth();
            float scaleY = mBeautifyViewHeightInit * 1f / mOriginalBitmap.getHeight();
            float scaleRate = Math.min(scaleX, scaleY);

            float startScale = mImgHeigth / (mOriginalBitmap.getHeight() * scaleRate);

            ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(mBeautifyView, "translationY", mStartY, 0);
            ObjectAnimator scaleAnimatorX = ObjectAnimator.ofFloat(mBeautifyView, "scaleX", startScale, 1);
            ObjectAnimator scaleAnimatorY = ObjectAnimator.ofFloat(mBeautifyView, "scaleY", startScale, 1);
            ObjectAnimator translationAnimator2 = ObjectAnimator.ofFloat(mControlPanel, "translationY", mBottomControlPanel, 0);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translationAnimator, scaleAnimatorX, scaleAnimatorY, translationAnimator2);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.setDuration(mInitAnimationDuration);
            animatorSet.start();

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    sendInitMsg(info);
                    info = null;
                }
            });
        } else {
            sendInitMsg(info);
            info = null;
        }
    }


    // 页面向下或向上移动的动画
    private void startPageAnimation(boolean state) {
        Animator featureLayerAnimator = getPageMoveAnimator(mFeatureContainer, (mSeekbarLayoutHeight + PxToDpi_xhdpi(2)), state);
        Animator controlPanelLayerAnimator = getPageMoveAnimator(mControlPanel, mSeekbarLayoutHeight , state);

        int start = mBeautifyViewHeightFinal, end = mBeautifyViewHeightFinal + mSeekbarLayoutHeight;
        if (!state) {
            start = start + end;
            end = start - end;
            start = start - end;
        }

        ValueAnimator beautifyViewAnimator = ValueAnimator.ofInt(start, end);
        beautifyViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                LayoutParams params = (LayoutParams) mBeautifyView.getLayoutParams();
                params.height = value;
                mBeautifyView.requestLayout();
            }
        });

        mBeautifyView.InitAnimDate(mBeautifyViewWidthFinal, start, mBeautifyViewWidthFinal, end);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(featureLayerAnimator, controlPanelLayerAnimator, beautifyViewAnimator);
        animatorSet.setDuration(mPageMoveAnimationDuration);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }


    private Animator getPageMoveAnimator(View view, int deltaY, boolean isDownAnimation) {
        ObjectAnimator objectAnimator;
        if (isDownAnimation) {
            objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0, deltaY);
        } else {
            objectAnimator = ObjectAnimator.ofFloat(view, "translationY", deltaY, 0);
        }
        return objectAnimator;
    }

    private AnimatorSet getScaleAnimator(View v, int startScale, int endScale, AnimatorListenerAdapter animationAdapter) {
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(v, "scaleX", startScale, endScale);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(v, "scaleY", startScale, endScale);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
        animatorSet.setDuration(200);
        if (startScale == 0) {
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        } else {
            animatorSet.setInterpolator(new LinearInterpolator());
        }

        if (animationAdapter != null) {
            animatorSet.addListener(animationAdapter);
        }
        return animatorSet;
    }

    private void startComparnBtnAnimation(int progress, boolean checkExistEffect) {
        final int startScale, endScale;
        if (mCompareBtn.getVisibility() != VISIBLE) {
            mCompareBtn.setVisibility(View.VISIBLE);
        }
        startScale = !(progress > 0) ? 1 : 0;
        endScale = !(progress > 0) ? 0 : 1;

        if (!checkExistEffect || (checkExistEffect && !checkExistBeautifyEffect())) {
            getScaleAnimator(mCompareBtn, startScale, endScale, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (endScale == 0) {
                        mCompareBtn.setVisibility(View.GONE);
                    }
                }
            }).start();
        }
    }

    // 判断现在是否存在美颜效果的数据
    private boolean checkExistBeautifyEffect() {
        if (mFaceLastIndex >= 0 && mSeekBarProgressDictionary != null) {
            boolean currentModeExist = false;
            SparseArray<Integer> modeData = mSeekBarProgressDictionary.get(mCurrentIndex);
            if (modeData != null) {
                currentModeExist = modeData.get(mFaceLastIndex) > 0;
            }
            return currentModeExist || checkExistBeautifyEffectInOtherMode();
        }
        return false;
    }

    // 判断其它模式下是否存在美颜效果的数据
    private boolean checkExistBeautifyEffectInOtherMode() {
        boolean hasEffectData = false;
        for (int i = 0; i < mSeekBarProgressDictionary.size(); i++) {
            if (i != mCurrentIndex) {
                SparseArray<Integer> modeData = mSeekBarProgressDictionary.get(i);
                int value = 0;
                if (modeData != null) {
                    value = modeData.get(mFaceLastIndex);
                }
                if (value > 0) {
                    hasEffectData = true;
                    break;
                }
            }
        }
        return hasEffectData;
    }

    private HashMap<String, Object> getBackAnimParam() {
        HashMap<String, Object> params = new HashMap<>();
        if (mBeautifyView == null) return params;
        float imgH = mBeautifyView.getImgHeight();
        params.put(PAGE_BACK_ANIM_IMG_H, imgH);
        float marginTop = (mBeautifyView.getHeight() - mBeautifyViewHeightFinal) / 2;
        params.put(PAGE_BACK_ANIM_VIEW_TOP_MARGIN, marginTop);
        return params;
    }

    private int calculateLetterXMargin(int progress) {
        int seekBarWidth = mSeekBarLayout.getSeekbarWidth();
        int thumbPositionInSeekbar = (int) ((seekBarWidth - (SEKKBAR_THUMB_HALF_WIDTH * 2)) * ((progress * 1.0f) / mSeekBarLayout.getDisplayColorSeekbar().getMax()));
        mThumbCenterX = thumbPositionInSeekbar + mSeekBarLayout.mSeekBarMargin + SEKKBAR_THUMB_HALF_WIDTH;
        int currentLeftMargin = mThumbCenterX - mProgressLetterRadius;
        return currentLeftMargin;
    }

    private void showExitDialog(final CloudAlbumDialog.OnButtonClickListener listener)
    {
        if (mExitDialog == null) {
            mExitDialog = new CloudAlbumDialog(getContext(),
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageUtils.AddSkin(getContext(), mExitDialog.getOkButtonBg());
            mExitDialog.setCancelButtonText(R.string.cancel)
                    .setOkButtonText(R.string.ensure)
                    .setMessage(R.string.confirm_back);
        }
        mExitDialog.setListener(null).setListener(listener);
        mExitDialog.show();
    }

    private void onExit()
    {
        mPageStateFlags |= ON_BACK_PRESS;
        cancel();
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



    protected abstract int implementData();





}

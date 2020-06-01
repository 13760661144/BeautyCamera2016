package cn.poco.home.home4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsBootAdRes;
import com.adnonstop.admasterlibs.data.AbsChannelAdRes;
import com.adnonstop.admasterlibs.data.AbsClickAdRes;
import com.adnonstop.admasterlibs.data.AbsFullscreenAdRes;
import com.adnonstop.admasterlibs.data.IAdSkin;
import com.adnonstop.beautymall.commutils.BMCheckNewTopic;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.HashMap;
import java.util.List;

import cn.poco.adMaster.BootAd;
import cn.poco.adMaster.HomeAd;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.campaignCenter.api.CampaignApi;
import cn.poco.campaignCenter.manager.ConnectionsManager;
import cn.poco.dynamicSticker.newSticker.CropCircleTransformation;
import cn.poco.exception.MyApplication;
import cn.poco.featuremenu.manager.AppFeatureManager;
import cn.poco.featuremenu.model.FeatureType;
import cn.poco.featuremenu.model.OtherFeature;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework2App;
import cn.poco.home.home4.introAnimation.AnimationController;
import cn.poco.home.home4.introAnimation.Config;
import cn.poco.home.home4.introAnimation.IntroAnimationView;
import cn.poco.home.home4.utils.GravitySensor;
import cn.poco.home.home4.utils.HomeUtils;
import cn.poco.home.home4.utils.OnHomeAnimationClickListener;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.home.home4.widget.ADRing;
import cn.poco.home.home4.widget.ArcBackGroundView;
import cn.poco.home.home4.widget.CameraCirCleView;
import cn.poco.home.home4.widget.CircleGiftView;
import cn.poco.home.home4.widget.CommonSkinDialog;
import cn.poco.home.home4.widget.RingDrawable;
import cn.poco.home.home4.widget.RopeAnimationView;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.system.ConfigIni;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.utils.ImageUtil;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

import static cn.poco.home.home4.utils.PercentUtil.HeightPxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.HeightPxxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.RadiusPxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.RadiusPxxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.WidthPxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.WidthPxxToPercent;
import static cn.poco.home.home4.widget.ArcBackGroundView.ARC_HEIGHT;
import static cn.poco.home.home4.widget.ArcBackGroundView.ARC_TOP_MARGIN;


/**
 * Created by lgd on 2016/11/22.
 */

public class MainPage extends IPage implements IActivePage
{
    private static final String TAG = "MainPage";

    private ArcBackGroundView mArcBackground;            //背景色,渐变和商业皮肤
    private RopeAnimationView mRopeAnimationView;       //  绳子动画

    //4个方向的滑动箭头和文字提示
    private LinearLayout mLeftTipParent;
    private View mLeftRedPoint;
    private String mLeftText;     //左滑网络提示
    private TextView mLeftTextNum;  //社区的消息提示个数
    private LinearLayout mRightTipParent;
    private TextView mRightTipTextView;
    private String mRightText;    //右滑网络提示
    private String mRightNewTime;   //右滑网络提示更新时间，过期判断？
    private View mRightRedPoint;    //右滑小红点
    private TextView mLeftTipView;
    private TextView mTopTipView;
    private ImageView mTopArrowImage;
    private ImageView mLeftArrowImage;
    private ImageView mRightArrowImage;
    private ImageView mBottomArrowImage;
    private TextView mBottomAlbumTipView;
    private ImageView mTopLogo;
    private ImageView mBottomArcView;

    //左下角和右下角按钮入口
    private FrameLayout mBeautyEntryBtn;
    private FrameLayout mLiveEntryBtn;
    private ImageView mLiveLogo;
    private TextView mBeautyTip;
    private TextView mLiveTip;

    //撤销和小圆广告
    private TextView mTopLeftUndoTip;
    private ImageView mTopLeftLine;
    private FrameLayout mTopLeftAdWrapFr;
    private ImageView mTopLeftAdArrow;
    private CircleGiftView mTopLeftSmallLogo;

    //中间相机和大圆
    private CircleGiftView mCenterADView;
    private CameraCirCleView mCameraCircleImage;
    private ADRing mCenterAdRing;

    //动画，资源，事件等
    private Animation mAnimationLeft;
    private Animation mAnimationTop;
    private Animation mAnimationRight;
    private Animation mAnimationBottom;
    private AnimatorSet mAnimationText;
    private AnimationController mIntroController;
    private IntroAnimationView mIntroView;
    private GravitySensor mySensor;
    private final static int SMALL_RADIUS_PRESENT = 35;
    private final static int SMALL_RADIUS = RadiusPxToPercent(SMALL_RADIUS_PRESENT);
    protected HomeAd mBusinessCore;            //广告素材，中间按钮
    private AbsAdRes mCenterAdRes;             //广告素材，中间按钮
    private AbsBootAdRes mLeftAdRes;           //小圆广告
//	private TestView view;

    //点击区域
    private int mClickMode = 0;
    protected final static int TOP = 1;
    protected final static int LEFT = 2;
    protected final static int RIGHT = 3;
    protected final static int BOTTOM = 4;
    protected final static int LEFT_BOTTOM = 5;
    protected final static int CAMERA = 6;
    protected final static int CENTER_AD = 7;
    protected final static int LEFT_TOP = 8;
    protected final static int RIGHT_BOTTOM = 9;
    private Rect mTopArea1 = new Rect();
    private Rect mTopArea2 = new Rect();
    private Rect mLeftTopArea = new Rect();
    private Rect mLeftArea = new Rect();
    private Rect mRightArea = new Rect();
    private Rect mBottomArea1 = new Rect();
    private Rect mBottomArea2 = new Rect();
    private Rect mLeftBottomArea = new Rect();
    private Rect mRightBottomArea = new Rect();

    private String mAdSkinResId;            //商业标志id
    private static boolean sIsSkipAnim = false;     //是否跳过商业皮肤动画
    public static boolean sIsLeftMenuHasNew = false;  //侧栏是否有红点
    protected static boolean sIsSlideBarHasNew = false;  //侧栏是否有红点
    protected static int sCommunityMsgNum = 0;  //社区消息提示数量
    protected static int sLastCommunityMsgNum = 0;  //上一次社区消息提示数量
    protected boolean mIsRightThemeHasNew = false;  //右侧素材是否有红点
    protected boolean mUiEnable = true;
    protected boolean mIsShowUndoBtn = false;   //左上角是否显示撤销按钮   和mIsShowSmallAD冲突，优先显示撤销
    private boolean mIsShowSmallAD = false;     //左上角是否显示广告，
    private boolean mIsShowLiveEntry = false;   //是否显示直播入口
    private String mLiveUrl;

    public MainPage(Context context) {
        super(context, null);
        initData();
        initUI();
    }


    @Override
    public void SetData(HashMap<String, Object> params) {
        boolean isCheckSkin = false;
        if (params != null) {
            if (params.containsKey(Home4Page.KEY_BOOT_IMG)) {
                mLeftAdRes = (AbsBootAdRes) params.get(Home4Page.KEY_BOOT_IMG);
            }
            if (params.containsKey(Home4Page.KEY_CHECK_SKIN)) {
                isCheckSkin = (Boolean) params.get(Home4Page.KEY_CHECK_SKIN);
            }

            if (params.containsKey("delay"))
            {
                boolean frist = (boolean) params.get("delay");
                params.remove("delay");
                if(frist)
                {
                    //第一次安装动画前隐藏
                    TagMgr.SetTagValue(getContext(), Tags.HOME_LIVE_JSON, "");
                    sIsLeftMenuHasNew = true;
                    setViewGone();
                }
            }
        }
        //缓存的商业id标志
        String saveAdResId = TagMgr.GetTagValue(getContext(), Tags.HOME_SKIN_AD_RES_ID);
        //判断是否在显示日期内
        String needShowSkinPath = HomeUtils.getSkinPath(getContext(), saveAdResId);
        if (!TextUtils.isEmpty(needShowSkinPath)) {
            mAdSkinResId = saveAdResId;
            mArcBackground.setSkinBitmap(BitmapFactory.decodeFile(needShowSkinPath));
            mIsShowUndoBtn = true;
        } else {
            TagMgr.SetTagValue(getContext(), Tags.HOME_SKIN_AD_RES_ID, "");
        }
        if (isCheckSkin && !TextUtils.isEmpty(mAdSkinResId)) {
            if (checkIsClearAdTheme()) {
                clearAdTheme(0);
            }
        }

        String json = TagMgr.GetTagValue(MyFramework2App.getInstance().getApplicationContext(), Tags.HOME_LIVE_JSON);
        if (!TextUtils.isEmpty(json)) {
            CampaignApi campaignApi = new CampaignApi();
            if (campaignApi.DecodeData(json)) {
                mGetDataDelagete.run(campaignApi, null);
            }
        }
        ConnectionsManager.getInstacne().getCampaignInfo("5", "1", "100", AppInterface.GetInstance(getContext()), mGetDataDelagete);

        //左上角撤回和广告按钮
            //左下角广告
        if (mLeftAdRes == null) {
            mLeftAdRes = BootAd.GetOneBootRes(getContext());
        }
        if (mLeftAdRes != null && mLeftAdRes.mReplayBtn != null) {
            mIsShowSmallAD = mTopLeftSmallLogo.setImagePath( mLeftAdRes.mReplayBtn);
        } else {
            mIsShowSmallAD = false;
        }
        if (!ConfigIni.hideBusiness) {
            mBusinessCore = new HomeAd(getContext());
            mBusinessCore.Run(mAdListener);
        }
        checkViewState();
        getSwitchText();
        checkNewState();
    }

    private void initData() {
        mySensor = new GravitySensor(getContext());
        mySensor.setSensorListener(sensorListener);
        EventCenter.addListener(mOnEventListener);
//        mLeftText = getResources().getString(R.string.homepage_welfare);
//        mRightText = getResources().getString(R.string.homepage_material);
        mLeftText = getResources().getString(R.string.homepage_left_tip);
        mRightText = getResources().getString(R.string.homepage_right_tip);
    }

    private void initUI() {
        LayoutParams params;

        if (SysConfig.IsDebug())
        {
            View view = new View(getContext());
//            view.setBackgroundColor(0x1fff0000);
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mBusinessCore.Run(mAdListener);
                }
            });
            params = new LayoutParams(WidthPxToPercent(200),HeightPxToPercent(140));
            addView(view, params);
        }

        //半月形背景，
        mArcBackground = new ArcBackGroundView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP;
        addView(mArcBackground, params);

        //97
        mRopeAnimationView = new RopeAnimationView(getContext());
        params = new LayoutParams(WidthPxToPercent(194), HeightPxToPercent(200));
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.topMargin = HeightPxToPercent(185);
        addView(mRopeAnimationView, params);

//        mParentFr = new FrameLayout(getContext());
//        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        addView(mParentFr, params);

        //mLiveLogo
        params = new LayoutParams(WidthPxToPercent(314), HeightPxToPercent(119));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mTopLogo = new ImageView(getContext());
        mTopLogo.setImageResource(R.drawable.home4_logo);
        mTopLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        params.topMargin = HeightPxToPercent(289);
        addView(mTopLogo, params);

        //上文字
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//		params.topMargin = HeightPxToPercent(230);
        params.topMargin = HeightPxToPercent(233);             //        225+8
        mTopTipView = new TextView(getContext());
        mTopTipView.setText(R.string.homepage_top_tip);
        mTopTipView.setTextColor(Color.WHITE);
        mTopTipView.getPaint().setFakeBoldText(true);
        mTopTipView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
        addView(mTopTipView, params);

        //上图标
        params = new LayoutParams(WidthPxToPercent(48), HeightPxToPercent(26));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = HeightPxToPercent(253);             //
        mTopArrowImage = new ImageView(getContext());
        mTopArrowImage.setVisibility(View.INVISIBLE);
        mTopArrowImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mTopArrowImage.setImageResource(R.drawable.home4_tip_top);
        addView(mTopArrowImage, params);


        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        //左文字
        mLeftTipParent = new LinearLayout(getContext());
        mLeftTipParent.setOrientation(LinearLayout.HORIZONTAL);
        mLeftTipParent.setGravity(Gravity.CENTER);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        params.topMargin = HeightPxToPercent(-7);
        params.leftMargin = WidthPxToPercent(22);
        addView(mLeftTipParent, params);
        {
            LinearLayout.LayoutParams ll;
            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.gravity = Gravity.CENTER;
//            mLeftTipView = new TextView(getContext());
            mLeftTipView = new TextView(getContext());
            mLeftTipView.setText(R.string.homepage_left_tip);
            mLeftTipView.setTextColor(Color.WHITE);
            mLeftTipView.getPaint().setFakeBoldText(true);
            mLeftTipView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
            mLeftTipParent.addView(mLeftTipView, ll);

            ll = new LinearLayout.LayoutParams(HeightPxToPercent(10),HeightPxToPercent(10));
            ll.gravity = Gravity.CENTER;
            ll.leftMargin = WidthPxToPercent(6);
            String s = getResources().getConfiguration().locale.getCountry();
            if (s.equals("UK") || s.equals("US")) {
                ll.topMargin = HeightPxToPercent(2);
            }
            mLeftRedPoint = new View(getContext());
            mLeftRedPoint.setVisibility(View.GONE);
            mLeftRedPoint.setBackgroundDrawable(shapeDrawable);
            mLeftTipParent.addView(mLeftRedPoint, ll);

            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.gravity = Gravity.CENTER;
//            String s = getResources().getConfiguration().locale.getCountry();
//            if (s.equals("UK") || s.equals("US")) {
//                ll.topMargin = HeightPxToPercent(2);
//            }
            ll.leftMargin = WidthPxToPercent(6);
            mLeftTextNum = new TextView(getContext());
            mLeftTextNum.setMinWidth(HeightPxxToPercent(41));
            mLeftTextNum.setMinHeight(HeightPxxToPercent(41));
            mLeftTextNum.setGravity(Gravity.CENTER);
            mLeftTextNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP,7);
            mLeftTextNum.setTextColor(Color.WHITE);
            mLeftTextNum.setBackgroundResource(R.drawable.home4_tip_circle_bk);
            mLeftTextNum.setVisibility(View.GONE);
            mLeftTipParent.addView(mLeftTextNum, ll);
        }

        //左图标
        params = new LayoutParams(WidthPxxToPercent(62), HeightPxxToPercent(42));
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        params.leftMargin = WidthPxToPercent(22);
        params.topMargin = HeightPxToPercent(28);
        mLeftArrowImage = new ImageView(getContext());
        mLeftArrowImage.setVisibility(View.INVISIBLE);
        mLeftArrowImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mLeftArrowImage.setImageResource(R.drawable.home4_tip_left);
        addView(mLeftArrowImage, params);

        //右图标
        params = new LayoutParams(WidthPxxToPercent(68), HeightPxxToPercent(42));
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.rightMargin = WidthPxToPercent(22);
        params.topMargin = HeightPxToPercent(28);
        mRightArrowImage = new ImageView(getContext());
        mRightArrowImage.setVisibility(View.INVISIBLE);
        mRightArrowImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mRightArrowImage.setImageResource(R.drawable.home4_tip_right);
        addView(mRightArrowImage, params);

        //右文字
        mRightTipParent = new LinearLayout(getContext());
        mRightTipParent.setOrientation(LinearLayout.HORIZONTAL);
        mRightTipParent.setGravity(Gravity.CENTER);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.topMargin = HeightPxToPercent(-7);
        params.rightMargin = WidthPxToPercent(22);
        addView(mRightTipParent, params);
        {
            LinearLayout.LayoutParams ll;
            ll = new LinearLayout.LayoutParams(HeightPxToPercent(10),HeightPxToPercent(10));
            ll.gravity = Gravity.CENTER;
            String s = getResources().getConfiguration().locale.getCountry();
            if (s.equals("UK") || s.equals("US")) {
                ll.topMargin = HeightPxToPercent(2);
            }
            mRightRedPoint = new View(getContext());
            mRightRedPoint.setVisibility(View.GONE);
            mRightRedPoint.setBackgroundDrawable(shapeDrawable);
            mRightTipParent.addView(mRightRedPoint, ll);

            ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.gravity = Gravity.CENTER;
            ll.leftMargin = WidthPxToPercent(6);
            mRightTipTextView = new TextView(getContext());
            mRightTipTextView.getPaint().setFakeBoldText(true);
            mRightTipTextView.setText(R.string.homepage_right_tip);
            mRightTipTextView.setTextColor(Color.WHITE);
            mRightTipTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
            mRightTipParent.addView(mRightTipTextView, ll);
        }


        //下图标  1185   95
        params = new LayoutParams(WidthPxToPercent(48), HeightPxToPercent(26));
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = HeightPxToPercent(110);                         //1197-10   93    1163+26 1189
        mBottomArrowImage = new ImageView(getContext());
        mBottomArrowImage.setVisibility(View.INVISIBLE);
        mBottomArrowImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mBottomArrowImage.setImageResource(R.drawable.home4_tip_bottom);
        addView(mBottomArrowImage, params);


        //广告按钮外圈，
        params = new LayoutParams(Config.RADIUS_BIG_CIRCLE * 2 - 2, Config.RADIUS_BIG_CIRCLE * 2 - 2);
        params.gravity = Gravity.CENTER;
        params.bottomMargin = Config.AD_CENTER_BOTTOM_MARGIN;
        mCenterAdRing = new ADRing(getContext());
        addView(mCenterAdRing, params);

        //
        //广告按钮，
        params = new LayoutParams(Config.RADIUS_BIG_CIRCLE * 2, Config.RADIUS_BIG_CIRCLE * 2);
        params.gravity = Gravity.CENTER;
        params.bottomMargin = Config.AD_CENTER_BOTTOM_MARGIN;
        mCenterADView = new CircleGiftView(getContext());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home4_ad_default);
        bitmap = Bitmap.createScaledBitmap(bitmap, RadiusPxxToPercent(232), RadiusPxxToPercent(122), true);
        mCenterADView.setImageBitmap(bitmap);
        mCenterADView.setScaleType(ImageView.ScaleType.CENTER);
//		mCenterADView.setImageResource(R.drawable.home4_ad_default);
        addView(mCenterADView, params);

        //拍照按钮
        params = new LayoutParams(Config.RADIUS_BIG_CIRCLE * 2, Config.RADIUS_BIG_CIRCLE * 2);
        params.gravity = Gravity.CENTER;
        params.topMargin = Config.CAMERA_CENTER_TOP_MARGIN;
        mCameraCircleImage = new CameraCirCleView(getContext());
        Bitmap bitmapCenter = BitmapFactory.decodeResource(getResources(), R.drawable.home4_camera_center);
//        bitmapCenter = Bitmap.createScaledBitmap(bitmapCenter, HeightPxToPercent(140), HeightPxToPercent(140), true);                         //140
        bitmapCenter = Bitmap.createScaledBitmap(bitmapCenter, RadiusPxxToPercent(212), RadiusPxxToPercent(212), true);                         //140
        mCameraCircleImage.setBitmapCenter(bitmapCenter);
		mCameraCircleImage.setOnTouchListener(mOnClickListener);
        addView(mCameraCircleImage, params);

        //底部
        mBottomArcView = new ImageView(getContext());
        params = new LayoutParams(Config.ARCH_WIDTH * 2, Config.ARCH_HEIGHT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mBottomArcView.setScaleType(ImageView.ScaleType.FIT_XY);
        mBottomArcView.setImageResource(R.drawable.home4_arc_background);
        mBottomArcView.setOnTouchListener(mOnClickListener);
        addView(mBottomArcView, params);

        mBottomAlbumTipView = new TextView(getContext());
        mBottomAlbumTipView.setGravity(Gravity.CENTER);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = HeightPxToPercent(27);
        mBottomAlbumTipView.setText(R.string.homepage_open_album);
        mBottomAlbumTipView.setTextColor(Color.WHITE);
        mBottomAlbumTipView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        addView(mBottomAlbumTipView,params);

        //左上角撤回
        {
            params = new LayoutParams(WidthPxxToPercent(72), HeightPxxToPercent(33));
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.topMargin = HeightPxToPercent(222);
            mTopLeftLine = new ImageView(getContext());
            mTopLeftLine.setVisibility(View.GONE);
            mTopLeftLine.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mTopLeftLine.setImageResource(R.drawable.home4_top_left_line);
            addView(mTopLeftLine, params);

            params = new LayoutParams(RadiusPxToPercent(SMALL_RADIUS_PRESENT * 2),RadiusPxToPercent(SMALL_RADIUS_PRESENT * 2));
            params.topMargin = HeightPxToPercent(218);
            params.leftMargin = WidthPxToPercent(44);
            mTopLeftAdWrapFr = new FrameLayout(getContext());
            RingDrawable ringDrawable = new RingDrawable();
            ringDrawable.setColor(0x87ffffff);
            ringDrawable.setStrokeWidth(RadiusPxToPercent(2));
            mTopLeftAdWrapFr.setPadding(RadiusPxToPercent(2),RadiusPxToPercent(2),RadiusPxToPercent(2),RadiusPxToPercent(2));
            mTopLeftAdWrapFr.setVisibility(View.GONE);
            mTopLeftAdWrapFr.setBackground(ringDrawable);
            addView(mTopLeftAdWrapFr,params);
            {
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                mTopLeftSmallLogo = new CircleGiftView(getContext());
//            mTopLeftSmallLogo.setVisibility(View.GONE);
                mTopLeftAdWrapFr.addView(mTopLeftSmallLogo, params);
            }

            params = new LayoutParams(SMALL_RADIUS * 2, SMALL_RADIUS * 2);
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.topMargin = HeightPxToPercent(218);
            params.leftMargin= WidthPxToPercent(44);
//        mTopLeftUndoTip = new UnDoCircleView(getContext());
            mTopLeftUndoTip = new TextView(getContext());
            mTopLeftUndoTip.setGravity(Gravity.CENTER);
            mTopLeftUndoTip.setTextColor(Color.WHITE);
            if (LanguageUtil.checkSystemLanguageIsChinese(getContext()))
            {
                mTopLeftUndoTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            } else
            {
                mTopLeftUndoTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            }
            mTopLeftUndoTip.setSingleLine();
            mTopLeftUndoTip.setText(R.string.homepage_undo);
            mTopLeftUndoTip.setBackgroundDrawable(new RingDrawable());
            mTopLeftUndoTip.setVisibility(View.GONE);
            addView(mTopLeftUndoTip, params);

            //箭头
            params = new LayoutParams(WidthPxToPercent(20), HeightPxToPercent(20));
            params.topMargin = HeightPxToPercent(254);       //1280 -1153
            params.leftMargin = SMALL_RADIUS * 2 + WidthPxxToPercent(72);
            mTopLeftAdArrow = new ImageView(getContext());
            mTopLeftAdArrow.setVisibility(View.GONE);
            mTopLeftAdArrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mTopLeftAdArrow.setImageResource(R.drawable.home4_top_left_arrow);
            addView(mTopLeftAdArrow, params);

        }
        {
            int btnBottom = HeightPxToPercent(162);
            int textBottom = HeightPxToPercent(130);
            int btnHorizontal= RadiusPxToPercent(69);
            int textHorizontal= RadiusPxToPercent(70);

            //美化入口
            mBeautyEntryBtn = new FrameLayout(getContext());
            mBeautyEntryBtn.setOnTouchListener(mOnClickListener);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            params.bottomMargin = btnBottom;
            addView(mBeautyEntryBtn, params);
            {
                ImageView logo = new ImageView(getContext());
                logo.setImageResource(R.drawable.home4_beauty_entry_logo);
                params = new LayoutParams(RadiusPxxToPercent(180), RadiusPxxToPercent(180));
                params.rightMargin = btnHorizontal;
                mBeautyEntryBtn.addView(logo, params);

                ImageView line = new ImageView(getContext());
                line.setImageResource(R.drawable.home4_beauty_entry_line);
                params = new LayoutParams(RadiusPxxToPercent(114), RadiusPxxToPercent(57));
                params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                mBeautyEntryBtn.addView(line, params);
            }

            mBeautyTip = new TextView(getContext());
            mBeautyTip.setMinWidth(RadiusPxxToPercent(180));
            mBeautyTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            mBeautyTip.setTextColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) (0.8f * 255)));
            mBeautyTip.setGravity(Gravity.CENTER);
            mBeautyTip.setText(R.string.homepage_beauty_entry);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            params.bottomMargin = textBottom;
            params.rightMargin = textHorizontal;
            addView(mBeautyTip, params);

            //直播入口
            mLiveEntryBtn = new FrameLayout(getContext());
            mLiveEntryBtn.setOnTouchListener(mOnClickListener);
            mLiveEntryBtn.setVisibility(View.GONE);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.bottomMargin = btnBottom;
            addView(mLiveEntryBtn, params);
            {
                mLiveLogo = new ImageView(getContext());
                mLiveLogo.setImageResource(R.drawable.home4_live_logo);
                params = new LayoutParams(RadiusPxxToPercent(180), RadiusPxxToPercent(180));
                params.leftMargin = btnHorizontal;
                mLiveEntryBtn.addView(mLiveLogo, params);

                ImageView line = new ImageView(getContext());
                line.setImageResource(R.drawable.home4_live_line);
                params = new LayoutParams(RadiusPxxToPercent(114), RadiusPxxToPercent(57));
                params.gravity = Gravity.BOTTOM;
                mLiveEntryBtn.addView(line, params);
            }

            mLiveTip = new TextView(getContext());
            mLiveTip.setVisibility(View.GONE);
            mLiveTip.setMinWidth(RadiusPxxToPercent(180));
            mLiveTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            mLiveTip.setTextColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) (0.8f * 255)));
            mLiveTip.setGravity(Gravity.CENTER);
            mLiveTip.setText(R.string.homepage_live_entry);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.bottomMargin = textBottom;
            params.leftMargin = textHorizontal;
            addView(mLiveTip, params);
        }

        mAnimationLeft = HomeUtils.getAnimationType(HomeUtils.LEFT_TO_RIGHT);
        mAnimationBottom = HomeUtils.getAnimationType(HomeUtils.BOTTOM_TO_TOP);
        mAnimationRight = HomeUtils.getAnimationType(HomeUtils.RIGHT_TO_LEFT);
        mAnimationTop = HomeUtils.getAnimationType(HomeUtils.TOP_TO_BOTTOM);

        int duration = 500;
        ValueAnimator.AnimatorUpdateListener rotationXListener = new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float val = (float) animation.getAnimatedValue();
                mLeftTipParent.setRotationX(val);
                mRightTipParent.setRotationX(val);
            }
        };
        ValueAnimator.AnimatorUpdateListener alphaListener = new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float val = (float) animation.getAnimatedValue();
                mLeftTipParent.setAlpha(val);
                mRightTipParent.setAlpha(val);
            }
        };
        ValueAnimator animator1 = new ValueAnimator();
        animator1.setDuration(duration);
        animator1.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if(mIsShowLocalOrNet){
                    mLeftTipView.setText(R.string.homepage_left_tip);
                    mRightTipTextView.setText(R.string.homepage_right_tip);
                }else{
                    mLeftTipView.setText(mLeftText);
                    mRightTipTextView.setText(mRightText);
                }
                mIsShowLocalOrNet = !mIsShowLocalOrNet;
            }
        });
        ValueAnimator animator2 = new ValueAnimator();
        animator2.setDuration(duration);
        animator2.setStartDelay(duration+50);

        if(HomeUtils.isEMUISystem()){
            animator1.setFloatValues(1,0);
            animator1.addUpdateListener(alphaListener);
            animator2.setFloatValues(0,1);
            animator2.addUpdateListener(alphaListener);
        }else{
            animator1.setFloatValues(0,90);
            animator1.addUpdateListener(rotationXListener);
            animator2.setFloatValues(90,0);
            animator2.addUpdateListener(rotationXListener);
        }
        mAnimationText = new AnimatorSet();
        mAnimationText.playTogether(animator1,animator2);
    }

    /**
     * 读取中间左右要显示的文字
     */
    private void getSwitchText()
    {
        List<OtherFeature> features= AppFeatureManager.getInstance().getHomePageFeature();
        for (int i = 0; i < features.size(); i++)
        {
            OtherFeature feature = features.get(i);
            if(feature.getFeature() == FeatureType.HOMEPAGE_LEFT_TEXT){
                mLeftText = feature.getTitle();
            }
            if(feature.getFeature() == FeatureType.HOMEPAGE_RIGHT_TEXT){
                mRightText = feature.getTitle();
                mRightNewTime = feature.getTime();
                String  lastNewTime = TagMgr.GetTagValue(getContext(),Tags.HOME_THEME_NEW_TIME);
                String curNewTime = feature.getTime();
                if(TextUtils.isEmpty(lastNewTime) || !lastNewTime.equals(curNewTime)){
                    mIsRightThemeHasNew = true;
                    checkNewState();
                }
            }
        }
    }

    /**
     * 检查是否显示对应的view
     */
    public void checkViewState()
    {
        if(mIsShowUndoBtn){
            mTopLeftAdArrow.setVisibility(View.GONE);
            mTopLeftAdWrapFr.setVisibility(View.GONE);
            mTopLeftLine.setVisibility(View.VISIBLE);
            mTopLeftUndoTip.setVisibility(View.VISIBLE);
        }else if(mIsShowSmallAD){
            mTopLeftUndoTip.setVisibility(View.GONE);
            mTopLeftAdWrapFr.setVisibility(View.VISIBLE);
            mTopLeftAdArrow.setVisibility(View.VISIBLE);
            mTopLeftLine.setVisibility(View.VISIBLE);
        }else{
            mTopLeftLine.setVisibility(View.GONE);
            mTopLeftAdWrapFr.setVisibility(View.GONE);
            mTopLeftUndoTip.setVisibility(View.GONE);
        }

        if(mIsShowLiveEntry){
            mLiveTip.setVisibility(View.VISIBLE);
            mLiveEntryBtn.setVisibility(View.VISIBLE);
        }else{
            mLiveEntryBtn.setVisibility(View.GONE);
            mLiveTip.setVisibility(View.GONE);
        }
    }

    public void isShowUnDoBtn(boolean isShow) {
        mIsShowUndoBtn = isShow;
        checkViewState();
    }

    /**
     * 检查红点
     */
    public void checkNewState()
    {
        setMenuNewState(sCommunityMsgNum, sIsLeftMenuHasNew | sIsSlideBarHasNew);
        setThemeNewState(mIsRightThemeHasNew);
    }

    private void setMenuNewState(int num, boolean hasNew)
    {
        if((num > 0 && sLastCommunityMsgNum == 0) || (num > 0 && sLastCommunityMsgNum >0 && num != sLastCommunityMsgNum)){
            mLeftTextNum.setVisibility(View.VISIBLE);
            mLeftRedPoint.setVisibility(View.GONE);
            if(num > 99)
            {
                mLeftTextNum.setText(String.valueOf(99+"+"));
            }else{
                mLeftTextNum.setText(String.valueOf(num));
            }
        }else{
            if(hasNew){
                mLeftRedPoint.setVisibility(View.VISIBLE);
                mLeftTextNum.setVisibility(View.GONE);
            }else{
                mLeftRedPoint.setVisibility(View.GONE);
                mLeftTextNum.setVisibility(View.GONE);
            }
        }
    }
    private void setThemeNewState(boolean isHasRed)
    {
        if(isHasRed){
            mRightRedPoint.setVisibility(View.VISIBLE);
        }else{
            mRightRedPoint.setVisibility(View.GONE);
        }
    }

    protected boolean mIsShowLocalOrNet;   //福利社和向右滑切换
    protected boolean mIsTextAnimCycle = false; //图标两个周期停留时间
    protected void startTipAnimation() {
        mCenterADView.setPaused(false);
        mRopeAnimationView.setIntercept(false);
        mTopLeftSmallLogo.setPaused(false);

        mLeftArrowImage.setVisibility(View.VISIBLE);
        mLeftArrowImage.startAnimation(mAnimationLeft);
        mBottomArrowImage.setVisibility(View.VISIBLE);
        mBottomArrowImage.startAnimation(mAnimationBottom);
        mRightArrowImage.setVisibility(View.VISIBLE);
        mRightArrowImage.startAnimation(mAnimationRight);
        mTopArrowImage.setVisibility(View.VISIBLE);
        mTopArrowImage.startAnimation(mAnimationTop);
        mLeftTipParent.setVisibility(View.VISIBLE);
//        mLeftTipParent.startAnimation(mAnimationText);
        if(mIsTextAnimCycle)
        {
            getSwitchText();
            mAnimationText.start();
        }
        mAnimationTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                //一定要这样做，不然动画在播放完毕的最后一帧会闪烁闪烁
                //初步猜测可能onAnimationEnd函数里面动画还没有播放完毕
                MainPage.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTopArrowImage.clearAnimation();
                        mLeftArrowImage.clearAnimation();
                        mRightArrowImage.clearAnimation();
                        mBottomArrowImage.clearAnimation();
//                        mLeftTipParent.clearAnimation();
                        mAnimationText.cancel();
                        mIsTextAnimCycle = !mIsTextAnimCycle;
                        startTipAnimation();
                    }
                }, 10);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    protected void stopTipAnimation() {
        mRopeAnimationView.setIntercept(true);
        mCenterADView.setPaused(true);
        mTopLeftSmallLogo.setPaused(true);

        mAnimationTop.setAnimationListener(null);
        mLeftArrowImage.setVisibility(View.GONE);
        mLeftArrowImage.clearAnimation();
        mBottomArrowImage.setVisibility(View.GONE);
        mBottomArrowImage.clearAnimation();
        mRightArrowImage.setVisibility(View.GONE);
        mRightArrowImage.clearAnimation();
        mTopArrowImage.setVisibility(View.GONE);
        mTopArrowImage.clearAnimation();

        mIsTextAnimCycle = false;
    }

    protected OnAnimationClickListener mOnClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mBottomArcView) {
                onClick(BOTTOM);
            }else if(v == mCameraCircleImage){
                onClick(CAMERA);
            }else if(v == mBeautyEntryBtn){
                onClick(RIGHT_BOTTOM);
            }else if(v == mLiveEntryBtn){
                onClick(LEFT_BOTTOM);
            }
        }
    };

    protected OnHomeAnimationClickListener mOnCircleClickListener = new OnHomeAnimationClickListener() {
        @Override
        public void onAnimationClickStart(View v)
        {

        }

        @Override
        public void onAnimationClick(View v) {
            if (v == mCenterADView && mCenterAdRes != null) {
                onClick(CENTER_AD);
            }
        }
    };

    private ConnectionsManager.RequestDelegate mGetDataDelagete = new ConnectionsManager.RequestDelegate() {
        @Override
        public void run(Object response, ConnectionsManager.NetWorkError error) {
            // 成功获取服务器数据
            String json = "";
            if (error == null) {
                CampaignApi campaignApi = (CampaignApi) response;
                mIsShowLiveEntry = false;
                if (campaignApi != null) {
                    if (campaignApi.mCustomBeautyData != null && campaignApi.mLiveData.size() > 0) {
                        json = campaignApi.json;
                        int random = (int) (Math.random() * campaignApi.mLiveData.size() + 0.5f) %campaignApi.mLiveData.size();
                        if(random < campaignApi.mLiveData.size()){
                            final String url= campaignApi.mLiveData.get(random).getOpenUrl();
                            final String title = campaignApi.mLiveData.get(random).getTitle();
                            String img = campaignApi.mLiveData.get(random).getCoverUrl();
                            if(url != null) {
                                final Context context = getContext();
                                if(context instanceof Activity && !((Activity)context).isDestroyed())
                                {
                                    Glide.with(context).load(img)
                                         .asBitmap()
                                         .transform(new CropCircleTransformation(context))
                                         .into(new SimpleTarget<Bitmap>() {
                                             @Override
                                             public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                 if (resource != null) {
                                                     mIsShowLiveEntry = true;
                                                     mLiveUrl = url;
                                                     mLiveTip.setText(title);
                                                     mLiveLogo.setImageBitmap(resource);
                                                     checkViewState();
                                                 }
                                             }
                                         });
                                }
                            }
                        }
                    }
                }
                checkViewState();
            }
            TagMgr.SetTagValue(MyFramework2App.getInstance().getApplicationContext(), Tags.HOME_LIVE_JSON, json);
            // 网络原因，无法获取数据

        }
    };

    private EventCenter.OnEventListener mOnEventListener = new EventCenter.OnEventListener()
    {
        @Override
        public void onEvent(int eventId, Object[] params)
        {
            //正在菜单模式下不显示红点
            if (eventId == EventID.HOME_MENU_NEW_STATE)
            {
                if (params.length > 0)
                {
                    sIsSlideBarHasNew = (boolean) params[0];
                    checkNewState();
                }
            } else if (eventId == EventID.NOTIFY_HOMEPAGE_TIPS)
            {
                sIsLeftMenuHasNew = true;
                checkNewState();
            } else if (eventId == EventID.NOTIFY_HOMEPAGE_CHAT)
            {
                if (params.length > 0)
                {
                    sCommunityMsgNum = (int) params[0];
                    checkNewState();
                }
            }else if(eventId == EventID.NOTIFY_HOMEPAGE_RIGHT_TIPS){
               getSwitchText();
            }
            if(mCurActiveMode == Home4Page.MENU){
                clearMenuNewState();
            }
        }
    };

    protected void onClick(int mode){
        if(mUiEnable)
        {
            if (mOnClickAreaCallBack != null)
            {
                mOnClickAreaCallBack.onAreaClick(mode);
            }
        }
    }

    protected String GetThumb(String[] arr) {
        String out = null;
        if (arr != null && arr.length > 0) {
            out = arr[0];
        }
        return out;
    }

    protected HomeAd.Callback mAdListener = new HomeAd.Callback() {
        @Override
        public void Show(final AbsAdRes res) {
            if (res != null) {
                String thumb = null;
                if (res instanceof AbsFullscreenAdRes) {
                    thumb = GetThumb(((AbsFullscreenAdRes) res).mAdm);
                    if (((AbsFullscreenAdRes) res).m_type == BaseRes.TYPE_NETWORK_URL) {
                        DownloadMgr.getInstance().DownloadRes(res, false, null);
                    }
                } else if (res instanceof AbsClickAdRes) {
                    thumb = GetThumb(((AbsClickAdRes) res).mAdm);
                } else if (res instanceof AbsChannelAdRes) {
                    thumb = GetThumb(((AbsChannelAdRes) res).mAdm);
                }
                if(res instanceof IAdSkin)
                {
                    if(sIsSkipAnim ||  !TextUtils.isEmpty(mAdSkinResId))
                    {
                        thumb = ((IAdSkin) res).getSkinCover();
                    }
                }
                if (thumb != null && thumb.length() > 0) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(thumb);
//                    if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
//                        mCenterAdRes = res;
//                        changeADBitmap(bitmap);
//                    }
                    if (mCenterADView != null) {
                        mCenterADView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        boolean result = mCenterADView.setImagePath(thumb);
                        if(result)
                        {
                            mCenterAdRes = res;
                            mCenterADView.setOnTouchListener(mOnCircleClickListener);
                        }
                    }
                }
            }
        }
    };

    protected void changAdPath(String path)
    {
        if (mCenterADView != null) {
            mCenterADView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            boolean result = mCenterADView.setImagePath(path);
            if(result)
            {
                mCenterADView.setOnTouchListener(mOnCircleClickListener);
            }
        }
    }

    /**
     * @param bitmap
     */
    protected void changeADBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap = Bitmap.createScaledBitmap(bitmap, Config.RADIUS_BIG_CIRCLE * 2, Config.RADIUS_BIG_CIRCLE * 2, true);
            bitmap = ImageUtil.makeCircleBmp(bitmap, 0, 0);
            if (mCenterADView != null) {
                mCenterADView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mCenterADView.setImageBitmap(bitmap);
                mCenterADView.setOnTouchListener(mOnCircleClickListener);
            }
        }
    }

    public void startGravity() {
        mySensor.onStart();
    }

    public void stopGravity() {
        mySensor.onStop();
    }

    private MainSensorListener sensorListener = new MainSensorListener();


    protected void checkThemeSkin()
    {
        if (checkIsClearAdTheme()) {
            clearAdTheme(0);
        } else {
            resume();
        }
    }

    protected int mCurActiveMode;

    @Override
    public void onPageActive(int lastActiveMode)
    {
        //有些机有bug,控件隐藏了
        if(!Home4Page.isFirst)
        {
            setViewVisible();
        }
        mCurActiveMode = Home4Page.HOME;
        TongJiUtils.onPageStart(getContext(), R.string.首页);
        MyBeautyStat.onPageStartByRes(R.string.首页_首页_主页面);
        if(lastActiveMode != Home4Page.NONE)
        {
            checkThemeSkin();
        }
        if(lastActiveMode == Home4Page.MENU){
            clearMenuNewState();
        }else if(lastActiveMode == Home4Page.MATERIAL){
            clearThemeNewState();
        }
    }

    @Override
    public void onPageInActive(int nextActiveMode)
    {
        mCurActiveMode = nextActiveMode;
        mUiEnable = false;
        TongJiUtils.onPageEnd(getContext(), R.string.首页);
        MyBeautyStat.onPageEndByRes(R.string.首页_首页_主页面);

        if(nextActiveMode == Home4Page.MENU)
        {
            clearMenuNewState();
        }else if(nextActiveMode == Home4Page.MATERIAL){
            clearThemeNewState();
        }
    }

    private void clearMenuNewState()
    {
        if (sIsSlideBarHasNew)
        {
            //滑动侧滑栏的动作时调用一下
            BMCheckNewTopic.slideHost(MyApplication.getInstance());
        }
        sLastCommunityMsgNum = sCommunityMsgNum;
        sIsLeftMenuHasNew = false;
        sIsSlideBarHasNew = false;
        checkNewState();
    }

    private void clearThemeNewState()
    {
        if(mIsRightThemeHasNew && !TextUtils.isEmpty(mRightNewTime))
        {
            TagMgr.SetTagValue(getContext(),Tags.HOME_THEME_NEW_TIME,mRightNewTime);
        }
        mIsRightThemeHasNew = false;
        checkNewState();
    }


    @Override
    public void setUiEnable(boolean uiEnable)
    {
        mUiEnable = uiEnable;
        if(uiEnable){
            resume();
        }else{
            pause();
        }
    }

    public interface OnCallback {

        void setUiEnable(boolean isEnable);

        void onIntroAnimEnd();

        void onThemeChange();

        //触摸左下角且有广告
        void isOnTouchTopLeft(boolean isTouchLeftBottom);

        void isOnTouchRightBottom(boolean isTouchRightBottom);

        void onAreaClick(int mode);
    }

    public AbsBootAdRes getLeftAdRes() {
        if (mIsShowSmallAD) {
            return mLeftAdRes;
        } else {
            return null;
        }
    }

    public String getLiveString()
    {
        return mLiveUrl;
    }

    public AbsAdRes getCenterAdRes() {
        return mCenterAdRes;
    }

    public void setViewGone() {
        mTopArrowImage.setVisibility(View.INVISIBLE);
        mLeftArrowImage.setVisibility(View.INVISIBLE);
        mRightArrowImage.setVisibility(View.INVISIBLE);
        mBottomArrowImage.setVisibility(View.INVISIBLE);
        mTopTipView.setVisibility(View.GONE);
        mLeftTipParent.setVisibility(View.GONE);
        mRightTipParent.setVisibility(View.GONE);
        mCenterAdRing.setVisibility(View.GONE);
        mCenterADView.setVisibility(View.GONE);
        mCameraCircleImage.setVisibility(View.GONE);
        mTopLeftAdArrow.setVisibility(View.GONE);
        mTopLeftAdWrapFr.setVisibility(View.GONE);
//        mAdLine.setVisibility(View.GONE);
        mBottomArcView.setVisibility(View.GONE);
        mBottomAlbumTipView.setVisibility(View.GONE);
        mBeautyTip.setVisibility(View.GONE);
        mBeautyEntryBtn.setVisibility(View.GONE);
        mLiveEntryBtn.setVisibility(View.GONE);
        mLiveTip.setVisibility(View.GONE);
    }

    public void setViewVisible() {
        mCenterAdRing.setVisibility(View.VISIBLE);
        mCenterAdRing.setAlpha(1f);
        mCenterADView.setVisibility(View.VISIBLE);
        mCenterADView.setAlpha(1f);
        mCameraCircleImage.setVisibility(View.VISIBLE);
        mCameraCircleImage.setAlpha(1f);

        checkViewState();

        mBottomArcView.setVisibility(View.VISIBLE);
        mBottomArcView.setAlpha(1f);
        mBottomAlbumTipView.setVisibility(View.VISIBLE);
        mBottomAlbumTipView.setAlpha(1f);

        mTopTipView.setVisibility(View.VISIBLE);
        mTopTipView.setAlpha(1f);
        mLeftTipParent.setVisibility(View.VISIBLE);
        mLeftTipParent.setAlpha(1f);
        mRightTipParent.setVisibility(View.VISIBLE);
        mRightTipParent.setAlpha(1f);

        mBeautyTip.setVisibility(View.VISIBLE);
        mBeautyTip.setAlpha(1f);
        mBeautyEntryBtn.setVisibility(View.VISIBLE);
        mBeautyEntryBtn.setAlpha(1f);
    }

    public void startIntroAnimation() {
        setViewGone();
        mIntroView = new IntroAnimationView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mIntroView, params);
        mIntroController = mIntroView.getAnimationController();
        mIntroController.setAnimationCallBack(new AnimationController.AnimationCallBack() {
            @Override
            public void onAnimationStart() {
                if (mOnClickAreaCallBack != null) {
                    mOnClickAreaCallBack.setUiEnable(false);
                }
            }

            @Override
            public void onAnimationEnd() {
                setViewVisible();
                if (mOnClickAreaCallBack != null) {
                    mOnClickAreaCallBack.setUiEnable(true);
                }
                if (mOnClickAreaCallBack != null) {
                    mOnClickAreaCallBack.onIntroAnimEnd();
                }
            }

            @Override
            public void onCirCleFadeInStart() {
                checkViewState();

                mCenterADView.setVisibility(View.VISIBLE);
                mCenterAdRing.setVisibility(View.VISIBLE);
                mCameraCircleImage.setVisibility(View.VISIBLE);
                mTopTipView.setVisibility(View.VISIBLE);
                mLeftTipParent.setVisibility(View.VISIBLE);
                mRightTipParent.setVisibility(View.VISIBLE);

                mBeautyEntryBtn.setVisibility(View.VISIBLE);
                mBeautyTip.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCirCleFadeIn(float alpha) {
                mCenterAdRing.setAlpha(1 - alpha);
                mCenterADView.setAlpha(1 - alpha);
                mCameraCircleImage.setAlpha(1 - alpha);
                mTopLeftAdArrow.setAlpha(1 - alpha);
                mTopLeftAdWrapFr.setAlpha(1 - alpha);
                mTopLeftLine.setAlpha(1 - alpha);
//                mAdLine.setAlpha(1 - alpha);
                mTopTipView.setAlpha(1 - alpha);
                mLeftTipParent.setAlpha(1 - alpha);
                mRightTipParent.setAlpha(1 - alpha);

                mBeautyEntryBtn.setAlpha(1 - alpha);
                mBeautyTip.setAlpha(1 - alpha);
                mLiveEntryBtn.setAlpha(1 - alpha);
                mLiveTip.setAlpha(1 - alpha);
            }

            @Override
            public void onArchFadeIn(float alpha, float scale) {
                mBottomArcView.setAlpha(1 - alpha);
                mBottomAlbumTipView.setAlpha(1 - alpha);
                mBottomArcView.setScaleX(scale);
                mBottomArcView.setScaleY(scale);
            }

            @Override
            public void onArchFadeStart() {
                mBottomArcView.setVisibility(View.VISIBLE);
                mBottomArcView.setAlpha(0f);
                mBottomAlbumTipView.setVisibility(View.VISIBLE);
                mBottomAlbumTipView.setAlpha(0f);
            }
        });
        mIntroController.startAnimation();
    }

    @Override
    public void onBack() {

    }

    public void resume() {
        if(!Home4Page.isFirst)
        {
            startGravity();
            startTipAnimation();
        }
    }

    public void pause() {
        stopTipAnimation();
        stopGravity();
    }

    @Override
    public void onResume() {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.首页);
        if(!Home4Page.isFirst)
        {
            //有些机有bug,控件隐藏了
            setViewVisible();

            resume();
            if (checkIsClearAdTheme())
            {
                clearAdTheme(0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.首页);
        pause();
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        super.onPageResult(siteID, params);
        checkThemeSkin();
    }

    @Override
    public void onClose() {
        super.onClose();
        ThemeResMgr2.ClearHolder();
        EventCenter.removeListener(mOnEventListener);
        if (mBusinessCore != null) {
            mBusinessCore.Clear();
            mBusinessCore = null;
        }
        pause();
        if (mRopeAnimationView != null) {
            mRopeAnimationView.onClose();
        }
        mGetDataDelagete = null;
    }

    @Override
    public void setTranslationY(float translationY) {
        if (translationY < 0) {
            super.setTranslationY(0);
            mArcBackground.setArcHeight((int) translationY);
            mArcBackground.setTranslationY(0);
            mRopeAnimationView.setTranslationY(0);
        } else {
            super.setTranslationY(translationY);
            if (translationY <= ArcBackGroundView.ARC_MAX_HEIGHT) {
                //拖拽，上移错位，看上去停留一样
                mArcBackground.setTranslationY((int) -translationY);
                mArcBackground.setArcHeight((int) translationY);
                mRopeAnimationView.setTranslationY((-translationY * 0.5f));
            } else {
                mArcBackground.setArcHeight(ArcBackGroundView.ARC_MAX_HEIGHT);
                mArcBackground.setTranslationY(-ArcBackGroundView.ARC_MAX_HEIGHT);
                mRopeAnimationView.setTranslationY(-ArcBackGroundView.ARC_MAX_HEIGHT * 0.5f);
            }
        }
    }

    @Override
    public float getTranslationY() {
        return super.getTranslationY();
    }

    protected boolean isShowAdDialog() {
        final AbsAdRes res = mCenterAdRes;
        boolean isShow = false;
        TongJi2.AddOnlineClickCount(getContext(), res.mAdId, R.integer.首页_点击大圆商业, getContext().getString(R.string.首页));
        MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_点击大圆商业);
        if (res instanceof IAdSkin)
        {
            String skinCover = ((IAdSkin) res).getSkinCover();
            if(!sIsSkipAnim && TextUtils.isEmpty(mAdSkinResId)){
                isShow = true;
                final CommonSkinDialog adDialog = new CommonSkinDialog((Activity) getContext(), res);
                final String finalSkinCover = skinCover;
                adDialog.setCallBack(new CommonSkinDialog.CallBack()
                {
                    @Override
                    public void onNo()
                    {
                        sIsSkipAnim = true;
//                        changeADBitmap(BitmapFactory.decodeFile(finalSkinCover));
                        changAdPath(finalSkinCover);
                        adDialog.dismiss();
                    }

                    @Override
                    public void onYes()
                    {
                        pause();
//                        changeADBitmap(BitmapFactory.decodeFile(finalSkinCover));
                        changAdPath(finalSkinCover);
                    }

                    @Override
                    public void onAnimationEnd(String skinBg, String skinCover)
                    {
                        adDialog.dismiss();
                        mAdSkinResId = res.mAdId;
                        TagMgr.SetTagValue(getContext(), Tags.HOME_SKIN_AD_RES_ID, res.mAdId);
                        startAdSkinTheme(skinBg, 800);
                    }
                });
                adDialog.show();
            }
        }
        return isShow;
    }

    class MainSensorListener implements GravitySensor.SensorListener {
        /**
         * 屏幕平躺向左 x为正，向左平移，
         * <p>
         * x，y位置不断改变， 上一次的位置不断跟踪向其靠近
         */
        float lastX = 0;
        float lastY = 0;
        float curX = 0;
        float curY = 0;
        float ratio = 1.5f;
        int gravityY = 6;

        @Override
        public void doRotate(final float x, final float y) {
            //不断获取并向目标位置移动
            curX = -RadiusPxToPercent((int) (x * ratio));
            if (y >= 0 && y <= gravityY) {
                curY = 0;
            } else if (y >= gravityY) {
                curY = RadiusPxToPercent((int) ((y - gravityY) * ratio * 2));
            } else {
                curY = RadiusPxToPercent((int) (y * ratio));
            }
            //根据距离提高速度
            float dx = (curX - lastX) / 5;
            float dy = (curY - lastY) / 5;
            if (Math.abs(dx) > 1) {
                if (dx > 0) {
                    dx = 1;
                } else {
                    dx = -1;
                }
            }
            if (Math.abs(dy) > 1) {
                if (dy > 0) {
                    dy = 1;
                } else {
                    dy = -1;
                }
            }
            //	//防抖动
            if (Math.abs(curX - lastX) >= 1f || Math.abs(curY - lastY) >= 1f) {
                lastX += dx;
                lastY += dy;
                mCenterADView.setTranslationX(lastX);
                mCenterADView.setTranslationY(lastY);
                mCameraCircleImage.setTranslationX(lastX);
                mCameraCircleImage.setTranslationY(lastY);
            }
        }
    }

    protected boolean checkIsClearAdTheme() {
        boolean b = false;
        if (!TextUtils.isEmpty(mAdSkinResId)) {
            String needShowSkinPath = HomeUtils.getSkinPath(getContext(), mAdSkinResId);
            if (TextUtils.isEmpty(needShowSkinPath)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 清除商业皮肤图片，替换缩略图，清除记录
     * @param isDelay
     */
    protected void clearAdTheme(int isDelay) {
        if (!TextUtils.isEmpty(mAdSkinResId)) {
            //找回原来的商业资源，恢复缩略图
           AbsAdRes adRes = null;
            if(mCenterAdRes != null && mCenterAdRes.mAdId.equals(mAdSkinResId))
            {
                //当前显示的是皮肤对应的商业资源
               adRes = HomeAd.GetOneHomeRes(getContext(), mAdSkinResId);
            }
            //清除记录
            mAdSkinResId = null;
            TagMgr.SetTagValue(getContext(), Tags.HOME_SKIN_AD_RES_ID,"");
            mArcBackground.setSkinTheme(false, null);
            final ObjectAnimator alpha = ObjectAnimator.ofInt(mArcBackground, "AdAlpha", 255, 0);
            alpha.setStartDelay(isDelay);
            alpha.setDuration(ArcBackGroundView.DURATION);
            final AbsAdRes finalAdRes = adRes;
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    pause();
                    isShowUnDoBtn(false);
                   if(finalAdRes != null){
                       mAdListener.Show(finalAdRes);
                   }
                    if (mOnClickAreaCallBack != null) {
                        mOnClickAreaCallBack.setUiEnable(false);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mArcBackground.setSkinBitmap(null);
                    if (mOnClickAreaCallBack != null) {
                        mOnClickAreaCallBack.setUiEnable(true);
                    }
                    if (mOnClickAreaCallBack != null) {
                        mOnClickAreaCallBack.onThemeChange();
                    }
                }
            });
            alpha.start();
        }else{
            mAdSkinResId = null;
            TagMgr.SetTagValue(getContext(), Tags.HOME_SKIN_AD_RES_ID,"");
            mArcBackground.setSkinBitmap(null);
            isShowUnDoBtn(false);
            if (mOnClickAreaCallBack != null) {
                mOnClickAreaCallBack.onThemeChange();
            }
        }
    }

    /**
     * 开始商业皮肤动画
     * @param skinPath
     * @param duration
     */
    protected void startAdSkinTheme(final String skinPath, final int duration) {
        mArcBackground.setSkinBitmap(BitmapFactory.decodeFile(skinPath));
        final ObjectAnimator alpha = ObjectAnimator.ofInt(mArcBackground, "AdAlpha", 0, 255);
        alpha.setDuration(duration);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                pause();
                isShowUnDoBtn(true);
                if (mOnClickAreaCallBack != null) {
                    mOnClickAreaCallBack.setUiEnable(false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnClickAreaCallBack != null) {
                    mOnClickAreaCallBack.setUiEnable(true);
                }
                if (mOnClickAreaCallBack != null) {
                    mOnClickAreaCallBack.onThemeChange();
                }
            }
        });
        alpha.start();
    }

    /**
     * 主题换肤， 如果有商业皮肤要清除
     * @param delay
     */
    protected void changeThemeSkin(int delay) {
        if (TextUtils.isEmpty(mAdSkinResId)) {
            pause();
            mArcBackground.setSkinBitmap(null);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mArcBackground.setSkinTheme(new ArcBackGroundView.Callback() {
                        @Override
                        public void onStart() {
                            pause();
                            if (mOnClickAreaCallBack != null) {
                                mOnClickAreaCallBack.setUiEnable(false);
                            }
                        }

                        @Override
                        public void onEnd() {
                            mArcBackground.setSkinBitmap(null);
                            if (mOnClickAreaCallBack != null) {
                                mOnClickAreaCallBack.setUiEnable(true);
                            }
                            if (mOnClickAreaCallBack != null) {
                                mOnClickAreaCallBack.onThemeChange();
                            }
                        }
                    });
                }
            }, delay);
        } else {
            clearAdTheme(delay);
        }
    }

    public RopeAnimationView getRopeAnimationView() {
        return mRopeAnimationView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //点击区域的半径范围
        int radius = PercentUtil.WidthPxToPercent(50);
        int topMargin = PercentUtil.WidthPxToPercent(26);
        int bottomMargin = PercentUtil.WidthPxToPercent(80);
        //顶部可以点击去运营中心的区域
        mTopArea1.set(0, 0, h, ARC_TOP_MARGIN + ARC_HEIGHT);
        mTopArea1.set(0, 0, h, ARC_TOP_MARGIN + ARC_HEIGHT);
        mTopArea2.set(w / 2 - radius, ARC_TOP_MARGIN + ARC_HEIGHT, w / 2 + radius, ARC_TOP_MARGIN + ARC_HEIGHT + radius * 2);
        //4个箭头提示的点击范围
        mLeftArea.set(0, h / 2 - radius + topMargin, 2 * radius, h / 2 + radius + topMargin);
        mRightArea.set(w - 2 * radius, h / 2 - radius + topMargin, w, h / 2 + radius + topMargin);
        mBottomArea1.set(w / 2 - radius, h - bottomMargin - radius * 2, w / 2 + radius, h - bottomMargin);
        mBottomArea2.set(w / 2 - Config.ARCH_WIDTH, h - Config.ARCH_HEIGHT, w / 2 + Config.ARCH_WIDTH, h);
        //左下角，右上角等范围
        int width = PercentUtil.WidthPxToPercent(120);
        int height = PercentUtil.WidthPxToPercent(80);
        mLeftBottomArea.set(0, h - bottomMargin - height, width, h - bottomMargin);
//        mRightBottomArea.set(w - width, h - bottomMargin - height, w, h - bottomMargin);
        mLeftTopArea.set(0,HeightPxToPercent(218),width,HeightPxToPercent(300));
        mRightBottomArea.set(w - WidthPxToPercent(120+70), h - HeightPxToPercent(170+120), w, h - HeightPxToPercent(134));
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            //onDown时重设下触摸位置
            if(mOnClickAreaCallBack != null){
                mOnClickAreaCallBack.isOnTouchTopLeft(false);
//                mOnClickAreaCallBack.isOnTouchRightBottom(false);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mClickMode = 0;
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (mOnClickAreaCallBack != null) {
                    if(mLeftTopArea.contains(x,y)){
                        mClickMode = LEFT_TOP;
                    }else if (mLeftBottomArea.contains(x, y)) {
                        mClickMode = LEFT_BOTTOM;
                    } else if (mRightBottomArea.contains(x, y)) {
                        mClickMode = RIGHT_BOTTOM;
                    } else if (mTopArea1.contains(x, y) || mTopArea2.contains(x, y)) {
                        mClickMode = TOP;
                    } else if (mLeftArea.contains(x, y)) {
                        mClickMode = LEFT;
                    } else if (mRightArea.contains(x, y)) {
                        mClickMode = RIGHT;
                    } else if (mBottomArea1.contains(x, y) || mBottomArea2.contains(x, y)) {
                        mClickMode = BOTTOM;
                    }

                    if (mClickMode == LEFT_TOP &&  mIsShowSmallAD && !mIsShowUndoBtn) {
                        mOnClickAreaCallBack.isOnTouchTopLeft(true);
                    } else {
                        mOnClickAreaCallBack.isOnTouchTopLeft(false);
                    }
                }
                if (mClickMode != 0) {
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if(mLeftTopArea.contains(x,y) && mClickMode == LEFT_TOP){
                    onClick(LEFT_TOP);
                } else if (mLeftBottomArea.contains(x, y) && mClickMode == LEFT_BOTTOM ) {
//                    onClick(LEFT_BOTTOM);
                } else if (mRightBottomArea.contains(x, y) && mClickMode == RIGHT_BOTTOM ) {
//                    onClick(RIGHT_BOTTOM);
                } else if ((mTopArea1.contains(x, y) || mTopArea2.contains(x, y)) && mClickMode == TOP) {
                    onClick(TOP);
                } else if (mLeftArea.contains(x, y) && mClickMode == LEFT) {
                    onClick(LEFT);
                } else if (mRightArea.contains(x, y) && mClickMode == RIGHT) {
                    onClick(RIGHT);
                } else if ((mBottomArea1.contains(x, y) || mBottomArea2.contains(x, y)) && mClickMode == BOTTOM) {
                    onClick(BOTTOM);
                }
                mClickMode = 0;
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private OnCallback mOnClickAreaCallBack;

    public void setClickAreaCallBack(OnCallback clickAreaCallBack) {
        this.mOnClickAreaCallBack = clickAreaCallBack;
    }

}

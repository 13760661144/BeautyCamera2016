package cn.poco.home.home4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.missionhall.utils.gz_Iutil.StoreTime;
import com.circle.common.chatlist.NotificationDataUtils;

import java.util.HashMap;

import cn.poco.MaterialMgr2.ThemeListPage;
import cn.poco.adMaster.BootAd;
import cn.poco.album.AlbumPage;
import cn.poco.banner.BannerCore3;
import cn.poco.bootimg.BootImgPage;
import cn.poco.business.FullScreenADPage;
import cn.poco.community.CommunityPage;
import cn.poco.featuremenu.manager.AppFeatureManager;
import cn.poco.featuremenu.model.FeatureType;
import cn.poco.featuremenu.page.FeatureMenuPage;
import cn.poco.framework.AnimatorHolder;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.userInfoMenu.LeftMenu;
import cn.poco.home.site.HomePageSite;
import cn.poco.image.filter;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.resource.BaseRes;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.taskCenter.MissionHelper;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

import static cn.poco.home.home4.HomeEventController.MOVE_TYPE_BOTTOM;
import static cn.poco.home.home4.HomeEventController.MOVE_TYPE_LEFT;
import static cn.poco.home.home4.HomeEventController.MOVE_TYPE_NONE;
import static cn.poco.home.home4.HomeEventController.MOVE_TYPE_RIGHT;
import static cn.poco.home.home4.HomeEventController.MOVE_TYPE_TOP;
import static cn.poco.home.home4.MainPage.BOTTOM;
import static cn.poco.home.home4.MainPage.CAMERA;
import static cn.poco.home.home4.MainPage.CENTER_AD;
import static cn.poco.home.home4.MainPage.LEFT;
import static cn.poco.home.home4.MainPage.LEFT_BOTTOM;
import static cn.poco.home.home4.MainPage.LEFT_TOP;
import static cn.poco.home.home4.MainPage.RIGHT;
import static cn.poco.home.home4.MainPage.RIGHT_BOTTOM;
import static cn.poco.home.home4.MainPage.TOP;
import static cn.poco.home.home4.utils.PercentUtil.RadiusPxToPercent;
import static cn.poco.utils.Utils.CheckSDCard;


/**
 * Created by lgd on 2016/11/22.
 */

public class Home4Page extends IPage
{
    protected static boolean isFirst = true;    // 开场动画，结束时为false
    protected boolean isBootOrMenu = false;  //  左滑手势判断是开机页true还是个人中心false
    protected boolean isBeautyOrAlbum = false;  //  右滑手势判断是美化入口true还是素材中心false
    protected boolean mIsLayoutFinish = false;
    protected int mCurMode = NONE;
    public final static int NONE = 0;
    public final static int ALBUM = 1;
    public final static int CAMPAIGN = 2;
    public final static int MATERIAL = 3;
    public final static int HOME = 4;
    public final static int MENU = 5;
    public final static int AD = 6;
    public final static int BOOT = 7;
    public final static int BEAUTY = 8;
    public final static String KEY_CUR_MODE = "cur_mode";
    public final static String KEY_CHECK_SKIN = "checkAdSkin";
    public final static String KEY_BOOT_IMG = "boot_img";
    public final static String KEY_TOP_DATA = "topData";
    public final static String KEY_COMMUNITY_URI = "community_uri";    //web版打开社区协议

    protected HomeDialogAgency mHomeDialogAgency;
    protected IPage mCurIPage;                //指向当前的模块view
    protected final HomePageSite mSite;
    protected FrameLayout mCenterPage;         //渐变背景，运营中心，MainPage加上去
    protected MainPage mMainLayout;
    protected CommunityPage mCommunityPage;
    protected ThemeListPage mRightTheme;
    protected AlbumPage mBottomAlbum;
    protected FeatureMenuPage mLeftMenu;
    protected BootImgPage mLeftBoot;
    protected FullScreenADPage mAdPage;
    protected BeautyEntryPage mBottomBeautyEntryPage;
    protected int mMenuWidth;
    protected HomeEventController mController;
    protected boolean isFirstInstall = false;            //引导页到主页，也是第一次安装的时候
    protected boolean mGestureEnable = true;           //是否能控制手势
    protected boolean mUiEnabled = true;                //主页事件控制  ，用于动画期间和点击切换页面，返回键控制,换肤 为false
    protected boolean mHasNoMemory = false;                        //是否内存不够

    public static final int OPEN_CLOUD_ALBUM = 1;
    public static final int OPEN_MY_CREDIT = 2;
    public static final int OPEN_WALLET = 3;
    public static final int OPEN_TASK_CENTER = 4;
    public static final int OPEN_CHAT = 5;
    public static final int OPEN_COMMENT = 6;
    public static final int OPEN_LIVE = 7;    //直播
    protected int m_openWhat = 0;

    protected AlertDialog mDialog;
    protected Bitmap m_maskBmp;
    public static String s_maskBmpPath;        //毛玻璃路径
    protected ImageView mGlassView;
    protected UpdateAppHandler mUpdateAppHandler;

    protected MissionHelper.BMCheckNewListener mBMCheckNewListener;
    private HashMap<String, Object> mMainData;
    private HashMap<String, Object> mTopData;
    private HashMap<String, Object> mAlbumData;
    /**
     * @param params delay : Boolean 是否延迟播放动画<br>
     *               boot_img : BootImgRes 开机启动页的广告<br>
     *               cmd : String 第三方打开软件携带的命令行例如open=xx这种
     */
    @Override
    public void SetData(HashMap<String, Object> params)
    {
//		mSite.m_myParams.put(KEY_ALBUM, true);
        mMainData = new HashMap<>();
        mTopData = new HashMap<>();
        if (params != null)
        {
            mMainData = (HashMap<String, Object>) params.clone();
            if (params.containsKey("delay"))
            {
                isFirstInstall = (boolean) params.get("delay");
                params.remove("delay");
            }
            if (params.containsKey(KEY_CUR_MODE))
            {
                mSite.m_myParams.put(KEY_CUR_MODE, params.get(KEY_CUR_MODE));
                params.remove(KEY_CUR_MODE);
            }
            if (params.containsKey(KEY_TOP_DATA))
            {
                mTopData.putAll((HashMap<String, Object>)params.get(KEY_TOP_DATA));
                params.remove(KEY_TOP_DATA);
            }
            if (params.containsKey(KEY_COMMUNITY_URI))
            {
                mTopData.put(KEY_COMMUNITY_URI,params.get(KEY_COMMUNITY_URI));
                params.remove(KEY_COMMUNITY_URI);
            }
            if(params.containsKey("cmd"))
            {
                final String cmd = (String)params.get("cmd");
                params.remove("cmd");
                this.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        BannerCore3.ExecuteCommand(getContext(), cmd, mSite.m_cmdProc);
                    }
                });
            }
        }
//        mSite.m_myParams.put(KEY_CUR_MODE,CAMPAIGN);
        if(mSite.m_myParams.containsKey(KEY_CUR_MODE)){
            int mode = (int) mSite.m_myParams.get(KEY_CUR_MODE);
            if(mode == ALBUM) {
                mAlbumData = new HashMap<>();
                mAlbumData.put("restore", true);
                isShowModuleView(true, ALBUM, false);
                mAlbumData.clear();
            }else
            {
                isShowModuleView(true, mode, false);
            }
        }
        AppFeatureManager.getInstance().getCurrentFeatureList(getContext());
        if (isFirst)
        {
            AppFeatureManager.getInstance().visitHangZhouServer(getContext());
//            mBMCheckNewListener = new MissionHelper.BMCheckNewListener();
//            mBMCheckNewListener.SetObj(this);
//            BMCheckNewTopic.checkNewTopic(mBMCheckNewListener);
            //更新对话框
            mUpdateAppHandler = new UpdateAppHandler(this);
            new UpdateAppThread(getContext(), mUpdateAppHandler).start();
            if (mCurMode == NONE)
            {
//                addModuleView(MENU,false);
                isShowModuleView(true,HOME,false);
                initIntroAnim();
            } else
            {
                isFirst = false;
            }
        } else
        {
            //如果不是第一次进主页
            if (mCurMode == NONE)
            {
                mMainData.put(KEY_CHECK_SKIN, true);
                isShowModuleView(true,HOME,false);
            }
        }
    }

    public Home4Page(Context context, BaseSite site)
    {
        super(context, site);
        mSite = (HomePageSite) site;
        mSite.setOnSiteCallBack(mOnSiteBack);
        mSite.m_cmdProc.SetCampaignCenterSite(mSite.mCampaignCenterSite);
        initData();
        initUI();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mIsLayoutFinish = true;
            }
        });
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        showOrHideStatusAndNavigation(visibility == View.GONE ? View.GONE : View.VISIBLE);
    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if (mCurMode != CAMPAIGN) {//社区
            showOrHideStatusAndNavigation(hasWindowFocus ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mCurMode != CAMPAIGN && mRealViewH < ShareData.m_screenRealHeight
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //解决从首页顶部下滑导致状态栏不隐藏的问题
            if (!mSystemUiVisibilityIsChange && bottom < mRealViewH) {
                mSystemUiVisibilityIsChange = true;
                ShareData.changeSystemUiVisibility(getContext(), true);
            } else {
                mSystemUiVisibilityIsChange = false;
            }
        }
    }

    private int mStatusAndNavigationVisible = -1;
    private boolean mSystemUiVisibilityIsChange;

    /**
     * 处理虚拟键的显示与隐藏
     * @param visible
     */
    private void showOrHideStatusAndNavigation(int visible) {
        if (visible != mStatusAndNavigationVisible) {
            if (visible == View.VISIBLE) {
                int systemUiVisibility = mCurMode == CAMPAIGN ? View.SYSTEM_UI_FLAG_VISIBLE : View.SYSTEM_UI_FLAG_FULLSCREEN;
                ShareData.showStatusAndNavigation(((Activity) getContext()), systemUiVisibility, true);
            }
            mStatusAndNavigationVisible = visible;
        }
    }

    private void initData()
    {
        mHomeDialogAgency = new HomeDialogAgency(this);
        mMenuWidth = ShareData.m_screenWidth;
        //手势控制器
        mController = new HomeEventController(getContext(), mHomeEventCallback);
        MissionHelper.setsOnActionCallBack(new MissionHelper.OnActionCallBack()
        {
            @Override
            public void onLogin()
            {
                m_openWhat = OPEN_TASK_CENTER;
                mSite.OnLogin(getContext(), MakeMaskBmpPath(), true);
            }

            @Override
            public void onBindPhone()
            {
                m_openWhat = OPEN_TASK_CENTER;
                SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
                LoginInfo lInfo = new LoginInfo();
                lInfo.mUserId = settingInfo.GetPoco2Id(false);
                lInfo.mAccessToken = settingInfo.GetPoco2Token(false);
                mSite.OnNoAmnBindPhone(getContext(), lInfo, MakeMaskBmpPath());
            }
        });
    }

    private void initUI()
    {
        LayoutParams params;

        mCenterPage = new FrameLayout(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mCenterPage, params);
        if (SysConfig.IsDebug())
        {
            TextView textView = new TextView(getContext());
            textView.setAlpha(0.5f);
            textView.setText("调试模式(点击切换大圆)");
            textView.getPaint().setFakeBoldText(true);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            textView.setTextColor(Color.RED);
            params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = ShareData.PxToDpi_xhdpi(20);
            params.rightMargin = ShareData.PxToDpi_xhdpi(20);
            addView(textView, params);
        }
        mGlassView = new ImageView(getContext());
        mGlassView.setScaleType(ImageView.ScaleType.FIT_XY);
        mGlassView.setVisibility(View.GONE);
        mGlassView.setAlpha(0f);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mGlassView, layoutParams);
    }

    private void initIntroAnim()
    {
        setUiEnabled(false);
        if (!isFirstInstall)
        {
            setTopTranslationY(RadiusPxToPercent(90));
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (isFirstInstall)
                {
                    Home4Page.this.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mMainLayout.startIntroAnimation();
                        }
                    }, 800);
                } else
                {
                    //模拟从主页拖拽回弹动画
                    Home4Page.this.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mController.introAnimation(new AnimatorHolder.AnimatorListener()
                            {
                                @Override
                                public void OnAnimationStart()
                                {
                                }

                                @Override
                                public void OnAnimationEnd()
                                {
                                    //绳子时间+延迟时间-回弹时间+10延迟
                                    //900+250-600+10
                                    Home4Page.this.postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            onHomeStart();
                                        }
                                    }, 560);
                                }
                            });
                        }
                    }, 350);
                }
            }
        });
    }

    private void onLoginPage(boolean isShowDlg)
    {
        if (isShowDlg)
        {
            showLoginDlg();
        } else
        {
            mSite.OnLogin(getContext(), MakeMaskBmpPath());
        }
    }

    private void showLoginDlg()
    {
        if (mDialog == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.confirm_title);
            builder.setMessage(R.string.login_first);
            builder.setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDialog.dismiss();
                    mSite.OnLogin(getContext(), MakeMaskBmpPath());
                }
            });
            builder.setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mDialog.dismiss();
                }
            });
            mDialog = builder.create();
        }
        if (!mDialog.isShowing())
        {
            mDialog.show();
        }
    }

    protected  void setGestureEnable(boolean gestureEnable)
    {
        this.mGestureEnable = gestureEnable;
    }

    public void setUiEnabled(boolean uiEnabled)
    {
        this.mUiEnabled = uiEnabled;
        if(mCurIPage != null && mCurIPage instanceof IActivePage){
            ((IActivePage) mCurIPage).setUiEnable(uiEnabled);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
//		Log.i("parentIntercept", "parentIntercept");
        if (!mUiEnabled)
        {
            return true;
        } else if (mGestureEnable && mCurMode != AD)
        {
            return mController.onInterceptTouchEvent(event);
        } else
        {
//			Log.i(TAG, "onTouchEvent: " + "onInterceptTouchEvent");
            return super.onInterceptTouchEvent(event);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
//		Log.i(TAG, "onTouchEvent: "+mUiEnabled);
        if (mUiEnabled)
        {
            return mController.onTouchEvent(event);
        } else
        {
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        mController.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    protected void clearGlassBk(boolean isClearPath)
    {
        m_initGlass = true;
        m_maskBmp = null;
        if (isClearPath)
        {
            s_maskBmpPath = null;
        }
    }

    protected String MakeMaskBmpPath()
    {
        String path = s_maskBmpPath;
        if (path == null)
        {
            if (m_maskBmp != null && !m_maskBmp.isRecycled())
            {
                path = FileCacheMgr.GetAppPath();
                Utils.SaveImg(getContext(), m_maskBmp, path, 100, false);
                s_maskBmpPath = path;
            }
        }
        return path;
    }

    protected boolean m_initGlass = true;

    protected void InitGlassBk()
    {
        if (m_initGlass || m_maskBmp == null)
        {
            m_initGlass = false;
            //是否有文件缓存
            if (!TextUtils.isEmpty(s_maskBmpPath))
            {
                m_maskBmp = cn.poco.imagecore.Utils.DecodeFile(s_maskBmpPath, null);
            }

            if (m_maskBmp == null && mIsLayoutFinish)
            {
                s_maskBmpPath = null;
                m_maskBmp = Bitmap.createBitmap(ShareData.m_screenWidth, mRealViewH, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(m_maskBmp);
                mCenterPage.draw(canvas);
                if (m_maskBmp != null)
                {
                    m_maskBmp = Bitmap.createScaledBitmap(m_maskBmp, m_maskBmp.getWidth() / 2, m_maskBmp.getHeight() / 2, true);
                    filter.fakeGlassBeauty(m_maskBmp, 0);
                }
            }
            if (mGlassView != null)
            {
                mGlassView.setImageBitmap(m_maskBmp);
            }
        }
    }

    private void openCameraPage()
    {
        mSite.OnCamera(getContext(), true);
    }

    public void openMenuItem(FeatureType item)
    {
        mLeftMenuDelegate.onFeatureClick(item);
    }

    private void setTopTranslationY(int distanceY)
    {
        if (mMainLayout != null)
        {
            mMainLayout.setTranslationY(distanceY);
        }
        //运营中心位移
        if (mCommunityPage != null)
        {
            int height = mCommunityPage.getActionbarHeight() * distanceY / mRealViewH - mCommunityPage.getActionbarHeight();
            if(height > mCommunityPage.getActionbarHeight())
            {
                height = mCommunityPage.getActionbarHeight();
            }
            mCommunityPage.setTranslationY(height);
        }
    }

    /**
     * 动画结束 做毛玻璃，弹框处理
     */
    private void onHomeStart()
    {
        isFirst = false;
        InitGlassBk();
        MakeMaskBmpPath();
        setUiEnabled(true);
        if(mHomeDialogAgency == null){
            mHomeDialogAgency = new HomeDialogAgency(this);
        }
        mHomeDialogAgency.checkAndShowDialog(isFirstInstall);
    }

    private MainPage.OnCallback mOnMainCallback = new MainPage.OnCallback()
    {
        @Override
        public void setUiEnable(boolean isEnable)
        {
            setUiEnabled(false);
        }

        @Override
        public void onIntroAnimEnd()
        {
            onHomeStart();
        }

        @Override
        public void onThemeChange()
        {
            clearGlassBk(true);
            InitGlassBk();
            MakeMaskBmpPath();
            if (mRightTheme != null)
            {
                mRightTheme.UpdateThemeStyle(m_maskBmp);
            }
            setUiEnabled(true);
        }

        @Override
        public void isOnTouchTopLeft(boolean isTouchLeftBottom)
        {
            isBootOrMenu = isTouchLeftBottom && mMainLayout.getLeftAdRes() != null;
//			mController.setBootOrMenu(isBootOrMenu);
        }

        @Override
        public void isOnTouchRightBottom(boolean isTouchRightBottom)
        {
            isBeautyOrAlbum = isTouchRightBottom;
        }

        @Override
        public void onAreaClick(int mode)
        {
            switch (mode)
            {
                case TOP:
                    TongJi2.AddCountByRes(getContext(), R.integer.首页_点击运营中心);
                    MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_点击运营中心);
                    mController.openPage(MOVE_TYPE_TOP);
                    break;
                case LEFT:
                    TongJi2.AddCountByRes(getContext(), R.integer.首页_点击个人中心);
                    MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_点击个人中心);
                    mController.openPage(MOVE_TYPE_LEFT);
                    break;
                case RIGHT:
                    TongJi2.AddCountByRes(getContext(), R.integer.首页_点击素材商店);
                    MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_点击素材商店);
                    mController.openPage(MOVE_TYPE_RIGHT);
                    break;
                case BOTTOM:
                    MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_点击打开相册);
                    TongJi2.AddCountByRes(getContext(), R.integer.首页_点击打开相册);
                    if (!mHasNoMemory)
                    {
                        mController.openPage(MOVE_TYPE_BOTTOM);
                    }
                    break;
                case CAMERA:
                    MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_拍照按钮);
                    TongJi2.AddCountByRes(getContext(), R.integer.首页_拍照);
                    if (!mHasNoMemory)
                    {
                        openCameraPage();
                    }
                    break;
                case LEFT_BOTTOM:
                    MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_左下角运营位置);
//                    TongJi2.AddOnlineClickCount(getContext(), null, R.integer.首页_点击左下角商业, getContext().getString(R.string.首页));
//                    mController.openPage(MOVE_TYPE_LEFT);
                    BannerCore3.ExecuteCommand(getContext(), mMainLayout.getLiveString(), mSite.m_cmdProc);
//                    if (UserMgr.IsLogin(getContext(), null))
//                    {
//                    } else
//                    {
//                        m_openWhat = OPEN_LIVE;
//                        onLoginPage(false);
//                    }

                    break;
                case CENTER_AD:
                    //MainPage内部调用对话框和统计
//						TongJi2.AddCountByRes(getContext(), R.integer.首页_点击大圆商业);
                    if(mMainLayout != null && !mMainLayout.isShowAdDialog())
                    {
                        final AbsAdRes res = mMainLayout.getCenterAdRes();
                        if (res.m_type == BaseRes.TYPE_NETWORK_URL)
                        {
                            mSite.OnAD(null, getContext(), res);
                        } else
                        {
                            mSite.OnAD(Home4Page.this, getContext(), res);
                        }
                    }
                    break;
                case LEFT_TOP:
                    if(isBootOrMenu){
                        MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_点击左下角商业);
                        TongJi2.AddOnlineClickCount(getContext(), null, R.integer.首页_点击左下角商业, getContext().getString(R.string.首页));
                        mController.openPage(MOVE_TYPE_LEFT);
                    }else{
                        mMainLayout.clearAdTheme(0);
                    }
                    break;
                case RIGHT_BOTTOM:
                    isBeautyOrAlbum = true;
                    MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_快捷修图);
                    mController.openPage(MOVE_TYPE_BOTTOM);
                    break;
                default:
                    break;
            }
        }
    };

    private FeatureMenuPage.FeatureMenuCallback mLeftMenuDelegate = new FeatureMenuPage.FeatureMenuCallback()
    {
        @Override
        public void onAvatarClick()
        {
            m_openWhat = 0;
            TongJi2.AddCountByRes(getContext(), R.integer.菜单_点击头像);
            MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_点击头像);
            if (UserMgr.IsLogin(getContext(), null))
            {
                mSite.OnUserInfo(getContext(), SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2Id(false));
            } else
            {
                onLoginPage(false);
            }
        }

        @Override
        public void onBusinessActivityClick(String clickProtocol) {
            BannerCore3.ExecuteCommand(getContext(), clickProtocol, mSite.m_cmdProc);
        }

        @Override
        public void onAdItemClick(int adPosition, String clickProtocol) {
            BannerCore3.ExecuteCommand(getContext(), clickProtocol, mSite.m_cmdProc);
        }

        @Override
        public void onFeatureClick(FeatureType feature)
        {
            if (feature == null) {
                return;
            }
            switch (feature)
            {
//				case AVATAR:
//				case EDITBTN:
//				case USERNAMEPOSITION:
//				{
//					m_openWhat = 0;
//					TongJi2.AddCountByRes(getContext(), R.integer.菜单_点击头像);
//					MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_点击头像);
//					if(UserMgr.IsLogin(getContext(),null))
//					{
//						mSite.OnUserInfo(getContext(),SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2Id(false));
//					}
//					else
//					{
//						onLoginPage(false);
//					}
//					break;
//				}
                case MYCREDIT:
//					MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_);
                    if (UserMgr.IsLogin(getContext(), null))
                    {
                        SettingInfo info = SettingInfoMgr.GetSettingInfo(getContext());
                        mSite.OnCredit(getContext(), info.GetPoco2Id(false), info.GetPoco2Token(false), MakeMaskBmpPath());
                    } else
                    {
                        m_openWhat = OPEN_MY_CREDIT;
                        onLoginPage(false);
                    }
                    break;

                case WALLET:
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_钱包);
                    if (UserMgr.IsLogin(getContext(), null))
                    {
                        SettingInfo info = SettingInfoMgr.GetSettingInfo(getContext());
                        String phone = info.GetPoco2Phone();
                        if (phone == null || phone.length() <= 5)
                        {
                            m_openWhat = OPEN_WALLET;
                            LoginInfo lInfo = new LoginInfo();
                            lInfo.mUserId = info.GetPoco2Id(false);
                            lInfo.mAccessToken = info.GetPoco2Token(false);
                            mSite.OnBindPhone(getContext(), lInfo, MakeMaskBmpPath());
                        } else
                        {
                            MissionHelper.getInstance().OpenWalletPage((Activity) getContext(), info.GetPoco2Id(false), mSite.m_cmdProc);
                        }
                    } else
                    {
                        m_openWhat = OPEN_WALLET;
                        onLoginPage(false);
                    }
                    break;
                case TASKHALL:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_任务大厅);
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_任务大厅);
                    // 杭州相关接口
                    StoreTime.setClickTime(System.currentTimeMillis(), getContext());

                    if (UserMgr.IsLogin(getContext(), null))
                    {
                        final SettingInfo info = SettingInfoMgr.GetSettingInfo(getContext());
                        if (info.GetPoco2Phone() != null && info.GetPoco2Phone().length() > 4)
                        {
                            MissionHelper.getInstance().OpenTaskCenter((Activity) getContext(), info.GetPoco2Id(false), true, mSite.m_cmdProc);
                        } else
                        {
                            MissionHelper.getInstance().OpenTaskCenter((Activity) getContext(), info.GetPoco2Id(false), false, mSite.m_cmdProc);
                        }
                    } else
                    {
                        MissionHelper.getInstance().OpenTaskCenter((Activity) getContext(), null, false, mSite.m_cmdProc);
                    }
                    break;
                }

                case CLOUDALBUM:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_云相册);
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_云相册);
                    if (UserMgr.IsLogin(getContext(), null))
                    {
                        final SettingInfo info = SettingInfoMgr.GetSettingInfo(getContext());
                        if (info.GetPoco2Phone() != null && info.GetPoco2Phone().length() > 4)
                        {
                            mSite.OnCloudAlbum(getContext(), info.GetPoco2Id(false), info.GetPoco2Token(false), MakeMaskBmpPath());
                        } else
                        {
                            m_openWhat = OPEN_CLOUD_ALBUM;
                            //绑手机
                            LoginInfo loginInfo = new LoginInfo();
                            loginInfo.mUserId = info.GetPoco2Id(false);
                            loginInfo.mAccessToken = info.GetPoco2Token(false);
                            mSite.OnBindPhone(getContext(), loginInfo, MakeMaskBmpPath());
                        }
                    } else
                    {
                        m_openWhat = OPEN_CLOUD_ALBUM;
//						showLoginDlg();
                        onLoginPage(false);
                    }
                    break;
                }

                case BEAUTYMALL:
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_福利社);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_福利社);
                    String userId = null;
                    if (UserMgr.IsLogin(getContext(), null))
                    {
                        userId = SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2Id(false);
                    }
                    mSite.OpenBeautyMallPage(getContext(), userId);
                    break;
                }

                case COMMENT: {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_互动消息);
                    if(UserMgr.IsLogin(getContext(),null))
                    {
                       openComment();
                    }else{
                        m_openWhat = OPEN_COMMENT;
                        onLoginPage(false);
                    }
                    break;
                }

                case CHAT: {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_聊天);
                    if(UserMgr.IsLogin(getContext(),null))
                    {
                        openChat();
                    }else{
                        m_openWhat = OPEN_CHAT;
                        onLoginPage(false);
                    }
                    break;
                }

                case PHOTOPRINTED:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_照片冲印);
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_照片冲印);
                    mSite.OpenPrintPage(getContext());
                    break;
                }

                case CUSTOMBEAUTY: {
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_美形定制);
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_美形定制);
                    TagMgr.ResetTag(getContext(), Tags.CAMERA_TAILOR_MADE_FLAG);
                    TagMgr.Save(getContext());
                    mSite.OnTailorMadeCamera(getContext());
                    break;
                }

                case LIVESHOW: {
                    mSite.onLive(getContext());
                    break;
                }

                case CHANGETHEMESKIN:
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_主题换肤);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_主题换肤);
                    TagMgr.SetTagValue(getContext(), Tags.NOTIFY_CHANGE_APP_SKIN_FEATURE, LeftMenu.NOTIFY_USER_VALUE);
//					mController.closePage(MOVE_TYPE_LEFT);
                    mSite.onTheme(getContext());
                    break;
                }

                case GENERALSETTING:
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置);
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_通用设置);
                    mSite.OnSetting(getContext());
                    break;
                }


                case SHAREWITHFRIENDS:
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_推荐给好友);
//                    mSite.onShareWithFriends(getContext(), s_maskBmpPath);
                    break;
                }

                case APPRECOMENDATION:
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_精品推荐);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_精品推荐);
                    mSite.OnAppMarket(getContext(), MakeMaskBmpPath());
                    break;
                }

                case RATEUS:
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_给个好评);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_APP评分);
                    mSite.OnScore(getContext());
                    break;
                }


                case HELPANDFEEDBACK:{
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_帮助与反馈);
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_帮助与反馈);
                    mSite.OnQuestion(getContext());
                    break;
                }

                case ABOUT:
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_关于);
                    mSite.OpenAboutUsPage(getContext());
                    break;
                }



                default:
                {
                    break;
                }
            }
        }
    };

    private void openChat()
    {
        isShowModuleView(false, MENU, false);
        isShowModuleView(true, CAMPAIGN, false);

        Object savedParams = mSite.m_myParams.get(HomePageSite.CHAT_MESSAGE_KEY);
        if (savedParams == null)
        {
            mCommunityPage.openPageByMessageType(NotificationDataUtils.TYPE_IM);
            return;
        }

        if (savedParams instanceof Integer)
        {
            int type = (Integer) savedParams;
            if (type == 0)
            {
                mCommunityPage.openPageByMessageType(NotificationDataUtils.TYPE_IM);
            } else
            {
                mCommunityPage.openPageByMessageType((int) savedParams);
            }
        }
    }

    private void openComment()
    {
        isShowModuleView(false, MENU, false);
        isShowModuleView(true, CAMPAIGN, false);

        Object savedParams = mSite.m_myParams.get(HomePageSite.COMMUNITY_MESSAGE_KEY);
        if (savedParams == null)
        {
            if (mCommunityPage != null)
            {
                mCommunityPage.openPageByMessageType(NotificationDataUtils.TYPE_LIKE);
            }
            return ;
        }
        if (savedParams instanceof Integer)
        {
            int type = (Integer) savedParams;
            if (mCommunityPage != null)
            {
                mCommunityPage.openPageByMessageType(type);
            }
        }
    }

    @Override
    public void onResume()
    {
        if (mCurIPage != null)
        {
            mCurIPage.onResume();
        }
        if(mCurMode == HOME && !isFirst){
//            isShowLoginTipDialog();
            if(mHomeDialogAgency != null){
                mHomeDialogAgency.isShowLoginTipDialog();
            }
        }
        int type = CheckSDCard(getContext());
        if (type == Utils.SDCARD_ERROR || type == Utils.SDCARD_OUT_OF_SPACE)
        {
            mHasNoMemory = true;
        } else
        {
            mHasNoMemory = false;
        }
    }

    @Override
    public void onPause()
    {
        if (mCurIPage != null)
        {
            mCurIPage.onPause();
        }
    }

    @Override
    public void onClose()
    {

        //停止事件，     例如相册长按点击进入美化页，放手期间时回退主页
        mController.onClose();
        m_maskBmp = null;
        mSite.removeOnSiteCallBack();
        if (mUpdateAppHandler != null)
        {
            mUpdateAppHandler.removeCallbacksAndMessages(null);
            mUpdateAppHandler.ClearAll();
        }

        if(mHomeDialogAgency != null){
            mHomeDialogAgency.clearAll();
        }

        if(mCurIPage instanceof IActivePage){
            ((IActivePage) mCurIPage).onPageInActive(NONE);
            ((IActivePage) mCurIPage).setUiEnable(false);
        }
        removeModuleView(HOME);
        removeModuleView(MENU);
        removeModuleView(CAMPAIGN);
        removeModuleView(ALBUM);
        removeModuleView(MATERIAL);
        removeModuleView(BOOT);
        removeModuleView(BEAUTY);

        MissionHelper.ClearAll2();
        if (mBMCheckNewListener != null)
        {
            mBMCheckNewListener.Clear();
            mBMCheckNewListener = null;
        }
    }

    /**
     * 调用手势控制器， 然后回调用mHomeEventCallback
     *
     * @param isShow
     * @param mode
     * @param isHasAmn
     */
    public void isShowModuleView(boolean isShow, int mode, boolean isHasAmn)
    {
        if(mode == NONE){
            return;
        }
        if(mode == HOME && !isShow){
            return;
        }
        if (mCurMode == mode && isShow)
        {
            return;
        }
        if (mCurMode != mode && !isShow)
        {
            return;
        }
        int direction = -1;
        switch (mode)
        {
            case HOME:
                //显示主页既是关闭其他页面
                if(mCurMode == AD)
                {
                    closeFullScreenADPage();
                }else{
                    direction = mController.getPageType();
                    if (direction != MOVE_TYPE_NONE)
                    {
                        isShow = false;  //打开home也就是关闭其他页面
                    }
                }
                break;
            case MENU:
                direction = MOVE_TYPE_LEFT;
                break;
            case BOOT:
                isBootOrMenu = true;
                direction = MOVE_TYPE_LEFT;
                break;
            case MATERIAL:
                direction = MOVE_TYPE_RIGHT;
                break;
            case BEAUTY:
                isBeautyOrAlbum = true;
                direction = MOVE_TYPE_BOTTOM;
                break;
            case ALBUM:
                direction = MOVE_TYPE_BOTTOM;
                break;
            case CAMPAIGN:
                direction = MOVE_TYPE_TOP;
                break;
            case AD:
                break;
            default:
                direction = MOVE_TYPE_NONE;
                break;
        }
        if (direction != -1)
        {
            if (isShow)
            {
                mController.openPage(direction, isHasAmn);
            } else
            {
                mController.closePage(direction, isHasAmn);
            }
        }
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        super.onPageResult(siteID, params);

        if (mCurIPage != null)
        {
            mCurIPage.onPageResult(siteID, params);
        }
        if (mCurMode == HOME || mCurMode == MENU)
        {
            TongJi2.AddCountByRes(getContext(), R.integer.首页);
            switch (siteID)
            {
                case SiteID.THEME:
                    isShowModuleView(false, MENU, false);
                    if (params != null && params.get("hasChangedSkin") != null)
                    {
                        boolean b = (boolean) params.get("hasChangedSkin");
                        if (b)
                        {
                            if (mCommunityPage != null)
                            {
                                mCommunityPage.changeThemeSkin();
                            }
                            if (mBottomAlbum != null)
                            {
                                mBottomAlbum.changeSkin();
                            }
                            if (mLeftBoot != null)
                            {
                                mLeftBoot.changeThemeSkin();
                            }
                            if(mLeftMenu != null){
                                mLeftMenu.notifySkinChange();
                            }
                            if (mMainLayout != null)
                            {
                                mMainLayout.changeThemeSkin(350);
                            }
                        }
                    }
					if(mLeftMenu != null)
					{
						mLeftMenu.notifySkinChange();
					}
                    break;
                case SiteID.BINDPHONE:
                {
                    String phone = SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2Phone();
                    if (m_openWhat != OPEN_TASK_CENTER)
                    {
                        if (phone == null || phone.length() <= 5)
                        {
                            break;
                        }
                    }
                }
                case SiteID.LOGIN:
                case SiteID.REGISTER_DETAIL:
                case SiteID.RESETPSW:
                    if (UserMgr.IsLogin(getContext(), null))
                    {
                        SettingInfo info = SettingInfoMgr.GetSettingInfo(getContext());
                        switch (m_openWhat)
                        {
                            case OPEN_CLOUD_ALBUM:
                            {
                                if (info.GetPoco2Phone() == null || info.GetPoco2Phone().length() <= 5)
                                {
                                    SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
                                    LoginInfo lInfo = new LoginInfo();
                                    lInfo.mUserId = settingInfo.GetPoco2Id(false);
                                    lInfo.mAccessToken = settingInfo.GetPoco2Token(false);
                                    mSite.OnBindPhone(getContext(), lInfo, MakeMaskBmpPath());
                                    break;
                                } else
                                {
                                    m_openWhat = 0;
                                    mSite.OnCloudAlbum(getContext(), info.GetPoco2Id(false), SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2Token(true), MakeMaskBmpPath());
                                }
                                break;
                            }
                            case OPEN_MY_CREDIT:
                                m_openWhat = 0;
                                mSite.OnCredit(getContext(), info.GetPoco2Id(false), SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2Token(true), MakeMaskBmpPath());
                                break;
                            case OPEN_WALLET:
                            {
                                if (info.GetPoco2Phone() == null || info.GetPoco2Phone().length() <= 5)
                                {
                                    SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
                                    LoginInfo lInfo = new LoginInfo();
                                    lInfo.mUserId = settingInfo.GetPoco2Id(false);
                                    lInfo.mAccessToken = settingInfo.GetPoco2Token(false);
                                    mSite.OnNoAmnBindPhone(getContext(), lInfo, MakeMaskBmpPath());
                                    break;
                                } else
                                {
                                    m_openWhat = 0;
                                    MissionHelper.getInstance().OpenWalletPage((Activity) getContext(), info.GetPoco2Id(false), mSite.m_cmdProc);
                                }
                                break;
                            }
                            case OPEN_TASK_CENTER:
                            {
                                if (info.GetPoco2Phone() == null || info.GetPoco2Phone().length() <= 5)
                                {
                                    MissionHelper.getInstance().OpenTaskCenter((Activity) getContext(), info.GetPoco2Id(false), false, mSite.m_cmdProc);
                                } else
                                {
                                    MissionHelper.getInstance().OpenTaskCenter((Activity) getContext(), info.GetPoco2Id(false), true, mSite.m_cmdProc);
                                }
                                m_openWhat = 0;
                                break;
                            }
                            case OPEN_CHAT:
                            {
                                openChat();
                                m_openWhat = 0;
                                break;
                            }
                            case OPEN_COMMENT:
                            {
                                openComment();
                                m_openWhat = 0;
                                break;
                            }
                            case OPEN_LIVE:
                                BannerCore3.ExecuteCommand(getContext(), mMainLayout.getLiveString(), mSite.m_cmdProc);
                                break;
                            default:
                                m_openWhat = 0;
                                break;
                        }
                    }
                    else
                    {
                        switch (m_openWhat)
                        {
                            case OPEN_TASK_CENTER:
                            {
                                MissionHelper.getInstance().OpenTaskCenter((Activity) getContext(), null, false, mSite.m_cmdProc);
                                break;
                            }
                        }
                        m_openWhat = 0;
                    }
                default:
                    break;
            }
        }
    }

    protected final int QUIT_DELAY = 3000;
    protected long quit_time = 0;
    protected Toast quit_toast;

    @Override
    public void onBack()
    {
        if (mUiEnabled && !mController.isOnGesture())
        {
            if(mHomeDialogAgency != null && mHomeDialogAgency.OnBack()){
                return;
            }
            if (mCurMode != HOME)
            {
                if (mCurMode == MENU)
                {
                    mController.closePage(MOVE_TYPE_LEFT);
                } else
                {
                    if (mCurIPage != null)
                    {
                        mCurIPage.onBack();
                    } else
                    {
                        mCurMode = HOME;
                    }
                }
                return;
            }
            long time = System.currentTimeMillis();
            if (time - quit_time > QUIT_DELAY)
            {
                //第一次点击提示
                if (quit_toast == null)
                {
                    quit_toast = Toast.makeText(getContext(), getResources().getString(R.string.press_again_and_quit) + getResources().getString(R.string.app_name_main), Toast.LENGTH_SHORT);
                }
                quit_toast.show();
            } else
            {
                if (quit_toast != null)
                {
                    quit_toast.cancel();
                    quit_toast = null;
                }
                mSite.OnBack(getContext());
            }
            quit_time = time;
        }
    }

    private HomePageSite.OnSiteBack mOnSiteBack = new HomePageSite.OnSiteBack()
    {
        @Override
        public void onBack(int siteId)
        {
            isShowModuleView(true,HOME,true);
//            if (siteId == SiteID.COMMUNITY_SDK)
//            {
//                mUiEnabled = false;
//                mController.closePage(MOVE_TYPE_TOP);
//            } else if (siteId == SiteID.ALBUM)
//            {
//                mUiEnabled = false;
//                mController.closePage(MOVE_TYPE_BOTTOM);
//            } else if (siteId == SiteID.THEME_LIST)
//            {
//                mUiEnabled = false;
//                mController.closePage(MOVE_TYPE_RIGHT);
//            } else if (siteId == SiteID.BUSINESS_DISPLAY)
//            {
//                closeFullScreenADPage();
//            } else if (siteId == SiteID.BOOT_IMG)
//            {
//                mUiEnabled = false;
//                mController.closePage(MOVE_TYPE_LEFT);
//            } else if (siteId == SiteID.FEATUREMENU)
//            {
//                mUiEnabled = false;
//                mController.closePage(MOVE_TYPE_LEFT);
//            }
        }


        @Override
        public void onSystemMessage(int type, int count) {
            AppFeatureManager.getInstance().setSystemMessage(count);
        }

        @Override
        public void onNewMessage(int type, int count) {
            AppFeatureManager.getInstance().setChatInfo(Home4Page.this.getContext(), count);
        }

        @Override
        public void onCommunityMessage(int type, int count) {
            AppFeatureManager.getInstance().setCommunityMessage(getContext(), count);
        }


    };

    public void openFullScreenADPage(HashMap<String, Object> params)
    {
        if(mCurIPage != null && mCurIPage instanceof IActivePage){
            ((IActivePage) mCurIPage).onPageInActive(HOME);
        }
        InitGlassBk();
        setUiEnabled(false);
        mGestureEnable = false;
        mGlassView.setVisibility(View.VISIBLE);
        mAdPage = (FullScreenADPage) addModuleView(AD, true, params);
        mAdPage.setOnOpenPageCallback(new FullScreenADPage.OnOpenPageCallback()
        {
            @Override
            public void onOpenStart()
            {
            }

            @Override
            public void onPageFade(float alpha)
            {
                if (alpha <= 0.5f)
                {
                    mGlassView.setAlpha(alpha * 2);
                } else
                {
                    mGlassView.setAlpha(1f);
                }
            }

            @Override
            public void onOpenEnd()
            {
                setUiEnabled(true);
            }
        });
        mAdPage.startOpenAnimation();
    }

    public void closeFullScreenADPage()
    {
        if (mAdPage != null)
        {
            mAdPage.setOnClosePageCallBack(new FullScreenADPage.OnClosePageCallBack()
            {
                @Override
                public void onCloseStart()
                {
                    setUiEnabled(false);
                }

                @Override
                public void onPageFade(float alpha)
                {
                    if (alpha <= 0.5f)
                    {
                        mGlassView.setAlpha(alpha * 2);
                    } else
                    {
                        mGlassView.setAlpha(1f);
                    }
                }

                @Override
                public void onCloseEnd()
                {
                    mGestureEnable = true;
                    removeModuleView(AD);
                    addModuleView(HOME, true, mMainData);
                    if(mCurIPage != null && mCurIPage instanceof IActivePage){
                        ((IActivePage) mCurIPage).onPageActive(AD);
                    }
                    setUiEnabled(true);
                }
            });
            mAdPage.startCloseAnimation();
        }else{
            mCurMode = HOME;
        }
    }

    private HomeEventController.Callback mHomeEventCallback = new HomeEventController.Callback()
    {
        @Override
        public void InitMain()
        {
            addModuleView(CAMPAIGN, false);
            addModuleView(HOME, false, mMainData);
        }

        @Override
        public boolean InitLeft()
        {
            if (isBootOrMenu)
            {
                HashMap<String, Object> data = new HashMap<>();
                if (mMainLayout != null)
                {
                    data.put(KEY_BOOT_IMG, mMainLayout.getLeftAdRes());
                } else
                {
                    data.put(KEY_BOOT_IMG, BootAd.GetOneBootRes(getContext()));
                }
                addModuleView(BOOT, false, data);
            } else
            {
                addModuleView(MENU, false);
                addModuleView(HOME, false, mMainData);
            }
            return isBootOrMenu;
        }

        @Override
        public int GetLeftW()
        {
            return isBootOrMenu ? ShareData.m_screenWidth : mMenuWidth;
        }

        @Override
        public int GetCurrentLeftX()
        {
            int v = 0;
            if (isBootOrMenu)
            {
                if (mLeftBoot != null)
                {
                    v = (int) mLeftBoot.getTranslationX();
                }
            } else
            {
                if (mLeftMenu != null)
                {
                    v = (int) mLeftMenu.getTranslationX();
                }
            }
            return v;
        }

        @Override
        public void SetLeft(int x, int y, float s)
        {
            if (mGlassView != null)
            {
                if (s <= 0.1f)
                {
                    mGlassView.setAlpha(s * 10);
                } else
                {
                    mGlassView.setAlpha(1f);
                }
            }
            if (isBootOrMenu)
            {
                if (mLeftBoot != null)
                {
//					LayoutParams fl = (LayoutParams)mLeftBoot.getLayoutParams();
//					fl.leftMargin = x;
//					mLeftBoot.requestLayout();
                    mLeftBoot.setTranslationX(x);
                }

            } else
            {
                if (mLeftMenu != null)
                {
                    mLeftMenu.setTranslationX(x);
                }
            }
        }

        @Override
        public void InitTop()
        {
            addModuleView(CAMPAIGN, false, mTopData);
        }

        @Override
        public int GetTopH()
        {
            return mRealViewH;
//			return ShareData.getCurrentScreenH((Activity) getContext());
        }

        @Override
        public int GetCurrentTopY()
        {
            int v = 0;
            if (mMainLayout != null)
            {
                v = (int) mMainLayout.getTranslationY();   //下拉的距离幅度
            }
            return v;
        }

        @Override
        public void SetTop(int x, int y, float s)
        {
//			if(mCampaignCenterPage != null)
//			{
//				mCampaignCenterPage.startBackgroundGradientColor(s);
//			}
            setTopTranslationY(y);
        }

        @Override
        public boolean InitRight()
        {
            addModuleView(MATERIAL, false);
            return true;
        }


        @Override
        public int GetRightW()
        {
            return ShareData.getScreenW();
        }

        @Override
        public int GetCurrentRightX()
        {
            int v = 0;
            if (mRightTheme != null)
            {
                v = (int) mRightTheme.getTranslationX();
            }
            return v;
        }

        @Override
        public void SetRight(int x, int y, float s)
        {
            if (mGlassView != null)
            {
                if (s <= 0.1f)
                {
                    mGlassView.setAlpha(s * 10);
                } else
                {
                    mGlassView.setAlpha(1f);
                }
            }
            if (mRightTheme != null)
            {
                mRightTheme.setTranslationX(x);
            }
        }

        @Override
        public boolean InitBottom()
        {
            if (isBeautyOrAlbum)
            {
                addModuleView(BEAUTY, false);

            }else{
                addModuleView(ALBUM, false, mAlbumData);
            }
            return isBeautyOrAlbum;
        }


        @Override
        public int GetBottomH()
        {
            return mRealViewH;
        }

        @Override
        public int GetCurrentBottomY()
        {
            int v = 0;
            if (isBeautyOrAlbum)
            {
                if (mBottomBeautyEntryPage != null)
                {
                    v = (int) mBottomBeautyEntryPage.getTranslationY();
                }
            } else
            {
                if (mBottomAlbum != null)
                {
                    v = (int) mBottomAlbum.getTranslationY();
                }
            }
            return v;
        }

        @Override
        public void SetBottom(int x, int y, float s)
        {
            if (mGlassView != null)
            {
                if (s <= 0.1f)
                {
                    mGlassView.setAlpha(s * 10);
                } else
                {
                    mGlassView.setAlpha(1f);
                }
            }
            if (isBeautyOrAlbum)
            {
                if (mBottomBeautyEntryPage != null)
                {
                    mBottomBeautyEntryPage.setTranslationY(y);
                }
            } else
            {
                if (mBottomAlbum != null)
                {
                    mBottomAlbum.setTranslationY(y);
                }
            }

        }

        @Override
        public boolean CanControl()
        {
            return mGestureEnable;
        }

        @Override
        public boolean CanBottomIntercept()
        {
            if ((mCurMode == HOME && mHasNoMemory) || mCurMode == BEAUTY)
            {
                return false;
            } else if (mCurMode == ALBUM && mBottomAlbum != null)
            {
                return mBottomAlbum.isScrollToTop();
            }
            return true;
        }

        protected int getHomeMode(int mode)
        {
            int pageMode = HOME;
            switch (mode){
                case MOVE_TYPE_NONE:
                    pageMode = HOME;
                    break;
                case MOVE_TYPE_LEFT:
                    if(isBootOrMenu){
                        pageMode = BOOT;
                    }else{
                        pageMode = MENU;
                    }
                    break;
                case MOVE_TYPE_BOTTOM:
                    if(isBeautyOrAlbum)
                    {
                        pageMode = BEAUTY;
                    }else{
                        pageMode = ALBUM;
                    }
                    break;
                case MOVE_TYPE_RIGHT:
                    pageMode = MATERIAL;
                    break;
                case MOVE_TYPE_TOP:
                    pageMode = CAMPAIGN;
                    break;
                default:
                    break;
            }
            return pageMode;
        }

        @Override
        public void onStart(int fromType, int endType, boolean isSlide)
        {
            int startHomeMode = mCurMode;
            int endHomeMode = getHomeMode(endType);
            if (!isSlide)
            {
                mUiEnabled = false;
            }
            if(mCurIPage instanceof IActivePage){
                ((IActivePage) mCurIPage).setUiEnable(false);
            }
            if (!(startHomeMode == NONE || startHomeMode == CAMPAIGN || endHomeMode == CAMPAIGN) || endHomeMode == BEAUTY)
            {
                InitGlassBk();
                MakeMaskBmpPath();
                mGlassView.setVisibility(View.VISIBLE);
            }
            switch (startHomeMode)
            {
                case HOME:
                    //从主页过来，更新页面
                    if (endType == MOVE_TYPE_BOTTOM && mBottomAlbum != null)
                    {
                        mBottomAlbum.notifyUpdate();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onEnd(int fromType, int endType, boolean isSlide)
        {
            int startHomeMode = mCurMode;
            int endHomeMode = getHomeMode(endType);
            if (startHomeMode != NONE && startHomeMode != endHomeMode)
            {
                if(mCurIPage != null && mCurIPage instanceof IActivePage){
                    ((IActivePage) mCurIPage).onPageInActive(endHomeMode);
                }
            }
            switch (startHomeMode)
            {
                case NONE:
                    if (endHomeMode == MENU | endHomeMode == HOME)
                    {
                        TongJi2.AddCountByRes(getContext(), R.integer.首页);
                    }
                case HOME:
                    if (endHomeMode == HOME)
                    {
                        //主页->主页
                        isBootOrMenu = false;   //默认滑向个人中心
                        mCurIPage = mMainLayout;
                        mGlassView.setAlpha(0f);
                    } else
                    {
                        if (endHomeMode == BOOT)
                        {
                            if (isSlide)
                            {
                                // 滑动与点击使用同一个统计
                                TongJi2.AddCountByRes(getContext(), R.integer.首页_点击左下角商业);
                                MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_点击左下角商业);
                            }
                            mCurIPage = mLeftBoot;
                        }else if(endHomeMode == MENU){
                            if (isSlide)
                            {
                                TongJi2.AddCountByRes(getContext(), R.integer.首页_左滑个人中心);
                                MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_左滑个人中心);
                            }
                            mCurIPage = mLeftMenu;
                            removeModuleView(BOOT);
                            removeModuleView(MATERIAL);
                            removeModuleView(ALBUM);
                            removeModuleView(BEAUTY);
                        } if (endHomeMode == MATERIAL)
                        {
                            if (isSlide)
                            {
                                TongJi2.AddCountByRes(getContext(), R.integer.首页_右滑素材商店);
                                MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_右滑素材商店);
                            }
                            if (mRightTheme != null)
                            {
                                mRightTheme.UpdateData();
                            }
                            mCurIPage = mRightTheme;
                            removeModuleView(ALBUM);
                            removeModuleView(BOOT);
                            removeModuleView(MENU);
                            removeModuleView(BEAUTY);
                        } else if (endHomeMode == CAMPAIGN)
                        {
                            if (isSlide)
                            {
                                TongJi2.AddCountByRes(getContext(), R.integer.首页_下拉运营中心);
                                MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_下拉运营中心);
                            }
                            if (mCommunityPage != null)
                            {
                                mCommunityPage.slideInCommunity(getContext());
                            }
                            mCurIPage = mCommunityPage;
                            removeModuleView(MATERIAL);
                            removeModuleView(ALBUM);
                            removeModuleView(BOOT);
                            removeModuleView(MENU);
                            removeModuleView(BEAUTY);
                        } else if (endHomeMode == ALBUM)
                        {
                            if (isSlide)
                            {
                                TongJi2.AddCountByRes(getContext(), R.integer.首页_上拉打开相册);
                                MyBeautyStat.onClickByRes(R.string.首页_首页_主页面_上拉打开相册);
                            }
                            mCurIPage = mBottomAlbum;
                            removeModuleView(BOOT);
                            removeModuleView(MATERIAL);
                            removeModuleView(MENU);
                            removeModuleView(BEAUTY);
                        }else if(endHomeMode == BEAUTY)
                        {
                            removeModuleView(BOOT);
                            removeModuleView(MATERIAL);
                            removeModuleView(MENU);
                            removeModuleView(ALBUM);
                            mCurIPage = mBottomBeautyEntryPage;
                        }
                    }
                    break;
                case BOOT:
                case MENU:
                case ALBUM:
                case MATERIAL:
                case CAMPAIGN:
                case BEAUTY:
                default:
                    //其他页面到->主页
                    if (endHomeMode == HOME)
                    {
                        if (isSlide)
                        {
                            if (startHomeMode == ALBUM)
                            {
                                MyBeautyStat.onClickByRes(R.string.选相册_选相册_主页面_下拉返回);
                            } else if (startHomeMode == MOVE_TYPE_RIGHT)
                            {
                                MyBeautyStat.onClickByRes(R.string.素材商店_素材商店首页_主页面_滑动返回);
                            }
                        }
                        if(startHomeMode == BOOT){
                            removeModuleView(BOOT);
                        }
                        if (startHomeMode == CAMPAIGN)
                        {
                            MyBeautyStat.onClickByRes(R.string.社区_首页_主页面_返回);
                        }
                        if (startHomeMode == MENU)
                        {
                            MyBeautyStat.onClickByRes(R.string.个人中心_首页_主页面_返回);
                        }
                        if (startHomeMode != MENU)
                        {
                            TongJi2.AddCountByRes(getContext(), R.integer.首页);
                        }
                        mCurIPage = mMainLayout;
                        isBootOrMenu = false;   //默认滑向个人中心
                        isBeautyOrAlbum = false;
                        mGlassView.setAlpha(0f);
                    }
                    break;
            }
            mCurMode = endHomeMode;
            mSite.m_myParams.put(KEY_CUR_MODE,mCurMode);
            if (startHomeMode != endHomeMode)
            {
                if(mCurIPage instanceof IActivePage){
                    ((IActivePage) mCurIPage).onPageActive(startHomeMode);
                }
            }
            setUiEnabled(true);
        }

        @Override
        public void TopReStore(float reBoundPercent)
        {
            if (mMainLayout != null)
            {
                mMainLayout.getRopeAnimationView().startReboundAnimation(reBoundPercent);
            }
        }
    };

    protected IPage addModuleView(int mode, boolean isShowPage)
    {
        return addModuleView(mode, isShowPage, null);
    }

    protected IPage addModuleView(int mode, boolean isShowPage, HashMap<String, Object> data)
    {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        IPage iPage = null;
        switch (mode)
        {
            case HOME:
                if (mMainLayout == null)
                {
                    mMainLayout = new MainPage(getContext());
                    mMainLayout.setClickAreaCallBack(mOnMainCallback);
                    mMainLayout.SetData(data);
                    mCenterPage.addView(mMainLayout, params);
                }
                iPage = mMainLayout;
                if (isShowPage)
                {
                    mGlassView.setVisibility(View.GONE);
                }
                break;
            case CAMPAIGN:
                if (mCommunityPage == null)
                {
                    mCommunityPage = new CommunityPage(getContext(), mSite.mCampaignCenterSite);
                    mCommunityPage.SetData(data);
                    mCenterPage.addView(mCommunityPage, 0, params);
                }
                iPage = mCommunityPage;
                if (!isShowPage)
                {
                    iPage.setTranslationY(-((CommunityPage) iPage).getActionbarHeight());
                }
                break;
            case ALBUM:
                if (mBottomAlbum == null)
                {
                    mBottomAlbum = new AlbumPage(getContext(), mSite.mAlbumSite);
                    mBottomAlbum.SetData(data);
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    addView(mBottomAlbum, params);
                }
                iPage = mBottomAlbum;
                if (!isShowPage)
                {
                    iPage.setTranslationY(mRealViewH);
                }
                break;
            case BEAUTY:
                if (mBottomBeautyEntryPage == null)
                {
                    mBottomBeautyEntryPage = new BeautyEntryPage(getContext(), mSite.mBeautyEntryPageSite);
                    mBottomBeautyEntryPage.SetData(data);
//                    mBottomBeautyEntryPage.setBk(m_maskBmp);
                    params.gravity = Gravity.RIGHT | Gravity.TOP;
                    addView(mBottomBeautyEntryPage, params);
                }
                iPage = mBottomBeautyEntryPage;
                if (!isShowPage)
                {
                    iPage.setTranslationY(mRealViewH);
                }
                break;
            case MATERIAL:
                if (mRightTheme == null)
                {
                    mRightTheme = new ThemeListPage(getContext(), mSite.mThemeListPageSite);
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    mRightTheme.SetData(data);
                    InitGlassBk();
                    mRightTheme.setBk(m_maskBmp);
                    addView(mRightTheme, params);
                }
                iPage = mRightTheme;
                if (!isShowPage)
                {
                    mGlassView.setVisibility(View.VISIBLE);
                    iPage.setTranslationX(ShareData.m_screenWidth);
                }
                break;
            case MENU:
                if (mLeftMenu == null)
                {
                    mLeftMenu = new FeatureMenuPage(getContext(), mSite.mMenuSite);
                    mLeftMenu.SetData(data);
                    params.width = mMenuWidth;
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    mLeftMenu.setMenuPageCallback(mLeftMenuDelegate);
                    addView(mLeftMenu, params);
                }
                iPage = mLeftMenu;
                if (!isShowPage)
                {
                    iPage.setTranslationX(-mMenuWidth);
                }
                break;
            case BOOT:
                if (mLeftBoot == null)
                {
                    mLeftBoot = new BootImgPage(getContext(), mSite.mBootImgPageSite);
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    addView(mLeftBoot, params);
//					HashMap<String, Object> data = new HashMap<>();
//					if(mMainData != null && mMainData.containsKey(KEY_BOOT_IMG))
//					{
//						data.put("img",mMainData.get(KEY_BOOT_IMG));
//					}
                    mLeftBoot.SetData(data);
                }
                iPage = mLeftBoot;
                if (!isShowPage)
                {
                    iPage.setTranslationX(-ShareData.m_screenWidth);
                }
                break;
            case AD:
                if (mAdPage == null)
                {
                    mAdPage = new FullScreenADPage(getContext(), mSite.mFullScreenDisplayPageSite);
                    mAdPage.SetData(data);
                    params.gravity = Gravity.LEFT | Gravity.TOP;
                    addView(mAdPage, params);
                }
                iPage = mAdPage;
                break;
            default:
                break;
        }
        if (iPage != null)
        {
            if (isShowPage)
            {
                mCurIPage = iPage;
                mCurMode = mode;
            }
            //防止view不消费事件，传递其他页面
            iPage.setClickable(true);
        } else
        {
            throw new RuntimeException("please add correct view");
        }
        return iPage;
    }

    private int mRealViewH = ShareData.m_screenHeight;  //小米全面屏     隐藏虚拟键 m_screenHeight还是一样
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if(h > 0){
            mRealViewH = h;
        }
        if (mBottomAlbum != null && mCurMode != ALBUM)
        {
            mBottomAlbum.setTranslationY(h);
        }
    }

    /**
     * 下拉remove 相册或素材，侧拉remove 相册，上拉remove 素材
     *
     * @param mode
     */
    protected void removeModuleView(int mode)
    {
        ViewGroup parent = null;
        IPage removeView = null;
        switch (mode)
        {
            case CAMPAIGN:
                if (mCommunityPage != null)
                {
                    removeView = mCommunityPage;
                    if (mCommunityPage.getParent() != null)
                    {
                        parent = (ViewGroup) mCommunityPage.getParent();
                    }
                    mCommunityPage = null;
                }
                break;
            case ALBUM:
                if (mBottomAlbum != null)
                {
                    removeView = mBottomAlbum;
                    if (mBottomAlbum.getParent() != null)
                    {
                        parent = (ViewGroup) mBottomAlbum.getParent();
                    }
                    mBottomAlbum = null;
                }
                break;
            case MATERIAL:
                if (mRightTheme != null)
                {
                    removeView = mRightTheme;
                    if (mRightTheme.getParent() != null)
                    {
                        parent = (ViewGroup) mRightTheme.getParent();
                    }
                    mRightTheme = null;
                }
                break;
            case MENU:
                if (mLeftMenu != null)
                {
                    removeView = mLeftMenu;
                    if (mLeftMenu.getParent() != null)
                    {
                        parent = (ViewGroup) mLeftMenu.getParent();
                    }
                    mLeftMenu = null;
                }
                break;
            case BOOT:
                if (mLeftBoot != null)
                {
                    removeView = mLeftBoot;
                    if (mLeftBoot.getParent() != null)
                    {
                        parent = (ViewGroup) mLeftBoot.getParent();
                    }
                    mLeftBoot = null;
                }
                break;
            case AD:
                if (mAdPage != null)
                {
                    removeView = mAdPage;
                    if (mAdPage.getParent() != null)
                    {
                        parent = (ViewGroup) mAdPage.getParent();
                    }
                    mAdPage = null;
                }
                break;
            case HOME:
                if (mMainLayout != null)
                {
                    removeView = mMainLayout;
                    if (mMainLayout.getParent() != null)
                    {
                        parent = (ViewGroup) mMainLayout.getParent();
                    }
                    mMainLayout = null;
                }
                break;
            case BEAUTY:
                if (mBottomBeautyEntryPage != null)
                {
                    removeView = mBottomBeautyEntryPage;
                    if (mBottomBeautyEntryPage.getParent() != null)
                    {
                        parent = (ViewGroup) mBottomBeautyEntryPage.getParent();
                    }
                    mBottomBeautyEntryPage = null;
                }
                break;
            default:
                break;
        }
        if (removeView != null)
        {
            removeView.onClose();
            if (parent != null)
            {
                parent.removeView(removeView);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        if (mCurIPage != null)
        {
            mCurIPage.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart()
    {
        if (mCurIPage != null)
        {
            mCurIPage.onStart();
        }
    }

    @Override
    public void onRestart()
    {
        if (mCurIPage != null)
        {
            mCurIPage.onRestart();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if (mCurIPage != null)
        {
            mCurIPage.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (mCurIPage != null)
        {
            mCurIPage.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onStop()
    {
        if (mCurIPage != null)
        {
            mCurIPage.onStop();
        }
    }

    @Override
    public void onDestroy()
    {
        if (mCurIPage != null)
        {
            mCurIPage.onDestroy();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mCurIPage != null)
        {
            return mCurIPage.onActivityResult(requestCode, resultCode, data);
        }
        return super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackResult(int siteID, HashMap<String, Object> params)
    {
        super.onBackResult(siteID, params);
    }

}

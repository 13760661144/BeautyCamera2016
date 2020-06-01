package cn.poco.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.beautyaccount.RequestParam;
import com.adnonstop.missionhall.utils.interact_gz.CompleteUserMaterialUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import cn.poco.advanced.ImageUtils;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.community.CommunityPage;
import cn.poco.featuremenu.manager.AppFeatureManager;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.image.filter;
import cn.poco.login.AreaList.AreaInfo2;
import cn.poco.login.site.UserInfoPageSite;
import cn.poco.loginlibs.info.BaseInfo;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.scorelibs.CreditUtils;
import cn.poco.scorelibs.info.CreditIncomeInfo;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.CommonUI;
import cn.poco.utils.Utils;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * 个人信息页面
 */
public class UserInfoPage extends IPage {
    //private static final String TAG = "用户信息";

    public static final int NONE = 0;
    protected static final int NICKNAME = 1;
    protected static final int PHONE = 2;
    protected static final int CREDITS = 3;
    protected static final int PASSWORD = 4;
    protected static final int SEX = 5;
    protected static final int BIRTH = 6;
    protected static final int AREA = 7;
    protected static final int HEAD_IMG = 8;

    protected static final int CHECK = 9;
    protected static final int UPDATE_PSW = 10;
    protected static final int UPDATE_CREDITS = 11;
    protected static final int UPDATE_USERINFO = 12;
    public static final int TEXT_SIZE = 16;

    protected static final int RELOGIN = 20;

    protected LinearLayout m_mainFr;
    protected FrameLayout m_topFr;
    protected ImageView m_backBtn;
    protected FrameLayout m_headFr;
    protected ImageView m_cameraBtn;
    protected ImageView m_headImg;
    protected int m_topBarHeight;
    protected Bitmap m_bg;

    protected UserInfoItem m_nickname; //昵称
    protected UserInfoItem m_phone;    //手机号
    //	protected UserInfoItem m_credits; //积分
    protected UserInfoItem m_password; //密码
    protected UserInfoItem m_sex; //性别
    protected UserInfoItem m_birth;    //生日
    protected UserInfoItem m_area;    //地区

    protected TextView m_exitLogin;

    protected FrameLayout m_editFr;
    protected FrameLayout m_editTopBar;
    protected ImageView m_editBack;
    protected TextView m_editTitle;
    protected TextView m_editComplete;
    protected ListView m_editList;
    protected EditText m_editName;
    protected WheelDatePicker m_datePicker;

    protected LinearLayout m_pswFr;
    protected boolean m_oldPswShow;
    protected boolean m_newPswShow;
    protected EditText m_oldPsw;
    protected EditText m_newPsw;
    protected ImageView m_oldPswScan;
    protected ImageView m_newPswScan;
    protected String m_newPassword;
    protected String m_oldPassword;

    protected LinearLayout m_sexGroup;
    protected ChooseItem m_man;
    protected ChooseItem m_woman;
    protected boolean m_chooseMan = true;
    //	protected boolean m_hasUserInfoChanged = false;
    protected boolean m_uiEnabled = true;

    protected CitiesPicker m_citiesPicker;
    protected AreaInfo2[] m_allAreaInfos;
    protected EditHeadIconImgPage m_headEditPage;

    protected int m_editTopBarHeight;

    protected int m_mode = NONE;

    protected UserInfo m_localInfo;
    protected UserInfo m_netInfo;
    private String m_userId;
    protected Handler m_threadHandler;
//    protected HandlerThread m_thread;
    protected Handler m_handler;

    private WaitAnimDialog m_dlg;
    private UserInfoPageSite mSite;

    private SettingInfo m_settingInfo;

    private FullScreenDlg mDialog;

    private boolean isCanBack = true;

    private Toast mToast;

    private boolean isClose = false;
    private TipsDialog tipsDialog;

    public interface GetUserInfoCallback
    {
        public void callback(boolean success);
    }

    public UserInfoPage(Context context, BaseSite site) {
        super(context, site);
        InitData();
        InitUI();
        mSite = (UserInfoPageSite) site;

        MyBeautyStat.onPageStartByRes(R.string.个人中心_登录注册_个人资料页);
        TongJiUtils.onPageStart(getContext(), R.string.个人主页);

        EventCenter.addListener(mOnEventListener);
    }

    /**
     * @param params String id:  用户id<br/>
     *               boolean isHideCredit:是否隐藏积分选项，true是隐藏，false和默认显示<br/>
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params != null) {
            if (params.get("id") != null) {
                setDatas((String) params.get("id"));
            }
            if (params.get("isHideCredit") != null && ((boolean) params.get("isHideCredit"))) {
                hideCreditItem();
            }
        }

    }

    @SuppressLint("HandlerLeak")
    protected void InitData() {
        ShareData.InitData(getContext());
        m_topBarHeight = ShareData.PxToDpi_xhdpi(499);
        m_editTopBarHeight = ShareData.PxToDpi_xhdpi(90);
        mToast = Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_networkerror), Toast.LENGTH_SHORT);
//        m_thread = new HandlerThread("UserInfoPage");
//        m_thread.start();

//        m_threadHandler = new Handler(m_thread.getLooper()) {
        m_threadHandler = new Handler() {
            @Override
            public void dispatchMessage(final Message msg) {
                switch (msg.what) {
                    case CHECK: {
                        LoginUtils2.getUserInfo((String)msg.obj, m_settingInfo.GetPoco2Token(false), new HttpResponseCallback()
                        {
                            @Override
                            public void response(Object object)
                            {
                                if(object == null)
                                {
                                    isCanBack = true;
                                    return;
                                }
                                UserInfo info = (UserInfo)object;
                                if (info.mProtocolCode == 200 && info.mCode == 0) {
                                    m_netInfo = info;
                                    Message uimsg = m_handler.obtainMessage();
                                    uimsg.what = CHECK;
                                    m_handler.sendMessage(uimsg);
                                    isCanBack = true;
                                } else if (info.mCode == 55019 || info.mCode == 55951 || info.mCode == 55952 || info.mCode == 55953 || info.mCode == 55954) {
                                    Message reLoginMsg = Message.obtain();
                                    reLoginMsg.what = RELOGIN;
                                    m_handler.sendMessage(reLoginMsg);
                                } else {
                                    isCanBack = true;
                                    if(mToast != null) mToast.show();
                                }
                            }
                        });

//                        m_netInfo = LoginUtils.getUserInfo((String) msg.obj, m_settingInfo.GetPoco2Token(false), AppInterface.GetInstance(getContext()));
//                        if (m_netInfo != null) {
//                            if (m_netInfo.mProtocolCode == 200 && m_netInfo.mCode == 0) {
//                                Message uimsg = m_handler.obtainMessage();
//                                uimsg.what = CHECK;
//                                m_handler.sendMessage(uimsg);
//                                isCanBack = true;
//                            } else if (m_netInfo.mProtocolCode == 205) {
//                                Message reLoginMsg = Message.obtain();
//                                reLoginMsg.what = RELOGIN;
//                                m_handler.sendMessage(reLoginMsg);
//                            } else {
//                                isCanBack = true;
//                            }
//                        } else {
//                            isCanBack = true;
//                        }
                        break;
                    }
                    case UPDATE_PSW: {
                        LoginUtils2.changePassWord(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), m_oldPassword, m_newPassword, new HttpResponseCallback()
                        {
                            @Override
                            public void response(Object object)
                            {
                                Message uimsg = m_handler.obtainMessage();
                                uimsg.what = UPDATE_PSW;
                                uimsg.obj = (BaseInfo)object;
                                m_handler.sendMessage(uimsg);
                            }
                        });

//                        BaseInfo data = LoginUtils.changePassWord(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), m_oldPassword, m_newPassword, AppInterface.GetInstance(getContext()));
//                        Message uimsg = m_handler.obtainMessage();
//                        uimsg.what = UPDATE_PSW;
//                        uimsg.obj = data;
//                        m_handler.sendMessage(uimsg);
                        break;
                    }
                    case UPDATE_CREDITS: {
                        if (m_netInfo != null) {
                            final int actionId = (int) msg.obj;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    CreditIncomeInfo info = CreditUtils.CreditIncome(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), actionId + "", null, AppInterface.GetInstance(getContext()));
                                    Message uimsg = m_handler.obtainMessage();
                                    uimsg.what = UPDATE_CREDITS;
                                    uimsg.obj = info;
                                    m_handler.sendMessage(uimsg);
                                }
                            }).start();
                        }
                        break;
                    }
                    case UPDATE_USERINFO: {
                        if(m_netInfo == null)
                        {
                            Message uimsg = m_handler.obtainMessage();
                            uimsg.what = UPDATE_USERINFO;
                            uimsg.obj = null;
                            m_handler.sendMessage(uimsg);
                            return;
                        }
                        if (m_newPassword != null && m_newPassword.length() == 0) {
                            m_newPassword = null;
                        }
                        LoginUtils2.updateUserInfo(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), m_netInfo, new HttpResponseCallback()
                        {
                            @Override
                            public void response(Object object)
                            {
                                if(object == null)
                                {
                                    Message uimsg = m_handler.obtainMessage();
                                    uimsg.what = UPDATE_USERINFO;
                                    uimsg.obj = null;
                                    m_handler.sendMessage(uimsg);
                                    return;
                                }
                                BaseInfo data = (BaseInfo)object;
                                if(data.mCode == 0 && msg.obj != null && (Integer) msg.obj > 0)
                                {
                                    Message creidtMsg = m_threadHandler.obtainMessage();
                                    creidtMsg.what = UPDATE_CREDITS;
                                    creidtMsg.obj = msg.obj;
                                    m_threadHandler.sendMessage(creidtMsg);

                                    new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            CompleteUserMaterialUtil.onUserMaterialChanged();
                                        }
                                    }).start();
                                }
                                Message uimsg = m_handler.obtainMessage();
                                uimsg.what = UPDATE_USERINFO;
                                uimsg.obj = data;
                                m_handler.sendMessage(uimsg);
                            }
                        });

//                        BaseInfo data = null;
//                        if (m_netInfo != null) {
//                            if (m_newPassword != null && m_newPassword.length() == 0) {
//                                m_newPassword = null;
//                            }
//                            data = LoginUtils.updateUserInfo(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), m_netInfo, AppInterface.GetInstance(getContext()));
//                        }
//                        //修改成功后发消息到网站加积分
//                        if (data != null && data.mCode == 0 && (Integer) msg.obj > 0) {
//                            Message creidtMsg = m_threadHandler.obtainMessage();
//                            creidtMsg.what = UPDATE_CREDITS;
//                            creidtMsg.obj = msg.obj;
//                            m_threadHandler.sendMessage(creidtMsg);
//
//                            CompleteUserMaterialUtil.onUserMaterialChanged();
//                        }
//
//                        Message uimsg = m_handler.obtainMessage();
//                        uimsg.what = UPDATE_USERINFO;
//                        uimsg.obj = data;
//                        m_handler.sendMessage(uimsg);
                        break;
                    }
                }
            }
        };

        m_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHECK: {
                        if (m_netInfo != null) {
                            m_localInfo = (UserInfo) m_netInfo.clone();
                            UpdateDataToUI(m_netInfo, true);
                        }
                        break;
                    }
                    case UPDATE_PSW: {
                        BaseInfo data = (BaseInfo) msg.obj;
                        if (data != null) {
                            if (data.mCode == 0 && data.mProtocolCode == 200) {
                                CommunityPage.isUserInfoModified = true;
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_modifysuccess), Toast.LENGTH_SHORT).show();
                                onBackBtn(false);
                            } else if (data.mCode == 10001) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_paramdataerror), Toast.LENGTH_SHORT).show();
                            } else if (data.mCode == 10002) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_oldpswerror), Toast.LENGTH_SHORT).show();
                            } else if (data.mCode == 10003) {
                                Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_operafail), Toast.LENGTH_SHORT).show();
                            } else {
                                if (data != null && data.mMsg != null) {
                                    Toast.makeText(getContext(), data.mMsg, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_modifypswfail), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (mToast != null) {
                                mToast.cancel();
                                mToast = null;
                            }
                            mToast = Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_networkerror), Toast.LENGTH_SHORT);
                            mToast.show();
                        }
                        SetWaitUI(false, "");
                        break;
                    }
                    case UPDATE_CREDITS: {
                        CreditIncomeInfo points = (CreditIncomeInfo) msg.obj;
                        if (points != null) {
                            if (points.mIncomeItems != null && points.mIncomeItems.size() > 0 && points.mIncomeItems.get(0).values > 0) {
                                if (m_netInfo != null) {
                                    m_netInfo.mFreeCredit = points.mCreditTotal;
                                    UpdateDataToUI(m_netInfo, false);
                                }
                                String m = points.mIncomeItems.get(0).message;
                                if (m != null && m.length() > 0) {
                                    Toast toast = Toast.makeText(getContext(), m, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        }
                        break;
                    }
                    case UPDATE_USERINFO: {
                        BaseInfo data = (BaseInfo) msg.obj;
                        if (data != null && data.mCode == 0) {
                            CommunityPage.isUserInfoModified = true;
                            m_localInfo = (UserInfo) m_netInfo.clone();
                            UpdateDataToUI(m_netInfo, false);
                            UserMgr.SaveCache(m_netInfo);
                            EventCenter.sendEvent(EventID.UPDATE_USER_INFO);
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_modifysuccess), Toast.LENGTH_SHORT).show();
                        } else {
                            m_netInfo = (UserInfo) m_localInfo.clone();
                            UpdateDataToUI(m_localInfo, false);
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_modifyfail), Toast.LENGTH_SHORT).show();
                        }
                        m_uiEnabled = true;
                        SetWaitUI(false, "");
                        if (m_mode == NICKNAME) {
                            onBackBtn(false);
                        }
                        break;
                    }
                    case RELOGIN: {
                        if (!isClose) {
                            tipsDialog = new TipsDialog(getContext(), null, getContext().getResources().getString(R.string.userinfopage_loginoverdata), null, getContext().getResources().getString(R.string.userinfopage_sure), new TipsDialog.Listener() {
                                @Override
                                public void cancel() {

                                }

                                @Override
                                public void ok() {
                                    UserMgr.ExitLogin(getContext());
                                    EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
                                    AppFeatureManager.getInstance().Invalidate();
                                    mSite.toLoginPage(getContext());
                                }
                            });
                            if (!isClose) {
                                tipsDialog.showDialog();
                            } else {
                                if (tipsDialog != null) {
                                    tipsDialog.dissmissDialog();
                                    tipsDialog = null;
                                }
                            }
                            isCanBack = true;
                        }
                        break;
                    }
                }
            }
        };
        m_settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
    }

    protected void InitUI() {
//		this.setBackgroundColor(0xffedede9);
        SetBK(UserInfoPage.this, true);
        LayoutParams fl;

        ScrollView scroll = new ScrollView(getContext());
        scroll.setVerticalScrollBarEnabled(false);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        scroll.setLayoutParams(fl);
        this.addView(scroll);

        m_mainFr = new LinearLayout(getContext());
        m_mainFr.setOrientation(LinearLayout.VERTICAL);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_mainFr.setLayoutParams(fl);
        scroll.addView(m_mainFr);

        LinearLayout.LayoutParams ll;
        m_topFr = new FrameLayout(getContext());
        ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, m_topBarHeight);
        m_topFr.setLayoutParams(ll);
        m_mainFr.addView(m_topFr);
        {
            m_headFr = new FrameLayout(getContext());
            fl = new LayoutParams(ShareData.PxToDpi_xhdpi(166), ShareData.PxToDpi_xhdpi(166));
            fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            fl.topMargin = ShareData.PxToDpi_xhdpi(166);
            m_headFr.setLayoutParams(fl);
            m_topFr.addView(m_headFr);
            {
                m_headImg = new ImageView(getContext());
                m_headImg.setScaleType(ScaleType.CENTER);
                fl = new LayoutParams(ShareData.PxToDpi_xhdpi(166), ShareData.PxToDpi_xhdpi(166));
                fl.gravity = Gravity.CENTER;
                m_headImg.setLayoutParams(fl);
                m_headFr.addView(m_headImg);
                m_headImg.setOnClickListener(m_btnListener);

                m_cameraBtn = new ImageView(getContext());
                m_cameraBtn.setScaleType(ScaleType.CENTER);
                m_cameraBtn.setBackgroundResource(R.drawable.userinfo_camera_btn_bg);
                m_cameraBtn.setImageResource(R.drawable.userinfo_camera_btn_color_icon);
                fl = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                m_cameraBtn.setLayoutParams(fl);
                m_headFr.addView(m_cameraBtn);
                m_cameraBtn.setOnClickListener(m_btnListener);
                ImageUtils.AddSkin(getContext(), m_cameraBtn);
            }

            m_backBtn = new ImageView(getContext());
            m_backBtn.setImageResource(R.drawable.userinfo_back_btn_over);
            fl = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.TOP | Gravity.LEFT;
            fl.topMargin = ShareData.PxToDpi_xhdpi(5);
            fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
            m_backBtn.setLayoutParams(fl);
            m_topFr.addView(m_backBtn);
            m_backBtn.setOnClickListener(m_btnListener);
        }

        m_nickname = new UserInfoItem(getContext());
        m_nickname.setTitle(getContext().getResources().getString(R.string.userinfopage_nickname));
        AddView(m_nickname);
        m_phone = new UserInfoItem(getContext());
        m_phone.setTitle(getContext().getResources().getString(R.string.userinfopage_phonenum));
        m_phone.showArrow(false);
        AddView(m_phone);
//		m_credits = new UserInfoItem(getContext());
//		m_credits.setTitle("我的积分");
//		AddView(m_credits);
        m_sex = new UserInfoItem(getContext());
        m_sex.setTitle(getContext().getResources().getString(R.string.userinfopage_sex));
        AddView(m_sex);
        m_birth = new UserInfoItem(getContext());
        m_birth.setTitle(getContext().getResources().getString(R.string.userinfopage_bird));
        AddView(m_birth);
        m_area = new UserInfoItem(getContext());
        m_area.setTitle(getContext().getResources().getString(R.string.userinfopage_area));
        AddView(m_area);
        m_password = new UserInfoItem(getContext());
        m_password.setTitle(getContext().getResources().getString(R.string.userinfopage_modifypsw));
        m_password.setVisibility(View.GONE);
        AddView(m_password);

        m_exitLogin = new TextView(getContext());
        m_exitLogin.setBackgroundColor(Color.WHITE);
        m_exitLogin.setGravity(Gravity.CENTER);
        m_exitLogin.setText(getContext().getResources().getString(R.string.userinfopage_exitlogin));
        m_exitLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        m_exitLogin.setTextColor(ImageUtils.GetSkinColor(0xffff1d5d));
        ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
        ll.gravity = Gravity.CENTER_HORIZONTAL;
        ll.topMargin = ShareData.PxToDpi_xhdpi(60);
        m_exitLogin.setLayoutParams(ll);
        m_mainFr.addView(m_exitLogin);
        m_exitLogin.setOnClickListener(m_btnListener);

        m_editFr = new FrameLayout(getContext());
        m_editFr.setVisibility(View.GONE);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_editFr.setLayoutParams(fl);
        this.addView(m_editFr);

        m_dlg = new WaitAnimDialog((Activity) getContext());
    }

    private void hideCreditItem() {
//		if(m_credits != null) m_credits.setVisibility(GONE);
    }

    private void SetWaitUI(boolean flag, String str) {
        if (flag) {
            if (m_dlg != null) {
                m_dlg.SetText(str);
                m_dlg.show();
            }
        } else {
            if (m_dlg != null) {
                m_dlg.hide();
            }
        }
    }

    protected void AddView(View view) {
        int viewH = ShareData.PxToDpi_xhdpi(88);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewH);
        view.setLayoutParams(ll);
        m_mainFr.addView(view);
        view.setOnClickListener(m_btnListener);
    }

    private EventCenter.OnEventListener mOnEventListener = new EventCenter.OnEventListener() {
        @Override
        public void onEvent(int eventId, Object[] params) {
            if (eventId == EventID.USE_INFO_PAGE_FORCE_EXIT_LOGIN) {
                //当被另一台设备挤下线时强制退出并关闭页面
                if (m_handler != null) {
                    m_handler.post(new Runnable() {
                        @Override
                        public void run() {
                            UserMgr.ExitLogin(getContext());
                            AppFeatureManager.getInstance().Invalidate();
                            EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);

                            CommunityPage.clearLoginInfo(getContext());
                            if (mSite != null) {
                                mSite.onExit(getContext());
                            }
                        }
                    });
                }
            }
        }
    };

    private void getUserInfo(final GetUserInfoCallback callback)
    {
        isCanBack = false;
        LoginUtils2.getUserInfo(m_userId, m_settingInfo.GetPoco2Token(false), new HttpResponseCallback()
        {
            @Override
            public void response(Object object)
            {
                isCanBack = true;
                if(object == null)
                {
                    if(callback != null) callback.callback(false);
                    return;
                }
                m_netInfo = (UserInfo)object;
                if(m_netInfo.mProtocolCode == 200 && m_netInfo.mCode == 0)
                {
                    if(callback != null) callback.callback(true);
                    return;
                }
                m_netInfo = null;
                if(callback != null) callback.callback(false);
            }
        });
    }

    protected OnClickListener m_btnListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == m_backBtn && isCanBack) {
                if (m_netInfo != null) {
                    if (m_netInfo.mProtocolCode == 200 && m_netInfo.mCode == 0) {
                        UserMgr.SaveCache(m_netInfo);
                    }
                }
                mSite.onBack(getContext());
            } else if (v == m_editBack) {
                onBackBtn(false);
            } else if (v == m_exitLogin) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_退出登录);
                UserMgr.ExitLogin(getContext());
                AppFeatureManager.getInstance().Invalidate();
                EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);

                CommunityPage.clearLoginInfo(getContext());
//				PocoCamera.main.onBack();
                mSite.onExit(getContext());
            } else if (v == m_editComplete) {
                if (m_mode == NICKNAME) {
                    if (m_netInfo != null) {
                        String text = String.valueOf(m_editName.getEditableText());
                        if (text.length() == 0) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_nicknametips), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        m_netInfo.mNickname = text;

                        m_uiEnabled = false;

                        SetWaitUI(true, getContext().getResources().getString(R.string.userinfopage_saving));
                        Message msg = m_threadHandler.obtainMessage();
                        msg.what = UPDATE_USERINFO;
                        msg.obj = -1;
                        m_threadHandler.sendMessage(msg);
                        hideSoftInput(m_editName);
                    }
//					onBackBtn(false);
                } else if (m_mode == PASSWORD) {
                    m_oldPassword = String.valueOf(m_oldPsw.getEditableText());
                    m_newPassword = String.valueOf(m_newPsw.getEditableText());
                    if (m_oldPassword.equals("")) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_writeoripsw), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    switch(RequestParam.isRightPassword(m_newPassword))
                    {
                        case -1:
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_write_new_psw), Toast.LENGTH_SHORT).show();
                            return;

                        case -2:
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.loginpage_passwordtips3), Toast.LENGTH_SHORT).show();
                            return;

                        case -3:
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_pswtips), Toast.LENGTH_SHORT).show();
                            return;
                    }

                    hideSoftInput(m_editComplete);
                    SetWaitUI(true, "");
                    Message msg = m_threadHandler.obtainMessage();
                    msg.what = UPDATE_PSW;
                    m_threadHandler.sendMessage(msg);
                }
            } else if (v == m_oldPswScan) {
                if (m_oldPswShow) {
                    m_oldPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    m_oldPswScan.setImageResource(R.drawable.userinfo_psw_show);
                } else {
                    m_oldPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    m_oldPswScan.setImageResource(R.drawable.userinfo_psw_hide);
                }
                m_oldPswShow = !m_oldPswShow;
                m_oldPsw.setSelection(m_oldPsw.getText().length());
            } else if (v == m_newPswScan) {
                if (m_newPswShow) {
                    m_newPsw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    m_newPswScan.setImageResource(R.drawable.userinfo_psw_show);
                } else {
                    m_newPsw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    m_newPswScan.setImageResource(R.drawable.userinfo_psw_hide);
                }
                m_newPswShow = !m_newPswShow;
                m_newPsw.setSelection(m_newPsw.getText().length());
            } else if (v == m_man) {
                if (m_netInfo != null) {
                    m_netInfo.mSex = "男";
                }
                m_chooseMan = true;
                m_man.onChoose(m_chooseMan);
                m_woman.onChoose(!m_chooseMan);

            } else if (v == m_woman) {
                if (m_netInfo != null) {
                    m_netInfo.mSex = "女";
                }
                m_chooseMan = false;
                m_man.onChoose(m_chooseMan);
                m_woman.onChoose(!m_chooseMan);
            }
//			else if(v == m_credits)
//			{
////				m_mode = CREDITS;
////				showEditView(NONE, m_mode);
//				mSite.onOpenCredit(m_settingInfo.GetPoco2Id(false), m_settingInfo.GetPoco2Token(false), null);
//			}

            if (v == m_nickname) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_昵称);
                m_mode = NICKNAME;

                showEditView(NONE, m_mode);
            } else if (v == m_password) {
                if (m_netInfo != null && m_netInfo.mMobile != null && m_netInfo.mMobile.length() > 0 && !m_netInfo.mMobile.equals("0")) {
                    MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_修改密码);
                    m_mode = PASSWORD;
                    showEditView(NONE, m_mode);
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.userinfopage_bindphonetips), Toast.LENGTH_SHORT).show();
                }
            } else if (v == m_sex) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_性别);
                m_mode = SEX;

                showEditView(NONE, m_mode);
            } else if (v == m_birth) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_生日);
                m_mode = BIRTH;

                showEditView(NONE, m_mode);
            } else if (v == m_area) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_地区);
                m_mode = AREA;

                showEditView(NONE, m_mode);
            } else if (v == m_cameraBtn) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_头像);
                if (m_isSaveBG && m_tempBGPath != null && m_tempBGPath.length() > 0) {
                    mSite.onCamera(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), m_tempBGPath, getContext());
                } else {
                    mSite.onCamera(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), null, getContext());
                }
            } else if (v == m_headImg) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_个人资料页_头像);
                HashMap<String, Object> params = new HashMap<>();

                if (m_isSaveBG && m_tempBGPath != null && m_tempBGPath.length() > 0) {
                    mSite.onChooseHeadBmp(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), m_tempBGPath, getContext());
                } else {
                    mSite.onChooseHeadBmp(m_netInfo.mUserId, m_settingInfo.GetPoco2Token(false), null, getContext());
                }

                return;
            } else if (v == m_phone) {
                if (m_localInfo.mMobile != null && m_localInfo.mMobile.length() > 0 && !m_localInfo.mMobile.equals("0")) {
                    return;
                }
                LoginInfo linfo = new LoginInfo();
                linfo.mUserId = m_localInfo.mUserId;
                linfo.mAccessToken = m_settingInfo.GetPoco2Token(false);
                HashMap<String, Object> datas = new HashMap<>();
                datas.put("loginInfo", linfo);
                datas.put("isHide", true);
                mSite.onBindPhone(datas, getContext());
            }

//            else if (!m_editable && isCanBack) {
//                mToast.show();
//            }

        }
    };

    protected boolean onBackBtn(boolean systemBackBtn) {
        if (m_mode == NONE && m_uiEnabled == false) return false;
        if (m_mode != NONE) {
            if (m_mode == AREA) {
                if (m_citiesPicker != null && m_citiesPicker.onBack()) {
                    return false;
                }
            } else if (m_mode == HEAD_IMG) {
//				PocoCamera.main.closePopupPage(m_choosePage);

            } else if (m_mode == BIRTH) {
                if (!m_localInfo.mBirthdayYear.equals(m_netInfo.mBirthdayYear) || !m_localInfo.mBirthdayMonth.equals(m_netInfo.mBirthdayMonth) || !m_localInfo.mBirthdayDay.equals(m_netInfo.mBirthdayDay)) {
                    m_uiEnabled = false;
                    SetWaitUI(true, getContext().getResources().getString(R.string.userinfopage_saving));
                    Message msg = m_threadHandler.obtainMessage();
                    msg.what = UPDATE_USERINFO;
                    msg.obj = getContext().getResources().getInteger(R.integer.积分_补充资料_生日);
                    m_threadHandler.sendMessage(msg);
                }

            } else if (m_mode == SEX) {
                if (m_netInfo.mSex != null && m_netInfo.mSex.length() > 0 && !m_netInfo.mSex.equals(m_localInfo.mSex)) {
                    m_uiEnabled = false;
                    SetWaitUI(true, getContext().getResources().getString(R.string.userinfopage_saving));
                    Message msg = m_threadHandler.obtainMessage();
                    msg.what = UPDATE_USERINFO;
                    msg.obj = getContext().getResources().getInteger(R.integer.积分_补充资料_性别);
                    m_threadHandler.sendMessage(msg);
                }
            }
            showEditView(m_mode, NONE);

            m_mode = NONE;

            UpdateDataToUI(m_localInfo, false);
            return false;
        } else if (!systemBackBtn && isCanBack) {
            mSite.onBack(getContext());
        }

        return true;
    }

    protected void showEditView(int lastMode, int mode) {
        clearUI(lastMode);
        m_editFr.setVisibility(View.VISIBLE);
        switch (mode) {
            case NICKNAME: {
                initNickPage();
//				addShowAnim(m_editFr,true);
//				DoLeftAnim(m_editFr,true);
//				DoLeftAnim(m_mainFr,false);
                DoLeftAnim1(m_editFr, true);
                DoLeftAnim1(m_mainFr, false);
                break;
            }
            case PASSWORD: {
                initPswPage();
//				addShowAnim(m_editFr,true);
//				DoLeftAnim(m_editFr,true);
//				DoLeftAnim(m_mainFr,false);
                DoLeftAnim1(m_editFr, true);
                DoLeftAnim1(m_mainFr, false);
                break;
            }
            case BIRTH: {
                m_mainFr.setVisibility(View.VISIBLE);
                initBirthPage();
                break;
            }
            case AREA: {
                initAreaPage();
//				addShowAnim(m_editFr,true);
//				DoLeftAnim(m_editFr,true);
//				DoLeftAnim(m_mainFr,false);
                DoLeftAnim1(m_editFr, true);
                DoLeftAnim1(m_mainFr, false);
                break;
            }
            case SEX: {
                initSexPage();
//				DoLeftAnim(m_editFr,true);
//				DoLeftAnim(m_mainFr,false);
                DoLeftAnim1(m_editFr, true);
                DoLeftAnim1(m_mainFr, false);
                break;
            }
            case CREDITS: {
                initCreditDeclare();
                break;
            }
            case NONE: {
                if (lastMode != BIRTH) {
                    m_mainFr.setVisibility(View.VISIBLE);
//				m_editFr.setVisibility(View.GONE);
//					DoRightAnim(m_mainFr,true);
//					DoRightAnim(m_editFr,false);
                    DoRightAnim1(m_mainFr, true);
                    DoRightAnim1(m_editFr, false);
                }
            }
            default:
                break;
        }
    }

    protected void clearUI(int mode) {
        switch (mode) {
            case NICKNAME: {
                hideSoftInput(m_editName);
                break;
            }
            case PASSWORD: {
                hideSoftInput(m_oldPsw);
            }
            case NONE: {
//				m_mainFr.setVisibility(View.GONE);
            }
        }

        if (mode != NONE) {
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    m_editFr.removeAllViews();
                    m_editFr.setVisibility(View.GONE);
                }
            }, 350);
        }

//		if(mode != NONE)
//		{
//			m_editFr.removeAllViews();
//			m_editFr.setVisibility(View.GONE);
//		}
//		else
//		{
//			this.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					m_editFr.removeAllViews();
//					m_editFr.setVisibility(View.GONE);
//				}
//			},350);
//		}
    }

    protected void initNickPage() {
//		m_editFr.setBackgroundDrawable(new BitmapDrawable(m_bg));
        SetBK(m_editFr, false);
        InitEditTopBar(getContext().getResources().getString(R.string.userinfopage_modifynickname), true);

        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        inflater.inflate(R.layout.edittext, m_editFr);
        m_editName = (EditText) findViewById(R.id.text);
        m_editName.setBackgroundColor(Color.WHITE);
        m_editName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE);
        m_editName.setTextColor(0xff333333);
        m_editName.setSingleLine();
        m_editName.setGravity(Gravity.CENTER_VERTICAL);
        MyTextUtils.setupLengthFilter(m_editName, getContext(), 32, true);
        m_editName.setPadding(ShareData.PxToDpi_xhdpi(28), 0, 0, 0);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
        fl.gravity = Gravity.TOP;
        fl.topMargin = ShareData.PxToDpi_xhdpi(40) + m_editTopBarHeight;
        m_editName.setLayoutParams(fl);
        m_editName.requestFocus();
        CommonUI.modifyEditTextCursor(m_editName, ImageUtils.GetSkinColor(0xffe75988));
//		m_editName.setFilters(new InputFilter[]{new InputFilter() {
//			@Override
//			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//				if (source.equals(" ")) {
//					Toast.makeText(getContext(), "昵称不能含有空格！", Toast.LENGTH_SHORT).show();
//					return "";
//				}
//				else
//				{
//					return null;
//				}
//
//			}
//		}
//		});
        showSoftInput(m_editName);

        if (m_netInfo != null) {
            m_editName.setText(m_netInfo.mNickname);
            m_editName.setSelection(m_editName.getText().toString().length());
        }
    }


    protected void initPswPage() {
        m_oldPswShow = false;
        m_newPswShow = false;
        InitEditTopBar(getContext().getResources().getString(R.string.userinfopage_modifypsw), true);
//		m_editFr.setBackgroundDrawable(new BitmapDrawable(m_bg));
        SetBK(m_editFr, false);

        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        inflater.inflate(R.layout.userinfo_psw_view, m_editFr);
        m_pswFr = (LinearLayout) findViewById(R.id.psw);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP;
        fl.topMargin = ShareData.PxToDpi_xhdpi(40) + m_editTopBarHeight;
        m_pswFr.setLayoutParams(fl);

        m_oldPsw = (EditText) findViewById(R.id.oldPsw);
        m_oldPsw.setHint(getContext().getResources().getString(R.string.userinfopage_oldpassword));
        m_oldPsw.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        m_oldPsw.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE);
        CommonUI.modifyEditTextCursor(m_oldPsw, ImageUtils.GetSkinColor(0xffe75988));
        m_oldPsw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20), new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                }
                return source;
            }
        }});
        m_oldPswScan = (ImageView) findViewById(R.id.oldPswScan);
        m_oldPswScan.setOnClickListener(m_btnListener);
        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(34);
        m_oldPswScan.setLayoutParams(fl);

        fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
        fl.leftMargin = ShareData.PxToDpi_xhdpi(30);
        m_oldPsw.setLayoutParams(fl);

        m_newPsw = (EditText) findViewById(R.id.newPsw);
        m_newPsw.setHint(getContext().getResources().getString(R.string.userinfopage_newpassword));

        m_newPsw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20), new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                }
                return source;
            }
        }});
        m_newPsw.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                String passwordRuleStr = getContext().getString(R.string.rule_password);
                return passwordRuleStr.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });
        m_oldPsw.setImeOptions(EditorInfo.IME_ACTION_DONE);
        CommonUI.modifyEditTextCursor(m_newPsw, ImageUtils.GetSkinColor(0xffe75988));
        m_newPsw.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE);
        m_newPswScan = (ImageView) findViewById(R.id.newPswScan);
        m_newPswScan.setOnClickListener(m_btnListener);
        m_newPsw.setLayoutParams(fl);

        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        fl.rightMargin = ShareData.PxToDpi_xhdpi(34);
        m_newPswScan.setLayoutParams(fl);

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        ll.leftMargin = ShareData.PxToDpi_xhdpi(30);
        ImageView line = (ImageView) findViewById(R.id.line);
        line.setLayoutParams(ll);

        m_oldPsw.requestFocus();
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftInput(m_oldPsw);
            }
        }, 360);
    }

    protected void initCreditDeclare() {
        InitEditTopBar(getContext().getResources().getString(R.string.userinfopage_integraltitle), false);
        m_editFr.setBackgroundColor(Color.WHITE);

        LinearLayout con = new LinearLayout(getContext());
        con.setBackgroundColor(Color.WHITE);
        con.setOrientation(LinearLayout.VERTICAL);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP;
        fl.topMargin = ShareData.PxToDpi_xhdpi(40) + m_editTopBarHeight;
        con.setLayoutParams(fl);
        m_editFr.addView(con);

        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips1), con);
        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips2), con);
        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips3), con);
        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips4), con);
        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips5), con);
        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips6), con);
        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips7), con);
        AddCreditDeclareView(getContext().getResources().getString(R.string.userinfopage_integraltips8), con);
    }

    protected void AddCreditDeclareView(String info, ViewGroup parent) {
        TextView text = new TextView(getContext());
        text.setPadding(ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(20));
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        text.setTextColor(0xff999999);
        autoSplitText(text, info);
        text.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        text.setLayoutParams(ll);
        parent.addView(text);
    }

    private void autoSplitText(TextView tv, String text) {
        String rawText = text; //原始文本
        Paint pt = tv.getPaint();
        float width = ShareData.m_screenWidth - tv.getPaddingLeft() - tv.getPaddingRight();
        int len = text.length();
        StringBuilder builder = new StringBuilder();
        float curWidth = 0;
        String space = "    ";
        float spaceWidth = pt.measureText(space);
        for (int i = 0; i < len; i++) {
            curWidth += pt.measureText(String.valueOf(rawText.charAt(i)));
            if (curWidth > width) {
                curWidth = 0;
                curWidth += spaceWidth + pt.measureText(String.valueOf(rawText.charAt(i)));
                builder.append("\n");
                builder.append(space);
                builder.append(rawText.charAt(i));
                continue;
            }
            builder.append(rawText.charAt(i));
        }
        String newText = builder.toString();
        tv.setText(newText);

    }

    protected void initBirthPage() {
        m_editFr.setBackgroundColor(0x99000000);

        FrameLayout fr = new FrameLayout(getContext());
        fr.setBackgroundColor(Color.WHITE);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(400));
        fl.gravity = Gravity.BOTTOM;
        fr.setLayoutParams(fl);
        m_editFr.addView(fr);
        m_editFr.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                m_editFr.setOnClickListener(null);
                onBackBtn(false);
            }
        });

        int year;
        int monthOfYear;
        int dayOfMonth;
        if (m_netInfo != null) {
            year = Integer.valueOf(m_netInfo.mBirthdayYear);
            monthOfYear = Integer.valueOf(m_netInfo.mBirthdayMonth);
            dayOfMonth = Integer.valueOf(m_netInfo.mBirthdayDay);
        } else {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        m_datePicker = new WheelDatePicker(getContext());
        m_datePicker.setBackgroundColor(0xffffffff);
        m_datePicker.SetOnFocusChangeListener(m_OnDateChangedListener);
        m_datePicker.InitDate(year, monthOfYear, dayOfMonth);
        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(400));
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        m_datePicker.setLayoutParams(fl);
        fr.addView(m_datePicker);
        DoUpAnim1(fr, true);

//		fl = (LayoutParams) m_editFr.getLayoutParams();
//		fl.leftMargin = 0;
//		m_editFr.setLayoutParams(fl);
        m_editFr.setTranslationX(0);
    }

    protected WheelDatePicker.OnFocusChangeListener m_OnDateChangedListener = new WheelDatePicker.OnFocusChangeListener() {

        @Override
        public void onChange(int year, int month, int day) {
            if (m_netInfo != null) {
                m_netInfo.mBirthdayYear = String.valueOf(year);
                m_netInfo.mBirthdayMonth = String.valueOf(month);
                m_netInfo.mBirthdayDay = String.valueOf(day);
            }
            UpdateDataToUI(m_netInfo, false);
        }

    };

    protected void initAreaPage() {
        InitEditTopBar(getContext().getResources().getString(R.string.userinfopage_area), false);
//		m_editFr.setBackgroundDrawable(new BitmapDrawable(m_bg));
        SetBK(m_editFr, false);
//		m_editFr.setBackgroundColor(Color.WHITE);
        if (m_allAreaInfos == null) {
            m_allAreaInfos = AreaList.GetLocationLists(getContext());
        }

        m_citiesPicker = new CitiesPicker(getContext(), m_citiesCB);
        if (m_netInfo != null && m_netInfo.mLocationId != null && m_netInfo.mLocationId.length() > 0) {
            m_citiesPicker.setSelectAreaId(Long.valueOf(m_netInfo.mLocationId));
        }
        m_citiesPicker.initData(m_allAreaInfos);
//		m_citiesPicker.setBackgroundColor(Color.WHITE);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.m_screenHeight - m_editTopBarHeight);
        fl.gravity = Gravity.TOP;
        fl.topMargin = m_editTopBarHeight;
        m_citiesPicker.setLayoutParams(fl);
        m_editFr.addView(m_citiesPicker);
    }

    protected CitiesPicker.OnChooseCallback m_citiesCB = new CitiesPicker.OnChooseCallback() {

        @Override
        public void onChoose(long id) {
            if (m_netInfo != null && m_uiEnabled) {
                if (m_netInfo.mLocationId.equals(String.valueOf(id))) {
                    showEditView(m_mode, NONE);

                    m_mode = NONE;
                } else {
                    m_netInfo.mLocationId = String.valueOf(id);
                    m_uiEnabled = false;

                    UserInfoPage.this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SetWaitUI(true, getContext().getResources().getString(R.string.userinfopage_saving));
                            Message msg = m_threadHandler.obtainMessage();
                            msg.what = UPDATE_USERINFO;
                            msg.obj = getContext().getResources().getInteger(R.integer.积分_补充资料_地区);
                            m_threadHandler.sendMessage(msg);
                        }
                    }, 350);

                    showEditView(m_mode, NONE);

                    m_mode = NONE;

                    UpdateDataToUI(m_netInfo, false);
                }
            }

        }
    };

    protected void initSexPage() {
        InitEditTopBar(getContext().getResources().getString(R.string.userinfopage_modifysex), false);
//		m_editFr.setBackgroundDrawable(new BitmapDrawable(m_bg));
        SetBK(m_editFr, false);
        m_chooseMan = false;
        if (m_netInfo != null && m_netInfo.mSex != null && m_netInfo.mSex.equals("男")) {
            m_chooseMan = true;
        }

        m_sexGroup = new LinearLayout(getContext());
        m_sexGroup.setBackgroundColor(Color.WHITE);
        m_sexGroup.setOrientation(LinearLayout.VERTICAL);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP;
        fl.topMargin = ShareData.PxToDpi_xhdpi(40) + m_editTopBarHeight;
        m_sexGroup.setLayoutParams(fl);
        m_editFr.addView(m_sexGroup);
        {
            m_man = new ChooseItem(getContext());
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
            m_man.setText(getContext().getResources().getString(R.string.userinfopage_man));
            m_man.showArrow(false);
            m_man.onChoose(m_chooseMan);
            m_man.setOnClickListener(m_btnListener);
            m_man.setLayoutParams(ll);
            m_sexGroup.addView(m_man);

            m_woman = new ChooseItem(getContext());
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(88));
            m_woman.setText(getContext().getResources().getString(R.string.userinfopage_women));
            m_woman.showArrow(false);
            m_woman.onChoose(!m_chooseMan);
            m_woman.setOnClickListener(m_btnListener);
            m_woman.setLayoutParams(ll);
            m_sexGroup.addView(m_woman);
        }
    }

    protected void InitEditTopBar(String title, boolean hasCompleteBtn) {
        m_editTopBar = new FrameLayout(getContext());
        m_editTopBar.setBackgroundColor(0xf4ffffff);
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, m_editTopBarHeight);
        fl.gravity = Gravity.TOP;
        m_editTopBar.setLayoutParams(fl);
        m_editFr.addView(m_editTopBar);
        {
            m_editBack = new ImageView(getContext());
            m_editBack.setImageResource(R.drawable.userinfo_back_btn);
            fl = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
            m_editBack.setLayoutParams(fl);
            m_editTopBar.addView(m_editBack);
            m_editBack.setOnClickListener(m_btnListener);
            ImageUtils.AddSkin(getContext(), m_editBack, 0xffe75988);


            m_editTitle = new TextView(getContext());
            m_editTitle.setText(title);
            m_editTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            m_editTitle.setTextColor(0xff333333);
            fl = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            m_editTitle.setLayoutParams(fl);
            m_editTopBar.addView(m_editTitle);

            if (hasCompleteBtn) {
                m_editComplete = new TextView(getContext());
                m_editComplete.setText(getContext().getResources().getString(R.string.userinfopage_finish));
                m_editComplete.setTextSize(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE);
                m_editComplete.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
                fl = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                fl.rightMargin = ShareData.PxToDpi_xhdpi(36);
                m_editComplete.setLayoutParams(fl);
                m_editTopBar.addView(m_editComplete);
                m_editComplete.setOnClickListener(m_btnListener);
            }
        }
    }

    public void showSoftInput(View v) {
        InputMethodManager manager = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.toggleSoftInputFromWindow(v.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void hideSoftInput(View v) {
        InputMethodManager manager = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void setDatas(String id) {
        m_localInfo = UserMgr.ReadCache(getContext());
        if(m_localInfo != null) m_netInfo = (UserInfo)m_localInfo.clone();
        UpdateDataToUI(m_localInfo, true);
        m_userId = id;
//		isCanBack = false;

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message msg = m_threadHandler.obtainMessage();
                msg.what = CHECK;
                msg.obj = m_userId;
                m_threadHandler.sendMessage(msg);
            }
        }, 350);
    }

    public void UpdateDataToUI(UserInfo userInfo, boolean headUpdate) {
        if (userInfo != null) {
            if (headUpdate) {
                updateSetTopFr(userInfo.mUserIcon);
            }
            m_nickname.setInfo(userInfo.mNickname);
            String mobile = getContext().getResources().getString(R.string.userinfopage_nobind);
            m_phone.showArrow(true);
            if (userInfo.mMobile != null && userInfo.mMobile.length() > 0 && !userInfo.mMobile.equals("0")) {
                m_phone.showArrow(false);
                mobile = userInfo.mMobile;
                m_password.setVisibility(View.VISIBLE);
            }
            m_phone.setInfo(mobile);
//			m_credits.setInfo(userInfo.mFreeCredit + "");
            String sex = getContext().getResources().getString(R.string.userinfopage_nowrite);
            if (userInfo.mSex != null && userInfo.mSex.length() > 0) {
                if (userInfo.mSex.equals("男")) {
                    sex = getContext().getResources().getString(R.string.userinfopage_man);
                } else if (userInfo.mSex.equals("女")) {
                    sex = getContext().getResources().getString(R.string.userinfopage_women);
                }
            }
            m_sex.setInfo(sex);
            String date = getContext().getResources().getString(R.string.userinfopage_nowrite);
            if (userInfo.mBirthdayYear != null && userInfo.mBirthdayYear.length() > 0) {
                date = userInfo.mBirthdayYear + "-" + userInfo.mBirthdayMonth + "-" + userInfo.mBirthdayDay;
            }
            m_birth.setInfo(date);
            if (m_allAreaInfos == null) {
                m_allAreaInfos = AreaList.GetLocationLists(getContext());
            }
            String name = getContext().getResources().getString(R.string.userinfopage_nowrite);
            String temp = null;
            if (userInfo.mLocationId != null && userInfo.mLocationId.length() > 0 && !userInfo.mLocationId.equals("null")) {
                try {
                    temp = AreaList.GetLocationStr(m_allAreaInfos, Long.valueOf(userInfo.mLocationId), " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (temp != null && temp.length() > 0) {
                name = temp;
            }
            m_area.setInfo(name);
        }
    }


    private String m_tempBGPath = null;
    private boolean m_isSaveBG = false;

    private void updateSetTopFr(final String hearUrl) {
        if (hearUrl != null && hearUrl.length() > 0) {
            ImageLoaderUtil.getBitmapByUrl(getContext(), hearUrl, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void loadImageSuccessfully(Object object) {
                    if (object != null) {
                        Bitmap temp = (Bitmap) object;
                        m_tempBGPath = FileCacheMgr.GetLinePath();

                        if (Utils.SaveTempImg(temp, m_tempBGPath)) {
                            m_isSaveBG = true;
                        }
//						m_bg = LoginOtherUtil.MakeBkBmp2(temp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x7fffffff);
                        final Bitmap topFr_bg = LoginOtherUtil.MakeBkBmp2(temp, ShareData.m_screenWidth, m_topBarHeight, 0x33000000);
                        final Bitmap headIcon = ImageUtils.MakeHeadBmp(temp, ShareData.PxToDpi_xhdpi(166), 4, 0xffffffff);
                        if (temp != null) {
                            temp = null;
                        }
                        UserInfoPage.this.post(new Runnable() {
                            @Override
                            public void run() {
                                if (topFr_bg != null && !topFr_bg.isRecycled() && m_topFr != null) {
                                    m_topFr.setBackgroundDrawable(new BitmapDrawable(topFr_bg));
                                }
                                if (headIcon != null && !headIcon.isRecycled() && m_headImg != null) {
                                    m_headImg.setImageBitmap(headIcon);
                                }
                            }
                        });
                    }
                }

                @Override
                public void failToLoadImage() {

                }
            });
        }

////				FutureTarget<File> future = Glide.with(getContext())
////						.load(hearUrl)
////						.downloadOnly(FutureTarget.SIZE_ORIGINAL, FutureTarget.SIZE_ORIGINAL);
////				try {
////					File cacheFile = future.get();
////					String path = cacheFile.getAbsolutePath();
////					if(m_bg != null)
////					{
////						m_bg.recycle();
////						m_bg = null;
////					}
////
////					Bitmap temp = BitmapFactory.decodeFile(path);
////					if(temp != null)
////					{
////						m_bg = LoginOtherUtil.MakeBkBmp2(temp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x33000000);
////						final Bitmap headIcon = ImageUtils.MakeHeadBmp(temp, ShareData.PxToDpi_xhdpi(166), 4, 0xffffffff);
////
////						UserInfoPage.this.post(new Runnable() {
////						  @Override
////						  public void run() {
////							  if(m_bg != null && !m_bg.isRecycled())
////							  {
////								  m_topFr.setBackgroundDrawable(new BitmapDrawable(m_bg));
////							  }
////							  if(headIcon != null && !headIcon.isRecycled())
////							  {
////								  m_headImg.setImageBitmap(headIcon);
////							  }
////						  }
////					  });
////					}
////
////				} catch (InterruptedException e) {
////					e.printStackTrace();
////				} catch (ExecutionException e) {
////					e.printStackTrace();
////				}
//
//			}
//		}).start();

    }

    public static Bitmap MakeBkBmp(Bitmap bmp, int outW, int outH, int fillColor) {
        Bitmap out;

        out = MakeBmp.CreateBitmap(bmp, outW / 2, outH / 2, (float) outW / (float) outH, 0, Config.ARGB_8888);
        //out = MakeBmp.CreateFixBitmap(bmp, outW, outH, MakeBmp.POS_CENTER, 0, Config.ARGB_8888);
        //filter.largeRblurOpacity(out, 100, 0);
        filter.fakeGlassBeauty(out, 0x19000000);
        Canvas canvas = new Canvas(out);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(fillColor);

        return out;
    }

    public void addShowAnim(final View view, boolean isShow) {
        if (view != null) {
            int startX = ShareData.m_screenWidth;
            int endX = 0;
            if (!isShow) {
                startX = 0;
                endX = ShareData.m_screenWidth;
            }
            TranslateAnimation translateAnimation = new TranslateAnimation(startX, endX, 0, 0);
            translateAnimation.setDuration(300);
            translateAnimation.start();
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (view != null) {
                        view.clearAnimation();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(translateAnimation);
//			translateAnimation.startNow();
        }
    }


    protected void DoRightAnim(View v, boolean show) {
        if (v == null)
            return;
        v.clearAnimation();
        int start;
        int end;
        if (show) {
            start = -1;
            end = 0;
            v.setVisibility(View.VISIBLE);
        } else {
            start = 0;
            end = 1;
            v.setVisibility(View.GONE);
        }
        AnimationSet as = new AnimationSet(true);
        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        ta.setDuration(350);
        as.addAnimation(ta);
        as.setAnimationListener(m_animSetListener);
        v.startAnimation(as);
    }


    protected void DoLeftAnim(View v, boolean show) {
        if (v == null)
            return;
        int start;
        int end;
        if (show) {
            start = 1;
            end = 0;
            v.setVisibility(View.VISIBLE);
        } else {
            start = 0;
            end = -1;
            v.setVisibility(View.GONE);
        }
        AnimationSet as = new AnimationSet(true);
        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        ta.setDuration(350);
        as.addAnimation(ta);
        as.setAnimationListener(m_animSetListener);
        v.startAnimation(as);
    }


    protected void DoLeftAnim1(View v, boolean show) {
        if (v == null)
            return;
        float start;
        float end;
        if (show) {
            start = ShareData.m_screenWidth;
            end = 0;
        } else {
            start = 0;
            end = -ShareData.m_screenWidth;
        }
        AnimationSet as = new AnimationSet(true);
//		TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//		ta.setDuration(350);
//		as.addAnimation(ta);
        ObjectAnimator ta = ObjectAnimator.ofFloat(v, "translationX", start, end);
        ta.setDuration(350);
        ta.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ta.start();

//		as.setAnimationListener(m_animSetListener);
//		v.startAnimation(as);
    }

    protected void DoRightAnim1(View v, boolean show) {
        if (v == null)
            return;
        v.clearAnimation();
        int start;
        int end;
        if (show) {
            start = -ShareData.m_screenWidth;
            end = 0;
        } else {
            start = 0;
            end = ShareData.m_screenWidth;
        }
//		AnimationSet as = new AnimationSet(true);
//		TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//		ta.setDuration(350);
//		as.addAnimation(ta);
//		as.setAnimationListener(m_animSetListener);
//		v.startAnimation(as);

        ObjectAnimator ta = ObjectAnimator.ofFloat(v, "translationX", start, end);
        ta.setDuration(350);
        ta.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ta.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//				FrameLayout.LayoutParams fl = (LayoutParams) m_editFr.getLayoutParams();
//				Log.i("bbbb","value = " + fl.leftMargin);
//				Log.i("bbbb","value = " + fl.rightMargin);
//				Log.i("bbbb","value = " + m_editFr.getTranslationX());
            }
        });
        ta.start();
    }


    protected void DoUpAnim1(View v, boolean show) {
        if (v == null)
            return;
        v.clearAnimation();
        float start;
        float end;
        if (show) {
            start = ShareData.PxToDpi_xhdpi(400);
            end = 0f;
        } else {
            start = 0f;
            end = -ShareData.PxToDpi_xhdpi(400);
        }
//		AnimationSet as = new AnimationSet(true);
//		TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, start, Animation.RELATIVE_TO_SELF, end, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//		ta.setDuration(350);
//		as.addAnimation(ta);
//		as.setAnimationListener(m_animSetListener);
//		v.startAnimation(as);

        ObjectAnimator ta = ObjectAnimator.ofFloat(v, "translationY", start, end);
        ta.setDuration(350);
        ta.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ta.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//				FrameLayout.LayoutParams fl = (LayoutParams) m_editFr.getLayoutParams();
//				Log.i("bbbb","value = " + fl.leftMargin);
//				Log.i("bbbb","value = " + fl.rightMargin);
//				Log.i("bbbb","value = " + m_editFr.getTranslationX());
            }
        });
        ta.start();
    }

    AnimationSet.AnimationListener m_animSetListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public void onBack() {
        if (m_uiEnabled && onBackBtn(true) && isCanBack) {
            if (m_netInfo != null) {
                if (m_netInfo.mProtocolCode == 200 && m_netInfo.mCode == 0) {
                    UserMgr.SaveCache(m_netInfo);
                }
            }
            isClose = true;
            mSite.onBack(getContext());
        }
    }

    private Bitmap m_mainBG;
    private Bitmap m_editBG;

    private void SetBK(View view, boolean isMain) {
        if (view != null) {
            if (isMain) {
                if (m_mainBG == null) {
                    m_mainBG = getBG(isMain);
                    view.setBackgroundDrawable(new BitmapDrawable(m_mainBG));
                } else {
                    view.setBackgroundDrawable(new BitmapDrawable(m_mainBG));
                }
            } else {
                if (m_editBG == null) {
                    m_editBG = getBG(isMain);
                    view.setBackgroundDrawable(new BitmapDrawable(m_editBG));
                } else {
                    view.setBackgroundDrawable(new BitmapDrawable(m_editBG));
                }
            }
        }
    }

    private Bitmap getBG(boolean isMain) {
        Bitmap out = null;
        if (isMain) {
            if (Home4Page.s_maskBmpPath != null && Home4Page.s_maskBmpPath.length() > 0) {
                Bitmap temp = cn.poco.imagecore.Utils.DecodeFile((String) Home4Page.s_maskBmpPath, null);
                if (temp != null) {
                    temp = LoginOtherUtil.MakeBkBmp2(temp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x99ffffff);
                    out = temp;
                }
            }
        } else {
            Bitmap temp = CommonUtils.GetScreenBmp((Activity) getContext(), this.getWidth(), this.getHeight());
            if (temp != null) {
                temp = LoginOtherUtil.MakeBkBmp2(temp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x99ffffff);
                out = temp;
            }
        }
        return out;
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }


    @Override
    public void onClose() {
//        m_thread.quit();
        if (m_bg != null) {
            m_topFr.setBackgroundColor(0xffffffff);
            m_editFr.setBackgroundColor(0xffffffff);
            m_bg.recycle();
            m_bg = null;
        }

        isClose = true;

        if (m_dlg != null) {
            m_dlg.dismiss();
            m_dlg = null;
        }

        if (tipsDialog != null) {
            tipsDialog.dissmissDialog();
            tipsDialog = null;
        }
        EventCenter.removeListener(mOnEventListener);

        MyBeautyStat.onPageEndByRes(R.string.个人中心_登录注册_个人资料页);
        TongJiUtils.onPageEnd(getContext(), R.string.个人主页);
    }

    @Override
    public void onResume() {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.个人主页);
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.个人主页);
    }

    public static int ISBACK = 10001;
    public static int FINISH = 10002;

    //返回和上传图片成功返回操作不同
    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        if (siteID == SiteID.REGISTER_HEAD) {
            if (params != null) {
                int m_opera = (int) params.get("m_opera");
                if (m_opera == ISBACK) {
                    int new_mode = (int) params.get("m_mode");
                    showEditView(m_mode, new_mode);
                    m_mode = new_mode;
                } else if (m_opera == FINISH) {
                    String id = (String) params.get("id");
                    String headUrl = (String) params.get("headUrl");
                    if (id != null && headUrl != null) {
                        CommunityPage.isUserInfoModified = true;
                        showEditView(m_mode, NONE);
                        m_mode = NONE;
                        EventCenter.sendEvent(EventID.UPDATE_USER_INFO);
                        if (headUrl != null) {
                            if (m_netInfo != null) {
                                m_netInfo.mUserIcon = headUrl;
                                UserMgr.SaveCache(m_netInfo);
                                UpdateDataToUI(m_netInfo, true);
                            }
                        }
                        showEditView(m_mode, NONE);

                        m_mode = NONE;
                    }
                }
            }
        }
    }
}

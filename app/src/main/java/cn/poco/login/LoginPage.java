package cn.poco.login;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.beautyaccount.RequestParam;

import java.util.HashMap;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.featuremenu.manager.AppFeatureManager;
import cn.poco.featuremenu.model.FeatureType;
import cn.poco.featuremenu.model.OtherFeature;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.login.site.LoginPageSite;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.share.ImageButton;
import cn.poco.share.SharePage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 登录页面
 */
public class LoginPage extends IPage {
    //private static final String TAG = "登陆";

    public static final int REQUEST_CODE = 0x7070;
    private int topBarHeight = ShareData.PxToDpi_xhdpi(74);
    private Bitmap bgBmp;

    private ImageView m_cancelBtn;
    private LinearLayout centerLoginCon;

    //国家国际区域码
    private String mCountry = getContext().getResources().getString(R.string.loginpage_country);

    //输入手机号
    private RelativeLayout rlCenterTelLin;
    private RelativeLayout rlout1;
    private ImageView centerTelIcon;
    private TextView mAreaCode;
    private ImageView mTocountryAreaCodeIcon;
    private EditTextWithDel centerTelInput;
    private ImageView centerTelBottomLine;
    //输入密码
    private RelativeLayout rlCenterPswLin;
    private RelativeLayout rlout2;
    private ImageView centerPswIcon;
    private EditTextWithDel centerPswInput;
    private ImageView centerPswBottomLine;

    private FrameLayout m_tipsFr;
    private ImageView m_tipsIcon;
    private TextView m_tipText;

    //登录按钮
    private TextView centerOkLogin;

    //忘记密码,创建账号
    private LinearLayout centerTvCon;
    private TextView centerLosePsw;
    private TextView centerCreateAccount;

    //分割线
    private LinearLayout bottomLoginCon;
    private RelativeLayout bottomSeparationCon;
    private ImageView bottomSeparationLeft;
    private TextView bottomSeparationTv;
    private ImageView bottomSeparationRight;

    private RelativeLayout bottomLoginIconTable;
    private ImageView bottomLoginIconWeixin;
    private ImageView bottomLoginIconSina;
    private LinearLayout bottomLoginIconTable2;

    private ImageButton mPswShowIcon;
    private TextView mTitle;
    private ImageView loginBtn;

    private String mAreaCodeNum = "86";

    private onLoginLisener mOnLoginListener;

    private boolean isHidePsw = true;

    private LoginStyle mLoginStyle;

    public static String mWeiXinGetCode = null;
    public Handler mHandler;

    private FullScreenDlg dialog;
    private ProgressBar mLoginLoading;
    private ImageView m_LoginLoading2;

    private Bitmap bkBmp;
    private boolean m_uiEnabled = true;
    LoginPageSite mSite;

    private FrameLayout m_upFr;
    private FrameLayout m_downFr;
    private int m_upFrHeight;

    private FrameLayout m_upFr_1;

//    private ImageView m_okBg;

    private final String m_pswErrorTips = getContext().getResources().getString(R.string.loginpage_passwordtips);
    private final String m_pswErrorTips1 = getContext().getResources().getString(R.string.loginpage_passwordtips2);
    private final String m_pswErrorTips2 = getContext().getResources().getString(R.string.loginpage_passwordtips3);
    private final String m_pswErrorTips3 = getContext().getResources().getString(R.string.resgisterpage_pswtips);
//    private final String m_notUserTips = getContext().getResources().getString(R.string.loginpage_notfound);

    private boolean mNoExitAnim;

    private ProgressDialog mDialog;

    public LoginPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (LoginPageSite) site;
        initUI();

        MyBeautyStat.onPageStartByRes(R.string.个人中心_登录注册_登录页面);
        TongJiUtils.onPageStart(getContext(), R.string.登录);
    }

    /**
     * @param params phoneNum:String 手机号码
     *               img:String 页面背景图片
     *               noExitAnim:boolean true时没有退出动画
     */
    @Override
    public void SetData(HashMap<String, Object> params) {

        if (params != null) {
            try
            {
                if (params.get("phoneNum") != null) {
                    setPhoneNum((String) params.get("phoneNum"));
                }
//            if (params.get("img") != null) {
//                SetBackground(Utils.DecodeFile((String) params.get("img"), null));
//            } else {
//                SetBackground(null);
//            }
                if(params.get("noExitAnim") != null)
                {
                    mNoExitAnim = (boolean)params.get("noExitAnim");
                }
            }
            catch(Throwable e)
            {
                e.printStackTrace();
            }
        }
        LoginAllAnim.SetBK(LoginPage.this);
        if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
        {
            this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath,null)));
        }
        else
        {
            this.setBackgroundDrawable(new BitmapDrawable(LoginOtherUtil.getTempBK(getContext())));
        }

        showAnim(bottomLoginCon,ShareData.PxToDpi_xhdpi(316),0f,400);
    }

    @Override
    public void onDestroy() {
        LoginOtherUtil.dismissProgressDialog(mDialog);
        super.onDestroy();
    }

    private boolean m_onceDown;
    private boolean m_onceUp;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initUI() {
        m_upFrHeight = ShareData.PxToDpi_xhdpi(686);
        mLoginStyle = new LoginStyle(getContext(), this);
        mHandler = new Handler();
        dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
        SharePage.initBlogConfig(getContext());

        m_upFr = new FrameLayout(getContext());
        LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT,m_upFrHeight);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_upFr.setLayoutParams(fl);
        this.addView(m_upFr);
        m_upFr.setBackgroundColor(Color.WHITE);

        m_upFr_1 = new FrameLayout(getContext());
        fl = new LayoutParams(LayoutParams.MATCH_PARENT,m_upFrHeight);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_upFr_1.setLayoutParams(fl);
        m_upFr.addView(m_upFr_1);
//        m_upFr.setBackgroundColor(Color.WHITE);

        m_downFr = new FrameLayout(getContext())
        {
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                startAnim();
            }
        };
        fl = new LayoutParams(LayoutParams.MATCH_PARENT,ShareData.m_screenHeight - m_upFrHeight);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        fl.topMargin = m_upFrHeight;
        m_downFr.setLayoutParams(fl);
        this.addView(m_downFr);

//        LoginAllAnim.SetBK(LoginPage.this);


        RelativeLayout.LayoutParams rlParams;
        LinearLayout.LayoutParams llParams;
        LayoutParams fParams;


        // 返回到上一层按钮

        FrameLayout topBar = new FrameLayout(getContext());
        fl = new LayoutParams(LayoutParams.MATCH_PARENT,ShareData.PxToDpi_xhdpi(90));
        fl.gravity = Gravity.TOP;
        topBar.setLayoutParams(fl);
        m_upFr_1.addView(topBar);

        m_cancelBtn = new ImageView(getContext());
        m_cancelBtn.setImageResource(R.drawable.framework_back_btn);
        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
//        fl.leftMargin = ShareData.PxToDpi_xhdpi(16);
//        fl.topMargin = ShareData.PxToDpi_xhdpi(24);
        m_cancelBtn.setLayoutParams(fl);
        m_cancelBtn.setOnClickListener(mOnClickListener);
        m_cancelBtn.setOnTouchListener(mOnTouchListener);
        topBar.addView(m_cancelBtn);
        ImageUtils.AddSkin(getContext(),m_cancelBtn);

        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(169), ShareData.PxToDpi_xhdpi(154));
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl.topMargin = ShareData.PxToDpi_xhdpi(40);
        ImageView m_passlogo = new ImageView(getContext());
        m_passlogo.setImageResource(R.drawable.beauty_login_pass_logo);
        m_upFr_1.addView(m_passlogo, fl);
        ImageUtils.AddSkin(getContext(),m_passlogo);

        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl.topMargin = ShareData.PxToDpi_xhdpi(243);
        centerLoginCon = new LinearLayout(getContext());
        centerLoginCon.setGravity(Gravity.CENTER_HORIZONTAL);
        centerLoginCon.setOrientation(LinearLayout.VERTICAL);
        m_upFr_1.addView(centerLoginCon, fl);

        //手机号码
        llParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
        llParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        llParams.rightMargin = ShareData.PxToDpi_xhdpi(27);
        rlCenterTelLin = new RelativeLayout(getContext());
        centerLoginCon.addView(rlCenterTelLin, llParams);
        {
            rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(190), LayoutParams.MATCH_PARENT);
            rlout1 = new RelativeLayout(getContext());
            rlCenterTelLin.addView(rlout1, rlParams);
            rlout1.setId(R.id.login_loginpage_rlout1);
            {
                rlParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                centerTelIcon = new ImageView(getContext());
                centerTelIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                centerTelIcon.setImageResource(R.drawable.beauty_login_del_logo);
                rlout1.addView(centerTelIcon, rlParams);


                rlParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
                mTocountryAreaCodeIcon = new ImageView(getContext());
                mTocountryAreaCodeIcon.setLayoutParams(rlParams);
                rlout1.addView(mTocountryAreaCodeIcon);
                mTocountryAreaCodeIcon.setImageResource(R.drawable.beautify_login_changecode_icon);
                mTocountryAreaCodeIcon.setId(R.id.login_loginpage_rlout1_changecode_icon);
                mTocountryAreaCodeIcon.setOnClickListener(mOnClickListener);

                rlParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.LEFT_OF,R.id.login_loginpage_rlout1_changecode_icon);
                rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
                rlParams.rightMargin = ShareData.PxToDpi_xhdpi(4);
                mAreaCode = new TextView(getContext());
                rlout1.addView(mAreaCode, rlParams);
                mAreaCode.setText("+" + mAreaCodeNum);
                mAreaCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
                mAreaCode.setTextColor(0xff000000);
                mAreaCode.setOnClickListener(mOnClickListener);
            }

            rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            rlParams.leftMargin = ShareData.PxToDpi_xhdpi(34);
            rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.login_loginpage_rlout1);
            centerTelInput = new EditTextWithDel(getContext(), -1, R.drawable.beauty_login_delete_logo);
            centerTelInput.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            centerTelInput.setBackgroundColor(0x00000000);
            centerTelInput.setPadding(0, 0, ShareData.PxToDpi_xhdpi(5), 0);
            centerTelInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
            centerTelInput.setTextColor(0xff000000);
            centerTelInput.setHintTextColor(0xffb3b3b3);
            centerTelInput.setHint(getContext().getResources().getString(R.string.loginpage_phonenumhint));
            centerTelInput.setSingleLine();
            centerTelInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            centerTelInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            rlCenterTelLin.addView(centerTelInput, rlParams);
            centerTelInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            centerTelInput.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (v instanceof EditTextWithDel) {
                            EditTextWithDel editText = (EditTextWithDel) v;
                            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    } else {
                        if (v instanceof EditTextWithDel) {
                            EditTextWithDel editText = (EditTextWithDel) v;
                            editText.setDrawable();
                        }

                    }
                }
            });
            centerTelInput.addTextChangedListener(textWatcher);
            rlCenterTelLin.setFocusable(true);
            rlCenterTelLin.setFocusableInTouchMode(true);

            rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            centerTelBottomLine = new ImageView(getContext());
            centerTelBottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
            centerTelBottomLine.setImageResource(R.drawable.beauty_login_line);
            centerLoginCon.addView(centerTelBottomLine, rlParams);
        }

        //输入密码框
        llParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
        llParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        llParams.rightMargin = ShareData.PxToDpi_xhdpi(27);
        rlCenterPswLin = new RelativeLayout(getContext());
        centerLoginCon.addView(rlCenterPswLin, llParams);
        {
            rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(190), LayoutParams.MATCH_PARENT);
            rlout2 = new RelativeLayout(getContext());
            rlCenterPswLin.addView(rlout2, rlParams);
            rlout2.setId(R.id.login_loginpage_rlout2);
            {
                rlParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                centerPswIcon = new ImageView(getContext());
                centerPswIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                centerPswIcon.setImageResource(R.drawable.beauty_login_comfir_psw);
                rlout2.addView(centerPswIcon, rlParams);
            }

            rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            rlParams.leftMargin = ShareData.PxToDpi_xhdpi(34);
            rlParams.rightMargin = ShareData.PxToDpi_xhdpi(50);
            rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.login_loginpage_rlout2);
            centerPswInput = new EditTextWithDel(getContext(), -1, R.drawable.beauty_login_delete_logo);
            centerPswInput.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            centerPswInput.setBackgroundColor(0x00000000);
            centerPswInput.setPadding(0, 0, ShareData.PxToDpi_xhdpi(5), 0);
            centerPswInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
            centerPswInput.setTextColor(0xff000000);
            centerPswInput.setHintTextColor(0xffb2b2b2);
            centerPswInput.setHint(getContext().getResources().getString(R.string.loginpage_passwordhint));
            centerPswInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
            centerPswInput.setSingleLine();
            centerPswInput.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            centerPswInput.setKeyListener(new NumberKeyListener() {
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
            centerPswInput.addTextChangedListener(textWatcher);
            centerPswInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            rlCenterPswLin.addView(centerPswInput, rlParams);
            centerPswInput.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (v instanceof EditTextWithDel) {
                            EditTextWithDel editText = (EditTextWithDel) v;
                            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    } else {
                        if (v instanceof EditTextWithDel) {
                            EditTextWithDel editText = (EditTextWithDel) v;
                            editText.setDrawable();
                        }
                    }
                }
            });

            mPswShowIcon = new ImageButton(getContext());
            rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(50), ShareData.PxToDpi_xhdpi(50));
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rlParams.rightMargin = ShareData.PxToDpi_xhdpi(5);
            mPswShowIcon.setButtonImage(R.drawable.userinfo_psw_hide_out, R.drawable.userinfo_psw_hide_over);
            rlCenterPswLin.addView(mPswShowIcon, rlParams);
            mPswShowIcon.setOnClickListener(mOnClickListener);

            rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            centerPswBottomLine = new ImageView(getContext());
            centerPswBottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
            centerPswBottomLine.setImageResource(R.drawable.beauty_login_line);
            centerLoginCon.addView(centerPswBottomLine, rlParams);
        }
        rlCenterPswLin.setFocusable(true);
        rlCenterPswLin.setFocusableInTouchMode(true);

        m_tipsFr = new FrameLayout(getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.LEFT;
        ll.topMargin = ShareData.PxToDpi_xhdpi(14);
        ll.leftMargin = ShareData.PxToDpi_xhdpi(35);
        m_tipsFr.setLayoutParams(ll);
        m_tipsFr.setVisibility(INVISIBLE);
        centerLoginCon.addView(m_tipsFr);
        {
            m_tipsIcon = new ImageView(getContext());
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
            m_tipsIcon.setLayoutParams(fl);
            m_tipsFr.addView(m_tipsIcon);
            m_tipsIcon.setImageResource(R.drawable.beauify_login_tips_icon);

            m_tipText = new TextView(getContext());
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(35);
            m_tipText.setLayoutParams(fl);
            m_tipText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12f);
            m_tipText.setTextColor(0xffff4b4b);
            m_tipText.setGravity(Gravity.CENTER);
            m_tipsFr.addView(m_tipText);
        }

        FrameLayout tempLoginBtn = new FrameLayout(getContext());
        llParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(541), ShareData.PxToDpi_xhdpi(80));
        llParams.topMargin = ShareData.PxToDpi_xhdpi(28);
        centerLoginCon.addView(tempLoginBtn, llParams);

        loginBtn = new ImageView(getContext());
        loginBtn.setEnabled(false);
        fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        loginBtn.setLayoutParams(fl);
        tempLoginBtn.addView(loginBtn);
        loginBtn.setOnClickListener(mOnClickListener);
        loginBtn.setImageDrawable(LoginOtherUtil.makeDrawableForSkin(R.drawable.beauty_login_btn_disable_new,R.drawable.beauty_login_btn_normal1,R.drawable.beauty_login_btn_press1,getContext()));

        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        centerOkLogin = new TextView(getContext());
        centerOkLogin.setGravity(Gravity.CENTER);
        centerOkLogin.setText(getContext().getResources().getString(R.string.loginpage_login));
        centerOkLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        centerOkLogin.setTextColor(0xffffffff);
        tempLoginBtn.addView(centerOkLogin, fl);

        m_LoginLoading2 = new ImageView(getContext());
        m_LoginLoading2.setScaleType(ImageView.ScaleType.CENTER);
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(50), ShareData.PxToDpi_xhdpi(50));
        fl.gravity = Gravity.CENTER;
        m_LoginLoading2.setImageResource(R.drawable.beauty_login_loading_logo);
        m_LoginLoading2.setVisibility(GONE);
        tempLoginBtn.addView(m_LoginLoading2, fl);

        llParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        llParams.gravity = Gravity.CENTER_HORIZONTAL;
        llParams.topMargin = ShareData.PxToDpi_xhdpi(20);
        centerTvCon = new LinearLayout(getContext());
        centerLoginCon.addView(centerTvCon, llParams);

        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        centerLosePsw = new TextView(getContext());
        centerLosePsw.setGravity(Gravity.CENTER);
        centerLosePsw.setTextColor(0xff6a6a6a);
        centerLosePsw.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        centerLosePsw.setText(getContext().getResources().getString(R.string.loginpage_forgetpassword));
        centerLosePsw.setPadding(ShareData.PxToDpi_xhdpi(13),ShareData.PxToDpi_xhdpi(10),ShareData.PxToDpi_xhdpi(13),ShareData.PxToDpi_xhdpi(10));
        centerLosePsw.setOnClickListener(mOnClickListener);
        centerLosePsw.setOnTouchListener(mOnTouchListener);
        centerTvCon.addView(centerLosePsw, llParams);

        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llParams.leftMargin = ShareData.PxToDpi_xhdpi(276);
        centerCreateAccount = new TextView(getContext());
        centerCreateAccount.setGravity(Gravity.CENTER);
        centerCreateAccount.setTextColor(0xff6a6a6a);
        centerCreateAccount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        centerCreateAccount.setText(getContext().getResources().getString(R.string.loginpage_createAccount));
        centerCreateAccount.setPadding(ShareData.PxToDpi_xhdpi(13),ShareData.PxToDpi_xhdpi(10),ShareData.PxToDpi_xhdpi(13),ShareData.PxToDpi_xhdpi(10));
        centerCreateAccount.setOnClickListener(mOnClickListener);
        centerCreateAccount.setOnTouchListener(mOnTouchListener);
        centerTvCon.addView(centerCreateAccount, llParams);
//
        fl = new LayoutParams(ShareData.m_screenWidth - 2 * ShareData.PxToDpi_xhdpi(55) + 1, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        fl.bottomMargin = ShareData.PxToDpi_xhdpi(63);
        bottomLoginCon = new LinearLayout(getContext());
        bottomLoginCon.setOrientation(LinearLayout.VERTICAL);
        m_downFr.addView(bottomLoginCon, fl);
        llParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        bottomSeparationCon = new RelativeLayout(getContext());
        bottomLoginCon.addView(bottomSeparationCon, llParams);

        rlParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rlParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        bottomSeparationTv = new TextView(getContext());
        bottomSeparationTv.setPadding(ShareData.PxToDpi_xhdpi(20), 0, ShareData.PxToDpi_xhdpi(20), 0);
        bottomSeparationTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        bottomSeparationTv.setTextColor(0xffffffff);
        bottomSeparationTv.setAlpha(0.8f);
        bottomSeparationTv.setText("or");
        bottomSeparationTv.setId(R.id.login_loginpage_bottomseparationtv);
        bottomSeparationCon.addView(bottomSeparationTv, rlParams);

        rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rlParams.addRule(RelativeLayout.LEFT_OF, R.id.login_loginpage_bottomseparationtv);
        bottomSeparationLeft = new ImageView(getContext());
        bottomSeparationLeft.setScaleType(ImageView.ScaleType.FIT_XY);
        bottomSeparationLeft.setBackgroundColor(0xffffffff);
        bottomSeparationLeft.setAlpha(0.3f);
        bottomSeparationCon.addView(bottomSeparationLeft, rlParams);

        rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.login_loginpage_bottomseparationtv);
        bottomSeparationRight = new ImageView(getContext());
        bottomSeparationRight.setScaleType(ImageView.ScaleType.FIT_XY);
        bottomSeparationRight.setBackgroundColor(0xffffffff);
        bottomSeparationRight.setAlpha(0.3f);
        bottomSeparationCon.addView(bottomSeparationRight, rlParams);

        llParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(116));
        llParams.gravity = Gravity.CENTER_HORIZONTAL;
        llParams.topMargin = ShareData.PxToDpi_xhdpi(61);

        bottomLoginIconTable2 = new LinearLayout(getContext());
        bottomLoginIconTable2.setOrientation(LinearLayout.HORIZONTAL);
        bottomLoginIconTable2.setFocusable(false);
        bottomLoginCon.addView(bottomLoginIconTable2, llParams);

        //是否要隐藏sina或微信登陆按钮
        boolean sina_hide = false;
        boolean weixin_hide = false;
        List<OtherFeature> temps = AppFeatureManager.getInstance().getOtherFeature();
        if(temps != null && temps.size() > 0)
        {
            for(int i = 0 ; i < temps.size(); i++)
            {
                OtherFeature feature = temps.get(i);
                if(feature != null)
                {
                    if(feature.getFeature() == FeatureType.SINA_LOGIN)
                    {
                        if(!feature.isUnlock().equals("yes"))
                        {
                            sina_hide = true;
                        }
                    }

                    if(feature.getFeature() == FeatureType.WECHAT_LOGIN)
                    {
                        if(!feature.isUnlock().equals("yes"))
                        {
                            weixin_hide = true;
                        }
                    }
                }
            }
        }
        if(!weixin_hide)
        {
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(116), ShareData.PxToDpi_xhdpi(116));
             bottomLoginIconWeixin = new ImageView(getContext());
        bottomLoginIconWeixin.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        bottomLoginIconWeixin.setImageResource(R.drawable.beauty_login_weixin);
        bottomLoginIconWeixin.setOnClickListener(mOnClickListener);
        bottomLoginIconWeixin.setOnTouchListener(mOnTouchListener);
        bottomLoginIconTable2.addView(bottomLoginIconWeixin, ll);
        }

        if(!sina_hide)
        {
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(116), ShareData.PxToDpi_xhdpi(116));
            if(!weixin_hide)
            {
                ll.leftMargin = ShareData.PxToDpi_xhdpi(78);
            }

            bottomLoginIconSina = new ImageView(getContext());
        bottomLoginIconSina.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        bottomLoginIconSina.setImageResource(R.drawable.beauty_login_weibo);
        bottomLoginIconSina.setOnClickListener(mOnClickListener);
        bottomLoginIconSina.setOnTouchListener(mOnTouchListener);
        bottomLoginIconTable2.addView(bottomLoginIconSina, ll);
        }

        LoginPage.this.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(loginBtn != null)
            {
                if(centerTelInput != null && centerTelInput.getText() != null && centerTelInput.getText().toString().length() > 0 && centerPswInput != null && centerPswInput.getText() != null && centerPswInput.getText().toString().length() > 0)
                {
                    loginBtn.setEnabled(true);
                }
                else
                {
                    loginBtn.setEnabled(false);
                }
            }


            if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
            {
                if(centerPswInput != null && centerPswInput.getText().toString().length() == 0 && (m_tipText.getText().toString().equals(m_pswErrorTips) || m_tipText.getText().toString().equals(m_pswErrorTips1) || m_tipText.getText().toString().equals(m_pswErrorTips2) || m_tipText.getText().toString().equals(m_pswErrorTips3)))
                {
                  m_tipsFr.setVisibility(INVISIBLE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(loginBtn != null)
            {
                if(centerTelInput != null && centerTelInput.getText() != null && centerTelInput.getText().toString().length() > 0 && centerPswInput != null && centerPswInput.getText() != null && centerPswInput.getText().toString().length() > 0)
                {
                    loginBtn.setEnabled(true);
                }
                else
                {
                    loginBtn.setEnabled(false);
                }
            }
        }
    };

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v == m_cancelBtn) {
                    m_cancelBtn.setAlpha(0.5f);
                }  else if (v == centerLosePsw) {
                    centerLosePsw.setAlpha(0.5f);
                } else if (v == centerCreateAccount) {
                    centerCreateAccount.setAlpha(0.5f);
                } else if (v == bottomLoginIconWeixin) {
                    bottomLoginIconWeixin.setAlpha(0.5f);
                } else if (v == bottomLoginIconSina) {
                    bottomLoginIconSina.setAlpha(0.5f);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v == m_cancelBtn) {
                    m_cancelBtn.setAlpha(1.0f);
                } else if (v == centerLosePsw) {
                    centerLosePsw.setAlpha(1.0f);
                } else if (v == centerCreateAccount) {
                    centerCreateAccount.setAlpha(1.0f);
                } else if (v == bottomLoginIconWeixin) {
                    bottomLoginIconWeixin.setAlpha(1.0f);
                } else if (v == bottomLoginIconSina) {
                    bottomLoginIconSina.setAlpha(1.0f);
                }
            }
            return false;
        }
    };


    public void setPhoneNum(String phoneNum) {
        centerTelInput.setText(phoneNum);
    }

    private NoDoubleClickListener mOnClickListener;
    {
        mOnClickListener = new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (v == m_cancelBtn) {
                    MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_登录页面_返回);
                    hideKeyboard();
                    mLoginStyle.back();
                    if(mNoExitAnim)
                    {
                        LoginAllAnim.ReSetLoginAnimData();
                        mSite.onBack(getContext());
                    }
                    else
                    {
                        LoginAllAnim.loginpageResetAnim(m_upFr);
                        showAnim(bottomLoginCon,0,ShareData.PxToDpi_xhdpi(68+116+62+50),300);
                        LoginPage.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LoginAllAnim.ReSetLoginAnimData();
                                mSite.onBack(getContext());
                            }
                        },300);
                    }
                }
                if (m_uiEnabled) {
                     if (v == mPswShowIcon) {
                        if (isHidePsw) {
                            MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_登录页面_密码明文);
                            centerPswInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            centerPswInput.setSelection(centerPswInput.length());
                            isHidePsw = false;
                            mPswShowIcon.setButtonImage(R.drawable.userinfo_psw_show_out, R.drawable.userinfo_psw_show_over);
                        } else {
                            MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_登录页面_密码加密);
                            centerPswInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            centerPswInput.setSelection(centerPswInput.length());
                            isHidePsw = true;
                            mPswShowIcon.setButtonImage(R.drawable.userinfo_psw_hide_out, R.drawable.userinfo_psw_hide_over);
                        }
                    }
                    else if(v == mTocountryAreaCodeIcon || v == mAreaCode)
                    {
                        hideKeyboard();
                        mSite.ChooseCountry(getContext());
                    }
                    else if (v == loginBtn) {
                         MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_登录页面_登录按钮);
                         m_tipsFr.setVisibility(INVISIBLE);
                         hideKeyboard();
                         String pwd = centerPswInput.getText().toString();
                         switch(RequestParam.isRightPassword(pwd))
                         {
                             case -1:
                                 m_tipsFr.setVisibility(VISIBLE);
                                 m_tipText.setText(m_pswErrorTips);
                                 return;

                             case -2:
                                 m_tipsFr.setVisibility(VISIBLE);
                                 m_tipText.setText(m_pswErrorTips2);
                                 return;

                             case -3:
                                 m_tipsFr.setVisibility(VISIBLE);
                                 m_tipText.setText(m_pswErrorTips3);
                                 return;
                         }
                         if (!LoginOtherUtil.isNetConnected(getContext())) {
                             LoginOtherUtil.showToast(getContext().getResources().getString(R.string.loginpage_networderror));
                             return;
                         }
                         mLoginStyle.LoginByPhone(mAreaCodeNum, centerTelInput.getText().toString(), centerPswInput.getText().toString(),m_phoneLoginLisener,LoginPage.this) ;
                    } else if (v == centerLosePsw) {
                         MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_登录页面_忘记密码);
                         HashMap<String, Object> params = new HashMap<>();
                                 params.put("img", bkBmp);
                                 LoginPageInfo info = new LoginPageInfo();
                                 info.m_country = mCountry;
                                 info.m_areaCodeNum = mAreaCodeNum;
                                 params.put("info", info);
                                 mSite.LosePsw(params,getContext());
                    } else if (v == centerCreateAccount) {
                         MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_登录页面_创建账号);
                         HashMap<String, Object> params = new HashMap<>();
                                 params.put("img", bkBmp);
                                 LoginPageInfo info = new LoginPageInfo();
                                 info.m_country = mCountry;
                                 info.m_areaCodeNum = mAreaCodeNum;
                                 params.put("info", info);
                                 mSite.creatAccount(params,getContext());
                    } else if (v == bottomLoginIconWeixin) {
                         MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_微信登录);
                         centerTelInput.clearFocus();
                         centerPswInput.clearFocus();
                         if(!LoginOtherUtil.isNetConnected(getContext()))
                         {
                             Toast.makeText(getContext(),getContext().getResources().getString(R.string.loginpage_networderror),Toast.LENGTH_SHORT).show();
                             return;
                         }
//                         LoginOtherUtil.showProgressDialog(getContext().getResources().getString(R.string.loginpage_binding),getContext());
                         mLoginStyle.WeiXinLogin(LoginPage.this, new onLoginLisener() {
                             @Override
                             public void onLoginSuccess(LoginInfo info,LoginStyle.LoginBaseInfo baseInfo,boolean isShouldBindPhone) {
                                 if(!isShouldBindPhone)
                                 {
                                     Toast.makeText(getContext(),getContext().getResources().getString(R.string.loginpage_loginsuccess),Toast.LENGTH_SHORT).show();
                                     EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
                                     mSite.loginSuccess(getContext());

                                     Handler handler = new Handler();
                                     handler.postDelayed(new Runnable()
                                     {
                                         @Override
                                         public void run()
                                         {
                                             TongJi2.AddOnlineClickCount(null, getResources().getInteger(R.integer.行为事件_用微信登录) + "", getResources().getString(R.string.登录));
                                         }
                                     },200);
                                 }
                                 else
                                 {
                                     //第三方登陆绑定手机
                                     mSite.thirdPartLoginOneStepFinish(getContext(),info,baseInfo);
                                 }
                             }

                             @Override
                             public void onLoginFailed() {}

                             @Override
                             public void onCancel() {}

                             @Override
                             public void onActionLogin() {
                                 hideKeyboard();
                             }

                             @Override
                             public void showLoginErrorTips(String str) {}
                         });
                    } else {
                         if (v == bottomLoginIconSina) {
                             MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_登录页面_新浪登录);
                             centerTelInput.clearFocus();
                             centerPswInput.clearFocus();
                             if(!LoginOtherUtil.isNetConnected(getContext()))
                             {
                                 Toast.makeText(getContext(),getContext().getResources().getString(R.string.loginpage_networderror),Toast.LENGTH_SHORT).show();
                                 return;
                             }
//                             mDialog = LoginOtherUtil.showProgressDialog(mDialog, getContext().getResources().getString(R.string.loginpage_binding),getContext());
                             mLoginStyle.bindSina(new onLoginLisener() {
                                 @Override
                                public void onLoginSuccess(LoginInfo info,LoginStyle.LoginBaseInfo baseInfo,boolean isShouldBindPhone)
                                 {
                                    if(!isShouldBindPhone)
                                    {
                                        Toast.makeText(getContext(),getContext().getResources().getString(R.string.loginpage_loginsuccess),Toast.LENGTH_SHORT).show();
                                        EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
                                        mSite.loginSuccess(getContext());
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                TongJi2.AddOnlineClickCount(null, getResources().getInteger(R.integer.行为事件_用微博登录) + "", getResources().getString(R.string.登录));
                                            }
                                        },200);
                                    }
                                    else
                                    {
                                        //第三方登陆绑定手机
                                        mSite.thirdPartLoginOneStepFinish(getContext(),info,baseInfo);
                                    }
                                }

                                @Override
                                public void onLoginFailed() {
//                                    LoginOtherUtil.dismissProgressDialog(mDialog);
                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onActionLogin() {

                                }

                                @Override
                                public void showLoginErrorTips(String str) {

                                }
                            },LoginPage.this);
                         }
                     }
                }
            }
        };
    }

    protected void SetBackground(Bitmap bk) {
        if (bk != null) {
            bkBmp = bk;
            bkBmp = MakeBmpV2.CreateFixBitmapV2(bkBmp,0,MakeBmpV2.FLIP_NONE,0,ShareData.m_screenWidth,ShareData.m_screenHeight - m_upFrHeight, Bitmap.Config.ARGB_8888);
            m_downFr.setBackgroundDrawable(new BitmapDrawable(bkBmp));
        } else {
            m_downFr.setBackgroundColor(Color.WHITE);
        }
    }

    private void showAnim(View view,float startY,float endY,int time)
    {
        if(view != null)
        {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"translationY",startY,endY);
            objectAnimator.setDuration(time);
            objectAnimator.start();
        }
    }

    protected void startAnim()
    {
        if(!m_onceDown && m_downFr.getHeight() > 0)
        {
            m_onceDown = true;
            LoginAllAnim.loginPageAnim(m_upFr,m_downFr,m_upFr_1);
        }
    }

    @Override
    public void onBack() {
       mOnClickListener.onClick(m_cancelBtn);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
    }

    @Override
    public void onResume() {
        hideKeyboard();
        TongJiUtils.onPageResume(getContext(), R.string.登录);
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.登录);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLoginStyle.getSinaBlog() != null) {
            mLoginStyle.getSinaBlog().onActivityResult(requestCode, resultCode, data, REQUEST_CODE);
        }
        return false;
    }

    @Override
    public void onClose() {
        if (bgBmp != null && !bgBmp.isRecycled()) {
            bgBmp.recycle();
            bgBmp = null;
        }
        this.setBackgroundDrawable(null);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        mLoginStyle.CancelLogin();
        MyBeautyStat.onPageEndByRes(R.string.个人中心_登录注册_登录页面);
        TongJiUtils.onPageEnd(getContext(), R.string.登录);
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {

        super.onPageResult(siteID, params);
        if (siteID == SiteID.CHOOSE_COUNTRY) {
            if (params != null) {
                if ((LoginPageInfo) params.get("info") != null) {
                    final LoginPageInfo info = (LoginPageInfo) params.get("info");
                    if (info != null) {
                        mCountry = info.m_country;
                        mAreaCodeNum = info.m_areaCodeNum;
                        mAreaCode.setText("+" + mAreaCodeNum);
                    }
                }
            }

        }
    }

    private

    onLoginLisener m_phoneLoginLisener = new onLoginLisener() {

        @Override
        public void onLoginSuccess(LoginInfo info, LoginStyle.LoginBaseInfo baseInfo,boolean isShouldBindPhone) {
            hideKeyboard();
            if (m_LoginLoading2 != null) {
                m_LoginLoading2.setVisibility(GONE);
                m_LoginLoading2.setAnimation(null);
            }
            if (centerOkLogin != null) {
                centerOkLogin.setVisibility(VISIBLE);
            }
            m_uiEnabled = true;
            EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
            mSite.loginSuccess(getContext());
            LoginOtherUtil.showToast(getContext().getResources().getString(R.string.loginpage_loginsuccess));

            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    TongJi2.AddOnlineClickCount(null, getResources().getInteger(R.integer.行为事件_用美人账号登录) + "", getResources().getString(R.string.登录));
                }
            },200);
        }

        @Override
        public void onLoginFailed() {
            centerPswInput.setEnabled(true);
            centerTelInput.setEnabled(true);
            if (m_LoginLoading2 != null) {
                m_LoginLoading2.setVisibility(GONE);
                m_LoginLoading2.clearAnimation();
                m_LoginLoading2.setAnimation(null);
            }
            if (centerOkLogin != null) {
                centerOkLogin.setVisibility(VISIBLE);
            }
            m_uiEnabled = true;
        }

        @Override
        public void onCancel() {
            m_uiEnabled = true;
        }

        @Override
        public void onActionLogin() {
            centerPswInput.setEnabled(false);
            centerTelInput.setEnabled(false);
            centerTelInput.clearFocus();
            centerPswInput.clearFocus();
            m_uiEnabled = false;
            if (m_LoginLoading2 != null) {
                m_LoginLoading2.setVisibility(VISIBLE);
                final RotateAnimation animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(500);
                animation.setRepeatCount(-1);
                m_LoginLoading2.startAnimation(animation);
            }
            if (centerOkLogin != null) {
                centerOkLogin.setVisibility(GONE);
            }
        }

        @Override
        public void showLoginErrorTips(String str) {
            if(m_tipText != null)
            {
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(str);
            }
            if(centerPswInput != null)
            {
                centerPswInput.setEnabled(true);
            }
            if(centerTelInput != null)
            {
                centerTelInput.setEnabled(true);
            }

        }
    };
}

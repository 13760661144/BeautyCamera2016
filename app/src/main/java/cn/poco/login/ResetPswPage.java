package cn.poco.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adnonstop.beautyaccount.RequestParam;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.credits.Credit;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.home.home4.Home4Page;
import cn.poco.login.site.ResetPswPageSite;
import cn.poco.loginlibs.info.BaseInfo;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.share.ImageButton;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 忘记密码填写密码和绑定手机填写密码共用页面
 */
@SuppressWarnings("ALL")
public class ResetPswPage extends IPage {
    //private static final String TAG = "重置密码";

    public final static int STATE_RESETPSW = 1;
    public final static int STATE_BINDPHONE = 2;
    private LoginInfo loginInfo;
    private int topBarHeight = ShareData.PxToDpi_xhdpi(74);
    private Bitmap bgBmp;

    private RelativeLayout m_topTabFr;
    private ImageView m_cancelBtn;

    private LinearLayout centerLoginCon;
    // 输入手机号
    private RelativeLayout centerSetPswCon;
    private ImageView centerSetPswIcon;
    private EditTextWithDel centerSetPswInput;
    private ImageView centerSetPswBottomLine;
    // 输入密码
    private RelativeLayout centerCommitPswCon;
    private ImageView centerCommitPswIcon;
    private EditTextWithDel centerCommitPswInput;
    private ImageView centerCommitPswBottomLine;
    private ProgressDialog mProgressDialog = new ProgressDialog(getContext());
    private ImageButton mPswShowIcon;
    private Bitmap bkBmp;

    private TextView centerOkLogin;

    public String phoneNum, verityCode, pwd, zoneNum;

    private FullScreenDlg dialog;
    private int mState = STATE_RESETPSW;
    private boolean isHidePsw = true;
    private ResetPswPageSite mSite;

    protected FrameLayout m_upFr;
    protected FrameLayout m_downFr;
    protected int m_upFrHeight;
    protected boolean m_once;
    protected FrameLayout m_upFr_1;

    private FrameLayout m_tipsFr;
    private ImageView m_tipsIcon;
    private TextView m_tipText;
    private final String m_pswtips = getContext().getResources().getString(R.string.resetpage_pswtips);
    private final String m_pswtips2 = getContext().getResources().getString(R.string.loginpage_passwordtips3);
    private final String m_pswEmptytips = getContext().getResources().getString(R.string.resetpage_pswemptytips);
    private UserInfo m_userInfo;

    private LoginStyle.LoginBaseInfo m_reLoginInfo;//绑定手机之后重新登录用到
    public ResetPswPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (ResetPswPageSite) site;
        initilize();

        MyBeautyStat.onPageStartByRes(R.string.个人中心_登录注册_忘记密码_步骤二);
        TongJiUtils.onPageStart(getContext(), R.string.忘记密码);
    }

    /**
     * @param params
     * img:页面背景图片
     * mode:忘记密码或绑定手机设置密码
     * info:LoginPageInfo
     **/
    @Override
    public void SetData(HashMap<String, Object> params) {
        if(params != null)
        {
            if(params.get("mode") != null)
            {
                this.mState = (int)params.get("mode");
            }
            setState(mState);
            if (params.get("info") != null) {
                this.phoneNum = ((LoginPageInfo)params.get("info")).m_phoneNum;
                this.loginInfo = ((LoginPageInfo)params.get("info")).m_info;
                this.verityCode = ((LoginPageInfo)params.get("info")).m_verityCode;
                this.zoneNum = ((LoginPageInfo)params.get("info")).m_areaCodeNum;
            }

            if(params.get("relogininfo") != null)
            {
                m_reLoginInfo = (LoginStyle.LoginBaseInfo) params.get("relogininfo");
            }

            if(params.get("userInfo") != null)
            {
                m_userInfo = (UserInfo) params.get("userInfo");
            }
        }

        if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
        {
            this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath,null)));
        }
        else
        {
            this.setBackground(new BitmapDrawable(LoginOtherUtil.getTempBK(getContext())));
        }

    }

    @Override
    public void onBack() {
        mSite.onBack(getContext());
    }

    public void initilize() {

        m_upFrHeight = ShareData.PxToDpi_xhdpi(500);
        m_upFr = new FrameLayout(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,m_upFrHeight);
        fl.gravity = Gravity.TOP;
        m_upFr.setLayoutParams(fl);
        this.addView(m_upFr);
//        LoginAllAnim.SetBK(ResetPswPage.this);
        m_upFr.setBackgroundColor(Color.WHITE);

        m_upFr_1 = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,m_upFrHeight);
        fl.gravity = Gravity.TOP;
        m_upFr_1.setLayoutParams(fl);
        m_upFr.addView(m_upFr_1);

        m_downFr = new FrameLayout(getContext())
        {
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if(!m_once)
                {
                    m_once = true;
                    LoginAllAnim.reSetPSWPageAnim(m_upFr,m_downFr,m_upFr_1);
                }
            }
        };
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ShareData.m_screenHeight - m_upFrHeight);
        fl.gravity = Gravity.TOP;
        fl.topMargin = m_upFrHeight;
        m_downFr.setLayoutParams(fl);
        this.addView(m_downFr);

        dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
        RelativeLayout.LayoutParams rlParams;
        LinearLayout.LayoutParams llParams;

        // 返回到上一层按钮
        FrameLayout topBar = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ShareData.PxToDpi_xhdpi(90));
        fl.gravity = Gravity.TOP;
        topBar.setLayoutParams(fl);
        m_upFr_1.addView(topBar);
        m_cancelBtn = new ImageView(getContext());
        m_cancelBtn.setImageResource(R.drawable.framework_back_btn);
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        m_cancelBtn.setLayoutParams(fl);
        m_cancelBtn.setOnClickListener(mOnClickListener);
        topBar.addView(m_cancelBtn);
        ImageUtils.AddSkin(getContext(),m_cancelBtn);

        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        fl.topMargin = ShareData.PxToDpi_xhdpi(150);
        centerLoginCon = new LinearLayout(getContext());
        centerLoginCon.setGravity(Gravity.CENTER_HORIZONTAL);
        centerLoginCon.setOrientation(LinearLayout.VERTICAL);
        m_upFr_1.addView(centerLoginCon, fl);

        // 设置新密码
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(92));
        llParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        llParams.rightMargin = ShareData.PxToDpi_xhdpi(40);
        centerSetPswCon = new RelativeLayout(getContext());
        centerLoginCon.addView(centerSetPswCon, llParams);
        {

            rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
            centerSetPswIcon = new ImageView(getContext());
            centerSetPswIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            centerSetPswIcon.setImageResource(R.drawable.beauty_login_setpsw_logo);
            centerSetPswCon.addView(centerSetPswIcon, rlParams);
            centerSetPswIcon.setId(R.id.login_resetpswpage_centersetpswicon);

            rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.login_resetpswpage_centersetpswicon);
            rlParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
            rlParams.rightMargin = ShareData.PxToDpi_xhdpi(48);
            centerSetPswInput = new EditTextWithDel(getContext(), -1, R.drawable.beauty_login_delete_logo);
            centerSetPswInput.setBackgroundColor(0x00000000);
            centerSetPswInput.setPadding(0, 0, ShareData.PxToDpi_xhdpi(5), 0);
            centerSetPswInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
            centerSetPswInput.setTextColor(0xff000000);
            centerSetPswInput.setHintTextColor(0xffb3b3b3);
            centerSetPswInput.setHint(getContext().getResources().getString(R.string.resetpage_newpsw));
            centerSetPswInput.setSingleLine();
            centerSetPswInput.setKeyListener(new NumberKeyListener() {
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
            centerSetPswInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            centerSetPswInput.setTypeface(Typeface.MONOSPACE, 0);
            centerSetPswInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            centerSetPswCon.addView(centerSetPswInput, rlParams);
            centerSetPswInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
                    {
                        if(centerSetPswInput.getText().toString().length() >= 8 && centerSetPswInput.getText().toString().length() <= 20 && m_tipText.getText().toString().equals(m_pswtips))
                        {
                            m_tipsFr.setVisibility(INVISIBLE);
                            return;
                        }
                        if(centerSetPswInput.getText().toString().length() > 0 && m_tipText.getText().toString().equals(m_pswEmptytips))
                        {
                            m_tipsFr.setVisibility(INVISIBLE);
                            return;
                        }
                    }

                    if(centerSetPswInput != null)
                    {
                        if(centerSetPswInput.length() >= 8)
                        {
                            centerOkLogin.setEnabled(true);
                        }
                        else
                        {
                            centerOkLogin.setEnabled(false);
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(centerSetPswInput != null)
                    {
                        if(centerSetPswInput.length() >= 8)
                        {
                            centerOkLogin.setEnabled(true);
                        }
                        else
                        {
                            centerOkLogin.setEnabled(false);
                        }
                    }
                }
            });

            mPswShowIcon = new ImageButton(getContext());
            rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(44), ShareData.PxToDpi_xhdpi(44));
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
            mPswShowIcon.setButtonImage(R.drawable.userinfo_psw_hide_out, R.drawable.userinfo_psw_hide_over);
            centerSetPswCon.addView(mPswShowIcon, rlParams);
            mPswShowIcon.setOnClickListener(mOnClickListener);

            rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            rlParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            centerSetPswBottomLine = new ImageView(getContext());
            centerSetPswBottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
            centerSetPswBottomLine.setImageResource(R.drawable.beauty_login_line);
            centerLoginCon.addView(centerSetPswBottomLine, rlParams);
        }


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
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
            m_tipsIcon.setLayoutParams(fl);
            m_tipsFr.addView(m_tipsIcon);
            m_tipsIcon.setImageResource(R.drawable.beauify_login_tips_icon);

            m_tipText = new TextView(getContext());
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(35);
            m_tipText.setLayoutParams(fl);
            m_tipText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12f);
            m_tipText.setTextColor(0xffff4b4b);
            m_tipText.setGravity(Gravity.CENTER);
            m_tipsFr.addView(m_tipText);
        }

        llParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(538), ShareData.PxToDpi_xhdpi(77));
        llParams.topMargin = ShareData.PxToDpi_xhdpi(32);
        llParams.gravity = Gravity.CENTER_HORIZONTAL;
        centerOkLogin = new TextView(getContext());
        centerOkLogin.setGravity(Gravity.CENTER);
        centerOkLogin.setText(getContext().getResources().getString(R.string.resetpage_finish));
        centerOkLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        centerOkLogin.setTextColor(0xffffffff);
        centerOkLogin.setEnabled(false);

        centerOkLogin.setBackgroundDrawable(LoginOtherUtil.makeDrawableForSkin(R.drawable.beauty_login_btn_disable,R.drawable.beauty_login_btn_normal1,R.drawable.beauty_login_btn_press1,getContext()));
        centerOkLogin.setOnClickListener(mOnClickListener);
        centerLoginCon.addView(centerOkLogin, llParams);
    }

    public void setState(int style) {
        this.mState = style;
        if (mState == STATE_BINDPHONE) {
            centerSetPswInput.setHint(getContext().getResources().getString(R.string.resetpage_setpsw));
        }
    }

    protected void SetBK(Bitmap bmp) {
        if (bmp != null) {
            bkBmp = bmp;
            m_downFr.setBackgroundDrawable(new BitmapDrawable(bmp));
        } else {
            m_downFr.setBackgroundColor(Color.WHITE);
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == m_cancelBtn) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_忘记密码_步骤二_设置新密码_返回);
                hideKeyboard();
                mSite.TipsBackToLastPage(getContext());
            } else if (v == centerOkLogin) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_忘记密码_步骤二_设置新密码_完成);
                hideKeyboard();
                if (isFitRule()) {
                    if (mState == STATE_RESETPSW) {
                        resetPassword(centerSetPswInput.getText().toString());
                    } else {
                        bindPhone(centerSetPswInput.getText().toString());
                    }
                }
            } else if (v == mPswShowIcon) {
                if (isHidePsw) {
                    MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_忘记密码_步骤二_设置新密码_密码明文);
                    centerSetPswInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    centerSetPswInput.setSelection(centerSetPswInput.length());
                    isHidePsw = false;
                    mPswShowIcon.setButtonImage(R.drawable.userinfo_psw_show_out, R.drawable.userinfo_psw_show_over);
                } else {
                    MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_忘记密码_步骤二_设置新密码_密码加密);
                    centerSetPswInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    centerSetPswInput.setSelection(centerSetPswInput.length());
                    isHidePsw = true;
                    mPswShowIcon.setButtonImage(R.drawable.userinfo_psw_hide_out, R.drawable.userinfo_psw_hide_over);
                }
            }
        }
    };

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
    }

    /**
     * 规则判断
     */
    private boolean isFitRule() {

        String pwdStr = centerSetPswInput.getText().toString();

        switch(RequestParam.isRightPassword(pwdStr))
        {
            case -1:
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(m_pswEmptytips);
                return false;

            case -2:
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(m_pswtips2);
                return false;

            case -3:
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(m_pswtips);
                return false;
        }

        return true;
    }

    protected void setHintText(String content) {
        centerSetPswInput.setHint(getContext().getResources().getString(R.string.resetpage_writepsw));
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void resetPassword(final String passwd) {

        LoginOtherUtil.ResetPswAction.ResetPswBaseInfo baseInfo = new LoginOtherUtil.ResetPswAction.ResetPswBaseInfo();
        baseInfo.m_site = mSite;
        baseInfo.m_passwd = passwd;
        baseInfo.m_zoneNum = zoneNum;
        baseInfo.m_phoneNum = phoneNum;
        baseInfo.m_verityCode = verityCode;
        baseInfo.loginInfo = loginInfo;
        LoginOtherUtil.ResetPswAction resetPswAction = new LoginOtherUtil.ResetPswAction(getContext(),baseInfo);
        resetPswAction.action();
    }

    public void bindPhone(final String psw) {

        final LoginOtherUtil.BindPhoneAction.BindPhoneActionBaseInfo baseInfo = new LoginOtherUtil.BindPhoneAction.BindPhoneActionBaseInfo();
        baseInfo.m_zoneNum = zoneNum;
        baseInfo.m_userId = loginInfo.mUserId;
        baseInfo.m_phoneNum = phoneNum;
        baseInfo.m_verityCode = verityCode;
        baseInfo.m_psw = psw;
        baseInfo.loginInfo = loginInfo;
        baseInfo.m_site = mSite;


        m_dialog = new ProgressDialog(getContext());
        m_dialog.setMessage(getContext().getResources().getString(R.string.loginutil_bindphoneing));
        m_dialog.setCancelable(false);
        m_dialog.show();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//               final BaseInfo out = LoginUtils.bindMobile(baseInfo.m_zoneNum, baseInfo.m_userId, baseInfo.m_phoneNum, baseInfo.m_verityCode, baseInfo.m_psw, AppInterface.GetInstance(getContext()));
//                if(out != null)
//                {
//                    if(out.mCode == 0 && out.mProtocolCode == 200)
//                    {
//                        boolean reloginSuccess = false;
//                        //重新登录获取新的用户id
//                        if(m_reLoginInfo != null)
//                        {
////                            if(m_reLoginInfo instanceof LoginStyle.SinaLoginBaseInfo)
////                            {
////                                TPLoginInfo info = null;
////                                    LoginStyle.SinaLoginBaseInfo tempInfo = (LoginStyle.SinaLoginBaseInfo) m_reLoginInfo;
////                                    info = LoginUtils.TPLogin(tempInfo.m_uid, tempInfo.m_accessToken, null, (int)Long.parseLong(tempInfo.m_expiresIn), LoginUtils.Partner.sina, AppInterface.GetInstance(getContext()));
////                                    if(info != null && info.mCode == 0 && info.mProtocolCode == 200)
////                                    {
////                                        reloginSuccess = true;
////                                        loginInfo = info;
////                                    }
////                            }
////                            else if(m_reLoginInfo instanceof LoginStyle.WeixinLoginBaseInfo)
////                            {
////                                TPLoginInfo info = null;
////                                LoginStyle.WeixinLoginBaseInfo tempInfo = (LoginStyle.WeixinLoginBaseInfo) m_reLoginInfo;
////                                info = LoginUtils.weChatLogin(tempInfo.m_weixinBlog.getOpenid(), tempInfo.m_weixinBlog.getAccessToken(), tempInfo.m_weixinBlog.getRefreshToken(), (int)Long.parseLong(tempInfo.m_weixinBlog.getExpiresin()), tempInfo.m_weixinBlog.getUnionid(), AppInterface.GetInstance(getContext()));
////                                if(info != null && info.mCode == 0 && info.mProtocolCode == 200)
////                                {
////                                    reloginSuccess = true;
////                                    loginInfo = info;
////                                }
////                            }
//
//                            LoginInfo info = LoginUtils.userLogin(baseInfo.m_zoneNum, baseInfo.m_phoneNum, baseInfo.m_psw, AppInterface.GetInstance(getContext()));
//                            if(info != null && info.mCode == 0 && info.mProtocolCode == 200)
//                            {
//                                reloginSuccess = true;
//                                loginInfo = info;
//                            }
//                        }
//
//                        if(reloginSuccess)
//                        {
//                           final UserInfo userInfo = LoginUtils.getUserInfo(loginInfo.mUserId, loginInfo.mAccessToken, AppInterface.GetInstance(getContext()));
//                            ResetPswPage.this.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                  if(userInfo != null)
//									{
//										if(userInfo.mCode == 0 && userInfo.mProtocolCode == 200)
//										{
//											UserMgr.SaveCache(userInfo);
//											bindSuccess(baseInfo.m_userId);
//											EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
//										}
//										else
//										{
//                                            bindSuccess(baseInfo.m_userId);
//										}
//									}
//									dismissDlg();
//                                }
//                            });
//                        }
//                        else
//                        {
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    dismissDlg();
//                                    mSite.reLogin(null,getContext());
//                                }
//                            });
//                        }
//                    }
//                    else
//                    {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (out.mCode == 10002)
//                                {
//                                    LoginOtherUtil.showToast(getContext().getResources().getString(R.string.loginutil_writepassword));
//                                }
//                                else
//                                {
//                                    if(out != null && out.mMsg != null && out.mMsg.length() > 0) LoginOtherUtil.showToast(out.mMsg);
//                                    else LoginOtherUtil.showToast(getContext().getResources().getString(R.string.loginutil_bindphonefail));
//                                }
//			             dismissDlg();
//                            }
//                        });
//                    }
//                }
//                else
//                {
//                    mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								dismissDlg();
//								LoginOtherUtil.showToast(getContext().getResources().getString(R.string.loginutil_networkerror));
//							}
//						});
//                }
//
//            }
//        }).start();

        LoginUtils2.bindMobile(baseInfo.m_zoneNum, baseInfo.m_userId, baseInfo.m_phoneNum, baseInfo.m_verityCode, baseInfo.m_psw, new HttpResponseCallback()
        {
            @Override
            public void response(Object object)
            {
                if(object == null)
                {
                    dismissDlg();
                    LoginOtherUtil.showToast(getContext().getResources().getString(R.string.loginutil_networkerror));
                    return;
                }
                BaseInfo out = (BaseInfo)object;
                if(out.mCode == 0 && out.mProtocolCode == 200)
                {
                    boolean reloginSuccess = false;
                    //重新登录获取新的用户id
                    if(m_reLoginInfo == null)
                    {
                        dismissDlg();
                        mSite.reLogin(null,getContext());
                        return;
                    }
                    LoginUtils2.userLogin(baseInfo.m_zoneNum, baseInfo.m_phoneNum, baseInfo.m_psw, new HttpResponseCallback()
                    {
                        @Override
                        public void response(Object object)
                        {
                            if(object == null)
                            {
                                dismissDlg();
                                mSite.reLogin(null,getContext());
                                return;
                            }
                            LoginInfo info = (LoginInfo)object;
                            if(info.mCode == 0 && info.mProtocolCode == 200)
                            {
                                loginInfo = info;
                                LoginUtils2.getUserInfo(loginInfo.mUserId, loginInfo.mAccessToken, new HttpResponseCallback()
                                {
                                    @Override
                                    public void response(Object object)
                                    {
                                        if(object == null)
                                        {
                                            dismissDlg();
                                            mSite.reLogin(null,getContext());
                                            return;
                                        }
                                        UserInfo userInfo = (UserInfo)object;
                                        if(userInfo.mCode == 0 && userInfo.mProtocolCode == 200)
                                        {
                                            UserMgr.SaveCache(userInfo);
                                            bindSuccess(baseInfo.m_userId);
                                            EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
                                        }
                                        else
                                        {
                                            bindSuccess(baseInfo.m_userId);
                                        }
                                        dismissDlg();
                                    }
                                });
                            }
                        }
                    });
                }
                else
                {
                    if(out != null && out.mMsg != null && out.mMsg.length() > 0) LoginOtherUtil.showToast(out.mMsg);
                    else LoginOtherUtil.showToast(getContext().getResources().getString(R.string.loginutil_bindphonefail));
                    dismissDlg();
                }
            }
        });
    }


    private void dismissDlg()
    {
        if (m_dialog != null) {
				m_dialog.dismiss();
			}
    }


    private void bindSuccess(String id)
    {
        if(loginInfo != null)
			{
				LoginOtherUtil.setSettingInfo(loginInfo);
			}
			Credit.CreditIncome(getContext(), getContext().getResources().getInteger(R.integer.积分_关联手机)+"");
			HashMap<String,Object> datas = new HashMap<String, Object>();
			datas.put("id", id);
			mSite.successBind(datas,getContext());
    }

    private ProgressDialog m_dialog = null;




    @SuppressLint("NewApi")
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
        if (bkBmp != null) {
            bkBmp = null;
        }

        MyBeautyStat.onPageEndByRes(R.string.个人中心_登录注册_忘记密码_步骤二);
        TongJiUtils.onPageEnd(getContext(), R.string.忘记密码);
    }

    @Override
    public void onResume() {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.忘记密码);
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.忘记密码);
    }

    private TipsDialog.Listener listener = new TipsDialog.Listener() {

        @Override
        public void ok() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }

        @Override
        public void cancel() {

        }
    };

    private void showDialog(View view) {
        if (dialog == null) {
            dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
        }
        if (dialog != null) {
            dialog.m_fr.removeAllViews();
            dialog.m_fr.addView(view);
            dialog.show();
        }
    }

    interface FinishLisener {
        public void comfirmBtn();
    }

}

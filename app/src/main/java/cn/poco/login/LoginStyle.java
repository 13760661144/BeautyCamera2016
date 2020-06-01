package cn.poco.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.SinaBlog.BindSinaCallback;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.credits.Credit;
import cn.poco.framework.IPage;
import cn.poco.holder.ObjHandlerHolder;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

public class LoginStyle {

    private SinaBlog mSina;
    private WeiXinBlog mWeiXin;
    private Context mContext;
    private IPage mPage;
    private boolean m_backed = false;

    private Handler mHandler = new Handler();

    private LoginRunable m_loginRunnable;
    private ProgressDialog mDialog;

    public LoginStyle(Context context, IPage page) {
        this.mContext = context;
        this.mPage = page;
    }

    public void back()
    {
        m_backed = true;
    }

    public void CancelLogin()
    {
        if(m_loginRunnable != null)
        {
            m_loginRunnable.Clear();
        }
    }


    //微博登陆
    public void bindSina(final onLoginLisener loginLisener, final LoginPage loginPage) {
        if (mSina == null) {
            mSina = new SinaBlog(mContext);
        }

        mSina.bindSinaWithSSO(new BindSinaCallback() {
            @Override
            public void success(final String accessToken, final String expiresIn, final String uid, String userName,
                                String nickName) {

                SinaLoginBaseInfo baseInfo = new SinaLoginBaseInfo();
                baseInfo.m_accessToken = accessToken;
                baseInfo.m_expiresIn = expiresIn;
                baseInfo.m_uid = uid;
                baseInfo.m_userName = userName;
                baseInfo.m_nickName = nickName;
                SinaLogin sinaLogin = new SinaLogin(mContext,baseInfo,loginLisener,loginPage);
//                new Thread(sinaLogin).start();
                ((Activity)mContext).runOnUiThread(sinaLogin);
            }

            @Override
            public void fail() {
                if(loginLisener != null)
                {
                    loginLisener.onLoginFailed();
                }
                switch (mSina.LAST_ERROR) {
                    case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                        Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.loginstyle_sinatips), Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.loginstyle_sinabinderror), Toast.LENGTH_SHORT).show();
                        break;
                }
//				LoginOtherUtil.showToastVeritical(PocoCamera.main.getApplicationContext(), "登录失败！");
            }
        });
    }

    public void WeiXinLogin(final LoginPage loginPage, final onLoginLisener loginLisener)
    {
        if (mWeiXin == null) mWeiXin = new WeiXinBlog(mContext);
        if (mWeiXin.registerWeiXin())
        {
            mWeiXin.getCode();
            SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
            {
                @Override
                public void onCallFinish(int result)
                {
                    SendWXAPI.removeListener(this);
//                    LoginOtherUtil.dismissProgressDialog();
                    if(LoginPage.mWeiXinGetCode != null && LoginPage.mWeiXinGetCode.length() > 0)
                    {
                        mDialog = LoginOtherUtil.showProgressDialog(mDialog, mContext.getResources().getString(R.string.loginstyle_binding),mContext);
                        String code = LoginPage.mWeiXinGetCode;
                        LoginPage.mWeiXinGetCode = null;
                        mWeiXin.setCode(code);
                        WeixinLoginBaseInfo baseInfo = new WeixinLoginBaseInfo();
                        baseInfo.m_weixinBlog = mWeiXin;

                        WeixinLogin weixinLogin = new WeixinLogin(mContext,baseInfo,loginLisener,loginPage);
//                        new Thread(weixinLogin).start();
                        ((Activity)mContext).runOnUiThread(weixinLogin);
                    }
                }
            };
            SendWXAPI.addListener(listener);
        }
        else {
            LoginOtherUtil.dismissProgressDialog(mDialog);
            switch (mWeiXin.LAST_ERROR) {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    Toast.makeText(mContext.getApplicationContext(),mContext.getResources().getString(R.string.loginstyle_wechattips), Toast.LENGTH_SHORT).show();
                    break;
                case WeiboInfo.BLOG_INFO_CLIENT_VERSION_LOW:
                    Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.loginstyle_wechattips2), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.loginstyle_wechatbinderror), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    public SinaBlog getSinaBlog() {
        return mSina;
    }


    //手机登陆
    public void LoginByPhone(final String zoneNum, final String phone, final String psw,final onLoginLisener loginLisener,LoginPage loginpage) {
        LoginOtherUtil.dismissProgressDialog(mDialog);
        if (loginLisener != null) {
            loginLisener.onActionLogin();
        }

        PhoneLoginBaseInfo baseInfo = new PhoneLoginBaseInfo();
        baseInfo.m_phoneNum = phone;
        baseInfo.m_zoneNum = zoneNum;
        baseInfo.m_password = psw;
        m_loginRunnable = new PhoneLogin(mContext,baseInfo,loginLisener,loginpage);
//        new Thread(m_loginRunnable).start();
        ((Activity)mContext).runOnUiThread(m_loginRunnable);
    }

    //手机登录的实现类。
    public class PhoneLogin extends LoginRunable
    {
        PhoneLogin(Context context,LoginBaseInfo info,onLoginLisener onLoginLisener,LoginPage loginPage) {
            super(context,onLoginLisener,loginPage);
            m_baseInfo = info;
        }

        @Override
        public void loginStart() {

        }

        @Override
        public void loginFinish() {

        }

        @Override
        public void loginReal(HttpResponseCallback callback)
        {
            if(m_baseInfo != null)
            {
                PhoneLoginBaseInfo baseInfo = (PhoneLoginBaseInfo) m_baseInfo;
                LoginUtils2.userLogin(baseInfo.m_zoneNum, baseInfo.m_phoneNum, baseInfo.m_password, callback);
            }
        }

//        @Override
//        public LoginInfo loginReal() {
//            LoginInfo info = null;
//            if(m_baseInfo != null)
//            {
//                PhoneLoginBaseInfo baseInfo = (PhoneLoginBaseInfo) m_baseInfo;
//                info = LoginUtils.userLogin(baseInfo.m_zoneNum, baseInfo.m_phoneNum, baseInfo.m_password, AppInterface.GetInstance(m_context));
//            }
//            return info;
//        }

        @Override
        public void loginSuccess() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Credit.syncCreditIncome(mContext, mContext.getResources().getInteger(R.integer.积分_每天使用) + "");
                }
            }).start();
        }

        @Override
        public void showLoginErrorTips(int code) {
            if(mOnloginLisener != null)
            {
                if (code == 55001) {
                    mOnloginLisener.showLoginErrorTips(m_context.getResources().getString(R.string.loginstyle_phonenumtips));
                } else if (code == 55002) {
                    mOnloginLisener.showLoginErrorTips(m_context.getResources().getString(R.string.loginstyle_passwordtips));
                } else if (code == 55003) {
                    mOnloginLisener.showLoginErrorTips(m_context.getResources().getString(R.string.loginstyle_notfound));
                } else if (code == 55004) {
                    mOnloginLisener.showLoginErrorTips(m_context.getResources().getString(R.string.loginstyle_accountlimit));
                } else if (code == 55005) {
                    mOnloginLisener.showLoginErrorTips(m_context.getResources().getString(R.string.loginstyle_passworderror));
                } else {
                    mOnloginLisener.showLoginErrorTips(mContext.getResources().getString(R.string.loginstyle_loginfail));
                }
            }
        }

        @Override
        public void loginFail() {

        }

    }


    //新浪登陆的实现类
    public class SinaLogin extends LoginRunable
    {
        SinaLogin(Context context, LoginBaseInfo info, onLoginLisener onLoginLisener,LoginPage loginPage) {
            super(context, onLoginLisener,loginPage);
            m_baseInfo = info;
        }

        @Override
        public void loginStart() {
            mDialog = LoginOtherUtil.showProgressDialog(mDialog, mContext.getResources().getString(R.string.loginstyle_logining),m_context);
        }

        @Override
        public void loginFinish() {
            LoginOtherUtil.dismissProgressDialog(mDialog);
        }

        @Override
        public void loginReal(HttpResponseCallback callback)
        {
            if(m_baseInfo != null)
            {
                SinaLoginBaseInfo baseInfo = (SinaLoginBaseInfo) m_baseInfo;
                LoginUtils2.TPLogin(baseInfo.m_uid, baseInfo.m_accessToken, Long.parseLong(baseInfo.m_expiresIn), callback);
            }
        }

//        @Override
//        public LoginInfo loginReal() {
//            //17147414574
////            final TPLoginInfo info = LoginUtils.TPLogin("966984036", "2.00NH15HCH5S6PC5ae6f453ea07jWVa", null, (int)Long.parseLong(m_baseInfo.m_expiresIn), LoginUtils.Partner.sina, AppInterface.GetInstance(m_context));
//            TPLoginInfo info = null;
//            if(m_baseInfo != null)
//            {
//                SinaLoginBaseInfo baseInfo = (SinaLoginBaseInfo) m_baseInfo;
//                info = LoginUtils.TPLogin(baseInfo.m_uid, baseInfo.m_accessToken, null, (int)Long.parseLong(baseInfo.m_expiresIn), LoginUtils.Partner.sina, AppInterface.GetInstance(m_context));
//            }
//
//            return info;
//        }

        @Override
        public void loginSuccess() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Credit.syncCreditIncome(m_context,m_context.getResources().getInteger(R.integer.积分_第三方登录)+"");
                    Credit.syncCreditIncome(mContext, mContext.getResources().getInteger(R.integer.积分_每天使用) + "");
                }
            }).start();
           }

        @Override
        public void showLoginErrorTips(int code) {
            if(code == 55007)
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_parametererror));
            }
            else if(code == 55009)
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_tokenfail));
            }
            else if(code == 55004)
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_accountlimit));
            }
            else
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_sinabinderror));
            }
            LoginOtherUtil.dismissProgressDialog(mDialog);
        }

        @Override
        public void loginFail() {

        }
    }


    //微信登陆的实现类。
    public class WeixinLogin extends LoginRunable
    {

        WeixinLogin(Context context, LoginBaseInfo info, onLoginLisener onLoginLisener,LoginPage loginPage) {
            super(context, onLoginLisener,loginPage);
            m_baseInfo = info;
        }

        @Override
        public void loginStart() {
            mDialog = LoginOtherUtil.showProgressDialog(mDialog, mContext.getResources().getString(R.string.loginstyle_binding),m_context);
        }

        @Override
        public void loginFinish() {
            LoginOtherUtil.dismissProgressDialog(mDialog);
        }

        @Override
        public void loginReal(final HttpResponseCallback callback)
        {
            if(m_baseInfo != null)
            {
                final WeixinLoginBaseInfo baseInfo = (WeixinLoginBaseInfo) m_baseInfo;
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(baseInfo != null && baseInfo.m_weixinBlog.getAccessTokenAndOpenid())
                        {
                            final boolean get_uid = baseInfo.m_weixinBlog.getUserUnionid();
                            mHandler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(get_uid) LoginUtils2.weChatLogin(baseInfo.m_weixinBlog.getOpenid(), baseInfo.m_weixinBlog.getAccessToken(), baseInfo.m_weixinBlog.getRefreshToken(), (int)Long.parseLong(baseInfo.m_weixinBlog.getExpiresin()), baseInfo.m_weixinBlog.getUnionid(), callback);
                                    else loginFail();
                                }
                            });
                        }
                        else
                        {
                            mHandler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    loginFail();
                                }
                            });
                        }
                    }
                }).start();
            }
        }

//        @Override
//        public LoginInfo loginReal() {
//            TPLoginInfo info = null;
//            if(m_baseInfo != null)
//            {
//                WeixinLoginBaseInfo baseInfo = (WeixinLoginBaseInfo) m_baseInfo;
//                if (baseInfo != null && baseInfo.m_weixinBlog.getAccessTokenAndOpenid())
//                {
//                    if(baseInfo.m_weixinBlog.getUserUnionid())
//                    {
//                        info = LoginUtils.weChatLogin(baseInfo.m_weixinBlog.getOpenid(), baseInfo.m_weixinBlog.getAccessToken(), baseInfo.m_weixinBlog.getRefreshToken(), (int)Long.parseLong(baseInfo.m_weixinBlog.getExpiresin()), baseInfo.m_weixinBlog.getUnionid(), AppInterface.GetInstance(m_context));
//                    }
//                    else
//                    {
//                        loginFail();
//                    }
//                }
//                else
//                {
//                    loginFail();
//                }
//            }
//            return info;
//        }

        @Override
        public void loginSuccess() {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   Credit.syncCreditIncome(mContext, mContext.getResources().getInteger(R.integer.积分_每天使用) + "");
                   Credit.syncCreditIncome(m_context,m_context.getResources().getInteger(R.integer.积分_第三方登录)+"");
               }
           }).start();
        }

        @Override
        public void showLoginErrorTips(int code)
        {
            if(code == 55007)
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_parametererror));
            }
            else if(code == 55009)
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_tokenfail));
            }
            else if(code == 55004)
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_accountlimit));
            }
            else
            {
                LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_sinabinderror));
            }
            LoginOtherUtil.dismissProgressDialog(mDialog);
        }

        @Override
        public void loginFail() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    WeixinLoginBaseInfo baseInfo = (WeixinLoginBaseInfo) m_baseInfo;
                    LoginOtherUtil.dismissProgressDialog(mDialog);
                    baseInfo.m_weixinBlog.backToSendStatus();
                    LoginOtherUtil.showToast(mContext.getResources().getString(R.string.loginstyle_wechatbinderror));
                }
            });
        }

    }



    //手机登录，sina登陆和微信登陆的流程的封装。
    public static abstract class LoginRunable extends ObjHandlerHolder<LoginPage> implements Runnable
    {
        public Context m_context;
        protected onLoginLisener mOnloginLisener;
        public LoginBaseInfo m_baseInfo;

        public LoginRunable(Context context,onLoginLisener lisener,LoginPage obj) {
            super(obj);
            this.m_context = context;
            this.mOnloginLisener = lisener;
        }

        @Override
        public void run() {
            this.post(new Runnable() {
                @Override
                public void run() {
                    if(mObj != null)
                    {
                        loginStart();
                    }
                }
            });
            loginReal(new HttpResponseCallback()
            {
                @Override
                public void response(Object object)
                {
                    if(object == null)
                    {
                        if(mObj != null)
                        {
                            if(mOnloginLisener != null)
                            {
                                mOnloginLisener.onLoginFailed();
                            }
                            LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_networderrorloginfail));
                            loginFinish();
                        }
                        return;
                    }
                    final LoginInfo loginInfo = (LoginInfo)object;
                    if(loginInfo.mCode == 0 && loginInfo.mProtocolCode == 200)
                    {
                        LoginUtils2.getUserInfo(loginInfo.mUserId, loginInfo.mAccessToken, new HttpResponseCallback()
                        {
                            @Override
                            public void response(Object object)
                            {
                                if(mObj == null) return;
                                if(object == null)
                                {
                                    LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_loginfail));
                                    if(mOnloginLisener != null)
                                    {
                                        mOnloginLisener.onLoginFailed();
                                    }
                                    loginFinish();
                                    return;
                                }
                                UserInfo userInfo = (UserInfo)object;
                                if(userInfo.mCode == 0 && userInfo.mProtocolCode == 200)
                                {
                                    if(userInfo.mMobile != null && userInfo.mMobile.length() > 1)
                                    {
                                        UserMgr.SaveCache(userInfo);
                                        LoginOtherUtil.setSettingInfo(loginInfo);
                                        loginSuccess();

                                        if(mOnloginLisener != null)
                                        {
                                            mOnloginLisener.onLoginSuccess(loginInfo,m_baseInfo,false);
                                        }
                                    }
                                    else
                                    {
                                        if(mOnloginLisener != null)
                                        {
                                            mOnloginLisener.onLoginSuccess(loginInfo,m_baseInfo,true);
                                        }
                                    }
                                }
                                else
                                {
                                    LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_loginfail));
                                    if(mOnloginLisener != null)
                                    {
                                        mOnloginLisener.onLoginFailed();
                                    }
                                }
                                loginFinish();
                            }
                        });
                    }
                    else
                    {
                        if(mObj == null) return;
                        if(mOnloginLisener != null)
                        {
                            mOnloginLisener.onLoginFailed();
                        }
                        showLoginErrorTips(loginInfo.mCode);
                        loginFinish();
                    }
                }
            });

//            final LoginInfo loginInfo = loginReal();
//            if(loginInfo != null)
//            {
//                if(loginInfo.mCode == 0 && loginInfo.mProtocolCode == 200)
//                {
//                    final UserInfo userInfo = LoginUtils.getUserInfo(loginInfo.mUserId, loginInfo.mAccessToken, AppInterface.GetInstance(m_context));
//                    this.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(mObj != null)
//                            {
//                                if(userInfo != null)
//                                {
//                                    if(userInfo.mCode == 0 && userInfo.mProtocolCode == 200)
//                                    {
//                                        if(userInfo.mMobile != null && userInfo.mMobile.length() > 1)
//                                        {
//                                            UserMgr.SaveCache(userInfo);
//                                            LoginOtherUtil.setSettingInfo(loginInfo);
//                                            loginSuccess();
//
//                                            if(mOnloginLisener != null)
//                                            {
//                                                mOnloginLisener.onLoginSuccess(loginInfo,m_baseInfo,false);
//                                            }
//                                        }
//                                        else
//                                        {
//                                            if(mOnloginLisener != null)
//                                            {
//                                                mOnloginLisener.onLoginSuccess(loginInfo,m_baseInfo,true);
//                                            }
//                                        }
//                                    }
//                                    else
//                                    {
//                                        LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_loginfail));
//                                        if(mOnloginLisener != null)
//                                        {
//                                            mOnloginLisener.onLoginFailed();
//                                        }
//                                    }
//                                }
//                                else
//                                {
//                                    LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_loginfail));
//                                    if(mOnloginLisener != null)
//                                    {
//                                        mOnloginLisener.onLoginFailed();
//                                    }
//                                }
//                                loginFinish();
//                            }
//                        }
//                    });
//                }
//                else
//                {
//                       this.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(mObj != null)
//                            {
//                                if(mOnloginLisener != null)
//                                {
//                                    mOnloginLisener.onLoginFailed();
//                                }
//                                showLoginErrorTips(loginInfo.mCode);
//                                loginFinish();
//                            }
//                        }
//                    });
//                }
//            }
//            else
//            {
//                this.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(mObj != null)
//                        {
//                            if(mOnloginLisener != null)
//                            {
//                                mOnloginLisener.onLoginFailed();
//                            }
//                            LoginOtherUtil.showToast(m_context.getResources().getString(R.string.loginstyle_networderrorloginfail));
//                            loginFinish();
//                        }
//                    }
//                });
//            }
        }

        public abstract void loginStart();

        public abstract void loginFinish();

//        public abstract LoginInfo loginReal();
        public abstract void loginReal(HttpResponseCallback callback);

        public abstract void loginSuccess();

        public abstract void showLoginErrorTips(int code);

        public abstract void loginFail();
        //
        // //登录成功后，同步脸型数据
        // public void loginSuccessToSyncShapeData()
        // {
        //     ShapeSyncResMgr.getInstance().post2UpdateSyncData(m_context);
        // }
    }

    public static class LoginBaseInfo
    {

    }

    //微信登陆用到的数据
    public static class WeixinLoginBaseInfo extends LoginBaseInfo
    {
        public WeiXinBlog m_weixinBlog;
    }


    //手机登陆用到的数据
    public static class PhoneLoginBaseInfo extends LoginBaseInfo
    {
        public String m_phoneNum;
        public String m_zoneNum;
        public String m_password;
    }

    //新浪登陆用到的数据
    public static class SinaLoginBaseInfo extends LoginBaseInfo
    {
        public String m_accessToken;
        public String m_expiresIn;
        public String m_uid;
        public String m_userName;
        public String m_nickName;
    }
}

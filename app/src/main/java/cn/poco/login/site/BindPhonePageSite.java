package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.BindPhonePage;
import cn.poco.webview.site.WebViewPageSite2;

/**
 * 侧栏云相册绑定手机，填写验证码
 */
public class BindPhonePageSite extends BaseSite{

    public BindPhonePageSite()
    {
     super(SiteID.BINDPHONE);
    }

    @Override
    public IPage MakePage(Context context) {
        return new BindPhonePage(context,this) ;
    }

    //填写验证码验证成功之后跳转调用
    public void bindSuccess(HashMap<String ,Object> datas,Context context)
    {
        MyFramework.SITE_Open(context, ResetPswPageSite.class,datas,Framework2.ANIM_NONE);
    }

    //假如填入已注册的手机号，弹出弹框，点击去登陆的时候调用
    public void toLoginPage(HashMap<String,Object> datas,Context context)
    {
//        MyFramework.SITE_BackTo(PocoCamera.main, HomePageSite.class,null, Framework2.ANIM_NONE);
        MyFramework.SITE_Open(context, LoginPageSite.class,datas,Framework2.ANIM_NONE);
    }

    //跳转到选择地区码页面的时候调用
    public void chooseCountry(Context context)
    {
        MyFramework.SITE_Popup(context,ChooseCountryAreaCodePageSite.class,null,Framework2.ANIM_NONE);
    }

    //点击返回按钮返回
    public void onBackToLastPage(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

    //点击手机返回键返回
    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    //点击美人信息协议的时候调用跳转
    public void OpenWebView(String url,Context context)
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("url", url);
        MyFramework.SITE_Popup(context, WebViewPageSite2.class, params, Framework2.ANIM_NONE);
    }
}

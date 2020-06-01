package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.RegisterLoginPage;
import cn.poco.utils.Utils;
import cn.poco.webview.site.WebViewPageSite2;

/**
 * 从登录页进入注册的验证手机
 */
public class RegisterLoginPageSite extends BaseSite {

    public RegisterLoginPageSite()
    {
        super(SiteID.REGISTER_VERITYCODE);
    }

    @Override
    public IPage MakePage(Context context) {
        return new RegisterLoginPage(context,this);
    }

    //验证成功验证码之后调用，跳到注册页
    public void verify_Code(HashMap<String,Object> datas,Context context)
    {
        HashMap<String ,Object> params = new HashMap<>();
        Bitmap bmp = (Bitmap) datas.get("img");
        String path = FileCacheMgr.GetLinePath();
        if(Utils.SaveTempImg(bmp,path))
        {
            params.put("img",path);
        }
        params.put("info",datas.get("info"));
        MyFramework.SITE_Open(context, RegisterLoginInfoPageSite.class, params, Framework2.ANIM_NONE);
    }

    //弹出选择国家和地区的页面
    public void chooseCountry(Context context)
    {
        MyFramework.SITE_Popup(context,ChooseCountryAreaCodePageSite.class,null,Framework2.ANIM_NONE);
    }

    //返回上一步
    public void toLoginPage(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

     //返回上一步
    public void onBackToLastPage(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

    //返回上一步
    public void onBack(Context context)
    {
        HashMap<String,Object> params = new HashMap<>();
        params.put("isBack",true);
        MyFramework.SITE_Back(context,params,Framework2.ANIM_NONE);
    }

     //弹出美人信息用户协议的页面
    public void OpenWebView(String url,Context context)
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("url", url);
        MyFramework.SITE_Popup(context, WebViewPageSite2.class, params, Framework2.ANIM_NONE);
    }
}

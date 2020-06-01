package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite17;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.LoginAllAnim;
import cn.poco.login.RegisterLoginInfoPage;

/**
 * 注册流程验证完手机进入
 */
public class RegisterLoginInfoPageSite extends BaseSite {

    public RegisterLoginInfoPageSite()
    {
        super(SiteID.REGISTER_DETAIL);
    }
    @Override
    public IPage MakePage(Context context) {
        return new RegisterLoginInfoPage(context,this);
    }

    //注册成功，返回首页
    public void registerSuccess(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_BackTo(context, HomePageSite.class, null, Framework2.ANIM_NONE);
    }

    //点击上传头像按钮调用
    public void uploadHeadImg(HashMap<String,Object> datas,Context context)
    {
        MyFramework.SITE_Popup(context, AlbumSite17.class, datas, Framework2.ANIM_TRANSLATION_TOP);
    }

    //返回上一页
    public void backtoLastPage(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

    //返回上一页
    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }
}

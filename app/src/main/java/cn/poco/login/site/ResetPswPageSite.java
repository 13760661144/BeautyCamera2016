package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.LoginAllAnim;
import cn.poco.login.ResetPswPage;



/**
 * 侧边栏进入云相册绑定手机或忘记密码验证完手机号后进入修改密码
 */
public class ResetPswPageSite extends BaseSite {


    public ResetPswPageSite()
    {
        super(SiteID.RESETPSW);
    }
    @Override
    public IPage MakePage(Context context) {
        return new ResetPswPage(context,this);
    }

    /**
     * 绑定手机成功跳转
     */
    public void successBind(HashMap<String,Object> datas,Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_BackTo(context, HomePageSite.class,datas, Framework2.ANIM_NONE);
    }


     /**
     * 绑定手机成功后，重新登录失败调用
     */
    public void reLogin(HashMap<String,Object> datas,Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_BackTo(context, LoginPageSite.class,datas, Framework2.ANIM_NONE);
    }

    /**
     * 返回上个页面
     */
    public void TipsBackToLastPage(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    /*public void TipsBackToHomePage()
    {
        MyFramework.SITE_BackTo(PocoCamera.main, HomePageSite.class,null, Framework2.ANIM_NONE);
    }*/

    /**
     * 设置密码成功
     */
    public void resetPswSuccess(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_BackTo(context, HomePageSite.class,null, Framework2.ANIM_NONE);
    }

    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }
}

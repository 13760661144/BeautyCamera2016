package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.LoginAllAnim;

/**
 * 第三方登陆进入绑定手机，设置密码
 */
public class ResetPswPageSite4 extends ResetPswPageSite{

    /**
     * 绑定手机成功跳转
     */
    @Override
    public void successBind(HashMap<String,Object> datas,Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_Open(context, HomePageSite.class, datas, Framework2.ANIM_NONE);
    }
}

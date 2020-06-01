package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 分享页到第三方登陆进入绑定手机，设置密码
 */
public class ResetPswPageSite7 extends ResetPswPageSite{

    /**
     * 绑定手机成功跳转
     */
    @Override
    public void successBind(HashMap<String,Object> datas,Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_ClosePopup(context,null, Framework2.ANIM_NONE);
    }
}

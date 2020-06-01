package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 素材解锁 第三方登陆进入绑定手机，设置密码
 */
public class ResetPswPageSite5 extends ResetPswPageSite{

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

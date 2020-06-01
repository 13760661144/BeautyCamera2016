package cn.poco.login.site;

import android.content.Context;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 从素材解锁到登陆，忘记密码，到设置密码
 */
public class ResetPswPageSite3 extends ResetPswPageSite {

    /**
     * 设置密码成功
     */
    @Override
    public void resetPswSuccess(Context context) {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }
}

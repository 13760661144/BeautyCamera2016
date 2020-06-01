package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 弹窗进入云相册没绑定手机，填写验证码
 */
public class BindPhonePageSite2 extends BindPhonePageSite {
    @Override
    public void bindSuccess(HashMap<String, Object> datas,Context context) {
        MyFramework.SITE_Open(context, ResetPswPageSite2.class, datas, Framework2.ANIM_NONE);
    }

    @Override
    public void toLoginPage(HashMap<String, Object> datas,Context context) {
        MyFramework.SITE_OpenAndClosePopup(context,LoginPageSite6.class,null,Framework2.ANIM_NONE);
    }
}

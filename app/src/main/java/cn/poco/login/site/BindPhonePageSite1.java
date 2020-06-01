package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 个人中心进入绑定手机，填写验证码
 */
public class BindPhonePageSite1 extends BindPhonePageSite{

    @Override
    public void bindSuccess(HashMap<String ,Object> datas,Context context)
    {
        MyFramework.SITE_Open(context, ResetPswPageSite1.class, datas, Framework2.ANIM_NONE);
    }
}

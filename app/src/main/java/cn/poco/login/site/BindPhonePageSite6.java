package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 分享页到第三方登陆强制绑定手机填写验证码，填写验证码
 */
public class BindPhonePageSite6 extends BindPhonePageSite{

    @Override
    public void bindSuccess(HashMap<String, Object> datas, Context context) {
        datas.put("userInfo",m_inParams.get("userInfo"));
        MyFramework.SITE_Open(context,ResetPswPageSite7.class,datas,Framework2.ANIM_NONE);
    }

}

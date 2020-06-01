package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 素材解锁 第三方登陆强制绑定手机填写验证码，填写验证码
 */
public class BindPhonePageSite5 extends BindPhonePageSite{

    @Override
    public void bindSuccess(HashMap<String, Object> datas, Context context) {
        datas.put("userInfo",m_inParams.get("userInfo"));
        MyFramework.SITE_Open(context,ResetPswPageSite5.class,datas,Framework2.ANIM_NONE);
    }

    //假如填入已注册的手机号，弹出弹框，点击去登陆的时候调用
    public void toLoginPage(HashMap<String,Object> datas,Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }
}

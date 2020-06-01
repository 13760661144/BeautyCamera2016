package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 第三方登陆强制绑定手机填写验证码，填写验证码
 */
public class BindPhonePageSite4 extends BindPhonePageSite{

    @Override
    public void toLoginPage(HashMap<String,Object> datas,Context context)
    {
//        MyFramework.SITE_BackTo(PocoCamera.main, HomePageSite.class,null, Framework2.ANIM_NONE);
        MyFramework.SITE_Open(context, LoginPageSite.class,datas,Framework2.ANIM_NONE);
    }

    @Override
    public void onBackToLastPage(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

    @Override
    public void bindSuccess(HashMap<String, Object> datas, Context context) {
        datas.put("userInfo",m_inParams.get("userInfo"));
        MyFramework.SITE_Open(context,ResetPswPageSite4.class,datas,Framework2.ANIM_NONE);
    }


    @Override
    public void onBack(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }
}

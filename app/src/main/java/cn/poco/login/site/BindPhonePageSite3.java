package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 侧栏 任务大厅->领任务->绑定手机，填写验证码
 * 或钱包->
 */
public class BindPhonePageSite3 extends BindPhonePageSite{

    public void toLoginPage(HashMap<String,Object> datas,Context context)
    {
//        MyFramework.SITE_BackTo(PocoCamera.main, HomePageSite.class,null, Framework2.ANIM_NONE);
        MyFramework.SITE_Open(context, LoginPageSite8.class,datas,Framework2.ANIM_NONE);
    }


    public void onBackToLastPage(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

    public void onBack(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }
}

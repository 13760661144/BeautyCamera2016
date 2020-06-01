package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite85;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 分享页，登录页，注册页
 */
public class RegisterLoginInfoPageSite2 extends RegisterLoginInfoPageSite {

    //注册成功，关闭弹出的页面
    public void registerSuccess(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }

    //点击上传头像按钮调用
    public void uploadHeadImg(HashMap<String,Object> datas,Context context)
    {
        MyFramework.SITE_Popup(context, AlbumSite85.class, datas, Framework2.ANIM_TRANSLATION_TOP);
    }
}

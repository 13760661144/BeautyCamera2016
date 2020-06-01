package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite11;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginAllAnim;

/**
 * 保存脸型数据注册登录site
 *
 * @author lmx
 *         Created by lmx on 2018-01-29.
 */

public class RegisterLoginInfoPageSite11 extends RegisterLoginInfoPageSite
{
    //注册成功
    public void registerSuccess(Context context)
    {
        LoginAllAnim.ReSetLoginAnimData();
        //ShapeSyncResMgr.getInstance().post2UpdateSyncData(context);
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }

    //上传头像
    public void uploadHeadImg(HashMap<String, Object> datas, Context context)
    {
        MyFramework.SITE_Popup(context, AlbumSite11.class, datas, Framework2.ANIM_TRANSLATION_TOP);
    }
}

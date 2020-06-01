package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 解锁登陆，注册，选择头像进入
 */
public class EditHeadIconImgPageSite3 extends EditHeadIconImgPageSite {

    @Override
    public void upLoadSuccess(HashMap<String,Object> datas,Context context)
    {
        MyFramework.SITE_BackTo(context, RegisterLoginInfoPageSite1.class, datas, Framework2.ANIM_NONE);
    }
    @Override
    public void onBackToLastPage(Context context)
    {
//        MyFramework.SITE_BackTo(PocoCamera.main,RegisterLoginInfoPageSite.class,null,Framework2.ANIM_NONE);
        MyFramework.SITE_BackTo(context,RegisterLoginInfoPageSite1.class,null,Framework2.ANIM_TRANSLATION_TOP);
    }

    @Override
    public void onBack(Context context)
    {
        MyFramework.SITE_ClosePopup(context,null,Framework2.ANIM_TRANSLATION_TOP);
    }

}

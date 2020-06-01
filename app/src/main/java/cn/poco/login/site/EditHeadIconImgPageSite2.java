package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.UserInfoPage;

/**
 * 积分打开用户信息，修改头像进入
 */
public class EditHeadIconImgPageSite2 extends EditHeadIconImgPageSite {
    @Override
    public void onBackToLastPage(Context context) {
        HashMap<String ,Object> datas = new HashMap<>();
        datas.put("m_mode" , UserInfoPage.NONE);
        datas.put("m_opera", UserInfoPage.ISBACK);
        MyFramework.SITE_BackTo(context, UserInfoPageSite2.class, datas, Framework2.ANIM_TRANSLATION_TOP);
    }

    @Override
    public void upLoadSuccess(HashMap<String, Object> params,Context context) {
        params.put("m_opera",UserInfoPage.FINISH);
        MyFramework.SITE_BackTo(context, UserInfoPageSite2.class,params, Framework2.ANIM_NONE);
    }
}

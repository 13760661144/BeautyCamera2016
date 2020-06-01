package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.UserInfoPage;

/**
 * 个人信息修改头像进入
 */
public class EditHeadIconImgPageSite1 extends EditHeadIconImgPageSite {
    //返回个人信息页
    @Override
    public void onBackToLastPage(Context context) {
        HashMap<String ,Object> datas = new HashMap<>();
        datas.put("m_mode" , UserInfoPage.NONE);
        datas.put("m_opera", UserInfoPage.ISBACK);
        MyFramework.SITE_BackTo(context, UserInfoPageSite.class, datas, Framework2.ANIM_TRANSLATION_TOP);
    }

    //上传头像成功，返回个人信息页
    @Override
    public void upLoadSuccess(HashMap<String, Object> params,Context context) {
        params.put("m_opera",UserInfoPage.FINISH);
        MyFramework.SITE_BackTo(context, UserInfoPageSite.class,params, Framework2.ANIM_NONE);
    }
}

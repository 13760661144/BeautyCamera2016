package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.EditHeadIconImgPage;

/**
 * 注册流程上传头像进入
 */
public class EditHeadIconImgPageSite extends BaseSite {

    public EditHeadIconImgPageSite()
    {
        super(SiteID.REGISTER_HEAD);
    }
    @Override
    public IPage MakePage(Context context) {
        return new EditHeadIconImgPage(context,this);
    }

    //上传头像成功，返回注册页
    public void upLoadSuccess(HashMap<String,Object> datas,Context context)
    {
        MyFramework.SITE_BackTo(context,RegisterLoginInfoPageSite.class,datas,Framework2.ANIM_NONE);
//        MyFramework.SITE_ClosePopup(PocoCamera.main,datas,Framework2.ANIM_NONE);
    }

    //返回注册页
    public void onBackToLastPage(Context context)
    {
//        MyFramework.SITE_BackTo(PocoCamera.main,RegisterLoginInfoPageSite.class,null,Framework2.ANIM_NONE);
        HashMap<String,Object> temp = new HashMap<>();
        temp.put("isBack",true);
        MyFramework.SITE_BackTo(context,RegisterLoginInfoPageSite.class,temp,Framework2.ANIM_TRANSLATION_TOP);
    }

    //返回上一页
    public void onBack(Context context)
    {
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_TRANSLATION_TOP);
    }

}

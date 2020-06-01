package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.ChooseCountryAreaCodePage;

/**
 * 登录，注册和忘记密码填写验证码时选择不同国家进入
 */
public class ChooseCountryAreaCodePageSite extends BaseSite{

    public ChooseCountryAreaCodePageSite()
    {
        super(SiteID.CHOOSE_COUNTRY);
    }

    @Override
    public IPage MakePage(Context context) {
        return new ChooseCountryAreaCodePage(context,this);
    }

    //选择完跳转
    public void onSel(HashMap<String,Object> datas,Context context)
    {
        MyFramework.SITE_ClosePopup(context,datas,Framework2.ANIM_NONE);
    }

    //返回上一步
    public void backToLastPage(Context context)
    {
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }

    //返回上一步
    public void onBack(Context context)
    {
        MyFramework.SITE_ClosePopup(context,null,Framework2.ANIM_NONE);
    }

}

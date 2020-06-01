package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.ResetLoginPswPage;
import cn.poco.utils.Utils;

/**
 * 登录页，点击忘记密码，验证手机号页面
 */
public class ResetLoginPswPageSite extends BaseSite {

    public ResetLoginPswPageSite()
    {
        super(SiteID.PRE_RESETPSW);
    }
    @Override
    public IPage MakePage(Context context) {
        return new ResetLoginPswPage(context,this);
    }

    //验证成功手机号之后跳转到设置密码页面
    public void pre_reset(HashMap<String,Object> dates,Context context)
    {
        HashMap<String,Object> params = new HashMap<>();
        String path = FileCacheMgr.GetLinePath();
        Bitmap bmp = (Bitmap) dates.get("img");
        if(Utils.SaveTempImg(bmp,path))
        {
            params.put("img",path);
        }
        params.put("info" ,dates.get("info"));
        params.put("mode" ,dates.get("mode"));
        MyFramework.SITE_Open(context, ResetPswPageSite.class, params, Framework2.ANIM_NONE);
    }

    //返回上一步
    public void backToLastPage(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

    //选择国家页面
    public void chooseCountry(Context context)
    {
        MyFramework.SITE_Popup(context,ChooseCountryAreaCodePageSite.class,null,Framework2.ANIM_NONE);
    }

    //返回上一步
    public void onBack(Context context)
    {
        MyFramework.SITE_Back(context,null,Framework2.ANIM_NONE);
    }

}

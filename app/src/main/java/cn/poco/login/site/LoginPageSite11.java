package cn.poco.login.site;

/**
 * @author lmx
 * Created by lmx on 2018-01-22.
 */

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.login.LoginStyle;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.utils.Utils;

/**
 * 保存脸型数据登录site
 */
public class LoginPageSite11 extends LoginPageSite
{
    //关闭弹出的登陆页面
    @Override
    public void onBack(Context context)
    {
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }

    //登陆成功，关闭弹出的登陆页面
    @Override
    public void loginSuccess(Context context)
    {
        MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
    }

    //创建账号
    @Override
    public void creatAccount(HashMap<String, Object> datas, Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        String path = FileCacheMgr.GetLinePath();
        Bitmap bmp = (Bitmap) datas.get("img");
        if (Utils.SaveTempImg(bmp, path))
        {
            params.put("img", path);
        }
        MyFramework.SITE_Open(context, RegisterLoginPageSite11.class, params, Framework2.ANIM_NONE);
    }


    //忘记密码
    @Override
    public void LosePsw(HashMap<String, Object> datas, Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        String path = FileCacheMgr.GetLinePath();
        Bitmap bmp = (Bitmap) datas.get("img");
        if (Utils.SaveTempImg(bmp, path))
        {
            params.put("img", path);
        }
        params.put("info", datas.get("info"));
        MyFramework.SITE_Open(context, ResetLoginPswPageSite11.class, params, Framework2.ANIM_NONE);
    }

    //第三方登录
    @Override
    public void thirdPartLoginOneStepFinish(Context context, LoginInfo info, LoginStyle.LoginBaseInfo baseInfo)
    {
        HashMap<String, Object> params = new HashMap<>();
        params.put("loginInfo", info);
        params.put("relogininfo", baseInfo);
        MyFramework.SITE_Open(context, BindPhonePageSite11.class, params, Framework2.ANIM_NONE);
    }
}

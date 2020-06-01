package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.utils.Utils;

/**
 * 保存脸型数据注册登录site
 *
 * @author lmx
 *         Created by lmx on 2018-01-29.
 */

public class RegisterLoginPageSite11 extends RegisterLoginPageSite
{
    //验证码验证成功
    public void verify_Code(HashMap<String, Object> datas, Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        Bitmap bmp = (Bitmap) datas.get("img");
        String path = FileCacheMgr.GetLinePath();
        if (Utils.SaveTempImg(bmp, path))
        {
            params.put("img", path);
        }
        params.put("info", datas.get("info"));
        MyFramework.SITE_Open(context, RegisterLoginInfoPageSite11.class, params, Framework2.ANIM_NONE);
    }
}

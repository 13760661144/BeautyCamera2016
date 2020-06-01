package cn.poco.login.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.utils.Utils;

/**
 * 保存脸型数据重置密码site
 *
 * @author lmx
 *         Created by lmx on 2018-01-29.
 */

public class ResetLoginPswPageSite11 extends ResetLoginPswPageSite
{
    @Override
    public void pre_reset(HashMap<String, Object> dates, Context context)
    {
        HashMap<String, Object> params = new HashMap<>();
        String path = FileCacheMgr.GetLinePath();
        Bitmap bmp = (Bitmap) dates.get("img");
        if (Utils.SaveTempImg(bmp, path))
        {
            params.put("img", path);
        }
        params.put("info", dates.get("info"));
        params.put("mode", dates.get("mode"));
        MyFramework.SITE_Open(context, ResetPswPageSite11.class, params, Framework2.ANIM_NONE);
    }
}

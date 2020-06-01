package cn.poco.beautify4.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.camera.BrightnessUtils;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * Created by lgh on 2017/8/30.
 * 社区打开
 */

public class Beautify4PageSite7 extends Beautify4PageSite
{
    @Override
    public void OnSave(Context context, HashMap<String, Object> params)
    {
        //恢复手机亮度，清除数据
        BrightnessUtils instance = BrightnessUtils.getInstance();
        if (instance != null)
        {
            instance.setContext(context).unregisterBrightnessObserver();
            instance.resetToDefault();
            instance.clearAll();
        }

        RotationImg2 img = (RotationImg2) params.get("img");
        String[] arr = new String[]{img.m_img.toString()};
        HashMap<String, Object> temp = new HashMap<>();
        temp.put("imgPath", arr);
        MyFramework.SITE_BackTo(context, HomePageSite.class, temp, Framework2.ANIM_NONE);
    }

//	@Override
//	public void OnBack()
//	{
//		MyFramework.SITE_BackTo(PocoCamera.main, CameraPageSite302.class, null, Framework2.ANIM_NONE);
//	}


    @Override
    public void OnBack(Context context)
    {
        super.OnBack(context);
    }
}

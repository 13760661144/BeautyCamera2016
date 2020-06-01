package cn.poco.camera.site;

import android.content.Context;

import cn.poco.camera3.CameraPageV3;
import cn.poco.framework.IPage;

/**
 * Created by admin on 2017/8/14.
 */

public class CameraPageSite111 extends CameraPageSite
{
    @Override
    public IPage MakePage(Context context)
    {
        return new CameraPageV3(context, this);
    }
}

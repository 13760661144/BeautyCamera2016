package cn.poco.brush.site;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.HashMap;

import cn.poco.MaterialMgr2.site.DownloadMorePageSite;
import cn.poco.brush.BrushPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite1;
import cn.poco.resource.ResType;
import cn.poco.share.site.SharePageSite7;
import cn.poco.utils.Utils;

/**
 * 素材美化-指尖魔法
 */
public class BrushPageSite extends BaseSite {
    public BrushPageSite() {
        super(SiteID.BRUSH);
    }

    @Override
    public IPage MakePage(Context context) {
        return new BrushPage(context, this);
    }

    public void OpenDownloadMore(Context context, ResType type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("type", type);
        MyFramework.SITE_Popup(context, DownloadMorePageSite.class, params, Framework2.ANIM_NONE);
    }

    public void OnLogin(Context context) {
        MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
    }

    /**
     * @param params img:Bitmap
     */
    public void OnBack(Context context, HashMap<String, Object> params) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    /**
     * @param params img:Bitmap<br/>
     */
    public void OnSave(Context context, HashMap<String, Object> params) {
        HashMap<String, Object> temp = new HashMap<>();
        Bitmap bmp = (Bitmap) params.get("img");
        if (bmp != null && !bmp.isRecycled()) {
            String path = FileCacheMgr.GetLinePath();
            if (!Utils.SaveTempImg(bmp, path)) {
                path = null;
            }
            if (path != null) {
                temp.put("img", path);
                temp.put("from_camera", true);
                MyFramework.SITE_Open(context, SharePageSite7.class, temp, Framework2.ANIM_NONE);
            }
        }
    }
}

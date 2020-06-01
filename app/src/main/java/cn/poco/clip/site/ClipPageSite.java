package cn.poco.clip.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.clip.ClipPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;

/**
 * 编辑（裁剪）site
 *
 * @author lmx
 *         Created by lmx on 2016/12/1.
 */

public class ClipPageSite extends BaseSite {

    public ClipPageSite() {
        super(SiteID.CLIP);
    }

    @Override
    public IPage MakePage(Context context) {
        return new ClipPage(context, this);
    }

    /**
     * @param params img:Bitmap
     */
    public void OnBack(Context context, HashMap<String, Object> params) {

    }

    /**
     * @param params img:Bitmap
     */
    public void OnSave(Context context, HashMap<String, Object> params) {
    }
}

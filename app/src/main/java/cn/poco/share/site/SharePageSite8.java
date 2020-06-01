package cn.poco.share.site;

import android.content.Context;

import cn.poco.album.site.AlbumSite65;
import cn.poco.face.FaceDataV2;
import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 一键萌妆
 */
public class SharePageSite8 extends SharePageSite
{
    @Override
    public void OnHome() {
        super.OnHome();
        FaceDataV2.ResetData();
    }

    public void PlayOneMoreTime(Context context)
    {
        MyFramework.SITE_Open(context, AlbumSite65.class, null, Framework2.ANIM_NONE);
    }
}

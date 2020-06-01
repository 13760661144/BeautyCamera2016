package cn.poco.gifEmoji;

import android.content.Context;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * @author lmx
 *         Created by lmx on 2017/11/17.
 */

public class GifEmojiSharePreviewPageSite extends BaseSite
{
    public GifEmojiSharePreviewPageSite()
    {
        super(SiteID.GIF_EMOJI_SHARE_PREVIEW);
    }

    @Override
    public IPage MakePage(Context context)
    {
        return new GifEmojiSharePreviewPage(context, this);
    }

    public void OnBack(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }
}

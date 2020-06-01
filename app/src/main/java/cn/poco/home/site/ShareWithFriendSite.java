package cn.poco.home.site;

import android.content.Context;

import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.home4.userInfoMenu.ShareWithFriendPage;

/**
 * Created by Shine on 2017/8/22.
 */

public class ShareWithFriendSite extends BaseSite{
    public ShareWithFriendSite() {
        super(SiteID.SHAREWITHFRIENDS);
    }

    @Override
    public IPage MakePage(Context context) {
        return new ShareWithFriendPage(context, this);
    }

    public void onBack(Context context) {
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_LEFT);    }


}

package cn.poco.home.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite5;
import cn.poco.banner.BannerCore3;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.home4.BeautyEntryPage;

/**
 * Created by lgd on 2017/12/13.
 */

public class BeautyEntryPageSite extends BaseSite
{

    public HomePageSite.CmdProc m_cmdProc;
    public BeautyEntryPageSite()
    {
        super(SiteID.BEAUTY_ENTRY);
        m_cmdProc = new HomePageSite.CmdProc();
    }

    public void OnBack(Context context)
    {

    }

    @Override
    public IPage MakePage(Context context)
    {
        return new BeautyEntryPage(context,this);
    }

    public void onAlbum(Context context, HashMap<String,Object> params)
    {
     //   params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.LIANGYAN.GetValue());
        params.put("hide_camera_entry",true);
        MyFramework.SITE_Popup(context, AlbumSite5.class, params, Framework2.ANIM_TRANSLATION_TOP);
    }

    public void onBanner(Context context, CampaignInfo campaignInfo) {
        BannerCore3.ExecuteCommand(context, campaignInfo.getOpenUrl(), m_cmdProc, campaignInfo);
    }
}

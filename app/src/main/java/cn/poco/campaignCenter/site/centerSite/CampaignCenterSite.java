package cn.poco.campaignCenter.site.centerSite;

import android.content.Context;

import cn.poco.banner.BannerCore3;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.page.CampaignCenterPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * Created by Shine on 2016/12/16.
 * 运营专区的site,首页用到
 */

public class CampaignCenterSite extends BaseSite{
    public HomePageSite.CmdProc m_cmdProc;
    public final static String SCROLL_POSITION = "scrollPosition";
    public final static String ITEM_OPEN_INDEX = "itemOpenIndex";

    public CampaignCenterSite() {
        super(SiteID.CAMPAIGNCENTER);
        MakeCmdProc();
    }

    @Override
    public IPage MakePage(Context context) {
        return new CampaignCenterPage(context, this);
    }

    public void onBack(Context context) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }


    public void onClickCampaignItem(Context context, CampaignInfo campaignInfo) {
        BannerCore3.ExecuteCommand(context, campaignInfo.getOpenUrl(), m_cmdProc, campaignInfo);
    }

    public void onRestoreState(Context context) {
        if (m_myParams != null && m_myParams.containsKey(ITEM_OPEN_INDEX)) {
            Object value = m_myParams.get(ITEM_OPEN_INDEX);
            m_myParams.remove(ITEM_OPEN_INDEX);
            if (value instanceof CampaignInfo) {
                CampaignInfo lastCampaignInfo = (CampaignInfo) value;
                BannerCore3.ExecuteCommand(context, lastCampaignInfo.getOpenUrl(), m_cmdProc, lastCampaignInfo);
            }
        }
    }




    protected void MakeCmdProc()
    {
        m_cmdProc = new HomePageSite.CmdProc();
    }

}

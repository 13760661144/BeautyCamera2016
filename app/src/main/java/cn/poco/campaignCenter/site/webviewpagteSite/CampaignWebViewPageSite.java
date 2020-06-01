package cn.poco.campaignCenter.site.webviewpagteSite;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite3;
import cn.poco.banner.BannerCore3;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite15;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.page.CampaignCenterWebViewPage;
import cn.poco.filterBeautify.FilterBeautifyProcessor;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;

/**
 * Created by Shine on 2016/12/22.
 * 运营专区详情页面的site,从运营专区外面的item点击进入
 */

public class CampaignWebViewPageSite extends BaseSite{
    public static final String KEY = "CampaignCenter";
    public HomePageSite.CmdProc m_cmdProc;

    public CampaignWebViewPageSite() {
        super(SiteID.CAMPAIGN_WEBVIEW_PAGE);
        MakeCmdProc();
    }

    @Override
    public IPage MakePage(Context context) {
        return new CampaignCenterWebViewPage(context, this);
    }


    public void OnBack(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void onBackWithAnimation(Context context) {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_LEFT);
    }

    public void OnClose(Context context)
    {
        MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
    }

    public void OnCamera(Context context)
    {
        //只有拍照
        HashMap<String, Object> datas = new HashMap<>();
        datas.put(CameraSetDataKey.KEY_START_MODE, 1);
        datas.putAll(CameraSetDataKey.GetWayTakePicture(false, true, FilterBeautifyProcessor.FILTER_BEAUTY));
        MyFramework.SITE_Popup(context, CameraPageSite15.class, datas, Framework2.ANIM_NONE);
    }

    public void OnSelPhoto(Context context)
    {
        HashMap<String, Object> datas = new HashMap<>();
        datas.put("from_camera", true);
        MyFramework.SITE_Popup(context, AlbumSite3.class, datas, Framework2.ANIM_NONE);
    }

    public void OnTryNow(Context context, CampaignInfo campaignInfo) {
        if (campaignInfo != null) {
            BannerCore3.ExecuteCommand(context, campaignInfo.getTryUrl(), m_cmdProc, KEY);
        }
    }

    protected void MakeCmdProc() {
        m_cmdProc = new HomePageSite.CmdProc();
    }

    public static boolean HasKey(String... args)
    {
        boolean out = false;

        if(args != null)
        {
            for(String temp : args)
            {
                if(temp != null && temp.equals(KEY))
                {
                    out = true;
                    break;
                }
            }
        }

        return out;
    }
}

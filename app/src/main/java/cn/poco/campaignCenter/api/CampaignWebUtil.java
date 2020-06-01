package cn.poco.campaignCenter.api;

import org.json.JSONObject;

import cn.poco.pocointerfacelibs.PocoWebUtils;

/**
 * Created by Shine on 2016/11/29.
 */

public class CampaignWebUtil{

    public static CampaignApi getCampaignInfoFromServer(String groupPosition, String pageIndex, String pageCount, ICampaign iCampaign) {
        CampaignApi api = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("position", groupPosition);
            jsonObject.put("page", pageIndex);
            jsonObject.put("page_count", pageCount);
            api = (CampaignApi) PocoWebUtils.Get(CampaignApi.class, iCampaign.getCampaignInfo(), false, jsonObject, null, iCampaign);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return api;
    }








}

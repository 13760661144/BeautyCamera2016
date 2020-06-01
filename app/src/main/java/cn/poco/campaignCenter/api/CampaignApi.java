package cn.poco.campaignCenter.api;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.pocointerfacelibs.AbsBaseInfo;

/**
 * Created by Shine on 2016/11/29.
 */

public class CampaignApi extends AbsBaseInfo {
    public List<CampaignInfo> mAutoSlideListData = new ArrayList<>();
    public List<CampaignInfo> mDisplayListData = new ArrayList<>();
    public List<CampaignInfo> mCampaignListData = new ArrayList<>();
    public List<CampaignInfo> mCustomBeautyData = new ArrayList<>();
    public List<CampaignInfo> mLiveData = new ArrayList<>();
    public int mFirstSectionCount, mSecondSectionCount, mThridSectionCount, mFourthSectionCount;

    private static final int ServerDataType = 5;
    public static final int Has_Position1 = 1 << 0;
    public static final int Has_Position2 = 1 << 1;
    public static final int Has_Position3 = 1 << 2;
    public static final int Has_Position4 = 1 << 3;
    public static final int Has_Position5 = 1 << 4;

    public int flags;

    public String json;

    @Override
    public boolean DecodeData(String str)
    {
        json = str;
        return super.DecodeData(str);
    }

    @Override
    protected boolean DecodeMyData(Object object) throws Throwable {
        JSONObject jsonObject = (JSONObject) object;
        JSONObject result = jsonObject.getJSONObject("ret_data");
//        Log.i("showJson", result.toString());

        for (int i = 1; i <= ServerDataType; i++) {
            boolean hasKey = result.has("position_" + i);
            String keyString = hasKey ? result.getString("position_" + i) : null;
            if (TextUtils.isEmpty(keyString)) {
                continue;
            }
            JSONArray layerResult = result.getJSONArray("position_" + i);

            for (int a = 0; a < layerResult.length(); a++) {
                JSONObject currentJsonObject = layerResult.getJSONObject(a);
                CampaignInfo campaignInfo = null;
                if (currentJsonObject.has("type")) {
                    String type = currentJsonObject.getString("type");

                    if (type.equals("1")) {
                        campaignInfo = new CampaignInfo.BusinessArticleInfo();
                        campaignInfo.setCampaignType(CampaignInfo.CampaignType.BusinessArticle);
                    } else if (type.equals("2")){
                        campaignInfo = new CampaignInfo.VideoInfo();
                        campaignInfo.setCampaignType(CampaignInfo.CampaignType.Vedio);
                    } else if (type.equals("3")) {
                        campaignInfo = new CampaignInfo.FeatureInfo();
                        campaignInfo.setCampaignType(CampaignInfo.CampaignType.Feature);
                    } else if (type.equals("4")) {
                        campaignInfo = new CampaignInfo.Topic();
                        campaignInfo.setCampaignType(CampaignInfo.CampaignType.Topic);
                    } else if (type.equals("5")) {
                        campaignInfo = new CampaignInfo.AdInfo();
                        campaignInfo.setCampaignType(CampaignInfo.CampaignType.Bussiness);
                    }
                }

                if (campaignInfo != null) {
                    if (currentJsonObject.has("img_url")) {
                        campaignInfo.setCoverUrl(currentJsonObject.getString("img_url"));
                    }

                    if (currentJsonObject.has("id")) {
                        campaignInfo.setId(currentJsonObject.getString("id"));
                    }

                    if (currentJsonObject.has("url")) {
                        campaignInfo.setOpenUrl(currentJsonObject.getString("url"));
                    }

                    if (currentJsonObject.has("position")) {
                        campaignInfo.setPosition(currentJsonObject.getString("position"));
                    }

                    if (currentJsonObject.has("sort")) {
                        campaignInfo.setSort(currentJsonObject.getString("sort"));
                    }

                    if (currentJsonObject.has("title")) {
                        campaignInfo.setTitle(currentJsonObject.getString("title"));
                    }

                    if (currentJsonObject.has("tj_id")) {
                        campaignInfo.setStatisticId(currentJsonObject.getString("tj_id"));
                    }

                    if (currentJsonObject.has("share_link")) {
                        campaignInfo.setShareLink(currentJsonObject.getString("share_link"));
                    }

                    if (currentJsonObject.has("share_title")) {
                        campaignInfo.setShareTitle(currentJsonObject.getString("share_title"));
                    }

                    if (currentJsonObject.has("share_img")) {
                        campaignInfo.setShareImg(currentJsonObject.getString("share_img"));
                    }

                    if (currentJsonObject.has("share_desc")) {
                        campaignInfo.setShareDescription(currentJsonObject.getString("share_desc"));
                    }

                    if (currentJsonObject.has("try_url")) {
                        campaignInfo.setTryUrl(currentJsonObject.getString("try_url"));
                    }

                    if (currentJsonObject.has("try_tj_id")) {
                        campaignInfo.setTryNowId(currentJsonObject.getString("try_tj_id"));
                    }

                    if (currentJsonObject.has("share_tj_id")) {
                        campaignInfo.setShareIconId(currentJsonObject.getString("share_tj_id"));
                    }
                }

                if (currentJsonObject.has("try_title")) {
                    campaignInfo.setTryTitle(currentJsonObject.getString("try_title"));
                }

                if (currentJsonObject.has("business_try_tj")) {
                    campaignInfo.setBusinessTryUrl(currentJsonObject.getString("business_try_tj"));
                }

                if (currentJsonObject.has("tj_url")) {
                    campaignInfo.setBannerTjUrl(currentJsonObject.getString("tj_url"));
                }

                if (i == 1) {
                    flags |= Has_Position1;
                    mAutoSlideListData.add(campaignInfo);
                } else if (i == 2) {
                    flags |= Has_Position2;
                    mDisplayListData.add(campaignInfo);
                } else if (i == 3) {
                    flags |= Has_Position3;
                    mCampaignListData.add(campaignInfo);
                } else if (i == 4) {
                    flags |= Has_Position4;
                    mCustomBeautyData.add(campaignInfo);
                }else if(i ==  5){
                    flags |= Has_Position5;
                    mLiveData.add(campaignInfo);
                }
            }
       }

        boolean has = result.has("position_1_count");
        if (has) {
            mFirstSectionCount = result.getInt("position_1_count");
        }

        has = result.has("position_2_count");
        if (has) {
            mSecondSectionCount = result.getInt("position_2_count");
        }

        has = result.has("position_3_count");
        if (has) {
            mThridSectionCount = result.getInt("position_3_count");
        }

        has = result.has("position_4_count");
        if (has) {
            mFourthSectionCount = result.getInt("position_4_count");
        }
        return true;
    }
}

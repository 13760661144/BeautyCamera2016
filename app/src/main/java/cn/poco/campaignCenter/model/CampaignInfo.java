package cn.poco.campaignCenter.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/14.
 */

public class CampaignInfo implements Cloneable, Serializable{

    public enum CampaignType {
        BusinessArticle("1"),
        Vedio("2"),
        Feature("3"),
        Topic("4"),
        Bussiness("5");

        String serverType;

        public static CampaignType getCampaignTypeByValue(String value) {
            if (value.equals("1")) {
                return BusinessArticle;
            } else if (value.equals("2")) {
                return Vedio;
            } else if (value.equals("3")) {
                return Feature;
            } else if (value.equals("4")) {
                return Topic;
            } else {
                return Bussiness;
            }
        }

        CampaignType(String type) {
            this.serverType = type;
        }

        public String getServerType() {
            return serverType;
        }
    }

    private String mId;
    private String mCoverUrl;
    private String mPosition;
    private String mSort;
    private String mOpenUrl;
    private String mTitle;
    private CampaignType mType;
    private String mStatisticId;
    private String mShareLink;
    private String mShareTitle;
    private String mShareDescription;
    private String mShareImg;
    private String mTryUrl;
    private String mTryTitle;
    private String mCacheImgNormal;
    private String mCacheImgForTwiter;

    private String mTryNowId;
    private String mShareIconId;
    private String mBusinessTryUrl;
    private String mBannerTjUrl;


    public void setId(String id) {
        this.mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setCoverUrl(String url) {
        this.mCoverUrl = url;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setPosition(String position) {
        this.mPosition = position;
    }

    public String getPosition() {
        return mPosition;
    }

    public void setSort(String sort) {
        this.mSort = sort;
    }

    public String getSort() {
        return mSort;
    }

    public void setOpenUrl(String openUrl) {
        this.mOpenUrl = openUrl;
    }

    public String getOpenUrl() {
        return mOpenUrl;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setCampaignType(CampaignType type) {
        this.mType = type;
    }

    public CampaignType getCampaignType() {
        return mType;
    }

    public void setStatisticId(String statisticId) {
        this.mStatisticId = statisticId;
    }

    public String getStatisticId() {
        return mStatisticId;
    }

    public void setShareLink(String link) {
        this.mShareLink = link;
    }

    public String getShareLink() {
        return mShareLink;
    }

    public void setShareTitle(String shareTitle) {
        this.mShareTitle = shareTitle;
    }

    public String getShareTitle() {
        return mShareTitle;
    }

    public void setShareDescription(String shareDescription) {
        this.mShareDescription = shareDescription;
    }

    public String getShareDescription() {
        return mShareDescription;
    }

    public void setShareImg(String img) {
        this.mShareImg = img;
    }

    public String getShareImg() {
        return mShareImg;
    }

    public void setCacheImgPath(String path) {
        this.mCacheImgNormal = path;
    }

    public void setCacheImgForTwiter(String path) {
        this.mCacheImgForTwiter = path;
    }

    public String getCacheImgPath() {
        return mCacheImgNormal;
    }

    public String getTwitterCacheImg() {
        return mCacheImgForTwiter;
    }


    public void setTryUrl(String url) {
        this.mTryUrl = url;
    }

    public String getTryUrl() {
        return mTryUrl;
    }

    public void setTryNowId(String id) {
        this.mTryNowId = id;
    }

    public String getTryNowId() {
        return mTryNowId;
    }

    public void setShareIconId(String id) {
        this.mShareIconId = id;
    }

    public String getShareIconId() {
        return mShareIconId;
    }

    public void setTryTitle(String title) {
        this.mTryTitle = title;
    }

    public String getTryTitle() {
        return mTryTitle;
    }

    public void setBusinessTryUrl(String url) {
        this.mBusinessTryUrl = url;
    }

    public String getBusinessTryUrl() {
       return mBusinessTryUrl;
    }

    public void setBannerTjUrl(String url) {
        this.mBannerTjUrl = url;
    }

    public String getBannerTjUrl() {
        return mBannerTjUrl;
    }


    @Override
    public CampaignInfo clone() {
        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCoverUrl(this.getCoverUrl());
        campaignInfo.setTitle(this.getTitle());
        campaignInfo.setCampaignType(this.getCampaignType());
        campaignInfo.setId(this.getId());
        campaignInfo.setOpenUrl(this.getOpenUrl());
        campaignInfo.setPosition(this.getPosition());
        campaignInfo.setSort(this.getSort());
        campaignInfo.setStatisticId(this.getStatisticId());
        campaignInfo.setShareDescription(this.getShareDescription());
        campaignInfo.setShareTitle(this.getShareTitle());
        campaignInfo.setShareLink(this.getShareLink());
        campaignInfo.setShareImg(this.getShareImg());
        campaignInfo.setCacheImgPath(this.getCacheImgPath());
        campaignInfo.setCacheImgForTwiter(this.getTwitterCacheImg());
        campaignInfo.setTryUrl(this.getTryUrl());
        campaignInfo.setTryTitle(this.getTryTitle());
        campaignInfo.setTryNowId(this.getTryNowId());
        campaignInfo.setShareIconId(this.getShareIconId());
        campaignInfo.setBusinessTryUrl(this.getBusinessTryUrl());
        campaignInfo.setBannerTjUrl(this.getBannerTjUrl());
        return campaignInfo;
    }

    public static CampaignInfo decodeJsonAndMakeCampaignInfo(String json) {
        CampaignInfo campaignInfo = null;
        try {
            campaignInfo = new CampaignInfo();
            JSONObject jsonObject = new JSONObject(json);
            campaignInfo.setTitle(jsonObject.getString("title"));
            campaignInfo.setOpenUrl(jsonObject.getString("url"));
            campaignInfo.setCoverUrl(jsonObject.getString("img_url"));
            campaignInfo.setTryUrl(jsonObject.getString("try_url"));
            campaignInfo.setBusinessTryUrl(jsonObject.getString("business_try_tj"));
            campaignInfo.setTryNowId(jsonObject.getString("try_tj_id"));
            campaignInfo.setShareLink(jsonObject.getString("share_link"));
            campaignInfo.setShareTitle(jsonObject.getString("share_title"));
            campaignInfo.setShareDescription(jsonObject.getString("share_desc"));
            campaignInfo.setShareImg(jsonObject.getString("share_img"));
            campaignInfo.setShareIconId(jsonObject.getString("share_tj_id"));
            campaignInfo.setStatisticId("tj_id");
            campaignInfo.setBannerTjUrl("tj_url");
            campaignInfo.setTryTitle(jsonObject.getString("try_title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return campaignInfo;
    }



    public static class AdInfo extends CampaignInfo {

    }

    public static class VideoInfo extends CampaignInfo {

    }

    public static class BusinessArticleInfo extends CampaignInfo {

    }


    public static class FeatureInfo extends CampaignInfo {

    }

    public static class Topic extends CampaignInfo {


    }

}

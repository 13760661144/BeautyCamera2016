package cn.poco.filterPendant;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.resource.BaseRes;
import cn.poco.resource.GlassRecommendResMgr2;
import cn.poco.resource.GlassRes;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.RecommendRes;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.widget.recycle.RecommendAdapter;

/**
 * Created by lgd on 2017/7/4.
 */

public class FilterPendantResMgr {

    public static boolean isPendantNewFlag = false;
    public static boolean isShapeNewFlag = false;


    public static RecommendAdapter.RecommendItemInfo getRecommdInfo(Context context)
    {
        Object[] logos = null;
        String[] names = null;
        ArrayList<RecommendRes> arr = null;
        RecommendAdapter.RecommendItemInfo tempInfo = null;
        //下载按钮
        ArrayList<RecommendRes> tempArr = GlassRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
        if (tempArr != null && tempArr.size() > 0) {
            arr = new ArrayList<>();
            for (RecommendRes temp : tempArr) {
                if (MgrUtils.hasDownloadGlass(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.ADV_RECO_GLASS_FLAG + temp.m_id)) {
                    arr.add(temp);
                }
            }
            int len = arr.size();
            if (len > 0) {
                logos = new Object[len];
                names = new String[len];
                for (int i = 0; i < len; i++) {
                    RecommendRes recommendRes = arr.get(i);
                    Object thumb = recommendRes.m_thumb;
                    if (thumb == null && !TextUtils.isEmpty(recommendRes.url_thumb)) {
                        thumb = recommendRes.url_thumb;
                    }
                    logos[i] = thumb;
                    names[i] = recommendRes.m_name;
                }
                //推荐按钮
                tempInfo = new RecommendAdapter.RecommendItemInfo();
                tempInfo.m_ex = arr;
                ((RecommendAdapter.RecommendItemInfo) tempInfo).setLogo(logos[0], names[0]);
            }
        }
        return tempInfo;
    }



    public static ArrayList<RecommendAdapter.ItemInfo> getRess(Context context,int glassType) {
        ArrayList<RecommendAdapter.ItemInfo> out = new ArrayList<>();
        RecommendAdapter.ItemInfo tempInfo;

        ArrayList<RecommendRes> arr = null;

        Object[] logos = null;
        String[] names = null;

        boolean isHasRecommend = false;
        //下载按钮
        tempInfo = new RecommendAdapter.DownloadItemInfo();
        ((RecommendAdapter.DownloadItemInfo) tempInfo).setNum(GlassResMgr2.getInstance().GetNoDownloadedCount(context));
        ArrayList<RecommendRes> tempArr = GlassRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
        if (tempArr != null && tempArr.size() > 0) {
            arr = new ArrayList<>();
            for (RecommendRes temp : tempArr) {
                if (MgrUtils.hasDownloadGlass(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.ADV_RECO_GLASS_FLAG + temp.m_id)) {
                    arr.add(temp);
                }
            }
            int len = arr.size();
            if (len > 0) {
                logos = new Object[len];
                names = new String[len];
                for (int i = 0; i < len; i++) {
                    RecommendRes recommendRes = arr.get(i);
                    Object thumb = recommendRes.m_thumb;
                    if (thumb == null && !TextUtils.isEmpty(recommendRes.url_thumb)) {
                        thumb = recommendRes.url_thumb;
                    }
                    logos[i] = thumb;
                    names[i] = recommendRes.m_name;
                }
                isHasRecommend = true;
            }
        }
        ((RecommendAdapter.DownloadItemInfo) tempInfo).setLogos(logos);
        out.add(tempInfo);
        if(isHasRecommend){
            //推荐按钮
            tempInfo = new RecommendAdapter.RecommendItemInfo();
            tempInfo.m_ex = arr;
            ((RecommendAdapter.RecommendItemInfo) tempInfo).setLogo(logos[0], names[0]);
            out.add(tempInfo);
        }


        ArrayList<GlassRes> m_allResArr = GlassResMgr2.getInstance().GetResArr();
        ArrayList<GlassRes> resArr = new ArrayList<>();
        if (m_allResArr != null) {
            for (GlassRes temp : m_allResArr) {
                if (temp.m_glassType == glassType) {
                    resArr.add(temp);
                }
            }
        }
        for (GlassRes res : resArr) {
            if (res != null) {
                tempInfo = new RecommendAdapter.ItemInfo();
                tempInfo.m_canDrag = true;
                tempInfo.m_logo = res.m_thumb;
                tempInfo.m_name = res.m_name;
                tempInfo.m_uri = res.m_id;
                tempInfo.m_ex = res;
                switch (res.m_type) {
                    case BaseRes.TYPE_NETWORK_URL:
                        //没下载,排队中,下载中
                        tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEED_DOWNLOAD;
                        break;
                    case BaseRes.TYPE_LOCAL_RES:
                    case BaseRes.TYPE_LOCAL_PATH:
                    default:
                        tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NORMAL;
                        break;
                }
                if (GlassResMgr2.getInstance().IsNewRes(tempInfo.m_uri)) {
                    tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEW;
                    if (res.m_glassType == GlassRes.GLASS_TYPE_SHAPE) {
                        isShapeNewFlag = true;
                    }else{
                        isPendantNewFlag = true;
                    }
//                    isPendantNewFlag = true;
                }
                out.add(tempInfo);
            }
        }
        return out;
    }

    public static ArrayList<RecommendAdapter.ItemInfo> getShapeRess(Context context) {
        ArrayList<RecommendAdapter.ItemInfo> out = new ArrayList<>();
        RecommendAdapter.ItemInfo tempInfo;

        ArrayList<RecommendRes> arr = null;

        //下载按钮
        tempInfo = new RecommendAdapter.DownloadItemInfo();
        ((RecommendAdapter.DownloadItemInfo) tempInfo).setNum(GlassResMgr2.getInstance().GetNoDownloadedCount(context));
        out.add(tempInfo);

        Object[] logos = null;
        String[] names = null;
        ArrayList<RecommendRes> tempArr = GlassRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
        if (tempArr != null && tempArr.size() > 0) {
            arr = new ArrayList<>();
            for (RecommendRes temp : tempArr) {
                if (MgrUtils.hasDownloadGlass(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.ADV_RECO_GLASS_FLAG + temp.m_id)) {
                    arr.add(temp);
                }
            }
            int len = arr.size();
            if (len > 0) {
                logos = new Object[len];
                names = new String[len];
                for (int i = 0; i < len; i++) {
                    RecommendRes recommendRes = arr.get(i);
                    Object thumb = recommendRes.m_thumb;
                    if (thumb == null && !TextUtils.isEmpty(recommendRes.url_thumb)) {
                        thumb = recommendRes.url_thumb;
                    }
                    logos[i] = thumb;
                    names[i] = recommendRes.m_name;
                }
                //推荐按钮
                tempInfo = new RecommendAdapter.RecommendItemInfo();
                tempInfo.m_ex = arr;
                ((RecommendAdapter.RecommendItemInfo) tempInfo).setLogo(logos[0], names[0]);
                out.add(tempInfo);
            }
        }

        ArrayList<GlassRes> m_allResArr = GlassResMgr2.getInstance().GetResArr();
        ArrayList<GlassRes> resArr = new ArrayList<>();
        if (m_allResArr != null) {
            for (GlassRes temp : m_allResArr) {
                if (temp.m_glassType == GlassRes.GLASS_TYPE_SHAPE) {
                    resArr.add(temp);
                }
            }
        }
        for (GlassRes res : resArr) {
            if (res != null) {
                tempInfo = new RecommendAdapter.ItemInfo();
                tempInfo.m_logo = res.m_thumb;
                tempInfo.m_name = res.m_name;
                tempInfo.m_uri = res.m_id;
                tempInfo.m_ex = res;
                switch (res.m_type) {
                    case BaseRes.TYPE_NETWORK_URL:
                        //没下载,排队中,下载中
                        tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEED_DOWNLOAD;
                        break;
                    case BaseRes.TYPE_LOCAL_RES:
                    case BaseRes.TYPE_LOCAL_PATH:
                    default:
                        tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NORMAL;
                        break;
                }
                if (GlassResMgr2.getInstance().IsNewRes(tempInfo.m_uri)) {
                    tempInfo.m_style = RecommendAdapter.ItemInfo.Style.NEW;
                    if (res.m_glassType == GlassRes.GLASS_TYPE_SHAPE) {
                        isShapeNewFlag = true;
                    }else{
                        isPendantNewFlag = true;
                    }
                }
                out.add(tempInfo);
            }
        }
        return out;
    }


    public static int getResFirstUri(ArrayList<RecommendAdapter.ItemInfo> arr) {
        int out = RecommendAdapter.ItemInfo.URI_NONE;
        if (arr != null) {
            for (RecommendAdapter.ItemInfo info : arr) {
                if (info.m_uri != RecommendAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI && info.m_uri != RecommendAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI) {
                    out = info.m_uri;
                    break;
                }
            }
        }
        return out;
    }
}

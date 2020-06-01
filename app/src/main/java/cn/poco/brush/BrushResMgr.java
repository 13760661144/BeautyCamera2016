package cn.poco.brush;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.resource.BaseRes;
import cn.poco.resource.BrushGroupRes;
import cn.poco.resource.BrushRecommendResMgr2;
import cn.poco.resource.BrushRes;
import cn.poco.resource.RecommendRes;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.widget.recycle.RecommendExAdapter;

public class BrushResMgr
{
    public static RecommendExAdapter.RecommendItemInfo getRecommendInfo(Context context)
    {
        ArrayList<RecommendRes> arr = null;
        Object[] logos = null;
        String[] names = null;
        RecommendExAdapter.RecommendItemInfo tempInfo = null;
        ArrayList<RecommendRes> tempArr = BrushRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
        if (tempArr != null && tempArr.size() > 0)
        {
            arr = new ArrayList<>();
            int len = tempArr.size();
            RecommendRes temp;
            for (int i = 0; i < len; i++)
            {
                temp = tempArr.get(i);
                if (MgrUtils.hasDownloadBrush(context, temp.m_id) == 0 && TagMgr.CheckTag(context, Tags.ADV_RECO_BRUSH_FLAG + temp.m_id))
                {
                    arr.add(temp);
                }
            }
            len = arr.size();
            if (len > 0)
            {
                logos = new Object[len];
                names = new String[len];
                for (int i = 0; i < len; i++)
                {
                    temp = arr.get(i);
                    Object thumb = temp.m_thumb;
                    if (thumb == null && !TextUtils.isEmpty(temp.url_thumb)) {
                        thumb = temp.url_thumb;
                    }
                    logos[i] = thumb;
                    names[i] = temp.m_name;
                }
                //推荐位
                tempInfo = new RecommendExAdapter.RecommendItemInfo();
                tempInfo.m_ex = arr;
                tempInfo.setLogo(logos, names);
            }
        }
        return tempInfo;
    }

    public static ArrayList<RecommendExAdapter.ItemInfo> GetBrushRes(Context context)
    {
        ArrayList<RecommendExAdapter.ItemInfo> out = new ArrayList<>();

        RecommendExAdapter.ItemInfo tempInfo;
        ArrayList<RecommendRes> arr = null;

        //下载按钮
        tempInfo = new RecommendExAdapter.DownloadItemInfo();
        ((RecommendExAdapter.DownloadItemInfo) tempInfo).setNum(cn.poco.resource.BrushResMgr2.getInstance().GetNoDownloadCount(context));
        out.add(tempInfo);

        RecommendExAdapter.RecommendItemInfo recommendInfo = getRecommendInfo(context);
        if (recommendInfo != null)
        {
            ((RecommendExAdapter.DownloadItemInfo) tempInfo).setLogos(recommendInfo.m_logos);
            ((RecommendExAdapter.DownloadItemInfo) tempInfo).setNames(recommendInfo.m_names);
            out.add(recommendInfo);
        }

        Object[] logos = null;
        String[] names = null;
        ArrayList<BrushGroupRes> resArr = cn.poco.resource.BrushResMgr2.getInstance().GetGroupResArr();
        for (BrushGroupRes res : resArr)
        {
            if (res != null)
            {
                int len2 = 1;
                if (res.m_group != null)
                {
                    len2 = res.m_group.size() + 1;
                }
                int[] uris = new int[len2];
                logos = new Object[len2];
                names = new String[len2];
                uris[0] = res.m_id;
                logos[0] = res.m_thumb;
                names[0] = res.m_name;
                if (res.m_group != null)
                {
                    BrushRes temp3;
                    for (int j = 1; j < len2; j++)
                    {
                        temp3 = res.m_group.get(j - 1);
                        uris[j] = temp3.m_id;
                        logos[j] = temp3.m_thumb;
                        names[j] = temp3.m_name;
                    }
                }
                tempInfo = new RecommendExAdapter.ItemInfo();
                tempInfo.m_uri = res.m_id;
                tempInfo.setData(uris, logos, names, res);
                tempInfo.m_canDrag = true;
                switch (res.m_type)
                {
                    case BaseRes.TYPE_NETWORK_URL:
                        tempInfo.m_style = RecommendExAdapter.ItemInfo.Style.NEED_DOWNLOAD;
                        break;
                    case BaseRes.TYPE_LOCAL_RES:
                    case BaseRes.TYPE_LOCAL_PATH:
                    default:
                        tempInfo.m_style = RecommendExAdapter.ItemInfo.Style.NORMAL;
                        break;
                }
                if (cn.poco.resource.BrushResMgr2.getInstance().IsNewGroup(res.m_id))
                {
                    tempInfo.m_style = RecommendExAdapter.ItemInfo.Style.NEW;
                }
                out.add(tempInfo);
            }
        }

        return out;
    }

    public static int GetFirstUri(ArrayList<RecommendExAdapter.ItemInfo> arr)
    {
        int out = 0;

        if (arr != null)
        {
            for (RecommendExAdapter.ItemInfo info : arr)
            {
                if (info.m_uri != RecommendExAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI || info.m_uri != RecommendExAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)
                {
                    if (info.m_uris != null && info.m_uris.length > 1)
                    {
                        out = info.m_uris[1];
                        break;
                    }
                }
            }
        }

        return out;
    }
}

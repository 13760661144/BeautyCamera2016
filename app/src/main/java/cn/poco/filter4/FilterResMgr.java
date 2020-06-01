package cn.poco.filter4;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.filter4.recycle.FilterAdapter;
import cn.poco.resource.BaseRes;
import cn.poco.resource.FilterGroupRes;
import cn.poco.resource.FilterRecommendResMgr2;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.LockRes;
import cn.poco.resource.RecommendRes;
import cn.poco.resource.ResourceUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;

/**
 * @author lmx
 *         Created by lmx on 2017/3/28.
 */

public class FilterResMgr
{
    public static ArrayList<FilterAdapter.ItemInfo> GetFilterRes(Context context, boolean openFromCamera)
    {
        return GetFilterRes(context, openFromCamera, true);
    }

    /**
     *
     * @param context
     * @param includeLock 是否包含加锁素材
     * @return
     */
    public static FilterAdapter.RecommendItemInfo getRecommendInfo(Context context, boolean includeLock)
    {
        ArrayList<RecommendRes> arr = null;
        Object[] logos = null;
        String[] names = null;
        FilterAdapter.RecommendItemInfo tempInfo = null;
        ArrayList<RecommendRes> tempArr = FilterRecommendResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
        if (tempArr != null && tempArr.size() > 0)
        {
            arr = new ArrayList<>();
            int len = tempArr.size();
            RecommendRes temp;
            for (int i = 0; i < len; i++)
            {
                temp = tempArr.get(i);
                if (MgrUtils.hasDownloadFilter(context, temp.m_id) == 0
                        && TagMgr.CheckTag(context, Tags.ADV_RECO_FILTER_FLAG + temp.m_id))
                {
                    if (includeLock)
                    {
                        arr.add(temp);
                    }
                    else
                    {
                        LockRes lockRes = MgrUtils.unLockTheme(temp.m_id);
                        if (lockRes == null) {
                            arr.add(temp);
                        }
                    }
                }
            }
            len = arr.size();
            if (len > 0)
            {
                logos = new Object[len];
                names = new String[len];
                int[] bkColors = new int[len];
                for (int i = 0; i < len; i++)
                {
                    temp = arr.get(i);
                    //暂时没有加载到微缩图，用url替代先
                    Object thumb = temp.m_thumb;
                    if (thumb == null && !TextUtils.isEmpty(temp.url_thumb))
                    {
                        thumb = temp.url_thumb;
                    }
                    logos[i] = thumb;
                    names[i] = temp.m_name;
                    bkColors[i] = temp.m_bkColor;
                }
                int recommendColor = bkColors[0];
                //额外推荐位置
                tempInfo = new FilterAdapter.RecommendItemInfo();
                tempInfo.setLogo(logos, names, recommendColor);
                tempInfo.m_ex = arr;
            }
        }
        return tempInfo;

    }

    public static FilterAdapter.RecommendItemInfo getRecommendInfo(Context context)
    {
        return getRecommendInfo(context, true);
    }

    /**
     * @param context
     * @param openFromCamera 是否相机page
     * @param isNeedBlurDark 是否需要虚化暗角
     * @return
     */
    public static ArrayList<FilterAdapter.ItemInfo> GetFilterRes(Context context, boolean openFromCamera, boolean isNeedBlurDark)
    {

        ArrayList<FilterAdapter.ItemInfo> out = new ArrayList<>();
        FilterAdapter.ItemInfo tempInfo;
        ArrayList<RecommendRes> arr = null;

        Object[] logos = null;
        String[] names = null;


        boolean hasRecommend = false;

        //模糊 暗角
        if (isNeedBlurDark)
        {
            tempInfo = new FilterAdapter.HeadItemInfo();
            out.add(tempInfo);
        }

        //下载
        tempInfo = new FilterAdapter.DownloadItemInfo();
        ((FilterAdapter.DownloadItemInfo) tempInfo).setNum(cn.poco.resource.FilterResMgr2.getInstance().GetNoDownloadCount());
        out.add(tempInfo);

        //原图
        tempInfo = new FilterAdapter.OriginalItemInfo();
        tempInfo.m_ex = ResourceUtils.GetItem(FilterResMgr2.getInstance().sync_GetLocalRes(context, null), 0);
        out.add(tempInfo);

        tempInfo = getRecommendInfo(context);
        if (tempInfo != null)
        {
            hasRecommend = true;
            out.add(tempInfo);
        }

        //商业临时arr，放到原图后
        ArrayList<FilterAdapter.ItemInfo> tempBussinessArr = new ArrayList<>();

        ArrayList<FilterGroupRes> resArr = cn.poco.resource.FilterResMgr2.getInstance().GetGroupResArr();
        for (int i = 0, size = resArr.size(); i < size; i++)
        {
            FilterGroupRes res = resArr.get(i);

            if (res != null)
            {
                int len2 = 1;
                if (res.m_group != null)
                {
                    len2 = res.m_group.size() + 1;
                }

                //判断是否更新到camera
                ArrayList<FilterRes> mTempList = new ArrayList<>();
                if (openFromCamera)
                {
                    for (int j = 1; j < len2; j++)
                    {
                        FilterRes filterRes = res.m_group.get(j - 1);
                        if (filterRes.m_isUpDateToCamera)
                        {
                            mTempList.add(filterRes);
                        }
                    }
                    if (mTempList.size() == 0)
                    {
                        continue;
                    }
                    else
                    {
                        len2 = mTempList.size() + 1;
                    }
                    res.m_group.clear();
                    res.m_group.addAll(mTempList);
                }

                int[] uris = new int[len2];
                logos = new Object[len2];
                names = new String[len2];
                uris[0] = res.m_id;
                logos[0] = res.m_thumb;
                names[0] = res.m_name;

                if (openFromCamera)
                {
                    FilterRes temp;
                    for (int k = 0; k < mTempList.size(); k++)
                    {
                        temp = mTempList.get(k);
                        uris[k + 1] = temp.m_id;
                        logos[k + 1] = temp.m_thumb;
                        names[k + 1] = temp.m_name;
                    }
                }
                else
                {
                    if (res.m_group != null)
                    {
                        FilterRes temp;
                        for (int j = 1; j < len2; j++)
                        {
                            temp = res.m_group.get(j - 1);
                            uris[j] = temp.m_id;
                            logos[j] = temp.m_thumb;
                            names[j] = temp.m_name;
                        }
                    }
                }

                tempInfo = new FilterAdapter.ItemInfo();
                tempInfo.m_canDrag = true;
                tempInfo.m_uri = res.m_id;
                tempInfo.setData(uris, logos, names, res, res.m_maskColor);
                switch (res.m_type)
                {
                    case BaseRes.TYPE_NETWORK_URL:
                        tempInfo.m_style = FilterAdapter.ItemInfo.Style.NEED_DOWNLOAD;
                        break;
                    case BaseRes.TYPE_LOCAL_RES:
                    case BaseRes.TYPE_LOCAL_PATH:
                    default:
                        tempInfo.m_style = FilterAdapter.ItemInfo.Style.NORMAL;
                        break;
                }
                if (cn.poco.resource.FilterResMgr2.getInstance().IsNewGroup(res.m_id))
                {
                    tempInfo.m_style = FilterAdapter.ItemInfo.Style.NEW;
                }


                //商业放到最前
                if (res.m_isBusiness)
                {
                    tempInfo.m_canDrag = false;
                    tempBussinessArr.add(tempInfo);
                }
                else
                {
                    out.add(tempInfo);
                }
            }
        }
        out.get(out.size() - 1).isDrawLine = false;

        if (tempBussinessArr.size() > 0)
        {
            //注意插入的下标位置，插到推荐位后
            if (isNeedBlurDark)
            {
                if (hasRecommend)
                {
                    out.addAll(4, tempBussinessArr);
                }
                else
                {
                    out.addAll(3, tempBussinessArr);

                }
            }
            else
            {
                if (hasRecommend)
                {
                    out.addAll(3, tempBussinessArr);
                }
                else
                {
                    out.addAll(2, tempBussinessArr);
                }
            }
        }
        return out;
    }

    /**
     * @param context
     * @param openFromCamera 是否相机page
     * @return
     */
    public static ArrayList<FilterAdapter.ItemInfo> GetLiveFilterRes(Context context, boolean openFromCamera)
    {

        ArrayList<FilterAdapter.ItemInfo> out = new ArrayList<>();
        FilterAdapter.ItemInfo tempInfo;
        ArrayList<RecommendRes> arr = null;

        Object[] logos = null;
        String[] names = null;


        boolean hasRecommend = false;

        //下载
        tempInfo = new FilterAdapter.DownloadItemInfo();
        ((FilterAdapter.DownloadItemInfo) tempInfo).setNum(cn.poco.resource.FilterResMgr2.getInstance().GetNoDownloadCount());
        out.add(tempInfo);

        //原图
        tempInfo = new FilterAdapter.OriginalItemInfo();
        tempInfo.m_ex = ResourceUtils.GetItem(FilterResMgr2.getInstance().sync_GetLocalRes(context, null), 0);
        out.add(tempInfo);

        tempInfo = getRecommendInfo(context, false);
        if (tempInfo != null)
        {
            hasRecommend = true;
            out.add(tempInfo);
        }

        //商业临时arr，放到原图后
        ArrayList<FilterAdapter.ItemInfo> tempBussinessArr = new ArrayList<>();

        ArrayList<FilterGroupRes> resArr = cn.poco.resource.FilterResMgr2.getInstance().GetGroupResArr();
        for (int i = 0, size = resArr.size(); i < size; i++)
        {
            FilterGroupRes res = resArr.get(i);

            if (res != null)
            {
                int len2 = 1;
                if (res.m_group != null)
                {
                    len2 = res.m_group.size() + 1;
                }

                //判断是否更新到camera
                ArrayList<FilterRes> mTempList = new ArrayList<>();
                if (openFromCamera)
                {
                    for (int j = 1; j < len2; j++)
                    {
                        FilterRes filterRes = res.m_group.get(j - 1);
                        if (filterRes.m_isUpDateToCamera)
                        {
                            mTempList.add(filterRes);
                        }
                    }
                    if (mTempList.size() == 0)
                    {
                        continue;
                    }
                    else
                    {
                        len2 = mTempList.size() + 1;
                    }
                    res.m_group.clear();
                    res.m_group.addAll(mTempList);
                }

                int[] uris = new int[len2];
                logos = new Object[len2];
                names = new String[len2];
                uris[0] = res.m_id;
                logos[0] = res.m_thumb;
                names[0] = res.m_name;

                if (openFromCamera)
                {
                    FilterRes temp;
                    for (int k = 0; k < mTempList.size(); k++)
                    {
                        temp = mTempList.get(k);
                        uris[k + 1] = temp.m_id;
                        logos[k + 1] = temp.m_thumb;
                        names[k + 1] = temp.m_name;
                    }
                }
                else
                {
                    if (res.m_group != null)
                    {
                        FilterRes temp;
                        for (int j = 1; j < len2; j++)
                        {
                            temp = res.m_group.get(j - 1);
                            uris[j] = temp.m_id;
                            logos[j] = temp.m_thumb;
                            names[j] = temp.m_name;
                        }
                    }
                }

                tempInfo = new FilterAdapter.ItemInfo();
                tempInfo.m_canDrag = true;
                tempInfo.m_uri = res.m_id;
                tempInfo.setData(uris, logos, names, res, res.m_maskColor);
                switch (res.m_type)
                {
                    case BaseRes.TYPE_NETWORK_URL:
                        tempInfo.m_style = FilterAdapter.ItemInfo.Style.NEED_DOWNLOAD;
                        break;
                    case BaseRes.TYPE_LOCAL_RES:
                    case BaseRes.TYPE_LOCAL_PATH:
                    default:
                        tempInfo.m_style = FilterAdapter.ItemInfo.Style.NORMAL;
                        break;
                }
                if (cn.poco.resource.FilterResMgr2.getInstance().IsNewGroup(res.m_id))
                {
                    tempInfo.m_style = FilterAdapter.ItemInfo.Style.NEW;
                }


                //商业放到最前
                if (res.m_isBusiness)
                {
                    tempInfo.m_canDrag = false;
                    tempBussinessArr.add(tempInfo);
                }
                else
                {
                    out.add(tempInfo);
                }
            }
        }
        out.get(out.size() - 1).isDrawLine = false;

        if (tempBussinessArr.size() > 0)
        {
            if (hasRecommend)
            {
                out.addAll(3, tempBussinessArr);
            }
            else
            {
                out.addAll(2, tempBussinessArr);
            }
        }
        return out;
    }


}

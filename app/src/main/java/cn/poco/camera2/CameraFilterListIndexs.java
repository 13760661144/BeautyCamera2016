package cn.poco.camera2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.poco.filter4.recycle.FilterAdapter;

/**
 * 滤镜下标管理
 *
 * @author lmx
 *         Created by lmx on 2017/6/28.
 */

public class CameraFilterListIndexs
{
    public LinkedHashMap<Integer, Integer> mFilterListPositionList = new LinkedHashMap<>(); //key : position , value : uri

    public CameraFilterListIndexs()
    {
    }

    public void reset()
    {
        if (mFilterListPositionList == null)
        {
            mFilterListPositionList = new LinkedHashMap<>();
        }
        else
        {
            mFilterListPositionList.clear();
        }
    }

    public void sortIndex(ArrayList<FilterAdapter.ItemInfo> list)
    {
        if (list != null)
        {
            //跳过下载更多 和 推荐位
            int index = 0;
            for (FilterAdapter.ItemInfo itemInfo : list)
            {
                if (itemInfo != null)
                {
                    if (itemInfo.m_uri == FilterAdapter.HeadItemInfo.HEAD_ITEM_URI)//暗角模糊
                    {
                        continue;
                    }

                    if (itemInfo.m_uri == FilterAdapter.DownloadItemInfo.DOWNLOAD_ITEM_URI)//下载更多
                    {
                        continue;
                    }

                    if (itemInfo.m_uri == FilterAdapter.RecommendItemInfo.RECOMMEND_ITEM_URI)//推荐位
                    {
                        continue;
                    }

                    if (itemInfo.m_uri == FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI)//原图
                    {
                        //原图对应uri为0
                        mFilterListPositionList.put(index, 0);
                        index++;
                        continue;
                    }

                    if (itemInfo.m_uris != null && itemInfo.m_uris.length > 1)
                    {
                        for (int i = 0, size = itemInfo.m_uris.length - 1; i < size; i++)
                        {
                            mFilterListPositionList.put(index, itemInfo.m_uris[i + 1]);
                            index++;
                        }
                    }
                }
            }
        }
    }

    public int getPositionByFilterId(int id)
    {
        if (mFilterListPositionList != null)
        {
            //id为0，则对应原图uri
            if (id == FilterAdapter.OriginalItemInfo.ORIGINAL_ITEM_URI) id = 0;
            for (Map.Entry<Integer, Integer> entry : mFilterListPositionList.entrySet())
            {
                int value = entry.getValue();
                if (value == id)
                {
                    return entry.getKey();
                }
            }
        }
        return -1;
    }

    public int setFilterIndex(int index)
    {
        if (mFilterListPositionList != null && mFilterListPositionList.size() > index && index >= 0)
        {
            Integer uri = mFilterListPositionList.get(index);
            if (uri != null)
            {
                return uri;
            }
        }
        return -1;
    }

    public int getSize()
    {
        return mFilterListPositionList != null ? mFilterListPositionList.size() : 0;
    }

    public void cleaAll()
    {
        if (mFilterListPositionList != null)
        {
            mFilterListPositionList.clear();
        }
        mFilterListPositionList = null;
    }
}

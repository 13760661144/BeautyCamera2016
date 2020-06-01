package cn.poco.camera3.mgr;

import android.content.Context;

import java.util.ArrayList;

import cn.poco.camera3.info.PreviewBgmInfo;
import cn.poco.dynamicSticker.DownloadState;
import cn.poco.resource.PreviewBgmResMgr2;

public class BgmResWrapper
{
    private static BgmResWrapper mInstance;

    private ArrayList<PreviewBgmInfo> mData;

    private int mSize;

    private int mSelIndex;

    private boolean isShowLocal;

    private BgmResWrapper()
    {
    }

    public static BgmResWrapper getInstance()
    {
        if (mInstance == null)
        {
            synchronized (BgmResWrapper.class)
            {
                if (mInstance == null)
                {
                    mInstance = new BgmResWrapper();
                }
            }
        }
        return mInstance;
    }

    private void InitData(Context context)
    {
        if (mData == null)
        {
            mData = PreviewBgmResMgr2.getInstance().GetResArr(context, this.isShowLocal);
            mSize = mData.size();
        }
    }

    public void setShowLocal(boolean showLocal)
    {
        this.isShowLocal = showLocal;
    }

    public int GetSelIndex()
    {
        return mSelIndex;
    }

    public PreviewBgmInfo GetInfo(Context context, int index)
    {
        InitData(context);

        if (index < 0 || index >= mSize) return null;

        return mData.get(index);
    }

    public PreviewBgmInfo GetInfoById(Context context, int id)
    {
        InitData(context);

        for (int i = 0; i < mSize; i++)
        {
            PreviewBgmInfo info = mData.get(i);
            if (info.getId() == id)
            {
                return info;
            }
        }

        return null;
    }

    public int GetIndexById(Context context, int id)
    {
        InitData(context);

        for (int i = 0; i < mSize; i++)
        {
            PreviewBgmInfo info = mData.get(i);
            if (info.getId() == id)
            {
                return i;
            }
        }

        return -1;
    }

    public int GetDataSize(Context context)
    {
        InitData(context);
        return mSize;
    }

    public void SetThumbRotationDegree(Context context, float degree)
    {
        InitData(context);
        PreviewBgmInfo info = GetInfo(context, mSelIndex);
        if (info != null)
        {
            info.setThumbDegree(degree);
        }
    }

    /**
     * @param index 新 index
     * @return 旧 index , -1 没有更新
     */
    public int UpdateSelIndex(Context context, int index)
    {
        if (index == mSelIndex)
        {
            return index;
        }

        int oldIndex = mSelIndex;

        // 将之前的info 旋转角度设为默认
        SetThumbRotationDegree(context, 0);
        setItemClipStatus(context, false);
        PreviewBgmInfo info = GetInfo(context, oldIndex);
        info.setIsSel(false);

        info = GetInfo(context, index);
        info.setIsSel(true);

        mSelIndex = index;

        return oldIndex;
    }

    public void setItemClipStatus(Context context, boolean isClip)
    {
        PreviewBgmInfo info = GetInfo(context, mSelIndex);
        if (info != null) {
            info.setIsClip(isClip);
        }
    }

    /**
     * @param id       资源id
     * @param progress 进度
     * @param state    example {@link DownloadState#DOWNLOAD_SUCCESS} 下载成功<br>
     *                 {@link DownloadState#WAITTING_FOR_DOWNLOAD} 等待下载<br>
     *                 {@link DownloadState#HAVE_DOWNLOADED} 已下载
     * @return 对应id 的 index
     */
    public int SetInfoDownloadState(Context context, int id, int progress, int state)
    {
        int index = GetIndexById(context, id);
        if (index >= 0)
        {
            PreviewBgmInfo info = mData.get(index);
            info.setProgress(progress);
            info.setState(state);
        }
        return index;
    }

    /***
     *
     * @param context
     * @param info
     * @param insertIndex
     * @param isSelect
     * @return 返回 旧 index
     */
    public int InsertBgmLocalInfo(Context context, PreviewBgmInfo info, int insertIndex, boolean isSelect)
    {
        InitData(context);

        if (insertIndex > mSelIndex)
        {
            mData.add(insertIndex, info);

        }
        else
        {
            mData.add(insertIndex, info);
            mSelIndex += 1;
        }

        mSize = mData.size();

        SetThumbRotationDegree(context, 0);
        setItemClipStatus(context, false);

        int oldSelectIndex = mSelIndex;

        if (isSelect)
        {
            PreviewBgmInfo oldPreviewInfo = mData.get(oldSelectIndex);
            if (oldPreviewInfo != null)
            {
                oldPreviewInfo.setIsSel(false);
            }
            mSelIndex = insertIndex;
        }

        return oldSelectIndex;
    }

    public void RemoveBgmLocalInfo(Context context, int removeIndex)
    {
        InitData(context);

        if (removeIndex >= mSize) return;


        if (removeIndex > mSelIndex)
        {
            mData.remove(removeIndex);
        }
        else
        {
            mData.remove(removeIndex);
            mSelIndex -= 1;
        }

        mSize = mData.size();
    }

    public void ClearMemory()
    {
        if (mData != null)
        {
            mData.clear();
            mData = null;
        }

        mInstance = null;
    }
}

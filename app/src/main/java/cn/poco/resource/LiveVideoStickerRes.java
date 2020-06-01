package cn.poco.resource;


import android.content.Context;

import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;

/**
 * @author lmx
 *         Created by lmx on 2018/1/24.
 */

public class LiveVideoStickerRes extends VideoStickerRes
{
    public LiveVideoStickerRes()
    {
        super(ResType.LIVE_VIDEO_FACE.GetValue());
    }

    @Override
    public String GetSaveParentPath()
    {
        return DownloadMgr.getInstance().LIVE_VIDEO_FACE_PATH;
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
    {
        if (item.m_onlyThumb)
        {

        }
        else
        {
            Context context = MyFramework2App.getInstance().getApplicationContext();
            ArrayList<LiveVideoStickerRes> arr = LiveVideoStickerResMgr2.getInstance().sync_GetSdcardRes(context, null);
            if (isNet)
            {
                if (arr != null)
                {
                    ResourceUtils.DeleteItem(arr, m_id);
                    arr.add(0, this);
                    LiveVideoStickerResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
            else
            {
                if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
                {
                    arr.add(0, this);
                    LiveVideoStickerResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
        }
    }
}

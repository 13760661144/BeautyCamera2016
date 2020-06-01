package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import cn.poco.dynamicSticker.ShowType;
import cn.poco.framework.MyFramework2App;

/**
 * @author lmx
 *         Created by lmx on 2017/5/22.
 */

public class VideoStickerGroupRes extends BaseRes
{
    public ArrayList<VideoStickerRes> m_group;

    /**
     * 是否属于隐藏分类（true：正常模式不显示，特殊渠道显示，false：正常模式显示）
     */
    public boolean m_isHide = false;

    /**
     * 分类显示类型（0：全部，1：镜头与社区，2：直播助手）<br/>
     * 查看 {@link cn.poco.dynamicSticker.ShowType.Label}
     */
    @ShowType.Label
    public int m_display = ShowType.Label.CAMERA;

    public int[] m_stickerIDArr;

    public VideoStickerGroupRes()
    {
        super(ResType.VIDEO_FACE_GROUP.GetValue());
    }

    public VideoStickerGroupRes(int resType)
    {
        super(resType);
    }

    @Override
    public String GetSaveParentPath()
    {
        return DownloadMgr.getInstance().VIDEO_FACE_PATH;
    }


    @Override
    public void OnBuildPath(DownloadTaskThread.DownloadItem item)
    {
        if (item != null)
        {
            int resLen = 1;
            if (item.m_onlyThumb)
            {

            }
            else
            {

            }

            item.m_paths = new String[resLen];
            item.m_urls = new String[resLen];

            String parentPath = GetSaveParentPath();
            String name = DownloadMgr.GetImgFileName(url_thumb);
            if (!TextUtils.isEmpty(name))
            {
                item.m_paths[0] = parentPath + File.separator + name;
                item.m_urls[0] = url_thumb;
            }
        }
    }

    @Override
    public void OnBuildData(DownloadTaskThread.DownloadItem item)
    {
        if (item != null && item.m_urls != null && item.m_urls.length > 0)
        {
            if (item.m_paths.length > 0 && item.m_paths[0] != null)
            {
                m_thumb = item.m_paths[0];
            }

            //放最后避免同步问题
            if (m_type == BaseRes.TYPE_NETWORK_URL)
            {
                m_type = BaseRes.TYPE_LOCAL_PATH;
            }
        }
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
            ArrayList<VideoStickerGroupRes> arr = VideoStickerGroupResMgr2.getInstance().sync_GetSdcardRes(context, null);
            if (isNet)
            {
                if (arr != null)
                {
                    ResourceUtils.DeleteItem(arr, m_id);
                    arr.add(0, this);
                    VideoStickerGroupResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
            else
            {
                if (arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
                {
                    arr.add(0, this);
                    VideoStickerGroupResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
        }
    }
}

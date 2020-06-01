package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;

public class PreviewBgmRes extends BaseRes
{
    public String url_res;

    public Object m_res;
    public String m_res_name;//res名

    public int m_production_id; // 作品 id

    public PreviewBgmRes()
    {
        super(ResType.PRE_BGM.GetValue());
    }

    @Override
    public void OnBuildPath(DownloadTaskThread.DownloadItem item)
    {
        if (item != null)
        {
            int resLen = 1;

            if (!item.m_onlyThumb)
            {
                resLen = 2;
            }
            // 文件路径
            item.m_paths = new String[resLen];
            // 下载路径
            item.m_urls = new String[resLen];

            String parentPath = getFolderPath();

            File file = new File(parentPath);
            if (!file.exists())
            {
                file.mkdirs();
            }

            String thumbPath = getThumbPath();

            if (!TextUtils.isEmpty(thumbPath))
            {
                item.m_paths[0] = thumbPath;// 缩略图路径
                item.m_urls[0] = url_thumb;
            }

            if (!item.m_onlyThumb)
            {
                String bgmPath = getBgmPath();
                if (!TextUtils.isEmpty(bgmPath))
                {
                    item.m_paths[1] = bgmPath;// mp3路径
                    item.m_urls[1] = url_res;
                }
            }
        }
    }

    @Override
    public void OnBuildData(DownloadTaskThread.DownloadItem item)
    {
        if (item != null && item.m_urls.length > 0)
        {
            if (item.m_onlyThumb)
            {
                if (item.m_paths[0] != null)
                {
                    m_thumb = item.m_paths[0];
                }
            }
            else
            {
                if (item.m_paths != null && item.m_paths.length > 1)
                {
                    if (item.m_paths[0] != null)
                    {
                        m_thumb = item.m_paths[0];
                    }

                    if (item.m_paths[1] != null)
                    {
                        m_res = item.m_paths[1];
                    }
                }
            }

            //放最后避免同步问题
            if (m_type == BaseRes.TYPE_NETWORK_URL)
            {
                m_type = BaseRes.TYPE_LOCAL_PATH;
            }
        }
    }

    @Override
    public String GetSaveParentPath()
    {
        return DownloadMgr.getInstance().PREVIEW_BGM_PATH;
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
    {
        if (!item.m_onlyThumb)
        {
            Context context = MyFramework2App.getInstance().getApplicationContext();
            ArrayList<PreviewBgmRes> arr = PreviewBgmResMgr2.getInstance().sync_GetSdcardRes(context, null);
            if (isNet)
            {
                if (arr != null)
                {
                    arr.add(this);
                    PreviewBgmResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
            else
            {
                if (arr != null)
                {
                    for (PreviewBgmRes res : arr)
                    {
                        if (res.m_id == m_id) return;
                    }

                    arr.add(this);
                    PreviewBgmResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
        }
    }

    public String getFolderPath()
    {
        return GetSaveParentPath() + File.separator + "." +String.valueOf(m_id);
    }
    /**
     * @return mp3 资源文件路径
     */
    public String getBgmPath()
    {
        String fileName = m_res_name  = DownloadMgr.GetFileName(url_res);

        return !TextUtils.isEmpty(fileName) ? getFolderPath() + File.separator + fileName : null;
    }

    /**
     * @return 缩略图文件路径
     */
    public String getThumbPath()
    {
        String fileName = DownloadMgr.GetImgFileName(url_thumb);

        return !TextUtils.isEmpty(fileName) ? getFolderPath() + File.separator + fileName : null;
    }
}

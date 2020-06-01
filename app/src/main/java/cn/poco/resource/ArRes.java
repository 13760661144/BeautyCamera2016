package cn.poco.resource;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by admin on 2018/1/27.
 */

public class ArRes extends BaseRes
{
    public String m_show_id;
    public String m_user_id;
    public String m_user_name;
    public String m_avatar_path;
    public String m_video_url;
    public String m_img_url;
    public String m_avatar_url;

    public ArRes()
    {
        super(ResType.AR_NEW_YEAR.GetValue());
    }

    @Override
    public void OnBuildData(DownloadTaskThread.DownloadItem item)
    {
        if (item != null)
        {
            if (item.m_urls != null && item.m_urls.length > 0)
            {
                if (item.m_paths != null && item.m_paths.length > 0 && item.m_paths[0] != null)
                {
                    m_thumb = item.m_paths[0];
                }

                if (item.m_paths != null && item.m_paths.length > 1 && item.m_paths[1] != null)
                {
                    m_avatar_path = item.m_paths[1];
                }

                //放最后避免同步问题
                if (m_type == BaseRes.TYPE_NETWORK_URL)
                {
                    m_type = BaseRes.TYPE_LOCAL_PATH;
                }
            }
        }
    }

    @Override
    public void OnBuildPath(DownloadTaskThread.DownloadItem item)
    {
        ///构造下载资源组
        if (item != null)
        {
            int resLen = 2;
            item.m_paths = new String[resLen];//目录路径
            item.m_urls = new String[resLen];//下载url

            String parentPath = GetSaveParentPath() + File.separator + m_show_id;//根据id创建目录
            File parentFile = new File(parentPath);
            if (!parentFile.exists())
            {
                boolean mkdirs = parentFile.mkdirs();
            }
            parentFile = null;

            String thumbName = getImageThumbPath();//xxx.img(png,jpg)
            if (!TextUtils.isEmpty(thumbName))
            {
                item.m_paths[0] = parentPath + File.separator + thumbName;
                item.m_urls[0] = m_img_url;
            }

            thumbName = getAvatarThumbPath();//xxx.img(png,jpg)
            if (!TextUtils.isEmpty(thumbName))
            {
                item.m_paths[1] = parentPath + File.separator + thumbName;
                item.m_urls[1] = m_avatar_url;
            }
        }
    }

    public String getImageThumbPath()
    {
        return DownloadMgr.GetImgFileName(m_img_url);
    }

    public String getAvatarThumbPath()
    {
        return DownloadMgr.GetImgFileName(m_avatar_url);
    }

    @Override
    public String GetSaveParentPath()
    {
        return  DownloadMgr.getInstance().AR_NEW_YEAR_PATH;
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
    {

    }
}

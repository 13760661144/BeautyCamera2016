package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;


/**
 * @author lmx
 *         Created by lmx on 2017/5/16.
 */

public class VideoStickerRes extends BaseRes
{
    public String m_tracking_link; //商业统计链接
    public String m_thumb_tracking_link;//微缩图触发统计
    public boolean m_is_business;

    public int m_bg_color;//缩略图背景色
    public String m_shape_type;//变形
    public boolean m_need_separate_shape;//有变形且有foreground 或 frame 素材
    public String m_prompt_text;//变形素材提示文本

    public boolean m_is_shape_compose = false; //是否失常用组合变形素材

    /**
     * 查看{@link cn.poco.dynamicSticker.ShowType#STICKER}、
     * {@link cn.poco.dynamicSticker.ShowType#GIF}、
     * {@link cn.poco.dynamicSticker.ShowType#BOTH}
     */
    public String m_show_type;//分类 sticker gif both

    /**
     * 查看{@link cn.poco.dynamicSticker.ShowType}
     */
    public int m_show_type_level;


    /*zip*/
    public String m_res_path;//zip包保存路径（.../id/zip_id/xxx.img）
    public String m_res_url;//zip包网络url
    public String m_res_name;//zip包名(xxx.img)

    /*music sound*/
    public boolean m_has_music;

    public boolean m_isAR4iOS;//是否是ar素材

    //解析后赋值对象，删除后应释放对象
    public cn.poco.dynamicSticker.v2.StickerRes mStickerRes;

    public VideoStickerRes()
    {
        super(ResType.VIDEO_FACE.GetValue());
    }

    public VideoStickerRes(int resTyp)
    {
        super(resTyp);
    }

    @Override
    public void CopyTo(BaseRes dst)
    {
        super.CopyTo(dst);
    }

    @Override
    public void OnBuildPath(DownloadTaskThread.DownloadItem item)
    {
        ///构造下载资源组
        if (item != null)
        {
            int resLen = 1;
            if (item.m_onlyThumb)
            {
            }
            else
            {
                /*
                 * thumb[0]
                 * zip [1]
                 */
                resLen = 2;
            }
            item.m_paths = new String[resLen];//目录路径
            item.m_urls = new String[resLen];//下载url

            String parentPath = GetSaveParentPath() + File.separator + m_id;//根据id创建目录
            File parentFile = new File(parentPath);
            if (!parentFile.exists())
            {
                boolean mkdirs = parentFile.mkdirs();
                //Log.d("mmm", "OnBuildData: create parent file " + mkdirs);
            }
            parentFile = null;

            String thumbName = DownloadMgr.GetImgFileName(url_thumb);//xxx.img(png,jpg)
            if (!TextUtils.isEmpty(thumbName))
            {
                item.m_paths[0] = parentPath + File.separator + thumbName;
                item.m_urls[0] = url_thumb;
            }
            if (!item.m_onlyThumb)
            {
                String resName = DownloadMgr.GetImgFileName(m_res_url);//xxx.img(zip)
                if (!TextUtils.isEmpty(resName))
                {
                    m_res_name = resName;
                    item.m_paths[1] = parentPath + File.separator + resName;
                    item.m_urls[1] = m_res_url;
                }
            }
        }
    }


    @Override
    public void OnBuildData(DownloadTaskThread.DownloadItem item)
    {
        if (item != null)
        {
            if (item.m_urls != null && item.m_urls.length > 0)
            {
                if (item.m_onlyThumb)
                {
                    if (item.m_paths != null && item.m_paths.length > 0 && item.m_paths[0] != null)
                    {
                        m_thumb = item.m_paths[0];
                    }
                }
                else
                {
                    if (item.m_paths != null && item.m_paths.length > 0)
                    {
                        if (item.m_paths[0] != null)
                        {
                            m_thumb = item.m_paths[0];//xxx.img(png,jpg)
                        }
                        if (item.m_paths[1] != null)
                        {
                            m_res_path = item.m_paths[1];//xxx.img(zip)
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
    }

    @Override
    public String GetSaveParentPath()
    {
        return DownloadMgr.getInstance().VIDEO_FACE_PATH;
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
            ArrayList<VideoStickerRes> arr = VideoStickerResMgr2.getInstance().sync_GetSdcardRes(context, null);
            if (isNet)
            {
                if (arr != null)
                {
                    ResourceUtils.DeleteItem(arr, m_id);
                    arr.add(0, this);
                    VideoStickerResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
            else
            {
                if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
                {
                    arr.add(0, this);
                    VideoStickerResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
        }
    }

}

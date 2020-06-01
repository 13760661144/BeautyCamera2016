package cn.poco.resource;


import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;

public class FilterRes extends BaseRes {

    public static final int FILTER_TYPE_IMG = 0x001;//图片模式
    public static final int FILTER_TYPE_IMP = 0x002;//接口模式

    public int m_filterType = FILTER_TYPE_IMG;

    public FilterData[] m_datas;

    public boolean m_isUpDateToCamera;//是否更新到镜头

    public String m_listThumbUrl;//主题列表的缩略图url

    public Object m_listThumbRes;

    public boolean m_isHaswatermark;//是否有水印

    public boolean m_isHasvignette;//是否有暗角

    public int m_filterAlpha = 80;

    public boolean m_isSkipFace;//是否避开人脸

    public boolean m_isStickerFilter;//是否是贴纸自带滤镜

    public static class FilterData
    {
        public Object m_res;
        public String m_url_img;
        public int[] m_params;
        public boolean m_isSkipFace;//是否避开人脸
    }

    public FilterRes() {
        super(ResType.FILTER.GetValue());
    }

    @Override
    public void CopyTo(BaseRes dst) {
        super.CopyTo(dst);
        if(dst instanceof FilterRes)
        {
            FilterRes dst2 = (FilterRes) dst;
            dst2.m_isUpDateToCamera = this.m_isUpDateToCamera;
            dst2.m_filterType = this.m_resType;
            dst2.m_datas = this.m_datas;
            dst2.m_listThumbUrl = this.m_listThumbUrl;
            dst2.m_listThumbRes = this.m_listThumbRes;
            dst2.m_filterAlpha = this.m_filterAlpha;
            dst2.m_isHaswatermark = this.m_isHaswatermark;
            dst2.m_isHasvignette = this.m_isHasvignette;
        }
    }

    @Override
    public void OnBuildPath(DownloadTaskThread.DownloadItem item) {
        if(item != null)
        {
            int resLen = 1;
            if(item.m_onlyThumb)
            {
            }
            else
            {
                if(m_datas != null && m_datas.length > 0)
                {
                    resLen += m_datas.length;
                }
            }
            resLen += 1;//主题缩略图
            item.m_urls = new String[resLen];
            item.m_paths = new String[resLen];
            String name = DownloadMgr.GetImgFileName(url_thumb);
            String parentPath = GetSaveParentPath();
            if(name != null && !name.equals(""))
            {
                item.m_paths[0] = parentPath + File.separator + name;
                item.m_urls[0] = url_thumb;
            }
            if(!item.m_onlyThumb)
            {
                for(int i = 0;i < m_datas.length; i++)
                {
                    FilterData data = m_datas[i];
                    if(data != null)
                    {
                        name = DownloadMgr.GetImgFileName(data.m_url_img);
                        item.m_paths[i+1] = parentPath + File.separator + name;
                        item.m_urls[i+1] = data.m_url_img;
                    }
                }

                name = DownloadMgr.GetImgFileName(m_listThumbUrl);
                item.m_paths[resLen - 1] = parentPath + File.separator + name;
                item.m_urls[resLen - 1] = m_listThumbUrl;
            }
        }
    }


    @Override
    public void OnBuildData(DownloadTaskThread.DownloadItem item) {
        if(item != null && item.m_urls.length > 0)
        {
            if(item.m_onlyThumb)
            {
                if(item.m_paths.length > 0 && item.m_paths[0] != null)
                {
                    m_thumb = item.m_paths[0];
                }
            }
            else
            {
                if(item.m_paths[0] != null)
                {
                    m_thumb = item.m_paths[0];
                }
                for(int i = 1; i < item.m_paths.length - 1; i++)
                {
                    if(item.m_paths[i] != null && (i - 1) < m_datas.length)
                    {
                        m_datas[i-1].m_res = item.m_paths[i];
                    }
                }

                if(item.m_paths[item.m_paths.length - 1] != null)
                {
                    m_listThumbRes = item.m_paths[item.m_paths.length - 1];
                }
            }

            //放最后避免同步问题
            if(m_type == BaseRes.TYPE_NETWORK_URL)
            {
                m_type = BaseRes.TYPE_LOCAL_PATH;
            }
        }
    }

    @Override
    public String GetSaveParentPath() {
        return DownloadMgr.getInstance().FILTER_PATH;
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet) {
        if(item.m_onlyThumb)
        {
        }
        else
        {
            Context context = MyFramework2App.getInstance().getApplicationContext();
            ArrayList<FilterRes> arr = FilterResMgr2.getInstance().sync_GetSdcardRes(context, null);
            if(isNet)
            {
                if(arr != null)
                {
                    ResourceUtils.DeleteItem(arr, m_id);
                    arr.add(0, this);
                    FilterResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
            else
            {
                if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
                {
                    arr.add(0, this);
                    FilterResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
                }
            }
        }
    }
}

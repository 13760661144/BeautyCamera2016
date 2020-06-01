package cn.poco.resource;

import android.text.TextUtils;

import java.io.File;

/**
 * Created by zwq on 2016/07/28 14:52.<br/><br/>
 */
public class LimitRes extends BaseRes {

    public boolean isLimit;                          //是否是限量标记
    public boolean isLimitEnd = true;                //限量结束标记
    public String mRealLimit;                        //限量的实时数量
    public String mLimitThumbUrl;                    //限量标记缩略图地址
    public String mLimitExplainThumbUrl;             //限量预览页缩略图地址
    public String mLimitExplainTitle;                //限量预览页标题
    public String mLimitExplainContent;              //限量预览页说明
    public String mLimitExplainRemainingThumbUrl;    //限量预览页剩余数缩略图地址
    public String mLimitExplainEndText;              //限量结束文案
    public String mLimitExplainEndTextLink;          //限量结束跳转链接
    public String mLimitExplainEndGoWebButtonText;   //限量结束跳转web按钮文案
    public String mLimitType;                        //限量类型
    public String mFlightKey;                        //限量请求下载key

    public String mLimitExplainRemainingThumb;
    public String mLimitThumb;
    public String mLimitExplainThumb;

    public LimitRes() {
        super(ResType.LIMIT.GetValue());
    }

    @Override
    public String GetSaveParentPath() {
        return DownloadMgr.getInstance().LIMIT_PATH;
    }

    @Override
    public void OnBuildPath(DownloadTaskThread.DownloadItem item) {
        if (item != null) {
            /*
            * mLimitExplainRemainingThumb
            * mLimitThumb
            * mLimitExplainThumb
            */
            int resLen = 0;
            if (item.m_onlyThumb) {
            } else {
                resLen = 3;
            }
            item.m_paths = new String[resLen];
            item.m_urls = new String[resLen];
            if (!item.m_onlyThumb) {
                String name = DownloadMgr.GetImgFileName(mLimitExplainRemainingThumbUrl);
                String parentPath = GetSaveParentPath();
                if (!TextUtils.isEmpty(name)) {
                    item.m_paths[0] = parentPath + File.separator + name;
                    if (!mLimitExplainRemainingThumbUrl.contains("?limit_icon_v2=1")) {
                        mLimitExplainRemainingThumbUrl += "?limit_icon_v2=1";//新版图标标记
                    }
                    item.m_urls[0] = mLimitExplainRemainingThumbUrl;
                }
                name = DownloadMgr.GetImgFileName(mLimitThumbUrl);
                if (!TextUtils.isEmpty(name)) {
                    item.m_paths[1] = parentPath + File.separator + name;
                    item.m_urls[1] = mLimitThumbUrl;
                }
                name = DownloadMgr.GetImgFileName(mLimitExplainThumbUrl);
                if (!TextUtils.isEmpty(name)) {
                    item.m_paths[2] = parentPath + File.separator + name;
                    item.m_urls[2] = mLimitExplainThumbUrl;
                }
            }
        }
    }

    @Override
    public void OnBuildData(DownloadTaskThread.DownloadItem item) {
        if (item != null && item.m_urls.length > 0) {
            if (item.m_onlyThumb) {
                if (item.m_paths.length > 0 && item.m_paths[0] != null) {
                    m_thumb = item.m_paths[0];
                }
            } else {
                if (item.m_paths[0] != null) {
                    mLimitExplainRemainingThumb = item.m_paths[0];
                }
                if (item.m_paths[1] != null) {
                    mLimitThumb = item.m_paths[1];
                }
                if (item.m_paths[2] != null) {
                    mLimitExplainThumb = item.m_paths[2];
                }

                //放最后避免同步问题
                if (m_type == BaseRes.TYPE_NETWORK_URL) {
                    m_type = BaseRes.TYPE_LOCAL_PATH;
                }
            }
        }
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet) {

    }
}

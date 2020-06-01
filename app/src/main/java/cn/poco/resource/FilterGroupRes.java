package cn.poco.resource;

import java.util.ArrayList;

/**
 * @author lmx
 *         Created by lmx on 2017/3/31.
 */

public class FilterGroupRes extends BaseRes {

    public boolean m_isBusiness = false;

    public ArrayList<FilterRes> m_group;
    //缩略图使用m_thumb
    public int m_maskColor; //背景颜色

    public FilterGroupRes() {
        super(ResType.FILTER_GROUP.GetValue());
    }

    @Override
    public String GetSaveParentPath() {
        return DownloadMgr.getInstance().OTHER_PATH;
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet) {
        if (item.m_onlyThumb) {
        } else {
        }
    }
}

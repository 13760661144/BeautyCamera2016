package cn.poco.filter4;

import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadTaskThread;
import cn.poco.resource.ResType;

/**
 * @author lmx
 *         Created by lmx on 2017/3/28.
 */

public class WatermarkItem extends BaseRes
{

    public int mTongJiId;

    public int type;

    public int mID;

    public Object res;

    public Object thumb;

    public WatermarkItem()
    {
        super(ResType.WATERMARK.GetValue());
    }

    public void setTongJiId(int tongJiId) {
        mTongJiId = tongJiId;
    }

    @Override
    public String GetSaveParentPath()
    {
        return null;
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
    {
    }
}

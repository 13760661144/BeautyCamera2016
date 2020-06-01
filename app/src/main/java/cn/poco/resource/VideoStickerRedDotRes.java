package cn.poco.resource;

/**
 * @author lmx
 *         Created by lmx on 2017/10/9.
 */

public class VideoStickerRedDotRes extends BaseRes
{
    public String m_tips;       //提示消失 空：提示小红点 有值：显示
    public long m_timestamp;    //时间戳标记，遇上次对比，如果不一样则表示更新 单位：秒

    public long m_last_timestamp;//上次红点的时间戳标记 单位：秒


    public boolean m_show_new = false;//显示new

    public VideoStickerRedDotRes()
    {
        super(ResType.RESOURCE_RED_DOT.GetValue());
    }

    @Override
    public String GetSaveParentPath()
    {
        return DownloadMgr.getInstance().RESOURCE_RED_DOT_PATH;
    }

    @Override
    public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
    {
        //TODO
    }
}

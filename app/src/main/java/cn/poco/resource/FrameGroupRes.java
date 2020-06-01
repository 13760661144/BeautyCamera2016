package cn.poco.resource;

import java.util.ArrayList;

public class FrameGroupRes extends BaseRes
{
	public ArrayList<FrameRes> m_group;

	public FrameGroupRes()
	{
		super(ResType.FRAME_GROUP.GetValue());
	}

	@Override
	public String GetSaveParentPath()
	{
		return DownloadMgr.getInstance().OTHER_PATH;
	}

	@Override
	public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
	{
		if(item.m_onlyThumb)
		{
		}
		else
		{
		}
	}
}

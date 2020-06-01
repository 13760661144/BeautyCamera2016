package cn.poco.resource;

import java.util.ArrayList;

public class BrushGroupRes extends BaseRes
{
	public ArrayList<BrushRes> m_group;

	public BrushGroupRes()
	{
		super(ResType.BRUSH_GROUP.GetValue());
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

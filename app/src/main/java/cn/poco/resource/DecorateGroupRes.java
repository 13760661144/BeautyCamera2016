package cn.poco.resource;

import java.util.ArrayList;

import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class DecorateGroupRes extends BaseRes
{
	public ArrayList<DecorateRes> m_group;
	public Object m_titleThumb;

	public DecorateGroupRes()
	{
		super(ResType.DECORATE_GROUP.GetValue());
	}

	@Override
	public String GetSaveParentPath()
	{
		return DownloadMgr.getInstance().OTHER_PATH;
	}

	@Override
	public void OnDownloadComplete(DownloadItem item, boolean isNet)
	{
		if(item.m_onlyThumb)
		{
		}
		else
		{
		}
	}
}

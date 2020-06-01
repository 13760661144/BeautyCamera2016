package cn.poco.resource;

import java.util.ArrayList;

import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class MakeupGroupRes extends BaseRes
{
	public ArrayList<MakeupRes> m_group;
	//缩略图使用m_thumb
	public int m_maskColor; //背景颜色

	public MakeupGroupRes()
	{
		super(ResType.MAKEUP_GROUP.GetValue());
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

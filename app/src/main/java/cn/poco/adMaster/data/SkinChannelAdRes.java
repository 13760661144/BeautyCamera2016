package cn.poco.adMaster.data;

import com.adnonstop.admasterlibs.data.AbsSkinChannelAdRes;

import cn.poco.resource.DownloadMgr;
import cn.poco.resource.DownloadTaskThread;
import cn.poco.resource.ResType;

/**
 * Created by Raining on 2017/8/21.
 */

public class SkinChannelAdRes extends AbsSkinChannelAdRes
{
	public SkinChannelAdRes()
	{
		super(ResType.AD_SKIN_CHANNEL.GetValue());
	}

	@Override
	public String GetSaveParentPath()
	{
		return DownloadMgr.getInstance().BUSINESS_PATH;
	}

	@Override
	public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
	{

	}
}

package cn.poco.adMaster.data;

import com.adnonstop.admasterlibs.data.AbsBootAdRes;

import cn.poco.resource.DownloadMgr;
import cn.poco.resource.DownloadTaskThread;
import cn.poco.resource.ResType;

/**
 * Created by Raining on 2017/8/21.
 */

public class BootAdRes extends AbsBootAdRes
{
	public BootAdRes()
	{
		super(ResType.AD_BOOT.GetValue());
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

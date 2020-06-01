package cn.poco.adMaster.data;

import com.adnonstop.admasterlibs.data.AbsSkinFullscreenAdRes;

import cn.poco.resource.DownloadMgr;
import cn.poco.resource.DownloadTaskThread;
import cn.poco.resource.ResType;

/**
 * Created by Raining on 2017/8/25.
 */

public class SkinFullscreenAdRes extends AbsSkinFullscreenAdRes
{
	public SkinFullscreenAdRes()
	{
		super(ResType.AD_SKIN_FULLSCREEN.GetValue());
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

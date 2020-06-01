package cn.poco.adMaster;

import android.content.Context;

import com.adnonstop.admasterlibs.AbsAdMaster;
import com.adnonstop.admasterlibs.data.AbsAdRes;

import org.json.JSONObject;

import cn.poco.adMaster.data.ClickAdRes;
import cn.poco.framework.EventID;
import cn.poco.resource.DownloadMgr;
import cn.poco.system.AppInterface;

/**
 * Created by Raining on 2017/9/29.
 * 侧边栏
 */

public class SidebarAdMaster extends AbsAdMaster
{
	public static final String POS_TEXT = "sidebar_txt";
	public static final String POS_BANNER = "sidebar_banner";

	private static SidebarAdMaster sInstance;

	private SidebarAdMaster(Context context)
	{
		super(AppInterface.GetInstance(context));
	}

	public synchronized static SidebarAdMaster getInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new SidebarAdMaster(context);
		}
		return sInstance;
	}

	@Override
	protected int GetLocalEventId()
	{
		return 0;
	}

	@Override
	protected int GetSdcardEventId()
	{
		return 0;
	}

	@Override
	protected long GetUpdateInterval()
	{
		return 60000;
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.AD_SIDEBAR_CLOUD_OK;
	}

	@Override
	protected String GetAdPosition()
	{
		return POS_TEXT + "," + POS_BANNER;
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return DownloadMgr.getInstance().BANNER_PATH + "/SidebarAdMaster.xxxx";
	}

	@Override
	protected AbsAdRes DecodeAdRes(JSONObject json)
	{
		AbsAdRes out;

		{
			out = new ClickAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}

		return out;
	}
}

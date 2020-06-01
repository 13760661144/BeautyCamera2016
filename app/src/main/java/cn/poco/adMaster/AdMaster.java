package cn.poco.adMaster;

import android.content.Context;

import com.adnonstop.admasterlibs.AbsAdMaster;
import com.adnonstop.admasterlibs.data.AbsAdRes;

import org.json.JSONObject;

import cn.poco.adMaster.data.BootAdRes;
import cn.poco.adMaster.data.ChannelAdRes;
import cn.poco.adMaster.data.ClickAdRes;
import cn.poco.adMaster.data.FullscreenAdRes;
import cn.poco.adMaster.data.SkinChannelAdRes;
import cn.poco.adMaster.data.SkinClickAdRes;
import cn.poco.adMaster.data.SkinFullscreenAdRes;
import cn.poco.framework.EventID;
import cn.poco.resource.DownloadMgr;
import cn.poco.system.AppInterface;

/**
 * Created by Raining on 2017/8/21.
 * 开机+通道
 */

public class AdMaster extends AbsAdMaster
{
	private static AdMaster sInstance;

	private AdMaster(Context context)
	{
		super(AppInterface.GetInstance(context));
	}

	public synchronized static AdMaster getInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new AdMaster(context);
		}
		return sInstance;
	}

	public synchronized static void clearInstance()
	{
		sInstance = null;
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
		return 300000;
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.AD_CLOUD_OK;
	}

	@Override
	protected String GetAdPosition()
	{
		return "boot,channel";
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return DownloadMgr.getInstance().BUSINESS_PATH + "/AdMaster.xxxx";
	}

	@Override
	protected AbsAdRes DecodeAdRes(JSONObject json)
	{
		AbsAdRes out;

		{
			out = new SkinChannelAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}
		if(out == null)
		{
			out = new SkinClickAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}
		if(out == null)
		{
			out = new SkinFullscreenAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}
		if(out == null)
		{
			out = new BootAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}
		if(out == null)
		{
			out = new ChannelAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}
		if(out == null)
		{
			out = new ClickAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}
		if(out == null)
		{
			out = new FullscreenAdRes();
			if(!out.Decode(json))
			{
				out = null;
			}
		}

		return out;
	}
}

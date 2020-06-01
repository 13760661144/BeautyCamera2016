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
 * Created by Raining on 2017/8/22.
 * app推荐banner
 */

public class RecommendAdMaster extends AbsAdMaster
{
	private static RecommendAdMaster sInstance;

	public RecommendAdMaster(Context context)
	{
		super(AppInterface.GetInstance(context));
	}

	public synchronized static RecommendAdMaster getInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new RecommendAdMaster(context);
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
		return 3000;
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.AD_RECOMMEND_CLOUD_OK;
	}

	@Override
	protected String GetAdPosition()
	{
		return "rmb";
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return DownloadMgr.getInstance().BANNER_PATH + "/RecommendAdMaster.xxxx";
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

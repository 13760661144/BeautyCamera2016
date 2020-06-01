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
 * Created by Raining on 2017/8/24.
 * 动态贴纸
 */

public class StickerAdMaster extends AbsAdMaster
{
	private static StickerAdMaster sInstance;

	private StickerAdMaster(Context context)
	{
		super(AppInterface.GetInstance(context));
	}

	public synchronized static StickerAdMaster getInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new StickerAdMaster(context);
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
		return EventID.AD_STICKER_CLOUD_OK;
	}

	@Override
	protected String GetAdPosition()
	{
		return "stickers_share";
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return DownloadMgr.getInstance().BANNER_PATH + "/StickerAdMaster.xxxx";
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

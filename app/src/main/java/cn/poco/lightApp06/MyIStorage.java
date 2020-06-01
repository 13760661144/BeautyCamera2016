package cn.poco.lightApp06;

import android.content.Context;

import org.json.JSONObject;

import cn.poco.pocointerfacelibs.PocoWebUtils;
import cn.poco.storagesystemlibs.IStorage;
import cn.poco.storagesystemlibs.StorageStruct;
import cn.poco.storagesystemlibs.StorageUtils;
import cn.poco.storagesystemlibs.UpdateInfo;
import cn.poco.storagesystemlibs.UploadInfo;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;

public class MyIStorage implements IStorage
{
	protected Context mContext;

	public MyIStorage(Context context)
	{
		mContext = context;
	}

	@Override
	public String GetTokenUrl()
	{
		if(SysConfig.IsDebug())
		{
			return "http://tw.adnonstop.com/beauty/app/api/beauty_camera/biz/beta/api/public/index.php?r=common/GetAliyunOSSUploadFlashToken";
		}
		else
		{
			return "http://open.adnonstop.com/beauty_camera/biz/prod/api/public/index.php?r=common/GetAliyunOSSUploadFlashToken";
		}
	}

	@Override
	public String GetUpdateMyWebUrl()
	{
		return null;
	}

	@Override
	public String MakeUpdateMyWebData(UpdateInfo info)
	{
		return StorageUtils.EncodeUpdateData(info, this);
	}

	@Override
	public UploadInfo GetUploadInfo(StorageStruct str, int num)
	{
		UploadInfo out = null;

		try
		{
			JSONObject json = new JSONObject();
			out = (UploadInfo)PocoWebUtils.Post(UploadInfo.class, GetTokenUrl(), false, json, null, null, this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	@Override
	public String GetAppName()
	{
		return "beauty_camera_android";
	}

	@Override
	public String GetAppVer()
	{
		return CommonUtils.GetAppVer(mContext);
	}

	@Override
	public String GetMKey()
	{
		return CommonUtils.GetIMEI(mContext);
	}
}

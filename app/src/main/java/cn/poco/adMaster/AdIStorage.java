package cn.poco.adMaster;

import android.content.Context;

import com.adnonstop.admasterlibs.AbsAdIStorage;
import com.adnonstop.admasterlibs.data.UploadData;

import cn.poco.storagesystemlibs.UpdateInfo;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;

/**
 * Created by Raining on 2017/8/21.
 * 商业上传阿里云
 */

public class AdIStorage extends AbsAdIStorage
{
	public AdIStorage(Context context, UploadData data)
	{
		super(context, data);
	}

	@Override
	public String GetAppName()
	{
		return "beauty_business";
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

	@Override
	public String GetTokenUrl()
	{
		if(SysConfig.IsDebug())
		{
			return "http://tw.adnonstop.com/zt/web/index.php?r=api/v1/oss/policy";
		}
		else
		{
			return "http://zt.adnonstop.com/index.php?r=api/v1/oss/policy";
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
		return null;
	}

	@Override
	public String GetUploadInfoAppName()
	{
		return "beauty_camera_android";
	}
}

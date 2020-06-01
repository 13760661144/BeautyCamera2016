package cn.poco.adMaster;

import android.content.Context;

import com.adnonstop.admasterlibs.AbsAdIStorage;
import com.adnonstop.admasterlibs.AbsUploadFile;
import com.adnonstop.admasterlibs.IAd;
import com.adnonstop.admasterlibs.data.UploadData;

import cn.poco.holder.ObjHandlerHolder;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;

/**
 * Created by Raining on 2017/8/21.
 * 商业上传图片/视频
 */

public class UploadFile extends AbsUploadFile
{
	protected String mUserId;
	protected String mUserName;
	protected String mUserSex;
	protected String mUserPhone;

	public UploadFile(Context context, UploadData data, IAd iAd, ObjHandlerHolder<Callback> callback)
	{
		super(context, data, iAd, callback);

		SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
		mUserId = info.GetPoco2Id(false);
		mUserName = info.GetPocoNick();
		mUserSex = info.GetPoco2Sex();
		mUserPhone = info.GetPoco2Phone();
	}

	@Override
	protected AbsAdIStorage GetAdIStorage(Context context, UploadData data)
	{
		return new AdIStorage(context, data);
	}

	@Override
	protected String GetUserId(Context context)
	{
		return mUserId;
	}

	@Override
	protected String GetUserName(Context context)
	{
		return mUserName;
	}

	@Override
	protected String GetUserSex(Context context)
	{
		return mUserSex;
	}

	@Override
	protected String GetUserPhone(Context context)
	{
		return mUserPhone;
	}
}

package cn.poco.setting;

import android.content.Context;
import android.content.SharedPreferences;

import cn.poco.system.FolderMgr;
import cn.poco.tianutils.CommonUtils;

public class SettingInfoMgr
{
	protected static SettingInfo s_info;

	protected static SettingInfo Read(Context context)
	{
		SettingInfo out = new SettingInfo();

		CommonUtils.SP_ReadSP(context, FolderMgr.SETTING_SP_NAME, out.m_data);

		return out;
	}

	public static boolean Save(Context context)
	{
		boolean out = false;

		if(s_info != null && s_info.m_change)
		{
			CommonUtils.SP_SaveMap(context, FolderMgr.SETTING_SP_NAME, s_info.m_data);
			s_info.DataChange(false);
		}

		return out;
	}

	public static SettingInfo GetSettingInfo(Context context)
	{
		if(s_info == null)
		{
			s_info = Read(context);
		}

		return s_info;
	}

	public static SharedPreferences GetSettingSP4Process(Context context)
	{
		return context.getSharedPreferences(FolderMgr.SETTING_SP_NAME, Context.MODE_MULTI_PROCESS);
	}
}

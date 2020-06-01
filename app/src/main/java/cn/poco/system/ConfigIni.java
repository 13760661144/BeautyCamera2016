package cn.poco.system;

import android.content.Context;
import android.os.Bundle;

import cn.poco.tianutils.CommonUtils;

public class ConfigIni
{
	public static String packTime = "";
	public static String miniVer = "";
	public static boolean hideAppMarket = false;
	public static boolean showChannelLogo = false;
	public static boolean hideBusiness = false;

	public static void readConfig(Context context)
	{
		Bundle bundle = CommonUtils.getApplicationMetaData(context);
		if(bundle != null)
		{
			packTime = bundle.getString("MY_PACK_TIME", "");
			miniVer = bundle.getString("MY_CHANNEL_VALUE", "");
			showChannelLogo = bundle.getBoolean("MY_SHOW_CHANNEL_LOGO", false);
			hideAppMarket = bundle.getBoolean("MY_HIDE_APP_MARKET", false);
			hideBusiness = bundle.getBoolean("MY_HIDE_BUSINESS", false);
		}
	}

	public static String getMiniVer()
	{
		return miniVer;
	}
}

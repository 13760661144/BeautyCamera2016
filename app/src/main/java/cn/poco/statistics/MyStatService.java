package cn.poco.statistics;

import android.content.Context;

import cn.poco.statisticlibs.AbsStatService;
import cn.poco.statisticlibs.IStat;
import cn.poco.system.AppInterface;
import cn.poco.system.ConfigIni;
import cn.poco.system.SysConfig;

/**
 * Created by Raining on 2017/3/31.
 * 统计服务
 */

public class MyStatService extends AbsStatService
{
	@Override
	protected synchronized IStat GetIStat(Context context)
	{
		return AppInterface.GetInstance(context);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		SysConfig.Read(this);
		ConfigIni.readConfig(this);
	}
}

package cn.poco.framework;

import cn.poco.adMaster.AdMaster;
import cn.poco.framework2.FrameworkApp;
import cn.poco.statistics.MyBeautyStat;

/**
 * Created by Raining on 2017/8/31.
 */

public class MyFramework2App extends FrameworkApp
{
	public static FrameworkApp getInstance()
	{
		return getInstance(MyFramework2App.class);
	}

	@Override
	public void onAppStart()
	{
		super.onAppStart();

		//统计获取用户id
		MyBeautyStat.checkLogin(mApp);
	}

	@Override
	public void onAllActivityDestroyed()
	{
		super.onAllActivityDestroyed();

		AdMaster.clearInstance();
	}
}

package cn.poco.framework;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.baidu.mobstat.StatService;

import cn.com.iresearch.mapptracker.IRMonitor;
import cn.poco.camera3.KeyBroadCastReceiver;
import cn.poco.framework2.BaseActivitySite;
import cn.poco.framework2.BaseFrameworkActivity;

/**
 * Created by Raining on 2017/11/3.
 * 实现{@link #getAppPackName(Context)}函数
 */

public abstract class BaseFwActivity<T extends BaseActivitySite> extends BaseFrameworkActivity<T>
{

	private KeyBroadCastReceiver myBroadCastReceiver;

	@Override
	protected void InitFinal(@Nullable Bundle savedInstanceState)
	{
		super.InitFinal(savedInstanceState);
		myBroadCastReceiver = new KeyBroadCastReceiver();
	}

	@Override
	protected String getAppPackName(Context context)
	{
		return "my.beautyCamera";
	}

	@Override
	protected void onResume()
	{
		if (myBroadCastReceiver != null)
		{
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			registerReceiver(myBroadCastReceiver, intentFilter);
		}

		super.onResume();

		try
		{
			IRMonitor.getInstance().onResume(this);
			StatService.onResume(this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause()
	{
		if (myBroadCastReceiver != null)
		{
			unregisterReceiver(myBroadCastReceiver);
		}
		try
		{
			IRMonitor.getInstance().onPause(this);
			StatService.onPause(this);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		super.onPause();
	}
}

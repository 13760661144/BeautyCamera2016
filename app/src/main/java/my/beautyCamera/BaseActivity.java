package my.beautyCamera;

import android.app.Activity;

import com.baidu.mobstat.StatService;

import cn.com.iresearch.mapptracker.IRMonitor;

/**
 * 无框架，只适合接受数据用
 */
public abstract class BaseActivity extends Activity
{
	@Override
	protected void onResume()
	{
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

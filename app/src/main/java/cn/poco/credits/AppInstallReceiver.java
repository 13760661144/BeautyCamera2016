package cn.poco.credits;

import android.content.Context;

import cn.poco.scorelibs.AbsAppInstall;
import cn.poco.scorelibs.AppName;

/**
 * 应用安装广播接收器
 * Created by MarkChan on 2016/6/12.
 */
public class AppInstallReceiver extends AbsAppInstall
{
	private static final String TAG = "AppInstallReceiver";

	@Override
	public void OnInstall(Context context, String pkgName, AppName name)
	{
		new MyAppInstall().OnInstall(context, pkgName, name);
	}
}
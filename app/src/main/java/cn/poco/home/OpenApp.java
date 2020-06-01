package cn.poco.home;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class OpenApp
{
	public static final int SUCCESS = 0;
	public static final int UPDATE = 1;
	public static final int DOWNLOAD = 2;
	private static final int POCO = 3;
	private static final int JANE = 4;
	private static final int PMIX = 5;
	private static final int INTERPHOTO = 6;
	private static final String s_janeName = "cn.poco.jane";
	private static final String s_pocoName = "my.PCamera";
	private static final String s_pMixName = "cn.poco.pMix";
	private static final String s_interPhotoName = "cn.poco.interphoto2";

	public static void openJane(Context context)
	{
		switch(GetAppState(context, s_janeName, JANE))
		{
			case SUCCESS:
			{
				openApp(context, s_janeName);
				break;
			}
			case UPDATE:
			case DOWNLOAD:
			{
				downloadApp(context, s_janeName);
				break;
			}
		}
	}

	public static void openPoco(Context context)
	{
		switch(GetAppState(context, s_pocoName, POCO))
		{
			case SUCCESS:
			{
				openApp(context, s_pocoName);
				break;
			}
			case UPDATE:
			case DOWNLOAD:
			{
				downloadApp(context, s_pocoName);
				break;
			}
		}
	}

	public static void openPMix(Context context)
	{
		switch(GetAppState(context, s_pMixName, PMIX))
		{
			case SUCCESS:
			{
				openApp(context, s_pMixName);
				break;
			}
			case UPDATE:
			case DOWNLOAD:
			{
				downloadApp(context, s_pMixName);
				break;
			}
		}
	}

	public static void openInterPhoto(Context context)
	{
		switch(GetAppState(context, s_interPhotoName, INTERPHOTO))
		{
			case SUCCESS:
			{
				openApp(context, s_interPhotoName);
				break;
			}
			case UPDATE:
			case DOWNLOAD:
			{
				downloadApp(context, s_interPhotoName);
				break;
			}
		}
	}

	public static void downloadApp(Context context, String packageName)
	{
		try
		{
			Uri uri = Uri.parse("market://details?id=" + packageName);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		catch(Exception e)
		{
			Toast.makeText(context, "还没有安装安卓市场，请先安装", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public static void openApp(Context context, String packageName)
	{
		try
		{
			PackageManager packageManager = context.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(packageName);
			context.startActivity(intent);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static int GetAppState(Context context, String packageName, int flag)
	{
		int out = SUCCESS;

		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			int versionCode = info.versionCode;
			switch(flag)
			{
				case POCO:
				{
					if(versionCode >= 80)
					{
						out = SUCCESS;
					}
					else
					{
						out = UPDATE;
					}
					break;
				}
				case JANE:
				{
					if(versionCode >= 4)
					{
						out = SUCCESS;
					}
					else
					{
						out = UPDATE;
					}
					break;
				}
				case PMIX:
				{
					if(versionCode >= 4)
					{
						out = SUCCESS;
					}
					else
					{
						out = UPDATE;
					}
					break;
				}
				case INTERPHOTO:
				{
					if(versionCode >= 1)
					{
						out = SUCCESS;
					}
					else
					{
						out = UPDATE;
					}
					break;
				}
				default:
					break;
			}

		}
		catch(Throwable e)
		{
			out = DOWNLOAD;
		}

		return out;
	}

}

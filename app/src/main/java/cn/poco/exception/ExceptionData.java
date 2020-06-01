package cn.poco.exception;

import android.content.Context;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import cn.poco.framework.BaseSite;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework2.IFramework;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.NetState;

public class ExceptionData
{
	public static String GetExceptionXML(Context context, Throwable ex)
	{
		String xml = null;
		if(context != null && ex != null)
		{
			xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><info>";
			xml += CollectDeviceInfo(context);
			xml += CollectErrorInfo(ex);
			xml += "</info>";
		}

		return xml;
	}

	/**
	 * 一定要在主线程运行
	 *
	 * @param context
	 * @param data
	 * @return
	 */
	public static String GetExceptionXML(Context context, String data)
	{
		String xml = null;
		if(context != null && data != null)
		{
			xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><info>";
			xml += CollectDeviceInfo(context);
			xml += "<err>MyLog--";
			xml += SafeString(data);
			xml += "</err>";
			xml += "</info>";
		}

		return xml;
	}

	// 收集系统信息
	protected static String CollectDeviceInfo(Context context)
	{
		String xml = "";
		// 收集硬件信息
		xml += "<system>" + SafeString(Build.VERSION.RELEASE) + "</system>";
		xml += "<brand>" + SafeString(Build.BRAND) + "</brand>";
		xml += "<manufacturer>" + SafeString(Build.MANUFACTURER) + "</manufacturer>";
		xml += "<fingerprint>" + SafeString(Build.FINGERPRINT) + "</fingerprint>";
		xml += "<model>" + SafeString(Build.MODEL) + "</model>";
		xml += "<cpu>" + SafeString(Build.CPU_ABI) + "</cpu>";
		xml += "<net>" + SafeString(NetState.GetConnectNet(context) + "") + "</net>";

		// 收集软件版本
		xml += "<ver>" + SafeString(SysConfig.GetAppVerNoSuffix(context)) + "</ver>";

		// 收集可用内存
		xml += "<mem>" + Runtime.getRuntime().maxMemory() / 1048576 + "</mem>";

		// 收集执行路径
		xml += "<path>" + MakeChain(context) + "</path>";

		return xml;
	}

	protected static String MakeChain(Context context)
	{
		String out = "";

		IFramework framework = MyFramework2App.getInstance().getFramework();
		if(framework != null)
		{
			ArrayList<BaseSite>[] siteList = framework.GetSiteList();
			if(siteList != null)
			{
				try
				{
					boolean first = true;
					ArrayList<BaseSite> arr;
					BaseSite temp;
					int len = siteList.length;
					for(int i = 0; i < len; i++)
					{
						arr = siteList[i];
						if(arr != null)
						{
							int size = arr.size();
							if(size > 0)
							{
								if(i != 0)
								{
									out += '|';
								}
								for(int j = 0; j < size; j++)
								{
									temp = arr.get(j);
									if(temp != null)
									{
										if(first)
										{
											first = false;
										}
										else
										{
											out += "--";
										}
										out += temp.GetID();
									}
								}
							}
						}
					}
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					out = "";
				}
			}
		}

		return out;
	}

	// 收集错误信息
	protected static String CollectErrorInfo(Throwable ex)
	{
		String xml = "";
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		ex.printStackTrace(printWriter);

		Throwable cause = ex.getCause();
		while(cause != null)
		{
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		printWriter.close();
		xml += "<err>" + SafeString(info.toString()) + "</err>";
		return xml;
	}

	public static String SafeString(String str)
	{
		if(str != null)
		{
			return str.replaceAll("<", "[").replaceAll(">", "]").replaceAll("&", "_");
		}
		else
		{
			return "";
		}
	}
}

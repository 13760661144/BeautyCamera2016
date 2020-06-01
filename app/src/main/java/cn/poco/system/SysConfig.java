package cn.poco.system;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;

import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmpV2;

/**
 * 系统内部配置信息
 *
 * @author POCO
 */
public class SysConfig
{
	protected static final HashMap<String, String> s_data = new HashMap<String, String>();
	protected static boolean s_init = false;
	protected static boolean s_change = false;

	protected static final String SDCARD_PATH = "SDCARD_PATH";//SDCARD的路径,以后扩展用户可修改
	protected static final String APP_FILE_NAME = "APP_FILE_NAME"; //程序文件夹名
	protected static final String IS_DEBUG = "IS_DEBUG";

	protected static final String APP_SKIN_COLOR = "APP_SKIN_COLOR"; //app皮肤颜色
	//public static int s_skinColor = Color.parseColor("#e75988"); //0为系统默认
	public static int s_skinColor = 0xffe75988; //0为系统默认

	protected static final String APP_SKIN_COLOR1 = "APP_SKIN_COLOR1"; //app皮肤渐变颜色1
	//public static int s_skinColor1 = Color.parseColor("#fcdcd3");
	public static int s_skinColor1 = 0xfffcdcd3;

	protected static final String APP_SKIN_COLOR2 = "APP_SKIN_COLOR2"; //app皮肤渐变颜色2
	//public static int s_skinColor2 = Color.parseColor("#ef7491");
	public static int s_skinColor2 = 0xffef7491;

	protected static final String APP_SKIN_COLOR_TYPE = "APP_SKIN_COLOR_TYPE"; //app皮肤渐变颜色类型
	public static int s_skinColorType = 0; //0 左上右下，1为垂直  2 右上左下

	protected static final String APP_SKIN_COLOR_INDEX = "APP_SKIN_COLOR_INDEX"; //主题下标
	public static int s_skinColorIndex = 0;

	public static final String APP_TEST_VER = "88.8.8";

	public synchronized static void Read(Context context)
	{
		if(!s_init)
		{
			CommonUtils.SP_ReadSP(context, FolderMgr.SYSTEM_CONFIG_SP_NAME, s_data);
			s_init = true;

			try
			{
				String v = s_data.get(APP_SKIN_COLOR);
				if(v != null && v.length() > 0)
				{
					s_skinColor = Integer.parseInt(v);
				}

				v = s_data.get(APP_SKIN_COLOR1);
				if(v != null && v.length() > 0)
				{
					s_skinColor1 = Integer.parseInt(v);
				}
				v = s_data.get(APP_SKIN_COLOR2);
				if(v != null && v.length() > 0)
				{
					s_skinColor2 = Integer.parseInt(v);
				}
				v = s_data.get(APP_SKIN_COLOR_TYPE);
				if(v != null && v.length() > 0)
				{
					s_skinColorType = Integer.parseInt(v);
				}
				v = s_data.get(APP_SKIN_COLOR_INDEX);
				if(v != null && v.length() > 0)
				{
					s_skinColorIndex = Integer.parseInt(v);
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	public synchronized static void Save(Context context)
	{
		if(s_init && s_change)
		{
			CommonUtils.SP_SaveMap(context, FolderMgr.SYSTEM_CONFIG_SP_NAME, s_data);
		}
	}

	public static void SetSkinColor(int color)
	{
		s_skinColor = color;
		if(s_skinColor == 0)
		{
			s_data.remove(APP_SKIN_COLOR);
		}
		else
		{
			s_data.put(APP_SKIN_COLOR, Integer.toString(color));
		}
		s_change = true;
	}

	public static void SetSkinGradientColor(int color1, int color2)
	{
		s_skinColor1 = color1;
		s_skinColor2 = color2;

		if(s_skinColor1 == 0)
		{
			s_data.remove(APP_SKIN_COLOR1);
		}
		else
		{
			s_data.put(APP_SKIN_COLOR1, Integer.toString(color1));
		}
		if(s_skinColor2 == 0)
		{
			s_data.remove(APP_SKIN_COLOR2);
		}
		else
		{
			s_data.put(APP_SKIN_COLOR2, Integer.toString(color2));
		}
		s_change = true;
	}

	public static void SetSkinGradientType(int type)
	{
		if(type < 0 || type > 2)
		{
			type = 0;
		}
		s_skinColorType = type;
		s_data.put(APP_SKIN_COLOR_TYPE, Integer.toString(type));
		s_change = true;
	}

	public static void SetSkinGradientIndex(int index)
	{
		if(index < 0)
		{
			index = 0;
		}
		s_skinColorIndex = index;
		s_data.put(APP_SKIN_COLOR_INDEX, Integer.toString(index));
		s_change = true;
	}

	public static String GetSDCardPath()
	{
		String out = s_data.get(SDCARD_PATH);

		if(out == null || out.length() <= 0)
		{
			out = Environment.getExternalStorageDirectory().getAbsolutePath();
		}

		return out;
	}

	public static String GetAppFileName()
	{
		String out = s_data.get(APP_FILE_NAME);

		if(out == null || out.length() <= 0)
		{
			out = "beautyCamera";
		}

		return out;
	}

	/**
	 * @return 程序文件夹路径
	 */
	public static String GetAppPath()
	{
		return GetSDCardPath() + File.separator + GetAppFileName();
	}

	public synchronized static boolean IsDebug()
	{
		boolean out = false;

		String temp = s_data.get(IS_DEBUG);
		if(temp != null)
		{
			out = true;
		}

		return out;
	}

	public synchronized static void SetDebug(boolean debug)
	{
		if(debug)
		{
			s_data.put(IS_DEBUG, "1");
		}
		else
		{
			s_data.remove(IS_DEBUG);
		}
		s_change = true;
	}

	public static String GetAppVer(Context context)
	{
		String out = null;

		if(IsDebug())
		{
			out = APP_TEST_VER;
		}
		else if(context != null)
		{
			try
			{
				out = CommonUtils.GetAppVer(context);
				String miniver = ConfigIni.getMiniVer();
				if(miniver != null)
				{
					out += miniver;
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return out;
	}

	public static String GetAppVer2(Context context)
	{
		String out = null;

		if(IsDebug())
		{
			String miniver = ConfigIni.getMiniVer();
			if(miniver != null)
			{
				out = APP_TEST_VER + miniver;
			}
			else
			{
				out = APP_TEST_VER;
			}
		}
		else if(context != null)
		{
			try
			{
				out = CommonUtils.GetAppVer(context);
				String miniver = ConfigIni.getMiniVer();
				if(miniver != null)
				{
					out += miniver;
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return out;
	}

	/**
	 * 有debug版本号判断
	 *
	 * @param context
	 * @return
	 */
	public static String GetAppVerNoSuffix(Context context)
	{
		String out = null;

		if(IsDebug())
		{
			out = APP_TEST_VER;
		}
		else
		{
			out = CommonUtils.GetAppVer(context);
		}

		return out;
	}

	public static boolean IsTestVer(String ver)
	{
		boolean out = false;

		if(ver != null && ver.contains(APP_TEST_VER))
		{
			out = true;
		}

		return out;
	}

	public static int GetPhotoSize(Context context)
	{
		return GetPhotoSize(context, SettingInfoMgr.GetSettingInfo(context).GetQualityState());
	}

	public static int GetPhotoSize(Context context, boolean quality)
	{
		int out = (int)Math.sqrt(Runtime.getRuntime().maxMemory() * MakeBmpV2.MEM_SCALE / 4);

		if(!quality)
		{
			out = out / 2;
		}

		if(out < 640)
		{
			out = 640;
		}

		return out;
	}
}

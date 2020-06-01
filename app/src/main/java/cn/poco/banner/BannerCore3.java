package cn.poco.banner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.adnonstop.admasterlibs.AdUtils;
import com.adnonstop.missionhall.ui.activities.MissionInfoActivity;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.poco.blogcore.Tools;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.SysConfig;
import cn.poco.taskCenter.MissionHelper;

public class BannerCore3
{
	public static class CmdStruct
	{
		public String m_cmd;
		public String[] m_params;

		public static HashMap<String, String> DecodeParams(String[] params)
		{
			HashMap<String, String> out = new HashMap<>();

			if(params != null)
			{
				String[] pair;
				for(String p : params)
				{
					pair = p.split("=");
					if(pair.length == 2)
					{
						out.put(pair[0], pair[1]);
					}
				}
			}

			return out;
		}

		public HashMap<String, String> GetMap()
		{
			return DecodeParams(m_params);
		}
	}

	public static CmdStruct GetCmdStruct(String cmdStr)
	{
		CmdStruct out = null;

		if(cmdStr != null)
		{
			String cmd = null;
			String[] params = null;

			int pos = cmdStr.indexOf("://");
			if(pos > -1)
			{
				int pos2 = cmdStr.indexOf("/?", pos + 3);
				if(pos2 > -1)
				{
					cmd = cmdStr.substring(pos + 3, pos2);
					pos = pos2 + 1;
				}
				else
				{
					cmd = cmdStr.substring(0, pos);
					pos += 3;
				}
			}
			int pos3 = cmdStr.indexOf("?", pos);
			if(pos3 > -1)
			{
				pos = pos3 + 1;
			}
			if(pos > -1 && pos < cmdStr.length())
			{
				String temp = cmdStr.substring(pos);
				params = temp.split("&");
			}

			out = new CmdStruct();
			out.m_cmd = cmd;
			out.m_params = params;
		}

		return out;
	}

	// 最后一个可变参数是用于运营专区需要的参数，可以不传.
	public static void ExecuteCommand(Context context, String cmdStr, CmdCallback cb, Object... args)
	{
		try
		{
			if(cmdStr != null && cb != null)
			{
				CmdStruct struct = GetCmdStruct(cmdStr);
				if(struct != null)
				{
					if(struct.m_cmd != null)
					{
						String comefrom = null;
						if(args != null && args.length > 0 && args[0] instanceof String)
						{
							comefrom = (String)args[0];
						}
						String cmd = struct.m_cmd.toLowerCase(Locale.ENGLISH);
						//注意全部用小写判断
						if(cmd.equals("jane"))
						{
							cb.OpenJane(context);
						}
						else if(cmd.equals("pococamera"))
						{
							cb.OpenPocoCamera(context);
						}
						else if(cmd.equals("artcamera20140919"))
						{
							cb.OpenPocoMix(context);
						}
						else if(cmd.equals("intercamera"))
						{
							cb.OpenInterPhoto(context);
						}
						else if(cmd.equals("beautycamera"))
						{
							HashMap<String, String> map = struct.GetMap();
							String value;
							map.put("comeFrom", comefrom);
							if((value = map.get("open")) != null)
							{
								if(map.size() > 1)
								{
									String[] strArr = new String[map.size() - 1];
									int i = 0;
									for(Map.Entry<String, String> entry : map.entrySet())
									{
										String k = entry.getKey();
										if(k != null && !k.equals("open"))
										{
											String v = entry.getValue();
											if(v == null)
											{
												v = "";
											}
											strArr[i] = k + '=' + v;
											i++;
										}
									}
									cb.OpenPage(context, Integer.parseInt(value), strArr);
								}
								else
								{
									comefrom = "comeFrom=" + comefrom;
									cb.OpenPage(context, Integer.parseInt(value), comefrom);
								}
							}
						}
						else if(cmd.equals("advbeauty"))
						{
							if(struct.m_params != null && struct.m_params.length > 0)
							{
								String[] pair = struct.m_params[0].split("=");
								if(pair.length == 2)
								{
									if(pair[0].equals("channel_value"))
									{
										if(pair[1].equals("pocoprintlomo"))
										{
											cb.OpenPrintPage(context);
										}
										else
										{
											ArrayList<String> arr = new ArrayList<>();
											arr.add(pair[1]);
											if(args != null)
											{
												for(Object obj : args)
												{
													if(obj instanceof String)
													{
														arr.add((String)obj);
													}
												}
											}
											cb.OpenBusinessPage(context, arr.toArray(new String[arr.size()]));
										}
									}
								}
							}
						}
						else if(cmd.equals("inapp"))
						{
							if(struct.m_params != null && struct.m_params.length > 0)
							{
								String gotoPage = "";
								String themeId = "";
								for(int i = 0; i < struct.m_params.length; i++)
								{
									String[] pair = struct.m_params[i].split("=");
									if(pair.length == 2)
									{
										if(pair[0].equals("goto_page"))
										{
											gotoPage = pair[1];
										}
										else if(pair[0].equals("itemID"))
										{
											themeId = pair[1];
										}
									}
								}
								if(gotoPage.equals("online_resources"))
								{
									comefrom = "comeFrom=" + comefrom;
									cb.OpenResourcePage(context, themeId, comefrom);
								}
							}
						}
						else if(cmd.equals("pocoprint"))
						{
							cb.OpenPrintPage(context);
						}
						else if(cmd.equals("action_externalweb") && struct.m_params != null && struct.m_params.length > 0)
						{
							String param = struct.m_params[0];
							if(param != null)
							{
								String[] pair = param.split("=");
								if(pair.length >= 2)
								{
									String url = pair[1];
									if(pair.length >= 3)
									{
										for(int i = 2; i < pair.length; i++)
										{
											url += "=" + pair[i];
										}
									}
									url = URLDecoder.decode(url, "UTF-8");
									cb.OpenSystemWebPage(context, url);
								}
							}
						}
						else if(cmd.equals("action_insideweb") && struct.m_params != null && struct.m_params.length > 0)
						{
							String param = struct.m_params[0];
							if(param != null)
							{
								String[] pair = param.split("=");
								if(pair.length >= 2)
								{
									String url = pair[1];
									if(pair.length >= 3)
									{
										for(int i = 2; i < pair.length; i++)
										{
											url += "=" + pair[i];
										}
									}
									url = URLDecoder.decode(url, "UTF-8");
									if(args.length > 0 && args[0] instanceof CampaignInfo)
									{
										CampaignInfo campaignInfo = (CampaignInfo)args[0];
										String originOpenUrl = campaignInfo.getOpenUrl();
										CampaignInfo temp = campaignInfo.clone();

										temp.setOpenUrl(url);
										cb.OpenMyWebPage(context, temp, originOpenUrl);
									}
									else
									{
										cb.OpenMyWebPage(context, url);
									}
								}
							}
						}
						else if(cmd.equals("action_share"))
						{
							cb.GoToShare(context, struct.m_params);
						}
						else if(cmd.equals("openapp"))
						{
							HashMap<String, String> map = struct.GetMap();
							String value;
							if((value = map.get("appurl")) != null)
							{
								value = URLDecoder.decode(value, "UTF-8");
								String pn = map.get("pkgname");
								if(pn != null && pn.length() > 0 && Tools.checkApkExist(context, pn = URLDecoder.decode(pn, "UTF-8")))
								{
									PackageManager packageManager = context.getPackageManager();
									Intent intent = packageManager.getLaunchIntentForPackage(pn);
									context.startActivity(intent);
								}
								else
								{
									cb.OpenSystemWebPage(context, value);
								}
							}
						}
						else if(cmd.equals("missionhall"))
						{
							MissionHelper.getInstance().OpenTaskCenter(context, cmdStr, cb);
						}
						else
						{
							cb.OpenWebPage(context, cmdStr);
						}
					}
					else
					{
						cb.OpenWebPage(context, cmdStr);
					}
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public static boolean IsOutsideCmd(String cmdStr)
	{
		boolean out = false;
		CmdStruct struct = GetCmdStruct(cmdStr);
		if(struct != null && struct.m_cmd != null)
		{
			if(struct.m_cmd.equals("jane") || struct.m_cmd.equals("pococamera") || struct.m_cmd.equals("ArtCamera20140919"))
			{
				out = true;
			}
		}
		return out;
	}

	public static String GetValue(String[] args, String key)
	{
		String out = null;

		try
		{
			if(args != null)
			{
				for(String temp : args)
				{
					if(temp.startsWith(key))
					{
						String[] arr = temp.split("=");
						out = arr[1];
						break;
					}
				}
			}
		}
		catch(Throwable e)
		{
		}

		return out;
	}

	public static int GetIntValue(String[] args, String key)
	{
		int out = 0;

		try
		{
			String v = GetValue(args, key);
			if(v != null)
			{
				out = Integer.parseInt(v);
			}
		}
		catch(Throwable e)
		{
		}

		return out;
	}

	public interface CmdCallback
	{
		/**
		 * @param code 0:美颜
		 *             1:美型(瘦身)
		 *             2:彩妆
		 *             3:滤镜
		 *             4:美化(不打开分类)
		 *             5:裁剪
		 *             6:相框
		 *             7:贴图
		 *             8:毛玻璃
		 *             9:马赛克
		 *             10:指尖魔法
		 *             <p>
		 *             16:镜头(全功能)
		 *             21:视频(动态帖纸)
		 *             <p>
		 *             22:用户信息页
		 *             23:云相册
		 *             <p>
		 *             26:祛痘
		 *             27:祛眼袋
		 *             28:亮眼
		 *             29:大眼
		 *             30:高鼻梁
		 *             31:微笑
		 *             32:一键萌装
		 *             33:返回首页(任务大厅用)
		 *             34:首页-运营中心(用于分享页banner)
		 *             35:gif
		 *             36:任务大厅(仅适用于首页大圆)
		 *             37:福利社
		 *             38:用户信息页(完成任务用)
		 *             39:镜头萌妆照
		 *             40:镜头高清拍照
		 *             41:视频+萌妆照(默认打开萌妆照)
		 *             42:只拍照(相当于只显示 高清拍照+萌妆照,默认打开萌妆照)
		 *             43:视频+萌妆照+gif(默认萌妆照)
		 *             44:打开相册(社区用)
		 * @param args
		 */
		void OpenPage(Context context, int code, String... args);

		/**
		 * @param args [0]URL
		 */
		void OpenWebPage(Context context, String... args);

		/**
		 * @param args [0]URL
		 */
		void OpenMyWebPage(Context context, Object... args);

		/**
		 * @param args [0]URL
		 */
		void OpenSystemWebPage(Context context, String... args);

		/**
		 * @param args [0]themeID
		 */
		void OpenPocoCamera(Context context, String... args);

		void OpenPocoMix(Context context, String... args);

		void OpenJane(Context context, String... args);

		void OpenInterPhoto(Context context, String... args);

		void OpenResourcePage(Context context, String... args);

		/**
		 * @param args [0]channel_value
		 */
		void OpenBusinessPage(Context context, String... args);

		void OpenPrintPage(Context context, String... args);

		void GoToShare(Context context, String... args);
	}

	public static void OpenUrl(Context context, String protocolUrl, OpenUrlCallback cb)
	{
		try
		{
			BannerCore3.CmdStruct struct = BannerCore3.GetCmdStruct(protocolUrl);
			if(struct != null && struct.m_cmd != null)
			{
				String cmd = struct.m_cmd.toLowerCase(Locale.ENGLISH);
				if((cmd.equals("action_insideweb") || cmd.equals("action_externalweb")) && struct.m_params != null && struct.m_params.length > 0)
				{
					String param = struct.m_params[0];
					if(param != null)
					{
						String[] pair = param.split("=");
						if(pair.length >= 2)
						{
							String url = AdUtils.AdDecodeUrl(context, URLDecoder.decode(pair[1], "UTF-8"));
							if(cmd.equals("action_insideweb"))
							{
								cb.OpenMyWeb(context, url);
							}
							else
							{
								cb.OpenSystemWeb(context, url);
							}
							return;
						}
					}
				}
			}

			String url = AdUtils.AdDecodeUrl(context, protocolUrl);
			if(url.contains(".poco.cn"))
			{
				cb.OpenMyWeb(context, url);
			}
			else
			{
				cb.OpenSystemWeb(context, url);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public interface OpenUrlCallback
	{
		public void OpenMyWeb(Context context, String url);

		public void OpenSystemWeb(Context context, String url);
	}
}

package cn.poco.resource;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Xml;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;

import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.system.SysConfig;

/**
 * Created by Raining on 2017/10/7.
 */

public class LockResMgr2 extends BaseResMgr<LockRes, ArrayList<LockRes>>
{
	protected final String THEME_CLOUD_CACHE_PATH = DownloadMgr.getInstance().LOCK_PATH + "/theme_cache.xxxx";
	protected final String THEME_CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/config/android_v1703.php?ver=" + SysConfig.GetAppVerNoSuffix(MyFramework2App.getInstance().getApplicationContext());

	public int m_unlockCaiZhuang = LockRes.SHARE_TYPE_NONE;

	protected ArrayList<LockRes> m_glassLockArr;
	protected ArrayList<LockRes> m_mosaicLockArr;
	protected ArrayList<LockRes> m_videoFaceLockArr;

	private static LockResMgr2 sInstance;

	private LockResMgr2()
	{
	}

	public synchronized static LockResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new LockResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<LockRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<LockRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<LockRes> arr, LockRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<LockRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		return null;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<LockRes> arr)
	{
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.LOCK_THEME_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return 0;
	}

	@Override
	protected LockRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		return null;
	}

	private static int parseUnlock(String string)
	{
		if(string != null)
		{
			if(string.equals("weixin"))
			{
				return LockRes.SHARE_TYPE_WEIXIN;
			}
			else if(string.equals("comment"))
			{
				return LockRes.SHARE_TYPE_MARKET;
			}
		}
		return LockRes.SHARE_TYPE_NONE;
	}

	@Override
	protected ArrayList<LockRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
	{
		ArrayList<LockRes> out = null;

		try
		{
			if(data != null)
			{
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(new StringReader(new String((byte[])data)));
				int event = parser.getEventType();
				String name = "";
				String temp = "";
				String tag = "";
				LockRes info = null;
				out = new ArrayList<>();
				while(event != XmlPullParser.END_DOCUMENT)
				{
					switch(event)
					{
						case XmlPullParser.START_TAG:
						{
							name = parser.getName();
							switch(name)
							{
								case "unlock_caizhuang":
									temp = parser.nextText();
									if(temp != null)
									{
										m_unlockCaiZhuang = parseUnlock(temp);
									}
									break;

								case "unlock_theme":
									tag = name;
									break;

								case "item":
									if(tag.equals("unlock_theme"))
									{
										temp = parser.getAttributeValue(null, "id");
										if(temp != null && temp.length() > 0)
										{
											info = new LockRes();
											info.m_id = Integer.parseInt(temp);
											out.add(info);
										}
									}
									break;

								case "unlock":
									if(tag.equals("unlock_theme") && info != null)
									{
										temp = parser.nextText();
										info.m_shareType = parseUnlock(temp);
									}
									break;

								case "weixin_img":
									if(tag.equals("unlock_theme") && info != null)
									{
										info.url_shareImg = parser.nextText();
									}
									break;

								case "weixin_title":
									if(tag.equals("unlock_theme") && info != null)
									{
										info.m_name = parser.nextText();
									}
									break;

								case "url":
									if(tag.equals("unlock_theme") && info != null)
									{
										info.m_shareLink = parser.nextText();
									}
									break;
							}
						}
						break;
					}
					event = parser.next();
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		if(out != null)
		{
			int len = out.size();
			LockRes temp;
			for(int i = 0; i < len; i++)
			{
				temp = out.get(i);
				if(temp.m_shareType == LockRes.SHARE_TYPE_NONE)
				{
					out.remove(i);
					i--;
					len--;
				}
			}
		}

		return out;
	}

	@Override
	protected String GetSdcardPath(Context context)
	{
		return null;
	}

	@Override
	protected int GetNewJsonVer()
	{
		return 0;
	}

	@Override
	protected String GetCloudUrl(Context context)
	{
		return THEME_CLOUD_URL;
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return THEME_CLOUD_CACHE_PATH;
	}

	@Override
	public LockRes GetItem(ArrayList<LockRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	protected static ArrayList<LockRes> GetGlassLockArr(String data)
	{
		ArrayList<LockRes> out = null;

		if(data != null)
		{
			try
			{
				JSONArray jsonArr = new JSONArray(data);
				int arrLen = jsonArr.length();
				JSONObject jsonObj;
				String temp;
				LockRes info;
				out = new ArrayList<>();
				for(int i = 0; i < arrLen; i++)
				{
					info = null;
					jsonObj = jsonArr.getJSONObject(i);
					temp = jsonObj.getString("lockType");
					if(temp != null)
					{
						if(temp.equals("comment"))
						{
							info = new LockRes();
							info.m_shareType = LockRes.SHARE_TYPE_MARKET;
						}
						else if(temp.equals("weixin"))
						{
							info = new LockRes();
							info.m_shareType = LockRes.SHARE_TYPE_WEIXIN;
						}
						if(info != null)
						{
							info.m_type = BaseRes.TYPE_NETWORK_URL;
							info.m_id = Integer.parseInt(jsonObj.getString("id"));
							info.url_showImg = jsonObj.getString("lockPage");
							info.m_showContent = jsonObj.getString("lockIntroduce");
                            try {
                                if (!TextUtils.isEmpty(info.m_showContent) && info.m_showContent.contains("%")) {
                                    info.m_showContent = URLDecoder.decode(info.m_showContent, "UTF-8");
                                }
                            } catch (Throwable ignored) {}
                            info.m_shareContent = jsonObj.getString("shareContent");
							if(jsonObj.has("name"))
							{
								info.m_name = jsonObj.getString("name");
							}
							else if(jsonObj.has("title"))
							{
								info.m_name = jsonObj.getString("title");
							}
							info.url_shareImg = jsonObj.getString("shareThumb");
							info.m_shareLink = jsonObj.getString("shareURL");

							out.add(info);
						}
					}
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return out;
	}

	protected static ArrayList<LockRes> GetMosaicLockArr(String data)
	{
		ArrayList<LockRes> out = null;

		if(data != null)
		{
			try
			{
				JSONArray jsonArr = new JSONArray(data);
				int arrLen = jsonArr.length();
				JSONObject jsonObj;
				String temp;
				LockRes info;
				out = new ArrayList<>();
				for(int i = 0; i < arrLen; i++)
				{
					info = null;
					jsonObj = jsonArr.getJSONObject(i);
					temp = jsonObj.getString("lockType");
					if(temp != null)
					{
						if(temp.equals("comment"))
						{
							info = new LockRes();
							info.m_shareType = LockRes.SHARE_TYPE_MARKET;
						}
						else if(temp.equals("weixin"))
						{
							info = new LockRes();
							info.m_shareType = LockRes.SHARE_TYPE_WEIXIN;
						}
						if(info != null)
						{
							info.m_type = BaseRes.TYPE_NETWORK_URL;
							info.m_id = Integer.parseInt(jsonObj.getString("id"));
							info.url_showImg = jsonObj.getString("lockPage");
							info.m_showContent = jsonObj.getString("lockIntroduce");
							info.m_shareContent = jsonObj.getString("shareContent");
							if(jsonObj.has("name"))
							{
								info.m_name = jsonObj.getString("name");
							}
							else if(jsonObj.has("title"))
							{
								info.m_name = jsonObj.getString("title");
							}
							info.url_shareImg = jsonObj.getString("shareThumb");
							info.m_shareLink = jsonObj.getString("shareURL");

							out.add(info);
						}
					}
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return out;
	}

	protected static ArrayList<LockRes> GetVideoFaceLockArr(String data)
	{
		ArrayList<LockRes> out = null;

		if(data != null)
		{
			try
			{
				JSONArray jsonArr = new JSONArray(data);
				int arrLen = jsonArr.length();
				JSONObject jsonObj;
				String temp;
				LockRes info;
				out = new ArrayList<>();
				for(int i = 0; i < arrLen; i++)
				{
					info = null;
					jsonObj = jsonArr.getJSONObject(i);
					temp = jsonObj.getString("lockType");
					if(temp != null)
					{
						if(temp.equals("comment"))
						{
							info = new LockRes();
							info.m_shareType = LockRes.SHARE_TYPE_MARKET;
						}
						else if(temp.equals("weixin"))
						{
							info = new LockRes();
							info.m_shareType = LockRes.SHARE_TYPE_WEIXIN;
						}
						if(info != null)
						{
							info.m_type = BaseRes.TYPE_NETWORK_URL;
							info.m_id = Integer.parseInt(jsonObj.getString("id"));
							info.url_showImg = jsonObj.getString("lockPage");
							info.m_showContent = jsonObj.getString("lockIntroduce");
							if(jsonObj.has("shareContent"))
							{
								info.m_shareContent = jsonObj.getString("shareContent");
							}
							if(jsonObj.has("name"))
							{
								info.m_name = jsonObj.getString("name");
							}
							else if(jsonObj.has("title"))
							{
								info.m_name = jsonObj.getString("title");
							}
							info.url_shareImg = jsonObj.getString("shareThumb");
							info.m_shareLink = jsonObj.getString("shareURL");

							out.add(info);
						}
					}
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return out;
	}

	public ArrayList<LockRes> getThemeLockArr()
	{
		return sync_ar_GetCloudCacheRes(MyFramework2App.getInstance().getApplicationContext(), null);
	}

	public ArrayList<LockRes> getGlassLockArr()
	{
		return m_glassLockArr;
	}

	public ArrayList<LockRes> getMosaicLockArr()
	{
		return m_mosaicLockArr;
	}

	public ArrayList<LockRes> getVideoFaceLockArr()
	{
		return m_videoFaceLockArr;
	}

	public void decodeGlassLockArr(String data)
	{
		final ArrayList<LockRes> arr = GetGlassLockArr(data);
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				m_glassLockArr = arr;
			}
		});
	}

	public void decodeMosaicLockArr(String data)
	{
		final ArrayList<LockRes> arr = GetMosaicLockArr(data);
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				m_mosaicLockArr = arr;
			}
		});
	}

	public void decodeVideoFaceLockArr(String data)
	{
		final ArrayList<LockRes> arr = GetVideoFaceLockArr(data);
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				m_videoFaceLockArr = arr;
			}
		});
	}
}

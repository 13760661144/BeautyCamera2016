package cn.poco.resource;

import android.content.Context;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;

import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.protocol.PocoProtocol;
import cn.poco.system.AppInterface;
import cn.poco.system.ConfigIni;
import cn.poco.system.SysConfig;

/**
 * Created by Raining on 2017/9/26.
 */

public class BannerResMgr2 extends BaseResMgr<BannerRes, ArrayList<BannerRes>>
{
	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().BANNER_PATH + "/banner5.xxxx"; //资源集合

	protected static final String CLOUD_URL = "http://api-a-m-s.adnonstop.com/beauty_camera/get_ads.php?ctype=android";
	public static final String B20 = "index_pop1"; //4.0.0首页弹窗
	public static final String B21 = "index_pop2"; //4.0.0美化前
	public static final String B22 = "index_pop3"; //4.0.0美化后

	private static BannerResMgr2 sInstance;

	private BannerResMgr2()
	{
	}

	public synchronized static BannerResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new BannerResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<BannerRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<BannerRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<BannerRes> arr, BannerRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<BannerRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		return null;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<BannerRes> arr)
	{
	}

	@Override
	protected Object sync_raw_ReadCloudData(Context context, DataFilter filter)
	{
		byte[] bannerData = null;

		try
		{
			JSONObject json = new JSONObject();
			json.put("pos", B20 + "," + B21 + "," + B22);
			json.put("ch", ConfigIni.getMiniVer().replace("_", ""));
			bannerData = PocoProtocol.Get(GetCloudUrl(context), SysConfig.GetAppVerNoSuffix(context), AppInterface.GetInstance(context).GetAppName(), false, AppInterface.GetInstance(context).GetMKey(), json, null);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return bannerData;
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return CLOUD_CACHE_PATH;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return 0;
	}

	@Override
	protected BannerRes ReadResItem(JSONObject model, boolean isPath)
	{
		BannerRes bannerRes = null;

		if(model != null)
		{
			try
			{
				bannerRes = new BannerRes();
				if(model.has("begin_time"))
				{
					String temp = model.getString("begin_time");
					if(temp != null && temp.length() > 0)
					{
						bannerRes.m_beginTime = Long.parseLong(temp);
					}
				}
				if(model.has("end_time"))
				{
					String temp = model.getString("end_time");
					if(temp != null && temp.length() > 0)
					{
						bannerRes.m_endTime = Long.parseLong(temp);
					}
				}
				if(model.has("pos"))
				{
					bannerRes.m_type = BaseRes.TYPE_NETWORK_URL;
					bannerRes.m_pos = model.getString("pos");
				}
				if(model.has("url") && !ConfigIni.hideBusiness)
				{
					String temp = model.getString("url");
					//非隐藏商业模式显示正常的
					if(temp != null)
					{
						bannerRes.m_cmdStr = URLDecoder.decode(temp, "UTF-8");
					}
				}
				if(model.has("download_url") && !ConfigIni.hideBusiness)
				{
					String temp = model.getString("download_url");
					//隐藏商业模式显示特定的
					if(temp != null)
					{
						bannerRes.m_cmdStr = URLDecoder.decode(temp, "UTF-8");
					}
				}
				if(model.has("pic"))
				{
					String temp = model.getString("pic");
					if(temp != null)
					{
						bannerRes.url_thumb = URLDecoder.decode(temp, "UTF-8");
					}
				}
				if(model.has("title"))
				{
					String temp = model.getString("title");
					if(temp != null)
					{
						bannerRes.m_name = URLDecoder.decode(temp, "UTF-8");
					}
				}
				if(model.has("ad_banner"))
				{
					String temp = model.getString("ad_banner");
					if(temp != null)
					{
						bannerRes.m_tjClickUrl = URLDecoder.decode(temp, "UTF-8");
					}
				}
				if(model.has("ad_show"))
				{
					String temp = model.getString("ad_show");
					if(temp != null)
					{
						bannerRes.m_tjShowUrl = URLDecoder.decode(temp, "UTF-8");
					}
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				bannerRes = null;
			}
		}

		return bannerRes;
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
		return CLOUD_URL;
	}

	@Override
	protected ArrayList<BannerRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object rawData)
	{
		ArrayList<BannerRes> out = null;
		try
		{
			if(rawData != null)
			{
				out = new ArrayList<>();
				String bannerJson = new String((byte[])rawData);
				//Log.e("Banner" , bannerJson);
				//System.out.println(bannerJson);
				JSONObject jsonObject = new JSONObject(bannerJson);
				JSONObject data = jsonObject.getJSONObject("data");
				JSONObject ret_data = data.getJSONObject("ret_data");
				JSONArray models = ret_data.getJSONArray("lists");
				BannerRes item;
				Object obj;
				for(int i = 0; i < models.length(); i++)
				{
					obj = models.get(i);
					if(obj instanceof JSONObject)
					{
						item = ReadResItem((JSONObject)obj, false);
						if(item != null)
						{
							out.add(item);
						}
					}
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.BANNER_CLOUD_OK;
	}

	@Override
	public ArrayList<BannerRes> sync_GetCloudCacheRes(Context context, DataFilter filter)
	{
		ArrayList<BannerRes> arr = mCloudResArr;
		ArrayList<BannerRes> arr2 = super.sync_GetCloudCacheRes(context, filter);

		synchronized(CLOUD_MEM_LOCK)
		{
			if(arr != arr2 && arr2 != null)
			{
				for(BannerRes res : arr2)
				{
					DownloadMgr.FastDownloadRes(res, false);
				}
			}
		}

		return arr2;
	}

	public static ArrayList<BannerRes> GetBannerResArr(ArrayList<BannerRes> arr, String pos)
	{
		ArrayList<BannerRes> out = new ArrayList<>();

		if(arr != null && pos != null)
		{
			BannerRes res;
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				res = arr.get(i);
				if(res.m_pos != null && res.m_pos.equals(pos))
				{
					out.add(res);
				}
			}
		}

		return out;
	}

	//拿到所有本地或者网络上的Banner数据，并通过传入的pos去筛选出与之对应的banner数据，并返回
	public ArrayList<BannerRes> GetBannerResArr(String pos)
	{
		ArrayList<BannerRes> out = new ArrayList<>();

		ArrayList<BannerRes> arr = sync_ar_GetCloudCacheRes(MyFramework2App.getInstance().getApplication(), null);
		out.addAll(GetBannerResArr(arr, pos));

		return out;
	}

	@Override
	public BannerRes GetItem(ArrayList<BannerRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}
}

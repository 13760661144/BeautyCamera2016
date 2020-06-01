package cn.poco.resource;

import android.content.Context;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.poco.framework.MyFramework2App;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;

/**
 * Created by Raining on 2017/10/8.
 */

public class NetTagMgr2 extends BaseResMgr<BaseRes, HashMap<String, String>>
{
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/switch_android.php";

	private static NetTagMgr2 sInstance;

	private NetTagMgr2()
	{
	}

	public synchronized static NetTagMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new NetTagMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(HashMap<String, String> arr)
	{
		return 0;
	}

	@Override
	public HashMap<String, String> MakeResArrObj()
	{
		return null;
	}

	@Override
	public boolean ResArrAddItem(HashMap<String, String> arr, BaseRes item)
	{
		return false;
	}

	@Override
	protected HashMap<String, String> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		return null;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, HashMap<String, String> arr)
	{
	}

	@Override
	protected int GetCloudEventId()
	{
		return 0;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return 0;
	}

	@Override
	protected BaseRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		return null;
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
	protected String GetCloudCachePath(Context context)
	{
		return null;
	}

	@Override
	public BaseRes GetItem(HashMap<String, String> arr, int id)
	{
		return null;
	}

	@Override
	protected HashMap<String, String> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
	{
		HashMap<String, String> out = null;

		try
		{
			if(data != null)
			{
				JSONArray jsonArr = new JSONArray(new String((byte[])data));
				Object obj;
				int arrLen = jsonArr.length();
				out = new HashMap<>();
				for(int i = 0; i < arrLen; i++)
				{
					obj = jsonArr.get(i);
					if(obj instanceof JSONObject)
					{
						if(((JSONObject)obj).has("sticker408"))
						{
							out.put(Tags.NET_TAG_VIDEO_ON_OFF, ((JSONObject)obj).getString("sticker408"));
						}
						else if(((JSONObject)obj).has("log_tips"))
						{
							out.put(Tags.NET_TAG_REG_TIP_ON_OFF, ((JSONObject)obj).getString("log_tips"));
						}
						else if(((JSONObject)obj).has("circle408"))
						{
							out.put(Tags.NET_TAG_CIRCLE_SHARE_VIDEO, ((JSONObject)obj).getString("circle408"));
						}
						else if(((JSONObject)obj).has("credit408"))
						{
							out.put(Tags.NET_TAG_HZ_CREDIT, ((JSONObject)obj).getString("credit408"));
						}
						else if(((JSONObject)obj).has("mission408"))
						{
							out.put(Tags.NET_TAG_HZ_MISSION, ((JSONObject)obj).getString("mission408"));
						}
						else if(((JSONObject)obj).has("wallet408"))
						{
							out.put(Tags.NET_TAG_HZ_WALLET, ((JSONObject)obj).getString("wallet408"));
						}
						else if(((JSONObject)obj).has("mall408"))
						{
							out.put(Tags.NET_TAG_HZ_MALL, ((JSONObject)obj).getString("mall408"));
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
	protected void sync_ui_CloudResChange(HashMap<String, String> oldArr, HashMap<String, String> newArr)
	{
		super.sync_ui_CloudResChange(oldArr, newArr);

		if(newArr != null && newArr.size() > 0)
		{
			//更新本地
			for(Map.Entry<String, String> temp : newArr.entrySet())
			{
				TagMgr.SetTagValue(MyFramework2App.getInstance().getApplicationContext(), temp.getKey(), temp.getValue());
			}
		}
	}

	// FIXME: 2017/10/8
}

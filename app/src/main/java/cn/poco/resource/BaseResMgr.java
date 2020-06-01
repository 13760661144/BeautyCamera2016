package cn.poco.resource;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.adnonstop.resourcelibs.DataFilter;
import com.adnonstop.resourcelibs.MemCache4UISyncBaseResMgr;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.framework.MyFramework2App;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;
import cn.poco.utils.MyNetCore;

public abstract class BaseResMgr<ResType extends BaseRes, ResArrType> extends MemCache4UISyncBaseResMgr<ResType, ResArrType>
{
	//为了减少升级/降级带来的BUG,只有版本相同才读取数据
	public int CURRENT_RES_JSON_VER = 1;

	protected boolean mInitOrderArr = false;
	protected final ArrayList<Integer> mOrderArr = new ArrayList<>();
	//为了减少升级/降级带来的BUG,只有版本相同才读取数据
	public int CURRENT_ORDER_JSON_VER = 1;

	public int m_oldID = 0;
	public boolean m_hasNewRes = false;

	protected abstract int GetNewOrderJsonVer();

	//{
	//	"ver":1,
	//	"order":[
	//		1,
	//		2,
	//		3
	//	]
	//}
	public void ReadOrderArr(Context context, String path)
	{
		try
		{
			byte[] data = CommonUtils.ReadFile(path);
			if(data != null)
			{
				mOrderArr.clear();

				JSONObject jsonObj = new JSONObject(new String(data));
				if(jsonObj.length() > 0)
				{
					if(jsonObj.has("ver"))
					{
						CURRENT_ORDER_JSON_VER = jsonObj.getInt("ver");
					}
					if(GetNewOrderJsonVer() == CURRENT_ORDER_JSON_VER && jsonObj.has("order"))
					{
						JSONArray jsonArr = jsonObj.getJSONArray("order");
						if(jsonArr != null)
						{
							int arrLen = jsonArr.length();
							for(int i = 0; i < arrLen; i++)
							{
								mOrderArr.add(jsonArr.getInt(i));
							}
						}
					}
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public void SaveOrderArr(Context context, int ver, String path)
	{
		ResourceUtils.WriteOrderArr(path, ver, mOrderArr);
	}

	protected void InitOrderArr(ArrayList<Integer> dstObj)
	{
	}

	public ArrayList<Integer> GetOrderArr()
	{
		if(!mInitOrderArr)
		{
			InitOrderArr(mOrderArr);
			mInitOrderArr = true;
		}
		return mOrderArr;
	}

	@Override
	protected int GetLocalEventId()
	{
		return 0;
	}

	protected abstract ResType ReadResItem(JSONObject jsonObj, boolean isPath);

	public boolean CheckIntact(ResType res)
	{
		return true;
	}

	protected abstract String GetSdcardPath(Context context);

	protected String GetOldIdFlag()
	{
		return null;
	}

	@Override
	protected Object sync_raw_ReadSdcardData(Context context, DataFilter filter)
	{
		Object obj = null;
		try
		{
			obj = CommonUtils.ReadFile(GetSdcardPath(context));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return obj;
	}

	protected abstract int GetNewJsonVer();

	//{
	//	"ver":1,
	//	"data":[
	//		...
	//	]
	//}
	@Override
	protected ResArrType sync_DecodeSdcardRes(Context context, DataFilter filter, Object data)
	{
		ResArrType out = MakeResArrObj();//sdcard默认都有返回值

		try
		{
			if(data != null)
			{
				//out = MakeResArrObj();
				JSONObject jsonObj = new JSONObject(new String((byte[])data));
				if(jsonObj.length() > 0)
				{
					if(jsonObj.has("ver"))
					{
						CURRENT_RES_JSON_VER = jsonObj.getInt("ver");
					}
					if(GetNewJsonVer() == CURRENT_RES_JSON_VER)
					{
						JSONArray jsonArr = jsonObj.getJSONArray("data");
						if(jsonArr != null)
						{
							int arrLen = jsonArr.length();
							ResType item;
							Object obj;
							for(int i = 0; i < arrLen; i++)
							{
								obj = jsonArr.get(i);
								if(obj instanceof JSONObject)
								{
									item = ReadResItem((JSONObject)obj, true);
									if(item != null && CheckIntact(item))
									{
										ResArrAddItem(out, item);
									}
								}
							}
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
	protected int GetSdcardEventId()
	{
		return 0;
	}

	protected abstract String GetCloudUrl(Context context);

	@Override
	protected Object sync_raw_ReadCloudData(Context context, DataFilter filter)
	{
		byte[] data = null;

		MyNetCore net = null;
		try
		{
			net = new MyNetCore(context);
			NetCore2.NetMsg msg = net.HttpGet(GetCloudUrl(context));
			if(msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK && msg.m_data != null)
			{
				data = msg.m_data;
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(net != null)
			{
				net.ClearAll();
			}
		}

		return data;
	}

	protected abstract String GetCloudCachePath(Context context);

	@Override
	protected Object sync_raw_ReadCloudCacheData(Context context, DataFilter filter)
	{
		Object obj = null;
		try
		{
			obj = CommonUtils.ReadFile(GetCloudCachePath(context));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	protected void sync_raw_WriteCloudData(Context context, DataFilter filter, Object data)
	{
		try
		{
			CommonUtils.SaveFile(GetCloudCachePath(context), (byte[])data);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected ResArrType sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
	{
		ResArrType out = null;

		try
		{
			if(data instanceof byte[] && ((byte[])data).length > 0)
			{
				JSONArray jsonArr = new JSONArray(new String((byte[])data));
				ResType item;
				Object obj;
				int arrLen = jsonArr.length();
				out = MakeResArrObj();
				for(int i = 0; i < arrLen; i++)
				{
					obj = jsonArr.get(i);
					if(obj instanceof JSONObject)
					{
						item = ReadResItem((JSONObject)obj, false);
						if(item != null)
						{
							ResArrAddItem(out, item);
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

	protected void UpdateOldId(ResArrType newArr)
	{
		final String FLAG = GetOldIdFlag();
		if(newArr != null && FLAG != null)
		{
			int maxID = 0;
			if(newArr instanceof ArrayList)
			{
				maxID = ResourceUtils.GetMaxID((ArrayList)newArr);
			}
			else if(newArr instanceof SparseArray)
			{
				maxID = ResourceUtils.GetMaxID((SparseArray)newArr);
			}
			else if(newArr instanceof HashMap)
			{
				maxID = ResourceUtils.GetMaxID((HashMap)newArr);
			}
			if(maxID > m_oldID && m_oldID != 0)
			{
				m_hasNewRes = true;
			}
			if(m_oldID == 0)
			{
				ResourceMgr.UpdateOldIDFlag(MyFramework2App.getInstance().getApplicationContext(), maxID, FLAG);
			}
			if(maxID > m_oldID)
			{
				m_oldID = maxID;
			}
		}
	}

	public void ClearOldId(Context context)
	{
		final String FLAG = GetOldIdFlag();
		if(FLAG != null)
		{
			ResourceMgr.UpdateOldIDFlag(context, m_oldID, FLAG);
			m_hasNewRes = false;
		}
	}

	public void ReadOldId(SharedPreferences sp)
	{
		final String FLAG = GetOldIdFlag();
		if(FLAG != null && sp != null)
		{
			m_oldID = sp.getInt(FLAG, 0);
		}
	}

	@Override
	public ResArrType sync_GetCloudCacheRes(Context context, DataFilter filter)
	{
		ResArrType arr = mCloudResArr;
		ResArrType arr2 = super.sync_GetCloudCacheRes(context, filter);

		synchronized(CLOUD_MEM_LOCK)
		{
			if(arr != arr2 && arr2 != null)
			{
				UpdateOldId(arr2);
			}
		}

		return arr2;
	}

	public abstract ResType GetItem(ResArrType arr, int id);

	public ResType GetRes(int id, boolean onlyCanUse)
	{
		ResType out = null;

		ResArrType arr;
		if((arr = sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null)) != null)
		{
			out = GetItem(arr, id);
		}
		if(out == null && (arr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null)) != null)
		{
			out = GetItem(arr, id);
		}
		if(!onlyCanUse && out == null && (arr = sync_ar_GetCloudCacheRes(MyFramework2App.getInstance().getApplication(), null)) != null)
		{
			out = GetItem(arr, id);
		}

		return out;
	}

	public ResType GetRes(int id)
	{
		return GetRes(id, false);
	}

	public ResArrType GetResArr(int[] ids, boolean onlyCanUse)
	{
		ResArrType out = MakeResArrObj();

		if(ids != null)
		{
			ResType temp;
			for(int id : ids)
			{
				temp = GetRes(id, onlyCanUse);
				if(temp != null)
				{
					ResArrAddItem(out, temp);
				}
			}
		}

		return out;
	}

	public ResArrType GetResArr(int[] ids)
	{
		return GetResArr(ids, false);
	}

	protected void RebuildNetResArr(ResArrType dst, ResArrType src)
	{
	}

	@Override
	protected void sync_ui_CloudResChange(ResArrType oldArr, ResArrType newArr)
	{
		if(oldArr != newArr)
		{
			RebuildNetResArr(newArr, oldArr);
		}

		if(newArr != null && GetResArrSize(newArr) > 0)
		{
			UpdateOldId(newArr);
		}
	}

	public void ReadNewFlagArr(Context context, SharedPreferences sp)
	{
	}
}

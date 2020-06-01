package cn.poco.login;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.poco.tianutils.CommonUtils;

public class AreaList
{
	//public static String COUNTRY_PATH = "cities/country.json";
	public static String COUNTRY_PATH2 = "cities/location.json";

	//public static ArrayList<AreaInfo> parseLocalJson(Context context, String localPath)
	//{
	//	ArrayList<AreaInfo> out = new ArrayList<AreaList.AreaInfo>();
	//	InputStreamReader inputReader = null;
	//	BufferedReader reader = null;
	//	try
	//	{
	//		InputStream is = context.getAssets().open(localPath);
	//		inputReader = new InputStreamReader(is);
	//		reader = new BufferedReader(inputReader);
	//		String inputLine = null;
	//		StringBuffer sb = new StringBuffer();
	//		while((inputLine = reader.readLine()) != null)
	//		{
	//			sb.append(inputLine).append("\n");
	//		}
	//		reader.close();
	//		inputReader.close();
	//
	//		String datas = sb.toString();
	//		JSONArray jsonArr = new JSONArray(datas);
	//		if(jsonArr != null && jsonArr.length() > 0)
	//		{
	//			int arrLen = jsonArr.length();
	//			AreaInfo item;
	//			String info;
	//			for(int i = 0; i < arrLen; i++)
	//			{
	//				info = (String)jsonArr.get(i);
	//				String[] infos = info.split(",");
	//				item = new AreaList.AreaInfo();
	//				if(infos != null)
	//				{
	//					item.m_name = infos[0];
	//					if(infos[1] != null && infos[1].length() > 0)
	//						item.m_id = Integer.parseInt(infos[1]);
	//				}
	//				out.add(item);
	//			}
	//		}
	//	}
	//	catch(Exception e)
	//	{
	//		try
	//		{
	//			if(reader != null)
	//			{
	//				reader.close();
	//			}
	//			if(inputReader != null)
	//			{
	//				inputReader.close();
	//			}
	//		}
	//		catch(IOException e1)
	//		{
	//		}
	//	}
	//	return out;
	//}
	//
	///**
	// * 获取所有国家信息
	// * 
	// * @return
	// */
	//public static ArrayList<AreaInfo> GetCountryLists(Context context)
	//{
	//	return parseLocalJson(context, COUNTRY_PATH);
	//}
	//
	///**
	// * 获取某个国家的省份信息(中国)
	// * 
	// * @param id
	// * @return
	// */
	//public static ArrayList<AreaInfo> GetProvinceListByCountry(Context context, int id)
	//{
	//	String path = "cities/province" + id + ".json";
	//	ArrayList<AreaInfo> out = parseLocalJson(context, path);
	//	return out;
	//}
	//
	///**
	// * 获取某个省份的城市信息
	// * 
	// * @param id
	// * @return
	// */
	//public static ArrayList<AreaInfo> GetCityListByProvince(Context context, int id)
	//{
	//	String path = "cities/" + id + ".json";
	//	ArrayList<AreaInfo> out = parseLocalJson(context, path);
	//	return out;
	//}

	public static class AreaInfo
	{
		public long m_id;
		public String m_name;
	}

	public static class AreaInfo2 extends AreaInfo
	{
		public AreaInfo2 m_parent;
		public AreaInfo2[] m_child;
	}

	private static AreaInfo2[] ReadLocationData(AreaInfo2 parent, JSONArray jsonArr)
	{
		AreaInfo2[] out = null;

		if(jsonArr != null && jsonArr.length() > 0)
		{
			int len = jsonArr.length();
			out = new AreaInfo2[len];
			AreaInfo2 temp;
			JSONObject jsonObj;
			for(int i = 0; i < len; i++)
			{
				try
				{
					temp = new AreaInfo2();
					jsonObj = jsonArr.getJSONObject(i);
					temp.m_parent = parent;
					temp.m_id = jsonObj.getLong("location_id");
					temp.m_name = jsonObj.getString("location_name");
					if(jsonObj.has("child"))
					{
						temp.m_child = ReadLocationData(temp, jsonObj.getJSONArray("child"));
					}
					out[i] = temp;
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}

		return out;
	}

	public static AreaInfo2[] GetLocationLists(Context context)
	{
		AreaInfo2[] out = null;

		try
		{
			byte[] data = CommonUtils.ReadData(context.getAssets().open(COUNTRY_PATH2));
			JSONArray jsonArr = new JSONArray(new String(data));
			out = ReadLocationData(null, jsonArr);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	public static AreaInfo2 GetLocation(AreaInfo2[] arr, long id)
	{
		AreaInfo2 out = null;

		if(arr != null && arr.length > 0)
		{
			for(int i = 0; i < arr.length; i++)
			{
				if(arr[i].m_id == id)
				{
					out = arr[i];
					break;
				}
				out = GetLocation(arr[i].m_child, id);
				if(out != null)
				{
					break;
				}
			}
		}

		return out;
	}

	public static String GetLocationStr(AreaInfo2[] arr, long id, String split)
	{
		String out = "";

		AreaInfo2 info = GetLocation(arr, id);
		if(info != null)
		{
			out = info.m_name;
			info = info.m_parent;
			while(info != null)
			{
				out = info.m_name + split + out;
				info = info.m_parent;
			}
		}

		return out;
	}
}

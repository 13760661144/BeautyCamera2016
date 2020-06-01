package cn.poco.resource;

import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Raining on 2017/9/28.
 */

public class ResourceUtils
{
	public static <T extends BaseRes> int HasItem(ArrayList<T> resArr, int id)
	{
		int out = -1;

		if(resArr != null)
		{
			int len = resArr.size();
			for(int i = 0; i < len; i++)
			{
				if(resArr.get(i).m_id == id)
				{
					out = i;
					break;
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> T DeleteItem(ArrayList<T> resArr, int id)
	{
		T out = null;

		int index = HasItem(resArr, id);
		if(index >= 0)
		{
			out = resArr.remove(index);
		}

		return out;
	}

	public static <T extends BaseRes> ArrayList<T> DeleteItems(ArrayList<T> resArr, int... ids)
	{
		ArrayList<T> out = new ArrayList<>();

		if(resArr != null && ids != null)
		{
			T temp;
			for(int i = 0; i < ids.length; i++)
			{
				temp = DeleteItem(resArr, ids[i]);
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		return out;
	}

	/**
	 * 按照order的排序
	 *
	 * @param localArr
	 * @param sdCardArr
	 * @param order
	 * @return
	 */
	public static <T extends BaseRes> ArrayList<T> BuildShowArr(ArrayList<T> localArr, ArrayList<T> sdCardArr, ArrayList<Integer> order)
	{
		ArrayList<T> out = new ArrayList<T>();

		ArrayList<T> tempArr = new ArrayList<T>();
		if(localArr != null)
		{
			tempArr.addAll(localArr);
		}
		if(sdCardArr != null)
		{
			tempArr.addAll(sdCardArr);
		}
		if(order != null)
		{
			T temp;
			int len = order.size();
			for(int i = 0; i < len; i++)
			{
				temp = DeleteItem(tempArr, order.get(i));
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		//ArrayList<T> tempLocalArr = new ArrayList<T>();
		//if(localArr != null)
		//{
		//	tempLocalArr.addAll(localArr);
		//}
		//ArrayList<T> tempSDCardArr = new ArrayList<T>();
		//if(sdCardArr != null)
		//{
		//	tempSDCardArr.addAll(sdCardArr);
		//}
		//if(order == null)
		//{
		//	order = new ArrayList<Integer>();
		//}
		//T temp1;
		//T temp2;
		//int id;
		//int len = order.size();
		//for(int i = 0; i < len; i++)
		//{
		//	id = order.get(i);
		//	temp1 = DeleteItem(tempLocalArr, id);
		//	temp2 = DeleteItem(tempSDCardArr, id);
		//	if(temp1 != null)
		//	{
		//		out.add(temp1);
		//	}
		//	else if(temp2 != null)
		//	{
		//		out.add(temp2);
		//	}
		//}
		//out.addAll(0, tempSDCardArr);
		//out.addAll(0, tempLocalArr);

		return out;
	}

	/**
	 * 删除重复的order
	 *
	 * @param order
	 * @return order是否有改变
	 */
	public static boolean DeleteRepetitionOrder(ArrayList<Integer> order)
	{
		boolean out = false;

		if(order != null)
		{
			int len = order.size();
			int temp;
			for(int i = 0; i < len; i++)
			{
				if(order.get(i) == null)
				{
					continue;
				}
				temp = order.get(i);
				for(int j = i + 1; j < len; j++)
				{
					if(order.get(j) == null)
					{
						continue;
					}
					if(temp == order.get(j))
					{
						order.remove(j);
						len--;
						j--;

						out = true;
					}
				}
			}
		}

		return out;
	}

	/**
	 * 删除无效的order
	 *
	 * @param arr
	 * @param order
	 * @return order是否有改变
	 */
	public static <T extends BaseRes> boolean DeleteInvalidateOrder(ArrayList<T> arr, ArrayList<Integer> order)
	{
		boolean out = false;

		if(arr != null && order != null)
		{
			int len = order.size();
			for(int i = 0; i < len; i++)
			{
				if(HasItem(arr, order.get(i)) < 0)
				{
					order.remove(i);
					len--;
					i--;

					out = true;
				}
			}
		}

		return out;
	}

	/**
	 * 删除多余的order,再把内置的素材插入到order
	 *
	 * @param localArr
	 * @param sdCardArr
	 * @param order
	 * @return order是否有改变
	 */
	public static <T extends BaseRes> boolean RebuildOrder(ArrayList<T> localArr, ArrayList<T> sdCardArr, ArrayList<Integer> order)
	{
		boolean out = DeleteRepetitionOrder(order);

		//删除多余的order
		//ArrayList<T> arr = new ArrayList<T>();
		//if(localArr != null)
		//{
		//	arr.addAll(localArr);
		//}
		//if(sdCardArr != null)
		//{
		//	arr.addAll(sdCardArr);
		//}
		//out = DeleteInvalidateOrder(arr, order);

		//把内置的素材插入到order
		if(localArr != null && order != null)
		{
			int len = localArr.size();
			T temp;
			for(int i = len - 1; i >= 0; i--)
			{
				temp = localArr.get(i);
				if(HasId(order, temp.m_id) < 0)
				{
					order.add(0, temp.m_id);
					out = true;
				}
			}
		}

		//把下载的素材插入到order
		if(sdCardArr != null && order != null)
		{
			int len = sdCardArr.size();
			T temp;
			for(int i = len - 1; i >= 0; i--)
			{
				temp = sdCardArr.get(i);
				if(HasId(order, temp.m_id) < 0)
				{
					order.add(temp.m_id);
					out = true;
				}
			}
		}

		return out;
	}

	public static int HasId(ArrayList<Integer> idArr, int id)
	{
		int out = -1;

		if(idArr != null)
		{
			int len = idArr.size();
			for(int i = 0; i < len; i++)
			{
				if(idArr.get(i) == id)
				{
					out = i;
					break;
				}
			}
		}

		return out;
	}

	public static int HasId(int[] ids, int id)
	{
		int out = -1;

		if(ids != null)
		{
			for(int i = 0; i < ids.length; i++)
			{
				if(ids[i] == id)
				{
					out = i;
					break;
				}
			}
		}

		return out;
	}

	public static boolean DeleteId(ArrayList<Integer> idArr, int id)
	{
		boolean out = false;

		if(idArr != null)
		{
			int len = idArr.size();
			for(int i = 0; i < len; i++)
			{
				if(idArr.get(i) == id)
				{
					idArr.remove(i);
					i--;
					len--;

					out = true;
				}
			}
		}

		return out;
	}

	public static boolean DeleteIds(ArrayList<Integer> idArr, int... ids)
	{
		boolean out = false;

		if(idArr != null && ids != null)
		{
			for(int i = ids.length - 1; i >= 0; i--)
			{
				if(DeleteId(idArr, ids[i]))
				{
					out = true;
				}
			}
		}

		return out;
	}

	public static boolean AddIds(ArrayList<Integer> idArr, int... ids)
	{
		boolean out = false;

		if(idArr != null && ids != null)
		{
			for(int i = ids.length - 1; i >= 0; i--)
			{
				if(HasId(idArr, ids[i]) < 0)
				{
					idArr.add(0, ids[i]);
					out = true;
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> T GetItem(ArrayList<T> resArr, int id)
	{
		T out = null;

		int index = HasItem(resArr, id);
		if(index >= 0)
		{
			out = resArr.get(index);
		}

		return out;
	}

	public static <T extends BaseRes> ArrayList<T> GetItems(ArrayList<T> resArr, int... ids)
	{
		ArrayList<T> out = new ArrayList<T>();

		if(resArr != null && ids != null)
		{
			T temp;
			for(int i = 0; i < ids.length; i++)
			{
				temp = GetItem(resArr, ids[i]);
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> ArrayList<T> GetItems(HashMap<Integer, T> resArr, int... ids)
	{
		ArrayList<T> out = new ArrayList<T>();

		if(resArr != null && ids != null)
		{
			T temp;
			for(int id : ids)
			{
				temp = resArr.get(id);
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> ArrayList<T> GetItems(SparseArray<T> resArr, int... ids)
	{
		ArrayList<T> out = new ArrayList<T>();

		if(resArr != null && ids != null)
		{
			T temp;
			for(int id : ids)
			{
				temp = resArr.get(id);
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> int GetMaxID(HashMap<Integer, T> arr)
	{
		int out = 0;

		if(arr != null)
		{
			int id;
			for(Iterator it = arr.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry e = (Map.Entry)it.next();
				id = (Integer)e.getKey();
				if(id > out)
				{
					out = id;
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> ArrayList<T> DeleteItems(HashMap<Integer, T> resArr, int... ids)
	{
		ArrayList<T> out = new ArrayList<T>();

		if(resArr != null && ids != null)
		{
			T temp;
			for(int id : ids)
			{
				temp = resArr.remove(id);
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> ArrayList<T> DeleteItems(SparseArray<T> resArr, int... ids)
	{
		ArrayList<T> out = new ArrayList<>();

		if(resArr != null && ids != null)
		{
			T temp;
			for(int id : ids)
			{
				temp = resArr.get(id);
				resArr.remove(id);
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> int AddSoleItem(ArrayList<T> resArr, int index, T res)
	{
		int out = -1;

		if(resArr != null && res != null)
		{
			DeleteItem(resArr, res.m_id);
			if(index < 0)
			{
				index = 0;
			}
			else if(index > resArr.size())
			{
				index = resArr.size();
			}
			resArr.add(index, res);
		}

		return out;
	}

	public static <T extends BaseRes> void MakeOrder(ArrayList<T> arr, ArrayList<Integer> order)
	{
		if(arr != null && order != null)
		{
			order.clear();
			int size = arr.size();
			for(int i = 0; i < size; i++)
			{
				order.add(arr.get(i).m_id);
			}
		}
	}

	public static void ChangeOrderPosition(ArrayList<Integer> order, int fromPos, int toPos)
	{
		if(order != null && order.size() > fromPos && order.size() > toPos && fromPos >= 0 && toPos >= 0)
		{
			int temp = order.remove(fromPos);
			order.add(toPos, temp);
		}
	}

	public static <T extends BaseRes> void ChangeArrayPosition(ArrayList<T> res, int fromPos, int toPos)
	{
		if(res != null && res.size() > fromPos && res.size() > toPos && fromPos >= 0 && toPos >= 0)
		{
			T temp = res.remove(fromPos);
			res.add(toPos, temp);
		}
	}

	public static boolean HasIntact(Object... arr)
	{
		boolean out = false;

		if(arr != null)
		{
			for(Object obj : arr)
			{
				if(obj != null && !obj.equals(""))
				{
					out = true;
					break;
				}
			}
		}

		return out;
	}

	public static void WriteOrderArr(String path, int ver, ArrayList<Integer> order)
	{
		if(path != null && order != null)
		{
			FileOutputStream fos = null;
			try
			{
				JSONObject json = new JSONObject();
				{
					json.put("ver", ver);

					JSONArray jsonArr = new JSONArray();
					{
						int len = order.size();
						for(int i = 0; i < len; i++)
						{
							jsonArr.put(order.get(i));
						}
					}
					json.put("order", jsonArr);
				}

				fos = new FileOutputStream(path);
				fos.write(json.toString().getBytes());
				fos.flush();
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
			finally
			{
				if(fos != null)
				{
					try
					{
						fos.close();
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static <T extends BaseRes> int GetMaxID(ArrayList<T> arr)
	{
		int out = 0;

		if(arr != null)
		{
			int len = arr.size();
			int id;
			for(int i = 0; i < len; i++)
			{
				id = arr.get(i).m_id;
				if(id > out)
				{
					out = id;
				}
			}
		}

		return out;
	}

	public static <T extends BaseRes> int GetMaxID(SparseArray<T> arr)
	{
		int out = 0;

		if(arr != null)
		{
			int len = arr.size();
			int id;
			for(int i = 0; i < len; i++)
			{
				id = arr.valueAt(i).m_id;
				if(id > out)
				{
					out = id;
				}
			}
		}

		return out;
	}

	/**
	 * @param str
	 * @param radix 多少进制10/16
	 * @return
	 */
	public static int[] ParseIds(String str, int radix)
	{
		int[] out = null;

		if(str != null && str.length() > 0)
		{
			String[] strIds = str.split(",");
			out = new int[strIds.length];
			for(int i = 0; i < strIds.length; i++)
			{
				out[i] = (int)Long.parseLong(strIds[i], radix);
			}
		}

		return out;
	}

	public static float[] ParseFloatArr(String str)
	{
		float[] out = null;

		if(str != null && str.length() > 0)
		{
			String[] arr = str.split(",");
			out = new float[arr.length];
			for(int k = 0; k < arr.length; k++)
			{
				out[k] = Float.parseFloat(arr[k]);
			}
		}

		return out;
	}

	public static String MakeStr(float[] arr)
	{
		String out = "";

		if(arr != null)
		{
			int len = arr.length;
			for(int i = 0; i < len; i++)
			{
				if(i != 0)
				{
					out += "," + (int)arr[i];
				}
				else
				{
					out += (int)arr[i];
				}
			}
		}

		return out;
	}

	public static String MakeStr(int[] arr, int radix)
	{
		String out = "";

		if(arr != null)
		{
			for(int i = 0; i < arr.length; i++)
			{
				if(i != 0)
				{
					out += "," + Integer.toString(arr[i], radix);
				}
				else
				{
					out += Integer.toString(arr[i], radix);
				}
			}
		}

		return out;
	}

	public static void RebuildNewFlagArr(ArrayList<? extends BaseRes> arr, ArrayList<Integer> newFlagArr)
	{
		if(arr != null && newFlagArr != null)
		{
			int len = newFlagArr.size();
			for(int i = 0; i < len; i++)
			{
				if(ResourceUtils.HasItem(arr, newFlagArr.get(i)) < 0)
				{
					newFlagArr.remove(i);
					i--;
					len--;
				}
			}
		}
	}

	public static void ParseNewFlagToArr(ArrayList<Integer> dst, String str)
	{
		if(dst != null && str != null)
		{
			dst.clear();
			int[] ids = ResourceUtils.ParseIds(str, 10);
			if(ids != null)
			{
				for(int i = 0; i < ids.length; i++)
				{
					dst.add(ids[i]);
				}
			}
		}
	}
}

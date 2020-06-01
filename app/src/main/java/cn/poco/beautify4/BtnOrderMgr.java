package cn.poco.beautify4;

import android.content.Context;

import java.util.ArrayList;

import cn.poco.beautify4.adapter.MyAdapter;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;

/**
 * Created by Raining on 2017/2/6.
 * 美化下面按钮删除/排序
 */

public class BtnOrderMgr
{
	public static UiMode[] ReadList2(Context context)
	{
		return parseList2(TagMgr.GetTagValue(context, Tags.BEAUTY4_BOTTOM_LIST2));
	}

	public interface ParseCallback
	{
		boolean IsContain(UiMode mode);
	}

	public static UiMode[] parseList(String str, ParseCallback cb)
	{
		UiMode[] out = null;
		if(str != null && str.length() > 0 && cb != null)
		{
			ArrayList<UiMode> list = new ArrayList<>();
			String[] arr = str.split("[|]");
			for(String s : arr)
			{
				try
				{
					UiMode mode = UiMode.GetType(Integer.parseInt(s));
					if(mode != null && cb.IsContain(mode))
					{
						list.add(mode);
					}
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			if(list.size() > 0)
			{
				out = new UiMode[list.size()];
				list.toArray(out);
			}
		}
		return out;
	}

	public static boolean isList2(UiMode mode)
	{
		boolean out = false;

		if(mode != null)
		{
			switch(mode)
			{
				case MEIYAN:
				case SHOUSHEN:
				case QUDOU:
				case DAYAN:
				case QUYANDAI:
				case LIANGYAN:
				case ZENGGAO:
				case WEIXIAO:
				case MEIYA:
				case SHOUBI:
				case GAOBILIANG:
				case YIJIANMENGZHUANG:
				case CAIZHUANG:
					out = true;
					break;
			}
		}

		return out;
	}

	public static UiMode[] parseList2(String str)
	{
		UiMode[] out = parseList(str, new ParseCallback()
		{
			@Override
			public boolean IsContain(UiMode mode)
			{
				return isList2(mode);
			}
		});
		if(out == null || out.length <= 0)
		{
			out = new UiMode[]{UiMode.MEIYAN, UiMode.SHOUSHEN, UiMode.QUDOU, UiMode.DAYAN, UiMode.QUYANDAI, UiMode.LIANGYAN, UiMode.ZENGGAO,
					UiMode.WEIXIAO, UiMode.MEIYA, UiMode.SHOUBI, UiMode.GAOBILIANG, UiMode.YIJIANMENGZHUANG, UiMode.CAIZHUANG};
		}
		return out;
	}

	public static UiMode[] ReadList3(Context context)
	{
		return parseList3(TagMgr.GetTagValue(context, Tags.BEAUTY4_BOTTOM_LIST3));
	}

	public static boolean isList3(UiMode mode)
	{
		boolean out = false;

		if(mode != null)
		{
			switch(mode)
			{
				case LVJING:
				case XIANGKUANG:
				case TIETU:
				case MAOBOLI:
				case MASAIKE:
				case ZHIJIANMOFA:
					out = true;
					break;
			}
		}

		return out;
	}

	public static UiMode[] parseList3(String str)
	{
		UiMode[] out = parseList(str, new ParseCallback()
		{
			@Override
			public boolean IsContain(UiMode mode)
			{
				return isList3(mode);
			}
		});
		if(out == null || out.length <= 0)
		{
			out = new UiMode[]{UiMode.LVJING, UiMode.XIANGKUANG, UiMode.TIETU, UiMode.MAOBOLI, UiMode.MASAIKE, UiMode.ZHIJIANMOFA, UiMode.PINTU};
		}
		return out;
	}

	public static void SaveOrder(Context context, boolean isList2, UiMode[] arr)
	{
		String str = "";
		int i = 0;
		if(arr != null)
		{
			for(UiMode mode : arr)
			{
				if(i != 0)
				{
					str += "|";
				}
				str += mode.GetValue();
				i++;
			}
			//System.out.println(str);
		}
		if(isList2)
		{
			TagMgr.SetTagValue(context, Tags.BEAUTY4_BOTTOM_LIST2, str);
		}
		else
		{
			TagMgr.SetTagValue(context, Tags.BEAUTY4_BOTTOM_LIST3, str);
		}
		TagMgr.Save(context);
	}

	public static void SaveOrder(Context context, boolean isList2, ArrayList<MyAdapter.MyItem> arr)
	{
		if(arr != null)
		{
			if(isList2)
			{
				ArrayList<UiMode> modeArr = new ArrayList<>();
				for(MyAdapter.MyItem item : arr)
				{
					if(isList2(item.mMode))
					{
						modeArr.add(item.mMode);
					}
				}
				UiMode[] arr2 = new UiMode[modeArr.size()];
				modeArr.toArray(arr2);
				SaveOrder(context, isList2, arr2);
			}
			else
			{
				ArrayList<UiMode> modeArr = new ArrayList<>();
				for(MyAdapter.MyItem item : arr)
				{
					if(isList3(item.mMode))
					{
						modeArr.add(item.mMode);
					}
				}
				UiMode[] arr2 = new UiMode[modeArr.size()];
				modeArr.toArray(arr2);
				SaveOrder(context, isList2, arr2);
			}
		}
	}
}

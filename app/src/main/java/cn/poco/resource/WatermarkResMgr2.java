package cn.poco.resource;

import android.content.Context;
import android.support.annotation.IntegerRes;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.poco.filter4.WatermarkItem;
import cn.poco.framework.MyFramework2App;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/10/7.
 */

public class WatermarkResMgr2 extends BaseResMgr<WatermarkItem, ArrayList<WatermarkItem>>
{
	private static WatermarkResMgr2 sInstance;

	private WatermarkResMgr2()
	{
	}

	public synchronized static WatermarkResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new WatermarkResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<WatermarkItem> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<WatermarkItem> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<WatermarkItem> arr, WatermarkItem item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<WatermarkItem> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<WatermarkItem> out = new ArrayList<>();
		WatermarkItem watermarkRes = null;
		{
			//NOTE 水印id 为素材统计id值
			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__non_res;
			String country = context.getResources().getConfiguration().locale.getCountry();
			if (country.equals("CN"))
			{
				watermarkRes.thumb = R.drawable.__wat__non_thumb;
			}
			else
			{
				watermarkRes.thumb = R.drawable.__wat__non_thumb_en;
			}
			watermarkRes.type = -1;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_无);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_无;
			out.add(watermarkRes);

			//NOTE 默认水印
			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__08_res;
			watermarkRes.thumb = R.drawable.__wat__08_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_流星);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_流星;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__07_res;
			watermarkRes.thumb = R.drawable.__wat__07_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_几何);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_几何;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__06_res;
			watermarkRes.thumb = R.drawable.__wat__06_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_猫);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_猫;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__09_res;
			watermarkRes.thumb = R.drawable.__wat__09_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_优雅);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_优雅;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__10_res;
			watermarkRes.thumb = R.drawable.__wat__10_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_音符);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_音符;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__11_res;
			watermarkRes.thumb = R.drawable.__wat__11_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_丘比特);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_丘比特;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__01_res;
			watermarkRes.thumb = R.drawable.__wat__01_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_我爱美人);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_我爱美人;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__02_res;
			watermarkRes.thumb = R.drawable.__wat__02_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_爱星);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_爱星;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__03_res;
			watermarkRes.thumb = R.drawable.__wat__03_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_爱心1);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_爱心1;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__04_res;
			watermarkRes.thumb = R.drawable.__wat__04_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_英文相机2);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_英文相机2;
			out.add(watermarkRes);

			watermarkRes = new WatermarkItem();
			watermarkRes.res = R.drawable.__wat__05_res;
			watermarkRes.thumb = R.drawable.__wat__05_thumb;
			watermarkRes.mID = getResourceId(context, R.integer.滤镜_水印_英文相机1);
			watermarkRes.mTongJiId = R.integer.滤镜_水印_英文相机1;
			out.add(watermarkRes);
		}
		return out;
	}

	private int getResourceId(Context context, @IntegerRes int resId)
	{
		int out = 0;
		if (context != null)
		{
			try
			{
				out = context.getResources().getInteger(resId);
			}
			catch (Throwable ignored)
			{
			}
		}
		return out;
	}

	public int GetDefaultWatermarkId(Context context)
	{
		int out = -1;
		if (context != null)
		{
			out = context.getResources().getInteger(R.integer.滤镜_水印_流星);
		}
		return out;
	}

	public int GetNonWatermarkId(Context context)
    {
        int out = -1;
        if (context != null)
        {
            out = context.getResources().getInteger(R.integer.滤镜_水印_无);
        }
        return out;
    }

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<WatermarkItem> arr)
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
	protected WatermarkItem ReadResItem(JSONObject jsonObj, boolean isPath)
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
		return null;
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return null;
	}

	@Override
	public WatermarkItem GetItem(ArrayList<WatermarkItem> arr, int id)
	{
		return null;
	}

	public WatermarkItem GetWaterMarkById(ArrayList<WatermarkItem> arr, int id)
	{
		if(arr != null)
		{
			for(WatermarkItem item : arr)
			{
				if(item != null && item.mID == id)
				{
					return item;
				}
			}
		}
		return null;
	}

	public WatermarkItem GetWaterMarkById(int id)
	{
		ArrayList<WatermarkItem> watermarkItems = sync_GetLocalRes(MyFramework2App.getInstance().getApplicationContext(), null);
		if(watermarkItems != null)
		{
			for(WatermarkItem item : watermarkItems)
			{
				if(item != null && item.mID == id)
				{
					return item;
				}
			}
		}
		return null;
	}
}

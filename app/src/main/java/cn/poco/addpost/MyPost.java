package cn.poco.addpost;

import android.content.Context;
import android.graphics.Bitmap;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.image.PocoNativeFilter;
import cn.poco.imagecore.ImageUtils;
import cn.poco.tianutils.NetCore2;

/**
 * Created by Raining on 2016/12/26.
 * face++网络接口
 */

public class MyPost
{
	public synchronized static byte[] Post(String url, String k, String s, byte[] file)
	{
		byte[] out = null;

		if(file != null)
		{
			NetCore2 net = new NetCore2();
			net.CONN_TIMEOUT = 6000;
			net.READ_TIMEOUT = 6000;

			HashMap<String, String> params = new HashMap<>();
			params.put("api_key", k);
			params.put("api_secret", s);
			params.put("return_landmark", "1");
			params.put("return_attributes", "none");

			NetCore2.FormData data;
			ArrayList<NetCore2.FormData> params2 = new ArrayList<>();
			data = new NetCore2.FormData();
			data.m_name = "image_file";
			data.m_filename = "NoName";
			data.m_data = file;
			params2.add(data);

			NetCore2.NetMsg msg = net.HttpPost(url, params, params2, true);
			if(msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK)
			{
				out = msg.m_data;
			}
		}

		return out;
	}

	public synchronized static Float[] Post(Context context, byte[] file, int w, int h)
	{
		Float[] out = null;

		try
		{
			//System.out.println("file size : " + file.length / 1024f);
			byte[] data = Post(PocoNativeFilter.getLru(context), PocoNativeFilter.getYek(context), PocoNativeFilter.getTerces(context), file);
			if(data != null)
			{
				out = detect.parse(new String(data), w, h);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	public synchronized static Float[] Post(Context context, Bitmap bmp)
	{
		Float[] out = null;

		if(bmp != null)
		{
			if(bmp.getConfig() != Bitmap.Config.ARGB_8888)
			{
				bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
			}
			int w = bmp.getWidth();
			int h = bmp.getHeight();
			out = Post(context, ImageUtils.JpgEncode(bmp, 85), w, h);
		}

		return out;
	}
}

package cn.poco.MaterialMgr2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;

import java.util.ArrayList;

import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.tianutils.ImageUtils;
import cn.poco.utils.FileUtil;

public class MyImageLoader
{
	private LruCache<String, Bitmap> m_imgCache;
	private ArrayList<LoadItem> m_cacheList = new ArrayList<>();
	private int m_maxLoadCount = 10;

	private Handler m_imgHandler;
	private HandlerThread m_thread;
	private Handler m_uiHanlder;

	public MyImageLoader()
	{
		//获取应用程序的最大内存
		final int maxMem = (int)(Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMem / 8;

		m_imgCache = new LruCache<String, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				int out = value.getRowBytes() * value.getHeight() / 1024;
				return out;
			}
		};

		m_thread = new HandlerThread("loadImage");
		m_thread.start();
		m_imgHandler = new Handler(m_thread.getLooper())
		{
			@Override
			public void handleMessage(Message msg)
			{
				String key = (String)msg.obj;
				LoadItem res = GetItem(key);
				if(res != null)
				{
					Bitmap bmp = null;
					if(res.cb != null)
					{
						bmp = res.cb.makeBmp(res.res);
					}
					if(bmp != null)
					{
						addBmpToImgCache(key, bmp);
						res.bmp = bmp;
					}

					Message uiMsg = m_uiHanlder.obtainMessage();
					uiMsg.obj = res;
					m_uiHanlder.sendMessage(uiMsg);
				}
			}
		};

		m_uiHanlder = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				LoadItem res = (LoadItem)msg.obj;
				if(res != null && res.cb != null)
				{
					res.cb.onLoadFinished(res.bmp, res.res);
				}
			}
		};
	}

	public void SetMaxLoadCount(int count)
	{
		m_maxLoadCount = count;
	}

	public Bitmap loadBmp(LoadItem item, ImageLoadCallback cb)
	{
		item.cb = cb;
		String key = item.key;
		Bitmap out = getBmpFromImgCache(key);
		if((out == null || out.isRecycled()) && item != null && item.res != null)
		{
			addToCache(item);
			if(item.res instanceof BaseRes && ((BaseRes)item.res).m_thumb == null
					&& ((BaseRes)item.res).url_thumb != null)
			{
				LoadNetBmp(item, key);
			}
			else
			{
				if(m_imgHandler != null)
				{
					Message msg = m_imgHandler.obtainMessage();
					msg.obj = key;
					m_imgHandler.sendMessage(msg);
				}
			}
		}
		return out;
	}

	public Bitmap loadThemeBmp(LoadItem item, ImageLoadCallback cb)
	{
		item.cb = cb;
		String key = item.key;
		Bitmap out = getBmpFromImgCache(key);
		if((out == null || out.isRecycled()) && item != null && item.res != null)
		{
			addToCache(item);
			if(((BaseRes)item.res).m_type == BaseRes.TYPE_NETWORK_URL)
			{
				downloadTheme(item, key);
			}
			else
			{
				if(m_imgHandler != null)
				{
					Message msg = m_imgHandler.obtainMessage();
					msg.obj = key;
					m_imgHandler.sendMessage(msg);
				}
			}
		}
		return out;
	}

	public void downloadTheme(LoadItem item, final String key)
	{
		item.downloadID = DownloadMgr.getInstance().DownloadRes((BaseRes)item.res, new AbsDownloadMgr.Callback()
		{
			@Override
			public void OnProgress(int downloadId, IDownload res, int progress)
			{

			}

			@Override
			public void OnComplete(int downloadId, IDownload res)
			{
				if(m_imgHandler != null)
				{
					Message msg = m_imgHandler.obtainMessage();
					msg.obj = key;
					m_imgHandler.sendMessage(msg);
				}
			}

			@Override
			public void OnFail(int downloadId, IDownload res)
			{

			}
		});
	}

	public void LoadNetBmp(LoadItem item, final String key)
	{
		item.downloadID = DownloadMgr.getInstance().DownloadResThumb((BaseRes)item.res, new DownloadMgr.Callback()
		{
			@Override
			public void OnProgress(int downloadId, IDownload res, int progress)
			{

			}

			@Override
			public void OnComplete(int downloadId, IDownload res)
			{
				if(m_imgHandler != null)
				{
					Message msg = m_imgHandler.obtainMessage();
					msg.obj = key;
					m_imgHandler.sendMessage(msg);
				}
			}

			@Override
			public void OnFail(int downloadId, IDownload res)
			{

			}
		});
	}

	private synchronized void addBmpToImgCache(String key, Bitmap bmp)
	{
		if(getBmpFromImgCache(key) == null && bmp != null)
		{
			m_imgCache.put(key, bmp);
		}
	}

	private synchronized Bitmap getBmpFromImgCache(String key)
	{
		if(null == key || "".equals(key))
			return null;

		return m_imgCache.get(key);
	}

	private synchronized void addToCache(LoadItem item)
	{
		m_cacheList.add(item);
		if(m_cacheList.size() > m_maxLoadCount)
		{
			removeFromCache();
		}
	}

	private synchronized void removeFromCache()
	{
		if(m_cacheList.isEmpty() == false)
		{
			LoadItem info = m_cacheList.remove(0);
			if(info != null)
			{
				info.cb = null;
				info.bmp = null;
				if(info.downloadID != -1)
					DownloadMgr.getInstance().CancelDownload(info.downloadID);
			}
		}
	}

	public synchronized LoadItem GetItem(String key)
	{
		if(m_cacheList.size() > 0)
		{
			int size = m_cacheList.size();
			LoadItem temp;
			for(int i = 0; i < size; i ++)
			{
				temp = m_cacheList.get(i);
				if(temp.key.equals(key))
				{
					m_cacheList.remove(i);
					return temp;
				}
			}
		}
		return null;
	}

	public synchronized void releaseMem(boolean qickThread)
	{
		if(m_imgCache != null)
		{
			m_imgCache.evictAll();
		}
		while(m_cacheList.size() > 0)
		{
			LoadItem info = m_cacheList.remove(0);
			if(info != null)
			{
				info.cb = null;
				info.bmp = null;
				if(info.downloadID != -1)
					DownloadMgr.getInstance().CancelDownload(info.downloadID);
			}
		}
		m_cacheList.clear();
		if(qickThread && m_thread != null)
		{
			m_thread.quit();
			m_imgHandler = null;
		}
	}

	public static Bitmap MakeBmp(Context context, Object res, int width, int height, float px)
	{
		Bitmap out;
//		Bitmap temp = Utils.DecodeImage(context, res, 0, -1, width, height);
		Bitmap temp = MakeBmp(context, res);
		out = ImageUtils.MakeRoundBmp(temp, width, height, px);
		return out;
	}

	/**
	 *
	 * @param context
	 * @param res		支持byte[]、String、资源文件几种类型
	 * @return
	 */
	public static Bitmap MakeBmp(Context context, Object res)
	{
		Bitmap out = null;
		if(res instanceof String)
		{
			if(FileUtil.isFileExists((String)res))
			{
				out = BitmapFactory.decodeFile((String)res);
			}
			else
			{
				out = FileUtil.getAssetsBitmap(context, (String)res);
			}
		}
		else if(res instanceof Integer)
		{
			out = BitmapFactory.decodeResource(context.getResources(), (Integer)res);
		}
		else if(res instanceof byte[])
		{
			out = BitmapFactory.decodeByteArray((byte[])res, 0, ((byte[])res).length);
		}
		return out;
	}

	public static interface ImageLoadCallback
	{
		/**
		 * 在子线程中加载完图片,回到主线程调用
		 *
		 * @param res
		 * @param bmp
		 */
		public void onLoadFinished(Bitmap bmp, Object res);

		public Bitmap makeBmp(Object res);
	}

	public static class LoadItem{
		public String key = "";
		public Object res;
		public ImageLoadCallback cb;
		public Bitmap bmp;
		public int downloadID = -1;

		public LoadItem(String key, Object res)
		{
			this.key = key;
			this.res = res;
		}
	}

}

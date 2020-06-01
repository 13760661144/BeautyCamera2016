package cn.poco.adMaster;

import android.content.Context;
import android.os.Handler;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AdPackage;
import com.adnonstop.resourcelibs.CallbackHolder;

import java.util.ArrayList;

import cn.poco.holder.IMessageHolder;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;

/**
 * Created by Raining on 2017/8/22.
 * 首页广告
 */

public class HomeAd extends AbsAd
{
	public HomeAd(Context context)
	{
		super(context);
	}

	@Override
	protected String GetPos()
	{
		return "channel";
	}

	@Override
	public void Run(Callback cb)
	{
		super.Run(cb);

		mHolder = new CallbackHolder<>(new IMessageHolder.Callback<ArrayList<AdPackage>>()
		{
			@Override
			public void OnHandlerRun(final ArrayList<AdPackage> obj)
			{
				if(obj != null && obj.size() > 0)
				{
					ArrayList<AbsAdRes> arr = obj.get(0).mAds;
					if(arr.size() > 0)
					{
						AbsAdRes[] arr2 = new AbsAdRes[arr.size()];
						arr.toArray(arr2);
						DownloadMgr.getInstance().DownloadRes(arr2, true, new AbsDownloadMgr.Callback2()
						{
							@Override
							public void OnGroupComplete(int downloadId, IDownload[] resArr)
							{
								Show(obj);
							}

							@Override
							public void OnGroupFail(int downloadId, IDownload[] resArr)
							{
							}

							@Override
							public void OnGroupProgress(int downloadId, IDownload[] resArr, int progress)
							{
							}

							@Override
							public void OnProgress(int downloadId, IDownload res, int progress)
							{
							}

							@Override
							public void OnComplete(int downloadId, IDownload res)
							{
							}

							@Override
							public void OnFail(int downloadId, IDownload res)
							{
							}
						});
						return;
					}
				}
				Show(obj);
			}
		});
		mHolder.SetHandler(new Handler());
		ArrayList<AdPackage> obj = AdMaster.getInstance(mContext).sync_ar_GetCloudCacheRes(mContext, null, mHolder);
		AbsAd.FastDownload(obj);
		Show(obj);
	}

	public static ArrayList<AdPackage> GetHomeAd(Context context)
	{
		ArrayList<AdPackage> obj = AdMaster.getInstance(context).sync_ar_GetCloudCacheRes(context, null, new CallbackHolder<>(new IMessageHolder.Callback<ArrayList<AdPackage>>()
		{
			@Override
			public void OnHandlerRun(final ArrayList<AdPackage> obj)
			{
				if(obj != null && obj.size() > 0)
				{
					ArrayList<AbsAdRes> arr = obj.get(0).mAds;
					if(arr.size() > 0)
					{
						AbsAdRes[] arr2 = new AbsAdRes[arr.size()];
						arr.toArray(arr2);
						DownloadMgr.getInstance().DownloadRes(arr2, true, null);
					}
				}
			}
		}));
		FastDownload(obj);
		return obj;
	}

	public static AbsAdRes GetOneHomeRes(Context context, String id)
	{
		ArrayList<AdPackage> obj = GetHomeAd(context);
		if(obj != null && obj.size() > 0)
		{
			return obj.get(0).GetAdById(id);
		}
		return null;
	}

	/**
	 * 商业全局统计
	 */
	public static void SendGlobalAdTj(Context context)
	{
		ArrayList<AdPackage> ad = GetHomeAd(context);
		if(ad != null && ad.size() > 0)
		{
			AdPackage pkg = ad.get(0);
			if(pkg != null)
			{
				ShareAdBanner.SendTj(context, pkg.mAdMonitor);
			}
		}
	}
}

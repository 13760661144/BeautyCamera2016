package cn.poco.adMaster;

import android.content.Context;
import android.os.Handler;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsBootAdRes;
import com.adnonstop.admasterlibs.data.AdPackage;
import com.adnonstop.resourcelibs.CallbackHolder;

import java.util.ArrayList;

import cn.poco.holder.IMessageHolder;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;

/**
 * Created by Raining on 2017/8/23.
 * 开机页广告
 */
public class BootAd extends AbsAd
{
	public BootAd(Context context)
	{
		super(context);
	}

	@Override
	protected String GetPos()
	{
		return "boot";
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
						DownloadMgr.getInstance().DownloadRes(arr2, false, new AbsDownloadMgr.Callback2()
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
		FastDownload(obj);
		Show(obj);
	}

	public static AbsBootAdRes GetOneBootRes(Context context)
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
						DownloadMgr.getInstance().DownloadRes(arr2, false, null);
					}
				}
			}
		}));
		FastDownload(obj);
		return (AbsBootAdRes)GetOneRes("boot", obj);
	}
}

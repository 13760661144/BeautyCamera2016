package cn.poco.adMaster;

import android.content.Context;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AdPackage;
import com.adnonstop.resourcelibs.CallbackHolder;

import java.util.ArrayList;

import cn.poco.resource.DownloadMgr;

/**
 * Created by Raining on 2017/8/23.
 * 广告基类
 */

public abstract class AbsAd
{
	protected Context mContext;
	protected CallbackHolder<ArrayList<AdPackage>> mHolder;
	protected Callback mCb;
	protected ArrayList<String> mOldTjs = new ArrayList<>();

	public AbsAd(Context context)
	{
		mContext = context;
	}

	protected abstract String GetPos();

	public void Run(Callback cb)
	{
		mCb = cb;
	}

	public static void FastDownload(ArrayList<AdPackage> arr)
	{
		if(arr != null)
		{
			for(AdPackage pkg : arr)
			{
				if(pkg.mAds != null)
				{
					for(AbsAdRes ad : pkg.mAds)
					{
						DownloadMgr.FastDownloadRes(ad, false);
					}
				}
			}
		}
	}

	public static AbsAdRes RandomOne(ArrayList<AbsAdRes> arr)
	{
		AbsAdRes out = null;

		if(arr != null)
		{
			ArrayList<AbsAdRes> tempArr = new ArrayList<>();
			int total = 0;
			for(AbsAdRes res : arr)
			{
				//这里判断是否下载了可显示部分
				if(res.mShowOk)
				{
					total += res.mProbability;
					tempArr.add(res);
				}
			}
			double r = Math.random();
			//System.out.println("Math.random() : " + r);
			int ran = (int)(total * r);
			total = 0;
			for(AbsAdRes res : tempArr)
			{
				total += res.mProbability;
				if(ran < total)
				{
					out = res;
					break;
				}
			}
		}

		return out;
	}

	public static AbsAdRes GetOneRes(String pos, ArrayList<AdPackage> obj)
	{
		AbsAdRes out = null;

		if(obj != null && obj.size() > 0)
		{
			AdPackage ad = obj.get(0);
			ArrayList<AbsAdRes> arr = ad.GetAdByPos(pos);
			out = RandomOne(arr);
		}

		return out;
	}

	protected void ShowTj(AbsAdRes res)
	{
		if(res != null)
		{
			ShareAdBanner.SendTjOnce(mContext, res.mShowTjs, mOldTjs);
		}
	}

	protected void Show(ArrayList<AdPackage> obj)
	{
		AbsAdRes res = GetOneRes(GetPos(), obj);
		if(res != null)
		{
			ShowTj(res);
			if(mCb != null)
			{
				mCb.Show(res);
			}
		}
	}

	public void Clear()
	{
		mCb = null;
		mContext = null;
		if(mHolder != null)
		{
			mHolder.Clear();
			mHolder = null;
		}
	}

	public interface Callback
	{
		void Show(AbsAdRes res);
	}
}

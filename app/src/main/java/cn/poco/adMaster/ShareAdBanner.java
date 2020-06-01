package cn.poco.adMaster;

import android.content.Context;
import android.os.Handler;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsClickAdRes;
import com.adnonstop.admasterlibs.data.AdPackage;
import com.adnonstop.resourcelibs.CallbackHolder;

import java.util.ArrayList;

import cn.poco.banner.BannerCore3;
import cn.poco.credits.Credit;
import cn.poco.holder.IMessageHolder;
import cn.poco.statistics.TongJi2;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/8/22.
 * 分享页banner
 */

public class ShareAdBanner
{
	protected Context mContext;
	protected CallbackHolder<ArrayList<AdPackage>> mHolder;
	protected ArrayList<AbsAdRes> mRes;
	protected Callback mCb;

	//protected boolean m_showTj = false; //UI显示调用后为true
	//protected boolean m_sendShowTj = false; //只发送一次
	protected ArrayList<String> mOldTjs = new ArrayList<>();

	public ShareAdBanner(Context context)
	{
		mContext = context;
		mHolder = new CallbackHolder<>(new IMessageHolder.Callback<ArrayList<AdPackage>>()
		{
			@Override
			public void OnHandlerRun(ArrayList<AdPackage> obj)
			{
				ShowBanner(obj);

				//if(m_showTj)
				{
					ShowTj();
				}
			}
		});
		mHolder.SetHandler(new Handler());
	}

	protected String GetPos()
	{
		return "share";
	}

	public void Run(Callback cb)
	{
		mCb = cb;
		ArrayList<AdPackage> obj = ShareAdMaster.getInstance(mContext).sync_ar_GetCloudCacheRes(mContext, null, mHolder);
		ShowBanner(obj);
	}

	protected void ShowBanner(ArrayList<AdPackage> obj)
	{
		if(obj != null && obj.size() > 0)
		{
			AdPackage ad = obj.get(0);
			mRes = ad.GetAdByPos(GetPos());
			if(mCb != null)
			{
				mCb.ShowBanner(mRes);
			}
		}
		else
		{
			if(mCb != null)
			{
				mCb.ShowBanner(null);
			}
		}
	}

	public static boolean HasTj(ArrayList<String> arr, String url)
	{
		if(arr != null && url != null)
		{
			for(String temp : arr)
			{
				if(temp != null && temp.equals(url))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static void SendTjOnce(Context context, String[] arr, ArrayList<String> old)
	{
		if(arr != null)
		{
			for(String str : arr)
			{
				if(!HasTj(old, str))
				{
					if(old != null && str != null)
					{
						old.add(str);
					}
					SendTj(context, str);
				}
			}
		}
	}

	public static void SendTj(Context context, String[] arr)
	{
		if(arr != null)
		{
			for(String str : arr)
			{
				SendTj(context, str);
			}
		}
	}

	public static void SendTj(Context context, String url)
	{
		if(url != null && url.length() > 0)
		{
			if(url.startsWith("http"))
			{
				Utils.UrlTrigger(context, url);
			}
			else
			{
				try
				{
					int value = Integer.parseInt(url);
					if(value != 0)
					{
						TongJi2.AddCountById(url);
					}
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	protected void ShowTj()
	{
		//m_showTj = true;
		//if(!m_sendShowTj && mRes != null && mContext != null)
		if(mRes != null && mContext != null)
		{
			//m_sendShowTj = true;
			for(AbsAdRes temp : mRes)
			{
				SendTjOnce(mContext, temp.mShowTjs, mOldTjs);
			}
		}
	}

	public void Show()
	{
		ShowTj();
	}

	public void Click(AbsAdRes res, BannerCore3.CmdCallback cb)
	{
		if(res != null && mContext != null)
		{
			SendTj(mContext, res.mClickTjs);
			if(res instanceof AbsClickAdRes)
			{
				String cmdStr = ((AbsClickAdRes)res).mClick;
				Credit.BussinessCreditIncome(cmdStr, mContext, mContext.getResources().getInteger(R.integer.积分_看广告));
				if(cmdStr != null && cmdStr.length() > 0)
				{
					BannerCore3.ExecuteCommand(mContext, cmdStr, cb);
				}
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
		void ShowBanner(ArrayList<AbsAdRes> arr);
	}
}

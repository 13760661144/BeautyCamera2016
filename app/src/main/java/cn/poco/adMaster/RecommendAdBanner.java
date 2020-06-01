package cn.poco.adMaster;

import android.content.Context;

import com.adnonstop.admasterlibs.data.AdPackage;

import java.util.ArrayList;

/**
 * Created by Raining on 2017/8/22.
 * app推荐banner
 */

public class RecommendAdBanner extends ShareAdBanner
{
	public RecommendAdBanner(Context context)
	{
		super(context);
	}

	@Override
	protected String GetPos()
	{
		return "rmb";
	}

	@Override
	public void Run(Callback cb)
	{
		mCb = cb;
		ArrayList<AdPackage> obj = RecommendAdMaster.getInstance(mContext).sync_ar_GetCloudCacheRes(mContext, null, mHolder);
		ShowBanner(obj);
	}
}

package cn.poco.adMaster;

import android.content.Context;

import com.adnonstop.admasterlibs.data.AdPackage;

import java.util.ArrayList;

/**
 * Created by Raining on 2017/8/24.
 * 动态贴纸推荐banner
 */

public class StickerAdBanner extends ShareAdBanner
{
	public StickerAdBanner(Context context)
	{
		super(context);
	}

	@Override
	protected String GetPos()
	{
		return "stickers_share";
	}

	@Override
	public void Run(Callback cb)
	{
		mCb = cb;
		ArrayList<AdPackage> obj = StickerAdMaster.getInstance(mContext).sync_ar_GetCloudCacheRes(mContext, null, mHolder);
		ShowBanner(obj);
	}
}

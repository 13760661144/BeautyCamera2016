package cn.poco.ad.site;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;

import java.util.HashMap;

import cn.poco.ad.ADPage;
import cn.poco.beautify4.site.Beautify4PageSite6;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.AnimatorHolder;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.site.HomePageSite;
import cn.poco.share.site.SharePageSite4;
import cn.poco.utils.Utils;

public class ADPageSite extends BaseSite
{
	public ADPageSite()
	{
		super(SiteID.AD);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new ADPage(context, this);
	}

	public void OnSave(Bitmap bmp,Context context)
	{
		String path = FileCacheMgr.GetLinePath();
		if(path != null)
		{
			if(Utils.SaveTempImg(bmp, path))
			{
				HashMap<String, Object> params = new HashMap<>();
				HomePageSite.CloneBusinessParams(params, m_inParams);
				params.put("img", path);
//				MyFramework.SITE_Open(PocoCamera.main, SharePageSite4.class, params, Framework2.ANIM_NONE);
				MyFramework.SITE_Open(context, false, SharePageSite4.class, params, new AnimatorHolder()
				{
					@Override
					public void doAnimation(View oldView, View newView, final AnimatorListener lst)
					{
						lst.OnAnimationStart();
						Handler handler = new Handler();
						handler.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								lst.OnAnimationEnd();
							}
						}, 500);
					}
				});
			}
		}
	}

	public void OpenAdvBeauty(RotationImg2[] imgs,Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		HomePageSite.CloneBusinessParams(params, m_inParams);
		params.put("imgs", imgs);
		MyFramework.SITE_Open(context, Beautify4PageSite6.class, params, Framework2.ANIM_NONE);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}
}

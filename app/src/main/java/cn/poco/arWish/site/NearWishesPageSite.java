package cn.poco.arWish.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.site.AlbumSite19;
import cn.poco.arWish.NearWishesPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by Anson on 2018/1/18.
 */

public class NearWishesPageSite extends BaseSite
{
	public NearWishesPageSite()
	{
		super(SiteID.NEAR_ARWISH);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new NearWishesPage(context, this);
	}

	public void onAlbum(Context context)
	{
		MyFramework.SITE_Popup(context, AlbumSite19.class, null, Framework2.ANIM_TRANSLATION_TOP);
	}

	public void openFindPage(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_Open(context, FindWishPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnOpenPublishWishPage(Context context)
	{

	}
}

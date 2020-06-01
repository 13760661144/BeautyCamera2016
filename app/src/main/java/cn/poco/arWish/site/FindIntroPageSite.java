package cn.poco.arWish.site;

import android.content.Context;

import cn.poco.album.site.AlbumSite19;
import cn.poco.arWish.FindIntroPage;
import cn.poco.arWish.FindWishPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by Anson on 2018/1/22.
 */

public class FindIntroPageSite extends BaseSite
{
	public FindIntroPageSite()
	{
		super(SiteID.AR_FIND_INTRO);
	}

	private Context m_context;
	@Override
	public IPage MakePage(Context context)
	{
		m_context = context;
		return new FindIntroPage(context,this);
	}

	public void onBack()
	{
		MyFramework.SITE_Back(m_context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}


	/**
	 * 打开相册
	 */
	public void onAlbum()
	{
		MyFramework.SITE_Popup(m_context, AlbumSite19.class, null, Framework2.ANIM_TRANSLATION_TOP);
	}

	/**
	 * 附近找
	 */
	public void showNearList()
	{
		MyFramework.SITE_Open(m_context, NearWishesPageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}
}

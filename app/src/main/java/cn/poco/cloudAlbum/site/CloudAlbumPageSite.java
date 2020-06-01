package cn.poco.cloudAlbum.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.AlbumPage;
import cn.poco.album.site.AlbumSite2;
import cn.poco.cloudAlbum.CloudAlbumPage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite4;

/**
 * 云相册的site
 * 流程：从测滑菜单，点击云相册按钮进入
 */
public class CloudAlbumPageSite extends BaseSite
{
	public CloudAlbumPageSite()
	{
		super(SiteID.CLOUD_ALBUM);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new CloudAlbumPage(context, this);
	}


	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	/**
	 * 打开选图
	 */
	public void OpenPickPhoto(Context context, String albumName, long freeVolume)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("mode", AlbumPage.CLOUD);
		params.put("max", 20);
		params.put("album_name", albumName);
		params.put("free_volume", freeVolume);
		MyFramework.SITE_Popup(context, AlbumSite2.class, params, Framework2.ANIM_NONE);
	}

	public void OpenLogin(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
//        MyFramework.SITE_OpenAndClosePopup(context, false, LoginPageSite4.class, params, Framework2.ANIM_NONE);
        MyFramework.SITE_Open(context, false, LoginPageSite4.class, params, Framework2.ANIM_NONE);
	}
}

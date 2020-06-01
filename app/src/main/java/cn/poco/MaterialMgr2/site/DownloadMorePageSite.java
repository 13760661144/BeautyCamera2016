package cn.poco.MaterialMgr2.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.MaterialMgr2.DownloadMorePage;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.LoginPageSite1;

/**
 * 下载更多
 */
public class DownloadMorePageSite extends BaseSite
{
	public static final String DOWNLOAD_MORE_TYPE = "download_more_type";
	public static final String DOWNLOAD_MORE_DEL = "download_more_del";
	public static final String DOWNLOAD_MORE_ID = "download_more_id";

	public DownloadMorePageSite()
	{
		super(SiteID.DOWNLOAD_MORE);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new DownloadMorePage(context, DownloadMorePageSite.this);
	}

	/**
	 * @param params {@link DownloadMorePageSite#DOWNLOAD_MORE_TYPE}:ResType 打开下载更多的类型,传给父页面<br/>
	 *               {@link DownloadMorePageSite#DOWNLOAD_MORE_DEL}:boolean 是否有删除元素操作
	 */
	public void OnBack(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_ClosePopup(context, params, Framework2.ANIM_NONE);
	}

	public void OnManagePageOpen(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_Popup(context, ManagePageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	/**
	 * @param params {@link DownloadMorePageSite#DOWNLOAD_MORE_TYPE}:ResType<br/>
	 *               {@link DownloadMorePageSite#DOWNLOAD_MORE_ID}:int<br/>
	 */
	public void OnResourceUse(Context context, HashMap<String, Object> params)
	{
		MyFramework.SITE_ClosePopup2(context, params, 1, Framework2.ANIM_NONE);
	}

	public void OnLogin(Context context)
	{
		MyFramework.SITE_Popup(context, LoginPageSite1.class, null, Framework2.ANIM_NONE);
	}
}

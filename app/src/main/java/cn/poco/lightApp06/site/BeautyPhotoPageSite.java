package cn.poco.lightApp06.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.community.site.PublishOpusSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.site.HomePageSite;
import cn.poco.lightApp06.BeautyPhotoPage;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.login.site.LoginPageSite10;

/**
 * Created by pocouser on 2017/12/13.
 */

public class BeautyPhotoPageSite extends BaseSite
{
	public BeautyPhotoPageSite()
	{
		super(SiteID.BEAUTY_PHOTO_PREVIEW_SHARE);
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new BeautyPhotoPage(context, this);
	}

	public void onBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	/**
	 * 将保存的图片分享到社区
	 *
	 * @param context
	 * @param filePath
	 * @param circleExtras {@link cn.poco.camera.site.CameraPageSite300#makeCircleExtra(int, String)}
	 */
	public void onSaveToCommunity(Context context, String filePath, String circleExtras)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("imgPath", new String[]{filePath});
		params.put(DataKey.COMMUNITY_SEND_CIRCLE_EXTRA, circleExtras);
		MyFramework.SITE_BackTo(context, HomePageSite.class, params, Framework2.ANIM_NONE);
	}

	/**
	 * 分享到社区
	 * @param path 路径
	 * @param type 类型 1:图片 2:视频 3: gif
	 */
	public void onCommunity(Context context, String path, int type){
		HashMap<String,Object> params=new HashMap<>();
		params.put("path", path);
		params.put("type", type);
		params.put("content", "");
		MyFramework.SITE_Popup(context, PublishOpusSite.class, params, Framework2.ANIM_NONE);
	}

	public void OnLogin(Context context) {
		MyFramework.SITE_Popup(context, LoginPageSite10.class, null, Framework2.ANIM_NONE);
	}

	public void onBindPhone(Context context){
		MyFramework.SITE_Popup(context, BindPhonePageSite.class, null, Framework2.ANIM_NONE);
	}

	public void OnCommunityHome(Context context)
	{
		HashMap<String, Object> param = new HashMap<>();
		param.put(Home4Page.KEY_CUR_MODE, Home4Page.CAMPAIGN);

		HashMap<String, Object> data = new HashMap<>();
		data.put("openFriendPage", true);

		param.put(Home4Page.KEY_TOP_DATA, data);

		MyFramework.SITE_Open(context, true, HomePageSite.class, param, Framework2.ANIM_NONE);
	}
}

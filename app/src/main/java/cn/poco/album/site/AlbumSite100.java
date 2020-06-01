package cn.poco.album.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.album.AlbumPage100;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;

/**
 * Created by: fwc
 * Date: 2017/9/5
 * 视频相册
 */
public class AlbumSite100 extends BaseSite {


	public AlbumSite100() {
		super(SiteID.ALBUM);
	}

	@Override
	public IPage MakePage(Context context) {
		return new AlbumPage100(context, this);
	}

	public void onBack(Context context) {
		MyFramework.SITE_ClosePopup(context, null, Framework2.ANIM_NONE);
	}

	public void onVideoSelected(Context context, HashMap<String, Object> params) {
		MyFramework.SITE_ClosePopup(context, params, Framework2.ANIM_NONE);
	}
}

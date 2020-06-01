package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * Created by pocouser on 2018/1/29.
 */

public class EditHeadIconImgPageSite403 extends EditHeadIconImgPageSite
{
	@Override
	public void upLoadSuccess(HashMap<String, Object> datas, Context context)
	{
		MyFramework.SITE_BackTo(context, RegisterLoginInfoPageSite403.class, datas, Framework2.ANIM_NONE);
	}

	@Override
	public void onBackToLastPage(Context context)
	{
		HashMap<String,Object> temp = new HashMap<>();
		temp.put("isBack",true);
		MyFramework.SITE_BackTo(context,RegisterLoginInfoPageSite403.class,temp,Framework2.ANIM_TRANSLATION_TOP);
	}
}

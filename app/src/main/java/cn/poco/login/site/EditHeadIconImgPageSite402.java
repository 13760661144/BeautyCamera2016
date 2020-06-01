package cn.poco.login.site;

import android.content.Context;

import java.util.HashMap;

import cn.poco.framework.MyFramework;
import cn.poco.framework2.Framework2;

/**
 * 社区到注册，到上传头像
 */

public class EditHeadIconImgPageSite402 extends EditHeadIconImgPageSite
{
	@Override
	public void upLoadSuccess(HashMap<String,Object> datas, Context context)
	{
		MyFramework.SITE_BackTo(context, RegisterLoginInfoPageSite402.class, datas, Framework2.ANIM_NONE);
	}
	@Override
	public void onBackToLastPage(Context context)
	{
		MyFramework.SITE_BackTo(context,RegisterLoginInfoPageSite402.class,null,Framework2.ANIM_TRANSLATION_TOP);
	}

	@Override
	public void onBack(Context context)
	{
		MyFramework.SITE_ClosePopup(context,null,Framework2.ANIM_TRANSLATION_TOP);
	}
}

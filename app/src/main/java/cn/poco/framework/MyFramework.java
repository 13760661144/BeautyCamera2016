package cn.poco.framework;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.poco.framework2.BaseFrameworkActivity;
import cn.poco.framework2.IFramework;

public class MyFramework
{
	public static void SITE_Open(Context context, boolean newLink, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, AnimatorHolder holder)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_Open(context, newLink, siteClass, params, holder);
		}
	}

	public static void SITE_Open(Context context, boolean newLink, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_Open(context, newLink, siteClass, params, animType);
		}
	}

	public static void SITE_Open(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_Open(context, siteClass, params, animType);
		}
	}

	public static void SITE_Popup(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_Popup(context, siteClass, params, animType);
		}
	}

	public static void SITE_ClosePopup(Context context, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_ClosePopup(context, params, animType);
		}
	}

	public static void SITE_ClosePopup2(Context context, HashMap<String, Object> params, int layerNum, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_ClosePopup2(context, params, layerNum, animType);
		}
	}

	public static void SITE_OpenAndClosePopup(Context context, boolean newLink, int layerNum, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_OpenAndClosePopup(context, newLink, layerNum, siteClass, params, animType);
		}
	}

	public static void SITE_OpenAndClosePopup(Context context, boolean newLink, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_OpenAndClosePopup(context, newLink, siteClass, params, animType);
		}
	}

	public static void SITE_OpenAndClosePopup(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_OpenAndClosePopup(context, siteClass, params, animType);
		}

	}

	public static void SITE_BackTo(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, AnimatorHolder holder)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_BackTo(context, siteClass, params, holder);
		}
	}

	public static void SITE_BackTo(Context context, Class<? extends BaseSite> siteClass, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_BackTo(context, siteClass, params, animType);
		}
	}

	public static void SITE_Back(Context context, HashMap<String, Object> params, AnimatorHolder holder)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_BackTo(context, null, params, holder);
		}

	}

	public static void SITE_Back(Context context, HashMap<String, Object> params, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_BackTo(context, null, params, animType);
		}
	}

	public static void SITE_BackAndOpen(Context context, Class<? extends BaseSite> backClass, Class<? extends BaseSite> openClass, HashMap<String, Object> openParams, int animType)
	{
		if(context instanceof IFramework)
		{
			((IFramework)context).SITE_BackAndOpen(context, backClass, openClass, openParams, animType);
		}
	}

	public static void SITE_Finish(Context context, int resultCode, Intent data)
	{
		if(context instanceof BaseFrameworkActivity)
		{
			((BaseFrameworkActivity)context).SITE_Finish(context, resultCode, data);
		}
	}

	public static IPage GetCurrentPage(Context context, Class<? extends IPage> pageClass)
	{
		if(context instanceof IFramework)
		{
			return ((IFramework)context).GetCurrentPage(pageClass);
		}
		return null;
	}

	public static BaseSite GetLinkSite(Context context, Class<? extends BaseSite> siteClass)
	{
		if(context instanceof IFramework)
		{
			return ((IFramework)context).GetLinkSite(siteClass);
		}
		return null;
	}

	public static int GetCurrentIndex(Context context)
	{
		if(context instanceof IFramework)
		{
			return ((IFramework)context).GetCurrentIndex();
		}
		return -1;
	}

	public static IPage GetTopPage(Context context)
	{
		if(context instanceof IFramework)
		{
			return ((IFramework)context).GetTopPage();
		}
		return null;
	}

	public static void AddTopView(Context context, View view, FrameLayout.LayoutParams fl)
	{
		if(context instanceof BaseFrameworkActivity)
		{
			((BaseFrameworkActivity)context).AddTopView(view, fl);
		}
	}

	public static void ClearTopView(Context context)
	{
		if(context instanceof BaseFrameworkActivity)
		{
			((BaseFrameworkActivity)context).ClearTopView();
		}
	}

	public static FrameLayout GetTopView(Context context)
	{
		if(context instanceof BaseFrameworkActivity)
		{
			return ((BaseFrameworkActivity)context).GetTopView();
		}
		return null;
	}

	public static final String EXTERNAL_CALL_TYPE = "EC_TYPE";
	public static final String EXTERNAL_CALL_IMG_SAVE_URI = "EC_IMG_SAVE_URI";

	public static final int EXTERNAL_CALL_TYPE_EDIT = 0x01;
	public static final int EXTERNAL_CALL_TYPE_CAMERA = 0x02;
	public static final int EXTERNAL_CALL_TYPE_CAMERA_VIDEO = 0x04;

	/**
	 * 复制外部调用的参数
	 *
	 * @param src
	 * @param dst
	 */
	public static void CopyExternalCallParams(HashMap<String, Object> src, HashMap<String, Object> dst)
	{
		if(src != null && dst != null)
		{
			Object obj;
			obj = src.get(EXTERNAL_CALL_TYPE);
			if(obj != null)
			{
				dst.put(EXTERNAL_CALL_TYPE, obj);
			}
			obj = src.get(EXTERNAL_CALL_IMG_SAVE_URI);
			if(obj != null)
			{
				dst.put(EXTERNAL_CALL_IMG_SAVE_URI, obj);
			}
		}
	}
}

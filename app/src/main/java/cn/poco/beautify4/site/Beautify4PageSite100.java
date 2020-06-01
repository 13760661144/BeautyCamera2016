package cn.poco.beautify4.site;

import android.content.Context;

import java.util.HashMap;

import my.beautyCamera.PocoCamera;
import my.beautyCamera.site.activity.MainActivitySite;

/**
 * Created by Raining on 2016/12/8.
 * 第三方调用
 */

public class Beautify4PageSite100 extends Beautify4PageSite
{
	@Override
	public void OnSave(Context context, HashMap<String, Object> params)
	{
		if(context instanceof PocoCamera)
		{
			MainActivitySite site = ((PocoCamera)context).getActivitySite();
			if(site != null)
			{
				site.OnSave(context, m_inParams, params);
			}
		}
	}
}

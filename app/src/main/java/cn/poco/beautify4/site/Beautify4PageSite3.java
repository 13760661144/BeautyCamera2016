package cn.poco.beautify4.site;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Raining on 2016/12/8.
 * 滤镜页进入
 */

public class Beautify4PageSite3 extends Beautify4PageSite
{
	@Override
	public void OnSave(Context context, HashMap<String, Object> params)
	{
		if (params != null) params.put("from_camera", true);
		super.OnSave(context, params);
	}
}

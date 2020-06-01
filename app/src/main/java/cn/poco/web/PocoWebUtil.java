package cn.poco.web;

import org.json.JSONObject;

import cn.poco.pocointerfacelibs.PocoWebUtils;
import cn.poco.web.info.UpdateInfo;

/**
 * Created by Raining on 2016/11/3.
 * 获取网络信息，更新状态等
 */

public class PocoWebUtil
{
	/**
	 *
	 * @param user_id
	 * @param iWeb
	 * @return
	 */
	public static UpdateInfo getAppUpdateInfo(String user_id, String systemVersion, IWeb iWeb)
	{
		UpdateInfo updateInfo = null;
		try
		{
			JSONObject json = new JSONObject();
			json.put("user_id", user_id);
			json.put("system_version", systemVersion);
			updateInfo = (UpdateInfo)PocoWebUtils.Get(UpdateInfo.class, iWeb.GetAppUpdateInfoUrl(), false, json, null, iWeb);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return updateInfo;
	}
}

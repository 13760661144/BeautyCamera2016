package cn.poco.arWish.serviceAPI;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.circle.common.serverapi.ProtocolParams;
import com.circle.utils.HttpExecutor;
import com.circle.utils.JSONQuery;
import com.circle.utils.Utils;
import com.taotie.circle.Configure;
import com.taotie.circle.PLog;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.poco.arWish.dataInfo.WishItemInfo;
import cn.poco.arWish.dataInfo.WishPageInfo;
import cn.poco.protocol.PocoProtocol;
import cn.poco.tianutils.CommonUtils;

/**
 * Created by Anson on 2018/1/24.
 */

public class ServiceAPI
{
	/***************************************
	 * 美人AR接口（create by Anson）
	 *********/
	//http://zt.adnonstop.com/index.php?r=api/v1/appdata/ar-lists
	private final static String[] URL_GET_ARWISH_LIST = {"http://tw.adnonstop.com/zt/web/index.php?r=api/v1/appdata/ar-lists", "GET"};

	/*********************AR结束***************************/




	/**
	 * @param url
	 * @param paramJson
	 * @param mon
	 * @return
	 */
	public static String getServiceJSON(Context context, String url, JSONObject paramJson, String mon)
	{
		String result = null;
		String jsonString = null;
		JSONObject postJson = new JSONObject();
		try
		{
			if(!paramJson.has("user_id"))
			{
				paramJson.put("user_id", Configure.getLoginUid());
			}
			if(!paramJson.has("access_token"))
			{
				paramJson.put("access_token", Configure.getLoginToken());
			}
			String param = new StringBuilder().append("poco_").append(paramJson.toString()).append("_app").toString();
			String signStr = Utils.md5sum(param);
			String signCode = signStr.substring(5, (signStr.length() - 8));
			postJson.put("version", CommonUtils.GetAppVer(context));
			postJson.put("os_type", "android");
			postJson.put("device", ProtocolParams.sDevice);
			postJson.put("ctime", System.currentTimeMillis());
			postJson.put("app_name", "beauty_camera_android");
			postJson.put("is_enc", 0);
			postJson.put("sign_code", signCode);
			postJson.put("imei", CommonUtils.GetIMEI(context));
			postJson.put("param", paramJson);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		jsonString = postJson.toString();
		PLog.out("anson", "发送参数    url--" + url + ":" + jsonString);
		if(mon != null && mon.equals("GET"))
		{
			jsonString = new String(android.util.Base64.encode(jsonString.getBytes(), android.util.Base64.NO_WRAP | android.util.Base64.URL_SAFE));
		}
		ArrayList<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
		params.add(new Pair<String, String>("req", jsonString));
		HttpExecutor httpExecutor = new HttpExecutor();
		result = httpExecutor.openUrl2(url, mon, params, null, null, null);
		PLog.out("anson", "返回--url " + url + ":");
		if (result != null)
		{
			for (int i = 0; i * 1024 * 3 < result.length(); i++)
			{
				if ((i + 1) * 1024 * 3 < result.length())
				{
					PLog.out("anson", result.substring(i * 1024 * 3, (i + 1) * 1024 * 3));
				} else
				{
					PLog.out("anson", result.substring(i * 1024 * 3));
				}
			}
		}
		if(result != null)
		{
			result = checkResult(result, url, mon, paramJson);
		}

		return result;
	}

	/**
	 * @param result
	 * @return
	 */
	private static String checkResult(String result, String url, String mon, final JSONObject paramJson)
	{
		String mResult = null;
		if(!TextUtils.isEmpty(result))
		{
			try
			{
				JSONObject mJsonObject = new JSONObject(result);
				if(mJsonObject.has("code"))
				{
					int resultCode = mJsonObject.getInt("code");
					switch(resultCode)
					{
						case 200:
							mResult = result;

							break;
						case 205:
							mResult = result;
							break;
						case 216:
						case 217:
						{
							mResult = null;
						}
						break;
						default:
							mResult = result;
							break;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return mResult;
	}

	public static WishPageInfo getWishPageInfo(Context context, JSONObject params)
	{
		String result = getServiceJSON(context, URL_GET_ARWISH_LIST[0], params, URL_GET_ARWISH_LIST[1]);
		return decodeWishPageInfo(result);
	}

	public static WishPageInfo getWishPageInfo(String version,JSONObject params)
	{
		byte[] result = PocoProtocol.Get(URL_GET_ARWISH_LIST[0], version, "beauty_camera_android", false, ProtocolParams.sIMei, params, null);
		return decodeWishPageInfo(new String(result));
	}

	private static WishPageInfo decodeWishPageInfo(String result)
	{
		WishPageInfo pageInfo = null;
		if(!TextUtils.isEmpty(result))
		{
			pageInfo = new WishPageInfo();
			JSONQuery query = new JSONQuery(result);
			if(query.getInt("code") == 200)
			{
				int code = query.getInt("data.ret_code");
				if(code != 0)
				{
					return null;
				}
				pageInfo.rangLocation = query.getString("data.ret_region");
				ArrayList<WishItemInfo> lists = new ArrayList<WishItemInfo>();
				JSONQuery[] jQArray = query.getJSONQueryArray("data.ret_data");
				if(jQArray != null && jQArray.length >0)
				{
					for(JSONQuery jQuery : jQArray)
					{
						WishItemInfo item = new WishItemInfo();
						item.imageUrl = jQuery.getString("img");
						item.videoUrl = jQuery.getString("video_url");
						item.userIcon = jQuery.getString("avatar");
						item.userName = jQuery.getString("nickname");
						item.isSecret = jQuery.getBoolean("secret");
						item.wishId = jQuery.getString("show_id");
						lists.add(item);
					}
					pageInfo.itemInfos = lists;
				}
			}
		}
		return  pageInfo;
	}
}

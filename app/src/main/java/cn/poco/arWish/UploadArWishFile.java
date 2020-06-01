package cn.poco.arWish;

import android.content.Context;

import com.adnonstop.admasterlibs.AbsAdIStorage;
import com.adnonstop.admasterlibs.AbsUploadFile;
import com.adnonstop.admasterlibs.IAd;
import com.adnonstop.admasterlibs.data.UploadData;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;

import cn.poco.adMaster.UploadFile;
import cn.poco.holder.ObjHandlerHolder;
import cn.poco.protocol.PocoProtocol;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;

/**
 * Created by pocouser on 2018/1/24.
 */

public class UploadArWishFile extends UploadFile
{
	private static final String URL = "http://zt.adnonstop.com/index.php?r=api/v1/appdata/ar-add";
	private static final String DEBUG_URL = "http://tw.adnonstop.com/zt/web/index.php?r=api/v1/appdata/ar-add";


	public UploadArWishFile(Context context, UploadData data, IAd iAd, ObjHandlerHolder<Callback> callback)
	{
		super(context, data, iAd, callback);
	}

	@Override
	protected void UploadMyWeb()
	{
		try
		{
			final JSONObject postJson = new JSONObject();
			JSONObject paramJson = new JSONObject();
			SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(mContext);

			if(settingInfo != null && settingInfo.GetPoco2Id(true) != null)
			{
				paramJson.put("user_id", settingInfo.GetPoco2Id(false));
				paramJson.put("nickname", settingInfo.GetPocoNick());
				paramJson.put("avatar", settingInfo.GetPoco2HeadUrl());
			}
			else
			{
				paramJson.put("user_id", "0");
				paramJson.put("nickname", "");
				paramJson.put("avatar", "");
			}
			paramJson.put("img", mImgUrl);
			paramJson.put("video_url", mVideoUrl);

			String param = new StringBuilder().append("poco_").append(paramJson.toString()).append("_app").toString();
			String signStr = CommonUtils.Encrypt("MD5", param.toString());
			String signCode = signStr.substring(5, (signStr.length() - 8));

			postJson.put("version", CommonUtils.GetAppVer(mContext));
			postJson.put("os_type", "android");
			postJson.put("ctime", System.currentTimeMillis());
			postJson.put("app_name", "beauty_camera_android");
			postJson.put("is_enc", 0);
			postJson.put("sign_code", signCode);
			postJson.put("imei", CommonUtils.GetIMEI(mContext));
			postJson.put("param", paramJson);

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String url = SysConfig.IsDebug() ? DEBUG_URL : URL;

						String jsonString = postJson.toString();
						NetCore2 net = new NetCore2();
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("req", jsonString);
						NetCore2.NetMsg msg = net.HttpPost(url, map, null);
						if(msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK && msg.m_data != null)
						{
							mShareUrl = getShareUrl(new String(msg.m_data));
						}
						if(net != null) net.ClearAll();

						if(mHolder != null)
						{
							mHolder.post(new Runnable()
							{
								@Override
								public void run()
								{
									if(mHolder != null)
									{
										AbsUploadFile.Callback cb = mHolder.GetObj();
										if(cb != null)
										{
											if(mShareUrl != null && mShareUrl.length() > 0)
											{
												cb.onProgress(100);
												cb.onSuccess(mShareUrl);
											}
											else
											{
												cb.onFailure();
											}
										}
									}
								}
							});
						}
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
					//完成上传
					mIsRunning = false;
				}
			}).start();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	private String getShareUrl(String json)
	{
		if(json == null) return null;
		try
		{
			JSONObject jsonObject = new JSONObject(json);
			int code = jsonObject.getInt("code");
			if(code != 200) return null;
			JSONObject dataJsonObject = jsonObject.getJSONObject("data");
			int ret_code = dataJsonObject.getInt("ret_code");
			if(ret_code != 0) return null;
			JSONObject retDataJsonObject = dataJsonObject.getJSONObject("ret_data");
			return retDataJsonObject.getString("word");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}

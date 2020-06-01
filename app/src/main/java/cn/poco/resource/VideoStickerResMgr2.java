package cn.poco.resource;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

import cn.poco.dynamicSticker.ShowType;
import cn.poco.dynamicSticker.StickerHelper;
import cn.poco.dynamicSticker.v2.StickerJsonParse;
import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.resource.protocol.MaterialResourceProtocol;
import cn.poco.resource.protocol.PageType;
import cn.poco.resource.protocol.ResourceGroup;
import cn.poco.system.AppInterface;
import cn.poco.utils.FileUtil;
import my.beautyCamera.R;

import static cn.poco.resource.ResourceUtils.HasItem;

/**
 * Created by Raining on 2017/10/8.
 */

public class VideoStickerResMgr2 extends BaseResMgr<VideoStickerRes, ArrayList<VideoStickerRes>>
{
	public static final int NEW_JSON_VER = 4;//FIXME 动态贴纸sd卡json版本号，若结构改动需升级版本号
	public final static int NEW_ORDER_JSON_VER = 4;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().VIDEO_FACE_PATH + "/video_face.xxxx"; //资源集合

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().VIDEO_FACE_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://open.adnonstop.com/app_source/biz/prod/api/public/index.php?r=Template/GetTemplateList";// 动态贴纸链接

	//test 内测
//    protected final String CLOUD_URL = "http://tw.adnonstop.com/beauty/app/api/app_source/biz/beta/api/public/index.php?r=Template/GetTemplateList";


	public final static String NEW_DOWNLOAD_FLAG = "video_face"; //记录在Preferences
	public ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "videoface_id";


	private static VideoStickerResMgr2 sInstance;
	private VideoStickerResMgr2()
	{
	}

	public synchronized static VideoStickerResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new VideoStickerResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<VideoStickerRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<VideoStickerRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<VideoStickerRes> arr, VideoStickerRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<VideoStickerRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<VideoStickerRes> out = new ArrayList<>();
		//NOTE 动态贴纸内置素材，常用组合变形素材，需要标记m_is_shape_compose为true

        VideoStickerRes res;
		res = new VideoStickerRes();
		res.m_id = 2189;
		res.m_tjId = 106010364;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_name = "小黑猫";
		res.m_thumb = R.drawable.__vis__7083201706011000533424690;
		res.m_shape_type = "natureMakeUp";
		res.m_show_type = "both";
		res.m_show_type_level = 3;
		res.m_has_music = false;
		res.m_res_path = "file:///android_asset/stickers/15482514220171214167art41150.zip";
		res.m_res_name = "15482514220171214167art41150.zip";
		out.add(res);

		res = new VideoStickerRes();
		res.m_id = 2220;
		res.m_tjId = 106009969;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_name = "元气少女";
		res.m_thumb = R.drawable.__vis__758081548251425919459bb8708a3317;
		res.m_shape_type = "none";
		res.m_show_type = "both";
		res.m_show_type_level = 3;
		res.m_has_music = false;
		res.m_res_path = "file:///android_asset/stickers/1548251422017091515art38348.zip";
		res.m_res_name = "1548251422017091515art38348.zip";
		out.add(res);

		res = new VideoStickerRes();
		res.m_id = 2148;
		res.m_tjId = 106014151;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_name = "情人节米奇";
		res.m_thumb = R.drawable.__vis__979671548251424451659bb86f48e497;
		res.m_shape_type = "natureMakeUp";
		res.m_show_type = "both";
		res.m_show_type_level = 3;
		res.m_has_music = false;
		res.m_res_path = "file:///android_asset/stickers/154825142201712151511art41153.zip";
		res.m_res_name = "154825142201712151511art41153.zip";
		out.add(res);

		return out;
	}

	public ArrayList<VideoStickerRes> GetAllLocalRes(boolean isBusiness)
	{
		ArrayList<VideoStickerRes> out = null;
		ArrayList<VideoStickerRes> list = sync_GetLocalRes(MyFramework2App.getInstance().getApplicationContext(), null);
		if(list != null && list.size() > 0)
		{
			out = new ArrayList<>();
			out.addAll(list);

			Iterator<VideoStickerRes> iterator = out.iterator();
			while(iterator.hasNext())
			{
				VideoStickerRes next = iterator.next();
				if(next != null)
				{
					//id：38559 纪梵希201708商业
					//id：39165 阿玛尼 201710商业（正常通道素材不显示，商业通道显示）
					if(next.m_id == 38559 || next.m_id == 39165)
					{
						next.m_is_business = true;
						if(!isBusiness)
						{
							iterator.remove();
						}
					}
					else if(isBusiness && !next.m_is_business)
					{
						//删除非商业素材
						iterator.remove();
					}
				}
			}
			iterator = null;
		}
		return out;
	}

	public ArrayList<VideoStickerRes> GetLocalResArr(boolean isGifMode, boolean isBusiness)
	{
		ArrayList<VideoStickerRes> out = GetAllLocalRes(isBusiness);
		if(out != null && out.size() > 0)
		{
			int[] ids = new int[out.size()];
			int index = 0;
			for(VideoStickerRes stickerRes : out)
			{
				if(stickerRes != null)
				{
					ids[index++] = stickerRes.m_id;
				}
			}

			if(ids.length > 0)
			{
				out = BuildGroupRes(ids, out, isGifMode);
			}
		}

		return out;
	}

	@Override
	protected Object sync_raw_ReadCloudData(Context context, DataFilter filter)
	{
		byte[] data = null;

		try
		{
			data = MaterialResourceProtocol.Get(GetCloudUrl(context), MaterialResourceProtocol.MATERIAL_RESOURCE_SERVER_VERSION, false, AppInterface.GetInstance(context).GetMKey(), MaterialResourceProtocol.GetReqParams(PageType.STICKER, MaterialResourceProtocol.IS_DEBUG, new ResourceGroup[]{ResourceGroup.LOCAL_RES, ResourceGroup.DOWNLOAD}), MaterialResourceProtocol.GetReqComeFromParams(context), null);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * 素材列表数据jsonArray
	 *
	 * @param jsonObject
	 * @return
	 */
	public static JSONArray GetRetDataList(JSONObject jsonObject)
	{
		JSONArray out = null;
		if(jsonObject != null)
		{
			try
			{
				if(jsonObject.has("data"))
				{
					JSONObject data = jsonObject.getJSONObject("data");
					if(data.has("ret_code") && data.getInt("ret_code") == 0)
					{
						if(data.has("ret_data"))
						{
							JSONObject ret_data = data.getJSONObject("ret_data");
							if(ret_data != null && ret_data.has("list"))
							{
								out = ret_data.getJSONArray("list");
							}
						}
					}
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		return out;
	}

	@Override
	protected ArrayList<VideoStickerRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
	{
		ArrayList<VideoStickerRes> out = null;

		try
		{
			if(data != null)
			{
				JSONObject jsonObject = new JSONObject(new String((byte[])data));
				JSONArray listArr = GetRetDataList(jsonObject);
				if(listArr != null)
				{
					out = new ArrayList<>();

					VideoStickerRes res;
					int length = listArr.length();
					for(int i = 0; i < length; i++)
					{
						Object obj = listArr.get(i);
						if(obj != null && obj instanceof JSONObject)
						{
							res = ReadResItem((JSONObject)obj, false);
							if(res != null)
							{
								out.add(res);
							}
						}
					}

					//解锁素材特殊处理
					String listStr = listArr.toString();
					LockResMgr2.getInstance().decodeVideoFaceLockArr(listStr);
					LimitResMgr.m_videoFaceLimitArr = LimitResMgr.GetVideoFaceLimitArr(listStr);
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	@Override
	protected ArrayList<VideoStickerRes> sync_DecodeSdcardRes(Context context, DataFilter filter, Object data)
	{
		return super.sync_DecodeSdcardRes(context, filter, data);
	}

	@Override
	public boolean CheckIntact(VideoStickerRes res)
	{
		boolean out = false;
		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb))
			{
				//当shapeType 不为null 或 none, content内容可以为空，zip包本地路径不能为空
				if(!TextUtils.isEmpty(res.m_shape_type) && !"none".equals(res.m_shape_type))
				{
					out = true;
				}
				else if(res.m_res_path != null && !TextUtils.isEmpty(res.m_res_path))
				{
					out = true;
				}
			}
		}
		return out;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<VideoStickerRes> arr)
	{
		FileOutputStream fos = null;

		try
		{
			JSONObject json = new JSONObject();
			{
				json.put("ver", NEW_JSON_VER);

				JSONArray jArr = new JSONArray();
				{
					if(arr != null)
					{
						JSONObject resJson = null;
						for(VideoStickerRes res : arr)
						{
							if(res != null)
							{
								resJson = new JSONObject();
								resJson.put("id", Integer.toString(res.m_id));
								resJson.put("tjid", Integer.toString(res.m_tjId));
								resJson.put("tracking_link", res.m_tracking_link != null ? res.m_tracking_link : "");
								resJson.put("thumb_tracking_link", !TextUtils.isEmpty(res.m_thumb_tracking_link) ? res.m_thumb_tracking_link : "");
								resJson.put("name", res.m_name != null ? res.m_name : "");
								resJson.put("thumb", (res.m_thumb != null && res.m_thumb instanceof String) ? res.m_thumb : "");
								resJson.put("bgcolor", Integer.toHexString(res.m_bg_color));
								resJson.put("shapeType", res.m_shape_type);
								resJson.put("showType", res.m_show_type);
								resJson.put("res", !TextUtils.isEmpty(res.m_res_path) ? res.m_res_path : "");
								resJson.put("res_name", !TextUtils.isEmpty(res.m_res_name) ? res.m_res_name : "");
								resJson.put("music", res.m_has_music);
								resJson.put("promptText", !TextUtils.isEmpty(res.m_prompt_text) ? res.m_prompt_text : "");
								resJson.put("url_thumb", !TextUtils.isEmpty(res.url_thumb) ? res.url_thumb : "");
								resJson.put("url_res", !TextUtils.isEmpty(res.m_res_url) ? res.m_res_url : "");
                                resJson.put("isAR4iOS", res.m_isAR4iOS);
								jArr.put(resJson);
							}
						}
					}
				}
				json.put("data", jArr);
			}
			fos = new FileOutputStream(SDCARD_PATH);
			fos.write(json.toString().getBytes());
			fos.flush();
		}
		catch(Throwable t)
		{
			fos = null;
			t.printStackTrace();
		}
		finally
		{
			if(fos != null)
			{
				try
				{
					fos.close();
				}
				catch(Throwable t)
				{
					t.printStackTrace();
				}
			}
		}
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.VIDEO_FACE_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected VideoStickerRes ReadResItem(JSONObject jsonObject, boolean isPath)
	{
		VideoStickerRes out = null;

		if(jsonObject != null)
		{
			try
			{
				out = new VideoStickerRes();

				if(isPath)
				{
					out.m_type = BaseRes.TYPE_LOCAL_PATH;
				}
				else
				{
					out.m_type = BaseRes.TYPE_NETWORK_URL;
				}

				//id
				String temp;
				if(jsonObject.has("id"))
				{
					temp = jsonObject.getString("id");
					if(!TextUtils.isEmpty(temp))
					{
						out.m_id = (int)Long.parseLong(temp, 10);
					}
					else
					{
						out.m_id = (int)(Math.random() * 10000000);
					}
				}
				//tjid
				if(jsonObject.has("tjid"))
				{
					temp = jsonObject.getString("tjid");
					if(!TextUtils.isEmpty(temp))
					{
						out.m_tjId = Integer.valueOf(temp);
					}
				}
				//tracking_link
				if(jsonObject.has("tracking_link"))
				{
					Object obj = jsonObject.get("tracking_link");
					if(obj != null && obj instanceof String)
					{
						out.m_tracking_link = (String)obj;
					}
				}
				//thumb_tracking_link
				if(jsonObject.has("thumb_tracking_link"))
				{
					if(jsonObject.has("thumb_tracking_link"))
					{
						temp = jsonObject.getString("thumb_tracking_link");
						if(!TextUtils.isEmpty(temp))
						{
							out.m_thumb_tracking_link = temp;
						}
					}
				}
				//name
				if(jsonObject.has("name"))
				{
					temp = jsonObject.getString("name");
					if(!TextUtils.isEmpty(temp))
					{
						out.m_name = temp;
					}
				}
				//thumb
				if(isPath)
				{
					if(jsonObject.has("thumb"))
					{
						out.m_thumb = jsonObject.getString("thumb");
					}
				}
				else
				{
					if(jsonObject.has("thumb"))
					{
						out.url_thumb = jsonObject.getString("thumb");
					}
				}

				//sd card
				if(isPath)
				{
					//url thumb
					if(jsonObject.has("url_thumb"))
					{
						out.url_thumb = jsonObject.getString("url_thumb");
					}

					//url res
					if(jsonObject.has("url_res"))
					{
						out.m_res_url = jsonObject.getString("url_res");
					}
				}

				//showType
				if(jsonObject.has("showType"))
				{
					temp = jsonObject.getString("showType");
					if(!TextUtils.isEmpty(temp))
					{
						if(temp.startsWith(ShowType.STICKER))
						{
							out.m_show_type_level = ShowType.GetType(ShowType.STICKER);
						}
						else if(temp.startsWith(ShowType.GIF))
						{
							out.m_show_type_level = ShowType.GetType(ShowType.GIF);
						}
						else
						{
							out.m_show_type_level = ShowType.GetType(ShowType.BOTH);
						}
						out.m_show_type = temp;
					}
				}
				//bgcolor
				if(jsonObject.has("bgcolor"))
				{
					temp = jsonObject.getString("bgcolor");
					if(!TextUtils.isEmpty(temp) && temp.length() >= 6)
					{
						try
						{
							if(temp.length() != 7 && temp.length() != 9)
							{
								if(temp.length() > 8)
								{
									temp = temp.substring(temp.length() - 8, temp.length());
								}
								temp = "#" + temp;
							}
							out.m_bg_color = Color.parseColor(temp);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				//shapeType
				if(jsonObject.has("shapeType"))
				{
					out.m_shape_type = jsonObject.getString("shapeType");
				}
				//zip资源
				if(isPath)
				{
					if(jsonObject.has("res"))
					{
						temp = jsonObject.getString("res");
						if(!TextUtils.isEmpty(temp))
						{
							out.m_res_path = temp;//xxx/video_face/id/xxx.img(.zip)
						}
					}
				}
				else
				{
					if(jsonObject.has("res"))
					{
						temp = jsonObject.getString("res");
						if(!TextUtils.isEmpty(temp))
						{
							out.m_res_url = temp;//http://xxx.zip
						}
					}
				}

				//zip名
				if(isPath)
				{
					if(jsonObject.has("res_name"))
					{
						temp = jsonObject.getString("res_name");
						if(!TextUtils.isEmpty(temp))
						{
							out.m_res_name = temp;
						}
					}
				}

				//sound effect
				if(jsonObject.has("music"))
				{
					out.m_has_music = jsonObject.getBoolean("music");
				}

				//变形素材提示文本
				if(jsonObject.has("promptText"))
				{
					out.m_prompt_text = jsonObject.getString("promptText");
				}

                if(jsonObject.has("isAR4iOS"))
                {
                    out.m_isAR4iOS = jsonObject.getBoolean("isAR4iOS");
                }

                if (out.m_isAR4iOS) {//过滤ar素材
                    out = null;
                }
            }
			catch(Throwable t)
			{
				out = null;
				t.printStackTrace();
			}
		}

		return out;
	}

	@Override
	protected String GetSdcardPath(Context context)
	{
		return SDCARD_PATH;
	}

	@Override
	protected int GetNewJsonVer()
	{
		return NEW_JSON_VER;
	}

	@Override
	protected String GetCloudUrl(Context context)
	{
		return CLOUD_URL;
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return CLOUD_CACHE_PATH;
	}

	@Override
	protected void sync_last_GetCloudRes(Context context, DataFilter filter, boolean justSave, ArrayList<VideoStickerRes> result)
	{
		super.sync_last_GetCloudRes(context, filter, justSave, result);

		if(result != null && result.size() > 0)
		{
			//下载图标
			VideoStickerRes[] arr2 = new VideoStickerRes[result.size()];
			result.toArray(arr2);
			//同步下载，线程阻塞
			DownloadMgr.getInstance().SyncDownloadRes(arr2, true);
		}
	}

	@Override
	protected String GetOldIdFlag()
	{
		return OLD_ID_FLAG;
	}

	@Override
	public ArrayList<VideoStickerRes> sync_GetCloudCacheRes(Context context, DataFilter filter)
	{
		ArrayList<VideoStickerRes> arr = mCloudResArr;
		ArrayList<VideoStickerRes> arr2 = super.sync_GetCloudCacheRes(context, filter);

		synchronized(CLOUD_MEM_LOCK)
		{
			if(arr != arr2 && arr2 != null)
			{
				for(VideoStickerRes res : arr2)
				{
					DownloadMgr.FastDownloadRes(res, true);
				}
			}
		}

		return arr2;
	}

	@Override
	public VideoStickerRes GetItem(ArrayList<VideoStickerRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	/**
	 * @param arr
	 * @param id
	 * @param isDeleteGet true 删除匹配id的res
	 * @return
	 */
	public VideoStickerRes GetItem(ArrayList<VideoStickerRes> arr, int id, boolean isDeleteGet)
	{
		if(arr != null)
		{
			Iterator<VideoStickerRes> iterator = arr.iterator();
			while(iterator.hasNext())
			{
				VideoStickerRes next = iterator.next();
				if(next != null && next.m_id == id)
				{
					if(isDeleteGet)
					{
						iterator.remove();
					}
					return next;
				}
			}
		}
		return null;
	}

	@Override
	protected void RebuildNetResArr(ArrayList<VideoStickerRes> dst, ArrayList<VideoStickerRes> src)
	{
		if(dst != null && src != null)
		{
			VideoStickerRes srcTemp;
			VideoStickerRes dstTemp;
			Class cls = VideoStickerRes.class;
			Field[] fields = cls.getDeclaredFields();
			int index;
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				dstTemp = dst.get(i);
				index = HasItem(src, dstTemp.m_id);
				if(index >= 0)
				{
					srcTemp = src.get(index);
					dstTemp.m_type = srcTemp.m_type;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.m_res_path = srcTemp.m_res_path;
					dstTemp.m_res_name = srcTemp.m_res_name;

					for(Field field : fields)
					{
						try
						{
							if(!Modifier.isFinal(field.getModifiers()))
							{
								Object value = field.get(dstTemp);
								field.set(srcTemp, value);
							}
						}
						catch(Throwable e2)
						{
							e2.printStackTrace();
						}
					}
					dst.set(i, srcTemp);
				}
			}
		}
	}

	/**
	 * @param ids
	 * @param resArr
	 * @param isGifMode 是否只显示gif资源
	 * @return
	 */
	public static ArrayList<VideoStickerRes> BuildGroupRes(int[] ids, ArrayList<VideoStickerRes> resArr, boolean isGifMode)
	{
		ArrayList<VideoStickerRes> out = new ArrayList<>();
		if(ids != null && resArr != null && resArr.size() > 0)
		{
			label:
			for(int id : ids)
			{
				for(VideoStickerRes stickerRes : resArr)
				{
					if(stickerRes != null && stickerRes.m_id == id)
					{
						if(isGifMode && stickerRes.m_show_type_level == ShowType.GetType(ShowType.GIF))
						{
							out.add(stickerRes);
						}
						else if(!isGifMode && stickerRes.m_show_type_level == ShowType.GetType(ShowType.STICKER))
						{
							out.add(stickerRes);
						}
						else if(stickerRes.m_show_type_level == ShowType.GetType(ShowType.BOTH))
						{
							out.add(stickerRes);
						}
						continue label;
					}
				}
			}
		}
		return out;
	}

	public static ArrayList<VideoStickerRes> BuildGroupRes(int[] ids, ArrayList<VideoStickerRes> resArr)
	{
		return BuildGroupRes(ids, resArr, false);
	}

	public boolean IsNewRes(int id)
	{
		boolean out = false;

		if(ResourceUtils.HasId(new_flag_arr, id) >= 0)
		{
			out = true;
		}

		return out;
	}

	public void AddNewFlag(Context context, int id)
	{
		ResourceMgr.AddNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, id);
	}

	public void DeleteNewFlag(Context context, int id)
	{
		ResourceMgr.DeleteNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, id);
	}

	public void DeleteNewFlag(Context context, int[] ids)
	{
		if(ids != null && ids.length > 0)
		{
			ResourceUtils.DeleteIds(new_flag_arr, ids);
			ResourceMgr.UpdateNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG);
		}
	}

	/**
	 * 删除当前res的zip包路径
	 *
	 * @param context
	 * @param videoStickerRes
	 * @return
	 */
	public static boolean DeleteVideoStickerResZip(Context context, VideoStickerRes videoStickerRes)
	{
		if(videoStickerRes != null && !TextUtils.isEmpty(videoStickerRes.m_res_path) && !StickerHelper.isAssetFile(context, videoStickerRes.m_res_path))
		{
			try
			{
				// /storage/emulated/0/beautyCamera/appdata/resource/video_face/1000/8b406a2d97a7d2a5205e50a54b48641b2017051810.img
				boolean delete = false;
				File file = new File(videoStickerRes.m_res_path);
				if(file.exists())
				{
					delete = file.delete();
				}

				//删除该id下隐藏文件夹
				String associate = videoStickerRes.GetSaveParentPath() + File.separator + videoStickerRes.m_id + File.separator + StickerJsonParse.BASE_ZIP_FOLDER_NAME;
				delete = FileUtil.deleteSDFile(associate, true);
				return delete;
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 获取所有素材 （网络 + 内置 + 已下载sdcard）
	 * @param context
	 * @return
	 */
	public ArrayList<VideoStickerRes> GetResArr(Context context)
	{
		ArrayList<VideoStickerRes> out = new ArrayList<>();

		ArrayList<VideoStickerRes> downloadArr = sync_ar_GetCloudCacheRes(context, null);
		ArrayList<VideoStickerRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<VideoStickerRes> localArr = sync_GetLocalRes(context, null);

		if(downloadArr != null && downloadArr.size() > 0)
		{
			ArrayList<VideoStickerRes> temp_arr = new ArrayList<>();
			//已下载
			if(sdcardArr != null)
			{
				temp_arr.addAll(sdcardArr);
			}

			//内置
			if(localArr != null)
			{
				//内置和已下载sd 资源是否存在相同id？
				temp_arr.addAll(localArr);
			}

			//网络
			for(VideoStickerRes res : downloadArr)
			{
				if(res != null)
				{
					//过滤相同id，删除temp_arr相同id的对象
					VideoStickerRes res2 = GetItem(temp_arr, res.m_id, true);
					if(res2 != null)
					{
						//主要是赋值网络thumb图链接和res资源链接
						res2.m_res_url = res.m_res_url;
						res2.url_thumb = res.url_thumb;

						//sd + 内置
						out.add(res2);
					}
					else
					{
						//网络
						out.add(res);
					}
				}
			}

			//添加sd + 内置
			out.addAll(temp_arr);
		}
		else
		{
			if(sdcardArr != null)
			{
				out.addAll(sdcardArr);
			}

			out.addAll(localArr);
		}

		return out;
	}

	/**
	 * @param context
	 * @param isBusiness 是否只是加载商业素材（特殊情况特加判断）
	 * @return
	 */
	public ArrayList<VideoStickerRes> GetResArr(Context context, boolean isBusiness)
	{
		ArrayList<VideoStickerRes> out = GetResArr(context);
		if(out != null && !out.isEmpty())
		{
			Iterator<VideoStickerRes> iterator = out.iterator();
			while(iterator.hasNext())
			{
				VideoStickerRes next = iterator.next();
				if(next != null)
				{
					//id：38559 纪梵希201708商业
					//id：39165 阿玛尼 201710商业（正常通道素材不显示，商业通道显示）
					if(next.m_id == 38559 || next.m_id == 39165)
					{
						next.m_is_business = true;
						if(!isBusiness)
						{
							iterator.remove();
						}
					}
					else if(isBusiness && !next.m_is_business)
					{
						//删除非商业素材
						iterator.remove();
					}
				}
			}
			iterator = null;
		}
		return out;
	}

	/**
	 * 获取已经下载的贴纸素材
	 *
	 * @param isGifMode
	 * @param isIncludeLocalRes 是否包含内置素材
	 * @return
	 */
	public ArrayList<VideoStickerGroupRes> GetDownloadGroupResArr(Context context, boolean isGifMode, boolean isIncludeLocalRes, boolean isBusiness, int... groupIds)
	{
		ArrayList<VideoStickerGroupRes> group_out = new ArrayList<>();

		ArrayList<VideoStickerRes> res_out = new ArrayList<>();
		ArrayList<VideoStickerRes> sdcardArr = sync_GetSdcardRes(context, null);

		if(sdcardArr != null)
		{
			res_out.addAll(sdcardArr);
		}

		if(isIncludeLocalRes)
		{
			ArrayList<VideoStickerRes> localArr = GetLocalResArr(isGifMode, isBusiness);
			if(localArr != null)
			{
				if(res_out.isEmpty())
				{
					res_out.addAll(localArr);
				}
				else
				{
					for(VideoStickerRes local_res : localArr)
					{
						if(local_res != null)
						{
							//删除相同素材，内置优先
							Iterator<VideoStickerRes> iterator = res_out.iterator();
							while(iterator.hasNext())
							{
								VideoStickerRes res_sd = iterator.next();
								if(res_sd != null && res_sd.m_id == local_res.m_id)
								{
									iterator.remove();
									break;
								}
							}
							res_out.add(local_res);
						}
					}
				}
			}
		}

		ArrayList<VideoStickerGroupRes> downloadGroupArr = VideoStickerGroupResMgr2.getInstance().getCloudDownloadRes(context, false, groupIds);
		if(downloadGroupArr != null)
		{
			for(VideoStickerGroupRes groupRes : downloadGroupArr)
			{   //重新构造，防止对象复用
				VideoStickerGroupRes newGroupRes = new VideoStickerGroupRes();
				newGroupRes.m_id = groupRes.m_id;
				newGroupRes.m_name = groupRes.m_name;
				newGroupRes.m_tjId = groupRes.m_tjId;
				newGroupRes.m_thumb = groupRes.m_thumb;
				newGroupRes.m_stickerIDArr = groupRes.m_stickerIDArr;
				group_out.add(newGroupRes);
			}
		}
		for(VideoStickerGroupRes groupRes : group_out)
		{
			if(groupRes != null && groupRes.m_stickerIDArr != null && groupRes.m_stickerIDArr.length > 0)
			{
				ArrayList<VideoStickerRes> list = BuildGroupRes(groupRes.m_stickerIDArr, res_out, isGifMode);
				if(list != null && list.size() > 0)
				{
					groupRes.m_group = list;
				}
				else
				{
					groupRes.m_group = null;
				}
			}
		}

		return group_out;
	}

	/**
	 * 获取贴纸素材（包括网络未下载 + 内置 + 已下载）
	 *
	 * @param context
	 * @param isBusiness      是否商业模式加载
	 * @param isGifMode       是否gif素材加载
	 * @param isShowHideGroup 是否显示hide标签组，true：显示{@link VideoStickerGroupRes#m_isHide}为true的标签
	 * @param groupIds        指定标签组
	 * @return
	 */
	public ArrayList<VideoStickerGroupRes> GetGroupResArr(Context context, boolean isBusiness, boolean isGifMode, boolean isShowHideGroup, int... groupIds)
	{
		ArrayList<VideoStickerGroupRes> group_out = new ArrayList<>();
		ArrayList<VideoStickerRes> res_out = new ArrayList<>();

		//long s = System.currentTimeMillis();

		//标签
		ArrayList<VideoStickerGroupRes> group_arr = VideoStickerGroupResMgr2.getInstance().getCloudDownloadRes(context, isShowHideGroup, groupIds);
		if(group_arr != null)
		{
			group_out.addAll(group_arr);
		}

		if(group_out.size() > 0)
		{
			//素材
			ArrayList<VideoStickerRes> temp_res = GetResArr(context, isBusiness);
			if(temp_res != null)
			{
				res_out.addAll(temp_res);
			}

			if(res_out.size() > 0)
			{
				for(VideoStickerGroupRes groupRes : group_out)
				{
					if(groupRes != null && groupRes.m_stickerIDArr != null && groupRes.m_stickerIDArr.length > 0)
					{
						//组id可能不在res_out里
						ArrayList<VideoStickerRes> list = BuildGroupRes(groupRes.m_stickerIDArr, res_out, isGifMode);

						if(list != null && list.size() > 0)
						{
							groupRes.m_group = list;
						}
						else
						{
							groupRes.m_group = null;
						}
					}
				}
			}
		}

		//Log.d("bbb", "VideoStickerResMgr2 --> GetGroupResArr: " + (System.currentTimeMillis() - s));

		return group_out;
	}


	public ArrayList<VideoStickerGroupRes> GetGroupResArr(Context context, boolean isBusiness, boolean isGifMode)
	{
		ArrayList<VideoStickerGroupRes> group_out = new ArrayList<>();
		ArrayList<VideoStickerRes> res_out = new ArrayList<>();

		//long s = System.currentTimeMillis();

		//标签
		ArrayList<VideoStickerGroupRes> downloadGroupArr = VideoStickerGroupResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(downloadGroupArr != null)
		{
			group_out.addAll(downloadGroupArr);
		}


		if(group_out.size() > 0)
		{
			//素材
			ArrayList<VideoStickerRes> temp_res = GetResArr(context, isBusiness);
			if(temp_res != null)
			{
				res_out.addAll(temp_res);
			}


			if(res_out.size() > 0)
			{
				for(VideoStickerGroupRes groupRes : group_out)
				{
					if(groupRes != null && groupRes.m_stickerIDArr != null && groupRes.m_stickerIDArr.length > 0)
					{
						//组id可能不在res_out里
						ArrayList<VideoStickerRes> list = BuildGroupRes(groupRes.m_stickerIDArr, res_out, isGifMode);

						if(list != null && list.size() > 0)
						{
							groupRes.m_group = list;
						}
						else
						{
							groupRes.m_group = null;
						}
					}
				}
			}
		}

		//Log.d("mmm", "GetVideoStickerGroupResArr: " + (System.currentTimeMillis() - s));

		return group_out;
	}

	public void DeleteRes(Context context, int[] ids)
	{
		if(ids != null && ids.length > 0)
		{
			ArrayList<VideoStickerRes> sdcardArr = sync_GetSdcardRes(context, null);
			ArrayList<VideoStickerRes> list = ResourceUtils.DeleteItems(sdcardArr, ids);
			if(list != null && list.size() > 0)
			{
				for(VideoStickerRes videoStickerRes : list)
				{
					if(videoStickerRes != null)
					{
						if(videoStickerRes.m_type == BaseRes.TYPE_LOCAL_PATH)
						{
							videoStickerRes.m_type = BaseRes.TYPE_NETWORK_URL;

							// VideoStickerGroupResMgr --> DeleteVideoStickerRes 删除zip资源
							boolean delete = DeleteVideoStickerResZip(context, videoStickerRes);
							//Log.d("bbb", "VideoStickerGroupResMgr --> DeleteVideoStickerRes: delete zip " + delete);
						}
						videoStickerRes.mStickerRes = null;//清除引用
					}
				}
				DeleteNewFlag(context, ids);
				sync_SaveSdcardRes(context, sdcardArr);
			}
		}
	}

	@Override
	public void ReadNewFlagArr(Context context, SharedPreferences sp)
	{
		String temp = sp.getString(NEW_DOWNLOAD_FLAG, null);
		ResourceUtils.ParseNewFlagToArr(new_flag_arr, temp);
		ResourceUtils.RebuildNewFlagArr(sync_GetSdcardRes(context, null), new_flag_arr);
	}
}

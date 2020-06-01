package cn.poco.resource;

import android.content.Context;
import android.content.SharedPreferences;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.system.SysConfig;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/10/8.
 */

public class MosaicResMgr2 extends BaseResMgr<MosaicRes, ArrayList<MosaicRes>>
{
	public final static int NEW_JSON_VER = 2;
	public final static int NEW_ORDER_JSON_VER = 2;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().MOSAIC_PATH + "/mosaic.xxxx";

	protected final String ORDER_PATH = DownloadMgr.getInstance().MOSAIC_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().MOSAIC_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/mosaic/android.php?version=%E7%BE%8E%E4%BA%BA%E7%9B%B8%E6%9C%BA2.6.2";// + "&random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/mosaic/android.php?version=%E7%BE%8E%E4%BA%BA%E7%9B%B8%E6%9C%BA88.8.8" + "&random=" + Math.random();

	public final static String NEW_DOWNLOAD_FLAG = "mosaic"; //记录在Preferences
	public ArrayList<Integer> new_flag_arr = new ArrayList<Integer>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "mosaic_id";

	private static MosaicResMgr2 sInstance;

	private MosaicResMgr2()
	{
	}

	public synchronized static MosaicResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new MosaicResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<MosaicRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<MosaicRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<MosaicRes> arr, MosaicRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<MosaicRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<MosaicRes> out = new ArrayList<>();
		MosaicRes res;

		res = new MosaicRes();
		res.m_id = 4;
		res.m_name = "红蓝彩格";
		res.m_img = R.drawable.__mos__40442015102612291098397234;
		res.m_thumb = R.drawable.__mos__78812015102913390578615827;
		res.m_icon = R.drawable.__mos__55322015102315105417837252;
		res.m_paintType = MosaicRes.PAINT_TYPE_TILE;
		res.m_tjId = 1066830;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 5;
		res.m_name = "千鸟格纹";
		res.m_img = R.drawable.__mos__99572015102612292412201836;
		res.m_thumb = R.drawable.__mos__74952015102315170499313361;
		res.m_icon = R.drawable.__mos__1611201510231517224303011;
		res.m_paintType = MosaicRes.PAINT_TYPE_TILE;
		res.m_tjId = 1066831;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 6;
		res.m_name = "英伦复古";
		res.m_img = R.drawable.__mos__2413201510261229375840550;
		res.m_thumb = R.drawable.__mos__38382015102315181377938136;
		res.m_icon = R.drawable.__mos__38382015102315183333372651;
		res.m_paintType = MosaicRes.PAINT_TYPE_TILE;
		res.m_tjId = 1066829;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 17;
		res.m_name = "羽毛花印";
		res.m_img = R.drawable.__mos__5370201510261525584563196;
		res.m_thumb = R.drawable.__mos__53702015102615255430681212;
		res.m_icon = R.drawable.__mos__537020151026152602878245;
		res.m_paintType = MosaicRes.PAINT_TYPE_TILE;
		res.m_tjId = 1066819;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 7;
		res.m_name = "民族涂笔";
		res.m_img = R.drawable.__mos__78352015110217462898951523;
		res.m_thumb = R.drawable.__mos__31552015102315200440262361;
		res.m_icon = R.drawable.__mos__4433201510231521575912116;
		res.m_paintType = MosaicRes.PAINT_TYPE_FILL;
		res.horizontal_fill = MosaicRes.POS_CENTER;
		res.vertical_fill = MosaicRes.POS_CENTER;
		res.m_tjId = 1066827;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 8;
		res.m_name = "色彩几何";
		res.m_img = R.drawable.__mos__15332015102612332247220061;
		res.m_thumb = R.drawable.__mos__75742015102315350450570320;
		res.m_icon = R.drawable.__mos__75742015102315345462288058;
		res.m_paintType = MosaicRes.PAINT_TYPE_FILL;
		res.horizontal_fill = MosaicRes.POS_CENTER;
		res.vertical_fill = MosaicRes.POS_CENTER;
		res.m_tjId = 1066821;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 11;
		res.m_name = "彩虹涂鸦";
		res.m_img = R.drawable.__mos__56062015102614051323829365;
		res.m_thumb = R.drawable.__mos__67522015102315430866695361;
		res.m_icon = R.drawable.__mos__48482015102315423787658351;
		res.m_paintType = MosaicRes.PAINT_TYPE_FILL;
		res.horizontal_fill = MosaicRes.POS_CENTER;
		res.vertical_fill = MosaicRes.POS_CENTER;
		res.m_tjId = 1066823;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 16;
		res.m_name = "涂色主义";
		res.m_img = R.drawable.__mos__98712015102615244074949255;
		res.m_thumb = R.drawable.__mos__9871201510261524376739849;
		res.m_icon = R.drawable.__mos__98712015102615244413768849;
		res.m_paintType = MosaicRes.PAINT_TYPE_TILE;
		res.m_tjId = 1066822;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 15;
		res.m_name = "梦幻迷蒙";
		res.m_img = R.drawable.__mos__5560201510261524012644012;
		res.m_thumb = R.drawable.__mos__55602015102615235767391763;
		res.m_icon = R.drawable.__mos__26702015102615240994036445;
		res.m_paintType = MosaicRes.PAINT_TYPE_FILL;
		res.horizontal_fill = MosaicRes.POS_CENTER;
		res.vertical_fill = MosaicRes.POS_CENTER;
		res.m_tjId = 1066824;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 14;
		res.m_name = "童话世界";
		res.m_img = R.drawable.__mos__27852015102615224673598523;
		res.m_thumb = R.drawable.__mos__27852015102615223862264280;
		res.m_icon = R.drawable.__mos__27852015102615232992587160;
		res.m_paintType = MosaicRes.PAINT_TYPE_FILL;
		res.horizontal_fill = MosaicRes.POS_END;
		res.vertical_fill = MosaicRes.POS_END;
		res.m_tjId = 1066825;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 13;
		res.m_name = "趣味涂鸦";
		res.m_img = R.drawable.__mos__16752015102615221310460584;
		res.m_thumb = R.drawable.__mos__16752015102615220890979713;
		res.m_icon = R.drawable.__mos__16752015102615221683582691;
		res.m_paintType = MosaicRes.PAINT_TYPE_TILE;
		res.m_tjId = 1066826;
		out.add(res);

		res = new MosaicRes();
		res.m_id = 12;
		res.m_name = "蓝调波普";
		res.m_img = R.drawable.__mos__31342015102615212871376427;
		res.m_thumb = R.drawable.__mos__31342015102615125468145654;
		res.m_icon = R.drawable.__mos__31342015102615213519194473;
		res.m_paintType = MosaicRes.PAINT_TYPE_FILL;
		res.horizontal_fill = MosaicRes.POS_CENTER;
		res.vertical_fill = MosaicRes.POS_CENTER;
		res.m_tjId = 1066820;
		out.add(res);

		return out;
	}

	@Override
	public boolean CheckIntact(MosaicRes res)
	{
		boolean out = false;

		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb) && ResourceUtils.HasIntact(res.m_img))
			{
				out = true;
			}
		}

		return out;
	}

	@Override
	protected ArrayList<MosaicRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
	{
		ArrayList<MosaicRes> out = super.sync_DecodeCloudRes(context, filter, data);

		try
		{
			//特殊处理
			LockResMgr2.getInstance().decodeMosaicLockArr(new String((byte[])data));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<MosaicRes> arr)
	{
		FileOutputStream fos = null;

		try
		{
			JSONObject json = new JSONObject();
			{
				json.put("ver", NEW_JSON_VER);

				JSONArray jsonArr = new JSONArray();
				{
					if(arr != null)
					{
						MosaicRes res;
						JSONObject jsonObj;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.get(i);
							if(res != null)
							{
								jsonObj = new JSONObject();
								jsonObj.put("id", Integer.toString(res.m_id));
								if(res.m_thumb instanceof String)
								{
									jsonObj.put("thumb", res.m_thumb);
								}
								else
								{
									jsonObj.put("thumb", "");
								}
								if(res.m_img instanceof String)
								{
									jsonObj.put("image", res.m_img);
								}
								else
								{
									jsonObj.put("image", "");
								}
								if(res.m_name != null)
								{
									jsonObj.put("name", res.m_name);
								}
								else
								{
									jsonObj.put("name", "");
								}
								if(res.m_paintType == MosaicRes.PAINT_TYPE_FILL)
								{
									jsonObj.put("mosaicType", "mosaicTypeLines");
								}
								else
								{
									jsonObj.put("mosaicType", "mosaicTypeTyle");
								}
								switch(res.horizontal_fill)
								{
									case MosaicRes.POS_START:
										jsonObj.put("landscapeCut", "4");
										break;
									case MosaicRes.POS_END:
										jsonObj.put("landscapeCut", "5");
										break;
									default:
										jsonObj.put("landscapeCut", "6");
										break;
								}
								switch(res.vertical_fill)
								{
									case MosaicRes.POS_START:
										jsonObj.put("verticalCut", "1");
										break;
									case MosaicRes.POS_END:
										jsonObj.put("verticalCut", "2");
										break;
									default:
										jsonObj.put("verticalCut", "3");
										break;
								}
								if(res.m_icon instanceof String)
								{
									jsonObj.put("selectedCircleImg", res.m_icon);
								}
								else
								{
									jsonObj.put("selectedCircleImg", "");
								}
								jsonObj.put("pushID", Integer.toString(res.m_tjId));
								jsonArr.put(jsonObj);
							}
						}
					}
				}
				json.put("data", jsonArr);
			}

			fos = new FileOutputStream(SDCARD_PATH);
			//System.out.println(json.toString());
			fos.write(json.toString().getBytes());
			fos.flush();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(fos != null)
			{
				try
				{
					fos.close();
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.MOSAIC_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected MosaicRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		MosaicRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new MosaicRes();
				if(isPath)
				{
					out.m_type = BaseRes.TYPE_LOCAL_PATH;
				}
				else
				{
					out.m_type = BaseRes.TYPE_NETWORK_URL;
				}
				String temp = jsonObj.getString("id");
				if(temp != null && temp.length() > 0)
				{
					out.m_id = Integer.parseInt(temp);
				}
				else
				{
					out.m_id = (int)(Math.random() * 10000000);
				}
				temp = jsonObj.getString("thumb");
				if(temp != null && temp.length() > 0)
				{
					if(isPath)
					{
						out.m_thumb = temp;
					}
					else
					{
						out.url_thumb = temp;
					}
				}
				temp = jsonObj.getString("image");
				if(temp != null && temp.length() > 0)
				{
					if(isPath)
					{

						out.m_img = temp;
					}
					else
					{
						out.url_img = temp;
					}
				}
				out.m_name = jsonObj.getString("name");
				temp = jsonObj.getString("mosaicType");
				if(temp != null)
				{
					if(temp.equals("mosaicTypeLines"))
					{
						out.m_paintType = MosaicRes.PAINT_TYPE_FILL;
					}
					else
					{
						out.m_paintType = MosaicRes.PAINT_TYPE_TILE;
					}
				}
				temp = jsonObj.getString("landscapeCut");
				if(temp != null && temp.length() > 0)
				{
					switch(Integer.parseInt(temp))
					{
						case 4:
							out.horizontal_fill = MosaicRes.POS_START;
							break;
						case 5:
							out.horizontal_fill = MosaicRes.POS_END;
							break;
						default:
							out.horizontal_fill = MosaicRes.POS_CENTER;
							break;
					}
				}
				temp = jsonObj.getString("verticalCut");
				if(temp != null && temp.length() > 0)
				{
					switch(Integer.parseInt(temp))
					{
						case 1:
							out.vertical_fill = MosaicRes.POS_START;
							break;
						case 2:
							out.vertical_fill = MosaicRes.POS_END;
							break;
						default:
							out.vertical_fill = MosaicRes.POS_CENTER;
							break;
					}
				}
				temp = jsonObj.getString("selectedCircleImg");
				if(temp != null && temp.length() > 0)
				{
					if(isPath)
					{
						out.m_icon = temp;
					}
					else
					{
						out.url_icon = temp;
					}
				}
				if(jsonObj.has("pushID"))
				{
					temp = jsonObj.getString("pushID");
					if(temp != null && temp.length() > 0)
					{
						out.m_tjId = Integer.parseInt(temp);
					}
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				out = null;
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
	protected void InitOrderArr(ArrayList<Integer> dstObj)
	{
		super.InitOrderArr(dstObj);

		ReadOrderArr();
		Context context = MyFramework2App.getInstance().getApplicationContext();
		if(ResourceUtils.RebuildOrder(sync_GetLocalRes(context, null), sync_GetSdcardRes(context, null), dstObj))
		{
			SaveOrderArr();
		}
	}

	@Override
	protected String GetCloudUrl(Context context)
	{
		if(SysConfig.IsDebug())
		{
			return CLOUD_TEST_URL;
		}
		else
		{
			return CLOUD_URL;
		}
	}

	@Override
	protected String GetCloudCachePath(Context context)
	{
		return CLOUD_CACHE_PATH;
	}

	@Override
	protected void sync_ui_CloudResChange(ArrayList<MosaicRes> oldArr, ArrayList<MosaicRes> newArr)
	{
		super.sync_ui_CloudResChange(oldArr, newArr);

		if(newArr != null && newArr.size() > 0)
		{
			//下载图标
			MosaicRes[] arr2 = new MosaicRes[newArr.size()];
			newArr.toArray(arr2);
			DownloadMgr.getInstance().SyncDownloadRes(arr2, true);
		}
	}

	@Override
	protected String GetOldIdFlag()
	{
		return OLD_ID_FLAG;
	}

	@Override
	public MosaicRes GetItem(ArrayList<MosaicRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	public void ReadOrderArr()
	{
		ReadOrderArr(MyFramework2App.getInstance().getApplication(), ORDER_PATH);
	}

	public void SaveOrderArr()
	{
		SaveOrderArr(MyFramework2App.getInstance().getApplication(), NEW_ORDER_JSON_VER, ORDER_PATH);
	}

	@Override
	protected void RebuildNetResArr(ArrayList<MosaicRes> dst, ArrayList<MosaicRes> src)
	{
		if(dst != null && src != null)
		{
			MosaicRes srcTemp;
			MosaicRes dstTemp;
			Class cls = MosaicRes.class;
			Field[] fields = cls.getDeclaredFields();
			int index;
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				dstTemp = dst.get(i);
				index = ResourceUtils.HasItem(src, dstTemp.m_id);
				if(index >= 0)
				{
					srcTemp = src.get(index);
					dstTemp.m_type = srcTemp.m_type;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.m_img = srcTemp.m_img;
					dstTemp.m_icon = srcTemp.m_icon;

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

	public ArrayList<MosaicRes> GetResArr()
	{
		Context context = MyFramework2App.getInstance().getApplicationContext();
		return ResourceUtils.BuildShowArr(sync_GetLocalRes(context, null), sync_GetSdcardRes(context, null), GetOrderArr());
	}

	public ArrayList<GroupRes> GetDownloadedGroupResArr()
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<MosaicRes> orgArr = new ArrayList<>();
		ArrayList<MosaicRes> tempArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplicationContext(), null);
		if(tempArr != null)
		{
			orgArr.addAll(tempArr);
		}
		ArrayList<ThemeRes> themeArr = ThemeResMgr2.getInstance().GetAllResArr();
		int len = themeArr.size();
		ThemeRes temp;
		ArrayList<MosaicRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_mosaicIDArr != null && temp.m_mosaicIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_mosaicIDArr);
				if(subArr.size() == temp.m_mosaicIDArr.length)
				{
					groupRes = new GroupRes();
					groupRes.m_themeRes = temp;
					groupRes.m_ress = new ArrayList<>();
					groupRes.m_ress.addAll(subArr);
					out.add(groupRes);
				}
			}
		}
		//其他
		if(orgArr.size() > 0)
		{
			temp = new ThemeRes();
			temp.m_name = "其他";
			temp.m_mosaicIDArr = new int[orgArr.size()];
			len = orgArr.size();
			for(int i = 0; i < len; i++)
			{
				temp.m_mosaicIDArr[i] = orgArr.get(i).m_id;
			}

			groupRes = new GroupRes();
			groupRes.m_themeRes = temp;
			groupRes.m_ress = new ArrayList<>();
			groupRes.m_ress.addAll(orgArr);
			out.add(groupRes);
		}

		return out;
	}

	public ArrayList<GroupRes> GetNoDownloadedGroupResArr(Context context)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<ThemeRes> downloadArr = ThemeResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(downloadArr != null)
		{
			ThemeRes temp;
			ArrayList<MosaicRes> resArr;
			GroupRes groupRes;
			boolean flag;
			int len = downloadArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if(temp.m_mosaicIDArr != null && temp.m_mosaicIDArr.length > 0)
				{
					flag = false;
					resArr = GetResArr(temp.m_mosaicIDArr);
					if(resArr.size() != temp.m_mosaicIDArr.length)
					{
						flag = true;
					}
					else
					{
						int len2 = resArr.size();
						for(int j = 0; j < len2; j++)
						{
							if(resArr.get(j).m_type == BaseRes.TYPE_NETWORK_URL)
							{
								flag = true;
								break;
							}
						}
					}
					if(flag)
					{
						groupRes = new GroupRes();
						groupRes.m_themeRes = temp;
						groupRes.m_ress = new ArrayList<>();
						groupRes.m_ress.addAll(resArr);
						out.add(groupRes);
					}
				}
			}
		}

		return out;
	}

	public int GetNoDownloadedCount(Context context)
	{
		return GetNoDownloadedGroupResArr(context).size();
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

	public ArrayList<MosaicRes> DeleteGroupRes(Context context, GroupRes res)
	{
		ArrayList<MosaicRes> out = new ArrayList<>();

		int[] ids = res.m_themeRes.m_mosaicIDArr;
		ArrayList<MosaicRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<MosaicRes> arr = ResourceUtils.DeleteItems(sdcardArr, ids);
		if(arr != null && arr.size() > 0)
		{
			MosaicRes temp;
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				temp = arr.get(i);
				if(temp.m_type == BaseRes.TYPE_LOCAL_PATH)
				{
					temp.m_type = BaseRes.TYPE_NETWORK_URL;
				}
			}
			ResourceUtils.DeleteIds(GetOrderArr(), ids);
			DeleteNewFlag(context, ids);
			sync_SaveSdcardRes(context, sdcardArr);
			SaveOrderArr();
		}

		ThemeResMgr2.getInstance().ClearEmptyRes(context, res.m_themeRes);

		return out;
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

	public void AddNewFlag(Context context, int[] ids)
	{
		if(ids != null)
		{
			if(ResourceUtils.AddIds(new_flag_arr, ids))
			{
				ResourceMgr.UpdateNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG);
			}
		}
	}

	public void AddId(int id)
	{
		ResourceUtils.DeleteId(GetOrderArr(), id);
		GetOrderArr().add(0, id);
		SaveOrderArr();
	}

	public void AddId(int[] ids)
	{
		if(ResourceUtils.AddIds(GetOrderArr(), ids))
		{
			SaveOrderArr();
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

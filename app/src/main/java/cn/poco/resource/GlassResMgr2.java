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
 * Created by Raining on 2017/10/7.
 */

public class GlassResMgr2 extends BaseResMgr<GlassRes, ArrayList<GlassRes>>
{
	public final static int NEW_JSON_VER = 2;
	public final static int NEW_ORDER_JSON_VER = 2;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().GLASS_PATH + "/glass.xxxx";

	protected final String ORDER_PATH = DownloadMgr.getInstance().GLASS_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().GLASS_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/glass/android.php?version=%E7%BE%8E%E4%BA%BA%E7%9B%B8%E6%9C%BA2.6.2";// + "&random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/glass/android.php?version=%E7%BE%8E%E4%BA%BA%E7%9B%B8%E6%9C%BA88.8.8" + "&random=" + Math.random();

	public final static String NEW_DOWNLOAD_FLAG = "glass"; //记录在Preferences
	public ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "glass_id";

	private static GlassResMgr2 sInstance;

	private GlassResMgr2()
	{
	}

	public synchronized static GlassResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new GlassResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<GlassRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<GlassRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<GlassRes> arr, GlassRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<GlassRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<GlassRes> out = new ArrayList<>();
		GlassRes res;

		res = new GlassRes();
		res.m_id = 13;
		res.m_name = "圆角空间";
		res.m_mask = R.drawable.__gla__52822015102317275452054786;
		res.m_thumb = R.drawable.__gla__52822015102317274376117846;
		res.m_icon = R.drawable.__gla__52822015102317284347771491;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x4dffffff;
		res.m_scale = 80;
		res.m_tjId = 1066799;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 24;
		res.m_name = "Choker";
		res.m_mask = R.drawable.__gla__71622015102614380690373527;
		res.m_img = R.drawable.__gla__71622015102614381046822766;
		res.m_thumb = R.drawable.__gla__7162201510261437212328116;
		res.m_icon = R.drawable.__gla__71622015102614394226247719;
		res.horizontal_pos = GlassRes.POS_START;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = 105;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x80ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066832;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 7;
		res.m_name = "Free";
		res.m_mask = R.drawable.__gla__29812015102217424738611088;
		res.m_thumb = R.drawable.__gla__29812015102217395098771949;
		res.m_icon = R.drawable.__gla__29812015102217395816867982;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = 105;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_START;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x99ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066833;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 14;
		res.m_name = "圆心绘影";
		res.m_mask = R.drawable.__gla__42362015102317294615476353;
		res.m_thumb = R.drawable.__gla__42362015102317293872551920;
		res.m_icon = R.drawable.__gla__42362015102317292997317845;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x4dffffff;
		res.m_scale = 80;
		res.m_tjId = 1066800;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 8;
		res.m_name = "Raining";
		res.m_mask = R.drawable.__gla__71592015102313570744888492;
		res.m_thumb = R.drawable.__gla__71592015102313564526524113;
		res.m_icon = R.drawable.__gla__71592015102313565131136771;
		res.horizontal_pos = GlassRes.POS_END;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = -58;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x80ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066834;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 18;
		res.m_name = "心情烙印";
		res.m_mask = R.drawable.__gla__4674201510231750501701397;
		res.m_thumb = R.drawable.__gla__46742015102317504384010652;
		res.m_icon = R.drawable.__gla__46742015102317511811958111;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x4dffffff;
		res.m_scale = 88;
		res.m_tjId = 1066801;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 15;
		res.m_name = "简约风尚";
		res.m_mask = R.drawable.__gla__51072015102317393283430654;
		res.m_thumb = R.drawable.__gla__17692015102317364755995442;
		res.m_icon = R.drawable.__gla__17692015102317431597129047;
		res.horizontal_pos = GlassRes.POS_START;
		res.horizontal_value = 0;
		res.h_fill_parent = 75;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = 100;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = true;
		res.m_color = 0x4dffffff;
		res.m_scale = 100;
		res.m_tjId = 1066802;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 23;
		res.m_name = "Deer";
		res.m_mask = R.drawable.__gla__70872017010320350021761665;
		res.m_thumb = R.drawable.__gla__70872017010320345440483052;
		res.m_icon = R.drawable.__gla__24202015102614364462960982;
		res.horizontal_pos = GlassRes.POS_START;
		res.horizontal_value = 7;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_START;
		res.vertical_value = 16;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x99ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066787;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 12;
		res.m_name = "Coffee Time";
		res.m_mask = R.drawable.__gla__31722015102315040484815657;
		res.m_img = R.drawable.__gla__14842015102315035773750011;
		res.m_thumb = R.drawable.__gla__14842015102315033222713815;
		res.m_icon = R.drawable.__gla__14842015102315042888495466;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x80ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066788;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 16;
		res.m_name = "梦之花卷";
		res.m_mask = R.drawable.__gla__57182015102317444135626476;
		res.m_thumb = R.drawable.__gla__57182015102317443314665443;
		res.m_icon = R.drawable.__gla__57182015102317442442257122;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = 100;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = 80;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = true;
		res.m_color = 0x4dffffff;
		res.m_scale = 100;
		res.m_tjId = 1066803;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 6;
		res.m_name = "Sunny";
		res.m_mask = R.drawable.__gla__54912015102217102977251240;
		res.m_img = R.drawable.__gla__42182015102217103465643421;
		res.m_thumb = R.drawable.__gla__54912015102217101553480038;
		res.m_icon = R.drawable.__gla__42182015102217091975411532;
		res.horizontal_pos = GlassRes.POS_END;
		res.horizontal_value = 6;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_START;
		res.vertical_value = 17;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x33ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066789;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 28;
		res.m_name = "六角幻影";
		res.m_mask = R.drawable.__gla__81092015102614550572398644;
		res.m_thumb = R.drawable.__gla__81092015102614551291517790;
		res.m_icon = R.drawable.__gla__36702015102614580160915551;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x4dffffff;
		res.m_scale = 93;
		res.m_tjId = 1066804;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 5;
		res.m_name = "Dreaming";
		res.m_mask = R.drawable.__gla__20482015102217064094750179;
		res.m_thumb = R.drawable.__gla__39262015102217061648471869;
		res.m_icon = R.drawable.__gla__3926201510221707429866398;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x80ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066790;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 27;
		res.m_name = "水滴奇幻";
		res.m_mask = R.drawable.__gla__28252015102614530720126418;
		res.m_thumb = R.drawable.__gla__28252015102614525917615730;
		res.m_icon = R.drawable.__gla__28252015102614525255015129;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x4dffffff;
		res.m_scale = 90;
		res.m_tjId = 1066805;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 4;
		res.m_name = "Moment";
		res.m_mask = R.drawable.__gla__78592017010320180784132817;
		res.m_thumb = R.drawable.__gla__7859201701032017565063422;
		res.m_icon = R.drawable.__gla__13602015102216505528585819;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_START;
		res.vertical_value = 5;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x80ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066791;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 26;
		res.m_name = "炫色水钻";
		res.m_mask = R.drawable.__gla__35902015102614513736472785;
		res.m_thumb = R.drawable.__gla__35902015102614512941144998;
		res.m_icon = R.drawable.__gla__35902015102614522418959726;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x4dffffff;
		res.m_scale = 88;
		res.m_tjId = 1066806;
		res.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
		out.add(res);

		res = new GlassRes();
		res.m_id = 22;
		res.m_name = "Hey!U";
		res.m_mask = R.drawable.__gla__16302015102614261394417825;
		res.m_thumb = R.drawable.__gla__16302015102614260797209811;
		res.m_icon = R.drawable.__gla__48562015102614265930598769;
		res.horizontal_pos = GlassRes.POS_START;
		res.horizontal_value = 4;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_START;
		res.vertical_value = 6;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x73ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066792;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 21;
		res.m_name = "Hopeful";
		res.m_mask = R.drawable.__gla__40592015102614232090261625;
		res.m_thumb = R.drawable.__gla__40592015102614231826049450;
		res.m_icon = R.drawable.__gla__40592015102614245096218348;
		res.horizontal_pos = GlassRes.POS_START;
		res.horizontal_value = 5;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0xccffffff;
		res.m_scale = 80;
		res.m_tjId = 1066793;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		res = new GlassRes();
		res.m_id = 19;
		res.m_name = "Joyous";
		res.m_mask = R.drawable.__gla__412320151026142129891590;
		res.m_thumb = R.drawable.__gla__28362015102614211032173962;
		res.m_icon = R.drawable.__gla__73292015102614220383831017;
		res.horizontal_pos = GlassRes.POS_CENTER;
		res.horizontal_value = 0;
		res.h_fill_parent = -1;
		res.self_offset_x = 0;
		res.vertical_pos = GlassRes.POS_CENTER;
		res.vertical_value = 0;
		res.v_fill_parent = -1;
		res.self_offset_y = 0;
		res.m_canFreedomZoom = false;
		res.m_color = 0x66ffffff;
		res.m_scale = 80;
		res.m_tjId = 1066794;
		res.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
		out.add(res);

		return out;
	}

	@Override
	public boolean CheckIntact(GlassRes res)
	{
		boolean out = false;

		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb) && ResourceUtils.HasIntact(res.m_mask))
			{
				out = true;
			}
		}

		return out;
	}

	@Override
	protected ArrayList<GlassRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
	{
		ArrayList<GlassRes> out = super.sync_DecodeCloudRes(context, filter, data);

		try
		{
			//特殊处理
			LockResMgr2.getInstance().decodeGlassLockArr(new String((byte[])data));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<GlassRes> arr)
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
						GlassRes res;
						JSONObject jsonObj;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.get(i);
							if(res != null)
							{
								jsonObj = new JSONObject();
								jsonObj.put("id", Integer.toString(res.m_id));
								if(res.m_glassType == GlassRes.GLASS_TYPE_PENDANT)
								{
									jsonObj.put("frostedType", "typeInner");
								}
								else
								{
									jsonObj.put("frostedType", "typeShape");
								}
								if(res.m_mask instanceof String)
								{
									jsonObj.put("mask", res.m_mask);
								}
								else
								{
									jsonObj.put("mask", "");
								}
								if(res.m_img instanceof String)
								{
									jsonObj.put("shape", res.m_img);
								}
								else
								{
									jsonObj.put("shape", "");
								}
								if(res.m_name != null)
								{
									jsonObj.put("title", res.m_name);
								}
								else
								{
									jsonObj.put("title", "");
								}
								if(res.m_thumb instanceof String)
								{
									jsonObj.put("thumb", res.m_thumb);
								}
								else
								{
									jsonObj.put("thumb", "");
								}
								String temp;
								if(res.horizontal_pos == GlassRes.POS_START && res.vertical_pos == GlassRes.POS_START)
								{
									temp = "1";
								}
								else if(res.horizontal_pos == GlassRes.POS_CENTER && res.vertical_pos == GlassRes.POS_START)
								{
									temp = "2";
								}
								else if(res.horizontal_pos == GlassRes.POS_END && res.vertical_pos == GlassRes.POS_START)
								{
									temp = "3";
								}
								else if(res.horizontal_pos == GlassRes.POS_START && res.vertical_pos == GlassRes.POS_CENTER)
								{
									temp = "4";
								}
								else if(res.horizontal_pos == GlassRes.POS_CENTER && res.vertical_pos == GlassRes.POS_CENTER)
								{
									temp = "5";
								}
								else if(res.horizontal_pos == GlassRes.POS_END && res.vertical_pos == GlassRes.POS_CENTER)
								{
									temp = "6";
								}
								else if(res.horizontal_pos == GlassRes.POS_START && res.vertical_pos == GlassRes.POS_END)
								{
									temp = "7";
								}
								else if(res.horizontal_pos == GlassRes.POS_CENTER && res.vertical_pos == GlassRes.POS_END)
								{
									temp = "8";
								}
								else
								{
									temp = "9";
								}
								int x = res.horizontal_value;
								if(res.self_offset_x != 0)
								{
									x = res.self_offset_x;
								}
								int y = res.vertical_value;
								if(res.self_offset_y != 0)
								{
									y = res.self_offset_y;
								}
								temp += "+" + x + "+" + y;
								if(res.h_fill_parent > 0 || res.v_fill_parent > 0)
								{
									temp += "+" + res.h_fill_parent + "+" + res.v_fill_parent;
								}
								jsonObj.put("location", temp);
								if(res.m_canFreedomZoom)
								{
									jsonObj.put("canChange", "1");
								}
								else
								{
									jsonObj.put("canChange", "0");
								}
								jsonObj.put("color", Integer.toHexString(res.m_color & 0x00FFFFFF));
								jsonObj.put("color_lucency", Integer.toString((int)(((res.m_color >> 24) & 0xFF) / 255f * 100 + 0.5f)));
								jsonObj.put("scale", res.m_scale);
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
		return EventID.GLASS_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected GlassRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		GlassRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new GlassRes();
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
				temp = jsonObj.getString("frostedType");
				if(temp != null)
				{
					if(temp.equals("typeInner"))
					{
						out.m_glassType = GlassRes.GLASS_TYPE_PENDANT;
					}
					else
					{
						out.m_glassType = GlassRes.GLASS_TYPE_SHAPE;
					}
				}
				temp = jsonObj.getString("mask");
				if(temp != null && temp.length() > 0)
				{
					if(isPath)
					{
						out.m_mask = temp;
					}
					else
					{
						out.url_mask = temp;
					}
				}
				temp = jsonObj.getString("shape");
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
				out.m_name = jsonObj.getString("title");
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
				temp = jsonObj.getString("canChange");
				if(temp != null && temp.length() > 0)
				{
					if(temp.equals("1"))
					{
						out.m_canFreedomZoom = true;
					}
				}
				temp = jsonObj.getString("location");
				if(temp != null && temp.length() > 0)
				{
					String[] ps = temp.split("\\+");
					switch(Integer.parseInt(ps[0]))
					{
						case 1:
							out.horizontal_pos = GlassRes.POS_START;
							out.vertical_pos = GlassRes.POS_START;
							break;
						case 2:
							out.horizontal_pos = GlassRes.POS_CENTER;
							out.vertical_pos = GlassRes.POS_START;
							break;
						case 3:
							out.horizontal_pos = GlassRes.POS_END;
							out.vertical_pos = GlassRes.POS_START;
							break;
						case 4:
							out.horizontal_pos = GlassRes.POS_START;
							out.vertical_pos = GlassRes.POS_CENTER;
							break;
						case 5:
							out.horizontal_pos = GlassRes.POS_CENTER;
							out.vertical_pos = GlassRes.POS_CENTER;
							break;
						case 6:
							out.horizontal_pos = GlassRes.POS_END;
							out.vertical_pos = GlassRes.POS_CENTER;
							break;
						case 7:
							out.horizontal_pos = GlassRes.POS_START;
							out.vertical_pos = GlassRes.POS_END;
							break;
						case 8:
							out.horizontal_pos = GlassRes.POS_CENTER;
							out.vertical_pos = GlassRes.POS_END;
							break;
						case 9:
						default:
							out.horizontal_pos = GlassRes.POS_END;
							out.vertical_pos = GlassRes.POS_END;
							break;
					}
					//特殊处理<0相对素材,>0相对父图片
					out.horizontal_value = Integer.parseInt(ps[1]);
					if(out.horizontal_value < 0)
					{
						out.self_offset_x = out.horizontal_value;
						out.horizontal_value = 0;
					}
					out.vertical_value = Integer.parseInt(ps[2]);
					if(out.vertical_value < 0)
					{
						out.self_offset_y = out.vertical_value;
						out.vertical_value = 0;
					}
					if(ps.length >= 5)
					{
						out.h_fill_parent = Integer.parseInt(ps[3]);
						if(out.h_fill_parent == 0)
						{
							out.h_fill_parent = -1;
						}
						out.v_fill_parent = Integer.parseInt(ps[4]);
						if(out.v_fill_parent == 0)
						{
							out.v_fill_parent = -1;
						}
					}
				}
				temp = jsonObj.getString("color");
				if(temp != null && temp.length() > 0)
				{
					out.m_color = ((int)Long.parseLong(temp, 16)) & 0x00FFFFFF;
					temp = jsonObj.getString("color_lucency");
					if(temp != null && temp.length() > 0)
					{
						int alpha = (((int)(Integer.parseInt(temp) / 100f * 255 + 0.5f)) & 0xFF) << 24;
						out.m_color |= alpha;
					}
					else
					{
						out.m_color |= 0xFF000000;
					}
				}
				temp = jsonObj.getString("scale");
				if(temp != null && temp.length() > 0)
				{
					out.m_scale = Integer.parseInt(temp);
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
	protected void sync_ui_CloudResChange(ArrayList<GlassRes> oldArr, ArrayList<GlassRes> newArr)
	{
		super.sync_ui_CloudResChange(oldArr, newArr);

		if(newArr != null && newArr.size() > 0)
		{
			GlassRes[] arr2 = new GlassRes[newArr.size()];
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
	public GlassRes GetItem(ArrayList<GlassRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	@Override
	protected void RebuildNetResArr(ArrayList<GlassRes> dst, ArrayList<GlassRes> src)
	{
		if(dst != null && src != null)
		{
			GlassRes srcTemp;
			GlassRes dstTemp;
			Class cls = GlassRes.class;
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
					dstTemp.m_mask = srcTemp.m_mask;
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

	public void ReadOrderArr()
	{
		ReadOrderArr(MyFramework2App.getInstance().getApplication(), ORDER_PATH);
	}

	public void SaveOrderArr()
	{
		SaveOrderArr(MyFramework2App.getInstance().getApplication(), NEW_ORDER_JSON_VER, ORDER_PATH);
	}

	public ArrayList<GroupRes> GetDownloadedGroupResArr()
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<GlassRes> orgArr = new ArrayList<>();
		ArrayList<GlassRes> tempArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplicationContext(), null);
		if(tempArr != null)
		{
			orgArr.addAll(tempArr);
		}
		ArrayList<ThemeRes> themeArr = ThemeResMgr2.getInstance().GetAllResArr();
		int len = themeArr.size();
		ThemeRes temp;
		ArrayList<GlassRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_glassIDArr != null && temp.m_glassIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_glassIDArr);
				if(subArr.size() == temp.m_glassIDArr.length)
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
			temp.m_glassIDArr = new int[orgArr.size()];
			len = orgArr.size();
			for(int i = 0; i < len; i++)
			{
				temp.m_glassIDArr[i] = orgArr.get(i).m_id;
			}

			groupRes = new GroupRes();
			groupRes.m_themeRes = temp;
			groupRes.m_ress = new ArrayList<>();
			groupRes.m_ress.addAll(orgArr);
			out.add(groupRes);
		}

		return out;
	}

	public ArrayList<GlassRes> GetResArr()
	{
		Context context = MyFramework2App.getInstance().getApplicationContext();
		return ResourceUtils.BuildShowArr(sync_GetLocalRes(context, null), sync_GetSdcardRes(context, null), GetOrderArr());
	}

	public ArrayList<GroupRes> GetNoDownloadedGroupResArr(Context context)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<ThemeRes> downloadArr = ThemeResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(downloadArr != null)
		{
			ThemeRes temp;
			ArrayList<GlassRes> resArr;
			GroupRes groupRes;
			boolean flag;
			int len = downloadArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if(temp.m_glassIDArr != null && temp.m_glassIDArr.length > 0)
				{
					flag = false;
					resArr = GetResArr(temp.m_glassIDArr);
                    int len2 = resArr.size();
                    if(len2 > 0 && len2 != temp.m_glassIDArr.length)
					{
						flag = true;
					}
					else
					{
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

	public ArrayList<GlassRes> DeleteGroupRes(Context context, GroupRes res)
	{
		ArrayList<GlassRes> out = new ArrayList<>();

		int[] ids = res.m_themeRes.m_glassIDArr;
		ArrayList<GlassRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<GlassRes> arr = ResourceUtils.DeleteItems(sdcardArr, ids);
		if(arr != null && arr.size() > 0)
		{
			GlassRes temp;
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

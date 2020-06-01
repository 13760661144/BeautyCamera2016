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

public class FrameExResMgr2 extends BaseResMgr<FrameExRes, ArrayList<FrameExRes>>
{
	public final static int NEW_JSON_VER = 1;
	public final static int NEW_ORDER_JSON_VER = 1;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().FRAME2_PATH + "/frame2.xxxx"; //资源集合

	protected final String ORDER_PATH = DownloadMgr.getInstance().FRAME2_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().FRAME2_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/sframe/android.php?version=3.1.4";// + "?random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/sframe/android.php?version=88.8.8";

	public final static String NEW_DOWNLOAD_FLAG = "frame12"; //记录在Preferences
	public final ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "frame2_id"; //判断是否有新素材更新

	private static FrameExResMgr2 sInstance;

	private FrameExResMgr2()
	{
	}

	public synchronized static FrameExResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new FrameExResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<FrameExRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<FrameExRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<FrameExRes> arr, FrameExRes item)
	{
		return arr.add(item);
	}

	public static final int FRAME_WHITE_ID = 0xFF000508;

	public static FrameExRes GetWhiteFrameRes()
	{
		FrameExRes res;

		res = new FrameExRes();
		res.m_id = FRAME_WHITE_ID;
		res.m_name = "百搭简方";
		res.m_thumb = R.drawable.__fra0__white_120;
		res.m_tjId = 1068716;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.mMaskColor = 0xffffffff;

		return res;
	}

	@Override
	protected ArrayList<FrameExRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<FrameExRes> out = new ArrayList<>();

		FrameExRes res;

		res = new FrameExRes();
		res.m_id = 10;
		res.m_tjId = 1070062;
		res.m_thumb = R.drawable.__fra2__3344201608191726398024564;
		res.m_name = "简圆";
		res.m1_1 = R.drawable.__fra2__33442016081917284955490726;
		res.m1_1_x = 56.0f / 1024f;
		res.m1_1_y = 56.0f / 1024f;
		res.m1_1_w = 915.0f / 1024f;
		res.m1_1_h = 915.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__33442016081917285367230066;
		res.m3_4_x = 56.0f / 768f;
		res.m3_4_y = 57.0f / 1024f;
		res.m3_4_w = 658.0f / 768f;
		res.m3_4_h = 913.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__33442016081917285789950660;
		res.m4_3_x = 56.0f / 1024f;
		res.m4_3_y = 56.0f / 768f;
		res.m4_3_w = 913.0f / 1024f;
		res.m4_3_h = 658.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 13;
		res.m_tjId = 1070065;
		res.m_thumb = R.drawable.__fra2__91392016081917451357823985;
		res.m_name = "简圆时尚";
		res.m1_1 = R.drawable.__fra2__77562016081917454097180455;
		res.m1_1_x = 101.0f / 1024f;
		res.m1_1_y = 101.0f / 1024f;
		res.m1_1_w = 825.0f / 1024f;
		res.m1_1_h = 825.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__77562016081917454461601395;
		res.m3_4_x = 77.0f / 768f;
		res.m3_4_y = 115.0f / 1024f;
		res.m3_4_w = 616.0f / 768f;
		res.m3_4_h = 796.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__77562016081917454848958744;
		res.m4_3_x = 115.0f / 1024f;
		res.m4_3_y = 77.0f / 768f;
		res.m4_3_w = 796.0f / 1024f;
		res.m4_3_h = 616.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 14;
		res.m_tjId = 1070066;
		res.m_thumb = R.drawable.__fra2__19982016081917492460630036;
		res.m_name = "LOMO";
		res.m1_1 = R.drawable.__fra2__199820160819175003534678;
		res.m1_1_x = 28.0f / 1024f;
		res.m1_1_y = 28.0f / 1024f;
		res.m1_1_w = 970.0f / 1024f;
		res.m1_1_h = 822.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__19982016081917500655506840;
		res.m3_4_x = 28.0f / 768f;
		res.m3_4_y = 28.0f / 1024f;
		res.m3_4_w = 714.0f / 768f;
		res.m3_4_h = 714.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__1998201608191750129898799;
		res.m4_3_x = 28.0f / 1024f;
		res.m4_3_y = 28.0f / 768f;
		res.m4_3_w = 970.0f / 1024f;
		res.m4_3_h = 572.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 15;
		res.m_tjId = 1070067;
		res.m_thumb = R.drawable.__fra2__14812016081917521822830268;
		res.m_name = "方圆";
		res.m1_1 = R.drawable.__fra2__14812016081917524562325783;
		res.m1_1_x = 56.0f / 1024f;
		res.m1_1_y = 56.0f / 1024f;
		res.m1_1_w = 914.0f / 1024f;
		res.m1_1_h = 914.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__1481201608191752509564365;
		res.m3_4_x = 57.0f / 768f;
		res.m3_4_y = 57.0f / 1024f;
		res.m3_4_w = 657.0f / 768f;
		res.m3_4_h = 913.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__14812016081917525450015514;
		res.m4_3_x = 57.0f / 1024f;
		res.m4_3_y = 57.0f / 768f;
		res.m4_3_w = 913.0f / 1024f;
		res.m4_3_h = 657.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 16;
		res.m_tjId = 1070068;
		res.m_thumb = R.drawable.__fra2__57982016081917545876764798;
		res.m_name = "菱心简约";
		res.m1_1 = R.drawable.__fra2__90002016081917550539507554;
		res.m1_1_x = 0.0f / 1024f;
		res.m1_1_y = 0.0f / 1024f;
		res.m1_1_w = 1024.0f / 1024f;
		res.m1_1_h = 1024.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__90002016081917550879222161;
		res.m3_4_x = 0.0f / 768f;
		res.m3_4_y = 0.0f / 1024f;
		res.m3_4_w = 768.0f / 768f;
		res.m3_4_h = 1024.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__57982016081917551283386755;
		res.m4_3_x = 0.0f / 1024f;
		res.m4_3_y = 0.0f / 768f;
		res.m4_3_w = 1024.0f / 1024f;
		res.m4_3_h = 768.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 17;
		res.m_tjId = 1070069;
		res.m_thumb = R.drawable.__fra2__33332016081919521450288914;
		res.m_name = "菱心幻影";
		res.m1_1 = R.drawable.__fra2__11712016081917564846795446;
		res.m1_1_x = 56.0f / 1024f;
		res.m1_1_y = 61.0f / 1024f;
		res.m1_1_w = 916.0f / 1024f;
		res.m1_1_h = 886.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__11712016081917565178256153;
		res.m3_4_x = 42.0f / 768f;
		res.m3_4_y = 98.0f / 1024f;
		res.m3_4_w = 687.0f / 768f;
		res.m3_4_h = 818.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__11712016081917565526750392;
		res.m4_3_x = 120.0f / 1024f;
		res.m4_3_y = 32.0f / 768f;
		res.m4_3_w = 787.0f / 1024f;
		res.m4_3_h = 695.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 18;
		res.m_tjId = 1070070;
		res.m_thumb = R.drawable.__fra2__8754201608191758316359584;
		res.m_name = "菱心叠影";
		res.m1_1 = R.drawable.__fra2__87542016081917584334160620;
		res.m1_1_x = 144.0f / 1024f;
		res.m1_1_y = 68.0f / 1024f;
		res.m1_1_w = 737.0f / 1024f;
		res.m1_1_h = 889.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__75132016081917584672498128;
		res.m3_4_x = 63.0f / 768f;
		res.m3_4_y = 125.0f / 1024f;
		res.m3_4_w = 645.0f / 768f;
		res.m3_4_h = 777.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__75132016081917585088206066;
		res.m4_3_x = 95.0f / 1024f;
		res.m4_3_y = 60.0f / 768f;
		res.m4_3_w = 836.0f / 1024f;
		res.m4_3_h = 651.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 19;
		res.m_tjId = 1070071;
		res.m_thumb = R.drawable.__fra2__74022016081918000973524795;
		res.m_name = "波浪圆形";
		res.m1_1 = R.drawable.__fra2__74022016081918001936687257;
		res.m1_1_x = 59.0f / 1024f;
		res.m1_1_y = 59.0f / 1024f;
		res.m1_1_w = 906.0f / 1024f;
		res.m1_1_h = 906.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__74022016081918002352621996;
		res.m3_4_x = 57.0f / 768f;
		res.m3_4_y = 57.0f / 1024f;
		res.m3_4_w = 655.0f / 768f;
		res.m3_4_h = 909.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__74022016081918002769028345;
		res.m4_3_x = 57.0f / 1024f;
		res.m4_3_y = 57.0f / 768f;
		res.m4_3_w = 909.0f / 1024f;
		res.m4_3_h = 655.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 20;
		res.m_tjId = 1070072;
		res.m_thumb = R.drawable.__fra2__39072016081918015035629867;
		res.m_name = "花瓣起伏";
		res.m1_1 = R.drawable.__fra2__39072016081918021334643589;
		res.m1_1_x = 105.0f / 1024f;
		res.m1_1_y = 96.0f / 1024f;
		res.m1_1_w = 813.0f / 1024f;
		res.m1_1_h = 831.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__39072016081918021697853151;
		res.m3_4_x = 76.0f / 768f;
		res.m3_4_y = 197.0f / 1024f;
		res.m3_4_w = 617.0f / 768f;
		res.m3_4_h = 632.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__39072016081918022080573045;
		res.m4_3_x = 173.0f / 1024f;
		res.m4_3_y = 37.0f / 768f;
		res.m4_3_w = 680.0f / 1024f;
		res.m4_3_h = 695.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 21;
		res.m_tjId = 1070073;
		res.m_thumb = R.drawable.__fra2__1154201608191808314232541;
		res.m_name = "萌态星星";
		res.m1_1 = R.drawable.__fra2__11542016081918084190503849;
		res.m1_1_x = 110.0f / 1024f;
		res.m1_1_y = 126.0f / 1024f;
		res.m1_1_w = 804.0f / 1024f;
		res.m1_1_h = 774.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__11542016081918084572921943;
		res.m3_4_x = 56.0f / 768f;
		res.m3_4_y = 198.0f / 1024f;
		res.m3_4_w = 656.0f / 768f;
		res.m3_4_h = 632.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__11542016081918085143375456;
		res.m4_3_x = 170.0f / 1024f;
		res.m4_3_y = 55.0f / 768f;
		res.m4_3_w = 658.0f / 1024f;
		res.m4_3_h = 684.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 22;
		res.m_tjId = 1070074;
		res.m_thumb = R.drawable.__fra2__63372016081918101735380140;
		res.m_name = "三角叠影";
		res.m1_1 = R.drawable.__fra2__63372016081918103248044929;
		res.m1_1_x = 66.0f / 1024f;
		res.m1_1_y = 136.0f / 1024f;
		res.m1_1_w = 895.0f / 1024f;
		res.m1_1_h = 755.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__63372016081918103588681080;
		res.m3_4_x = 33.0f / 768f;
		res.m3_4_y = 179.0f / 1024f;
		res.m3_4_w = 705.0f / 768f;
		res.m3_4_h = 656.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__63372016081918103968075830;
		res.m4_3_x = 103.0f / 1024f;
		res.m4_3_y = 71.0f / 768f;
		res.m4_3_w = 765.0f / 1024f;
		res.m4_3_h = 617.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 23;
		res.m_tjId = 1070075;
		res.m_thumb = R.drawable.__fra2__43042016081918130266564697;
		res.m_name = "圆弧简约";
		res.m1_1 = R.drawable.__fra2__10222016081918131020856885;
		res.m1_1_x = 0.0f / 1024f;
		res.m1_1_y = 34.0f / 1024f;
		res.m1_1_w = 976.0f / 1024f;
		res.m1_1_h = 991.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__10222016081918131332397892;
		res.m3_4_x = 0.0f / 768f;
		res.m3_4_y = 34.0f / 1024f;
		res.m3_4_w = 743.0f / 768f;
		res.m3_4_h = 991.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__10222016081918131635918999;
		res.m4_3_x = 0.0f / 1024f;
		res.m4_3_y = 26.0f / 768f;
		res.m4_3_w = 970.0f / 1024f;
		res.m4_3_h = 744.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 24;
		res.m_tjId = 1070076;
		res.m_thumb = R.drawable.__fra2__152620160819181433223217;
		res.m_name = "斜切简约";
		res.m1_1 = R.drawable.__fra2__15262016081918145048883625;
		res.m1_1_x = 0.0f / 1024f;
		res.m1_1_y = 0.0f / 1024f;
		res.m1_1_w = 1024.0f / 1024f;
		res.m1_1_h = 934.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__15262016081918145317579677;
		res.m3_4_x = 0.0f / 768f;
		res.m3_4_y = 0.0f / 1024f;
		res.m3_4_w = 768.0f / 768f;
		res.m3_4_h = 882.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__15262016081918145671183039;
		res.m4_3_x = 0.0f / 1024f;
		res.m4_3_y = 0.0f / 768f;
		res.m4_3_w = 1024.0f / 1024f;
		res.m4_3_h = 691.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		res = new FrameExRes();
		res.m_id = 25;
		res.m_tjId = 1070077;
		res.m_thumb = R.drawable.__fra2__20162016081918160684145147;
		res.m_name = "斜切叠影";
		res.m1_1 = R.drawable.__fra2__20162016081918162558344375;
		res.m1_1_x = 0.0f / 1024f;
		res.m1_1_y = 55.0f / 1024f;
		res.m1_1_w = 1024.0f / 1024f;
		res.m1_1_h = 872.0f / 1024f;
		res.m3_4 = R.drawable.__fra2__20162016081918162896593337;
		res.m3_4_x = 0.0f / 768f;
		res.m3_4_y = 47.0f / 1024f;
		res.m3_4_w = 768.0f / 768f;
		res.m3_4_h = 881.0f / 1024f;
		res.m4_3 = R.drawable.__fra2__2016201608191816343459695;
		res.m4_3_x = 0.0f / 1024f;
		res.m4_3_y = 0.0f / 768f;
		res.m4_3_w = 1024.0f / 1024f;
		res.m4_3_h = 768.0f / 768f;
		res.mMaskColor = 0xffffffff;
		out.add(res);

		return out;
	}

	@Override
	public boolean CheckIntact(FrameExRes res)
	{
		boolean out = false;
		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb) && ResourceUtils.HasIntact(res.m1_1, res.m3_4, res.m4_3))
			{
				out = true;
			}
		}
		return out;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<FrameExRes> arr)
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
						FrameExRes res;
						JSONObject jsonObj;
						JSONArray jsonArr2;
						JSONObject jsonObj2;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.get(i);
							if(res != null)
							{
								jsonObj = new JSONObject();
								jsonObj.put("file_tracking_id", Integer.toString(res.m_id));
								if(res.m_name != null)
								{
									jsonObj.put("name", res.m_name);
								}
								else
								{
									jsonObj.put("name", "");
								}
								if(res.m_thumb instanceof String)
								{
									jsonObj.put("thumb_120", res.m_thumb);
								}
								else
								{
									jsonObj.put("thumb_120", "");
								}
								jsonObj.put("tracking_code", res.m_tjId);
								jsonObj.put("bkcolor", "#" + Integer.toHexString(res.mMaskColor & 0xFFFFFF));
								jsonArr2 = new JSONArray();
								{
									if(res.m3_4 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "3_4");
										if(res.f3_4 != null)
										{
											jsonObj2.put("img", res.f3_4);
										}
										jsonObj2.put("mask", res.m3_4);
										jsonObj2.put("mask_x", (int)(res.m3_4_x * 768f));
										jsonObj2.put("mask_y", (int)(res.m3_4_y * 1024f));
										jsonObj2.put("mask_height", (int)(res.m3_4_h * 1024f));
										jsonObj2.put("mask_width", (int)(res.m3_4_w * 768f));
										jsonArr2.put(jsonObj2);
									}
									if(res.m4_3 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "4_3");
										if(res.f4_3 != null)
										{
											jsonObj2.put("img", res.f4_3);
										}
										jsonObj2.put("mask", res.m4_3);
										jsonObj2.put("mask_x", (int)(res.m4_3_x * 1024f));
										jsonObj2.put("mask_y", (int)(res.m4_3_y * 768f));
										jsonObj2.put("mask_height", (int)(res.m4_3_h * 768f));
										jsonObj2.put("mask_width", (int)(res.m4_3_w * 1024f));
										jsonArr2.put(jsonObj2);
									}
									if(res.m1_1 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "1_1");
										if(res.f1_1 != null)
										{
											jsonObj2.put("img", res.f1_1);
										}
										jsonObj2.put("mask", res.m1_1);
										jsonObj2.put("mask_x", (int)(res.m1_1_x * 1024f));
										jsonObj2.put("mask_y", (int)(res.m1_1_y * 1024f));
										jsonObj2.put("mask_height", (int)(res.m1_1_h * 1024f));
										jsonObj2.put("mask_width", (int)(res.m1_1_w * 1024f));
										jsonArr2.put(jsonObj2);
									}
								}
								jsonObj.put("res_arr", jsonArr2);
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
		return EventID.FRAME2_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected FrameExRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		FrameExRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new FrameExRes();
				if(isPath)
				{
					out.m_type = BaseRes.TYPE_LOCAL_PATH;
				}
				else
				{
					out.m_type = BaseRes.TYPE_NETWORK_URL;
				}
				String temp = jsonObj.getString("file_tracking_id");
				if(temp != null && temp.length() > 0)
				{
					out.m_id = Integer.parseInt(temp);
				}
				else
				{
					out.m_id = (int)(Math.random() * 10000000);
				}
				out.m_name = jsonObj.getString("name");
				if(isPath)
				{
					out.m_thumb = jsonObj.getString("thumb_120");
				}
				else
				{
					out.url_thumb = jsonObj.getString("thumb_120");
				}
				if(jsonObj.has("tracking_code"))
				{
					Object obj = jsonObj.get("tracking_code");
					if(obj != null && !obj.equals(""))
					{
						out.m_tjId = jsonObj.getInt("tracking_code");
					}
				}
				temp = jsonObj.getString("bkcolor");
				if(temp != null && temp.length() > 0)
				{
					out.mMaskColor = 0xff000000 | (int)Long.parseLong(temp.replace("#", "").trim(), 16);
				}
				JSONArray jsonArr = jsonObj.getJSONArray("res_arr");
				if(jsonArr != null)
				{
					Object obj;
					JSONObject jsonObj2;
					int len = jsonArr.length();
					for(int i = 0; i < len; i++)
					{
						obj = jsonArr.get(i);
						if(obj instanceof JSONObject)
						{
							jsonObj2 = (JSONObject)obj;

							temp = jsonObj2.getString("type");
							if(temp != null)
							{
								if(temp.equals("3_4"))
								{
									if(jsonObj2.has("img"))
									{
										if(isPath)
										{
											out.f3_4 = jsonObj2.getString("img");
										}
										else
										{
											out.url_f3_4 = jsonObj2.getString("img");
										}
									}
									if(isPath)
									{
										out.m3_4 = jsonObj2.getString("mask");
									}
									else
									{
										out.url_m3_4 = jsonObj2.getString("mask");
									}
									out.m3_4_x = jsonObj2.getInt("mask_x") / 768f;
									out.m3_4_y = jsonObj2.getInt("mask_y") / 1024f;
									out.m3_4_h = jsonObj2.getInt("mask_height") / 1024f;
									out.m3_4_w = jsonObj2.getInt("mask_width") / 768f;
								}
								else if(temp.equals("4_3"))
								{
									if(jsonObj2.has("img"))
									{
										if(isPath)
										{
											out.f4_3 = jsonObj2.getString("img");
										}
										else
										{
											out.url_f4_3 = jsonObj2.getString("img");
										}
									}
									if(isPath)
									{
										out.m4_3 = jsonObj2.getString("mask");
									}
									else
									{
										out.url_m4_3 = jsonObj2.getString("mask");
									}
									out.m4_3_x = jsonObj2.getInt("mask_x") / 1024f;
									out.m4_3_y = jsonObj2.getInt("mask_y") / 768f;
									out.m4_3_h = jsonObj2.getInt("mask_height") / 768f;
									out.m4_3_w = jsonObj2.getInt("mask_width") / 1024f;
								}
								else if(temp.equals("1_1"))
								{
									if(jsonObj2.has("img"))
									{
										if(isPath)
										{
											out.f1_1 = jsonObj2.getString("img");
										}
										else
										{
											out.url_f1_1 = jsonObj2.getString("img");
										}
									}
									if(isPath)
									{
										out.m1_1 = jsonObj2.getString("mask");
									}
									else
									{
										out.url_m1_1 = jsonObj2.getString("mask");
									}
									out.m1_1_x = jsonObj2.getInt("mask_x") / 1024f;
									out.m1_1_y = jsonObj2.getInt("mask_y") / 1024f;
									out.m1_1_h = jsonObj2.getInt("mask_height") / 1024f;
									out.m1_1_w = jsonObj2.getInt("mask_width") / 1024f;
								}
							}
						}
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
	protected String GetOldIdFlag()
	{
		return OLD_ID_FLAG;
	}

	@Override
	public FrameExRes GetItem(ArrayList<FrameExRes> arr, int id)
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
	protected void RebuildNetResArr(ArrayList<FrameExRes> dst, ArrayList<FrameExRes> src)
	{
		if(dst != null && src != null)
		{
			FrameExRes srcTemp;
			FrameExRes dstTemp;
			Class cls = FrameExRes.class;
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
					dstTemp.m1_1 = srcTemp.m1_1;
					dstTemp.m4_3 = srcTemp.m4_3;
					dstTemp.m3_4 = srcTemp.m3_4;
					dstTemp.f1_1 = srcTemp.f1_1;
					dstTemp.f3_4 = srcTemp.f3_4;
					dstTemp.f4_3 = srcTemp.f4_3;

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

	public ArrayList<GroupRes> GetNoDownloadGroupResArr(Context context)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<ThemeRes> downloadArr = ThemeResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(downloadArr != null)
		{
			ThemeRes temp;
			ArrayList<FrameExRes> resArr;
			GroupRes groupRes;
			boolean flag;
			int len = downloadArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if(temp.m_sFrameIDArr != null && temp.m_sFrameIDArr.length > 0)
				{
					flag = false;
					resArr = GetResArr(temp.m_sFrameIDArr, false);
					if(resArr.size() != temp.m_sFrameIDArr.length)
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

	public int GetNoDownloadCount(Context context)
	{
		return GetNoDownloadGroupResArr(context).size();
	}

	public ArrayList<GroupRes> GetDownloadedGroupResArr(Context context)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<FrameExRes> orgArr = new ArrayList<>();
		ArrayList<FrameExRes> tempArr = sync_GetSdcardRes(context, null);
		if(tempArr != null)
		{
			orgArr.addAll(tempArr);
		}
		ArrayList<ThemeRes> themeArr = new ArrayList<>();
		ArrayList<ThemeRes> tempThemeArr = ThemeResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null);
		if(tempThemeArr != null)
		{
			themeArr.addAll(tempThemeArr);
		}
		tempThemeArr = ThemeResMgr2.getInstance().sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);
		if(tempThemeArr != null)
		{
			themeArr.addAll(tempThemeArr);
		}
		int len = themeArr.size();
		ThemeRes temp;
		ArrayList<FrameExRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_sFrameIDArr != null && temp.m_sFrameIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_sFrameIDArr);
				if(subArr.size() == temp.m_sFrameIDArr.length)
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
			temp.m_sFrameIDArr = new int[orgArr.size()];
			len = orgArr.size();
			for(int i = 0; i < len; i++)
			{
				temp.m_sFrameIDArr[i] = orgArr.get(i).m_id;
			}

			groupRes = new GroupRes();
			groupRes.m_themeRes = temp;
			groupRes.m_ress = new ArrayList<>();
			groupRes.m_ress.addAll(orgArr);
			out.add(groupRes);
		}

		return out;
	}

	@Override
	public FrameExRes GetRes(int id, boolean onlyCanUse)
	{
		FrameExRes out;
		if(id == FRAME_WHITE_ID)
		{
			out = GetWhiteFrameRes();
		}
		else
		{
			out = super.GetRes(id, onlyCanUse);
		}
		return out;
	}

	public ArrayList<FrameExRes> GetResArr()
	{
		Context context = MyFramework2App.getInstance().getApplicationContext();
		return ResourceUtils.BuildShowArr(sync_GetLocalRes(context, null), sync_GetSdcardRes(context, null), GetOrderArr());
	}

	public void DeleteNewFlag(Context context, int[] ids)
	{
		if(ids != null && ids.length > 0)
		{
			ResourceUtils.DeleteIds(new_flag_arr, ids);
			ResourceMgr.UpdateNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG);
		}
	}

	public void DeleteNewFlag(Context context, int id)
	{
		ResourceMgr.DeleteNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, id);
	}

	public void DeleteGroupRes(Context context, GroupRes res)
	{
		int[] ids = res.m_themeRes.m_sFrameIDArr;
		ArrayList<FrameExRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<FrameExRes> arr2 = ResourceUtils.DeleteItems(sdcardArr, ids);
		if(arr2 != null && arr2.size() > 0)
		{
			FrameExRes temp;
			int len = arr2.size();
			for(int i = 0; i < len; i++)
			{
				temp = arr2.get(i);
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

	public void AddNewFlag(Context context, int[] ids)
	{
		if(ids != null && ids.length > 0)
		{
			if(ResourceUtils.AddIds(new_flag_arr, ids))
			{
				ResourceMgr.UpdateNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG);
			}
		}
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

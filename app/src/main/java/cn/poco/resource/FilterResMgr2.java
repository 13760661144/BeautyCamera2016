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
 * Created by Raining on 2017/9/29.
 */

public class FilterResMgr2 extends BaseResMgr<FilterRes, ArrayList<FilterRes>>
{
	public final static int NEW_JSON_VER = 1;
	public final static int NEW_ORDER_JSON_VER = 1;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().FILTER_PATH + "/filter.xxxx"; //资源集合

	protected final String ORDER_PATH = DownloadMgr.getInstance().FILTER_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().FILTER_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/filter/android.php?version=%E7%BE%8E%E4%BA%BA%E7%9B%B8%E6%9C%BAv4.1.3";// + "?random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/filter/android.php?version=88.8.8";

	public final static String NEW_DOWNLOAD_FLAG = "filter"; //记录在Preferences
	public final ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "filter_id"; //判断是否有新素材更新

	private static FilterResMgr2 sInstance;

	private FilterResMgr2()
	{
	}

	public synchronized static FilterResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new FilterResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<FilterRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<FilterRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<FilterRes> arr, FilterRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<FilterRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<FilterRes> out = new ArrayList<>();
		FilterRes filterRes = null;
		{
			//默认的内置数据
			filterRes = new FilterRes();
			filterRes.m_name = "Original";
			filterRes.m_id = 0;
			filterRes.m_filterAlpha = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 550;
			filterRes.m_name = "Cake";
			filterRes.m_thumb = R.drawable.__fil__89332017090815124176343270;
			filterRes.m_listThumbRes = R.drawable.__fil__89332017090815123061105075;
			filterRes.m_tjId = 106014353;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105200700_5334_6566668810;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 549;
			filterRes.m_name = "Fruitage";
			filterRes.m_thumb = R.drawable.__fil__608320170908152142549259;
			filterRes.m_listThumbRes = R.drawable.__fil__42142017090815213015428677;
			filterRes.m_tjId = 106014181;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105200735_2143_1505785218;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 554;
			filterRes.m_name = "Macaroon";
			filterRes.m_thumb = R.drawable.__fil__20652017090815222116888212;
			filterRes.m_listThumbRes = R.drawable.__fil__5876201709081522115034750;
			filterRes.m_tjId = 106014614;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201045_5500_4961168575;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 551;
			filterRes.m_name = "Baking";
			filterRes.m_thumb = R.drawable.__fil__67302017090815240255614630;
			filterRes.m_listThumbRes = R.drawable.__fil__13672017090815235652788116;
			filterRes.m_tjId = 106014220;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201100_7380_8684774329;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 552;
			filterRes.m_name = "Sushi";
			filterRes.m_thumb = R.drawable.__fil__99462017090815250894752943;
			filterRes.m_listThumbRes = R.drawable.__fil__4sushi;
			filterRes.m_tjId = 106014084;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201144_6728_8637855758;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 553;
			filterRes.m_name = "Confiture";
			filterRes.m_thumb = R.drawable.__fil__63892017090815262996855446;
			filterRes.m_listThumbRes = R.drawable.__fil__96382017090815262245202945;
			filterRes.m_tjId = 106014354;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201204_3376_2022200270;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 449;
			filterRes.m_name = "萝莉";
			filterRes.m_thumb = R.drawable.__fil__25802017041414463678481133;
			filterRes.m_listThumbRes = R.drawable.__fil__34282017041419134983313025;
			filterRes.m_tjId = 106012057;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201301_9534_2916685234;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 447;
			filterRes.m_name = "薄荷";
			filterRes.m_thumb = R.drawable.__fil__25072017041414074897657151;
			filterRes.m_listThumbRes = R.drawable.__fil__64692017041711525850412126;
			filterRes.m_tjId = 106012055;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[3];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201331_5847_4333264387;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__67822017041219164696086117;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 46;
			filterRes.m_datas[1].m_params[1] = 60;
			filterRes.m_datas[2] = new FilterRes.FilterData();
			filterRes.m_datas[2].m_res = R.drawable.__fil__95492017041219170131871651;
			filterRes.m_datas[2].m_isSkipFace = false;
			filterRes.m_datas[2].m_params = new int[2];
			filterRes.m_datas[2].m_params[0] = 45;
			filterRes.m_datas[2].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 446;
			filterRes.m_name = "暧昧";
			filterRes.m_thumb = R.drawable.__fil__15942017041414064890225896;
			filterRes.m_listThumbRes = R.drawable.__fil__81722017041715365317931856;
			filterRes.m_tjId = 106012358;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201407_4760_1222422048;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 450;
			filterRes.m_name = "樱桃";
			filterRes.m_thumb = R.drawable.__fil__93952017041414073329050017;
			filterRes.m_listThumbRes = R.drawable.__fil__98752017041419141962084092;
			filterRes.m_tjId = 106012058;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201441_6991_6231366959;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 448;
			filterRes.m_name = "告白";
			filterRes.m_thumb = R.drawable.__fil__34662017041414071779372141;
			filterRes.m_listThumbRes = R.drawable.__fil__99802017041419143279599262;
			filterRes.m_tjId = 106012056;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[2];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201519_9875_4728349228;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__7177201704101623228136753;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 45;
			filterRes.m_datas[1].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 451;
			filterRes.m_name = "精灵";
			filterRes.m_thumb = R.drawable.__fil__32932017041414070440136727;
			filterRes.m_listThumbRes = R.drawable.__fil__83862017041419145921072677;
			filterRes.m_tjId = 106012448;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[3];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201540_3457_5458080932;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__55552017041219282310964088;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 41;
			filterRes.m_datas[1].m_params[1] = 50;
			filterRes.m_datas[2] = new FilterRes.FilterData();
			filterRes.m_datas[2].m_res = R.drawable.__fil__2486201704122041059832821;
			filterRes.m_datas[2].m_isSkipFace = false;
			filterRes.m_datas[2].m_params = new int[2];
			filterRes.m_datas[2].m_params[0] = 45;
			filterRes.m_datas[2].m_params[1] = 90;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 496;
			filterRes.m_name = "Pancake";
			filterRes.m_thumb = R.drawable.__fil__26122017041218423919787298;
			filterRes.m_listThumbRes = R.drawable.__fil__56842017041420145446740266;
			filterRes.m_tjId = 106012432;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 60;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201655_1592_3603741614;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 497;
			filterRes.m_name = "Limon";
			filterRes.m_thumb = R.drawable.__fil__4353201704121843134522570;
			filterRes.m_listThumbRes = R.drawable.__fil__96002017041415460916860832;
			filterRes.m_tjId = 106012433;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 60;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201729_3812_1598083999;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 498;
			filterRes.m_name = "Cherry";
			filterRes.m_thumb = R.drawable.__fil__5799201704121843255475152;
			filterRes.m_listThumbRes = R.drawable.__fil__21782017041415462921553792;
			filterRes.m_tjId = 106012354;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 60;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180108120517_9062_8220880982;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 499;
			filterRes.m_name = "Ice-lolly";
			filterRes.m_thumb = R.drawable.__fil__47622017041218435236670368;
			filterRes.m_listThumbRes = R.drawable.__fil__68682017041415465598226231;
			filterRes.m_tjId = 106012434;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 60;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201819_1583_5091299826;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 500;
			filterRes.m_name = "Cream";
			filterRes.m_thumb = R.drawable.__fil__94102017041218440423565994;
			filterRes.m_listThumbRes = R.drawable.__fil__33502017041415472768027928;
			filterRes.m_tjId = 106012435;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 60;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201853_1163_1097562700;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 501;
			filterRes.m_name = "Soda water";
			filterRes.m_thumb = R.drawable.__fil__44712017041218442249887845;
			filterRes.m_listThumbRes = R.drawable.__fil__82332017041415480175605845;
			filterRes.m_tjId = 106012436;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105201915_6214_8396199384;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 454;
			filterRes.m_name = "恋巴黎";
			filterRes.m_thumb = R.drawable.__fil__15154344820171129164632_6071_7643653249;
			filterRes.m_listThumbRes = R.drawable.__fil__75432017041409210066798851;
			filterRes.m_tjId = 106012062;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105202033_9722_3548478788;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 457;
			filterRes.m_name = "香榭丽舍";
			filterRes.m_thumb = R.drawable.__fil__15154344820171129164646_7151_4235285555;
			filterRes.m_listThumbRes = R.drawable.__fil__80772017041409213859889361;
			filterRes.m_tjId = 106012064;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[2];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105202104_4882_5672672829;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__78182017041322392184050572;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 45;
			filterRes.m_datas[1].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 453;
			filterRes.m_name = "茶花";
			filterRes.m_thumb = R.drawable.__fil__15154344820171129164701_8506_2939550621;
			filterRes.m_listThumbRes = R.drawable.__fil__35582017041409130851974282;
			filterRes.m_tjId = 106012061;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = true;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105202310_4782_3730546461;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 452;
			filterRes.m_name = "芭蕾";
			filterRes.m_thumb = R.drawable.__fil__15154344820171129164714_9197_7738138085;
			filterRes.m_listThumbRes = R.drawable.__fil__25062017041409123374103478;
			filterRes.m_tjId = 106012060;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[2];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105202427_6807_2965935604;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__88342017041322571237676311;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 45;
			filterRes.m_datas[1].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 455;
			filterRes.m_name = "普罗旺斯";
			filterRes.m_thumb = R.drawable.__fil__15154344820171129164726_1363_8212150002;
			filterRes.m_listThumbRes = R.drawable.__fil__97902017041409212517415892;
			filterRes.m_tjId = 106015159;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105202449_4314_7820344288;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 458;
			filterRes.m_name = "庄园";
			filterRes.m_thumb = R.drawable.__fil__15154344820171129164746_6777_8616900896;
			filterRes.m_listThumbRes = R.drawable.__fil__70042017041409220321465158;
			filterRes.m_tjId = 106012065;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105202513_9936_8616765469;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 509;
			filterRes.m_name = "Peach";
			filterRes.m_thumb = R.drawable.__fil__52112017041414173961848178;
			filterRes.m_listThumbRes = R.drawable.__fil__88692017041419522892930577;
			filterRes.m_tjId = 106012353;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[4];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__48052017041322340960843944;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__64092017041322342097329884;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 46;
			filterRes.m_datas[1].m_params[1] = 100;
			filterRes.m_datas[2] = new FilterRes.FilterData();
			filterRes.m_datas[2].m_res = R.drawable.__fil__64092017041322342748731040;
			filterRes.m_datas[2].m_isSkipFace = false;
			filterRes.m_datas[2].m_params = new int[2];
			filterRes.m_datas[2].m_params[0] = 41;
			filterRes.m_datas[2].m_params[1] = 100;
			filterRes.m_datas[3] = new FilterRes.FilterData();
			filterRes.m_datas[3].m_res = R.drawable.__fil__70842017041322343511097073;
			filterRes.m_datas[3].m_isSkipFace = false;
			filterRes.m_datas[3].m_params = new int[2];
			filterRes.m_datas[3].m_params[0] = 45;
			filterRes.m_datas[3].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 456;
			filterRes.m_name = "Jasmine";
			filterRes.m_thumb = R.drawable.__fil__17182017041414141989428062;
			filterRes.m_listThumbRes = R.drawable.__fil__21092017041711300295095560;
			filterRes.m_tjId = 106012387;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[2];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__61302017041322273081645845;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__19132017041322273714720546;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 41;
			filterRes.m_datas[1].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 469;
			filterRes.m_name = "Camellia";
			filterRes.m_thumb = R.drawable.__fil__51202017041414143983916077;
			filterRes.m_listThumbRes = R.drawable.__fil__24662017041419532329670751;
			filterRes.m_tjId = 106012383;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__11522017041321255656111157;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 505;
			filterRes.m_name = "Rosa";
			filterRes.m_thumb = R.drawable.__fil__85102017041414145856324960;
			filterRes.m_listThumbRes = R.drawable.__fil__5950201704141953459964940;
			filterRes.m_tjId = 106012388;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[4];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__21982017041322284776880398;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__47512017041322295759764416;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 46;
			filterRes.m_datas[1].m_params[1] = 100;
			filterRes.m_datas[2] = new FilterRes.FilterData();
			filterRes.m_datas[2].m_res = R.drawable.__fil__97942017041322290810868356;
			filterRes.m_datas[2].m_isSkipFace = false;
			filterRes.m_datas[2].m_params = new int[2];
			filterRes.m_datas[2].m_params[0] = 1;
			filterRes.m_datas[2].m_params[1] = 100;
			filterRes.m_datas[3] = new FilterRes.FilterData();
			filterRes.m_datas[3].m_res = R.drawable.__fil__37952017041322292935463713;
			filterRes.m_datas[3].m_isSkipFace = false;
			filterRes.m_datas[3].m_params = new int[2];
			filterRes.m_datas[3].m_params[0] = 61;
			filterRes.m_datas[3].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 506;
			filterRes.m_name = "Lavender";
			filterRes.m_thumb = R.drawable.__fil__17632017041414161843186575;
			filterRes.m_listThumbRes = R.drawable.__fil__94282017041713490749638876;
			filterRes.m_tjId = 106012389;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[4];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__62952017041322305487905454;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__74112017041322310243474087;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 41;
			filterRes.m_datas[1].m_params[1] = 100;
			filterRes.m_datas[2] = new FilterRes.FilterData();
			filterRes.m_datas[2].m_res = R.drawable.__fil__74112017041322311112388917;
			filterRes.m_datas[2].m_isSkipFace = false;
			filterRes.m_datas[2].m_params = new int[2];
			filterRes.m_datas[2].m_params[0] = 30;
			filterRes.m_datas[2].m_params[1] = 100;
			filterRes.m_datas[3] = new FilterRes.FilterData();
			filterRes.m_datas[3].m_res = R.drawable.__fil__61602017041322312037966737;
			filterRes.m_datas[3].m_isSkipFace = false;
			filterRes.m_datas[3].m_params = new int[2];
			filterRes.m_datas[3].m_params[0] = 61;
			filterRes.m_datas[3].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 507;
			filterRes.m_name = "Sunflower";
			filterRes.m_thumb = R.drawable.__fil__38262017041414170236318755;
			filterRes.m_listThumbRes = R.drawable.__fil__77652017041713492887525033;
			filterRes.m_tjId = 106012390;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[3];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__93462017041322321941347451;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__83782017041322323083341146;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 45;
			filterRes.m_datas[1].m_params[1] = 100;
			filterRes.m_datas[2] = new FilterRes.FilterData();
			filterRes.m_datas[2].m_res = R.drawable.__fil__39782017041322323746433692;
			filterRes.m_datas[2].m_isSkipFace = false;
			filterRes.m_datas[2].m_params = new int[2];
			filterRes.m_datas[2].m_params[0] = 38;
			filterRes.m_datas[2].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 503;
			filterRes.m_name = "Dandelion";
			filterRes.m_thumb = R.drawable.__fil__53912017041414175344685635;
			filterRes.m_listThumbRes = R.drawable.__fil__80522017041713494748868461;
			filterRes.m_tjId = 106012384;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__31172017041321555259880526;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 511;
			filterRes.m_name = "Lilac";
			filterRes.m_thumb = R.drawable.__fil__49572017041414132646847762;
			filterRes.m_listThumbRes = R.drawable.__fil__38052017041420590382794052;
			filterRes.m_tjId = 106012418;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__49572017041414125119391359;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 508;
			filterRes.m_name = "Clover";
			filterRes.m_thumb = R.drawable.__fil__17712017041414171578957169;
			filterRes.m_listThumbRes = R.drawable.__fil__67732017041714021343401493;
			filterRes.m_tjId = 106012391;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__89172017041322333492352240;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 510;
			filterRes.m_name = "Tulip";
			filterRes.m_thumb = R.drawable.__fil__74852017041414180899790468;
			filterRes.m_listThumbRes = R.drawable.__fil__67022017041713504075387515;
			filterRes.m_tjId = 106012392;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 100;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[2];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__8786201704132235241552833;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			filterRes.m_datas[1] = new FilterRes.FilterData();
			filterRes.m_datas[1].m_res = R.drawable.__fil__33462017041322352956614260;
			filterRes.m_datas[1].m_isSkipFace = false;
			filterRes.m_datas[1].m_params = new int[2];
			filterRes.m_datas[1].m_params[0] = 33;
			filterRes.m_datas[1].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 588;
			filterRes.m_name = "Fruitage";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203113_5525_2307525316;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203105_3945_2277308870;
			filterRes.m_tjId = 1062716389;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105203119_2972_1234511087;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 590;
			filterRes.m_name = "Camellia";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203257_2031_2877786724;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203251_1069_9366092932;
			filterRes.m_tjId = 1062716391;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105203303_1613_2298860197;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 592;
			filterRes.m_name = "萝莉";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203432_1311_2855935987;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203427_3264_1834631528;
			filterRes.m_tjId = 1062716393;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180108153421_4517_3470315697;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 593;
			filterRes.m_name = "Cake";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203510_8548_2111717365;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203502_2403_3794670518;
			filterRes.m_tjId = 1062716394;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105203517_3694_3248764561;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 594;
			filterRes.m_name = "Limon";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203600_9049_6968849145;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203550_5367_1173676956;
			filterRes.m_tjId = 1062716395;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 60;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105203606_8205_3494586315;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 595;
			filterRes.m_name = "Macaroon";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203647_1457_3009348561;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203640_1129_1760069952;
			filterRes.m_tjId = 1062716396;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105203653_9277_8276370041;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 596;
			filterRes.m_name = "Baking";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203730_5728_7557703387;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203723_3827_4464056049;
			filterRes.m_tjId = 1062716397;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105203735_4109_5063447721;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);

			filterRes = new FilterRes();
			filterRes.m_id = 597;
			filterRes.m_name = "恋巴黎";
			filterRes.m_thumb = R.drawable.__fil__15154344820180105203810_6632_5790457545;
			filterRes.m_listThumbRes = R.drawable.__fil__15154344820180105203804_9551_9087390364;
			filterRes.m_tjId = 1062716398;
			filterRes.m_type = BaseRes.TYPE_LOCAL_RES;
			filterRes.m_filterType = FilterRes.FILTER_TYPE_IMG;
			filterRes.m_isUpDateToCamera = true;
			filterRes.m_filterAlpha = 50;
			filterRes.m_isHasvignette = false;
			filterRes.m_isSkipFace = false;
			filterRes.m_datas = new FilterRes.FilterData[1];
			filterRes.m_datas[0] = new FilterRes.FilterData();
			filterRes.m_datas[0].m_res = R.drawable.__fil__15154344820180105203817_6778_8414301885;
			filterRes.m_datas[0].m_isSkipFace = false;
			filterRes.m_datas[0].m_params = new int[2];
			filterRes.m_datas[0].m_params[0] = 1;
			filterRes.m_datas[0].m_params[1] = 100;
			out.add(filterRes);
		}

		return out;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected FilterRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		FilterRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new FilterRes();
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

				temp = jsonObj.getString("pushID");
				if(temp != null && temp.length() > 0)
				{
					out.m_tjId = Integer.parseInt(temp);
				}

				out.m_name = jsonObj.getString("name");
				if(isPath)
				{
					out.m_thumb = jsonObj.getString("thumb");
				}
				else
				{
					out.url_thumb = jsonObj.getString("thumb");
				}

				if(jsonObj.has("listThumb"))
				{
					if(isPath)
					{
						out.m_listThumbRes = jsonObj.getString("listThumb");
					}
					else
					{
						out.m_listThumbUrl = jsonObj.getString("listThumb");
					}
				}

				if(jsonObj.has("resType"))
				{
					out.m_filterType = jsonObj.getInt("resType");
				}

				if(jsonObj.has("camera"))
				{
					out.m_isUpDateToCamera = jsonObj.getBoolean("camera");
				}

				if(jsonObj.has("watermark"))
				{
					out.m_isHaswatermark = jsonObj.getBoolean("watermark");
				}

				if(jsonObj.has("vignette"))
				{
					out.m_isHasvignette = jsonObj.getBoolean("vignette");
				}

				if(jsonObj.has("alpha"))
				{
					out.m_filterAlpha = jsonObj.getInt("alpha");
				}

				if(jsonObj.has("skipFace"))
				{
					out.m_isSkipFace = jsonObj.getBoolean("skipFace");
				}

				JSONArray jsonArray = jsonObj.getJSONArray("res");
				if(jsonArray != null)
				{
					Object obj;
					JSONObject jsonObject2;
					int len = jsonArray.length();
					if(len > 0)
					{
						FilterRes.FilterData[] filterDatas = new FilterRes.FilterData[len];
						//out.m_datas = new FilterRes.FilterData[len];
						for(int i = 0; i < len; i++)
						{
							filterDatas[i] = new FilterRes.FilterData();
							obj = jsonArray.get(i);
							if(obj instanceof JSONObject)
							{
								jsonObject2 = (JSONObject)obj;
								if(jsonObject2.has("img"))
								{
									if(isPath)
									{
										filterDatas[i].m_res = jsonObject2.getString("img");
									}
									else
									{
										filterDatas[i].m_url_img = jsonObject2.getString("img");
									}

								}

								if(jsonObject2.has("params"))
								{
									if(jsonObject2.get("params") instanceof JSONArray)
									{
										JSONArray jsonArray1 = jsonObject2.getJSONArray("params");
										int len1 = jsonArray1.length();
										int[] tempParams = new int[len1];
										for(int j = 0; j < len1; j++)
										{
											tempParams[j] = jsonArray1.getInt(j);
										}
										filterDatas[i].m_params = tempParams;
									}
								}

								if(jsonObject2.has("skipFace"))
								{
									filterDatas[i].m_isSkipFace = jsonObject2.getBoolean("skipFace");
								}

								if(i == 0)
								{
									out.m_isSkipFace = filterDatas[i].m_isSkipFace;
								}
							}
						}
						out.m_datas = filterDatas;
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

	/**
	 * 检查素材是否有效
	 */
	@Override
	public boolean CheckIntact(FilterRes res)
	{
		boolean out = false;
		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb, res.m_listThumbUrl))
			{
				if(res.m_datas != null && res.m_datas.length > 0)
				{
					FilterRes.FilterData[] temp = res.m_datas;
					for(int i = 0; i < temp.length; i++)
					{
						if(!ResourceUtils.HasIntact(temp[i].m_res))
						{
							return false;
						}
					}
				}
				out = true;
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
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<FilterRes> arr)
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
						FilterRes res;
						JSONObject jsonObj;
						JSONArray jsonArr2;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.get(i);
							if(res != null)
							{
								jsonObj = new JSONObject();
								jsonObj.put("id", Integer.toString(res.m_id));
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
									jsonObj.put("thumb", res.m_thumb);
								}
								else
								{
									jsonObj.put("thumb", "");
								}
								jsonObj.put("pushID", res.m_tjId);
								jsonObj.put("resType", res.m_resType);
								if(res.m_listThumbRes != null)
								{
									jsonObj.put("listThumb", res.m_listThumbRes);
								}
								jsonObj.put("camera", res.m_isUpDateToCamera);
								jsonObj.put("watermark", res.m_isHaswatermark);
								jsonObj.put("vignette", res.m_isHasvignette);
								jsonObj.put("alpha", res.m_filterAlpha);
								jsonObj.put("skipFace", res.m_isSkipFace);
								jsonArr2 = new JSONArray();
								{
									if(res.m_datas != null && res.m_datas.length > 0)
									{
										for(int j = 0; j < res.m_datas.length; j++)
										{
											FilterRes.FilterData filterData = res.m_datas[j];
											if(filterData != null)
											{
												JSONObject jsonObject2 = new JSONObject();
												jsonObject2.put("img", filterData.m_res);
												jsonObject2.put("skipFace", filterData.m_isSkipFace);
												JSONArray jsonArray3 = new JSONArray();
												if(filterData.m_params != null && filterData.m_params.length > 0)
												{
													for(int k = 0; k < filterData.m_params.length; k++)
													{
														jsonArray3.put(filterData.m_params[k]);
													}
													jsonObject2.put("params", jsonArray3);
												}
												jsonArr2.put(jsonObject2);
											}
										}
									}
								}
								jsonObj.put("res", jsonArr2);
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
	protected void InitOrderArr(ArrayList<Integer> dstObj)
	{
		super.InitOrderArr(dstObj);

		ReadOrderArr();
		Context context = MyFramework2App.getInstance().getApplicationContext();
		if(ResourceUtils.RebuildOrder(ThemeResMgr2.getInstance().sync_GetLocalRes(context, null), ThemeResMgr2.getInstance().sync_GetSdcardRes(context, null), dstObj))
		{
			SaveOrderArr();
		}
	}

	@Override
	protected String GetOldIdFlag()
	{
		return OLD_ID_FLAG;
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.FILTER_CLOUD_OK;
	}

	@Override
	public FilterRes GetItem(ArrayList<FilterRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	public ArrayList<FilterGroupRes> GetGroupResArr()
	{
		ArrayList<FilterGroupRes> out = new ArrayList<>();
		ArrayList<FilterRes> orgArr = new ArrayList<>();
		ArrayList<FilterRes> tempArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);
		if(tempArr != null)
		{
			orgArr.addAll(tempArr);
		}

		ArrayList<ThemeRes> resArr = ResourceUtils.BuildShowArr(ThemeResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null),
				ThemeResMgr2.getInstance().sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null), GetOrderArr());

		ThemeRes temp;
		tempArr = null;
		FilterGroupRes group;
		int len = resArr.size();
		for(int i = 0; i < len; i++)
		{
			temp = resArr.get(i);
			if(temp.m_filterIDArr != null && temp.m_filterIDArr.length > 0)
			{
				ResourceUtils.DeleteItems(orgArr, temp.m_filterIDArr);
				tempArr = GetResArr(temp.m_filterIDArr, true);
				if(tempArr.size() == temp.m_filterIDArr.length)
				{
					group = new FilterGroupRes();
					group.m_id = temp.m_id;
					group.m_name = temp.m_name;
					group.m_thumb = temp.m_filter_thumb_res;
					group.m_maskColor = temp.m_filter_mask_color;
					group.m_isBusiness = temp.m_isBusiness;
					group.m_group = tempArr;
					out.add(group);
				}
			}
		}

		//其他
		if(orgArr.size() > 0)
		{
			Object thumb = null;
			temp = new ThemeRes();
			temp.m_name = "其他";
			temp.m_filterIDArr = new int[orgArr.size()];
			len = orgArr.size();
			for (int i = 0; i < len; i++)
			{
				if (thumb == null) {
					FilterRes filterRes = orgArr.get(i);
					if (filterRes != null) thumb = filterRes.m_thumb;
				}
				temp.m_filterIDArr[i] = orgArr.get(i).m_id;
			}

			group = new FilterGroupRes();
			group.m_id = temp.m_id;
			group.m_name = temp.m_name;
			group.m_thumb = thumb == null ? R.mipmap.ic_launcher : thumb;
			group.m_group = orgArr;
			out.add(group);
		}

		return out;
	}

	public ArrayList<GroupRes> GetDownloadedGroupResArr(Context context)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<FilterRes> orgArr = new ArrayList<>();
		ArrayList<FilterRes> tempArr = sync_GetSdcardRes(context, null);
		if(tempArr != null)
		{
			orgArr.addAll(tempArr);
		}
		ArrayList<ThemeRes> themeArr = ThemeResMgr2.getInstance().GetAllResArr();
		int len = themeArr.size();
		ThemeRes temp;
		ArrayList<FilterRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_filterIDArr != null && temp.m_filterIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_filterIDArr);
				if(subArr.size() == temp.m_filterIDArr.length)
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
		//if(orgArr.size() > 0)
		//{
		//	temp = new ThemeRes();
		//	temp.m_name = "其他";
		//	temp.m_filterIDArr = new int[orgArr.size()];
		//	len = orgArr.size();
		//	for(int i = 0; i < len; i++)
		//	{
		//		temp.m_filterIDArr[i] = orgArr.get(i).m_id;
		//	}
		//
		//	groupRes = new GroupRes();
		//	groupRes.m_themeRes = temp;
		//	groupRes.m_ress = new ArrayList<>();
		//	groupRes.m_ress.addAll(orgArr);
		//	out.add(groupRes);
		//}

		return out;
	}

	public ArrayList<GroupRes> GetNoDownloadGroupResArr(Context context, boolean isShowHide)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<ThemeRes> downloadArr = ThemeResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if (downloadArr != null)
		{
			ThemeRes temp;
			ArrayList<FilterRes> resArr;
			GroupRes groupRes;

			boolean flag;
			int len = downloadArr.size();
			for (int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if (temp.m_filterIDArr != null && temp.m_filterIDArr.length > 0)
				{
					flag = false;
					resArr = GetResArr(temp.m_filterIDArr, false);
					if (resArr.size() != temp.m_filterIDArr.length)
					{
						flag = true;
						if (temp.m_isHide && !isShowHide)
						{
							flag = false;
						}
					}
					else
					{
						int len2 = resArr.size();
						for (int j = 0; j < len2; j++)
						{
							if (resArr.get(j).m_type == BaseRes.TYPE_NETWORK_URL)
							{
								flag = true;

								if (temp.m_isHide && !isShowHide)
								{
									flag = false;
								}
								break;
							}
						}
					}
					if (flag)
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

	public int GetNoDownloadCount()
	{
		return GetNoDownloadCount(false);
	}

	public int GetNoDownloadCount(boolean isShowHide)
	{
		return GetNoDownloadGroupResArr(MyFramework2App.getInstance().getApplicationContext(), isShowHide).size();
	}

	public boolean IsNewGroup(int themeId)
	{
		boolean out = false;

		if(ResourceUtils.HasId(new_flag_arr, themeId) >= 0)
		{
			out = true;
		}

		return out;
	}

	public void DeleteGroupNewFlag(Context context, int themeId)
	{
		ResourceMgr.DeleteNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, themeId);
	}

	public void AddGroupNewFlag(Context context, int themeId)
	{
		ResourceMgr.AddNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, themeId);
	}

	public void AddGroupId(int themeId)
	{
		ResourceUtils.DeleteId(GetOrderArr(), themeId);
		GetOrderArr().add(0, themeId);
		SaveOrderArr();
	}

	public void DeleteGroupRes(Context context, GroupRes res)
	{
		int[] ids = res.m_themeRes.m_filterIDArr;
		ArrayList<FilterRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<FilterRes> arr = ResourceUtils.DeleteItems(sdcardArr, ids);
		if (arr != null && arr.size() > 0)
		{
			FilterRes temp;
			int len = arr.size();
			for (int i = 0; i < len; i++)
			{
				temp = arr.get(i);
				if (temp.m_type == BaseRes.TYPE_LOCAL_PATH)
				{
					temp.m_type = BaseRes.TYPE_NETWORK_URL;
				}
			}
			ResourceUtils.DeleteId(GetOrderArr(), res.m_themeRes.m_id);
			DeleteGroupNewFlag(context, res.m_themeRes.m_id);
			sync_SaveSdcardRes(context, sdcardArr);
			SaveOrderArr();
		}

		ThemeResMgr2.getInstance().ClearEmptyRes(context, res.m_themeRes);
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
	protected void RebuildNetResArr(ArrayList<FilterRes> dst, ArrayList<FilterRes> src)
	{
		if (dst != null && src != null)
		{
			FilterRes srcTemp;
			FilterRes dstTemp;
			Class cls = FilterRes.class;
			Field[] fields = cls.getDeclaredFields();
			int index;
			int len = dst.size();
			for (int i = 0; i < len; i++)
			{
				dstTemp = dst.get(i);
				index = ResourceUtils.HasItem(src, dstTemp.m_id);
				if (index >= 0)
				{
					srcTemp = src.get(index);
					dstTemp.m_type = srcTemp.m_type;
					dstTemp.m_listThumbRes = srcTemp.m_listThumbRes;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.m_datas = srcTemp.m_datas;

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

	@Override
	public void ReadNewFlagArr(Context context, SharedPreferences sp)
	{
		String temp = sp.getString(NEW_DOWNLOAD_FLAG, null);
		ResourceUtils.ParseNewFlagToArr(new_flag_arr, temp);
		ResourceUtils.RebuildNewFlagArr(ThemeResMgr2.getInstance().sync_GetSdcardRes(context, null), new_flag_arr);
	}
}

package cn.poco.resource;

import android.content.Context;
import android.content.SharedPreferences;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;

import cn.poco.adMaster.HomeAd;
import cn.poco.business.ChannelValue;
import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.system.SysConfig;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/10/9.
 */

public class MakeupComboResMgr2 extends BaseResMgr<MakeupRes, ArrayList<MakeupRes>>
{
	public final static int NEW_JSON_VER = MakeupResMgr2.NEW_JSON_VER;
	public final static int NEW_ORDER_JSON_VER = MakeupResMgr2.NEW_ORDER_JSON_VER;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().MAKEUP_PATH + "/makeup_combo.xxxx";

	protected final String ORDER_PATH = DownloadMgr.getInstance().MAKEUP_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().MAKEUP_PATH + "/makeup_combo_cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=makeup_combo_meirenxiangjiv2.6.2";// + "?random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=makeup_combo_88.8.8";

	public final static String NEW_DOWNLOAD_FLAG = "makeup_group2"; //以主题为组,记录在Preferences
	public ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态,主题id
	public final static String OLD_ID_FLAG = "makeup_group_id";

	private static MakeupComboResMgr2 sInstance;

	private MakeupComboResMgr2()
	{
	}

	public synchronized static MakeupComboResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new MakeupComboResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<MakeupRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<MakeupRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<MakeupRes> arr, MakeupRes item)
	{
		return arr.add(item);
	}

	/**
	 * @param dst 组合
	 * @param src 单个
	 */
	protected static void BuildComboResItem(ArrayList<MakeupRes> dst, ArrayList<MakeupRes> src)
	{
		if(dst != null && src != null)
		{
			MakeupRes temp;
			ArrayList<MakeupRes.MakeupData> resArr = new ArrayList<>();
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				temp = dst.get(i);
				if(temp.m_groupId != null)
				{
					temp.m_groupObj = new MakeupRes[temp.m_groupId.length];
					resArr.clear();
					MakeupRes temp2;
					for(int j = 0; j < temp.m_groupId.length; j++)
					{
						temp2 = ResourceUtils.GetItem(src, temp.m_groupId[j]);
						temp.m_groupObj[j] = temp2;
						if(temp2 != null && temp2.m_groupRes != null)
						{
							for(MakeupRes.MakeupData v : temp2.m_groupRes)
							{
								if(v != null)
								{
									resArr.add(v);
								}
							}
						}
					}
					temp.m_groupRes = resArr.toArray(new MakeupRes.MakeupData[resArr.size()]);
				}
			}
		}
	}

	protected ArrayList<MakeupRes> ReadLocalComboResArr(ArrayList<MakeupRes> unitArr)
	{
		ArrayList<MakeupRes> out = new ArrayList<>();
		MakeupRes res;

			    res = new MakeupRes();
				res.m_id = 121;
				res.m_name = "NM1";
				res.m_tjId = 1066693;
				res.m_thumb = R.drawable.__mak__70212015012920515994289183_120;
				res.m_thumb2 = R.drawable.__mak__28242015081409360149459659;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{18,19,45,33,102};
				res.m_groupAlphas = new int[]{60,50,60,60,40};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 150;
				res.m_name = "NM2";
				res.m_tjId = 1066694;
				res.m_thumb = R.drawable.__mak__24772015012920511580527159_120;
				res.m_thumb2 = R.drawable.__mak__34742015081409361616004193;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{153,156,55,40,164,160};
				res.m_groupAlphas = new int[]{60,60,40,60,50,40};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 141;
				res.m_name = "NM3";
				res.m_tjId = 1066695;
				res.m_thumb = R.drawable.__mak__45052015022614272571309833_120;
				res.m_thumb2 = R.drawable.__mak__869920150814093625850723;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{136,72,146,66,29,138,103};
				res.m_groupAlphas = new int[]{60,60,50,40,60,50,70};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 142;
				res.m_name = "PG1";
				res.m_tjId = 1066696;
				res.m_thumb = R.drawable.__mak__33552015022614322062353198_120;
				res.m_thumb2 = R.drawable.__mak__62462015081409363439959243;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{18,147,64,29,99};
				res.m_groupAlphas = new int[]{60,60,40,60,60};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 131;
				res.m_name = "PG2";
				res.m_tjId = 1066697;
				res.m_thumb = R.drawable.__mak__52882015012920531671899947_120;
				res.m_thumb2 = R.drawable.__mak__2495201508140936446569351;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{17,75,128,49,35,115};
				res.m_groupAlphas = new int[]{40,60,30,40,30,30};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 122;
				res.m_name = "SO1";
				res.m_tjId = 1066698;
				res.m_thumb = R.drawable.__mak__24132015022614280899180325_120;
				res.m_thumb2 = R.drawable.__mak__32482015081409365568929345;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{5,76,56,34,93,111};
				res.m_groupAlphas = new int[]{60,60,60,60,50,60};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 140;
				res.m_name = "SO2";
				res.m_tjId = 1066699;
				res.m_thumb = R.drawable.__mak__54402015012920523363371910_120;
				res.m_thumb2 = R.drawable.__mak__19562015081415095962038178;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{6,80,145,67,27,105};
				res.m_groupAlphas = new int[]{60,60,40,40,70,35};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 123;
				res.m_name = "EM1";
				res.m_tjId = 1066700;
				res.m_thumb = R.drawable.__mak__8631201502261431332506923_120;
				res.m_thumb2 = R.drawable.__mak__55102015081415093844997131;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{1,71,47,35,108};
				res.m_groupAlphas = new int[]{40,60,60,30,50};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 134;
				res.m_name = "EM2";
				res.m_tjId = 1066701;
				res.m_thumb = R.drawable.__mak__39952015022614282660257966_120;
				res.m_thumb2 = R.drawable.__mak__49622015081409373553569831;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{12,71,125,51,42,106};
				res.m_groupAlphas = new int[]{60,60,30,40,40,60};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 165;
				res.m_name = "EM3";
				res.m_tjId = 1066702;
				res.m_thumb = R.drawable.__mak__68162015012920533735459349_120;
				res.m_thumb2 = R.drawable.__mak__12662015081415094755777851;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{155,77,26,47,158,90,163};
				res.m_groupAlphas = new int[]{60,60,60,60,60,50,40};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2795;
				res.m_name = "N°1";
				res.m_tjId = 106014920;
				res.m_thumb = R.drawable.__mak__15154344820171011191311_3008_4027843732;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191319_3775_7876866613;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3093};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2796;
				res.m_name = "N°2";
				res.m_tjId = 106014921;
				res.m_thumb = R.drawable.__mak__15154344820171011191511_6634_9776546164;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191516_7698_8065223115;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3094};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2797;
				res.m_name = "N°3";
				res.m_tjId = 106014922;
				res.m_thumb = R.drawable.__mak__15154344820171011191610_4870_8911675692;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191615_4045_5884925475;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3095};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2798;
				res.m_name = "N°4";
				res.m_tjId = 106014923;
				res.m_thumb = R.drawable.__mak__15154344820171011191642_8052_3555239191;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191649_9601_9869101880;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3096};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2799;
				res.m_name = "N°5";
				res.m_tjId = 106014924;
				res.m_thumb = R.drawable.__mak__15154344820171011191738_6326_9891509502;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191743_2863_8135254406;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3097};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2800;
				res.m_name = "N°6";
				res.m_tjId = 106014925;
				res.m_thumb = R.drawable.__mak__15154344820171011191806_9797_1305455452;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191813_8744_7953476294;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3098};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2801;
				res.m_name = "N°7";
				res.m_tjId = 106014926;
				res.m_thumb = R.drawable.__mak__15154344820171011191833_2349_4353974163;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191837_7863_6628829435;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3099};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2802;
				res.m_name = "N°8";
				res.m_tjId = 106014927;
				res.m_thumb = R.drawable.__mak__15154344820171011191905_7226_8275673789;
				res.m_thumb2 = R.drawable.__mak__15154344820171011191910_2641_6425796313;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3100};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2803;
				res.m_name = "N°9";
				res.m_tjId = 106014928;
				res.m_thumb = R.drawable.__mak__15154344820171011192006_5410_3491456281;
				res.m_thumb2 = R.drawable.__mak__15154344820171011192012_9998_3572130465;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3101};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2804;
				res.m_name = "N°10";
				res.m_tjId = 106014929;
				res.m_thumb = R.drawable.__mak__15154344820171011192054_1917_2392981250;
				res.m_thumb2 = R.drawable.__mak__15154344820171011192100_5694_7205411544;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3102};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2805;
				res.m_name = "N°11";
				res.m_tjId = 106014930;
				res.m_thumb = R.drawable.__mak__15154344820171011192124_4111_1277644587;
				res.m_thumb2 = R.drawable.__mak__15154344820171011192129_5626_3937334470;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3103};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2806;
				res.m_name = "N°12";
				res.m_tjId = 106014832;
				res.m_thumb = R.drawable.__mak__15154344820171011192211_2718_8622704094;
				res.m_thumb2 = R.drawable.__mak__15154344820171011192216_5955_1493299430;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3091,3092,3104};
				res.m_groupAlphas = new int[]{60,60,100};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2811;
				res.m_name = "132";
				res.m_tjId = 106014974;
				res.m_thumb = R.drawable.__mak__15154344820171012181243_5088_6173416920;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181248_6942_1149309653;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3112};
				res.m_groupAlphas = new int[]{40,50,50,85};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2808;
				res.m_name = "189";
				res.m_tjId = 106014971;
				res.m_thumb = R.drawable.__mak__15154344820171012182737_2899_1002082014;
				res.m_thumb2 = R.drawable.__mak__15154344820171012182740_1400_4126364798;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3109};
				res.m_groupAlphas = new int[]{40,50,50,60};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2807;
				res.m_name = "184";
				res.m_tjId = 106014970;
				res.m_thumb = R.drawable.__mak__15154344820171012180621_6157_9359525019;
				res.m_thumb2 = R.drawable.__mak__15154344820171012180626_8145_3984647351;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3108};
				res.m_groupAlphas = new int[]{40,50,50,60};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2809;
				res.m_name = "193";
				res.m_tjId = 106014972;
				res.m_thumb = R.drawable.__mak__15154344820171012181123_5419_3885720207;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181127_3858_3299316159;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3110};
				res.m_groupAlphas = new int[]{40,50,50,60};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2810;
				res.m_name = "397";
				res.m_tjId = 106014973;
				res.m_thumb = R.drawable.__mak__15154344820171012181204_7598_2290453831;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181209_3462_1923542482;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3111};
				res.m_groupAlphas = new int[]{40,50,50,60};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2812;
				res.m_name = "172";
				res.m_tjId = 106014975;
				res.m_thumb = R.drawable.__mak__15154344820171012181331_7714_3058300857;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181335_7815_6449814066;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3113};
				res.m_groupAlphas = new int[]{40,50,50,85};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2813;
				res.m_name = "191";
				res.m_tjId = 106014976;
				res.m_thumb = R.drawable.__mak__15154344820171012181423_1497_3726904277;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181427_1051_2843218619;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3114};
				res.m_groupAlphas = new int[]{40,50,50,85};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2814;
				res.m_name = "105";
				res.m_tjId = 106014977;
				res.m_thumb = R.drawable.__mak__15154344820171012181509_3815_1393165975;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181514_4058_2253103312;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3115};
				res.m_groupAlphas = new int[]{40,50,50,85};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2815;
				res.m_name = "122";
				res.m_tjId = 106014978;
				res.m_thumb = R.drawable.__mak__15154344820171012181545_9983_3657595652;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181549_3799_6955976030;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3116};
				res.m_groupAlphas = new int[]{40,50,50,85};
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2816;
				res.m_name = "192";
				res.m_tjId = 106014979;
				res.m_thumb = R.drawable.__mak__15154344820171012181620_2563_8705437992;
				res.m_thumb2 = R.drawable.__mak__15154344820171012181629_6966_5902512606;
				res.m_groupAlpha = 0x50;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupId = new int[]{3105,3106,3107,3117};
				res.m_groupAlphas = new int[]{40,50,50,85};
				out.add(res);


		BuildComboResItem(out, unitArr);

		return out;
	}

	@Override
	protected ArrayList<MakeupRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		return ReadLocalComboResArr(MakeupResMgr2.getInstance().sync_GetLocalRes(context, null));
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<MakeupRes> arr)
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
						MakeupRes res;
						JSONObject jsonObj;
						JSONObject jsonObj2;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.get(i);
							jsonObj = new JSONObject();
							jsonObj.put("file_tracking_id", Integer.toHexString(res.m_id));
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
							if(res.m_thumb2 instanceof String)
							{
								jsonObj.put("thumb_inner", res.m_thumb2);
							}
							else
							{
								jsonObj.put("thumb_inner", "");
							}
							jsonObj.put("tracking_code", res.m_tjId);
							jsonObj.put("restype_id", 8);
							jsonObj2 = new JSONObject();
							{
								jsonObj2.put("alpha", res.m_groupAlpha);
								jsonObj2.put("combo_ids", ResourceUtils.MakeStr(res.m_groupId, 16));
								jsonObj2.put("combo_alpha", ResourceUtils.MakeStr(res.m_groupAlphas, 10));
							}
							jsonObj.put("res_arr", jsonObj2);
							jsonArr.put(jsonObj);
						}
					}
				}
				json.put("data", jsonArr);
			}

			fos = new FileOutputStream(SDCARD_PATH);
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
	public ArrayList<MakeupRes> sync_GetSdcardRes(Context context, DataFilter filter)
	{
		ArrayList<MakeupRes> arr = mSdcardResArr;
		ArrayList<MakeupRes> arr2 = super.sync_GetSdcardRes(context, filter);

		synchronized(SDCARD_MEM_LOCK)
		{
			if(arr != arr2 && arr2 != null)
			{
				ArrayList<MakeupRes> makeupArr = new ArrayList<>();
				ArrayList<MakeupRes> tempArr = MakeupResMgr2.getInstance().sync_GetLocalRes(context, null);
				if(tempArr != null)
				{
					makeupArr.addAll(tempArr);
				}
				tempArr = MakeupResMgr2.getInstance().sync_GetSdcardRes(context, null);
				if(tempArr != null)
				{
					makeupArr.addAll(tempArr);
				}
				tempArr = MakeupResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
				if(tempArr != null)
				{
					makeupArr.addAll(tempArr);
				}
				BuildComboResItem(arr2, makeupArr);
			}
		}

		return arr2;
	}

	@Override
	protected int GetCloudEventId()
	{
		return EventID.MAKEUP_COMBO_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected MakeupRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		return MakeupResMgr2.ReadResItem(jsonObj, isPath, true);
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
	public MakeupRes GetItem(ArrayList<MakeupRes> arr, int id)
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

	public void AddNewFlag(Context context, int themeId)
	{
		ResourceMgr.AddNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, themeId);
	}

	public void AddNewFlag(Context context, int[] themeIds)
	{
		if(themeIds != null && themeIds.length > 0)
		{
			if(ResourceUtils.AddIds(new_flag_arr, themeIds))
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

	public void DeleteNewFlag(Context context, int[] themeIds)
	{
		if(themeIds != null && themeIds.length > 0)
		{
			ResourceUtils.DeleteIds(new_flag_arr, themeIds);
			ResourceMgr.UpdateNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG);
		}
	}

	public void DeleteNewFlag(Context context, int themeId)
	{
		ResourceMgr.DeleteNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, themeId);
	}

	public boolean IsNewRes(int themeId)
	{
		boolean out = false;

		if(ResourceUtils.HasId(new_flag_arr, themeId) >= 0)
		{
			out = true;
		}

		return out;
	}

	/**
	 * 检查彩妆子素材是否在使用
	 *
	 * @param id
	 * @return
	 */
	protected boolean CheckSubMakeupResInUse(ArrayList<MakeupRes> sdcardArr, int id)
	{
		boolean out = false;

		if(sdcardArr != null)
		{
			MakeupRes temp;
			int len = sdcardArr.size();
			OK:
			for(int i = 0; i < len; i++)
			{
				temp = sdcardArr.get(i);
				if(temp.m_groupId != null)
				{
					for(int j = 0; j < temp.m_groupId.length; j++)
					{
						if(temp.m_groupId[j] == id)
						{
							out = true;
							break OK;
						}
					}
				}
			}
		}

		return out;
	}

	public ArrayList<MakeupRes> DeleteGroupRes(Context context, GroupRes res)
	{
		ArrayList<MakeupRes> out = new ArrayList<>();

		int[] ids = res.m_themeRes.m_makeupIDArr;
		ArrayList<MakeupRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<MakeupRes> arr = ResourceUtils.DeleteItems(sdcardArr, ids);
		if(arr != null && arr.size() > 0)
		{
			MakeupRes temp;
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				temp = arr.get(i);
				if(temp.m_type == BaseRes.TYPE_LOCAL_PATH)
				{
					temp.m_type = BaseRes.TYPE_NETWORK_URL;
				}
				//需要删除子素材
				if(temp.m_groupId != null)
				{
					int len2 = temp.m_groupId.length;
					int id;
					for(int j = 0; j < len2; j++)
					{
						id = temp.m_groupId[j];
						if(!CheckSubMakeupResInUse(sdcardArr, id))
						{
							MakeupRes temp2 = ResourceUtils.DeleteItem(MakeupResMgr2.getInstance().sync_GetSdcardRes(context, null), id);
							if(temp2 != null && temp2.m_type == BaseRes.TYPE_LOCAL_PATH)
							{
								temp2.m_type = BaseRes.TYPE_NETWORK_URL;
							}
						}
					}
				}
			}
			MakeupResMgr2.getInstance().sync_SaveSdcardRes(context, MakeupResMgr2.getInstance().sync_GetSdcardRes(context, null));

			ResourceUtils.DeleteId(GetOrderArr(), res.m_themeRes.m_id);
			DeleteNewFlag(context, res.m_themeRes.m_id);
			sync_SaveSdcardRes(context, sdcardArr);
			SaveOrderArr();
		}

		ThemeResMgr2.getInstance().ClearEmptyRes(context, res.m_themeRes);

		return out;
	}

	@Override
	protected void RebuildNetResArr(ArrayList<MakeupRes> dst, ArrayList<MakeupRes> src)
	{
		MakeupResMgr2.RebuildNetResArr2(dst, src);
	}

	@Override
	protected void sync_last_GetCloudRes(Context context, DataFilter filter, boolean justSave, ArrayList<MakeupRes> result)
	{
		super.sync_last_GetCloudRes(context, filter, justSave, result);

		//确保获取套装获取完成时,散件也获取成功
		MakeupResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
	}

	@Override
	protected void sync_ui_CloudResChange(ArrayList<MakeupRes> oldArr, ArrayList<MakeupRes> newArr)
	{
		super.sync_ui_CloudResChange(oldArr, newArr);

		if(newArr != null && newArr.size() > 0)
		{
			Context context = MyFramework2App.getInstance().getApplicationContext();
			ArrayList<MakeupRes> makeupArr = new ArrayList<>();
			ArrayList<MakeupRes> tempArr = MakeupResMgr2.getInstance().sync_GetLocalRes(context, null);
			if(tempArr != null)
			{
				makeupArr.addAll(tempArr);
			}
			tempArr = MakeupResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(tempArr != null)
			{
				makeupArr.addAll(tempArr);
			}
			tempArr = MakeupResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
			if(tempArr != null)
			{
				makeupArr.addAll(tempArr);
			}
			BuildComboResItem(newArr, makeupArr);
		}
	}

	@Override
	protected String GetOldIdFlag()
	{
		return OLD_ID_FLAG;
	}

	@Override
	public ArrayList<MakeupRes> sync_GetCloudCacheRes(Context context, DataFilter filter)
	{
		ArrayList<MakeupRes> arr = mCloudResArr;
		ArrayList<MakeupRes> arr2 = super.sync_GetCloudCacheRes(context, filter);

		synchronized(CLOUD_MEM_LOCK)
		{
			if(arr != arr2 && arr2 != null)
			{
				ArrayList<MakeupRes> makeupArr = new ArrayList<>();
				ArrayList<MakeupRes> tempArr = MakeupResMgr2.getInstance().sync_GetLocalRes(context, null);
				if(tempArr != null)
				{
					makeupArr.addAll(tempArr);
				}
				tempArr = MakeupResMgr2.getInstance().sync_GetSdcardRes(context, null);
				if(tempArr != null)
				{
					makeupArr.addAll(tempArr);
				}
				tempArr = MakeupResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
				if(tempArr != null)
				{
					makeupArr.addAll(tempArr);
				}
				BuildComboResItem(arr2, makeupArr);
			}
		}

		return arr2;
	}

	public ArrayList<GroupRes> GetNoDownloadGroupResArr(Context context)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<ThemeRes> downloadArr = ThemeResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(downloadArr != null)
		{
			ThemeRes temp;
			ArrayList<MakeupRes> resArr;
			GroupRes groupRes;
			boolean flag;
			int len = downloadArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if(temp.m_makeupIDArr != null && temp.m_makeupIDArr.length > 0 && !temp.m_isHide)
				{
					flag = false;
					resArr = GetResArr(temp.m_makeupIDArr);
                    int len2 = resArr.size();
                    if(len2 > 0 && resArr.size() != temp.m_makeupIDArr.length)
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

	public int GetNoDownloadCount(Context context)
	{
		return GetNoDownloadGroupResArr(context).size();
	}

	public ArrayList<GroupRes> GetDownloadedGroupResArr()
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<MakeupRes> orgArr = new ArrayList<>();
		Context context = MyFramework2App.getInstance().getApplicationContext();
		ArrayList<MakeupRes> tempArr = sync_GetSdcardRes(context, null);
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
		ArrayList<MakeupRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_makeupIDArr != null && temp.m_makeupIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_makeupIDArr);
				if(subArr.size() == temp.m_makeupIDArr.length)
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
			temp.m_makeupIDArr = new int[orgArr.size()];
			len = orgArr.size();
			for(int i = 0; i < len; i++)
			{
				temp.m_makeupIDArr[i] = orgArr.get(i).m_id;
			}

			groupRes = new GroupRes();
			groupRes.m_themeRes = temp;
			groupRes.m_ress = new ArrayList<>();
			groupRes.m_ress.addAll(orgArr);
			out.add(groupRes);
		}

		return out;
	}

	public ArrayList<MakeupGroupRes> GetGroupResArr()
	{
		ArrayList<MakeupGroupRes> out = new ArrayList<>();

		ArrayList<ThemeRes> resArr = ResourceUtils.BuildShowArr(ThemeResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null), ThemeResMgr2.getInstance().sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null), GetOrderArr());

		//商业素材上线时间判断
		{
			//兰蔻商业
			ThemeRes theme = ThemeResMgr2.getInstance().GetRes(1419);
			if(theme != null)
			{
				int index = findIndexById(resArr,1419);
				AbsAdRes tempAdRes = HomeAd.GetOneHomeRes(MyFramework2App.getInstance().getApplicationContext(), ChannelValue.AD82_2);
		        long curTime=System.currentTimeMillis();
		        if(tempAdRes != null) {
					if (curTime < tempAdRes.mBeginTime || curTime > tempAdRes.mEndTime) {
					if(index > -1)
					{
						resArr.remove(index);
					}
				}
				}
				else
				{
					if(index > -1)
					{
						resArr.remove(index);
					}
				}

			}



			//ysl商业
			theme = ThemeResMgr2.getInstance().GetRes(1414);
			if(theme != null)
			{
				int index = findIndexById(resArr,1414);
				  AbsAdRes tempAdRes = HomeAd.GetOneHomeRes(MyFramework2App.getInstance().getApplicationContext(), ChannelValue.AD85);
		     long curTime=System.currentTimeMillis();
			if(tempAdRes != null)
		    {
				 if (curTime < tempAdRes.mBeginTime || curTime > tempAdRes.mEndTime)
				 {
					if(index > -1)
					{
						resArr.remove(index);
					}
				 }
		    }
		    else
		    {
				if(index > -1)
				{
					resArr.remove(index);
				}
		     }
			}

		}

		ThemeRes temp;
		ArrayList<MakeupRes> tempArr;
		MakeupGroupRes group;
		int len = resArr.size();
		for(int i = 0; i < len; i++)
		{
			temp = resArr.get(i);
			if(temp.m_makeupIDArr != null && temp.m_makeupIDArr.length > 0)
			{
				tempArr = GetResArr(temp.m_makeupIDArr, true);
				if(tempArr.size() == temp.m_makeupIDArr.length)
				{
					group = new MakeupGroupRes();
					group.m_id = temp.m_id;
					group.m_name = temp.m_name;
					group.m_thumb = temp.m_makeupThumb;
					group.m_maskColor = temp.m_makeupColor;
					group.m_group = tempArr;
					out.add(group);
				}
			}
		}

		return out;
	}

	public int findIndexById(ArrayList<ThemeRes> arrs,int id)
	{
		int out = -1;
		for(int i = 0; i < arrs.size(); i++)
		{
			ThemeRes temp = arrs.get(i);
			if(temp != null && temp.m_id == id)
			{
				out = i;
			}
		}
		return out;
	}

	@Override
	public void ReadNewFlagArr(Context context, SharedPreferences sp)
	{
		String temp = sp.getString(NEW_DOWNLOAD_FLAG, null);
		ResourceUtils.ParseNewFlagToArr(new_flag_arr, temp);
		ResourceUtils.RebuildNewFlagArr(ThemeResMgr2.getInstance().sync_GetSdcardRes(context, null), new_flag_arr);
	}
}

package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.image.PocoCompositeOperator;
import cn.poco.system.SysConfig;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/10/9.
 */

public class MakeupResMgr2 extends BaseResMgr<MakeupRes, ArrayList<MakeupRes>>
{
	public final static int NEW_JSON_VER = 6;
	public final static int NEW_ORDER_JSON_VER = 6;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().MAKEUP_PATH + "/makeup.xxxx"; //资源集合

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().MAKEUP_PATH + "/makeup_cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=makeup_meirenxiangjiv2.6.2";// + "?random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=makeup_88.8.8";

	private static MakeupResMgr2 sInstance;

	private MakeupResMgr2()
	{
	}

	public synchronized static MakeupResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new MakeupResMgr2();
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

	//阿玛尼商业唇彩时间
	public static long s_startTime1 = 0l;//红管505
	public static long s_startTime2 = 0l;//红管511
	public static long s_startTime3 = 0l;//红管512
	public static long s_endTime = 0l;

	@Override
	protected ArrayList<MakeupRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<MakeupRes> out = new ArrayList<>();

		MakeupRes  res = new MakeupRes();
				res.m_id = 1;
				res.m_name = "BK01";
				res.m_tjId = 1065496;
				res.m_thumb = R.drawable.__mak__63902016122715370986419352_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				MakeupRes.MakeupData data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 32776;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{214.0f,59.0f,93.0f,26.0f,93.0f,59.0f,22.0f,60.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121110867336};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2;
				res.m_name = "BR01";
				res.m_tjId = 1065497;
				res.m_thumb = R.drawable.__mak__21462016122715373150386186_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32784;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{214.0f,59.0f,93.0f,26.0f,93.0f,59.0f,22.0f,60.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234120734933775};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3;
				res.m_name = "TN01";
				res.m_tjId = 1065498;
				res.m_thumb = R.drawable.__mak__87402016122715374415712310_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32792;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{214.0f,59.0f,93.0f,26.0f,93.0f,59.0f,22.0f,60.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234120924870234};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 4;
				res.m_name = "BK02";
				res.m_tjId = 1065499;
				res.m_thumb = R.drawable.__mak__89802016122715464361826770_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32800;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{212.0f,66.0f,88.0f,20.0f,87.0f,51.0f,24.0f,70.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234122955958442};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 5;
				res.m_name = "BR02";
				res.m_tjId = 1065500;
				res.m_thumb = R.drawable.__mak__33522016122715460281661798_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32808;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{212.0f,66.0f,88.0f,20.0f,87.0f,51.0f,24.0f,70.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234122702828389};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 6;
				res.m_name = "TN02";
				res.m_tjId = 1065501;
				res.m_thumb = R.drawable.__mak__68482016122715463225281876_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32816;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{212.0f,66.0f,88.0f,20.0f,87.0f,51.0f,24.0f,70.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234122847468216};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 7;
				res.m_name = "BK03";
				res.m_tjId = 1065502;
				res.m_thumb = R.drawable.__mak__75572016122715453964322876_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32824;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{216.0f,65.0f,77.0f,23.0f,77.0f,55.0f,14.0f,54.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234161651401845};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 8;
				res.m_name = "BR03";
				res.m_tjId = 1065503;
				res.m_thumb = R.drawable.__mak__88272016122715444050891663_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32832;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{216.0f,65.0f,77.0f,23.0f,77.0f,55.0f,14.0f,54.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234122315249886};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 9;
				res.m_name = "TN03";
				res.m_tjId = 1065504;
				res.m_thumb = R.drawable.__mak__4790201612271545171327886_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32840;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{216.0f,65.0f,77.0f,23.0f,77.0f,55.0f,14.0f,54.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234122444503411};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 10;
				res.m_name = "BK04";
				res.m_tjId = 1065505;
				res.m_thumb = R.drawable.__mak__31512016122715434723792619_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32848;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{207.0f,64.0f,90.0f,22.0f,85.0f,50.0f,20.0f,61.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234122210103202};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 11;
				res.m_name = "BR04";
				res.m_tjId = 1065506;
				res.m_thumb = R.drawable.__mak__272220161227154314392634_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32856;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{207.0f,64.0f,90.0f,22.0f,85.0f,50.0f,20.0f,61.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121913476201};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 12;
				res.m_name = "TN04";
				res.m_tjId = 1065507;
				res.m_thumb = R.drawable.__mak__87172016122715432947010823_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32864;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{207.0f,64.0f,90.0f,22.0f,85.0f,50.0f,20.0f,61.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234122102198548};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 16;
				res.m_name = "BK05";
				res.m_tjId = 1065511;
				res.m_thumb = R.drawable.__mak__36642016122715414456985412_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32896;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{210.0f,57.0f,77.0f,29.0f,77.0f,57.0f,21.0f,73.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121738542808};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 17;
				res.m_name = "BR05";
				res.m_tjId = 1065512;
				res.m_thumb = R.drawable.__mak__67342016122715422821401380_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 32904;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{210.0f,57.0f,77.0f,29.0f,77.0f,57.0f,21.0f,73.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121313365372};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 18;
				res.m_name = "TN05";
				res.m_tjId = 1065513;
				res.m_thumb = R.drawable.__mak__24722016122715425384834032_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32912;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{210.0f,57.0f,77.0f,29.0f,77.0f,57.0f,21.0f,73.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121553115330};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 136;
				res.m_name = "RB01";
				res.m_tjId = 1065715;
				res.m_thumb = R.drawable.__mak__4947201612280919457378339_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 33856;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{214.0f,59.0f,93.0f,26.0f,93.0f,59.0f,22.0f,60.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234120045807785};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 153;
				res.m_name = "RB02";
				res.m_tjId = 1067081;
				res.m_thumb = R.drawable.__mak__47072017011020112771386535_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 65;
				data.m_ex = 20;
				data.m_id = 33992;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{216.0f,57.0f,99.0f,27.0f,95.0f,57.0f,20.0f,61.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234104347234445};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 154;
				res.m_name = "RB03";
				res.m_tjId = 1065759;
				res.m_thumb = R.drawable.__mak__31542016122812110150137557_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 34000;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{214.0f,59.0f,93.0f,26.0f,93.0f,59.0f,22.0f,60.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234134625503774};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 155;
				res.m_name = "BK06";
				res.m_tjId = 1065769;
				res.m_thumb = R.drawable.__mak__27362016122812104421946594_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 34008;
				data.m_makeupType = MakeupType.EYEBROW_L.GetValue();
				data.m_pos = new float[]{210.0f,57.0f,95.0f,21.0f,95.0f,50.0f,23.0f,60.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234120313707540};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 19;
				res.m_name = "ND01";
				res.m_tjId = 1065514;
				res.m_thumb = R.drawable.__mak__9761201701102149558825417_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffed0b7;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 41;
				data.m_id = 32920;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234162122308022};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 128;
				res.m_name = "PK01";
				res.m_tjId = 1065609;
				res.m_thumb = R.drawable.__mak__68162016122809334741900216_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffdb4be;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 38;
				data.m_id = 33792;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234152227842900};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 23;
				res.m_name = "BR01";
				res.m_tjId = 1065518;
				res.m_thumb = R.drawable.__mak__5422201612280929495530980_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff8a595;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 38;
				data.m_id = 32952;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234165144655800};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 22;
				res.m_name = "PK02";
				res.m_tjId = 1065517;
				res.m_thumb = R.drawable.__mak__49862016122809310927665995_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffe0687c;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 38;
				data.m_id = 32944;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234164900221340};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 146;
				res.m_name = "ND02";
				res.m_tjId = 1065709;
				res.m_thumb = R.drawable.__mak__26022016122809331840974871_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffd9a17c;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 30;
				data.m_id = 33936;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234103401610744};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 145;
				res.m_name = "OR01";
				res.m_tjId = 1065708;
				res.m_thumb = R.drawable.__mak__49472016122809300910709595_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffe9684f;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 30;
				data.m_id = 33928;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234103302999130};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 21;
				res.m_name = "OR02";
				res.m_tjId = 1065516;
				res.m_thumb = R.drawable.__mak__31292017011021501164130283_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffc977d;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 38;
				data.m_id = 32936;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234163016489200};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 156;
				res.m_name = "WT01";
				res.m_tjId = 1065761;
				res.m_thumb = R.drawable.__mak__74162016122809305277829841_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffa68260;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 41;
				data.m_id = 34016;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234215125146522};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 147;
				res.m_name = "PG01";
				res.m_tjId = 1065710;
				res.m_thumb = R.drawable.__mak__26142016122809312637924514_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffad6a6e;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 38;
				data.m_id = 33944;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234113008133755};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 125;
				res.m_name = "BK01";
				res.m_tjId = 106011264;
				res.m_thumb = R.drawable.__mak__35082016122809314994087080_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xff51535d;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 30;
				data.m_ex = 30;
				data.m_id = 33768;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234183612411978};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 25;
				res.m_name = "BR02";
				res.m_tjId = 1065520;
				res.m_thumb = R.drawable.__mak__98632016122809323557436979_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffa77666;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 38;
				data.m_id = 32968;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__123416544549314};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 127;
				res.m_name = "GD01";
				res.m_tjId = 1065608;
				res.m_thumb = R.drawable.__mak__23312016122809325923321088_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffd6b475;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 30;
				data.m_ex = 38;
				data.m_id = 33784;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__12341838325607};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 20;
				res.m_name = "BR03";
				res.m_tjId = 1065515;
				res.m_thumb = R.drawable.__mak__59512016122809303767441253_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffb4725c;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 38;
				data.m_id = 32928;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__123416272123495};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 26;
				res.m_name = "GR01";
				res.m_tjId = 1065521;
				res.m_thumb = R.drawable.__mak__21442016122809333356555815_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xff928f8f;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 30;
				data.m_id = 32976;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234165541167226};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 27;
				res.m_name = "L-01";
				res.m_tjId = 1065522;
				res.m_thumb = R.drawable.__mak__92132017011020181383576680_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 70;
				data.m_ex = 20;
				data.m_id = 65968;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234165740621112};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 28;
				res.m_name = "L-02";
				res.m_tjId = 1065523;
				res.m_thumb = R.drawable.__mak__75002017011020183349163450_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 32992;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170013586196};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 29;
				res.m_name = "L-03";
				res.m_tjId = 1065524;
				res.m_thumb = R.drawable.__mak__89272017011020213498179983_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66000;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170427171906};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 30;
				res.m_name = "L-04";
				res.m_tjId = 1065525;
				res.m_thumb = R.drawable.__mak__1988201701102021513219291_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66016;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170614712272};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 31;
				res.m_name = "L-05";
				res.m_tjId = 1065526;
				res.m_thumb = R.drawable.__mak__89622017011116391925903739_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66032;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170855427869};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 32;
				res.m_name = "L-06";
				res.m_tjId = 1065527;
				res.m_thumb = R.drawable.__mak__14922017011116393320063586_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66048;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170943285559};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 33;
				res.m_name = "L-07";
				res.m_tjId = 1065528;
				res.m_thumb = R.drawable.__mak__62822017011116400123314088_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66064;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234171055188811};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 34;
				res.m_name = "L-08";
				res.m_tjId = 1065529;
				res.m_thumb = R.drawable.__mak__66262017011116402883611660_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66080;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234171145812465};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 35;
				res.m_name = "L-09";
				res.m_tjId = 1065530;
				res.m_thumb = R.drawable.__mak__90452017011020225883149747_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 30;
				data.m_ex = 20;
				data.m_id = 66096;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234171231112189};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 36;
				res.m_name = "L-10";
				res.m_tjId = 1065531;
				res.m_thumb = R.drawable.__mak__49902017011020232616438495_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66112;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234172133875910};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132224;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234172353947874};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 37;
				res.m_name = "L-11";
				res.m_tjId = 1065532;
				res.m_thumb = R.drawable.__mak__67792017011020240864832555_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66128;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234172942770173};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132256;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234172949700161};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 38;
				res.m_name = "L-12";
				res.m_tjId = 106011260;
				res.m_thumb = R.drawable.__mak__58702017011020244421862146_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66144;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234173340335154};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132288;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234173400224954};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 39;
				res.m_name = "L-13";
				res.m_tjId = 106011261;
				res.m_thumb = R.drawable.__mak__61002017011020250431277061_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66160;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234173604492745};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132320;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234173610443164};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 40;
				res.m_name = "L-14";
				res.m_tjId = 106011262;
				res.m_thumb = R.drawable.__mak__24612017011020252150335370_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66176;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234173926208211};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132352;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234173932451094};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 41;
				res.m_name = "L-15";
				res.m_tjId = 1065536;
				res.m_thumb = R.drawable.__mak__84202017011020254179720340_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 20;
				data.m_ex = 20;
				data.m_id = 66192;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234174338566528};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 20;
				data.m_ex = 20;
				data.m_id = 132384;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234174344677654};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 42;
				res.m_name = "L-16";
				res.m_tjId = 106011263;
				res.m_thumb = R.drawable.__mak__6690201701102026004783822_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 66208;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234174628398622};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 132416;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234174638781412};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 158;
				res.m_name = "L-17";
				res.m_tjId = 1065763;
				res.m_thumb = R.drawable.__mak__25232017011020262663355496_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 68064;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234215801589749};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 136128;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234215827975020};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 43;
				res.m_name = "BK01";
				res.m_tjId = 1065538;
				res.m_thumb = R.drawable.__mak__69272017011020580957526296_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66224;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.5f,10.0f,1.0f,0.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234142334397783,R.drawable.__mak__1234101432543454};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 44;
				res.m_name = "BR01";
				res.m_tjId = 1065539;
				res.m_thumb = R.drawable.__mak__60172017011020583791727954_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66240;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.5f,10.0f,1.0f,0.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__123414241265215,R.drawable.__mak__1234102254459260};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 45;
				res.m_name = "BK02";
				res.m_tjId = 1067082;
				res.m_thumb = R.drawable.__mak__31682017011020591169604625_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66256;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.75f,25.0f,1.375f,-37.5f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234142533459291,R.drawable.__mak__1234102706945512};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 46;
				res.m_name = "BR02";
				res.m_tjId = 1065541;
				res.m_thumb = R.drawable.__mak__10392017011020595551398295_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66272;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.75f,25.0f,1.375f,-37.5f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234142608846429,R.drawable.__mak__1234104442800223};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 47;
				res.m_name = "BK03";
				res.m_tjId = 1065542;
				res.m_thumb = R.drawable.__mak__86662017011116321210118881_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66288;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234142737395716,R.drawable.__mak__1234104645452443};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 48;
				res.m_name = "BR03";
				res.m_tjId = 1065543;
				res.m_thumb = R.drawable.__mak__85412017011116324515608121_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66304;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234142942519660,R.drawable.__mak__1234105036800093};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 49;
				res.m_name = "BK04";
				res.m_tjId = 1065544;
				res.m_thumb = R.drawable.__mak__81042017011116330425301394_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 66320;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234143145306918,R.drawable.__mak__1234105902462204};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 50;
				res.m_name = "BR04";
				res.m_tjId = 1065545;
				res.m_thumb = R.drawable.__mak__25132017011116332544175196_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66336;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234143338685907,R.drawable.__mak__1234110241536860};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 51;
				res.m_name = "BK05";
				res.m_tjId = 1065546;
				res.m_thumb = R.drawable.__mak__86972017011116340237172975_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 66352;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-35.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234144122608946,R.drawable.__mak__1234110445124683};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 52;
				res.m_name = "BR05";
				res.m_tjId = 1065547;
				res.m_thumb = R.drawable.__mak__82692017011116341724487264_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66368;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-35.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234144200229820,R.drawable.__mak__1234110704686726};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 53;
				res.m_name = "BK06";
				res.m_tjId = 1065548;
				res.m_thumb = R.drawable.__mak__77282017011021030530991458_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66384;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.75f,-5.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234144310693511,R.drawable.__mak__1234151205144939};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 54;
				res.m_name = "BR06";
				res.m_tjId = 1065549;
				res.m_thumb = R.drawable.__mak__10522017011021034232357836_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66400;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.75f,-5.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234144408937468,R.drawable.__mak__1234151522414624};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 55;
				res.m_name = "BK07";
				res.m_tjId = 1065550;
				res.m_thumb = R.drawable.__mak__2343201701111635095834975_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66416;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.5f,10.0f,1.0f,0.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234144722883792,R.drawable.__mak__1234144721761158};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132832;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170539941830};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 56;
				res.m_name = "BR07";
				res.m_tjId = 1065551;
				res.m_thumb = R.drawable.__mak__83172017011116352570487397_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66432;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234144835110495,R.drawable.__mak__1234144831814236};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132864;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170607412411};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 57;
				res.m_name = "BK08";
				res.m_tjId = 1065552;
				res.m_thumb = R.drawable.__mak__46552017011116354033395485_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66448;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.75f,25.0f,1.375f,-37.5f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234144959700306,R.drawable.__mak__1234144956773535};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132896;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170402875295};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 58;
				res.m_name = "BR08";
				res.m_tjId = 1065553;
				res.m_thumb = R.drawable.__mak__6803201701111635586145036_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66464;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.75f,25.0f,1.375f,-37.5f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234145050690818,R.drawable.__mak__123414504890688};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132928;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234170742290637};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 59;
				res.m_name = "BK09";
				res.m_tjId = 1065554;
				res.m_thumb = R.drawable.__mak__43772017011116361461222657_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66480;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{0.75f,25.0f,1.375f,-37.5f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234145158479968,R.drawable.__mak__1234145155659666};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132960;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234171024107680};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 60;
				res.m_name = "BR09";
				res.m_tjId = 1065555;
				res.m_thumb = R.drawable.__mak__84752017011116363135786966_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66496;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234145329430211,R.drawable.__mak__1234145326166036};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 132992;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__123417135465608};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 61;
				res.m_name = "BK10";
				res.m_tjId = 1065556;
				res.m_thumb = R.drawable.__mak__28232017011116364580544367_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66512;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234145505101234,R.drawable.__mak__123414550310933};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 133024;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__123417153681012};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 62;
				res.m_name = "BR10";
				res.m_tjId = 1065557;
				res.m_thumb = R.drawable.__mak__23852017011116370382692618_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66528;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234145558870839,R.drawable.__mak__1234145556572200};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 133056;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234171853460417};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 63;
				res.m_name = "BK11";
				res.m_tjId = 1065558;
				res.m_thumb = R.drawable.__mak__64762017011116372181253458_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 66544;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__123414573644786,R.drawable.__mak__1234145732993461};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 20;
				data.m_id = 133088;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234172325805099};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 64;
				res.m_name = "BR11";
				res.m_tjId = 1065559;
				res.m_thumb = R.drawable.__mak__22062017011116374564478822_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 66560;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234145839585817,R.drawable.__mak__1234145836822558};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 133120;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234172555581184};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 65;
				res.m_name = "BK12";
				res.m_tjId = 1065560;
				res.m_thumb = R.drawable.__mak__30382017011116380335439318_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 70;
				data.m_ex = 20;
				data.m_id = 66576;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234145943125336,R.drawable.__mak__1234145940966770};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 70;
				data.m_ex = 20;
				data.m_id = 133152;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121743816416};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 66;
				res.m_name = "BR12";
				res.m_tjId = 1065561;
				res.m_thumb = R.drawable.__mak__45592017011116382219905390_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 66592;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__123410415354002,R.drawable.__mak__1234103945792580};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 133184;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121829286323};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 67;
				res.m_name = "BK13";
				res.m_tjId = 1065562;
				res.m_thumb = R.drawable.__mak__3424201701111638351874215_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 66608;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-35.0f};
				data.m_pos = new float[]{213.0f,112.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234150123732014,R.drawable.__mak__1234150120625157};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 20;
				data.m_id = 133216;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{213.0f,112.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__1234121912909914};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 103;
				res.m_name = "ND01";
				res.m_tjId = 1065590;
				res.m_thumb = R.drawable.__mak__89722017011015154685175078_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff4bca7;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 70;
				data.m_ex = 1;
				data.m_id = 33592;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114035883664};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 101;
				res.m_name = "PK01";
				res.m_tjId = 1065585;
				res.m_thumb = R.drawable.__mak__50192017011015113661251070_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff4a0b6;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 70;
				data.m_ex = 41;
				data.m_id = 33576;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234113858319156};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 102;
				res.m_name = "PK02";
				res.m_tjId = 1065589;
				res.m_thumb = R.drawable.__mak__20422017011015151187340229_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff4afaf;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33584;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114019574129};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 105;
				res.m_name = "OR01";
				res.m_tjId = 1065587;
				res.m_thumb = R.drawable.__mak__62222017011015124141156451_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffc9e86;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 35;
				data.m_ex = 1;
				data.m_id = 33608;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234113939518516};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 106;
				res.m_name = "OR02";
				res.m_tjId = 1065588;
				res.m_thumb = R.drawable.__mak__5066201701101514375159157_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff38e86;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33616;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234113957687038};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 100;
				res.m_name = "PK03";
				res.m_tjId = 1065584;
				res.m_thumb = R.drawable.__mak__37292017011015091579421322_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff382a9;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33568;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__123411382898825};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 104;
				res.m_name = "OR03";
				res.m_tjId = 1065586;
				res.m_thumb = R.drawable.__mak__84362017011015120172017966_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff58c7d;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33600;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234113918853993};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 108;
				res.m_name = "RD01";
				res.m_tjId = 1065592;
				res.m_thumb = R.drawable.__mak__940920170110151624925089_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffb92939;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33632;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114400999517};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 109;
				res.m_name = "PR01";
				res.m_tjId = 1065593;
				res.m_thumb = R.drawable.__mak__4688201701101516597736948_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffd03a5f;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 45;
				data.m_ex = 1;
				data.m_id = 33640;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114450567805};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 163;
				res.m_name = "PR02";
				res.m_tjId = 1065768;
				res.m_thumb = R.drawable.__mak__87402017011015181466496882_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffcb4579;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 34;
				data.m_id = 34072;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234220844303731};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 160;
				res.m_name = "PR03";
				res.m_tjId = 106011243;
				res.m_thumb = R.drawable.__mak__95452017011015173389306220_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffe092b5;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 34;
				data.m_id = 34048;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__123422031238710};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 99;
				res.m_name = "PR04";
				res.m_tjId = 1065583;
				res.m_thumb = R.drawable.__mak__11922017011015082367623354_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff49dc6;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33560;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234113455554922};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 115;
				res.m_name = "PK04";
				res.m_tjId = 1065599;
				res.m_thumb = R.drawable.__mak__51352017011015203418809998_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff85f99;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 30;
				data.m_ex = 41;
				data.m_id = 33688;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234120130607528};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 113;
				res.m_name = "GR01";
				res.m_tjId = 1065597;
				res.m_thumb = R.drawable.__mak__92562017011015195735447274_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff9969f;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33672;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234115727599862};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 110;
				res.m_name = "GP01";
				res.m_tjId = 1065595;
				res.m_thumb = R.drawable.__mak__5407201701101519044704075_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff895b5;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33648;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__123411464595195};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 111;
				res.m_name = "GO01";
				res.m_tjId = 1065594;
				res.m_thumb = R.drawable.__mak__77132017011015183964766633_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffaaa93;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33656;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114520143056};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 112;
				res.m_name = "GO02";
				res.m_tjId = 1065596;
				res.m_thumb = R.drawable.__mak__25742017011015193482791497_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffeb8877;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33664;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114708411376};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 77;
				res.m_name = "BR01";
				res.m_tjId = 1065572;
				res.m_thumb = R.drawable.__mak__75012017040717292928133524_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33384;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234183014920390};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 78;
				res.m_name = "YL01";
				res.m_tjId = 1065573;
				res.m_thumb = R.drawable.__mak__97742017040717295947679192_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33392;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234183040995191};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 86;
				res.m_name = "YL02";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__87762017040717302130981481_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 1;
				data.m_id = 33456;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234183842227131};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 84;
				res.m_name = "BR02";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__44842017040717304169554551_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 30;
				data.m_ex = 1;
				data.m_id = 33440;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234175951214155};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 88;
				res.m_name = "GR01";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__59992017040717310288514553_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33472;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234175826249541};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 74;
				res.m_name = "BK01";
				res.m_tjId = 1065569;
				res.m_thumb = R.drawable.__mak__29892017040717312339015111_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33360;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234182605863815};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 79;
				res.m_name = "YL03";
				res.m_tjId = 1065574;
				res.m_thumb = R.drawable.__mak__96972017040717314791135765_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33400;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234183057704918};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 80;
				res.m_name = "BR03";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__85882017040717320149964567_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33408;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234183604853999};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 81;
				res.m_name = "BK02";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__6838201704071732198207362_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33416;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234183638820502};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 71;
				res.m_name = "BK03";
				res.m_tjId = 1065566;
				res.m_thumb = R.drawable.__mak__25392017040717323994086832_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33336;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234182426307722};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 72;
				res.m_name = "YL04";
				res.m_tjId = 1065567;
				res.m_thumb = R.drawable.__mak__92362017040717330270506854_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33344;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234182532725344};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 83;
				res.m_name = "PK01";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__95132017040717332832012138_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33432;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234180012977639};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 87;
				res.m_name = "OR01";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__72112017040717334869260653_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 1;
				data.m_id = 33464;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234175847742220};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 85;
				res.m_name = "YL05";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__28392017040717340039450580_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33448;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234175925273973};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 76;
				res.m_name = "GN01";
				res.m_tjId = 1065571;
				res.m_thumb = R.drawable.__mak__38972017040717343875889090_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33376;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234182946968998};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 75;
				res.m_name = "BL01";
				res.m_tjId = 1065570;
				res.m_thumb = R.drawable.__mak__34452017040717345678460286_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33368;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234182744331807};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 73;
				res.m_name = "BL02";
				res.m_tjId = 1065568;
				res.m_thumb = R.drawable.__mak__17622017040717351590337824_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33352;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234182549569630};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 82;
				res.m_name = "BK04";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__12542017040717352633842364_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 33424;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234183708448502};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 89;
				res.m_name = "GN02";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__85602017040717354553215247_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 30;
				data.m_ex = 1;
				data.m_id = 33480;
				data.m_makeupType = MakeupType.EYE_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234184046515639};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 91;
				res.m_name = "RD01";
				res.m_tjId = 1065576;
				res.m_thumb = R.drawable.__mak__18042017011019121655674125_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffdb2aa;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33496;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234153048947822};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 90;
				res.m_name = "PR01";
				res.m_tjId = 1065575;
				res.m_thumb = R.drawable.__mak__34142017011019144196942866_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff9a5bb;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33488;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114338438662};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 96;
				res.m_name = "PK01";
				res.m_tjId = 1065580;
				res.m_thumb = R.drawable.__mak__44992017011019135537608823_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffda8ad;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33536;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234153830816977};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 98;
				res.m_name = "OR01";
				res.m_tjId = 1065582;
				res.m_thumb = R.drawable.__mak__7690201701101914237534827_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffaba9f;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33552;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234153922332569};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 94;
				res.m_name = "RD02";
				res.m_tjId = 1065579;
				res.m_thumb = R.drawable.__mak__43442017011019150961705670_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff390a3;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33520;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234152942902055};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 93;
				res.m_name = "PK02";
				res.m_tjId = 1065578;
				res.m_thumb = R.drawable.__mak__23372017011019130733515450_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffffa39d;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33512;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114301980505};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 92;
				res.m_name = "PR02";
				res.m_tjId = 1065577;
				res.m_thumb = R.drawable.__mak__46562017011019124574480660_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff8c5d7;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33504;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234153143684489};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 164;
				res.m_name = "WT01";
				res.m_tjId = 0;
				res.m_thumb = R.drawable.__mak__20652017011019114617382192_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffffe2d2;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 34080;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234160652995165};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 138;
				res.m_name = "BR01";
				res.m_tjId = 106011259;
				res.m_thumb = R.drawable.__mak__12932017011019110279674268_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff5c099;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 33872;
				data.m_makeupType = MakeupType.CHEEK_L.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__1234114153324367};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2840;
				res.m_name = "Pearl";
				res.m_tjId = 1069908;
				res.m_thumb = R.drawable.__mak__85212017011215411377999357_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffef4db;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 75;
				data.m_ex = 0xfffef4db;
				data.m_id = 55488;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2841;
				res.m_name = "Peach";
				res.m_tjId = 1069909;
				res.m_thumb = R.drawable.__mak__439820170113152402147413_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffffe4e4;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 75;
				data.m_ex = 0xffffe4e4;
				data.m_id = 55496;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2842;
				res.m_name = "Light";
				res.m_tjId = 1069910;
				res.m_thumb = R.drawable.__mak__20482017011215442664844327_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffbd9a9;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 75;
				data.m_ex = 0xfffbd9a9;
				data.m_id = 55504;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2843;
				res.m_name = "Fresh";
				res.m_tjId = 1069911;
				res.m_thumb = R.drawable.__mak__47502017011215443937507286_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfff7c09c;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 75;
				data.m_ex = 0xfff7c09c;
				data.m_id = 55512;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2844;
				res.m_name = "Rose";
				res.m_tjId = 1069912;
				res.m_thumb = R.drawable.__mak__48902017011215445350326142_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xfffdbdb6;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 75;
				data.m_ex = 0xfffdbdb6;
				data.m_id = 55520;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2845;
				res.m_name = "Natural";
				res.m_tjId = 1069913;
				res.m_thumb = R.drawable.__mak__5811201701121545245613798_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xffd19e71;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 75;
				data.m_ex = 0xffd19e71;
				data.m_id = 55528;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2846;
				res.m_name = "Golden";
				res.m_tjId = 1069914;
				res.m_thumb = R.drawable.__mak__27902017011215453971951186_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xff98611a;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 80;
				data.m_ex = 0xff98611a;
				data.m_id = 55536;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 2849;
				res.m_name = "Tan";
				res.m_tjId = 1069993;
				res.m_thumb = R.drawable.__mak__30102017011215460857439177_120;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0xff54250b;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 80;
				data.m_ex = 0xff54250b;
				data.m_id = 55560;
				data.m_makeupType = MakeupType.FOUNDATION.GetValue();
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3091;
				res.m_name = "";
				res.m_tjId = 106014918;
				res.m_thumb = R.drawable.__mak__15154344820171011190024_9910_7401217894;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 114992;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,0.5f,15.0f};
				data.m_pos = new float[]{264.0f,136.0f,165.0f,74.0f,100.0f,111.0f};
				data.m_res = new Object[]{R.drawable.__mak__15154344820171011190038_4302_2761485869,R.drawable.__mak__15154344820171011190043_5643_2643709044};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 229984;
				data.m_makeupType = MakeupType.EYELASH_DOWN_L.GetValue();
				data.m_pos = new float[]{264.0f,136.0f,165.0f,141.0f,100.0f,111.0f};
				data.m_res = new Object[]{R.drawable.__mak__15154344820171011190048_1289_7920632649};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3092;
				res.m_name = "";
				res.m_tjId = 106014919;
				res.m_thumb = R.drawable.__mak__15154344820171011190824_4925_2579465805;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 115008;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{264.0f,136.0f,165.0f,74.0f,100.0f,111.0f};
				data.m_res = new Object[]{R.drawable.__mak__15154344820171011190840_4447_7218280544};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3093;
				res.m_name = "";
				res.m_tjId = 106014945;
				res.m_thumb = R.drawable.__mak__15154344820171012151422_3099_7150563312;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57512;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151457_4061_8141030832};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3094;
				res.m_name = "";
				res.m_tjId = 106014946;
				res.m_thumb = R.drawable.__mak__15154344820171012151521_1960_6230720295;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57520;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151531_4214_5626656143};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3095;
				res.m_name = "";
				res.m_tjId = 106014947;
				res.m_thumb = R.drawable.__mak__15154344820171012151552_6513_9491083260;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57528;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151603_8000_4168193981};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3096;
				res.m_name = "";
				res.m_tjId = 106014948;
				res.m_thumb = R.drawable.__mak__15154344820171012151629_4125_3143198929;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57536;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151637_9802_7963271294};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3097;
				res.m_name = "";
				res.m_tjId = 106014949;
				res.m_thumb = R.drawable.__mak__15154344820171012151655_8851_4695537145;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57544;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151712_8558_3407955196};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3098;
				res.m_name = "";
				res.m_tjId = 106014950;
				res.m_thumb = R.drawable.__mak__15154344820171012151737_7064_7119294408;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57552;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151742_4952_9265143742};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3099;
				res.m_name = "";
				res.m_tjId = 106014951;
				res.m_thumb = R.drawable.__mak__15154344820171012151812_7166_6662069339;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57560;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151818_7216_6573676890};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3100;
				res.m_name = "";
				res.m_tjId = 106014952;
				res.m_thumb = R.drawable.__mak__15154344820171012151834_2824_2579161671;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57568;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151845_6309_2714456720};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3101;
				res.m_name = "";
				res.m_tjId = 106014953;
				res.m_thumb = R.drawable.__mak__15154344820171012151924_2643_4666166818;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57576;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012151929_7185_2909937169};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3102;
				res.m_name = "";
				res.m_tjId = 106014954;
				res.m_thumb = R.drawable.__mak__15154344820171012151947_8087_2789301172;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57584;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012152006_5135_4860914944};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3103;
				res.m_name = "";
				res.m_tjId = 106014955;
				res.m_thumb = R.drawable.__mak__15154344820171012152021_4560_6702752264;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57592;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012152025_1675_3901327357};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3104;
				res.m_name = "";
				res.m_tjId = 106014956;
				res.m_thumb = R.drawable.__mak__15154344820171012152046_9139_9802561483;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57600;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012152049_8680_3652120027};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3105;
				res.m_name = "";
				res.m_tjId = 106014957;
				res.m_thumb = R.drawable.__mak__15154344820171012175450_1987_7153657550;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 40;
				data.m_ex = 38;
				data.m_id = 57608;
				data.m_makeupType = MakeupType.KOHL_L.GetValue();
				data.m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f,122.0f,127.0f};
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012175511_1257_4881805778};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3106;
				res.m_name = "";
				res.m_tjId = 106014958;
				res.m_thumb = R.drawable.__mak__15154344820171012175603_3781_2359166197;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 115232;
				data.m_makeupType = MakeupType.EYELASH_UP_L.GetValue();
				data.m_params = new float[]{1.0f,0.0f,1.0f,-20.0f};
				data.m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012175611_6193_4943620612,R.drawable.__mak__15154344820171012175615_5391_4056444030};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3107;
				res.m_name = "";
				res.m_tjId = 106014959;
				res.m_thumb = R.drawable.__mak__15154344820171012175923_5719_4178639459;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[2];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 115248;
				data.m_makeupType = MakeupType.EYELINER_UP_L.GetValue();
				data.m_pos = new float[]{206.0f,109.0f,121.0f,66.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012184817_4351_7963742591};
				res.m_groupRes[0] = data;
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 50;
				data.m_ex = 1;
				data.m_id = 230496;
				data.m_makeupType = MakeupType.EYELINER_DOWN_L.GetValue();
				data.m_pos = new float[]{206.0f,109.0f,122.0f,127.0f,49.0f,104.0f};
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012184827_4229_1434113274};
				res.m_groupRes[1] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3108;
				res.m_name = "";
				res.m_tjId = 106014960;
				res.m_thumb = R.drawable.__mak__15154344820171012180108_9278_6626391077;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57632;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180113_7898_4099802614};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3109;
				res.m_name = "";
				res.m_tjId = 106014961;
				res.m_thumb = R.drawable.__mak__15154344820171012180134_4437_8912117104;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57640;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180139_3738_1081430717};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3110;
				res.m_name = "";
				res.m_tjId = 106014962;
				res.m_thumb = R.drawable.__mak__15154344820171012180201_4469_2715655910;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57648;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180205_4946_1299022392};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3111;
				res.m_name = "";
				res.m_tjId = 106014963;
				res.m_thumb = R.drawable.__mak__15154344820171012180241_4331_7991235422;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57656;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180245_9097_5660149876};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3112;
				res.m_name = "";
				res.m_tjId = 106014964;
				res.m_thumb = R.drawable.__mak__15154344820171012180310_5602_3682569134;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57664;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180314_7153_2330801145};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3113;
				res.m_name = "";
				res.m_tjId = 106014965;
				res.m_thumb = R.drawable.__mak__15154344820171012180327_2068_1572040663;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57672;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180333_8903_3667491637};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3114;
				res.m_name = "";
				res.m_tjId = 106014966;
				res.m_thumb = R.drawable.__mak__15154344820171012180347_4322_3634975762;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57680;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180356_1138_7745356035};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3115;
				res.m_name = "";
				res.m_tjId = 106014967;
				res.m_thumb = R.drawable.__mak__15154344820171012180415_4506_4382831342;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57688;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180418_3878_4317069943};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3116;
				res.m_name = "";
				res.m_tjId = 106014968;
				res.m_thumb = R.drawable.__mak__15154344820171012180431_4179_5888721978;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57696;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180435_5924_4839460403};
				res.m_groupRes[0] = data;
				out.add(res);

				res = new MakeupRes();
				res.m_id = 3117;
				res.m_name = "";
				res.m_tjId = 106014969;
				res.m_thumb = R.drawable.__mak__15154344820171012180449_7001_2124141198;
				res.m_groupAlpha = 0x64;
				res.m_maskColor = 0x0;
				res.m_type = BaseRes.TYPE_LOCAL_RES;
				res.m_groupRes = new MakeupRes.MakeupData[1];
				data = new MakeupRes.MakeupData();
				data.m_defAlpha = 60;
				data.m_ex = 1;
				data.m_id = 57704;
				data.m_makeupType = MakeupType.LIP.GetValue();
				data.m_res = new Object[]{R.drawable.__mak__15154344820171012180455_4648_7625465622};
				res.m_groupRes[0] = data;
				out.add(res);

		return out;

					}

	@Override
	public boolean CheckIntact(MakeupRes res)
	{
		boolean out = false;

		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb) && res.m_groupRes != null && res.m_groupRes.length > 0 && res.m_groupRes[0] != null && (res.m_groupRes[0].m_makeupType == MakeupType.FOUNDATION.GetValue() || res.m_groupRes[0].m_res != null && res.m_groupRes[0].m_res.length > 0 && ResourceUtils.HasIntact(res.m_groupRes[0].m_res[0])))
			{
				out = true;
			}
		}

		return out;
	}

	protected static int GetTag(int type)
	{
		int out = -1;

		if(type == MakeupType.EYELINER_DOWN_L.GetValue() || type == MakeupType.EYELASH_DOWN_L.GetValue())
		{
			out = 2;
		}
		else if(type == MakeupType.EYELINER_UP_L.GetValue() || type == MakeupType.EYELASH_UP_L.GetValue())
		{
			out = 1;
		}
		else
		{
			out = 0;
		}

		return out;
	}

	protected static int GetClassify(int type)
	{
		int out = -1;

		if(type == MakeupType.EYEBROW_L.GetValue())
		{
			out = 1;
		}
		else if(type == MakeupType.KOHL_L.GetValue())
		{
			out = 2;
		}
		else if(type == MakeupType.EYELINER_DOWN_L.GetValue())
		{
			out = 3;
		}
		else if(type == MakeupType.EYELINER_UP_L.GetValue())
		{
			out = 3;
		}
		else if(type == MakeupType.EYELASH_DOWN_L.GetValue())
		{
			out = 4;
		}
		else if(type == MakeupType.EYELASH_UP_L.GetValue())
		{
			out = 4;
		}
		else if(type == MakeupType.EYE_L.GetValue())
		{
			out = 5;
		}
		else if(type == MakeupType.CHEEK_L.GetValue())
		{
			out = 6;
		}
		else if(type == MakeupType.LIP.GetValue())
		{
			out = 7;
		}
		else if(type == MakeupType.FOUNDATION.GetValue())
		{
			out = 9;
		}

		return out;
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
						JSONArray jsonArr2;
						JSONObject jsonObj2;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.get(i);
							if(res != null)
							{
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
								int makeupType = res.m_groupRes[0].m_makeupType;
								jsonObj.put("restype_id", GetClassify(makeupType));
								if(makeupType == MakeupType.FOUNDATION.GetValue())
								{
									jsonObj.put("alpha", res.m_groupRes[0].m_defAlpha);
									int color = 0xffffffff;
									if(res.m_groupRes[0].m_ex instanceof Integer)
									{
										color = (Integer)res.m_groupRes[0].m_ex;
									}
									color &= 0x00ffffff;
									jsonObj.put("color", Integer.toHexString(color));
								}
								else
								{
									jsonArr2 = new JSONArray();
									{
										if(res.m_groupRes != null)
										{
											MakeupRes.MakeupData data;
											int len2 = res.m_groupRes.length;
											for(int j = 0; j < len2; j++)
											{
												data = res.m_groupRes[j];
												if(data != null)
												{
													int len3 = data.m_res.length;
													for(int k = 0; k < len3; k++)
													{
														jsonObj2 = new JSONObject();

														if(data.m_res[k] != null)
														{
															jsonObj2.put("img", data.m_res[k]);
														}
														else
														{
															jsonObj2.put("img", "");
														}
														jsonObj2.put("alpha", data.m_defAlpha);
														jsonObj2.put("tag", GetTag(data.m_makeupType));
														jsonObj2.put("bks", ResourceUtils.MakeStr((float[])data.m_params));
														jsonObj2.put("points", ResourceUtils.MakeStr(data.m_pos));
														jsonObj2.put("blend", MakeComposite((Integer)data.m_ex));

														jsonArr2.put(jsonObj2);
													}
												}
											}
										}
									}
									jsonObj.put("res_arr", jsonArr2);
								}
								jsonArr.put(jsonObj);
							}
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
	protected int GetCloudEventId()
	{
		return EventID.MAKEUP_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected MakeupRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		return ReadResItem(jsonObj, isPath, false);
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

	protected static int ParseType(int classify, int tag)
	{
		int out = -1;

		switch(classify)
		{
			case 1:
				out = MakeupType.EYEBROW_L.GetValue();
				break;
			case 2:
				out = MakeupType.KOHL_L.GetValue();
				break;
			case 3:
				if(tag == 2)
				{
					out = MakeupType.EYELINER_DOWN_L.GetValue();
				}
				else
				{
					out = MakeupType.EYELINER_UP_L.GetValue();
				}
				break;
			case 4:
				if(tag == 2)
				{
					out = MakeupType.EYELASH_DOWN_L.GetValue();
				}
				else
				{
					out = MakeupType.EYELASH_UP_L.GetValue();
				}
				break;
			case 5:
				out = MakeupType.EYE_L.GetValue();
				break;
			case 6:
				out = MakeupType.CHEEK_L.GetValue();
				break;
			case 7:
				out = MakeupType.LIP.GetValue();
				break;
			case 9:
				out = MakeupType.FOUNDATION.GetValue();
				break;
		}
		return out;
	}

	protected static int ParseComposite(String composite)
	{
		if(composite != null)
		{
			if(composite.equals("diejia"))
			{
				return PocoCompositeOperator.OverlayCompositeOp;
			}
			else if(composite.equals("lvse"))
			{
				return PocoCompositeOperator.ScreenCompositeOp;
			}
			else if(composite.equals("ysjd"))
			{
				return PocoCompositeOperator.ColorDodgeCompositeOp;
			}
			else if(composite.equals("ysjs"))
			{
				return PocoCompositeOperator.ColorBurnCompositeOp;
			}
			else if(composite.equals("normal"))
			{
				return PocoCompositeOperator.NoCompositeOp;
			}
			else if(composite.equals("bianan"))
			{
				return PocoCompositeOperator.DarkenCompositeOp;
			}
			else if(composite.equals("qiangguang"))
			{
				return PocoCompositeOperator.HardLightCompositeOp;
			}
			else if(composite.equals("bianliang"))
			{
				return PocoCompositeOperator.LightenCompositeOp;
			}
			else if(composite.equals("zpdd"))
			{
				return PocoCompositeOperator.MultiplyCompositeOp;
			}
			else if(composite.equals("rouguang"))
			{
				return PocoCompositeOperator.SoftLightCompositeOp;
			}
			else if(composite.equals("xxjd"))
			{
				return PocoCompositeOperator.LinearDodgeCompositeOp;
			}
			else if(composite.equals("chazhi"))
			{
				return PocoCompositeOperator.DifferenceCompositeOp;
			}
			else if(composite.equals("paichu"))
			{
				return PocoCompositeOperator.ExclusionCompositeOp;
			}
			else if(composite.equals("xxguang"))
			{
				return PocoCompositeOperator.LinearLightCompositeOp;
			}
			else if(composite.equals("liangguang"))
			{
				return PocoCompositeOperator.VividLightCompositeOp;
			}
		}
		return PocoCompositeOperator.NoCompositeOp;
	}

	public static String MakeComposite(int composite)
	{
		String out = "";

		switch(composite)
		{
			case PocoCompositeOperator.ScreenCompositeOp:
				out = "lvse";
				break;
			case PocoCompositeOperator.ColorDodgeCompositeOp:
				out = "ysjd";
				break;
			case PocoCompositeOperator.ColorBurnCompositeOp:
				out = "ysjs";
				break;
			case PocoCompositeOperator.DarkenCompositeOp:
				out = "bianan";
				break;
			case PocoCompositeOperator.HardLightCompositeOp:
				out = "qiangguang";
				break;
			case PocoCompositeOperator.LightenCompositeOp:
				out = "bianliang";
				break;
			case PocoCompositeOperator.MultiplyCompositeOp:
				out = "zpdd";
				break;
			case PocoCompositeOperator.SoftLightCompositeOp:
				out = "rouguang";
				break;
			case PocoCompositeOperator.LinearDodgeCompositeOp:
				out = "xxjd";
				break;
			case PocoCompositeOperator.DifferenceCompositeOp:
				out = "chazhi";
				break;
			case PocoCompositeOperator.ExclusionCompositeOp:
				out = "paichu";
				break;
			case PocoCompositeOperator.LinearLightCompositeOp:
				out = "xxguang";
				break;
			case PocoCompositeOperator.OverlayCompositeOp:
				out = "diejia";
				break;
			case PocoCompositeOperator.VividLightCompositeOp:
				out = "liangguang";
				break;
			case PocoCompositeOperator.NoCompositeOp:
			default:
				out = "normal";
				break;
		}

		return out;
	}

	public static MakeupRes ReadResItem(JSONObject jsonObj, boolean isPath, boolean isCombo)
	{
		MakeupRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new MakeupRes();
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
					out.m_id = (int)Long.parseLong(temp, 16);
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
				if(isCombo)
				{
					if(isPath)
					{
						out.m_thumb2 = jsonObj.getString("thumb_inner");
					}
					else
					{
						out.url_thumb2 = jsonObj.getString("thumb_inner");
					}
				}
				if(jsonObj.has("bkcolor"))
				{
					temp = jsonObj.getString("bkcolor");
					if(TextUtils.isEmpty(temp))
					{
						//out.m_maskColor = 0xffffffff;
					}
					else
					{
						out.m_maskColor = 0xff000000 | (int)Long.parseLong(temp.replace("#", "").trim(), 16);
					}
				}
				out.m_tjId = jsonObj.getInt("tracking_code");
				int classify = 0;
				temp = jsonObj.getString("restype_id");
				if(temp != null && temp.length() > 0)
				{
					classify = (int)Long.parseLong(temp);
				}
				if(classify == 9 && !isCombo)
				{
					out.m_groupRes = new MakeupRes.MakeupData[1];
					MakeupRes.MakeupData item = new MakeupRes.MakeupData();
					item.m_makeupType = ParseType(classify, 0);
					item.m_id = (out.m_id | 0x1000) << 3;
					item.m_defAlpha = jsonObj.getInt("alpha");
					temp = jsonObj.getString("color");
					if(temp != null && temp.length() > 0)
					{
						item.m_ex = 0xff000000 | (int)Long.parseLong(temp.replace("#", "").trim(), 16);
					}
					else
					{
						item.m_ex = 0xffffffff;
					}
					out.m_groupRes[0] = item;
				}
				else if(classify != 8 && !isCombo)
				{
					JSONArray jsonArr = jsonObj.getJSONArray("res_arr");
					if(jsonArr != null)
					{
						Object obj;
						JSONObject jsonObj2;
						int len = jsonArr.length();
						MakeupRes.MakeupData item;
						SparseArray<ArrayList<MakeupRes.MakeupData>> map = new SparseArray();
						ArrayList<MakeupRes.MakeupData> arr;
						for(int i = 0; i < len; i++)
						{
							obj = jsonArr.get(i);
							if(obj instanceof JSONObject)
							{
								jsonObj2 = (JSONObject)obj;
								int tag = jsonObj2.getInt("tag");
								arr = map.get(tag);
								if(arr == null)
								{
									arr = new ArrayList<>();
									map.put(tag, arr);
								}
								item = new MakeupRes.MakeupData();
								arr.add(item);

								if(isPath)
								{
									item.m_res = new Object[1];
									item.m_res[0] = jsonObj2.getString("img");
								}
								else
								{
									item.url_res = new String[1];
									item.url_res[0] = jsonObj2.getString("img");
								}
								item.m_defAlpha = jsonObj2.getInt("alpha");
								item.m_makeupType = ParseType(classify, tag);
								item.m_params = ResourceUtils.ParseFloatArr(jsonObj2.getString("bks"));
								item.m_pos = ResourceUtils.ParseFloatArr(jsonObj2.getString("points"));
								item.m_ex = ParseComposite(jsonObj2.getString("blend"));
								item.m_id = (out.m_id | 0x1000) << (tag + 3);
							}
						}
						//合并素材
						len = map.size();
						out.m_groupRes = new MakeupRes.MakeupData[len];
						for(int i = 0; i < len; i++)
						{
							item = null;
							arr = map.valueAt(i);
							if(arr != null)
							{
								int len2 = arr.size();
								if(len2 > 0)
								{
									Object[] objs = null;
									String[] strs = null;
									if(isPath)
									{
										objs = new Object[len2];
									}
									else
									{
										strs = new String[len2];
									}
									for(int j = 0; j < len2; j++)
									{
										item = arr.get(j);
										if(isPath)
										{
											objs[j] = item.m_res[0];
										}
										else
										{
											strs[j] = item.url_res[0];
										}
									}
									if(isPath)
									{
										item.m_res = objs;
									}
									else
									{
										item.url_res = strs;
									}
								}
							}
							if(item != null)
							{
								out.m_groupRes[i] = item;
							}
						}
					}
				}
				else if(classify == 8 && isCombo)
				{
					JSONObject jsonObj2 = jsonObj.getJSONObject("res_arr");
					if(jsonObj2 != null)
					{
						if(jsonObj2.has("alpha"))
						{
							try
							{
								temp = jsonObj2.getString("alpha");
								if(temp != null && temp.length() > 0)
								{
									out.m_groupAlpha = (int)Long.parseLong(temp);
								}
							}
							catch(Throwable e)
							{
							}
						}
						if(jsonObj2.has("combo_ids"))
						{
							out.m_groupId = ResourceUtils.ParseIds(jsonObj2.getString("combo_ids"), 16);
						}
						if(jsonObj2.has("combo_alpha"))
						{
							out.m_groupAlphas = ResourceUtils.ParseIds(jsonObj2.getString("combo_alpha"), 10);
							if(out.m_groupId != null)
							{
								if(out.m_groupAlphas == null || out.m_groupAlphas.length != out.m_groupId.length)
								{
									out.m_groupAlphas = new int[out.m_groupId.length];
									for(int i = 0; i < out.m_groupId.length; i++)
									{
										out.m_groupAlphas[i] = 100;
									}
								}
							}
							else
							{
								out.m_groupAlphas = null;
							}
						}
					}
				}
				else
				{
					out = null;
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
	protected void RebuildNetResArr(ArrayList<MakeupRes> dst, ArrayList<MakeupRes> src)
	{
		RebuildNetResArr2(dst, src);
	}

	public static void RebuildNetResArr2(ArrayList<MakeupRes> dst, ArrayList<MakeupRes> src)
	{
		if(dst != null && src != null)
		{
			MakeupRes srcTemp;
			MakeupRes dstTemp;
			Class cls = MakeupRes.class;
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
					dstTemp.m_thumb2 = srcTemp.m_thumb2;
					if(srcTemp.m_groupRes != null && dstTemp.m_groupRes != null)
					{
						MakeupRes.MakeupData dstData;
						MakeupRes.MakeupData srcData = null;
						for(int k = 0; k < dstTemp.m_groupRes.length; k++)
						{
							dstData = dstTemp.m_groupRes[k];
							for(int j = 0; j < srcTemp.m_groupRes.length; j++)
							{
								srcData = srcTemp.m_groupRes[j];
								if(dstData.m_id == srcData.m_id)
								{
									break;
								}
							}
							if(srcData != null)
							{
								dstData.m_res = srcData.m_res;
								dstData.m_type = srcData.m_type;
								dstData.m_thumb = srcData.m_thumb;
							}
						}
					}

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

	protected static void FilterMakeupResArr(ArrayList<MakeupRes> dst, ArrayList<MakeupRes> src, int types)
	{
		if(dst != null && src != null)
		{
			MakeupRes temp;
			int len = src.size();
			for(int i = 0; i < len; i++)
			{
				temp = src.get(i);
				if(temp != null && (temp.m_groupRes[0].m_makeupType & types) != 0)
				{
					dst.add(temp);
				}
			}
		}
	}

	public ArrayList<MakeupRes> GetResArr(int types)
	{
		ArrayList<MakeupRes> out = new ArrayList<>();

		Context context = MyFramework2App.getInstance().getApplicationContext();
		FilterMakeupResArr(out, sync_GetLocalRes(context, null), types);
		FilterMakeupResArr(out, sync_GetSdcardRes(context, null), types);

		return out;
	}

	/**
	 * 没有下载
	 */
	public ArrayList<MakeupRes> GetLocalResArr(int types, boolean hasBusiness)
	{
		ArrayList<MakeupRes> out = new ArrayList<>();

		FilterMakeupResArr(out, sync_GetLocalRes(MyFramework2App.getInstance().getApplicationContext(), null), types);

		if(!hasBusiness)
		{
			//TODO: 移除商业的id
			int[] hideress = {
					3091,3092,3093,3094,3095,3096,3097,3098,3099,3100,3101,3102,3103,3104,
					3105,3106,3107,3108,3109,3110,3111,3112,3113,3114,3115
					,3116,3117
					};
			ResourceUtils.DeleteItems(out, hideress);
		}

		return out;
	}
}

package cn.poco.resource;

import android.content.Context;
import android.content.SharedPreferences;
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
import cn.poco.system.SysConfig;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/9/28.
 */

public class DecorateResMgr2 extends BaseResMgr<DecorateRes, SparseArray<DecorateRes>>
{
	public final static int NEW_JSON_VER = 3;
	public final static int NEW_ORDER_JSON_VER = 3;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().DECORATE_PATH + "/decorate.xxxx"; //资源集合

	protected final String ORDER_PATH = DownloadMgr.getInstance().DECORATE_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().DECORATE_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=deco_meirenxiangjiv2.2.0";// + "?random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=deco_88.8.8";

	public final static String NEW_DOWNLOAD_FLAG = "decorate"; //记录在Preferences
	public final ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "decorate_id";

	private static DecorateResMgr2 sInstance;

	private DecorateResMgr2()
	{
	}

	public synchronized static DecorateResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new DecorateResMgr2();
		}
		return sInstance;
	}


	@Override
	public int GetResArrSize(SparseArray<DecorateRes> arr)
	{
		return arr.size();
	}

	@Override
	public SparseArray<DecorateRes> MakeResArrObj()
	{
		return new SparseArray<>();
	}

	@Override
	public boolean ResArrAddItem(SparseArray<DecorateRes> arr, DecorateRes item)
	{
		if(arr != null && item != null)
		{
			arr.put(item.m_id, item);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected SparseArray<DecorateRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		SparseArray<DecorateRes> out = new SparseArray<>();

		DecorateRes res;

		res = new DecorateRes();
		res.m_id = 224;
		res.m_name = "love";
		res.m_res = R.drawable.__dec__1234112410110652;
		res.m_thumb = R.drawable.__dec__77822015021111240043517071_120;
		res.m_tjId = 1065988;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 225;
		res.m_name = "cute";
		res.m_res = R.drawable.__dec__123418143878069;
		res.m_thumb = R.drawable.__dec__13952015012618144248990433_120;
		res.m_tjId = 1065422;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 226;
		res.m_name = "Happy";
		res.m_res = R.drawable.__dec__1234181508509389;
		res.m_thumb = R.drawable.__dec__93602015012618150222897649_120;
		res.m_tjId = 1065423;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 227;
		res.m_name = "五角星";
		res.m_res = R.drawable.__dec__1234181535240506;
		res.m_thumb = R.drawable.__dec__26282015012618153576210178_120;
		res.m_tjId = 1064690;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 228;
		res.m_name = "爱心";
		res.m_res = R.drawable.__dec__1234181604394846;
		res.m_thumb = R.drawable.__dec__15332015012618155242566087_120;
		res.m_tjId = 1065424;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 229;
		res.m_name = "闪电";
		res.m_res = R.drawable.__dec__1234181630614202;
		res.m_thumb = R.drawable.__dec__31322015012618162249476764_120;
		res.m_tjId = 1065425;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 230;
		res.m_name = "砖石";
		res.m_res = R.drawable.__dec__1234181653953326;
		res.m_thumb = R.drawable.__dec__97962015012618164630795373_120;
		res.m_tjId = 0;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 231;
		res.m_name = "kiss";
		res.m_res = R.drawable.__dec__1234181802734825;
		res.m_thumb = R.drawable.__dec__87402015012618175761856523_120;
		res.m_tjId = 1065426;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 232;
		res.m_name = "喵喵";
		res.m_res = R.drawable.__dec__1234181818912497;
		res.m_thumb = R.drawable.__dec__6355201501261818147050076_120;
		res.m_tjId = 1065427;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 233;
		res.m_name = "帽子";
		res.m_res = R.drawable.__dec__1234181836252196;
		res.m_thumb = R.drawable.__dec__13842015012618183120317740_120;
		res.m_tjId = 0;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 234;
		res.m_name = "Hello";
		res.m_res = R.drawable.__dec__1234181904466927;
		res.m_thumb = R.drawable.__dec__87772015012618185824764511_120;
		res.m_tjId = 1065428;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 235;
		res.m_name = "心";
		res.m_res = R.drawable.__dec__1234181925754800;
		res.m_thumb = R.drawable.__dec__83782015012618192149260278_120;
		res.m_tjId = 1065429;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 236;
		res.m_name = "Thank you";
		res.m_res = R.drawable.__dec__1234182009714308;
		res.m_thumb = R.drawable.__dec__75092015012618200045535175_120;
		res.m_tjId = 1065430;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 237;
		res.m_name = "yummy";
		res.m_res = R.drawable.__dec__1234182034528293;
		res.m_thumb = R.drawable.__dec__20492015012618202880098034_120;
		res.m_tjId = 1065431;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 238;
		res.m_name = "心心";
		res.m_res = R.drawable.__dec__1234182058910974;
		res.m_thumb = R.drawable.__dec__16592015012618205298862343_120;
		res.m_tjId = 1065432;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 239;
		res.m_name = "桃心";
		res.m_res = R.drawable.__dec__1234182122884637;
		res.m_thumb = R.drawable.__dec__96512015012618211490364832_120;
		res.m_tjId = 1065433;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 240;
		res.m_name = "满天星";
		res.m_res = R.drawable.__dec__1234182154329026;
		res.m_thumb = R.drawable.__dec__74302015012618214570944787_120;
		res.m_tjId = 1065434;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 241;
		res.m_name = "花边爱心";
		res.m_res = R.drawable.__dec__1234141844337143;
		res.m_thumb = R.drawable.__dec__7329201501261822385763441_120;
		res.m_tjId = 1065435;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 242;
		res.m_name = "stake";
		res.m_res = R.drawable.__dec__1234182325158447;
		res.m_thumb = R.drawable.__dec__6379201501261823131053145_120;
		res.m_tjId = 1065436;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 243;
		res.m_name = "高跟鞋";
		res.m_res = R.drawable.__dec__1234112334325296;
		res.m_thumb = R.drawable.__dec__91032015021111232593283122_120;
		res.m_tjId = 1065437;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 244;
		res.m_name = "红唇";
		res.m_res = R.drawable.__dec__1234182455424380;
		res.m_thumb = R.drawable.__dec__43292015012618245096261969_120;
		res.m_tjId = 1065438;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 245;
		res.m_name = "眼睫毛";
		res.m_res = R.drawable.__dec__1234182555953471;
		res.m_thumb = R.drawable.__dec__75932015012618255231307244_120;
		res.m_tjId = 1065439;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 246;
		res.m_name = "手提包";
		res.m_res = R.drawable.__dec__1234103935602160;
		res.m_thumb = R.drawable.__dec__33772015012710393018717873_120;
		res.m_tjId = 1065440;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 247;
		res.m_name = "化妆盒";
		res.m_res = R.drawable.__dec__1234103957482002;
		res.m_thumb = R.drawable.__dec__94522015012710395096432988_120;
		res.m_tjId = 1065441;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 248;
		res.m_name = "刷子";
		res.m_res = R.drawable.__dec__1234104017268326;
		res.m_thumb = R.drawable.__dec__83842015012710401372030965_120;
		res.m_tjId = 1065442;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 249;
		res.m_name = "鞋子";
		res.m_res = R.drawable.__dec__1234104043832057;
		res.m_thumb = R.drawable.__dec__50032015012710405073884143_120;
		res.m_tjId = 1065443;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 250;
		res.m_name = "嘴巴";
		res.m_res = R.drawable.__dec__1234104113664009;
		res.m_thumb = R.drawable.__dec__13202015012710410893341039_120;
		res.m_tjId = 1064650;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 251;
		res.m_name = "蝴蝶结";
		res.m_res = R.drawable.__dec__1234104226574020;
		res.m_thumb = R.drawable.__dec__92592015012710422259564886_120;
		res.m_tjId = 1065444;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 252;
		res.m_name = "戒子";
		res.m_res = R.drawable.__dec__1234104258681328;
		res.m_thumb = R.drawable.__dec__82072015012710424566015718_120;
		res.m_tjId = 1065445;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 253;
		res.m_name = "墨镜";
		res.m_res = R.drawable.__dec__1234104823377011;
		res.m_thumb = R.drawable.__dec__82362015012710483251032250_120;
		res.m_tjId = 1065446;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 254;
		res.m_name = "蓝色墨镜";
		res.m_res = R.drawable.__dec__123410504137916;
		res.m_thumb = R.drawable.__dec__18992015012710503722393777_120;
		res.m_tjId = 1065447;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 255;
		res.m_name = "wow";
		res.m_res = R.drawable.__dec__12341051367221;
		res.m_thumb = R.drawable.__dec__49702015012710513175755663_120;
		res.m_tjId = 1064721;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 256;
		res.m_name = "New";
		res.m_res = R.drawable.__dec__1234105235944232;
		res.m_thumb = R.drawable.__dec__94762015012710522646533638_120;
		res.m_tjId = 1065448;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 257;
		res.m_name = "Chu";
		res.m_res = R.drawable.__dec__1234105257555221;
		res.m_thumb = R.drawable.__dec__85212015012710525319776098_120;
		res.m_tjId = 1065449;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 258;
		res.m_name = "Pink";
		res.m_res = R.drawable.__dec__1234105315461659;
		res.m_thumb = R.drawable.__dec__35692015012710531076220762_120;
		res.m_tjId = 1065450;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 259;
		res.m_name = "FASHIONISTA";
		res.m_res = R.drawable.__dec__1234105546129885;
		res.m_thumb = R.drawable.__dec__46062015012710554159261472_120;
		res.m_tjId = 1065451;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 260;
		res.m_name = "Make up";
		res.m_res = R.drawable.__dec__123410560812889;
		res.m_thumb = R.drawable.__dec__93102015012710560260529974_120;
		res.m_tjId = 1065452;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 261;
		res.m_name = "cooidnate";
		res.m_res = R.drawable.__dec__1234105628588848;
		res.m_thumb = R.drawable.__dec__44392015012710562034908125_120;
		res.m_tjId = 1065453;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 262;
		res.m_name = "Look at me";
		res.m_res = R.drawable.__dec__1234105702310909;
		res.m_thumb = R.drawable.__dec__28382015012710565625626616_120;
		res.m_tjId = 1065454;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 263;
		res.m_name = "Fayorite style";
		res.m_res = R.drawable.__dec__1234110722523504;
		res.m_thumb = R.drawable.__dec__59902015012711071850169553_120;
		res.m_tjId = 1065455;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 264;
		res.m_name = "父本母本";
		res.m_res = R.drawable.__dec__1234110742936619;
		res.m_thumb = R.drawable.__dec__54822015012711073816845868_120;
		res.m_tjId = 1065456;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 265;
		res.m_name = "兔子";
		res.m_res = R.drawable.__dec__1234110851812994;
		res.m_thumb = R.drawable.__dec__35902015012711084263895463_120;
		res.m_tjId = 1065457;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 266;
		res.m_name = "鸟";
		res.m_res = R.drawable.__dec__1234110910806657;
		res.m_thumb = R.drawable.__dec__90622015012711090749652359_120;
		res.m_tjId = 1065458;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 267;
		res.m_name = "TTA";
		res.m_res = R.drawable.__dec__1234111010749316;
		res.m_thumb = R.drawable.__dec__33232015012711100718677859_120;
		res.m_tjId = 1065459;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 268;
		res.m_name = "Daris";
		res.m_res = R.drawable.__dec__1234112246755011;
		res.m_thumb = R.drawable.__dec__98652015021111221643128847_120;
		res.m_tjId = 1065460;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 269;
		res.m_name = "25uec";
		res.m_res = R.drawable.__dec__1234121252357330;
		res.m_thumb = R.drawable.__dec__60672015012712130031457574_120;
		res.m_tjId = 1065461;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 270;
		res.m_name = "soct";
		res.m_res = R.drawable.__dec__1234121322336191;
		res.m_thumb = R.drawable.__dec__36872015012712131611526650_120;
		res.m_tjId = 1065462;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 271;
		res.m_name = "letter";
		res.m_res = R.drawable.__dec__1234121348876755;
		res.m_thumb = R.drawable.__dec__59702015012712134512260585_120;
		res.m_tjId = 1065463;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 272;
		res.m_name = "coffee";
		res.m_res = R.drawable.__dec__123412141018047;
		res.m_thumb = R.drawable.__dec__62252015012712140649491588_120;
		res.m_tjId = 1065464;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 273;
		res.m_name = "茶壶";
		res.m_res = R.drawable.__dec__1234121428541719;
		res.m_thumb = R.drawable.__dec__90022015012712142513223771_120;
		res.m_tjId = 1065465;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 274;
		res.m_name = "Tea Time";
		res.m_res = R.drawable.__dec__1234121517110337;
		res.m_thumb = R.drawable.__dec__88782015012712151180400469_120;
		res.m_tjId = 1065466;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 275;
		res.m_name = "刀叉";
		res.m_res = R.drawable.__dec__1234121710296828;
		res.m_thumb = R.drawable.__dec__50132015012712170648834744_120;
		res.m_tjId = 1065467;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 276;
		res.m_name = "邮戳";
		res.m_res = R.drawable.__dec__1234121740109158;
		res.m_thumb = R.drawable.__dec__98472015012712173563018234_120;
		res.m_tjId = 1065468;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 277;
		res.m_name = "文字";
		res.m_res = R.drawable.__dec__1234121808812464;
		res.m_thumb = R.drawable.__dec__48792015012712175362746074_120;
		res.m_tjId = 1065469;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 278;
		res.m_name = "牡丹花";
		res.m_res = R.drawable.__dec__1234121831764519;
		res.m_thumb = R.drawable.__dec__7162201501271218268853559_120;
		res.m_tjId = 1065470;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 279;
		res.m_name = "蓝色花";
		res.m_res = R.drawable.__dec__1234121853994920;
		res.m_thumb = R.drawable.__dec__99902015012712184950439781_120;
		res.m_tjId = 1065471;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 280;
		res.m_name = "绿蝴蝶";
		res.m_res = R.drawable.__dec__123412191113350;
		res.m_thumb = R.drawable.__dec__18432015012712190612242444_120;
		res.m_tjId = 1065472;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 281;
		res.m_name = "紫蝴蝶";
		res.m_res = R.drawable.__dec__1234121938860105;
		res.m_thumb = R.drawable.__dec__20952015012712193455602392_120;
		res.m_tjId = 1065473;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 282;
		res.m_name = "Recistered";
		res.m_res = R.drawable.__dec__1234122007458777;
		res.m_thumb = R.drawable.__dec__43332015012712200194423418_120;
		res.m_tjId = 1065474;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 283;
		res.m_name = "英文";
		res.m_res = R.drawable.__dec__1234122045736728;
		res.m_thumb = R.drawable.__dec__35072015012712204145524993_120;
		res.m_tjId = 1065475;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 284;
		res.m_name = "花朵";
		res.m_res = R.drawable.__dec__1234122109710852;
		res.m_thumb = R.drawable.__dec__69782015012712210249654596_120;
		res.m_tjId = 1065476;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 285;
		res.m_name = "黄花";
		res.m_res = R.drawable.__dec__1234122132257307;
		res.m_thumb = R.drawable.__dec__65982015012712212691020859_120;
		res.m_tjId = 1065477;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 286;
		res.m_name = "红花";
		res.m_res = R.drawable.__dec__1234122157790508;
		res.m_thumb = R.drawable.__dec__62022015012712215297031143_120;
		res.m_tjId = 1065478;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 287;
		res.m_name = "camera";
		res.m_res = R.drawable.__dec__1234122227645026;
		res.m_thumb = R.drawable.__dec__19902015012712221822489127_120;
		res.m_tjId = 1065479;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 288;
		res.m_name = "玫瑰";
		res.m_res = R.drawable.__dec__1234122247439229;
		res.m_thumb = R.drawable.__dec__10242015012712224356612968_120;
		res.m_tjId = 1065480;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 289;
		res.m_name = "素花";
		res.m_res = R.drawable.__dec__1234122306498467;
		res.m_thumb = R.drawable.__dec__12442015012712230287734296_120;
		res.m_tjId = 1065481;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 290;
		res.m_name = "明信片";
		res.m_res = R.drawable.__dec__1234122350642788;
		res.m_thumb = R.drawable.__dec__81802015012712234714147662_120;
		res.m_tjId = 1065482;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 291;
		res.m_name = "Herat";
		res.m_res = R.drawable.__dec__1234122406660964;
		res.m_thumb = R.drawable.__dec__13062015012712240319509083_120;
		res.m_tjId = 1065483;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 292;
		res.m_name = "Follow";
		res.m_res = R.drawable.__dec__1234122422919484;
		res.m_thumb = R.drawable.__dec__21072015012712241971549760_120;
		res.m_tjId = 1065484;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 293;
		res.m_name = "因为";
		res.m_res = R.drawable.__dec__1234122452356835;
		res.m_thumb = R.drawable.__dec__81592015012712244738304563_120;
		res.m_tjId = 1065485;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 294;
		res.m_name = "wanna";
		res.m_res = R.drawable.__dec__1234122641234163;
		res.m_thumb = R.drawable.__dec__42612015012712263645101923_120;
		res.m_tjId = 1065486;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 295;
		res.m_name = "蝴蝶";
		res.m_res = R.drawable.__dec__1234122729212733;
		res.m_thumb = R.drawable.__dec__67242015012712272141609335_120;
		res.m_tjId = 1065487;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 296;
		res.m_name = "飞蛾";
		res.m_res = R.drawable.__dec__1234133938517006;
		res.m_thumb = R.drawable.__dec__54082015012713393493041867_120;
		res.m_tjId = 1065488;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 297;
		res.m_name = "树";
		res.m_res = R.drawable.__dec__1234122832185777;
		res.m_thumb = R.drawable.__dec__55272015012712282783710549_120;
		res.m_tjId = 0;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 298;
		res.m_name = "Travel";
		res.m_res = R.drawable.__dec__1234122853267757;
		res.m_thumb = R.drawable.__dec__70222015012712284789897919_120;
		res.m_tjId = 1065489;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 299;
		res.m_name = "Today";
		res.m_res = R.drawable.__dec__1234122922780075;
		res.m_thumb = R.drawable.__dec__47972015012712291813604728_120;
		res.m_tjId = 1065490;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 300;
		res.m_name = "Cute&#10084;";
		res.m_res = R.drawable.__dec__1234122940681685;
		res.m_thumb = R.drawable.__dec__11122015012712293640717524_120;
		res.m_tjId = 1065491;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 301;
		res.m_name = "小鸟";
		res.m_res = R.drawable.__dec__1234123013434401;
		res.m_thumb = R.drawable.__dec__53662015012712300842062421_120;
		res.m_tjId = 1065492;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 302;
		res.m_name = "小兔子";
		res.m_res = R.drawable.__dec__1234123057634495;
		res.m_thumb = R.drawable.__dec__44432015012712304484042213_120;
		res.m_tjId = 1065493;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 303;
		res.m_name = "铁塔";
		res.m_res = R.drawable.__dec__1234134007885923;
		res.m_thumb = R.drawable.__dec__63902015012713400376400012_120;
		res.m_tjId = 1065494;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 403;
		res.m_name = "戒指";
		res.m_res = R.drawable.__dec__1234134057177174;
		res.m_thumb = R.drawable.__dec__40682015012713403141402261_120;
		res.m_tjId = 1065667;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 401;
		res.m_name = "樱桃";
		res.m_res = R.drawable.__dec__1234134212982881;
		res.m_thumb = R.drawable.__dec__45622015012713420921328627_120;
		res.m_tjId = 1065665;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 402;
		res.m_name = "macarons1";
		res.m_res = R.drawable.__dec__1234134233744236;
		res.m_thumb = R.drawable.__dec__93012015012713423078381774_120;
		res.m_tjId = 1065666;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 404;
		res.m_name = "macarons2";
		res.m_res = R.drawable.__dec__1234134259460098;
		res.m_thumb = R.drawable.__dec__44292015012713425594040125_120;
		res.m_tjId = 1065668;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 405;
		res.m_name = "皇冠";
		res.m_res = R.drawable.__dec__1234134320179393;
		res.m_thumb = R.drawable.__dec__59302015012713431461862698_120;
		res.m_tjId = 1065669;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 406;
		res.m_name = "气球";
		res.m_res = R.drawable.__dec__1234134336904355;
		res.m_thumb = R.drawable.__dec__87582015012713433362336681_120;
		res.m_tjId = 0;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 407;
		res.m_name = "叶子love";
		res.m_res = R.drawable.__dec__1234134417947353;
		res.m_thumb = R.drawable.__dec__2614201501271344008634496_120;
		res.m_tjId = 1065670;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 408;
		res.m_name = "项链";
		res.m_res = R.drawable.__dec__1234134500103929;
		res.m_thumb = R.drawable.__dec__70662015012713445594229971_120;
		res.m_tjId = 1065671;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 409;
		res.m_name = "粉色蝴蝶结";
		res.m_res = R.drawable.__dec__1234134523399500;
		res.m_thumb = R.drawable.__dec__54262015012713451947153835_120;
		res.m_tjId = 1065672;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 410;
		res.m_name = "糖果";
		res.m_res = R.drawable.__dec__1234134640253118;
		res.m_thumb = R.drawable.__dec__62832015012713464057374337_120;
		res.m_tjId = 1065673;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 411;
		res.m_name = "lovelyday";
		res.m_res = R.drawable.__dec__1234134707900983;
		res.m_thumb = R.drawable.__dec__14262015012713470248298672_120;
		res.m_tjId = 1065674;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 412;
		res.m_name = "macarons";
		res.m_res = R.drawable.__dec__1234134742599929;
		res.m_thumb = R.drawable.__dec__63162015012713473626412444_120;
		res.m_tjId = 1065675;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 413;
		res.m_name = "钻石";
		res.m_res = R.drawable.__dec__1234134819431488;
		res.m_thumb = R.drawable.__dec__4691201501271348057339279_120;
		res.m_tjId = 1065676;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 414;
		res.m_name = "leadyou";
		res.m_res = R.drawable.__dec__1234134906199253;
		res.m_thumb = R.drawable.__dec__84732015012713485957599121_120;
		res.m_tjId = 1065677;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 415;
		res.m_name = "girltopic";
		res.m_res = R.drawable.__dec__1234134950124622;
		res.m_thumb = R.drawable.__dec__51362015012713494423373632_120;
		res.m_tjId = 1065678;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 416;
		res.m_name = "花边奖牌";
		res.m_res = R.drawable.__dec__1234135028282377;
		res.m_thumb = R.drawable.__dec__94602015012713502345937375_120;
		res.m_tjId = 1065679;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 417;
		res.m_name = "粉花边心形";
		res.m_res = R.drawable.__dec__1234135058801617;
		res.m_thumb = R.drawable.__dec__90562015012713504936698459_120;
		res.m_tjId = 1065680;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 418;
		res.m_name = "prerry";
		res.m_res = R.drawable.__dec__1234135123337774;
		res.m_thumb = R.drawable.__dec__94082015012713511794834162_120;
		res.m_tjId = 1065681;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 419;
		res.m_name = "sweetgirly";
		res.m_res = R.drawable.__dec__1234135153672196;
		res.m_thumb = R.drawable.__dec__58492015012713514720585740_120;
		res.m_tjId = 1065682;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 420;
		res.m_name = "MUST";
		res.m_res = R.drawable.__dec__1234140137298831;
		res.m_thumb = R.drawable.__dec__40182015012714013167093766_120;
		res.m_tjId = 1065683;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 421;
		res.m_name = "小旗帜";
		res.m_res = R.drawable.__dec__1234140210720100;
		res.m_thumb = R.drawable.__dec__56612015012714020556235438_120;
		res.m_tjId = 1065684;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 422;
		res.m_name = "雪花";
		res.m_res = R.drawable.__dec__1234140502305743;
		res.m_thumb = R.drawable.__dec__36552015012714045523630976_120;
		res.m_tjId = 1065685;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 423;
		res.m_name = "黄闪电";
		res.m_res = R.drawable.__dec__1234140525630522;
		res.m_thumb = R.drawable.__dec__66102015012714052187119615_120;
		res.m_tjId = 1065686;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 424;
		res.m_name = "水滴";
		res.m_res = R.drawable.__dec__1234140543205557;
		res.m_thumb = R.drawable.__dec__73982015012714053996030010_120;
		res.m_tjId = 1065687;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 425;
		res.m_name = "星星";
		res.m_res = R.drawable.__dec__1234140605509635;
		res.m_thumb = R.drawable.__dec__3141201501271406014456844_120;
		res.m_tjId = 1065154;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 426;
		res.m_name = "星星2";
		res.m_res = R.drawable.__dec__1234140626108066;
		res.m_thumb = R.drawable.__dec__46522015012714062054630073_120;
		res.m_tjId = 1065688;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 427;
		res.m_name = "cool";
		res.m_res = R.drawable.__dec__1234141013731482;
		res.m_thumb = R.drawable.__dec__65262015012714100956768579_120;
		res.m_tjId = 1065689;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 428;
		res.m_name = "cloud";
		res.m_res = R.drawable.__dec__1234141055222438;
		res.m_thumb = R.drawable.__dec__95582015012714105064273151_120;
		res.m_tjId = 1065690;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 429;
		res.m_name = "太阳";
		res.m_res = R.drawable.__dec__1234160604914538;
		res.m_thumb = R.drawable.__dec__40122015012716055987188110_120;
		res.m_tjId = 1065691;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 430;
		res.m_name = "行星";
		res.m_res = R.drawable.__dec__1234161242730457;
		res.m_thumb = R.drawable.__dec__25012015012716123440711561_120;
		res.m_tjId = 1065692;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 431;
		res.m_name = "shining";
		res.m_res = R.drawable.__dec__1234164815184800;
		res.m_thumb = R.drawable.__dec__47092015012716480912020128_120;
		res.m_tjId = 1065693;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 432;
		res.m_name = "云";
		res.m_res = R.drawable.__dec__1234165359770868;
		res.m_thumb = R.drawable.__dec__47202015012716535525947428_120;
		res.m_tjId = 0;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 433;
		res.m_name = "龙卷风";
		res.m_res = R.drawable.__dec__1234165429483658;
		res.m_thumb = R.drawable.__dec__70012015012716542550822095_120;
		res.m_tjId = 1065694;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 434;
		res.m_name = "rainyday";
		res.m_res = R.drawable.__dec__1234165508757267;
		res.m_thumb = R.drawable.__dec__73212015012716545344349799_120;
		res.m_tjId = 1065695;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 435;
		res.m_name = "彩虹";
		res.m_res = R.drawable.__dec__1234165559243308;
		res.m_thumb = R.drawable.__dec__42692015012716560721819911_120;
		res.m_tjId = 1064660;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 436;
		res.m_name = "月亮星星";
		res.m_res = R.drawable.__dec__123416580083804;
		res.m_thumb = R.drawable.__dec__28722015012716575554938730_120;
		res.m_tjId = 1065696;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 437;
		res.m_name = "holiday";
		res.m_res = R.drawable.__dec__123416582987685;
		res.m_thumb = R.drawable.__dec__77002015012716582212824290_120;
		res.m_tjId = 1065697;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 438;
		res.m_name = "beautday";
		res.m_res = R.drawable.__dec__1234165845851107;
		res.m_thumb = R.drawable.__dec__7951201501271658425202160_120;
		res.m_tjId = 1065698;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 439;
		res.m_name = "IFeel";
		res.m_res = R.drawable.__dec__1234165938170788;
		res.m_thumb = R.drawable.__dec__71912015012716593248734453_120;
		res.m_tjId = 1065699;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 440;
		res.m_name = "雨伞";
		res.m_res = R.drawable.__dec__1234172355259863;
		res.m_thumb = R.drawable.__dec__67682015012716595059558448_120;
		res.m_tjId = 1065700;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		res = new DecorateRes();
		res.m_id = 441;
		res.m_name = "飘叶";
		res.m_res = R.drawable.__dec__1234170151426784;
		res.m_thumb = R.drawable.__dec__67142015012717014386465548_120;
		res.m_tjId = 1065701;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		out.put(res.m_id, res);

		return out;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected DecorateRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		DecorateRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new DecorateRes();
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
				out.m_tjId = jsonObj.getInt("tracking_code");
				JSONObject jsonObj2 = jsonObj.getJSONObject("res_arr");
				if(jsonObj2 != null && jsonObj2.length() > 0)
				{
					if(isPath)
					{
						if(jsonObj2.has("pic"))
						{
							out.m_res = jsonObj2.getString("pic");
						}
					}
					else
					{
						if(jsonObj2.has("pic"))
						{
							out.url_res = jsonObj2.getString("pic");
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
	public boolean CheckIntact(DecorateRes res)
	{
		boolean out = false;

		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb) && ResourceUtils.HasIntact(res.m_res))
			{
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
	protected void sync_raw_SaveSdcardRes(Context context, SparseArray<DecorateRes> arr)
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
						DecorateRes res;
						JSONObject jsonObj;
						JSONObject jsonObj2;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.valueAt(i);
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
								jsonObj.put("tracking_code", res.m_tjId);
								jsonObj2 = new JSONObject();
								{
									if(res.m_res instanceof String)
									{
										jsonObj2.put("pic", res.m_res);
									}
									else
									{
										jsonObj2.put("pic", "");
									}
								}
								jsonObj.put("res_arr", jsonObj2);
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
		return EventID.DECORATE_CLOUD_OK;
	}

	@Override
	public DecorateRes GetItem(SparseArray<DecorateRes> arr, int id)
	{
		if(arr != null)
		{
			return arr.get(id);
		}
		return null;
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

	public void ReadOrderArr()
	{
		ReadOrderArr(MyFramework2App.getInstance().getApplication(), ORDER_PATH);
	}

	public void SaveOrderArr()
	{
		SaveOrderArr(MyFramework2App.getInstance().getApplication(), NEW_ORDER_JSON_VER, ORDER_PATH);
	}

	@Override
	protected void RebuildNetResArr(SparseArray<DecorateRes> dst, SparseArray<DecorateRes> src)
	{
		if(dst != null && src != null)
		{
			DecorateRes srcTemp;
			DecorateRes dstTemp;
			Class cls = DecorateRes.class;
			Field[] fields = cls.getDeclaredFields();
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				dstTemp = dst.valueAt(i);
				int key = dst.keyAt(i);
				srcTemp = src.get(key);
				if(srcTemp != null)
				{
					dstTemp.m_type = srcTemp.m_type;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.m_res = srcTemp.m_res;

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
					dst.put(key, srcTemp);
				}
			}
		}
	}

	public ArrayList<DecorateRes> GetResArr2(int[] ids, boolean onlyCanUse)
	{
		ArrayList<DecorateRes> out = new ArrayList<>();

		if(ids != null)
		{
			DecorateRes temp;
			for(int id : ids)
			{
				temp = GetRes(id, onlyCanUse);
				if(temp != null)
				{
					out.add(temp);
				}
			}
		}

		return out;
	}

	public ArrayList<DecorateGroupRes> GetGroupResArr()
	{
		ArrayList<DecorateGroupRes> out = new ArrayList<>();

		ArrayList<ThemeRes> resArr = ResourceUtils.BuildShowArr(ThemeResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null), ThemeResMgr2.getInstance().sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null), GetOrderArr());

		ThemeRes temp;
		ArrayList<DecorateRes> tempArr;
		DecorateGroupRes group;
		int len = resArr.size();
		for(int i = 0; i < len; i++)
		{
			temp = resArr.get(i);
			if(temp.m_decorateIDArr != null && temp.m_decorateIDArr.length > 0)
			{
				tempArr = GetResArr2(temp.m_decorateIDArr, true);
				if(tempArr.size() == temp.m_decorateIDArr.length)
				{
					group = new DecorateGroupRes();
					group.m_id = temp.m_id;
					group.m_name = temp.m_name;
					group.m_titleThumb = temp.m_decorateThumb;
					group.m_group = tempArr;
					out.add(group);
				}
			}
		}
		return out;
	}

	public ArrayList<GroupRes> GetDownloadedGroupResArr()
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		SparseArray<DecorateRes> orgArr = new SparseArray<>();
		SparseArray<DecorateRes> sdcardArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);
		if(sdcardArr != null)
		{
			int len = sdcardArr.size();
			for(int i = 0; i < len; i++)
			{
				orgArr.put(sdcardArr.keyAt(i), sdcardArr.valueAt(i));
			}
		}
		ArrayList<ThemeRes> themeArr = new ArrayList<>();
		ArrayList<ThemeRes> tempArr = ThemeResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null);
		if(tempArr != null)
		{
			themeArr.addAll(tempArr);
		}
		tempArr = ThemeResMgr2.getInstance().sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);
		if(tempArr != null)
		{
			themeArr.addAll(tempArr);
		}
		int len = themeArr.size();
		ThemeRes temp;
		ArrayList<DecorateRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_decorateIDArr != null && temp.m_decorateIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_decorateIDArr);
				if(subArr.size() == temp.m_decorateIDArr.length)
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
			len = orgArr.size();
			temp.m_decorateIDArr = new int[len];

			groupRes = new GroupRes();
			groupRes.m_themeRes = temp;
			groupRes.m_ress = new ArrayList<>();
			for(int i = 0; i < len; i++)
			{
				groupRes.m_ress.add(orgArr.valueAt(i));
				temp.m_decorateIDArr[i] = orgArr.keyAt(i);
			}
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
			ArrayList<DecorateRes> resArr;
			GroupRes groupRes;
			boolean flag;
			int len = downloadArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if(temp.m_decorateIDArr != null && temp.m_decorateIDArr.length > 0)
				{
					flag = false;
					resArr = GetResArr2(temp.m_decorateIDArr, false);
					if(resArr.size() != temp.m_decorateIDArr.length)
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

	public void DeleteGroupNewFlag(Context context, int themeId)
	{
		ResourceMgr.DeleteNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, themeId);
	}

	public void AddGroupNewFlag(Context context, int themeId)
	{
		ResourceMgr.AddNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, themeId);
	}

	public ArrayList<DecorateRes> DeleteGroupRes(Context context, GroupRes res)
	{
		ArrayList<DecorateRes> out = new ArrayList<>();

		int[] ids = res.m_themeRes.m_decorateIDArr;
		SparseArray<DecorateRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<DecorateRes> arr = ResourceUtils.DeleteItems(sdcardArr, ids);
		if(arr != null && arr.size() > 0)
		{
			DecorateRes temp;
			int len = arr.size();
			for(int i = 0; i < len; i++)
			{
				temp = arr.get(i);
				if(temp.m_type == BaseRes.TYPE_LOCAL_PATH)
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

		return out;
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

	public void AddGroupId(int themeId)
	{
		if(sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null) != null)
		{
			ResourceUtils.DeleteId(GetOrderArr(), themeId);
			GetOrderArr().add(0, themeId);
			SaveOrderArr();
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

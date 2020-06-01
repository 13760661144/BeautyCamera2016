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
 * Created by Raining on 2017/9/27.
 */

public class BrushResMgr2 extends BaseResMgr<BrushRes, ArrayList<BrushRes>>
{
	public final static int NEW_JSON_VER = 1;
	public final static int NEW_ORDER_JSON_VER = 1;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().BRUSH_PATH + "/brush.xxxx"; //资源集合

	protected final String ORDER_PATH = DownloadMgr.getInstance().BRUSH_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().BRUSH_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/finger_images/android.php?version=3.1.3";
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/finger_images/android.php?version=88.8.8";

	public final static String NEW_DOWNLOAD_FLAG = "brush"; //记录在Preferences
	public final ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "brush_id"; //判断是否有新素材更新

	private static BrushResMgr2 sInstance;

	private BrushResMgr2()
	{
	}

	public synchronized static BrushResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new BrushResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<BrushRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<BrushRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<BrushRes> arr, BrushRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<BrushRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<BrushRes> out = new ArrayList<>();
		BrushRes res;

		res = new BrushRes();
		res.m_id = 120;
		res.m_name = "血拼时1";
		res.m_thumb = R.drawable.__bru__21912016081616063885618055;
		res.m_tjId = 1070041;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__201608161618266254676, R.drawable.__bru__201608161618268811476, R.drawable.__bru__2016081616182610515076, R.drawable.__bru__2016081616182623863976, R.drawable.__bru__2016081616182630974476, R.drawable.__bru__2016081616182635435376, R.drawable.__bru__2016081616182649577976};
		res.m_ra = -90;
		res.m_rb = 90;
		res.m_sa = 0.4f;
		res.m_sb = 0.7f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 121;
		res.m_name = "血拼时2";
		res.m_thumb = R.drawable.__bru__20302016081616072635222873;
		res.m_tjId = 1070042;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616184387663238, R.drawable.__bru__2016081616184389505938, R.drawable.__bru__2016081616184389741238, R.drawable.__bru__2016081616184412864171, R.drawable.__bru__2016081616184416893671};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.4f;
		res.m_sb = 0.6f;
		res.m_da = 80;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 122;
		res.m_name = "血拼时3";
		res.m_thumb = R.drawable.__bru__37842016081616080142445431;
		res.m_tjId = 1070043;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616193434200364, R.drawable.__bru__2016081616193439670464, R.drawable.__bru__2016081616193439965764, R.drawable.__bru__2016081616193454890864, R.drawable.__bru__2016081616193463203164, R.drawable.__bru__2016081616193465231764, R.drawable.__bru__2016081616193472869664};
		res.m_ra = -90;
		res.m_rb = 90;
		res.m_sa = 0.3f;
		res.m_sb = 0.6f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 123;
		res.m_name = "血拼时4";
		res.m_thumb = R.drawable.__bru__29852016081616085154430769;
		res.m_tjId = 1070044;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616195175588072, R.drawable.__bru__2016081616195177655272, R.drawable.__bru__2016081616195179582772, R.drawable.__bru__2016081616195195033172, R.drawable.__bru__2016081616195199308372, R.drawable.__bru__201608161619521917915, R.drawable.__bru__2016081616195216559415};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.3f;
		res.m_sb = 0.6f;
		res.m_da = 80;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 124;
		res.m_name = "血拼时5";
		res.m_thumb = R.drawable.__bru__72892016081616092576359886;
		res.m_tjId = 1070045;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616203187564357, R.drawable.__bru__2016081616203190938057, R.drawable.__bru__2016081616203211817189, R.drawable.__bru__2016081616203233637189, R.drawable.__bru__2016081616203237407489, R.drawable.__bru__2016081616203252846789, R.drawable.__bru__2016081616203260423389, R.drawable.__bru__2016081616203185893557};
		res.m_ra = -90;
		res.m_rb = 90;
		res.m_sa = 0.4f;
		res.m_sb = 0.7f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 125;
		res.m_name = "血拼时6";
		res.m_thumb = R.drawable.__bru__9620201608161610034872951;
		res.m_tjId = 1070046;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616205658141198, R.drawable.__bru__2016081616205661336598, R.drawable.__bru__2016081616205662578098, R.drawable.__bru__2016081616205676088798, R.drawable.__bru__2016081616205689027998, R.drawable.__bru__2016081616205690824298, R.drawable.__bru__2016081616205692307798, R.drawable.__bru__201608161620579612941};
		res.m_ra = -90;
		res.m_rb = 90;
		res.m_sa = 0.3f;
		res.m_sb = 0.45f;
		res.m_da = 80;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 111;
		res.m_name = "热情岛屿-音乐";
		res.m_thumb = R.drawable.__bru__85772016081615594270737392;
		res.m_tjId = 1069983;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616145718096740, R.drawable.__bru__2016081616145718323140, R.drawable.__bru__2016081616145751179140, R.drawable.__bru__2016081616145795042140, R.drawable.__bru__2016081616145922147414, R.drawable.__bru__2016081616150280897521};
		res.m_ra = 0;
		res.m_rb = 90;
		res.m_sa = 0.2f;
		res.m_sb = 0.5f;
		res.m_da = 80;
		res.m_db = 180;
		out.add(res);

		res = new BrushRes();
		res.m_id = 110;
		res.m_name = "热情岛屿-沙滩";
		res.m_thumb = R.drawable.__bru__68082016081615591723856095;
		res.m_tjId = 1069982;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616142467903110, R.drawable.__bru__2016081616142475564510, R.drawable.__bru__2016081616142484804310, R.drawable.__bru__2016081616142493809910, R.drawable.__bru__2016081616142590964442, R.drawable.__bru__201608161614287814494};
		res.m_ra = 0;
		res.m_rb = 90;
		res.m_sa = 0.3f;
		res.m_sb = 0.5f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 108;
		res.m_name = "热情岛屿-海螺";
		res.m_thumb = R.drawable.__bru__56592016081615573022539364;
		res.m_tjId = 1069981;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616130214675265, R.drawable.__bru__2016081616130217838565, R.drawable.__bru__2016081616130219931265, R.drawable.__bru__2016081616130238318265, R.drawable.__bru__2016081616130249578765};
		res.m_ra = 0;
		res.m_rb = 90;
		res.m_sa = 0.3f;
		res.m_sb = 0.5f;
		res.m_da = 120;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 109;
		res.m_name = "热情岛屿-雪糕";
		res.m_thumb = R.drawable.__bru__74762016081615581199330637;
		res.m_tjId = 1069980;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616140423029440, R.drawable.__bru__2016081616140425525240, R.drawable.__bru__2016081616140427223840, R.drawable.__bru__2016081616140449033140, R.drawable.__bru__2016081616140454430340};
		res.m_ra = 0;
		res.m_rb = 90;
		res.m_sa = 0.3f;
		res.m_sb = 0.5f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 104;
		res.m_name = "起床时-太阳";
		res.m_thumb = R.drawable.__bru__44892016081615525156741465;
		res.m_tjId = 1070029;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616111131892385, R.drawable.__bru__2016081616111136231185, R.drawable.__bru__2016081616111137401085, R.drawable.__bru__2016081616111151347485, R.drawable.__bru__2016081616111160223985, R.drawable.__bru__2016081616111163084885, R.drawable.__bru__2016081616111168349085, R.drawable.__bru__2016081616111176777985, R.drawable.__bru__2016081616111184960385};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.2f;
		res.m_sb = 0.5f;
		res.m_da = 120;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 105;
		res.m_name = "起床时-闹钟";
		res.m_thumb = R.drawable.__bru__55452016081615542116250288;
		res.m_tjId = 1070030;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616113179351110, R.drawable.__bru__2016081616113179534110, R.drawable.__bru__2016081616113179885110, R.drawable.__bru__2016081616113198882510, R.drawable.__bru__201608161611325090042, R.drawable.__bru__201608161611327028142, R.drawable.__bru__2016081616113216300442};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.3f;
		res.m_sb = 0.6f;
		res.m_da = 120;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 106;
		res.m_name = "起床时-花蝴蝶";
		res.m_thumb = R.drawable.__bru__88042016081615551865125937;
		res.m_tjId = 1070031;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616115851800626, R.drawable.__bru__2016081616115856323326, R.drawable.__bru__2016081616115857048126, R.drawable.__bru__2016081616115873120426, R.drawable.__bru__2016081616115880692826, R.drawable.__bru__2016081616115883035526, R.drawable.__bru__2016081616115890524426, R.drawable.__bru__2016081616115899462826, R.drawable.__bru__201608161611592616113, R.drawable.__bru__2016081616115910970313};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.2f;
		res.m_sb = 0.4f;
		res.m_da = 130;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 107;
		res.m_name = "起床时-音乐";
		res.m_thumb = R.drawable.__bru__25652016081615563969963084;
		res.m_tjId = 1070032;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616121453389391, R.drawable.__bru__2016081616121453997691, R.drawable.__bru__2016081616121455394891, R.drawable.__bru__2016081616121478516491, R.drawable.__bru__2016081616121481229091, R.drawable.__bru__2016081616121482902891, R.drawable.__bru__2016081616121496885391, R.drawable.__bru__20160816161215648479, R.drawable.__bru__201608161612156413179, R.drawable.__bru__2016081616121512361879, R.drawable.__bru__2016081616121516620279, R.drawable.__bru__2016081616121525547679};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.2f;
		res.m_sb = 0.5f;
		res.m_da = 120;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 102;
		res.m_name = "吃货一刻-奶酪";
		res.m_thumb = R.drawable.__bru__11522016081615501946007568;
		res.m_tjId = 1070027;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081615495876416820, R.drawable.__bru__2016081615495910615152, R.drawable.__bru__2016081615495998001252, R.drawable.__bru__2016081615500013360085, R.drawable.__bru__2016081615500080365985, R.drawable.__bru__2016081615500112755927};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.25f;
		res.m_sb = 0.4f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 101;
		res.m_name = "吃货一刻-零食";
		res.m_thumb = R.drawable.__bru__1501201608161548247425593;
		res.m_tjId = 1070026;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081615490429878334, R.drawable.__bru__2016081615490432448834, R.drawable.__bru__2016081615490432718534, R.drawable.__bru__2016081615490450052834, R.drawable.__bru__2016081615490456820534};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.25f;
		res.m_sb = 0.4f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 103;
		res.m_name = "吃货一刻-咸食";
		res.m_thumb = R.drawable.__bru__59082016081615514794693771;
		res.m_tjId = 1070028;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__201608161552009502740, R.drawable.__bru__2016081615520012527940, R.drawable.__bru__2016081615520013361240, R.drawable.__bru__2016081615520032921940, R.drawable.__bru__2016081615520037585740, R.drawable.__bru__2016081615520040175640};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.25f;
		res.m_sb = 0.4f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 112;
		res.m_name = "难过时-落叶";
		res.m_thumb = R.drawable.__bru__18912016081616002588327239;
		res.m_tjId = 1070033;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616152794864562, R.drawable.__bru__2016081616152795526862, R.drawable.__bru__2016081616152793799062, R.drawable.__bru__2016081616152815556094, R.drawable.__bru__2016081616152819004694, R.drawable.__bru__2016081616152838002194, R.drawable.__bru__2016081616152842066794, R.drawable.__bru__2016081616152849561794};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.8f;
		res.m_sb = 0.9f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 113;
		res.m_name = "难过时-雪花";
		res.m_thumb = R.drawable.__bru__75662016081616011595264077;
		res.m_tjId = 1070034;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616155014127639, R.drawable.__bru__2016081616155015558839, R.drawable.__bru__2016081616155016571839, R.drawable.__bru__2016081616155032813439, R.drawable.__bru__2016081616155040595239, R.drawable.__bru__2016081616155045023839, R.drawable.__bru__2016081616155048419139, R.drawable.__bru__2016081616155055897039};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 1.0f;
		res.m_sb = 1.0f;
		res.m_da = 80;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 114;
		res.m_name = "难过时-泪水";
		res.m_thumb = R.drawable.__bru__86932016081616015971167556;
		res.m_tjId = 1070035;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616160969326722, R.drawable.__bru__2016081616160970182122, R.drawable.__bru__2016081616160970949722, R.drawable.__bru__2016081616160989536722, R.drawable.__bru__2016081616160991692822, R.drawable.__bru__2016081616160992557322, R.drawable.__bru__201608161616106262499, R.drawable.__bru__2016081616161012390399, R.drawable.__bru__2016081616161016379699, R.drawable.__bru__2016081616161021503499};
		res.m_ra = 0;
		res.m_rb = 0;
		res.m_sa = 1.0f;
		res.m_sb = 1.0f;
		res.m_da = 50;
		res.m_db = 150;
		out.add(res);

		res = new BrushRes();
		res.m_id = 115;
		res.m_name = "难过时-云朵";
		res.m_thumb = R.drawable.__bru__16482016081616025811488323;
		res.m_tjId = 1070036;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616162566827542, R.drawable.__bru__2016081616162567639742, R.drawable.__bru__2016081616162568842342, R.drawable.__bru__2016081616162591196942, R.drawable.__bru__2016081616162594440042, R.drawable.__bru__2016081616162596078842, R.drawable.__bru__2016081616162615379775, R.drawable.__bru__2016081616162623250675, R.drawable.__bru__2016081616162625132075, R.drawable.__bru__2016081616162632206275};
		res.m_ra = 0;
		res.m_rb = 0;
		res.m_sa = 0.3f;
		res.m_sb = 0.6f;
		res.m_da = 60;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 116;
		res.m_name = "难过时-雪点";
		res.m_thumb = R.drawable.__bru__58912016081616033093842621;
		res.m_tjId = 1070037;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616164069554576, R.drawable.__bru__2016081616164069042276, R.drawable.__bru__2016081616164070417176, R.drawable.__bru__2016081616164088669876, R.drawable.__bru__2016081616164091973776, R.drawable.__bru__2016081616164093551576, R.drawable.__bru__201608161616414453864, R.drawable.__bru__2016081616164113387364, R.drawable.__bru__2016081616164118192664};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 1.0f;
		res.m_sb = 1.0f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 119;
		res.m_name = "幸运降临-四叶草";
		res.m_thumb = R.drawable.__bru__43692016081616060141268331;
		res.m_tjId = 1070040;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616175192457971, R.drawable.__bru__2016081616175193777371, R.drawable.__bru__2016081616175194228571, R.drawable.__bru__2016081616175211598314, R.drawable.__bru__2016081616175212916414};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.5f;
		res.m_sb = 1.0f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 117;
		res.m_name = "幸运降临-星星";
		res.m_thumb = R.drawable.__bru__11982016081616043240905185;
		res.m_tjId = 1070038;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__20160816161708871635, R.drawable.__bru__201608161617082124335, R.drawable.__bru__201608161617111494186};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.5f;
		res.m_sb = 0.9f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 118;
		res.m_name = "幸运降临-萤火虫";
		res.m_thumb = R.drawable.__bru__71062016081616052140443445;
		res.m_tjId = 1070039;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081616173934334189, R.drawable.__bru__2016081616173935863989, R.drawable.__bru__2016081616173935523189};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.5f;
		res.m_sb = 1.0f;
		res.m_da = 120;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 97;
		res.m_name = "洗完澡-小鸭子";
		res.m_thumb = R.drawable.__bru__95882016081615392151242584;
		res.m_tjId = 1070022;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081615383437024053, R.drawable.__bru__2016081615383438166053, R.drawable.__bru__2016081615383438136153, R.drawable.__bru__2016081615383455304753, R.drawable.__bru__2016081615383457915353};
		res.m_ra = 0;
		res.m_rb = 90;
		res.m_sa = 0.35f;
		res.m_sb = 0.6f;
		res.m_da = 20;
		res.m_db = 130;
		out.add(res);

		res = new BrushRes();
		res.m_id = 98;
		res.m_name = "洗完澡-蝴蝶";
		res.m_thumb = R.drawable.__bru__98822016081615433580290942;
		res.m_tjId = 1070023;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081615445722697777, R.drawable.__bru__2016081615445724600677, R.drawable.__bru__2016081615445724846977, R.drawable.__bru__2016081615445738360277, R.drawable.__bru__2016081615445741140877, R.drawable.__bru__2016081615445744777577};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.4f;
		res.m_sb = 0.7f;
		res.m_da = 40;
		res.m_db = 100;
		out.add(res);

		res = new BrushRes();
		res.m_id = 99;
		res.m_name = "洗完澡-泡泡";
		res.m_thumb = R.drawable.__bru__66062016081615452583259935;
		res.m_tjId = 1070024;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081615461311532898, R.drawable.__bru__2016081615461312844598, R.drawable.__bru__2016081615461314120798, R.drawable.__bru__2016081615461325798198};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.4f;
		res.m_sb = 0.7f;
		res.m_da = 100;
		res.m_db = 200;
		out.add(res);

		res = new BrushRes();
		res.m_id = 100;
		res.m_name = "洗完澡-叶子";
		res.m_thumb = R.drawable.__bru__59252016081615474010489070;
		res.m_tjId = 1070025;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_res = new Object[]{R.drawable.__bru__2016081615473184257549, R.drawable.__bru__2016081615473186476049, R.drawable.__bru__2016081615473187276149, R.drawable.__bru__201608161547321659436};
		res.m_ra = 0;
		res.m_rb = 360;
		res.m_sa = 0.4f;
		res.m_sb = 0.7f;
		res.m_da = 70;
		res.m_db = 200;
		out.add(res);

		return out;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected BrushRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		BrushRes out = null;

		if(jsonObj != null)
		{
			try
			{
				out = new BrushRes();
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
				out.m_name = jsonObj.getString("title");
				if(isPath)
				{
					out.m_thumb = jsonObj.getString("thumb");
				}
				else
				{
					out.url_thumb = jsonObj.getString("thumb");
				}

				temp = jsonObj.getString("pushID");
				if(temp != null && temp.length() > 0)
				{
					out.m_tjId = Integer.parseInt(temp);
				}
				out.m_sa = jsonObj.getInt("scaleMin") / 100f;
				out.m_sb = jsonObj.getInt("scaleMax") / 100f;
				out.m_da = jsonObj.getInt("distanceMin");
				out.m_db = jsonObj.getInt("distanceMax");
				out.m_ra = jsonObj.getInt("degreeMin");
				out.m_rb = jsonObj.getInt("degreeMax");

				String values = (String)jsonObj.get("res_arr");
				if(isPath)
				{
					out.m_res = values.split(",");
				}
				else
				{
					out.url_res = values.split(",");
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
	public boolean CheckIntact(BrushRes res)
	{
		boolean out = false;

		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb) && res.m_res != null && res.m_res.length > 0 && ResourceUtils.HasIntact(res.m_res[0]))
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
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<BrushRes> arr)
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
						BrushRes res;
						JSONObject jsonObj;
						int len = arr.size();
						for(int i = 0; i < len; i++)
						{
							res = arr.get(i);
							if(res != null)
							{
								jsonObj = new JSONObject();
								jsonObj.put("id", res.m_id);
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
								jsonObj.put("pushID", Integer.toString(res.m_tjId));

								//////////////////////////////
								jsonObj.put("degreeMin", Integer.toString(res.m_ra));
								jsonObj.put("degreeMax", Integer.toString(res.m_rb));
								jsonObj.put("distanceMin", Integer.toString(res.m_da));
								jsonObj.put("distanceMax", Integer.toString(res.m_db));
								jsonObj.put("scaleMin", Integer.toString((int)(res.m_sa * 100)));
								jsonObj.put("scaleMax", Integer.toString((int)(res.m_sb * 100)));

								String res_arr = "";
								if(res.m_res != null && res.m_res.length > 0)
								{

									for(int j = 0; j < res.m_res.length; j++)
									{
										res_arr += res.m_res[j] + ",";
									}
								}
								res_arr = res_arr.substring(0, res_arr.length() - 1);
								jsonObj.put("res_arr", res_arr);
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
		return EventID.BRUSH_CLOUD_OK;
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

	public ArrayList<GroupRes> GetDownloadedGroupResArr(Context context)
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		ArrayList<BrushRes> orgArr = new ArrayList<>();
		ArrayList<BrushRes> tempArr = sync_GetSdcardRes(context, null);
		if(tempArr != null)
		{
			orgArr.addAll(tempArr);
		}
		ArrayList<ThemeRes> themeArr = ThemeResMgr2.getInstance().GetAllResArr();
		int len = themeArr.size();
		ThemeRes temp;
		ArrayList<BrushRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_brushIDArr != null && temp.m_brushIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_brushIDArr);
				if(subArr.size() == temp.m_brushIDArr.length)
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
			temp.m_brushIDArr = new int[orgArr.size()];
			len = orgArr.size();
			for(int i = 0; i < len; i++)
			{
				temp.m_brushIDArr[i] = orgArr.get(i).m_id;
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
			ArrayList<BrushRes> resArr;
			GroupRes groupRes;
			boolean flag;
			int len = downloadArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if(temp.m_brushIDArr != null && temp.m_brushIDArr.length > 0)
				{
					flag = false;
					resArr = GetResArr(temp.m_brushIDArr, false);
					if(resArr.size() != temp.m_brushIDArr.length)
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
		return GetNoDownloadedGroupResArr(context).size();
	}

	public ArrayList<BrushGroupRes> GetGroupResArr()
	{
		ArrayList<BrushGroupRes> out = new ArrayList<>();

		ArrayList<BrushRes> orgArr = new ArrayList<>();
		ArrayList<BrushRes> tempArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);
		if(tempArr != null)
		{
			orgArr.addAll(tempArr);
		}
		ArrayList<ThemeRes> resArr = ResourceUtils.BuildShowArr(ThemeResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null), ThemeResMgr2.getInstance().sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null), GetOrderArr());

		ThemeRes temp;
		tempArr = null;
		BrushGroupRes group;
		int len = resArr.size();
		for(int i = 0; i < len; i++)
		{
			temp = resArr.get(i);
			if(temp.m_brushIDArr != null && temp.m_brushIDArr.length > 0)
			{
				ResourceUtils.DeleteItems(orgArr, temp.m_brushIDArr);
				tempArr = GetResArr(temp.m_brushIDArr, true);
				if(tempArr.size() == temp.m_brushIDArr.length)
				{
					group = new BrushGroupRes();
					group.m_id = temp.m_id;
					group.m_name = temp.m_name;
					group.m_thumb = temp.m_brushThumb;
					group.m_group = tempArr;
					out.add(group);
				}
			}
		}

		//其他
		if(orgArr.size() > 0)
		{
			temp = new ThemeRes();
			temp.m_name = "其他";
			temp.m_brushIDArr = new int[orgArr.size()];
			len = orgArr.size();
			for(int i = 0; i < len; i++)
			{
				temp.m_brushIDArr[i] = orgArr.get(i).m_id;
			}

			group = new BrushGroupRes();
			group.m_id = temp.m_id;
			group.m_name = temp.m_name;
			group.m_thumb = R.mipmap.ic_launcher;
			group.m_group = orgArr;
			out.add(group);
		}

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

	public ArrayList<BrushRes> DeleteGroupRes(Context context, GroupRes res)
	{
		ArrayList<BrushRes> out = new ArrayList<>();

		int[] ids = res.m_themeRes.m_brushIDArr;
		ArrayList<BrushRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<BrushRes> arr = ResourceUtils.DeleteItems(sdcardArr, ids);
		if(arr != null && arr.size() > 0)
		{
			BrushRes temp;
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

	//public void DeleteNewFlag(Context context, int[] ids)
	//{
	//	if(ids != null && ids.length > 0)
	//	{
	//		ResourceUtils.DeleteIds(new_flag_arr, ids);
	//		ResourceMgr.UpdateNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG);
	//	}
	//}
	//
	//public void AddNewFlag(Context context, int id)
	//{
	//	ResourceMgr.AddNewFlag(context, new_flag_arr, NEW_DOWNLOAD_FLAG, id);
	//}

	@Override
	public BrushRes GetItem(ArrayList<BrushRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	@Override
	protected void RebuildNetResArr(ArrayList<BrushRes> dst, ArrayList<BrushRes> src)
	{
		if(dst != null && src != null)
		{
			BrushRes srcTemp;
			BrushRes dstTemp;
			Class cls = BrushRes.class;
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

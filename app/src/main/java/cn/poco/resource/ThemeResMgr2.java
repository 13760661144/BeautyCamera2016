package cn.poco.resource;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.adnonstop.resourcelibs.CallbackHolder;
import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.holder.ObjHolder;
import cn.poco.system.SysConfig;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/9/27.
 */

public class ThemeResMgr2 extends BaseResMgr<ThemeRes, ArrayList<ThemeRes>>
{
	public final static int NEW_JSON_VER = 5;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().THEME_PATH + "/theme.xxxx";

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().THEME_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/theme/android_v1703.php?version=%E7%BE%8E%E4%BA%BA%E7%9B%B8%E6%9C%BAv4.1.3";// + "&random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/theme/android_v1703.php?version=88.8.8";

	public final static String OLD_ID_FLAG = "theme_id"; //判断是否有新素材更新

	private static ThemeResMgr2 sInstance;

	private ThemeResMgr2()
	{
	}

	public synchronized static ThemeResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new ThemeResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(ArrayList<ThemeRes> arr)
	{
		return arr.size();
	}

	@Override
	public ArrayList<ThemeRes> MakeResArrObj()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean ResArrAddItem(ArrayList<ThemeRes> arr, ThemeRes item)
	{
		return arr.add(item);
	}

	@Override
	protected ArrayList<ThemeRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		ArrayList<ThemeRes> out = new ArrayList<>();

		ThemeRes res;
		{
			res = new ThemeRes();
			res.m_id = 1763;
			res.m_name = "推荐";
			res.m_tjId = 1064916399;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 76;
			res.m_detail = "精选多款热门自拍滤镜，快速get最美的自己！";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filterName = "推荐";
			res.m_filterDetail = "精选多款热门自拍滤镜，快速get最美的自己！";
			res.m_filter_theme_name = new String[1];
			res.m_filter_theme_name[0] = "Fruitage";
			res.m_filter_mask_color = 0xFF000000 | 0xffc18bc2;
			res.m_filter_theme_icon_res = new Object[1];
			res.m_filter_theme_icon_res[0]=R.drawable.__the__15154344820180105204053_1254_5767909455;
			res.m_filter_thumb_res =R.drawable.__the__15154344820180105204113_3878_3589727206;
			res.m_filterIDArr = new int[]{592,594,597,590,588,593,595,596};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 1273;
			res.m_name = "早安少女";
			res.m_tjId = 106012414;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 77;
			res.m_detail = "元气少女养成记，自拍之计在于晨，晨光美颜，元气满满一整天。";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filterName = "早安少女";
			res.m_filterDetail = "元气少女养成记，自拍之计在于晨，晨光美颜，元气满满一整天。";
			res.m_filter_theme_name = new String[1];
			res.m_filter_theme_name[0] = "萝莉";
			res.m_filter_mask_color = 0xFF000000 | 0xffebb4be;
			res.m_filter_theme_icon_res = new Object[1];
			res.m_filter_theme_icon_res[0]=R.drawable.__the__65982017041419151571802498;
			res.m_filter_thumb_res =R.drawable.__the__25222017041223261249109718;
			res.m_filterIDArr = new int[]{450,449,448,446,451,447};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 1384;
			res.m_name = "食物美学";
			res.m_tjId = 106014222;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 78;
			res.m_detail = "将美食拍摄进行到底，全新滤镜助攻打造诱惑系美图。";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filterName = "食物美学";
			res.m_filterDetail = "从香味到色泽，全新滤镜为吃货的美食图片做第二道深“加工”，打造美食的视觉诱惑！";
			res.m_filter_theme_name = new String[1];
			res.m_filter_theme_name[0] = "Baking";
			res.m_filter_mask_color = 0xFF000000 | 0xffffc85f;
			res.m_filter_theme_icon_res = new Object[1];
			res.m_filter_theme_icon_res[0]=R.drawable.__the__15154344820170911101855_9149_1726603318;
			res.m_filter_thumb_res =R.drawable.__the__15154344820170911101936_4654_1496319078;
			res.m_filterIDArr = new int[]{550,549,554,551,552,553};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 1283;
			res.m_name = "甜点聚会";
			res.m_tjId = 106012327;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 79;
			res.m_detail = "忙里偷闲，与闺蜜来个小清新的下午茶，鲜艳清新的色调与自拍和甜点更配噢~";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filterName = "甜点聚会";
			res.m_filterDetail = "忙里偷闲，与闺蜜来个小清新的下午茶，鲜艳清新的色调与自拍和甜点更配噢~";
			res.m_filter_theme_name = new String[1];
			res.m_filter_theme_name[0] = "Pancake";
			res.m_filter_mask_color = 0xFF000000 | 0xff93d8ea;
			res.m_filter_theme_icon_res = new Object[1];
			res.m_filter_theme_icon_res[0]=R.drawable.__the__32932017041415491396726127;
			res.m_filter_thumb_res =R.drawable.__the__40082017041218453580523315;
			res.m_filterIDArr = new int[]{496,497,498,499,500,501};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 1274;
			res.m_name = "浪漫之旅";
			res.m_tjId = 106012411;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 80;
			res.m_detail = "每一张旅图都有故事，每一张旅图都带着情怀，文艺腔调，让你的旅拍更有情怀与故事。";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filterName = "浪漫之旅";
			res.m_filterDetail = "每一张旅图都有故事，每一张旅图都带着情怀，文艺腔调，让你的旅拍更有情怀与故事。";
			res.m_filter_theme_name = new String[1];
			res.m_filter_theme_name[0] = "芭蕾";
			res.m_filter_mask_color = 0xFF000000 | 0xffbfabd5;
			res.m_filter_theme_icon_res = new Object[1];
			res.m_filter_theme_icon_res[0]=R.drawable.__the__66412017041409252492397661;
			res.m_filter_thumb_res =R.drawable.__the__15154344820171129173851_6621_2851778936;
			res.m_filterIDArr = new int[]{454,457,455,458,452,453};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 1294;
			res.m_name = "花之絮语";
			res.m_tjId = 106012412;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 81;
			res.m_detail = "与花来一场不期而遇的邂逅，开始一段童话对世人遥远而美丽的诉说。";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filterName = "花之絮语";
			res.m_filterDetail = "与花来一场不期而遇的邂逅，开始一段童话对世人遥远而美丽的诉说。";
			res.m_filter_theme_name = new String[1];
			res.m_filter_theme_name[0] = "Peach";
			res.m_filter_mask_color = 0xFF000000 | 0xffff7eaa;
			res.m_filter_theme_icon_res = new Object[1];
			res.m_filter_theme_icon_res[0]=R.drawable.__the__94992017041713510316503637;
			res.m_filter_thumb_res =R.drawable.__the__90782017041414184042239220;
			res.m_filterIDArr = new int[]{509,456,469,505,506,507,503,511,508,510};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 674;
			res.m_name = "简约几何";
			res.m_tjId = 106011614;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 103;
			res.m_detail = "简约几何";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_sFrameIDArr = new int[]{10,13,14,15,16,17,18,19,20,21,22,23,24,25};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 664;
			res.m_name = "幸福降临";
			res.m_tjId = 1069988;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 108;
			res.m_detail = "幸福降临";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_brushThumb = R.drawable.__the__68532016081611480360081331;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_brushIDArr = new int[]{118,117,119};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 661;
			res.m_name = "热情岛屿";
			res.m_tjId = 1069984;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 109;
			res.m_detail = "热情岛屿";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_brushThumb = R.drawable.__the__69372016081518372749043552;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_brushIDArr = new int[]{109,108,110,111};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 665;
			res.m_name = "难过时";
			res.m_tjId = 1069989;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 110;
			res.m_detail = "难过时";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_brushThumb = R.drawable.__the__26142016081610412781504568;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_brushIDArr = new int[]{116,115,114,113,112};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 662;
			res.m_name = "血拼时";
			res.m_tjId = 1069985;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 111;
			res.m_detail = "血拼时";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_brushThumb = R.drawable.__the__7485201608151838136021551;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_brushIDArr = new int[]{125,124,123,122,121,120};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 666;
			res.m_name = "吃货一刻";
			res.m_tjId = 1069990;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 112;
			res.m_detail = "吃货一刻";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_brushThumb = R.drawable.__the__38622016081610441090204561;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_brushIDArr = new int[]{103,101,102};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 667;
			res.m_name = "起床时";
			res.m_tjId = 1069991;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 113;
			res.m_detail = "起床时";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_brushThumb = R.drawable.__the__57552016081610450111705586;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_brushIDArr = new int[]{107,106,105,104};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 663;
			res.m_name = "洗完澡";
			res.m_tjId = 1069986;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 114;
			res.m_detail = "洗完澡";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_brushThumb = R.drawable.__the__38152016081518393627171427;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_brushIDArr = new int[]{97,100,99,98};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 635;
			res.m_name = "Trend Style";
			res.m_tjId = 1069566;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 123;
			res.m_detail = "Trend Style";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_frameThumb = R.drawable.__the__49672016071413574381337827;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_frameIDArr = new int[]{774,773,772};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 629;
			res.m_name = "幻彩空间";
			res.m_tjId = 1069548;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 124;
			res.m_detail = "幻彩空间";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_frameThumb = R.drawable.__the__74822016071221282182677060;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_frameIDArr = new int[]{767,766};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 630;
			res.m_name = "Free Zone";
			res.m_tjId = 1069540;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 125;
			res.m_detail = "Free Zone";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_frameThumb = R.drawable.__the__45282016071221302091202072;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_frameIDArr = new int[]{769,768};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 631;
			res.m_name = "手账涂绘";
			res.m_tjId = 1069549;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 126;
			res.m_detail = "手账涂绘";
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_frameThumb = R.drawable.__the__12382016071221311261745840;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_frameIDArr = new int[]{771,770};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 28;
			res.m_name = "少女杂志";
			res.m_tjId = 0;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 187;
			res.m_detail = "";
			res.m_decorateThumb = R.drawable.__the__11222015020516391811051197;
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_decorateIDArr = new int[]{421,420,419,418,417,416,415,414,413,412,411,410,409,408,407,406,405,404,402,401,403};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 29;
			res.m_name = "心情日记";
			res.m_tjId = 0;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 188;
			res.m_detail = "";
			res.m_decorateThumb = R.drawable.__the__64472015020516411650459178;
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_decorateIDArr = new int[]{441,440,439,438,437,436,435,434,433,432,431,430,429,428,427,426,425,424,423,422};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 9;
			res.m_name = "CUTE";
			res.m_tjId = 1065978;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 189;
			res.m_detail = "CUTE";
			res.m_decorateThumb = R.drawable.__the__56922015012718240861920620;
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_decorateIDArr = new int[]{241,240,239,238,237,236,235,234,233,232,231,230,229,228,227,226,225,224};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 10;
			res.m_name = "I.T girl";
			res.m_tjId = 1065979;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 190;
			res.m_detail = "I.T girl";
			res.m_decorateThumb = R.drawable.__the__31912015012718555554917259;
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_decorateIDArr = new int[]{264,263,262,261,260,259,258,257,256,255,254,253,252,251,250,249,248,247,246,245,244,243,242};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 11;
			res.m_name = "复古趴";
			res.m_tjId = 1065980;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 191;
			res.m_detail = "复古趴";
			res.m_decorateThumb = R.drawable.__the__94142015012718573269272437;
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_decorateIDArr = new int[]{286,285,284,283,282,281,280,279,278,277,276,275,274,273,272,271,270,269,268,267,266,265};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 12;
			res.m_name = "旅行记";
			res.m_tjId = 1065977;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 192;
			res.m_detail = "旅行记";
			res.m_decorateThumb = R.drawable.__the__99962015012718594447416865;
			res.m_makeupColor = 0xFF000000 | 0x0;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_decorateIDArr = new int[]{303,302,301,300,299,298,297,296,295,294,293,292,291,290,289,288,287};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 199;
			res.m_name = "裸妆物语";
			res.m_tjId = 0;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 193;
			res.m_detail = "";
			res.m_makeupThumb = R.drawable.__the__19102015081316250219209713;
			res.m_makeupColor = 0xFF000000 | 0xfff0c9b8;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_makeupIDArr = new int[]{121,150,141};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 202;
			res.m_name = "彩妆诱惑";
			res.m_tjId = 0;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 194;
			res.m_detail = "";
			res.m_makeupThumb = R.drawable.__the__46472015081316252736800499;
			res.m_makeupColor = 0xFF000000 | 0xffff3c45;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_makeupIDArr = new int[]{123,134,165};
			out.add(res);

			res = new ThemeRes();
			res.m_id = 200;
			res.m_name = "粉漾少女";
			res.m_tjId = 0;
			res.m_tjShowId = 0;
			res.m_isBusiness = false;
			res.m_isHide = true;
			res.m_order = 195;
			res.m_detail = "";
			res.m_makeupThumb = R.drawable.__the__82232015081316245090382031;
			res.m_makeupColor = 0xFF000000 | 0xfff79da3;
			res.m_type = BaseRes.TYPE_LOCAL_RES;
			res.m_filter_mask_color = 0xFF000000 | 0x0;
			res.m_makeupIDArr = new int[]{142,131};
			out.add(res);
		}
		return out;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return 0;
	}

	@Override
	protected ThemeRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		ThemeRes out = null;

		if(jsonObj != null)
		{
			try
			{
				Object obj;

				out = new ThemeRes();
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
					out.m_id = (int)Long.parseLong(temp, 10);
				}
				else
				{
					out.m_id = (int)(Math.random() * 10000000);
				}
				out.m_name = jsonObj.getString("name");
				out.m_detail = jsonObj.getString("detail");
				if(isPath)
				{
					out.m_thumb = jsonObj.getString("icon");
					out.m_decorateThumb = jsonObj.getString("decorate_icon");
					out.m_pic = jsonObj.getString("pic");
					if(jsonObj.has("makeup_icon"))
					{
						out.m_makeupThumb = jsonObj.get("makeup_icon");
					}
					if(jsonObj.has("frame_icon"))
					{
						out.m_frameThumb = jsonObj.get("frame_icon");
					}
					if(jsonObj.has("brush_icon"))
					{
						out.m_brushThumb = jsonObj.get("brush_icon");
					}
					if(jsonObj.has("filter_list_icon"))
					{
						if(jsonObj.get("filter_list_icon") instanceof JSONArray)
						{
							JSONArray temps = (JSONArray)jsonObj.get("filter_list_icon");
							if(temps.length() > 0)
							{
								String[] filter_ress = new String[temps.length()];
								for(int i = 0; i < temps.length(); i++)
								{
									filter_ress[i] = temps.getString(i);
								}
								out.m_filter_theme_icon_res = filter_ress;
							}
						}
					}
					if(jsonObj.has("filter_icon"))
					{
						out.m_filter_thumb_res = jsonObj.get("filter_icon");
					}
				}
				else
				{
					out.url_thumb = jsonObj.getString("icon");
					out.url_decorateThumb = jsonObj.getString("decorate_icon");
					out.url_pic = jsonObj.getString("pic");
					if(jsonObj.has("makeup_icon"))
					{
						out.url_makeupThumb = jsonObj.getString("makeup_icon");
					}
					if(jsonObj.has("frame_icon"))
					{
						out.url_frameThumb = jsonObj.getString("frame_icon");
					}
					if(jsonObj.has("brush_icon"))
					{
						out.url_brushThumb = jsonObj.getString("brush_icon");
					}
					if(jsonObj.has("filter_list_icon"))
					{
						if(jsonObj.get("filter_list_icon") instanceof JSONArray)
						{
							JSONArray temps = (JSONArray)jsonObj.get("filter_list_icon");
							if(temps.length() > 0)
							{
								String[] filter_urls = new String[temps.length()];
								for(int i = 0; i < temps.length(); i++)
								{
									filter_urls[i] = temps.getString(i);
								}
								out.m_filter_theme_icon_url = filter_urls;
							}
						}
					}
					if(jsonObj.has("filter_icon"))
					{
						out.m_filter_thumb_url = jsonObj.getString("filter_icon");
					}
				}

				if(jsonObj.has("filter_list_name"))
				{
					if(jsonObj.get("filter_list_name") instanceof JSONArray)
					{
						JSONArray temps = (JSONArray)jsonObj.get("filter_list_name");
						if(temps.length() > 0)
						{
							String[] filter_name = new String[temps.length()];
							for(int i = 0; i < temps.length(); i++)
							{
								filter_name[i] = temps.getString(i);
							}
							out.m_filter_theme_name = filter_name;
						}
					}
				}

				if(jsonObj.has("filter_detail"))
				{
					out.m_filterDetail = jsonObj.getString("filter_detail");
				}
				if(jsonObj.has("filter_name"))
				{
					out.m_filterName = jsonObj.getString("filter_name");
				}
				if(jsonObj.has("filter_mask_color"))
				{
					obj = jsonObj.get("filter_mask_color");
					if(obj instanceof String)
					{
						temp = (String)obj;
						if(temp.length() > 0)
						{
							out.m_filter_mask_color = 0xFF000000 | (int)Long.parseLong(temp, 16);
						}
					}
				}

				obj = jsonObj.get("makeup_mask_color");
				if(obj instanceof String)
				{
					temp = (String)obj;
					if(temp.length() > 0)
					{
						out.m_makeupColor = 0xFF000000 | (int)Long.parseLong(temp, 16);
					}
				}
				out.m_tjId = jsonObj.getInt("tj_id");
				out.m_tjShowId = jsonObj.getInt("show_id");
				if(jsonObj.has("tj_link") && (obj = jsonObj.get("tj_link")) instanceof String)
				{
					out.m_tjLink = (String)obj;
				}
				out.m_order = jsonObj.getInt("order");
				temp = jsonObj.getString("is_hide");
				if(temp != null && temp.length() > 0)
				{
					int value = (int)Long.parseLong(temp);
					out.m_isHide = (value == 0) ? false : true;
				}
				temp = jsonObj.getString("is_business");
				if(temp != null && temp.length() > 0)
				{
					int value = (int)Long.parseLong(temp);
					out.m_isBusiness = (value == 0) ? false : true;
				}
				obj = jsonObj.get("content");
				if(obj instanceof JSONObject)
				{
					JSONObject jsonObj2 = (JSONObject)obj;
					if(jsonObj2.has("decorate"))
					{
						out.m_decorateIDArr = ResourceUtils.ParseIds(jsonObj2.getString("decorate"), 16);
					}
					if(jsonObj2.has("frame"))
					{
						out.m_frameIDArr = ResourceUtils.ParseIds(jsonObj2.getString("frame"), 16);
					}
					if(jsonObj2.has("card"))
					{
						out.m_cardIDArr = ResourceUtils.ParseIds(jsonObj2.getString("card"), 16);
					}
					if(jsonObj2.has("cosmetics_group"))
					{
						out.m_makeupIDArr = ResourceUtils.ParseIds(jsonObj2.getString("cosmetics_group"), 16);
					}
					if(jsonObj2.has("glass"))
					{
						out.m_glassIDArr = ResourceUtils.ParseIds(jsonObj2.getString("glass"), 10);
					}
					if(jsonObj2.has("mosaic"))
					{
						out.m_mosaicIDArr = ResourceUtils.ParseIds(jsonObj2.getString("mosaic"), 10);
					}
					if(jsonObj2.has("finger"))
					{
						out.m_brushIDArr = ResourceUtils.ParseIds(jsonObj2.getString("finger"), 10);
					}
					if(jsonObj2.has("sframe"))
					{
						out.m_sFrameIDArr = ResourceUtils.ParseIds(jsonObj2.getString("sframe"), 10);
					}
					if(jsonObj2.has("filter"))
					{
						out.m_filterIDArr = ResourceUtils.ParseIds(jsonObj2.getString("filter"), 10);
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
	protected void sync_raw_SaveSdcardRes(Context context, ArrayList<ThemeRes> arr)
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
						ThemeRes res;
						JSONObject jsonObj;
						JSONObject jsonObj2;
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
									jsonObj.put("name", res.m_name);
								}
								else
								{
									jsonObj.put("name", "");
								}
								if(res.m_detail != null)
								{
									jsonObj.put("detail", res.m_detail);
								}
								else
								{
									jsonObj.put("detail", "");
								}
								if(res.m_thumb instanceof String)
								{
									jsonObj.put("icon", res.m_thumb);
								}
								else
								{
									jsonObj.put("icon", "");
								}
								if(res.m_decorateThumb instanceof String)
								{
									jsonObj.put("decorate_icon", res.m_decorateThumb);
								}
								else
								{
									jsonObj.put("decorate_icon", "");
								}
								if(res.m_pic instanceof String)
								{
									jsonObj.put("pic", res.m_pic);
								}
								else
								{
									jsonObj.put("pic", "");
								}
								if(res.m_makeupThumb instanceof String)
								{
									jsonObj.put("makeup_icon", res.m_makeupThumb);
								}
								else
								{
									jsonObj.put("makeup_icon", "");
								}
								jsonObj.put("makeup_mask_color", Integer.toHexString(res.m_makeupColor));
								if(res.m_frameThumb instanceof String)
								{
									jsonObj.put("frame_icon", res.m_frameThumb);
								}
								else
								{
									jsonObj.put("frame_icon", "");
								}
								jsonObj.put("filter_mask_color", Integer.toHexString(res.m_filter_mask_color));
								if(res.m_brushThumb instanceof String)
								{
									jsonObj.put("brush_icon", res.m_brushThumb);
								}
								else
								{
									jsonObj.put("brush_icon", "");
								}
								jsonObj.put("tj_id", res.m_tjId);
								if(res.m_tjLink != null)
								{
									jsonObj.put("tj_link", res.m_tjLink);
								}
								jsonObj.put("show_id", res.m_tjShowId);
								jsonObj.put("order", res.m_order);
								jsonObj.put("is_hide", res.m_isHide ? 1 : 0);
								jsonObj.put("is_business", res.m_isBusiness ? 1 : 0);
								//滤镜信息
								jsonObj.put("filter_detail", res.m_filterDetail);
								jsonObj.put("filter_name", res.m_filterName);
								if(res.m_filter_theme_icon_res instanceof String[])
								{
									if(res.m_filter_theme_icon_res != null && res.m_filter_theme_icon_res.length > 0)
									{
										JSONArray arrays = new JSONArray();
										for(int j = 0; j < res.m_filter_theme_icon_res.length; j++)
										{
											arrays.put(res.m_filter_theme_icon_res[j]);
										}
										jsonObj.put("filter_list_icon", arrays);
									}
								}
								if(res.m_filter_theme_name != null && res.m_filter_theme_name.length > 0)
								{
									JSONArray arrays = new JSONArray();
									for(int j = 0; j < res.m_filter_theme_name.length; j++)
									{
										arrays.put(res.m_filter_theme_name[j]);
									}
									jsonObj.put("filter_list_name", arrays);
								}
								if(res.m_filter_thumb_res instanceof String)
								{
									jsonObj.put("filter_icon", res.m_filter_thumb_res);
								}
								else
								{
									jsonObj.put("filter_icon", "");
								}
								jsonObj2 = new JSONObject();
								{
									if(res.m_decorateIDArr != null && res.m_decorateIDArr.length > 0)
									{
										jsonObj2.put("decorate", ResourceUtils.MakeStr(res.m_decorateIDArr, 16));
									}
									if(res.m_frameIDArr != null && res.m_frameIDArr.length > 0)
									{
										jsonObj2.put("frame", ResourceUtils.MakeStr(res.m_frameIDArr, 16));
									}
									if(res.m_cardIDArr != null && res.m_cardIDArr.length > 0)
									{
										jsonObj2.put("card", ResourceUtils.MakeStr(res.m_cardIDArr, 16));
									}
									if(res.m_makeupIDArr != null && res.m_makeupIDArr.length > 0)
									{
										jsonObj2.put("cosmetics_group", ResourceUtils.MakeStr(res.m_makeupIDArr, 16));
									}
									if(res.m_glassIDArr != null && res.m_glassIDArr.length > 0)
									{
										jsonObj2.put("glass", ResourceUtils.MakeStr(res.m_glassIDArr, 10));
									}
									if(res.m_mosaicIDArr != null && res.m_mosaicIDArr.length > 0)
									{
										jsonObj2.put("mosaic", ResourceUtils.MakeStr(res.m_mosaicIDArr, 10));
									}
									if(res.m_brushIDArr != null && res.m_brushIDArr.length > 0)
									{
										jsonObj2.put("finger", ResourceUtils.MakeStr(res.m_brushIDArr, 10));
									}
									if(res.m_sFrameIDArr != null && res.m_sFrameIDArr.length > 0)
									{
										jsonObj2.put("sframe", ResourceUtils.MakeStr(res.m_sFrameIDArr, 10));
									}
									if(res.m_filterIDArr != null && res.m_filterIDArr.length > 0)
									{
										jsonObj2.put("filter", ResourceUtils.MakeStr(res.m_filterIDArr, 10));
									}
								}
								jsonObj.put("content", jsonObj2);
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
		return EventID.THEME_CLOUD_OK;
	}

	@Override
	protected void RebuildNetResArr(ArrayList<ThemeRes> dst, ArrayList<ThemeRes> src)
	{
		if(dst != null && src != null)
		{
			ThemeRes srcTemp;
			ThemeRes dstTemp;
			Class cls = ThemeRes.class;
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
					dstTemp.m_decorateThumb = srcTemp.m_decorateThumb;
					dstTemp.m_makeupThumb = srcTemp.m_makeupThumb;
					dstTemp.m_frameThumb = srcTemp.m_frameThumb;
					dstTemp.m_brushThumb = srcTemp.m_brushThumb;
					dstTemp.m_filter_thumb_res = srcTemp.m_filter_thumb_res;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.m_pic = srcTemp.m_pic;

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

	protected static void BuildThemeArr(ArrayList<ThemeRes> dst, ArrayList<ThemeRes> src)
	{
		if(dst != null && src != null)
		{
			ThemeRes srcTemp;
			ThemeRes dstTemp;
			int index;
			int len = src.size();
			for(int i = 0; i < len; i++)
			{
				srcTemp = src.get(i);
				index = ResourceUtils.HasItem(dst, srcTemp.m_id);
				if(index >= 0)
				{
					dstTemp = dst.get(index);
					dstTemp.m_type = srcTemp.m_type;
					dstTemp.m_decorateThumb = srcTemp.m_decorateThumb;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.m_pic = srcTemp.m_pic;
				}
			}
		}
	}

	//选择排序算法
	public static void SelectSort(ArrayList<ThemeRes> args)
	{
		if(args != null)
		{
			int len = args.size();
			for(int i = 0; i < len - 1; i++)
			{
				int min = i;
				for(int j = i + 1; j < len; j++)
				{
					if(args.get(min).m_order > args.get(j).m_order)
					{
						min = j;
					}
				}
				if(min != i)
				{
					ThemeRes temp = args.get(i);
					args.set(i, args.get(min));
					args.set(min, temp);
				}
			}
		}
	}

	/**
	 * 增加素材类型后需要继续完善
	 *
	 * @param res
	 * @return
	 */
	protected static boolean IsEmpty(ThemeRes res)
	{
		boolean out = true;

		if(res != null)
		{
			Context context = MyFramework2App.getInstance().getApplicationContext();
			if(out && res.m_frameIDArr != null)
			{
				if(ResourceUtils.GetItems(FrameResMgr2.getInstance().sync_GetSdcardRes(context, null), res.m_frameIDArr).size() == res.m_frameIDArr.length)
				{
					out = false;
				}
			}
			if(out && res.m_makeupIDArr != null)
			{
				if(ResourceUtils.GetItems(MakeupComboResMgr2.getInstance().sync_GetSdcardRes(context, null), res.m_makeupIDArr).size() == res.m_makeupIDArr.length)
				{
					out = false;
				}
			}
			if(out && res.m_decorateIDArr != null)
			{
				if(ResourceUtils.GetItems(DecorateResMgr2.getInstance().sync_GetSdcardRes(context, null), res.m_decorateIDArr).size() == res.m_decorateIDArr.length)
				{
					out = false;
				}
			}
			if(out && res.m_glassIDArr != null)
			{
				if(ResourceUtils.GetItems(GlassResMgr2.getInstance().sync_GetSdcardRes(context, null), res.m_glassIDArr).size() == res.m_glassIDArr.length)
				{
					out = false;
				}
			}
			if(out && res.m_mosaicIDArr != null)
			{
				if(ResourceUtils.GetItems(MosaicResMgr2.getInstance().sync_GetSdcardRes(context, null), res.m_mosaicIDArr).size() == res.m_mosaicIDArr.length)
				{
					out = false;
				}
			}
			if(out && res.m_filterIDArr != null)
			{
				if(ResourceUtils.GetItems(FilterResMgr2.getInstance().sync_GetSdcardRes(context, null), res.m_filterIDArr).size() == res.m_filterIDArr.length)
				{
					out = false;
				}
			}
			//添加其他类型素材后需要完善
		}

		return out;
	}

	@Override
	protected String GetOldIdFlag()
	{
		return OLD_ID_FLAG;
	}

	@Override
	protected void UpdateOldId(ArrayList<ThemeRes> newArr)
	{
		super.UpdateOldId(newArr);

		if(Looper.getMainLooper() == Looper.myLooper())
		{
			UpdateOldId2();
		}
		else
		{
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					UpdateOldId2();
				}
			});
		}
	}

	protected void UpdateOldId2()
	{
		//更新标志
		if(m_hasNewRes)
		{
			if(sNewFlagHolder != null)
			{
				Callback cb = sNewFlagHolder.GetObj();
				if(cb != null)
				{
					cb.UpdateNewFlag(m_hasNewRes);
				}
			}
		}
	}

	@Override
	public ArrayList<ThemeRes> sync_GetCloudCacheRes(Context context, DataFilter filter)
	{
		ArrayList<ThemeRes> arr = mCloudResArr;
		ArrayList<ThemeRes> arr2 = super.sync_GetCloudCacheRes(context, filter);

		synchronized(CLOUD_MEM_LOCK)
		{
			if(arr != arr2 && arr2 != null)
			{
				//数据有刷新,通常是第一次
				RebuildNetResArr(arr2, arr);
				BuildThemeArr(arr2, sync_GetLocalRes(context, filter));
				BuildThemeArr(arr2, sync_GetSdcardRes(context, filter));
				SelectSort(arr2);
			}
		}

		return arr2;
	}

	@Override
	protected void sync_ui_CloudResChange(ArrayList<ThemeRes> oldArr, ArrayList<ThemeRes> newArr)
	{
		super.sync_ui_CloudResChange(oldArr, newArr);

		if(newArr != null && newArr.size() > 0)
		{
			Context context = MyFramework2App.getInstance().getApplicationContext();
			BuildThemeArr(newArr, sync_GetLocalRes(context, null));
			BuildThemeArr(newArr, sync_GetSdcardRes(context, null));
			SelectSort(newArr);
		}
	}

	public ArrayList<ThemeRes> GetAllResArr()
	{
		ArrayList<ThemeRes> out = sync_ar_GetCloudCacheRes(MyFramework2App.getInstance().getApplication(), null);

		if(out == null || out.size() <= 0)
		{
			out = new ArrayList<>();

			ArrayList<ThemeRes> localArr = sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null);
			ArrayList<ThemeRes> sdcardArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);

			if(localArr != null)
			{
				out.addAll(localArr);
			}
			if(sdcardArr != null)
			{
				out.addAll(sdcardArr);
			}
			SelectSort(out);
		}

		return out;
	}

	@Override
	public ThemeRes GetItem(ArrayList<ThemeRes> arr, int id)
	{
		return ResourceUtils.GetItem(arr, id);
	}

	@Override
	public ThemeRes GetRes(int id, boolean onlyCanUse)
	{
		ThemeRes out = null;

		ArrayList<ThemeRes> arr;
		if((arr = sync_ar_GetCloudCacheRes(MyFramework2App.getInstance().getApplication(), null)) != null)
		{
			out = ResourceUtils.GetItem(arr, id);
		}
		if(out == null && (arr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null)) != null)
		{
			out = ResourceUtils.GetItem(arr, id);
		}
		if(out == null && (arr = sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null)) != null)
		{
			out = ResourceUtils.GetItem(arr, id);
		}

		return out;
	}

	public void ClearEmptyRes(Context context, ThemeRes res)
	{
		if(res != null && res.m_id != BaseRes.NONE_ID)
		{
			//不在服务器上的主题才能删除
			ArrayList<ThemeRes> downloadArr = sync_ar_GetCloudCacheRes(context, null);
			ThemeRes temp = ResourceUtils.GetItem(downloadArr, res.m_id);
			if(temp == null && IsEmpty(res))
			{
				ArrayList<ThemeRes> sdcardArr = sync_GetSdcardRes(context, null);
				temp = ResourceUtils.DeleteItem(sdcardArr, res.m_id);
				if(temp != null && temp.m_type == BaseRes.TYPE_LOCAL_PATH)
				{
					temp.m_type = BaseRes.TYPE_NETWORK_URL;
					sync_SaveSdcardRes(context, sdcardArr);
				}
			}
		}
	}

	//*************给首页调用*********************
	public interface Callback
	{
		void UpdateNewFlag(boolean hasNew);
	}

	private static ObjHolder<Callback> sNewFlagHolder;
	public static boolean CheckNewFlag(Context context, ObjHolder<Callback> holder)
	{
		ThemeResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null, new CallbackHolder<ArrayList<ThemeRes>>(null));
		sNewFlagHolder = holder;
		return ThemeResMgr2.getInstance().m_hasNewRes;
	}

	public static void ClearNewFlag(Context context)
	{
		if(ThemeResMgr2.getInstance().m_hasNewRes)
		{
			ThemeResMgr2.getInstance().ClearOldId(context);
		}
	}

	/**
	 * 退出清理
	 */
	public static void ClearHolder()
	{
		sNewFlagHolder = null;
	}
	//*************end 给首页调用*****************
}

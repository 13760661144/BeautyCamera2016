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
 * Created by Raining on 2017/10/7.
 */

public class FrameResMgr2 extends BaseResMgr<FrameRes, SparseArray<FrameRes>>
{
	public final static int NEW_JSON_VER = 3;
	public final static int NEW_ORDER_JSON_VER = 4;

	protected final String SDCARD_PATH = DownloadMgr.getInstance().FRAME_PATH + "/frame.xxxx"; //资源集合

	protected final String ORDER_PATH = DownloadMgr.getInstance().FRAME_PATH + "/order.xxxx"; //显示的item&排序(不存在这里的id不会显示)

	protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().FRAME_PATH + "/cache.xxxx";
	protected final String CLOUD_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=frames_meirenxiangjiv2.2.0";// + "?random=" + Math.random();
	protected final String CLOUD_TEST_URL = "http://beauty-material.adnonstop.com/API/beauty_camera/json_api/android.php?typename=frames_88.8.8";

	public final static String NEW_DOWNLOAD_FLAG = "frame2"; //记录在Preferences
	public final ArrayList<Integer> new_flag_arr = new ArrayList<>(); //新下载显示new状态
	public final static String OLD_ID_FLAG = "frame_id"; //判断是否有新素材更新

	private static FrameResMgr2 sInstance;

	private FrameResMgr2()
	{
	}

	public synchronized static FrameResMgr2 getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new FrameResMgr2();
		}
		return sInstance;
	}

	@Override
	public int GetResArrSize(SparseArray<FrameRes> arr)
	{
		return arr.size();
	}

	@Override
	public SparseArray<FrameRes> MakeResArrObj()
	{
		return new SparseArray<>();
	}

	@Override
	public boolean ResArrAddItem(SparseArray<FrameRes> arr, FrameRes item)
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
	protected SparseArray<FrameRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
	{
		SparseArray<FrameRes> out = new SparseArray<>();

		FrameRes res;

		res = new FrameRes();
		res.m_id = 766;
		res.m_name = "几何相框1";
		res.m_thumb = R.drawable.__fra__72942016071414284431863022_120;
		res.m_tjId = 1069534;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__1234211505359752;
		res.f3_4 = R.drawable.__fra__1234211509638696;
		res.f4_3 = R.drawable.__fra__1234211546318687;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 767;
		res.m_name = "几何相框2";
		res.m_thumb = R.drawable.__fra__80972016071221171375678623_120;
		res.m_tjId = 1069535;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__1234211720656923;
		res.f3_4 = R.drawable.__fra__1234211723581059;
		res.f4_3 = R.drawable.__fra__1234211733151627;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 768;
		res.m_name = "Free相框1";
		res.m_thumb = R.drawable.__fra__84622016071221175850831080_120;
		res.m_tjId = 1069545;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__1234211808203609;
		res.f3_4 = R.drawable.__fra__1234211810326641;
		res.f4_3 = R.drawable.__fra__1234211812408623;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 769;
		res.m_name = "Free相框2";
		res.m_thumb = R.drawable.__fra__28892016071221192192292512_120;
		res.m_tjId = 1069544;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__1234211929680850;
		res.f3_4 = R.drawable.__fra__1234211932335033;
		res.f4_3 = R.drawable.__fra__1234212517295851;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 770;
		res.m_name = "影像相框1";
		res.m_thumb = R.drawable.__fra__13222016071221203552157414_120;
		res.m_tjId = 1069542;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__1234212046839929;
		res.f3_4 = R.drawable.__fra__1234212049775967;
		res.f4_3 = R.drawable.__fra__1234212052277338;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 771;
		res.m_name = "影像相框2";
		res.m_thumb = R.drawable.__fra__34922016071221215048238648_120;
		res.m_tjId = 1069543;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__1234212200499698;
		res.f3_4 = R.drawable.__fra__1234212202758629;
		res.f4_3 = R.drawable.__fra__1234212205110698;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 772;
		res.m_name = "线条相框1";
		res.m_thumb = R.drawable.__fra__43742016071412183452767672_120;
		res.m_tjId = 1069561;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__123412184645905;
		res.f3_4 = R.drawable.__fra__123412184996827;
		res.f4_3 = R.drawable.__fra__1234121851558525;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 773;
		res.m_name = "线条相框2";
		res.m_thumb = R.drawable.__fra__4166201607141219436939993_120;
		res.m_tjId = 1069562;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__123412193717736;
		res.f3_4 = R.drawable.__fra__1234121945357569;
		res.f4_3 = R.drawable.__fra__1234121949655815;
		out.put(res.m_id, res);

		res = new FrameRes();
		res.m_id = 774;
		res.m_name = "线条相框3";
		res.m_thumb = R.drawable.__fra__62742016071412210398476218_120;
		res.m_tjId = 1069563;
		res.m_type = BaseRes.TYPE_LOCAL_RES;
		res.m_bkColor = 0xffffffff;
		res.m_frameType = FrameRes.FRAME_TYPE_IMAGE;
		res.f1_1 = R.drawable.__fra__123412210754343;
		res.f3_4 = R.drawable.__fra__1234122113891695;
		res.f4_3 = R.drawable.__fra__1234122116724224;
		out.put(res.m_id, res);

		return out;
	}

	private static int sFrameFlag = 0;

	public static int GetFrameCoverIcon(int themeId)
	{
		int out = 0;

		switch(themeId)
		{
			case 640:
				out = R.drawable.__nfra__56412016072110325260198046;
				break;

			case 638:
				out = R.drawable.__nfra__84142016071915433097977543;
				break;

			case 594:
				out = R.drawable.__nfra__70692016062909371743062059;
				break;

			case 636:
				out = R.drawable.__nfra__44822016071417512969143139;
				break;

			case 628:
				out = R.drawable.__nfra__80982016071310110110539010;
				break;

			case 626:
				out = R.drawable.__nfra__63652016070711015541081296;
				break;

			case 624:
				out = R.drawable.__nfra__3135201607051744234896319;
				break;

			case 620:
				out = R.drawable.__nfra__57442016062910285999268531;
				break;

			case 618:
				out = R.drawable.__nfra__9631201606290927484361421;
				break;

			case 616:
				out = R.drawable.__nfra__42262016062909295140376328;
				break;

			case 622:
				out = R.drawable.__nfra__79622016063010072160874316;
				break;

			case 612:
				out = R.drawable.__nfra__75762016062909313058813872;
				break;

			case 608:
				out = R.drawable.__nfra__1181201606290932103539612;
				break;

			case 606:
				out = R.drawable.__nfra__5939201606290934319003215;
				break;

			case 602:
				out = R.drawable.__nfra__16962016062909345825361776;
				break;

			case 600:
				out = R.drawable.__nfra__64912016062909352817636953;
				break;

			case 592:
				out = R.drawable.__nfra__35082016062909390344808513;
				break;

			case 591:
				out = R.drawable.__nfra__60962016062909393865931517;
				break;

			case 355:
				out = R.drawable.__nfra__12812016062909400260114226;
				break;

			case 354:
				out = R.drawable.__nfra__85352016062909413397932881;
				break;

			case 352:
				out = R.drawable.__nfra__38302016062909415441605983;
				break;

			case 338:
				out = R.drawable.__nfra__30552016062909440020725197;
				break;

			case 350:
				out = R.drawable.__nfra__92822016062909442593442649;
				break;

			case 334:
				out = R.drawable.__nfra__83162016062909444917809758;
				break;

			case 332:
				out = R.drawable.__nfra__47442016062909451330732722;
				break;

			case 319:
				out = R.drawable.__nfra__56502016062909453799825131;
				break;

			case 330:
				out = R.drawable.__nfra__65982016062909462994472988;
				break;

			case 329:
				out = R.drawable.__nfra__39572016062909470338955860;
				break;

			case 327:
				out = R.drawable.__nfra__17852016062909473124586363;
				break;

			case 326:
				out = R.drawable.__nfra__592420160629094815781842;
				break;

			case 324:
				out = R.drawable.__nfra__44222016062909484873085372;
				break;

			case 321:
				out = R.drawable.__nfra__93452016062909491318992723;
				break;

			case 318:
				out = R.drawable.__nfra__655920160629095024938818;
				break;

			case 315:
				out = R.drawable.__nfra__44202016062909520771957655;
				break;

			case 294:
				out = R.drawable.__nfra__81402016062909531374207424;
				break;

			case 292:
				out = R.drawable.__nfra__35382016062909565096573748;
				break;

			case 289:
				out = R.drawable.__nfra__5707201606290957189473051;
				break;

			case 286:
				out = R.drawable.__nfra__73282016062909575861884336;
				break;

			case 285:
				out = R.drawable.__nfra__90242016062909583776818934;
				break;

			case 283:
				out = R.drawable.__nfra__44782016062909593152452366;
				break;

			case 279:
				out = R.drawable.__nfra__44332016062910011334152671;
				break;

			case 277:
				out = R.drawable.__nfra__32052016062910031722435365;
				break;

			case 275:
				out = R.drawable.__nfra__10192016062910034637384611;
				break;

			case 215:
				out = R.drawable.__nfra__13372016062910040968883078;
				break;

			case 211:
				out = R.drawable.__nfra__45682016062910060883786791;
				break;

			case 207:
				out = R.drawable.__nfra__48162016062910064962962763;
				break;

			case 208:
				out = R.drawable.__nfra__84892016062910074953859564;
				break;

			case 205:
				out = R.drawable.__nfra__15542016062910082121092216;
				break;

			case 59:
				out = R.drawable.__nfra__15542016062910082121092216;
				break;

			case 60:
				out = R.drawable.__nfra__68002016062910093416651375;
				break;

			case 57:
				out = R.drawable.__nfra__58242016062910095798482752;
				break;

			case 54:
				out = R.drawable.__nfra__1582201606291010234197881;
				break;

			case 52:
				out = R.drawable.__nfra__90292016062910111722226423;
				break;

			case 51:
				out = R.drawable.__nfra__11742016062910114178834177;
				break;

			case 47:
				out = R.drawable.__nfra__46852016062910121272546687;
				break;

			case 46:
				out = R.drawable.__nfra__44062016062910124366406196;
				break;

			case 44:
				out = R.drawable.__nfra__88302016062910133963426458;
				break;

			case 42:
				out = R.drawable.__nfra__74682016062910141924729387;
				break;

			case 4:
				out = R.drawable.__nfra__46212016062910145234817272;
				break;

			case 39:
				out = R.drawable.__nfra__80762016062910151651202681;
				break;

			case 6:
				out = R.drawable.__nfra__19282016062910154641667459;
				break;

			case 37:
				out = R.drawable.__nfra__87222016062910160861693748;
				break;

			case 36:
				out = R.drawable.__nfra__52152016062910165013641098;
				break;

			case 5:
				out = R.drawable.__nfra__62782016062910172012535130;
				break;

			case 7:
				out = R.drawable.__nfra__76682016062910183127847526;
				break;

			case 45:
				out = R.drawable.__nfra__68322016062910191328151475;
				break;

			case 8:
				out = R.drawable.__nfra__14682016062910195592872480;
				break;

			case 309:
				out = R.drawable.__nfra__57002016062910202250104251;
				break;

			case 303:
				out = R.drawable.__nfra__99352016062910210076265116;
				break;

			case 300:
				out = R.drawable.__nfra__47002016062910361046536173;
				break;

			case 316:
				out = R.drawable.__nfra__13532016062910223278568015;
				break;

			default:
				switch(sFrameFlag % 3)
				{
					case 0:
						out = R.drawable.__nfra__1;
						break;
					case 1:
						out = R.drawable.__nfra__2;
						break;
					default:
						out = R.drawable.__nfra__3;
						break;
				}
				sFrameFlag++;
				break;
		}
		return out;
	}

	@Override
	public boolean CheckIntact(FrameRes res)
	{
		boolean out = false;

		if(res != null)
		{
			if(ResourceUtils.HasIntact(res.m_thumb) && ResourceUtils.HasIntact(res.f1_1, res.f4_3, res.f3_4, res.f16_9, res.f9_16))
			{
				out = true;
			}
		}

		return out;
	}

	@Override
	protected void sync_raw_SaveSdcardRes(Context context, SparseArray<FrameRes> arr)
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
						FrameRes res;
						JSONObject jsonObj;
						JSONArray jsonArr2;
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
								jsonObj.put("bkcolor", "#" + Integer.toHexString(res.m_bkColor & 0xFFFFFF));
								jsonArr2 = new JSONArray();
								{
									if(res.f3_4 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "3_4");
										jsonObj2.put("img", res.f3_4);
										jsonArr2.put(jsonObj2);
									}
									if(res.f4_3 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "4_3");
										jsonObj2.put("img", res.f4_3);
										jsonArr2.put(jsonObj2);
									}
									if(res.f9_16 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "9_16");
										jsonObj2.put("img", res.f9_16);
										jsonArr2.put(jsonObj2);
									}
									if(res.f16_9 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "16_9");
										jsonObj2.put("img", res.f16_9);
										jsonArr2.put(jsonObj2);
									}
									if(res.f1_1 != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "1_1");
										jsonObj2.put("img", res.f1_1);
										jsonArr2.put(jsonObj2);
									}
									if(res.f_bk != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "bk");
										jsonObj2.put("img", res.f_bk);
										jsonArr2.put(jsonObj2);
									}
									if(res.f_top != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "top");
										jsonObj2.put("img", res.f_top);
										jsonArr2.put(jsonObj2);
									}
									if(res.f_middle != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "middle");
										jsonObj2.put("img", res.f_middle);
										jsonArr2.put(jsonObj2);
									}
									if(res.f_bottom != null)
									{
										jsonObj2 = new JSONObject();
										jsonObj2.put("type", "bottom");
										jsonObj2.put("img", res.f_bottom);
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
		return EventID.FRAME_CLOUD_OK;
	}

	@Override
	protected int GetNewOrderJsonVer()
	{
		return NEW_ORDER_JSON_VER;
	}

	@Override
	protected FrameRes ReadResItem(JSONObject jsonObj, boolean isPath)
	{
		FrameRes out = null;
		if(jsonObj != null)
		{
			try
			{
				out = new FrameRes();
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
				temp = jsonObj.getString("bkcolor");
				if(temp != null && temp.length() > 0)
				{
					out.m_bkColor = 0xff000000 | (int)Long.parseLong(temp.replace("#", "").trim(), 16);
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
									if(isPath)
									{
										out.f3_4 = jsonObj2.getString("img");
									}
									else
									{
										out.url_f3_4 = jsonObj2.getString("img");
									}
								}
								else if(temp.equals("4_3"))
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
								else if(temp.equals("1_1"))
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
								else if(temp.equals("9_16"))
								{
									if(isPath)
									{
										out.f9_16 = jsonObj2.getString("img");
									}
									else
									{
										out.url_f9_16 = jsonObj2.getString("img");
									}
								}
								else if(temp.equals("16_9"))
								{
									if(isPath)
									{
										out.f16_9 = jsonObj2.getString("img");
									}
									else
									{
										out.url_f16_9 = jsonObj2.getString("img");
									}
								}
								else if(temp.equals("bk"))
								{
									if(isPath)
									{
										out.f_bk = jsonObj2.getString("img");
									}
									else
									{
										out.url_f_bk = jsonObj2.getString("img");
									}
								}
								else if(temp.equals("top"))
								{
									out.m_frameType = FrameRes.FRAME_TYPE_PIECE;
									if(isPath)
									{
										out.f_top = jsonObj2.getString("img");
									}
									else
									{
										out.url_f_top = jsonObj2.getString("img");
									}
								}
								else if(temp.equals("middle"))
								{
									if(isPath)
									{
										out.f_middle = jsonObj2.getString("img");
									}
									else
									{
										out.url_f_middle = jsonObj2.getString("img");
									}
								}
								else if(temp.equals("bottom"))
								{
									if(isPath)
									{
										out.f_bottom = jsonObj2.getString("img");
									}
									else
									{
										out.url_f_bottom = jsonObj2.getString("img");
									}
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
		if(ResourceUtils.RebuildOrder(ThemeResMgr2.getInstance().sync_GetLocalRes(context, null), ThemeResMgr2.getInstance().sync_GetSdcardRes(context, null), dstObj))
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
	public FrameRes GetItem(SparseArray<FrameRes> arr, int id)
	{
		if(arr != null)
		{
			return arr.get(id);
		}
		return null;
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
	protected void RebuildNetResArr(SparseArray<FrameRes> dst, SparseArray<FrameRes> src)
	{
		if(dst != null && src != null)
		{
			FrameRes srcTemp;
			FrameRes dstTemp;
			Class cls = FrameRes.class;
			Field[] fields = cls.getDeclaredFields();
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				dstTemp = dst.valueAt(i);
				srcTemp = src.get(dstTemp.m_id);
				if(srcTemp != null)
				{
					dstTemp.m_type = srcTemp.m_type;
					dstTemp.m_thumb = srcTemp.m_thumb;
					dstTemp.f1_1 = srcTemp.f1_1;
					dstTemp.f4_3 = srcTemp.f4_3;
					dstTemp.f3_4 = srcTemp.f3_4;
					dstTemp.f16_9 = srcTemp.f16_9;
					dstTemp.f9_16 = srcTemp.f9_16;
					dstTemp.f_bk = srcTemp.f_bk;
					dstTemp.f_top = srcTemp.f_top;
					dstTemp.f_middle = srcTemp.f_middle;
					dstTemp.f_bottom = srcTemp.f_bottom;

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
					dst.put(dstTemp.m_id, srcTemp);
				}
			}
		}
	}

	public ArrayList<FrameRes> GetResArr2(int[] ids, boolean onlyCanUse)
	{
		ArrayList<FrameRes> out = new ArrayList<>();

		if(ids != null)
		{
			FrameRes temp;
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

	public ArrayList<FrameGroupRes> GetGroupResArr()
	{
		ArrayList<FrameGroupRes> out = new ArrayList<>();

		SparseArray<FrameRes> orgArr = new SparseArray<>();
		SparseArray<FrameRes> sdcardArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);
		if(sdcardArr != null)
		{
			int len = sdcardArr.size();
			for(int i = 0; i < len; i++)
			{
				orgArr.put(sdcardArr.keyAt(i), sdcardArr.valueAt(i));
			}
		}
		ArrayList<ThemeRes> resArr = ResourceUtils.BuildShowArr(ThemeResMgr2.getInstance().sync_GetLocalRes(MyFramework2App.getInstance().getApplication(), null), ThemeResMgr2.getInstance().sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null), GetOrderArr());

		ThemeRes temp;
		ArrayList<FrameRes> tempArr;
		FrameGroupRes group;
		int len = resArr.size();
		for(int i = 0; i < len; i++)
		{
			temp = resArr.get(i);
			if(temp.m_frameIDArr != null && temp.m_frameIDArr.length > 0)
			{
				ResourceUtils.DeleteItems(orgArr, temp.m_frameIDArr);
				tempArr = GetResArr2(temp.m_frameIDArr, true);
				if(tempArr.size() == temp.m_frameIDArr.length)
				{
					group = new FrameGroupRes();
					group.m_id = temp.m_id;
					group.m_name = temp.m_name;
					group.m_thumb = temp.m_frameThumb;
					if(group.m_thumb == null || (group.m_thumb instanceof String && ((String)group.m_thumb).length() <= 0) || (group.m_thumb instanceof Integer && ((Integer)group.m_thumb) == 0))
					{
						group.m_thumb = GetFrameCoverIcon(group.m_id);
					}
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
			temp.m_frameIDArr = new int[orgArr.size()];

			group = new FrameGroupRes();
			group.m_id = temp.m_id;
			group.m_name = temp.m_name;
			group.m_thumb = GetFrameCoverIcon(group.m_id);
			group.m_group = new ArrayList<>();
			len = orgArr.size();
			for(int i = 0; i < len; i++)
			{
				group.m_group.add(orgArr.valueAt(i));
				temp.m_frameIDArr[i] = orgArr.keyAt(i);
			}
			out.add(group);
		}

		return out;
	}

	public ArrayList<GroupRes> GetNoDownloadGroupResArr()
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		Context context = MyFramework2App.getInstance().getApplicationContext();
		ArrayList<ThemeRes> downloadArr = ThemeResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
		if(downloadArr != null)
		{
			ThemeRes temp;
			ArrayList<FrameRes> resArr;
			GroupRes groupRes;
			boolean flag;
			int len = downloadArr.size();
			for(int i = 0; i < len; i++)
			{
				temp = downloadArr.get(i);
				if(temp.m_frameIDArr != null && temp.m_frameIDArr.length > 0)
				{
					flag = false;
					resArr = GetResArr2(temp.m_frameIDArr, false);
					if(resArr.size() != temp.m_frameIDArr.length)
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

	public int GetNoDownloadCount()
	{
		return GetNoDownloadGroupResArr().size();
	}

	public ArrayList<GroupRes> GetDownloadedGroupResArr()
	{
		ArrayList<GroupRes> out = new ArrayList<>();

		SparseArray<FrameRes> orgArr = new SparseArray<>();
		SparseArray<FrameRes> sdcardArr = sync_GetSdcardRes(MyFramework2App.getInstance().getApplication(), null);
		if(sdcardArr != null)
		{
			int len = sdcardArr.size();
			for(int i = 0; i < len; i++)
			{
				orgArr.put(sdcardArr.keyAt(i), sdcardArr.valueAt(i));
			}
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
		ArrayList<FrameRes> subArr;
		GroupRes groupRes;
		for(int i = 0; i < len; i++)
		{
			temp = themeArr.get(i);
			if(temp.m_frameIDArr != null && temp.m_frameIDArr.length > 0)
			{
				subArr = ResourceUtils.DeleteItems(orgArr, temp.m_frameIDArr);
				if(subArr.size() == temp.m_frameIDArr.length)
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
			temp.m_frameIDArr = new int[orgArr.size()];

			len = orgArr.size();
			groupRes = new GroupRes();
			groupRes.m_themeRes = temp;
			groupRes.m_ress = new ArrayList<>();
			for(int i = 0; i < len; i++)
			{
				groupRes.m_ress.add(orgArr.valueAt(i));
				temp.m_frameIDArr[i] = orgArr.keyAt(i);
			}
			out.add(groupRes);
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

	public void DeleteGroupRes(Context context, GroupRes res)
	{
		int[] ids = res.m_themeRes.m_frameIDArr;
		SparseArray<FrameRes> sdcardArr = sync_GetSdcardRes(context, null);
		ArrayList<FrameRes> arr = ResourceUtils.DeleteItems(sdcardArr, ids);
		if(arr != null && arr.size() > 0)
		{
			FrameRes temp;
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

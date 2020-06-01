package cn.poco.statistics;

import android.content.Context;
import android.os.Build;

import com.adnonstop.beautyaccount.firstopenapp.FirstOpenAppStaManager;
import com.adnonstop.beautyaccount.firstopenapp.bean.FirstOpenApp;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import cn.poco.exception.MyApplication;
import cn.poco.framework.MyFramework2App;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statisticlibs.BeautyStat;
import cn.poco.tianutils.CommonUtils;

/**
 * Created by Raining on 2017/9/8.
 * 神策统计
 */

public class MyBeautyStat extends BeautyStat
{
	/**
	 * 点击统计
	 *
	 * @param resId 资源id
	 */
	public static void onClickByRes(int resId)
	{
		Context context = MyFramework2App.getInstance().getApplicationContext();
		if(context != null)
		{
			onClick(context.getResources().getString(resId));
		}
	}

	/**
	 * 页面开始
	 *
	 * @param resPageId 页面资源id
	 */
	public static void onPageStartByRes(int resPageId)
	{
		Context context = MyFramework2App.getInstance().getApplicationContext();
		if(context != null)
		{
			onPageStart(context.getResources().getString(resPageId));
		}
	}

	/**
	 * 页面结束
	 *
	 * @param resPageId 页面资源id
	 */
	public static void onPageEndByRes(int resPageId)
	{
		Context context = MyFramework2App.getInstance().getApplicationContext();
		if(context != null)
		{
			onPageEnd(context.getResources().getString(resPageId));
		}
	}

	public enum BlogType
	{
		朋友圈,
		微信好友,
		微博,
		QQ空间,
		QQ好友,
		短信,
		Facebook,
		Twitter,
	}

	/**
	 * 分享成功后统计
	 *
	 * @param type      博客类型
	 * @param resPageId 页面资源id
	 */
	public static void onShareCompleteByRes(BlogType type, int resPageId)
	{
		if(type != null)
		{
			Context context = MyFramework2App.getInstance().getApplicationContext();
			if(context != null)
			{
				try
				{
					JSONObject properties = new JSONObject();
					properties.put("channel", type.toString());
					properties.put("page_id", context.getResources().getString(resPageId));
					onClick("share", properties);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public enum EffectType
	{
		none,
		nenbai,
		xijie,
		qingwei,
		liangbai,
		ziran,
		menglong,
		zidingyi,
		jingbai,
	}

	/**
	 * 美颜
	 */
	public static void onUseBeautyEffect(EffectType type, int alpha)
	{
		if(type != null)
		{
			try
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "美颜效果");
				properties.put("value", alpha);
				properties.put("effect_type", type.toString());
				onClick("use", properties);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	public enum FaceType
	{
		瓜子脸,
		鹅蛋脸,
		锥子脸,
	}

	private static String IMEI = null;

	private static String GetTag()
	{
		if(IMEI == null)
		{
			IMEI = CommonUtils.GetIMEI(MyFramework2App.getInstance().getApplicationContext());
			if(IMEI == null || IMEI.length() < 4)
			{
				IMEI = UUID.randomUUID().toString();
			}
		}
		return IMEI + System.currentTimeMillis();
	}

	/**
	 * 瘦身
	 */
	public static void onSaveSlim(boolean useHand, int handValue, boolean useTool, FaceType faceType, int faceValue)
	{
		try
		{
			String tag = GetTag();
			if(useHand)
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "手动瘦脸");
				properties.put("tag", tag);
				properties.put("value", handValue);
				onClick("use", properties);
			}
			if(useTool)
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "瘦脸工具");
				properties.put("tag", tag);
				onClick("use", properties);
			}
			if(faceType != null)
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "脸型");
				properties.put("tag", tag);
				properties.put("face_type", faceType.toString());
				properties.put("value", faceValue);
				onClick("use", properties);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 祛痘
	 */
	public static void onUseAntiAcne(int qudou)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "祛痘");
			properties.put("value", qudou);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 眼部处理
	 */
	public static void onUseEye(int dayan, int quyandai, int liangyan)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "大眼_祛眼袋_亮眼");
			properties.put("dayan", dayan);
			properties.put("quyandai", quyandai);
			properties.put("liangyan", liangyan);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 高鼻梁
	 */
	public static void onUseNose(int gaobiliang)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "高鼻梁");
			properties.put("value", gaobiliang);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 微笑
	 */
	public static void onUseSmile(int weixiao)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "微笑");
			properties.put("value", weixiao);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public enum CosFaceType
	{
		小黑猫,
		天使猫,
		糖果猫咪,
		乔巴,
		阿拉蕾,
		美少女,
		草莓兔,
		气球派对,
		山兔,
		麋鹿妆,
		小恶魔,
		鹿公主,
		迷糊汪,
		嘻哈侠,
		甜心汪,
		俊介君,
	}

	/**
	 * 一键萌妆
	 */
	public static void onUseCosFace(CosFaceType type, int alpha)
	{
		if(type != null)
		{
			try
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "一键萌妆");
				properties.put("value", alpha);
				properties.put("mengzhuang_type", type.toString());
				onClick("use", properties);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 滤镜
	 *
	 * @param useXuhua   是否使用了虚化
	 * @param useAnjiao  是否使用了暗角
	 * @param filterTjId 资源统计id
	 */
	public static void onUseFilter(boolean useXuhua, boolean useAnjiao, String filterTjId, int filterAlpha)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "滤镜");
			properties.put("blur", useXuhua);
			properties.put("dark", useAnjiao);
			properties.put("filter_tjid", filterTjId);
			properties.put("value", filterAlpha);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public enum AdvFaceType
	{
		_无,
		_我的,
		_自然修饰,
		_芭比公主,
		_精致网红,
		_激萌少女,
		_摩登女王,
		_呆萌甜心,
		_嘟嘟童颜,
		_小脸女神;

		@Override
		public String toString()
		{
			return super.toString().replace("_", "");
		}
	}

	public static class CameraBeautyData
	{
		public AdvFaceType lianxing;//脸型选择
		public int meifu;//美肤数值
		public int meiya;//美牙数值
		public int fuse;//肤色数值
		public int shoulian;//瘦脸数值
		public int xiaolian;//小脸数值
		public int xiaolian2;//削脸数值
		public int etou;//额头数值
		public int quangu;//颧骨数值
		public int dayan;//大眼数值
		public int yanjiao;//眼角数值
		public int yanju;//眼距数值
		public int shoubi;//瘦鼻数值
		public int biyi;//鼻翼数值
		public int bizigaodu;//鼻子高度数值
		public int xiaba;//下巴数值
		public int zuixing;//嘴型数值
		public int zuibagaodu;//嘴巴高度数值
	}

	/**
	 * 美形定制
	 *
	 * @param isLive 是否直播
	 */
	public static void onUseCameraBeauty(CameraBeautyData data, boolean isLive)
	{
		try
		{
			JSONObject properties = new JSONObject();
			if(!isLive)
			{
				properties.put("type", "美形定制");
			}
			if(data.lianxing != null)
			{
				properties.put("lianxing", data.lianxing.toString());
			}
			properties.put("meifu", data.meifu);
			properties.put("meiya", data.meiya);
			properties.put("fuse", data.fuse);
			properties.put("shoulian", data.shoulian);
			properties.put("xiaolian", data.xiaolian);
			properties.put("xiaolian2", data.xiaolian2);
			properties.put("etou", data.etou);
			properties.put("quangu", data.quangu);
			properties.put("dayan", data.dayan);
			properties.put("yanjiao", data.yanjiao);
			properties.put("yanju", data.yanju);
			properties.put("shoubi", data.shoubi);
			properties.put("biyi", data.biyi);
			properties.put("bizigaodu", data.bizigaodu);
			properties.put("xiaba", data.xiaba);
			properties.put("zuixing", data.zuixing);
			properties.put("zuibagaodu", data.zuibagaodu);

			if(!isLive)
			{
				onClick("use", properties);
			}
			else
			{
				onClick("live_feature", properties);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 拍摄延迟模式
	 */
	public enum CameraTimer
	{
		_关闭,
		_1S,
		_2S,
		_10S;

		@Override
		public String toString()
		{
			return super.toString().replace("_", "");
		}
	}

	/**
	 * 表情包拍摄
	 *
	 * @param useFace     是否使用脸型
	 * @param touchScreen 是否触屏拍摄
	 * @param frontCamera 是否前置摄像头
	 */
	public static void onUseCameraGif(String resTjId, String filterTjId, boolean useFace, String faceResTjId, CameraTimer timer, boolean touchScreen, boolean frontCamera)
	{
		try
		{
			String tag = GetTag();
			if(useFace)
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "镜头脸型");
				properties.put("tag", tag);
				properties.put("res_tjid", faceResTjId);
				onClick("use", properties);
			}
			JSONObject properties = new JSONObject();
			properties.put("type", "表情包拍摄");
			properties.put("tag", tag);
			properties.put("res_tjid", resTjId);
			properties.put("filter_tjid", filterTjId);
			onClick("use", properties);

			properties = new JSONObject();
			properties.put("shoudongkg", timer.toString());
			properties.put("chuping", touchScreen);
			properties.put("shext", frontCamera ? "前置" : "后置");
			onClick("takephotos_bqb", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public enum CameraScale
	{
		_1比1,
		_4比3,
		_16比9,
		_9比16,
		_full;

		@Override
		public String toString()
		{
			return super.toString().replace("_", "");
		}
	}

	/**
	 * 萌妆照拍摄
	 *
	 * @param useFace     是否使用脸型
	 * @param touchScreen 是否触屏拍摄
	 * @param frontCamera 是否前置摄像头
	 */
	public static void onUseCameraCosFace(String resTjId, String filterTjId, boolean useFace, String faceResTjId, CameraTimer timer, boolean touchScreen, CameraScale scale, boolean frontCamera)
	{
		try
		{
			String tag = GetTag();
			if(useFace)
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "镜头脸型");
				properties.put("tag", tag);
				properties.put("res_tjid", faceResTjId);
				onClick("use", properties);
			}
			JSONObject properties = new JSONObject();
			properties.put("type", "萌妆照拍摄");
			properties.put("tag", tag);
			properties.put("res_tjid", resTjId);
			properties.put("filter_tjid", filterTjId);
			onClick("use", properties);

			properties = new JSONObject();
			properties.put("shoudongkg", timer.toString());
			properties.put("chuping", touchScreen);
			properties.put("bili", scale.toString());
			properties.put("shext", frontCamera ? "前置" : "后置");
			onClick("takephotos_mzz", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public enum VideoTime
	{
		_10秒,
		_3分钟;

		@Override
		public String toString()
		{
			return super.toString().replace("_", "");
		}
	}

	/**
	 * 视频拍摄
	 *
	 * @param touchScreen 是否触屏拍摄
	 * @param frontCamera 是否前置摄像头
	 */
	public static void onUseCameraVideo(String resTjId, String filterTjId, boolean useFace, String faceResTjId, CameraTimer timer, boolean touchScreen, CameraScale scale, boolean frontCamera, VideoTime vt)
	{
		try
		{
			String tag = GetTag();
			if(useFace)
			{
				JSONObject properties = new JSONObject();
				properties.put("type", "镜头脸型");
				properties.put("tag", tag);
				properties.put("res_tjid", faceResTjId);
				onClick("use", properties);
			}
			JSONObject properties = new JSONObject();
			properties.put("type", "视频拍摄");
			properties.put("tag", tag);
			properties.put("res_tjid", resTjId);
			properties.put("filter_tjid", filterTjId);
			onClick("use", properties);

			properties = new JSONObject();
			properties.put("shoudongkg", timer.toString());
			properties.put("chuping", touchScreen);
			properties.put("bili", scale.toString());
			properties.put("shext", frontCamera ? "前置" : "后置");
			properties.put("shichang", vt.toString());
			onClick("takephotos_sp", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 美颜拍照
	 *
	 * @param touchScreen 是否触屏拍摄
	 * @param frontCamera 是否前置摄像头
	 */
	public static void onUseCameraTakePhoto(String filterTjId, CameraTimer timer, boolean touchScreen, CameraScale scale, boolean frontCamera)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "美颜拍照");
			properties.put("filter_tjid", filterTjId);
			onClick("use", properties);

			properties = new JSONObject();
			properties.put("shoudongkg", timer.toString());
			properties.put("chuping", touchScreen);
			properties.put("bili", scale.toString());
			properties.put("shext", frontCamera ? "前置" : "后置");
			onClick("takephotos_gq", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 添加统计
	 * 点叉的时候统计
	 */
	public static void onUseLiveCamera(String resTjId, String filterTjId)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("live_stickerid", resTjId);
			onClick("live_sticker", properties);

			properties = new JSONObject();
			properties.put("live_filterid", filterTjId);
			onClick("live_filter", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 主题换肤
	 *
	 * @param themeName 主题名
	 */
	public static void onUseTheme(String themeName)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "主题换肤");
			properties.put("theme_name", themeName);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 美牙
	 */
	public static void onUseTooth(int meiya)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "美牙");
			properties.put("value", meiya);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 瘦鼻
	 */
	public static void onUseNose2(int shoubi)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "瘦鼻");
			properties.put("value", shoubi);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 相框页
	 */
	public static void onUseFrame(String resTjId)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "相框");
			properties.put("res_tjid", resTjId);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 简约相框
	 */
	public static void onUseSimpleFrame(String resTjId, int colorIndex)
	{
		try
		{
			JSONObject properties = new JSONObject();
			properties.put("type", "简约相框");
			properties.put("value", colorIndex);
			properties.put("res_tjid", resTjId);
			onClick("use", properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 贴图
	 */
	public static void onUseDecorate(String[] resTjIds)
	{
		try
		{
			if(resTjIds != null)
			{
				String tag = GetTag();
				for(String resTjId : resTjIds)
				{
					JSONObject properties = new JSONObject();
					properties.put("type", "贴图");
					properties.put("tag", tag);
					properties.put("res_tjid", resTjId);
					onClick("use", properties);
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 毛玻璃
	 */
	public static void onUseGlass(String[] resTjIds)
	{
		try
		{
			if(resTjIds != null)
			{
				String tag = GetTag();
				for(String resTjId : resTjIds)
				{
					JSONObject properties = new JSONObject();
					properties.put("type", "毛玻璃");
					properties.put("tag", tag);
					properties.put("res_tjid", resTjId);
					onClick("use", properties);
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 马赛克
	 */
	public static void onUseMosaic(String[] resTjIds)
	{
		try
		{
			if(resTjIds != null)
			{
				String tag = GetTag();
				for(String resTjId : resTjIds)
				{
					JSONObject properties = new JSONObject();
					properties.put("type", "马赛克");
					properties.put("tag", tag);
					properties.put("res_tjid", resTjId);
					onClick("use", properties);
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 指尖魔法
	 */
	public static void onUseBrush(String[] resTjIds)
	{
		try
		{
			if(resTjIds != null)
			{
				String tag = GetTag();
				for(String resTjId : resTjIds)
				{
					JSONObject properties = new JSONObject();
					properties.put("type", "指尖魔法");
					properties.put("tag", tag);
					properties.put("res_tjid", resTjId);
					onClick("use", properties);
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public enum MakeupType
	{
		组合,
		粉底,
		腮红,
		唇彩,
		眉毛,
		眼影,
		眼线,
		睫毛,
		美瞳,
	}

	public static class Makeup
	{
		public MakeupType type;
		public String resTjId;
		public int alpha;
	}

	/**
	 * 彩妆
	 */
	public static void onUseMakeup(ArrayList<Makeup> makeups)
	{
		try
		{
			if(makeups != null)
			{
				String tag = GetTag();
				for(Makeup makeup : makeups)
				{
					JSONObject properties = new JSONObject();
					properties.put("type", "彩妆");
					properties.put("makeup_type", makeup.type.toString());
					properties.put("tag", tag);
					properties.put("value", makeup.alpha);
					properties.put("res_tjid", makeup.resTjId);
					onClick("use", properties);
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public enum DownloadType
	{
		全部,
		相框,
		简约边框,
		贴图,
		彩妆,
		毛玻璃,
		马赛克,
		指尖魔法,
		滤镜,
	}

	public static long sHzFirstTime;

	public synchronized static void checkLogin(Context context)
	{
		final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
		if(settingInfo != null)
		{
			String userId = settingInfo.GetPoco2Id(true);
			if(userId != null && userId.length() > 0)
			{
				if(sHzFirstTime == 0)
				{
					try
					{
						Long userId2 = Long.parseLong(userId.trim());
						FirstOpenAppStaManager.getFirstOpenAppTime(userId2, MyApplication.HZ_APP_NAME, new FirstOpenAppStaManager.CallBack()
						{
							@Override
							public void onFailure(String msg)
							{
								MyBeautyStat.onLogin(settingInfo.GetPoco2Id(true), settingInfo.GetPoco2Sex(), settingInfo.GetPoco2BirthdayYear(), settingInfo.GetPoco2BirthdayMonth(), settingInfo.GetPoco2BirthdayDay(), settingInfo.GetPoco2Phone(), settingInfo.GetPoco2RegisterTime(), MyFramework2App.getInstance().GetLastRunTime() + "", sHzFirstTime);
							}

							@Override
							public void onResponse(FirstOpenApp firstOpenApp)
							{
								if(firstOpenApp != null)
								{
									sHzFirstTime = firstOpenApp.getFirstTime();
								}
								MyBeautyStat.onLogin(settingInfo.GetPoco2Id(true), settingInfo.GetPoco2Sex(), settingInfo.GetPoco2BirthdayYear(), settingInfo.GetPoco2BirthdayMonth(), settingInfo.GetPoco2BirthdayDay(), settingInfo.GetPoco2Phone(), settingInfo.GetPoco2RegisterTime(), MyFramework2App.getInstance().GetLastRunTime() + "", sHzFirstTime);
							}
						});
					}
					catch(Throwable e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					MyBeautyStat.onLogin(settingInfo.GetPoco2Id(true), settingInfo.GetPoco2Sex(), settingInfo.GetPoco2BirthdayYear(), settingInfo.GetPoco2BirthdayMonth(), settingInfo.GetPoco2BirthdayDay(), settingInfo.GetPoco2Phone(), settingInfo.GetPoco2RegisterTime(), MyFramework2App.getInstance().GetLastRunTime() + "", sHzFirstTime);
				}
			}
			else
			{
				MyBeautyStat.onLogout(MyFramework2App.getInstance().GetLastRunTime() + "", sHzFirstTime);
			}
		}
	}

	public static void onLogin(String userId, String sex, String year, String month, String day, String phone, String registerTime, String lastRunTime, long firstRunTime)
	{
		BeautyStat.onLogin(userId);
		try
		{
			JSONObject properties = new JSONObject();
			if(userId != null && userId.length() > 0)
			{
				properties.put("userid", userId);
			}
			if(sex != null && sex.length() > 0)
			{
				properties.put("sex", sex);
			}
			if(year != null && month != null && day != null)
			{
				if(month.length() == 1)
				{
					month = "0" + month;
				}
				if(day.length() == 1)
				{
					day = "0" + day;
				}
				properties.put("year", year + "-" + month + "-" + day);
			}
			if(phone != null && phone.length() > 0)
			{
				properties.put("phone", phone);
			}
			if(registerTime != null && registerTime.length() > 0)
			{
				try
				{
					long time = Long.parseLong(registerTime) * 1000;
					Date date = new Date(time);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
					String dateStr = sdf.format(date);
					properties.put("register_time", dateStr);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			if(lastRunTime != null && lastRunTime.length() > 0)
			{
				try
				{
					long time = Long.parseLong(lastRunTime);
					Date date = new Date(time);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
					String dateStr = sdf.format(date);
					properties.put("last_active", dateStr);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			if(firstRunTime > 0)
			{
				try
				{
					Date date = new Date(firstRunTime);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
					String dateStr = sdf.format(date);
					properties.put("$first_visit_time", dateStr);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			String imei = CommonUtils.GetIMEI(MyFramework2App.getInstance().getApplicationContext());
			if(imei != null && imei.length() > 0)
			{
				properties.put("only_key", imei);
			}
			properties.put("brand", Build.BRAND);
			properties.put("model", Build.MODEL);
			SensorsDataAPI.sharedInstance().profileSet(properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public static void onLogout(String lastRunTime, long firstRunTime)
	{
		BeautyStat.onLogout();
		try
		{
			JSONObject properties = new JSONObject();
			if(lastRunTime != null && lastRunTime.length() > 0)
			{
				try
				{
					long time = Long.parseLong(lastRunTime);
					Date date = new Date(time);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
					String dateStr = sdf.format(date);
					properties.put("last_active", dateStr);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			if(firstRunTime > 0)
			{
				try
				{
					Date date = new Date(firstRunTime);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
					String dateStr = sdf.format(date);
					properties.put("$first_visit_time", dateStr);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
			String imei = CommonUtils.GetIMEI(MyFramework2App.getInstance().getApplicationContext());
			if(imei != null && imei.length() > 0)
			{
				properties.put("only_key", imei);
			}
			properties.put("brand", Build.BRAND);
			properties.put("model", Build.MODEL);
			SensorsDataAPI.sharedInstance().profileSet(properties);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 素材中心
	 *
	 * @param resTjId 主题统计id
	 */
	public static void onDownloadRes(DownloadType type, boolean isRecommend, String resTjId)
	{
		try
		{
			if(type != null)
			{
				JSONObject properties = new JSONObject();
				properties.put("type", type.toString());
				properties.put("recommend", isRecommend);
				properties.put("res_tjid", resTjId);
				onClick("download", properties);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param tjId 后台录入的banner统计id
	 */
	public static void onBanner(String tjId)
	{
		try
		{
			if(tjId != null)
			{
				JSONObject properties = new JSONObject();
				properties.put("clickbanner", tjId);
				onClick("rec_banner", properties);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}

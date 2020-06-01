package cn.poco.exception;

import android.support.multidex.MultiDexApplication;

import com.adnonstop.beautyaccount.CallbackListener;
import com.adnonstop.beautyaccount.HttpRequest;
import com.adnonstop.beautyaccount.LoginConfig;
import com.adnonstop.beautyaccount.LoginConstant;
import com.adnonstop.beautyaccount.RequestParam;
import com.adnonstop.beautyaccount.firstopenapp.FirstOpenAppStaManager;
import com.adnonstop.beautymall.BeautyMallConfig;
import com.adnonstop.changeface.Helper;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mobstat.StatService;
import com.circle.common.serverapi.ProtocolParams;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.taotie.circle.Community;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

import cn.com.iresearch.mapptracker.IRMonitor;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.MyFramework2App;
import cn.poco.image.PocoDetector;
import cn.poco.image.PocoFaceTracker;
import cn.poco.imagecore.Utils;
import cn.poco.login.BeautyIMConnect;
import cn.poco.resource.DownloadMgr;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statisticlibs.BeautyStat;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.AppInterface;
import cn.poco.system.ConfigIni;
import cn.poco.system.FolderMgr;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.taskCenter.MissionHelper;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.FileUtil;

public class MyApplication extends MultiDexApplication
{
	public static String HZ_APP_NAME = "beauty_camera_android";

	protected static MyApplication sApp;

	public synchronized static MyApplication getInstance()
	{
		return sApp;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		sApp = this;

		CaughtExceptionHandler exceptionHandler = new CaughtExceptionHandler();
		exceptionHandler.Init(getApplicationContext());

		CrashReport.initCrashReport(getApplicationContext(), "a75a5e1abb", false);

		MyFramework2App.getInstance().onCreate(this);

		SysConfig.Read(this); //读取系统配
		ConfigIni.readConfig(this);
		TagMgr.Init(FolderMgr.OTHER_CONFIG_SP_NAME);
		DownloadMgr.InitInstance(this);

		//设置社区一些参数
		Community.init(Community.BEAUTY, SysConfig.GetAppFileName(), "beautycamerasns");
		Community.setAppSkinColor(SysConfig.s_skinColor, SysConfig.s_skinColor1, SysConfig.s_skinColor2);
		String community_version = "1.7.3";// TODO: 2017/10/9  社区的版本，发版本前需要检查一下
		ProtocolParams.init("beauty_camera_android", community_version, SysConfig.IsDebug() ? ProtocolParams.BETA_ENVIROMENT : ProtocolParams.RELEASE_ENVIROMENT, CommonUtils.GetAppVer(this), CommonUtils.GetIMEI(this));

		// service进程或者主进程都需要调用
		BeautyIMConnect.getInstance().init();

		Utils.MAX_SIZE = 2400;

		//初始化文件管理并清理APP级缓存
		String processName = CommonUtils.GetProcessName(this);
		//System.out.println("processName : " + processName);
		//System.out.println("packageName : " + getPackageName());
		if(processName != null && processName.equals(this.getPackageName()))
		{
			//TODO: *******************主进程***********************
			try
			{
				//删除ar祝福临时下载路径
				String path = cn.poco.video.FileUtils.getVideoOutputSysDir() + File.separator + "temp_ar_video.xxx";
				File file = new File(path);
				if(file.exists())
				{
					FileUtil.deleteFile(file, false);
				}
				// 清除ar视频播放缓存
				file = new File(FolderMgr.getInstance().PATH_PLAYER_CACHE);
				FileUtil.deleteFile(file, false);

				//主进程连接IM的在线服务
				BeautyIMConnect.getInstance().connetIM();
				TagMgr.SetTag(getApplicationContext(), Tags.CAMERA_OPEN_COUNT);
				FileCacheMgr.Init(FolderMgr.getInstance().IMAGE_CACHE_PATH, true);

				//初始化人脸识别需要的数据
				PocoDetector.preReadandWriteXML(this, SysConfig.GetAppFileName());
				PocoFaceTracker.preinitULSTracker(this, 5);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}

			//统计
			try
			{
				//艾瑞
				IRMonitor.getInstance().init(this, "d81fe5027f798f66", null);

				//百度统计
				//StatService.setDebugOn(true);
				String channel = ConfigIni.getMiniVer();
				if(channel != null && channel.length() > 0)
				{
					StatService.setAppChannel(this, channel, true);
				}
				else
				{
					StatService.setAppChannel(this, null, false);
				}

				//神策
				BeautyStat.Config config = MyBeautyStat.getDefaultConfig(this);
				if(SysConfig.IsDebug())
				{
					config.serverURL = "http://tj.adnonstop.com:8106/sa?project=mrxj_project_test";
					config.configureUrl = "http://tj.adnonstop.com:8106/config/?project=mrxj_project_test";
					config.debugMode = SensorsDataAPI.DebugMode.DEBUG_AND_TRACK;
				}
				config.channel = ConfigIni.getMiniVer();
				MyBeautyStat.Init(config);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}

			try
			{
				//图片统计
				//PhotoStat.Data data = new PhotoStat.Data();
				//data.appName = "beauty_camera_android";
				//data.appVer = CommonUtils.GetAppVer(this);
				//data.switchUrl = "http://open.adnonstop.com/beauty_camera/biz/prod/api/public/index.php?r=picture/index/init";
				//data.saveUrl = "http://open.adnonstop.com/beauty_camera/biz/prod/api/public/index.php?r=picture/index/save";
				//data.tokenUrl = "http://open.adnonstop.com/beauty_camera/biz/prod/api/public/index.php?r=picture/index/aliyunOSSToken";
				//if(SysConfig.IsDebug())
				//{
				//	data.switchUrl = "http://tw.adnonstop.com/beauty/app/api/beauty_camera/biz/beta/api/public/index.php?r=picture/index/init";
				//	data.saveUrl = "http://tw.adnonstop.com/beauty/app/api/beauty_camera/biz/beta/api/public/index.php?r=picture/index/save";
				//	data.tokenUrl = "http://tw.adnonstop.com/beauty/app/api/beauty_camera/biz/beta/api/public/index.php?r=picture/index/aliyunOSSToken";
				//}
				//PhotoStat.Init(this, data);

				//杭州配置
				boolean isDebug = false;
				if(SysConfig.IsDebug())
				{
					isDebug = true;
					com.adnonstop.missionhall.Constant.HttpConstant.setModel(true);
				}
				new BeautyMallConfig.Builder().appSource("beauty_camera").appSourceVersion("1.0.2").setDebugModel(isDebug).setApplication(this).build();

				//杭州统计第一次使用
				String appName = HZ_APP_NAME;
				Long userId = null;
				SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(this);
				if(settingInfo != null)
				{
					String id = settingInfo.GetPoco2Id(true);
					if(id != null && id.length() > 0)
					{
						userId = Long.parseLong(id.trim());
					}
				}
				FirstOpenAppStaManager.firstOpenApp(this, appName, CommonUtils.GetAppVer(this), "0", userId, null);

				//手机数据收集
				SettingInfo info = SettingInfoMgr.GetSettingInfo(this);
				String id = null;
				String phone = null;
				if(info != null)
				{
					id = info.GetPoco2Id(false);
					phone = info.GetPoco2Phone();
				}
				String url;
				if(SysConfig.IsDebug())
				{
					url = "http://tw.adnonstop.com/beauty/app/api/collect/index.php";
				}
				else
				{
					url = "http://open.adnonstop.com/collect/index.php";
				}
				MyBeautyStat.other(url, id, phone, AppInterface.GetInstance(this));

				//杭州换脸
				Helper.init(this);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}

			try
			{
				//杭州美人账号登录注册模块初始化
				new LoginConfig.Builder().setApplication(this).setAppName("beauty_camera_android").setVersionName(SysConfig.GetAppVerNoSuffix(this)).setDebugModel(SysConfig.IsDebug()).build();

				MissionHelper.getInstance();

				SettingInfo info = SettingInfoMgr.GetSettingInfo(this);
				if(info == null) return;
				String id = info.GetPoco2Id(true);
				String accessToken = info.GetPoco2Token(true);
				if(id != null && id.length() > 0 && accessToken != null && accessToken.length() > 0)
				{
					Long userId = Long.parseLong(id.trim());
					String openAppRecord = RequestParam.openAppRecord(userId, accessToken, "false");
					HttpRequest.getInstance().postRequest(LoginConstant.USER_LOGIN_ACTION, openAppRecord, new CallbackListener()
					{
						@Override
						public void success(JSONObject jsonObject, String s)
						{
						}

						@Override
						public void failure(int i, String s, String s1)
						{
						}
					}, "openAppRecord");
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			//TODO: ******************其他进程***********************
			FileCacheMgr.Init(FolderMgr.getInstance().IMAGE_CACHE_PATH, false);
		}
	}
}

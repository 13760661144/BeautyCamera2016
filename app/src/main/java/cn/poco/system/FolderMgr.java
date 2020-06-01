package cn.poco.system;

/**
 * 改为单例,修复读取配置的BUG
 */
public class FolderMgr
{
	private static FolderMgr sInstance;

	public synchronized static FolderMgr getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new FolderMgr();
		}
		return sInstance;
	}


	/**
	 * SD card
	 */
	//素材中心
	public final String RESOURCE_TEMP_PATH = SysConfig.GetAppPath() + "/appdata/resource/temp";
	public final String RESOURCE_FRAME_PATH = SysConfig.GetAppPath() + "/appdata/resource/frame";
	public final String RESOURCE_CARD_PATH = SysConfig.GetAppPath() + "/appdata/resource/card";
	public final String RESOURCE_DECORATE_PATH = SysConfig.GetAppPath() + "/appdata/resource/decorate";
	public final String RESOURCE_MAKEUP_PATH = SysConfig.GetAppPath() + "/appdata/resource/makeup";
	public final String RESOURCE_PUZZLE_BK_PATH = SysConfig.GetAppPath() + "/appdata/resource/puzzle_bk";
	public final String RESOURCE_PUZZLE_TEMPLATE_PATH = SysConfig.GetAppPath() + "/appdata/resource/puzzle_template";
	public final String RESOURCE_THEME_PATH = SysConfig.GetAppPath() + "/appdata/resource/theme";
	public final String RESOURCE_MOSAIC_PATH = SysConfig.GetAppPath() + "/appdata/resource/mosaic";
	public final String RESOURCE_GLASS_PATH = SysConfig.GetAppPath() + "/appdata/resource/glass";
	public final String RESOURCE_VIDEO_FACE_PATH = SysConfig.GetAppPath() + "/appdata/resource/video_face";
	public final String RESOURCE_LIVE_VIDEO_FACE_PATH = SysConfig.GetAppPath() + "/appdata/resource/live_video_face";
	public final String RESOURCE_RECOMMEND_PATH = SysConfig.GetAppPath() + "/appdata/resource/recommend4";
	public final String RESOURCE_APP_MARKET_PATH = SysConfig.GetAppPath() + "/appdata/resource/app_market";
	public final String RESOURCE_BRUSH_PATH = SysConfig.GetAppPath() + "/appdata/resource/brush";
	public final String RESOURCE_FRAME2_PATH = SysConfig.GetAppPath() + "/appdata/resource/frame2";
	public final String RESOURCE_FILTER_PATH = SysConfig.GetAppPath() + "/appdata/resource/filter";
	public final String RESOURCE_PREVIEW_BGM_PATH = SysConfig.GetAppPath() + "/appdata/resource/pre_bgm";
	public final String RESOURCE_RED_DOT_PATH = SysConfig.GetAppPath() + "/appdata/resource/red_dot";
	public final String FEATURE_MENU_PATH = SysConfig.GetAppPath() + "/appdata/featuremenu";
	public final String SHAPE_DATA_PATH = SysConfig.GetAppPath() + "/appdata/shape_data";
	public final String AR_NEW_YEAR_DATA_PATH = SysConfig.GetAppPath() + "/appdata/ar_data";

	//其他资源
	public final String OTHER_PATH = SysConfig.GetAppPath() + "/appdata/other";

	//解锁
	public final String LOCK_PATH = SysConfig.GetAppPath() + "/appdata/lock";

	//限量素材
	public final String LIMIT_PATH = SysConfig.GetAppPath() + "/appdata/limit";

	//banner
	public final String BANNER_PATH = SysConfig.GetAppPath() + "/appdata/banner5";

	//business
	public final String BUSINESS_PATH = SysConfig.GetAppPath() + "/appdata/business5";

	//运行时图片缓存
	public final String IMAGE_CACHE_PATH = SysConfig.GetAppPath() + "/appdata/rcache";

	public final String PRINTER_PATH = SysConfig.GetAppPath() + "/appdata/temp";

	//ar配置
	public final String PATH_AR_SETTING = SysConfig.GetAppPath() + "/appdata/ar";

	//播放器视频缓存
	public final String PATH_PLAYER_CACHE = SysConfig.GetAppPath() + "/appdata/player";

	public boolean IsCachePath(String path)
	{
		boolean out = false;

		if(path != null)
		{
			out = path.contains(IMAGE_CACHE_PATH);
		}

		return out;
	}

	//视频插件
	public final String VIDEO_TEMP_PATH = SysConfig.GetAppPath() + "/appdata/videopulgin/temp";
	public final String VIDEO_RES_PATH = SysConfig.GetAppPath() + "/appdata/videopulgin/res";

	// 运营专区
	public final String CAMPAIGN_CENTER_PATH = SysConfig.GetAppPath() + "/appdata/campaigncenter";
	public final String CAMPAIGN_CENTER_CACHE_IMG_PATH = SysConfig.GetAppPath() + "/appdata/campaigncenter/cacheImage";

	// 分享给好友
	public static final String SHARE_WITH_FRIENDS_PATH = SysConfig.GetAppPath() + "appdata/shareWithFriends";

	/**
	 * data/data
	 * <p>
	 * cache
	 * files
	 * databases
	 */
	public static final String VIDEO_LOCAL_PATH = "videopulgin";

	//用户信息
	public final String USER_INFO_TEMP = SysConfig.GetAppPath() + "/appdata/userinfo/temp";
	public final String USER_INFO = SysConfig.GetAppPath() + "/appdata/userinfo";

	/*
	 * 以下是SharedPreferences文件名
	 */
	public static final String SETTING_SP_NAME = "setting_sp"; //设置页面
	public static final String SYSTEM_CONFIG_SP_NAME = "system_config_sp"; //系统配置
	public static final String OTHER_CONFIG_SP_NAME = "other_config_sp"; //其他配置
	public static final String NEW_RES_FLAG_SP_NAME = "new_res_flag_sp"; //各种资源的new标志
}

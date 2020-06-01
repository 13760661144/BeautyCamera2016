package cn.poco.resource;

import android.content.Context;

import cn.poco.system.FolderMgr;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;
import cn.poco.utils.MyNetCore;

public class DownloadMgr extends AbsDownloadMgr
{
	private static DownloadMgr sInstance;

	public static synchronized DownloadMgr getInstance()
	{
		return sInstance;
	}

	public static synchronized void InitInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new DownloadMgr(context);
		}
	}

	public String FRAME_PATH;
	public String CARD_PATH;
	public String DECORATE_PATH;
	public String MAKEUP_PATH;
	public String PUZZLE_BK_PATH;
	public String PUZZLE_TEMPLATE_PATH;
	public String THEME_PATH;
	public String MOSAIC_PATH;
	public String GLASS_PATH;
	public String LOCK_PATH;
	public String BANNER_PATH;
	public String VIDEO_FACE_PATH;
	public String LIVE_VIDEO_FACE_PATH;
	public String BUSINESS_PATH;
	public String RECOMMEND_PATH;
	public String APP_MARKET_PATH;
	public String BRUSH_PATH;
	public String FRAME2_PATH;
	public String OTHER_PATH;
    public String LIMIT_PATH;
	public String FILTER_PATH;
	public String PREVIEW_BGM_PATH;
	public String RESOURCE_RED_DOT_PATH;
	public String FEATURE_MENU_PATH;
	public String SHAPE_DATA_PATH;
	public String AR_NEW_YEAR_PATH;

	public DownloadMgr(Context context)
	{
		super(context, FolderMgr.getInstance().RESOURCE_TEMP_PATH);
	}

	@Override
	protected void InitData(Context context)
	{
		FRAME_PATH = FolderMgr.getInstance().RESOURCE_FRAME_PATH;
		CARD_PATH = FolderMgr.getInstance().RESOURCE_CARD_PATH;
		DECORATE_PATH = FolderMgr.getInstance().RESOURCE_DECORATE_PATH;
		MAKEUP_PATH = FolderMgr.getInstance().RESOURCE_MAKEUP_PATH;
		PUZZLE_BK_PATH = FolderMgr.getInstance().RESOURCE_PUZZLE_BK_PATH;
		PUZZLE_TEMPLATE_PATH = FolderMgr.getInstance().RESOURCE_PUZZLE_TEMPLATE_PATH;
		THEME_PATH = FolderMgr.getInstance().RESOURCE_THEME_PATH;
		MOSAIC_PATH = FolderMgr.getInstance().RESOURCE_MOSAIC_PATH;
		GLASS_PATH = FolderMgr.getInstance().RESOURCE_GLASS_PATH;
		LOCK_PATH = FolderMgr.getInstance().LOCK_PATH;
		BANNER_PATH = FolderMgr.getInstance().BANNER_PATH;
		VIDEO_FACE_PATH = FolderMgr.getInstance().RESOURCE_VIDEO_FACE_PATH;
		LIVE_VIDEO_FACE_PATH = FolderMgr.getInstance().RESOURCE_LIVE_VIDEO_FACE_PATH;
		BUSINESS_PATH = FolderMgr.getInstance().BUSINESS_PATH;
		RECOMMEND_PATH = FolderMgr.getInstance().RESOURCE_RECOMMEND_PATH;
		APP_MARKET_PATH = FolderMgr.getInstance().RESOURCE_APP_MARKET_PATH;
		BRUSH_PATH = FolderMgr.getInstance().RESOURCE_BRUSH_PATH;
		FRAME2_PATH = FolderMgr.getInstance().RESOURCE_FRAME2_PATH;
		OTHER_PATH = FolderMgr.getInstance().OTHER_PATH;
		LIMIT_PATH = FolderMgr.getInstance().LIMIT_PATH;
		FILTER_PATH = FolderMgr.getInstance().RESOURCE_FILTER_PATH;
		PREVIEW_BGM_PATH = FolderMgr.getInstance().RESOURCE_PREVIEW_BGM_PATH;
		FEATURE_MENU_PATH = FolderMgr.getInstance().FEATURE_MENU_PATH;
		RESOURCE_RED_DOT_PATH = FolderMgr.getInstance().RESOURCE_RED_DOT_PATH;
		SHAPE_DATA_PATH = FolderMgr.getInstance().SHAPE_DATA_PATH;
		AR_NEW_YEAR_PATH = FolderMgr.getInstance().AR_NEW_YEAR_DATA_PATH;

		CommonUtils.MakeFolder(FRAME_PATH);
		CommonUtils.MakeFolder(CARD_PATH);
		CommonUtils.MakeFolder(DECORATE_PATH);
		CommonUtils.MakeFolder(MAKEUP_PATH);
		CommonUtils.MakeFolder(PUZZLE_BK_PATH);
		CommonUtils.MakeFolder(PUZZLE_TEMPLATE_PATH);
		CommonUtils.MakeFolder(THEME_PATH);
		CommonUtils.MakeFolder(MOSAIC_PATH);
		CommonUtils.MakeFolder(GLASS_PATH);
		CommonUtils.MakeFolder(LOCK_PATH);
		CommonUtils.MakeFolder(BANNER_PATH);
		CommonUtils.MakeFolder(VIDEO_FACE_PATH);
		CommonUtils.MakeFolder(BUSINESS_PATH);
		CommonUtils.MakeFolder(RECOMMEND_PATH);
		CommonUtils.MakeFolder(APP_MARKET_PATH);
		CommonUtils.MakeFolder(BRUSH_PATH);
		CommonUtils.MakeFolder(FRAME2_PATH);
		CommonUtils.MakeFolder(OTHER_PATH);
        CommonUtils.MakeFolder(LIMIT_PATH);
		CommonUtils.MakeFolder(FILTER_PATH);
		CommonUtils.MakeFolder(PREVIEW_BGM_PATH);
		CommonUtils.MakeFolder(FEATURE_MENU_PATH);
		CommonUtils.MakeFolder(RESOURCE_RED_DOT_PATH);
		CommonUtils.MakeFolder(SHAPE_DATA_PATH);
	}

	@Override
	protected DownloadTaskThread MakeDownloadTaskThread(Context context, String tempPath, int threadNum, DownloadTaskThread.CallbackHandler cb)
	{
		return new MyDownloadTaskThread(context, tempPath, threadNum, cb);
	}

	private static class MyDownloadTaskThread extends DownloadTaskThread
	{
		public MyDownloadTaskThread(Context context, String tempPath, int threadNum, CallbackHandler cb)
		{
			super(context, tempPath, threadNum, cb);
		}

		@Override
		protected ResourceDownloader MakeResourceDownloader(Context context, String tempPath, ResourceDownloader.CallbackHandler cb)
		{
			return new MyResourceDownloader(context, tempPath, cb);
		}
	}

	private static class MyResourceDownloader extends ResourceDownloader
	{
		public MyResourceDownloader(Context context, String tempPath, CallbackHandler cb)
		{
			super(context, tempPath, cb);
		}

		@Override
		protected NetCore2 MakeNetCore(Context context)
		{
			return new MyNetCore(context);
		}
	}
}

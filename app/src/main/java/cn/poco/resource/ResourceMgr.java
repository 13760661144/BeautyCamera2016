package cn.poco.resource;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import cn.poco.camera3.beauty.data.BeautyResMgr;
import cn.poco.camera3.beauty.data.ShapeSyncResMgr;
import cn.poco.camera3.beauty.data.ShapeResMgr;
import cn.poco.resource.protocol.MaterialResourceProtocol;
import cn.poco.system.FolderMgr;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.FileUtil;

public class ResourceMgr
{
	public static final String NEW_FLAG_DB_NAME = "resource_new_flag";

	public static boolean BUSINESS_RES_LOCK_FLAG = true;
	public static final Object BUSINESS_RES_LOCK = new Object(); //商业素材锁,配合BUSINESS_RES_LOCK_FLAG使用

//	private static long oldTime;
//	private static long currentTime;

	/**
	 * 主线程运行
	 *
	 * @param context
	 */
	public static void PreInit(Context context)
	{

//		oldTime = System.currentTimeMillis();
		//测试链接
		MaterialResourceProtocol.IS_DEBUG = SysConfig.IsDebug();

		ReadAllOldIDFalg(context);
//		currentTime = System.currentTimeMillis();
//		System.out.println("TIME -3: " + (currentTime - oldTime));
//		oldTime = currentTime;
		BannerResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
//		currentTime = System.currentTimeMillis();
//		System.out.println("TIME -2: " + (currentTime - oldTime));
//		oldTime = currentTime;
	}

	/**
	 * 版本升级特殊判断
	 *
	 * @param context
	 */
	private static void ReadUpdate(Context context)
	{
		//TODO 旧版本升级到4.0.8（200）强制删除旧版本bgm素材
		if(TagMgr.GetTagIntValue(context, Tags.RESOURCE_PRE_BGM_VERSION) < 200)
		{
			FileUtil.deleteSDFile(FolderMgr.getInstance().RESOURCE_PREVIEW_BGM_PATH);
		}
		//TODO 旧版本升级到4.1.3（206）强制删除旧版本贴纸素材 & 已下载滤镜素材
		if (TagMgr.GetTagIntValue(context, Tags.RESOURCE_VIDEO_STICKER_VERSION) < 206)
		{
			FileUtil.deleteSDFile(FolderMgr.getInstance().RESOURCE_VIDEO_FACE_PATH);
		}
		if (TagMgr.GetTagIntValue(context, Tags.RESOURCE_FILTER_VERSION) < 206)
		{
			FileUtil.deleteSDFile(FolderMgr.getInstance().RESOURCE_FILTER_PATH);
			FileUtil.deleteSDFile(FilterRecommendResMgr2.CLOUD_CACHE_PATH);
		}
		//保存版本
		int vc = CommonUtils.GetAppVerCode(context);
		TagMgr.SetTagValue(context, Tags.RESOURCE_FILTER_VERSION, String.valueOf(vc));
		TagMgr.SetTagValue(context, Tags.RESOURCE_PRE_BGM_VERSION, String.valueOf(vc));
		TagMgr.SetTagValue(context, Tags.RESOURCE_VIDEO_STICKER_VERSION, String.valueOf(vc));
		TagMgr.Save(context);
	}

	private static void ReadBeautyShapeRes(Context context)
	{
		//从sdcard加载数据
		BeautyResMgr.getInstance().SyncGetSdcardArr(context);
		ShapeResMgr.getInstance().SyncGetSdcardArr(context);

		ShapeSyncResMgr.getInstance().SyncGetSdcardArr(context);
		ShapeSyncResMgr.getInstance().UpdateSyncData(context);
	}

	/**
	 * 有网络访问需要在线程执行
	 *
	 * @param context
	 */
	public static void Init(Context context)
	{
		ReadUpdate(context);
		ReadBeautyShapeRes(context);
		ReadAllNewFlag(context);
		ReadCloudRes(context);
	}

	protected static void ReadCloudRes(Context context)
	{
		LockResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		FrameResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		DecorateResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		synchronized(BUSINESS_RES_LOCK)
		{
			BUSINESS_RES_LOCK_FLAG = false;
			BUSINESS_RES_LOCK.notifyAll();
		}
		ThemeResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		FrameExResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		BannerResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		PreviewBgmResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		MakeupResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		MakeupComboResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		MosaicResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		GlassResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		VideoStickerResRedDotMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		VideoStickerGroupResRedDotMrg2.getInstance().sync_ac_GetCloudRes(context, null, true);
		VideoStickerResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		VideoStickerGroupResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);


		LiveVideoStickerResRedDotMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		LiveVideoStickerGroupResRedDotMrg2.getInstance().sync_ac_GetCloudRes(context, null, true);
		LiveVideoStickerResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		LiveVideoStickerGroupResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		BrushResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
//		ThemeResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		FilterResMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);
		NetTagMgr2.getInstance().sync_ac_GetCloudRes(context, null, true);

		//推荐必须在最后,2017/10/14因为推荐为没考虑logo下载问题所以改为获取后同步下载logo,缓存的fast download无效
		BrushRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		FilterRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		FrameExRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		FrameRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		GlassRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		MakeupComboRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		MosaicRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
		PendantRecommendResMgr2.getInstance().sync_ac_GetCloudRes(context, null, false);
	}

	/**
	 * 非UI线程调用,线程阻塞
	 *
	 * @param context
	 */
	public void ReloadCloudRes(Context context)
	{
		ReadCloudRes(context);
	}

	public static void WaitBusinessRes()
	{
		try
		{
			while(BUSINESS_RES_LOCK_FLAG)
			{
				BUSINESS_RES_LOCK.wait();
			}
			//System.out.println("BUSINESS WAIT OK!");
		}
		catch(Throwable e)
		{
		}
	}

	protected synchronized static void ReadAllNewFlag(Context context)
	{
		try
		{
			SharedPreferences sp = context.getSharedPreferences(NEW_FLAG_DB_NAME, Context.MODE_PRIVATE);

			FrameResMgr2.getInstance().ReadNewFlagArr(context, sp);
			FrameExResMgr2.getInstance().ReadNewFlagArr(context, sp);
			DecorateResMgr2.getInstance().ReadNewFlagArr(context, sp);
			MakeupComboResMgr2.getInstance().ReadNewFlagArr(context, sp);
			MosaicResMgr2.getInstance().ReadNewFlagArr(context, sp);
			GlassResMgr2.getInstance().ReadNewFlagArr(context, sp);
			VideoStickerResMgr2.getInstance().ReadNewFlagArr(context, sp);
			LiveVideoStickerResMgr2.getInstance().ReadNewFlagArr(context, sp);
			FilterResMgr2.getInstance().ReadNewFlagArr(context, sp);
			BrushResMgr2.getInstance().ReadNewFlagArr(context, sp);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}


	protected synchronized static void ReadAllOldIDFalg(Context context)
	{
		try
		{
			SharedPreferences sp = context.getSharedPreferences(NEW_FLAG_DB_NAME, Context.MODE_PRIVATE);
			FrameResMgr2.getInstance().ReadOldId(sp);
			FrameExResMgr2.getInstance().ReadOldId(sp);
			DecorateResMgr2.getInstance().ReadOldId(sp);
			GlassResMgr2.getInstance().ReadOldId(sp);
			MosaicResMgr2.getInstance().ReadOldId(sp);
			VideoStickerResMgr2.getInstance().ReadOldId(sp);
			BrushResMgr2.getInstance().ReadOldId(sp);
			FilterResMgr2.getInstance().ReadOldId(sp);
			MakeupComboResMgr2.getInstance().ReadOldId(sp);
			ThemeResMgr2.getInstance().ReadOldId(sp);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}

	public synchronized static void UpdateNewFlag(Context context, ArrayList<Integer> dst, String flag)
	{
		StringBuffer buf = new StringBuffer(192);
		int size = dst.size();
		for(int i = 0; i < size; i++)
		{
			if(i != 0)
			{
				buf.append(",");
				buf.append(dst.get(i));
			}
			else
			{
				buf.append(dst.get(i));
			}
		}
		if(context == null) return;
		SharedPreferences sp = context.getSharedPreferences(NEW_FLAG_DB_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(flag, buf.toString());
		editor.commit();
	}

	public synchronized static void UpdateOldIDFlag(Context context, int id, String flag)
	{
		SharedPreferences sp = context.getSharedPreferences(NEW_FLAG_DB_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(flag, id);
		editor.commit();
	}

	public static void DeleteNewFlag(Context context, ArrayList<Integer> dst, String flag, int id)
	{
		if(dst != null && flag != null)
		{
			if(ResourceUtils.DeleteId(dst, id))
			{
				UpdateNewFlag(context, dst, flag);
			}
		}
	}

	public static void AddNewFlag(Context context, ArrayList<Integer> dst, String flag, int id)
	{
		if(dst != null && flag != null)
		{
			if(ResourceUtils.HasId(dst, id) < 0)
			{
				dst.add(0, id);
				UpdateNewFlag(context, dst, flag);
			}
		}
	}
}

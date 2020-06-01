package cn.poco.MaterialMgr2;

import android.content.Context;

import java.util.ArrayList;

import cn.poco.credits.Credit;
import cn.poco.framework.MyFramework2App;
import cn.poco.resource.BaseRes;
import cn.poco.resource.BrushRes;
import cn.poco.resource.BrushResMgr2;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FilterRes;
import cn.poco.resource.FilterResMgr2;
import cn.poco.resource.FrameExRes;
import cn.poco.resource.FrameExResMgr2;
import cn.poco.resource.FrameRes;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.GlassRes;
import cn.poco.resource.GlassResMgr2;
import cn.poco.resource.IDownload;
import cn.poco.resource.LockRes;
import cn.poco.resource.LockResMgr2;
import cn.poco.resource.MakeupComboResMgr2;
import cn.poco.resource.MakeupRes;
import cn.poco.resource.MosaicRes;
import cn.poco.resource.MosaicResMgr2;
import cn.poco.resource.ResType;
import cn.poco.resource.ThemeRes;
import cn.poco.resource.ThemeResMgr2;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import my.beautyCamera.R;

public class MgrUtils
{
	private static int m_prepareCount = 0; //没有下载的个数
	private static int m_downloadingCount = 0;
	private static int m_completeCount = 0;

	public static LockRes unLockTheme(int themeId)
	{
		ArrayList<LockRes> infos = LockResMgr2.getInstance().getThemeLockArr();
		if(infos != null)
		{
			int num = infos.size();
			for(int i = 0; i < num; i++)
			{
				LockRes lockInfo = infos.get(i);
				if(lockInfo != null)
				{
					if(lockInfo.m_id == themeId)
					{
						return lockInfo;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 检查一组素材的下载状态
	 *
	 * @param ress        该组素材
	 * @param downloadIds 下载完成的素材的id, 可以为null
	 * @return 该组素材的下载状态
	 */
	public static int checkGroupDownloadState(ArrayList<? extends BaseRes> ress, ArrayList<Integer> downloadIds)
	{
		m_prepareCount = 0;
		m_downloadingCount = 0;
		m_completeCount = 0;

		if(null == ress || ress.size() == 0) return BaseItemInfo.PREPARE;
		for(BaseRes res : ress)
		{
			int flag = checkDownloadState(res);
			if(flag != 0)
			{
				m_downloadingCount++;
			}
			else
			{
				if(res.m_type == BaseRes.TYPE_NETWORK_URL)
				{
					m_prepareCount++;
				}
				else
				{
					m_completeCount++;
					if(null != downloadIds)
					{
						downloadIds.add(res.m_id);
					}
				}
			}
		}

		if(m_downloadingCount != 0)
		{
			return BaseItemInfo.LOADING;
		}
		else if(m_completeCount == ress.size())
		{
			return BaseItemInfo.COMPLETE;
		}
		else if(m_prepareCount == ress.size())
		{
			return BaseItemInfo.PREPARE;
		}
		else
		{
			return BaseItemInfo.CONTINUE;
		}
	}

	/**
	 * 获取某个资源的下载状态
	 *
	 * @param res
	 * @return 0:没有下载
	 * 1:等待中
	 * 2:下载中
	 */
	public static int checkDownloadState(BaseRes res)
	{
		int flag = 0;
		if(res != null && res.m_type == BaseRes.TYPE_NETWORK_URL)
		{
			flag = DownloadMgr.getInstance().GetStateById(res.m_id, res.getClass());
		}
		return flag;
	}

	public static void AddThemeCredit(Context context, ThemeRes res)
	{
		ArrayList<BaseItemInfo> ress = GetThemeInfos(context, res);
		if(ress != null)
		{
			int count = ress.size();
			if(count > 0)
			{
				int flag = -1;
				int state2 = 0; //下载完成
				for(int i = 0; i < count; i++)
				{
					flag = ress.get(i).m_state;
					if(flag == BaseItemInfo.COMPLETE)
					{
						state2++;
					}
				}
				if(state2 == count)
				{
					String params = Credit.APP_ID + Credit.THEME + res.m_id;
					Credit.CreditIncome(params, MyFramework2App.getInstance().getApplicationContext(), R.integer.积分_首次使用新素材);
					return;
				}
			}
		}
	}

	public static ArrayList<BaseItemInfo> GetThemeInfos(Context context, ThemeRes res)
	{
		ArrayList<BaseItemInfo> out = new ArrayList<>();
		if(res != null)
		{
			BaseItemInfo itemInfo;

			boolean lock = false;
			LockRes lockRes = MgrUtils.unLockTheme(res.m_id);
			if(lockRes != null && lockRes.m_shareType != LockRes.SHARE_TYPE_NONE && TagMgr.CheckTag(context, Tags.THEME_UNLOCK + res.m_id))
			{
				lock = true;
			}

			if(res.m_frameIDArr != null && res.m_frameIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.FRAME);
				itemInfo.m_name = context.getString(R.string.material_frame);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<BaseRes>();
				itemInfo.m_ress.addAll(FrameResMgr2.getInstance().GetResArr2(res.m_frameIDArr, false));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_frameIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
			if(res.m_sFrameIDArr != null && res.m_sFrameIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.FRAME2);
				itemInfo.m_name = context.getString(R.string.material_frame2);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<>();
				itemInfo.m_ress.addAll(FrameExResMgr2.getInstance().GetResArr(res.m_sFrameIDArr, false));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_sFrameIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
			if(res.m_decorateIDArr != null && res.m_decorateIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.DECORATE);
				itemInfo.m_name = context.getString(R.string.material_decorate);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<BaseRes>();
				itemInfo.m_ress.addAll(DecorateResMgr2.getInstance().GetResArr2(res.m_decorateIDArr, false));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_decorateIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
			if(res.m_makeupIDArr != null && res.m_makeupIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.MAKEUP_GROUP);
				itemInfo.m_name = context.getString(R.string.material_makeup);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<BaseRes>();
				itemInfo.m_ress.addAll(MakeupComboResMgr2.getInstance().GetResArr(res.m_makeupIDArr));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_makeupIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
			if(res.m_glassIDArr != null && res.m_glassIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.GLASS);
				itemInfo.m_name = context.getString(R.string.material_glass);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<BaseRes>();
				itemInfo.m_ress.addAll(GlassResMgr2.getInstance().GetResArr(res.m_glassIDArr));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_glassIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
			if(res.m_mosaicIDArr != null && res.m_mosaicIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.MOSAIC);
				itemInfo.m_name = context.getString(R.string.material_mosaic);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<BaseRes>();
				itemInfo.m_ress.addAll(MosaicResMgr2.getInstance().GetResArr(res.m_mosaicIDArr));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_mosaicIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
			if(res.m_brushIDArr != null && res.m_brushIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.BRUSH);
				itemInfo.m_name = context.getString(R.string.material_brush);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<BaseRes>();
				itemInfo.m_ress.addAll(BrushResMgr2.getInstance().GetResArr(res.m_brushIDArr, false));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_brushIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
			if (res.m_filterIDArr != null && res.m_filterIDArr.length > 0)
			{
				itemInfo = new BaseItemInfo(res, ResType.FILTER);
				itemInfo.m_name = context.getString(R.string.material_filter);
				itemInfo.m_lock = lock;
				itemInfo.m_ress = new ArrayList<BaseRes>();
				itemInfo.m_ress.addAll(FilterResMgr2.getInstance().GetResArr(res.m_filterIDArr, false));
				itemInfo.m_state = MgrUtils.checkGroupDownloadState(itemInfo.m_ress, null);
				itemInfo.m_progress = (int)(MgrUtils.getM_completeCount() / (float)res.m_filterIDArr.length * 100);
				itemInfo.isAllShow = true;
				out.add(itemInfo);
			}
		}
		return out;
	}

	public static void AddDownloadTj(ResType type, ThemeRes theme)
	{
		if(type != null && theme != null)
		{
			switch(type)
			{
				case FRAME:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.相框, false, theme.m_tjId + "");
					break;
				}
				case DECORATE:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.贴图, false, theme.m_tjId + "");
					break;
				}
				case MAKEUP_GROUP:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.彩妆, false, theme.m_tjId + "");
					break;
				}
				case MOSAIC:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.马赛克, false, theme.m_tjId + "");
					break;
				}
				case GLASS:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.毛玻璃, false, theme.m_tjId + "");
					break;
				}
				case BRUSH:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.指尖魔法, false, theme.m_tjId + "");
					break;
				}
				case FRAME2:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.简约边框, false, theme.m_tjId + "");
					break;
				}
				case FILTER:
				{
					MyBeautyStat.onDownloadRes(MyBeautyStat.DownloadType.滤镜, false, theme.m_tjId + "");
					break;
				}
			}
		}
	}

	/**
	 * @param themeID
	 * @return 0 未下载
	 * 1 没有获取到资源
	 * 2 已下载
	 */
	public static int hasDownloadFrame(Context context, int themeID)
	{
		ThemeRes res = getThemeRes(context, themeID);
		if(res == null) return 1;
		ArrayList<FrameRes> frameRes = FrameResMgr2.getInstance().GetResArr2(res.m_frameIDArr, false);
		if(frameRes == null || frameRes.size() <= 0) return 1;
		int state = checkGroupDownloadState(frameRes, null);
		if(state == BaseItemInfo.PREPARE) return 0;
		return 2;
	}

	public static int hasDownloadSimpleFrame(Context context, int themeID)
	{
		ThemeRes res = getThemeRes(context, themeID);
		if(res == null) return 1;
		ArrayList<FrameExRes> frameRes = FrameExResMgr2.getInstance().GetResArr(res.m_sFrameIDArr, false);
		if(frameRes == null || frameRes.size() <= 0) return 1;
		int state = checkGroupDownloadState(frameRes, null);
		if(state == BaseItemInfo.PREPARE) return 0;
		return 2;
	}

	public static int hasDownloadFilter(Context context, int themeID)
	{
		ThemeRes res = getThemeRes(context, themeID);
		if(res == null) return 1;
		ArrayList<FilterRes> filterRes = FilterResMgr2.getInstance().GetResArr(res.m_filterIDArr, false);
		if(filterRes == null || filterRes.size() <= 0) return 1;
		int state = checkGroupDownloadState(filterRes, null);
		if(state == BaseItemInfo.PREPARE) return 0;
		return 2;
	}

	public static int hasDownloadMosaic(Context context, int themeID)
	{
		ThemeRes res = getThemeRes(context, themeID);
		if(res == null) return 1;
		ArrayList<MosaicRes> mosaicRes = MosaicResMgr2.getInstance().GetResArr(res.m_mosaicIDArr);
		if(mosaicRes == null || mosaicRes.size() <= 0) return 1;
		int state = checkGroupDownloadState(mosaicRes, null);
		if(state == BaseItemInfo.PREPARE) return 0;
		return 2;
	}

	public static int hasDownloadGlass(Context context, int themeID)
	{
		ThemeRes res = getThemeRes(context, themeID);
		if(res == null) return 1;
		ArrayList<GlassRes> glassRes = GlassResMgr2.getInstance().GetResArr(res.m_glassIDArr);
		if(glassRes == null || glassRes.size() <= 0) return 1;
		int state = checkGroupDownloadState(glassRes, null);
		if(state == BaseItemInfo.PREPARE) return 0;
		return 2;
	}

	public static int hasDownloadMakeup(Context context, int themeID)
	{
		ThemeRes res = getThemeRes(context, themeID);
		if(res == null) return 1;
		if(res.m_type == BaseRes.TYPE_NETWORK_URL) return 0;
		ArrayList<MakeupRes> makeups = MakeupComboResMgr2.getInstance().GetResArr(res.m_makeupIDArr);
		if(makeups == null || makeups.size() <= 0) return 1;
		int state = checkGroupDownloadState(makeups, null);
		if(state == BaseItemInfo.PREPARE) return 0;
		return 2;
	}

	public static int hasDownloadBrush(Context context, int themeID)
	{
		ThemeRes res = getThemeRes(context, themeID);
		if(res == null) return 1;
		ArrayList<BrushRes> brushRes = BrushResMgr2.getInstance().GetResArr(res.m_brushIDArr, false);
		if(brushRes == null || brushRes.size() <= 0) return 1;
		int state = checkGroupDownloadState(brushRes, null);
		if(state == BaseItemInfo.PREPARE) return 0;
		return 2;
	}

	public static ThemeRes getThemeRes(Context context, int themeID)
	{
		ArrayList<ThemeRes> ress = ThemeResMgr2.getInstance().GetAllResArr();
		if(null == ress) return null;
		int len = ress.size();
		ThemeRes res = null;
		for(int i = 0; i < len; i++)
		{
			if(ress.get(i).m_id == themeID)
			{
				res = ress.get(i);
				break;
			}
		}
		return res;
	}

	public static int getM_prepareCount()
	{
		return m_prepareCount;
	}

	public static int getM_downloadingCount()
	{
		return m_downloadingCount;
	}

	public static int getM_completeCount()
	{
		return m_completeCount;
	}

	public static class MyDownloadCB implements DownloadMgr.Callback2
	{
		MyCB m_cb;

		ArrayList<MyDownloadInfo> m_downloadInfo = new ArrayList<>();

		public MyDownloadCB(MyCB cb)
		{
			m_cb = cb;
		}

		public void setDatas(int[] ids, ResType type, int themeId, int downloadId)
		{
			MyDownloadInfo info = new MyDownloadInfo();
			info.m_ids = ids;
			info.m_type = type;
			info.m_themeId = themeId;
			info.m_downloadId = downloadId;
			m_downloadInfo.add(info);
		}

		@Override
		public void OnProgress(int downloadId, IDownload res, int progress)
		{

		}

		@Override
		public void OnComplete(int downloadId, IDownload res)
		{
			if(m_cb != null)
			{
				m_cb.OnComplete(downloadId, res);
			}
		}

		@Override
		public void OnFail(final int downloadId, final IDownload res)
		{
			if(m_cb != null)
			{
				m_cb.OnFail(downloadId, res);
			}
		}

		@Override
		public void OnGroupComplete(int downloadId, IDownload[] resArr)
		{
			int size = m_downloadInfo.size();
			MyDownloadInfo info;
			for(int i = 0; i < size; i++)
			{
				info = m_downloadInfo.get(i);
				if(info.m_downloadId == downloadId)
				{
					addToNewMgr(info.m_type, info.m_themeId, info.m_ids);
					break;
				}
			}
			if(m_cb != null)
			{
				m_cb.OnGroupComplete(downloadId, resArr);
			}
		}

		@Override
		public void OnGroupFail(int downloadId, IDownload[] resArr)
		{
			if(m_cb != null)
			{
				m_cb.OnGroupFailed(downloadId, resArr);
			}
		}

		public void ClearAll()
		{
			m_cb = null;
		}

		private void addToNewMgr(ResType type, int m_themeId, int[] ids)
		{
			if(type == ResType.FRAME)
			{
				//将下载完成的边框加到本地new队列中
				FrameResMgr2.getInstance().AddGroupId(m_themeId);
				FrameResMgr2.getInstance().AddGroupNewFlag(MyFramework2App.getInstance().getApplicationContext(), m_themeId);
			}
			else if(type == ResType.DECORATE)
			{
				//将下载完成的装饰加到本地new队列中
				//System.out.println("themeId: " + m_themeId);
				DecorateResMgr2.getInstance().AddGroupId(m_themeId);
				DecorateResMgr2.getInstance().AddGroupNewFlag(MyFramework2App.getInstance().getApplicationContext(), m_themeId);
			}
			else if(type == ResType.MAKEUP_GROUP)
			{
				//将下载完成的彩妆加到本地new队列中
				MakeupComboResMgr2.getInstance().AddId(m_themeId);
				MakeupComboResMgr2.getInstance().AddNewFlag(MyFramework2App.getInstance().getApplicationContext(), m_themeId);
			}
			else if(type == ResType.MOSAIC)
			{
				MosaicResMgr2.getInstance().AddId(ids);
				MosaicResMgr2.getInstance().AddNewFlag(MyFramework2App.getInstance().getApplicationContext(), ids);
			}
			else if(type == ResType.GLASS)
			{
				GlassResMgr2.getInstance().AddId(ids);
				GlassResMgr2.getInstance().AddNewFlag(MyFramework2App.getInstance().getApplicationContext(), ids);
			}
			else if(type == ResType.BRUSH)
			{
				BrushResMgr2.getInstance().AddGroupId(m_themeId);
				BrushResMgr2.getInstance().AddGroupNewFlag(MyFramework2App.getInstance().getApplicationContext(), m_themeId);
			}
			else if(type == ResType.FRAME2)
			{
				FrameExResMgr2.getInstance().AddId(ids);
				FrameExResMgr2.getInstance().AddNewFlag(MyFramework2App.getInstance().getApplicationContext(), ids);
			} else if (type == ResType.FILTER) {
				FilterResMgr2.getInstance().AddGroupId(m_themeId);
				FilterResMgr2.getInstance().AddGroupNewFlag(MyFramework2App.getInstance().getApplicationContext(), m_themeId);
			}
		}

		@Override
		public void OnGroupProgress(int downloadId, IDownload[] resArr, int progress)
		{
			if(m_cb != null)
			{
				m_cb.OnProgress(downloadId, resArr, progress);
			}
		}
	}

	public static class MyRefreshCb
	{
		MyCB2 m_cb;

		public MyRefreshCb(MyCB2 cb)
		{
			m_cb = cb;
		}

		public void OnFinish()
		{
			if(m_cb != null)
			{
				m_cb.OnFinish();
			}
		}

		public void ClearAll()
		{
			m_cb = null;
		}
	}

	public static class MyDownloadInfo
	{
		ResType m_type;
		int[] m_ids;
		int m_themeId = -1;
		int m_downloadId;
	}

	public interface MyCB2
	{
		public void OnFinish();
	}

	/**
	 * 处理当前页面的回调函数{@link MyDownloadCB #OnComplete(int, BaseRes) #OnFail(int,
	 * BaseRes)}}
	 * 使退出的时候能够释放当前页面，防止内存泄露
	 *
	 * @author pocouser
	 */
	public interface MyCB
	{
		public void OnFail(final int downloadId, final IDownload res);

		public void OnGroupFailed(int downloadId, IDownload[] resArr);

		public void OnComplete(int downloadId, IDownload res);

		public void OnProgress(int downloadId, IDownload[] resArr, int progress);

		public void OnGroupComplete(int downloadId, IDownload[] resArr);
	}

}

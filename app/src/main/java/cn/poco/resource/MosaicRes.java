package cn.poco.resource;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;
import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class MosaicRes extends BaseRes
{
	public static final int PAINT_TYPE_FILL = 0;
	public static final int PAINT_TYPE_TILE = 1;

	public static final int POS_START = 1;
	public static final int POS_CENTER = 2;
	public static final int POS_END = 3;

	public int m_paintType = PAINT_TYPE_FILL;

	public Object m_img;
	public Object m_icon;

	public int horizontal_fill = POS_CENTER;
	public int vertical_fill = POS_CENTER;

	public String url_img;
	public String url_icon;

	public MosaicRes()
	{
		super(ResType.MOSAIC.GetValue());
	}

	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			/*
			 * thumb
			 * img
			 * icon
			 */
			int resLen = 1;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				resLen = 3;
			}
			item.m_paths = new String[resLen];
			item.m_urls = new String[resLen];
			String name = DownloadMgr.GetImgFileName(url_thumb);
			String parentPath = GetSaveParentPath();
			if(name != null && !name.equals(""))
			{
				item.m_paths[0] = parentPath + File.separator + name;
				item.m_urls[0] = url_thumb;
			}
			if(!item.m_onlyThumb)
			{
				name = DownloadMgr.GetImgFileName(url_img);
				if(name != null && !name.equals(""))
				{
					item.m_paths[1] = parentPath + File.separator + name;
					item.m_urls[1] = url_img;
				}
				name = DownloadMgr.GetImgFileName(url_icon);
				if(name != null && !name.equals(""))
				{
					item.m_paths[2] = parentPath + File.separator + name;
					item.m_urls[2] = url_icon;
				}
			}
		}
	}

	@Override
	public void OnBuildData(DownloadItem item)
	{
		if(item != null && item.m_urls.length > 0)
		{
			if(item.m_onlyThumb)
			{
				if(item.m_paths.length > 0 && item.m_paths[0] != null)
				{
					m_thumb = item.m_paths[0];
				}
			}
			else
			{
				if(item.m_paths[0] != null)
				{
					m_thumb = item.m_paths[0];
				}
				if(item.m_paths[1] != null)
				{
					m_img = item.m_paths[1];
				}
				if(item.m_paths[2] != null)
				{
					m_icon = item.m_paths[2];
				}

				//放最后避免同步问题
				if(m_type == BaseRes.TYPE_NETWORK_URL)
				{
					m_type = BaseRes.TYPE_LOCAL_PATH;
				}
			}
		}
	}

	@Override
	public String GetSaveParentPath()
	{
		return DownloadMgr.getInstance().MOSAIC_PATH;
	}

	@Override
	public void OnDownloadComplete(DownloadItem item, boolean isNet)
	{
		if(item.m_onlyThumb)
		{
		}
		else
		{
			Context context = MyFramework2App.getInstance().getApplicationContext();
			ArrayList<MosaicRes> arr = MosaicResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(isNet)
			{
				if(arr != null)
				{
					//特殊处理,增加id
					MosaicResMgr2.getInstance().AddId(m_id);

					ResourceUtils.DeleteItem(arr, m_id);
					arr.add(0, this);
					MosaicResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
			else
			{
				if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
				{
					//特殊处理,增加id
					MosaicResMgr2.getInstance().AddId(m_id);

					arr.add(0, this);
					MosaicResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
		}
	}
}

package cn.poco.resource;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;

public class BrushRes extends BaseRes
{
	public int m_ra; //旋转范围a-b
	public int m_rb;

	public float m_sa; //拖动时缩放值 a-b
	public float m_sb;

	public int m_da; //间距a-b
	public int m_db;

	public Object[] m_res;

	public String[] url_res;

	public BrushRes()
	{
		super(ResType.BRUSH.GetValue());
	}

	@Override
	public String GetSaveParentPath()
	{
		return DownloadMgr.getInstance().BRUSH_PATH;
	}

	@Override
	public void OnBuildPath(DownloadTaskThread.DownloadItem item)
	{
		if(item != null)
		{
			/*
			 *thumb
			 * m_res[]
			 */
			int resLen = 1;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				if(url_res != null)
				{
					resLen += url_res.length;
				}
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
			if(!item.m_onlyThumb && resLen > 1)
			{
				for(int i = 0; i < url_res.length; i++)
				{
					name = DownloadMgr.GetImgFileName(url_res[i]);
					if(name != null && !name.equals(""))
					{
						item.m_paths[i + 1] = parentPath + File.separator + name;
						item.m_urls[i + 1] = url_res[i];
					}
				}
			}
		}
	}

	@Override
	public void OnBuildData(DownloadTaskThread.DownloadItem item)
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
				m_res = new Object[item.m_paths.length - 1];
				for(int i = 0; i < m_res.length; i++)
				{
					if(item.m_paths[i + 1]!=null)
					{
						m_res[i] = item.m_paths[i + 1];
					}
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
	public void OnDownloadComplete(DownloadTaskThread.DownloadItem item, boolean isNet)
	{
		if(item.m_onlyThumb)
		{
		}
		else
		{
			Context context = MyFramework2App.getInstance().getApplicationContext();
			ArrayList<BrushRes> arr = BrushResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(isNet)
			{
				if(arr != null)
				{
					ResourceUtils.DeleteItem(arr, m_id);
					arr.add(0, this);
					BrushResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
			else
			{
				if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
				{
					arr.add(0, this);
					BrushResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
		}
	}
}

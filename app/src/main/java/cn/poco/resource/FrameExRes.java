package cn.poco.resource;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;

public class FrameExRes extends BaseRes
{
	public int mMaskColor;

	public Object f1_1;
	public Object m1_1;
	public float m1_1_x;
	public float m1_1_y;
	public float m1_1_w;
	public float m1_1_h;

	public Object f4_3;
	public Object m4_3;
	public float m4_3_x;
	public float m4_3_y;
	public float m4_3_w;
	public float m4_3_h;

	public Object f3_4;
	public Object m3_4;
	public float m3_4_x;
	public float m3_4_y;
	public float m3_4_w;
	public float m3_4_h;

	public String url_f1_1;
	public String url_m1_1;
	public String url_f4_3;
	public String url_m4_3;
	public String url_f3_4;
	public String url_m3_4;

	public FrameExRes()
	{
		super(ResType.FRAME2.GetValue());
	}


	@Override
	public void OnBuildPath(DownloadTaskThread.DownloadItem item)
	{
		if(item != null)
		{
			/*
			 *
			 * m1_1
			 * m4_3
			 * m3_4
			 */
			int resLen = 1;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				resLen = 7;
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
				name = DownloadMgr.GetImgFileName(url_m1_1);
				if(name != null && !name.equals(""))
				{
					item.m_paths[1] = parentPath + File.separator + name;
					item.m_urls[1] = url_m1_1;
				}
				name = DownloadMgr.GetImgFileName(url_m4_3);
				if(name != null && !name.equals(""))
				{
					item.m_paths[2] = parentPath + File.separator + name;
					item.m_urls[2] = url_m4_3;
				}
				name = DownloadMgr.GetImgFileName(url_m3_4);
				if(name != null && !name.equals(""))
				{
					item.m_paths[3] = parentPath + File.separator + name;
					item.m_urls[3] = url_m3_4;
				}
				name = DownloadMgr.GetImgFileName(url_f1_1);
				if(name != null && !name.equals(""))
				{
					item.m_paths[4] = parentPath + File.separator + name;
					item.m_urls[4] = url_f1_1;
				}
				name = DownloadMgr.GetImgFileName(url_f3_4);
				if(name != null && !name.equals(""))
				{
					item.m_paths[5] = parentPath + File.separator + name;
					item.m_urls[5] = url_f3_4;
				}
				name = DownloadMgr.GetImgFileName(url_f4_3);
				if(name != null && !name.equals(""))
				{
					item.m_paths[6] = parentPath + File.separator + name;
					item.m_urls[6] = url_f4_3;
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
				if(item.m_paths[1] != null)
				{
					m1_1 = item.m_paths[1];
				}
				if(item.m_paths[2] != null)
				{
					m4_3 = item.m_paths[2];
				}
				if(item.m_paths[3] != null)
				{
					m3_4 = item.m_paths[3];
				}
				if(item.m_paths[4] != null)
				{
					f1_1 = item.m_paths[4];
				}
				if(item.m_paths[5] != null)
				{
					f3_4 = item.m_paths[5];
				}
				if(item.m_paths[6] != null)
				{
					f4_3 = item.m_paths[6];
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
		return DownloadMgr.getInstance().FRAME2_PATH;
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
			ArrayList<FrameExRes> arr = FrameExResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(isNet)
			{
				if(arr != null)
				{
					ResourceUtils.DeleteItem(arr, m_id);
					arr.add(0, this);
					FrameExResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
			else
			{
				if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
				{
					arr.add(0, this);
					FrameExResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
		}
	}
}

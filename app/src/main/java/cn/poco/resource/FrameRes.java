package cn.poco.resource;

import android.content.Context;
import android.util.SparseArray;

import java.io.File;

import cn.poco.framework.MyFramework2App;
import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class FrameRes extends BaseRes
{
	public static final int FRAME_TYPE_PIECE = 0x01;
	public static final int FRAME_TYPE_IMAGE = 0x02;

	public int m_frameType = FRAME_TYPE_IMAGE;

	public Object f1_1;
	public Object f4_3;
	public Object f3_4;
	public Object f16_9;
	public Object f9_16;
	public Object f_bk; //背景资源
	public Object f_top;
	public Object f_middle;
	public Object f_bottom;

	public String url_f1_1;
	public String url_f4_3;
	public String url_f3_4;
	public String url_f16_9;
	public String url_f9_16;
	public String url_f_bk; //背景资源
	public String url_f_top;
	public String url_f_middle;
	public String url_f_bottom;

	public int m_bkColor = 0xffffffff; //背景颜色值

	public FrameRes()
	{
		super(ResType.FRAME.GetValue());
	}

	@Override
	public void CopyTo(BaseRes dst)
	{
		super.CopyTo(dst);

		if(dst instanceof FrameRes)
		{
			FrameRes dst2 = (FrameRes)dst;
			dst2.m_frameType = FRAME_TYPE_IMAGE;
			dst2.f1_1 = f1_1;
			dst2.f4_3 = f4_3;
			dst2.f3_4 = f3_4;
			dst2.f16_9 = f16_9;
			dst2.f9_16 = f9_16;
			dst2.f_bk = f_bk;
			dst2.f_top = f_top;
			dst2.f_middle = f_middle;
			dst2.f_bottom = f_bottom;
			dst2.url_f1_1 = url_f1_1;
			dst2.url_f4_3 = url_f4_3;
			dst2.url_f3_4 = url_f3_4;
			dst2.url_f16_9 = url_f16_9;
			dst2.url_f9_16 = url_f9_16;
			dst2.url_f_bk = url_f_bk;
			dst2.url_f_top = url_f_top;
			dst2.url_f_middle = url_f_middle;
			dst2.url_f_bottom = url_f_bottom;
			dst2.m_bkColor = m_bkColor;
		}
	}

	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			/*
			 * thumb
			 * f1_1
			 * f4_3
			 * f3_4
			 * f16_9
			 * f9_16
			 * f_bk
			 * f_top
			 * f_middle
			 * f_bottom
			 */
			int resLen = 1;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				resLen = 10;
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
				name = DownloadMgr.GetImgFileName(url_f1_1);
				if(name != null && !name.equals(""))
				{
					item.m_paths[1] = parentPath + File.separator + name;
					item.m_urls[1] = url_f1_1;
				}
				name = DownloadMgr.GetImgFileName(url_f4_3);
				if(name != null && !name.equals(""))
				{
					item.m_paths[2] = parentPath + File.separator + name;
					item.m_urls[2] = url_f4_3;
				}
				name = DownloadMgr.GetImgFileName(url_f3_4);
				if(name != null && !name.equals(""))
				{
					item.m_paths[3] = parentPath + File.separator + name;
					item.m_urls[3] = url_f3_4;
				}
				name = DownloadMgr.GetImgFileName(url_f16_9);
				if(name != null && !name.equals(""))
				{
					item.m_paths[4] = parentPath + File.separator + name;
					item.m_urls[4] = url_f16_9;
				}
				name = DownloadMgr.GetImgFileName(url_f9_16);
				if(name != null && !name.equals(""))
				{
					item.m_paths[5] = parentPath + File.separator + name;
					item.m_urls[5] = url_f9_16;
				}
				name = DownloadMgr.GetImgFileName(url_f_bk);
				if(name != null && !name.equals(""))
				{
					item.m_paths[6] = parentPath + File.separator + name;
					item.m_urls[6] = url_f_bk;
				}
				name = DownloadMgr.GetImgFileName(url_f_top);
				if(name != null && !name.equals(""))
				{
					item.m_paths[7] = parentPath + File.separator + name;
					item.m_urls[7] = url_f_top;
				}
				name = DownloadMgr.GetImgFileName(url_f_middle);
				if(name != null && !name.equals(""))
				{
					item.m_paths[8] = parentPath + File.separator + name;
					item.m_urls[8] = url_f_middle;
				}
				name = DownloadMgr.GetImgFileName(url_f_bottom);
				if(name != null && !name.equals(""))
				{
					item.m_paths[9] = parentPath + File.separator + name;
					item.m_urls[9] = url_f_bottom;
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
					f1_1 = item.m_paths[1];
				}
				if(item.m_paths[2] != null)
				{
					f4_3 = item.m_paths[2];
				}
				if(item.m_paths[3] != null)
				{
					f3_4 = item.m_paths[3];
				}
				if(item.m_paths[4] != null)
				{
					f16_9 = item.m_paths[4];
				}
				if(item.m_paths[5] != null)
				{
					f9_16 = item.m_paths[5];
				}
				if(item.m_paths[6] != null)
				{
					f_bk = item.m_paths[6];
				}
				if(item.m_paths[7] != null)
				{
					f_top = item.m_paths[7];
				}
				if(item.m_paths[8] != null)
				{
					f_middle = item.m_paths[8];
				}
				if(item.m_paths[9] != null)
				{
					f_bottom = item.m_paths[9];
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
		return DownloadMgr.getInstance().FRAME_PATH;
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
			SparseArray<FrameRes> arr = FrameResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(isNet)
			{
				if(arr != null)
				{
					arr.put(m_id, this);
					FrameResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
			else
			{
				if(arr != null && arr.get(m_id) == null)
				{
					arr.put(m_id, this);
					FrameResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
		}
	}
}

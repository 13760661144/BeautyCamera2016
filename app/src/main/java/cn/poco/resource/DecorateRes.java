package cn.poco.resource;

import android.content.Context;
import android.util.SparseArray;

import java.io.File;
import java.util.HashMap;

import cn.poco.framework.MyFramework2App;
import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class DecorateRes extends BaseRes
{
	public Object m_res;

	public String url_res;

	public DecorateRes()
	{
		super(ResType.DECORATE.GetValue());
	}

	@Override
	public void CopyTo(BaseRes dst)
	{
		super.CopyTo(dst);

		if(dst instanceof DecorateRes)
		{
			DecorateRes dst2 = (DecorateRes)dst;
			dst2.m_res = m_res;
			dst2.url_res = url_res;
		}
	}

	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			/*
			 * thumb
			 * res
			 */
			int resLen = 1;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				resLen = 2;
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
				name = DownloadMgr.GetImgFileName(url_res);
				if(name != null && !name.equals(""))
				{
					item.m_paths[1] = parentPath + File.separator + name;
					item.m_urls[1] = url_res;
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
					m_res = item.m_paths[1];
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
		return DownloadMgr.getInstance().DECORATE_PATH;
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
			SparseArray<DecorateRes> sdcardResArr = DecorateResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(isNet)
			{
				if(sdcardResArr != null)
				{
					sdcardResArr.put(m_id, this);
					DecorateResMgr2.getInstance().sync_SaveSdcardRes(context, sdcardResArr);
				}
			}
			else
			{
				if(sdcardResArr != null && sdcardResArr.get(m_id) == null)
				{
					sdcardResArr.put(m_id, this);
					DecorateResMgr2.getInstance().sync_SaveSdcardRes(context, sdcardResArr);
				}
			}
		}
	}
}

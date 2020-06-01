package cn.poco.resource;

import java.io.File;

import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class LockRes extends BaseRes
{
	public static final int SHARE_TYPE_NONE = 0;
	public static final int SHARE_TYPE_WEIXIN = 0x1;
	public static final int SHARE_TYPE_MARKET = 0x2;

	public Object m_showImg;
	public String m_showContent;

	public int m_shareType = SHARE_TYPE_NONE;
	public Object m_shareImg;
	public String m_shareContent;
	public String m_shareLink;

	public String url_showImg;
	public String url_shareImg;

	public LockRes()
	{
		super(ResType.LOCK.GetValue());
	}
	
	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			/*
			 * showImg
			 * shareImg
			 */
			int resLen = 0;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				resLen = 2;
			}
			item.m_paths = new String[resLen];
			item.m_urls = new String[resLen];
			if(!item.m_onlyThumb)
			{
				String name = DownloadMgr.GetImgFileName(url_showImg);
				String parentPath = GetSaveParentPath();
				if(name != null && !name.equals(""))
				{
					item.m_paths[0] = parentPath + File.separator + name;
					item.m_urls[0] = url_showImg;
				}
				name = DownloadMgr.GetImgFileName(url_shareImg);
				if(name != null && !name.equals(""))
				{
					item.m_paths[1] = parentPath + File.separator + name;
					item.m_urls[1] = url_shareImg;
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
					m_showImg = item.m_paths[0];
				}
				if(item.m_paths[1] != null)
				{
					m_shareImg = item.m_paths[1];
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
		return DownloadMgr.getInstance().LOCK_PATH;
	}

	@Override
	public void OnDownloadComplete(DownloadItem item, boolean isNet)
	{
		if(item.m_onlyThumb)
		{
		}
		else
		{
		}
	}
}

package cn.poco.resource;

import java.io.File;

import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class RecommendRes extends BaseRes
{
	public Object m_showImg;//弹框素材, 可为空
	public String m_showContent;

	public String url_showImg;

	//彩妆推荐位用
	public int m_bkColor = 0xFFFFFFFF;

	public RecommendRes()
	{
		super(ResType.RECOMMEND.GetValue());
	}

	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			/*
			 * thumb
			 * showImg
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
				name = DownloadMgr.GetImgFileName(url_showImg);
				if(name != null && !name.equals(""))
				{
					item.m_paths[1] = parentPath + File.separator + name;
					item.m_urls[1] = url_showImg;
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
					m_showImg = item.m_paths[1];
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
		return DownloadMgr.getInstance().RECOMMEND_PATH;
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

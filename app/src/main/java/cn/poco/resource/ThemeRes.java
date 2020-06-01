package cn.poco.resource;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;
import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class ThemeRes extends BaseRes
{
	public String m_detail; //主题介绍
	public Object m_pic; //主题内容页介绍图
	public int m_tjShowId; //主题显示统计id
	public String m_tjLink; //商业统计链接
	public int m_order; //主题排序，可能不连续，按照数值的大小从小到大排列
	public boolean m_isHide; //是否隐藏
	public boolean m_isBusiness; //是否商业
	public Object m_decorateThumb; //装饰顶部bar缩略图
	public Object m_makeupThumb; //彩妆主题的缩略图
	public Object m_frameThumb; //边框主题的缩略图
	public Object m_brushThumb; //指尖魔法的缩略图
	public int m_makeupColor; //彩妆主题缩略图的遮罩颜色

	public String url_pic;
	public String url_decorateThumb;
	public String url_makeupThumb;
	public String url_frameThumb;
	public String url_brushThumb;

	public int[] m_frameIDArr;
	public int[] m_cardIDArr;
	public int[] m_decorateIDArr;
	public int[] m_makeupIDArr;
	public int[] m_puzzleTemplateIDArr;
	public int[] m_puzzleBkIDArr;
	public int[] m_mosaicIDArr;
	public int[] m_glassIDArr;
	public int[] m_brushIDArr;
	public int[] m_sFrameIDArr;
	public int[] m_filterIDArr;

	//滤镜主题参数
	public String m_filterDetail;//滤镜描述
	public String m_filterName;//滤镜主题名
	public String[] m_filter_theme_icon_url;
	public String m_filter_thumb_url;
	public String[] m_filter_theme_name;//滤镜名字
	public Object[] m_filter_theme_icon_res;//滤镜主题详情图标
	public Object m_filter_thumb_res;//滤镜主题缩略图
	public int m_filter_mask_color;//滤镜主题覆盖颜色

	public ThemeRes()
	{
		super(ResType.THEME.GetValue());
	}

	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			/*
			 * thumb
			 * pic
			 * decorateThumb
			 * makeupThumb
			 * frameThumb
			 */
			int resLen = 1;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				resLen = 7;
				if(m_filter_theme_icon_url != null && m_filter_theme_icon_url.length > 0)
				{
					resLen += m_filter_theme_icon_url.length;
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
			if(!item.m_onlyThumb)
			{
				name = DownloadMgr.GetImgFileName(url_pic);
				if(name != null && !name.equals(""))
				{
					item.m_paths[1] = parentPath + File.separator + name;
					item.m_urls[1] = url_pic;
				}
				name = DownloadMgr.GetImgFileName(url_decorateThumb);
				if(name != null && !name.equals(""))
				{
					item.m_paths[2] = parentPath + File.separator + name;
					item.m_urls[2] = url_decorateThumb;
				}
				name = DownloadMgr.GetImgFileName(url_makeupThumb);
				if(name != null && !name.equals(""))
				{
					item.m_paths[3] = parentPath + File.separator + name;
					item.m_urls[3] = url_makeupThumb;
				}
				name = DownloadMgr.GetImgFileName(url_frameThumb);
				if(name != null && !name.equals(""))
				{
					item.m_paths[4] = parentPath + File.separator + name;
					item.m_urls[4] = url_frameThumb;
				}
				name = DownloadMgr.GetImgFileName(url_brushThumb);
				if(name != null && !name.equals(""))
				{
					item.m_paths[5] = parentPath + File.separator + name;
					item.m_urls[5] = url_brushThumb;
				}

				name = DownloadMgr.GetImgFileName(m_filter_thumb_url);
				if(name != null && !name.equals(""))
				{
					item.m_paths[6] = parentPath + File.separator + name;
					item.m_urls[6] = m_filter_thumb_url;
				}


				if(m_filter_theme_icon_url != null && m_filter_theme_icon_url.length > 0)
				{
					for (int i = 0; i < m_filter_theme_icon_url.length; i++)
					{
						name = DownloadMgr.GetImgFileName(m_filter_theme_icon_url[i]);
						if(name != null && !name.equals(""))
						{
							item.m_paths[7 + i] = parentPath + File.separator + name;
							item.m_urls[7 + i] = m_filter_theme_icon_url[i];
						}
					}
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
					m_pic = item.m_paths[1];
				}
				if(item.m_paths[2] != null)
				{
					m_decorateThumb = item.m_paths[2];
				}
				if(item.m_paths[3] != null)
				{
					m_makeupThumb = item.m_paths[3];
				}
				if(item.m_paths[4] != null)
				{
					m_frameThumb = item.m_paths[4];
				}
				if(item.m_paths[5] != null)
				{
					m_brushThumb = item.m_paths[5];
				}
				if(item.m_paths[6] != null)
				{
					m_filter_thumb_res = item.m_paths[6];
				}

				if(item.m_paths.length > 7)
				{
					for(int i = 0; i < (item.m_paths.length - 7); i++)
					{
						if(m_filter_theme_icon_res != null)
						{
							m_filter_theme_icon_res[i] = item.m_paths[7 + i];
						}
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
	public String GetSaveParentPath()
	{
		return DownloadMgr.getInstance().THEME_PATH;
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
			ArrayList<ThemeRes> arr = ThemeResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(isNet)
			{
				if(arr != null)
				{
					ResourceUtils.DeleteItem(arr, m_id);
					arr.add(0, this);
					ThemeResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
			else
			{
				if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
				{
					arr.add(0, this);
					ThemeResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
		}
	}

	public boolean isOnlyFilter() {
		if (m_frameIDArr != null && m_frameIDArr.length > 0) {
			return false;
		}

		if (m_sFrameIDArr != null && m_sFrameIDArr.length > 0) {
			return false;
		}

		if (m_decorateIDArr != null && m_decorateIDArr.length > 0) {
			return false;
		}

		if (m_makeupIDArr != null && m_makeupIDArr.length > 0) {
			return false;
		}

		if (m_glassIDArr != null && m_glassIDArr.length > 0) {
			return false;
		}

		if (m_mosaicIDArr != null && m_mosaicIDArr.length > 0) {
			return false;
		}

		if (m_brushIDArr != null && m_brushIDArr.length > 0) {
			return false;
		}

		if (m_filterIDArr != null && m_filterIDArr.length > 0) {
			return true;
		}

		return false;
	}
}

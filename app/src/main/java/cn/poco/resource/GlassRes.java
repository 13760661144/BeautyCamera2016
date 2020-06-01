package cn.poco.resource;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;
import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class GlassRes extends BaseRes
{
	public static final int POS_CENTER = 0;//居中
	public static final int POS_START = 1;//左对齐或顶部对齐
	public static final int POS_END = 2;//右对齐或底部对齐

	public static final int GLASS_TYPE_SHAPE = 0;
	public static final int GLASS_TYPE_PENDANT = 1;

	public int m_glassType = GLASS_TYPE_SHAPE;

	/**
	 * 取值：@see {@link #POS_CENTER}
	 */
	public int horizontal_pos = POS_CENTER;//水平方向
	public int horizontal_value = 0;//水平方向偏移值百分比
	/**
	 * 取值：@see {@link #POS_CENTER}
	 */
	public int vertical_pos = POS_CENTER;//垂直方向
	public int vertical_value = 0;//垂直方向偏移值百分比

	public int h_fill_parent = -1; //水平方向,-1表示按内容缩放,0-100表示填充父图片的百分比大小
	public int v_fill_parent = -1; //垂直方向
	public int self_offset_x = 0; //-??到??
	public int self_offset_y = 0;

	public boolean m_canFreedomZoom = false; //是否可自由缩放

	public Object m_img;//素材, 可为空
	public Object m_mask;//素材mask, 不能为空

	public int m_color = 0x4DFFFFFF;//默认颜色
	public int m_scale = 80;//默认缩放百分比

	public Object m_icon;

	public String url_img;
	public String url_mask;
	public String url_icon;

	public GlassRes()
	{
		super(ResType.GLASS.GetValue());
	}

	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			/*
			 * thumb
			 * img
			 * mask
			 * icon
			 */
			int resLen = 1;
			if(item.m_onlyThumb)
			{
			}
			else
			{
				resLen = 4;
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
				name = DownloadMgr.GetImgFileName(url_mask);
				if(name != null && !name.equals(""))
				{
					item.m_paths[2] = parentPath + File.separator + name;
					item.m_urls[2] = url_mask;
				}
				name = DownloadMgr.GetImgFileName(url_icon);
				if(name != null && !name.equals(""))
				{
					item.m_paths[3] = parentPath + File.separator + name;
					item.m_urls[3] = url_icon;
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
					m_mask = item.m_paths[2];
				}
				if(item.m_paths[3] != null)
				{
					m_icon = item.m_paths[3];
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
		return DownloadMgr.getInstance().GLASS_PATH;
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
			ArrayList<GlassRes> arr = GlassResMgr2.getInstance().sync_GetSdcardRes(context, null);
			if(isNet)
			{
				if(arr != null)
				{
					//特殊处理,增加id
					GlassResMgr2.getInstance().AddId(m_id);

					ResourceUtils.DeleteItem(arr, m_id);
					arr.add(0, this);
					GlassResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
			else
			{
				if(arr != null && ResourceUtils.HasItem(arr, m_id) < 0)
				{
					//特殊处理,增加id
					GlassResMgr2.getInstance().AddId(m_id);

					arr.add(0, this);
					GlassResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
		}
	}
}

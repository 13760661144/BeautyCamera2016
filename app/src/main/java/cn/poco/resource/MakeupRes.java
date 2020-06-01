package cn.poco.resource;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import cn.poco.framework.MyFramework2App;
import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class MakeupRes extends BaseRes
{
	public MakeupData[] m_groupRes; //组合资源对象(不一定是套装)
	public int m_groupAlpha = 100; //套装总体透明度

	public Object m_thumb2; //第二种缩略图,美化界面使用
	public String url_thumb2;

	public int[] m_groupId; //组合id
	public int[] m_groupAlphas; //对应单个素材透明度,长度和m_groupId一样
	public MakeupRes[] m_groupObj;

	public int m_maskColor; //局部logo的覆盖颜色

	public static class MakeupData extends BaseRes
	{
		public MakeupData()
		{
			super(ResType.MAKEUP_DATA.GetValue());
		}

		public int m_makeupType = MakeupType.NONE.GetValue(); //资源类型
		public Object[] m_res; //素材
		public String[] url_res;
		public String url_thumb;
		public float[] m_pos; //素材位置点
		public Object m_params; //k b,例如new float[]{0.5f, 10, 1f, 0};
		public Object m_ex = 38; //叠加模式,粉底颜色
		public int m_defAlpha = 100; //默认透明度,粉底透明度

		@Override
		public String GetSaveParentPath()
		{
			return DownloadMgr.getInstance().OTHER_PATH;
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

	public MakeupRes()
	{
		super(ResType.MAKEUP.GetValue());
	}

	/**
	 * 获取套装组合每个分类的alpha
	 *
	 * @param makeupType
	 * @return
	 */
	public int GetComboAlpha(int makeupType)
	{
		int out = 100;

		if(m_groupObj != null && m_groupAlphas != null && m_groupObj.length == m_groupAlphas.length)
		{
			MakeupRes temp;
			for(int i = 0; i < m_groupObj.length; i++)
			{
				temp = m_groupObj[i];
				if(temp != null && HasType(temp.m_groupRes, makeupType))
				{
					out = m_groupAlphas[i];
					break;
				}
			}
		}

		return out;
	}

	protected static boolean HasType(MakeupData[] ress, int makeupType)
	{
		boolean out = false;

		if(ress != null)
		{
			for(int i = 0; i < ress.length; i++)
			{
				if(ress[i].m_makeupType == makeupType)
				{
					out = true;
					break;
				}
			}
		}

		return out;
	}

	@Override
	public void OnBuildPath(DownloadItem item)
	{
		if(item != null)
		{
			//构造资源数组
			if(m_groupRes != null)
			{
				MakeupData temp;
				int len = m_groupRes.length;
				for(int i = 0; i < len; i++)
				{
					temp = m_groupRes[i];
					if(temp != null && temp.url_res != null && (temp.m_res == null || temp.m_res.length != temp.url_res.length))
					{
						temp.m_res = new Object[temp.url_res.length];
					}
				}
			}
			/*
			 * thumb
			 * thumb2
			 * groupObj[0]
			 * {
			 * thumb
			 * }
			 * groupRes[0]
			 * {
			 * url_res[0]
			 * ...
			 * }
			 * ...
			 */
			int resLen = 2; //新加一个缩略图
			if(item.m_onlyThumb)
			{
			}
			else
			{
				if(m_groupObj != null)
				{
					resLen += m_groupObj.length; //每个资源的thumb
				}
				if(m_groupRes != null)
				{
					MakeupData temp;
					int len = m_groupRes.length;
					for(int i = 0; i < len; i++)
					{
						temp = m_groupRes[i];
						if(temp != null && temp.url_res != null)
						{
							resLen += temp.url_res.length;
						}
					}
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
			name = DownloadMgr.GetImgFileName(url_thumb2);
			if(name != null && !name.equals(""))
			{
				item.m_paths[1] = parentPath + File.separator + name;
				item.m_urls[1] = url_thumb2;
			}
			if(!item.m_onlyThumb)
			{
				int index = 2;
				if(m_groupObj != null)
				{
					MakeupRes temp;
					int len = m_groupObj.length;
					for(int i = 0; i < len; i++)
					{
						temp = m_groupObj[i];
						if(temp != null)
						{
							name = DownloadMgr.GetImgFileName(temp.url_thumb);
							if(name != null && !name.equals(""))
							{
								item.m_paths[index] = parentPath + File.separator + name;
								item.m_urls[index] = temp.url_thumb;
							}
						}
						index++;
					}
				}
				if(m_groupRes != null)
				{
					MakeupData temp;
					int len = m_groupRes.length;
					for(int i = 0; i < len; i++)
					{
						temp = m_groupRes[i];
						if(temp != null && temp.url_res != null)
						{
							int len2 = temp.url_res.length;
							for(int j = 0; j < len2; j++)
							{
								name = DownloadMgr.GetImgFileName(temp.url_res[j]);
								if(name != null && !name.equals(""))
								{
									item.m_paths[index] = parentPath + File.separator + name;
									item.m_urls[index] = temp.url_res[j];
								}
								index++;
							}
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
				int index = 0;
				if(item.m_paths[index] != null)
				{
					m_thumb = item.m_paths[index];
				}
				index++;
				if(item.m_paths[index] != null)
				{
					m_thumb2 = item.m_paths[index];
				}
				index++;
				if(m_groupObj != null)
				{
					MakeupRes temp;
					int len = m_groupObj.length;
					for(int i = 0; i < len; i++)
					{
						temp = m_groupObj[i];
						if(temp != null && item.m_paths[index] != null)
						{
							temp.m_thumb = item.m_paths[index];
						}
						index++;
					}
				}
				if(m_groupRes != null)
				{
					MakeupData temp;
					int len = m_groupRes.length;
					for(int i = 0; i < len; i++)
					{
						temp = m_groupRes[i];
						if(temp != null && temp.url_res != null)
						{
							int len2 = temp.url_res.length;
							for(int j = 0; j < len2; j++)
							{
								if(item.m_paths[index] != null)
								{
									temp.m_res[j] = item.m_paths[index];
								}
								index++;
							}
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
		return DownloadMgr.getInstance().MAKEUP_PATH;
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
			//组合
			if(m_groupId != null)
			{
				ArrayList<MakeupRes> comboArr = MakeupComboResMgr2.getInstance().sync_GetSdcardRes(context, null);
				if(comboArr != null)
				{
					ResourceUtils.DeleteItem(comboArr, m_id);
					comboArr.add(0, this);
					MakeupComboResMgr2.getInstance().sync_SaveSdcardRes(context, comboArr);
				}

				ArrayList<MakeupRes> sdcardArr = MakeupResMgr2.getInstance().sync_GetSdcardRes(context, null);
				if(sdcardArr != null)
				{
					ArrayList<MakeupRes> localArr = MakeupResMgr2.getInstance().sync_GetLocalRes(context, null);
					ArrayList<MakeupRes> downloadArr = MakeupResMgr2.getInstance().sync_ar_GetCloudCacheRes(context, null);
					MakeupRes temp;
					for(int id : m_groupId)
					{
						temp = ResourceUtils.GetItem(downloadArr, id);
						//改变子彩妆的素材状态
						if(temp != null && temp.m_type == BaseRes.TYPE_NETWORK_URL)
						{
							temp.m_type = BaseRes.TYPE_LOCAL_PATH;
						}
						if(ResourceUtils.HasItem(localArr, id) < 0 && ResourceUtils.HasItem(sdcardArr, id) < 0)
						{
							if(temp != null)
							{
								sdcardArr.add(0, temp);
							}
						}
					}
					MakeupResMgr2.getInstance().sync_SaveSdcardRes(context, sdcardArr);
				}
			}
			else
			{
				ArrayList<MakeupRes> arr = MakeupResMgr2.getInstance().sync_GetSdcardRes(context, null);
				if(arr != null)
				{
					ResourceUtils.DeleteItem(arr, m_id);
					arr.add(0, this);
					MakeupResMgr2.getInstance().sync_SaveSdcardRes(context, arr);
				}
			}
		}
	}
}

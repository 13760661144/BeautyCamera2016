package cn.poco.MaterialMgr2;

import java.util.ArrayList;

import cn.poco.resource.BaseRes;
import cn.poco.resource.ResType;
import cn.poco.resource.ThemeRes;

/**
 * Created by admin on 2016/5/20.
 */
public class BaseItemInfo
{
	public static final int URI_NONE = 0xFFFFFFF0;
	public static final int PREPARE = 201;	//需要下载，但是还没开始下载
	public static final int LOADING = 202;	//正在下载
	public static final int COMPLETE = 203;	//不需要下载或者下载完成
	public static final int CONTINUE = 204;	//需要下载，但是已经下载完成一部分

	public BaseItemInfo(){}

	public BaseItemInfo(ThemeRes res, ResType type)
	{
		if(res != null && type != null)
		{
			switch(type)
			{
				case FRAME:
				{
					m_ids = res.m_frameIDArr;
					break;
				}
				case DECORATE:
				{
					m_ids = res.m_decorateIDArr;
					break;
				}
				case MAKEUP_GROUP:
				{
					m_ids = res.m_makeupIDArr;
					break;
				}
				case MOSAIC:
				{
					m_ids = res.m_mosaicIDArr;
					break;
				}
				case GLASS:
				{
					m_ids = res.m_glassIDArr;
					break;
				}
				case BRUSH:
				{
					m_ids = res.m_brushIDArr;
					break;
				}
				case FRAME2:
				{
					m_ids = res.m_sFrameIDArr;
					break;
				}
				case FILTER:
				{
					m_ids = res.m_filterIDArr;
					break;
				}
			}
		}
		m_themeRes = res;
		m_type = type;
	}

	public int m_uri = URI_NONE;
	public String m_name;
	public ResType m_type = ResType.THEME;
	public ThemeRes m_themeRes;
	public ArrayList<BaseRes> m_ress;
	public int[] m_ids; //每次给资源的时候，一定要给到id，ress可以为空，ids不能为空

	public int m_downloadID;

	public int m_state =PREPARE;
	public int m_progress = 0;

	public boolean m_isChecked = false;	//CheckBox是否选中
	public boolean m_lock = false;	//加锁
	public boolean isAllShow = false;	//是否全部显示

}

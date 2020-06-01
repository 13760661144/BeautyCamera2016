package cn.poco.resource;

import cn.poco.resource.DownloadTaskThread.DownloadItem;

public class BannerRes extends BaseRes
{
	//public long m_date;
	public long m_beginTime;
	public long m_endTime;
	public String m_pos;
	public String m_cmdStr;
	public String m_tjClickUrl;
	public String m_tjShowUrl;

	public BannerRes()
	{
		super(ResType.BANNER.GetValue());
	}

	@Override
	public void CopyTo(BaseRes dst)
	{
		super.CopyTo(dst);

		if(dst != null && dst instanceof BannerRes)
		{
			BannerRes temp = (BannerRes)dst;
			temp.m_beginTime = m_beginTime;
			temp.m_endTime = m_endTime;
			temp.m_pos = m_pos;
			temp.m_cmdStr = m_cmdStr;
			temp.m_tjClickUrl = m_tjClickUrl;
			temp.m_tjShowUrl = m_tjShowUrl;
		}
	}

	public static boolean MyEquals(Object a, Object b)
	{
		boolean out = false;

		if((a == null && b == null) || (a != null && b != null && a.equals(b)))
		{
			out = true;
		}

		return out;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof BannerRes)
		{
			BannerRes temp = (BannerRes)o;
			if(!(this.m_id == temp.m_id))
			{
				return false;
			}
			if(!MyEquals(this.m_name, temp.m_name))
			{
				return false;
			}
			//if(!MyEquals(this.m_thumb, temp.m_thumb))
			//{
			//	return false;
			//}
			if(!(this.m_tjId == temp.m_tjId))
			{
				return false;
			}
			//if(!(this.m_type == temp.m_type))
			//{
			//	return false;
			//}
			if(!MyEquals(this.url_thumb, temp.url_thumb))
			{
				return false;
			}
			if(!(this.m_beginTime == temp.m_beginTime))
			{
				return false;
			}
			if(!(this.m_endTime == temp.m_endTime))
			{
				return false;
			}
			if(!MyEquals(this.m_pos, temp.m_pos))
			{
				return false;
			}
			if(!MyEquals(this.m_cmdStr, temp.m_cmdStr))
			{
				return false;
			}
			if(!MyEquals(this.m_tjClickUrl, temp.m_tjClickUrl))
			{
				return false;
			}
			if(!MyEquals(this.m_tjShowUrl, temp.m_tjShowUrl))
			{
				return false;
			}

			return true;
		}
		else
		{
			return super.equals(o);
		}
	}

	@Override
	public String GetSaveParentPath()
	{
		return DownloadMgr.getInstance().BANNER_PATH;
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

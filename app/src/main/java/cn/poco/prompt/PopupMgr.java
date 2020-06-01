package cn.poco.prompt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.banner.BannerCore3;
import cn.poco.resource.BannerRes;
import cn.poco.resource.BannerResMgr2;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.system.TagMgr;

public class PopupMgr
{
	public static boolean hasShowHome = false;
	public static boolean hasShowBeautyBefore = false;
	public static boolean hasShowBeautyAfter = false;

	protected PopupUI m_view;

	protected String m_pos;
	protected boolean m_canJumpApp;
	protected BannerRes m_res;

	protected boolean m_recycle = false;

	protected MyDownloadCallback m_dlcb;
	protected PopupUI.Callback m_popupcb;
	protected Callback m_cb;

	public static interface Callback extends PopupUI.Callback
	{
		public void OnJump(PopupMgr view, BannerRes res);
	}

	/**
	 * @param pos
	 * @param canJumpApp 能否跳转到其他APP
	 * @param cb
	 */
	public PopupMgr(Context context, String pos, boolean canJumpApp, Callback cb)
	{
		m_pos = pos;
		m_canJumpApp = canJumpApp;
		m_cb = cb;
		m_dlcb = new MyDownloadCallback(this);
		m_popupcb = new PopupUI.Callback()
		{
			@Override
			public void OnCloseBtn()
			{
				OnCancel(true);

				if(m_cb != null)
				{
					m_cb.OnCloseBtn();
				}
			}

			@Override
			public void OnClose()
			{
				ClearAll();
				m_recycle = true;
				if(m_cb != null)
				{
					m_cb.OnClose();
				}
			}

			@Override
			public void OnBtn()
			{
				OnCancel(false);
				if(m_cb != null)
				{
					m_cb.OnJump(PopupMgr.this, m_res);
				}
			}
		};

		m_res = GetShowBanner(context, m_pos, canJumpApp);

		if(m_res != null)
		{
			m_view = new PopupUI((Activity) context, m_popupcb);
		}
	}

	protected static class MyDownloadCallback implements DownloadMgr.Callback2
	{
		public PopupMgr m_thiz;

		public MyDownloadCallback(PopupMgr thiz)
		{
			m_thiz = thiz;
		}

		@Override
		public void OnProgress(int downloadId, IDownload res, int progress)
		{
		}

		@Override
		public void OnComplete(int downloadId, IDownload res)
		{
			if(m_thiz != null)
			{
				m_thiz.m_view.SetImgState(PopupUI.IMG_STATE_COMPLETE);
				m_thiz.m_view.SetImg(((BannerRes)res).m_thumb);
			}
		}

		@Override
		public void OnFail(int downloadId, IDownload res)
		{
		}

		@Override
		public void OnGroupComplete(int downloadId, IDownload[] resArr)
		{
		}

		@Override
		public void OnGroupFail(int downloadId, IDownload[] resArr)
		{
		}

		@Override
		public void OnGroupProgress(int downloadId, IDownload[] resArr, int progress)
		{
		}

		public void ClearAll()
		{
			m_thiz = null;
		}
	}

	public void Create()
	{
		if(m_view != null)
		{
			m_view.CreateUI();
			m_view.SetImg(null);
			if(m_res != null)
			{
				if(m_res.m_type == BaseRes.TYPE_NETWORK_URL)
				{
					//下载资源
					m_view.SetImgState(PopupUI.IMG_STATE_LOADING);

					DownloadMgr.getInstance().DownloadRes(m_res, m_dlcb);
				}
				else
				{
					m_view.SetImgState(PopupUI.IMG_STATE_COMPLETE);
					m_view.SetImg(m_res.m_thumb);
				}

				m_view.SetContent(m_res.m_name);
			}
		}
	}

	public void SetBk(Bitmap bkBmp)
	{
		if(m_view != null)
		{
			m_view.SetBk(bkBmp);
		}
	}

	public void Show(FrameLayout fr)
	{
		if(m_view != null && !IsShow())
		{
			SetShowState(m_pos, true);

			m_view.Show(fr);
		}
	}

	public boolean IsShow()
	{
		boolean out = false;

		if(m_view != null)
		{
			out = m_view.IsShow();
		}

		return out;
	}

	public boolean CanShow()
	{
		return m_res != null;
	}

	public boolean IsRecycle()
	{
		return m_recycle;
	}

	public void OnCancel(boolean hasAnim)
	{
		if(m_view != null)
		{
			m_view.OnCancel(hasAnim);
		}
	}

	public static BannerRes GetShowBanner(Context context, String pos, boolean canJumpApp)
	{
		BannerRes out = null;

		if(CanShow(pos))
		{
			ArrayList<BannerRes> arr = BannerResMgr2.getInstance().GetBannerResArr(pos);
			int len;
			if(arr != null && (len = arr.size()) > 0)
			{
				BannerRes res;
				for(int i = 0; i < len; i++)
				{
					res = arr.get(i);
					String flag = res.m_pos + res.m_beginTime + i;
					if(TagMgr.CheckTag(context, flag) && (canJumpApp || (!canJumpApp && !BannerCore3.IsOutsideCmd(res.m_cmdStr))))
					{
						TagMgr.SetTag(context, flag);

						out = res;
						break;
					}
				}
			}
		}

		return out;
	}

	/**
	 * 检查能否显示
	 *
	 * @return
	 */
	public static boolean CanShow(String pos)
	{
		boolean out = false;

		if(pos != null)
		{
			if(!hasShowHome && pos.equals(BannerResMgr2.B20))
			{
				out = true;
			}
			if(!hasShowBeautyBefore && pos.equals(BannerResMgr2.B21))
			{
				out = true;
			}
			if(!hasShowBeautyAfter && pos.equals(BannerResMgr2.B22))
			{
				out = true;
			}
		}

		return out;
	}

	public static void SetShowState(String pos, boolean state)
	{
		if(pos != null)
		{
			if(pos.equals(BannerResMgr2.B20))
			{
				hasShowHome = state;
			}
			if(pos.equals(BannerResMgr2.B21))
			{
				hasShowBeautyBefore = state;
			}
			if(pos.equals(BannerResMgr2.B22))
			{
				hasShowBeautyAfter = state;
			}
		}
	}

	public void ClearAll()
	{
		if(m_dlcb != null)
		{
			m_dlcb.ClearAll();
			m_dlcb = null;
		}

		if(m_view != null)
		{
			m_view.ClearAll();
			m_view = null;
		}
	}
}

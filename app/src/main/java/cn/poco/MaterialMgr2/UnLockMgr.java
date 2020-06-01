package cn.poco.MaterialMgr2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.poco.credits.Credit;
import cn.poco.image.filter;
import cn.poco.login.UserMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.LockRes;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.share.SharePage;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.MakeBmp;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

public class UnLockMgr
{
	protected UnLockUI m_view;

	protected LockRes m_res;
	private Context m_context;

	protected MyWXCallback m_wxcb;
	protected CreditCallback m_creditcb;
	protected UnLockUI.Callback m_recomcb;
	protected Callback m_cb;

	protected boolean m_recycle = false;
	protected boolean m_canClick = false;

	public static interface Callback
	{
		public void UnlockSuccess(BaseRes res);

		public void OnCloseBtn();

		public void OnBtn(int state);

		public void OnClose();

		public void OnLogin();
	}

	public UnLockMgr(Context context, LockRes res, Callback cb)
	{
		m_context = context;
		m_res = res;
		m_cb = cb;
		m_wxcb = new MyWXCallback(this, m_res.m_id);
		m_creditcb = new CreditCallback(this, m_res.m_id);
		m_recomcb = new UnLockUI.Callback()
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
			public void OnWeiXin()
			{
				switch(m_res.m_shareType)
				{
					case LockRes.SHARE_TYPE_MARKET:
					{
						OpenMarket(getContext(), m_res.m_id);
						if(m_cb != null)
						{
							m_cb.UnlockSuccess(m_res);
							m_cb.OnClose();
						}
						break;
					}

					case LockRes.SHARE_TYPE_WEIXIN:
					{
						String url = null;
						if(m_res.m_shareLink != null && m_res.m_shareLink.length() > 0)
						{
							url = m_res.m_shareLink;
						}
						SharePage.unlockResourceByWeiXin(m_context, m_res.m_name, url, MakeWXLogo(getContext(), m_res.m_shareImg), m_wxcb);
						break;
					}
				}
			}

			@Override
			public void OnCredit(String credit)
			{
				if(m_canClick)
				{
					m_canClick = false;
					int c = -1;
					if(credit != null && credit.length() > 0)
					{
						c = Integer.parseInt(credit);
					}
					if(c >= 60)
					{
						if(m_view != null)
						{
							m_view.ConsumeCredit(m_res.m_id, m_creditcb);
						}
					}
					else
					{
						if(c < 0 && !UserMgr.IsLogin(m_context,null))
						{
							if(m_cb != null)
							{
								m_cb.OnLogin();
							}
						}
						else
						{
							if(m_view != null)
							{
								m_view.showToast("当前积分余额不足！");
							}
						}
					}
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
		};

		m_view = new UnLockUI((Activity)m_context, m_recomcb);
	}

	public Context getContext()
	{
		return m_context;
	}

	public void Create()
	{
		if(m_view != null)
		{
			if(m_res != null)
			{
				DownloadMgr.getInstance().DownloadRes(m_res, null);
			}
			m_view.CreateUI();
			UpdateCredit();
			switch(m_res.m_shareType)
			{
				case LockRes.SHARE_TYPE_MARKET:
				{
					m_view.SetWeixinLockContent(R.string.unlock_share_to_market);
					break;
				}

				case LockRes.SHARE_TYPE_WEIXIN:
				{
					m_view.SetWeixinLockContent(R.string.unlock_share_to_weixin);
					break;
				}
			}
		}
	}

	public void Show(FrameLayout fr)
	{
		if(m_view != null && !IsShow())
		{
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

	public void UpdateCredit()
	{
		m_canClick = true;
		if(!UserMgr.IsLogin(m_context,null))
		{
			if(m_view != null)
			{
				m_view.unLogin();
				return;
			}
		}
		if(m_view != null)
		{
			String credit = null;
			SettingInfo info = SettingInfoMgr.GetSettingInfo(m_context);
			if(info != null)
			{
				credit = info.GetPoco2Credit();
			}
			if(credit == null)
			{
				credit = "";
			}
			m_view.SetCredit(credit);
		}
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

	/**
	 *
	 * @param bmp    图片即可，统一做毛玻璃
	 * @param needGlass
	 */
	public void SetBk(Bitmap bmp, boolean needGlass)
	{
		if(m_view != null)
		{
			if(needGlass)
			{
				bmp = filter.fakeGlassBeauty(bmp, 0x33000000);
			}
			m_view.SetBk(bmp);
		}
	}

	protected static class MyWXCallback implements SendWXAPI.WXCallListener
	{
		public UnLockMgr m_thiz;
		public int m_themeID;

		public MyWXCallback(UnLockMgr thiz, int themeID)
		{
			m_thiz = thiz;
			m_themeID = themeID;
		}

		@Override
		public void onCallFinish(int result)
		{
			if(result != BaseResp.ErrCode.ERR_USER_CANCEL && result != BaseResp.ErrCode.ERR_BAN)
			{
				if(m_thiz != null)
				{
					ClearThemeLockFlag(m_thiz.getContext(), m_themeID);
				}

				if(m_thiz != null && m_thiz.m_view != null)
				{
					if(m_thiz.m_cb != null)
					{
						m_thiz.m_cb.UnlockSuccess(m_thiz.m_res);
						m_thiz.m_cb.OnClose();
					}
				}
			}
		}

		public void ClearAll()
		{
			m_thiz = null;
		}
	}

	public static class CreditCallback implements Credit.Callback
	{
		public UnLockMgr m_thiz;
		public int m_themeID;

		public CreditCallback(UnLockMgr thiz, int themeID)
		{
			m_thiz = thiz;
			m_themeID = themeID;
		}

		@Override
		public void OnSuccess(String msg)
		{
			if(m_thiz != null)
			{
				ClearThemeLockFlag(m_thiz.getContext(), m_themeID);
			}
			if(m_thiz != null && m_thiz.m_view != null)
			{
				if(m_thiz.m_cb != null)
				{
					m_thiz.m_cb.UnlockSuccess(m_thiz.m_res);
					m_thiz.m_cb.OnClose();
				}
			}
		}

		@Override
		public void OnFailed(String msg)
		{
			if(m_thiz != null)
			{
				m_thiz.m_view.showToast(msg);
			}
		}

		public void ClearAll()
		{
			m_thiz = null;
		}
	}

	/**
	 * 清理主题锁
	 *
	 * @param themeID
	 */
	public static void ClearThemeLockFlag(Context context, int themeID)
	{
		TagMgr.SetTag(context, Tags.THEME_UNLOCK + themeID);
	}

	public static void OpenMarket(Context context, int themeID)
	{
		try
		{
			Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		catch(Throwable e)
		{
			Toast.makeText(context, "还没有安装安卓市场，请先安装", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

		ClearThemeLockFlag(context, themeID);
	}

	public static Bitmap MakeWXLogo(Context context, Object res)
	{
		Bitmap out = null;

		if(res != null)
		{
			out = cn.poco.imagecore.Utils.DecodeImage(context, res, 0, -1, -1, -1);
		}
		if(out == null)
		{
			out = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
		}
		if(out != null)
		{
			if(out.getWidth() > 180 || out.getHeight() > 180)
			{
				out = MakeBmp.CreateBitmap(out, 180, 180, -1, 0, Config.ARGB_8888);
			}
		}

		return out;
	}

	public void ClearAll()
	{
		if(m_wxcb != null)
		{
			m_wxcb.ClearAll();
			m_wxcb = null;
		}
		if(m_creditcb != null)
		{
			m_creditcb.ClearAll();
			m_creditcb = null;
		}
		if(m_view != null)
		{
			m_view.ClearAll();
			m_view = null;
		}
	}
}

package cn.poco.beautify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.poco.resource.LockRes;
import cn.poco.resource.LockResMgr2;
import cn.poco.share.SharePage;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

public class AsetUnlock
{
	public static final int ASET_LOCK_URI1 = 0x098;
	public static final int ASET_LOCK_URI2 = 0x0A6;

	protected Activity m_ac;
	protected Callback m_cb;
	protected FullScreenDlg m_unlockFr;
	protected FrameLayout m_unlockContent;
	protected ImageView m_unlockImg;
	protected ImageView m_unlockBtn;
	protected int m_unlockTempUri;

	protected AlertDialog m_5sUnlockDlg;

	public AsetUnlock(Activity ac, Callback cb)
	{
		m_ac = ac;
		m_cb = cb;
	}

	protected void MakeUnlockDlg1(int uri)
	{
		m_unlockTempUri = uri;

		if(m_unlockFr == null)
		{
			FrameLayout.LayoutParams fl;

			m_unlockFr = new FullScreenDlg(m_ac, R.style.waitDialog);
			m_unlockFr.setCancelable(true);
			m_unlockFr.m_fr.setBackgroundColor(0xF0FFFFFF);
			m_unlockContent = m_unlockFr.m_fr;
			m_unlockContent.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(m_unlockFr != null)
					{
						m_unlockFr.dismiss();
					}

					if(m_cb != null)
					{
						m_cb.OnCancel();
					}
				}
			});

			m_unlockImg = new ImageView(m_ac);
			m_unlockImg.setScaleType(ScaleType.CENTER_INSIDE);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			fl.bottomMargin = (int)(0.32f * ShareData.m_screenHeight);
			m_unlockFr.AddView(m_unlockImg, fl);

			m_unlockBtn = new ImageView(m_ac);
			m_unlockBtn.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector(m_ac, R.drawable.photofactory_makeup_unlock_btn_out, R.drawable.photofactory_makeup_unlock_btn_over));
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			fl.bottomMargin = (int)(0.17f * ShareData.m_screenHeight);
			m_unlockFr.AddView(m_unlockBtn, fl);
			m_unlockBtn.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					//String flag;
					String text;
					int logo;
					if(m_unlockTempUri == ASET_LOCK_URI1)
					{
						//flag = DECORATE_LOCK_FLAG1;
						text = "冷空气挡不住的美丽，最IN秋冬彩妆登陆美人相机！";
						logo = R.drawable.photofactory_makeup_unlock_logo1;
					}
					else
					{
						//flag = DECORATE_LOCK_FLAG2;
						text = "冷空气挡不住的美丽，最IN秋冬彩妆登陆美人相机！";
						logo = R.drawable.photofactory_makeup_unlock_logo1;
					}
					SharePage.unlockResourceByWeiXin(m_ac, text, BitmapFactory.decodeResource(m_ac.getResources(), logo), new SendWXAPI.WXCallListener()
					{
						@Override
						public void onCallFinish(int result)
						{
							if(result != BaseResp.ErrCode.ERR_USER_CANCEL)
							{
								//注意需求是否要解锁全部
								TagMgr.SetTag(m_ac, Tags.BEAUTY_ASET_LOCK1);
								TagMgr.SetTag(m_ac, Tags.BEAUTY_ASET_LOCK2);

								if(m_unlockFr != null)
								{
									m_unlockFr.dismiss();
								}

								if(m_cb != null)
								{
									m_cb.OnUnlockFinish();
								}
							}
						}

					});
					if(m_cb != null)
					{
						m_cb.OnUnlockBtn();
					}
				}
			});
		}

		if(m_unlockTempUri == ASET_LOCK_URI1)
		{
			m_unlockImg.setImageResource(R.drawable.photofactory_makeup_unlock_img1);
		}
		else
		{
			m_unlockImg.setImageResource(R.drawable.photofactory_makeup_unlock_img2);
		}
	}

	protected void MakeUnlockDlg2()
	{
		if(m_5sUnlockDlg == null)
		{
			AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(m_ac);
			dlgBuilder.setTitle("解锁");
			dlgBuilder.setMessage("给我们个五星评价就能抢先使用新素材了哦");
			dlgBuilder.setPositiveButton("是", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//市场评论解锁
					try
					{
						Uri uri = Uri.parse("market://details?id=" + m_ac.getPackageName());
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						m_ac.startActivity(intent);
					}
					catch(Throwable e)
					{
					}

					//注意需求是否要解锁全部
					TagMgr.SetTag(m_ac, Tags.BEAUTY_ASET_LOCK1);
					TagMgr.SetTag(m_ac, Tags.BEAUTY_ASET_LOCK2);

					if(m_cb != null)
					{
						m_cb.OnUnlockBtn();
						m_cb.OnUnlockFinish();
					}
				}
			});
			dlgBuilder.setNegativeButton("否", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if(m_cb != null)
					{
						m_cb.OnCancel();
					}
				}
			});

			m_5sUnlockDlg = dlgBuilder.create();
		}
	}

	public void ShowDlg(int uri)
	{
		if(LockResMgr2.getInstance().m_unlockCaiZhuang == LockRes.SHARE_TYPE_WEIXIN)
		{
			MakeUnlockDlg1(uri);
			if(m_unlockFr != null)
			{
				m_unlockFr.show();
			}
		}
		else
		{
			MakeUnlockDlg2();
			if(m_5sUnlockDlg != null)
			{
				m_5sUnlockDlg.show();
			}
		}
	}

	public void ClearAll()
	{
		if(m_unlockFr != null)
		{
			m_unlockFr.dismiss();
			m_unlockFr = null;
		}
		if(m_5sUnlockDlg != null)
		{
			m_5sUnlockDlg.dismiss();
			m_5sUnlockDlg = null;
		}
	}

	public interface Callback
	{
		public void OnCancel();

		public void OnUnlockBtn();

		public void OnUnlockFinish();
	}
}

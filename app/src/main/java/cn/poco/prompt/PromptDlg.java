package cn.poco.prompt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.poco.MaterialMgr2.MgrUtils;
import cn.poco.banner.BannerCore3;
import cn.poco.resource.BannerRes;
import cn.poco.resource.BannerResMgr2;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.statistics.TongJi2;
import cn.poco.system.TagMgr;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class PromptDlg
{
	public static boolean hasShowHome = false;
	public static boolean hasShowBeautyBefore = false;
	public static boolean hasShowBeautyAfter = false;

	private Activity m_activity;
	private CallBack m_cb;

	private FullScreenDlg m_dialog;
	private ImageView m_bg;
	private Bitmap m_bgBmp;

	private RelativeLayout m_animFr;
	private ImageView m_img;
	private ProgressBar m_progresBar;
	private TextView m_join;
	private ImageView m_popDown;
	private String m_pos;
	private ArrayList<BannerRes> m_bannerRess;
	private BannerRes m_res;
	private boolean m_canSleep = true;
	private boolean m_isJoin = false;
	private boolean m_isDoAnim = false;

	public PromptDlg(Activity activity, CallBack cb)
	{
		m_activity = activity;
		m_cb = cb;
		initUI();
	}

	private void initUI()
	{
		m_dialog = new FullScreenDlg(m_activity, R.style.waitDialog)
		{
			@Override
			public void dismiss()
			{
				if(m_cb != null)
				{
					m_cb.onCancel();
					//m_cb = null;
				}
				if(m_downloadCB != null)
				{
					m_downloadCB.ClearAll();
				}
				m_bg.setBackgroundColor(0xffffffff);
				super.dismiss();
			}
		};

		FrameLayout.LayoutParams fl;

		m_bg = new ImageView(m_activity);
		m_bg.setBackgroundColor(0xeeffffff);
		m_bg.setOnClickListener(m_clickListener);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		m_dialog.AddView(m_bg, fl);

		m_animFr = new RelativeLayout(m_activity);
		fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(500), ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(288));
		fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xhdpi(288);
		m_animFr.setLayoutParams(fl);
		m_dialog.m_fr.addView(m_animFr);
		{
			RelativeLayout.LayoutParams rl;
			FrameLayout imgFr = new FrameLayout(m_activity);
			imgFr.setBackgroundColor(0xeeffffff);
			imgFr.setId(R.id.prompt_fr);
			rl = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(500), ShareData.PxToDpi_xhdpi(500));
			rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			imgFr.setLayoutParams(rl);
			m_animFr.addView(imgFr);
			{
				m_img = new ImageView(m_activity);
				fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(500), ShareData.PxToDpi_xhdpi(500));
				imgFr.addView(m_img, fl);

				m_progresBar = new ProgressBar(m_activity);
				m_progresBar.setVisibility(View.GONE);
				m_progresBar.setIndeterminateDrawable(m_activity.getResources().getDrawable(R.drawable.prompt_progress));
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER;
				imgFr.addView(m_progresBar, fl);
			}

			m_join = new TextView(m_activity);
			m_join.setId(R.id.prompt_join);
			m_join.setOnClickListener(m_clickListener);
			//m_join.setBackgroundColor(0xff59c8af);
			m_join.setBackgroundResource(R.drawable.prompt_advanced_beautify_text_dlg_ok_btn);
			m_join.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			m_join.setTextColor(0xffffffff);
			m_join.setGravity(Gravity.CENTER);
			rl = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(92));
			rl.addRule(RelativeLayout.BELOW, R.id.prompt_fr);
			m_join.setLayoutParams(rl);
			m_animFr.addView(m_join);
		}

		m_popDown = new ImageView(m_activity);
		m_popDown.setOnClickListener(m_clickListener);
		m_popDown.setImageResource(R.drawable.homepage_vip_arrow_btn);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		int margin = ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(288) - ShareData.PxToDpi_xhdpi(597);
		fl.bottomMargin = margin / 2 - ShareData.PxToDpi_xhdpi(10);
		m_popDown.setLayoutParams(fl);
		m_dialog.m_fr.addView(m_popDown);
	}

	private View.OnClickListener m_clickListener = new View.OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if(v == m_join)
			{
				//TongJi.add_using_id_count("1066828");
				m_isJoin = true;
				hide();
			}
			else
			{
				m_isJoin = false;
				hide();
			}
		}
	};

	public void setShowDlg(String pos)
	{
		m_pos = pos;
		if(checkShow())
		{
			setDatas();
		}
	}

	/**
	 * @param pos
	 * @param canSleep 能否睡眠跳到其他软件
	 */
	public void setShowDlg(String pos, boolean canSleep)
	{
		m_canSleep = canSleep;
		setShowDlg(pos);
	}

	/**
	 * 需要复制一张,用完会自动recycle
	 *
	 * @param bmp
	 */
	public void SetBg(Bitmap bmp)
	{
		if(m_bg != null)
		{
			m_bg.setBackgroundDrawable(null);
			m_bg.setBackgroundColor(0xeeffffff);
		}
		if(m_bgBmp != null)
		{
			m_bgBmp.recycle();
			m_bgBmp = null;
		}
		m_bgBmp = bmp;
		if(m_bgBmp != null)
		{
			m_bg.setBackgroundDrawable(new BitmapDrawable(m_bgBmp));
			m_bg.invalidate();
		}
	}

	private void setDatas()
	{
		if(m_pos != null)
		{
			m_bannerRess = BannerResMgr2.getInstance().GetBannerResArr(m_pos);
			if(m_bannerRess != null && m_bannerRess.size() > 0)
			{
				int size = m_bannerRess.size();
				for(int i = 0; i < size; i++)
				{
					BannerRes res = m_bannerRess.get(i);
					String queryFlag = res.m_pos + res.m_beginTime + i;
					if(TagMgr.CheckTag(m_activity, queryFlag) && (m_canSleep || (!m_canSleep && !BannerCore3.IsOutsideCmd(res.m_cmdStr))))
					{
						setData(res);
						show();
						TagMgr.SetTag(m_activity, queryFlag);
						setHasShow(true);
						break;
					}
				}
			}
		}
	}

	/**
	 * 检查是否显示
	 *
	 * @return
	 */
	private boolean checkShow()
	{
		if(m_pos == null || m_pos.equals("")) return false;
		else
		{
			if(m_pos.equals(BannerResMgr2.B20) && !hasShowHome) return true;
			if(m_pos.equals(BannerResMgr2.B21) && !hasShowBeautyBefore) return true;
			if(m_pos.equals(BannerResMgr2.B22) && !hasShowBeautyAfter) return true;
			return false;
		}
	}

	/**
	 * 设置是否已经显示过了
	 *
	 * @param isShow
	 */
	private void setHasShow(boolean isShow)
	{
		if(m_pos.equals(BannerResMgr2.B20)) hasShowHome = isShow;
		if(m_pos.equals(BannerResMgr2.B21)) hasShowBeautyBefore = isShow;
		if(m_pos.equals(BannerResMgr2.B22)) hasShowBeautyAfter = isShow;
	}

	public void setData(BannerRes res)
	{
		m_res = res;
		m_join.setText(m_res.m_name);
		int flag = DownloadMgr.getInstance().GetStateById(m_res.m_id, m_res.getClass());
		if(flag == 0)
		{
			//还没有下载的
			if(m_res.m_type == BaseRes.TYPE_NETWORK_URL)
			{
				startDownloading(m_res);
			}
			else //已经下载完了的
			{
				m_join.setText(m_res.m_name);
				Bitmap bmp = BitmapFactory.decodeFile((String)m_res.m_thumb);
				m_img.setImageBitmap(bmp);
			}
		}
		else
		{
			startDownloading(m_res);
		}
	}

	private MgrUtils.MyDownloadCB m_downloadCB = null;

	private void startDownloading(BannerRes res)
	{
		m_progresBar.setVisibility(View.VISIBLE);
		m_downloadCB = new MgrUtils.MyDownloadCB(new MgrUtils.MyCB()
		{

			@Override
			public void OnProgress(int downloadId, IDownload[] resArr, int progress)
			{

			}

			@Override
			public void OnGroupComplete(int downloadId, IDownload[] resArr)
			{

			}

			@Override
			public void OnFail(int downloadId, IDownload res)
			{
				Toast.makeText(m_activity, "下载失败", Toast.LENGTH_SHORT).show();
				//				hide();
			}

			@Override
			public void OnGroupFailed(int downloadId, IDownload[] resArr)
			{

			}

			@Override
			public void OnComplete(int downloadId, IDownload res)
			{
				if(res == null || !(res instanceof BannerRes)) return;
				m_progresBar.setVisibility(View.GONE);
				m_img.setVisibility(View.VISIBLE);
				BannerRes lockRes = (BannerRes)res;
				Bitmap bmp = null;
				if(lockRes.m_thumb instanceof String)
				{
					bmp = BitmapFactory.decodeFile((String)lockRes.m_thumb);
				}
				else
				{
					bmp = (Bitmap)lockRes.m_thumb;
				}
				m_img.setImageBitmap(bmp);
				m_join.setText(lockRes.m_name);
			}
		});
		DownloadMgr.getInstance().DownloadRes(res, false, m_downloadCB);
	}

	public void hide()
	{
		if(m_dialog != null && !m_isDoAnim)
		{
			m_isDoAnim = true;
			doAnim1(m_animFr, false);
			doBgAnim(m_bg, 1.0f, 0.0f, true);
			doBgAnim(m_popDown, 1.0f, 0.0f, false);
		}
	}

	public void show()
	{
		if(m_dialog != null && !m_isDoAnim)
		{
			m_isDoAnim = true;
			m_dialog.show();
			doAnim(m_animFr, true);
			doBgAnim(m_bg, 0.0f, 1.0f, true);
			doBgAnim(m_popDown, 0.0f, 1.0f, false);
		}
	}

	private void doAnim(View view, boolean isOpen)
	{
		int start;
		int end;
		if(isOpen)
		{
			start = -1;
			end = 0;
		}
		else
		{
			start = 0;
			end = 1;
		}
		AnimationSet animSet = new AnimationSet(true);
		MyElasticAnimation ta = new MyElasticAnimation(AnimationSet.RELATIVE_TO_SELF, 0, AnimationSet.RELATIVE_TO_SELF, 0, AnimationSet.RELATIVE_TO_SELF, start, AnimationSet.RELATIVE_TO_SELF, end);
		ta.setDuration(450);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);
		animSet.addAnimation(ta);
		view.startAnimation(animSet);
	}

	private void doAnim1(View view, boolean isOpen)
	{
		int start;
		int end;
		if(isOpen)
		{
			start = -1;
			end = 0;
		}
		else
		{
			start = 0;
			end = 1;
		}
		AnimationSet animSet = new AnimationSet(true);
		TranslateAnimation ta = new TranslateAnimation(AnimationSet.RELATIVE_TO_SELF, 0, AnimationSet.RELATIVE_TO_SELF, 0, AnimationSet.RELATIVE_TO_SELF, start, AnimationSet.RELATIVE_TO_SELF, end);
		ta.setDuration(350);
		animSet.setFillEnabled(true);
		animSet.setFillAfter(true);
		animSet.addAnimation(ta);
		view.startAnimation(animSet);
	}

	private void doBgAnim(View v, float start, final float end, boolean lst)
	{
		AnimationSet as = new AnimationSet(true);
		AlphaAnimation aa = new AlphaAnimation(start, end);
		aa.setDuration(350);
		//as.setFillEnabled(true);
		//as.setFillAfter(true);
		as.addAnimation(aa);
		//是否监听动画结束
		if(lst)
		{
			as.setAnimationListener(new Animation.AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					m_isDoAnim = false;
					if(m_dialog != null && end == 0.0f)
					{
						m_dialog.dismiss();
					}
					if(m_isJoin)
					{
						//点击统计
						if(m_res != null)
						{
							if(m_res.m_tjClickUrl != null && m_res.m_tjClickUrl.length() > 0 && !m_res.m_tjClickUrl.equals("0"))
							{
								TongJi2.AddCountById(m_res.m_tjClickUrl);
							}
						}
						if(m_cb != null)
						{
							m_cb.onJoinClick(PromptDlg.this, m_res);
							m_cb = null;
						}
					}
				}
			});
		}
		v.startAnimation(as);
	}

	public static interface CallBack
	{
		public void onJoinClick(PromptDlg obj, BannerRes res);

		public void onCancel();
	}

}

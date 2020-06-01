package cn.poco.MaterialMgr2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.advanced.ImageUtils;
import cn.poco.credits.Credit;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 只实现了界面逻辑<BR/>
 * 1.RecomDisplayUI obj = new RecomDisplayUI(...);<BR/>
 * 2.obj.CreateUI();<BR/>
 * 3.obj.SetBk(..); / obj.SetImg(...); / obj.SetContent(...);<BR/>
 * 4.obj.SetImgState(...); / obj.SetBtnState(...);<BR/>
 * 5.obj.Show(...);<BR/>
 * 6.obj.OnCancel();<BR/>
 */
public class UnLockUI
{
	protected boolean m_uiEnabled;
	protected Activity m_ac;
	protected Callback m_cb;

	protected int m_frW;
	protected int m_frH;
	protected int m_fr3W;
	protected int m_fr3H;
	protected int m_itemH;

	protected FrameLayout m_parent;
	protected FrameLayout m_fr0;
	protected ImageView m_bk;
	protected Bitmap m_bkBmp;
	protected FrameLayout m_fr1;
	protected FrameLayout m_fr3;
	protected boolean m_animFinish = true;

	protected FrameLayout m_weixinUnlock;
	protected TextView m_weixinTip;
	protected FrameLayout m_creditUnlock;
	private TextView m_creditTip;
	private ImageView m_creditWarn;
	protected ImageView m_cancelBtn;

	protected String m_credit = "";

	public interface Callback
	{
		/**
		 * 完成关闭动画后回调
		 */
		public void OnClose();

		/**
		 * 点击关闭或空白地方
		 */
		public void OnCloseBtn();

		public void OnWeiXin();

		public void OnCredit(String credit);
	}

	public UnLockUI(Activity ac, Callback cb)
	{
		m_ac = ac;
		m_cb = cb;
	}

	public void CreateUI()
	{
		if(m_fr0 == null)
		{
			ShareData.InitData(m_ac);
			m_frW = ShareData.m_screenRealWidth;
			m_frH = ShareData.m_screenRealHeight;
			m_fr3W = ShareData.PxToDpi_xhdpi(611);
			m_itemH = ShareData.PxToDpi_xhdpi(236);
			m_fr3H = m_itemH * 2 + ShareData.PxToDpi_xhdpi(30);

			m_fr0 = new FrameLayout(m_ac);
			{
				FrameLayout.LayoutParams fl;

				m_bk = new ImageView(m_ac);
				if(m_bkBmp != null)
				{
					m_bk.setBackgroundDrawable(new BitmapDrawable(m_bkBmp));
				}
				else
				{
					m_bk.setBackgroundResource(R.drawable.login_tips_all_bk);
				}
				fl = new FrameLayout.LayoutParams(ShareData.m_screenRealWidth, ShareData.m_screenRealHeight);
				fl.gravity = Gravity.LEFT | Gravity.TOP;
				m_bk.setLayoutParams(fl);
				m_fr0.addView(m_bk);
				m_bk.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if(m_cb != null)
						{
							m_cb.OnCloseBtn();
						}
					}
				});

				/*ImageView mask = new ImageView(m_ac);
				mask.setBackgroundColor(0x33000000);
				fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
				fl.gravity = Gravity.LEFT | Gravity.TOP;
				m_fr0.addView(mask, fl);*/

				m_fr1 = new FrameLayout(m_ac);
				fl = new FrameLayout.LayoutParams(m_frW, m_frH);
				fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
				fl.bottomMargin = ShareData.PxToDpi_xhdpi(50);
				m_fr1.setLayoutParams(fl);
				m_fr0.addView(m_fr1);
				{
					int topMargin = (m_frH - m_fr3H) / 2;
					m_fr3 = new FrameLayout(m_ac);
					m_fr3.setVisibility(View.VISIBLE);
					fl = new FrameLayout.LayoutParams(m_fr3W, m_frH);
					fl.gravity = Gravity.CENTER;
					m_fr3.setLayoutParams(fl);
					m_fr1.addView(m_fr3);
					{
						ImageView bg = new ImageView(m_ac);
//						bg.setBackgroundResource(R.drawable.display_up_shadow);
						fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, m_itemH);
						fl.gravity = Gravity.TOP;
						fl.topMargin = topMargin;
						bg.setLayoutParams(fl);
						m_fr3.addView(bg);

						int itemH = ShareData.PxToDpi_xhdpi(220);//220+16
						int itemW = ShareData.PxToDpi_xhdpi(606);
						int iconW = ShareData.PxToDpi_xhdpi(110);
						m_weixinUnlock = new FrameLayout(m_ac);
						m_weixinUnlock.setOnClickListener(m_btnLst);
						m_weixinUnlock.setBackgroundResource(R.drawable.display_up_bg);
						fl = new FrameLayout.LayoutParams(itemW, itemH);
						fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
						fl.topMargin = (m_itemH - itemH) / 2 + topMargin;
						m_weixinUnlock.setLayoutParams(fl);
						m_fr3.addView(m_weixinUnlock);
						{
							ImageView icon = new ImageView(m_ac);
							icon.setScaleType(ScaleType.CENTER);
							icon.setImageResource(R.drawable.display_share_icon);
							ImageUtils.AddSkin(m_ac, icon);
							fl = new FrameLayout.LayoutParams(iconW, itemH);
							fl.gravity = Gravity.LEFT;
							fl.leftMargin = ShareData.PxToDpi_xhdpi(10);
							icon.setLayoutParams(fl);
							m_weixinUnlock.addView(icon);

							m_weixinTip = new TextView(m_ac);
							m_weixinTip.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
							m_weixinTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
							m_weixinTip.setText(R.string.unlock_share_to_weixin);
							fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(380), FrameLayout.LayoutParams.WRAP_CONTENT);
							fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
							fl.leftMargin = iconW + ShareData.PxToDpi_xhdpi(32);
							m_weixinTip.setLayoutParams(fl);
							m_weixinUnlock.addView(m_weixinTip);

							ImageView next = new ImageView(m_ac);
							next.setImageResource(R.drawable.display_choose_btn);
							ImageUtils.AddSkin(m_ac, next);
							fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
							fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
							fl.rightMargin = ShareData.PxToDpi_xhdpi(34);
							next.setLayoutParams(fl);
							m_weixinUnlock.addView(next);
						}

						TextView mid = new TextView(m_ac);
						mid.setGravity(Gravity.CENTER);
						mid.setBackgroundResource(R.drawable.display_middle_icon);
						mid.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
						mid.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
						mid.setText("OR");
						fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(40), ShareData.PxToDpi_xhdpi(40));
						fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
						fl.topMargin = itemH - ShareData.PxToDpi_xhdpi(4) + topMargin;
//						fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
						mid.setLayoutParams(fl);
						m_fr3.addView(mid);

						bg = new ImageView(m_ac);
//						bg.setBackgroundResource(R.drawable.display_down_shadow);
						fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, m_itemH);
						fl.gravity = Gravity.TOP;
						fl.topMargin = itemH + ShareData.PxToDpi_xhdpi(15) + topMargin;
						bg.setLayoutParams(fl);
						m_fr3.addView(bg);

						m_creditUnlock = new FrameLayout(m_ac);
						m_creditUnlock.setOnClickListener(m_btnLst);
						m_creditUnlock.setBackgroundResource(R.drawable.display_down_bg);
						fl = new FrameLayout.LayoutParams(itemW, itemH);
						fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
						fl.topMargin = topMargin + itemH + ShareData.PxToDpi_xhdpi(19) + (m_itemH - itemH) / 2  - ShareData.PxToDpi_xhdpi(3);
						m_creditUnlock.setLayoutParams(fl);
						m_fr3.addView(m_creditUnlock);
						{
							ImageView icon = new ImageView(m_ac);
							icon.setScaleType(ScaleType.CENTER);
							icon.setImageResource(R.drawable.display_credit_icon);
							ImageUtils.AddSkin(m_ac, icon);
							fl = new FrameLayout.LayoutParams(iconW, itemH);
							fl.gravity = Gravity.LEFT;
							fl.leftMargin = ShareData.PxToDpi_xhdpi(10);
							icon.setLayoutParams(fl);
							m_creditUnlock.addView(icon);

							LinearLayout mTextLinearLayout = new LinearLayout(m_ac);
							mTextLinearLayout.setOrientation(LinearLayout.VERTICAL);
							mTextLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
							fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, itemH);
							fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
							fl.leftMargin = iconW;
							m_creditUnlock.addView(mTextLinearLayout, fl);
							{
								TextView text = new TextView(m_ac);
								text.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
								text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
								text.setText(R.string.unlock_use_credit);
								LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
								layoutParams.gravity = Gravity.CENTER_VERTICAL;
								layoutParams.leftMargin = ShareData.PxToDpi_xhdpi(32 + 10);
								mTextLinearLayout.addView(text, layoutParams);

								LinearLayout mTextLinearLayout2 = new LinearLayout(m_ac);
								mTextLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);
								layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								layoutParams.gravity = Gravity.CENTER_VERTICAL;
								layoutParams.leftMargin = ShareData.PxToDpi_xhdpi(32 + 10);
								mTextLinearLayout.addView(mTextLinearLayout2, layoutParams);

								m_creditWarn = new ImageView(m_ac);
								m_creditWarn.setImageResource(R.drawable.display_prompt_icon);
								layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								layoutParams.gravity = Gravity.CENTER_VERTICAL;
								mTextLinearLayout2.addView(m_creditWarn, layoutParams);

								m_creditTip = new TextView(m_ac);
								m_creditTip.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
								m_creditTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								m_creditTip.setText(R.string.unlock_credit_not_enough);
								layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
								layoutParams.gravity = Gravity.CENTER_VERTICAL;
								mTextLinearLayout2.addView(m_creditTip, layoutParams);
							}

							ImageView next = new ImageView(m_ac);
							next.setImageResource(R.drawable.display_choose_btn);
							fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
							fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
							fl.rightMargin = ShareData.PxToDpi_xhdpi(34);
							ImageUtils.AddSkin(m_ac, next);
							m_creditUnlock.addView(next, fl);

							//TextView text = new TextView(m_ac);
							//text.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
							//text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
							//text.setText(R.string.unlock_use_credit);
							//fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
							//fl.gravity = Gravity.LEFT;
							//fl.topMargin = ShareData.PxToDpi_xhdpi(70);
							//fl.leftMargin = iconW + ShareData.PxToDpi_xhdpi(32);
							//text.setLayoutParams(fl);
							//m_creditUnlock.addView(text);

							//m_creditTip = new TextView(m_ac);
							//m_creditTip.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
							//m_creditTip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
							//m_creditUnlock.addView(m_creditTip);

							//m_creditWarn = new ImageView(m_ac);
							//m_creditWarn.setImageResource(R.drawable.display_prompt_icon);
							//fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
							//fl.gravity = Gravity.LEFT | Gravity.TOP;
							//fl.leftMargin = iconW + ShareData.PxToDpi_xhdpi(32);
							//fl.topMargin = ShareData.PxToDpi_xhdpi(122);
							//m_creditWarn.setLayoutParams(fl);
							//m_creditUnlock.addView(m_creditWarn);

							//m_creditTip.setText(R.string.unlock_credit_not_enough);
							//fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
							//fl.gravity = Gravity.LEFT | Gravity.TOP;
							//fl.leftMargin = iconW + ShareData.PxToDpi_xhdpi(60);
							//fl.topMargin = ShareData.PxToDpi_xhdpi(120);
							//m_creditTip.setLayoutParams(fl);

							//ImageView next = new ImageView(m_ac);
							//next.setImageResource(R.drawable.display_choose_btn);
							//ImageUtils.AddSkin(m_ac, next);
							//fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
							//fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
							//fl.rightMargin = ShareData.PxToDpi_xhdpi(34);
							//next.setLayoutParams(fl);
							//m_creditUnlock.addView(next);
						}
					}

					m_cancelBtn = new ImageView(m_ac);
					m_cancelBtn.setScaleType(ScaleType.CENTER);
					m_cancelBtn.setImageResource(R.drawable.recom_display_ui_arrow_btn);
					fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(80));
					fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
					fl.bottomMargin = ShareData.PxToDpi_xhdpi(50);
					m_cancelBtn.setLayoutParams(fl);
					m_fr1.addView(m_cancelBtn);
					m_cancelBtn.setOnClickListener(m_btnLst);
				}
			}
		}
	}

	public void SetBk(Bitmap bkBmp)
	{
		if(m_bk != null)
		{
			m_bk.setBackgroundDrawable(null);
		}
		if(m_bkBmp != null)
		{
			m_bkBmp.recycle();
			m_bkBmp = null;
		}
		m_bkBmp = bkBmp;
		if(m_bk != null)
		{
			m_bk.setBackgroundDrawable(new BitmapDrawable(m_bkBmp));
		}
		else
		{
			m_bk.setBackgroundResource(R.drawable.login_tips_all_bk);
		}
	}

	public boolean IsShow()
	{
		boolean out = false;

		if(m_parent != null && m_fr0 != null)
		{
			int len = m_parent.getChildCount();
			for(int i = 0; i < len; i++)
			{
				if(m_parent.getChildAt(i) == m_fr0)
				{
					out = true;
					break;
				}
			}
		}

		return out;
	}

	public void SetWeixinLockContent(int text)
	{
		m_weixinTip.setText(text);
	}

	public void SetCredit(String credit)
	{
		FrameLayout.LayoutParams fl;
		int iconW = ShareData.PxToDpi_xhdpi(110);
		m_credit = credit;
		if(credit != null && Integer.parseInt(credit) >= 60)
		{
			m_creditWarn.setVisibility(View.GONE);
			m_creditTip.setText(m_ac.getString(R.string.unlock_user_current_credit) + credit);
			//fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
			//fl.gravity = Gravity.LEFT | Gravity.TOP;
			//fl.leftMargin = iconW + ShareData.PxToDpi_xhdpi(32);
			//fl.topMargin = ShareData.PxToDpi_xhdpi(120);
			//m_creditTip.setLayoutParams(fl);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
			m_creditTip.setLayoutParams(layoutParams);
		}
		else
		{
			m_creditWarn.setVisibility(View.VISIBLE);
			m_creditTip.setText(R.string.unlock_credit_not_enough);
			//fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
			//fl.gravity = Gravity.LEFT | Gravity.TOP;
			//fl.leftMargin = iconW + ShareData.PxToDpi_xhdpi(60);
			//fl.topMargin = ShareData.PxToDpi_xhdpi(120);
			//m_creditTip.setLayoutParams(fl);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.CENTER_VERTICAL;
			m_creditTip.setLayoutParams(layoutParams);
		}
	}

	public void unLogin()
	{
		FrameLayout.LayoutParams fl;
		int iconW = ShareData.PxToDpi_xhdpi(110);
		m_creditWarn.setVisibility(View.VISIBLE);
		m_creditTip.setText(R.string.unlock_user_login_tip);
		//fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), FrameLayout.LayoutParams.WRAP_CONTENT);
		//fl.gravity = Gravity.LEFT | Gravity.TOP;
		//fl.leftMargin = iconW + ShareData.PxToDpi_xhdpi(60);
		//fl.topMargin = ShareData.PxToDpi_xhdpi(120);
		//m_creditTip.setLayoutParams(fl);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(400), LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER_VERTICAL;
		m_creditTip.setLayoutParams(layoutParams);
	}

	public void Show(FrameLayout parent)
	{
		if(m_animFinish && m_fr0 != null)
		{
			m_parent = parent;
			m_uiEnabled = true;

			if(m_parent != null && m_fr0 != null)
			{
				m_parent.removeView(m_fr0);
				FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.m_screenRealWidth, ShareData.m_screenRealHeight);
				m_fr0.setLayoutParams(fl);
				m_parent.addView(m_fr0);

				m_animFinish = false;
				SetFr1State(true, true, new AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{
						// TODO Auto-generated method stub
					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						m_animFinish = true;
					}
				});
			}
		}
	}

	protected void SetFr1State(boolean isOpen, boolean hasAnimation, AnimationListener lst)
	{
		if(m_fr1 != null)
		{
//			m_fr1.clearAnimation();
			m_weixinUnlock.clearAnimation();
			m_creditUnlock.clearAnimation();
			m_bk.clearAnimation();
			m_cancelBtn.clearAnimation();

			TranslateAnimation ta = null;
			TranslateAnimation ta1 = null;
			AlphaAnimation aa = null;
			AlphaAnimation aa1 = null;
			if(isOpen)
			{
				m_weixinUnlock.setVisibility(View.VISIBLE);
				m_creditUnlock.setVisibility(View.VISIBLE);

				if(hasAnimation)
				{
					ta1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_SELF, 0);
					ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_SELF, 0);
					aa = new AlphaAnimation(0, 1);
				}
			}
			else
			{
				m_weixinUnlock.setVisibility(View.GONE);
				m_creditUnlock.setVisibility(View.GONE);
				m_cancelBtn.setVisibility(View.GONE);

				if(hasAnimation)
				{
					ta1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1);
					ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1);
					aa = new AlphaAnimation(1, 0);
					aa1 = new AlphaAnimation(1, 0);
				}
			}

			if(hasAnimation)
			{
				AnimationSet as;
				as = new AnimationSet(true);
				ta.setDuration(350);
				as.addAnimation(ta);
				as.setAnimationListener(lst);
				m_creditUnlock.startAnimation(as);

				ta1.setDuration(350);
				m_weixinUnlock.startAnimation(ta1);

				aa.setDuration(350);
				m_bk.startAnimation(aa);

				if(aa1 != null)
				{
					aa1.setDuration(200);
					m_cancelBtn.startAnimation(aa1);
				}
			}
			else
			{
				if(lst != null)
				{
					lst.onAnimationEnd(null);
				}
			}
		}
	}

	public void OnCancel(boolean hasAnim)
	{
		if(m_uiEnabled)
		{
			m_uiEnabled = false;

			SetFr1State(false, hasAnim, new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					if(m_parent != null && m_fr0 != null)
					{
						m_parent.removeView(m_fr0);
						if(m_fr1 != null)
						{
							m_fr1.clearAnimation();
						}
						if(m_weixinUnlock != null)
						{
							m_weixinUnlock.clearAnimation();
						}
						if(m_creditUnlock != null)
						{
							m_creditUnlock.clearAnimation();
						}
						if(m_bk != null)
						{
							m_bk.clearAnimation();
						}
					}

					if(m_cb != null)
					{
						m_cb.OnClose();
					}
				}
			});
		}
	}

	public void ConsumeCredit(final int themeId, Credit.Callback cb)
	{
		String params = Credit.APP_ID + Credit.THEME + themeId;
		Credit.CreditConsume(params, m_ac, 1049, cb);
	}

	protected Toast m_toast;
	public void showToast(String msg)
	{
		if(m_toast == null)
		{
			m_toast = Toast.makeText(m_ac, msg, Toast.LENGTH_SHORT);
		}
		m_toast.setText(msg);
		m_toast.show();
	}

	protected View.OnClickListener m_btnLst = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v == m_cancelBtn)
			{
				if(m_cb != null)
				{
					m_cb.OnCloseBtn();
				}
			}
			else if(v == m_weixinUnlock)
			{
				if(m_cb != null)
				{
					m_cb.OnWeiXin();
				}
			}
			else if(v == m_creditUnlock)
			{
				if(m_cb != null)
				{
					m_cb.OnCredit(m_credit);
				}
			}
		}
	};

	public void ClearAll()
	{
		SetBk(null);
	}
}

package cn.poco.prompt;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import my.beautyCamera.R;

public class PopupUI
{
	public static final int IMG_STATE_COMPLETE = 0x1;
	public static final int IMG_STATE_LOADING = 0x2;

	protected boolean m_uiEnabled;
	protected Activity m_ac;
	protected Callback m_cb;

	protected int m_fr2W;
	protected int m_fr2H;
	protected int m_imgW;
	protected int m_imgH;

	protected FrameLayout m_parent;
	protected FrameLayout m_fr0;
	protected ImageView m_bk;
	protected Bitmap m_bkBmp;
	protected FrameLayout m_fr2;
	protected boolean m_animFinish = true;

	protected FrameLayout m_imgFr;
	protected ProgressBar m_imgLoading;
	protected ImageView m_img;
	protected Bitmap m_imgBmp;
	protected int m_imgState;
	protected TextView m_cancelBtn;
	protected LinearLayout m_useBtn;
	protected ProgressBar m_loading;
	protected TextView m_useText;
	private int shapeRadius;

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

		public void OnBtn();
	}

	public PopupUI(Activity ac, Callback cb)
	{
		m_ac = ac;
		m_cb = cb;
	}

	public void CreateUI()
	{
		if(m_fr0 == null)
		{
			ShareData.InitData(m_ac);
			m_fr2W = ShareData.PxToDpi_xhdpi(570);
			m_fr2H = ShareData.PxToDpi_xhdpi(844 + 30);
			m_imgW = ShareData.PxToDpi_xhdpi(570);
			m_imgH = ShareData.PxToDpi_xhdpi(570);
			shapeRadius = ShareData.PxToDpi_xhdpi(30);

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
					m_bk.setBackgroundColor(0xEEFFFFFF);
				}
				fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
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

				m_fr2 = new FrameLayout(m_ac);
				m_fr2.setBackgroundDrawable(DrawableUtils.shapeDrawable(0xffffffff, shapeRadius));
				fl = new FrameLayout.LayoutParams(m_fr2W, m_fr2H);
				fl.gravity = Gravity.CENTER;
				m_fr2.setLayoutParams(fl);
				m_fr0.addView(m_fr2);
				{
					m_imgFr = new FrameLayout(m_ac);
					fl = new FrameLayout.LayoutParams(m_imgW, m_imgH);
					fl.gravity = Gravity.LEFT | Gravity.TOP;
					m_imgFr.setLayoutParams(fl);
					m_fr2.addView(m_imgFr);
					{
						m_imgLoading = new ProgressBar(m_ac);
						m_imgLoading.setIndeterminateDrawable(m_ac.getResources().getDrawable(R.drawable.unlock_progress));
						m_imgLoading.setVisibility(View.GONE);
						fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(46), ShareData.PxToDpi_xhdpi(46));
						fl.gravity = Gravity.CENTER;
						m_imgLoading.setLayoutParams(fl);
						m_imgFr.addView(m_imgLoading);

						m_img = new ImageView(m_ac);
						m_img.setScaleType(ScaleType.CENTER_CROP);
						fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
						fl.gravity = Gravity.CENTER;
						m_img.setLayoutParams(fl);
						m_imgFr.addView(m_img);
					}

					LinearLayout bottomLayout = new LinearLayout(m_ac);
					bottomLayout.setOrientation(LinearLayout.VERTICAL);
					bottomLayout.setBackgroundResource(R.drawable.display_bottom_bg);
					fl = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.LEFT | Gravity.TOP;
					fl.topMargin = ShareData.PxToDpi_xhdpi(530);
					m_fr2.addView(bottomLayout, fl);

					LinearLayout btnLayout = new LinearLayout(m_ac);
					btnLayout.setOrientation(LinearLayout.VERTICAL);
					fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
					fl.leftMargin = ShareData.PxToDpi_xhdpi(60);
					fl.rightMargin = ShareData.PxToDpi_xhdpi(60);
					fl.topMargin = ShareData.PxToDpi_xhdpi(24);
					fl.gravity = Gravity.BOTTOM;
					m_fr2.addView(btnLayout, fl);
					{
						LinearLayout.LayoutParams ll;

						m_useBtn = new LinearLayout(m_ac);
						Bitmap bgBmp = BitmapFactory.decodeResource(m_ac.getResources(), R.drawable.unlock_download_bg);
						m_useBtn.setBackgroundDrawable(DrawableUtils.pressedSelector(m_ac, ImageUtils.AddSkin(m_ac, bgBmp), 0.86f));
						m_useBtn.setGravity(Gravity.CENTER);
						m_useBtn.setOnClickListener(m_btnLst);
						ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(450), ShareData.getRealPixel_720P(80));
						btnLayout.addView(m_useBtn, ll);
						{
							LinearLayout confirmLayout = new LinearLayout(m_ac);
							confirmLayout.setGravity(Gravity.CENTER_VERTICAL);
							ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
							m_useBtn.addView(confirmLayout, ll);
							{
								m_loading = new ProgressBar(m_ac);
								m_loading.setIndeterminateDrawable(m_ac.getResources().getDrawable(R.drawable.unlock_progress));
								m_loading.setVisibility(View.GONE);
								ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(24), ShareData.PxToDpi_xhdpi(24));
								ll.gravity = Gravity.CENTER_VERTICAL;
								confirmLayout.addView(m_loading, ll);

								m_useText = new TextView(m_ac);
								m_useText.setText(R.string.unlock_download);
								m_useText.setClickable(true);
								m_useText.getPaint().setFakeBoldText(true);
								m_useText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
								m_useText.setTextColor(DrawableUtils.colorPressedDrawable2(0xffffffff, 0x99ffffff));
								ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_VERTICAL;
								confirmLayout.addView(m_useText, ll);
							}
						}

						m_cancelBtn = new TextView(m_ac);
						m_cancelBtn.setText(R.string.unlock_cancel);
						m_cancelBtn.getPaint().setFakeBoldText(true);
						m_cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
						m_cancelBtn.setTextColor(DrawableUtils.colorPressedDrawable2(0xffa0a0a0, 0x99a0a0a0));
						m_cancelBtn.setGravity(Gravity.CENTER);
						m_cancelBtn.setOnClickListener(m_btnLst);
						ll = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
						m_cancelBtn.setLayoutParams(ll);
						btnLayout.addView(m_cancelBtn);
					}
				}
			}
			m_fr2.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
				}
			});

			SetImgState(m_imgState);
		}
	}

	public void SetBk(Bitmap bkBmp)
	{
		if(m_bk != null)
		{
			m_bk.setBackgroundDrawable(null);
		}
		m_bkBmp = null;
		/*if(m_bkBmp != null)
		{
			m_bkBmp.recycle();
			m_bkBmp = null;
		}*/
		m_bkBmp = bkBmp;
		if(m_bk != null)
		{
			m_bk.setBackgroundDrawable(new BitmapDrawable(m_bkBmp));
		}
	}

	public void SetImg(Object res)
	{
		if(m_imgBmp != null)
		{
			m_imgBmp.recycle();
			m_imgBmp = null;
		}
		if(m_img != null)
		{
			m_img.setImageBitmap(null);
		}

		if(res != null)
		{
			Bitmap temp = cn.poco.imagecore.Utils.DecodeImage(m_ac, res, 0, -1, m_imgW, m_imgH);
			m_imgBmp = MakeBmp.CreateBitmap(temp, m_imgW, m_imgH, -1, 0, Config.ARGB_8888);
			temp.recycle();
			temp = null;
			if(m_img != null)
			{
				m_img.setImageBitmap(cn.poco.tianutils.ImageUtils.MakeRoundBmp(m_imgBmp, shapeRadius / 2));
			}
		}
	}

	public void SetContent(String content)
	{
		if(m_useText != null)
		{
			m_useText.setText(content);
		}
	}

	public void SetImgState(int state)
	{
		switch(state)
		{
			case IMG_STATE_COMPLETE:
				if(m_imgLoading != null)
				{
					m_imgLoading.setVisibility(View.GONE);
				}
				if(m_img != null)
				{
					m_img.setVisibility(View.VISIBLE);
				}

				m_imgState = state;
				break;

			case IMG_STATE_LOADING:
				if(m_imgLoading != null)
				{
					m_imgLoading.setVisibility(View.VISIBLE);
				}
				if(m_img != null)
				{
					m_img.setVisibility(View.GONE);
				}

				m_imgState = state;
				break;

			default:
				break;
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

	public void Show(FrameLayout parent)
	{
		if(m_animFinish && m_fr0 != null)
		{
			m_parent = parent;
			m_uiEnabled = true;

			if(m_parent != null && m_fr0 != null)
			{
				m_parent.removeView(m_fr0);
				FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
				m_fr0.setLayoutParams(fl);
				m_parent.addView(m_fr0);

				m_animFinish = false;
				SetFr2State(true, true, new AnimationListener()
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
						m_animFinish = true;
					}
				});
			}
		}
	}

	protected void SetFr2State(boolean isOpen, boolean hasAnimation, AnimationListener lst)
	{
		if(m_fr2 != null)
		{
			m_fr2.clearAnimation();
			m_cancelBtn.clearAnimation();
			m_bk.clearAnimation();

			TranslateAnimation ta = null;
			AlphaAnimation aa = null;
			if(isOpen)
			{
				m_fr2.setVisibility(View.VISIBLE);
				m_cancelBtn.setVisibility(View.VISIBLE);
				m_bk.setVisibility(View.VISIBLE);

				if(hasAnimation)
				{
					ta = new MyElasticAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_SELF, 0);
					aa = new AlphaAnimation(0, 1);
				}
			}
			else
			{
				m_fr2.setVisibility(View.GONE);
				m_cancelBtn.setVisibility(View.GONE);
				m_bk.setVisibility(View.GONE);

				if(hasAnimation)
				{
					ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1);
					aa = new AlphaAnimation(1, 0);
				}
			}

			if(hasAnimation)
			{
				AnimationSet as;
				as = new AnimationSet(true);
				ta.setDuration(350);
				as.addAnimation(ta);
				as.setAnimationListener(lst);
				m_fr2.startAnimation(as);

				aa.setDuration(350);
				if(isOpen)
				{
					m_cancelBtn.startAnimation(aa);
				}
				else
				{
					m_cancelBtn.startAnimation(ta);
				}
				m_bk.startAnimation(aa);
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

			SetFr2State(false, hasAnim, new AnimationListener()
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
					if(m_parent != null && m_fr0 != null)
					{
						m_parent.removeView(m_fr0);
						if(m_fr2 != null)
						{
							m_fr2.clearAnimation();
						}
						if(m_cancelBtn != null)
						{
							m_cancelBtn.clearAnimation();
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

	protected View.OnClickListener m_btnLst = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v == m_useBtn)
			{
				if(m_cb != null)
				{
					m_cb.OnBtn();
				}
			}
			else if(v == m_cancelBtn)
			{
				if(m_cb != null)
				{
					m_cb.OnCloseBtn();
				}
			}
		}
	};

	public void ClearAll()
	{
		SetBk(null);
		SetImg(null);
	}
}

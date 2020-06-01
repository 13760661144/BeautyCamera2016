package cn.poco.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.campaignCenter.utils.ClickEffectUtil;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2017/12/14.
 */

public class SimpleSharePage extends FrameLayout
{
	public enum ShareType
	{
		wechat,
		wechat_friends_circle,
		sina,
		qq,
		qzone,
		community,
		facebook,
		twitter,
		instagram
	}

	public interface SimpleSharePageClickListener
	{
		public void onClick(SimpleSharePage.ShareType type);
	}

	private LinearLayout mMainFrame;
	private TextView mTitle;
	private TextView mCancel;

	private boolean mAnimeRun = false;
	private boolean mNeedAnime = false;

	private int mBKColor = 0xfff0f0f0;
	private Bitmap mScreenshots;
	private String mTitleText;

	private SimpleSharePageClickListener mListener;

	public SimpleSharePage(@NonNull Context context)
	{
		super(context);
		SharePage.initBlogConfig(getContext());
	}

	public void needAnime()
	{
		mNeedAnime = true;
	}

	public void changeTitle(String title)
	{
		mTitleText = title;
	}

	public void setBackGColor(@ColorInt int color)
	{
		mBKColor = color;
	}

	/**
	 * 传入界面截图磨砂玻璃
	 * @param screenshots 界面截图
	 * @param height 控件高度
	 */
	public void setScreenshots(Bitmap screenshots, int height)
	{
//		if(screenshots == null || screenshots.isRecycled() || height <= 0) return;
		if(screenshots == null || screenshots.isRecycled()) return;
		height = ShareData.PxToDpi_xhdpi(378);
		mScreenshots = Bitmap.createBitmap(ShareData.m_screenWidth, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mScreenshots);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(screenshots, 0, height - screenshots.getHeight(), null);
	}

	public void init(ArrayList<SimpleSharePage.ShareType> shareList, SimpleSharePageClickListener listener)
	{
		mListener = listener;
		setBackgroundColor(0x4D000000);
		setOnClickListener(mOnClickListener);

		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		mMainFrame = new LinearLayout(getContext());
		mMainFrame.setOrientation(LinearLayout.VERTICAL);
		if(mScreenshots != null && !mScreenshots.isRecycled()) mMainFrame.setBackgroundDrawable(new BitmapDrawable(getResources(), mScreenshots));
		else mMainFrame.setBackgroundColor(mBKColor);
		mMainFrame.setOnClickListener(mOnClickListener);
		mMainFrame.setVisibility(View.GONE);
		fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
		addView(mMainFrame, fl);
		{
			mTitle = new TextView(getContext());
			mTitle.setPadding(0, ShareData.PxToDpi_xhdpi(8), 0, 0);
			if(mTitleText != null) mTitle.setText(mTitleText);
			else mTitle.setText(getContext().getString(R.string.share_to));
			mTitle.setGravity(Gravity.CENTER_VERTICAL);
			mTitle.setIncludeFontPadding(false);
			mTitle.setAlpha(0.86f);
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(106));
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			ll.leftMargin = ShareData.PxToDpi_xhdpi(42);
			mMainFrame.addView(mTitle, ll);

			if(shareList != null && shareList.size() > 0)
			{
//				LinearLayout shareFrame = new LinearLayout(getContext());
//				shareFrame.setOrientation(LinearLayout.VERTICAL);
//				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//				ll.gravity = Gravity.LEFT | Gravity.TOP;
//				mMainFrame.addView(shareFrame, ll);

				HorizontalScrollView weiboScroll = new HorizontalScrollView(getContext());
				weiboScroll.setHorizontalScrollBarEnabled(false);
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.LEFT | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(9);
				mMainFrame.addView(weiboScroll, ll);

				int button_num = 0;
				int margin = (ShareData.m_screenWidth - ShareData.PxToDpi_xxhdpi(138) * 5) / 6;
				LinearLayout buttonFrame = null;

				for(SimpleSharePage.ShareType type : shareList)
				{
					if(button_num == 0)
					{
						buttonFrame = new LinearLayout(getContext());
						buttonFrame.setOrientation(LinearLayout.HORIZONTAL);
//						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//						ll.gravity = Gravity.LEFT | Gravity.TOP;
//						ll.bottomMargin = ShareData.PxToDpi_xhdpi(38);
//						shareFrame.addView(buttonFrame, ll);
						fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						fl.gravity = Gravity.LEFT | Gravity.TOP;
						weiboScroll.addView(buttonFrame, fl);

						LinearLayout community = new LinearLayout(getContext());
						community.setOrientation(LinearLayout.VERTICAL);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						ll.gravity = Gravity.LEFT | Gravity.TOP;
						ll.leftMargin = ShareData.PxToDpi_xhdpi(37);
						buttonFrame.addView(community, ll);
						{
							ImageView icon = new ImageView(getContext());
							icon.setImageResource(R.drawable.share_weibo_circle_normal);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
							community.addView(icon, ll);

							TextView name = new TextView(getContext());
							name.setText(getResources().getString(R.string.Community));
							name.setAlpha(0.86f);
							name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
							ll.topMargin = ShareData.PxToDpi_xhdpi(10);
							community.addView(name, ll);
						}
						community.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View view)
							{
								if(mListener != null) mListener.onClick(ShareType.community);
							}
						});

						View line = new View(getContext());
						line.setBackgroundColor(0x15000000);
						ll = new LinearLayout.LayoutParams(1, ShareData.PxToDpi_xhdpi(52));
						ll.gravity = Gravity.LEFT | Gravity.TOP;
						ll.topMargin = ShareData.PxToDpi_xhdpi(26);
						ll.leftMargin = ShareData.PxToDpi_xhdpi(25);
						buttonFrame.addView(line, ll);

						margin = ShareData.PxToDpi_xhdpi(30);
					}
					else
					{
						margin = ShareData.PxToDpi_xhdpi(32);
					}
					switch(type)
					{
						case wechat:
							LinearLayout wechat = new LinearLayout(getContext());
							wechat.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(wechat, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_wechat_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								wechat.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.wechat_friends));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								wechat.addView(name, ll);
							}
							wechat.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.wechat);
								}
							});
							button_num++;
							break;

						case wechat_friends_circle:
							LinearLayout wechat_friends = new LinearLayout(getContext());
							wechat_friends.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(wechat_friends, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_wechat_friend_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								wechat_friends.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.friends_circle));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								wechat_friends.addView(name, ll);
							}
							wechat_friends.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.wechat_friends_circle);
								}
							});
							button_num++;
							break;

						case sina:
							LinearLayout sina = new LinearLayout(getContext());
							sina.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(sina, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_sina_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								sina.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.sina_weibo));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								sina.addView(name, ll);
							}
							sina.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.sina);
								}
							});
							button_num++;
							break;

						case qq:
							LinearLayout qq = new LinearLayout(getContext());
							qq.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(qq, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_qq_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								qq.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.QQ));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								qq.addView(name, ll);
							}
							qq.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.qq);
								}
							});
							button_num++;
							break;

						case qzone:
							LinearLayout qzone = new LinearLayout(getContext());
							qzone.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(qzone, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_qzone_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								qzone.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.QQZone));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								qzone.addView(name, ll);
							}
							qzone.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.qzone);
								}
							});
							button_num++;
							break;

						case facebook:
							LinearLayout facebook = new LinearLayout(getContext());
							facebook.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(facebook, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_facebook_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								facebook.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.Facebook));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								facebook.addView(name, ll);
							}
							facebook.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.facebook);
								}
							});
							button_num++;
							break;

						case twitter:
							LinearLayout twitter = new LinearLayout(getContext());
							twitter.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(twitter, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_twitter_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								twitter.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.Twitter));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								twitter.addView(name, ll);
							}
							twitter.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.twitter);
								}
							});
							button_num++;
							break;

						case instagram:
							LinearLayout instagram = new LinearLayout(getContext());
							instagram.setOrientation(LinearLayout.VERTICAL);
							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							ll.gravity = Gravity.LEFT | Gravity.TOP;
							ll.leftMargin = margin;
							buttonFrame.addView(instagram, ll);
							{
								ImageView icon = new ImageView(getContext());
								icon.setImageResource(R.drawable.share_weibo_instagarm_normal);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								instagram.addView(icon, ll);

								TextView name = new TextView(getContext());
								name.setText(getResources().getString(R.string.Instagram));
								name.setAlpha(0.86f);
								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
								instagram.addView(name, ll);
							}
							instagram.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View view)
								{
									if(mListener != null) mListener.onClick(SimpleSharePage.ShareType.instagram);
								}
							});
							button_num++;
							break;

//						case community:
//							LinearLayout community = new LinearLayout(getContext());
//							community.setOrientation(LinearLayout.VERTICAL);
//							ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//							ll.gravity = Gravity.LEFT | Gravity.TOP;
//							ll.leftMargin = margin;
//							buttonFrame.addView(community, ll);
//							{
//								ImageView icon = new ImageView(getContext());
//								icon.setImageResource(R.drawable.share_weibo_circle_normal);
//								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//								community.addView(icon, ll);
//
//								TextView name = new TextView(getContext());
//								name.setText(getResources().getString(R.string.Community));
//								name.setAlpha(0.86f);
//								name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
//								ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//								ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
//								ll.topMargin = ShareData.PxToDpi_xhdpi(10);
//								community.addView(name, ll);
//							}
//							community.setOnClickListener(new OnClickListener()
//							{
//								@Override
//								public void onClick(View view)
//								{
//									if(mListener != null) mListener.onClick(ShareType.community);
//								}
//							});
//							button_num++;
//							break;
					}
//					if(button_num == 5) button_num = 0;
				}
				View view = buttonFrame.getChildAt(buttonFrame.getChildCount() - 1);
				if(view != null) ((LinearLayout.LayoutParams)view.getLayoutParams()).rightMargin = ShareData.PxToDpi_xhdpi(30);

				shareList.clear();
			}

			mCancel = new TextView(getContext());
			mCancel.setText(getResources().getString(R.string.webviewpage_cancel));
			mCancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			mCancel.setGravity(Gravity.CENTER);
			mCancel.setBackgroundColor(0xffffffff);
			ClickEffectUtil.addTextViewClickEffect(mCancel, Color.parseColor("#333333"), Color.parseColor("#80333333"));
			ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			ll.topMargin = ShareData.PxToDpi_xhdpi(44);
			mMainFrame.addView(mCancel, ll);
			mCancel.setOnClickListener(mOnClickListener);
		}

		if(mNeedAnime)
		{
			post(new Runnable()
			{
				@Override
				public void run()
				{
					mMainFrame.setVisibility(View.VISIBLE);
					animation(true);
				}
			});
		}
	}

	private OnClickListener mOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if(mAnimeRun) return;
			if(view == mMainFrame) return;
			onBack();
		}
	};

	private void animation(final boolean start_anime)
	{
		if(mAnimeRun) return;
		mAnimeRun = true;
		float fromY;
		float toY;
		if(start_anime)
		{
			fromY = 1;
			toY = 0;
		}
		else
		{
			fromY = 0;
			toY = 1;
		}
		TranslateAnimation translate = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, fromY,
				Animation.RELATIVE_TO_SELF, toY);
		translate.setDuration(360);
		translate.setAnimationListener(new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation){}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				mAnimeRun = false;
				if(mMainFrame != null) mMainFrame.clearAnimation();
				if(!start_anime && mListener != null) mListener.onClick(null);
			}

			@Override
			public void onAnimationRepeat(Animation animation){}
		});
		if(mMainFrame != null) mMainFrame.startAnimation(translate);
	}

	public void onBack()
	{
		if(mNeedAnime)
		{
			animation(false);
		}
		else
		{
			if(mListener != null) mListener.onClick(null);
		}
	}

	public void close()
	{
		removeAllViews();
		if(mScreenshots != null && !mScreenshots.isRecycled())
		{
			mScreenshots.recycle();
			mScreenshots = null;
		}
		if(mMainFrame != null) mMainFrame.clearAnimation();
		mListener = null;
		System.gc();
	}
}

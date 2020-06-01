package cn.poco.gifEmoji;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.ctrls.SharedTipsView;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.TwitterBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.framework.SiteID;
import cn.poco.home.site.HomePageSite;
import cn.poco.lightApp06.LightApp06Page;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.setting.SettingPage;
import cn.poco.share.AdvBannerViewPager;
import cn.poco.share.ShareButton;
import cn.poco.share.ShareFrame;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.share.SinaRequestActivity;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

/**
 * Created by pocouser on 2017/6/1.
 */

public class GifEmojiSharePage extends FrameLayout
{
	private static final int TAG = R.string.表情包_分享;

	private FrameLayout m_shareFrame;
	private ImageView m_shareBackground;
	private View m_topBackground;
	private FrameLayout m_topFrame;
	private FrameLayout m_bottomFrame;
	private ImageView m_homeBtn;
	private ImageView m_backBtn;
	private HorizontalScrollView m_weiboScroll;
	private LinearLayout mIconQQ;
	private LinearLayout mIconSina;
	private LinearLayout mIconQzone;
	private LinearLayout mIconTwitter;
	private LinearLayout mIconWeiXin;
	private LinearLayout mIconCommunity;
	private LinearLayout m_weiboFrame;
	private ImageView mImageHolder;
	private LinearLayout m_buttonFrame;
	private ShareButton m_cameraBtn;
//	private ShareButton m_communityBtn;
	private AdvBannerViewPager mAdvBar;

	private boolean m_emojiSaved = true;
	private String m_savePath;
	private SinaBlog mSina;
	private QzoneBlog2 mQzone;
	private WeiXinBlog mWeiXin;
	private TwitterBlog mTwitter;
	private BackOnClickListener mListener;
	private String mResId;

	private boolean onAnimation = false;

	public interface BackOnClickListener
	{
		public void back();

		public void home();

		public void preview();

		public void camera();

		public void onCommunity();

		public void onLogin();

		public void onBindPhone();

		public void onHomeCommunity();
	}

	public GifEmojiSharePage(Context context, String savePath, String res_id, HomePageSite.CmdProc cmdProc)
	{
		super(context);
		SharePage.initBlogConfig(context);
		ShareData.InitData(context);
		init(cmdProc);
		m_savePath = savePath;
		mResId = res_id;
		MyBeautyStat.onPageStartByRes(R.string.拍照_表情包分享_主页面);
	}

	public void setBackOnClickListener(BackOnClickListener listener)
	{
		mListener = listener;
	}

	public void ClearMemory()
	{
		if(mAdvBar != null)
		{
			mAdvBar.clean();
			mAdvBar = null;
		}

		mClickListener = null;
		mListener = null;
		mBtnListener = null;

		mIconWeiXin.setOnClickListener(null);
		mIconSina.setOnClickListener(null);
		mIconQzone.setOnClickListener(null);
		mIconQQ.setOnClickListener(null);
		mIconTwitter.setOnClickListener(null);
		mIconCommunity.setOnClickListener(null);
		m_backBtn.setOnTouchListener(null);
		m_homeBtn.setOnTouchListener(null);
		m_shareBackground.clearAnimation();
		m_topFrame.clearAnimation();
		m_topBackground.clearAnimation();
		m_weiboFrame.clearAnimation();
		MyBeautyStat.onPageEndByRes(R.string.拍照_表情包分享_主页面);
	}

	/**
	 * 传入背景图
	 *
	 * @param org
	 */
	public void setBackground(Bitmap org) {
		if (org == null || org.isRecycled()) return;

		Bitmap bmp;
		if (org.getWidth() != ShareData.m_screenWidth || org.getHeight() != ShareData.m_screenHeight) {
			bmp = MakeBmp.CreateFixBitmap(org, org.getWidth()/8, org.getWidth() * 16/9/8, MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
		}
		else bmp = org.copy(Bitmap.Config.ARGB_8888, true);
		Bitmap background = BeautifyResMgr2.MakeBkBmp(bmp, ShareData.m_screenWidth/8, ShareData.m_screenHeight/8, 0x1A000000);//0x1A000000
		if(background != null && !background.isRecycled())
		{
			BitmapDrawable bd = new BitmapDrawable(getResources(), background);
			m_shareBackground.setBackgroundDrawable(bd);
		}
		mImageHolder.setImageBitmap(LightApp06Page.makeThumbPlayIcon(getContext(), org));

		post(new Runnable()
		{
			@Override
			public void run()
			{
				onAnimation = true;
				ShareFrame.UIanime(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
                    {
						setAlpha(1);
					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						if(m_shareBackground != null) m_shareBackground.clearAnimation();
						if(m_topFrame != null) m_topFrame.clearAnimation();
						if(m_topBackground != null) m_topBackground.clearAnimation();
						if(mAdvBar != null)
						{
							mAdvBar.clearAnimation();
							mAdvBar.autoPage();
						}
						onAnimation = false;
					}

					@Override
					public void onAnimationRepeat(Animation animation){}
				});
			}
		});
	}

	private void init(HomePageSite.CmdProc cmdProc)
	{
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		m_shareFrame = new FrameLayout(getContext());
		m_shareFrame.setOnClickListener(mClickListener);
		addView(m_shareFrame, fl);

		m_shareBackground = new ImageView(getContext());
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		m_shareFrame.addView(m_shareBackground, fl);

		LinearLayout mainFrame = new LinearLayout(getContext());
		mainFrame.setOrientation(LinearLayout.VERTICAL);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		m_shareFrame.addView(mainFrame, fl);

		FrameLayout topFrame = new FrameLayout(getContext());
		ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(825));
		ll.gravity = Gravity.TOP | Gravity.LEFT;
		ll.weight = 0;
		mainFrame.addView(topFrame, ll);

		FrameLayout bottomFrame = new FrameLayout(getContext());
		ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll.gravity = Gravity.TOP | Gravity.LEFT;
		ll.weight = 1;
		mainFrame.addView(bottomFrame, ll);

		m_topBackground = new View(getContext());
		//if(LightApp06PageAD79.changeAdvBackground()) m_topBackground.setBackgroundResource(R.drawable.light_app06_ad79_topframe_bg);
		//else
		m_topBackground.setBackgroundColor(Color.WHITE);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		topFrame.addView(m_topBackground, fl);

		m_topFrame = new FrameLayout(getContext());
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		topFrame.addView(m_topFrame, fl);
		{
			m_homeBtn = new ImageView(getContext());
			m_homeBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			m_homeBtn.setImageResource(R.drawable.share_top_home_normal);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.RIGHT | Gravity.TOP;
			fl.rightMargin = ShareData.PxToDpi_xhdpi(9);
			fl.topMargin = ShareData.PxToDpi_xhdpi(4);
			m_topFrame.addView(m_homeBtn, fl);
			ImageUtils.AddSkin(getContext(), m_homeBtn);
			m_homeBtn.setOnTouchListener(mBtnListener);

			m_backBtn = new ImageView(getContext());
			m_backBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			m_backBtn.setImageResource(R.drawable.framework_back_btn);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.TOP;
			fl.topMargin = ShareData.PxToDpi_xhdpi(5);
			fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
			m_topFrame.addView(m_backBtn, fl);
			ImageUtils.AddSkin(getContext(), m_backBtn);
			m_backBtn.setOnTouchListener(mBtnListener);

			TextView save_text = new TextView(getContext());
			save_text.setText(getContext().getResources().getString(R.string.share_ui_top_title));
			save_text.setTextColor(0xff333333);
			save_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = ShareData.PxToDpi_xhdpi(25);
			m_topFrame.addView(save_text, fl);

			LinearLayout imageFrame = new LinearLayout(getContext());
			imageFrame.setOrientation(LinearLayout.HORIZONTAL);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = ShareData.PxToDpi_xhdpi(197);
			m_topFrame.addView(imageFrame, fl);

			mImageHolder = new ImageView(getContext());
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.TOP | Gravity.LEFT;
			imageFrame.addView(mImageHolder, ll);
			mImageHolder.setOnClickListener(mClickListener);

			m_buttonFrame = new LinearLayout(getContext());
			m_buttonFrame.setOrientation(LinearLayout.VERTICAL);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			ll.leftMargin = ShareData.PxToDpi_xhdpi(26);
			imageFrame.addView(m_buttonFrame, ll);

//			m_communityBtn = new ShareButton(getContext());
//			m_communityBtn.init(R.drawable.share_button_community_normal, getContext().getResources().getString(R.string.share_icon_share_community), new OnAnimationClickListener()
//			{
//				@Override
//				public void onAnimationClick(View v) {
//					if(onAnimation) return;
//					shareToCommunity();
//				}
//
//				@Override
//				public void onTouch(View v){}
//
//				@Override
//				public void onRelease(View v){}
//			});
//			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
//			ll.gravity = Gravity.TOP | Gravity.LEFT;
//			m_buttonFrame.addView(m_communityBtn, ll);

			m_cameraBtn = new ShareButton(getContext());
			m_cameraBtn.init(R.drawable.share_button_camera_normal, getContext().getResources().getString(R.string.share_icon_camera), new OnAnimationClickListener()
			{
				@Override
				public void onAnimationClick(View v)
				{
					if(onAnimation) return;
					MyBeautyStat.onClickByRes(R.string.拍照_表情包分享_主页面_继续拍照);
					if(mListener != null) mListener.camera();
				}

				@Override
				public void onTouch(View v){}

				@Override
				public void onRelease(View v){}
			});
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
			ll.gravity = Gravity.TOP | Gravity.LEFT;
//			ll.topMargin = ShareData.PxToDpi_xhdpi(36);
			m_buttonFrame.addView(m_cameraBtn, ll);
		}

		m_bottomFrame = new FrameLayout(getContext());
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(228));
		fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
		m_topFrame.addView(m_bottomFrame, fl);
		{
			LinearLayout share_text = new LinearLayout(getContext());
			share_text.setOrientation(LinearLayout.HORIZONTAL);
			fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.LEFT;
			fl.leftMargin = ShareData.PxToDpi_xhdpi(34);
			m_bottomFrame.addView(share_text, fl);
			{
				TextView text = new TextView(getContext());
				text.setText(getContext().getResources().getString(R.string.share_ui_bottom_title));
				text.setTextColor(0x99000000);
				text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				share_text.addView(text, ll);

				ImageView arrow = new ImageView(getContext());
				arrow.setImageResource(R.drawable.share_text_arrow);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
				share_text.addView(arrow, ll);

				ImageView line = new ImageView(getContext());
				line.setBackgroundColor(0x19000000);
//				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(524), 1);
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(310), 1);
				ll.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(6);
				share_text.addView(line, ll);
			}

			m_weiboScroll = new HorizontalScrollView(getContext());
			m_weiboScroll.setHorizontalScrollBarEnabled(false);
			fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
			fl.bottomMargin = ShareData.PxToDpi_xhdpi(34);
			m_bottomFrame.addView(m_weiboScroll, fl);
			{
				m_weiboFrame = new LinearLayout(getContext());
				m_weiboFrame.setOrientation(LinearLayout.HORIZONTAL);
				fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.LEFT | Gravity.TOP;
				m_weiboScroll.addView(m_weiboFrame, fl);

				//在一起
				mIconCommunity = new LinearLayout(getContext());
				mIconCommunity.setOrientation(LinearLayout.VERTICAL);
				mIconCommunity.setOnClickListener(mClickListener);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(30);
				m_weiboFrame.addView(mIconCommunity, ll);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_circle_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconCommunity.addView(icon, ll);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.Community));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					ll.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconCommunity.addView(name, ll);
				}

				View line = new View(getContext());
				line.setBackgroundColor(0x15000000);
				ll = new LinearLayout.LayoutParams(1, ShareData.PxToDpi_xhdpi(52));
				ll.gravity = Gravity.LEFT | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(26);
				ll.leftMargin = ShareData.PxToDpi_xhdpi(25);
				m_weiboFrame.addView(line, ll);

				//绑定微信
				mIconWeiXin = new LinearLayout(getContext());
				mIconWeiXin.setOrientation(LinearLayout.VERTICAL);
				mIconWeiXin.setOnClickListener(mClickListener);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(30);
				m_weiboFrame.addView(mIconWeiXin, ll);
				{
					ImageView icon = new ImageView(getContext());
//						icon.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_wechat_normal, R.drawable.share_weibo_wechat_press));
					icon.setImageResource(R.drawable.share_weibo_wechat_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconWeiXin.addView(icon, ll);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.wechat_friends));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					ll.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconWeiXin.addView(name, ll);
				}

				//绑定QQ
				mIconQQ = new LinearLayout(getContext());
				mIconQQ.setOrientation(LinearLayout.VERTICAL);
				mIconQQ.setOnClickListener(mClickListener);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconQQ, ll);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_qq_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconQQ.addView(icon, ll);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.QQ));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					ll.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconQQ.addView(name, ll);
				}

				//绑定QQ空间
				mIconQzone = new LinearLayout(getContext());
				mIconQzone.setOrientation(LinearLayout.VERTICAL);
				mIconQzone.setOnClickListener(mClickListener);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconQzone, ll);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_qzone_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconQzone.addView(icon, ll);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.QQZone));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					ll.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconQzone.addView(name, ll);
				}

				//绑定Sina
				mIconSina = new LinearLayout(getContext());
				mIconSina.setOrientation(LinearLayout.VERTICAL);
				mIconSina.setOnClickListener(mClickListener);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconSina, ll);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_sina_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconSina.addView(icon, ll);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.sina_weibo));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					ll.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconSina.addView(name, ll);
				}

				//绑定Twitter
				mIconTwitter = new LinearLayout(getContext());
				mIconTwitter.setOrientation(LinearLayout.VERTICAL);
				mIconTwitter.setOnClickListener(mClickListener);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
				ll.rightMargin = ShareData.PxToDpi_xhdpi(38);
				m_weiboFrame.addView(mIconTwitter, ll);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_twitter_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconTwitter.addView(icon, ll);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.Twitter));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					ll.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconTwitter.addView(name, ll);
				}
			}
		}

		mAdvBar = new AdvBannerViewPager(getContext(), cmdProc, AdvBannerViewPager.PAGE_STICKER);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		bottomFrame.addView(mAdvBar, fl);
	}

	private View.OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if(onAnimation) return;

            if (TextUtils.isEmpty(m_savePath) || !new File(m_savePath).exists()) {
                Toast.makeText(getContext(), R.string.preview_pic_delete, Toast.LENGTH_LONG).show();
                return;
            }
			if(view == mIconWeiXin)
			{
				saveFile();
				sendToWeiXin(m_savePath);
			}
			else if(view == mIconSina)
			{
				saveFile();
				if(!SettingPage.checkSinaBindingStatus(getContext()))
				{
					if(mSina == null) mSina = new SinaBlog(getContext());
					mSina.bindSinaWithSSO(new SinaBlog.BindSinaCallback()
					{
						@Override
						public void success(final String accessToken, String expiresIn, String uid, String userName, String nickName)
						{
							//获取是绑定的时间:
							String saveTime = String.valueOf(System.currentTimeMillis() / 1000);

							SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUid(uid);
							SettingInfoMgr.GetSettingInfo(getContext()).SetSinaAccessToken(accessToken);
							SettingInfoMgr.GetSettingInfo(getContext()).SetSinaSaveTime(saveTime);
							SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUserName(userName);
							SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUserNick(nickName);
							SettingInfoMgr.GetSettingInfo(getContext()).SetSinaExpiresIn(expiresIn);

							sendToSina(m_savePath);
						}

						@Override
						public void fail()
						{
							switch(mSina.LAST_ERROR)
							{
								case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
									AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
									dlg.setTitle(getResources().getString(R.string.tips));
									dlg.setMessage(getResources().getString(R.string.share_sina_error_clinet_no_install));
									dlg.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
									dlg.show();
									break;

								default:
									SharePage.msgBox(getContext(), getResources().getString(R.string.share_sina_bind_fail));
									break;
							}
						}
					});
				}
				else
				{
					sendToSina(m_savePath);
				}
			}
			else if(view == mIconQzone)
			{
				saveFile();
				sendToQzone(m_savePath);
			}
			else if(view == mIconQQ)
			{
				saveFile();
				sendToQQ(m_savePath);
			}
			else if(view == mIconTwitter)
			{
				saveFile();
				sendToTwitter(m_savePath);
			}
			else if(view == mIconCommunity)
			{
				saveFile();
				shareToCommunity();
			}
//			else if(view == m_backBtn)
//			{
//				if(mListener != null) mListener.back();
// 			}
 			else if(view == mImageHolder)
			{
				MyBeautyStat.onClickByRes(R.string.拍照_表情包分享_主页面_查看预览图);
				if(mListener != null) mListener.preview();
			}
		}
	};

	protected OnAnimationClickListener mBtnListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(onAnimation) return;

			if (v == m_homeBtn)
			{
				MyBeautyStat.onClickByRes(R.string.拍照_表情包分享_主页面_回到首页);
				if(mListener != null) mListener.home();
			}
			else if (v == m_backBtn)
			{
				MyBeautyStat.onClickByRes(R.string.拍照_表情包分享_主页面_返回上一级);
				onAnimation = true;
				ShareFrame.UIanime2(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{
						setAlpha(1);
					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						setVisibility(View.GONE);
						if(m_shareBackground != null) m_shareBackground.clearAnimation();
						if(m_topFrame != null) m_topFrame.clearAnimation();
						if(m_topBackground != null) m_topBackground.clearAnimation();
						if(mAdvBar != null) mAdvBar.clearAnimation();
						onAnimation = false;
						if(mListener != null) mListener.back();
					}

					@Override
					public void onAnimationRepeat(Animation animation){}
				});
			}
		}

		@Override
		public void onTouch(View v){}

		@Override
		public void onRelease(View v){}
	};

	private void saveFile()
	{
		if (m_emojiSaved) return;
		String emojiPath = saveEmoji(getContext(), m_savePath);
		if (emojiPath != null && emojiPath.length() > 0)
		{
			m_savePath = null;
			m_savePath = emojiPath;
			m_emojiSaved = true;
		}
	}

	public static String saveEmoji(Context context, String cachePath) {
		if (cachePath == null || cachePath.length() <= 0 || context == null) return null;

		File file = new File(cachePath);
		if (!file.exists()) return null;

		StringBuffer sb = new StringBuffer();
		String dirName = LightApp06Page.GetPhotoSavePath();
		File destDir = new File(dirName);
		if (!destDir.exists()) destDir.mkdirs();
		sb.append(dirName);
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		String strDate = df.format(date);
		String strRand = Integer.toString((int) (Math.random() * 100));
		if (strRand.length() < 4) strRand = "0000".substring(strRand.length()) + strRand;
		sb.append(File.separator + strDate + strRand + ".gif");
		try {
			File copyFile = new File(sb.toString());
			FileUtils.copyFile(file, copyFile);
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + copyFile)));
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendToSina(String pic) {
		if (pic == null || pic.length() <= 0 || !new File(pic).exists())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
			return;
		}

		if (mSina == null) {
			mSina = new SinaBlog(getContext());
		}
		if (!mSina.checkSinaClientInstall()) {
			AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
			dlg.setTitle(getResources().getString(R.string.tips));
			dlg.setMessage(getResources().getString(R.string.share_sina_error_clinet_no_install));
			dlg.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ensure), (DialogInterface.OnClickListener) null);
			dlg.show();
			return;
		}
		mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(getContext()).GetSinaAccessToken());
		mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse() {
			@Override
			public void response(boolean send_success, int response_code) {
				if (send_success) {
					switch (response_code) {
						case WBConstants.ErrorCode.ERR_OK:
							TongJi2.AddOnlineClickCount(getContext(), mResId, R.integer.拍照_动态贴纸_预览_分享_gif_分享到新浪, getResources().getString(TAG));
							ShareTools.addIntegral(getContext());
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG);
							MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_表情包分享_主页面);
							break;

						case WBConstants.ErrorCode.ERR_CANCEL:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
							break;

						case WBConstants.ErrorCode.ERR_FAIL:
						case SinaBlog.NO_RESPONSE:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
							break;
					}
				} else {
					SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
				}
			}
		});

		Intent intent = new Intent(getContext(), SinaRequestActivity.class);
		intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT_AND_PIC);
		intent.putExtra("pic", pic);
		intent.putExtra("content", ShareFrame.SHARE_DEFAULT_TEXT);
		((Activity) getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
	}

	private void sendToQzone(String pic) {
		if (mQzone == null) {
			mQzone = new QzoneBlog2(getContext());
		}
		if (!mQzone.checkQQClientInstall()) {
			AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
			dlg.setTitle(getContext().getResources().getString(R.string.tips));
			dlg.setMessage(getContext().getResources().getString(R.string.share_qq_error_clinet_no_install));
			dlg.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getResources().getString(R.string.ensure), (DialogInterface.OnClickListener) null);
			dlg.show();
			return;
		}
		mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
			@Override
			public void sendComplete(int result) {
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
					TongJi2.AddOnlineClickCount(getContext(), mResId, R.integer.拍照_动态贴纸_预览_分享_gif_分享到QQ空间, getResources().getString(TAG));
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_表情包分享_主页面);
				}
				else if (result == QzoneBlog2.SEND_CANCEL) {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});
		if(!mQzone.sendToPublicQzone(1, pic)) SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
	}

	private void sendToQQ(String pic) {
		if (mQzone == null) {
			mQzone = new QzoneBlog2(getContext());
		}
		if (!mQzone.checkQQClientInstall()) {
			AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
			dlg.setTitle(getContext().getResources().getString(R.string.tips));
			dlg.setMessage(getContext().getResources().getString(R.string.share_qq_error_clinet_no_install));
			dlg.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getResources().getString(R.string.ensure), (DialogInterface.OnClickListener) null);
			dlg.show();
			return;
		}
		mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
			@Override
			public void sendComplete(int result) {
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
					TongJi2.AddOnlineClickCount(getContext(), mResId, R.integer.拍照_动态贴纸_预览_分享_gif_分享到QQ好友, getResources().getString(TAG));
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_表情包分享_主页面);
				}
				else if (result == QzoneBlog2.SEND_CANCEL) {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});
		if(!mQzone.sendToQQ(pic)) SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
	}

	private void sendToWeiXin(String pic) {
		if (pic == null || pic.length() <= 0 || !new File(pic).exists())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
			return;
		}

		if (mWeiXin == null) {
			mWeiXin = new WeiXinBlog(getContext());
		}
		Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
		if (mWeiXin.sendEmojiToWeiXin(pic, thumb)) {
			SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener() {
				@Override
				public void onCallFinish(int result) {
					switch (result) {
						case BaseResp.ErrCode.ERR_OK:
							TongJi2.AddOnlineClickCount(getContext(), mResId, R.integer.拍照_动态贴纸_预览_分享_gif_分享到微信好友, getResources().getString(TAG));
							ShareTools.addIntegral(getContext());
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
							MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_表情包分享_主页面);
							break;
						case BaseResp.ErrCode.ERR_USER_CANCEL:
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
							break;
						case BaseResp.ErrCode.ERR_AUTH_DENIED:
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
							break;
						default:
							break;
					}
					SendWXAPI.removeListener(this);
				}
			};
			SendWXAPI.addListener(listener);
		} else {
			SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, true);
		}
	}

	private void sendToTwitter(String pic)
	{
		if (pic == null || pic.length() <= 0 || !new File(pic).exists())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
			return;
		}

		if(mTwitter == null) mTwitter = new TwitterBlog(getContext());
		if (!mTwitter.sendToTwitter(pic, null)) {
			String message = null;
			switch (mTwitter.LAST_ERROR) {
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_twitter_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_image_is_null);
					break;

				case WeiboInfo.BLOG_INFO_OTHER_ERROR:
					message = getContext().getResources().getString(R.string.share_error_start_application_fail);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
		}
		else
		{
			TongJi2.AddOnlineClickCount(getContext(), mResId, R.integer.拍照_动态贴纸_预览_分享_gif_分享到推特, getResources().getString(TAG));
			MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_表情包分享_主页面);
		}
	}

	private void shareToCommunity()
	{
		if(!UserMgr.IsLogin(getContext(), null)){
			//未登录去登录
			if(mListener != null) mListener.onLogin();
			return;
		}
		UserInfo userInfo = UserMgr.ReadCache(getContext());

		if(userInfo !=null && TextUtils.isEmpty(userInfo.mMobile)){
			//未完善资料去完善资料
			if(mListener != null) mListener.onBindPhone();
			return;
		}
		if(mListener != null) mListener.onCommunity();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mSina != null) mSina.onActivityResult(requestCode, resultCode, data, -1);
		if (mQzone != null) mQzone.onActivityResult(requestCode, resultCode, data);
	}

	public void onBack()
	{
		if(onAnimation) return;
		onAnimation = true;
		ShareFrame.UIanime2(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation)
			{
				setAlpha(1);
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				setVisibility(View.GONE);
				if(m_shareBackground != null) m_shareBackground.clearAnimation();
				if(m_topFrame != null) m_topFrame.clearAnimation();
				if(m_topBackground != null) m_topBackground.clearAnimation();
				if(mAdvBar != null) mAdvBar.clearAnimation();
				onAnimation = false;
				if(mListener != null) mListener.back();
			}

			@Override
			public void onAnimationRepeat(Animation animation){}
		});
	}

	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		switch(siteID)
		{
			case SiteID.PUBLISH_OPUS_PAGE:
				if(params != null && params.containsKey("isSuccess"))
				{
					boolean b = (boolean)params.get("isSuccess");
					if(b) showSuccessDialog();
				}
				break;

			case SiteID.LOGIN:
			case SiteID.REGISTER_DETAIL:
			case SiteID.RESETPSW:
				if(!UserMgr.IsLogin(getContext(), null)) return;
				UserInfo userInfo = UserMgr.ReadCache(getContext());
				if(userInfo == null) return;
				if(TextUtils.isEmpty(userInfo.mMobile)) return ;
				shareToCommunity();
				break;
		}
	}

	private void showSuccessDialog()
	{
		SharedTipsView view=new SharedTipsView(getContext());

		final Dialog dialog = new Dialog(getContext(), R.style.fullDialog1);
		view.setJump2AppClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(mListener != null) mListener.onHomeCommunity();
				dialog.dismiss();
			}
		});
		view.setStayClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
		dialog.setContentView(view);
	}
}

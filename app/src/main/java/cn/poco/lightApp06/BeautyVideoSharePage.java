package cn.poco.lightApp06;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.AbsUploadFile;
import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsChannelAdRes;
import com.adnonstop.admasterlibs.data.UploadData;
import com.circle.ctrls.SharedTipsView;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.poco.adMaster.HomeAd;
import cn.poco.adMaster.UploadFile;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.blogcore.FacebookBlog;
import cn.poco.blogcore.InstagramBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.TwitterBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.business.ChannelValue;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.SiteID;
import cn.poco.holder.ObjHandlerHolder;
import cn.poco.home.site.HomePageSite;
import cn.poco.lightApp06.site.BeautyVideoPageSite;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingPage;
import cn.poco.share.AdvBannerViewPager;
import cn.poco.share.DialogView2;
import cn.poco.share.ShareButton;
import cn.poco.share.ShareFrame;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.share.SinaRequestActivity;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

import static cn.poco.setting.SettingInfoMgr.GetSettingInfo;

/**
 * Created by pocouser on 2017/11/14.
 */

public class BeautyVideoSharePage extends FrameLayout
{
	private BeautyVideoPageSite mPageSite;

	private FrameLayout m_progressFrame;
	private FrameLayout m_shareFrame;
	private ImageView m_shareBackground;
	private View m_topBackground;
	private FrameLayout m_topFrame;
	private FrameLayout m_bottomFrame;
	private HorizontalScrollView m_weiboScroll;
	private ImageView m_homeBtn;
	private ImageView m_backBtn;
	private LinearLayout mIconCommunity;
	private LinearLayout mIconQQ;
	private LinearLayout mIconSina;
	private LinearLayout mIconQzone;
	private LinearLayout mIconFaceBook;
	private LinearLayout mIconTwitter;
	private LinearLayout mIconInstagram;
	private LinearLayout mIconWeiXin;
	private LinearLayout mIconWXFriends;
	private LinearLayout m_weiboFrame;
	private LinearLayout m_imageFrame;
	private ImageView mImageHolder;
	private LinearLayout m_buttonFrame;
	private ShareButton m_cameraBtn;
	private ShareButton m_beautifyBtn;
	private RoundProgressBar m_progress;
	private AdvBannerViewPager mAdvBar;

	private Bitmap m_ShareBmp;

	private boolean m_onClose = false;
	private boolean m_videoSaving = false;    //视频是否保存中
	private String m_savePath;
	private boolean m_videoSaved = false;
	private long mVideoDuration;
	private String m_shareUrl;
	private String m_picPath;
	private SinaBlog mSina;
	private QzoneBlog2 mQzone;
	private WeiXinBlog mWeiXin;
	private FacebookBlog mFacebook;
	private TwitterBlog mTwitter;
	private InstagramBlog mInstagram;
	//    private ICIRCLEAPI mCircleApi;
	private ProgressDialog mProgressDialog;
	private String m_shareBrowse;
	private Bitmap m_background;
	private boolean m_uploadComplete = false;
	private Bitmap m_shareDialogBG;
	private boolean m_isShareVideoDialogShow = false;
	private boolean onAnimation = false;

	private ObjHandlerHolder<AbsUploadFile.Callback> m_uploadCallback;
	private AnimationCallback m_animationCallback;

	private String mShareTitle = getResources().getString(R.string.lightapp06_share_title);
	private String mShareText = getResources().getString(R.string.lightapp06_share_text);
	private String mShareDescription = getResources().getString(R.string.lightapp06_share_description);
	private String mShareFriendsTitle = getResources().getString(R.string.lightapp06_share_title);

	private boolean isVideoSharePage;

	//商业
	private String mChannelValue;
	protected AbsChannelAdRes mActChannelAdRes;
	protected AbsChannelAdRes.SharePageData mActConfigure;
	private JSONObject mAdvPostStr;
	//阿玛尼商业
	private boolean mArmani = false;
	//YSL商业
	private boolean mYSL = false;

	public interface UploadVideoCallback
	{
		public void uploadComplete(String url);

		public void uploadFail();
	}

	public interface ShareVideoDialogCallback
	{
		public void shareVideoType(boolean uploadServer);
	}

	public interface AnimationCallback
	{
		/**
		 * 弹框分享页动画完成回调
		 *
		 * @param openShare true：打开弹框动画，false：关闭弹框动画
		 */
		void onAnimationEnd(boolean openShare);

		/**
		 * 弹框分享页动画开始回调
		 *
		 * @param openShare true：打开弹框动画，false：关闭弹框动画
		 */
		void onAnimationStart(boolean openShare);
	}

	private int resId;
	private String resTjId;

	public BeautyVideoSharePage(Context context, BaseSite site, boolean isVideoSharePage)
	{
		super(context);
		mPageSite = (BeautyVideoPageSite) site;
		this.isVideoSharePage = isVideoSharePage;

		SharePage.initBlogConfig(context);
		ShareData.InitData(context);

		init();
	}

	public void setAnimationCallback(AnimationCallback callback)
	{
		m_animationCallback = callback;
	}

	/**
	 * type : 0 图片 1：视频
	 *
	 * @param params
	 */
	public void SetData(HashMap<String, Object> params)
	{
		int type = -1;
		String path = null;

		if(params != null)
		{
			if(params.containsKey("type"))
			{
				type = (Integer)params.get("type");
			}
			if(params.containsKey("path"))
			{
				path = (String)params.get("path");
			}
			if (params.containsKey("video_duration"))
			{
				mVideoDuration = (Long) params.get("video_duration");
			}
			if(params.containsKey("channelValue"))
			{
				mChannelValue = (String)params.get("channelValue");
				if(mChannelValue != null && mChannelValue.length() > 0)
				{
					AbsAdRes adRes = HomeAd.GetOneHomeRes(getContext(), mChannelValue);
					if(adRes != null && adRes instanceof AbsChannelAdRes)
					{
						mActChannelAdRes = (AbsChannelAdRes)adRes;
						mActConfigure = (AbsChannelAdRes.SharePageData)((AbsChannelAdRes)adRes).GetPageData(AbsChannelAdRes.SharePageData.class);
						if(mActConfigure != null && System.currentTimeMillis() < mActChannelAdRes.mEndTime)
						{
							Utils.UrlTrigger(getContext(), mActConfigure.mSendTj);
							mAdvPostStr = HomePageSite.makePostVar(getContext(), mActChannelAdRes.mAdId);
							mShareText = mActConfigure.mDefaultContent;
							mShareTitle = mActConfigure.mWeixinFriendTitle;
							mShareDescription = mActConfigure.mWeixinFriendContent;
							mShareFriendsTitle = mActConfigure.mWeixinCircleTitle;
							if(mChannelValue.equals(ChannelValue.AD84)) mArmani = true;
						}
						else mChannelValue = null;
					}
					else mChannelValue = null;
				}
				else mChannelValue = null;
			}
			if(params.containsKey("res_id"))
			{
				resId = (Integer)params.get("res_id");
				if(resId == 39167)
				{
					mShareText = "#NOTINNOCENT  LOOK，YSL夹心唇膏全新上市，肆意展现叛逆本能@YSL圣罗兰美妆";
					mShareTitle = "YSL夹心唇膏#NOTINNOCENT LOOK";
					mShareDescription = "YSL夹心唇膏全新上市，肆意展现叛逆本能";
					mShareFriendsTitle = "YSL夹心唇膏#NOTINNOCENT LOOK";
					mYSL = true;
				}
			}
			if(params.containsKey("res_tj_id"))
			{
				resTjId = (String)params.get("res_tj_id");
			}
		}

		isVideoSharePage = type == 1;
		if(type == 0)
		{
			if(path != null)
			{
				m_savePath = path;
				setPicture(path);
			}
		}
		else if(type == 1)
		{
			if(path != null)
			{
				m_savePath = path;
				setVideo(m_savePath);
			}
		}
	}

	private void init()
	{
		LinearLayout.LayoutParams lParams;
		FrameLayout.LayoutParams rParams;

		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.gravity = Gravity.LEFT | Gravity.TOP;
		m_progressFrame = new FrameLayout(getContext());
		addView(m_progressFrame, rParams);
		m_progressFrame.setVisibility(View.GONE);

		m_shareFrame = new FrameLayout(getContext());
		m_shareFrame.setOnClickListener(mClickListener);
		m_shareFrame.setVisibility(View.GONE);
		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		addView(m_shareFrame, rParams);

		m_progress = new RoundProgressBar(getContext());
		m_progress.setMax(100);
		m_progress.setProgress(0);
		rParams = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(200), ShareData.PxToDpi_xhdpi(200));
		rParams.gravity = Gravity.CENTER;
		rParams.bottomMargin = ShareData.PxToDpi_xhdpi(78);
		m_progressFrame.addView(m_progress, rParams);

		m_shareBackground = new ImageView(getContext());
		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.gravity = Gravity.TOP | Gravity.LEFT;
		m_shareFrame.addView(m_shareBackground, rParams);

		LinearLayout mainFrame = new LinearLayout(getContext());
		mainFrame.setOrientation(LinearLayout.VERTICAL);
		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.gravity = Gravity.TOP | Gravity.LEFT;
		m_shareFrame.addView(mainFrame, rParams);

		FrameLayout topFrame = new FrameLayout(getContext());
		lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(825));
		lParams.gravity = Gravity.TOP | Gravity.LEFT;
		lParams.weight = 0;
		mainFrame.addView(topFrame, lParams);

		FrameLayout bottomFrame = new FrameLayout(getContext());
		lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lParams.gravity = Gravity.TOP | Gravity.LEFT;
		lParams.weight = 1;
		mainFrame.addView(bottomFrame, lParams);

		m_topBackground = new View(getContext());
		m_topBackground.setBackgroundColor(Color.WHITE);
		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.gravity = Gravity.TOP | Gravity.LEFT;
		topFrame.addView(m_topBackground, rParams);

		m_topFrame = new FrameLayout(getContext());
		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParams.gravity = Gravity.TOP | Gravity.LEFT;
		topFrame.addView(m_topFrame, rParams);
		{
			m_homeBtn = new ImageView(getContext());
			m_homeBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			m_homeBtn.setImageResource(R.drawable.share_top_home_normal);
			rParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			rParams.gravity = Gravity.RIGHT | Gravity.TOP;
			rParams.rightMargin = ShareData.PxToDpi_xhdpi(9);
			rParams.topMargin = ShareData.PxToDpi_xhdpi(4);
			m_topFrame.addView(m_homeBtn, rParams);
			ImageUtils.AddSkin(getContext(), m_homeBtn);
			m_homeBtn.setOnTouchListener(mBtnListener);

			m_backBtn = new ImageView(getContext());
			m_backBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			m_backBtn.setImageResource(R.drawable.framework_back_btn);
			rParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			rParams.gravity = Gravity.LEFT | Gravity.TOP;
			rParams.topMargin = ShareData.PxToDpi_xhdpi(5);
			rParams.leftMargin = ShareData.PxToDpi_xhdpi(2);
			m_topFrame.addView(m_backBtn, rParams);
			ImageUtils.AddSkin(getContext(), m_backBtn);
			m_backBtn.setOnTouchListener(mBtnListener);

			TextView save_text = new TextView(getContext());
			save_text.setText(getContext().getResources().getString(R.string.share_ui_top_title));
			save_text.setTextColor(0xff333333);
			save_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			rParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			rParams.topMargin = ShareData.PxToDpi_xhdpi(25);
			m_topFrame.addView(save_text, rParams);

			m_imageFrame = new LinearLayout(getContext());
			m_imageFrame.setOrientation(LinearLayout.HORIZONTAL);
			rParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			rParams.topMargin = ShareData.PxToDpi_xhdpi(197);
			m_topFrame.addView(m_imageFrame, rParams);

			mImageHolder = new ImageView(getContext());
			lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lParams.gravity = Gravity.TOP | Gravity.LEFT;
			mImageHolder.setMinimumWidth(ShareData.PxToDpi_xhdpi(150));
			mImageHolder.setMinimumHeight(ShareData.PxToDpi_xhdpi(150));
			m_imageFrame.addView(mImageHolder, lParams);
			mImageHolder.setOnClickListener(mClickListener);
		}

		m_bottomFrame = new FrameLayout(getContext());
		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(228));
		rParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
		m_topFrame.addView(m_bottomFrame, rParams);
		{
			LinearLayout share_text = new LinearLayout(getContext());
			share_text.setOrientation(LinearLayout.HORIZONTAL);
			rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			rParams.gravity = Gravity.TOP | Gravity.LEFT;
			rParams.leftMargin = ShareData.PxToDpi_xhdpi(34);
			m_bottomFrame.addView(share_text, rParams);
			{
				TextView text = new TextView(getContext());
				text.setText(getContext().getResources().getString(R.string.share_ui_bottom_title));
				text.setTextColor(0x99000000);
				text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				share_text.addView(text, lParams);

				ImageView arrow = new ImageView(getContext());
				arrow.setImageResource(R.drawable.share_text_arrow);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(10);
				share_text.addView(arrow, lParams);

				ImageView line = new ImageView(getContext());
				line.setBackgroundColor(0x19000000);
//                lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(524), 1);
				lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(310), 1);
				lParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(6);
				share_text.addView(line, lParams);
			}

			m_weiboScroll = new HorizontalScrollView(getContext());
			m_weiboScroll.setHorizontalScrollBarEnabled(false);
			rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			rParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
			rParams.bottomMargin = ShareData.PxToDpi_xhdpi(34);
			m_bottomFrame.addView(m_weiboScroll, rParams);
			{
				m_weiboFrame = new LinearLayout(getContext());
				m_weiboFrame.setOrientation(LinearLayout.HORIZONTAL);
				rParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				rParams.gravity = Gravity.LEFT | Gravity.TOP;
				m_weiboScroll.addView(m_weiboFrame, rParams);

				//在一起
				mIconCommunity = new LinearLayout(getContext());
				mIconCommunity.setOrientation(LinearLayout.VERTICAL);
				mIconCommunity.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
				m_weiboFrame.addView(mIconCommunity, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_circle_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconCommunity.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.Community));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconCommunity.addView(name, lParams);
				}

				View line = new View(getContext());
				line.setBackgroundColor(0x15000000);
				lParams = new LinearLayout.LayoutParams(1, ShareData.PxToDpi_xhdpi(52));
				lParams.gravity = Gravity.LEFT | Gravity.TOP;
				lParams.topMargin = ShareData.PxToDpi_xhdpi(26);
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(25);
				m_weiboFrame.addView(line, lParams);

				//绑定微信
				mIconWeiXin = new LinearLayout(getContext());
				mIconWeiXin.setOrientation(LinearLayout.VERTICAL);
				mIconWeiXin.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
				m_weiboFrame.addView(mIconWeiXin, lParams);
				{
					ImageView icon = new ImageView(getContext());
//						icon.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_wechat_normal, R.drawable.share_weibo_wechat_press));
					icon.setImageResource(R.drawable.share_weibo_wechat_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconWeiXin.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.wechat_friends));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconWeiXin.addView(name, lParams);
				}

				//绑定微信朋友圈
				mIconWXFriends = new LinearLayout(getContext());
				mIconWXFriends.setOrientation(LinearLayout.VERTICAL);
				mIconWXFriends.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconWXFriends, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_wechat_friend_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconWXFriends.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.friends_circle));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconWXFriends.addView(name, lParams);
				}

				//绑定QQ
				mIconQQ = new LinearLayout(getContext());
				mIconQQ.setOrientation(LinearLayout.VERTICAL);
				mIconQQ.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconQQ, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_qq_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconQQ.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.QQ));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconQQ.addView(name, lParams);
				}

				//绑定QQ空间
				mIconQzone = new LinearLayout(getContext());
				mIconQzone.setOrientation(LinearLayout.VERTICAL);
				mIconQzone.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconQzone, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_qzone_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconQzone.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.QQZone));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconQzone.addView(name, lParams);
				}

				//绑定Sina
				mIconSina = new LinearLayout(getContext());
				mIconSina.setOrientation(LinearLayout.VERTICAL);
				mIconSina.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconSina, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_sina_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconSina.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.sina_weibo));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconSina.addView(name, lParams);
				}

				//绑定FaceBook
				mIconFaceBook = new LinearLayout(getContext());
				mIconFaceBook.setOrientation(LinearLayout.VERTICAL);
				mIconFaceBook.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconFaceBook, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_facebook_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconFaceBook.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.Facebook));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconFaceBook.addView(name, lParams);
				}

				//绑定Instagram
				mIconInstagram = new LinearLayout(getContext());
				mIconInstagram.setOrientation(LinearLayout.VERTICAL);
				mIconInstagram.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
				m_weiboFrame.addView(mIconInstagram, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_instagarm_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconInstagram.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.Instagram));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconInstagram.addView(name, lParams);
				}

				//绑定Twitter
				mIconTwitter = new LinearLayout(getContext());
				mIconTwitter.setOrientation(LinearLayout.VERTICAL);
				mIconTwitter.setOnClickListener(mClickListener);
				lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lParams.gravity = Gravity.TOP | Gravity.LEFT;
				lParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
				lParams.rightMargin = ShareData.PxToDpi_xhdpi(38);
				m_weiboFrame.addView(mIconTwitter, lParams);
				{
					ImageView icon = new ImageView(getContext());
					icon.setImageResource(R.drawable.share_weibo_twitter_normal);
					icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mIconTwitter.addView(icon, lParams);

					TextView name = new TextView(getContext());
					name.setText(getResources().getString(R.string.Twitter));
					name.setAlpha(0.8f);
					name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
					lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					lParams.topMargin = ShareData.PxToDpi_xhdpi(10);
					mIconTwitter.addView(name, lParams);
				}
			}
		}

		if(mArmani)
			mAdvBar = new AdvBannerViewPager(getContext(), mPageSite.m_cmdProc, R.drawable.share_advertising_banner_ad84, "http://cav.adnonstop.com/cav/f53576787a/0068903162/?url=http://www.giorgioarmanibeauty.cn/landing-pages/170815cushion.html");
		else
			mAdvBar = new AdvBannerViewPager(getContext(), mPageSite.m_cmdProc, AdvBannerViewPager.PAGE_STICKER);
		rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rParams.gravity = Gravity.CENTER;
		bottomFrame.addView(mAdvBar, rParams);
	}

	private void addButton()
	{
		if(mArmani)
		{
			((LinearLayout.LayoutParams)mImageHolder.getLayoutParams()).gravity = Gravity.CENTER;
			return;
		}

		LinearLayout.LayoutParams ll;

		if (m_buttonFrame == null)
		{
			m_buttonFrame = new LinearLayout(getContext());
			m_buttonFrame.setOrientation(LinearLayout.VERTICAL);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			ll.leftMargin = ShareData.PxToDpi_xhdpi(26);
			m_imageFrame.addView(m_buttonFrame, ll);
		}

		if(!isVideoSharePage)
		{
			if (m_cameraBtn == null)
			{
				m_cameraBtn = new ShareButton(getContext());
				m_cameraBtn.init(R.drawable.share_button_camera_normal, getContext().getResources().getString(R.string.share_icon_camera), new OnAnimationClickListener()
				{
					@Override
					public void onAnimationClick(View v)
					{
						if (m_videoSaving || onAnimation || m_onClose) return;
						//MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_继续拍照);
						TongJi2.AddCountByRes(getContext(), R.integer.拍照_萌装照_预览_继续拍摄);
						if (mPageSite != null) mPageSite.OnCamera(getContext());
					}
				});
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				m_buttonFrame.addView(m_cameraBtn, ll);
			}

			if (m_beautifyBtn == null)
			{
				m_beautifyBtn = new ShareButton(getContext());
				m_beautifyBtn.init(R.drawable.share_button_beautify_normal, getContext().getResources().getString(R.string.share_icon_beautify), new OnAnimationClickListener()
				{
					@Override
					public void onAnimationClick(View v)
					{
						if (m_videoSaving || onAnimation || m_onClose) return;
						//MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_美颜美图);
						TongJi2.AddCountByRes(getContext(), R.integer.拍照_萌装照_预览_美颜美图);
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("img", new RotationImg2[]{Utils.Path2ImgObj(m_savePath)});
						if (mPageSite != null) mPageSite.OnBeautyFace(getContext(), params);
					}
				});
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.topMargin = ShareData.PxToDpi_xhdpi(36);
				m_buttonFrame.addView(m_beautifyBtn, ll);
			}
		}
		else
		{
			if (m_cameraBtn == null)
			{
				m_cameraBtn = new ShareButton(getContext());
				m_cameraBtn.init(R.drawable.share_button_camera_normal, getContext().getResources().getString(R.string.share_icon_camera), new OnAnimationClickListener()
				{
					@Override
					public void onAnimationClick(View v)
					{
						if (m_videoSaving || onAnimation || m_onClose) return;
						MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_继续拍照);
						if (mPageSite != null) mPageSite.OnCamera(getContext());
					}
				});
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				m_buttonFrame.addView(m_cameraBtn, ll);
			}
		}
	}

	/**
	 * 传入视频icon背景图
	 *
	 * @param bmp
	 */
	public void setVideoBackground(Bitmap bmp)
	{
		isVideoSharePage = true;

		if(bmp == null || bmp.isRecycled()) return;

		m_picPath = FileCacheMgr.GetLinePath() + ".img";
		CommonUtils.MakeParentFolder(m_picPath);
		Utils.SaveImg(getContext(), bmp, m_picPath, 100, false);//TempImageFile

		if(bmp.getWidth() != ShareData.m_screenWidth || bmp.getHeight() != ShareData.m_screenHeight)
		{
			bmp = MakeBmp.CreateBitmap(bmp, ShareData.m_screenWidth, ShareData.m_screenHeight, -1, 0, Bitmap.Config.ARGB_8888);
		}
		m_background = BeautifyResMgr2.MakeBkBmp(bmp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x24000000);
		BitmapDrawable bd = new BitmapDrawable(getResources(), m_background);
		m_shareBackground.setBackgroundDrawable(bd);
		m_progressFrame.setBackgroundDrawable(bd);

		mImageHolder.setImageBitmap(makeThumbPlayIcon(getContext(), bmp));
		addButton();

		Bitmap browse = MakeBmp.CreateFixBitmap(bmp, 150, 150, MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
		if(browse == null || browse.isRecycled()) return;
		int w = browse.getWidth();
		int icon_w = (int)((float)w * 11f / 27f);
		Canvas canvas = new Canvas(browse);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Bitmap icon = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), R.drawable.light_app06_share_videoplay_icon, 0, -1, icon_w, icon_w), icon_w, icon_w, -1, 0, Bitmap.Config.ARGB_8888);
		canvas.drawBitmap(icon, (w - icon_w) / 2, (w - icon_w) / 2, null);

		m_shareBrowse = FileCacheMgr.GetLinePath() + ".img";
		CommonUtils.MakeParentFolder(m_shareBrowse);
		Utils.SaveImg(getContext(), bmp, m_shareBrowse, 100, false);

		icon.recycle();
		browse.recycle();
		System.gc();
	}

	private void setPicture(String path)
	{
		MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照分享页_主页面);
		isVideoSharePage = false;
		m_progressFrame.setVisibility(View.GONE);

		Bitmap bitmap = cn.poco.imagecore.Utils.DecodeImage(getContext(), path, 0, -1, -1, -1);
		if(bitmap != null && !bitmap.isRecycled())
		{
			m_ShareBmp = bitmap;

			Bitmap tempBmp = MakeBmp.CreateBitmap(bitmap, ShareData.m_screenWidth, ShareData.m_screenHeight, -1, 0, Bitmap.Config.ARGB_8888);
			BitmapDrawable bd = new BitmapDrawable(getResources(), BeautifyResMgr2.MakeBkBmp(tempBmp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x24000000));
			m_shareBackground.setBackgroundDrawable(bd);

			Bitmap thumb = MakeBmp.CreateFixBitmap(bitmap, ShareData.PxToDpi_xhdpi(320), ShareData.PxToDpi_xhdpi(320), MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
			mImageHolder.setImageBitmap(ShareFrame.makeCircle(thumb));

			addButton();
			openAnimation();
		}
		else
		{
			Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_not_exist_pic), Toast.LENGTH_SHORT).show();
		}
	}

	private void setVideo(String path)
	{
		m_savePath  = path;
		MyBeautyStat.onPageStartByRes(R.string.拍照_视频分享页_主页面);
		openAnimation();
	}

	public static Bitmap makeThumbPlayIcon(Context context, Bitmap bmp)
	{
		if(context == null || bmp == null || bmp.isRecycled()) return null;
		Bitmap thumb = MakeBmp.CreateFixBitmap(bmp, ShareData.PxToDpi_xhdpi(320), ShareData.PxToDpi_xhdpi(320), MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
		if(thumb == null || thumb.isRecycled()) return null;
		thumb = ShareFrame.makeCircle(thumb);
		Bitmap play_icon = MakeBmp.CreateFixBitmap(cn.poco.imagecore.Utils.DecodeImage(context, R.drawable.light_app06_thumb_play_icon, 0, -1, ShareData.PxToDpi_xhdpi(92), ShareData.PxToDpi_xhdpi(92)), ShareData.PxToDpi_xhdpi(92), ShareData.PxToDpi_xhdpi(92), MakeBmp.POS_START, 0, Bitmap.Config.ARGB_8888);
		if(play_icon == null || play_icon.isRecycled()) return thumb;
		Canvas canvas = new Canvas(thumb);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(play_icon, (thumb.getWidth() - play_icon.getWidth()) / 2, (thumb.getHeight() - play_icon.getHeight()) / 2, null);
		return thumb;
	}

	/**
	 * 传入视频保存进度
	 *
	 * @param progress 当前进度，范围0~100
	 */
	public void setVideoSaveProgress(int progress)
	{
		if(m_progressFrame.getVisibility() != View.VISIBLE)
		{
			return;
		}
		m_progress.setProgress(progress);
	}

	private View.OnClickListener mClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			if(m_videoSaving || onAnimation || m_onClose) return;
			if(view == mIconWXFriends)
			{
				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendPicToWeiXin(m_savePath, false);
					return;
				}

				if(m_savePath == null || !new File(m_savePath).exists())
				{
					Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
					return;
				}

				if(mChannelValue == null)
				{
					showShareVideoDialog2(SharePage.WEIXIN, new BeautyVideoSharePage.ShareVideoDialogCallback()
					{
						@Override
						public void shareVideoType(boolean uploadServer)
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
							if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
							if(mWeiXin.isWXAppInstalled()) {
								mWeiXin.openWeiXinWithSDK();
							}else {
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_weixin_error_client_no_install), Toast.LENGTH_LONG).show();
							}
						}
					});
					return;
				}
				if(mYSL) {
					Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
				}
				showShareVideoDialog(SharePage.WEIXIN, new BeautyVideoSharePage.ShareVideoDialogCallback()
				{
					@Override
					public void shareVideoType(boolean uploadServer)
					{
						if(uploadServer)
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
							if(m_shareUrl != null && m_shareUrl.length() > 0)
							{
								sendToWeiXin(m_shareUrl, m_shareBrowse, false);
								return;
							}

							uploadVideo(m_savePath, new BeautyVideoSharePage.UploadVideoCallback()
							{
								@Override
								public void uploadComplete(String url)
								{
									m_shareUrl = url;
									if(!sendToWeiXin(m_shareUrl, m_shareBrowse, false))
									{
										if(m_uploadComplete)
										{
											m_uploadComplete = false;
											m_videoSaving = false;
											m_shareFrame.setVisibility(View.VISIBLE);
											m_progressFrame.setVisibility(View.GONE);
										}
									}
								}

								@Override
								public void uploadFail()
								{
									Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
								}
							});
						}
						else
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
							if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
							if(mWeiXin.isWXAppInstalled()) {
								mWeiXin.openWeiXinWithSDK();
							} else {
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_weixin_error_client_no_install), Toast.LENGTH_LONG).show();
							}
						}
					}
				});
			}
			else if(view == mIconWeiXin)
			{
				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendPicToWeiXin(m_savePath, true);
					return;
				}

				if(m_shareUrl != null && m_shareUrl.length() > 0)
				{
					sendToWeiXin(m_shareUrl, m_shareBrowse, true);
					return;
				}

				if(m_savePath == null || !new File(m_savePath).exists())
				{
					Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
					return;
				}

				if(mChannelValue == null)
				{
					showShareVideoDialog2(SharePage.WEIXIN, new BeautyVideoSharePage.ShareVideoDialogCallback()
					{
						@Override
						public void shareVideoType(boolean uploadServer)
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
							if(mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
							if(mWeiXin.isWXAppInstalled()) mWeiXin.openWeiXinWithSDK();
							else
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_weixin_error_client_no_install), Toast.LENGTH_LONG).show();
						}
					});
					return;
				}
				if(mYSL)
					Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
				uploadVideo(m_savePath, new BeautyVideoSharePage.UploadVideoCallback()
				{
					@Override
					public void uploadComplete(String url)
					{
						m_shareUrl = url;
						if(!sendToWeiXin(m_shareUrl, m_shareBrowse, true))
						{
							if(m_uploadComplete)
							{
								m_uploadComplete = false;
								m_videoSaving = false;
								m_shareFrame.setVisibility(View.VISIBLE);
								m_progressFrame.setVisibility(View.GONE);
							}
						}
					}

					@Override
					public void uploadFail()
					{
						Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
					}
				});
			}
			else if(view == mIconSina)
			{
				if(mSina == null)
				{
					mSina = new SinaBlog(getContext());
				}
				if(!SettingPage.checkSinaBindingStatus(getContext()))
				{
					mSina.bindSinaWithSSO(new SinaBlog.BindSinaCallback()
					{
						@Override
						public void success(final String accessToken, String expiresIn, String uid, String userName, String nickName)
						{
							//获取是绑定的时间:
							String saveTime = String.valueOf(System.currentTimeMillis() / 1000);

							GetSettingInfo(getContext()).SetSinaUid(uid);
							GetSettingInfo(getContext()).SetSinaAccessToken(accessToken);
							GetSettingInfo(getContext()).SetSinaSaveTime(saveTime);
							GetSettingInfo(getContext()).SetSinaUserName(userName);
							GetSettingInfo(getContext()).SetSinaUserNick(nickName);
							GetSettingInfo(getContext()).SetSinaExpiresIn(expiresIn);

							if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
							{
								sendPicToSina(m_savePath);
								return;
							}

							if(m_savePath == null || !new File(m_savePath).exists())
							{
								Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
								return;
							}

							if(mChannelValue == null)
							{
//								showShareVideoDialog2(SharePage.SINA, new BeautyVideoSharePage.ShareVideoDialogCallback()
//								{
//									@Override
//									public void shareVideoType(boolean uploadServer)
//									{
//										MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
//										if(!openSina()) Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
//									}
//								});
								sendVideoToSina(m_savePath);
								return;
							}
							if(mYSL)
								Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
							showShareVideoDialog(SharePage.SINA, new BeautyVideoSharePage.ShareVideoDialogCallback()
							{
								@Override
								public void shareVideoType(boolean uploadServer)
								{
									if(uploadServer)
									{
										MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
										if(m_shareUrl != null && m_shareUrl.length() > 0)
										{
											sendToSina(mShareText + m_shareUrl);
											return;
										}
										uploadVideo(m_savePath, new BeautyVideoSharePage.UploadVideoCallback()
										{
											@Override
											public void uploadComplete(String url)
											{
												m_shareUrl = url;
												if(!sendToSina(mShareText + m_shareUrl))
												{
													if(m_uploadComplete)
													{
														m_uploadComplete = false;
														m_videoSaving = false;
														m_shareFrame.setVisibility(View.VISIBLE);
														m_progressFrame.setVisibility(View.GONE);
													}
												}
											}

											@Override
											public void uploadFail()
											{
												Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
											}
										});
									}
									else
									{
										MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
										if(!openSina()) Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
									}
								}
							});
						}

						@Override
						public void fail()
						{
							switch(mSina.LAST_ERROR)
							{
								case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
									Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
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
					if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
					{
						sendPicToSina(m_savePath);
						return;
					}

					if(m_savePath == null || !new File(m_savePath).exists())
					{
						Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
						return;
					}

					if(mChannelValue == null)
					{
//						showShareVideoDialog2(SharePage.SINA, new BeautyVideoSharePage.ShareVideoDialogCallback()
//						{
//							@Override
//							public void shareVideoType(boolean uploadServer)
//							{
//								MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
//								if(!openSina()) Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
//							}
//						});
						sendVideoToSina(m_savePath);
						return;
					}
					if(mYSL)
						Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
					showShareVideoDialog(SharePage.SINA, new BeautyVideoSharePage.ShareVideoDialogCallback()
					{
						@Override
						public void shareVideoType(boolean uploadServer)
						{
							if(uploadServer)
							{
								MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
								if(m_shareUrl != null && m_shareUrl.length() > 0)
								{
									sendToSina(mShareText + m_shareUrl);
									return;
								}
								uploadVideo(m_savePath, new BeautyVideoSharePage.UploadVideoCallback()
								{
									@Override
									public void uploadComplete(String url)
									{
										m_shareUrl = url;
										if(!sendToSina(mShareText + m_shareUrl))
										{
											if(m_uploadComplete)
											{
												m_uploadComplete = false;
												m_videoSaving = false;
												m_shareFrame.setVisibility(View.VISIBLE);
												m_progressFrame.setVisibility(View.GONE);
											}
										}
									}

									@Override
									public void uploadFail()
									{
										Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
									}
								});
							}
							else
							{
								MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
								if(!openSina()) Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
							}
						}
					});
				}
			}
			else if(view == mIconQzone)
			{
				if(mQzone == null)
				{
					mQzone = new QzoneBlog2(getContext());
				}

				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendPicToQzone(m_savePath);
					return;
				}

				if(m_savePath == null || !new File(m_savePath).exists())
				{
					Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
					return;
				}

				if(mChannelValue == null)
				{
					showShareVideoDialog2(SharePage.QZONE, new BeautyVideoSharePage.ShareVideoDialogCallback()
					{
						@Override
						public void shareVideoType(boolean uploadServer)
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
							if(mQzone == null) mQzone = new QzoneBlog2(getContext());
							if(mQzone.checkQQClientInstall()) mQzone.openQQ();
							else
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
						}
					});
					return;
				}
				if(mYSL)
					Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
				showShareVideoDialog(SharePage.QZONE, new BeautyVideoSharePage.ShareVideoDialogCallback()
				{
					@Override
					public void shareVideoType(boolean uploadServer)
					{
						if(uploadServer)
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
							if(m_shareUrl != null && m_shareUrl.length() > 0)
							{
								sendToQzone(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse));
								return;
							}

							uploadVideo(m_savePath, new BeautyVideoSharePage.UploadVideoCallback()
							{
								@Override
								public void uploadComplete(String url)
								{
									m_shareUrl = url;
									if(!sendToQzone(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse)))
									{
										if(m_uploadComplete)
										{
											m_uploadComplete = false;
											m_videoSaving = false;
											m_shareFrame.setVisibility(View.VISIBLE);
											m_progressFrame.setVisibility(View.GONE);
										}
									}
								}

								@Override
								public void uploadFail()
								{
									Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
								}
							});
						}
						else
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
							if(mQzone == null) mQzone = new QzoneBlog2(getContext());
							if(mQzone.checkQQClientInstall()) mQzone.openQQ();
							else
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
						}
					}
				});
			}
			else if(view == mIconQQ)
			{
				if(mQzone == null)
				{
					mQzone = new QzoneBlog2(getContext());
				}

				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendPicToQQ(m_savePath);
					return;
				}

				if(m_savePath == null || !new File(m_savePath).exists())
				{
					Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
					return;
				}

				if(mChannelValue == null)
				{
					showShareVideoDialog2(SharePage.QZONE, new BeautyVideoSharePage.ShareVideoDialogCallback()
					{
						@Override
						public void shareVideoType(boolean uploadServer)
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
							if(mQzone == null) mQzone = new QzoneBlog2(getContext());
							if(mQzone.checkQQClientInstall()) mQzone.openQQ();
							else
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
						}
					});
					return;
				}
				if(mYSL)
					Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
				showShareVideoDialog(SharePage.QZONE, new BeautyVideoSharePage.ShareVideoDialogCallback()
				{
					@Override
					public void shareVideoType(boolean uploadServer)
					{
						if(uploadServer)
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
							if(m_shareUrl != null && m_shareUrl.length() > 0)
							{
								sendToQQ(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse));
								return;
							}

							uploadVideo(m_savePath, new BeautyVideoSharePage.UploadVideoCallback()
							{
								@Override
								public void uploadComplete(String url)
								{
									m_shareUrl = url;
									if(!sendToQQ(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse)))
									{
										if(m_uploadComplete)
										{
											m_uploadComplete = false;
											m_videoSaving = false;
											m_shareFrame.setVisibility(View.VISIBLE);
											m_progressFrame.setVisibility(View.GONE);
										}
									}
								}

								@Override
								public void uploadFail()
								{
									Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
								}
							});
						}
						else
						{
							MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
							if(mQzone == null) mQzone = new QzoneBlog2(getContext());
							if(mQzone.checkQQClientInstall()) mQzone.openQQ();
							else
								Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
						}
					}
				});
			}
            else if(view == mIconCommunity)
            {
                if (!checkVideoDuration(2000, 900000)) {
                    Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_share_together_video_time_limit), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                {
                    sendToCircle(m_savePath, null);
                    return;
                }
                sendVideoToCircle(m_savePath, null);
            }
			else if(view == mIconFaceBook)
			{
				if(mYSL)
					Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendPicToFacebook(m_savePath);
					return;
				}
				if(m_savePath == null || !new File(m_savePath).exists())
				{
					Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
					return;
				}
				sendVideoToFacebook(m_savePath);
			}
			else if(view == mIconTwitter)
			{
				if(mYSL)
					Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendPicToTwitter(m_savePath, null);
					return;
				}
				if(m_savePath == null || !new File(m_savePath).exists())
				{
					Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
					return;
				}
				sendVideoToTwitter(m_savePath, null);
			}
			else if(view == mIconInstagram)
			{
				if(mYSL)
					Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendPicToInstagram(m_savePath);
					return;
				}
				if(m_savePath == null || !new File(m_savePath).exists())
				{
					Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
					return;
				}
				sendVideoToInstagram(m_savePath);
			}
			else if(view == mImageHolder)
			{
				onPause();
				if (!isVideoSharePage) {
					//已删除 MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_查看预览图);
					onPreview(getContext(), m_savePath, false);
					return;
				} else {
					MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_查看预览图);
				}
				onPreview(getContext(), m_savePath, true);
			}
//            else if (view == m_back2) {
//                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_分享_返回);
//                m_shareFrame.setVisibility(View.GONE);
//                m_videoFrame.setVisibility(View.VISIBLE);
//                m_showVideoFrame = true;
//                m_video.setPageShow(m_showVideoFrame);
//                m_video.onResume();
//            }
		}
	};


	private void onPreview(Context context, String path, boolean isVideo)
	{
		if(mPageSite != null)
		{
			mPageSite.OnPreview(context, path, isVideo);
		}
	}

	protected OnAnimationClickListener mBtnListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(onAnimation || m_onClose) return;

			if(v == m_homeBtn)
			{
				if (isVideoSharePage) {
					MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_回到首页);
				} else {
					MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_回到首页);
				}
				mPageSite.OnHome(getContext());
			}
			else if(v == m_backBtn)
			{
				if (isVideoSharePage) {
					MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_返回上一级);
				} else {
					//已删除 MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_返回上一级);
				}
				TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_分享_返回);
				onBack();
			}
		}

		@Override
		public void onTouch(View v)
		{
		}

		@Override
		public void onRelease(View v)
		{
		}
	};

	private void openAnimation()
	{
		if (m_shareFrame != null) m_shareFrame.setVisibility(View.VISIBLE);
		post(new Runnable()
		{
			@Override
			public void run()
			{
				ShareFrame.UIanime(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{
						onAnimation = true;
						if(m_animationCallback != null) m_animationCallback.onAnimationStart(true);
					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						animation.setAnimationListener(null);
						onAnimation = false;
						if(m_shareBackground != null) m_shareBackground.clearAnimation();
						if(m_topFrame != null) m_topFrame.clearAnimation();
						if(m_topBackground != null) m_topBackground.clearAnimation();
						if(mAdvBar != null)
						{
							mAdvBar.clearAnimation();
							if(mAdvBar.isFirstShow()) mAdvBar.autoPage();
							else mAdvBar.onStart();
						}
						onAnimation = false;
						if(m_animationCallback != null) m_animationCallback.onAnimationEnd(true);
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{
					}
				});
			}
		});
	}

	private void closeAnimation()
	{
		post(new Runnable()
		{
			@Override
			public void run()
			{
				ShareFrame.UIanime2(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{
						onAnimation = true;
						if (m_animationCallback != null) m_animationCallback.onAnimationStart(false);
					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						animation.setAnimationListener(null);
						onAnimation = false;
						if(m_onClose) return;
						if (m_shareFrame != null) m_shareFrame.setVisibility(View.GONE);
						if(m_shareBackground != null) m_shareBackground.clearAnimation();
						if(m_topFrame != null) m_topFrame.clearAnimation();
						if(m_topBackground != null) m_topBackground.clearAnimation();
						if(mAdvBar != null) mAdvBar.clearAnimation();
						if (m_animationCallback != null) m_animationCallback.onAnimationEnd(false);
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{
					}
				});
			}
		});
	}

	private boolean checkVideoDuration(long min, long max)
	{
		if(m_videoSaved && (mVideoDuration < min || mVideoDuration > max))
		{
			return false;
		}
		return true;
	}

	public static String GetPhotoSavePath()
	{
		String out = null;
		File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		if(dcim != null)
		{
			out = dcim.getAbsolutePath() + File.separator + "Camera";
		}
		else
		{
			out = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera";
		}
		//魅族的默认相册路径不同，原来的路径图库不显示
		String manufacturer = android.os.Build.MANUFACTURER;
		if(manufacturer != null)
		{
			manufacturer = manufacturer.toLowerCase(Locale.getDefault());
			if(manufacturer.contains("meizu"))
			{
				out = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Camera";
			}
		}
		CommonUtils.MakeFolder(out);
		return out;
	}

	private String makeQzoneShareThumbPath(String img_path)
	{
		if(img_path == null || img_path.length() <= 0) return null;

		File file = new File(img_path);
		if(!file.exists()) return null;

		StringBuffer sb = new StringBuffer();
		String dirName = GetPhotoSavePath();
		File destDir = new File(dirName);
		if(!destDir.exists()) destDir.mkdirs();
		sb.append(dirName);
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
		String strDate = df.format(date);
		String strRand = Integer.toString((int)(Math.random() * 100));
		if(strRand.length() < 4) strRand = "0000".substring(strRand.length()) + strRand;
		sb.append(File.separator + strDate + strRand + ".jpg");
		try
		{
			File copyFile = new File(sb.toString());
			FileUtils.copyFile(file, copyFile);
			return sb.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送到Sina微博;
	 */
	private boolean sendToSina(final String url)
	{
		if(url == null || url.length() <= 0)
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_url), Toast.LENGTH_LONG).show();
			return false;
		}

		if(mSina == null) mSina = new SinaBlog(getContext());
		if(!mSina.checkSinaClientInstall())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return false;
		}
		mSina.SetAccessToken(GetSettingInfo(getContext()).GetSinaAccessToken());
		mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
		{
			@Override
			public void response(boolean send_success, int response_code)
			{
				if(send_success)
				{
					switch(response_code)
					{
						case WBConstants.ErrorCode.ERR_OK:
							ShareTools.addIntegral(getContext());
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG);
							MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_视频分享页_主页面);
							break;

						case WBConstants.ErrorCode.ERR_CANCEL:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
							break;

						case WBConstants.ErrorCode.ERR_FAIL:
						case SinaBlog.NO_RESPONSE:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
							break;
					}
				}
				else
				{
					SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
				}
			}
		});

		Intent intent = new Intent(getContext(), SinaRequestActivity.class);
		intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT);
		intent.putExtra("content", url);
		((Activity)getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
		return true;
	}

	private void sendPicToSina(String pic)
	{
		if(pic == null || pic.length() <= 0 || !new File(pic).exists())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
			return;
		}

		if(mSina == null)
		{
			mSina = new SinaBlog(getContext());
		}
		if(!mSina.checkSinaClientInstall())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return;
		}
		mSina.SetAccessToken(GetSettingInfo(getContext()).GetSinaAccessToken());
		mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
		{
			@Override
			public void response(boolean send_success, int response_code)
			{
				if(send_success)
				{
					switch(response_code)
					{
						case WBConstants.ErrorCode.ERR_OK:
							ShareTools.addIntegral(getContext());
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG);
							MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_萌妆照分享页_主页面);
							break;

						case WBConstants.ErrorCode.ERR_CANCEL:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
							break;

						case WBConstants.ErrorCode.ERR_FAIL:
						case SinaBlog.NO_RESPONSE:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
							break;
					}
				}
				else
				{
					SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
				}
			}
		});

		Intent intent = new Intent(getContext(), SinaRequestActivity.class);
		intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT_AND_PIC);
		intent.putExtra("pic", pic);
		if(resId == 710 || resId == 711 || resId == 712 || resId == 713)
			intent.putExtra("content", mShareText);
		else intent.putExtra("content", ShareFrame.SHARE_DEFAULT_TEXT);
		((Activity)getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
	}

	private void sendVideoToSina(String video_path)
	{
		if(video_path == null || video_path.length() <= 0 || !new File(video_path).exists())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_video_is_null), Toast.LENGTH_LONG).show();
			return;
		}

		if(mSina == null)
		{
			mSina = new SinaBlog(getContext());
		}
		if(!mSina.checkSinaClientInstall())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return;
		}
		mSina.SetAccessToken(GetSettingInfo(getContext()).GetSinaAccessToken());
		mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
		{
			@Override
			public void response(boolean send_success, int response_code)
			{
				if(send_success)
				{
					switch(response_code)
					{
						case WBConstants.ErrorCode.ERR_OK:
							ShareTools.addIntegral(getContext());
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG);
							MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_视频分享页_主页面);
							break;

						case WBConstants.ErrorCode.ERR_CANCEL:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
							break;

						case WBConstants.ErrorCode.ERR_FAIL:
						case SinaBlog.NO_RESPONSE:
							SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
							break;
					}
				}
				else
				{
					SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
				}
			}
		});

		Intent intent = new Intent(getContext(), SinaRequestActivity.class);
		intent.putExtra("video", video_path);
		intent.putExtra("content", ShareFrame.SHARE_DEFAULT_TEXT);
		((Activity)getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
	}

	private boolean openSina()
	{
		if(mSina == null) mSina = new SinaBlog(getContext());
		if(!mSina.checkSinaClientInstall()) return false;

		Intent intent = new Intent(getContext(), SinaRequestActivity.class);
		intent.putExtra("content", " ");
		((Activity)getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
		return true;
	}

	private boolean sendToQzone(String url, final String pic_path)
	{
		if(mQzone == null)
		{
			mQzone = new QzoneBlog2(getContext());
		}
		if(!mQzone.checkQQClientInstall())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return false;
		}
		mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			@Override
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_视频分享页_主页面);
				}
				else if(result == QzoneBlog2.SEND_CANCEL)
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
				File file = new File(pic_path);
				if(file.exists()) file.delete();
			}
		});
		if(!mQzone.sendToQzone2(mShareDescription, pic_path, mShareTitle, url))
		{
			SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
			return false;
		}
		return true;
	}

	private void sendPicToQzone(String pic)
	{
		if(mQzone == null)
		{
			mQzone = new QzoneBlog2(getContext());
		}
		if(!mQzone.checkQQClientInstall())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return;
		}
		mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			@Override
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_萌妆照分享页_主页面);
				}
				else if(result == QzoneBlog2.SEND_CANCEL)
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});
		if(!mQzone.sendToPublicQzone(1, pic))
		{
			SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
		}
	}

	private boolean sendToQQ(String url, final String pic_path)
	{
		if(mQzone == null)
		{
			mQzone = new QzoneBlog2(getContext());
		}
		if(!mQzone.checkQQClientInstall())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return false;
		}
		mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			@Override
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
//                    TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer., getResources().getString(TAG));
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_视频分享页_主页面);
				}
				else if(result == QzoneBlog2.SEND_CANCEL)
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
				File file = new File(pic_path);
				if(file.exists()) file.delete();
			}
		});
		if(!mQzone.sendUrlToQQ(pic_path, mShareTitle, mShareDescription, url))
		{
			SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
			return false;
		}
		return true;
	}

	private void sendPicToQQ(String pic)
	{
		if(mQzone == null)
		{
			mQzone = new QzoneBlog2(getContext());
		}
		if(!mQzone.checkQQClientInstall())
		{
			Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
			return;
		}
		mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
		mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			@Override
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS)
				{
//                    TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer., getResources().getString(TAG));
					ShareTools.addIntegral(getContext());
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_萌妆照分享页_主页面);
				}
				else if(result == QzoneBlog2.SEND_CANCEL)
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});
		if(!mQzone.sendToQQ(pic))
		{
			SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
		}
	}

	private boolean sendToWeiXin(String url, String pic, final boolean WXSceneSession)
	{
		if(url == null || url.length() <= 0 || pic == null || pic.length() <= 0) return false;

		if(mWeiXin == null)
		{
			mWeiXin = new WeiXinBlog(getContext());
		}
		Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
		String title = WXSceneSession ? mShareTitle : mShareFriendsTitle;
		if(mWeiXin.sendUrlToWeiXin(url, title, mShareDescription, thumb, WXSceneSession))
		{
			SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
			{
				@Override
				public void onCallFinish(int result)
				{
					switch(result)
					{
						case BaseResp.ErrCode.ERR_OK:
							if(WXSceneSession)
							{
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_视频分享页_主页面);
							}
							else
							{
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_视频分享页_主页面);
							}
							ShareTools.addIntegral(getContext());
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
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
		}
		else
		{
			SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, WXSceneSession);
			return false;
		}
		return true;
	}

	private void sendPicToWeiXin(String pic, final boolean WXSceneSession)
	{
		if(pic == null || pic.length() <= 0) return;

		if(mWeiXin == null)
		{
			mWeiXin = new WeiXinBlog(getContext());
		}
		Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
		if(mWeiXin.sendToWeiXin(pic, thumb, WXSceneSession))
		{
			SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
			{
				@Override
				public void onCallFinish(int result)
				{
					switch(result)
					{
						case BaseResp.ErrCode.ERR_OK:
							if(WXSceneSession)
							{
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_萌妆照分享页_主页面);
							}
							else
							{
								MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_萌妆照分享页_主页面);
							}
							ShareTools.addIntegral(getContext());
							Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
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
		}
		else
		{
			SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, WXSceneSession);
		}
	}

	/**
	 * 发送图片到Facebook
	 *
	 * @param pic 图片路径(图片不能大于12mb)
	 */
	public void sendPicToFacebook(String pic)
	{
		if(mFacebook == null) mFacebook = new FacebookBlog(getContext());
		Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, -1, -1);
		final ProgressDialog mFacebookProgressDialog = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.share_facebook_client_call));
		mFacebookProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mFacebookProgressDialog.setCancelable(true);
		boolean send_success = mFacebook.sendPhotoToFacebookBySDK(bmp, new FacebookBlog.FaceBookSendCompleteCallback()
		{
			@Override
			public void sendComplete(int result, String error_info)
			{
				if(mFacebookProgressDialog.isShowing()) mFacebookProgressDialog.dismiss();
				else return;
				if(result == FacebookBlog.RESULT_SUCCESS)
				{
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					ShareTools.addIntegral(getContext());
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_萌妆照分享页_主页面);
				}
				else
				{
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});

		if(!send_success)
		{
			mFacebookProgressDialog.dismiss();
			String message = null;
			switch(mFacebook.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_facebook_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_image_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_facebook_client_start_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 发送视频到Facebook
	 *
	 * @param video_path 视频路径，视频不能大于12m
	 */
	public void sendVideoToFacebook(String video_path)
	{
		if(mFacebook == null) mFacebook = new FacebookBlog(getContext());
		final ProgressDialog mFacebookProgressDialog = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.share_facebook_client_call));
		mFacebookProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mFacebookProgressDialog.setCancelable(true);
		boolean send_success = mFacebook.sendVideoToFacebookBySDK(video_path, new FacebookBlog.FaceBookSendCompleteCallback()
		{
			@Override
			public void sendComplete(int result, String error_info)
			{
				if(mFacebookProgressDialog.isShowing()) mFacebookProgressDialog.dismiss();
				else return;
				if(result == FacebookBlog.RESULT_SUCCESS)
				{
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
					ShareTools.addIntegral(getContext());
					MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_视频分享页_主页面);
				}
				else
				{
					Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
				}
			}
		});

		if(!send_success)
		{
			mFacebookProgressDialog.dismiss();
			String message;
			switch(mFacebook.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_facebook_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_VIDEO_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_video_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_facebook_client_start_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 发送图片和文字到Twitter，两者至少有一种
	 *
	 * @param pic     图片路径
	 * @param content 文字内容
	 */
	public void sendPicToTwitter(String pic, String content)
	{
		if(mTwitter == null) mTwitter = new TwitterBlog(getContext());
		if(!mTwitter.sendToTwitter(pic, content))
		{
			String message;
			switch(mTwitter.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_twitter_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_context_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			return;
		}
		ShareTools.addIntegral(getContext());
		MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_萌妆照分享页_主页面);
	}

	/**
	 * 发送视频和文字到Twitter，两者至少有一种
	 *
	 * @param video_path 视频路径
	 * @param content    文字内容
	 */
	public void sendVideoToTwitter(String video_path, String content)
	{
		if(mTwitter == null) mTwitter = new TwitterBlog(getContext());
		if(!mTwitter.sendVideoToTwitter(video_path, content))
		{
			String message;
			switch(mTwitter.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_twitter_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_VIDEO_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_video_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			return;
		}
		ShareTools.addIntegral(getContext());
		MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_视频分享页_主页面);
	}

	/**
	 * 发送图片到Instagram
	 *
	 * @param pic 图片
	 */
	public void sendPicToInstagram(String pic)
	{
		if(mInstagram == null) mInstagram = new InstagramBlog(getContext());
		if(!mInstagram.sendToInstagram(pic))
		{
			String message;
			switch(mInstagram.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_instagram_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_image_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			return;
		}
		ShareTools.addIntegral(getContext());
	}

	/**
	 * 发送视频到Instagram
	 *
	 * @param video_path 视频路径
	 */
	public void sendVideoToInstagram(String video_path)
	{
		if(mInstagram == null) mInstagram = new InstagramBlog(getContext());
		if(!mInstagram.sendVideoToInstagram(video_path))
		{
			String message;
			switch(mInstagram.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = getContext().getResources().getString(R.string.share_instagram_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_VIDEO_IS_NULL:
					message = getContext().getResources().getString(R.string.share_error_video_is_null);
					break;

				default:
					message = getContext().getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
			return;
		}
		ShareTools.addIntegral(getContext());
	}

	/**
	 * 发送图片和文字到Circle
	 *
	 * @param pic     图片路径
	 * @param content 文字内容
	 */
	public void sendToCircle(String pic, String content)
	{
		if(pic == null || pic.length() <= 0 || !new File(pic).exists()) return;

//        if(mCircleApi == null) mCircleApi = CircleSDK.createApi(getContext(), 3);
//        CircleMultiInfo multiInfo = new CircleMultiInfo();
//        if(content != null)
//        {
//            TextObject textObject = new TextObject();
//            textObject.text = content;
//            multiInfo.add(textObject);
//        }
//
//        ImageObject imageObject = new ImageObject();
//        imageObject.imgUri = ShareTools.decodePath(pic);
//        imageObject.imgExtra = makeCircleExtra(resId, resTjId);
//        multiInfo.add(imageObject);
//
//        CircleApi.setOnCallBackListener(new CircleApi.OnCallBackListener()
//        {
//            @Override
//            public void OnMessage(int code , String msg)
//            {
//                if(code == 0) ShareTools.addIntegral(getContext());
//                SharePage.showCircleCodeMessage(getContext(), code);
//            }
//        });
//        mCircleApi.attachInfo(multiInfo);
//        mCircleApi.share();
		shareToCommunity(pic, content, 1);
	}

	/**
	 * 发送视频和文字到Circle
	 *
	 * @param video_path 视频路径
	 * @param content    文字内容
	 */
	public void sendVideoToCircle(String video_path, String content)
	{
		if(video_path == null || video_path.length() <= 0 || !new File(video_path).exists()) return;

//        if(mCircleApi == null) mCircleApi = CircleSDK.createApi(getContext(), 3);
//        CircleMultiInfo multiInfo = new CircleMultiInfo();
//        if(content != null)
//        {
//            TextObject textObject = new TextObject();
//            textObject.text = content;
//            multiInfo.add(textObject);
//        }
//        VideoObject videoObject = new VideoObject();
//        videoObject.videoUri = ShareTools.decodePath(video_path);
//        videoObject.videoExtra = makeCircleExtra(resId, resTjId);
//        multiInfo.add(videoObject);
//
//        CircleApi.setOnCallBackListener(new CircleApi.OnCallBackListener()
//        {
//            @Override
//            public void OnMessage(int code , String msg)
//            {
//                if(code == 0) ShareTools.addIntegral(getContext());
//                SharePage.showCircleCodeMessage(getContext(), code);
//            }
//        });
//        mCircleApi.attachInfo(multiInfo);
//        mCircleApi.share();
		shareToCommunity(video_path, content, 2);
	}

	public static String makeCircleExtra(int resId, String resTjId)
	{
		try
		{
			JSONObject extra = new JSONObject();
			JSONArray material = new JSONArray();
			JSONObject res = new JSONObject();
			res.put("id", resId);
			if(resTjId != null) res.put("stat_id", resTjId);
			material.put(res);
			extra.put("material", material);
			//System.out.println(extra.toString());
			return extra.toString();
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private void shareToCommunity(String path, String content, int type)
	{
		if(!UserMgr.IsLogin(getContext(), null))
		{
			//未登录去登录
			mPageSite.OnLogin(getContext());
			return;
		}
		UserInfo userInfo = UserMgr.ReadCache(getContext());
		if(userInfo != null && TextUtils.isEmpty(userInfo.mMobile))
		{
			//未完善资料去完善资料
			mPageSite.OnBindPhone(getContext());
			return;
		}
		if(content == null) content = "";
		if(TextUtils.isEmpty(path))
		{
			return;
		}
		mPageSite.OnCommunity(getContext(), path, content, type, makeCircleExtra(resId, resTjId));
	}

	private void showSuccessDialog()
	{
		SharedTipsView view = new SharedTipsView(getContext());

		final Dialog dialog = new Dialog(getContext(), R.style.fullDialog1);
		view.setJump2AppClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mPageSite.OnCommunityHome(getContext());
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

	private void uploadVideo(String video_path, final BeautyVideoSharePage.UploadVideoCallback callback)
	{
		m_videoSaving = true;
		m_shareFrame.setVisibility(View.GONE);
		m_progressFrame.setVisibility(View.VISIBLE);
		setVideoSaveProgress(0);

		final UploadData data = new UploadData();
		if(mChannelValue != null && mChannelValue.length() > 0) data.mChannelValue = mChannelValue;
		else data.mChannelValue = "beauty_2017";
		data.mImgPath = m_picPath;
		data.mPostParams = SharePage.getPostActivitiesStr2(getContext(), mAdvPostStr).toString();
		data.mVideoPath = video_path;
		data.mStatId = resTjId;

		if(m_uploadCallback == null)
		{
			m_uploadCallback = new ObjHandlerHolder<AbsUploadFile.Callback>(new AbsUploadFile.Callback()
			{
				@Override
				public void onProgress(int progress)
				{
					setVideoSaveProgress(progress);
				}

				@Override
				public void onSuccess(final String shareUrl)
				{
					post(new Runnable()
					{
						@Override
						public void run()
						{
							if(shareUrl == null || shareUrl.length() <= 0)
							{
								m_videoSaving = false;
								m_shareFrame.setVisibility(View.VISIBLE);
								m_progressFrame.setVisibility(View.GONE);
								if(callback != null) callback.uploadFail();
								return;
							}
							m_uploadComplete = true;
							if(callback != null) callback.uploadComplete(shareUrl);
						}
					});
				}

				@Override
				public void onFailure()
				{
					post(new Runnable()
					{
						@Override
						public void run()
						{
							m_videoSaving = false;
							m_shareFrame.setVisibility(View.VISIBLE);
							m_progressFrame.setVisibility(View.GONE);
							if(callback != null) callback.uploadFail();
						}
					});
				}
			});
		}

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new UploadFile(getContext(), data, AppInterface.GetInstance(getContext()), m_uploadCallback);
			}
		}).start();
	}

	/**
	 * 上传图片到poco获取分享链接
	 *
	 * @return
	 */
//    private String sendToPocoGetUrl(String pic, String mp4_url) {
//        String post_url = "http://www1.poco.cn/topic/interface/beautycamera_common_post.php";
//        String post_str;
//        if(mAdvPostStr != null) post_str = getPostActivitiesStr(mAdvPostStr);
//        else post_str = getPostActivitiesStr();
//        String url = sendPocoActivities(post_url, post_str, pic, mp4_url);
//        return url;
//    }
	private String makePostStr(Context context)
	{
		String postVar = "";
		try
		{
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = null;
			if(tm != null)
			{
				imei = tm.getDeviceId();
			}
			if(imei == null)
			{
				imei = Integer.toString((int)(Math.random() * 1000000));
			}
			imei = CommonUtils.Encrypt("MD5", imei);

			postVar += "|channel_value:" + URLEncoder.encode("beautycamera_2016", "UTF-8");
			postVar += "|hash:" + URLEncoder.encode(imei, "UTF-8");
			String frame = Integer.toString(0 & 0x00ff, 16);
			if(frame.length() < 2)
			{
				frame = "0" + frame;
			}
			postVar += "|frame_id:" + frame;
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return postVar;
	}

	private String getPostActivitiesStr()
	{
		String postVal = makePostStr(getContext());
		String postText = "动态贴纸";
		try
		{
//			if(GetSettingInfo(getContext()).GetPocoId() != null && GetSettingInfo(getContext()).GetPocoId().length() > 0)
//			{
//				postText += "|poco_id:" + URLEncoder.encode(GetSettingInfo(getContext()).GetPocoId(), "UTF-8");
//			}
			if(GetSettingInfo(getContext()).GetSinaUid() != null && GetSettingInfo(getContext()).GetSinaUid().length() > 0 && GetSettingInfo(getContext()).GetSinaUserNick() != null && GetSettingInfo(getContext()).GetSinaUserNick().length() > 0)
			{
				postText += "|sina:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUid(), "UTF-8");
				postText += "|sina_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUserNick(), "UTF-8");
			}
			if(GetSettingInfo(getContext()).GetQzoneOpenid() != null && GetSettingInfo(getContext()).GetQzoneOpenid().length() > 0 && GetSettingInfo(getContext()).GetQzoneUserName() != null && GetSettingInfo(getContext()).GetQzoneUserName().length() > 0)
			{
				postText += "|q_zone:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneOpenid(), "UTF-8");
				postText += "|q_zone_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneUserName(), "UTF-8");
			}
			if(SharePage.city != null && SharePage.city.length() > 0)
			{
				postText += "|city:" + URLEncoder.encode(SharePage.city, "UTF-8");
				postText += "|latlng:" + SharePage.lat + "," + SharePage.lon;
			}
			postVal += "|content:" + URLEncoder.encode(postText, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return postVal;
	}

	private String getPostActivitiesStr(String postVal)
	{
		if(postVal == null) return null;
		String postText = postVal;
		try
		{
//			if(GetSettingInfo(getContext()).GetPocoId() != null && GetSettingInfo(getContext()).GetPocoId().length() > 0)
//			{
//				postText += "|poco_id:" + URLEncoder.encode(GetSettingInfo(getContext()).GetPocoId(), "UTF-8");
//			}
			if(GetSettingInfo(getContext()).GetSinaUid() != null && GetSettingInfo(getContext()).GetSinaUid().length() > 0 && GetSettingInfo(getContext()).GetSinaUserNick() != null && GetSettingInfo(getContext()).GetSinaUserNick().length() > 0)
			{
				postText += "|sina:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUid(), "UTF-8");
				postText += "|sina_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUserNick(), "UTF-8");
			}
			if(GetSettingInfo(getContext()).GetQzoneOpenid() != null && GetSettingInfo(getContext()).GetQzoneOpenid().length() > 0 && GetSettingInfo(getContext()).GetQzoneUserName() != null && GetSettingInfo(getContext()).GetQzoneUserName().length() > 0)
			{
				postText += "|q_zone:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneOpenid(), "UTF-8");
				postText += "|q_zone_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneUserName(), "UTF-8");
			}
			if(SharePage.city != null && SharePage.city.length() > 0)
			{
				postText += "|city:" + URLEncoder.encode(SharePage.city, "UTF-8");
				postText += "|latlng:" + SharePage.lat + "," + SharePage.lon;
			}
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return postText;
	}

	private String sendPocoActivities(String postUrl, String postStr, String file, String mp4_url)
	{
		if(postStr == null) return null;

		final String BOUNDARY = java.util.UUID.randomUUID().toString();
		final String RETURN = "\r\n";
		final String PREFIX = "--";
		final String CHARSET = "UTF-8";
		HttpURLConnection conn = null;
		try
		{
			HashMap<String, String> values = new HashMap<String, String>();
			values.put("post_str", postStr);
			values.put("mp4_url", mp4_url);

			URL _url = new URL(postUrl);
			conn = (HttpURLConnection)_url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(60000);
			conn.setReadTimeout(60000);
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", CHARSET);
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String, String> entry : values.entrySet())
			{
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(RETURN);
				sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + RETURN);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + RETURN);
				sb.append("Content-Transfer-Encoding: 8bit" + RETURN);
				sb.append(RETURN);
				sb.append(entry.getValue());
				sb.append(RETURN);
			}
			outStream.write(sb.toString().getBytes());
			if(file != null)
			{
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(RETURN);
				sb1.append("Content-Disposition: form-data; name=\"opus\"; filename=\"" + file + "\"" + RETURN);
				sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + RETURN);
				sb1.append(RETURN);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = is.read(buffer)) != -1)
				{
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(RETURN.getBytes());

				outStream.write((PREFIX + BOUNDARY + PREFIX + RETURN).getBytes());
				outStream.flush();
			}

			int responseCode = conn.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				//解析返回的数据
				InputStreamReader isReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
				BufferedReader reader = new BufferedReader(isReader);
				String line;
				StringBuilder strb = new StringBuilder();
				while((line = reader.readLine()) != null)
				{
					strb.append(line);
				}
				String str = strb.toString();
				isReader.close();
				reader.close();
				if(str != null && str.length() > 0)
				{
					JSONObject json = new JSONObject(str);
					json = json.getJSONObject("Result");
					if(json != null)
					{
						String code = json.getString("ResultCode");
						String err = json.getString("ResultMessage");
						if(code != null && code.equals("0") && err != null && err.equals("success"))
						{
							conn.disconnect();
							return json.getString("share_img_link");
						}
					}
				}
			}
			else
			{
				//System.out.println("发送活动：服务器返还不为200，responseCode=" + responseCode);
			}
			outStream.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			conn.disconnect();
		}
		return null;
	}

	/**
	 * 隐藏键盘
	 */
	private void hideKeyboard()
	{
		post(new Runnable()
		{
			@Override
			public void run()
			{
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		});
	}

	private void showShareVideoDialog(int blog_type, final BeautyVideoSharePage.ShareVideoDialogCallback callback)
	{
		if(m_isShareVideoDialogShow || m_onClose) return;

		m_isShareVideoDialogShow = true;
		final Dialog dlg = new Dialog(getContext(), R.style.notitledialog);

		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;

		FrameLayout mainFrame = new FrameLayout(getContext());
		mainFrame.setBackgroundDrawable(new BitmapDrawable(getResources(), getGlassBackground()));
		fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mainFrame.setLayoutParams(fl);
		mainFrame.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_返回);
				dlg.dismiss();
			}
		});

		FrameLayout frame = new FrameLayout(getContext());
		fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568), ShareData.PxToDpi_xhdpi(686));
		fl.gravity = Gravity.CENTER;
		mainFrame.addView(frame, fl);
		{
			LinearLayout share_frame = new LinearLayout(getContext());
			share_frame.setOrientation(LinearLayout.VERTICAL);
			share_frame.setBackgroundResource(R.drawable.light_app06_sharetype_dialog_share_bg);
			fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(328));
			fl.gravity = Gravity.TOP | Gravity.LEFT;
			frame.addView(share_frame, fl);
			{
				TextView share_title = new TextView(getContext());
				share_title.setText(getContext().getResources().getString(R.string.lightapp06_share_type_title1));
				share_title.setTextColor(Color.BLACK);
				share_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				share_title.getPaint().setFakeBoldText(true);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(44);
				share_frame.addView(share_title, ll);

				TextView share_description = new TextView(getContext());
				share_description.setText(getContext().getResources().getString(R.string.lightapp06_share_type_please) + getShareTypeString(blog_type) + "\n" + getContext().getResources().getString(R.string.lightapp06_share_type_description1));
				share_description.setTextColor(Color.BLACK);
				share_description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				share_description.setGravity(Gravity.CENTER);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(20);
				share_frame.addView(share_description, ll);

				FrameLayout share_button = new FrameLayout(getContext());
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(260), LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(34);
				share_frame.addView(share_button, ll);
				{
					ImageView bg = new ImageView(getContext());
					bg.setImageResource(R.drawable.photofactory_noface_help_btn);
					bg.setScaleType(ImageView.ScaleType.FIT_XY);
					fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					fl.gravity = Gravity.LEFT | Gravity.TOP;
					share_button.addView(bg, fl);
					cn.poco.advanced.ImageUtils.AddSkin(getContext(), bg);

					TextView text = new TextView(getContext());
					text.setTextColor(Color.WHITE);
					text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					text.setText(getShareTypeString(blog_type));
					text.getPaint().setFakeBoldText(true);
					fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.CENTER;
					share_button.addView(text, fl);
				}
				share_button.setOnTouchListener(new OnAnimationClickListener()
				{
					@Override
					public void onAnimationClick(View v)
					{
						dlg.dismiss();
						if(callback != null) callback.shareVideoType(false);
					}

					@Override
					public void onTouch(View v)
					{
					}

					@Override
					public void onRelease(View v)
					{
					}
				});
			}

			ImageView or_icon = new ImageView(getContext());
			or_icon.setImageResource(R.drawable.light_app06_sharetype_dialog_or);
			fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			frame.addView(or_icon, fl);

			TextView or_text = new TextView(getContext());
			or_text.setText("OR");
			int color = cn.poco.advanced.ImageUtils.GetSkinColor();
			if(color != 0) or_text.setTextColor(color);
			else or_text.setTextColor(0xffe75988);
			or_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
			fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			frame.addView(or_text, fl);

			LinearLayout upload_frame = new LinearLayout(getContext());
			upload_frame.setOrientation(LinearLayout.VERTICAL);
			upload_frame.setBackgroundResource(R.drawable.light_app06_sharetype_dialog_upload_bg);
			fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(328));
			fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
			frame.addView(upload_frame, fl);
			{
				TextView upload_title = new TextView(getContext());
				upload_title.setText(getContext().getResources().getString(R.string.lightapp06_share_type_title2));
				upload_title.setTextColor(Color.BLACK);
				upload_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				upload_title.getPaint().setFakeBoldText(true);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(44);
				upload_frame.addView(upload_title, ll);

				TextView upload_description = new TextView(getContext());
				upload_description.setText(getContext().getResources().getString(R.string.lightapp06_share_type_description2));
				upload_description.setTextColor(Color.BLACK);
				upload_description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				upload_description.setGravity(Gravity.CENTER);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(20);
				upload_frame.addView(upload_description, ll);

				FrameLayout upload_button = new FrameLayout(getContext());
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(260), LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(34);
				upload_frame.addView(upload_button, ll);
				{
					ImageView bg = new ImageView(getContext());
					bg.setImageResource(R.drawable.photofactory_noface_help_btn);
					bg.setScaleType(ImageView.ScaleType.FIT_XY);
					fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					fl.gravity = Gravity.LEFT | Gravity.TOP;
					upload_button.addView(bg, fl);
					cn.poco.advanced.ImageUtils.AddSkin(getContext(), bg);

					TextView text = new TextView(getContext());
					text.setTextColor(Color.WHITE);
					text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					text.setText(getContext().getResources().getString(R.string.lightapp06_share_type_upload));
					text.getPaint().setFakeBoldText(true);
					fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.CENTER;
					upload_button.addView(text, fl);
				}
				upload_button.setOnTouchListener(new OnAnimationClickListener()
				{
					@Override
					public void onAnimationClick(View v)
					{
						dlg.dismiss();
						if(callback != null) callback.shareVideoType(true);
					}

					@Override
					public void onTouch(View v)
					{
					}

					@Override
					public void onRelease(View v)
					{
					}
				});
			}
		}
		frame.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return true;
			}
		});

		dlg.setContentView(mainFrame);
		dlg.setCanceledOnTouchOutside(false);
		Window w = dlg.getWindow();
		w.setWindowAnimations(R.style.fullDialog);
		dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialogInterface)
			{
				m_isShareVideoDialogShow = false;
			}
		});
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialogInterface)
			{
				MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_返回);
			}
		});
		dlg.show();
	}

	private void showShareVideoDialog2(int blog_type, final BeautyVideoSharePage.ShareVideoDialogCallback callback)
	{
		final Dialog dlg = new Dialog(getContext(), R.style.notitledialog);
		final DialogView2 view2 = new DialogView2(getContext(), getGlassBackground());
		view2.setInfo(getContext().getResources().getString(R.string.lightapp06_share_type_please) + getShareTypeString(blog_type) + getContext().getResources().getString(R.string.lightapp06_share_type_after) + "\n" + getContext().getResources().getString(R.string.lightapp06_share_type_description1), getShareTypeString(blog_type), new SharePage.DialogListener()
		{
			@Override
			public void onClick(int view)
			{
				if(view == DialogView2.VIEW_BUTTON)
				{
					if(callback != null) callback.shareVideoType(false);
				}
				dlg.dismiss();
			}
		});
		dlg.setContentView(view2);
		dlg.setCanceledOnTouchOutside(false);
		Window w = dlg.getWindow();
		w.setWindowAnimations(R.style.fullDialog);
		dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialogInterface)
			{
				view2.clean();
			}
		});
		dlg.show();
	}

	public Bitmap getGlassBackground()
	{
		if(m_shareDialogBG == null || m_shareDialogBG.isRecycled())
		{
			m_shareDialogBG = SharePage.makeGlassBackground(SharePage.screenCapture(getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight));
		}
		return m_shareDialogBG;
	}

	private String getShareTypeString(int blog_type)
	{
		switch(blog_type)
		{
			case SharePage.WEIXIN:
				return getContext().getResources().getString(R.string.lightapp06_share_type_wexin);

			case SharePage.SINA:
				return getContext().getResources().getString(R.string.lightapp06_share_type_sina);

			case SharePage.QZONE:
				return getContext().getResources().getString(R.string.lightapp06_share_type_qzone);
		}
		return null;
	}

	/**
	 * @return true：动画正在进行中 & 已经关闭 & 视频保存中，false：执行动画
	 */
	public boolean onBack()
	{
		if (m_videoSaving || onAnimation || m_onClose) return true;
		mAdvBar.onStop();
		closeAnimation();
		return false;
	}


	public void onPause()
	{
		if(mAdvBar != null) mAdvBar.onStop();
	}

	public void onResume()
	{
		if(m_uploadComplete)
		{
			m_uploadComplete = false;
			m_videoSaving = false;
			m_shareFrame.setVisibility(View.VISIBLE);
			m_progressFrame.setVisibility(View.GONE);
		}
		if(mAdvBar != null) mAdvBar.onStart();
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(mSina != null)
		{
			mSina.onActivityResult(requestCode, resultCode, data, -1);
		}
		if(mQzone != null)
		{
			mQzone.onActivityResult(requestCode, resultCode, data);
		}
		if(mFacebook != null)
		{
			mFacebook.onActivityResult(requestCode, resultCode, data, 10086);
		}
		return false;
	}

	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		if(mAdvBar != null) mAdvBar.onStart();
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
				if(TextUtils.isEmpty(userInfo.mMobile)) return;
				if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
				{
					sendToCircle(m_savePath, null);
					return;
				}
				sendVideoToCircle(m_savePath, null);
				break;
		}
	}

	public void onClose()
	{
		m_onClose = true;
		if(m_shareBackground != null) m_shareBackground.clearAnimation();
		if(m_topFrame != null) m_topFrame.clearAnimation();
		if(m_topBackground != null) m_topBackground.clearAnimation();
		if(m_weiboScroll != null) m_weiboScroll.clearAnimation();
		if(m_background != null && !m_background.isRecycled())
		{
			m_background.recycle();
			m_background = null;
		}
		if(m_shareDialogBG != null && !m_shareDialogBG.isRecycled())
		{
			m_shareDialogBG.recycle();
			m_shareDialogBG = null;
		}
		if(mSina != null)
		{
			mSina.clear();
			mSina = null;
		}
		if(mQzone != null)
		{
			mQzone.clear();
			mQzone = null;
		}
		m_ShareBmp = null;
		mWeiXin = null;
//        mCircleApi = null;
		if(mFacebook != null)
		{
			mFacebook.clear();
			mFacebook = null;
		}
		if(mTwitter != null)
		{
			mTwitter.clear();
			mTwitter = null;
		}
		if(mAdvBar != null)
		{
			mAdvBar.clean();
			mAdvBar = null;
		}
		if(m_uploadCallback != null)
		{
			m_uploadCallback.Clear();
			m_uploadCallback = null;
		}
		mAdvPostStr = null;
		mActConfigure = null;
		mChannelValue = null;
		mActChannelAdRes = null;
		m_animationCallback = null;
		MyBeautyStat.onPageEndByRes(isVideoSharePage ? R.string.拍照_视频分享页_主页面 : R.string.拍照_萌妆照分享页_主页面);
		System.gc();
	}
}

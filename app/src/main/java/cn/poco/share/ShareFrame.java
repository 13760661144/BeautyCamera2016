package cn.poco.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.business.ChannelValue;
import cn.poco.camera.RotationImg2;
import cn.poco.framework.FileCacheMgr;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.setting.SettingPage;
import cn.poco.statisticlibs.PhotoStat;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.NetState;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

public class ShareFrame extends FrameLayout
{
	private SharePage mParent;                            //父类实例，用于调用父类的方法

//	private String 				mCachePicPath;				//缓存图片路径
	private String mSavedPicPath;                        	//保存图片路径
	private Context mContext;
	private SendBlogPage mSendBlog;                         //poco弹出的发送界面
	private RotationImg2 mOrgInfo;                          //美化传进来的原始数据

	//**************UI布局***************
	private ImageView m_background;							//背景
	private View m_topBackground;							//上层白背景
	private FrameLayout m_topFrame;                         //上层
	private FrameLayout m_bottomFrame;                      //下层做动画层
	private HorizontalScrollView m_weiboScroll;				//微博滚动条
	private ImageView m_homeBtn;                            //返回主页
	private ShareButton m_cameraBtn;                        //拍照
	private ShareButton m_beautifyBtn;                      //美颜美化
	private ImageView m_backBtn;                            //后退
	private ShareButton m_editNextBtn;                      //编辑下一张
//	private ShareButton m_communityBtn;						//分享社区
	private LinearLayout m_weiboFrame;                      //微博按钮层
	private LinearLayout m_buttonFrame;

	private ImageView mImageHolder;                        	//缩略图

	private LinearLayout mIconCommunity;						//在一起
//	private FrameLayout mTogetherTip;						//在一起 提示
	private LinearLayout mIconQQ;                            	//QQ
	private LinearLayout mIconSina;                            //Sina微博
	private LinearLayout mIconQzone;                           //QQ空间
	private LinearLayout mIconFaceBook;                        //Facebook
	private LinearLayout mIconTwitter;                        	//Twitter
	private LinearLayout mIconInstagram;                       //Instagram
	private LinearLayout mIconWeiXin;                        	//微信
	private LinearLayout mIconWXFriends;                       //微信好友圈

	private AdvBannerViewPager mAdvBar;
//	private ShareAdvBar2 mAdvBar;                           //广告条
	private Bitmap mBackground;                        		//背景磨砂玻璃图
	public Bitmap mThumb;                                	//缩略图
	public static ArrayList<TopicItem> mTopics;
	public static boolean gettingTopics = false;

	public static final float ENGLISH_NUM_LENGTH = 7.2f;     //标签中英文、数字字符串标准大小
	public static final float CHINESE_LENGTH = 12;        	//标签中汉字字符串标准大小

	private boolean isActivities = false;                   //是否从广告活动进来
	private String activities_text;                         //广告文本
	protected boolean notSaveMode = false;
	private int mBlogType;
	protected int mFormat = 0;
	private boolean notSendAct = false;
	private boolean sendACT = false;
	private boolean sendActUrl = false;                            //商业为分享链接
	private boolean onAnimation = false;
	public static final String DOWNLOAD_URL = "http://www.adnonstop.com/beauty_camera/share_friend/";        //普通分享结尾附带链接
	public static final String SHARE_DEFAULT_TEXT = "#美人相机#https://www.adnonstop.com/beauty_camera/wap/index.php";

	//显示内置素材商业banner
	protected String mBannerChannelValue;

	//兰蔻定制通道
	private boolean mLancome2 = false;
	//佰草集定制通道
	private boolean mBaicaoji = false;

	//跳简拼
    private boolean mIsFromCamera;

	private long mStartTime;

	public ShareFrame(Context context, SharePage parent)
	{
		super(context);
		mParent = parent;
	}

	protected void notSendActivities(boolean not_send)
	{
		notSendAct = not_send;
	}

    public void setIsFromCamera(boolean fromCamera) {
        mIsFromCamera = fromCamera;
    }

	protected void initialize(Context context)
	{
		this.setVisibility(View.GONE);
		mContext = context;

		//判断是否活动页面;
		if(mParent.mActConfigure != null)
		{
			isActivities = true;
			if(mParent.mActConfigure.mDefaultContent != null)
			{
				activities_text = mParent.mActConfigure.mDefaultContent;
				if(mParent.mActConfigure.mIsShareLink) sendActUrl = true;
			}
			if(mParent.mActChannelAdRes != null && mParent.mActChannelAdRes.mAdId != null)
			{
				if(mParent.mActChannelAdRes.mAdId.equals(ChannelValue.AD83)) mBaicaoji = true;
				else if(mParent.mActChannelAdRes.mAdId.equals(ChannelValue.AD82_1)) mLancome2 = true;
			}
			mBannerChannelValue = null;
		}

//		this.setBackgroundColor(Color.WHITE);

		LinearLayout.LayoutParams ll;
		FrameLayout.LayoutParams fl;

		m_background = new ImageView(context);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		addView(m_background, fl);

		LinearLayout mainFrame = new LinearLayout(context);
		mainFrame.setOrientation(LinearLayout.VERTICAL);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		addView(mainFrame, fl);

		FrameLayout topFrame = new FrameLayout(context);
		ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(825));
		ll.gravity = Gravity.TOP | Gravity.LEFT;
		ll.weight = 0;
		mainFrame.addView(topFrame, ll);

		FrameLayout bottomFrame = new FrameLayout(context);
		ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll.gravity = Gravity.TOP | Gravity.LEFT;
		ll.weight = 1;
		mainFrame.addView(bottomFrame, ll);

		m_topBackground = new View(context);
		//if(LightApp06PageAD79.changeAdvBackground()) m_topBackground.setBackgroundResource(R.drawable.light_app06_ad79_topframe_bg);
		//else
		m_topBackground.setBackgroundColor(Color.WHITE);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		topFrame.addView(m_topBackground, fl);

		m_topFrame = new FrameLayout(context);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		topFrame.addView(m_topFrame, fl);
		{
			m_homeBtn = new ImageView(context);
			m_homeBtn.setScaleType(ScaleType.CENTER_INSIDE);
			m_homeBtn.setImageResource(R.drawable.share_top_home_normal);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.RIGHT | Gravity.TOP;
			fl.rightMargin = ShareData.PxToDpi_xhdpi(9);
			fl.topMargin = ShareData.PxToDpi_xhdpi(4);
			m_topFrame.addView(m_homeBtn, fl);
			ImageUtils.AddSkin(getContext(), m_homeBtn);
			m_homeBtn.setOnTouchListener(mBtnListener);

			m_backBtn = new ImageView(context);
			m_backBtn.setScaleType(ScaleType.CENTER_INSIDE);
			m_backBtn.setImageResource(R.drawable.framework_back_btn);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.TOP;
			fl.topMargin = ShareData.PxToDpi_xhdpi(5);
			fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
			m_topFrame.addView(m_backBtn, fl);
			ImageUtils.AddSkin(getContext(), m_backBtn);
			m_backBtn.setOnTouchListener(mBtnListener);

			TextView save_text = new TextView(context);
			save_text.setText(mContext.getResources().getString(R.string.share_ui_top_title));
			save_text.setTextColor(0xff333333);
			save_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = ShareData.PxToDpi_xhdpi(25);
			m_topFrame.addView(save_text, fl);
			if(notSaveMode) save_text.setVisibility(View.INVISIBLE);

			LinearLayout imageFrame = new LinearLayout(getContext());
			imageFrame.setOrientation(LinearLayout.HORIZONTAL);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			fl.topMargin = ShareData.PxToDpi_xhdpi(197);
			m_topFrame.addView(imageFrame, fl);

			mImageHolder = new ImageView(context);
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

			if(!isActivities && !notSaveMode)
			{
				switch(mFormat)
				{
					case 0:
//						m_communityBtn = new ShareButton(getContext());
//						m_communityBtn.init(R.drawable.share_button_community_normal, mContext.getResources().getString(R.string.share_icon_share_community), new OnAnimationClickListener()
//						{
//							@Override
//							public void onAnimationClick(View v) {
//								if(onAnimation) return;
//
//								TongJi2.AddCountByRes(getContext(), R.integer.分享到在一起);
//								if(!NetState.IsConnectNet(mContext))
//								{
//									Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
//									return;
//								}
//								if(mParent.mActConfigure != null) Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);
//
//								if(sendActUrl)
//								{
//									mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.CIRCLE);
//								}
//								else
//								{
//									String act_text = null;
//									if(mParent.mActConfigure != null && mParent.mActConfigure.mDefaultContent != null) act_text = mParent.mActConfigure.mDefaultContent;
//									mParent.setContentAndPic(act_text, getShareSavePath());
//									mParent.startSendSdkClient(SharePage.CIRCLE);
//									sendToActivities(activities_text, getShareSavePath(), SharePage.CIRCLE);
//								}
//							}
//
//							@Override
//							public void onTouch(View v)
//							{
//							}
//
//							@Override
//							public void onRelease(View v)
//							{
//							}
//						});
//						ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
//						ll.gravity = Gravity.TOP | Gravity.LEFT;
//						m_buttonFrame.addView(m_communityBtn, ll);

						m_cameraBtn = new ShareButton(getContext());
						m_cameraBtn.init(R.drawable.share_button_camera_normal, mContext.getResources().getString(R.string.share_icon_camera), new OnAnimationClickListener()
						{
							@Override
							public void onAnimationClick(View v) {
								if(onAnimation) return;
								if (mIsFromCamera) {
									//MyBeautyStat.onClickByRes(R.string.拍照_拍照保存页_主页面_继续拍照);
									TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_继续拍照);
								}
								mParent.m_site.OnCamera();
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
						ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
						ll.gravity = Gravity.TOP | Gravity.LEFT;
//						ll.topMargin = ShareData.PxToDpi_xhdpi(36);
						m_buttonFrame.addView(m_cameraBtn, ll);

						m_beautifyBtn = new ShareButton(getContext());
						m_beautifyBtn.init(R.drawable.share_button_beautify_normal, mContext.getResources().getString(R.string.share_icon_beautify), new OnAnimationClickListener()
						{
							@Override
							public void onAnimationClick(View v) {
								if(onAnimation) return;
								if (mIsFromCamera) {
									//MyBeautyStat.onClickByRes(R.string.拍照_拍照保存页_主页面_美颜美图);
									TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_美颜美化);
								}
								HashMap<String, Object> params = new HashMap<String, Object>();
								if(mOrgInfo != null) params.put("img", new RotationImg2[]{mOrgInfo});
								else
								{
									params.put("img", new RotationImg2[]{Utils.Path2ImgObj(getShareSavePath())});
								}
								mParent.m_site.OnBeautyFace(params);
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
						ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
						ll.gravity = Gravity.TOP | Gravity.LEFT;
						ll.topMargin = ShareData.PxToDpi_xhdpi(36);
						m_buttonFrame.addView(m_beautifyBtn, ll);
						break;

					default:
//						m_communityBtn = new ShareButton(getContext());
//						m_communityBtn.init(R.drawable.share_button_community_normal, mContext.getResources().getString(R.string.share_icon_share_community), new OnAnimationClickListener()
//						{
//							@Override
//							public void onAnimationClick(View v) {
//								if(onAnimation) return;
//
//								TongJi2.AddCountByRes(getContext(), R.integer.分享到在一起);
//								if(!NetState.IsConnectNet(mContext))
//								{
//									Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
//									return;
//								}
//								if(mParent.mActConfigure != null) Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);
//
//								if(sendActUrl)
//								{
//									mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.CIRCLE);
//								}
//								else
//								{
//									String act_text = null;
//									if(mParent.mActConfigure != null && mParent.mActConfigure.mDefaultContent != null) act_text = mParent.mActConfigure.mDefaultContent;
//									mParent.setContentAndPic(act_text, getShareSavePath());
//									mParent.startSendSdkClient(SharePage.CIRCLE);
//									sendToActivities(activities_text, getShareSavePath(), SharePage.CIRCLE);
//								}
//							}
//
//							@Override
//							public void onTouch(View v)
//							{
//							}
//
//							@Override
//							public void onRelease(View v)
//							{
//							}
//						});
//						ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
//						ll.gravity = Gravity.TOP | Gravity.LEFT;
//						m_buttonFrame.addView(m_communityBtn, ll);

						m_editNextBtn = new ShareButton(getContext());
						m_editNextBtn.init(R.drawable.share_button_beautify_normal, mContext.getResources().getString(R.string.share_icon_edit_next), new OnAnimationClickListener()
						{
							@Override
							public void onAnimationClick(View v) {
								if(onAnimation) return;
								if (!mIsFromCamera) {
									TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_编辑下一张);
									MyBeautyStat.onClickByRes(R.string.美颜美图_保存页_主页面_编辑下一张);
								}
								mParent.m_site.OnBeautyFaceNext();
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
						ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
						ll.gravity = Gravity.TOP | Gravity.LEFT;
//						ll.topMargin = ShareData.PxToDpi_xhdpi(36);
						m_buttonFrame.addView(m_editNextBtn, ll);
						break;
				}
			}
//			else if(notSaveMode)
//			{
//				m_communityBtn = new ShareButton(getContext());
//				m_communityBtn.init(R.drawable.share_button_community_normal, mContext.getResources().getString(R.string.share_icon_share_community), new OnAnimationClickListener()
//				{
//					@Override
//					public void onAnimationClick(View v) {
//						if(onAnimation) return;
//
//						TongJi2.AddCountByRes(getContext(), R.integer.分享到在一起);
//						if(!NetState.IsConnectNet(mContext))
//						{
//							Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
//							return;
//						}
//						if(mParent.mActConfigure != null) Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);
//
//						if(sendActUrl)
//						{
//							mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.CIRCLE);
//						}
//						else
//						{
//							String act_text = null;
//							if(mParent.mActConfigure != null && mParent.mActConfigure.mDefaultContent != null) act_text = mParent.mActConfigure.mDefaultContent;
//							mParent.setContentAndPic(act_text, getShareSavePath());
//							mParent.startSendSdkClient(SharePage.CIRCLE);
//							sendToActivities(activities_text, getShareSavePath(), SharePage.CIRCLE);
//						}
//					}
//
//					@Override
//					public void onTouch(View v)
//					{
//					}
//
//					@Override
//					public void onRelease(View v)
//					{
//					}
//				});
//				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
//				ll.gravity = Gravity.TOP | Gravity.LEFT;
//				m_buttonFrame.addView(m_communityBtn, ll);
//			}
			else ((LinearLayout.LayoutParams)mImageHolder.getLayoutParams()).gravity = Gravity.CENTER;


			m_bottomFrame = new FrameLayout(context);
			fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(228));
			fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
			m_topFrame.addView(m_bottomFrame, fl);
			{
				LinearLayout share_text = new LinearLayout(context);
				share_text.setOrientation(LinearLayout.HORIZONTAL);
				fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.TOP | Gravity.LEFT;
				fl.leftMargin = ShareData.PxToDpi_xhdpi(34);
				m_bottomFrame.addView(share_text, fl);
				{
					TextView text = new TextView(context);
					text.setText(mContext.getResources().getString(R.string.share_ui_bottom_title));
					text.setTextColor(0x99000000);
					text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.TOP | Gravity.LEFT;
					share_text.addView(text, ll);

					ImageView arrow = new ImageView(context);
					arrow.setImageResource(R.drawable.share_text_arrow);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
					share_text.addView(arrow, ll);

					ImageView line = new ImageView(context);
					line.setBackgroundColor(0x19000000);
					ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(310), 1);
					ll.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(6);
					share_text.addView(line, ll);
				}

				m_weiboScroll = new HorizontalScrollView(context);
				m_weiboScroll.setHorizontalScrollBarEnabled(false);
				fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
				fl.bottomMargin = ShareData.PxToDpi_xhdpi(34);
				m_bottomFrame.addView(m_weiboScroll, fl);
				{
					m_weiboFrame = new LinearLayout(context);
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
						icon.setScaleType(ScaleType.CENTER_INSIDE);
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
						icon.setScaleType(ScaleType.CENTER_INSIDE);
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

					//绑定微信朋友圈
					mIconWXFriends = new LinearLayout(getContext());
					mIconWXFriends.setOrientation(LinearLayout.VERTICAL);
					mIconWXFriends.setOnClickListener(mClickListener);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.TOP | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
					m_weiboFrame.addView(mIconWXFriends, ll);
					{
						ImageView icon = new ImageView(getContext());
						icon.setImageResource(R.drawable.share_weibo_wechat_friend_normal);
						icon.setScaleType(ScaleType.CENTER_INSIDE);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
						mIconWXFriends.addView(icon, ll);

						TextView name = new TextView(getContext());
						name.setText(getResources().getString(R.string.friends_circle));
						name.setAlpha(0.8f);
						name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
						ll.topMargin = ShareData.PxToDpi_xhdpi(10);
						mIconWXFriends.addView(name, ll);
					}

					//绑定QQ
					mIconQQ = new LinearLayout(getContext());
					mIconQQ.setOrientation(LinearLayout.VERTICAL);
					mIconQQ.setOnClickListener(mClickListener);
					mIconQQ.setOnLongClickListener(mLongClickListener);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.TOP | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
					m_weiboFrame.addView(mIconQQ, ll);
					{
						ImageView icon = new ImageView(getContext());
						icon.setImageResource(R.drawable.share_weibo_qq_normal);
						icon.setScaleType(ScaleType.CENTER_INSIDE);
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
					mIconQzone.setOnLongClickListener(mLongClickListener);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.TOP | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
					m_weiboFrame.addView(mIconQzone, ll);
					{
						ImageView icon = new ImageView(getContext());
						icon.setImageResource(R.drawable.share_weibo_qzone_normal);
						icon.setScaleType(ScaleType.CENTER_INSIDE);
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
					mIconSina.setOnLongClickListener(mLongClickListener);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.TOP | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
					m_weiboFrame.addView(mIconSina, ll);
					{
						ImageView icon = new ImageView(getContext());
						icon.setImageResource(R.drawable.share_weibo_sina_normal);
						icon.setScaleType(ScaleType.CENTER_INSIDE);
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

					//绑定FaceBook
					mIconFaceBook = new LinearLayout(getContext());
					mIconFaceBook.setOrientation(LinearLayout.VERTICAL);
					mIconFaceBook.setOnClickListener(mClickListener);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.TOP | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
					m_weiboFrame.addView(mIconFaceBook, ll);
					{
						ImageView icon = new ImageView(getContext());
						icon.setImageResource(R.drawable.share_weibo_facebook_normal);
						icon.setScaleType(ScaleType.CENTER_INSIDE);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
						mIconFaceBook.addView(icon, ll);

						TextView name = new TextView(getContext());
						name.setText(getResources().getString(R.string.Facebook));
						name.setAlpha(0.8f);
						name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
						ll.topMargin = ShareData.PxToDpi_xhdpi(10);
						mIconFaceBook.addView(name, ll);
					}

					//绑定Instagram
					mIconInstagram = new LinearLayout(getContext());
					mIconInstagram.setOrientation(LinearLayout.VERTICAL);
					mIconInstagram.setOnClickListener(mClickListener);
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.TOP | Gravity.LEFT;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(36);
					m_weiboFrame.addView(mIconInstagram, ll);
					{
						ImageView icon = new ImageView(getContext());
						icon.setImageResource(R.drawable.share_weibo_instagarm_normal);
						icon.setScaleType(ScaleType.CENTER_INSIDE);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
						mIconInstagram.addView(icon, ll);

						TextView name = new TextView(getContext());
						name.setText(getResources().getString(R.string.Instagram));
						name.setAlpha(0.8f);
						name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
						ll.topMargin = ShareData.PxToDpi_xhdpi(10);
						mIconInstagram.addView(name, ll);
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
						icon.setScaleType(ScaleType.CENTER_INSIDE);
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
//				m_weiboScroll.setOnTouchListener(new OnTouchListener()
//				{
//					@Override
//					public boolean onTouch(View view, MotionEvent motionEvent)
//					{
//						removeTogetherTip();
//						return false;
//					}
//				});
			}

//			if(TagMgr.CheckTag(getContext(), Tags.SHARE_TOGETHER_TIP_FLAG))
//			{
//				mTogetherTip = new FrameLayout(getContext());
//				mTogetherTip.setBackgroundResource(R.drawable.share_circle_tip);
//				fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
//				fl.bottomMargin = ShareData.PxToDpi_xhdpi(136);
//				fl.leftMargin =  -ShareData.PxToDpi_xhdpi(6);
//				m_topFrame.addView(mTogetherTip, fl);
//				{
//					TextView text = new TextView(getContext());
//					text.setText(mContext.getResources().getString(R.string.share_together_tip_text));
//					int color = ImageUtils.GetSkinColor();
//					if(color != 0) text.setTextColor(color);
//					else text.setTextColor(0xffe75988);
//					text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
//					fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//					fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//					fl.topMargin = ShareData.PxToDpi_xhdpi(34);
//					mTogetherTip.addView(text, fl);
//				}
//				mTogetherTip.setVisibility(View.GONE);
//				mTogetherTip.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View view)
//					{
//						removeTogetherTip();
//					}
//				});
//			}
		}

		if(mBaicaoji) mAdvBar = new AdvBannerViewPager(context, mParent.m_site.m_cmdProc, R.drawable.share_advertising_banner_ad83, "http://cav.adnonstop.com/cav/c11475f7ee/0072703162/?url=http://clickc.admaster.com.cn/c/a96159,b2025429,c3064,i0,m101,8a2,8b3,h");
		else if(mLancome2) mAdvBar = new AdvBannerViewPager(context, mParent.m_site.m_cmdProc, R.drawable.share_advertising_banner_ad82, "http://cav.adnonstop.com/cav/76534134f4/0073003162/?url=https://equity-vip.tmall.com/agent/mobile.htm?agentId=58242&_bind=true");
		else mAdvBar = getBussinessBanner(mBannerChannelValue);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		bottomFrame.addView(mAdvBar, fl);
		if(mParent.mHideBanner) mAdvBar.setVisibility(View.GONE);
		else mAdvBar.autoPage();
	}

	private AdvBannerViewPager getBussinessBanner(String channel_value)
	{
		if(channel_value == null || channel_value.length() <= 0) return new AdvBannerViewPager(getContext(), mParent.m_site.m_cmdProc, AdvBannerViewPager.PAGE_SHARE);
		//if(channel_value.equals(ChannelValue.AD78_2)) return new AdvBannerViewPager(getContext(), mParent.m_site.m_cmdProc, R.drawable.share_advertising_banner_ad78_2, "http://cav.adnonstop.com/cav/ecae381794/0070902161/?url=http://www.maccosmetics.com.cn/collections-liptensity");
		//else if(channel_value.equals(ChannelValue.AD81_1)) return new AdvBannerViewPager(getContext(), mParent.m_site.m_cmdProc, R.drawable.share_advertising_banner_ad81, "http://cav.adnonstop.com/cav/76534134f4/0070203008/?url=https://equity-vip.tmall.com/agent/mobile.htm?agentId=53584&_bind=true");
		//else if(channel_value.equals(ChannelValue.AD81_2)) return new AdvBannerViewPager(getContext(), mParent.m_site.m_cmdProc, R.drawable.share_advertising_banner_ad81, "http://cav.adnonstop.com/cav/76534134f4/0070203162/?url=https://equity-vip.tmall.com/agent/mobile.htm?agentId=53584&_bind=true");
		return new AdvBannerViewPager(getContext(), mParent.m_site.m_cmdProc, AdvBannerViewPager.PAGE_SHARE);
	}

//	private void togetherTipAnime()
//	{
//		if(mTogetherTip == null) return;
//		ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.406f, Animation.RELATIVE_TO_SELF, 1f);
//		scale.setDuration(300);
//		scale.setInterpolator(new OvershootInterpolator());
//		scale.setAnimationListener(new Animation.AnimationListener()
//		{
//			@Override
//			public void onAnimationStart(Animation animation){}
//
//			@Override
//			public void onAnimationEnd(Animation animation)
//			{
//				if(mTogetherTip != null) mTogetherTip.clearAnimation();
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation){}
//		});
//		mTogetherTip.setVisibility(View.VISIBLE);
//		mTogetherTip.startAnimation(scale);
//	}

//	private void removeTogetherTip()
//	{
//		if(mTogetherTip == null) return;
//		TagMgr.SetTag(getContext(), Tags.SHARE_TOGETHER_TIP_FLAG);
//		mTogetherTip.clearAnimation();
//		m_topFrame.removeView(mTogetherTip);
//		mTogetherTip = null;
//		System.gc();
//	}

	//打开系统软键盘
	protected void showSoftKeyboard(final EditText editText)
	{
		InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, 0);
	}

	//返回入口
	public void setImage(RotationImg2 rotationImg)
	{
		if(rotationImg == null || rotationImg.m_img == null)
		{
			SharePage.msgBox(mContext, mContext.getResources().getString(R.string.share_pic_error));
			return;
		}
		mOrgInfo = rotationImg;
		Bitmap bmp = MakeBmpV2.CreateBitmapV2(cn.poco.imagecore.Utils.DecodeImage(mContext, ((RotationImg2) mOrgInfo).m_img, ((RotationImg2) mOrgInfo).m_degree, -1, -1, -1), ((RotationImg2) mOrgInfo).m_degree, ((RotationImg2) mOrgInfo).m_flip, -1, -1, -1, Bitmap.Config.ARGB_8888);
		if(bmp != null && !bmp.isRecycled())
		{
			SettingInfo info = SettingInfoMgr.GetSettingInfo(getContext());
			if(info == null) return;
			PhotoStat.Stat(getContext(), bmp, CommonUtils.GetAppVer(getContext()), info.GetPoco2Id(true));
			Bitmap thumb = createThumb(bmp);
			if(thumb != null) mImageHolder.setImageBitmap(thumb);
			setBackground(bmp);
		}
		mStartTime = System.currentTimeMillis();

		ShareFrame.this.post(new Runnable()
		{
			@Override
			public void run()
			{
				onAnimation = true;
				ShareFrame.this.setVisibility(View.VISIBLE);
				UIanime(m_background, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation){}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						if(m_background != null) m_background.clearAnimation();
						if(m_topFrame != null) m_topFrame.clearAnimation();
						if(m_topBackground != null) m_topBackground.clearAnimation();
						if(m_weiboScroll != null) m_weiboScroll.clearAnimation();
						if(mAdvBar != null) mAdvBar.clearAnimation();
//						togetherTipAnime();
						onAnimation = false;
					}

					@Override
					public void onAnimationRepeat(Animation animation){}
				});
			}
		});
	}

	public String getShareSavePath()
	{
		if(mOrgInfo == null) return null;
		if(mSavedPicPath == null || mSavedPicPath.length() <= 0)
		{
			if(((RotationImg2) mOrgInfo).m_img != null && ((RotationImg2) mOrgInfo).m_img instanceof String) mSavedPicPath = (String)((RotationImg2) mOrgInfo).m_img;
			else
			{
				Bitmap bmp = MakeBmpV2.CreateBitmapV2(cn.poco.imagecore.Utils.DecodeImage(mContext, ((RotationImg2)mOrgInfo).m_img, ((RotationImg2)mOrgInfo).m_degree, -1, -1, -1), ((RotationImg2)mOrgInfo).m_degree, ((RotationImg2)mOrgInfo).m_flip, -1, -1, -1, Bitmap.Config.ARGB_8888);
				mSavedPicPath = Utils.SaveImg(mContext, bmp, FileCacheMgr.GetLinePath(), 100, false);
			}
		}
		return mSavedPicPath;
	}

	public static void UIanime(final View background, final View topFrame, final View topBackground, final View banner, final Animation.AnimationListener listener)
	{
		TranslateAnimation translate = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1,
				Animation.RELATIVE_TO_SELF, 0);
		translate.setDuration(360);
		translate.setAnimationListener(listener);
//		translate.setAnimationListener(new Animation.AnimationListener()
//		{
//			@Override
//			public void onAnimationStart(Animation animation){}
//
//			@Override
//			public void onAnimationEnd(Animation animation)
//			{
//				background.clearAnimation();
//				topBackground.clearAnimation();
//				TranslateAnimation translate2 = new TranslateAnimation(0, 0, 0, ShareData.PxToDpi_xhdpi(4));
//				translate2.setDuration(300);
//				translate2.setInterpolator(new CycleInterpolator(1));
//				translate2.setAnimationListener(listener);
//				weiboFrame.startAnimation(translate2);
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation){}
//		});

		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(360);

		if(background != null) background.startAnimation(alpha);
		if(banner != null) banner.startAnimation(alpha);
		if(topBackground != null) topBackground.startAnimation(translate);
		if(topFrame != null) topFrame.startAnimation(translate);
	}

	public static void UIanime2(final View background, final View topFrame, final View topBackground, final View banner, final Animation.AnimationListener listener)
	{
		TranslateAnimation translate = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1);
		translate.setStartOffset(500);
		translate.setDuration(360);
		translate.setAnimationListener(listener);

		AlphaAnimation alpha = new AlphaAnimation(1, 0);
		alpha.setStartOffset(500);
		alpha.setDuration(360);

		if(background != null) background.startAnimation(alpha);
		if(banner != null) banner.startAnimation(alpha);
		if(topBackground != null) topBackground.startAnimation(translate);
		if(topFrame != null) topFrame.startAnimation(translate);
	}

	protected OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(onAnimation) return;

            if (v == mImageHolder) {
                if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_点击照片查看);
					//MyBeautyStat.onClickByRes(R.string.拍照_拍照保存页_主页面_查看预览图);
                }
				if(mParent != null && mParent.m_site != null)
					mParent.m_site.OnPreview(getShareSavePath());
			}
			else if(v == mIconSina)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到新浪);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到新浪);
                }
				mBlogType = SharePage.SINA;
				if(!SettingPage.checkSinaBindingStatus(mContext))
				{
					mParent.bindSina(new SharePage.BindCompleteListener()
					{
						@Override
						public void success()
						{
//							showSendBlogPage();
							String act_text = SHARE_DEFAULT_TEXT;
							if(mParent.mActConfigure != null)
							{
								Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);
								if(mParent.mActConfigure.mDefaultContent != null) act_text = mParent.mActConfigure.mDefaultContent;
							}

							if(sendActUrl)
							{
								mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.SINA);
							}
							else
							{
								mParent.setContentAndPic(act_text, getShareSavePath());
								mParent.sendToSina();
								sendToActivities(act_text, getShareSavePath(), SharePage.SINA);
							}
						}

						@Override
						public void fail()
						{
						}
					});
				}
				else
				{
//					showSendBlogPage();
					String act_text = SHARE_DEFAULT_TEXT;
					if(mParent.mActConfigure != null)
					{
						Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);
						if(mParent.mActConfigure.mDefaultContent != null) act_text = mParent.mActConfigure.mDefaultContent;
					}

					if(sendActUrl)
					{
						mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.SINA);
					}
					else
					{
						mParent.setContentAndPic(act_text, getShareSavePath());
						mParent.sendToSina();
						sendToActivities(act_text, getShareSavePath(), SharePage.SINA);
					}
				}
			}
			else if(v == mIconQzone)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到QQ空间);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到QQ空间);
                }
				mBlogType = SharePage.QZONE;
				String qzone_content;
				if(mParent.mActConfigure != null)
				{
					Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);
					qzone_content = mParent.mActConfigure.mDefaultContent;
				}
				else
					qzone_content = SHARE_DEFAULT_TEXT;
				if(sendActUrl)
				{
					mParent.shareActivitiesUrl(getShareSavePath(), qzone_content, SharePage.QZONE);
				}
				else
				{
					mParent.setContentAndPic(qzone_content, getShareSavePath());
					mParent.startSendSdkClient(SharePage.QZONE);
					sendToActivities(qzone_content, getShareSavePath(), SharePage.QZONE);
				}
			}
			else if(v == mIconWeiXin)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到微信好友);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到微信好友);
                }
				if(mParent.mActConfigure != null)
					Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);

				if(mParent.registerWeiXin(SharePage.STATUS_WX_NORMAL))
				{
					if(sendActUrl)
					{
						mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.WEIXIN);
					}
					else
					{
						mParent.setContentAndPic(null, getShareSavePath());
						mParent.startSendSdkClient(SharePage.WEIXIN);
						sendToActivities(activities_text, getShareSavePath(), SharePage.WEIXIN);
					}
				}
			}
			else if(v == mIconWXFriends)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到朋友圈);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到朋友圈);
                }
				if(mParent.mActConfigure != null)
					Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);

				if(mParent.registerWeiXin(SharePage.STATUS_WX_FRIENDS))
				{
					if(sendActUrl)
					{
						mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.WXFRIENDS);
					}
					else
					{
						mParent.setContentAndPic(null, getShareSavePath());
						mParent.startSendSdkClient(SharePage.WXFRIENDS);
						sendToActivities(activities_text, getShareSavePath(), SharePage.WXFRIENDS);
					}
				}
			}
			else if(v == mIconQQ)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到QQ好友);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到QQ好友);
                }
				mBlogType = SharePage.QQ;
				String qq_content = null;
				if(mParent.mActConfigure != null)
				{
					Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);
					qq_content = mParent.mActConfigure.mDefaultContent;
				}
				else qq_content = "图片";
				if(sendActUrl)
				{
					mParent.shareActivitiesUrl(getShareSavePath(), qq_content, SharePage.QQ);
				}
				else
				{
					mParent.setContentAndPic(qq_content, getShareSavePath());
					mParent.startSendSdkClient(SharePage.QQ);
					sendToActivities(qq_content, getShareSavePath(), SharePage.QQ);
				}
			}
			else if(v == mIconFaceBook)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到facebook);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到facebook);
                }
				if(mParent.mActConfigure != null)
					Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);

				if(sendActUrl)
				{
					mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.FACEBOOK);
				}
				else
				{
					mParent.setContentAndPic(null, getShareSavePath());
					mParent.startSendSdkClient(SharePage.FACEBOOK);
					sendToActivities(activities_text, getShareSavePath(), SharePage.FACEBOOK);
				}
			}
			else if(v == mIconTwitter)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到twitter);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到twitter);
                }
				if(mParent.mActConfigure != null)
					Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);

				if(sendActUrl)
				{
					mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.TWITTER);
				}
				else
				{
					mParent.setContentAndPic(null, getShareSavePath());
					mParent.startSendSdkClient(SharePage.TWITTER);
					sendToActivities(activities_text, getShareSavePath(), SharePage.TWITTER);
				}
			}
			else if(v == mIconInstagram)
			{
//				removeTogetherTip();
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if (mIsFromCamera) {
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_分享到ins);
                } else {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_分享到ins);
                }
				if(mParent.mActConfigure != null)
					Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);

				if(sendActUrl)
				{
					mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.INSTAGRAM);
				}
				else
				{
					mParent.setContentAndPic(null, getShareSavePath());
					mParent.startSendSdkClient(SharePage.INSTAGRAM);
					sendToActivities(activities_text, getShareSavePath(), SharePage.INSTAGRAM);
				}
			}
			else if(v == mIconCommunity)
			{
				TongJi2.AddCountByRes(getContext(), R.integer.分享到在一起);
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}
				if(mParent.mActConfigure != null) Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);

				if(sendActUrl)
				{
					mParent.shareActivitiesUrl(getShareSavePath(), mParent.mActConfigure.mDefaultContent, SharePage.CIRCLE);
				}
				else
				{
					String act_text = null;
					if(mParent.mActConfigure != null && mParent.mActConfigure.mDefaultContent != null) act_text = mParent.mActConfigure.mDefaultContent;
					mParent.setContentAndPic(act_text, getShareSavePath());
					mParent.startSendSdkClient(SharePage.CIRCLE);
					sendToActivities(activities_text, getShareSavePath(), SharePage.CIRCLE);
				}
			}
//			else if(v == m_cameraBtn)
//			{
//				TongJi2.AddCountByRes(getContext(), R.integer.分享_拍照);
//				mParent.m_site.OnCamera();
//			}
//			else if(v == m_beautifyBtn)
//			{
//				TongJi2.AddCountByRes(getContext(), R.integer.分享_美颜);
//
//				HashMap<String, Object> params = new HashMap<String, Object>();
//				if(mOrgInfo != null) params.put("img", new RotationImg2[]{mOrgInfo});
//				else
//				{
//					RotationImg2 img = new RotationImg2();
//					img.m_img = getShareSavePath();
//					params.put("img", new RotationImg2[]{img});
//				}
//				mParent.m_site.OnBeautyFace(params);
//			}
//			else if(v == m_editNextBtn)
//			{
//				TongJi2.AddCountByRes(getContext(), R.integer.保存页_美颜下一张);
//
//				mParent.m_site.OnBeautyFaceNext();
//			}
//			else if(v == mIconShrink)
//			{
//				changeWeiboFrame(false, true);
//			}
		}
	};

	/**
	 * 所有按钮的长按事件;
	 */
	protected OnLongClickListener mLongClickListener = new OnLongClickListener()
	{
		@Override
		public boolean onLongClick(View v)
		{
			if(onAnimation) return false;

			if(v == mIconSina)
			{
				if(SettingPage.checkSinaBindingStatus(mContext))
				{
					AlertDialog alert = new AlertDialog.Builder(mContext).create();
					alert.setTitle(mContext.getResources().getString(R.string.confirm_title));
					alert.setMessage(mContext.getResources().getString(R.string.share_sina_cancel_bind));
					alert.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							SettingPage.clearSinaConfigure(mContext);
						}
					});
					alert.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.no), (DialogInterface.OnClickListener)null);
					alert.show();
				}
			}
			else if(v == mIconQzone)
			{
				if(SettingPage.checkQzoneBindingStatus(mContext))
				{
					AlertDialog alert = new AlertDialog.Builder(mContext).create();
					alert.setTitle(mContext.getResources().getString(R.string.confirm_title));
					alert.setMessage(mContext.getResources().getString(R.string.share_qq_cancel_bind));
					alert.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							SettingPage.clearQzoneConfigure(mContext);
						}
					});
					alert.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.no), (DialogInterface.OnClickListener)null);
					alert.show();
				}
			}
			return false;
		}
	};

	protected OnAnimationClickListener mBtnListener = new OnAnimationClickListener()
	{
		@Override
		public void onAnimationClick(View v)
		{
			if(onAnimation) return;

            if (v == m_homeBtn) {
				long interval = System.currentTimeMillis() - mStartTime;
				if (interval > 300) {
					if (mIsFromCamera) {
						//MyBeautyStat.onClickByRes(R.string.拍照_拍照保存页_主页面_回到首页);
						TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_返回首页);
					} else {
						MyBeautyStat.onClickByRes(R.string.美颜美图_保存页_主页面_回到首页);
						TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_返回首页);
					}
					mParent.m_site.OnHome();
				}
            } else if (v == m_backBtn) {
                if (mIsFromCamera) {
					//MyBeautyStat.onClickByRes(R.string.拍照_拍照保存页_主页面_返回上一级);
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_预览_保存_返回上一级);
                } else
				{
					MyBeautyStat.onClickByRes(R.string.美颜美图_保存页_主页面_返回);
					TongJi2.AddCountByRes(getContext(), R.integer.修图_保存_返回);
				}
				//商业需要
				/*if(mParent.mActConfigure != null && !mParent.mOnBack &&
						mParent.mActConfigure.m_channelValue != null &&
						mParent.mActConfigure.m_channelValue.equals("miaocuijiao_201508"))
				{
					mParent.mOnBack = true;
					PocoCamera.main.onBackPressed();
					PocoCamera.main.onBackPressed();
					return;
				}
				PocoCamera.main.onBackPressed();*/
				mParent.m_site.OnBack();
				outPageAnime();
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

	/*private PocoEntry.Callback m_janeEntryCallback = new PocoEntry.Callback()
	{
		@Override
		public void OnClose() {}

		@Override
		public void OnBtn()
		{
			switch(JaneEntry.GetAppState(getContext()))
			{
			case JaneEntry.DOWNLOAD:
			case JaneEntry.UPDATE:
				if(!NetState.IsConnectNet(mContext))
				{
					Toast.makeText(mContext, mContext.getResources().getText(R.string.net_weak_tip), Toast.LENGTH_LONG).show();
					return;
				}

				CommonUtils.OpenBrowser(getContext(), "http://world.poco.cn/app/jane/share.php");
				break;
			case JaneEntry.SUCCESS:
				String[] imgs = new String[]{getShareSavePath()};
				int[] ds = new int[]{CommonUtils.GetImgInfo(getShareSavePath())[0]};

				try
				{
					Intent intent = new Intent();
					intent.setAction("cn.poco.jane.puzzle");
					intent.putExtra("imgs", imgs);
					intent.putExtra("rotations", ds);
					intent.putExtra("package", getContext().getApplicationContext().getPackageName());
					((Activity)getContext()).startActivity(intent);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	};*/
	
	/*public boolean isJaneOpen()
	{
		if(m_janeEntry != null && m_janeEntry.IsShow())
		{
			m_janeEntry.OnCancel();
			return true;
		}
		return false;
	}*/

	public void outPageAnime()
	{
		onAnimation = true;
		UIanime2(m_background, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
		{
			@Override
			public void onAnimationStart(Animation animation){}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				setVisibility(View.GONE);
				onAnimation = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation){}
		});
	}

	public void clear()
	{
		onAnimation = false;
//		removeTogetherTip();
		if(m_background != null) m_background.clearAnimation();
		if(m_topFrame != null) m_topFrame.clearAnimation();
		if(m_topBackground != null) m_topBackground.clearAnimation();
		if(m_weiboScroll != null) m_weiboScroll.clearAnimation();
		if(mAdvBar != null)
		{
			mAdvBar.clearAnimation();
			mAdvBar.clean();
			mAdvBar = null;
		}
		if(mThumb != null && !mThumb.isRecycled())
		{
			mThumb.recycle();
			mThumb = null;
		}
		mOrgInfo = null;
		if(mBackground != null && !mBackground.isRecycled())
		{
			mBackground.recycle();
			mBackground = null;
		}
//		if(mTogetherTip != null)
//		{
//			mTogetherTip.clearAnimation();
//		}
		closeSendBlogPage();
		removeAllViews();
//				addCachePic();
		hideKeyboard();
	}

	/**
	 * 隐藏键盘
	 */
	public void hideKeyboard()
	{
		this.post(new Runnable()
		{
			@Override
			public void run()
			{
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		});
	}

	/**
	 * 制作缩略图;
	 *
	 * @param path 图片地址
	 */
	private Bitmap createThumb(String path)
	{
		float scale = 1;
//		if(isActivities) scale = 1.5f;
		int thumb_w = (int)(ShareData.PxToDpi_xhdpi(304) * scale);
		int thumb_h = (int)(ShareData.PxToDpi_xhdpi(304) * scale);
		int rotate = CommonUtils.GetImgInfo(path)[0];
		Bitmap thumb = MakeBmp.CreateFixBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), path, rotate, -1, thumb_w, thumb_h), thumb_w, thumb_h, MakeBmp.POS_CENTER, rotate, Config.ARGB_8888);
		if(thumb == null || thumb.isRecycled()) return null;
		mThumb = makeCircle(thumb);
		thumb.recycle();
		System.gc();
		if(mThumb == null || mThumb.isRecycled()) return null;
		int w = mThumb.getWidth();
		int h = mThumb.getHeight();
		thumb = Bitmap.createBitmap((int)(w + ShareData.PxToDpi_xhdpi(32) * scale), (int)(h + ShareData.PxToDpi_xhdpi(32) * scale), Config.ARGB_8888);
		Canvas canvas = new Canvas(thumb);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(0x40ffffff);
		canvas.drawCircle((float)thumb.getWidth() / 2f, (float)thumb.getHeight() / 2f, thumb.getWidth() / 2, paint);
		canvas.drawBitmap(mThumb, ShareData.PxToDpi_xhdpi(16) * scale, ShareData.PxToDpi_xhdpi(16) * scale, null);
//		paint.setColor(0x32000000);
//		canvas.drawCircle((float)thumb.getWidth() / 2f, (float)thumb.getHeight() / 2f, (float)w / 2f, paint);
		System.gc();
		return thumb;
	}

	/**
	 * 制作缩略图;
	 *
	 * @param org 原始图片
	 */
	private Bitmap createThumb(Bitmap org)
	{
		if(org == null || org.isRecycled()) return null;

		int thumb_w = ShareData.PxToDpi_xhdpi(320);
		int thumb_h = ShareData.PxToDpi_xhdpi(320);
		Bitmap thumb = MakeBmp.CreateFixBitmap(org, thumb_w, thumb_h, MakeBmp.POS_CENTER, 0, Config.ARGB_8888);
		if(thumb == null || thumb.isRecycled()) return null;
		mThumb = makeCircle(thumb);
		return mThumb;
	}

	public static Bitmap makeCircle(Bitmap org)
	{
		if(org == null || org.isRecycled()) return null;
		int w = org.getWidth();
		int h = org.getHeight();
		Bitmap circle = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(circle);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawCircle((float)w / 2f, (float)h / 2f, (float)w / 2f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(org, 0, 0, paint);
		return circle;
	}

	@SuppressWarnings("deprecation")
	private void setBackground(Bitmap bmp)
	{
		if(bmp == null || bmp.isRecycled()) return;
		mBackground = BeautifyResMgr2.MakeBkBmp(bmp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x33000000);
		BitmapDrawable bd = new BitmapDrawable(getResources(), mBackground);
		m_background.setBackgroundDrawable(bd);
	}

	/**
	 * 制作预览图
	 *
	 * @param context
	 * @param path
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap makePreviewBitmap(Context context, String path)
	{
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = 1;
		int bigest = opts.outWidth > opts.outHeight ? opts.outWidth : opts.outHeight;

		int ScreenW = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		int ScreenH = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();

		if(bigest > ScreenH)
		{
			opts.inSampleSize = bigest / ScreenH;
		}
//		if(opts.inSampleSize > 7)
//		{
//			opts.inSampleSize = 7;
//		}
//		else if(opts.inSampleSize < 1)
//		{
//			opts.inSampleSize = 1;
//		}
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Config.ARGB_8888;
		Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile(path, opts, true);

		if(bmp == null || bmp.isRecycled()) return null;

		int w = bmp.getWidth();
		int h = bmp.getHeight();
		float scale = 1f;
		if(w >= h)
		{
			scale = (float)ScreenW / w;
		}
		else
		{
			scale = (float)ScreenH / h;
			if(w * scale > ScreenW)
			{
				scale = (float)ScreenW / w;
			}
		}
		int outW = (int)(w * scale);
		int outH = (int)(h * scale);
		Bitmap outBmp = Bitmap.createBitmap(outW, outH, Config.ARGB_8888);
		if(outBmp == null || outBmp.isRecycled()) return null;
		Canvas canvas = new Canvas(outBmp);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		canvas.drawBitmap(bmp, matrix, null);
		bmp.recycle();
		System.gc();
		return outBmp;
	}

	/**
	 * 按720宽屏幕比例制作素材资源
	 *
	 * @param res     资源库
	 * @param id      素材id
	 * @param screenW 当前屏幕宽
	 * @param dpi     手机DPI系数
	 * @return
	 */
	public static Bitmap makeResourceByScreenScale(Resources res, int id, int screenW, float dpi)
	{
		if(res == null || screenW <= 0)
		{
			return null;
		}
		float scale = (float)screenW / dpi / 360f;
		Bitmap resBitmap = BitmapFactory.decodeResource(res, id);
		if(scale >= 1)
		{
			return resBitmap;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newBitmap = Bitmap.createBitmap((int)(resBitmap.getWidth() * scale), (int)(resBitmap.getHeight() * scale), Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(resBitmap, matrix, null);
		resBitmap.recycle();
		System.gc();
		return newBitmap;
	}

	/**
	 * 获取字符串中英语数字的数量
	 *
	 * @param text 字符串
	 * @return 英语数字的数量
	 */
	public static int getEnglishNumber(String text)
	{
		int count = 0;
		if(text != null && text.length() > 0)
		{
			char[] chars = text.toCharArray();
			//判断每个字符
			for(int i = 0; i < chars.length; i++)
			{
				if((chars[i] >= 65 && chars[i] <= 90) || (chars[i] >= 97 && chars[i] <= 122))
				{
					count++;
				}
				else if(chars[i] >= 48 && chars[i] <= 57)
				{
					count++;
				}
			}
		}
		return count;
	}

//**********************微博发送文字输入界面*******************

	public void showSendBlogPage()
	{
		closeSendBlogPage();
		mSendBlog = new SendBlogPage(getContext());
		mSendBlog.setOnClickListener(mClickListener);
		mSendBlog.init(mParent.getGlassBackground(), mThumb, mBlogType, activities_text);
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.TOP | Gravity.LEFT;
		this.addView(mSendBlog, fl);
		mSendBlog.setDialogListener(new SharePage.DialogListener()
		{
			@Override
			public void onClick(int view)
			{
				String content = null;

				switch(view)
				{
					case SendBlogPage.ID_BACK:
						content = mSendBlog.getText();
						activities_text = content;
						closeSendBlogPage();
						break;

					case SendBlogPage.ID_SEND:
						if(mParent.mActConfigure != null)
							Utils.UrlTrigger(getContext(), mParent.mActConfigure.mSendTj);

						content = mSendBlog.getText();
						if(content == null || content.length() <= 0)        //如果输入框没有内容
						{
							if(mParent.mActConfigure != null && mParent.mActConfigure.mDefaultContent != null && mParent.mActConfigure.mDefaultContent.length() > 0)
							{
								content = mParent.mActConfigure.mDefaultContent;
							}
							else
							{
								content = mContext.getResources().getString(R.string.share_content_default);
							}
						}
						else
						{
							activities_text = content;
						}

						closeSendBlogPage();
						if(sendActUrl)
						{
							mParent.shareActivitiesUrl(getShareSavePath(), content, mBlogType);
						}
						else
						{
							if(mParent.mActConfigure == null && content != null && content.length() <= 100) content += "#美人相机# " + DOWNLOAD_URL;

							switch(mBlogType)
							{
								case SharePage.SINA:
									mParent.setContentAndPic(content, getShareSavePath());
									mParent.sendToSina();
									sendToActivities(content, getShareSavePath(), mBlogType);
									break;

		//						case SharePage.QQ:
		//							mParent.setContentAndPic(content, getShareSavePath());
		//							mParent.startSendSdkClient(SharePage.QQ);
		//							sendToActivities(content, getShareSavePath());
		//							break;
							}
						}
						break;

					default:
						break;
				}
			}
		});
	}

	/**
	 * 微博发送界面是否被打开
	 *
	 * @return boolean
	 */
	public boolean sendBlogFrameStatue()
	{
		if(mSendBlog != null) return true;
		return false;
	}

	/**
	 * 关闭微博发送页
	 */
	public void closeSendBlogPage()
	{
		if(mSendBlog != null)
		{
			this.removeView(mSendBlog);
			mSendBlog.setOnClickListener(null);
			mSendBlog.clean();
			mSendBlog = null;
		}
		hideKeyboard();
	}

	private void sendToActivities(String content, String file, int send_type)
	{
		if(mParent.mActConfigure != null && isActivities && !sendACT && !notSendAct && !sendActUrl)
		{
			sendACT = true;
			mParent.sendToPocoActivities(content, file, send_type);
		}
	}

	public boolean onBack()
	{
		if(onAnimation) return true;
		return false;
	}

	public void onPause()
	{
		if(mAdvBar != null) mAdvBar.onStop();
	}

	public void onResume()
	{
		if(mAdvBar != null) mAdvBar.onStart();
	}
}

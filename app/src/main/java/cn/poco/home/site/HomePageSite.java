package cn.poco.home.site;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.adnonstop.admasterlibs.AdUtils;
import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsBootAdRes;
import com.adnonstop.admasterlibs.data.AbsChannelAdRes;
import com.adnonstop.admasterlibs.data.AbsClickAdRes;
import com.adnonstop.admasterlibs.data.AbsFullscreenAdRes;
import com.adnonstop.changeface.ChangeFaceIntroActivity;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import cn.poco.MaterialMgr2.site.ThemeListPageSite;
import cn.poco.MaterialMgr2.site.ThemeListPageSite4;
import cn.poco.MaterialMgr2.site.ThemeListPageSite5;
import cn.poco.Theme.site.ThemePageSite;
import cn.poco.about.site.AboutPageSite;
import cn.poco.adMaster.HomeAd;
import cn.poco.adMaster.ShareAdBanner;
import cn.poco.album.site.AlbumSite;
import cn.poco.album.site.AlbumSite13;
import cn.poco.album.site.AlbumSite14;
import cn.poco.album.site.AlbumSite300;
import cn.poco.album.site.AlbumSite64;
import cn.poco.album.site.AlbumSite80;
import cn.poco.appmarket.site.MarketPageSite;
import cn.poco.arWish.site.ArIntroIndexSite;
import cn.poco.banner.BannerCore3;
import cn.poco.beautify4.UiMode;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.bootimg.site.BootImgPageSite1;
import cn.poco.business.ChannelValue;
import cn.poco.business.site.DownloadBusinessPageSite;
import cn.poco.business.site.DownloadBusinessPageSite2;
import cn.poco.business.site.DownloadBusinessPageSite3;
import cn.poco.business.site.DownloadBusinessPageSite67;
import cn.poco.business.site.DownloadBusinessPageSite68;
import cn.poco.business.site.DownloadBusinessPageSite69;
import cn.poco.business.site.FullScreenDisplayPageSite;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.site.CameraPageSite;
import cn.poco.camera.site.CameraPageSite14;
import cn.poco.camera.site.CameraPageSite17;
import cn.poco.camera.site.CameraPageSite300;
import cn.poco.camera.site.CameraPageSite301;
import cn.poco.camera.site.CameraPageSite302;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.site.centerSite.CampaignCenterSite;
import cn.poco.campaignCenter.site.webviewpagteSite.CampaignWebViewPageSite;
import cn.poco.cloudAlbum.site.CloudAlbumPageSite;
import cn.poco.cloudAlbum.site.CloudAlbumPageSite2;
import cn.poco.community.site.CommunitySite;
import cn.poco.credits.Credit;
import cn.poco.credits.MyAppInstall;
import cn.poco.exception.MyApplication;
import cn.poco.featuremenu.model.FeatureType;
import cn.poco.featuremenu.site.FeatureMenuSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework.SiteID;
import cn.poco.framework2.Framework2;
import cn.poco.home.OpenApp;
import cn.poco.home.home4.Home4Page;
import cn.poco.live.site.LiveIntroPageSite;
import cn.poco.login.site.BindPhonePageSite;
import cn.poco.login.site.BindPhonePageSite2;
import cn.poco.login.site.BindPhonePageSite3;
import cn.poco.login.site.LoginPageSite;
import cn.poco.login.site.LoginPageSite2;
import cn.poco.login.site.LoginPageSite3;
import cn.poco.login.site.LoginPageSite403;
import cn.poco.login.site.LoginPageSite7;
import cn.poco.login.site.UserInfoPageSite;
import cn.poco.login.site.UserInfoPageSite2;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.scorelibs.AbsAppInstall;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.setting.SettingPage;
import cn.poco.setting.site.SettingPageSite;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.statistics.TongJi2;
import cn.poco.taskCenter.MissionHelper;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;
import cn.poco.webview.site.WebViewPageSite;
import my.beautyCamera.R;

/**
 * 唯一入口
 */
public class HomePageSite extends BaseSite
{
	public static final String BUSINESS_KEY = "business";
	public static final String POST_STR_KEY = "business_post_str";

    public static final String CHAT_MESSAGE_KEY = "chat_message_type";
	public static final String COMMUNITY_MESSAGE_KEY = "community_message_type";

	public CmdProc m_cmdProc;

	public AlbumSite mAlbumSite = new AlbumSite()
	{
		@Override
		public void onBack(Context context)
		{
			if(onSiteCallBack != null)
			{
				onSiteCallBack.onBack(SiteID.ALBUM);
			}
		}
	};
	public CommunitySite mCampaignCenterSite = new CommunitySite()
	{
		@Override
		public void onBack(Context context)
		{
			if(onSiteCallBack != null)
			{
				onSiteCallBack.onBack(SiteID.COMMUNITY_SDK);
			}
		}

		@Override
		public void onSystemMessage(int type, int count)
		{
			HomePageSite.this.m_myParams.put(COMMUNITY_MESSAGE_KEY, type);
			if(onSiteCallBack != null)
			{
				onSiteCallBack.onSystemMessage(type, count);
			}
		}

		@Override
		public void onChatMessage(int type, int count)
		{
			HomePageSite.this.m_myParams.put(CHAT_MESSAGE_KEY, type);
			if(onSiteCallBack != null)
			{
				onSiteCallBack.onNewMessage(type, count);
			}
		}

		@Override
		public void onCommunityMessage(int type, int count) {
			HomePageSite.this.m_myParams.put(COMMUNITY_MESSAGE_KEY, type);
            if (onSiteCallBack != null) {
                onSiteCallBack.onCommunityMessage(type, count);
			}
		}

		@Override
		public void openPageByMessageType(int type) {
		}
	};

	public ThemeListPageSite mThemeListPageSite = new ThemeListPageSite()
	{
		@Override
		public void OnBack(Context context)
		{
			if(onSiteCallBack != null)
			{
				onSiteCallBack.onBack(SiteID.THEME_LIST);
			}
		}
	};

    public FullScreenDisplayPageSite mFullScreenDisplayPageSite = new FullScreenDisplayPageSite()
    {
        @Override
        public void OnBack(Context context)
        {
            if(onSiteCallBack != null)
            {
                onSiteCallBack.onBack(SiteID.BUSINESS_DISPLAY);
            }
        }
    };

	public BeautyEntryPageSite mBeautyEntryPageSite = new BeautyEntryPageSite()
	{
		@Override
		public void OnBack(Context context)
		{
			if(onSiteCallBack != null)
			{
				onSiteCallBack.onBack(SiteID.BEAUTY_ENTRY);
			}
		}
	};

    public BootImgPageSite1 mBootImgPageSite = new BootImgPageSite1()
    {
        @Override
        public void OnBack(Context context)
        {
            if(onSiteCallBack != null)
            {
                onSiteCallBack.onBack(SiteID.BOOT_IMG);
            }
        }
    };

    public FeatureMenuSite mMenuSite = new FeatureMenuSite(){
		@Override
		public void OnBack(Context context)
		{
			if(onSiteCallBack != null)
			{
				onSiteCallBack.onBack(SiteID.FEATUREMENU);
			}
		}
	};

	public HomePageSite()
	{
		super(SiteID.HOME);
		MakeCmdProc();
	}

	/**
	 * 注意构造函数调用
	 */
	protected void MakeCmdProc()
	{
		m_cmdProc = new CmdProc();
	}

	@Override
	public IPage MakePage(Context context)
	{
		return new Home4Page(context, this);
	}

	public void OnBack(Context context)
	{
		MyFramework.SITE_Back(context, null, Framework2.ANIM_NONE);
	}

	public void OnCamera(Context context)
	{
		OnCamera(context,false);
	}

	public void OnCamera(Context context,boolean hasAnim)
	{
//        if (m_myParams != null) {
//            if (m_myParams.containsKey("hasCloseAnim")) {
//                m_myParams.remove("hasCloseAnim");
//            }
//            if (hasAnim) {
//                m_myParams.put("hasCloseAnim", hasAnim);
//            }
//        }
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CameraSetDataKey.KEY_START_MODE, 1);
		params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.ALL_TYPE);
		MyFramework.SITE_Open(context, CameraPageSite.class, params, Framework2.ANIM_NONE);
	}

	public void OnLeftAd(Context context,AbsBootAdRes res)
	{
		HashMap<String, Object> params = new HashMap<>();
		if(res != null)
		{
			params.put("img", res);
			MyFramework.SITE_Popup(context, BootImgPageSite1.class, params, Framework2.ANIM_TRANSLATION_RIGHT);
		}
	}

	public void onTheme(Context context)
	{
		MyFramework.SITE_Popup(context, ThemePageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnCloudAlbum(Context context,String id, String token, String maskBmp)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("id", id);
		params.put("token", token);
		params.put("bg", maskBmp);
		MyFramework.SITE_Open(context, CloudAlbumPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnBindPhone(Context context,LoginInfo lInfo, String maskBmp)
	{
		HashMap<String, Object> datas = new HashMap<>();
		datas.put("loginInfo", lInfo);
		datas.put("img", maskBmp);
		datas.put("isHide", false);
		MyFramework.SITE_Popup(context, BindPhonePageSite.class, datas, Framework2.ANIM_TRANSLATION_LEFT);
	}

	/**
	 * 无动画切换的流程
	 *
	 * @param lInfo
	 * @param maskBmp
	 */
	public void OnNoAmnBindPhone(Context context,LoginInfo lInfo, String maskBmp)
	{
		HashMap<String, Object> datas = new HashMap<>();
		datas.put("loginInfo", lInfo);
		datas.put("img", maskBmp);
		datas.put("isHide", false);
		MyFramework.SITE_Popup(context, BindPhonePageSite3.class, datas, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnLogin(Context context,String maskBmp)
	{
		OnLogin(context,maskBmp, false);
	}

	public void OnLogin(Context context,String maskBmp, boolean isFromTaskHall)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("img", maskBmp);
		if(isFromTaskHall)
		{
			MyFramework.SITE_Popup(context, LoginPageSite7.class, params, Framework2.ANIM_NONE);
		}
		else
		{
			MyFramework.SITE_Popup(context, LoginPageSite.class, params, Framework2.ANIM_NONE);
		}
	}

	public void OnUserInfo(Context context,String id)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put("id", id);
		MyFramework.SITE_Open(context, UserInfoPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnSetting(Context context)
	{
		MyFramework.SITE_Open(context, SettingPageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void onLive(Context context)
	{
        if (m_cmdProc != null) {
            m_cmdProc.OpenPage(context, 48, (String[])null);
        }
	}

	public void OnCredit(Context context, String id, String token, String maskPath)
	{
		//检查软件是否安装，然后加积分
		AbsAppInstall.CheckAllAppInstalled(context, new MyAppInstall());

		MissionHelper.getInstance().OpenCredit((Activity) context, id, m_cmdProc);
	}

	public void OnTaskCenter(Context context, String id, String token, String maskPath)
	{
		//MissionHelper.getInstance().OpenTaskCenter(context, id, m_cmdProc);
	}

	public void OnQuestion(Context context)
	{
		SettingPage.OpenQuestionPage(context);
	}

	public void OnScore(Context context)
	{
		try
		{
			Uri uri = Uri.parse("market://details?id=" + context.getApplicationContext().getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		catch(Throwable e)
		{
			Toast.makeText(context, "还没有安装安卓市场，请先安装", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public void OnAppMarket(Context context, String maskPath)
	{
		HashMap<String, Object> params = new HashMap<>();
        params.put("background", maskPath);
        MyFramework.SITE_Open(context, MarketPageSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OnUpdateNow(Context context, String url)
	{
		CommonUtils.OpenBrowser(context, url);
	}

	public void OnUpdateDetail(Context context, String url)
	{
		if(url != null)
		{
			if(url.startsWith("http"))
			{
				HashMap<String, Object> params = new HashMap<>();
				params.put("url", url);
				MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_NONE);
			}
			else
			{
				CommonUtils.OpenBrowser(context, url);
			}
		}
	}

	public void OpenPrintPage(Context context)
	{
		//MyFramework.SITE_Open(context, PrinterPageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	/**
	 * 镜头预览智能美形定制
	 */
	public void OnTailorMadeCamera(Context context)
	{
		HashMap<String, Object> params = new HashMap<>();
		params.put(CameraSetDataKey.KEY_TAILOR_MADE_SETTING, true);
		params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.CUTE);
		params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE, ShutterConfig.ALL_TYPE);
		MyFramework.SITE_Open(context, CameraPageSite14.class , params, Framework2.ANIM_NONE);
	}

	public void OpenAboutUsPage(Context context)
	{
		MyFramework.SITE_Popup(context, AboutPageSite.class, null, Framework2.ANIM_TRANSLATION_LEFT);
	}

	public void OpenBeautyMallPage(Context context,String userId)
	{
		MissionHelper.getInstance().OpenBeautyMall((Activity) context, userId, m_cmdProc);
	}

	public void onShareWithFriends(Context context,String bitmapPath) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("bitmapPath", bitmapPath);
		MyFramework.SITE_Popup(context, ShareWithFriendSite.class, params, Framework2.ANIM_TRANSLATION_LEFT);	}


	public static void CloneBusinessParams(HashMap<String, Object> dst, HashMap<String, Object> src)
	{
		if(src != null && dst != null)
		{
			dst.put(HomePageSite.BUSINESS_KEY, src.get(HomePageSite.BUSINESS_KEY));
			dst.put(HomePageSite.POST_STR_KEY, src.get(HomePageSite.POST_STR_KEY));
		}
	}

	public static JSONObject makePostVar(Context context, String channelValue)
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("os", "android");
			json.put("channel_value", URLEncoder.encode(channelValue));
			String imei = CommonUtils.GetIMEI(context);
			if(imei == null)
			{
				imei = java.util.UUID.randomUUID().toString();
			}
			json.put("hash", CommonUtils.Encrypt("MD5", imei));
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * @param dst src会合并到这里
	 * @param src
	 */
	public static void postStrMerge(JSONObject dst, JSONObject src)
	{
		if(dst != null && src != null)
		{
			try
			{
				Iterator<String> iterator = src.keys();
				while(iterator.hasNext())
				{
					String key = iterator.next();
					dst.put(key, src.get(key));
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 点击首页(左下/右上)广告
	 *
	 * @param context
	 * @param res
	 */
	public void OnAD(@Nullable Home4Page home4Page, Context context, AbsAdRes res)
	{
		OnAppAD(home4Page, context, res, m_cmdProc);
	}

	/**
	 * APP AD的总入口
	 *
	 * @param context
	 * @param res
	 * @param proc
	 */
	public static void OnAppAD(@Nullable Home4Page home4Page, Context context, AbsAdRes res, final HomePageSite.CmdProc proc, String... args)
	{
		if(res != null)
		{
			boolean isCampaignCenter = CampaignWebViewPageSite.HasKey(args);

			ShareAdBanner.SendTj(context, res.mClickTjs);
			String channelValue = res.mAdId;
			String click = null;
			if(res instanceof AbsClickAdRes)
			{
				click = ((AbsClickAdRes)res).mClick;
			}
			if(channelValue != null && channelValue.length() > 0)
			{
				Credit.BussinessCreditIncome(channelValue, context, context.getResources().getInteger(R.integer.积分_看广告));
				if(channelValue.equals(ChannelValue.POCO_044))
				{
					//打开内部网页
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("url", click);
					MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_NONE);
					return;
				}
				else if(channelValue.equals(ChannelValue.POCO_062))
				{
					//走弹窗/浏览器调用协议
					BannerCore3.ExecuteCommand(context, click, proc);
					return;
				}
				else if(channelValue.equals(ChannelValue.POCO_063))
				{
					//跳素材中心,跟banner协议一样
					BannerCore3.ExecuteCommand(context, click, proc);
					return;
				}
				else if(channelValue.equals(ChannelValue.POCO_064) || channelValue.equals(ChannelValue.POCO_ICONCLICK1) || channelValue.equals(ChannelValue.POCO_ICONCLICK2) || channelValue.equals(ChannelValue.POCO_ICONCLICK3))
				{
					//打开外部网页
					CommonUtils.OpenBrowser(context, click);
					return;
				}
//				else if(channelValue.equals(ChannelValue.AD79))
//				{
//					//纪梵希
//					HashMap<String, Object> params = new HashMap<>();
//					params.put("channelValue",ChannelValue.AD79);
//					params.put(HomePageSite.BUSINESS_KEY, res);
//					params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
//					//MyFramework.SITE_Open(context, DownloadBusinessPageSite64.class, params, Framework2.ANIM_NONE);
//					return;
//				}
//				else if(channelValue.equals(ChannelValue.AD78_2))
//				{
//					//mac
//					HashMap<String, Object> params = new HashMap<>();
//					params.put(HomePageSite.BUSINESS_KEY, res);
//					params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
//					MyFramework.SITE_Open(context, DownloadBusinessPageSite65.class, params, Framework2.ANIM_NONE);
//					return;
//				}
//				else if(channelValue.equals(ChannelValue.AD81_1))
//				{
//					//兰蔻
//					HashMap<String, Object> params = new HashMap<>();
//					params.put(HomePageSite.BUSINESS_KEY, res);
//					params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
//					MyFramework.SITE_Open(context, DownloadBusinessPageSite66.class, params, Framework2.ANIM_NONE);
//					return;
//				}
				else if(channelValue.equals(ChannelValue.AD82_1))
				{
					//兰蔻2
					HashMap<String, Object> params = new HashMap<>();
					params.put("channelValue",ChannelValue.AD82_1);
					params.put(HomePageSite.BUSINESS_KEY, res);
					params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
					MyFramework.SITE_Open(context, DownloadBusinessPageSite67.class, params, Framework2.ANIM_NONE);
					return;
				}
				else if(channelValue.equals(ChannelValue.AD83))
				{
					//佰草集
					HashMap<String, Object> params = new HashMap<>();
					params.put("channelValue",ChannelValue.AD83);
					params.put(HomePageSite.BUSINESS_KEY, res);
					params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
					MyFramework.SITE_Open(context, DownloadBusinessPageSite68.class, params, Framework2.ANIM_NONE);
					return;
				}
				else if(channelValue.equals(ChannelValue.AD84))
                {
                    //阿玛尼
					HashMap<String, Object> params = new HashMap<>();
					params.put("channelValue",ChannelValue.AD84);
					params.put(HomePageSite.BUSINESS_KEY, res);
					params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
					MyFramework.SITE_Open(context, DownloadBusinessPageSite69.class, params, Framework2.ANIM_NONE);
					return;
                }
				// TODO: 记得return
				//这里添加商业判断
				else
				{
					//默认通用通道
					if(res instanceof AbsFullscreenAdRes)
					{
						AbsFullscreenAdRes adRes = (AbsFullscreenAdRes) res;
						if(adRes.mPageAdm == null || adRes.mPageAdm.length == 0){
							home4Page = null;
						}
						//全屏广告
						HashMap<String, Object> params = new HashMap<>();
						params.put(HomePageSite.BUSINESS_KEY, res);
						params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
						if(home4Page != null)
						{
							home4Page.openFullScreenADPage(params);
						}
						else
						{
							MyFramework.SITE_Popup(context, DownloadBusinessPageSite3.class, params, Framework2.ANIM_NONE);
						}
					}
					else if(res instanceof AbsClickAdRes)
					{
						if(click != null && !click.toLowerCase().startsWith("http"))
						{
							//非http,走协议
							BannerCore3.ExecuteCommand(context, click, proc);
							return;
						}
						BannerCore3.OpenUrl(context, click, new BannerCore3.OpenUrlCallback()
						{
							@Override
							public void OpenMyWeb(Context context, String url)
							{
								proc.OpenMyWebPage(context, url);
							}

							@Override
							public void OpenSystemWeb(Context context, String url)
							{
								proc.OpenSystemWebPage(context, url);
							}
						});
					}
					else if(res instanceof AbsChannelAdRes)
					{
						AbsChannelAdRes.AbsPageData page = ((AbsChannelAdRes)res).GetNext(null);
						if(page instanceof AbsChannelAdRes.GatePageData)
						{
							//正常"马上参加"-"选图/拍照"
							HashMap<String, Object> params = new HashMap<>();
							params.put(HomePageSite.BUSINESS_KEY, res);
							params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
							MyFramework.SITE_Open(context, DownloadBusinessPageSite.class, params, Framework2.ANIM_NONE);
						}
						else if(page instanceof AbsChannelAdRes.SelPhotoPageData)
						{
							//只显示"选图/拍照"页
							HashMap<String, Object> params = new HashMap<>();
							params.put(HomePageSite.BUSINESS_KEY, res);
							params.put(HomePageSite.POST_STR_KEY, makePostVar(context, channelValue));
							MyFramework.SITE_Open(context, DownloadBusinessPageSite2.class, params, Framework2.ANIM_NONE);
						}
					}
					return;
				}
			}
			//其余情况判断m_clickUrl
			BannerCore3.ExecuteCommand(context, click, proc);
		}
	}

	public static class CmdProc implements BannerCore3.CmdCallback
	{
		protected CommunitySite mCampaignCenterSite;

		public void SetCampaignCenterSite(CommunitySite site)
		{
			mCampaignCenterSite = site;
		}

		@Override
		public void OpenPage(Context context,int code, String... args)
		{
			//boolean gotoSave = CampaignWebViewPageSite.HasKey(args);
			boolean gotoSave = true;
			//String comeFrom = BannerCore3.GetValue(args, "comeFrom");
			//if (!TextUtils.isEmpty(comeFrom) && comeFrom.equals("CampaignCenter"))
			//{
			//	gotoSave = true;
			//}
			switch(code)
			{
				case 0:
				{
					//素材美化 -> 美颜
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}

					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.MEIYAN.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}

					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 1:
				{
					//素材美化 -> 瘦身
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.SHOUSHEN.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 2:
				{
					//素材美化 -> 彩妆
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}

					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.CAIZHUANG.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 3:
				{
					//素材美化 -> 滤镜
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.LVJING.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 4:
				{
					//美颜美化 界面
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.BEAUTIFY.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 5:
				{
					//裁剪编辑
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.CLIP.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 6:
				{
					//素材美化 -> 相框
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
					}
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.XIANGKUANG.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 7:
				{
					//素材美化 -> 贴图
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
						}
					}
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.TIETU.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 8:
				{
					//素材美化 -> 毛玻璃
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
						}
					}
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.MAOBOLI.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 9:
				{
					//素材美化 -> 马赛克
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
						}
					}
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.MASAIKE.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 10:
				{
					//素材美化 -> 指尖魔法
					int selUri = 0;
					if(args != null && args.length > 0)
					{
						try
						{
							selUri = Integer.parseInt(args[0]);
						}
						catch(Throwable e)
						{
						}
					}
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.ZHIJIANMOFA.GetValue());
					if(selUri != 0)
					{
						params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
					}
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 16://镜头(全功能)
				{
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					int filterId = BannerCore3.GetIntValue(args, "filter");
					if(!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom)){
						//社区调用
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put(CameraSetDataKey.KEY_START_MODE, 1);
                        params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
                        params.put(CameraSetDataKey.KEY_CAMERA_SELECT_TAB_TYPE, ShutterConfig.TabType.PHOTO);
						params.put(CameraSetDataKey.KEY_CAMERA_SHOW_TAB_TYPE,
								ShutterConfig.TabType.GIF | ShutterConfig.TabType.CUTE |
								ShutterConfig.TabType.PHOTO | ShutterConfig.TabType.VIDEO);
						if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
						MyFramework.SITE_Popup(context, CameraPageSite300.class, params, Framework2.ANIM_NONE);
					}else{
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put(CameraSetDataKey.KEY_START_MODE, 1);
						if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
						MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
					}
					break;
				}

				case 21:
				{
					//视频(动态帖纸)
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.putAll(CameraSetDataKey.GetOnlyVideoStep());
					int category = BannerCore3.GetIntValue(args, "category");//分类id
					int selUri = BannerCore3.GetIntValue(args, "select");//素材id
					int filterId = BannerCore3.GetIntValue(args, "filter");//滤镜id
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					boolean isUserDefaultSticker = false;
					if(selUri == 0 || category == 0)
					{
						isUserDefaultSticker = true;
						category = -1;
						selUri = -2;
					}
					if (!isUserDefaultSticker)
					{
						params.put(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN, true);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, category);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, selUri);
					}
					if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
					if(!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom)){
						//社区调用
						params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 2000L);
						MyFramework.SITE_Popup(context, CameraPageSite301.class, params, Framework2.ANIM_NONE);
					}else{
						MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
					}
					break;
				}

				case 22:
				case 38://用户信息页(完成任务用)
				{
					SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
					String id = info.GetPoco2Id(true);
					String token = info.GetPoco2Token(true);
					if(id != null && id.length() > 0 && token != null && token.length() > 0)
					{
						HashMap<String, Object> params = new HashMap<>();
						params.put("id", id);
						params.put("isHideCredit", true);
						MyFramework.SITE_Open(context, UserInfoPageSite2.class, params, Framework2.ANIM_NONE);
					}
					else
					{
						HashMap<String, Object> params = new HashMap<>();
						MyFramework.SITE_Popup(context, LoginPageSite3.class, params, Framework2.ANIM_NONE);
					}
					break;
				}

				case 23:
				{
					SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
					String id = info.GetPoco2Id(true);
					String token = info.GetPoco2Token(true);
					String mobile = info.GetPoco2Phone();
					if(id != null && id.length() > 0 && token != null && token.length() > 0)
					{
						if(mobile == null || mobile.length() <= 0 || mobile.equals("0"))
						{
							HashMap<String, Object> params = new HashMap<>();
							LoginInfo lInfo = new LoginInfo();
							lInfo.mUserId = id;
							lInfo.mAccessToken = token;
							params.put("loginInfo", lInfo);
							params.put("isShowTips", false);
							MyFramework.SITE_Popup(context, BindPhonePageSite2.class, params, Framework2.ANIM_NONE);
						}
						else
						{
							HashMap<String, Object> params = new HashMap<>();
							params.put("id", id);
							params.put("token", token);
							MyFramework.SITE_Open(context, CloudAlbumPageSite2.class, params, Framework2.ANIM_NONE);
						}
					}
					else
					{
						HashMap<String, Object> params = new HashMap<>();
						MyFramework.SITE_Popup(context, LoginPageSite2.class, params, Framework2.ANIM_NONE);
					}
					break;
				}

				case 26:
				{
					//素材美化 -> 祛痘
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.QUDOU.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 27:
				{
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.QUYANDAI.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 28:
				{
					//素材美化 -> 亮眼
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.LIANGYAN.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 29:
				{
					//素材美化 -> 大眼
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.DAYAN.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 30:
				{
					//素材美化 -> 高鼻梁
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.GAOBILIANG.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 31:
				{
					//素材美化 -> 微笑
					HashMap<String, Object> params = new HashMap<>();
					params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.WEIXIAO.GetValue());
					Class<? extends BaseSite> siteClass = AlbumSite13.class;
					if(gotoSave)
					{
						siteClass = AlbumSite14.class;
					}
					MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 32:
				{
					//素材美化 -> 一键萌装
                    int selUri = 0;
                    if(args != null && args.length > 0)
                    {
                        try
                        {
                            selUri = Integer.parseInt(args[0]);
                        }
                        catch(Throwable e)
                        {
                        }
                    }
                    String comeFrom = BannerCore3.GetValue(args, "comeFrom");
                    HashMap<String, Object> params = new HashMap<>();
                    if (selUri != 0)
                    {
                        params.put(DataKey.BEAUTIFY_DEF_SEL_URI, selUri);
                    }
                    params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.YIJIANMENGZHUANG.GetValue());

                    Class<? extends BaseSite> siteClass = AlbumSite64.class;
                    if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
                    {
                        // 社区调用
                        siteClass = AlbumSite300.class;
                    }
                    MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
					break;
				}

				case 33:
				{
					Home4Page page = (Home4Page)MyFramework.GetCurrentPage(context, Home4Page.class);
					if(page != null)
					{
						page.isShowModuleView(false,Home4Page.MENU, false);
					}
					break;
				}

				case 34:
				{
					// 2017/7/10 首页-运营中心(用于分享页banner)
					HashMap<String, Object> data = new HashMap<>();
					data.put(Home4Page.KEY_CUR_MODE,Home4Page.CAMPAIGN);
					MyFramework.SITE_Open(context, true, HomePageSite.class, data, Framework2.ANIM_NONE);
					break;
				}

				case 35:
				{
					// gif流程协议
					int category = BannerCore3.GetIntValue(args, "category");//分类id
					int selUri = BannerCore3.GetIntValue(args, "select");//素材id
					int filterId = BannerCore3.GetIntValue(args, "filter");//滤镜id
                    String comeFrom = BannerCore3.GetValue(args, "comeFrom");
                    HashMap<String, Object> params = new HashMap<>();
					params.putAll(CameraSetDataKey.GetGifStep());
					boolean isUserDefaultSticker = false;
					if(selUri == 0 || category == 0)
					{
						isUserDefaultSticker = true;
						category = -1;
						selUri = -2;
					}
					if (!isUserDefaultSticker)
					{
						params.put(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN, true);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, category);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, selUri);
					}
					if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
                    if(!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
                    {
                        //社区调用
                        MyFramework.SITE_Popup(context, CameraPageSite301.class, params, Framework2.ANIM_NONE);
                    }
                    else
                    {
                        MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
                    }
					break;
				}

				case 36:
				{
					// 2017/7/20 任务大厅(仅适用于首页大圆)
					Home4Page page = (Home4Page)MyFramework.GetCurrentPage(context, Home4Page.class);
					if(page != null)
					{
						page.openMenuItem(FeatureType.TASKHALL);
					}
					break;
				}

				case 37://福利社
				{
					MissionHelper.getInstance().OpenBeautyMall((Activity) context, SettingInfoMgr.GetSettingInfo(MyApplication.getInstance()).GetPoco2Id(false), this);
					break;
				}

				case 39:
				{
					// 镜头萌妆照
					HashMap<String, Object> params = new HashMap<>();
					params.putAll(CameraSetDataKey.GetMakeupStep());
					int category = BannerCore3.GetIntValue(args, "category");//分类id
					int selUri = BannerCore3.GetIntValue(args, "select");//素材id
					int filterId= BannerCore3.GetIntValue(args, "filter");//滤镜id
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					boolean isUserDefaultSticker = false;
					if (selUri == 0 || category == 0)
					{
						isUserDefaultSticker = true;
						category = -1;
						selUri = -2;
					}
					if (!isUserDefaultSticker)
					{
						params.put(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN, true);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, category);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, selUri);
					}
					if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						//社区调用
						params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 2000L);
						MyFramework.SITE_Popup(context, CameraPageSite301.class, params, Framework2.ANIM_NONE);
					}
					else
					{
						MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
					}
					break;
				}

				case 40:
				{
					// 镜头高清拍照
                    HashMap<String, Object> params = new HashMap<>();
                    params.putAll(CameraSetDataKey.GetOnlyTakePicture(true));
					int filterId= BannerCore3.GetIntValue(args, "filter");//滤镜id
					if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
                    String comeFrom = BannerCore3.GetValue(args, "comeFrom");
                    if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
                    {
						params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
						//params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);
						//params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
                        //社区调用
                        MyFramework.SITE_Popup(context, CameraPageSite302.class, params, Framework2.ANIM_NONE);
                    }
                    else
                    {
                        MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
                    }
					break;
				}

				case 41:
				{
					// 视频+萌妆照(默认打开萌妆照)
                    HashMap<String, Object> params = new HashMap<>();
                    params.putAll(CameraSetDataKey.GetMakeupAndVideo());
                    int category = BannerCore3.GetIntValue(args, "category");//分类id
                    int selUri = BannerCore3.GetIntValue(args, "select");//素材id
					int filterId= BannerCore3.GetIntValue(args, "filter");//滤镜id
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					boolean isUserDefaultSticker = false;
					if (selUri == 0 || category == 0)
					{
						isUserDefaultSticker = true;
						category = -1;
						selUri = -2;
					}
					if (!isUserDefaultSticker)
					{
						params.put(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN, true);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, category);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, selUri);
					}
					if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
                    {
                        //社区调用
						params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
						params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 2000L);
                        MyFramework.SITE_Popup(context, CameraPageSite302.class, params, Framework2.ANIM_NONE);
                    }
                    else
                    {
                        MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
                    }
                    break;
				}

				case 42:
				{
					// 只拍照(相当于只显示 高清拍照+萌妆照,默认打开萌妆照)
                    HashMap<String, Object> params = new HashMap<>();
                    params.putAll(CameraSetDataKey.GetMakeupAndTakePicture());
                    int category = BannerCore3.GetIntValue(args, "category");//分类id
                    int selUri = BannerCore3.GetIntValue(args, "select");//素材id
					int filterId= BannerCore3.GetIntValue(args, "filter");//滤镜id
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					boolean isUserDefaultSticker = false;
					if (selUri == 0 || category == 0)
                    {
                    	isUserDefaultSticker = true;
                        category = -1;
                        selUri = -2;
                    }
                    if (!isUserDefaultSticker)
					{
						params.put(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN, true);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, category);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, selUri);
					}
					if (filterId != 0) {params.put(CameraSetDataKey.KEY_CAMERA_FILTER_ID, filterId);}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
                    {
						params.put(CameraSetDataKey.KEY_HIDE_PHOTO_PICKER_BTN, true);
						//params.put(CameraSetDataKey.KEY_IS_FILTER_BEAUTITY_PROCESS, true);
						//params.put(CameraSetDataKey.KEY_FILTER_BEAUTITY_PROCESS_MASK, FilterBeautifyProcessor.FILTER_BEAUTY);
                        //社区调用
                        MyFramework.SITE_Popup(context, CameraPageSite302.class, params, Framework2.ANIM_NONE);
                    }
                    else
                    {
                        MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
                    }
                    break;
				}

				case 43:
				{
					// 视频+萌妆照+gif(默认萌妆照)
                    HashMap<String, Object> params = new HashMap<>();
                    params.putAll(CameraSetDataKey.GetStickerStep(ShutterConfig.TabType.CUTE));
                    int category = BannerCore3.GetIntValue(args, "category");//分类id
                    int selUri = BannerCore3.GetIntValue(args, "select");//素材id
                    String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					boolean isUserDefaultSticker = false;
					if (selUri == 0 || category == 0)
                    {
                    	isUserDefaultSticker = true;
                        category = -1;
                        selUri = -2;
                    }
                    if (!isUserDefaultSticker)
					{
						params.put(CameraSetDataKey.KEY_HIDE_STICKER_MANAGER_BTN, true);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_CATEGORY_ID, category);
						params.put(CameraSetDataKey.KEY_CAMERA_STICKER_STICKER_ID, selUri);
					}
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
                    {
                        //社区调用
                        params.put(CameraSetDataKey.KEY_VIDEO_RECORD_MIN_DURATION, 2000L);
                        MyFramework.SITE_Popup(context, CameraPageSite302.class, params, Framework2.ANIM_NONE);
                    }
                    else
                    {
                        MyFramework.SITE_Open(context, CameraPageSite17.class, params, Framework2.ANIM_NONE);
                    }
                    break;
				}

				case 44:
				{
					//打开相册（给社区用）
					HashMap<String, Object> params = new HashMap<>();
					params.put("from_camera", true);
					MyFramework.SITE_Popup(context, AlbumSite80.class, params, Framework2.ANIM_NONE);
					break;
				}

                case 45:
                {
                    //素材美化 -> 美牙
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
                    params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.MEIYA.GetValue());
                    Class<? extends BaseSite> siteClass = AlbumSite13.class;
                    if(gotoSave)
                    {
                        siteClass = AlbumSite14.class;
                    }
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
                    MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
                    break;
                }
                case 46:
                {
                    //素材美化 -> 瘦鼻
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
                    params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.SHOUBI.GetValue());
                    Class<? extends BaseSite> siteClass = AlbumSite13.class;
                    if(gotoSave)
                    {
                        siteClass = AlbumSite14.class;
                    }
                    if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
                    MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
                    break;
                }
                case 47:
                {
                    //素材美化 -> 增高
					String comeFrom = BannerCore3.GetValue(args, "comeFrom");
					HashMap<String, Object> params = new HashMap<>();
                    params.put(DataKey.BEAUTIFY_DEF_OPEN_PAGE, UiMode.ZENGGAO.GetValue());
                    Class<? extends BaseSite> siteClass = AlbumSite13.class;
                    if(gotoSave)
                    {
                        siteClass = AlbumSite14.class;
                    }
					if (!TextUtils.isEmpty(comeFrom) && "community".equals(comeFrom))
					{
						// 社区调用
						siteClass = AlbumSite300.class;
					}
                    MyFramework.SITE_Popup(context, siteClass, params, Framework2.ANIM_NONE);
                    break;
                }

				case 48:
				{
					// 直播助手
					SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
					String id = info.GetPoco2Id(true);
					if(id != null && id.length() > 0)
					{
						MyFramework.SITE_Open(context, LiveIntroPageSite.class, null, Framework2.ANIM_NONE);
					}
					else
					{
						HashMap<String, Object> params = new HashMap<>();
						MyFramework.SITE_Popup(context, LoginPageSite403.class, params, Framework2.ANIM_NONE);
					}
					break;
				}
                case 49:
                {
                    // AR祝福 test
                    MyFramework.SITE_Popup(context, ArIntroIndexSite.class, null, Framework2.ANIM_NONE);
                    break;
                }
                case 50:
                {
                    // 换脸sdk
                    Activity activity = MyFramework2App.getInstance().getActivity();
                    if(activity != null) {
                        Intent intent = new Intent(activity, ChangeFaceIntroActivity.class);
	                    String source = BannerCore3.GetValue(args, "source");
	                    intent.putExtra("source", source);
	                    activity.startActivity(intent);
                    }
                    break;
                }

				default:
					break;
			}
		}

		@Override
		public void OpenWebPage(Context context, String... args)
		{
			String url = null;
			if(args != null && args.length > 0 && (url = args[0]) != null)
			{
				if((url.startsWith("http") || url.startsWith("ftp")) && url.contains(".poco.cn"))
				{
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("url", url);
					MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_NONE);
				}
				else
				{
					url = AdUtils.AdDecodeUrl(context, url);
					CommonUtils.OpenBrowser(context, url);
				}
			}
		}

		@Override
		public void OpenMyWebPage(Context context, Object... args)
		{
			Object obj = null;
			Object secondObj = null;
			if(args != null)
			{
				if(args.length > 0)
				{
					obj = args[0];
				}
				if(args.length > 1)
				{
					secondObj = args[1];
				}
			}
			if(obj instanceof String)
			{
				HashMap<String, Object> params = new HashMap<>();
				params.put("url", obj);
				MyFramework.SITE_Popup(context, WebViewPageSite.class, params, Framework2.ANIM_NONE);
			}
			else if(obj instanceof CampaignInfo)
			{
				HashMap<String, Object> params = new HashMap<>();
				params.put("campaignInfo", obj);
				if(secondObj instanceof String)
				{
					CampaignInfo temp = ((CampaignInfo)obj).clone();
					temp.setOpenUrl((String)secondObj);
					if(mCampaignCenterSite != null)
					{
						mCampaignCenterSite.m_myParams.put(CampaignCenterSite.ITEM_OPEN_INDEX, temp);
					}
				}
				MyFramework.SITE_Popup(context, CampaignWebViewPageSite.class, params, Framework2.ANIM_NONE);
			}
		}

		@Override
		public void OpenSystemWebPage(Context context, String... args)
		{
			String url = null;
			if(args != null && args.length > 0 && (url = args[0]) != null)
			{
				url = AdUtils.AdDecodeUrl(context, url);
				CommonUtils.OpenBrowser(context, url);
			}
		}

		@Override
		public void OpenPocoCamera(Context context,String... args)
		{
			OpenApp.openPoco(context);
		}

		@Override
		public void OpenPocoMix(Context context,String... args)
		{
			OpenApp.openPMix(context);
		}

		@Override
		public void OpenJane(Context context,String... args)
		{
			OpenApp.openJane(context);
		}

		@Override
		public void OpenInterPhoto(Context context,String... args)
		{
			OpenApp.openInterPhoto(context);
		}

		@Override
		public void OpenResourcePage(Context context,String... args)
		{
			String def_id;
			HashMap<String, Object> params = new HashMap<String, Object>();
			if(args != null && args.length > 0 && (def_id = args[0]) != null && def_id.length() > 0)
			{
				params.put("defID", Integer.parseInt(def_id));
			}
			String comeFrom = BannerCore3.GetValue(args, "comeFrom");
			if(comeFrom!=null && "community".equals(comeFrom)){

				//社区调用素材中心
				MyFramework.SITE_Popup(context, ThemeListPageSite5.class, params, Framework2.ANIM_NONE);
			}else{
				MyFramework.SITE_Open(context, ThemeListPageSite4.class, params, Framework2.ANIM_NONE);
			}
		}

		@Override
		public void OpenBusinessPage(Context context,String... args)
		{
			if(args != null && args.length > 0)
			{
				OnAppAD(null, context, HomeAd.GetOneHomeRes(context, args[0]), this, args);
			}
		}

		@Override
		public void OpenPrintPage(Context context,String... args)
		{
			//MyFramework.SITE_Open(context, PrinterPageSite.class, null, Framework2.ANIM_NONE);
		}

		protected ShareBlogData mShareBlogData;

		public void SetShareData(ShareBlogData data)
		{
			mShareBlogData = data;
		}

		@Override
		public void GoToShare(final Context context, String... args)
		{
			if(mShareBlogData != null && args != null)
			{
				String title0 = "";
				String content0 = "";
				String platform0 = "";
				String callbackUrl0 = "";
				String imgUrl0 = "";
				String weixinUser0 = "";
				String tjId0 = null;
				for(String arg : args)
				{
					String[] pair = arg.split("=");
					if(pair.length == 2)
					{
						switch(pair[0])
						{
							case "shareplatform":
								platform0 = pair[1];
								break;
							case "sharetxt":
								content0 = pair[1];
								break;
							case "sharetitle":
								title0 = pair[1];
								break;
							case "sharelink":
								callbackUrl0 = pair[1];
								break;
							case "shareimg":
								imgUrl0 = pair[1];
								break;
							case "weixinuser":
								weixinUser0 = pair[1];
								break;
							case "tj_id":
								tjId0 = pair[1];
								break;
						}
					}
				}
				if(platform0.equals("weixin") && weixinUser0.equals("1"))
				{
					platform0 = "weixinuser";
				}

				TongJi2.AddCountById(tjId0);
				final String title = title0.length() > 0 ? URLDecoder.decode(title0) : context.getResources().getString(R.string.share_default_title);
				final String content = content0 != null ? URLDecoder.decode(content0) : null;
				final String platform = platform0;
				final String imgUrl = URLDecoder.decode(imgUrl0);
				final String callbackUrl = URLDecoder.decode(callbackUrl0);
				if(imgUrl != null && imgUrl.length() > 0)
				{
					mShareBlogData.ShowWaitDlg();

					final Handler uiHandler = new Handler();
					final String imgPath = FileCacheMgr.GetAppPath(".jpg");
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							NetCore2 net = new NetCore2();
							final NetCore2.NetMsg msg = net.HttpGet(imgUrl, null, imgPath, null);
							uiHandler.post(new Runnable()
							{
								@Override
								public void run()
								{
									if(msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK)
									{
										if(platform.equals("sina"))
										{
											mShareBlogData.CloseWaitDlg();
											if(SettingPage.checkSinaBindingStatus(context))
											{
												mShareBlogData.mShare.sendToSinaBySDK(content + " " + callbackUrl, imgPath, new ShareTools.SendCompletedListener()
												{
													@Override
													public void getResult(Object result)
													{
														switch((int)result)
														{
															case WBConstants.ErrorCode.ERR_OK:
																Toast.makeText(context, context.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
																break;

															case WBConstants.ErrorCode.ERR_CANCEL:
																Toast.makeText(context, context.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
																break;

															case WBConstants.ErrorCode.ERR_FAIL:
																Toast.makeText(context, context.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
																break;
														}
													}
												});
											}
											else
											{
												mShareBlogData.mShare.bindSina(new SharePage.BindCompleteListener()
												{
													@Override
													public void success()
													{
														mShareBlogData.mShare.sendToSinaBySDK(content + " " + callbackUrl, imgPath, new ShareTools.SendCompletedListener()
														{
															@Override
															public void getResult(Object result)
															{
																switch((int)result)
																{
																	case WBConstants.ErrorCode.ERR_OK:
																		Toast.makeText(context, context.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
																		break;

																	case WBConstants.ErrorCode.ERR_CANCEL:
																		Toast.makeText(context, context.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
																		break;

																	case WBConstants.ErrorCode.ERR_FAIL:
																		Toast.makeText(context, context.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
																		break;
																}
															}
														});
													}

													@Override
													public void fail()
													{
													}
												});
											}
										}
										else if(platform.equals("qqzone"))
										{
											mShareBlogData.CloseWaitDlg();
											mShareBlogData.mShare.bindQzone(new SharePage.BindCompleteListener()
											{
												@Override
												public void success()
												{
													mShareBlogData.ShowWaitDlg();
													mShareBlogData.mShare.sendUrlToQzone(imgUrl, title, content, callbackUrl, new ShareTools.SendCompletedListener()
													{
														@Override
														public void getResult(Object result)
														{
															mShareBlogData.CloseWaitDlg();
															switch((int)result)
															{
																case QzoneBlog2.SEND_SUCCESS:
																	Toast.makeText(context, context.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
																	break;

																case QzoneBlog2.SEND_CANCEL:
																	Toast.makeText(context, context.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
																	break;

																case QzoneBlog2.SEND_FAIL:
																	Toast.makeText(context, context.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
																	break;

																default:
																	AlertDialog dlg = new AlertDialog.Builder(context).create();
																	dlg.setTitle(context.getResources().getString(R.string.tips));
																	dlg.setMessage(context.getResources().getString(R.string.share_qq_error_clinet_no_install));
																	dlg.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
																	dlg.show();
																	break;
															}
														}
													});
												}

												@Override
												public void fail()
												{
												}
											});
										}
										else if(platform.equals("qq"))
										{
											mShareBlogData.CloseWaitDlg();
											mShareBlogData.mShare.bindQzone(new SharePage.BindCompleteListener()
											{
												@Override
												public void success()
												{
													mShareBlogData.ShowWaitDlg();
													mShareBlogData.mShare.sendUrlToQQ(title, content, imgPath, callbackUrl, new ShareTools.SendCompletedListener()
													{
														@Override
														public void getResult(Object result)
														{
															mShareBlogData.CloseWaitDlg();
															switch((int)result)
															{
																case QzoneBlog2.SEND_SUCCESS:
																	Toast.makeText(context, context.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
																	break;

																case QzoneBlog2.SEND_CANCEL:
																	Toast.makeText(context, context.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
																	break;

																case QzoneBlog2.SEND_FAIL:
																	Toast.makeText(context, context.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
																	break;

																default:
																	AlertDialog dlg = new AlertDialog.Builder(context).create();
																	dlg.setTitle(context.getResources().getString(R.string.tips));
																	dlg.setMessage(context.getResources().getString(R.string.share_qq_error_clinet_no_install));
																	dlg.setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.ensure), (DialogInterface.OnClickListener)null);
																	dlg.show();
																	break;
															}
														}
													});
												}

												@Override
												public void fail()
												{
												}
											});
										}
										else if(platform.equals("weixin"))
										{
											mShareBlogData.CloseWaitDlg();
											mShareBlogData.mShare.sendUrlToWeiXin(imgPath, callbackUrl, title, content, false, new ShareTools.SendCompletedListener()
											{
												@Override
												public void getResult(Object result)
												{
													switch((int)result)
													{
														case BaseResp.ErrCode.ERR_OK:
															Toast.makeText(context, context.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
															break;

														case BaseResp.ErrCode.ERR_USER_CANCEL:
															Toast.makeText(context, context.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
															break;

														case BaseResp.ErrCode.ERR_AUTH_DENIED:
															Toast.makeText(context, context.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
															break;
													}
												}
											});
										}
										else if(platform.equals("weixinuser"))
										{
											mShareBlogData.CloseWaitDlg();
											mShareBlogData.mShare.sendUrlToWeiXin(imgPath, callbackUrl, title, content, true, new ShareTools.SendCompletedListener()
											{
												@Override
												public void getResult(Object result)
												{
													switch((int)result)
													{
														case BaseResp.ErrCode.ERR_OK:
															Toast.makeText(context, context.getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
															break;

														case BaseResp.ErrCode.ERR_USER_CANCEL:
															Toast.makeText(context, context.getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
															break;

														case BaseResp.ErrCode.ERR_AUTH_DENIED:
															Toast.makeText(context, context.getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
															break;
													}
												}
											});
										}
									}
									else
									{
										mShareBlogData.CloseWaitDlg();
										Toast.makeText(context, context.getResources().getString(R.string.webviewpage_get_data_fail), Toast.LENGTH_LONG).show();
									}
								}
							});
						}
					}).start();
				}
			}
		}
	}

	public static class ShareBlogData
	{
		protected ShareTools mShare;
		protected ProgressDialog mDlg;
		protected Context mContext;

		public ShareBlogData(Context context)
		{
			mContext = context;
			mShare = new ShareTools(mContext);
		}

		public void ShowWaitDlg()
		{
			if(mDlg == null)
			{
				mDlg = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.share_sending));
				mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			}
			mDlg.show();
		}

		public void CloseWaitDlg()
		{
			if(mDlg != null)
			{
				mDlg.dismiss();
				mDlg = null;
			}
		}

		public void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			mShare.onActivityResult(requestCode, resultCode, data);
		}

		public void ClearAll()
		{
			CloseWaitDlg();
		}
	}

	private OnSiteBack onSiteCallBack;

	public void setOnSiteCallBack(OnSiteBack onSiteCallBack)
	{
		this.onSiteCallBack = onSiteCallBack;
	}

	public void removeOnSiteCallBack()
	{
		this.onSiteCallBack = null;
	}

	public interface OnSiteBack
	{
		void onBack(int siteId);

		void onSystemMessage(int type, int count);

		void onNewMessage(int type, int count);

		void onCommunityMessage(int type, int count);

	}
}

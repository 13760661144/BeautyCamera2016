package cn.poco.community;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.circle.common.chatlist.NotificationDataUtils;
import com.circle.common.serverapi.PageDataInfo;
import com.circle.common.serverapi.ProtocolParams;
import com.circle.framework.ICallback;
import com.circle.framework.OnOutSiteLoginListener;
import com.circle.utils.dn.DnImg;
import com.taotie.circle.CommunityLayout;
import com.taotie.circle.Configure;
import com.taotie.circle.Constant;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.poco.beautify4.Beautify4Page;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.community.site.CommunitySite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework.SiteID;
import cn.poco.framework2.IFramework;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.home4.IActivePage;
import cn.poco.login.BeautyIMConnect;
import cn.poco.login.HttpResponseCallback;
import cn.poco.login.LoginUtils2;
import cn.poco.login.UserMgr;
import cn.poco.login.site.activity.LoginActivitySite;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.share.ShareTools;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * 社区page
 * Created by lgh on 2017/7/28.
 */
public class CommunityPage extends IPage implements EventCenter.OnEventListener, IActivePage
{

	private final static String TAG = "aaa";

	private CommunityLayout mCommunityLayout;
	private CommunitySite mSite;
	private OnOutSiteLoginListener oslistener;
	private Context mContext;
	private DnImg mDnImg;
	private ProgressDialog mDialog;
	private Handler mHandler = new Handler();
	public static boolean isUserInfoModified = false;//是否修改了用户信息
	private boolean isFullScreen = true;

	private boolean mIsBack;
	private boolean mPageIsActive;

	public CommunityPage(Context context, BaseSite site)
	{
		super(context, site);
//		//初始化社区
//		XAlien.init(context);
//		ProtocolParams.init("x_star_app_android", "1.6.5", ProtocolParams.BETA_ENVIROMENT);
//		CommunityLayout.init(context);

		onCreate(context,site,false);
	}

	private void onCreate(Context context, BaseSite site,boolean isRestore){
		mContext = context;
		mSite = (CommunitySite)site;
		mCommunityLayout = new CommunityLayout(context, isRestore);
		LayoutParams fp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mCommunityLayout, fp);
		mCommunityLayout.setOnOutsideCallback(mOnCommunityCallback);//设置社区调用外部功能的回调
		mCommunityLayout.onStart();//进入社区调用，社区框架需要用到
		mCommunityLayout.setMeetPageAllowTips(false);//是否允许社区主页弹提示

		try
		{
			mDnImg = new DnImg();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		//分享是弹出的提示框
		mDialog = new ProgressDialog(context);
		mDialog.setMessage("请稍后.....");
		//添加广播事件监听
		EventCenter.addListener(this);
		changeThemeSkin();

		//保证社区的登录状态跟app的登录状态一致
		if(!UserMgr.IsLogin(context, null))
		{
			mCommunityLayout.clearLoginInfo();
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		super.onWindowFocusChanged(hasWindowFocus);
		if(mPageIsActive && hasWindowFocus)
		{
			ShareData.changeSystemUiVisibility(getContext(), false);
		}
	}

	/**
	 * 刷新用户信息
	 */
	private void refreshUser()
	{
		SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
		LoginUtils2.getUserInfo(settingInfo.GetPoco2Id(false), settingInfo.GetPoco2Token(false), new HttpResponseCallback()
		{
			@Override
			public void response(Object object)
			{
				if(object == null) return;
				UserInfo userInfo = (UserInfo)object;
				UserMgr.SaveCache(userInfo);
				EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
			}
		});
	}

	/**
	 * 退出全屏
	 *
	 * @param activity
	 */
	private void quitFullScreen(Activity activity)
	{
		if(!isFullScreen)
		{
			return;
		}
		isFullScreen = false;
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 设置全屏
	 *
	 * @param activity
	 */
	private void setFullScreen(Activity activity)
	{
		if(isFullScreen)
		{
			return;
		}
		isFullScreen = true;
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 从首页滑入社区时调用
	 */
	public void slideInCommunity(Context context)
	{
		//进入社区退出全屏
		quitFullScreen((Activity)context);
		//进入社区的时候需要同步一下社区用户数据
		setUserInfo(context);
	}

	/**
	 * 打开社区指定页面
	 *
	 * @param uri
	 */
	public void openLink(Uri uri)
	{
		String uriString = uri.toString();
		if(uriString.startsWith("beautycamerasns"))
		{
			uriString = uriString.replace("beautycamerasns", "sns");
		}
		else
		{
			return;
		}
		final String PageID = uri.getQueryParameter("pid");
		final String finalUriString = uriString;
		if(!TextUtils.isEmpty(PageID) && "1280014".equals(PageID))
		{
			//打开忘记密码
			mSite.openResetPwdPage(mContext);
			return;
		}
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				mCommunityLayout.openWapLink(PageID, finalUriString);
			}
		}, 500);
	}

	/**
	 * 设置用户数据
	 *
	 * @param context
	 */
	public void setUserInfo(Context context)
	{
		if(UserMgr.IsLogin(context, null))
		{
			UserInfo userInfo = UserMgr.ReadCache(context);
			PageDataInfo.LoginInfo loginInfo = new PageDataInfo.LoginInfo();
			loginInfo.userId = userInfo.mUserId;
			loginInfo.nickname = userInfo.mNickname;
			loginInfo.icon = userInfo.mUserIcon;
			loginInfo.sex = userInfo.mSex;
			loginInfo.mobile = userInfo.mMobile;
			loginInfo.zone_num = userInfo.mZoneNum;
			loginInfo.birthday_year = userInfo.mBirthdayYear;
			loginInfo.birthday_month = userInfo.mBirthdayMonth;
			loginInfo.birthday_day = userInfo.mBirthdayDay;
			loginInfo.locationId = userInfo.mLocationId;
			SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
			settingInfo.GetPoco2RefreshToken();
			loginInfo.token = settingInfo.GetPoco2Token(true);
			loginInfo.refreshToken = settingInfo.GetPoco2RefreshToken();
			loginInfo.code = 0;
			if(isUserInfoModified)
			{
				//是否主app修改了用户信息，如果修改则需要同步社区
				isUserInfoModified = false;
				loginInfo.isModify = true;
			}
			mCommunityLayout.setLoginInfo(context, loginInfo, false);
		}
		else
		{
			mCommunityLayout.clearLoginInfo();
		}
	}

	/**
	 * 修改社区的皮肤
	 */
	public void changeThemeSkin()
	{
		mCommunityLayout.setAppSkinColor(SysConfig.s_skinColor, SysConfig.s_skinColor1, SysConfig.s_skinColor2);
	}

	/**
	 * 社区回调
	 */
	private ICallback mOnCommunityCallback = new ICallback()
	{
		@Override
		public void onBack(Object... obj)
		{
			if(mIsBack)
			{
				return;
			}
			mIsBack = true;
			mCommunityLayout.openMainPage();
			if(mCommunityLayout.mTopPage != null)
			{
				mCommunityLayout.mTopPage.onStop();
			}
			mSite.onBack(mContext);
			setFullScreen((Activity)mContext);
		}

		@Override
		public void onLogin(OnOutSiteLoginListener listener)
		{
			setFullScreen((Activity)mContext);
			oslistener = listener;
			mSite.onLogin(mContext, "");
		}

		@Override
		public void onRegistration()
		{
			setFullScreen((Activity)mContext);
			mSite.onRegistration(mContext);
		}

		@Override
		public void onBindPhone(OnOutSiteLoginListener listener)
		{
			setFullScreen((Activity)mContext);
			SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(getContext());
			LoginInfo lInfo = new LoginInfo();
			lInfo.mUserId = settingInfo.GetPoco2Id(false);
			lInfo.mAccessToken = settingInfo.GetPoco2Token(false);
			mSite.onBindPhone(mContext, lInfo);
		}

		@Override
		public void logout(boolean isLogoutOneself)
		{
			//退出登录
			UserMgr.ExitLogin(getContext());
			//通知首页左侧菜单头像
			EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
			//清除社区登录信息
//			mCommunityLayout.clearLoginInfo();
			if(!isLogoutOneself)
			{
				BeautyIMConnect.showKickLogoutDlg();
			}
		}


		@Override
		public void openFunction(HashMap<String, Object> params)
		{
			setFullScreen((Activity)mContext);
			mSite.openCamera(mContext, params);
		}

		@Override
		public void openPhotoPicker(HashMap<String, Object> params)
		{
			setFullScreen((Activity)mContext);
			mSite.openPhotoPicker(mContext, params);
		}

		@Override
		public void onNewMessage(int type, int count)
		{
			if(type == NotificationDataUtils.TYPE_SYSTEM || type == NotificationDataUtils.TYPE_CIRCLE_NOTIC)
			{
				//系统消息
				mSite.onSystemMessage(type, count);
			}
			else if(type == NotificationDataUtils.TYPE_IM)
			{
				//聊天消息
				mSite.onChatMessage(type, count);
			}
			else
			{
				//社区消息
				mSite.onCommunityMessage(type, count);
			}
		}


		@Override
		public void onShare(int where, PageDataInfo.ShareInfo2 shareInfo)
		{
			if(tools == null)
			{
				tools = new ShareTools(mContext);
				tools.needAddIntegral(false);
			}
			if(mDnImg == null)
			{
				mDnImg = new DnImg();
			}
			switch(where)
			{
				case Constant.SHARE_TO_QQ_FRIEND:
					//qq好友
					shareToQQFriend(shareInfo);
					break;
				case Constant.SHARE_TO_QQ_ZONE:
					//qq空间
					shareToQQZone(shareInfo);
					break;
				case Constant.SHARE_TO_WX_CIRCLE:
					//微信朋友圈
					shareToWXCircle(shareInfo);
					break;
				case Constant.SHARE_TO_WX_FRIEND:
					//微信好友
					shareToWXFriend(shareInfo);
					break;
				case Constant.SHARE_TO_WEIBO:
					//微博
					shareToWeiBo(shareInfo);
					break;
			}
		}

		@Override
		public void onSoftWen(String jsonStr)
		{
			setFullScreen((Activity)mContext);
			CampaignInfo campaignInfo = CampaignInfo.decodeJsonAndMakeCampaignInfo(jsonStr);
			// 触发统计
			Utils.UrlTrigger(mContext, campaignInfo.getBannerTjUrl());

//			final String suffix = ".img";
//			final String type = "twitter";
//			final String typeNormal = "other";
//			String identifier = String.valueOf(System.currentTimeMillis());
//			String filePathOther = FolderMgr.getInstance().CAMPAIGN_CENTER_PATH + File.separator + identifier + typeNormal + suffix;
//			String filePathTwitter = FolderMgr.getInstance().CAMPAIGN_CENTER_PATH + File.separator + identifier + type + suffix;
//			campaignInfo.setCacheImgPath(filePathOther);
//			campaignInfo.setCacheImgForTwiter(filePathTwitter);
			int position = mCommunityLayout.getIndexPageCurrentItemPosition(campaignInfo.getCoverUrl());
			mSite.m_myParams.put(CommunitySite.ITEM_OPEN_INDEX, position);

			TongJi2.AddCountById(campaignInfo.getStatisticId());
			mSite.onClickCampaignItem(mContext, campaignInfo);
		}

		@Override
		public void onJoinActivity(String agreement_url)
		{
			if(TextUtils.isEmpty(agreement_url))
			{
				return;
			}
			setFullScreen((Activity)mContext);
			mSite.onJoinActivity(mContext, agreement_url);
		}

		@Override
		public void refreshUserInfo(PageDataInfo.UserInfo userInfo)
		{
			refreshUser();
		}

		@Override
		public void OpenJiFen(int uiId)
		{

		}

		@Override
		public void getJiFenCount(String uiId)
		{

		}

		@Override
		public void openAgreementUrl(String url)
		{
			if(TextUtils.isEmpty(url))
			{
				return;
			}
			setFullScreen((Activity)mContext);
			mSite.openUrl(mContext, url);
		}

	};

	private ShareTools tools;

	/**
	 * 分享到qq好友
	 *
	 * @param shareInfo 分享内容
	 */
	private void shareToQQFriend(PageDataInfo.ShareInfo2 shareInfo)
	{
		tools.sendUrlToQQ(shareInfo.title, shareInfo.content, shareInfo.share_img_url, shareInfo.share_url, new ShareTools.SendCompletedListener()
		{
			@Override
			public void getResult(Object result)
			{
				showToast(result);
			}
		});
	}

	/**
	 * 分享到qq空间
	 *
	 * @param shareInfo 分享内容
	 */
	private void shareToQQZone(PageDataInfo.ShareInfo2 shareInfo)
	{
		tools.sendUrlToQzone(shareInfo.share_img_url, shareInfo.title, shareInfo.content, shareInfo.share_url, new ShareTools.SendCompletedListener()
		{
			@Override
			public void getResult(Object result)
			{
				showToast(result);
			}
		});
	}

	/**
	 * 分享到微信朋友圈
	 *
	 * @param shareInfo 分享内容
	 */
	private void shareToWXCircle(final PageDataInfo.ShareInfo2 shareInfo)
	{
		mDialog.show();
		mDnImg.dnImg(shareInfo.share_img_url, 150, new DnImg.OnDnImgListener()
		{
			@Override
			public void onProgress(String url, int downloadedSize, int totalSize)
			{

			}

			@Override
			public void onFinish(String url, String file, Bitmap bmp)
			{
				mDialog.dismiss();
				if(file == null)
				{
					if(FileUtil.isFileExists(shareInfo.share_img_url))
					{
						file = shareInfo.share_img_url;
					}
					else
					{
						return;
					}
				}
				tools.sendUrlToWeiXin(file, shareInfo.share_url, shareInfo.title, shareInfo.content, false, new ShareTools.SendCompletedListener()
				{
					@Override
					public void getResult(Object result)
					{
						showToast(result);
					}
				});
			}
		});
	}

	/**
	 * 分享到微信好友
	 *
	 * @param shareInfo 分享内容
	 */
	private void shareToWXFriend(final PageDataInfo.ShareInfo2 shareInfo)
	{
		mDialog.show();
		mDnImg.dnImg(shareInfo.share_img_url, 150, new DnImg.OnDnImgListener()
		{
			@Override
			public void onProgress(String url, int downloadedSize, int totalSize)
			{

			}

			@Override
			public void onFinish(String url, String file, Bitmap bmp)
			{
				mDialog.dismiss();
				if(file == null)
				{
					if(FileUtil.isFileExists(shareInfo.share_img_url))
					{
						file = shareInfo.share_img_url;
					}
					else
					{
						return;
					}
				}
				tools.sendUrlToWeiXin(file, shareInfo.share_url, shareInfo.title, shareInfo.content, true, new ShareTools.SendCompletedListener()
				{
					@Override
					public void getResult(Object result)
					{
						showToast(result);
					}
				});
			}
		});
	}

	/**
	 * 分享到微博
	 *
	 * @param shareInfo 分享内容
	 */
	private void shareToWeiBo(final PageDataInfo.ShareInfo2 shareInfo)
	{
		mDialog.show();
		mDnImg.dnImg(shareInfo.share_img_url, 150, new DnImg.OnDnImgListener()
		{
			@Override
			public void onProgress(String url, int downloadedSize, int totalSize)
			{

			}

			@Override
			public void onFinish(String url, String file, Bitmap bmp)
			{
				mDialog.dismiss();
				if(file == null)
				{
					if(FileUtil.isFileExists(shareInfo.share_img_url))
					{
						file = shareInfo.share_img_url;
					}
					else
					{
						return;
					}
				}
				tools.sendToSinaBySDK(shareInfo.other_text+shareInfo.share_url, file, new ShareTools.SendCompletedListener(){
					@Override
					public void getResult(Object result)
					{
						showToast(result);
					}
				});
//				tools.sendUrlToSina(shareInfo.other_text, file, shareInfo.title, shareInfo.content, shareInfo.share_url, new ShareTools.SendCompletedListener()
//				{
//					@Override
//					public void getResult(Object result)
//					{
//						showToast(result);
//					}
//				});
			}
		});
	}

	private void showToast(Object result)
	{

	}

	/**
	 * 根据消息类型打开想要的页面
	 *
	 * @param type 消息的类型 参考{@link NotificationDataUtils#TYPE_ALL}
	 */
	public void openPageByMessageType(int type)
	{
		if(mCommunityLayout != null)
		{
			mCommunityLayout.openPageByMessageType(type);
		}

		if(mSite != null)
		{
			mSite.openPageByMessageType(type);
		}
	}


	/**
	 * 恢复现场
	 * 软文用到
	 */
	public void onRestoreState()
	{
		mSite.onRestoreState(mContext);
	}

	/**
	 * 获取社区标题栏高度
	 *
	 * @return 返回标题栏高度
	 */
	public int getActionbarHeight()
	{
		return mCommunityLayout.getActionbarHeight();
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{
		if(params != null && params.containsKey("openFriendPage"))
		{
//			mHandler.postDelayed(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					setFullScreen((Activity)getContext());
//				}
//			},500);
			mCommunityLayout.openFriendPage();
		}
		else if(params != null && params.containsKey(Home4Page.KEY_COMMUNITY_URI))
		{
			Uri uri = (Uri)params.get(Home4Page.KEY_COMMUNITY_URI);
			openLink(uri);
		}
		else if(params != null && params.containsKey(CommunitySite.ITEM_OPEN_INDEX))
		{
			int position = (int)params.get(CommunitySite.ITEM_OPEN_INDEX);
			if(position>1){
				mCommunityLayout.setIndexPagePosition(position);
			}
//            onRestoreState();
		}
	}

	private String reSaveImage(String tempPath)
	{
		try
		{
			String path = Utils.MakeSavePhotoPath(mContext, Beautify4Page.GetImgScaleWH(tempPath));
			FileUtils.copyFile(new File(tempPath), new File(path));
			Utils.FileScan(getContext(), path);
			return path;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 清理用户信息
	 * 退出登录后调用
	 */
	public static void clearLoginInfo(Context context)
	{
		if(Configure.isLogin())
		{
			NotificationDataUtils.getInstance().clearUnread();
			Configure.clearLoginInfo();
			Configure.saveConfig(context);
		}
	}


	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		quitFullScreen((Activity)mContext);
		//Log.i("aaa", "onPageResult: " + siteID);
		if(CommunityLayout.main==null){
			//恢复,  因为CommunityLayout.main是静态的问题，再执行分享到社区流程退出后会清除改变量，在这里需要执行恢复操作
			onCreate(mContext,mSite,true);
		}
		if(siteID == SiteID.LOGIN || siteID == SiteID.RESETPSW || siteID == SiteID.REGISTER_DETAIL)
		{
			//登录和绑定手机并完善资料后同步社区用户数据
			setUserInfo(mContext);
			quitFullScreen((Activity)mContext);
		}
		else
		{
			if(params == null)
			{
				if(siteID != SiteID.CAMPAIGN_WEBVIEW_PAGE)
				{
					mSite.onRestoreState(mContext);
				}
				else
				{
					if(mSite != null && mSite.m_myParams != null && mSite.m_myParams.containsKey(CommunitySite.ITEM_OPEN_INDEX))
					{
						int position = (int)mSite.m_myParams.get(CommunitySite.ITEM_OPEN_INDEX);
						mCommunityLayout.setIndexPagePosition(position);
						mSite.m_myParams.remove(CommunitySite.ITEM_OPEN_INDEX);
					}
				}
				return;
			}
			try
			{
				HashMap<String, Object> extra = new HashMap<>();
				if(params.containsKey(DataKey.COMMUNITY_SEND_CIRCLE_EXTRA) && !TextUtils.isEmpty(params.get(DataKey.COMMUNITY_SEND_CIRCLE_EXTRA).toString()))
				{
					extra.put("extra", params.get(DataKey.COMMUNITY_SEND_CIRCLE_EXTRA));
				}

				if(params.containsKey("imgPath"))
				{
					String[] imgs = (String[])params.get("imgPath");
					for(int i = 0; i < imgs.length; i++)
					{
						if(!imgs[i].contains("."))
						{
							imgs[i] = reSaveImage(imgs[i]);
						}
					}
					mCommunityLayout.onPageResult(Constant.TYPE_IMAGE_PATH, imgs, extra);
				}
				else if(params.containsKey("videoPath"))
				{
					String videoPath = params.get("videoPath").toString();
					mCommunityLayout.onPageResult(Constant.TYPE_VIDEO_PATH, new String[]{videoPath}, extra);
				}
				else if(params.containsKey("gifPath"))
				{
					String gifPath = params.get("gifPath").toString();
					mCommunityLayout.onPageResult(Constant.TYPE_GIF_PATH, new String[]{gifPath}, extra);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		super.onPageResult(siteID, params);
	}

	@Override
	public void onPause()
	{
		super.onPause();
//		if(mDialog!=null){
//			mDialog.dismiss();
//		}
		if(mPageIsActive)
		{
			IFramework framework = MyFramework2App.getInstance().getFramework();
			if(framework != null && framework.GetTopPage() instanceof Home4Page)
			{
				setFullScreen((Activity)mContext);
			}
		}
		mCommunityLayout.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		IFramework framework = MyFramework2App.getInstance().getFramework();
		if(framework != null && framework.GetTopPage() instanceof Home4Page)
		{
			quitFullScreen((Activity)mContext);
		}
		mCommunityLayout.onResume();
	}

	@Override
	public void onStart()
	{
		super.onStart();
//		quitFullScreen((Activity)mContext);
		mCommunityLayout.onStart();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		mCommunityLayout.onStop();
	}

	@Override
	public void onClose()
	{
		super.onClose();
		if(mDnImg != null)
		{
			mDnImg.stopAll();
			mDnImg = null;
		}
		if(tools != null)
		{
			tools = null;
		}
		EventCenter.removeListener(this);
		mCommunityLayout.onClose();
	}

	@Override
	public boolean onActivityKeyDown(int keyCode, KeyEvent event)
	{
		if(mCommunityLayout.onActivityKeyDown(keyCode, event))
		{
			return true;
		}
		return super.onActivityKeyDown(keyCode, event);
	}

	@Override
	public boolean onActivityKeyUp(int keyCode, KeyEvent event)
	{
		if(mCommunityLayout.onActivityKeyUp(keyCode, event))
		{

			return true;
		}
		return super.onActivityKeyUp(keyCode, event);
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(tools != null)
		{
			//分享用到
			tools.onActivityResult(requestCode, resultCode, data);
		}
		if(requestCode == LoginActivitySite.REQUEST_CODE)
		{
			setUserInfo(mContext);
		}
		if(mCommunityLayout.onActivityResult(requestCode, resultCode, data))
		{
			return true;
		}
		return super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBack()
	{
		if(mIsBack)
		{//解决快速点击返回键动画异常问题
			return;
		}
		mCommunityLayout.onBack();
	}

	@Override
	public void onEvent(int eventId, Object[] params)
	{
		if(eventId == EventID.HOMEPAGE_UPDATE_MENU_AVATAR)
		{
			if(!UserMgr.IsLogin(mContext, null))
			{
				//退出登录后清除社区内的登录信息
				mCommunityLayout.clearLoginInfo();
			}
		}
		else if(eventId == EventID.COMMUNITY_UPDATE_ENVIRONMENT)
		{
			//切换环境要重新登录
			//退出登录
			UserMgr.ExitLogin(getContext());
			//通知首页左侧菜单头像
			EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
			//切换环境通知社区
			ProtocolParams.setEnvironment(SysConfig.IsDebug() ? ProtocolParams.BETA_ENVIROMENT : ProtocolParams.RELEASE_ENVIROMENT);
		}
		else if(eventId == EventID.USE_INFO_PAGE_FORCE_EXIT_LOGIN)
		{
			mCommunityLayout.notifyCommunityLogout(mContext);
		}
	}

	@Override
	public void onPageActive(int lastActiveMode)
	{
		//进入社区
		mIsBack = false;
		mPageIsActive = true;
		TongJiUtils.onPageStart(getContext(), R.string.运营专区);
		MyBeautyStat.onPageStartByRes(R.string.社区_首页_主页面);
		mCommunityLayout.setMeetPageAllowTips(true);
		ShareData.changeSystemUiVisibility(getContext(), false);
	}

	@Override
	public void onPageInActive(int nextActiveMode)
	{
		//退出社区
		mIsBack = false;
		TongJiUtils.onPageEnd(getContext(), R.string.运营专区);
		MyBeautyStat.onPageEndByRes(R.string.社区_首页_主页面);
		mCommunityLayout.setMeetPageAllowTips(false);
		mPageIsActive = false;
		ShareData.changeSystemUiVisibility(getContext(), true);
	}

	@Override
	public void setUiEnable(boolean uiEnable)
	{

	}
}

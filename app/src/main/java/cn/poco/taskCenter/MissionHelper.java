package cn.poco.taskCenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.beautymall.callBack.MallCallBack;
import com.adnonstop.beautymall.commutils.BMCheckNewTopic;
import com.adnonstop.beautymall.constant.BeautyUser;
import com.adnonstop.beautymall.ui.activities.MyIntegrationActivity;
import com.adnonstop.beautymall.ui.activities.homepage.NewBeautyMallHomeActivity;
import com.adnonstop.hzbeautycommonlib.ShareValueHZCommon;
import com.adnonstop.hzbeautycommonlib.StaticsManagers.MaineActivityLifeCicle;
import com.adnonstop.hzbeautycommonlib.Statistics.BaseEvent;
import com.adnonstop.hzbeautycommonlib.Statistics.BeautyMall.PageTojiEvent;
import com.adnonstop.hzbeautycommonlib.Statistics.MHStatistics.MHPageTojiEvent;
import com.adnonstop.missionhall.Constant.KeyConstant;
import com.adnonstop.missionhall.Constant.intetact_gz.IntentKey;
import com.adnonstop.missionhall.Constant.intetact_gz.MissionHallActivityTags;
import com.adnonstop.missionhall.callback.common.HallCallBack;
import com.adnonstop.missionhall.model.interact_gz.MissionJumpTask;
import com.adnonstop.missionhall.model.interact_gz.MissionTojiEvent;
import com.adnonstop.missionhall.ui.activities.HallActivity;
import com.adnonstop.missionhall.ui.activities.MissionInfoActivity;
import com.adnonstop.missionhall.ui.activities.WalletActivity;
import com.baidu.mobstat.StatService;
import com.example.beautylogin.BeautyLoginCall;
import com.example.beautylogin.BeautyLoginInfo;
import com.example.beautylogin.ThirdLoginInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.Locale;

import cn.poco.adMaster.HomeAd;
import cn.poco.banner.BannerCore3;
import cn.poco.credits.Credit;
import cn.poco.exception.MyApplication;
import cn.poco.featuremenu.manager.AppFeatureManager;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework2.IFramework;
import cn.poco.holder.ObjHolder;
import cn.poco.home.home4.Home4Page;
import cn.poco.home.site.HomePageSite;
import cn.poco.login.HttpResponseCallback;
import cn.poco.login.LoginOtherUtil;
import cn.poco.login.LoginUtils2;
import cn.poco.login.UserMgr;
import cn.poco.login.utils.AliyunHeadUpload;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.TongJi2;
import cn.poco.system.AppInterface;
import cn.poco.system.SysConfig;
import my.beautyCamera.R;

/**
 * Created by POCO on 2017/6/9.
 * 任务大厅 回调
 */

public class MissionHelper
{
	private static MissionHelper mInstance;

	private static final String APP_NAME = "beauty_camera";//beauty_camera

	private static final int LOGIN = 0x01;//(登录)
	private static final int BIND = 0x02;//(绑定手机)
	public int mFlag;

	private BannerCore3.CmdCallback mCmdProc;
	public String mActivityTag;

	private MissionHelper()
	{
		if(!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}
		MaineActivityLifeCicle.getInstance(MyFramework2App.getInstance().getApplication()).onMainActivityCreated();
	}

	public static MissionHelper getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new MissionHelper();

			//分享
			HallCallBack.getInstance().setOnShareMissionHallListener(new HallCallBack.OnShareMissionHallListenter()
			{
				@Override
				public void onShare(Context context, ShareValueHZCommon shareValue)
				{
					if(shareValue.getHzsdkDistinguish() == ShareValueHZCommon.HZSDKDistinguish.MissionHall)
					{
						ShareValueHZCommon.SocialNetwork socialNetwork = shareValue.getSocialNetwork();
						String shareTitle = shareValue.getShareTitle();
						String shareContent = shareValue.getShareContent();
						String shareImgUrl = shareValue.getShareIcon();
						String shareLinkUrl = shareValue.getShareLinkedUrl();

						Activity activity = (Activity)context;
						Intent intent = new Intent(activity, SendBlogActivity.class);
						intent.putExtra("shareTitle", shareTitle);
						intent.putExtra("shareContent", shareContent);
						//File shareIconFile = new File(Environment.getExternalStorageDirectory(), APP_FOLDER + "/appdata/missionHall/shareIcon.png");
						//intent.putExtra("shareImgUrl", shareIconFile.getAbsolutePath());
						//intent.putExtra("shareImgUrl", ShareIconUtil.getShareIconPath(context));
						intent.putExtra("shareImgUrl", shareImgUrl);
						intent.putExtra("shareLinkUrl", shareLinkUrl);
						intent.putExtra("type", socialNetwork);
						activity.startActivityForResult(intent, SendBlogActivity.RESULT_CODE);
					}
				}
			});

			//绑定手机
			HallCallBack.getInstance().setOnBindMolileListener(new HallCallBack.OnBindMobileListener()
			{
				@Override
				public void onBindMobile(String activityTag)
				{
					if(mInstance != null)
					{
						mInstance.mActivityTag = activityTag;
						mInstance.mFlag = BIND;

						//调用我们的界面
						Handler handler = new Handler();
						handler.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								if(sOnActionCallBack != null)
								{
									sOnActionCallBack.onBindPhone();
								}
							}
						}, 50);
					}
				}
			});

			//登录
			HallCallBack.getInstance().setOnLoginListener(new HallCallBack.OnLoginListener()
			{
				@Override
				public void onLogin(String activityTag)
				{
					if(mInstance != null)
					{
						mInstance.mActivityTag = activityTag;
						mInstance.mFlag = LOGIN;

						//调用我们的界面
						Handler handler = new Handler();
						handler.postDelayed(new Runnable()
						{
							@Override
							public void run()
							{
								if(sOnActionCallBack != null)
								{
									sOnActionCallBack.onLogin();
								}
							}
						}, 50);
					}
				}
			});
		}

		return mInstance;
	}

	public void SetJumpData(BannerCore3.CmdCallback proc)
	{
		mCmdProc = proc;
	}

	public void ResetFlag()
	{
		mActivityTag = null;
		mFlag = 0;
	}

	static
	{
		BeautyLoginCall.getInstance().setBeauty_loginListener(new BeautyLoginCall.IBeauty_LoginListener()
		{
			@Override
			public void onBeautyLoginSuccess(final Context context, BeautyLoginInfo beautyLoginInfo, final String flag)
			{
				//System.out.println(content.toString());

				LoginInfo loginInfo = null;
				if(beautyLoginInfo != null)
				{
					loginInfo = new LoginInfo();
					loginInfo.mUserId = beautyLoginInfo.getUserId();
					loginInfo.mAccessToken = beautyLoginInfo.getAccessToken();
					loginInfo.mExpireTime = "" + beautyLoginInfo.getExpireTime();
					loginInfo.mRefreshToken = beautyLoginInfo.getRefreshToken();
					loginInfo.mAppId = "" + beautyLoginInfo.getAppId();
					loginInfo.mAddTime = "" + beautyLoginInfo.getAddTime();
					loginInfo.mUpdateTime = "" + beautyLoginInfo.getUpdateTime();
				}

				if(loginInfo != null)
				{
					Context tempContext;
					if(context instanceof Activity)
					{
						tempContext = context;
					}
					else
					{
						tempContext = MyFramework2App.getInstance().getActivity();
					}

					final ProgressDialog dialog = LoginOtherUtil.showProgressDialog(new ProgressDialog(tempContext), "绑定中...", tempContext);

					final LoginInfo loginInfo2 = loginInfo;
					LoginUtils2.getUserInfo(loginInfo.mUserId, loginInfo.mAccessToken, new HttpResponseCallback()
					{
						@Override
						public void response(Object object)
						{
							if(object == null)
							{
								LoginOtherUtil.dismissProgressDialog(dialog);
								LoginOtherUtil.showToast("登陆失败!");
								return;
							}
							UserInfo userInfo = (UserInfo)object;
							if(userInfo.mCode == 0 && userInfo.mProtocolCode == 200)
							{
								LoginOtherUtil.dismissProgressDialog(dialog);

								UserMgr.SaveCache(userInfo);
								LoginOtherUtil.setSettingInfo(loginInfo2);

								if(flag != null && flag.equals(com.example.beautylogin.KeyConstant.BEAUTY_LOGIN_REGISTER))
								{
									new Thread(new Runnable()
									{
										@Override
										public void run()
										{
											Credit.CreditIncome_notThreadinMethod(context, context.getResources().getInteger(R.integer.积分_手机注册) + "", loginInfo2.mUserId, loginInfo2.mAccessToken, mHandler);
										}
									}).start();

								}

								EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);

								TongJi2.AddOnlineClickCount(null, context.getResources().getInteger(R.integer.行为事件_用微信登录) + "", context.getResources().getString(R.string.登录));
								Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_每天使用) + "");
							}
							else
							{
								/*int code = userInfo.mCode;
								if(code == 10001)
								{
									mOnloginLisener.showLoginErrorTips("请输入手机号");
								}
								else if(code == 10002)
								{
									mOnloginLisener.showLoginErrorTips("请输入密码");
								}
								else if(code == 10003)
								{
									mOnloginLisener.showLoginErrorTips("用户不存在");
								}
								else if(code == 10004)
								{
									mOnloginLisener.showLoginErrorTips("你的账号已被禁用");
								}
								else if(code == 10005)
								{
									mOnloginLisener.showLoginErrorTips("你输入的密码不正确，请重新输入。");
								}
								else
								{
									mOnloginLisener.showLoginErrorTips("登陆失败");
								}*/
								LoginOtherUtil.dismissProgressDialog(dialog);
								LoginOtherUtil.showToast("登陆失败!");
							}
						}
					});
				}
				else
				{
					LoginOtherUtil.showToast("网络异常,登录失败!");
				}
			}

			@Override
			public void onBeautyLoginFailure(Context context, String msg, String flag)
			{
				/*Toast.makeText(PocoCamera.main , msg , Toast.LENGTH_SHORT).show();
				System.out.println("login_error:"+msg);*/
			}
		});

		BeautyLoginCall.getInstance().setBeauty_uploadHeadThumbListener(new BeautyLoginCall.IBeauty_UploadHeadThumbListener()
		{
			@Override
			public String uploadHeadThumb(String userId, String accessToken, String path)
			{
				AliyunHeadUpload upload = new AliyunHeadUpload();
				String headUrl = upload.uploadHeadThumb(MyApplication.getInstance(), userId, accessToken, path, AppInterface.GetInstance(MyApplication.getInstance()));
				return headUrl;
			}
		});

		BeautyLoginCall.getInstance().setBeauty_thirdLoginListener(new BeautyLoginCall.IBeauty_ThirdLoginListener()
		{
			@Override
			public void onBeautyThirdLoginSuccess(final Context context, ThirdLoginInfo content)
			{
				// 宿主第三方登录
				LoginInfo loginInfo = null;
				if(content != null)
				{
					loginInfo = new LoginInfo();

					loginInfo.mUserId = content.getUserId();
					loginInfo.mAccessToken = content.getAccessToken();
					loginInfo.mExpireTime = "" + content.getExpireTime();
					loginInfo.mRefreshToken = content.getRefreshToken();
				}

				if(loginInfo != null)
				{
					Context tempContext;
					if(context instanceof Activity)
					{
						tempContext = context;
					}
					else
					{
						tempContext = MyFramework2App.getInstance().getActivity();
					}

					final ProgressDialog dialog = LoginOtherUtil.showProgressDialog(new ProgressDialog(tempContext), "绑定中...", tempContext);

					final LoginInfo loginInfo2 = loginInfo;
					LoginUtils2.getUserInfo(loginInfo.mUserId, loginInfo.mAccessToken, new HttpResponseCallback()
					{
						@Override
						public void response(Object object)
						{
							if(object == null)
							{
								LoginOtherUtil.dismissProgressDialog(dialog);
								LoginOtherUtil.showToast("登陆失败!");
								return;
							}
							UserInfo userInfo = (UserInfo)object;
							if(userInfo.mCode == 0 && userInfo.mProtocolCode == 200)
							{
								LoginOtherUtil.dismissProgressDialog(dialog);

								if(userInfo.mMobile != null && userInfo.mMobile.length() > 0 && !userInfo.mMobile.equals("0"))
								{
									UserMgr.SaveCache(userInfo);
									LoginOtherUtil.setSettingInfo(loginInfo2);

									Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_第三方登录) + "");

									EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);

									TongJi2.AddOnlineClickCount(null, context.getResources().getInteger(R.integer.行为事件_用微信登录) + "", context.getResources().getString(R.string.登录));
									Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_每天使用) + "");
								}
							}
							else
							{
								LoginOtherUtil.dismissProgressDialog(dialog);
								LoginOtherUtil.showToast("登陆失败!");
							}
						}
					});
				}
				else
				{
					LoginOtherUtil.showToast("网络异常,登录失败!");
				}
			}
		});

		BeautyLoginCall.getInstance().setOnShareListenter_beautyLogin(new BeautyLoginCall.OnShareListenter_BeautyLogin()
		{
			@Override
			public void onShare(Context context, ShareValueHZCommon shareValue)
			{
				if(shareValue.getHzsdkDistinguish() != ShareValueHZCommon.HZSDKDistinguish.MissionHall)
				{
					String shareTitle = shareValue.getShareTitle();
					ShareValueHZCommon.SocialNetwork socialNetwork = shareValue.getSocialNetwork();
					String shareContent = shareValue.getShareContent();
					String shareImgUrl = shareValue.getShareIcon();
					String shareLinkUrl = shareValue.getShareLinkedUrl();

					Activity activity = (Activity)context;
					Intent intent = new Intent(activity, SendBlogActivity.class);
					intent.putExtra("shareTitle", shareTitle);
					intent.putExtra("shareContent", shareContent);

					intent.putExtra("shareImgUrl", shareImgUrl);
					intent.putExtra("shareLinkUrl", shareLinkUrl);
					intent.putExtra("type", socialNetwork);
					activity.startActivityForResult(intent, SendBlogActivity.RESULT_CODE);
				}
			}
		});

		MallCallBack.getInstance().setOpenMissionHallListener(new MallCallBack.OpenMissionHallListener()
		{
			@Override
			public void open(Context context, String userid)
			{

				Bundle bundle = new Bundle();
				bundle.putString(KeyConstant.APP_VERSION, SysConfig.GetAppVerNoSuffix(context));
				bundle.putString(KeyConstant.APP_NAME, APP_NAME);
				bundle.putString(KeyConstant.RECEIVER_ID, userid);
				Intent intent = new Intent(context, HallActivity.class);
				intent.putExtra(KeyConstant.BASE_DATA, bundle);
				context.startActivity(intent);
			}
		});

		BeautyLoginCall.getInstance().setBeauty_bindPhoneListener(new BeautyLoginCall.IBeauty_BindPhoneListener()
		{

			/**
			 * @param phone 电话号码
			 */
			@Override
			public void bindSuccess(Context context, String phone)
			{
				SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
				settingInfo.SetPoco2Phone(phone);
				SettingInfoMgr.Save(context);
				//加积分
				Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_关联手机) + "");
			}
		});

		BeautyLoginCall.getInstance().setLogoutListener(new BeautyLoginCall.LogoutListener()
		{
			@Override
			public void logout()
			{
				UserMgr.ExitLogin(MyFramework2App.getInstance().getActivity());
				EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
			}
		});
	}

	public static class BMCheckNewListener extends ObjHolder<AppFeatureManager> implements BMCheckNewTopic.TopicUpdataListener
	{
		@Override
		public void isApperaRedPoint(boolean isSlideBarAppera, boolean isBMDoorAppear, String tipsTop, String tipsBottom)
		{
			if(!TextUtils.isEmpty(tipsTop))
			{
				EventCenter.sendEvent(EventID.HOME_MENU_NEW_STATE, new Object[]{isSlideBarAppera});
			}
			AppFeatureManager.getInstance().setBeautyMallRedDotInfo(isBMDoorAppear, tipsTop, tipsBottom);
		}
	}

	public void OpenBeautyMall(Activity activity, String userid, HomePageSite.CmdProc proc)
	{
		SetJumpData(proc);
		BeautyUser.userId = null;
		BeautyUser.telNumber = null;
		if(userid != null)
		{
			BeautyUser.userId = userid;
		}

		String telNum = null;
		if(UserMgr.IsLogin(activity, null))
		{
			telNum = SettingInfoMgr.GetSettingInfo(activity).GetPoco2Phone();
		}
		if(telNum != null && telNum.length() > 4)
		{
			BeautyUser.telNumber = telNum;
		}

		Intent intent = new Intent(activity, NewBeautyMallHomeActivity.class);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
	}

	public void OpenTaskCenter(Context context, String cmdStr, BannerCore3.CmdCallback proc)
	{
		SetJumpData(proc);

		ResetFlag();

		Uri uri = Uri.parse(cmdStr);
		if(uri != null)
		{
			Intent intent = new Intent(context, MissionInfoActivity.class);
			Bundle bundle1 = new Bundle();
			bundle1.putString(KeyConstant.APP_NAME, "beauty_camera");
			bundle1.putString(KeyConstant.APP_VERSION, SysConfig.GetAppVerNoSuffix(context));
			bundle1.putString(KeyConstant.MISSION_ID, uri.getQueryParameter("missionId"));
			String userId = SettingInfoMgr.GetSettingInfo(context).GetPoco2Id(false);
			if(!TextUtils.isEmpty(userId))
			{
				bundle1.putString(KeyConstant.RECEIVER_ID, userId);
			}
			bundle1.putString(MissionHallActivityTags.MISSION_ACTIVITY_TAG, MissionHallActivityTags.MH_EXTERNAL_OPEN_INFO);
			intent.putExtra(KeyConstant.BASE_DATA, bundle1);
			context.startActivity(intent);
		}
	}

	public void OpenTaskCenter(final Activity activity, String userId, boolean success, BannerCore3.CmdCallback proc)
	{
		//testUploadToAli(activity);
		SetJumpData(proc);

		if(mActivityTag == null)
		{
			Bundle bundle = new Bundle();
			bundle.putString(KeyConstant.APP_VERSION, SysConfig.GetAppVerNoSuffix(activity));
			bundle.putString(KeyConstant.APP_NAME, APP_NAME);
			bundle.putString(KeyConstant.RECEIVER_ID, userId);
			Intent intent1 = new Intent(activity, HallActivity.class);
			intent1.putExtra(KeyConstant.BASE_DATA, bundle);
			activity.startActivity(intent1);
			activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
		}
		else
		{
			switch(mFlag)
			{
				case LOGIN:
				{
					if(TextUtils.equals(MissionHallActivityTags.MISSION_INFO_ACTIVITY, mActivityTag))
					{
						Intent intent = new Intent(activity, MissionInfoActivity.class);
						intent.putExtra(IntentKey.GZ_START_MISSON_ACTIVITY_FROM_LOGIN_Intent_KEY, userId);
						activity.startActivity(intent);
						activity.overridePendingTransition(com.adnonstop.missionhall.R.anim.slide_from_right, com.adnonstop.missionhall.R.anim.slide_to_left);
					}
					else if(TextUtils.equals(MissionHallActivityTags.MISSION_HALL_ACTIVITY, mActivityTag))
					{
						Intent intent = new Intent(activity, HallActivity.class);
						intent.putExtra(IntentKey.GZ_START_MISSON_ACTIVITY_FROM_LOGIN_Intent_KEY, userId);
						activity.startActivity(intent);
						activity.overridePendingTransition(com.adnonstop.missionhall.R.anim.slide_from_right, com.adnonstop.missionhall.R.anim.slide_to_left);
					}
					break;
				}

				case BIND:
				{
					if(TextUtils.equals(MissionHallActivityTags.MISSION_INFO_ACTIVITY, mActivityTag))
					{
						Bundle bundle = new Bundle();
						bundle.putBoolean(IntentKey.MISSION_BINDMOBILE_SUCCESS, success);//true:绑定成功;false:绑定失败
						bundle.putString(IntentKey.MISSION_BINDMOBILE_USERID, userId);//用户绑定失败： userId=null
						Intent intent = new Intent(activity, MissionInfoActivity.class);
						intent.putExtra(IntentKey.GZ_START_MISSON_ACTIVITY_FROM_BINDMOBILE_INTENT_KEY, bundle);
						activity.startActivity(intent);
						activity.overridePendingTransition(com.adnonstop.missionhall.R.anim.slide_from_right, com.adnonstop.missionhall.R.anim.slide_to_left);
					}
					else if(TextUtils.equals(MissionHallActivityTags.MISSION_WALLET_ACTIVITY, mActivityTag))
					{
						Bundle bundle = new Bundle();
						bundle.putBoolean(IntentKey.MISSION_BINDMOBILE_SUCCESS, success);//true:绑定成功;false:绑定失败
						bundle.putString(IntentKey.MISSION_BINDMOBILE_USERID, userId);//用户绑定失败： userId=null
						Intent intent = new Intent(activity, WalletActivity.class);
						intent.putExtra(IntentKey.GZ_START_MISSON_ACTIVITY_FROM_BINDMOBILE_INTENT_KEY, bundle);
						activity.startActivity(intent);
						activity.overridePendingTransition(com.adnonstop.missionhall.R.anim.slide_from_right, com.adnonstop.missionhall.R.anim.slide_to_left);
					}
					break;
				}
			}
		}

		ResetFlag();
	}

	public void OpenWalletPage(final Activity activity, String userId, HomePageSite.CmdProc proc)
	{
		SetJumpData(proc);

		boolean showHall = AppFeatureManager.getInstance().isTaskHallFeatureOn();
		boolean showMall = AppFeatureManager.getInstance().isBeautyMallSwitchOn();

		Bundle bundle = new Bundle();
		bundle.putString(KeyConstant.APP_VERSION, SysConfig.GetAppVerNoSuffix(activity));
		bundle.putString(KeyConstant.APP_NAME, APP_NAME);
		bundle.putString(KeyConstant.RECEIVER_ID, userId);
		bundle.putBoolean(KeyConstant.SHOW_HALL, showHall);
		bundle.putBoolean(KeyConstant.SHOW_MALL, showMall);
		Intent intent = new Intent(activity, WalletActivity.class);
		intent.putExtra(KeyConstant.BASE_DATA, bundle);
		activity.startActivity(intent);
		activity.overridePendingTransition(com.adnonstop.missionhall.R.anim.slide_from_right, com.adnonstop.missionhall.R.anim.slide_to_left);
	}

	public void OpenCredit(Activity activity, String id, HomePageSite.CmdProc proc)
	{
		SetJumpData(proc);
		BeautyUser.userId = null;
		BeautyUser.telNumber = null;
		if(id != null)
		{
			BeautyUser.userId = id;
		}
		String telNum = null;
		if(UserMgr.IsLogin(activity, null))
		{
			telNum = SettingInfoMgr.GetSettingInfo(activity).GetPoco2Phone();
		}
		if(telNum != null && telNum.length() > 4)
		{
			BeautyUser.telNumber = telNum;
		}

		Intent intent = new Intent(activity, MyIntegrationActivity.class);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
	}

	public static void ClearAll2()
	{
		if(mInstance != null)
		{
			mInstance.ClearAll();
		}
	}

	public void ClearAll()
	{
		if(EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().unregister(this);
			HallCallBack.getInstance().setOnShareMissionHallListener(null);
			HallCallBack.getInstance().setOnBindMolileListener(null);
			HallCallBack.getInstance().setOnLoginListener(null);
		}
		mCmdProc = null;
		mInstance = null;
		sOnActionCallBack = null;

		MallCallBack.getInstance().clearOpenMissionHallListener();
		MaineActivityLifeCicle.getInstance(MyFramework2App.getInstance().getApplication()).onMainActivityDestryed();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void missionOpenPage(final MissionJumpTask event)
	{
		if(event != null)
		{
			switch(event.getMhActionCode())
			{
				case MissionJumpTask.MH_ACTION_INNER_AD:
					if(event.getInnnerAdCode() == MissionJumpTask.MH_CODE_INNER_AD_REQUEST)
					{
						String link = event.getJumpLink();
						if(link != null)
						{
							BannerCore3.CmdStruct struct = BannerCore3.GetCmdStruct(link);
							if(struct != null && struct.m_cmd != null)
							{
								String cmd = struct.m_cmd.toLowerCase(Locale.ENGLISH);
								switch(cmd)
								{
									case "advbeauty":
									{
										if(struct.m_params != null && struct.m_params.length > 0)
										{
											String[] pair = struct.m_params[0].split("=");
											if(pair.length == 2)
											{
												if(pair[0].equals("channel_value"))
												{
													String id = pair[1];
													if(id != null && id.length() > 0)
													{
														AbsAdRes res = HomeAd.GetOneHomeRes(MyFramework2App.getInstance().getApplicationContext(), id);
														if(res != null)
														{
															//协议 正确，通知任务大厅
															MissionJumpTask eventSuccessful = new MissionJumpTask(MissionJumpTask.MH_ACTION_INNER_AD);
															eventSuccessful.setInnnerAdCode(MissionJumpTask.MH_CODE_INNER_AD_SUCCESSFUL);
															EventBus.getDefault().post(eventSuccessful);
															return;
														}
													}
												}
											}
										}
										break;
									}

									case "beautycamera":
									{
										//协议 正确，通知任务大厅
										MissionJumpTask eventSuccessful = new MissionJumpTask(MissionJumpTask.MH_ACTION_INNER_AD);
										eventSuccessful.setInnnerAdCode(MissionJumpTask.MH_CODE_INNER_AD_SUCCESSFUL);
										EventBus.getDefault().post(eventSuccessful);
										return;
									}
								}
							}
						}

						//协议 不正确，通知任务大厅
						MissionJumpTask eventFailed = new MissionJumpTask(MissionJumpTask.MH_ACTION_INNER_AD);
						eventFailed.setInnnerAdCode(MissionJumpTask.MH_CODE_INNER_AD_FAILED);
						EventBus.getDefault().post(eventFailed);
					}
					else if(event.getInnnerAdCode() == MissionJumpTask.MH_CODE_INNER_AD_OPEN)
					{
						String link = event.getJumpLink();
						if(link != null)
						{
							BannerCore3.CmdStruct struct = BannerCore3.GetCmdStruct(link);
							if(struct != null && struct.m_cmd != null)
							{
								String cmd = struct.m_cmd.toLowerCase(Locale.ENGLISH);
								switch(cmd)
								{
									case "advbeauty":
									{
										Handler handler = new Handler();
										handler.postDelayed(new Runnable()
										{
											@Override
											public void run()
											{
												String link = event.getJumpLink();
												if(!TextUtils.isEmpty(link))
												{
													//System.out.println(link);
													IFramework iFramework = MyFramework2App.getInstance().getFramework();
													if(iFramework != null && iFramework.GetTopPage() != null && iFramework.GetTopPage() instanceof Home4Page)
													{
														Home4Page page = (Home4Page)iFramework.GetTopPage();
														page.isShowModuleView(true, Home4Page.HOME, false);
													}
													BannerCore3.ExecuteCommand((Context)MyFramework2App.getInstance().getFramework(), link, mCmdProc);
												}
											}
										}, 100);
										break;
									}

									case "beautycamera":
									{
										Handler handler = new Handler();
										handler.postDelayed(new Runnable()
										{
											@Override
											public void run()
											{
												String link = event.getJumpLink();
												if(!TextUtils.isEmpty(link))
												{
													//System.out.println(link);
													BannerCore3.ExecuteCommand((Context)MyFramework2App.getInstance().getFramework(), link, mCmdProc);
												}
											}
										}, 100);
										return;
									}
								}
							}
						}
					}
					break;

				case MissionJumpTask.MH_ACTION_MATERIAL:
				{
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							String link = "beautyCamera://open=38";
							if(!TextUtils.isEmpty(link))
							{
								//System.out.println(link);
								BannerCore3.ExecuteCommand((Context)MyFramework2App.getInstance().getFramework(), link, mCmdProc);
							}
						}
					}, 100);
					break;
				}
				case MissionJumpTask.MH_ACTION_EXPAND_TIZHI:
				default:
				{
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							String link = event.getJumpLink();
							if(!TextUtils.isEmpty(link))
							{
								//System.out.println(link);
								BannerCore3.ExecuteCommand((Context)MyFramework2App.getInstance().getFramework(), link, mCmdProc);
							}
						}
					}, 100);
					break;
				}
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void missionToji(MissionTojiEvent event)
	{
		if(event != null && !TextUtils.isEmpty(event.id))
		{
			TongJi2.AddCountById(event.id);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void missionTojiBaidu(MHPageTojiEvent event)
	{
		if(event != null && event.getAction() != null && event.getContext() != null)
		{
			switch(event.getAction())
			{
				case START:
					if(event.getmProperty() == BaseEvent.PagerProperty.ACTIVITY)
					{
						StatService.onResume(event.getContext());
					}
					else
					{
						StatService.onPageStart(event.getContext(), event.getContent());
					}
					break;
				case END:
					if(event.getmProperty() == BaseEvent.PagerProperty.ACTIVITY)
					{
						StatService.onPause(event.getContext());
					}
					else
					{
						StatService.onPageEnd(event.getContext(), event.getContent());
					}
					break;
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void mallTj(PageTojiEvent event)
	{
		if(event != null && event.getAction() != null && event.getContext() != null)
		{
			switch(event.getAction())
			{
				case START:
					if(event.getProperty() == BaseEvent.PagerProperty.ACTIVITY)
					{
						StatService.onResume(event.getContext());
					}
					else
					{
						StatService.onPageStart(event.getContext(), event.getmContent());
					}
					break;
				case END:
					if(event.getProperty() == BaseEvent.PagerProperty.ACTIVITY)
					{
						StatService.onPause(event.getContext());
					}
					else
					{
						StatService.onPageEnd(event.getContext(), event.getmContent());
					}
					break;
			}
		}
	}

	static private OnActionCallBack sOnActionCallBack;

	static public void setsOnActionCallBack(OnActionCallBack onActionCallBack)
	{
		sOnActionCallBack = onActionCallBack;
	}

	public interface OnActionCallBack
	{
		void onLogin();

		void onBindPhone();
	}

	private static Handler mHandler = new Handler();
}

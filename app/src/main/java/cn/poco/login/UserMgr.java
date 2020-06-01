package cn.poco.login;

import android.content.Context;
import android.text.TextUtils;

import com.adnonstop.beautyaccount.firstopenapp.FirstOpenAppStaManager;
import com.adnonstop.beautymall.constant.BeautyUser;
import com.taotie.circle.Configure;

import java.io.File;
import java.net.HttpURLConnection;

import cn.poco.cloudAlbum.TransportImgs;
import cn.poco.exception.MyApplication;
import cn.poco.framework.MyFramework2App;
import cn.poco.holder.ObjHolder;
import cn.poco.loginlibs.ILogin;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.AppInterface;
import cn.poco.system.FolderMgr;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;
import cn.poco.utils.MyNetCore;

public class UserMgr
{
	public static final String HEAD_PATH = FolderMgr.getInstance().USER_INFO + File.separator + "head2.img"; //显示头像的真实路径
	public static final String HEAD_TEMP_PATH = FolderMgr.getInstance().USER_INFO_TEMP + File.separator + "head2.img"; //裁剪临时文件
	public static final String TEMP_IMG_PATH = FolderMgr.getInstance().USER_INFO_TEMP + File.separator + "temp2.img"; //下载的临时文件

	static
	{
		new File(FolderMgr.getInstance().USER_INFO).mkdirs();
		new File(FolderMgr.getInstance().USER_INFO_TEMP).mkdirs();
	}

	public UserMgr()
	{
	}

	public synchronized static UserInfo ReadCache(Context context)
	{
		UserInfo out = null;

		SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
		if(settingInfo != null)
		{
			String id = settingInfo.GetPoco2Id(true);
			String token = settingInfo.GetPoco2Token(true);
			if(id != null && id.length() > 0 & token != null && token.length() > 0)
			{
				out = new UserInfo();
				out.mZoneNum = settingInfo.GetPoco2AreaCode();
				out.mUserIcon = settingInfo.GetPoco2HeadUrl();
				out.mUserId = settingInfo.GetPoco2Id(false);
				out.mMobile = settingInfo.GetPoco2Phone();
				out.mBirthdayYear = settingInfo.GetPoco2BirthdayYear();
				out.mBirthdayMonth = settingInfo.GetPoco2BirthdayMonth();
				out.mBirthdayDay = settingInfo.GetPoco2BirthdayDay();
				try
				{
					String credit = settingInfo.GetPoco2Credit();
					if(credit != null) out.mFreeCredit = Integer.parseInt(credit);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
				out.mNickname = settingInfo.GetPocoNick();
				out.mSex = settingInfo.GetPoco2Sex();
				out.mLocationId = settingInfo.GetPoco2LocationId();
			}
		}

		return out;
	}

	public synchronized static void GetUserInfo(Context context, final ObjHolder<UserInfoCallback> holder)
	{
		SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
		if(settingInfo != null)
		{
			final String userId = settingInfo.GetPoco2Id(false);
			final String accessToken = settingInfo.GetPoco2Token(false);
			final ILogin iLogin = AppInterface.GetInstance(context);
			if(!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(accessToken))
			{
//				new Thread(new Runnable()
//				{
//					@Override
//					public void run()
//					{
//						Handler uiHandler = new Handler(Looper.getMainLooper());
//						final UserInfo userInfo = LoginUtils.getUserInfo(userId, accessToken, iLogin);
//						uiHandler.post(new Runnable()
//						{
//							@Override
//							public void run()
//							{
//								SaveCache(userInfo);
//								if(holder != null)
//								{
//									UserInfoCallback cb = holder.GetObj();
//									if(cb != null)
//									{
//										cb.onRefresh(userInfo);
//									}
//								}
//							}
//						});
//					}
//				}).start();

				LoginUtils2.getUserInfo(userId, accessToken, new HttpResponseCallback()
				{
					@Override
					public void response(Object object)
					{
						if(object == null) return;
						UserInfo userInfo = (UserInfo)object;
						SaveCache(userInfo);
						if(holder != null)
						{
							UserInfoCallback cb = holder.GetObj();
							if(cb != null) cb.onRefresh(userInfo);
						}
					}
				});
			}
		}
	}

	public synchronized static void SaveCache(UserInfo info)
	{
		if(info != null && !TextUtils.isEmpty(info.mUserId)){
			SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(MyApplication.getInstance());
			settingInfo.SetPoco2Id(info.mUserId);
			settingInfo.SetPoco2Phone(info.mMobile);
			settingInfo.SetPoco2HeadUrl(info.mUserIcon);
			settingInfo.SetPoco2Credit(Integer.toString(info.mFreeCredit));
			settingInfo.SetPoco2AreaCode(info.mZoneNum);
			settingInfo.SetPoco2BirthdayYear(info.mBirthdayYear);
			settingInfo.SetPoco2BirthdayMonth(info.mBirthdayMonth);
			settingInfo.SetPoco2BirthdayDay(info.mBirthdayDay);
			settingInfo.SetPocoNick(info.mNickname);
			settingInfo.SetPoco2Sex(info.mSex);
			settingInfo.SetPoco2LocationId(info.mLocationId);
			settingInfo.SetPoco2RegisterTime(info.mUserRegisterTime);
			SettingInfoMgr.Save(MyApplication.getInstance());

			BeautyUser.userId = info.mUserId;//福利社
			MyBeautyStat.checkLogin(MyApplication.getInstance());//统计
			try
			{
				Long userId = null;
				if(info.mUserId != null && info.mUserId.length() > 0)
				{
					userId = Long.parseLong(info.mUserId.trim());
				}
				FirstOpenAppStaManager.firstLogin(MyFramework2App.getInstance().getApplication(), MyApplication.HZ_APP_NAME, CommonUtils.GetAppVer(MyFramework2App.getInstance().getApplicationContext()), "0", userId, null);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}

			BeautyIMConnect.getInstance().connetIM();
		}
	}

	public interface LoginCallback
	{
		public void OnLogin(LoginInfo loginInfo);

		public void ExitLogin();
	}

	public interface UserInfoCallback
	{
		public void onRefresh(UserInfo userInfo);
	}

	/**
	 * 系统读取CONFIG后才能调用
	 *
	 * @return
	 */
	public static boolean IsLogin(final Context context, final LoginCallback cb)
	{
		boolean out = false;

		SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
		final String id = info.GetPoco2Id(true);
		final String accessToken = info.GetPoco2Token(true);
		if(id != null && id.length() > 0 && accessToken != null && accessToken.length() > 0)
		{
			out = true;
		}
		else
		{
			if(cb != null)
			{
				final String id2 = info.GetPoco2Id(false);
				final String accessToken2 = info.GetPoco2Token(false);
				final String refreshToken = info.GetPoco2RefreshToken();
				if(id2 != null && id2.length() > 0 && accessToken2 != null && accessToken2.length() > 0 && refreshToken != null && refreshToken.length() > 0)
				{
//					new Thread(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							final LoginInfo info = LoginUtils.refreshToken(id2, refreshToken, AppInterface.GetInstance(context));
//							Handler handler = new Handler(Looper.getMainLooper());
//							handler.post(new Runnable()
//							{
//								@Override
//								public void run()
//								{
//									if(info != null)
//									{
//										if(info.mCode == 0)
//										{
//											cb.OnLogin(info);
//										}
//										else if(info.mCode == 216)
//										{
//											cb.ExitLogin();
//										}
//									}
//									else
//									{
//										cb.ExitLogin();
//									}
//								}
//							});
//						}
//					}).start();
					LoginUtils2.refreshToken(id2, refreshToken, new HttpResponseCallback()
					{
						@Override
						public void response(Object object)
						{
							if(object == null)
							{
								cb.ExitLogin();
								return;
							}
							LoginInfo info = (LoginInfo)object;
							if(info.mCode == 0) cb.OnLogin(info);
							else cb.ExitLogin();
						}
					});
				}
				else
				{
					cb.ExitLogin();
				}
			}
		}

		return out;
	}

	/**
	 * 下载图片
	 *
	 * @param url
	 * @return
	 */
	public static String DownloadHeadImg(Context context, String url)
	{
		String out = null;

		if(url != null && url.length() > 0)
		{
			try
			{
				NetCore2 net = new MyNetCore(context);
				NetCore2.NetMsg msg = net.HttpGet(url, null, TEMP_IMG_PATH, null);
				if(msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK)
				{
					File file = new File(HEAD_PATH);
					if(file != null && file.exists())
					{
						file.delete();
					}
					file = new File(TEMP_IMG_PATH);
					if(file != null && file.exists())
					{
						file.renameTo(new File(HEAD_PATH));
					}
					out = HEAD_PATH;
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		return out;
	}

	/**
	 * 移动文件
	 *
	 * @param src 原始路径
	 * @param dst 目标路径
	 * @return
	 */
	public static boolean MoveFile(String src, String dst)
	{
		boolean out = false;

		if(src != null && dst != null)
		{
			File file2 = new File(dst);
			if(file2.exists())
			{
				file2.delete();
			}
			File file1 = new File(src);
			if(file1.exists())
			{
				out = file1.renameTo(file2);
			}
		}

		return out;
	}

	public static void ExitLogin(Context context)
	{
		BeautyIMConnect.getInstance().diconnectIM();

		Configure.clearLoginInfo();
		Configure.saveConfig(context);

		SettingInfoMgr.GetSettingInfo(context).ClearPoco2();
		SettingInfoMgr.Save(context);

		File file = new File(HEAD_PATH);
		if(file != null && file.exists())
		{
			file.delete();
		}
		file = new File(HEAD_TEMP_PATH);
		if(file != null && file.exists())
		{
			file.delete();
		}

		// 清除上传的记录
		TransportImgs.getInstance(context).clear();
	}
}

package cn.poco.login.site.activity;

import android.app.Activity;
import android.content.Intent;

import com.adnonstop.hzbeautycommonlib.login.event.LoginEvent;
import com.adnonstop.hzbeautycommonlib.login.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework;
import cn.poco.framework.RequestCode;
import cn.poco.framework2.BaseActivitySite;
import cn.poco.login.activity.LoginActivity;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;

/**
 * Created by Raining on 2017/11/3.
 * 独立登录Activity调用
 * <p/>
 * activity result
 * <br/>
 * {@link #KEY_ID}<br/>
 * {@link #KEY_TOKEN}<br/>
 * {@link #KEY_NICK_NAME}<br/>
 * {@link #KEY_PHONE}<br/>
 * {@link #KEY_SEX}<br/>
 */

public class LoginActivitySite extends BaseActivitySite
{
	public static final String KEY_ID = "account_id";
	public static final String KEY_TOKEN = "account_token";
	public static final String KEY_NICK_NAME = "account_nick_name";
	public static final String KEY_PHONE = "account_phone";
	public static final String KEY_SEX = "account_sex";

	public static final int REQUEST_CODE = RequestCode.LOGIN;

	@Override
	public Class<? extends Activity> getActivityClass()
	{
		return LoginActivity.class;
	}

	public void onBack(Activity activity)
	{
		MyFramework.SITE_Finish(activity, Activity.RESULT_CANCELED, null);

		EventBus.getDefault().post(new LogoutEvent());
		EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
	}

	public static void LoginSuccess(Activity activity)
	{
		Intent intent = new Intent();
		try
		{
			SettingInfo info = SettingInfoMgr.GetSettingInfo(activity);
			intent.putExtra(KEY_ID, info.GetPoco2Id(false));
			intent.putExtra(KEY_TOKEN, info.GetPoco2Token(false));
			intent.putExtra(KEY_NICK_NAME, info.GetPocoNick());
			intent.putExtra(KEY_PHONE, info.GetPoco2Phone());
			intent.putExtra(KEY_SEX, info.GetPoco2Sex());
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		MyFramework.SITE_Finish(activity, Activity.RESULT_OK, intent);

		try
		{
			SettingInfo info = SettingInfoMgr.GetSettingInfo(activity);
			LoginEvent event = new LoginEvent();
			event.mUserId = info.GetPoco2Id(false);
			event.mAccessToken = info.GetPoco2Token(false);
			event.mExpireTime = info.GetPoco2ExpiresIn();
			event.mUserName = info.GetPocoNick();
			event.mSex = info.GetPoco2Sex();
			event.mIconUrl = info.GetPoco2HeadUrl();
			event.mPhone = info.GetPoco2Phone();
			EventBus.getDefault().post(event);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
	}

	public void onLogin(Activity activity)
	{
		LoginSuccess(activity);
	}

	public void onBindBack(Activity activity)
	{
		MyFramework.SITE_Finish(activity, Activity.RESULT_CANCELED, null);

		//EventBus.getDefault().post(new LogoutEvent());
		EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
	}

	public void BindSuccess(Activity activity, HashMap<String, Object> data)
	{
		Intent intent = new Intent();
		try
		{
			SettingInfo info = SettingInfoMgr.GetSettingInfo(activity);
			intent.putExtra(LoginActivitySite.KEY_ID, info.GetPoco2Id(false));
			intent.putExtra(LoginActivitySite.KEY_TOKEN, info.GetPoco2Token(false));
			intent.putExtra(LoginActivitySite.KEY_NICK_NAME, info.GetPocoNick());
			intent.putExtra(LoginActivitySite.KEY_PHONE, info.GetPoco2Phone());
			intent.putExtra(LoginActivitySite.KEY_SEX, info.GetPoco2Sex());
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		MyFramework.SITE_Finish(activity, Activity.RESULT_OK, intent);
	}
}

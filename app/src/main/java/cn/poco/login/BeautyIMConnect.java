package cn.poco.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.adnonstop.beautymall.constant.BeautyUser;
import com.adnonstop.hzbeautycommonlib.login.event.LogoutEvent;
import com.circle.common.chatlist.ChatListPageNew;
import com.circle.common.mqtt_v2.BackgroundMsgService;
import com.circle.common.mqtt_v2.IMConnect;
import com.circle.ctrls.CustomGenericDialog;
import com.circle.utils.Utils;
import com.taotie.circle.Community;

import org.greenrobot.eventbus.EventBus;

import cn.poco.exception.MyApplication;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.framework2.BaseActivitySite;
import cn.poco.login.activity.LoginActivity;
import cn.poco.login.site.activity.LoginActivitySite;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import my.beautyCamera.R;

/**
 * Created by poco on 2017/12/26.
 */

public class BeautyIMConnect extends IMConnect
{
	private static BeautyIMConnect sBeautyIM = null;

	public static BeautyIMConnect getInstance()
	{
		if(sBeautyIM == null)
		{
			synchronized(BeautyIMConnect.class)
			{
				if(sBeautyIM == null)
				{
					sBeautyIM = new BeautyIMConnect();
				}
			}
		}
		return sBeautyIM;
	}

	@Override
	protected BackgroundMsgService.NotificationInfo getNotificationInfo()
	{
		BackgroundMsgService.NotificationInfo notificationInfo = new BackgroundMsgService.NotificationInfo();
		notificationInfo.appCode = Community.BEAUTY;
		notificationInfo.packName = "my.beautyCamera";
		notificationInfo.activityName = "my.beautyCamera.PocoCamera";
		notificationInfo.appNameResourceId = R.string.app_name_main;
		notificationInfo.notifyIcon = R.mipmap.ic_launcher;
		notificationInfo.stateIcon = R.drawable.notify_ic_statebar;

		return notificationInfo;
	}

	@Override
	protected Context getAppContext()
	{
		return MyApplication.getInstance().getApplicationContext();
	}

	@Override
	protected void imLogout()
	{
		//福利社特殊处理
		String userId = BeautyUser.userId;
		String telNumber = BeautyUser.telNumber;
		UserMgr.ExitLogin(MyApplication.getInstance());
		BeautyUser.userId = userId;
		BeautyUser.telNumber = telNumber;

		showKickLogoutDlg();
		//通知退出登录并关闭用户信息页
		EventCenter.sendEvent(EventID.USE_INFO_PAGE_FORCE_EXIT_LOGIN);
	}

	@Override
	protected IMUserInfo getIMUserInfo()
	{
		SettingInfo settingInfo02 = SettingInfoMgr.GetSettingInfo(MyApplication.getInstance());

		IMUserInfo imUserInfo = new IMUserInfo();
		imUserInfo.uid = settingInfo02.GetPoco2Id(false);
		imUserInfo.token = settingInfo02.GetPoco2Token(false);
		imUserInfo.nickName = settingInfo02.GetPocoNick();
		imUserInfo.refresh_token = settingInfo02.GetPoco2RefreshToken();
		return imUserInfo;
	}

	@Override
	protected boolean isLogin()
	{
		return !TextUtils.isEmpty(SettingInfoMgr.GetSettingInfo(MyApplication.getInstance()).GetPoco2Id(false));
	}

	@Override
	protected String getAppPath()
	{
		return Utils.getSdcardPath();
	}

	@Override
	protected String[] getShildIds()
	{
		String[] shildIds = {ChatListPageNew.OFFICIAL_IM_UID_CIRCLE, ChatListPageNew.OFFICIAL_IM_UID_JANE, ChatListPageNew.OFFICIAL_IM_UID_COMPLEX, ChatListPageNew.OFFICIAL_IM_UID_MAN, ChatListPageNew.OFFICIAL_IM_UID_INTER, ChatListPageNew.OFFICIAL_IM_UID_PLUS, ChatListPageNew.OFFICIAL_IM_UID_TINY, ChatListPageNew.OFFICIAL_IM_UID_POCO};
		return shildIds;
	}

	private static boolean sDialogOk = false;

	/**
	 * 显示被踢的对话框
	 */
	public static void showKickLogoutDlg()
	{
		Activity activity = MyFramework2App.getInstance().getActivity();
		if(activity==null){
			return;
		}
		sDialogOk = false;
		final CustomGenericDialog alertDialog = new CustomGenericDialog(activity);
		alertDialog.setText("", "您的账号于另一台设备登录而导致下线。如非本人操作，则密码可能已经泄露，请重新登录修改密码。");
		alertDialog.setNegativeButton("取消", new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				alertDialog.dismiss();
			}
		});
		alertDialog.setPositiveButton("重新登录", new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				sDialogOk = true;
				alertDialog.dismiss();

				Activity activity = MyFramework2App.getInstance().getActivity();
				if(activity != null)
				{
					Intent intent = new Intent();
					intent.putExtra("type", LoginActivity.LOGIN);
					BaseActivitySite.setClass(intent, activity, LoginActivitySite.class);
					activity.startActivityForResult(intent, LoginActivitySite.REQUEST_CODE);
					activity.overridePendingTransition(0, 0);
				}
			}
		});
		alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				if(!sDialogOk)
				{
					EventBus.getDefault().post(new LogoutEvent());
					EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
				}
			}
		});
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
        EventCenter.sendEvent(EventID.SHOW_MULTI_DEVICE_LOGIN_DIALOG);
	}
}

package cn.poco.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.circle.common.serverapi.PageDataInfo;
import com.circle.common.share.ShareData;
import com.circle.framework.ICallback;
import com.circle.framework.OnOutSiteLoginListener;
import com.taotie.circle.Community;
import com.taotie.circle.CommunityLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.poco.beautify4.Beautify4Page;
import cn.poco.community.site.CommunitySite;
import cn.poco.community.site.PublishOpusSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.login.UserMgr;
import cn.poco.login.site.activity.LoginActivitySite;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.SysConfig;
import cn.poco.utils.Utils;

/**
 * Created by lgh on 2017/8/24.
 */
public class PublishOpusPage extends IPage
{
	private CommunityLayout mCommunityLayout;
	private PublishOpusSite mSite;
	private Context mContext;

	public PublishOpusPage(Context context, BaseSite site)
	{
		super(context, site);
		this.mSite = (PublishOpusSite)site;
		this.mContext = context;
		init(context);
	}

	private void init(Context context)
	{
		mCommunityLayout = new CommunityLayout(context);
		LayoutParams fp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mCommunityLayout, fp);
		mCommunityLayout.setOnOutsideCallback(new ICallback()
		{

			@Override
			public void onBack(Object... obj)
			{
				setFullScreen((Activity)mContext);
				boolean isSuccess = false;
				if(obj != null && obj.length > 0)
				{
					isSuccess = (boolean)obj[0];
				}
				mSite.onBack(mContext, isSuccess);
			}

			@Override
			public void onLogin(OnOutSiteLoginListener listener)
			{

			}

			@Override
			public void onRegistration()
			{

			}

			@Override
			public void onBindPhone(OnOutSiteLoginListener listener)
			{

			}

			@Override
			public void logout(boolean isLogoutOneself)
			{

			}

			@Override
			public void openFunction(HashMap<String, Object> params)
			{
				setFullScreen((Activity)mContext);
				mSite.openCamera(mContext,params);
			}

			@Override
			public void openPhotoPicker(HashMap<String, Object> params)
			{

			}

			@Override
			public void onNewMessage(int type, int count)
			{

			}

			@Override
			public void onShare(int where, PageDataInfo.ShareInfo2 shareInfo)
			{

			}

			@Override
			public void onSoftWen(String url)
			{

			}

			@Override
			public void onJoinActivity(String agreement_url)
			{

			}

			@Override
			public void refreshUserInfo(PageDataInfo.UserInfo userInfo)
			{

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

			}
		});
		mCommunityLayout.onStart();
		setUserInfo(context);
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
			mCommunityLayout.setLoginInfo(context,loginInfo,true);
		}
		else
		{
			mCommunityLayout.clearLoginInfo();
		}
	}

	/**
	 * 退出全屏
	 *
	 * @param activity
	 */
	private void quitFullScreen(Activity activity)
	{
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 设置全屏
	 *
	 * @param activity
	 */
	private void setFullScreen(Activity activity)
	{
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private String reSaveImage(String tempPath)
	{
		try
		{
			String path = SysConfig.GetAppPath()+"/XAlien/local/pageimgcache/"+System.currentTimeMillis()+".jpg";
			FileUtils.copyFile(new File(tempPath), new File(path));
//			Utils.FileScan(getContext(), path);
			return path;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{
		quitFullScreen((Activity)mContext);
		String path = (String)params.get("path");
		if(!path.contains("."))
		{
			path = reSaveImage(path);
		}
		if(TextUtils.isEmpty(path)){
			mSite.onBack(getContext(),false);
//			mCommunityLayout.onClose();
			return ;
		}
		int type = (int)params.get("type");
		String content = (String)params.get("content");
		String extraStr = "";
		if(params.containsKey("extra") && !TextUtils.isEmpty((String)params.get("extra")))
		{
			extraStr = params.get("extra").toString();
		}
		int come_from = 3;//标识来自美人相机的分享
		mCommunityLayout.openPublishPage(path, content, type, extraStr, ShareData.COME_FROM_BEAUTY);
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		quitFullScreen((Activity)mContext);
		if (siteID == SiteID.LOGIN || siteID == SiteID.RESETPSW || siteID==SiteID.REGISTER_DETAIL) {
			//登录和绑定手机并完善资料后同步社区用户数据
			setUserInfo(mContext);
		} else
		{
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
					mCommunityLayout.onPageResult(1, imgs, extra);
				}
				else if(params.containsKey("videoPath"))
				{
					String videoPath = params.get("videoPath").toString();
					mCommunityLayout.onPageResult(2, new String[]{videoPath}, extra);
				}
				else if(params.containsKey("gifPath"))
				{
					String gifPath = params.get("gifPath").toString();
					mCommunityLayout.onPageResult(3, new String[]{gifPath}, extra);
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
	public boolean onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode== LoginActivitySite.REQUEST_CODE)
		{
			setUserInfo(mContext);
		}
		if (mCommunityLayout.onActivityResult(requestCode, resultCode, data)) {
			return true;
		}
		return super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBack()
	{
		mCommunityLayout.onBack();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		quitFullScreen((Activity)mContext);
		mCommunityLayout.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mCommunityLayout.onPause();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		mCommunityLayout.onStop();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		mCommunityLayout.onStart();
	}

	@Override
	public void onClose()
	{
		super.onClose();
		mCommunityLayout.onClose();
	}

}

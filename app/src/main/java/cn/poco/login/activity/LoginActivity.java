package cn.poco.login.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.framework.BaseFwActivity;
import cn.poco.framework.BaseSite;
import cn.poco.framework2.Framework2;
import cn.poco.login.site.BindPhonePageSite401;
import cn.poco.login.site.LoginPageSite401;
import cn.poco.login.site.activity.LoginActivitySite;
import cn.poco.loginlibs.info.LoginInfo;

/**
 * Created by Raining on 2017/11/3.
 * <p/>
 * 登录activity
 * <p/>
 * type:int 类型(必填) {@link #BIND} {@link #LOGIN}<br/>
 * phoneNum:String 手机号码,可null<br/>
 */

public class LoginActivity extends BaseFwActivity<LoginActivitySite>
{
	public static final String LOGIN = "login";
	public static final String BIND = "bind";

	@Override
	protected void onAppMapGate(Context context, Bundle savedInstanceState, boolean newActivity)
	{
		String type = null;

		String phoneNum = null;

		String userId = null;
		String accessToken = null;

		Intent intent = getIntent();
		if(intent != null)
		{
			type = intent.getStringExtra("type");
			if(type == null)
			{
				type = "";
			}
			switch(type)
			{
				case LOGIN:
				{
					phoneNum = intent.getStringExtra("phoneNum");
					break;
				}

				case BIND:
				{
					userId = intent.getStringExtra("userId");
					accessToken = intent.getStringExtra("accessToken");
					break;
				}

				default:
				{
					Uri uri = intent.getData();
					if(uri != null)
					{
						type = uri.getAuthority();
						if(type == null)
						{
							type = "";
						}
						switch(type)
						{
							case LOGIN:
							{
								phoneNum = uri.getQueryParameter("phoneNum");
								break;
							}
							case BIND:
							{
								userId = uri.getQueryParameter("userId");
								accessToken = uri.getQueryParameter("accessToken");
								break;
							}
						}
					}
					break;
				}
			}
		}
		if(newActivity)
		{
			ArrayList<BaseSite> arr = mFramework.GetCurrentSiteList();
			if(arr != null && arr.size() > 0)
			{
				mFramework.onCreate(context, savedInstanceState);
			}
			else
			{
				if(type != null)
				{
					switch(type)
					{
						case LOGIN:
						{
							HashMap<String, Object> params = new HashMap<>();
							params.put("noExitAnim", true);
							if(phoneNum != null && phoneNum.length() > 0)
							{
								params.put("phoneNum", phoneNum);
							}
							SITE_Open(context, true, LoginPageSite401.class, params, Framework2.ANIM_NONE);
							break;
						}

						case BIND:
						{
							HashMap<String, Object> params = new HashMap<>();
							if(userId != null && userId.length() > 0 && accessToken != null && accessToken.length() > 0)
							{
								LoginInfo lInfo = new LoginInfo();
								lInfo.mUserId = userId;
								lInfo.mAccessToken = accessToken;
								params.put("loginInfo", lInfo);
							}
							SITE_Open(context, true, BindPhonePageSite401.class, params, Framework2.ANIM_NONE);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	protected void InitData(@Nullable Bundle savedInstanceState)
	{
		super.InitData(savedInstanceState);

		if(mSite == null)
		{
			mSite = new LoginActivitySite();
		}
	}
}

package cn.poco.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.login.LoginPage;
import my.beautyCamera.BaseActivity;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

/**
 * Created by pocouser on 2017/6/13.
 */

public class BlogLoginActivity extends BaseActivity
{
	private SinaBlog mSina;
	private QzoneBlog2 mQQ;
	private WeiXinBlog mWechat;
	private Intent mIntent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SharePage.initBlogConfig(this);

		mIntent = getIntent();
		String type = getIntent().getExtras().getString("type");
		if(type == null || type.length() <= 0)
		{
			setResult(RESULT_CANCELED, mIntent);
			finish();
		}
		if(type.equals("sina"))
		{
			loginSina();
		}
		else if(type.equals("qq"))
		{
			loginQQ();
		}
		else if(type.equals("wechat"))
		{
			loginWechat();
		}
		else
		{
			setResult(RESULT_CANCELED, mIntent);
			finish();
		}
	}

	private void loginSina()
	{
		mSina = new SinaBlog(this);
		mSina.bindSinaWithSSO(new SinaBlog.BindSinaCallback()
		{
			@Override
			public void success(final String accessToken, String expiresIn, String uid, String userName, String nickName)
			{
				mIntent.putExtra("accessToken", accessToken);
				mIntent.putExtra("expiresIn", expiresIn);
				mIntent.putExtra("uid", uid);
				mIntent.putExtra("userName", userName);
				mIntent.putExtra("nickName", nickName);
				setResult(RESULT_OK, mIntent);
				finish();
			}

			@Override
			public void fail()
			{
				switch(mSina.LAST_ERROR)
				{
					case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
						Toast.makeText(BlogLoginActivity.this, getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
						break;

					default:
						Toast.makeText(BlogLoginActivity.this, getResources().getString(R.string.share_sina_bind_fail), Toast.LENGTH_LONG).show();
						break;
				}
				setResult(RESULT_CANCELED, mIntent);
				finish();
			}
		});
	}

	private void loginQQ()
	{
		mQQ = new QzoneBlog2(this);
		mQQ.bindQzoneWithSDK(new QzoneBlog2.BindQzoneCallback()
		{
			@Override
			public void success(String accessToken, String expiresIn, String openId, String nickName)
			{
				mIntent.putExtra("accessToken", accessToken);
				mIntent.putExtra("expiresIn", expiresIn);
				mIntent.putExtra("openid", openId);
				mIntent.putExtra("nickName", nickName);
				setResult(RESULT_OK, mIntent);
				finish();
			}

			@Override
			public void fail()
			{
				switch(mQQ.LAST_ERROR)
				{
					case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
						Toast.makeText(BlogLoginActivity.this, getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
						break;

					default:
						Toast.makeText(BlogLoginActivity.this, getResources().getString(R.string.share_qq_bind_fail), Toast.LENGTH_LONG).show();
						break;
				}
				setResult(RESULT_CANCELED, mIntent);
				finish();
			}
		});
	}

	private void loginWechat()
	{
		mWechat = new WeiXinBlog(this);
		if(mWechat.registerWeiXin())
		{
			mWechat.getCode();
			SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
			{
				@Override
				public void onCallFinish(int result)
				{
					switch (result)
					{
						case BaseResp.ErrCode.ERR_OK:
							if(LoginPage.mWeiXinGetCode != null && LoginPage.mWeiXinGetCode.length() > 0)
							{
								loginWechat2(LoginPage.mWeiXinGetCode);
								LoginPage.mWeiXinGetCode = null;
							}
							break;

						default:
							setResult(RESULT_CANCELED, mIntent);
							finish();
							break;
					}
					SendWXAPI.removeListener(this);
				}
			};
			SendWXAPI.addListener(listener);
		}
		else
		{
			switch (mWechat.LAST_ERROR) {
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					Toast.makeText(BlogLoginActivity.this, this.getResources().getString(R.string.loginstyle_wechattips), Toast.LENGTH_SHORT).show();
					break;
				case WeiboInfo.BLOG_INFO_CLIENT_VERSION_LOW:
					Toast.makeText(BlogLoginActivity.this, this.getResources().getString(R.string.loginstyle_wechattips2), Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(BlogLoginActivity.this, this.getResources().getString(R.string.loginstyle_wechatbinderror), Toast.LENGTH_SHORT).show();
					break;
			}
			setResult(RESULT_CANCELED, mIntent);
			finish();
		}
	}

	public void loginWechat2(final String code)
	{
		if(mWechat == null)
		{
			setResult(RESULT_CANCELED, mIntent);
			finish();
		}

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				mWechat.setCode(code);
				if(mWechat.getAccessTokenAndOpenid())
				{
					if(mWechat.getUserUnionid())
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								mIntent.putExtra("accessToken", mWechat.getAccessToken());
								mIntent.putExtra("refreshToken", mWechat.getRefreshToken());
								mIntent.putExtra("openid", mWechat.getOpenid());
								mIntent.putExtra("unionid", mWechat.getUnionid());
								mIntent.putExtra("expiresIn", mWechat.getExpiresin());
								setResult(RESULT_OK, mIntent);
								finish();
							}
						});
						return;
					}
				}
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						setResult(RESULT_CANCELED, mIntent);
						finish();
					}
				});
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(mSina != null) mSina.onActivityResult(requestCode, resultCode, data, -1);
		if(mQQ != null) mQQ.onActivityResult(requestCode, resultCode, data);
	}
}

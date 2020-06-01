package cn.poco.taskCenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.adnonstop.hzbeautycommonlib.Constant.CommonConstant;
import com.adnonstop.hzbeautycommonlib.ShareValueHZCommon;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.Tools;
import cn.poco.framework.FileCacheMgr;
import cn.poco.setting.SettingPage;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.BaseActivity;
import my.beautyCamera.R;

/**
 * Created by POCO on 2017/6/7.
 */

public class SendBlogActivity extends BaseActivity
{
	public static final int RESULT_CODE = 1232;

	private ShareTools m_share;
	private boolean send_twitter = false;
	private boolean mIsFinish;

	private ShareValueHZCommon.SocialNetwork mType;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if(savedInstanceState != null)
		{
			mIsFinish = savedInstanceState.getBoolean("finish", false);
		}
		if(mIsFinish)
		{
			finish();
		}
		else
		{
			FrameLayout fr = new FrameLayout(this);
			fr.setBackgroundColor(0x80000000);
			{
				WaitAnimDialog.WaitAnimView loading = new WaitAnimDialog.WaitAnimView(this);
				FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER;
				fr.addView(loading, fl);
			}
			setContentView(fr);
			fr.setClickable(true);
			fr.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					SendBlogActivity.this.finish();
				}
			});

			final String shareTitle = getIntent().getStringExtra("shareTitle");
			final String shareContent = getIntent().getStringExtra("shareContent");
			final String shareImgUrl = getIntent().getStringExtra("shareImgUrl");
			final String shareLinkUrl = getIntent().getStringExtra("shareLinkUrl");
			final Object type = getIntent().getExtras().get("type");

			SharePage.initBlogConfig(this);
			m_share = new ShareTools(this);
			m_share.needAddIntegral(false);

			if(shareImgUrl.startsWith("http"))
			{
				final String shareIconPath = FileCacheMgr.GetAppPath();
				Tools.downloadImageUrl(shareImgUrl, shareIconPath, new Tools.DownloadCompleteCallback()
				{
					@Override
					public void complete(boolean success)
					{
						share(shareTitle, shareContent, shareIconPath, shareLinkUrl, type);
					}
				});
			}
			else
			{
				share(shareTitle, shareContent, shareImgUrl, shareLinkUrl, type);
			}
			mIsFinish = true;
		}
	}

	private void share(final String shareTitle, final String shareContent, final String shareImgUrl, final String shareLinkUrl, Object type)
	{
		if(type instanceof ShareValueHZCommon.SocialNetwork)
		{
			mType = (ShareValueHZCommon.SocialNetwork)type;
			switch(mType)
			{
				case WEIBO:
					String content = "";
					if(shareTitle != null && shareTitle.length() > 0)
					{
						content += shareTitle;
					}
					if(shareContent != null && shareContent.length() > 0)
					{
						if(content.length() > 0) content += "，";
						content += shareContent;
					}
					if(shareLinkUrl != null && shareLinkUrl.length() > 0)
					{
						if(content.length() > 0) content += " ";
						content += shareLinkUrl;
					}
					if(SettingPage.checkSinaBindingStatus(this))
					{
						m_share.sendToSinaBySDK(content, shareImgUrl, new ShareTools.SendCompletedListener()
						{
							@Override
							public void getResult(Object result)
							{
								int response = (int)result;
								if(response == WBConstants.ErrorCode.ERR_OK)
								{
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_SUCCESS_RESULT_CODE, new Intent());
								}
								else
								{
									if(response == WBConstants.ErrorCode.ERR_CANCEL) Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
									else if(response == WBConstants.ErrorCode.ERR_FAIL) Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
								}
								finish();
							}
						});
					}
					else
					{
						final String content2 = content;
						m_share.bindSina(new SharePage.BindCompleteListener()
						{
							@Override
							public void success()
							{
								m_share.sendToSinaBySDK(content2, shareImgUrl, new ShareTools.SendCompletedListener()
								{
									@Override
									public void getResult(Object result)
									{
										int response = (int)result;
										if(response == WBConstants.ErrorCode.ERR_OK)
										{
											Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
											setResult(CommonConstant.SHARE_SUCCESS_RESULT_CODE, new Intent());
										}
										else
										{
											if(response == WBConstants.ErrorCode.ERR_CANCEL) Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
											else if(response == WBConstants.ErrorCode.ERR_FAIL) Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
											setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
										}
										finish();
									}
								});
							}

							@Override
							public void fail()
							{
								setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
								finish();
							}
						});
					}
					break;

				case QQ:
					ShareTools.SendCompletedListener qq_callback = new ShareTools.SendCompletedListener()
					{
						@Override
						public void getResult(Object result)
						{
							switch((int)result)
							{
								case QzoneBlog2.SEND_SUCCESS:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_SUCCESS_RESULT_CODE, new Intent());
									break;

								case QzoneBlog2.SEND_FAIL:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								case QzoneBlog2.SEND_CANCEL:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								default:
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;
							}
							finish();
						}
					};

					if(shareTitle != null && shareTitle.length() > 0 && shareContent != null && shareContent.length() > 0 && shareLinkUrl != null && shareLinkUrl.length() > 0)
					{
						m_share.sendUrlToQQ(shareTitle, shareContent, shareImgUrl, shareLinkUrl, qq_callback);
					}
					else
					{
						m_share.sendToQQ(shareImgUrl, qq_callback);
					}
					break;

				case QZONE:
					ShareTools.SendCompletedListener qzone_callback = new ShareTools.SendCompletedListener()
					{
						@Override
						public void getResult(Object result)
						{
							switch((int)result)
							{
								case QzoneBlog2.SEND_SUCCESS:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_SUCCESS_RESULT_CODE, new Intent());
									break;

								case QzoneBlog2.SEND_FAIL:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								case QzoneBlog2.SEND_CANCEL:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								default:
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;
							}
							finish();
						}
					};
					if(shareTitle != null && shareTitle.length() > 0 && shareContent != null && shareContent.length() > 0 && shareLinkUrl != null && shareLinkUrl.length() > 0)
					{
						m_share.sendUrlToQzone(shareImgUrl, shareTitle, shareContent, shareLinkUrl, qzone_callback);
					}
					else
					{
						m_share.sendToQzone(shareImgUrl, qzone_callback);
					}
					break;

				case WECHAT:
					ShareTools.SendCompletedListener wechat_callback = new ShareTools.SendCompletedListener()
					{
						@Override
						public void getResult(Object result)
						{
							switch((int)result)
							{
								case BaseResp.ErrCode.ERR_OK:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_SUCCESS_RESULT_CODE, new Intent());
									break;

								case BaseResp.ErrCode.ERR_USER_CANCEL:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								case BaseResp.ErrCode.ERR_AUTH_DENIED:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								default:
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;
							}
							finish();
						}
					};

					if(shareTitle != null && shareTitle.length() > 0 && shareContent != null && shareContent.length() > 0 && shareLinkUrl != null && shareLinkUrl.length() > 0)
					{
						m_share.sendUrlToWeiXin(shareImgUrl, shareLinkUrl, shareTitle, shareContent, true, wechat_callback);
					}
					else
					{
						m_share.sendToWeiXin(shareImgUrl, true, wechat_callback);
					}
					break;

				case WECHAT_MOMENT:
					ShareTools.SendCompletedListener wechat_moment_callback = new ShareTools.SendCompletedListener()
					{
						@Override
						public void getResult(Object result)
						{
							switch((int)result)
							{
								case BaseResp.ErrCode.ERR_OK:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_SUCCESS_RESULT_CODE, new Intent());
									break;

								case BaseResp.ErrCode.ERR_USER_CANCEL:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								case BaseResp.ErrCode.ERR_AUTH_DENIED:
									Toast.makeText(SendBlogActivity.this, getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;

								default:
									setResult(CommonConstant.SHARE_FAIL_RESULT_CODE, new Intent());
									break;
							}
							finish();
						}
					};

					if(shareTitle != null && shareTitle.length() > 0 && shareContent != null && shareContent.length() > 0 && shareLinkUrl != null && shareLinkUrl.length() > 0)
					{
						m_share.sendUrlToWeiXin(shareImgUrl, shareLinkUrl, shareTitle, shareContent, false, wechat_moment_callback);
					}
					else
					{
						m_share.sendToWeiXin(shareImgUrl, false, wechat_moment_callback);
					}
					break;

				case FACEBOOK:
					m_share.sendUrlToFacebook(shareTitle, shareContent, shareLinkUrl, new ShareTools.SendCompletedListener()
					{
						@Override
						public void getResult(Object result)
						{
							finish();
						}
					});
					break;

				case TWITTER:
					send_twitter = true;
					boolean success = m_share.sendToTwitter(shareImgUrl, shareContent + " " + shareLinkUrl);
					if(!success)
					{
						finish();
					}
					break;

				default:
					finish();
					break;
			}
		}
		else
		{
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean("finish", mIsFinish);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy()
	{
		m_share = null;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		m_share.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume()
	{
		if(send_twitter)
		{
			finish();
		}
		super.onResume();
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();

		//解决去到第三方界面按home键回到app的bug
//		if(mType == ShareValueHZCommon.SocialNetwork.WECHAT || mType == ShareValueHZCommon.SocialNetwork.WECHAT_MOMENT)
//		{
//			Handler handler = new Handler();
//			handler.postDelayed(new Runnable()
//			{
//				@Override
//				public void run()
//				{
//					SendBlogActivity.this.finish();
//				}
//			}, 60);
//		}
	}
}

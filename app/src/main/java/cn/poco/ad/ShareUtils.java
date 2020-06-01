package cn.poco.ad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.poco.blogcore.PocoBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.QzoneBlog2.SendQQorQzoneCompletelistener;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.setting.SettingPage;
import cn.poco.share.Constant;
import cn.poco.share.LoginDialog;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.share.SinaRequestActivity;
import cn.poco.utils.WaitDialog;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

public class ShareUtils
{
	public static final int WEIXIN = 1; //微信
	public static final int FRIEND = 2; //微信朋友圈
	public static final int TENCENT_BLOG = 3; //腾讯微博
	public static final int QQ_ZONE = 4; //qq空间
	public static final int SINA_BLOG = 5; //新浪微博
	public static final int POCO_WORLD = 6; //poco世界

	private static final int TOKEN_TIMEOUT = 43200; //微博有效期预留12小时

	private Context m_context;
	private OnCompleteCallback m_cb;
	private WaitDialog m_progressDlg;

	//QQ空间
	private QzoneBlog2 m_qzone;

	//sina blog
	private SinaBlog m_sina;

	//poco
	private PocoBlog m_poco;
	private LoginDialog m_loginDlg;
	private int m_bindByOtherAccount = -1;

	//微信
	private WeiXinBlog m_weiXin;

	public ShareUtils(Context context, OnCompleteCallback cb)
	{
		m_context = context;
		SharePage.initBlogConfig(context);
		m_progressDlg = new WaitDialog(m_context, R.style.waitDialog);
		m_cb = cb;
	}

	/**
	 * 判断是否已经绑定QQ空间
	 * 
	 * @return true 表示已经绑定了QQ空间
	 */
	public boolean isQzoneBinded()
	{
		return SettingPage.checkQzoneBindingStatus(m_context);
	}

	/**
	 * 绑定QQ空间
	 */
	public void bindQZone()
	{
		final boolean bindPoco = m_bindByOtherAccount == SharePage.QZONE ? true : false;
		m_bindByOtherAccount = -1;

		if(m_qzone == null)
		{
			m_qzone = new QzoneBlog2(m_context);
		}
		m_qzone.bindQzoneWithSDK(new QzoneBlog2.BindQzoneCallback()
		{

			@Override
			public void success(String accessToken, String expiresIn, String openId, String nickName)
			{
				SettingInfoMgr.GetSettingInfo(m_context).SetQzoneAccessToken(accessToken);
				SettingInfoMgr.GetSettingInfo(m_context).SetQzoneOpenid(openId);
				SettingInfoMgr.GetSettingInfo(m_context).SetQzoneExpiresIn(expiresIn);
				SettingInfoMgr.GetSettingInfo(m_context).SetQzoneSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
				SettingInfoMgr.GetSettingInfo(m_context).SetQzoneUserName(nickName);

				m_cb.success(QQ_ZONE);
			}

			@Override
			public void fail()
			{
				switch(m_qzone.LAST_ERROR)
				{
					case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
						AlertDialog dlg = new AlertDialog.Builder(m_context).create();
						dlg.setTitle("提示");
						dlg.setMessage("还没有安装最新手机QQ，需要安装后才能绑定");
						dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener)null);
						dlg.show();
						break;

					default:
						SharePage.msgBox(m_context, "绑定QQ空间失败");
						break;
				}
			}
		});
	}

	public void sendToQQ(String pic, String title, String content, String url)
	{
		if(m_qzone == null)
			m_qzone = new QzoneBlog2(m_context);
		m_qzone.sendUrlToQQ(pic, title, content, url);
	}

	/**
	 * 发送到QQ空间
	 */
	public void sendToQzone(String content, String url, String file)
	{
		if(m_qzone == null) m_qzone = new QzoneBlog2(m_context);
		m_qzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(m_context).GetQzoneAccessToken());
		m_qzone.setOpenId(SettingInfoMgr.GetSettingInfo(m_context).GetQzoneOpenid());
		m_qzone.sendToQzone2(content, file, "来自美人相机", url);
		m_qzone.setSendQQorQzoneCompletelistener(new SendQQorQzoneCompletelistener()
		{	
			@Override
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS)	
				{
					addTongJi();
					if(m_cb != null) m_cb.shareSuccess(QQ_ZONE);
				}
			}
		});
	}

	/**
	 * 纯图片发送到QQ空间
	 * @param file
	 */
	public void sendToQzone(String file)
	{
		if(m_qzone == null) m_qzone = new QzoneBlog2(m_context);
		m_qzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(m_context).GetQzoneAccessToken());
		m_qzone.setOpenId(SettingInfoMgr.GetSettingInfo(m_context).GetQzoneOpenid());
		m_qzone.sendToPublicQzone(1, file);
		m_qzone.setSendQQorQzoneCompletelistener(new SendQQorQzoneCompletelistener()
		{	
			@Override
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS)	
				{
					addTongJi();
					if(m_cb != null) m_cb.shareSuccess(QQ_ZONE);
				}
			}
		});
	}
	
	/**
	 * 判断新浪微博是否绑定
	 * 
	 * @return
	 */
	public boolean isSinaBined()
	{
		return SettingPage.checkSinaBindingStatus(m_context);
	}

	/**
	 * 绑定新浪微博
	 */
	public void bindSina()
	{
		if(m_sina == null)
		{
			m_sina = new SinaBlog(m_context);
		}
		m_sina.bindSinaWithSSO(new SinaBlog.BindSinaCallback()
		{

			@Override
			public void success(final String accessToken, String expiresIn, String uid, String userName, String nickName)
			{
				SettingInfoMgr.GetSettingInfo(m_context).SetSinaAccessToken(accessToken);
				SettingInfoMgr.GetSettingInfo(m_context).SetSinaUid(uid);
				SettingInfoMgr.GetSettingInfo(m_context).SetSinaExpiresIn(expiresIn);
				SettingInfoMgr.GetSettingInfo(m_context).SetSinaSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
				SettingInfoMgr.GetSettingInfo(m_context).SetSinaUserName(userName);
				SettingInfoMgr.GetSettingInfo(m_context).SetSinaUserNick(nickName);

				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						m_sina.flowerCameraSinaWeibo(Constant.sinaUserId, accessToken);
					}
				}).start();
				m_cb.success(SINA_BLOG);
			}

			@Override
			public void fail()
			{
				switch(m_sina.LAST_ERROR)
				{
					case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
						SharePage.msgBox(m_context, "还没有安装最新的新浪微博客户端");
						break;

					default:
						SharePage.msgBox(m_context, "绑定新浪微博失败");
						break;
				}
			}
		});
	}

	/**
	 * 发送新浪微博
	 */
	public void sendToSina(final String content, final String file)
	{
		if(m_sina == null) m_sina = new SinaBlog(m_context);
		if(!m_sina.checkSinaClientInstall())
		{
			uploadFailedOnUIThread(m_context, SINA_BLOG);
			return;
		}
		m_sina.SetAccessToken(SettingInfoMgr.GetSettingInfo(m_context).GetSinaAccessToken());
		m_sina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
		{
			@Override
			public void response(boolean send_success, int response_code)
			{
				if(send_success)
				{
					switch(response_code)
					{
						case WBConstants.ErrorCode.ERR_OK:
							ShareTools.addIntegral(m_context);
							showToastOnUIThread(m_context, "分享到新浪微博成功", Toast.LENGTH_SHORT);
							if(m_cb != null) m_cb.shareSuccess(SINA_BLOG);
							break;

						case WBConstants.ErrorCode.ERR_CANCEL:
						case WBConstants.ErrorCode.ERR_FAIL:
						case SinaBlog.NO_RESPONSE:
							uploadFailedOnUIThread(m_context, SINA_BLOG);
							break;

						case WeiboInfo.BLOG_INFO_IMAGE_SIZE_TOO_LARGE:
							showToastOnUIThread(m_context, "发送的图片超过限制", Toast.LENGTH_LONG);
							break;
					}
				}
				else
				{
					uploadFailedOnUIThread(m_context, SINA_BLOG);
				}
			}
		});

		Intent intent = new Intent(m_context, SinaRequestActivity.class);
		intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT_AND_PIC);
		intent.putExtra("pic", file);
		intent.putExtra("content", content);
		((Activity) m_context).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
	}

	public boolean isQQBined()
	{
		return SettingPage.checkQzoneBindingStatus(m_context);
	}

	/**
	 * 发送图片到微信
	 * 
	 * @param path
	 *            url
	 * @param thumb
	 *            缩略图
	 * @param WXSceneSession
	 *            true 好友， false 朋友圈
	 * @return
	 */
	public boolean sendToWeiXin(final String path, final Bitmap thumb, final boolean WXSceneSession)
	{
		if(m_weiXin == null)
		{
			m_weiXin = new WeiXinBlog(m_context);
		}
		setWaitDlg(true, "");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean isInstalled = m_weiXin.isWXAppInstalled();
				if(!isInstalled)
				{
					showToastOnUIThread(m_context, "还没有安装微信，需要安装后才能分享", Toast.LENGTH_LONG);
				}
				boolean isSuccess = m_weiXin.sendToWeiXin(path, thumb, WXSceneSession);
				if(!isSuccess)
				{
					switch(m_weiXin.LAST_ERROR)
					{
						case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
							showToastOnUIThread(m_context, "图片不存在，请检查图片路径。", Toast.LENGTH_LONG);
							break;

						case WeiboInfo.BLOG_INFO_OTHER_ERROR:
							showToastOnUIThread(m_context, "URL地址出现问题。", Toast.LENGTH_LONG);
							break;

						case WeiboInfo.BLOG_INFO_CLIENT_VERSION_LOW:
							if(WXSceneSession)
							{
								showToastOnUIThread(m_context, "该功能在微信4.0以上版本才能使用，请升级你的微信版本。", Toast.LENGTH_LONG);
							}
							else
							{
								showToastOnUIThread(m_context, "朋友圈功能在微信4.2以上版本才能使用，请升级你的微信版本。", Toast.LENGTH_LONG);
							}
							break;

						case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
							showToastOnUIThread(m_context, "还没有安装微信，需要安装后才能分享。", Toast.LENGTH_LONG);
							break;

						case WeiboInfo.BLOG_INFO_THUMB_ERROR:
							showToastOnUIThread(m_context, "缩略图出现问题。", Toast.LENGTH_LONG);
							break;
					}
				}
				else
				{
					addTongJi();
					SendWXAPI.WXCallListener wxlistener = new SendWXAPI.WXCallListener()
					{
						@Override
						public void onCallFinish(int result)
						{
							if(result == BaseResp.ErrCode.ERR_OK)
							{
								ShareTools.addIntegral(m_context);
								if(m_cb != null)
								{
									if(WXSceneSession) m_cb.shareSuccess(WEIXIN);
									else m_cb.shareSuccess(FRIEND);
								}
							}
							SendWXAPI.removeListener(this);
						}
					};
					SendWXAPI.addListener(wxlistener);
				}
				setWaitDlg(false, "");
			}
		}).start();

		return false;
	}

	/**
	 * 发送图片链接到微信
	 * 
	 * @param url
	 *            链接
	 * @param title
	 *            标题
	 * @param description
	 *            描述
	 * @param thumb
	 *            缩略图
	 * @param WXSceneSession
	 *            true 好友 ， false 朋友圈
	 * @return
	 */
	public boolean sendUrlToWeiXin(final String url, final String title, final String description, final Bitmap thumb, final boolean WXSceneSession)
	{
		if(m_weiXin == null)
		{
			m_weiXin = new WeiXinBlog(m_context);
		}
		setWaitDlg(true, "");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean isInstalled = m_weiXin.isWXAppInstalled();
				if(!isInstalled)
				{
					showToastOnUIThread(m_context, "还没有安装微信，需要安装后才能分享", Toast.LENGTH_LONG);
				}
				boolean isSuccess = m_weiXin.sendUrlToWeiXin(url, title, description, thumb, WXSceneSession);
				if(!isSuccess)
				{
					switch(m_weiXin.LAST_ERROR)
					{
						case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
							showToastOnUIThread(m_context, "图片不存在，请检查图片路径。", Toast.LENGTH_LONG);
							break;

						case WeiboInfo.BLOG_INFO_OTHER_ERROR:
							showToastOnUIThread(m_context, "URL地址出现问题。", Toast.LENGTH_LONG);
							break;

						case WeiboInfo.BLOG_INFO_CLIENT_VERSION_LOW:
							if(WXSceneSession)
							{
								showToastOnUIThread(m_context, "该功能在微信4.0以上版本才能使用，请升级你的微信版本。", Toast.LENGTH_LONG);
							}
							else
							{
								showToastOnUIThread(m_context, "朋友圈功能在微信4.2以上版本才能使用，请升级你的微信版本。", Toast.LENGTH_LONG);
							}
							break;

						case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
							showToastOnUIThread(m_context, "还没有安装微信，需要安装后才能分享。", Toast.LENGTH_LONG);
							break;

						case WeiboInfo.BLOG_INFO_THUMB_ERROR:
							showToastOnUIThread(m_context, "缩略图出现问题。", Toast.LENGTH_LONG);
							break;
					}
				}
				else
				{
					addTongJi();
					SendWXAPI.WXCallListener wxlistener = new SendWXAPI.WXCallListener()
					{
						@Override
						public void onCallFinish(int result)
						{
							if(result == BaseResp.ErrCode.ERR_OK)
							{
								ShareTools.addIntegral(m_context);
								if(m_cb != null)
								{
									if(WXSceneSession) m_cb.shareSuccess(WEIXIN);
									else m_cb.shareSuccess(FRIEND);
								}
							}
							SendWXAPI.removeListener(this);
						}
					};
					SendWXAPI.addListener(wxlistener);
				}
				setWaitDlg(false, "");
			}
		}).start();

		return false;
	}

	/**
	 * 在UI线程显示Toast
	 * 
	 * @param context
	 * @param text
	 * @param time
	 */
	public void showToastOnUIThread(final Context context, final String text, final int time)
	{
		if(context != null && text != null)
		{
			((Activity)context).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					Toast.makeText(context, text, time).show();
				}
			});
		}
	}

	/**
	 * 分享失败
	 * 
	 * @param context
	 * @param flag
	 */
	public void uploadFailedOnUIThread(final Context context, final int flag)
	{
		if(context != null)
		{
			((Activity)context).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					AlertDialog m_alertDialog = new AlertDialog.Builder(m_context).create();
					//m_alertDialog.setTitle("提示");
					m_alertDialog.setMessage("分享失败");
					m_alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "重试", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							m_cb.shareFailed(flag);
						}
					});
					m_alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", (DialogInterface.OnClickListener)null);
					m_alertDialog.show();
				}
			});
		}
	}

	/**
	 * 分享成功
	 */
	public void shareSucceed()
	{
		AlertDialog m_alertDialog = new AlertDialog.Builder(m_context).create();
		//m_alertDialog.setTitle("提示");
		m_alertDialog.setMessage("分享成功");
		m_alertDialog.setCanceledOnTouchOutside(false);
		m_alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "关闭", (DialogInterface.OnClickListener)null);
		m_alertDialog.show();
	}

	/**
	 * 必须要掉用，否则绑定不成功
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(m_sina != null)
		{
			m_sina.onActivityResult(requestCode, resultCode, data, -1);
		}
		
		if(m_qzone != null)
		{
			m_qzone.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * 
	 * @param isShow
	 * @param str
	 *            不能为 null
	 */
	public void setWaitDlg(final boolean isShow, final String str)
	{
		//运行在UI线程
		((Activity)m_context).runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				if(!isShow && m_progressDlg != null)
				{
					m_progressDlg.hide();
				}
				else if(isShow && m_progressDlg != null)
				{
					m_progressDlg.show();
				}
			}
		});
	}

	public void clearAll()
	{
		if(m_loginDlg != null)
		{
			m_loginDlg.dismiss();
			m_loginDlg = null;
		}
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				if(m_sina != null)
				{
					m_sina.ClearAll();
					m_sina = null;
				}
				if(m_poco != null)
				{
					m_poco.ClearAll();
					m_poco = null;
				}
				if(m_weiXin != null)
				{
					m_weiXin = null;
				}
				if(m_qzone != null)
				{
					m_qzone.ClearAll();
					m_qzone = null;
				}
			}
		}).start();
		;
		if(m_progressDlg != null)
		{
			m_progressDlg.dismiss();
			m_progressDlg = null;
		}
	}

	private void addTongJi()
	{
		//TongJi2.AddCountByRes(m_context, R.integer.首页_散景_分享成功);
	}

	public interface OnCompleteCallback
	{
		public void success(int which);

		public void shareFailed(int which);

		public void shareSuccess(int witch);
	}
}

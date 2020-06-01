package cn.poco.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.sina.weibo.sdk.constant.WBConstants;
import com.taotie.cn.circlesdk.CircleApi;
import com.taotie.cn.circlesdk.CircleSDK;
import com.taotie.cn.circlesdk.ICIRCLEAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.io.File;

import BaseDataType.CircleMultiInfo;
import BaseDataType.ImageObject;
import BaseDataType.TextObject;
import BaseDataType.VideoObject;
import cn.poco.blogcore.FacebookBlog;
import cn.poco.blogcore.InstagramBlog;
import cn.poco.blogcore.PocoBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.Tools;
import cn.poco.blogcore.TwitterBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.credits.Credit;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.MakeBmp;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

public class ShareTools
{
	private Context mContext;

	private SinaBlog mSina;
	private QzoneBlog2 mQzone;
	private WeiXinBlog mWeiXin;
	private FacebookBlog mFacebook;
	private TwitterBlog mTwitter;
	private InstagramBlog mInstagram;
	private ICIRCLEAPI mCircleApi;

	private static final int WX_THUMB_SIZE = 150;						//微信限制缩略图最大边长

	private boolean needAddIntegral = true;

	public interface SendCompletedListener
	{
		public void getResult(Object result);
	}

	public ShareTools(Context context)
	{
		mContext = context;
		SharePage.initBlogConfig(context);
	}

	/**
	 * 添加分享积分
	 * @param context
	 */
	public static void addIntegral(Context context)
	{
		Credit.CreditIncome(null, context, R.integer.积分_分享照片到第三方平台);
	}

	private void addIntegral(Context context, boolean needAddIntegral)
	{
		if(needAddIntegral) addIntegral(context);
	}

	/**
	 * 是否需要添加积分
	 * @param needAddIntegral true为需要，false为不需要
	 */
	public void needAddIntegral(boolean needAddIntegral)
	{
		this.needAddIntegral = needAddIntegral;
	}

	/**
	 * 绑定新浪
	 * @param listener 监听器
	 */
	public void bindSina(final SharePage.BindCompleteListener listener)
	{
		if(mSina == null) mSina = new SinaBlog(mContext);

		mSina.bindSinaWithSSO(new SinaBlog.BindSinaCallback()
		{
			@Override
			public void success(final String accessToken, String expiresIn, String uid, String userName, String nickName)
			{
				SettingInfoMgr.GetSettingInfo(mContext).SetSinaAccessToken(accessToken);
				SettingInfoMgr.GetSettingInfo(mContext).SetSinaUid(uid);
				SettingInfoMgr.GetSettingInfo(mContext).SetSinaExpiresIn(expiresIn);
				SettingInfoMgr.GetSettingInfo(mContext).SetSinaSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
				SettingInfoMgr.GetSettingInfo(mContext).SetSinaUserName(userName);
				SettingInfoMgr.GetSettingInfo(mContext).SetSinaUserNick(nickName);

				if(listener != null) listener.success();
			}

			@Override
			public void fail()
			{
				switch(mSina.LAST_ERROR)
				{
					case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
						Toast.makeText(mContext, mContext.getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
						break;

					default:
						Toast.makeText(mContext, mContext.getResources().getString(R.string.share_sina_bind_fail), Toast.LENGTH_LONG).show();
						break;
				}
				if(listener != null) listener.fail();
			}
		});
	}

	/**
	 * 绑定QQ空间
	 * @param listener 监听器
	 */
	public void bindQzone(final SharePage.BindCompleteListener listener)
	{
		if(mQzone == null) mQzone = new QzoneBlog2(mContext);

		mQzone.bindQzoneWithSDK(new QzoneBlog2.BindQzoneCallback()
		{
			@Override
			public void success(String accessToken, String expiresIn, String openId,
								String nickName)
			{
				SettingInfoMgr.GetSettingInfo(mContext).SetQzoneAccessToken(accessToken);
				SettingInfoMgr.GetSettingInfo(mContext).SetQzoneOpenid(openId);
				SettingInfoMgr.GetSettingInfo(mContext).SetQzoneExpiresIn(expiresIn);
				SettingInfoMgr.GetSettingInfo(mContext).SetQzoneSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
				SettingInfoMgr.GetSettingInfo(mContext).SetQzoneUserName(nickName);
				if(listener != null) listener.success();
			}

			@Override
			public void fail()
			{
				switch(mQzone.LAST_ERROR)
				{
					case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
						Toast.makeText(mContext, mContext.getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
						break;

					default:
						Toast.makeText(mContext, mContext.getResources().getString(R.string.share_qq_bind_fail), Toast.LENGTH_LONG).show();
						break;
				}
				if(listener != null) listener.fail();
			}
		});
	}

	/**
	 * 用sdk发送图文到新浪微博(只能在主线程调用)
	 * @param content 文本内容
	 * @param pic 图片路径
	 * @param listener 发送完成监听器，会返回int结果,WBConstants.ErrorCode.ERR_OK表示发送成功，WBConstants.ErrorCode.ERR_CANCEL为用户取消,WBConstants.ErrorCode.ERR_FAIL为发送失败,-1为没有安装新浪客户端
	 */
	public void sendToSinaBySDK(String content, String pic, final SendCompletedListener listener)
	{
		if(mSina == null) mSina = new SinaBlog(mContext);
		if(!mSina.checkSinaClientInstall())
		{
			Toast.makeText(mContext, mContext.getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
			if(listener != null) listener.getResult(-1);
			return;
		}
		mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(mContext).GetSinaAccessToken());
		mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
		{
			@Override
			public void response(boolean send_success, int response_code)
			{
				if(send_success)
				{
					switch(response_code)
					{
					case WBConstants.ErrorCode.ERR_OK:
						addIntegral(mContext, needAddIntegral);
						if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_OK);
						break;

					case WBConstants.ErrorCode.ERR_CANCEL:
						if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_CANCEL);
						break;

					case WBConstants.ErrorCode.ERR_FAIL:
					case SinaBlog.NO_RESPONSE:
						if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_FAIL);
						break;
					}
				}
				else
				{
					if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_FAIL);
				}
			}
		});

		Intent intent = new Intent(mContext, SinaRequestActivity.class);
		intent.putExtra("pic", pic);
		intent.putExtra("content", content);
		((Activity) mContext).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
	}

	/**
	 * 用sdk发送文字和链接到新浪微博(只能在主线程调用)
	 * @param content 文本内容
	 * @param pic 图片路径
	 * @param title 链接标题
	 * @param description 链接内容
	 * @param url 分享链接
	 * @param listener 发送完成监听器，会返回int结果,WBConstants.ErrorCode.ERR_OK表示发送成功，WBConstants.ErrorCode.ERR_CANCEL为用户取消,WBConstants.ErrorCode.ERR_FAIL为发送失败,-1为没有安装新浪客户端
	 */
	public void sendUrlToSina(String content, String pic, String title, String description, String url, final SendCompletedListener listener)
	{
		if(mSina == null) mSina = new SinaBlog(mContext);
		if(!mSina.checkSinaClientInstall())
		{
			Toast.makeText(mContext, mContext.getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
			if(listener != null) listener.getResult(-1);
			return;
		}
		mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(mContext).GetSinaAccessToken());
		mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
		{
			@Override
			public void response(boolean send_success, int response_code)
			{
				if(send_success)
				{
					switch(response_code)
					{
						case WBConstants.ErrorCode.ERR_OK:
							addIntegral(mContext, needAddIntegral);
							if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_OK);
							break;

						case WBConstants.ErrorCode.ERR_CANCEL:
							if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_CANCEL);
							break;

						case WBConstants.ErrorCode.ERR_FAIL:
						case SinaBlog.NO_RESPONSE:
							if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_FAIL);
							break;
					}
				}
				else
				{
					if(listener != null) listener.getResult(WBConstants.ErrorCode.ERR_FAIL);
				}
			}
		});

		Intent intent = new Intent(mContext, SinaRequestActivity.class);
		intent.putExtra("pic", pic);
		intent.putExtra("content", content);
		intent.putExtra("link", url);
		intent.putExtra("link_title", title);
		intent.putExtra("link_content", description);
		((Activity) mContext).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
	}

	/**
	 * 发送图片到QQ空间
	 * @param pic 图片路径
	 * @param listener 发送完成监听器，会返回int结果QzoneBlog2.SEND_SUCCESS(发送成功),QzoneBlog2.SEND_CANCEL(用户取消发送),QzoneBlog2.SEND_FAIL(发送失败)。
	 *                 如果用户没有安装手机QQ客户端则返回-1
	 */
	public void sendToQzone(String pic, final SendCompletedListener listener)
	{
		if(mQzone == null) mQzone = new QzoneBlog2(mContext);
		mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneAccessToken());
		mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS) addIntegral(mContext, needAddIntegral);
				if(listener != null) listener.getResult(result);
			}
		});
		boolean success = mQzone.sendToPublicQzone(1, pic);
		if(!success)
		{
			SharePage.showQQErrorMessageToast(mContext, mQzone.LAST_ERROR);
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送图片到QQ空间
	 * @param pic 图片路径
	 * @param title 标题
	 * @param content 文字内容
	 * @param url url地址
	 * @param listener 发送完成监听器，会返回int结果QzoneBlog2.SEND_SUCCESS(发送成功),QzoneBlog2.SEND_CANCEL(用户取消发送),QzoneBlog2.SEND_FAIL(发送失败)。
	 *                 如果用户没有安装手机QQ客户端则返回-1
	 */
	public void sendUrlToQzone(String pic, String title, String content, String url, final SendCompletedListener listener)
	{
		if(mQzone == null) mQzone = new QzoneBlog2(mContext);
		mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneAccessToken());
		mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS) addIntegral(mContext, needAddIntegral);
				if(listener != null) listener.getResult(result);
			}
		});
		boolean success = mQzone.sendToQzone2(content, pic, title, url);
		if(!success)
		{
			SharePage.showQQErrorMessageToast(mContext, mQzone.LAST_ERROR);
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送图片到QQ
	 * @param pic 图片路径
	 * @param listener 发送完成监听器，会返回int结果QzoneBlog2.SEND_SUCCESS(发送成功),QzoneBlog2.SEND_CANCEL(用户取消发送),QzoneBlog2.SEND_FAIL(发送失败)。
	 *                 如果用户没有安装手机QQ客户端则返回-1
	 */
	public void sendToQQ(String pic, final SendCompletedListener listener)
	{
		if(mQzone == null) mQzone = new QzoneBlog2(mContext);
		mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneAccessToken());
		mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS) addIntegral(mContext, needAddIntegral);
				if(listener != null) listener.getResult(result);
			}
		});
		boolean success = mQzone.sendToQQ(pic);
		if(!success)
		{
			SharePage.showQQErrorMessageToast(mContext, mQzone.LAST_ERROR);
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送url到QQ
	 * @param title 标题
	 * @param content 文字内容
	 * @param pic 图片路径
	 * @param listener 发送完成监听器，会返回int结果QzoneBlog2.SEND_SUCCESS(发送成功),QzoneBlog2.SEND_CANCEL(用户取消发送),QzoneBlog2.SEND_FAIL(发送失败)。
	 *                 如果用户没有安装手机QQ客户端则返回-1
	 */
	public void sendUrlToQQ(String title, String content, String pic, String url, final SendCompletedListener listener)
	{
		if(mQzone == null) mQzone = new QzoneBlog2(mContext);
		mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneAccessToken());
		mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(mContext).GetQzoneOpenid());
		mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
		{
			public void sendComplete(int result)
			{
				if(result == QzoneBlog2.SEND_SUCCESS) addIntegral(mContext, needAddIntegral);
				if(listener != null) listener.getResult(result);
			}
		});
		boolean success = mQzone.sendUrlToQQ(pic, title, content, url);
		if(!success)
		{
			SharePage.showQQErrorMessageToast(mContext, mQzone.LAST_ERROR);
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送图片到微信
	 * @param pic 图片路径
	 * @param WXSceneSession true为发送到微信好友，false为发送到微信朋友圈
	 * @param listener 发送完成监听器，会返回int结果BaseResp.ErrCode.ERR_OK(发送成功),BaseResp.ErrCode.ERR_USER_CANCEL(用户取消发送),BaseResp.ErrCode.ERR_AUTH_DENIED(发送失败)三种结果。
	 *                 如果因某些原因错误导致无法跳到微信客户端进行发送则返回-1
	 */
	public void sendToWeiXin(String pic, boolean WXSceneSession, final SendCompletedListener listener)
	{
		if(mWeiXin == null) mWeiXin = new WeiXinBlog(mContext);
		Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(mContext, pic, 0, -1, WX_THUMB_SIZE, WX_THUMB_SIZE), WX_THUMB_SIZE, WX_THUMB_SIZE, -1, 0, Bitmap.Config.ARGB_8888);
		if(mWeiXin.sendToWeiXin(pic, thumb, WXSceneSession))
		{
			SendWXAPI.WXCallListener wxlistener = new SendWXAPI.WXCallListener()
			{
				@Override
				public void onCallFinish(int result)
				{
					if(result == BaseResp.ErrCode.ERR_OK) addIntegral(mContext, needAddIntegral);
					if(listener != null) listener.getResult(result);
					SendWXAPI.removeListener(this);
				}
			};
			SendWXAPI.addListener(wxlistener);
		}
		else
		{
			SharePage.showWeiXinErrorMessage(mContext, mWeiXin.LAST_ERROR, WXSceneSession);
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送图片到微信
	 * @param pic 图片路径
	 * @param url 发送的url链接
	 * @param title 标题
	 * @param content 内容
	 * @param WXSceneSession true为发送到微信好友，false为发送到微信朋友圈
	 * @param listener 发送完成监听器，会返回int结果BaseResp.ErrCode.ERR_OK(发送成功),BaseResp.ErrCode.ERR_USER_CANCEL(用户取消发送),BaseResp.ErrCode.ERR_AUTH_DENIED(发送失败)三种结果。
	 *                 如果因某些原因错误导致无法跳到微信客户端进行发送则返回-1
	 */
	public void sendUrlToWeiXin(String pic, String url, String title, String content, boolean WXSceneSession, final SendCompletedListener listener)
	{
		if(mWeiXin == null) mWeiXin = new WeiXinBlog(mContext);
		Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(mContext, pic, 0, -1, WX_THUMB_SIZE, WX_THUMB_SIZE), WX_THUMB_SIZE, WX_THUMB_SIZE, -1, 0, Bitmap.Config.ARGB_8888);
		if(mWeiXin.sendUrlToWeiXin(url, title, content, thumb, WXSceneSession))
		{
			SendWXAPI.WXCallListener wxlistener = new SendWXAPI.WXCallListener()
			{
				@Override
				public void onCallFinish(int result)
				{
					if(result == BaseResp.ErrCode.ERR_OK) addIntegral(mContext, needAddIntegral);
					if(listener != null) listener.getResult(result);
					SendWXAPI.removeListener(this);
				}
			};
			SendWXAPI.addListener(wxlistener);
		}
		else
		{
			SharePage.showWeiXinErrorMessage(mContext, mWeiXin.LAST_ERROR, WXSceneSession);
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送文件到微信（只适用于微信好友）
	 * @param file 文件路径
	 * @param title 分享标题，可以为null
	 * @param thumb 分享时显示的缩略图，大小最好控制在200*200以内
	 * @param listener 发送完成监听器，会返回int结果BaseResp.ErrCode.ERR_OK(发送成功),BaseResp.ErrCode.ERR_USER_CANCEL(用户取消发送),BaseResp.ErrCode.ERR_AUTH_DENIED(发送失败)三种结果。
	 *                 如果因某些原因错误导致无法跳到微信客户端进行发送则返回-1
	 */
	public void sendFileToWeiXin(String file, String title, Bitmap thumb, final SendCompletedListener listener)
	{
		if(mWeiXin == null) mWeiXin = new WeiXinBlog(mContext);
		if(mWeiXin.sendFileToWeiXin(file, title, thumb))
		{
			SendWXAPI.WXCallListener wxlistener = new SendWXAPI.WXCallListener()
			{
				@Override
				public void onCallFinish(int result)
				{
					if(result == BaseResp.ErrCode.ERR_OK) addIntegral(mContext, needAddIntegral);
					if(listener != null) listener.getResult(result);
					SendWXAPI.removeListener(this);
				}
			};
			SendWXAPI.addListener(wxlistener);
		}
		else
		{
			SharePage.showWeiXinErrorMessage(mContext, mWeiXin.LAST_ERROR, true);
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送图片到Facebook
	 * @param pic 图片路径(图片不能大于12mb)
	 * @param listener 发送完成监听器，会返回int结果FacebookBlog.RESULT_SUCCESS(发送成功或取消发送),FacebookBlog.RESULT_FAIL(发送失败)两种结果，由于成功与取消两者返回数据一样导致无法判别。
	 *                 如果因某些原因错误导致无法跳到Facebook客户端进行发送则返回-1，会以Toast显示错误原因
	 */
	public void sendToFacebook(Bitmap pic, final SendCompletedListener listener)
	{
		if(mFacebook == null) mFacebook = new FacebookBlog(mContext);
		boolean send_success = mFacebook.sendPhotoToFacebookBySDK(pic, new FacebookBlog.FaceBookSendCompleteCallback()
		{
			@Override
			public void sendComplete(int result, String error_info)
			{
				if(result == FacebookBlog.RESULT_SUCCESS) addIntegral(mContext, needAddIntegral);
				else if(error_info != null && error_info.length() > 0) Toast.makeText(mContext, error_info, Toast.LENGTH_LONG).show();
				if(listener != null) listener.getResult(result);
			}
		});

		if(!send_success)
		{
			String message = null;
			switch(mFacebook.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = mContext.getResources().getString(R.string.share_facebook_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
					message = mContext.getResources().getString(R.string.share_error_image_is_null);
					break;

				default:
					message = mContext.getResources().getString(R.string.share_facebook_client_start_fail);
					break;
			}
			Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送url到Facebook
	 * @param title 标题,不能为null
	 * @param content 文字内容，为null时会被Facebook自动填充不相关内容
	 * @param url 跳转链接，不能为null
	 * @param listener 发送完成监听器，会返回int结果FacebookBlog.RESULT_SUCCESS(发送成功或取消发送),FacebookBlog.RESULT_FAIL(发送失败)两种结果，由于成功与取消两者返回数据一样导致无法判别。
	 *                 如果因某些原因错误导致无法跳到Facebook客户端进行发送则返回-1，会以Toast显示错误原因
	 */
	public void sendUrlToFacebook(String title, String content, String url, final SendCompletedListener listener)
	{
		if(mFacebook == null) mFacebook = new FacebookBlog(mContext);
		boolean send_success = mFacebook.sendUrlToFacebookBySDK(title, content, url, new FacebookBlog.FaceBookSendCompleteCallback()
		{
			@Override
			public void sendComplete(int result, String error_info)
			{
				if(result == FacebookBlog.RESULT_SUCCESS) addIntegral(mContext, needAddIntegral);
				else if(error_info != null && error_info.length() > 0) Toast.makeText(mContext, error_info, Toast.LENGTH_LONG).show();
				if(listener != null) listener.getResult(result);
			}
		});

		if(!send_success)
		{
			String message = null;
			switch(mFacebook.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = mContext.getResources().getString(R.string.share_facebook_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
					message = mContext.getResources().getString(R.string.share_error_context_is_null);
					break;

				default:
					message = mContext.getResources().getString(R.string.share_facebook_client_start_fail);
					break;
			}
			Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			if(listener != null) listener.getResult(-1);
		}
	}

	/**
	 * 发送图片和文字到Twitter，两者至少有一种
	 * @param pic 图片路径
	 * @param content 文字内容
	 */
	public boolean sendToTwitter(String pic, String content)
	{
		if(mTwitter == null) mTwitter = new TwitterBlog(mContext);
		if(!mTwitter.sendToTwitter(pic, content))
		{
			String message = null;
			switch(mTwitter.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = mContext.getResources().getString(R.string.share_twitter_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
					message = mContext.getResources().getString(R.string.share_error_context_is_null);
					break;

				default:
					message = mContext.getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			return false;
		}
		addIntegral(mContext, needAddIntegral);
		return true;
	}

	/**
	 * 发送图片到Instagram
	 * @param pic 图片
	 */
	public boolean sendToInstagram(String pic)
	{
		if(mInstagram == null) mInstagram = new InstagramBlog(mContext);
		if(!mInstagram.sendToInstagram(pic))
		{
			String message = null;
			switch(mInstagram.LAST_ERROR)
			{
				case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
					message = mContext.getResources().getString(R.string.share_instagram_client_no_install);
					break;

				case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
					message = mContext.getResources().getString(R.string.share_error_image_is_null);
					break;

				default:
					message = mContext.getResources().getString(R.string.share_send_fail);
					break;
			}
			Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
			return false;
		}
		addIntegral(mContext, needAddIntegral);
		return true;
	}

	/**
	 * 发送图片和文字到Circle
	 * @param pic 图片路径
	 * @param content 文字内容
	 * @param listener 发送监听器，code=0时发送成功，code=1时取消发送，code=10001时没有安装客户端，code=10000时跳转到客户端成功，其他值时发送失败
	 */
	public void sendToCircle(String pic, String content, final SendCompletedListener listener)
	{
		if(pic == null || pic.length() <= 0 || !new File(pic).exists())
		{
			Toast.makeText(mContext, mContext.getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
			return;
		}
		if(mCircleApi == null) mCircleApi = CircleSDK.createApi(mContext, 3);
		CircleMultiInfo multiInfo = new CircleMultiInfo();
		if(content != null)
		{
			TextObject textObject = new TextObject();
			textObject.text = content;
			multiInfo.add(textObject);
		}

		ImageObject imageObject = new ImageObject();
		imageObject.imgUri = decodePath(pic);
		multiInfo.add(imageObject);

		CircleApi.setOnCallBackListener(new CircleApi.OnCallBackListener()
		{
			@Override
			public void OnMessage(int code , String msg)
			{
				if(code == 0) addIntegral(mContext, needAddIntegral);
				if(listener != null) listener.getResult(code);
			}
		});
		mCircleApi.attachInfo(multiInfo);
		mCircleApi.share();
	}

	/**
	 * 发送视频和文字到Circle
	 * @param video_path 视频路径
	 * @param content 文字内容
	 * @param listener 发送监听器，code=0时发送成功，code=1时取消发送，code=10001时没有安装客户端，code=10000时跳转到客户端成功，其他值时发送失败
	 */
	public void sendVideoToCircle(String video_path, String content, final SendCompletedListener listener)
	{
		if(video_path == null || video_path.length() <= 0 || !new File(video_path).exists())
		{
			Toast.makeText(mContext, mContext.getResources().getString(R.string.share_error_video_is_null), Toast.LENGTH_LONG).show();
			return;
		}
		if(mCircleApi == null) mCircleApi = CircleSDK.createApi(mContext, 3);
		CircleMultiInfo multiInfo = new CircleMultiInfo();
		if(content != null)
		{
			TextObject textObject = new TextObject();
			textObject.text = content;
			multiInfo.add(textObject);
		}
		VideoObject videoObject = new VideoObject();
		videoObject.videoUri = decodePath(video_path);
		multiInfo.add(videoObject);

		CircleApi.setOnCallBackListener(new CircleApi.OnCallBackListener()
		{
			@Override
			public void OnMessage(int code , String msg)
			{
				if(code == 0) addIntegral(mContext, needAddIntegral);
				if(listener != null) listener.getResult(code);
			}
		});
		mCircleApi.attachInfo(multiInfo);
		mCircleApi.share();
	}

	//解析一下本地链接是否合法
	public static Uri decodePath(String path)
	{
		Uri uri = null;
		if(path.startsWith("file:///storage"))
		{
			uri = Uri.parse(path);
		}
		if(path.startsWith("/storage"))
		{
			path = "file://" + path;
			uri = Uri.parse(path);
		}
		return uri;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(mSina != null) mSina.onActivityResult(requestCode, resultCode, data, 10086);
		if(mQzone != null) mQzone.onActivityResult(requestCode, resultCode, data);
		if(mFacebook != null) mFacebook.onActivityResult(requestCode, resultCode, data, 10086);
	}
}

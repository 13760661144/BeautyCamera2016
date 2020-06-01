package cn.poco.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.io.File;

import cn.poco.blogcore.FacebookBlog;
import cn.poco.blogcore.InstagramBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.TwitterBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.MakeBmp;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

/**
 * @author lmx
 *         Created by lmx on 2017-12-20.
 */

public class ShareSendUtil
{

    public @interface SendCBType
    {
        int succeed = 1;
        int fail = 2;
        int cancel = 3;
        int error = 4;
        int auth_denied = 5;
    }

    private SinaBlog mSina;
    private QzoneBlog2 mQzone;
    private WeiXinBlog mWeiXin;
    private FacebookBlog mFacebook;
    private TwitterBlog mTwitter;
    private InstagramBlog mInstagram;

    public ShareSendUtil()
    {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (mSina != null) mSina.onActivityResult(requestCode, resultCode, data, -1);
        if (mQzone != null) mQzone.onActivityResult(requestCode, resultCode, data);
        if (mFacebook != null) mFacebook.onActivityResult(requestCode, resultCode, data, -1);
    }

    public void clear()
    {
        if (mSina != null)
        {
            mSina.clear();
            mSina = null;
        }
        if (mQzone != null)
        {
            mQzone.clear();
            mQzone = null;
        }
        if (mFacebook != null)
        {
            mFacebook.clear();
            mFacebook = null;
        }
        if (mTwitter != null)
        {
            mTwitter.clear();
            mTwitter = null;
        }
        mWeiXin = null;
        mInstagram = null;
    }
    
    
    public void sendPicToWeiXin(@NonNull final Context context, String pic, final boolean WXSceneSession, final OnShareSendCallback callback)
    {
        if (pic == null || pic.length() <= 0) return;

        if (mWeiXin == null)
        {
            mWeiXin = new WeiXinBlog(context);
        }
        Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(context, pic, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
        if (mWeiXin.sendToWeiXin(pic, thumb, WXSceneSession))
        {
            SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener()
            {
                @Override
                public void onCallFinish(int result)
                {
                    switch (result)
                    {
                        case BaseResp.ErrCode.ERR_OK:
                            ShareTools.addIntegral(context);
                            showToast(context, R.string.share_send_success, true);
                            if (callback != null)
                            {
                                callback.onSendCallback(SendCBType.succeed);
                            }
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            showToast(context, R.string.share_send_cancel, true);
                            if (callback != null)
                            {
                                callback.onSendCallback(SendCBType.cancel);
                            }
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            showToast(context, R.string.share_send_fail, true);
                            if (callback != null)
                            {
                                callback.onSendCallback(SendCBType.auth_denied);
                            }
                            break;
                        default:
                            break;
                    }
                    SendWXAPI.removeListener(this);
                }
            };
            SendWXAPI.addListener(listener);
        }
        else
        {
            SharePage.showWeiXinErrorMessage(context, mWeiXin.LAST_ERROR, WXSceneSession);
        }
    }

    public void sendPicToSina(@NonNull final Context context, String pic, final OnShareSendCallback callback)
    {
        if (pic == null || pic.length() <= 0 || !new File(pic).exists())
        {
            showToast(context, R.string.share_error_image_is_null, true);
            return;
        }

        if (mSina == null)
        {
            mSina = new SinaBlog(context);
        }
        if (!mSina.checkSinaClientInstall())
        {
            showToast(context, R.string.share_sina_error_clinet_no_install, true);
            return;
        }
        mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(context).GetSinaAccessToken());
        mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse()
        {
            @Override
            public void response(boolean send_success, int response_code)
            {
                if (send_success)
                {
                    switch (response_code)
                    {
                        case WBConstants.ErrorCode.ERR_OK:

                            ShareTools.addIntegral(context);
                            showToast(context, R.string.share_send_success, true);
                            if (callback != null)
                            {
                                callback.onSendCallback(SendCBType.succeed);
                            }
                            break;

                        case WBConstants.ErrorCode.ERR_CANCEL:
                            showToast(context, R.string.share_send_cancel, true);
                            if (callback != null)
                            {
                                callback.onSendCallback(SendCBType.cancel);
                            }
                            break;

                        case WBConstants.ErrorCode.ERR_FAIL:
                        case SinaBlog.NO_RESPONSE:
                            showToast(context, R.string.share_send_fail, true);
                            if (callback != null)
                            {
                                callback.onSendCallback(SendCBType.fail);
                            }
                            break;
                        case WeiboInfo.BLOG_INFO_IMAGE_SIZE_TOO_LARGE:
                            showToast(context, R.string.share_error_image_too_large, true);
                            break;
                    }
                }
                else
                {
                    showToast(context, R.string.share_send_fail, true);
                }
            }
        });

        Intent intent = new Intent(context, SinaRequestActivity.class);
        intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT_AND_PIC);
        intent.putExtra("pic", pic);
        intent.putExtra("content", ShareFrame.SHARE_DEFAULT_TEXT);
        ((Activity) context).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
    }

    public void sendPicToQQ(@NonNull final Context context, String pic, final OnShareSendCallback callback)
    {
        if (mQzone == null)
        {
            mQzone = new QzoneBlog2(context);
        }
        if (!mQzone.checkQQClientInstall())
        {
            showToast(context, R.string.share_qq_error_clinet_no_install, true);
            return;
        }
        mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(context).GetQzoneAccessToken());
        mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(context).GetQzoneOpenid());
        mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
        {
            @Override
            public void sendComplete(int result)
            {
                if (result == QzoneBlog2.SEND_SUCCESS)
                {
                    ShareTools.addIntegral(context);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.succeed);
                    }
                }
                else if (result == QzoneBlog2.SEND_CANCEL)
                {
                    showToast(context, R.string.share_send_cancel, true);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.cancel);
                    }
                }
                else
                {
                    showToast(context, R.string.share_send_fail, true);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.fail);
                    }
                }
            }
        });
        if (!mQzone.sendToQQ(pic))
        {
            SharePage.showQQErrorMessageToast(context, mQzone.LAST_ERROR);
        }
    }

    public void sendPicToQzone(@NonNull final Context context, String pic, final OnShareSendCallback callback)
    {
        if (mQzone == null)
        {
            mQzone = new QzoneBlog2(context);
        }
        if (!mQzone.checkQQClientInstall())
        {
            showToast(context, R.string.share_qq_error_clinet_no_install, true);
            return;
        }
        mQzone.SetAccessToken(SettingInfoMgr.GetSettingInfo(context).GetQzoneAccessToken());
        mQzone.setOpenId(SettingInfoMgr.GetSettingInfo(context).GetQzoneOpenid());
        mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener()
        {
            @Override
            public void sendComplete(int result)
            {
                if (result == QzoneBlog2.SEND_SUCCESS)
                {
                    ShareTools.addIntegral(context);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.succeed);
                    }
                }
                else if (result == QzoneBlog2.SEND_CANCEL)
                {
                    showToast(context, R.string.share_send_cancel, true);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.cancel);
                    }
                }
                else
                {
                    showToast(context, R.string.share_send_fail, true);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.fail);
                    }
                }
            }
        });
        if (!mQzone.sendToPublicQzone(1, pic))
        {
            SharePage.showQQErrorMessageToast(context, mQzone.LAST_ERROR);
        }
    }

    /**
     * 发送图片到Facebook
     *
     * @param pic 图片路径(图片不能大于12mb)
     */
    public void sendPicToFacebook(@NonNull final Context context, String pic, final OnShareSendCallback callback)
    {
        if (mFacebook == null)
        {
            mFacebook = new FacebookBlog(context);
        }
        Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(context, pic, 0, -1, -1, -1);
        final ProgressDialog mFacebookProgressDialog = ProgressDialog.show(context, "", context.getResources().getString(R.string.share_facebook_client_call));
        mFacebookProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mFacebookProgressDialog.setCancelable(true);
        boolean send_success = mFacebook.sendPhotoToFacebookBySDK(bmp, new FacebookBlog.FaceBookSendCompleteCallback()
        {
            @Override
            public void sendComplete(int result, String error_info)
            {
                if (mFacebookProgressDialog.isShowing())
                {
                    mFacebookProgressDialog.dismiss();
                }
                else
                {
                    return;
                }
                if (result == FacebookBlog.RESULT_SUCCESS)
                {
                    showToast(context, R.string.share_send_success, true);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.succeed);
                    }
                }
                else
                {
                    showToast(context, R.string.share_send_fail, true);
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.fail);
                    }
                }
            }
        });

        if (!send_success)
        {
            mFacebookProgressDialog.dismiss();
            int message = 0;
            switch (mFacebook.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = R.string.share_facebook_client_no_install;
                    break;

                case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                    message = R.string.share_error_image_is_null;
                    break;

                default:
                    message = R.string.share_facebook_client_start_fail;
                    break;
            }
            showToast(context, message, true);
        }
    }

    /**
     * 发送图片和文字到Twitter，两者至少有一种
     *
     * @param pic     图片路径
     * @param content 文字内容
     */
    public void sendPicToTwitter(@NonNull final Context context, String pic, String content, final OnShareSendCallback callback)
    {
        if (mTwitter == null)
        {
            mTwitter = new TwitterBlog(context);
        }
        if (!mTwitter.sendToTwitter(pic, content))
        {
            int message;
            switch (mTwitter.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = R.string.share_twitter_client_no_install;
                    break;

                case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
                    message = R.string.share_error_context_is_null;
                    break;

                default:
                    message = R.string.share_send_fail;
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.fail);
                    }
                    break;
            }
            showToast(context, message, true);
            return;
        }
        ShareTools.addIntegral(context);
        if (callback != null)
        {
            callback.onSendCallback(SendCBType.succeed);
        }
    }

    /**
     * 发送图片到Instagram
     *
     * @param pic 图片
     */
    public void sendPicToInstagram(@NonNull final Context context, String pic, final OnShareSendCallback callback)
    {
        if (mInstagram == null)
        {
            mInstagram = new InstagramBlog(context);
        }
        if (!mInstagram.sendToInstagram(pic))
        {
            int message;
            switch (mInstagram.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = R.string.share_instagram_client_no_install;
                    break;

                case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                    message = R.string.share_error_image_is_null;
                    break;

                default:
                    message = R.string.share_send_fail;
                    if (callback != null)
                    {
                        callback.onSendCallback(SendCBType.fail);
                    }
                    break;
            }
            showToast(context, message, true);
            return;
        }
        ShareTools.addIntegral(context);
        if (callback != null)
        {
            callback.onSendCallback(SendCBType.fail);
        }
    }

    private void showToast(Context context, @StringRes int strId, boolean longToast) {
        if (context == null) return;
        showToast(context, context.getString(strId), longToast);
    }
    private void showToast(Context context, String msg, boolean longToast) {
        if (context == null) return;
        Toast.makeText(context, msg, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }


    public interface OnShareSendCallback
    {
        void onSendCallback(@SendCBType int type);
    }
}

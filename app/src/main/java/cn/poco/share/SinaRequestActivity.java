package cn.poco.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.io.File;

import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.MakeBmp;
import my.beautyCamera.BaseActivity;

public class SinaRequestActivity extends BaseActivity implements WbShareCallback
{
    private WbShareHandler weiboAPI;
    private boolean time_out = true;
    private boolean send = false;
    private boolean destroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = null;

        String content = null;
        String pic = null;
        String link = null;
        String link_title = null;
        String link_content = null;
        String video = null;

        if (getIntent() != null && (bundle = getIntent().getExtras()) != null) {
            content = bundle.getString("content");
            pic = bundle.getString("pic");
            link = bundle.getString("link");
            link_title = bundle.getString("link_title");
            link_content = bundle.getString("link_content");
            video = bundle.getString("video");
        }

        int send_type = -1;
        boolean send_pic = false;
        boolean send_text = false;
        boolean send_link = false;
        boolean send_video = false;
        if (pic != null && pic.length() > 0 && new File(pic).exists()) send_pic = true;
        if (content != null && content.length() > 0) send_text = true;
        if (link != null && link.length() > 0) send_link = true;
        if (video != null && video.length() > 0 && new File(video).exists()) send_video = true;

        if (send_pic && send_link && send_text) send_type = SinaBlog.SEND_TYPE_TEXT_AND_LINK;
        else if (send_pic && send_text) send_type = SinaBlog.SEND_TYPE_TEXT_AND_PIC;
        else if (send_pic) send_type = SinaBlog.SEND_TYPE_PIC;
        else if (send_video) send_type = SinaBlog.SEND_TYPE_VIDEO;
        else if (send_text) send_type = SinaBlog.SEND_TYPE_TEXT;
        else {
            responseTimer();
            return;
        }

        weiboAPI = new WbShareHandler(this);
        weiboAPI.registerApp();
        if (savedInstanceState != null) weiboAPI.doResultIntent(getIntent(),this);
        switch (send_type) {
            case SinaBlog.SEND_TYPE_PIC: {
                SinaBlog mSina = new SinaBlog(this);
                mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(this).GetSinaAccessToken());
                if (!mSina.sendBitmapToWeibo(pic)) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", mSina.LAST_ERROR);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                send = true;
                break;
            }
            case SinaBlog.SEND_TYPE_TEXT_AND_PIC: {
                if (pic == null || pic.length() <= 0 || !new File(pic).exists()) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                SinaBlog mSina = new SinaBlog(this);
                mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(this).GetSinaAccessToken());
                if (!mSina.sendMultiMessage(content, pic, null, null, null, null)) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", mSina.LAST_ERROR);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                send = true;
                break;
            }
            case SinaBlog.SEND_TYPE_TEXT_AND_LINK: {
                if (pic == null || pic.length() <= 0 || !new File(pic).exists()) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                SinaBlog mSina = new SinaBlog(this);
                mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(this).GetSinaAccessToken());
                Bitmap thumb = MakeBmp.CreateFixBitmap(cn.poco.imagecore.Utils.DecodeImage(this, pic, 0, -1, 100, 100), 100, 100, MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
                if (!mSina.sendMultiMessage(content, null, link_title, link_content, thumb, link)) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", mSina.LAST_ERROR);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                send = true;
                break;
            }
            case SinaBlog.SEND_TYPE_VIDEO: {
                if (video == null || video.length() <= 0 || !new File(video).exists()) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", WeiboInfo.BLOG_INFO_VIDEO_IS_NULL);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                SinaBlog mSina = new SinaBlog(this);
                mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(this).GetSinaAccessToken());
                if (!mSina.sendVideo(content, video)) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", mSina.LAST_ERROR);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                send = true;
            }
            case SinaBlog.SEND_TYPE_TEXT: {
                SinaBlog mSina = new SinaBlog(this);
                mSina.SetAccessToken(SettingInfoMgr.GetSettingInfo(this).GetSinaAccessToken());
                if (!mSina.sendMultiMessage(content, null, null, null, null, null)) {
                    Intent intent = new Intent();
                    intent.putExtra("send_success", false);
                    intent.putExtra("response", mSina.LAST_ERROR);
                    setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                    finish();
                }
                send = true;
                break;
            }

            default:
                responseTimer();
                break;
        }

    }

    private void responseTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timer = 3000;
                while (time_out && timer > 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        timer -= 100;
                    }
                }
                if (!time_out) return;
                if (destroy) return;
                SinaRequestActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//						Intent intent = new Intent();
//						intent.putExtra("send_success", true);
//						intent.putExtra("response", SinaBlog.NO_RESPONSE);
//						setResult(SinaBlog.SINA_REQUEST_CODE, intent);
                        finish();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        destroy = true;
        time_out = true;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        weiboAPI.doResultIntent(intent,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (send) responseTimer();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onWbShareSuccess()
    {
        time_out = false;
        Intent intent = new Intent();
        intent.putExtra("send_success", true);
        intent.putExtra("response", WBConstants.ErrorCode.ERR_OK);
        setResult(SinaBlog.SINA_REQUEST_CODE, intent);
        finish();
    }

    @Override
    public void onWbShareCancel()
    {
        time_out = false;
        Intent intent = new Intent();
        intent.putExtra("send_success", true);
        intent.putExtra("response", WBConstants.ErrorCode.ERR_CANCEL);
        setResult(SinaBlog.SINA_REQUEST_CODE, intent);
        finish();
    }

    @Override
    public void onWbShareFail()
    {
        time_out = false;
        Intent intent = new Intent();
        intent.putExtra("send_success", true);
        intent.putExtra("response", WBConstants.ErrorCode.ERR_FAIL);
        setResult(SinaBlog.SINA_REQUEST_CODE, intent);
        finish();
    }
}
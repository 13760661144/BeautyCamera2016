package cn.poco.home.home4.userInfoMenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.poco.blogcore.QzoneBlog2;
import cn.poco.campaignCenter.utils.ToastUtil;
import cn.poco.campaignCenter.widget.share.ShareIconView;
import cn.poco.campaignCenter.widget.share.ShareIconView.ShareIconInfo;
import cn.poco.campaignCenter.widget.share.ShareInfoType;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.home.site.ShareWithFriendSite;
import cn.poco.share.ShareTools;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/8/22.
 */

public class ShareWithFriendPage extends IPage{
    private static final String SHARE_FRIEND_LINK = "http://a.app.qq.com/o/simple.jsp?pkgname=my.beautyCamera";

    private Context mContext;
    private ShareIconView mShareIconView;
    private ShareWithFriendSite mSite;
    private List<ShareIconView.ShareIconInfo> mShareInfoList = new ArrayList<>();
    final String [] names = new String [] {getResources().getString(R.string.friends_circle), getResources().getString(R.string.wechat_friends),getResources().getString(R.string.QQZoneAlias),
            getResources().getString(R.string.QQFriends), getResources().getString(R.string.sina_weibo), getResources().getString(R.string.Facebook), getResources().getString(R.string.Instagram), getResources().getString(R.string.Twitter)};

    final int [] resId = new int [] {R.drawable.sharewithfriends_friends_circle, R.drawable.sharewithfriends_wechat_friend, R.drawable.sharewithfriends_qq_zone,
            R.drawable.sharewithfriends_qq_friends, R.drawable.sharewithfriends_sina_weibo, R.drawable.sharewithfriends_facebook, R.drawable.sharewithfriends_instagram, R.drawable.sharewithfriends_twitter};

    private ShareTools mShareTools;
    public ShareWithFriendPage(@NonNull Context context, ShareWithFriendSite site) {
        super(context, site);
        mContext = context;
        mSite = site;
        initSharedAppInfo();
        initView();
        this.setWillNotDraw(false);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSite.onBack(mContext);
                return false;
            }
        });
        saveSharedImageToSdCard();
        mShareTools = new ShareTools(context);
    }

    private void initSharedAppInfo() {
        ShareIconView.ShareIconInfo wechatFriendCircle = new ShareIconView.ShareIconInfo();
        wechatFriendCircle.name = names[0];
        wechatFriendCircle.resId = resId[0];
        wechatFriendCircle.mShareType = ShareInfoType.FRIEND_CIRCLE;
        mShareInfoList.add(wechatFriendCircle);

        ShareIconInfo wechatFriends = new ShareIconInfo();
        wechatFriends.name = names[1];
        wechatFriends.resId = resId[1];
        wechatFriends.mShareType = ShareInfoType.WECHAT_FRIENDS;
        mShareInfoList.add(wechatFriends);

        ShareIconInfo qqZone = new ShareIconInfo();
        qqZone.name = names[2];
        qqZone.resId = resId[2];
        qqZone.mShareType = ShareInfoType.Q_ZONE;
        mShareInfoList.add(qqZone);

        ShareIconInfo qq = new ShareIconInfo();
        qq.name = names[3];
        qq.resId = resId[3];
        qq.mShareType = ShareInfoType.QQ;
        mShareInfoList.add(qq);

        ShareIconInfo sinaWeibo = new ShareIconInfo();
        sinaWeibo.name = names[4];
        sinaWeibo.resId = resId[4];
        sinaWeibo.mShareType = ShareInfoType.SINA_WEIBO;
        mShareInfoList.add(sinaWeibo);

        ShareIconInfo facebook = new ShareIconInfo();
        facebook.name = names[5];
        facebook.resId = resId[5];
        facebook.mShareType = ShareInfoType.FACE_BOOK;
        mShareInfoList.add(facebook);

        ShareIconInfo instgram = new ShareIconInfo();
        instgram.name = names[6];
        instgram.resId = resId[6];
        instgram.mShareType = ShareInfoType.INSTAGRAM;
        mShareInfoList.add(instgram);

        ShareIconInfo twitter = new ShareIconInfo();
        twitter.name = names[7];
        twitter.resId = resId[7];
        twitter.mShareType = ShareInfoType.TWITTER;
        mShareInfoList.add(twitter);
    }

    private void initView() {
        mShareIconView = new ShareIconView.ShareIconViewBuilder(mContext, 4).verticalMargin(ShareData.PxToDpi_xhdpi(84)).bottomMargin(0).iconTextSizeAndColor(11, 0x99000000).shareInfoList(mShareInfoList).itemOnClickListener(mClickListener).create();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(68) * 2, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mShareIconView.setLayoutParams(params);
        this.addView(mShareIconView);
    }


    /**
     * 把要分享到各大社交平台的缩略图先保存到指定的路径
     */
    private static String sImgPathToDomesticApp, sImgPathToForeignSocialMedia, sImgPathToInstagramApp;
    private static final String RES_DIRECTORY= "shareWithFriends";
    private static final String RES_IMGPATH_DOMESTICAPP = "sharewithfriends_todomesticapp.jpg";
    private static final String RES_IMGPATH_FOREIGNAPP = "sharewithfriends_toforeignapp.jpg";
    private static final String RES_IMGPATH_INSTAGRAM = "sharewithfriends_toinstagram.jpg";


    private void saveSharedImageToSdCard () {
        if (sImgPathToDomesticApp == null) {
            sImgPathToDomesticApp = FileCacheMgr.GetAppPath();
            final String resPath = RES_DIRECTORY.concat(File.separator).concat(RES_IMGPATH_DOMESTICAPP);
            FileUtil.assets2SD(mContext, resPath, sImgPathToDomesticApp, true);
        }

        if (sImgPathToForeignSocialMedia == null) {
            sImgPathToForeignSocialMedia = FileCacheMgr.GetAppPath();
            final String resPath = RES_DIRECTORY.concat(File.separator).concat(RES_IMGPATH_FOREIGNAPP);
            FileUtil.assets2SD(mContext, resPath, sImgPathToForeignSocialMedia, true);
        }

        if (sImgPathToInstagramApp == null) {
            sImgPathToInstagramApp = FileCacheMgr.GetAppPath();
            final String resPath = RES_DIRECTORY.concat(File.separator).concat(RES_IMGPATH_INSTAGRAM);
            FileUtil.assets2SD(mContext, resPath, sImgPathToInstagramApp, true);
        }
    }



    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ShowWaitDlg();
            Object tagObject = v.getTag();
            if (tagObject instanceof Integer) {
                int tag = (Integer) tagObject;
                String shareTitle = getResources().getString(R.string.share_with_friends_title);
                switch (tag) {
                    case ShareInfoType.FRIEND_CIRCLE :
                        mShareTools.sendUrlToWeiXin(sImgPathToDomesticApp, SHARE_FRIEND_LINK, shareTitle, null, false, mShareCallback);
                        break;
                    case ShareInfoType.WECHAT_FRIENDS :
                        mShareTools.sendUrlToWeiXin(sImgPathToDomesticApp, SHARE_FRIEND_LINK, shareTitle, null, true, mShareCallback);
                        break;
                    case ShareInfoType.Q_ZONE :
                        mShareTools.sendUrlToQzone(sImgPathToDomesticApp, shareTitle, null, SHARE_FRIEND_LINK, mShareCallback);
                        break;
                    case ShareInfoType.QQ :
                        mShareTools.sendUrlToQQ(shareTitle, null, sImgPathToDomesticApp, SHARE_FRIEND_LINK, mShareCallback);
                        break;
                    case ShareInfoType.SINA_WEIBO : {
                        // 因为希望新浪微博分享出去的图片想要清晰的大图，所以这里也用了这张图片
                        mShareTools.sendToSinaBySDK(shareTitle + " " + SHARE_FRIEND_LINK, sImgPathToForeignSocialMedia, mShareCallback);
                        break;
                    }

                    case ShareInfoType.FACE_BOOK : {
                        Bitmap bitmap = BitmapFactory.decodeFile(sImgPathToInstagramApp);
                        if (bitmap != null) {
                            mShareTools.sendToFacebook(bitmap, mShareCallback);
                        }
                        break;
                    }

                    case ShareInfoType.TWITTER : {
                        mShareTools.sendToTwitter(sImgPathToForeignSocialMedia, shareTitle + " " + SHARE_FRIEND_LINK);
                        CloseWaitDlg();
                        break;
                    }

                    case ShareInfoType.INSTAGRAM : {
                        mShareTools.sendToInstagram(sImgPathToInstagramApp);
                        CloseWaitDlg();
                        break;
                    }

                    default: {

                    }
                }
            }
        }
    };

    private ShareTools.SendCompletedListener mShareCallback = new ShareTools.SendCompletedListener() {
        @Override
        public void getResult(Object result) {
            CloseWaitDlg();
            int shareResult = (Integer)result;
            switch (shareResult) {
                // 发送成功
                // 微信朋友圈，微信好友, 新浪微博,Facebook
                case BaseResp.ErrCode.ERR_OK :
                    // QQ空间, QQ好友
                case QzoneBlog2.SEND_SUCCESS : {
                    ToastUtil.showToast(getContext(), getResources().getString(R.string.share_successfully));
                    break;
                }

                // 用户取消发送
                // 微信朋友圈，微信好友
                case BaseResp.ErrCode.ERR_USER_CANCEL :
                    // 新浪微博, Facebook
                case WBConstants.ErrorCode.ERR_CANCEL :
                    // QQ空间，QQ好友
                case QzoneBlog2.SEND_CANCEL : {
                    ToastUtil.showToast(getContext(), getResources().getString(R.string.user_cancel_share));
                    break;
                }

                // 分享失败
                // 微信朋友圈，微信好友
                case BaseResp.ErrCode.ERR_AUTH_DENIED :
                    // QQ空间，QQ好友
                case QzoneBlog2.SEND_FAIL:
                    // 新浪微博, Facebook
                case WBConstants.ErrorCode.ERR_FAIL: {
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_to_share), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    @Override
    public void SetData(HashMap<String, Object> params) {
        boolean paramsValid = params.containsKey("bitmapPath");
        if (paramsValid) {
            Object param = params.get("bitmapPath");
            if (param instanceof String) {
                String path = (String)param;
                boolean isFileExist = FileUtil.isFileExists(path);
                if (isFileExist) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if (bitmap != null) {
                        this.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    }
                }
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x80FFFFFF);
    }

    @Override
    public void onBack() {
        mSite.onBack(getContext());
    }


    @Override
    public void onResume() {
        super.onResume();
        CloseWaitDlg();
    }

    @Override
    public void onClose() {
        mShareIconView.clear();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        mShareTools.onActivityResult(requestCode, resultCode, data);
        return super.onActivityResult(requestCode, resultCode, data);
    }

    private ProgressDialog mProgressDialog;

    /**
     * 展示加载对话框
     */
    private void ShowWaitDlg()
    {
        if(mProgressDialog == null)
        {
            mProgressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.show();
    }


    /**
     * 关闭加载对话框
     */
    public void CloseWaitDlg()
    {
        if(mProgressDialog != null)
        {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }

}

package cn.poco.campaignCenter.widget.share;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.poco.blogcore.QzoneBlog2;
import cn.poco.campaignCenter.manager.ConnectionsManager;
import cn.poco.campaignCenter.manager.FileManager;
import cn.poco.campaignCenter.utils.ClickEffectUtil;
import cn.poco.campaignCenter.utils.ToastUtil;
import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.share.ShareTools;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by Shine on 2016/12/12.
 */

public class ShareLayout extends FrameLayout{
    public interface ShareLayoutDelegate {
        void onImageItemClick(int index);
        void onCancelBtnClick();
    }

    private LinearLayout mViewContainer, mIconContainer;

    public ShareIconView mShareIconLayout;
    private TextView mTitle, mCancel;
    private ProgressDialog mProgressDialog;
    public View mBg;

    private int mLayoutLeftPadding, mLayoutRightPadding;

    private ShareTools mShareTools;
    private ShareLayoutDelegate mDelegate;

    private static final int FIRST_CLICK = 0x00000010;

    private static final int CLICK_TWITTER_LIKED_APP = 0x00000000;
    private static final int CLICK_OTHER_APP = 0x00000001;
    private static final int CLICK_TYPE_MASK = 0x1;

    private int mShareInfo;

    private String mShareTitle;
    private String mShareLink;
    private String mShareContent;
    private String mShareImg;
    private String mShareImgType2; // banner原图
    private String mLocalCacheOtherUrl;
    private String mLocalCacheTwitterUrl;

    private boolean mDefaultLayout;

    public ShareLayout(Context context, boolean defaultLayout) {
        super(context);
        this.mDefaultLayout = defaultLayout;
        initLayoutData();
        initView(context);
    }

    private void initLayoutData() {
        mLayoutLeftPadding = ShareData.PxToDpi_xhdpi(30);
        mLayoutRightPadding = ShareData.PxToDpi_xhdpi(30);
    }

    public void setLayoutData(int leftPadding, int rightPadding) {
        mLayoutLeftPadding = leftPadding;
        mLayoutRightPadding = rightPadding;
    }

    private void initView(Context context) {
        mBg = new View(context);
        FrameLayout.LayoutParams paramsBg = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBg.setLayoutParams(paramsBg);
        this.addView(mBg);
        mShareTools = new ShareTools(context);
        mShareTools.needAddIntegral(false);
        mViewContainer = new LinearLayout(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawColor(0x4D000000);
                canvas.drawColor(0xDCFFFFFF);
            }
        };
        mViewContainer.setWillNotDraw(false);
        mViewContainer.setOrientation(LinearLayout.VERTICAL);
        mViewContainer.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT , FrameLayout.LayoutParams.WRAP_CONTENT);
        mViewContainer.setLayoutParams(frameParams);
        this.addView(mViewContainer);

        mIconContainer = new LinearLayout(context);
        mIconContainer.setClickable(true);
        mIconContainer.setOrientation(LinearLayout.VERTICAL);
        mIconContainer.setGravity(Gravity.CENTER_VERTICAL);
        mIconContainer.setPadding(mLayoutLeftPadding, 0, mLayoutRightPadding, 0);
        frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(448));
        mIconContainer.setLayoutParams(frameParams);
        mViewContainer.addView(mIconContainer);

        mTitle = new TextView(context);
        mTitle.setPadding(0, ShareData.PxToDpi_xhdpi(8), 0, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(106));
        mTitle.setText(context.getString(R.string.share_to));
        mTitle.setGravity(Gravity.CENTER_VERTICAL);
        mTitle.setIncludeFontPadding(false);
        mTitle.setLayoutParams(params);
        mTitle.setAlpha(0.86f);
        mIconContainer.addView(mTitle);

        if (mDefaultLayout) {
            mShareIconLayout = new ShareIconView.ShareIconViewBuilder(context, 5).itemOnClickListener(layoutClickListener).create();
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mShareIconLayout.setLayoutParams(params1);
            mIconContainer.addView(mShareIconLayout);
        }

        mCancel = new TextView(context);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100));
        mCancel.setText(context.getString(R.string.webviewpage_cancel));
        mCancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mCancel.setGravity(Gravity.CENTER);
        mCancel.setBackgroundColor(0xffffffff);
        mCancel.setLayoutParams(params2);
        mCancel.setOnClickListener(layoutClickListener);
        ClickEffectUtil.addTextViewClickEffect(mCancel, Color.parseColor("#333333"), Color.parseColor("#80333333"));
        mViewContainer.addView(mCancel);
    }


    public void setUpShareInfo(String title, String content, String link, String img, String shareImg2, String downloadImgNormal, String downloadImgTwitter) {
        this.mShareTitle = title;
        this.mShareContent = content;
        this.mShareLink = link;
        this.mShareImg = img;
        this.mShareImgType2 = shareImg2;

        mLocalCacheOtherUrl = downloadImgNormal;
        mLocalCacheTwitterUrl = downloadImgTwitter;
    }

    public void setShareIconView(ShareIconView shareIconView) {
        mShareIconLayout = shareIconView;
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mShareIconLayout.setLayoutParams(params1);
        mIconContainer.addView(mShareIconLayout);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY));
    }

    private OnClickListener layoutClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mDelegate != null) {
                if (v == mCancel) {
                    mDelegate.onCancelBtnClick();
                } else {
                    Integer tag = (Integer) v.getTag();
                    if (tag != null) {
                        if (isNetWorkAvailable(getContext())) {
                            // 是否点击(新浪微博或者推特)
                            boolean isTwitterLikeAppClick = (tag == ShareInfoType.SINA_WEIBO || tag== ShareInfoType.TWITTER) ? true : false;
                            String cachePath = isTwitterLikeAppClick ? mLocalCacheTwitterUrl : mLocalCacheOtherUrl;

                            String shareImg = isTwitterLikeAppClick ? mShareImgType2 : mShareImg;
                            int state = FileManager.getInstacne().checkCacheFile(cachePath);

                            //1. 初次点击，或者没有图片缓存，则从服务器那里下载图片, 否则
                            // 如果这次点击的是推特或者新浪微博，而上次缓存的图片也是推特或者新浪微博的，则直接分享，否则先从服务器下载对应类型的
                            // 如果这次点击的不是推特或者新浪微博，而上次缓存的图片也不是推特或者新浪微博类型的，则直接分享，否则先从服务器下载对应类型的;

                            // 2. 为了保证缓存的图片和目前的一致，一定是初次点击下载图片成功的前提下
                            if (state != FileManager.FAIL_TO_CREATE_FILE) {
                                if ((mShareInfo & FIRST_CLICK) != FIRST_CLICK || state == FileManager.CREATE_NEW_FILE) {
                                    downloadImgFromServer(tag, shareImg, cachePath, isTwitterLikeAppClick);
                                } else {
                                    // 获取上次下载回来的图片类型
                                    int clickInfo = (mShareInfo & CLICK_TYPE_MASK);
                                    if (isTwitterLikeAppClick) {
                                        if (clickInfo == CLICK_TWITTER_LIKED_APP) {
                                            ShowWaitDlg();
                                            shareToFamousApp((Integer) v.getTag(), mLocalCacheTwitterUrl);
                                        } else {
                                            downloadImgFromServer(tag, shareImg, cachePath, true);
                                        }
                                    } else {
                                        if (clickInfo == CLICK_OTHER_APP) {
                                            ShowWaitDlg();
                                            shareToFamousApp((Integer) v.getTag(), mLocalCacheOtherUrl);
                                        } else {
                                            downloadImgFromServer(tag, shareImg, cachePath, false);
                                        }
                                    }
                                }

                            } else {
                                ToastUtil.showToast(getContext(), getResources().getString(R.string.share_send_fail));
                            }
                            mDelegate.onImageItemClick((Integer) v.getTag());
                        } else {
                            ToastUtil.showToast(getContext(), getResources().getString(R.string.poor_network));
                        }
                    }
                }
            }
        }
    } ;

    public void setDelegate(ShareLayoutDelegate delegate) {
        this.mDelegate = delegate;
    }


    private void downloadImgFromServer(final int index, final String shareImg, final String downloadFilePath, final boolean isClickTwitter) {
        ShowWaitDlg();
        ConnectionsManager.getInstacne().downloadImage(shareImg, downloadFilePath, new ConnectionsManager.RequestDelegate() {
            @Override
            public void run(Object response, ConnectionsManager.NetWorkError error) {
                CloseWaitDlg();
                if (error == null) {
                    if (response instanceof  String) {
                        if ((mShareInfo & FIRST_CLICK) != FIRST_CLICK) {
                            mShareInfo |= FIRST_CLICK;
                        }

                        if (isClickTwitter) {
                            mLocalCacheTwitterUrl = (String)response;
                            shareToFamousApp(index, mLocalCacheTwitterUrl);
                            mShareInfo |= CLICK_TWITTER_LIKED_APP;
                        } else {
                            mLocalCacheOtherUrl = (String)response;
                            shareToFamousApp(index, mLocalCacheOtherUrl);
                            mShareInfo |= CLICK_OTHER_APP;
                        }
                    }
                } else {
                    ToastUtil.showToast(getContext(), getResources().getString(R.string.fail_to_share));
                }
            }
        });
    }

    private void ShowWaitDlg()
    {
        if(mProgressDialog == null)
        {
            mProgressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.show();
    }

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

    public void clear() {
        mShareIconLayout.clear();
        ToastUtil.clear();
    }

    public ShareTools getShareTools() {
        return mShareTools;
    }


    private void shareToFamousApp(int index, String cacheImgPath) {
        CloseWaitDlg();
        if (index == ShareInfoType.FRIEND_CIRCLE) {
            mShareTools.sendUrlToWeiXin(cacheImgPath, mShareLink, mShareTitle, mShareContent, false, sendCompletedListener);
        } else if (index == ShareInfoType.WECHAT_FRIENDS) {
            mShareTools.sendUrlToWeiXin(cacheImgPath, mShareLink, mShareTitle, mShareContent, true, sendCompletedListener);
        } else if (index == ShareInfoType.SINA_WEIBO) {
            String finalContetent = mShareTitle.concat(" ").concat(mShareLink);
            mShareTools.sendToSinaBySDK(finalContetent, cacheImgPath, sendCompletedListener);
        } else if (index == ShareInfoType.Q_ZONE) {
            mShareTools.sendUrlToQzone(cacheImgPath, mShareTitle, mShareContent, mShareLink, sendCompletedListener);
        } else if (index == ShareInfoType.QQ) {
            mShareTools.sendUrlToQQ(mShareTitle, mShareContent, cacheImgPath, mShareLink, sendCompletedListener);
        } else if (index == ShareInfoType.FACE_BOOK) {
            mShareTools.sendUrlToFacebook(mShareTitle, mShareContent, mShareLink, null);
        } else if (index == ShareInfoType.TWITTER) {
            String finalContetent = mShareTitle.concat(" ").concat(mShareLink);
            mShareTools.sendToTwitter(cacheImgPath, finalContetent);
        }
    }

    private ShareTools.SendCompletedListener sendCompletedListener = new ShareTools.SendCompletedListener() {
        @Override
        public void getResult(Object result) {
           int shareResult = (Integer)result;
            switch (shareResult) {
                // 发送成功
                // 微信朋友圈，微信好友, 新浪微博
                case BaseResp.ErrCode.ERR_OK :
                    // QQ空间, QQ好友
                case QzoneBlog2.SEND_SUCCESS : {
                    ToastUtil.showToast(getContext(), getResources().getString(R.string.share_successfully));
                    break;
                }

                // 用户取消发送
                // 微信朋友圈，微信好友
                case BaseResp.ErrCode.ERR_USER_CANCEL :
                    // 新浪微博
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
                    // 新浪微博
                case WBConstants.ErrorCode.ERR_FAIL: {
                    Toast.makeText(getContext(), getContext().getString(R.string.fail_to_share), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    private boolean isNetWorkAvailable(Context context) {
        boolean networkState = (NetWorkUtils.isNetworkConnected(context) || NetWorkUtils.isWifiContected(context));
        return networkState;
    }



}



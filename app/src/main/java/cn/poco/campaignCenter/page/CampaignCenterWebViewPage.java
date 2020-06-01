package cn.poco.campaignCenter.page;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adnonstop.admasterlibs.AdUtils;

import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;

import cn.poco.advanced.ImageUtils;
import cn.poco.banner.BannerCore3;
import cn.poco.beautifyEyes.util.StatisticHelper;
import cn.poco.camera.RotationImg2;
import cn.poco.campaignCenter.model.CampaignInfo;
import cn.poco.campaignCenter.site.webviewpagteSite.CampaignWebViewPageSite;
import cn.poco.campaignCenter.ui.cells.RoundedBgCell;
import cn.poco.campaignCenter.utils.AndroidUtil;
import cn.poco.campaignCenter.widget.share.ShareActionSheet;
import cn.poco.campaignCenter.widget.view.EmptyHolderView;
import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.SiteID;
import cn.poco.home.site.HomePageSite;
import cn.poco.system.AppInterface;
import cn.poco.system.FolderMgr;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MyWebView;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MyNetCore;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * Created by Shine on 2016/12/22.
 */

public class CampaignCenterWebViewPage extends MyWebView {
    private static final String LOG = "CampaignCenterWebView";

    private FrameLayout mTopBar;
    protected ImageView m_backBtn;
    protected TextView m_title;
    protected ProgressBar m_progressBar;
    protected ProgressDialog mProgressDialog;

    private CampaignInfo mCampaignInfo;
//    protected ImageView mShareBtn;
//    protected ImageView mGoHomeBtn;
    protected ImageView mTopBarRightBtn;

    private FrameLayout mBottomViewContainer;
    private RoundedBgCell mTryNow;
    private ShareActionSheet mShareActionSheet;
    private EmptyHolderView mEmptyHolderView;

    private String m_url;
    private CampaignWebViewPageSite mSite;
    private AppInterface mAppInterface;
    private GestureDetector mGestureDetector;
//    private boolean mIsBusinessArticle = true;
    private HomePageSite.ShareBlogData mShareBlogData;

    //private static final int REQUEST_FILE_CHOOSER = 1;
    //private ValueCallback<Uri[]> mValueCallback;
    protected ValueCallback<Uri> m_filePathCallback1;
    protected ValueCallback<Uri[]> m_filePathCallback2;

    public CampaignCenterWebViewPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (CampaignWebViewPageSite) site;
        mShareBlogData = new HomePageSite.ShareBlogData(context);
        mSite.m_cmdProc.SetShareData(mShareBlogData);

        StatisticHelper.countPageEnter(context, context.getString(R.string.运营专区_详情));
    }

    @Override
    protected void Init() {
        ShareData.InitData((Activity)getContext());
        FrameLayout.LayoutParams fl;
        this.setBackgroundColor(0xFFFFFFFF);

        int topBarH;
        mAppInterface = AppInterface.GetInstance(getContext());

        topBarH = ShareData.PxToDpi_xhdpi(90);
        mTopBar = new FrameLayout(getContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    requestDisallowInterceptTouchEvent(true);
                }
                return super.onTouchEvent(event);
            }
        };

        mTopBar.setBackgroundColor(0xf4ffffff);
        mTopBar.setBackgroundColor(Color.parseColor("#f5ffffff"));
        fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topBarH, Gravity.LEFT | Gravity.TOP);
        mTopBar.setLayoutParams(fl);
        this.addView(mTopBar);

        mTopBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                m_webView.scrollTo(0, 0);
            }
        });

        {
            m_backBtn = new ImageView(getContext());
            m_backBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
            m_backBtn.setImageResource(R.drawable.framework_back_btn);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
            m_backBtn.setLayoutParams(params);
            mTopBar.addView(m_backBtn);

            mTopBarRightBtn = new ImageView(getContext());
            mTopBarRightBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            mTopBarRightBtn.setImageResource(R.drawable.framework_home_icon);
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            params1.rightMargin = ShareData.PxToDpi_xhdpi(10);
            mTopBarRightBtn.setLayoutParams(params1);
            mTopBar.addView(mTopBarRightBtn);

//            mShareBtn = new ImageView(getContext());
//            mShareBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            mShareBtn.setImageResource(R.drawable.framework_share_btn);
//            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//            params1.rightMargin = ShareData.PxToDpi_xhdpi(10);
//            mShareBtn.setLayoutParams(params1);
//            mTopBar.addView(mShareBtn);

            m_title = new TextView(getContext());
            m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            m_title.setTextColor(Color.parseColor("#333333"));
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            m_title.setLayoutParams(params2);
            mTopBar.addView(m_title);
            m_title.setVisibility(View.GONE);

            mShareActionSheet = new ShareActionSheet(getContext(), R.style.waitDialog, true);
        }

        m_webView = new WebView(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenHeight - topBarH);
        fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
        this.addView(m_webView, fl);

        mEmptyHolderView = new EmptyHolderView(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.m_screenHeight - topBarH);
        fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mEmptyHolderView.setLayoutParams(fl);
        mEmptyHolderView.setClickable(true);
        mEmptyHolderView.setVisibility(View.GONE);
        this.addView(mEmptyHolderView);

        mBottomViewContainer = new FrameLayout(getContext());
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(100), Gravity.BOTTOM | Gravity.RIGHT);
        mBottomViewContainer.setBackgroundColor(Color.WHITE);
        mBottomViewContainer.setBackgroundColor(0xF5FFFFFF);
        mBottomViewContainer.setLayoutParams(params3);
        this.addView(mBottomViewContainer);
        mBottomViewContainer.setVisibility(View.GONE);

        int pbarH = ShareData.PxToDpi_xhdpi(4);
        m_progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        m_progressBar.setMax(100);
        m_progressBar.setMinimumHeight(pbarH);
        m_progressBar.getProgressDrawable().setColorFilter(SysConfig.s_skinColor, PorterDuff.Mode.SRC_IN);
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, pbarH);
        fl.gravity = Gravity.LEFT | Gravity.TOP;
        fl.topMargin = topBarH;
        this.addView(m_progressBar, fl);
        m_progressBar.setVisibility(View.GONE);

        mTryNow = new RoundedBgCell(getContext());

        // 根据皮肤颜色显示不同的按钮颜色
        addSkin();
        if (!NetWorkUtils.isNetContected(this.getContext()) && !NetWorkUtils.isWifiContected(getContext())) {
            m_webView.setVisibility(View.GONE);
            mEmptyHolderView.setVisibility(View.VISIBLE);
            if (mTryNow != null && mTryNow.getVisibility() != GONE) {
                mTryNow.setVisibility(View.GONE);
            }
//            mShareBtn.setVisibility(View.GONE);
        }

        InitWebViewSetting(m_webView.getSettings());
        m_webView.getSettings().setUserAgentString(m_webView.getSettings().getUserAgentString() + " beautyCamera/" + SysConfig.GetAppVer(getContext()));

        setWebChromeClient(new MyWebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress < 100)
                {
                    m_progressBar.setVisibility(View.VISIBLE);
                    m_progressBar.setProgress(newProgress);
                }
                else
                {
                    m_progressBar.setVisibility(View.GONE);
                }

                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if(title != null)
                {
                    m_title.setText(title);
                }
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback)
            {
                super.onShowCustomView(view, callback);

                if(m_videoView != null)
                {
                    mTopBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onHideCustomView()
            {
                super.onHideCustomView();

                mTopBar.setVisibility(View.VISIBLE);
            }

            //            @Override
//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                mValueCallback = filePathCallback;
//                AndroidUtil.openFileChooser((Activity) webView.getContext(), REQUEST_FILE_CHOOSER);
//                return true;
//            }
            //5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
            {
                ShowFileChooser(null, filePathCallback);
                return true;
            }

            //4.1.1
            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture)
            {
                ShowFileChooser(filePathCallback, null);
            }

            //3.0+
            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType)
            {
                ShowFileChooser(filePathCallback, null);
            }

            //3.0-
            public void openFileChooser(ValueCallback<Uri> filePathCallback)
            {
                ShowFileChooser(filePathCallback, null);
            }
        });

        setWebViewClient(new MyWebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.setVisibility(View.GONE);
                mEmptyHolderView.setVisibility(View.VISIBLE);
                mTryNow.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                String temp = url.toLowerCase(Locale.ENGLISH);
                if(!temp.startsWith("http") && !temp.startsWith("ftp"))
                {
                    BannerCore3.ExecuteCommand(getContext(), url, mSite.m_cmdProc);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if(url.startsWith("BeautyCamera://") || url.startsWith("beautycamera://"))
                {
                    BannerCore3.ExecuteCommand(getContext(), url, mSite.m_cmdProc);
                }
                else
                {
                    super.onPageStarted(view, url, favicon);
                }
            }

        });

        setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                CommonUtils.OpenBrowser(getContext(), url);
            }
        });
        mGestureDetector = new GestureDetector(this.getContext(), new GestureListener());
    }

    protected void ShowFileChooser(final ValueCallback<Uri> cb1, final ValueCallback<Uri[]> cb2)
    {
        m_filePathCallback1 = cb1;
        m_filePathCallback2 = cb2;
        CharSequence[] items = {getResources().getString(R.string.webviewpage_album), getResources().getString(R.string.webviewpage_camera)};
        AlertDialog dlg = new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.webviewpage_select_image_source)).setItems(items, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case 0:
                    {
                        mSite.OnSelPhoto(getContext());
                        break;
                    }

                    case 1:
                    {
                        mSite.OnCamera(getContext());
                        break;
                    }

                    default:
                        break;
                }
            }
        }).create();
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if(m_filePathCallback1 != null)
                {
                    m_filePathCallback1.onReceiveValue(null);
                    m_filePathCallback1 = null;
                }
                if(m_filePathCallback2 != null)
                {
                    m_filePathCallback2.onReceiveValue(null);
                    m_filePathCallback2 = null;
                }
                //m_photoPath = null;
            }
        });
        dlg.show();
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        Object args = params.get("campaignInfo");
        if (args instanceof CampaignInfo) {
            CampaignInfo temp = (CampaignInfo) args;
            mCampaignInfo = temp.clone();
//            mIsBusinessArticle = mCampaignInfo.getelse {
            final String suffix = ".img";
            final String type = "twitter";
            final String typeNormal = "other";
            String identifier = String.valueOf(System.currentTimeMillis());
            String filePathOther = FolderMgr.getInstance().CAMPAIGN_CENTER_PATH + File.separator + identifier + typeNormal + suffix;
            String filePathTwitter = FolderMgr.getInstance().CAMPAIGN_CENTER_PATH + File.separator + identifier + type + suffix;
            mCampaignInfo.setCacheImgPath(filePathOther);
            mCampaignInfo.setCacheImgForTwiter(filePathTwitter);

            if (TextUtils.isEmpty(mCampaignInfo.getShareLink())) {
                mTopBarRightBtn.setImageResource(R.drawable.framework_home_icon);
                mTopBarRightBtn.setTag("home_btn");
            } else {
                mTopBarRightBtn.setImageResource(R.drawable.framework_share_btn);
                mTopBarRightBtn.setTag("share_btn");
            }
            // 微博和推特改成banner原图
            mShareActionSheet.setUpShareContentInfo(mCampaignInfo.getShareTitle(), mCampaignInfo.getShareDescription(),mCampaignInfo.getShareLink(), mCampaignInfo.getShareImg(), mCampaignInfo.getCoverUrl(),mCampaignInfo.getCacheImgPath(), mCampaignInfo.getTwitterCacheImg());
            initAnimationTouchListener();
            loadUrl(temp.getOpenUrl());
//            if (mIsBusinessArticle && !TextUtils.isEmpty(mCampaignInfo.getTryUrl())) {
            if (!TextUtils.isEmpty(mCampaignInfo.getTryUrl())) {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextSize(AndroidUtil.convertDpToPixel(this.getContext(), 14));
                paint.setColor(0xFFFFFFFF);
                //分别以15个dp作为边缘间距
                final int padding = AndroidUtil.convertDpToPixel(this.getContext(), 15);
                if (TextUtils.isEmpty(mCampaignInfo.getTryTitle())) {
                    mCampaignInfo.setTryTitle(getResources().getString(R.string.try_rightnow));
                }
                int buttonWidth = (int)paint.measureText(mCampaignInfo.getTryTitle(), 0, mCampaignInfo.getTryTitle().length() - 1) + (padding * 2);
                int finalButtonWidth = Math.min(buttonWidth + ShareData.PxToDpi_xhdpi(120), ShareData.m_screenWidth);
                mTryNow.setViewWidth(finalButtonWidth);
                mTryNow.setText(mCampaignInfo.getTryTitle());
                FrameLayout.LayoutParams params4 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                mTryNow.setLayoutParams(params4);
                mBottomViewContainer.addView(mTryNow);
                mBottomViewContainer.setVisibility(View.VISIBLE);
                mTryNow.addSkin(ImageUtils.GetSkinColor());
                mTryNow.setOnTouchListener(onAnimationClickListener);
            } else {
                mBottomViewContainer.setVisibility(View.GONE);
            }
        } else {
            Log.i(LOG, "the input params is null");
        }
    }


    @Override
    public void loadUrl(String url)
    {
        m_url = MyNetCore.GetPocoUrl(getContext(), url);
        m_url = AdUtils.AdDecodeUrl(getContext(), m_url);
        m_url = AddMyParams(getContext(), m_url);

        super.loadUrl(m_url);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (mIsBusinessArticle) {
            boolean result = mGestureDetector.onTouchEvent(ev);
            if (result) {
                return result;
            }
//        }
        return super.onInterceptTouchEvent(ev);
    }


    public static String AddMyParams(Context context, String url)
    {
        String out = url;

        if(out != null && out.contains("is_ime=1"))
        {
            String imei = CommonUtils.GetIMEI(context);
            if(imei != null && imei.length() > 0)
            {
                if(out.contains("?"))
                {
                    out += "&";
                }
                else
                {
                    out += "?";
                }
                out += "en_str=" + new String(MyEncode(imei, "beautycamera"));
                out += "&ime_str=" + imei;
            }
        }

        return out;
    }

    public static byte[] MyEncode(String key, String data)
    {
        byte[] out = null;

        byte[] keyArr = MD5(key).getBytes();
        byte[] dataArr = data.getBytes();

        int len = dataArr.length;
        int l = keyArr.length;
        int x = 0;
        for(int i = 0; i < len; i++)
        {
            if(x == l)
            {
                x = 0;
            }
            dataArr[i] += keyArr[x];
            x++;
        }
        out = Base64.encode(dataArr, Base64.DEFAULT | Base64.NO_WRAP);
        return out;
    }


    public static String MD5(String data)
    {
        String out = null;

        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer buf = new StringBuffer();
            String temp;
            for(int i = 0; i < encryption.length; i++)
            {
                temp = Integer.toHexString(0xff & encryption[i]);
                if(temp.length() == 1)
                {
                    buf.append("0").append(temp);
                }
                else
                {
                    buf.append(temp);
                }
            }

            out = buf.toString();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }

        return out;
    }

    public void reloadUrl()
    {
        if(m_url != null)
        {
            loadUrl(m_url);
        }
    }

    @Override
    public void onBack()
    {

        if(m_webView != null && m_webView.getVisibility() == View.GONE)
        {
            if(m_webChromeClient != null)
            {
                m_webChromeClient.onHideCustomView();
                mSite.OnBack(getContext());
                return;
            }
        }

        if(m_webView != null)
        {
            if(m_webView.canGoBack())
            {
                m_webView.goBack();
                mSite.OnBack(getContext());
                return;
            }
        }
        mSite.OnBack(getContext());
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        switch(siteID)
        {
            case SiteID.ALBUM:
            case SiteID.CAMERA:
            {
                if(params != null)
                {
                    RotationImg2[] imgs = (RotationImg2[])params.get("imgs");
                    if(imgs != null && imgs.length > 0 && imgs[0].m_orgPath != null)
                    {
                        Uri uri = Uri.fromFile(new File(imgs[0].m_orgPath));
                        if(uri != null)
                        {
                            if(m_filePathCallback2 != null)
                            {
                                m_filePathCallback2.onReceiveValue(new Uri[]{uri});
                                m_filePathCallback2 = null;
                            }
                            else if(m_filePathCallback1 != null)
                            {
                                m_filePathCallback1.onReceiveValue(uri);
                                m_filePathCallback1 = null;
                            }
                        }
                    }
                }

                if(m_filePathCallback1 != null)
                {
                    m_filePathCallback1.onReceiveValue(null);
                    m_filePathCallback1 = null;
                }
                if(m_filePathCallback2 != null)
                {
                    m_filePathCallback2.onReceiveValue(null);
                    m_filePathCallback2 = null;
                }
                break;
            }
        }
        super.onPageResult(siteID, params);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_FILE_CHOOSER) {
//            if (resultCode == Activity.RESULT_OK) {
//                Uri result = data.getData();
//                mValueCallback.onReceiveValue(new Uri[] {result});
//                return true;
//            } else {
//                mValueCallback.onReceiveValue(null);
//                mValueCallback = null;
//                return false;
//            }
//        }
        mShareActionSheet.getShareTools().onActivityResult(requestCode, resultCode, data);
        mShareBlogData.onActivityResult(requestCode, resultCode, data);
        return super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        StatisticHelper.countPageResume(getContext(), getContext().getString(R.string.运营专区_详情));
    }

    @Override
    public void onPause() {
        StatisticHelper.countPagePause(getContext(), getContext().getString(R.string.运营专区_详情));
    }

    @Override
    public void onClose() {
        super.onClose();
        mShareBlogData.ClearAll();
        StatisticHelper.countPageLeave(getContext(), getContext().getString(R.string.运营专区_详情));
    }


    private void addSkin() {
        ImageUtils.AddSkin(getContext(), m_backBtn);
        ImageUtils.AddSkin(getContext(), mTopBarRightBtn);
    }

    private void initAnimationTouchListener() {
        m_backBtn.setOnTouchListener(onAnimationClickListener);
        mTopBarRightBtn.setOnTouchListener(onAnimationClickListener);
    }

    private OnAnimationClickListener onAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == m_backBtn) {
                mSite.OnBack(getContext());
            } else if(v == mTopBarRightBtn) {
                Object tag = mTopBarRightBtn.getTag();
                if (tag instanceof String) {
                    String type = (String)tag;
                    if (type.equals("home_btn")) {
                        if (m_webView != null) {
                            if (!m_webView.canGoBack()) {
                                mSite.onBackWithAnimation(getContext());
                            }
                        }
                    } else if (type.equals("share_btn")) {
                        mShareActionSheet.show();
                    }
                }
                mAppInterface.onClickShare(mCampaignInfo.getShareIconId());
            } else if(v == mTryNow){
                mAppInterface.onClickShare(mCampaignInfo.getTryNowId());
//                if (mIsBusinessArticle) {
                Utils.UrlTrigger(v.getContext(), mCampaignInfo.getBusinessTryUrl());
//                }
                mSite.OnTryNow(v.getContext(), mCampaignInfo);
            }
        }
        @Override
        public void onTouch(View v) {

        }

        @Override
        public void onRelease(View v) {
        }
    };

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float mInitialX = e1.getX();
            float mInitialY = e1.getY();
            long mInitialTime = e1.getEventTime();

            float mFinalX = e2.getX();
            float mFinalY = e2.getY();
            long mFinalTime = e2.getEventTime();

            boolean distanceCondition = ((mFinalX - mInitialX) * 1.0f / (Math.abs(mFinalY - mInitialY))) > 3;
            final long twoSecond = 1000;
            boolean timeCondition = (mFinalTime - mInitialTime) < twoSecond;
            if (distanceCondition && timeCondition) {
                mSite.onBackWithAnimation(getContext());
                return true;
            }else {
                return false;
            }
        };
    }
}

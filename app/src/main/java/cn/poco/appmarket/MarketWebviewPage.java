package cn.poco.appmarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsClickAdRes;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.adMaster.RecommendAdBanner;
import cn.poco.adMaster.ShareAdBanner;
import cn.poco.advanced.ImageUtils;
import cn.poco.appmarket.site.MarketPageSite;
import cn.poco.blogcore.Tools;
import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MyWebView;
import cn.poco.tianutils.NetState;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * Created by pocouser on 2017/7/27.
 */

public class MarketWebviewPage extends IPage {
    private static final int TAG = R.string.精品推荐;

    public static final String URL = "http://zt.adnonstop.com/index.php?r=wap/recommend/page/index&os_type=android&come_from=beauty_camera";
    public static final String DEBUG_URL = "http://tw.adnonstop.com/zt/web/index.php?r=wap/recommend/page/index&os_type=android&come_from=beauty_camera";

    protected MarketPageSite m_Site;
    protected boolean mQuit;
    protected ImageView m_backBtn;
    protected TextView m_title;
    private WebView m_webview;
    private ImageView m_banner;
    private RecommendAdBanner m_bannerCore;
    private ArrayList<AbsAdRes> m_advRes;
    private WaitAnimDialog mWaitDialog;
    private LinearLayout mErrorTipLayout;

    public MarketWebviewPage(Context context, BaseSite site) {
        super(context, site);
        m_Site = (MarketPageSite) site;
        Init();

        TongJiUtils.onPageStart(getContext(), TAG);
    }

    protected void Init() {
        ShareData.InitData((Activity) getContext());
        LinearLayout body = new LinearLayout(getContext());
        body.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.addView(body, fl);
        {
            FrameLayout topBar = new FrameLayout(getContext());
//			topBar.setBackgroundResource(R.drawable.framework_top_bar_bg);
            topBar.setBackgroundColor(Color.WHITE);
            int topBarH = ShareData.PxToDpi_xhdpi(90);
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, topBarH);
            ll.gravity = Gravity.LEFT | Gravity.TOP;
            ll.weight = 0;
            body.addView(topBar, ll);
            {
                fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                m_backBtn = new ImageView(getContext());
                m_backBtn.setImageResource(R.drawable.framework_back_btn);
                topBar.addView(m_backBtn, fl);
                ImageUtils.AddSkin(getContext(), m_backBtn);
                m_backBtn.setOnTouchListener(new OnAnimationClickListener() {
                    @Override
                    public void onAnimationClick(View v) {
                        onBack();
                    }

                    @Override
                    public void onTouch(View v) {
                    }

                    @Override
                    public void onRelease(View v) {
                    }
                });

                m_title = new TextView(getContext());
                m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                m_title.setText(getResources().getString(R.string.appmarket_topbar_title));
                m_title.setTextColor(0xff000000);
                m_title.setGravity(Gravity.CENTER);
                fl = new LayoutParams(ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(300), LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.CENTER;
                topBar.addView(m_title, fl);
            }

            m_webview = new WebView(getContext());
            ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            ll.gravity = Gravity.LEFT | Gravity.TOP;
            ll.weight = 1;
            body.addView(m_webview, ll);
            InitWebViewSetting(m_webview.getSettings());
            m_webview.setWebViewClient(new MyWebView.MyWebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("openApp://") || url.startsWith("openapp://")) {
                        int pkg_pos = url.indexOf("pkgname=");
                        int pkg_pos2 = url.indexOf("&");
                        int url_pos = url.indexOf("appurl=");
                        if (pkg_pos != -1 && pkg_pos2 > pkg_pos) {
                            try {
                                String package_name = URLDecoder.decode(url.substring(pkg_pos + 8, pkg_pos2), "UTF-8");
                                if (Tools.checkApkExist(getContext(), package_name)) {
//									Toast.makeText(getContext(), getContext().getResources().getString(R.string.appmarket_webview_app_installed), Toast.LENGTH_SHORT).show();
                                    PackageManager packageManager = getContext().getPackageManager();
                                    Intent intent = packageManager.getLaunchIntentForPackage(package_name);
                                    getContext().startActivity(intent);
                                    return true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (url_pos == -1) return true;
                        try {
                            String app_url = URLDecoder.decode(url.substring(url_pos + 7), "UTF-8");
                            Uri uri = Uri.parse(app_url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            getContext().startActivity(Intent.createChooser(intent, ""));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if (url.startsWith("openApp://") || url.startsWith("openapp://")) return;
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mWaitDialog != null && mWaitDialog.isShowing()) {
                        mWaitDialog.dismiss();
                    }
                }
            });

            m_banner = new ImageView(getContext());
            m_banner.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            ll.gravity = Gravity.LEFT | Gravity.TOP;
            ll.weight = 0;
            body.addView(m_banner, ll);
            m_banner.setOnClickListener(m_OnClickListener);

            mWaitDialog = new WaitAnimDialog((Activity) getContext());
            mWaitDialog.SetGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, ShareData.PxToDpi_xhdpi(200));

        }
        mErrorTipLayout = new LinearLayout(getContext());
        mErrorTipLayout.setOrientation(LinearLayout.VERTICAL);
        mErrorTipLayout.setVisibility(View.GONE);
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        addView(mErrorTipLayout, fl);
        {
            ImageView icon = new ImageView(getContext());
            icon.setImageResource(R.drawable.campaigncenter_network_warn_big);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            mErrorTipLayout.addView(icon, lp);

            TextView txt = new TextView(getContext());
            txt.setText(getContext().getString(R.string.poor_network));
            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            txt.setTextColor(0xffcccccc);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mErrorTipLayout.addView(txt, lp);
        }

        m_bannerCore = new RecommendAdBanner(getContext());
        m_bannerCore.Run(new ShareAdBanner.Callback() {
            @Override
            public void ShowBanner(ArrayList<AbsAdRes> arr) {
                m_advRes = arr;
                if (arr != null && arr.size() > 0 && arr.get(0) instanceof AbsClickAdRes && NetState.IsConnectNet(getContext())) {
                    AbsClickAdRes res = (AbsClickAdRes) arr.get(0);
                    if (res.url_adm != null && res.url_adm.length > 0 && res.url_adm[0] != null) {
                        Glide.with(getContext()).load(res.url_adm[0])
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .fitCenter()
                                .into(m_banner);
                        m_bannerCore.Show();
                    }
                }
            }
        });

    }

    protected void InitWebViewSetting(WebSettings settings) {
        settings.setAppCachePath(getContext().getDir(MyWebView.CACHE_FILE_NAME, Context.MODE_PRIVATE).getPath());
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);

        settings.setGeolocationDatabasePath(getContext().getDir(MyWebView.GPS_DB_FILE_NAME, Context.MODE_PRIVATE).getPath());
        settings.setGeolocationEnabled(true);

        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        //settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);
        //settings.setSupportZoom(true);
        //settings.setBuiltInZoomControls(true);
        //settings.setAppCacheMaxSize(1024 * 1024 * 30);
        //settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
    }

    private Bitmap makeBannerBitmap(Bitmap org) {
        if (org == null || org.isRecycled()) return null;
        int w = org.getWidth();
        int h = org.getHeight();
        int out_h = (int) ((float) ShareData.m_screenWidth * h / w);
        return MakeBmp.CreateFixBitmap(org, ShareData.m_screenWidth, out_h, MakeBmp.POS_START, 0, Bitmap.Config.ARGB_8888);
    }

    private OnClickListener m_OnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == m_banner) {
                if (m_bannerCore != null && m_advRes != null && m_advRes.size() > 0 && m_Site != null) {
                    TongJi2.AddCountByRes(getContext(), R.integer.精品应用_banner);
                    m_bannerCore.Click(m_advRes.get(0), m_Site.m_cmdProc);
                }
            } else if (v == m_backBtn) {
                onBack();
            }
        }
    };

    @Override
    public void onClose() {
        TongJiUtils.onPageEnd(getContext(), TAG);
        mQuit = true;
        if (m_bannerCore != null) {
            m_bannerCore.Clear();
            m_bannerCore = null;
        }
        if (m_advRes != null) {
            m_advRes.clear();
            m_advRes = null;
        }
        if (m_webview != null) {
            m_webview.stopLoading();
            m_webview.loadUrl("about:blank");
            m_webview.setOnClickListener(null);
            m_webview.setOnTouchListener(null);
            m_webview.setWebViewClient(null);
            m_webview.setWebChromeClient(null);
            //还需要移除所有JavascriptInterface
            MarketWebviewPage.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (m_webview != null) {
                        m_webview.stopLoading();
                        m_webview.destroyDrawingCache();
                        m_webview.destroy();
                        m_webview = null;
                        MarketWebviewPage.this.removeAllViews();
                    }
                }
            }, 1000);
        }
        cleanGlide();
        super.onClose();
    }

    private void cleanGlide() {
        Glide.get(getContext()).clearMemory();
//        new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                Glide.get(PocoCamera.main).clearDiskCache();
//            }
//        }).start();
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params == null) return;
        String background = (String) params.get("background");
        if (background != null && background.length() > 0) {
            Bitmap bmp = cn.poco.imagecore.Utils.DecodeFile(background, null);
            if (bmp == null || bmp.isRecycled()) return;
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(0x4DFFFFFF);
            setBackgroundDrawable(new BitmapDrawable(bmp));
        }

        if (!NetWorkUtils.isNetworkConnected(getContext())) {
            if (mErrorTipLayout != null) {
                mErrorTipLayout.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (mWaitDialog != null) {
            mWaitDialog.show();
        }
        String url = SysConfig.IsDebug() ? DEBUG_URL : URL;
        m_webview.loadUrl(url + "&" + SysConfig.GetAppVerNoSuffix(getContext()));
    }

    @Override
    public void onBack() {
        if (m_webview != null) {
            if (m_webview.canGoBack()) {
                m_webview.goBack();
                return;
            }
        }

        m_Site.onBack();
    }
}

package cn.poco.taskCenter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import cn.poco.advanced.ImageUtils;
import cn.poco.banner.BannerCore3;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.credits.Credit;
import cn.poco.credits.Utils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.login.HttpResponseCallback;
import cn.poco.login.LoginUtils2;
import cn.poco.login.TipsDialog;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.LoginUtils;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.scorelibs.AbsAppInstall;
import cn.poco.scorelibs.CreditUtils;
import cn.poco.setting.SettingPage;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.taskCenter.site.TaskCenterPageSite;
import cn.poco.tianutils.NetCore2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 任务大厅
 */
public class TaskCenterPage extends IPage implements View.OnClickListener {

    //private static final String TAG = "任务大厅";

    private static final String TASK_INTRO_URL = "http://www.adnonstop.com/beauty_camera/wap/credit_description.php";

    public static final String EXTRA_USER_ID = "cn.poco.taskCenter.TaskCenterPage.user_id";
    public static final String EXTRA_ACCESS_TOKEN = "cn.poco.taskCenter.TaskCenterPage.access_token";
    public static final String EXTRA_BITMAP = "cn.poco.taskCenter.TaskCenterPage.bg_bitmap";

    // =============== 跳转协议 =============== //
    // 模板列表 ?index=0
    private static final String MODEL_CMD = "jane://action_model/?";
    // 首页广告 ?position=0
    private static final String AD_INDEX_CMD = "jane://action_ad_index/?";
    // 外跳链接 ?openUrl=www.baidu.com
    private static final String EXTERNAL_WEB_CMD = "jane://action_externalWeb/?";
    // 内跳链接 ?openUrl=www.baidu.com
    private static final String INSIDE_WEB_CMD = "jane://action_insideWeb/?";
    // 云相册
    private static final String CLOUD_ALBUM_CMD = "jane://action_cloudAlbum/";
    // 用户中心: sex / birthday / area / bindPhone
    private static final String USER_CENTER_CMD = "jane://action_userCenter/";
    // 反馈
    private static final String FEEDBACK_CMD = "jane://action_feedback/";
    // APP下载
    private static final String DOWNLOAD_APP_CMD = "jane://action_feedback/";
    // 定制活动(商业通道) jane://action_Business/?businessId=visitingcardtest_201605
    private static final String BUSINESS_CMD = "jane://action_Business/?";
    private static final String CHANGE_CREDIT_CMD = "jane://action_changeCredit/?";
    private static final String TJ_CMD = "beautycamera://action_tj/?";
    private static final String IGNORE = "beautycamera://action_changeCredit/?";

    // 分享邀请码到第三方平台
    private static final String INVITE_CMD = "jane://action_share/?";
    // TP
    private static final String TP_WEIXIN = "weixin";
    private static final String TP_WEIBO = "sina";
    private static final String TP_QQ_ZONE = "qqzone";
    // 邀请对应的keys
    private static final String KEY_SHARE_PLATFORM = "shareplatform";
    private static final String KEY_SHARE_TXT = "sharetxt";
    private static final String KEY_SHARE_IMG = "shareimg";
    private static final String KEY_SHARE_LINK = "sharelink";
    // 微信: 朋友圈 = 0/微信好友 = 1
    private static final String KEY_WEIXIN_USER = "weixinuser";
    /**
     * 分享平台: 微博/微信/...
     */
    private int mSharePlatformType = -1;

    private String mSharePlatform;
    private String mShareTxt;
    private String mShareImg;
    private String mShareLink;
    private String mWeixinUser;

    private Context mContext;

    private Bitmap mBgBitmap;
//    private ImageButton mBackImgBtn;
    private ImageView mBackImgBtn;
    private ImageView mInfoImgBtn;
    private TextView mTitleTextView;
//    private ImageButton mInfoImgBtn;
    private WebView mWebView;
    protected ProgressDialog mProgressDialog;
    private ShareTools m_share;
    private RelativeLayout rl_net_error;


    private Handler mHandler = new Handler();

//    private ShareManager2 mShareManager;

    private String mUrl;

    protected TaskCenterPageSite mSite;

    public TaskCenterPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (TaskCenterPageSite) site;
        mContext = context;
        m_share = new ShareTools(getContext());
        m_share.needAddIntegral(false);
        initView();

        //检查已安装poco系应用 -> 积分触发
//        String actionIds = LoginBiz.getInstalledCredit(context, APIConfig.JANEPLUS_PACKAGE_NAME);
        if( Utils.isInstalled(context, AbsAppInstall.PN_JP)){
            Credit.CreditIncome(context, context.getResources().getInteger(R.integer.积分_下载_简客)+"");
        }
        TongJiUtils.onPageStart(getContext(), R.string.任务大厅);
    }

    private void initView() {
        this.setBackgroundColor(0xFFFFFFFF);
        RelativeLayout m_mainView = (RelativeLayout) inflate(mContext, R.layout.task_center_page_layout, null);
        addView(m_mainView);
        mBackImgBtn = (ImageView) findViewById(R.id.task_center_page_ib_back);
        ImageUtils.AddSkin(getContext(), mBackImgBtn);
        mBackImgBtn.setOnClickListener(this);
        mInfoImgBtn = (ImageView) findViewById(R.id.task_center_page_ib_info);
        mInfoImgBtn.setOnClickListener(this);
        mTitleTextView = (TextView) findViewById(R.id.task_center_page_tv_title);
        rl_net_error = (RelativeLayout) findViewById(R.id.rl_net_error);

        mTitleTextView.setTextColor(0xff333333);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
        ImageUtils.AddSkin(getContext(),mBackImgBtn);
        ImageUtils.AddSkin(getContext(),mInfoImgBtn);

        TextView errorTextView = (TextView) findViewById(R.id.textView3);
        errorTextView.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
        TextView errorTextView1 = (TextView) findViewById(R.id.textview4);
        errorTextView1.setTextColor(ImageUtils.GetSkinColor(0xffe75988));
        ImageView errorWifiImg = (ImageView) findViewById(R.id.iv_wifi);
        ImageUtils.AddSkin(getContext(),errorWifiImg);

        RelativeLayout m_topBar = (RelativeLayout) findViewById(R.id.task_center_page_title_bar);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
        rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        m_topBar.setLayoutParams(rl);

        // 配置WebView
        mWebView = (WebView) findViewById(R.id.task_center_page_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        if (!NetWorkUtils.isNetContected(getContext()) && !NetWorkUtils.isWifiContected(getContext())) {
            rl_net_error.setVisibility(VISIBLE);
            mWebView.setVisibility(GONE);
            mInfoImgBtn.setOnClickListener(null);
            return;
        }

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.task_center_page_pb);
        progressBar.getProgressDrawable().setColorFilter(ImageUtils.GetSkinColor(), PorterDuff.Mode.SRC_IN);
        progressBar.setMax(100); // WebChromeClient reports in range 0-100
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitleTextView.setText(title);
            }

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(TJ_CMD)) {
                    String tjId = getParams(url).get("id");
                    //Log.d(TAG, "shouldOverrideUrlLoading: the TJ id -> " + tjId);
                    TongJi2.AddCountById(tjId);
                    return true;
                }
                String temp = url.toLowerCase(Locale.ENGLISH);
                if (!temp.startsWith("http") && !temp.startsWith("ftp")) {
                    ParseCommand(url);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.startsWith("BeautyCamera://") || url.startsWith("beautycamera://")) {
                    ParseCommand(url);
                } else {
                    super.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                rl_net_error.setVisibility(VISIBLE);
                mWebView.setVisibility(GONE);
                mInfoImgBtn.setOnClickListener(null);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(view.getTitle() != null)
                {
                    mTitleTextView.setText(view.getTitle());
                }
            }
        });
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        if(siteID == SiteID.WEBVIEW)
        {
            TongJi2.EndPage(getContext(),getResources().getString(R.string.任务大厅_积分协议));
        }
    }

    protected void ParseCommand(String command) {
        BannerCore3.CmdStruct struct = BannerCore3.GetCmdStruct(command);
        if (struct != null) {
            if (struct.m_cmd != null && struct.m_cmd.equals("action_share")) {
                ArrayList<String> args = new ArrayList<String>();
                if (struct.m_params != null) {
                    for (int i = 0; i < struct.m_params.length; i++) {
                        args.add(struct.m_params[i]);
                    }
                }

                if (args.size() > 0) {
                    //shareplatform=(sina,qzone,qq,weixin)&sharetxt=xxxx&shareimg=xxxx&sharelink=xxxx&weixinuser=1
                    String content = "";
                    String platform = "";
                    String callbackUrl = "";
                    String imgUrl = "";
                    String weixinUser = "";
                    String tjId = null;
                    for (int i = 0; i < args.size(); i++) {
                        String[] pair = args.get(i).split("=");
                        if (pair.length == 2) {
                            if (pair[0].equals("shareplatform")) {
                                platform = pair[1];
                            } else if (pair[0].equals("sharetxt")) {
                                content = pair[1];
                            } else if (pair[0].equals("sharelink")) {
                                callbackUrl = pair[1];
                            } else if (pair[0].equals("shareimg")) {
                                imgUrl = pair[1];
                            } else if (pair[0].equals("weixinuser")) {
                                weixinUser = pair[1];
                            } else if (pair[0].equals("tj_id")) {
                                tjId = pair[1];
                            }
                        }
                    }
                    if (platform.equals("weixin") && weixinUser.equals("1")) {
                        platform = "weixinuser";
                    }
                    shareTo(platform, content, imgUrl, callbackUrl, tjId);
                }
                return;
            }

            if(command.contains(IGNORE))
            {
                return;
            }
            BannerCore3.ExecuteCommand(getContext(), command, mSite.m_cmdProc);
        }
    }

    protected void ShowWaitDlg() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getContext(), "", "发送中...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.show();
    }

    protected void CloseWaitDlg() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void shareTo(final String platform, final String text, final String orgImgUrl, final String orgCallbackUrl, String tjId) {
        TongJi2.AddCountById(tjId);

        final String content = text != null ? URLDecoder.decode(text) : null;
        final String imgUrl = URLDecoder.decode(orgImgUrl);
        final String callbackUrl = URLDecoder.decode(orgCallbackUrl);

        if (imgUrl != null && imgUrl.length() > 0) {
            ShowWaitDlg();

            final Handler uiHandler = new Handler();
            final String imgPath = FileCacheMgr.GetAppPath(".jpg");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NetCore2 net = new NetCore2();
                    final NetCore2.NetMsg msg = net.HttpGet(imgUrl, null, imgPath, null);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK) {
                                if (platform.equals("sina")) {
                                    CloseWaitDlg();
                                    if (SettingPage.checkSinaBindingStatus(getContext())) {
                                        m_share.sendToSinaBySDK(content + " " + callbackUrl, imgPath, new ShareTools.SendCompletedListener() {
                                            @Override
                                            public void getResult(Object result) {
                                                int response = (int) result;
                                                if (response == WBConstants.ErrorCode.ERR_OK)
                                                    Toast.makeText(getContext(), "发送新浪微博成功", Toast.LENGTH_LONG).show();
                                                else if (response == WBConstants.ErrorCode.ERR_CANCEL)
                                                    Toast.makeText(getContext(), "取消发送新浪微博", Toast.LENGTH_LONG).show();
                                                else if (response == -1)
                                                    Toast.makeText(getContext(), "尚未安装新浪微博客户端", Toast.LENGTH_LONG).show();
                                                else
                                                    Toast.makeText(getContext(), "发送新浪微博失败", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        m_share.bindSina(new SharePage.BindCompleteListener() {
                                            @Override
                                            public void success() {
                                                m_share.sendToSinaBySDK(content + " " + callbackUrl, imgPath, new ShareTools.SendCompletedListener() {
                                                    @Override
                                                    public void getResult(Object result) {
                                                        int response = (int) result;
                                                        if (response == WBConstants.ErrorCode.ERR_OK)
                                                            Toast.makeText(getContext(), "发送新浪微博成功", Toast.LENGTH_LONG).show();
                                                        else if (response == WBConstants.ErrorCode.ERR_CANCEL)
                                                            Toast.makeText(getContext(), "取消发送新浪微博", Toast.LENGTH_LONG).show();
                                                        else if (response == -1)
                                                            Toast.makeText(getContext(), "尚未安装新浪微博客户端", Toast.LENGTH_LONG).show();
                                                        else
                                                            Toast.makeText(getContext(), "发送新浪微博失败", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void fail() {
                                            }
                                        });
                                    }
                                } else if (platform.equals("qqzone")) {
                                    if (SettingPage.checkQzoneBindingStatus(getContext())) {
                                        m_share.sendUrlToQzone(imgPath, "来自美人相机分享", content, callbackUrl, new ShareTools.SendCompletedListener() {
                                            @Override
                                            public void getResult(Object result) {
                                                CloseWaitDlg();
                                                switch ((int) result) {
                                                    case QzoneBlog2.SEND_SUCCESS:
                                                    case QzoneBlog2.SEND_FAIL:
                                                        break;

                                                    case QzoneBlog2.SEND_CANCEL:
                                                        Toast.makeText(getContext(), "取消发送到QQ空间", Toast.LENGTH_LONG).show();
                                                        break;

                                                    default:
                                                        AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                                                        dlg.setTitle("提示");
                                                        dlg.setMessage("还没有安装最新手机QQ，需要安装后才能发送");
                                                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener) null);
                                                        dlg.show();
                                                        break;
                                                }
                                            }
                                        });
                                    } else {
                                        CloseWaitDlg();
                                        m_share.bindQzone(new SharePage.BindCompleteListener() {
                                            @Override
                                            public void success() {
                                                ShowWaitDlg();
                                                m_share.sendUrlToQzone(imgPath, "来自美人相机分享", content, callbackUrl, new ShareTools.SendCompletedListener() {
                                                    @Override
                                                    public void getResult(Object result) {
                                                        CloseWaitDlg();
                                                        switch ((int) result) {
                                                            case QzoneBlog2.SEND_SUCCESS:
                                                            case QzoneBlog2.SEND_FAIL:
                                                                break;

                                                            case QzoneBlog2.SEND_CANCEL:
                                                                Toast.makeText(getContext(), "取消发送到QQ空间", Toast.LENGTH_LONG).show();
                                                                break;

                                                            default:
                                                                AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                                                                dlg.setTitle("提示");
                                                                dlg.setMessage("还没有安装最新手机QQ，需要安装后才能发送");
                                                                dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener) null);
                                                                dlg.show();
                                                                break;
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void fail() {
                                            }
                                        });
                                    }
                                } else if (platform.equals("qq")) {
                                    if (SettingPage.checkQzoneBindingStatus(getContext())) {
                                        m_share.sendUrlToQQ("来自美人相机分享", content, imgPath, callbackUrl, new ShareTools.SendCompletedListener() {
                                            @Override
                                            public void getResult(Object result) {
                                                CloseWaitDlg();
                                                switch ((int) result) {
                                                    case QzoneBlog2.SEND_SUCCESS:
                                                    case QzoneBlog2.SEND_FAIL:
                                                        break;

                                                    case QzoneBlog2.SEND_CANCEL:
                                                        Toast.makeText(getContext(), "取消发送到QQ", Toast.LENGTH_LONG).show();
                                                        break;

                                                    default:
                                                        AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                                                        dlg.setTitle("提示");
                                                        dlg.setMessage("还没有安装最新手机QQ，需要安装后才能发送");
                                                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener) null);
                                                        dlg.show();
                                                        break;
                                                }
                                            }
                                        });
                                    } else {
                                        CloseWaitDlg();
                                        m_share.bindQzone(new SharePage.BindCompleteListener() {
                                            @Override
                                            public void success() {
                                                ShowWaitDlg();
                                                m_share.sendUrlToQQ("来自美人相机分享", content, imgPath, callbackUrl, new ShareTools.SendCompletedListener() {
                                                    @Override
                                                    public void getResult(Object result) {
                                                        CloseWaitDlg();
                                                        switch ((int) result) {
                                                            case QzoneBlog2.SEND_SUCCESS:
                                                            case QzoneBlog2.SEND_FAIL:
                                                                break;

                                                            case QzoneBlog2.SEND_CANCEL:
                                                                Toast.makeText(getContext(), "取消发送到QQ", Toast.LENGTH_LONG).show();
                                                                break;

                                                            default:
                                                                AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
                                                                dlg.setTitle("提示");
                                                                dlg.setMessage("还没有安装最新手机QQ，需要安装后才能发送");
                                                                dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener) null);
                                                                dlg.show();
                                                                break;
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void fail() {
                                            }
                                        });
                                    }
                                } else if (platform.equals("weixin")) {
                                    CloseWaitDlg();
                                    m_share.sendUrlToWeiXin(imgPath, callbackUrl, content, null, false, new ShareTools.SendCompletedListener() {
                                        @Override
                                        public void getResult(Object result) {
                                            switch ((int) result) {
                                                case BaseResp.ErrCode.ERR_OK:
                                                    Toast.makeText(getContext(), "发送微信朋友圈成功", Toast.LENGTH_LONG).show();
                                                    break;

                                                case BaseResp.ErrCode.ERR_USER_CANCEL:
                                                    Toast.makeText(getContext(), "取消发送微信朋友圈", Toast.LENGTH_LONG).show();
                                                    break;

                                                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                                    Toast.makeText(getContext(), "发送微信朋友圈失败", Toast.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }
                                    });
                                } else if (platform.equals("weixinuser")) {
                                    CloseWaitDlg();
                                    m_share.sendUrlToWeiXin(imgPath, callbackUrl, content, null, true, new ShareTools.SendCompletedListener() {
                                        @Override
                                        public void getResult(Object result) {
                                            switch ((int) result) {
                                                case BaseResp.ErrCode.ERR_OK:
                                                    Toast.makeText(getContext(), "发送微信好友成功", Toast.LENGTH_LONG).show();
                                                    break;

                                                case BaseResp.ErrCode.ERR_USER_CANCEL:
                                                    Toast.makeText(getContext(), "取消发送微信好友", Toast.LENGTH_LONG).show();
                                                    break;

                                                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                                    Toast.makeText(getContext(), "发送微信好友失败", Toast.LENGTH_LONG).show();
                                                    break;
                                            }
                                        }
                                    });
                                }
                            } else {
                                CloseWaitDlg();
                                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    /**
     * 实现View.OnClickListener接口
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
//        Bitmap bg = Utils.takeScreenShot((Activity) getContext());
        switch (v.getId()) {
            case R.id.task_center_page_ib_back:
                // WebView后退到之前打开的页面
                onBack();
                break;
            case R.id.task_center_page_ib_info:
                TongJi2.StartPage(getContext(),getResources().getString(R.string.任务大厅_积分协议));
                mSite.OpenWebView(TASK_INTRO_URL,getContext());
//                popupBrowserPage(Constant.URL_INTEGRAL_INTRO);
//                    EventBus.getDefault().post(new IntegralEvent(20, false));
                break;
            default:
                break;
        }
    }

    /**
     * 解析字符串返回 名称=值的参数表 (www.xxx.com?a=1&b=2 => a=1,b=2)
     *
     * @param url
     * @return
     */
    private LinkedHashMap<String, String> getParams(String url) {
        url = url.substring(url.indexOf('?') + 1, url.length());
        if (url != null && !url.equals("") && url.indexOf("=") > 0) {
            LinkedHashMap result = new LinkedHashMap();

            String name = null;
            String value = null;
            int i = 0;
            while (i < url.length()) {
                char c = url.charAt(i);
                switch (c) {
                    case 61: // =
                        value = "";
                        break;
                    case 38: // &
                        if (name != null && value != null && !name.equals("")) {
                            try {
                                value = URLDecoder.decode(value, "utf-8");
                                result.put(name, value);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        name = null;
                        value = null;
                        break;
                    default:
                        if (value != null) {
                            value = (value != null) ? (value + c) : "" + c;
                        } else {
                            name = (name != null) ? (name + c) : "" + c;
                        }
                }
                i++;
            }

            if (name != null && value != null && !name.equals("")) {
                try {
                    value = URLDecoder.decode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result.put(name, value);
            }

            return result;
        }
        return null;
    }

    /**
     * 1. String userId         用户Id (key: {@link TaskCenterPage#EXTRA_USER_ID})
     * 2. String accessToken    访问Token (key: {@link TaskCenterPage#EXTRA_ACCESS_TOKEN})
     * 3. String bgBitmap       进入任务大厅时前一个页面的截屏的高斯模糊的Bitmap (key: {@link TaskCenterPage#EXTRA_BITMAP})
     *
     * @param params
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        String userId = (String) params.get(EXTRA_USER_ID);
        String accessToken = (String) params.get(EXTRA_ACCESS_TOKEN);
        checkIsOutLogin(userId,accessToken);
//        mBgBitmap = Utils.DecodeFile((String)params.get(EXTRA_BITMAP), null);
    }

    private void checkIsOutLogin(final String id, final String token)
    {
        LoginUtils2.getUserInfo(id, token, new HttpResponseCallback()
        {
            @Override
            public void response(Object object)
            {
                if(object == null) return;
                UserInfo m_netInfo = (UserInfo)object;
                if(m_netInfo.mProtocolCode == 205)
                {
                    TipsDialog tipsDialog = new TipsDialog(getContext(), null, "登录信息过期，请重新登录!", null, "确定", new TipsDialog.Listener()
                    {
                        @Override
                        public void cancel()
                        {

                        }

                        @Override
                        public void ok()
                        {
                            UserMgr.ExitLogin(getContext());
                            EventCenter.sendEvent(EventID.HOMEPAGE_UPDATE_MENU_AVATAR);
                            mSite.toLoginPage(getContext());
                        }
                    });
                    tipsDialog.showDialog();
                }
                else
                {
                    mUrl = CreditUtils.GetMyTaskCenterUrl(id, token, AppInterface.GetInstance(getContext()));
                    mWebView.loadUrl(mUrl);
                }
            }
        });
    }

    @Override
    public void onBack() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            mSite.OnBack(getContext());
        }
    }

    @Override
    public void onClose() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        TongJiUtils.onPageEnd(getContext(), R.string.任务大厅);
        super.onClose();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        if(mWebView != null)
        {
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.任务大厅);
    }

    @Override
    public void onResume() {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.任务大厅);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        m_share.onActivityResult(requestCode, resultCode, data);

        return super.onActivityResult(requestCode, resultCode, data);
    }
}

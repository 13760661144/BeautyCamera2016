package cn.poco.lightApp06;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.AbsUploadFile;
import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsChannelAdRes;
import com.adnonstop.admasterlibs.data.UploadData;
import com.circle.ctrls.SharedTipsView;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.poco.adMaster.HomeAd;
import cn.poco.adMaster.UploadFile;
import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.BeautifyResMgr2;
import cn.poco.blogcore.FacebookBlog;
import cn.poco.blogcore.InstagramBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.blogcore.TwitterBlog;
import cn.poco.blogcore.WeiXinBlog;
import cn.poco.blogcore.WeiboInfo;
import cn.poco.business.ChannelValue;
import cn.poco.camera.BrightnessUtils;
import cn.poco.camera.CameraSetDataKey;
import cn.poco.camera.RotationImg2;
import cn.poco.camera2.AudioControlUtils;
import cn.poco.camera3.VideoMgr;
import cn.poco.filter4.WatermarkItem;
import cn.poco.framework.BaseSite;
import cn.poco.framework.DataKey;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.holder.ObjHandlerHolder;
import cn.poco.home.site.HomePageSite;
import cn.poco.image.filter;
import cn.poco.lightApp06.site.LightApp06PageSite;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.setting.SettingPage;
import cn.poco.share.AdvBannerViewPager;
import cn.poco.share.DialogView2;
import cn.poco.share.ShareButton;
import cn.poco.share.ShareFrame;
import cn.poco.share.SharePage;
import cn.poco.share.ShareTools;
import cn.poco.share.SinaRequestActivity;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.utils.PhotoMark;
import cn.poco.utils.Utils;
import cn.poco.video.NativeUtils;
import cn.poco.video.VideoPreviewPage;
import cn.poco.video.VideoUtils;
import my.beautyCamera.R;
import my.beautyCamera.wxapi.SendWXAPI;

import static cn.poco.setting.SettingInfoMgr.GetSettingInfo;

/**
 * 动态贴纸
 * @deprecated
 */
public class LightApp06Page extends IPage {
    private static final int TAG = R.string.动态贴纸_分享;

    private LightApp06PageSite mPageSite;

    private VideoPreviewPage m_videoFrame;
    private FrameLayout m_progressFrame;
    private FrameLayout m_shareFrame;
    private ImageView m_shareBackground;
    private View m_topBackground;
    private FrameLayout m_topFrame;
    private FrameLayout m_bottomFrame;
    private HorizontalScrollView m_weiboScroll;
    private ImageView m_homeBtn;
    private ImageView m_backBtn;
//    private ImageView mIconTogether;
//    private FrameLayout mTogetherTip;
    private ImageView mIconQQ;
    private ImageView mIconSina;
    private ImageView mIconQzone;
    private ImageView mIconFaceBook;
    private ImageView mIconTwitter;
    private ImageView mIconInstagram;
    private ImageView mIconWeiXin;
    private ImageView mIconWXFriends;
    private LinearLayout m_weiboFrame;
    private LinearLayout m_imageFrame;
    private ImageView mImageHolder;
    private LinearLayout m_buttonFrame;
    private ShareButton m_cameraBtn;
    private ShareButton m_beautifyBtn;
//    private ShareButton m_communityBtn;
    private RoundProgressBar m_progress;
    private AdvBannerViewPager mAdvBar;

    private Bitmap m_ShareBmp;

    private boolean m_onClose = false;
    private boolean m_videoSaving = true;    //视频是否保存中
    private String m_savePath;
    private boolean m_videoSaved = false;
    private long mVideoDuration;
    private String m_shareUrl;
    private String m_picPath;
    private SinaBlog mSina;
    private QzoneBlog2 mQzone;
    private WeiXinBlog mWeiXin;
    private FacebookBlog mFacebook;
    private TwitterBlog mTwitter;
    private InstagramBlog mInstagram;
//    private ICIRCLEAPI mCircleApi;
    private ProgressDialog mProgressDialog;
    private String m_shareBrowse;
    private boolean m_showVideoFrame = true;
    private Bitmap m_background;
    private boolean m_bmpSaved = false;
    private boolean m_uploadComplete = false;
    private Bitmap m_shareDialogBG;
    private boolean m_isShareVideoDialogShow = false;
    private boolean onAnimation = false;
    private int mWaterMarkResId = -1;

    private ObjHandlerHolder<AbsUploadFile.Callback> m_uploadCallback;

    private String mShareTitle = getResources().getString(R.string.lightapp06_share_title);
    private String mShareText = getResources().getString(R.string.lightapp06_share_text);
	private String mShareDescription = getResources().getString(R.string.lightapp06_share_description);
    private String mShareFriendsTitle = getResources().getString(R.string.lightapp06_share_title);

    private float mPreviewResRatio;      //预览资源的比例 {@link cn.poco.camera.CameraConfig.PreviewRatio}
    private boolean m_thirdParty = false;//第三方调用
    protected VideoMgr m_videoMgr = null;//分段视频数据对象
    private int mOrientation = 0;
    private boolean mVideoRecordAudioEnable = true;//视频录音是否静音

    //true：视频合成成功后直接跳转到save，不弹出分享页
    private boolean mIsSaveJump = false;

    //商业
    private String mChannelValue;
    protected AbsChannelAdRes mActChannelAdRes;
    protected AbsChannelAdRes.SharePageData mActConfigure;
    private JSONObject mAdvPostStr;
    //阿玛尼商业
    private boolean mArmani = false;
    //YSL商业
    private boolean mYSL = false;

    public interface UploadVideoCallback {
        public void uploadComplete(String url);

        public void uploadFail();
    }

    public interface ShareVideoDialogCallback
    {
        public void shareVideoType(boolean uploadServer);
    }

    private final int BG = 0x1;
    private final int PROGRESS = 0x2;
    private final int FINISH = 0x3;
    private PageHandler mPageHandler;

    private int resId;
    private String resTjId;

    private class PageHandler extends Handler {
        public PageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BG:
                    setBackground((Bitmap) msg.obj);
                    break;
                case PROGRESS:
                    setVideoSaveProgress(msg.arg1);
//					Log.i("bbb", "Progress:"+msg.arg1);
                    break;
                case FINISH:
//					Toast.makeText(getContext(), "录制完成:", Toast.LENGTH_SHORT).show();
                    String path = (String) msg.obj;
//                    Log.i("bbb", "mp4Path:" + path);
                    setVideoCachePath(path);
                    break;
                default:
                    break;
            }
        }
    }

    public LightApp06Page(Context context, BaseSite site) {
        super(context, site);
        mPageSite = (LightApp06PageSite) site;

        SharePage.initBlogConfig(context);
        ShareData.InitData(context);

        mWaterMarkResId = WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext());
        mPageHandler = new PageHandler(Looper.getMainLooper());

        TongJiUtils.onPageStart(getContext(), TAG);
    }

    public static String makePostVar(Context context, String channelValue)
    {
        //String out = "|os:android";
        String out = "";

        if(channelValue != null)
        {
            out += "|channel_value:" + URLEncoder.encode(channelValue);
        }
        String imei = CommonUtils.GetIMEI(context);
        if(imei == null)
        {
            imei = java.util.UUID.randomUUID().toString();
        }
        out += "|hash:" + CommonUtils.Encrypt("MD5", imei);

        return out;
    }

    /**
     * type : 0 图片 1：视频
     * @param params
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        int type = -1;
        Bitmap bmp = null;
        String mp4Path = null;
        int filterId = 0;
        int videoWidth = 0;
        int videoHeight = 0;

        if (params != null) {
            if (params.containsKey("type")) {
                type = (Integer) params.get("type");
            }
            if (params.containsKey("bmp")) {
                bmp = (Bitmap) params.get("bmp");
            }
            if (params.containsKey("mp4_path")) {
                mp4Path = (String) params.get("mp4_path");
            }
            if (params.containsKey(DataKey.COLOR_FILTER_ID)) {
                filterId = (Integer) params.get(DataKey.COLOR_FILTER_ID);
            }
            if (params.containsKey("width")) {
                videoWidth = (Integer) params.get("width");
            }
            if (params.containsKey("height")) {
                videoHeight = (Integer) params.get("height");
            }
            if (params.containsKey("ratio")) {
                this.mPreviewResRatio = (Float) params.get("ratio");
            }
            if (params.containsKey("video_mgr"))
            {
                m_videoMgr = (VideoMgr) params.get("video_mgr");
            }
            if (params.containsKey("orientation"))
            {
                mOrientation = (int) params.get("orientation");
            }
            if (params.containsKey("save_jump"))
            {
                this.mIsSaveJump = (Boolean) params.get("save_jump");
            }
            if (params.containsKey("record_audio_enable"))
            {
                this.mVideoRecordAudioEnable = (Boolean) params.get("record_audio_enable");
            }
            if (params.containsKey("channelValue"))
            {
                mChannelValue = (String) params.get("channelValue");
                if(mChannelValue != null && mChannelValue.length() > 0)
                {
                    AbsAdRes adRes = HomeAd.GetOneHomeRes(getContext(), mChannelValue);
                    if(adRes != null && adRes instanceof AbsChannelAdRes)
                    {
                        mActChannelAdRes = (AbsChannelAdRes)adRes;
                        mActConfigure = (AbsChannelAdRes.SharePageData)((AbsChannelAdRes)adRes).GetPageData(AbsChannelAdRes.SharePageData.class);
                        if(mActConfigure != null && System.currentTimeMillis() < mActChannelAdRes.mEndTime)
                        {
                            Utils.UrlTrigger(getContext(), mActConfigure.mSendTj);
                            mAdvPostStr = HomePageSite.makePostVar(getContext(), mActChannelAdRes.mAdId);
                            mShareText = mActConfigure.mDefaultContent;
                            mShareTitle = mActConfigure.mWeixinFriendTitle;
                            mShareDescription = mActConfigure.mWeixinFriendContent;
                            mShareFriendsTitle = mActConfigure.mWeixinCircleTitle;
                            if(mChannelValue.equals(ChannelValue.AD84)) mArmani = true;
                        }
                        else mChannelValue = null;
                    }
                    else mChannelValue = null;
                }
                else mChannelValue = null;
            }
            if (params.containsKey("res_id")) {
                resId = (Integer) params.get("res_id");
                if(resId == 39167)
                {
                    mShareText = "#NOTINNOCENT  LOOK，YSL夹心唇膏全新上市，肆意展现叛逆本能@YSL圣罗兰美妆";
                    mShareTitle = "YSL夹心唇膏#NOTINNOCENT LOOK";
                    mShareDescription = "YSL夹心唇膏全新上市，肆意展现叛逆本能";
                    mShareFriendsTitle = "YSL夹心唇膏#NOTINNOCENT LOOK";
                    mYSL = true;
                }
            }
            if (params.containsKey("res_tj_id")) {
                resTjId = (String) params.get("res_tj_id");
            }
            if (params.containsKey("thirdParty"))
            {
                m_thirdParty = (Boolean) params.get("thirdParty");
            }
        }

        init();

        if (type == 0) {
            if (bmp != null) {
                setPicture(bmp);
            }
        } else if (type == 1) {
            if (m_videoFrame != null && m_videoMgr != null)
            {
                m_videoFrame.setVideoDuration(m_videoMgr.getRecordDuration());
            }
            m_videoSaving = false;
            if (mPageHandler != null) {
                final String finalMp4Path = mp4Path;
                final VideoMgr finalVideoMgr = m_videoMgr;
                mPageHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (finalVideoMgr != null
                                && finalVideoMgr.getVideoList() != null
                                && finalVideoMgr.getVideoNum() > 0)
                        {
                            ArrayList<VideoMgr.SubVideo> videoList = finalVideoMgr.getVideoList();
                            String[] videoPath = new String[videoList.size()];
                            for (int i = 0; i < videoPath.length; i++)
                            {
                                videoPath[i] = videoList.get(i).mPath;
                            }
                            setVideoCachePath(videoPath);
                        }
                        else if (!TextUtils.isEmpty(finalMp4Path))
                        {
                            setVideoCachePath(finalMp4Path);
                        }
                    }
                }, 100);
            }
        }
    }

    private void init() {
        LinearLayout.LayoutParams lParams;
        FrameLayout.LayoutParams rParams;

        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.gravity = Gravity.LEFT | Gravity.TOP;
        m_progressFrame = new FrameLayout(getContext());
        addView(m_progressFrame, rParams);
        m_progressFrame.setVisibility(View.GONE);

        m_videoFrame = new VideoPreviewPage(getContext());
        m_videoFrame.setListener(mPreviewButtonListener);
        m_videoFrame.setBackgroundColor(Color.WHITE);
        m_videoFrame.setVisibility(View.GONE);
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        addView(m_videoFrame, rParams);

        m_shareFrame = new FrameLayout(getContext());
        m_shareFrame.setOnClickListener(mClickListener);
        m_shareFrame.setVisibility(View.GONE);
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        addView(m_shareFrame, rParams);

//        m_saveFrame = new FrameLayout(getContext());
//        m_saveFrame.setVisibility(View.GONE);
//        m_saveFrame.setOnClickListener(mClickListener);
//        addView(m_saveFrame, rParams);

        m_progress = new RoundProgressBar(getContext());
        m_progress.setMax(100);
        m_progress.setProgress(0);
        rParams = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(200), ShareData.PxToDpi_xhdpi(200));
        rParams.gravity = Gravity.CENTER;
        rParams.bottomMargin = ShareData.PxToDpi_xhdpi(78);
        m_progressFrame.addView(m_progress, rParams);

        m_shareBackground = new ImageView(getContext());
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.gravity = Gravity.TOP | Gravity.LEFT;
        m_shareFrame.addView(m_shareBackground, rParams);

        LinearLayout mainFrame = new LinearLayout(getContext());
        mainFrame.setOrientation(LinearLayout.VERTICAL);
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.gravity = Gravity.TOP | Gravity.LEFT;
        m_shareFrame.addView(mainFrame, rParams);

        FrameLayout topFrame = new FrameLayout(getContext());
        lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(825));
        lParams.gravity = Gravity.TOP | Gravity.LEFT;
        lParams.weight = 0;
        mainFrame.addView(topFrame, lParams);

        FrameLayout bottomFrame = new FrameLayout(getContext());
        lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lParams.gravity = Gravity.TOP | Gravity.LEFT;
        lParams.weight = 1;
        mainFrame.addView(bottomFrame, lParams);

        m_topBackground = new View(getContext());
        m_topBackground.setBackgroundColor(Color.WHITE);
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.gravity = Gravity.TOP | Gravity.LEFT;
        topFrame.addView(m_topBackground, rParams);

        m_topFrame = new FrameLayout(getContext());
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.gravity = Gravity.TOP | Gravity.LEFT;
        topFrame.addView(m_topFrame, rParams);
        {
            m_homeBtn = new ImageView(getContext());
            m_homeBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            m_homeBtn.setImageResource(R.drawable.share_top_home_normal);
            rParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            rParams.gravity = Gravity.RIGHT | Gravity.TOP;
            rParams.rightMargin = ShareData.PxToDpi_xhdpi(9);
            rParams.topMargin = ShareData.PxToDpi_xhdpi(4);
            m_topFrame.addView(m_homeBtn, rParams);
            ImageUtils.AddSkin(getContext(), m_homeBtn);
            m_homeBtn.setOnTouchListener(mBtnListener);

            m_backBtn = new ImageView(getContext());
            m_backBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            m_backBtn.setImageResource(R.drawable.framework_back_btn);
            rParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            rParams.gravity = Gravity.LEFT | Gravity.TOP;
            rParams.topMargin = ShareData.PxToDpi_xhdpi(5);
            rParams.leftMargin = ShareData.PxToDpi_xhdpi(2);
            m_topFrame.addView(m_backBtn, rParams);
            ImageUtils.AddSkin(getContext(), m_backBtn);
            m_backBtn.setOnTouchListener(mBtnListener);

            TextView save_text = new TextView(getContext());
            save_text.setText(getContext().getResources().getString(R.string.share_ui_top_title));
            save_text.setTextColor(0xff333333);
            save_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            rParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            rParams.topMargin = ShareData.PxToDpi_xhdpi(25);
            m_topFrame.addView(save_text, rParams);

            m_imageFrame = new LinearLayout(getContext());
            m_imageFrame.setOrientation(LinearLayout.HORIZONTAL);
            rParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            rParams.topMargin = ShareData.PxToDpi_xhdpi(197);
            m_topFrame.addView(m_imageFrame, rParams);

            mImageHolder = new ImageView(getContext());
            lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lParams.gravity = Gravity.TOP | Gravity.LEFT;
            mImageHolder.setMinimumWidth(ShareData.PxToDpi_xhdpi(150));
            mImageHolder.setMinimumHeight(ShareData.PxToDpi_xhdpi(150));
            m_imageFrame.addView(mImageHolder, lParams);
            mImageHolder.setOnClickListener(mClickListener);
        }

        m_bottomFrame = new FrameLayout(getContext());
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(206));
        rParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        m_topFrame.addView(m_bottomFrame, rParams);
        {
            LinearLayout share_text = new LinearLayout(getContext());
            share_text.setOrientation(LinearLayout.HORIZONTAL);
            rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            rParams.gravity = Gravity.TOP | Gravity.LEFT;
            rParams.leftMargin = ShareData.PxToDpi_xhdpi(34);
            m_bottomFrame.addView(share_text, rParams);
            {
                TextView text = new TextView(getContext());
                text.setText(getContext().getResources().getString(R.string.share_ui_bottom_title));
                text.setTextColor(0x99000000);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                share_text.addView(text, lParams);

                ImageView arrow = new ImageView(getContext());
                arrow.setImageResource(R.drawable.share_text_arrow);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(10);
                share_text.addView(arrow, lParams);

                ImageView line = new ImageView(getContext());
                line.setBackgroundColor(0x19000000);
//                lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(524), 1);
                lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(310), 1);
                lParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(6);
                share_text.addView(line, lParams);
            }

            m_weiboScroll = new HorizontalScrollView(getContext());
            m_weiboScroll.setHorizontalScrollBarEnabled(false);
            rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            rParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
            rParams.bottomMargin = ShareData.PxToDpi_xhdpi(40);
            m_bottomFrame.addView(m_weiboScroll, rParams);
            {
                m_weiboFrame = new LinearLayout(getContext());
                m_weiboFrame.setOrientation(LinearLayout.HORIZONTAL);
                rParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                rParams.gravity = Gravity.LEFT | Gravity.TOP;
                m_weiboScroll.addView(m_weiboFrame, rParams);

                //绑定微信朋友圈
                mIconWXFriends = new ImageView(getContext());
                mIconWXFriends.setImageResource(R.drawable.share_weibo_wechat_friend_normal);
//					mIconWXFriends.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_wechat_friend_normal, R.drawable.share_weibo_wechat_friend_press));
                mIconWXFriends.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconWXFriends.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(33);
                m_weiboFrame.addView(mIconWXFriends, lParams);

                //绑定微信
                mIconWeiXin = new ImageView(getContext());
                mIconWeiXin.setImageResource(R.drawable.share_weibo_wechat_normal);
//					mIconWeiXin.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_wechat_normal, R.drawable.share_weibo_wechat_press));
                mIconWeiXin.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconWeiXin.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(35);
                m_weiboFrame.addView(mIconWeiXin, lParams);

                //绑定Sina
                mIconSina = new ImageView(getContext());
                mIconSina.setImageResource(R.drawable.share_weibo_sina_normal);
//					mIconSina.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_sina_normal, R.drawable.share_weibo_sina_press));
                mIconSina.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconSina.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(35);
                m_weiboFrame.addView(mIconSina, lParams);

                //绑定QQ空间
                mIconQzone = new ImageView(getContext());
                mIconQzone.setImageResource(R.drawable.share_weibo_qzone_normal);
//					mIconQzone.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_qzone_normal, R.drawable.share_weibo_qzone_press));
                mIconQzone.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconQzone.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(35);
                m_weiboFrame.addView(mIconQzone, lParams);

                //绑定QQ
                mIconQQ = new ImageView(getContext());
                mIconQQ.setImageResource(R.drawable.share_weibo_qq_normal);
//					mIconQQ.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_qq_normal, R.drawable.share_weibo_qq_press));
                mIconQQ.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconQQ.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(35);
                m_weiboFrame.addView(mIconQQ, lParams);

                //绑定FaceBook
                mIconFaceBook = new ImageView(getContext());
                mIconFaceBook.setImageResource(R.drawable.share_weibo_facebook_normal);
//					mIconFaceBook.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_poco_normal, R.drawable.share_weibo_poco_press));
                mIconFaceBook.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconFaceBook.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(35);
                m_weiboFrame.addView(mIconFaceBook, lParams);

                //绑定Instagram
                mIconInstagram = new ImageView(getContext());
                mIconInstagram.setImageResource(R.drawable.share_weibo_instagarm_normal);
//					mIconInstagram.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_poco_normal, R.drawable.share_weibo_poco_press));
                mIconInstagram.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconInstagram.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(35);
                m_weiboFrame.addView(mIconInstagram, lParams);

                //绑定Twitter
                mIconTwitter = new ImageView(getContext());
                mIconTwitter.setImageResource(R.drawable.share_weibo_twitter_normal);
//					mIconTwitter.setImageDrawable(CommonUtils.CreateXHDpiBtnSelector((Activity)getContext(), R.drawable.share_weibo_poco_normal, R.drawable.share_weibo_poco_press));
                mIconTwitter.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mIconTwitter.setOnClickListener(mClickListener);
                lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lParams.gravity = Gravity.TOP | Gravity.LEFT;
                lParams.leftMargin = ShareData.PxToDpi_xhdpi(35);
                lParams.rightMargin = ShareData.PxToDpi_xhdpi(36);
                m_weiboFrame.addView(mIconTwitter, lParams);
            }
//            m_weiboScroll.setOnTouchListener(new OnTouchListener()
//            {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent)
//                {
//                    removeTogetherTip();
//                    return false;
//                }
//            });
        }

        if(mArmani) mAdvBar = new AdvBannerViewPager(getContext(), mPageSite.m_cmdProc, R.drawable.share_advertising_banner_ad84, "http://cav.adnonstop.com/cav/f53576787a/0068903162/?url=http://www.giorgioarmanibeauty.cn/landing-pages/170815cushion.html");
        else mAdvBar = new AdvBannerViewPager(getContext(), mPageSite.m_cmdProc, AdvBannerViewPager.PAGE_STICKER);
        rParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rParams.gravity = Gravity.CENTER;
        bottomFrame.addView(mAdvBar, rParams);
    }

    private void addButton()
    {
        if(mArmani)
        {
            ((LinearLayout.LayoutParams)mImageHolder.getLayoutParams()).gravity = Gravity.CENTER;
            return;
        }

        LinearLayout.LayoutParams ll;

        m_buttonFrame = new LinearLayout(getContext());
        m_buttonFrame.setOrientation(LinearLayout.VERTICAL);
        ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        ll.leftMargin = ShareData.PxToDpi_xhdpi(26);
        m_imageFrame.addView(m_buttonFrame, ll);

        if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
        {
//            m_communityBtn = new ShareButton(getContext());
//            m_communityBtn.init(R.drawable.share_button_community_normal, getContext().getResources().getString(R.string.share_icon_share_community), new OnAnimationClickListener()
//            {
//                @Override
//                public void onAnimationClick(View v) {
//                    if(m_videoSaving || onAnimation || m_onClose) return;
//                    if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
//                        if (!cn.poco.video.FileUtils.isFileExists(m_savePath))
//                        {
//                            Toast.makeText(getContext(), getResources().getString(R.string.preview_pic_delete), Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                    }
//                    saveFile();
//                    if (!checkVideoDuration(2000, 900000)) {
//                        Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_share_together_video_time_limit), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
//                    if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
//                    {
//                        sendToCircle(m_savePath, null);
//                        return;
//                    }
//                    if(m_savePath == null || !new File(m_savePath).exists())
//                    {
//                        Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    sendVideoToCircle(m_savePath, null);
//                }
//
//                @Override
//                public void onTouch(View v){}
//
//                @Override
//                public void onRelease(View v){}
//            });
//            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
//            ll.gravity = Gravity.TOP | Gravity.LEFT;
//            m_buttonFrame.addView(m_communityBtn, ll);

            m_cameraBtn = new ShareButton(getContext());
            m_cameraBtn.init(R.drawable.share_button_camera_normal, getContext().getResources().getString(R.string.share_icon_camera), new OnAnimationClickListener()
            {
                @Override
                public void onAnimationClick(View v)
                {
                    if(m_videoSaving || onAnimation || m_onClose) return;
                    //MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_继续拍照);
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_萌装照_预览_继续拍摄);
                    mPageSite.OnCamera(getContext());
                }

                @Override
                public void onTouch(View v){}

                @Override
                public void onRelease(View v){}
            });
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
            ll.gravity = Gravity.TOP | Gravity.LEFT;
//            ll.topMargin = ShareData.PxToDpi_xhdpi(36);
            m_buttonFrame.addView(m_cameraBtn, ll);

            m_beautifyBtn = new ShareButton(getContext());
            m_beautifyBtn.init(R.drawable.share_button_beautify_normal, getContext().getResources().getString(R.string.share_icon_beautify), new OnAnimationClickListener()
            {
                @Override
                public void onAnimationClick(View v)
                {
                    if(m_videoSaving || onAnimation || m_onClose) return;
                    if(m_savePath == null || !new File(m_savePath).exists())
                    {
                        Toast.makeText(getContext(), getResources().getString(R.string.preview_pic_delete), Toast.LENGTH_LONG).show();
                        return;
                    }
                    //MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_美颜美图);
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_萌装照_预览_美颜美图);
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("img", new RotationImg2[]{Utils.Path2ImgObj(m_savePath)});
                    mPageSite.OnBeautyFace(getContext(), params);
                }

                @Override
                public void onTouch(View v){}

                @Override
                public void onRelease(View v){}
            });
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
            ll.gravity = Gravity.TOP | Gravity.LEFT;
            ll.topMargin = ShareData.PxToDpi_xhdpi(36);
            m_buttonFrame.addView(m_beautifyBtn, ll);
        }
        else
        {
//            m_communityBtn = new ShareButton(getContext());
//            m_communityBtn.init(R.drawable.share_button_community_normal, getContext().getResources().getString(R.string.share_icon_share_community), new OnAnimationClickListener()
//            {
//                @Override
//                public void onAnimationClick(View v) {
//                    if(m_videoSaving || onAnimation || m_onClose) return;
//                    if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
//                        if (!cn.poco.video.FileUtils.isFileExists(m_savePath))
//                        {
//                            Toast.makeText(getContext(), getResources().getString(R.string.preview_pic_delete), Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                    }
//                    saveFile();
//                    if (!checkVideoDuration(2000, 900000)) {
//                        Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_share_together_video_time_limit), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
//                    if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
//                    {
//                        sendToCircle(m_savePath, null);
//                        return;
//                    }
//                    if(m_savePath == null || !new File(m_savePath).exists())
//                    {
//                        Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    sendVideoToCircle(m_savePath, null);
//                }
//
//                @Override
//                public void onTouch(View v){}
//
//                @Override
//                public void onRelease(View v){}
//            });
//            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
//            ll.gravity = Gravity.TOP | Gravity.LEFT;
//            m_buttonFrame.addView(m_communityBtn, ll);

            m_cameraBtn = new ShareButton(getContext());
            m_cameraBtn.init(R.drawable.share_button_camera_normal, getContext().getResources().getString(R.string.share_icon_camera), new OnAnimationClickListener()
            {
                @Override
                public void onAnimationClick(View v)
                {
                    if(m_videoSaving || onAnimation || m_onClose) return;
                    MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_继续拍照);
                    mPageSite.OnCamera(getContext());
                }

                @Override
                public void onTouch(View v){}

                @Override
                public void onRelease(View v){}
            });
            ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(304), ShareData.PxToDpi_xhdpi(76));
            ll.gravity = Gravity.TOP | Gravity.LEFT;
//            ll.topMargin = ShareData.PxToDpi_xhdpi(36);
            m_buttonFrame.addView(m_cameraBtn, ll);
        }
    }

//    private void togetherTipAnime()
//    {
//        if(mTogetherTip == null || m_onClose) return;
//        ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.406f, Animation.RELATIVE_TO_SELF, 1f);
//        scale.setDuration(300);
//        scale.setInterpolator(new OvershootInterpolator());
//        scale.setAnimationListener(new Animation.AnimationListener()
//        {
//            @Override
//            public void onAnimationStart(Animation animation){}
//
//            @Override
//            public void onAnimationEnd(Animation animation)
//            {
//                if(mTogetherTip != null) mTogetherTip.clearAnimation();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation){}
//        });
//        mTogetherTip.setVisibility(View.VISIBLE);
//        mTogetherTip.startAnimation(scale);
//    }

//    private void removeTogetherTip()
//    {
//        if(mTogetherTip == null) return;
//        TagMgr.SetTag(getContext(), Tags.SHARE_TOGETHER_TIP_FLAG);
//        mTogetherTip.clearAnimation();
//        m_topFrame.removeView(mTogetherTip);
//        mTogetherTip = null;
//        System.gc();
//    }

    /**
     * 传入背景图
     *
     * @param bmp
     */
    public void setBackground(Bitmap bmp) {
        if (bmp == null || bmp.isRecycled()) return;
        try {
            m_picPath = FileCacheMgr.GetLinePath() + ".img";
            CommonUtils.MakeParentFolder(m_picPath);
            Utils.SaveImg(getContext(), bmp, m_picPath, 100, false);//TempImageFile

            if (bmp.getWidth() != ShareData.m_screenWidth || bmp.getHeight() != ShareData.m_screenHeight) {
                bmp = MakeBmp.CreateBitmap(bmp, ShareData.m_screenWidth, ShareData.m_screenHeight, -1, 0, Bitmap.Config.ARGB_8888);
            }
            m_background = BeautifyResMgr2.MakeBkBmp(bmp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x24000000);
            BitmapDrawable bd = new BitmapDrawable(getResources(), m_background);
            m_shareBackground.setBackgroundDrawable(bd);
            m_progressFrame.setBackgroundDrawable(bd);

            mImageHolder.setImageBitmap(makeThumbPlayIcon(getContext(), bmp));
            addButton();

            Bitmap browse = MakeBmp.CreateFixBitmap(bmp, 150, 150, MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
            if (browse == null || browse.isRecycled()) return;
            int w = browse.getWidth();
            int icon_w = (int) ((float) w * 11f / 27f);
            Canvas canvas = new Canvas(browse);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Bitmap icon = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), R.drawable.light_app06_share_videoplay_icon, 0, -1, icon_w, icon_w), icon_w, icon_w, -1, 0, Bitmap.Config.ARGB_8888);
            canvas.drawBitmap(icon, (w - icon_w) / 2, (w - icon_w) / 2, null);

            m_shareBrowse = FileCacheMgr.GetLinePath() + ".img";
            CommonUtils.MakeParentFolder(m_shareBrowse);
            Utils.SaveImg(getContext(), bmp, m_shareBrowse, 100, false);

            icon.recycle();
            browse.recycle();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPicture(Bitmap bitmap) {
        m_videoSaving = false;
        m_progressFrame.setVisibility(View.GONE);
        if (m_videoFrame != null)
        {
            m_videoFrame.setVisibility(View.VISIBLE);
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            m_ShareBmp = bitmap;

            Bitmap tempBmp = MakeBmp.CreateBitmap(bitmap, ShareData.m_screenWidth, ShareData.m_screenHeight, -1, 0, Bitmap.Config.ARGB_8888);
            BitmapDrawable bd = new BitmapDrawable(getResources(), BeautifyResMgr2.MakeBkBmp(tempBmp, ShareData.m_screenWidth, ShareData.m_screenHeight, 0x24000000));
            m_shareBackground.setBackgroundDrawable(bd);

            Bitmap thumb = MakeBmp.CreateFixBitmap(bitmap, ShareData.PxToDpi_xhdpi(320), ShareData.PxToDpi_xhdpi(320), MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
            mImageHolder.setImageBitmap(ShareFrame.makeCircle(thumb));

            m_videoFrame.init(false, mPreviewButtonListener, mPreviewResRatio, bitmap);
            addButton();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_not_exist_pic), Toast.LENGTH_SHORT).show();
        }
        MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照预览页_主页面);
    }

    public static Bitmap makeThumbPlayIcon(Context context, Bitmap bmp)
    {
        if(context == null || bmp == null || bmp.isRecycled()) return null;
        Bitmap thumb = MakeBmp.CreateFixBitmap(bmp, ShareData.PxToDpi_xhdpi(320), ShareData.PxToDpi_xhdpi(320), MakeBmp.POS_CENTER, 0, Bitmap.Config.ARGB_8888);
        if(thumb == null || thumb.isRecycled()) return null;
        thumb = ShareFrame.makeCircle(thumb);
        Bitmap play_icon = MakeBmp.CreateFixBitmap(cn.poco.imagecore.Utils.DecodeImage(context, R.drawable.light_app06_thumb_play_icon, 0, -1, ShareData.PxToDpi_xhdpi(92), ShareData.PxToDpi_xhdpi(92)), ShareData.PxToDpi_xhdpi(92), ShareData.PxToDpi_xhdpi(92), MakeBmp.POS_START, 0, Bitmap.Config.ARGB_8888);
        if(play_icon == null || play_icon.isRecycled()) return thumb;
        Canvas canvas = new Canvas(thumb);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(play_icon, (thumb.getWidth() - play_icon.getWidth()) / 2, (thumb.getHeight() - play_icon.getHeight()) / 2, null);
        return thumb;
    }

    /**
     * 传入视频保存进度
     *
     * @param progress 当前进度，范围0~100
     */
    public void setVideoSaveProgress(int progress) {
        if (m_progressFrame.getVisibility() != View.VISIBLE) {
            return;
        }
        m_progress.setProgress(progress);
    }

    /**
     * 传入视频临时文件路径
     *
     * @param path
     */
    public void setVideoCachePath(String path) {
        m_savePath = path;
        if (m_videoFrame != null)
        {
            m_videoSaving = false;
            if (m_savePath != null && m_savePath.length() > 0 && new File(m_savePath).exists())
            {
                AudioControlUtils.pauseOtherMusic(getContext());//暂停后台音乐
                if (m_progressFrame != null) m_progressFrame.setVisibility(View.GONE);
                m_videoFrame.setPageShow(m_showVideoFrame = true);
                m_videoFrame.setVisibility(View.VISIBLE);
                m_videoFrame.setThirdParty(m_thirdParty);
                m_videoFrame.setOrientation(mOrientation);
                //2017010 阿玛尼商业隐藏btn
                if (!TextUtils.isEmpty(mChannelValue) && ChannelValue.AD84.equals(mChannelValue))
                {
                    m_videoFrame.setChannelValue(mChannelValue);
                    m_videoFrame.setHideBgMusicBtn(true);
                    m_videoFrame.setHideVideoProgress(true);
                    m_videoFrame.setFullVideoScreen(true);
                }
                m_videoFrame.init(true, mPreviewButtonListener, mPreviewResRatio, m_savePath);
            }
            else
            {
                Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_not_exist_video), Toast.LENGTH_LONG).show();
            }
        }
        MyBeautyStat.onPageStartByRes(R.string.拍照_视频预览页_主页面);
    }

    /**
     * 传入视频临时文件路径
     *
     * @param path
     */
    public void setVideoCachePath(String... path)
    {
        if (m_videoFrame != null)
        {
            m_videoSaving = false;
            if (path != null && path.length > 0)
            {
                AudioControlUtils.pauseOtherMusic(getContext());//暂停后台音乐
                if (m_progressFrame != null) m_progressFrame.setVisibility(View.GONE);
                m_videoFrame.setPageShow(m_showVideoFrame = true);
                m_videoFrame.setVisibility(View.VISIBLE);
                m_videoFrame.setThirdParty(m_thirdParty);
                m_videoFrame.setOrientation(mOrientation);
                m_videoFrame.setRecordAudioEnable(mVideoRecordAudioEnable);
                Object[] previews = new Object[path.length];
                System.arraycopy(path, 0, previews, 0, path.length);
                //2017010 阿玛尼商业隐藏btn
                if (!TextUtils.isEmpty(mChannelValue) && ChannelValue.AD84.equals(mChannelValue))
                {
                    m_videoFrame.setChannelValue(mChannelValue);
                    m_videoFrame.setHideBgMusicBtn(true);
                    m_videoFrame.setHideVideoProgress(true);
                    m_videoFrame.setFullVideoScreen(true);
                }
                m_videoFrame.init(true, mPreviewButtonListener, mPreviewResRatio, previews);
            }
            else
            {
                Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_not_exist_video), Toast.LENGTH_LONG).show();
            }
        }
        MyBeautyStat.onPageStartByRes(R.string.拍照_视频预览页_主页面);
    }

    /**
     * 背景图
     *
     * @param mp4Path
     * @return
     */
    private Bitmap getBackGroundThumb(String mp4Path)
    {
        Bitmap out = null;
        if (FileUtil.isFileExists(mp4Path))
        {
            try
            {
                out = NativeUtils.getNextFrameBitmapFromFile(mp4Path, 0);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            finally
            {
                NativeUtils.cleanVideoGroupByIndex(0);
            }
            if (mOrientation > 0 && out != null && !out.isRecycled()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(mOrientation);
                Bitmap temp = Bitmap.createBitmap(out, 0, 0, out.getWidth(), out.getHeight(), matrix, true);
                if (temp != out) out.recycle();
                out = temp;
                temp = null;
            } else if (out == null || out.isRecycled()){
                out = CommonUtils.GetScreenBmp((Activity) getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight);
            }
        }
        return out;
    }

    public Bitmap getGalssThumb(Bitmap in, int fakeColor)
    {
        if (in != null && !in.isRecycled())
        {
            try
            {
                in = filter.fakeGlassBeauty(in, fakeColor);
            }
            catch (Throwable t)
            {
            }
        }
        return in;
    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(m_videoSaving || onAnimation || m_onClose) return;

//            if (view == m_back) {
//                AudioControlUtils.setCanResumeMusic(true);
//                AudioControlUtils.resumeOtherMusic(getContext());
//
//                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_返回);
//                if (mPageSite != null) {
//                    mPageSite.onBack();
//                }
//            }
//            else if (view == m_share) {
//                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_分享);
//                /*if(resId == 710 || resId == 711 || resId == 712 || resId == 713)
//                {
//                    final BusinessRes res = BusinessGroupResMgr.GetOneLocalBusinessRes2(ChannelValue.AD62);
//                    if(res != null && res.m_inputInfoArr.size() > 0 && System.currentTimeMillis() < res.m_endTime)
//                    {
//                        YongHeDaWangDialog dlg = new YongHeDaWangDialog(getContext(), R.style.dialog);
//                        dlg.setCancelable(false);
//                        dlg.show();
//                        dlg.setChannelValue(res.m_channelValue);
//                        dlg.setOkListener(new ActSignUpDialog.OkListener()
//                        {
//                            @Override
//                            public void onOk(String postStr)
//                            {
//                                Utils.UrlTrigger(getContext(), res.m_submitTjUrl);
//                                mAdvPostStr = HomePageSite.makePostVar(getContext(), res.m_channelValue);
//                                if(postStr != null) mAdvPostStr += postStr;
//                                m_showVideoFrame = false;
//                                m_video.setPageShow(m_showVideoFrame);
//                                m_video.onPause();
//                                m_videoFrame.setVisibility(View.GONE);
//                                m_shareFrame.setVisibility(View.VISIBLE);
//                            }
//                        });
//                        return;
//                    }
//                }*/
//                saveFile();
//                m_showVideoFrame = false;
//                m_video.setPageShow(m_showVideoFrame);
//                m_video.onPause();
//                m_videoFrame.setVisibility(View.GONE);
//                m_shareFrame.setVisibility(View.VISIBLE);
//                UIanime(new Animation.AnimationListener()
//                {
//                    @Override
//                    public void onAnimationStart(Animation animation){}
//
//                    @Override
//                    public void onAnimationEnd(Animation animation)
//                    {
//                        m_topFrame.clearAnimation();
//                        togetherTipAnime();
//                        onAnimation = false;
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation){}
//                });
//            }
            if (view == mIconWXFriends) {
                saveFile();
                if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
                    sendPicToWeiXin(m_savePath, false);
                    return;
                }

                if(m_savePath == null || !new File(m_savePath).exists())
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                    return;
                }

                if(mChannelValue == null)
                {
                    showShareVideoDialog2(SharePage.WEIXIN, new ShareVideoDialogCallback()
                    {
                        @Override
                        public void shareVideoType(boolean uploadServer)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                            if (mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
                            if(mWeiXin.isWXAppInstalled()) mWeiXin.openWeiXinWithSDK();
                            else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_weixin_error_client_no_install), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                showShareVideoDialog(SharePage.WEIXIN, new ShareVideoDialogCallback()
                {
                    @Override
                    public void shareVideoType(boolean uploadServer)
                    {
                        if(uploadServer)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
                            if (m_shareUrl != null && m_shareUrl.length() > 0) {
                                sendToWeiXin(m_shareUrl, m_shareBrowse, false);
                                return;
                            }

                            uploadVedio(m_savePath, new UploadVideoCallback() {
                                @Override
                                public void uploadComplete(String url) {
                                    m_shareUrl = url;
                                    if(!sendToWeiXin(m_shareUrl, m_shareBrowse, false))
                                    {
                                        if(m_uploadComplete)
                                        {
                                            m_uploadComplete = false;
                                            m_videoSaving = false;
                                            m_shareFrame.setVisibility(View.VISIBLE);
                                            m_progressFrame.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void uploadFail() {
                                    Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                            if (mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
                            if(mWeiXin.isWXAppInstalled()) mWeiXin.openWeiXinWithSDK();
                            else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_weixin_error_client_no_install), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if (view == mIconWeiXin) {
                saveFile();
                if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
                    sendPicToWeiXin(m_savePath, true);
                    return;
                }

                if (m_shareUrl != null && m_shareUrl.length() > 0) {
                    sendToWeiXin(m_shareUrl, m_shareBrowse, true);
                    return;
                }

                if(m_savePath == null || !new File(m_savePath).exists())
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                    return;
                }

                if(mChannelValue == null)
                {
                    showShareVideoDialog2(SharePage.WEIXIN, new ShareVideoDialogCallback()
                    {
                        @Override
                        public void shareVideoType(boolean uploadServer)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                            if (mWeiXin == null) mWeiXin = new WeiXinBlog(getContext());
                            if(mWeiXin.isWXAppInstalled()) mWeiXin.openWeiXinWithSDK();
                            else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_weixin_error_client_no_install), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                uploadVedio(m_savePath, new UploadVideoCallback() {
                    @Override
                    public void uploadComplete(String url) {
                        m_shareUrl = url;
                        if(!sendToWeiXin(m_shareUrl, m_shareBrowse, true))
                        {
                            if(m_uploadComplete)
                            {
                                m_uploadComplete = false;
                                m_videoSaving = false;
                                m_shareFrame.setVisibility(View.VISIBLE);
                                m_progressFrame.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void uploadFail() {
                        Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
                    }
                });
            } else if (view == mIconSina) {
                saveFile();
                if(mSina == null) {
                    mSina = new SinaBlog(getContext());
                }
                if(!SettingPage.checkSinaBindingStatus(getContext()))
                {
                    mSina.bindSinaWithSSO(new SinaBlog.BindSinaCallback() {
                        @Override
                        public void success(final String accessToken, String expiresIn, String uid, String userName, String nickName) {
                            //获取是绑定的时间:
                            String saveTime = String.valueOf(System.currentTimeMillis() / 1000);

                            GetSettingInfo(getContext()).SetSinaUid(uid);
                            GetSettingInfo(getContext()).SetSinaAccessToken(accessToken);
                            GetSettingInfo(getContext()).SetSinaSaveTime(saveTime);
                            GetSettingInfo(getContext()).SetSinaUserName(userName);
                            GetSettingInfo(getContext()).SetSinaUserNick(nickName);
                            GetSettingInfo(getContext()).SetSinaExpiresIn(expiresIn);

                            if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
                                sendPicToSina(m_savePath);
                                return;
                            }

                            if(m_savePath == null || !new File(m_savePath).exists())
                            {
                                Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                                return;
                            }

                            if(mChannelValue == null)
                            {
                                showShareVideoDialog2(SharePage.SINA, new ShareVideoDialogCallback()
                                {
                                    @Override
                                    public void shareVideoType(boolean uploadServer)
                                    {
                                        MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                                        if (mSina == null) mSina = new SinaBlog(getContext());
                                        if(mSina.checkSinaClientInstall()) mSina.openSina();
                                        else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                            showShareVideoDialog(SharePage.SINA, new ShareVideoDialogCallback()
                            {
                                @Override
                                public void shareVideoType(boolean uploadServer)
                                {
                                    if(uploadServer)
                                    {
                                        MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
                                        if (m_shareUrl != null && m_shareUrl.length() > 0) {
                                            sendToSina(mShareText + m_shareUrl);
                                            return;
                                        }
                                        uploadVedio(m_savePath, new UploadVideoCallback() {
                                            @Override
                                            public void uploadComplete(String url) {
                                                m_shareUrl = url;
                                                if(!sendToSina(mShareText + m_shareUrl))
                                                {
                                                    if(m_uploadComplete)
                                                    {
                                                        m_uploadComplete = false;
                                                        m_videoSaving = false;
                                                        m_shareFrame.setVisibility(View.VISIBLE);
                                                        m_progressFrame.setVisibility(View.GONE);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void uploadFail() {
                                                Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                                        if (mSina == null) mSina = new SinaBlog(getContext());
                                        if(mSina.checkSinaClientInstall()) mSina.openSina();
                                        else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }

                        @Override
                        public void fail()
                        {
                            switch(mSina.LAST_ERROR)
                            {
                                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
                                    break;

                                default:
                                    SharePage.msgBox(getContext(), getResources().getString(R.string.share_sina_bind_fail));
                                    break;
                            }
                        }
                    });
                } else {
                    if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
                        sendPicToSina(m_savePath);
                        return;
                    }

                    if(m_savePath == null || !new File(m_savePath).exists())
                    {
                        Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(mChannelValue == null)
                    {
                        showShareVideoDialog2(SharePage.SINA, new ShareVideoDialogCallback()
                        {
                            @Override
                            public void shareVideoType(boolean uploadServer)
                            {
                                MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                                if (mSina == null) mSina = new SinaBlog(getContext());
                                if(mSina.checkSinaClientInstall()) mSina.openSina();
                                else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                    showShareVideoDialog(SharePage.SINA, new ShareVideoDialogCallback()
                    {
                        @Override
                        public void shareVideoType(boolean uploadServer)
                        {
                            if(uploadServer)
                            {
                                MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
                                if (m_shareUrl != null && m_shareUrl.length() > 0) {
                                    sendToSina(mShareText + m_shareUrl);
                                    return;
                                }
                                uploadVedio(m_savePath, new UploadVideoCallback() {
                                    @Override
                                    public void uploadComplete(String url) {
                                        m_shareUrl = url;
                                        if(!sendToSina(mShareText + m_shareUrl))
                                        {
                                            if(m_uploadComplete)
                                            {
                                                m_uploadComplete = false;
                                                m_videoSaving = false;
                                                m_shareFrame.setVisibility(View.VISIBLE);
                                                m_progressFrame.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                    @Override
                                    public void uploadFail() {
                                        Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else
                            {
                                MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                                if (mSina == null) mSina = new SinaBlog(getContext());
                                if(mSina.checkSinaClientInstall()) mSina.openSina();
                                else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            } else if (view == mIconQzone) {
                saveFile();
                if (mQzone == null) {
                    mQzone = new QzoneBlog2(getContext());
                }

                if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
                    sendPicToQzone(m_savePath);
                    return;
                }

                if(m_savePath == null || !new File(m_savePath).exists())
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mChannelValue == null) {
                    showShareVideoDialog2(SharePage.QZONE, new ShareVideoDialogCallback()
                    {
                        @Override
                        public void shareVideoType(boolean uploadServer)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                            if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                            if(mQzone.checkQQClientInstall()) mQzone.openQQ();
                            else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                showShareVideoDialog(SharePage.QZONE, new ShareVideoDialogCallback()
                {
                    @Override
                    public void shareVideoType(boolean uploadServer)
                    {
                        if(uploadServer)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
                            if(m_shareUrl != null && m_shareUrl.length() > 0) {
                                sendToQzone(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse));
                                return;
                            }

                            uploadVedio(m_savePath, new UploadVideoCallback() {
                                @Override
                                public void uploadComplete(String url) {
                                    m_shareUrl = url;
                                    if(!sendToQzone(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse)))
                                    {
                                        if(m_uploadComplete)
                                        {
                                            m_uploadComplete = false;
                                            m_videoSaving = false;
                                            m_shareFrame.setVisibility(View.VISIBLE);
                                            m_progressFrame.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void uploadFail() {
                                    Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                            if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                            if(mQzone.checkQQClientInstall()) mQzone.openQQ();
                            else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else if(view == mIconQQ)
            {
                saveFile();
                if (mQzone == null) {
                    mQzone = new QzoneBlog2(getContext());
                }

                if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
                    sendPicToQQ(m_savePath);
                    return;
                }

                if(m_savePath == null || !new File(m_savePath).exists())
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                    return;
                }

                if(mChannelValue == null)
                {
                    showShareVideoDialog2(SharePage.QZONE, new ShareVideoDialogCallback()
                    {
                        @Override
                        public void shareVideoType(boolean uploadServer)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                            if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                            if(mQzone.checkQQClientInstall()) mQzone.openQQ();
                            else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                showShareVideoDialog(SharePage.QZONE, new ShareVideoDialogCallback()
                {
                    @Override
                    public void shareVideoType(boolean uploadServer)
                    {
                        if(uploadServer)
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_开始上传);
                            if(m_shareUrl != null && m_shareUrl.length() > 0) {
                                sendToQQ(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse));
                                return;
                            }

                            uploadVedio(m_savePath, new UploadVideoCallback() {
                                @Override
                                public void uploadComplete(String url) {
                                    m_shareUrl = url;
                                    if(!sendToQQ(m_shareUrl, makeQzoneShareThumbPath(m_shareBrowse)))
                                    {
                                        if(m_uploadComplete)
                                        {
                                            m_uploadComplete = false;
                                            m_videoSaving = false;
                                            m_shareFrame.setVisibility(View.VISIBLE);
                                            m_progressFrame.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void uploadFail() {
                                    Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_video_upload_fail), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else
                        {
                            MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_打开XX);
                            if(mQzone == null) mQzone = new QzoneBlog2(getContext());
                            if(mQzone.checkQQClientInstall()) mQzone.openQQ();
                            else Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
//            else if(view == mIconTogether)
//            {
//                saveFile();
//                if (!checkVideoDuration(2000, 900000)) {
//                    Toast.makeText(getContext(), getResources().getString(R.string.lightapp06_share_together_video_time_limit), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
//                {
//                    sendToCircle(m_savePath, null);
//                    return;
//                }
//                sendVideoToCircle(m_savePath, null);
//            }
            else if(view == mIconFaceBook)
            {
                saveFile();
                if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                {
                    sendPicToFacebook(m_savePath);
                    return;
                }
                if(m_savePath == null || !new File(m_savePath).exists())
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                    return;
                }
                sendVideoToFacebook(m_savePath);
            }
            else if(view == mIconTwitter)
            {
                saveFile();
                if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                {
                    sendPicToTwitter(m_savePath, null);
                    return;
                }
                if(m_savePath == null || !new File(m_savePath).exists())
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                    return;
                }
                sendVideoToTwitter(m_savePath, null);
            }
            else if(view == mIconInstagram)
            {
                saveFile();
                if(mYSL) Utils.UrlTrigger(getContext(), "http://cav.adnonstop.com/cav/fe0a01a3d9/0071803177/?url=https://a-m-s-ios.poco.cn/images/blank.gif");
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                {
                    sendPicToInstagram(m_savePath);
                    return;
                }
                if(m_savePath == null || !new File(m_savePath).exists())
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.preview_video_delete), Toast.LENGTH_LONG).show();
                    return;
                }
                sendVideoToInstagram(m_savePath);
            }
            else if(view == mImageHolder)
            {
                onPause();
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                {
                    //MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_查看预览图);
                    onPreview(getContext(), m_savePath, false);
                    return;
                }
                MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_查看预览图);
                onPreview(getContext(), m_savePath, true);
            }
//            else if (view == m_back2) {
//                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_分享_返回);
//                m_shareFrame.setVisibility(View.GONE);
//                m_videoFrame.setVisibility(View.VISIBLE);
//                m_showVideoFrame = true;
//                m_video.setPageShow(m_showVideoFrame);
//                m_video.onResume();
//            }
        }
    };


    private void onPreview(Context context, String path, boolean isVideo)
    {
        if (mPageSite != null)
        {
            if (!cn.poco.video.FileUtils.isFileExists(path)) {
                Toast.makeText(context, isVideo ? R.string.preview_video_delete : R.string.preview_pic_delete, Toast.LENGTH_SHORT).show();
                return;
            }
            mPageSite.OnPreview(context, path, isVideo);
        }
    }

    protected VideoPreviewPage.OnClickListener mPreviewButtonListener = new VideoPreviewPage.OnClickListener()
    {
        @Override
        public void click(int id, boolean isVideo)
        {
            if(onAnimation || m_onClose) return;

            switch(id)
            {
                case VideoPreviewPage.ID_BACK:
                    AudioControlUtils.setCanResumeMusic(true);
                    AudioControlUtils.resumeOtherMusic(getContext());

                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_返回);

                    if (mPageSite != null) {
                        if (m_thirdParty)// 第三方
                        {
                            FileUtil.deleteSDFile(m_savePath);
                            mPageSite.OnThirdPartyBack(getContext(), true);
                            break;
                        }
                        mPageSite.onBack(getContext(), getVideoData());
                    }
                    break;

                case VideoPreviewPage.ID_SHARE:
                    BrightnessUtils instance = BrightnessUtils.getInstance();
                    if (instance != null)
                    {
                        instance.setContext(getContext()).unregisterBrightnessObserver();
                        instance.resetToDefault();
                        instance.clearAll();
                    }
                    TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_分享);
                    if (m_videoFrame == null)
                    {
                        break;
                    }
                    String video_path = m_videoFrame.getVideoOutputPath();
                    // 第三方
                    if (m_thirdParty)
                    {
                        if (cn.poco.video.FileUtils.isFileExists(video_path))
                        {
                            FileUtil.deleteSDFile(m_savePath);
                            mPageSite.OnThirdPartySave(getContext(), video_path, true);
                        }
                        else
                        {
                            mPageSite.OnThirdPartySave(getContext(), m_savePath, true);
                        }
                        break;
                    }
                    //合成后直接跳转到社区
                    if (mIsSaveJump)
                    {
                        if (cn.poco.video.FileUtils.isFileExists(video_path))
                        {
                            FileUtil.deleteSDFile(m_savePath);
                            mPageSite.onSaveToCommunity(getContext(), video_path, LightApp06Page.makeCircleExtra(resId, resTjId));
                        }
                        else
                        {
                            mPageSite.onSaveToCommunity(getContext(), m_savePath, LightApp06Page.makeCircleExtra(resId, resTjId));
                        }
                        break;
                    }
                    if(video_path != null)
                    {
                        m_videoSaved = false;
                        m_savePath = video_path;
                        m_shareUrl = null;
                    }

                    if (!isVideo && !cn.poco.video.FileUtils.isFileExists(m_savePath))
                    {
                        m_savePath = resavePicture();
                    }
                    saveFile();
                    m_showVideoFrame = false;
                    m_videoFrame.setPageShow(false);
                    //m_videoFrame.onPause();
                    m_videoFrame.setVisibility(View.GONE);
                    m_shareFrame.setVisibility(View.VISIBLE);
                    if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                    {
                        MyBeautyStat.onPageEndByRes(R.string.拍照_萌妆照预览页_主页面);
                        MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照分享页_主页面);
                    }
                    else
                    {
                        MyBeautyStat.onPageEndByRes(R.string.拍照_视频预览页_主页面);
                        MyBeautyStat.onPageStartByRes(R.string.拍照_视频分享页_主页面);
                    }

                    ShareFrame.UIanime(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation){
                            onAnimation = true;
                        }

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            onAnimation = false;
                            if(m_shareBackground != null) m_shareBackground.clearAnimation();
                            if(m_topFrame != null) m_topFrame.clearAnimation();
                            if(m_topBackground != null) m_topBackground.clearAnimation();
                            if(mAdvBar != null)
                            {
                                mAdvBar.clearAnimation();
                                if(mAdvBar.isFirstShow()) mAdvBar.autoPage();
                                else mAdvBar.onStart();
                            }
//                            togetherTipAnime();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation){}
                    });
                    break;
            }
        }

        @Override
        public void onReady()
        {
            handlerBackGroundThumbThread();
        }

        @Override
        public void onStartMix()
        {
            if (m_background == null)
            {
                setBackground(backGroundThumb);
            }
        }
    };

    private Bitmap backGroundThumb;
    private void handlerBackGroundThumbThread()
    {
        if (backGroundThumb != null) return;
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (m_videoMgr != null && m_videoMgr.getVideoNum() > 0)
                {
                    VideoMgr.SubVideo subVideo = m_videoMgr.getVideoList().get(0);
                    if (subVideo != null)
                    {
                        backGroundThumb = getBackGroundThumb(subVideo.mPath);
                        if (backGroundThumb != null)
                        {
                            final Bitmap dialogBk = getGalssThumb(backGroundThumb.copy(Bitmap.Config.ARGB_8888, true), 0x19000000);
                            if (dialogBk != null && !dialogBk.isRecycled())
                            {
                                LightApp06Page.this.post(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (m_videoFrame != null)
                                        {
                                            m_videoFrame.setProgressDlgBackground(dialogBk);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }

    protected OnAnimationClickListener mBtnListener = new OnAnimationClickListener()
    {
        @Override
        public void onAnimationClick(View v)
        {
            if(onAnimation || m_onClose) return;

            if (v == m_homeBtn)
            {
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled()) {
                    //MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_回到首页);
                }
                else MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_回到首页);
                mPageSite.OnHome(getContext());
            }
            else if (v == m_backBtn)
            {
                TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_分享_返回);
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                {
                    //MyBeautyStat.onClickByRes(R.string.拍照_萌妆照分享页_主页面_返回上一级);
                    MyBeautyStat.onPageEndByRes(R.string.拍照_萌妆照分享页_主页面);
                    MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照预览页_主页面);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_返回上一级);
                    MyBeautyStat.onPageEndByRes(R.string.拍照_视频分享页_主页面);
                    MyBeautyStat.onPageStartByRes(R.string.拍照_视频预览页_主页面);
                }
//                if(mTogetherTip != null) mTogetherTip.setVisibility(View.GONE);
//                removeTogetherTip();
                mAdvBar.onStop();
                if (m_videoFrame != null) m_videoFrame.setVisibility(View.VISIBLE);
                ShareFrame.UIanime2(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation){
                        onAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        onAnimation = false;
                        if(m_onClose) return;
                        if(m_shareBackground != null) m_shareBackground.clearAnimation();
                        if(m_topFrame != null) m_topFrame.clearAnimation();
                        if(m_topBackground != null) m_topBackground.clearAnimation();
                        if(mAdvBar != null) mAdvBar.clearAnimation();
                        if (m_shareFrame != null) m_shareFrame.setVisibility(View.GONE);
                        m_showVideoFrame = true;
                        if (m_videoFrame != null)
                        {
                            m_videoFrame.setPageShow(m_showVideoFrame);
                            m_videoFrame.onResume(false);
                            m_videoFrame.changeSystemUiVisibility(View.GONE);//沉浸式导航栏处理
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation){}
                });
            }
        }

        @Override
        public void onTouch(View v){}

        @Override
        public void onRelease(View v){}
    };

    private void saveFile() {
        if (m_ShareBmp != null && !m_ShareBmp.isRecycled()) {

            Bitmap waterMark = null;
            if (m_videoFrame != null) {//换水印
                WatermarkItem watermarkItem = m_videoFrame.getWaterMark();
                if (watermarkItem != null && watermarkItem.res != null && watermarkItem.mID != mWaterMarkResId) {
                    mWaterMarkResId = watermarkItem.mID;
                    if (watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                    {
                        waterMark = MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
                    }
                    m_bmpSaved = false;
                }
            }

            if (m_bmpSaved) return;
            m_savePath = null;

            boolean addDate = SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState();
            Bitmap saveBitmap = null;
            if (addDate || (waterMark != null && !waterMark.isRecycled())) {
                saveBitmap = m_ShareBmp.copy(Bitmap.Config.ARGB_8888, true);
            }
            if (waterMark != null && !waterMark.isRecycled()) {
                PhotoMark.drawWaterMarkLeft(saveBitmap, waterMark, addDate);
            }
            if(addDate) {
                PhotoMark.drawDataLeft(saveBitmap);
            }
            if (saveBitmap == null || saveBitmap.isRecycled()) {
                saveBitmap = m_ShareBmp;
            }

            m_savePath = Utils.SaveImg(getContext(), saveBitmap, Utils.MakeSavePhotoPath(getContext(), (float) saveBitmap.getWidth() / saveBitmap.getHeight()), 100, true);
            if (m_savePath == null) return;
            m_bmpSaved = true;
            return;
        }
        if (m_videoSaved) return;
        String videoPath = saveVideo(m_savePath);
        if (videoPath != null && videoPath.length() > 0) {
            m_savePath = null;
            m_savePath = videoPath;
            mVideoDuration = (long) VideoUtils.getDurationFromVideo(videoPath);
            m_videoSaved = true;
        }
    }

    private String resavePicture()
    {
        if (m_ShareBmp != null && !m_ShareBmp.isRecycled())
        {
            Bitmap waterMark = null;
            if (m_videoFrame != null) {//换水印
                WatermarkItem watermarkItem = m_videoFrame.getWaterMark();
                if (watermarkItem != null && watermarkItem.res != null) {
                     mWaterMarkResId = watermarkItem.mID;
                    if (watermarkItem.mID != WatermarkResMgr2.getInstance().GetNonWatermarkId(getContext()))
                    {
                        waterMark = MakeBmpV2.DecodeImage(getContext(), watermarkItem.res, 0, -1, -1, -1, Bitmap.Config.ARGB_8888);
                    }
                    m_bmpSaved = false;
                }
            }

            boolean addDate = SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState();
            Bitmap saveBitmap = null;
            if (addDate || (waterMark != null && !waterMark.isRecycled())) {
                saveBitmap = m_ShareBmp.copy(Bitmap.Config.ARGB_8888, true);
            }
            if (waterMark != null && !waterMark.isRecycled()) {
                PhotoMark.drawWaterMarkLeft(saveBitmap, waterMark, addDate);
            }
            if(addDate) {
                PhotoMark.drawDataLeft(saveBitmap);
            }
            if (saveBitmap == null || saveBitmap.isRecycled()) {
                saveBitmap = m_ShareBmp;
            }

            m_savePath = Utils.SaveImg(getContext(), saveBitmap, Utils.MakeSavePhotoPath(getContext(), (float) saveBitmap.getWidth() / saveBitmap.getHeight()), 100, true);
            if (m_savePath == null) return null;
            m_bmpSaved = true;
            return m_savePath;
        }
        return null;
    }

    private boolean checkVideoDuration(long min, long max) {
        if (m_videoSaved && (mVideoDuration < min || mVideoDuration > max)) {
            return false;
        }
        return true;
    }

    private String saveVideo(String cachePath) {
        if (cachePath == null || cachePath.length() <= 0) return null;

        File file = new File(cachePath);
        if (!file.exists()) return null;

        StringBuffer sb = new StringBuffer();
        String dirName = GetPhotoSavePath();
        File destDir = new File(dirName);
        if (!destDir.exists()) destDir.mkdirs();
        sb.append(dirName);
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String strDate = df.format(date);
        String strRand = Integer.toString((int) (Math.random() * 100));
        if (strRand.length() < 4) strRand = "0000".substring(strRand.length()) + strRand;
        sb.append(File.separator + strDate + strRand + ".mp4");
        try {
            File copyFile = new File(sb.toString());
            FileUtils.copyFile(file, copyFile);
            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + copyFile)));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String GetPhotoSavePath() {
        String out = null;
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (dcim != null) {
            out = dcim.getAbsolutePath() + File.separator + "Camera";
        } else {
            out = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM" + File.separator + "Camera";
        }
        //魅族的默认相册路径不同，原来的路径图库不显示
        String manufacturer = android.os.Build.MANUFACTURER;
        if (manufacturer != null) {
            manufacturer = manufacturer.toLowerCase(Locale.getDefault());
            if (manufacturer.contains("meizu")) {
                out = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Camera";
            }
        }
        CommonUtils.MakeFolder(out);
        return out;
    }

    private String makeQzoneShareThumbPath(String img_path) {
        if (img_path == null || img_path.length() <= 0) return null;

        File file = new File(img_path);
        if (!file.exists()) return null;

        StringBuffer sb = new StringBuffer();
        String dirName = GetPhotoSavePath();
        File destDir = new File(dirName);
        if (!destDir.exists()) destDir.mkdirs();
        sb.append(dirName);
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String strDate = df.format(date);
        String strRand = Integer.toString((int) (Math.random() * 100));
        if (strRand.length() < 4) strRand = "0000".substring(strRand.length()) + strRand;
        sb.append(File.separator + strDate + strRand + ".jpg");
        try {
            File copyFile = new File(sb.toString());
            FileUtils.copyFile(file, copyFile);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送到Sina微博;
     */
    private boolean sendToSina(final String url) {
        if (url == null || url.length() <= 0)
        {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_url), Toast.LENGTH_LONG).show();
            return false;
        }

        if(mSina == null) mSina = new SinaBlog(getContext());
        if (!mSina.checkSinaClientInstall()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
            return false;
        }
        mSina.SetAccessToken(GetSettingInfo(getContext()).GetSinaAccessToken());
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
                            TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_视频_分享到新浪, getResources().getString(TAG));
                            ShareTools.addIntegral(getContext());
                            SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG);
                            MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_视频分享页_主页面);
                            break;

                        case WBConstants.ErrorCode.ERR_CANCEL:
                            SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
                            break;

                        case WBConstants.ErrorCode.ERR_FAIL:
                        case SinaBlog.NO_RESPONSE:
                            SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
                            break;
                    }
                }
                else
                {
                    SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
                }
            }
        });

        Intent intent = new Intent(getContext(), SinaRequestActivity.class);
        intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT);
        intent.putExtra("content", url);
        ((Activity) getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
        return true;
    }

    private void sendPicToSina(String pic) {
        if (pic == null || pic.length() <= 0 || !new File(pic).exists())
        {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_error_image_is_null), Toast.LENGTH_LONG).show();
            return;
        }

        if (mSina == null) {
            mSina = new SinaBlog(getContext());
        }
        if (!mSina.checkSinaClientInstall()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_sina_error_clinet_no_install), Toast.LENGTH_LONG).show();
            return;
        }
        mSina.SetAccessToken(GetSettingInfo(getContext()).GetSinaAccessToken());
        mSina.setSendSinaResponse(new SinaBlog.SendSinaResponse() {
            @Override
            public void response(boolean send_success, int response_code) {
                if (send_success) {
                    switch (response_code) {
                        case WBConstants.ErrorCode.ERR_OK:
                            TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到新浪, getResources().getString(TAG));
                            ShareTools.addIntegral(getContext());
                            SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG);
                            MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微博, R.string.拍照_萌妆照分享页_主页面);
                            break;

                        case WBConstants.ErrorCode.ERR_CANCEL:
                            SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG);
                            break;

                        case WBConstants.ErrorCode.ERR_FAIL:
                        case SinaBlog.NO_RESPONSE:
                            SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
                            break;
                    }
                } else {
                    SharePage.showToastOnUIThread(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG);
                }
            }
        });

        Intent intent = new Intent(getContext(), SinaRequestActivity.class);
        intent.putExtra("type", SinaBlog.SEND_TYPE_TEXT_AND_PIC);
        intent.putExtra("pic", pic);
        if(resId == 710 || resId == 711 || resId == 712 || resId == 713) intent.putExtra("content", mShareText);
        else intent.putExtra("content", ShareFrame.SHARE_DEFAULT_TEXT);
        ((Activity) getContext()).startActivityForResult(intent, SinaBlog.SINA_REQUEST_CODE);
    }

    private boolean sendToQzone(String url, final String pic_path) {
        if (mQzone == null) {
            mQzone = new QzoneBlog2(getContext());
        }
        if (!mQzone.checkQQClientInstall()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
            return false;
        }
        mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
        mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
        mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
            @Override
            public void sendComplete(int result)
            {
                if(result == QzoneBlog2.SEND_SUCCESS)
                {
                    TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_视频_分享到QQ空间, getResources().getString(TAG));
                    ShareTools.addIntegral(getContext());
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                    MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_视频分享页_主页面);
                }
                else if (result == QzoneBlog2.SEND_CANCEL) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                }
                File file = new File(pic_path);
                if (file.exists()) file.delete();
            }
        });
        if(!mQzone.sendToQzone2(mShareDescription, pic_path, mShareTitle, url))
        {
            SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
            return false;
        }
        return true;
    }

    private void sendPicToQzone(String pic) {
        if (mQzone == null) {
            mQzone = new QzoneBlog2(getContext());
        }
        if (!mQzone.checkQQClientInstall()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
            return;
        }
        mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
        mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
        mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
            @Override
            public void sendComplete(int result) {
                if(result == QzoneBlog2.SEND_SUCCESS)
                {
                    TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到QQ空间, getResources().getString(TAG));
                    ShareTools.addIntegral(getContext());
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                    MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ空间, R.string.拍照_萌妆照分享页_主页面);
                }
                else if (result == QzoneBlog2.SEND_CANCEL) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                }
            }
        });
        if(!mQzone.sendToPublicQzone(1, pic))
        {
            SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
        }
    }

    private boolean sendToQQ(String url, final String pic_path) {
        if (mQzone == null) {
            mQzone = new QzoneBlog2(getContext());
        }
        if (!mQzone.checkQQClientInstall()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
            return false;
        }
        mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
        mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
        mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
            @Override
            public void sendComplete(int result)
            {
                if(result == QzoneBlog2.SEND_SUCCESS)
                {
//                    TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer., getResources().getString(TAG));
                    ShareTools.addIntegral(getContext());
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                    MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_视频分享页_主页面);
                }
                else if (result == QzoneBlog2.SEND_CANCEL) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                }
                File file = new File(pic_path);
                if (file.exists()) file.delete();
            }
        });
        if(!mQzone.sendUrlToQQ(pic_path, mShareTitle, mShareDescription, url))
        {
            SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
            return false;
        }
        return true;
    }

    private void sendPicToQQ(String pic) {
        if (mQzone == null) {
            mQzone = new QzoneBlog2(getContext());
        }
        if (!mQzone.checkQQClientInstall()) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_qq_error_clinet_no_install), Toast.LENGTH_LONG).show();
            return;
        }
        mQzone.SetAccessToken(GetSettingInfo(getContext()).GetQzoneAccessToken());
        mQzone.setOpenId(GetSettingInfo(getContext()).GetQzoneOpenid());
        mQzone.setSendQQorQzoneCompletelistener(new QzoneBlog2.SendQQorQzoneCompletelistener() {
            @Override
            public void sendComplete(int result) {
                if(result == QzoneBlog2.SEND_SUCCESS)
                {
//                    TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer., getResources().getString(TAG));
                    ShareTools.addIntegral(getContext());
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                    MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.QQ好友, R.string.拍照_萌妆照分享页_主页面);
                }
                else if (result == QzoneBlog2.SEND_CANCEL) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                }
            }
        });
        if(!mQzone.sendToQQ(pic))
        {
            SharePage.showQQErrorMessageToast(getContext(), mQzone.LAST_ERROR);
        }
    }

    private boolean sendToWeiXin(String url, String pic, final boolean WXSceneSession) {
        if (url == null || url.length() <= 0 || pic == null || pic.length() <= 0) return false;

        if (mWeiXin == null) {
            mWeiXin = new WeiXinBlog(getContext());
        }
        Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
        String title = WXSceneSession ? mShareTitle : mShareFriendsTitle;
        if (mWeiXin.sendUrlToWeiXin(url, title, mShareDescription, thumb, WXSceneSession)) {
            SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener() {
                @Override
                public void onCallFinish(int result) {
                    switch (result) {
                        case BaseResp.ErrCode.ERR_OK:
                            if(WXSceneSession)
                            {
                                TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_视频_分享到微信好友, getResources().getString(TAG));
                                MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_视频分享页_主页面);
                            }
                            else
                            {
                                TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_视频_分享到朋友圈, getResources().getString(TAG));
                                MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_视频分享页_主页面);
                            }
                            ShareTools.addIntegral(getContext());
                            Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            Toast.makeText(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    SendWXAPI.removeListener(this);
                }
            };
            SendWXAPI.addListener(listener);
        } else {
            SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, WXSceneSession);
            return false;
        }
        return true;
    }

    private void sendPicToWeiXin(String pic, final boolean WXSceneSession) {
        if (pic == null || pic.length() <= 0) return;

        if (mWeiXin == null) {
            mWeiXin = new WeiXinBlog(getContext());
        }
        Bitmap thumb = MakeBmp.CreateBitmap(cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, 150, 150), 150, 150, -1, 0, Bitmap.Config.ARGB_8888);
        if (mWeiXin.sendToWeiXin(pic, thumb, WXSceneSession)) {
            SendWXAPI.WXCallListener listener = new SendWXAPI.WXCallListener() {
                @Override
                public void onCallFinish(int result) {
                    switch (result) {
                        case BaseResp.ErrCode.ERR_OK:
                            if(WXSceneSession)
                            {
                                TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到微信好友, getResources().getString(TAG));
                                MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.微信好友, R.string.拍照_萌妆照分享页_主页面);
                            }
                            else
                            {
                                TongJi2.AddOnlineClickCount(getContext(), resTjId, R.integer.拍照_动态贴纸_预览_分享_分享到朋友圈, getResources().getString(TAG));
                                MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.朋友圈, R.string.拍照_萌妆照分享页_主页面);
                            }
                            ShareTools.addIntegral(getContext());
                            Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            Toast.makeText(getContext(), getResources().getString(R.string.share_send_cancel), Toast.LENGTH_LONG).show();
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    SendWXAPI.removeListener(this);
                }
            };
            SendWXAPI.addListener(listener);
        } else {
            SharePage.showWeiXinErrorMessage(getContext(), mWeiXin.LAST_ERROR, WXSceneSession);
        }
    }

    /**
     * 发送图片到Facebook
     * @param pic 图片路径(图片不能大于12mb)
     */
    public void sendPicToFacebook(String pic)
    {
        if(mFacebook == null) mFacebook = new FacebookBlog(getContext());
        Bitmap bmp = cn.poco.imagecore.Utils.DecodeImage(getContext(), pic, 0, -1, -1, -1);
        final ProgressDialog mFacebookProgressDialog = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.share_facebook_client_call));
        mFacebookProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mFacebookProgressDialog.setCancelable(true);
        boolean send_success = mFacebook.sendPhotoToFacebookBySDK(bmp, new FacebookBlog.FaceBookSendCompleteCallback()
        {
            @Override
            public void sendComplete(int result, String error_info)
            {
                if(mFacebookProgressDialog.isShowing()) mFacebookProgressDialog.dismiss();
                else return;
                if(result == FacebookBlog.RESULT_SUCCESS)
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                    ShareTools.addIntegral(getContext());
                    MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_萌妆照分享页_主页面);
                }
                else
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                }
            }
        });

        if(!send_success)
        {
            mFacebookProgressDialog.dismiss();
            String message = null;
            switch(mFacebook.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = getContext().getResources().getString(R.string.share_facebook_client_no_install);
                    break;

                case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                    message = getContext().getResources().getString(R.string.share_error_image_is_null);
                    break;

                default:
                    message = getContext().getResources().getString(R.string.share_facebook_client_start_fail);
                    break;
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 发送视频到Facebook
     * @param video_path 视频路径，视频不能大于12m
     */
    public void sendVideoToFacebook(String video_path)
    {
        if(mFacebook == null) mFacebook = new FacebookBlog(getContext());
        final ProgressDialog mFacebookProgressDialog = ProgressDialog.show(getContext(), "", getContext().getResources().getString(R.string.share_facebook_client_call));
        mFacebookProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mFacebookProgressDialog.setCancelable(true);
        boolean send_success = mFacebook.sendVideoToFacebookBySDK(video_path, new FacebookBlog.FaceBookSendCompleteCallback()
        {
            @Override
            public void sendComplete(int result, String error_info)
            {
                if(mFacebookProgressDialog.isShowing()) mFacebookProgressDialog.dismiss();
                else return;
                if(result == FacebookBlog.RESULT_SUCCESS)
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_success), Toast.LENGTH_LONG).show();
                    ShareTools.addIntegral(getContext());
                    MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Facebook, R.string.拍照_视频分享页_主页面);
                }
                else
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.share_send_fail), Toast.LENGTH_LONG).show();
                }
            }
        });

        if(!send_success)
        {
            mFacebookProgressDialog.dismiss();
            String message;
            switch(mFacebook.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = getContext().getResources().getString(R.string.share_facebook_client_no_install);
                    break;

                case WeiboInfo.BLOG_INFO_VIDEO_IS_NULL:
                    message = getContext().getResources().getString(R.string.share_error_video_is_null);
                    break;

                default:
                    message = getContext().getResources().getString(R.string.share_facebook_client_start_fail);
                    break;
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 发送图片和文字到Twitter，两者至少有一种
     * @param pic 图片路径
     * @param content 文字内容
     */
    public void sendPicToTwitter(String pic, String content)
    {
        if(mTwitter == null) mTwitter = new TwitterBlog(getContext());
        if(!mTwitter.sendToTwitter(pic, content))
        {
            String message;
            switch(mTwitter.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = getContext().getResources().getString(R.string.share_twitter_client_no_install);
                    break;

                case WeiboInfo.BLOG_INFO_CONTEXT_IS_NULL:
                    message = getContext().getResources().getString(R.string.share_error_context_is_null);
                    break;

                default:
                    message = getContext().getResources().getString(R.string.share_send_fail);
                    break;
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            return;
        }
        ShareTools.addIntegral(getContext());
        MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_萌妆照分享页_主页面);
    }

    /**
     * 发送视频和文字到Twitter，两者至少有一种
     * @param video_path 视频路径
     * @param content 文字内容
     */
    public void sendVideoToTwitter(String video_path, String content)
    {
        if(mTwitter == null) mTwitter = new TwitterBlog(getContext());
        if(!mTwitter.sendVideoToTwitter(video_path, content))
        {
            String message;
            switch(mTwitter.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = getContext().getResources().getString(R.string.share_twitter_client_no_install);
                    break;

                case WeiboInfo.BLOG_INFO_VIDEO_IS_NULL:
                    message = getContext().getResources().getString(R.string.share_error_video_is_null);
                    break;

                default:
                    message = getContext().getResources().getString(R.string.share_send_fail);
                    break;
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            return;
        }
        ShareTools.addIntegral(getContext());
        MyBeautyStat.onShareCompleteByRes(MyBeautyStat.BlogType.Twitter, R.string.拍照_视频分享页_主页面);
    }

    /**
     * 发送图片到Instagram
     * @param pic 图片
     */
    public void sendPicToInstagram(String pic)
    {
        if(mInstagram == null) mInstagram = new InstagramBlog(getContext());
        if(!mInstagram.sendToInstagram(pic))
        {
            String message;
            switch(mInstagram.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = getContext().getResources().getString(R.string.share_instagram_client_no_install);
                    break;

                case WeiboInfo.BLOG_INFO_IMAGE_IS_NULL:
                    message = getContext().getResources().getString(R.string.share_error_image_is_null);
                    break;

                default:
                    message = getContext().getResources().getString(R.string.share_send_fail);
                    break;
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            return;
        }
        ShareTools.addIntegral(getContext());
    }

    /**
     * 发送视频到Instagram
     * @param video_path 视频路径
     */
    public void sendVideoToInstagram(String video_path)
    {
        if(mInstagram == null) mInstagram = new InstagramBlog(getContext());
        if(!mInstagram.sendVideoToInstagram(video_path))
        {
            String message;
            switch(mInstagram.LAST_ERROR)
            {
                case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
                    message = getContext().getResources().getString(R.string.share_instagram_client_no_install);
                    break;

                case WeiboInfo.BLOG_INFO_VIDEO_IS_NULL:
                    message = getContext().getResources().getString(R.string.share_error_video_is_null);
                    break;

                default:
                    message = getContext().getResources().getString(R.string.share_send_fail);
                    break;
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            return;
        }
        ShareTools.addIntegral(getContext());
    }

    /**
     * 发送图片和文字到Circle
     * @param pic 图片路径
     * @param content 文字内容
     */
    public void sendToCircle(String pic, String content)
    {
        if(pic == null || pic.length() <= 0 || !new File(pic).exists()) return;

//        if(mCircleApi == null) mCircleApi = CircleSDK.createApi(getContext(), 3);
//        CircleMultiInfo multiInfo = new CircleMultiInfo();
//        if(content != null)
//        {
//            TextObject textObject = new TextObject();
//            textObject.text = content;
//            multiInfo.add(textObject);
//        }
//
//        ImageObject imageObject = new ImageObject();
//        imageObject.imgUri = ShareTools.decodePath(pic);
//        imageObject.imgExtra = makeCircleExtra(resId, resTjId);
//        multiInfo.add(imageObject);
//
//        CircleApi.setOnCallBackListener(new CircleApi.OnCallBackListener()
//        {
//            @Override
//            public void OnMessage(int code , String msg)
//            {
//                if(code == 0) ShareTools.addIntegral(getContext());
//                SharePage.showCircleCodeMessage(getContext(), code);
//            }
//        });
//        mCircleApi.attachInfo(multiInfo);
//        mCircleApi.share();
        shareToCommunity(pic, content, 1);
    }

    /**
     * 发送视频和文字到Circle
     * @param video_path 视频路径
     * @param content 文字内容
     */
    public void sendVideoToCircle(String video_path, String content)
    {
        if(video_path == null || video_path.length() <= 0 || !new File(video_path).exists()) return;

//        if(mCircleApi == null) mCircleApi = CircleSDK.createApi(getContext(), 3);
//        CircleMultiInfo multiInfo = new CircleMultiInfo();
//        if(content != null)
//        {
//            TextObject textObject = new TextObject();
//            textObject.text = content;
//            multiInfo.add(textObject);
//        }
//        VideoObject videoObject = new VideoObject();
//        videoObject.videoUri = ShareTools.decodePath(video_path);
//        videoObject.videoExtra = makeCircleExtra(resId, resTjId);
//        multiInfo.add(videoObject);
//
//        CircleApi.setOnCallBackListener(new CircleApi.OnCallBackListener()
//        {
//            @Override
//            public void OnMessage(int code , String msg)
//            {
//                if(code == 0) ShareTools.addIntegral(getContext());
//                SharePage.showCircleCodeMessage(getContext(), code);
//            }
//        });
//        mCircleApi.attachInfo(multiInfo);
//        mCircleApi.share();
        shareToCommunity(video_path, content, 2);
    }

    public static String makeCircleExtra(int resId, String resTjId)
    {
        try
        {
            JSONObject extra = new JSONObject();
            JSONArray material = new JSONArray();
            JSONObject res = new JSONObject();
            res.put("id", resId);
            if(resTjId != null) res.put("stat_id", resTjId);
            material.put(res);
            extra.put("material", material);
            //System.out.println(extra.toString());
            return extra.toString();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void shareToCommunity(String path, String content, int type)
    {
        if(!UserMgr.IsLogin(getContext(), null)){
            //未登录去登录
            mPageSite.OnLogin(getContext());
            return ;
        }
        UserInfo userInfo = UserMgr.ReadCache(getContext());
        if(userInfo != null && TextUtils.isEmpty(userInfo.mMobile))
        {
            //未完善资料去完善资料
            mPageSite.OnBindPhone(getContext());
            return ;
        }
        if(content == null) content = "";
        if(TextUtils.isEmpty(path))
        {
            return ;
        }
        mPageSite.OnCommunity(getContext(), path, content, type, makeCircleExtra(resId, resTjId));
    }

    private void showSuccessDialog()
    {
        SharedTipsView view=new SharedTipsView(getContext());

        final Dialog dialog = new Dialog(getContext(), R.style.fullDialog1);
        view.setJump2AppClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPageSite.OnCommunityHome(getContext());
                dialog.dismiss();
            }
        });
        view.setStayClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setContentView(view);
    }

    private void uploadVedio(String video_path, final UploadVideoCallback callback) {
//        mProgressDialog = ProgressDialog.show(getContext(), "", getResources().getString(R.string.lightapp06_video_uploading));
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_videoSaving = true;
        m_shareFrame.setVisibility(View.GONE);
        m_progressFrame.setVisibility(View.VISIBLE);
        setVideoSaveProgress(0);

        final UploadData data = new UploadData();
        if(mChannelValue != null && mChannelValue.length() > 0) data.mChannelValue = mChannelValue;
        else data.mChannelValue = "beauty_2017";
        data.mImgPath = m_picPath;
        data.mPostParams = SharePage.getPostActivitiesStr2(getContext(), mAdvPostStr).toString();
        data.mVideoPath = video_path;
        data.mStatId = resTjId;

        if(m_uploadCallback == null)
        {
            m_uploadCallback = new ObjHandlerHolder<AbsUploadFile.Callback>(new AbsUploadFile.Callback()
            {
                @Override
                public void onProgress(int progress)
                {
                    setVideoSaveProgress(progress);
                }

                @Override
                public void onSuccess(final String shareUrl)
                {
                    post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(shareUrl == null || shareUrl.length() <= 0)
                            {
                                m_videoSaving = false;
                                m_shareFrame.setVisibility(View.VISIBLE);
                                m_progressFrame.setVisibility(View.GONE);
                                if (callback != null) callback.uploadFail();
                                return;
                            }
                            m_uploadComplete = true;
                            if (callback != null) callback.uploadComplete(shareUrl);
                        }
                    });
                }

                @Override
                public void onFailure()
                {
                    post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            m_videoSaving = false;
                            m_shareFrame.setVisibility(View.VISIBLE);
                            m_progressFrame.setVisibility(View.GONE);
                            if (callback != null) callback.uploadFail();
                        }
                    });
                }
            });
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                new UploadFile(getContext(), data, AppInterface.GetInstance(getContext()), m_uploadCallback);
            }
        }).start();
    }

    /**
     * 上传图片到poco获取分享链接
     *
     * @return
     */
//    private String sendToPocoGetUrl(String pic, String mp4_url) {
//        String post_url = "http://www1.poco.cn/topic/interface/beautycamera_common_post.php";
//        String post_str;
//        if(mAdvPostStr != null) post_str = getPostActivitiesStr(mAdvPostStr);
//        else post_str = getPostActivitiesStr();
//        String url = sendPocoActivities(post_url, post_str, pic, mp4_url);
//        return url;
//    }

    private String makePostStr(Context context) {
        String postVar = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = null;
            if (tm != null) {
                imei = tm.getDeviceId();
            }
            if (imei == null) {
                imei = Integer.toString((int) (Math.random() * 1000000));
            }
            imei = CommonUtils.Encrypt("MD5", imei);

            postVar += "|channel_value:" + URLEncoder.encode("beautycamera_2016", "UTF-8");
            postVar += "|hash:" + URLEncoder.encode(imei, "UTF-8");
            String frame = Integer.toString(0 & 0x00ff, 16);
            if (frame.length() < 2) {
                frame = "0" + frame;
            }
            postVar += "|frame_id:" + frame;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return postVar;
    }

    private String getPostActivitiesStr() {
        String postVal = makePostStr(getContext());
        String postText = "动态贴纸";
        try {
//            if (GetSettingInfo(getContext()).GetPocoId() != null && GetSettingInfo(getContext()).GetPocoId().length() > 0) {
//                postText += "|poco_id:" + URLEncoder.encode(GetSettingInfo(getContext()).GetPocoId(), "UTF-8");
//            }
            if (GetSettingInfo(getContext()).GetSinaUid() != null && GetSettingInfo(getContext()).GetSinaUid().length() > 0 &&
                    GetSettingInfo(getContext()).GetSinaUserNick() != null && GetSettingInfo(getContext()).GetSinaUserNick().length() > 0) {
                postText += "|sina:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUid(), "UTF-8");
                postText += "|sina_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUserNick(), "UTF-8");
            }
            if(GetSettingInfo(getContext()).GetQzoneOpenid() != null && GetSettingInfo(getContext()).GetQzoneOpenid().length() > 0 &&
                    GetSettingInfo(getContext()).GetQzoneUserName() != null && GetSettingInfo(getContext()).GetQzoneUserName().length() > 0)
            {
                postText += "|q_zone:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneOpenid(), "UTF-8");
                postText += "|q_zone_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneUserName(), "UTF-8");
            }
            if (SharePage.city != null && SharePage.city.length() > 0) {
                postText += "|city:" + URLEncoder.encode(SharePage.city, "UTF-8");
                postText += "|latlng:" + SharePage.lat + "," + SharePage.lon;
            }
            postVal += "|content:" + URLEncoder.encode(postText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return postVal;
    }

    private String getPostActivitiesStr(String postVal) {
        if(postVal == null) return null;
        String postText = postVal;
        try {
//            if (GetSettingInfo(getContext()).GetPocoId() != null && GetSettingInfo(getContext()).GetPocoId().length() > 0) {
//                postText += "|poco_id:" + URLEncoder.encode(GetSettingInfo(getContext()).GetPocoId(), "UTF-8");
//            }
            if (GetSettingInfo(getContext()).GetSinaUid() != null && GetSettingInfo(getContext()).GetSinaUid().length() > 0 &&
                    GetSettingInfo(getContext()).GetSinaUserNick() != null && GetSettingInfo(getContext()).GetSinaUserNick().length() > 0) {
                postText += "|sina:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUid(), "UTF-8");
                postText += "|sina_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetSinaUserNick(), "UTF-8");
            }
            if(GetSettingInfo(getContext()).GetQzoneOpenid() != null && GetSettingInfo(getContext()).GetQzoneOpenid().length() > 0 &&
                    GetSettingInfo(getContext()).GetQzoneUserName() != null && GetSettingInfo(getContext()).GetQzoneUserName().length() > 0)
            {
                postText += "|q_zone:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneOpenid(), "UTF-8");
                postText += "|q_zone_nickname:" + URLEncoder.encode(GetSettingInfo(getContext()).GetQzoneUserName(), "UTF-8");
            }
            if (SharePage.city != null && SharePage.city.length() > 0) {
                postText += "|city:" + URLEncoder.encode(SharePage.city, "UTF-8");
                postText += "|latlng:" + SharePage.lat + "," + SharePage.lon;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return postText;
    }

    private String sendPocoActivities(String postUrl, String postStr, String file, String mp4_url) {
        if (postStr == null) return null;

        final String BOUNDARY = java.util.UUID.randomUUID().toString();
        final String RETURN = "\r\n";
        final String PREFIX = "--";
        final String CHARSET = "UTF-8";
        HttpURLConnection conn = null;
        try {
            HashMap<String, String> values = new HashMap<String, String>();
            values.put("post_str", postStr);
            values.put("mp4_url", mp4_url);

            URL _url = new URL(postUrl);
            conn = (HttpURLConnection) _url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", CHARSET);
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(RETURN);
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + RETURN);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + RETURN);
                sb.append("Content-Transfer-Encoding: 8bit" + RETURN);
                sb.append(RETURN);
                sb.append(entry.getValue());
                sb.append(RETURN);
            }
            outStream.write(sb.toString().getBytes());
            if (file != null) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(RETURN);
                sb1.append("Content-Disposition: form-data; name=\"opus\"; filename=\"" + file + "\"" + RETURN);
                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + RETURN);
                sb1.append(RETURN);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                outStream.write(RETURN.getBytes());

                outStream.write((PREFIX + BOUNDARY + PREFIX + RETURN).getBytes());
                outStream.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //解析返回的数据
                InputStreamReader isReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(isReader);
                String line;
                StringBuilder strb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    strb.append(line);
                }
                String str = strb.toString();
                isReader.close();
                reader.close();
                if (str.length() > 0) {
                    JSONObject json = new JSONObject(str);
                    json = json.getJSONObject("Result");
                    if (json != null) {
                        String code = json.getString("ResultCode");
                        String err = json.getString("ResultMessage");
                        if (code != null && code.equals("0") && err != null && err.equals("success")) {
                            conn.disconnect();
                            return json.getString("share_img_link");
                        }
                    }
                }
            } else {
                //System.out.println("发送活动：服务器返还不为200，responseCode=" + responseCode);
            }
            outStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(conn != null) conn.disconnect();
        }
        return null;
    }

    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        if (mPageHandler != null) {
            mPageHandler.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) imm.hideSoftInputFromWindow(getWindowToken(), 0);
                }
            });
        }
    }

    private void showShareVideoDialog(int blog_type, final ShareVideoDialogCallback callback)
    {
        if(m_isShareVideoDialogShow || m_onClose) return;

        m_isShareVideoDialogShow = true;
        final Dialog dlg = new Dialog(getContext(), R.style.notitledialog);

        FrameLayout.LayoutParams fl;
        LinearLayout.LayoutParams ll;

        FrameLayout mainFrame = new FrameLayout(getContext());
        mainFrame.setBackgroundDrawable(new BitmapDrawable(getResources(), getGlassBackground()));
        fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainFrame.setLayoutParams(fl);
        mainFrame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_返回);
                dlg.dismiss();
            }
        });

        FrameLayout frame = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568), ShareData.PxToDpi_xhdpi(686));
        fl.gravity = Gravity.CENTER;
        mainFrame.addView(frame, fl);
        {
            LinearLayout share_frame = new LinearLayout(getContext());
            share_frame.setOrientation(LinearLayout.VERTICAL);
            share_frame.setBackgroundResource(R.drawable.light_app06_sharetype_dialog_share_bg);
            fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(328));
            fl.gravity = Gravity.TOP | Gravity.LEFT;
            frame.addView(share_frame, fl);
            {
                TextView share_title = new TextView(getContext());
                share_title.setText(getContext().getResources().getString(R.string.lightapp06_share_type_title1));
                share_title.setTextColor(Color.BLACK);
                share_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                share_title.getPaint().setFakeBoldText(true);
                ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ll.topMargin = ShareData.PxToDpi_xhdpi(44);
                share_frame.addView(share_title, ll);

                TextView share_description = new TextView(getContext());
                share_description.setText(getContext().getResources().getString(R.string.lightapp06_share_type_please) + getShareTypeString(blog_type) + "\n" + getContext().getResources().getString(R.string.lightapp06_share_type_description1));
                share_description.setTextColor(Color.BLACK);
                share_description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                share_description.setGravity(Gravity.CENTER);
                ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ll.topMargin = ShareData.PxToDpi_xhdpi(20);
                share_frame.addView(share_description, ll);

                FrameLayout share_button = new FrameLayout(getContext());
                ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(260), LayoutParams.WRAP_CONTENT);
                ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ll.topMargin = ShareData.PxToDpi_xhdpi(34);
                share_frame.addView(share_button, ll);
                {
                    ImageView bg = new ImageView(getContext());
                    bg.setImageResource(R.drawable.photofactory_noface_help_btn);
                    bg.setScaleType(ImageView.ScaleType.FIT_XY);
                    fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    fl.gravity = Gravity.LEFT | Gravity.TOP;
                    share_button.addView(bg, fl);
                    cn.poco.advanced.ImageUtils.AddSkin(getContext(), bg);

                    TextView text = new TextView(getContext());
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    text.setText(getShareTypeString(blog_type));
                    text.getPaint().setFakeBoldText(true);
                    fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    fl.gravity = Gravity.CENTER;
                    share_button.addView(text, fl);
                }
                share_button.setOnTouchListener(new OnAnimationClickListener()
                {
                    @Override
                    public void onAnimationClick(View v)
                    {
                        dlg.dismiss();
                        if(callback != null) callback.shareVideoType(false);
                    }

                    @Override
                    public void onTouch(View v){}

                    @Override
                    public void onRelease(View v){}
                });
            }

            ImageView or_icon = new ImageView(getContext());
            or_icon.setImageResource(R.drawable.light_app06_sharetype_dialog_or);
            fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            frame.addView(or_icon, fl);

            TextView or_text = new TextView(getContext());
            or_text.setText("OR");
            int color = cn.poco.advanced.ImageUtils.GetSkinColor();
            if(color != 0) or_text.setTextColor(color);
            else or_text.setTextColor(0xffe75988);
            or_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
            fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            frame.addView(or_text, fl);

            LinearLayout upload_frame = new LinearLayout(getContext());
            upload_frame.setOrientation(LinearLayout.VERTICAL);
            upload_frame.setBackgroundResource(R.drawable.light_app06_sharetype_dialog_upload_bg);
            fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(328));
            fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
            frame.addView(upload_frame, fl);
            {
                TextView upload_title = new TextView(getContext());
                upload_title.setText(getContext().getResources().getString(R.string.lightapp06_share_type_title2));
                upload_title.setTextColor(Color.BLACK);
                upload_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                upload_title.getPaint().setFakeBoldText(true);
                ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ll.topMargin = ShareData.PxToDpi_xhdpi(44);
                upload_frame.addView(upload_title, ll);

                TextView upload_description = new TextView(getContext());
                upload_description.setText(getContext().getResources().getString(R.string.lightapp06_share_type_description2));
                upload_description.setTextColor(Color.BLACK);
                upload_description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                upload_description.setGravity(Gravity.CENTER);
                ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ll.topMargin = ShareData.PxToDpi_xhdpi(20);
                upload_frame.addView(upload_description, ll);

                FrameLayout upload_button = new FrameLayout(getContext());
                ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(260), LayoutParams.WRAP_CONTENT);
                ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                ll.topMargin = ShareData.PxToDpi_xhdpi(34);
                upload_frame.addView(upload_button, ll);
                {
                    ImageView bg = new ImageView(getContext());
                    bg.setImageResource(R.drawable.photofactory_noface_help_btn);
                    bg.setScaleType(ImageView.ScaleType.FIT_XY);
                    fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                    fl.gravity = Gravity.LEFT | Gravity.TOP;
                    upload_button.addView(bg, fl);
                    cn.poco.advanced.ImageUtils.AddSkin(getContext(), bg);

                    TextView text = new TextView(getContext());
                    text.setTextColor(Color.WHITE);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    text.setText(getContext().getResources().getString(R.string.lightapp06_share_type_upload));
                    text.getPaint().setFakeBoldText(true);
                    fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    fl.gravity = Gravity.CENTER;
                    upload_button.addView(text, fl);
                }
                upload_button.setOnTouchListener(new OnAnimationClickListener()
                {
                    @Override
                    public void onAnimationClick(View v)
                    {
                        dlg.dismiss();
                        if(callback != null) callback.shareVideoType(true);
                    }

                    @Override
                    public void onTouch(View v){}

                    @Override
                    public void onRelease(View v){}
                });
            }
        }
        frame.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        dlg.setContentView(mainFrame);
        dlg.setCanceledOnTouchOutside(false);
        Window w = dlg.getWindow();
        w.setWindowAnimations(R.style.fullDialog);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                m_isShareVideoDialogShow = false;
            }
        });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                MyBeautyStat.onClickByRes(R.string.拍照_视频分享页_主页面_分享弹窗_返回);
            }
        });
        dlg.show();
    }

    private void showShareVideoDialog2(int blog_type, final ShareVideoDialogCallback callback)
    {
        final Dialog dlg = new Dialog(getContext(), R.style.notitledialog);
        final DialogView2 view2 = new DialogView2(getContext(), getGlassBackground());
        view2.setInfo(getContext().getResources().getString(R.string.lightapp06_share_type_please) + getShareTypeString(blog_type) + getContext().getResources().getString(R.string.lightapp06_share_type_after) + "\n" + getContext().getResources().getString(R.string.lightapp06_share_type_description1), getShareTypeString(blog_type), new SharePage.DialogListener()
        {
            @Override
            public void onClick(int view)
            {
                if(view == DialogView2.VIEW_BUTTON)
                {
                    if(callback != null) callback.shareVideoType(false);
                }
                dlg.dismiss();
            }
        });
        dlg.setContentView(view2);
        dlg.setCanceledOnTouchOutside(false);
        Window w = dlg.getWindow();
        if(w != null) w.setWindowAnimations(R.style.fullDialog);
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                view2.clean();
            }
        });
        dlg.show();
    }

    public Bitmap getGlassBackground()
    {
        if(m_shareDialogBG == null || m_shareDialogBG.isRecycled())
        {
            m_shareDialogBG = SharePage.makeGlassBackground(SharePage.screenCapture(getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight));
        }
        return m_shareDialogBG;
    }

    private String getShareTypeString(int blog_type)
    {
        switch(blog_type)
        {
            case SharePage.WEIXIN:
                return getContext().getResources().getString(R.string.lightapp06_share_type_wexin);

            case SharePage.SINA:
                return getContext().getResources().getString(R.string.lightapp06_share_type_sina);

            case SharePage.QZONE:
                return getContext().getResources().getString(R.string.lightapp06_share_type_qzone);
        }
        return null;
    }

    @Override
    public void onBack() {
        if(m_videoSaving || onAnimation || m_onClose) return;
        if(!m_showVideoFrame)
        {
            TongJi2.AddCountByRes(getContext(), R.integer.拍照_动态贴纸_预览_分享_返回);
            if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
            {
                MyBeautyStat.onPageEndByRes(R.string.拍照_萌妆照分享页_主页面);
                MyBeautyStat.onPageStartByRes(R.string.拍照_萌妆照预览页_主页面);
            }
            else
            {
                MyBeautyStat.onPageEndByRes(R.string.拍照_视频分享页_主页面);
                MyBeautyStat.onPageStartByRes(R.string.拍照_视频预览页_主页面);
            }
//          if(mTogetherTip != null) mTogetherTip.setVisibility(View.GONE);
//            removeTogetherTip();
            mAdvBar.onStop();
            m_videoFrame.setVisibility(View.VISIBLE);
            ShareFrame.UIanime2(m_shareBackground, m_topFrame, m_topBackground, mAdvBar, new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation){
                    onAnimation = true;
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    onAnimation = false;
                    if(m_onClose) return;
                    if(m_shareBackground != null) m_shareBackground.clearAnimation();
                    if(m_topFrame != null) m_topFrame.clearAnimation();
                    if(m_topBackground != null) m_topBackground.clearAnimation();
                    if(mAdvBar != null) mAdvBar.clearAnimation();
                    if(m_shareFrame != null) m_shareFrame.setVisibility(View.GONE);
                    m_showVideoFrame = true;
                    if (m_videoFrame != null)
                    {
                        m_videoFrame.setPageShow(m_showVideoFrame);
                        m_videoFrame.onResume(false);
                        m_videoFrame.changeSystemUiVisibility(View.GONE);//沉浸式导航栏处理
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation){}
            });
            return;
        }
        if(m_videoFrame != null && m_videoFrame.onBack()) return;

        if(mPageSite != null) mPageSite.onBack(getContext(), getVideoData());
    }

    private HashMap<String, Object> getVideoData() {
        HashMap<String, Object> params = null;
        if (!m_videoSaved && m_videoMgr != null)
        {
            params = new HashMap<>();
            params.put(CameraSetDataKey.KEY_IS_RESUME_VIDEO_PAUSE, true);
            params.put(CameraSetDataKey.KEY_RESUME_VIDEO_PAUSE_MGR, m_videoMgr);
        }
        else
        {
            if (m_videoMgr != null) m_videoMgr.clearAll();
        }
        if (params == null)
        {
            params = new HashMap<>();
        }
        params.put(CameraSetDataKey.KEY_IS_SHOW_STICKER_SELECTOR, false);
        m_videoMgr = null;
        return params;
    }

    @Override
    public void onPause() {
        TongJiUtils.onPagePause(getContext(), TAG);
        if (m_showVideoFrame && m_videoFrame != null) {
            m_videoFrame.onPause(true);
        }
        if(mAdvBar != null && !m_showVideoFrame) mAdvBar.onStop();
    }

    @Override
    public void onResume() {
        TongJiUtils.onPageResume(getContext(), TAG);
        if (m_showVideoFrame && m_videoFrame != null) {
            m_videoFrame.onResume(true);
        }
        if(m_uploadComplete)
        {
            m_uploadComplete = false;
            m_videoSaving = false;
            m_shareFrame.setVisibility(View.VISIBLE);
            m_progressFrame.setVisibility(View.GONE);
        }
        if(mAdvBar != null && !m_showVideoFrame)
        {
            mAdvBar.onStart();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSina != null) {
            mSina.onActivityResult(requestCode, resultCode, data, -1);
        }
        if (mQzone != null) {
            mQzone.onActivityResult(requestCode, resultCode, data);
        }
        if(mFacebook != null) {
            mFacebook.onActivityResult(requestCode, resultCode, data, 10086);
        }
        return false;
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params)
    {
        if(mAdvBar != null && !m_showVideoFrame) mAdvBar.onStart();
        switch(siteID)
        {
            case SiteID.PUBLISH_OPUS_PAGE:
                if(params != null && params.containsKey("isSuccess"))
                {
                    boolean b = (boolean)params.get("isSuccess");
                    if(b) showSuccessDialog();
                }
                break;

            case SiteID.LOGIN:
            case SiteID.REGISTER_DETAIL:
            case SiteID.RESETPSW:
                if(!UserMgr.IsLogin(getContext(), null)) return;
                UserInfo userInfo = UserMgr.ReadCache(getContext());
                if(userInfo == null) return;
                if(TextUtils.isEmpty(userInfo.mMobile)) return ;
                if(m_ShareBmp != null && !m_ShareBmp.isRecycled())
                {
                    sendToCircle(m_savePath, null);
                    return;
                }
                sendVideoToCircle(m_savePath, null);
                break;
        }
        super.onPageResult(siteID, params);
    }

    @Override
    public void onClose() {
        m_onClose = true;
//        removeTogetherTip();
        if(m_shareBackground != null) m_shareBackground.clearAnimation();
        if(m_topFrame != null) m_topFrame.clearAnimation();
        if(m_topBackground != null) m_topBackground.clearAnimation();
        if(m_weiboScroll != null) m_weiboScroll.clearAnimation();
        if(m_background != null && !m_background.isRecycled())
        {
            m_background.recycle();
            m_background = null;
        }
        if(m_shareDialogBG != null && !m_shareDialogBG.isRecycled())
        {
            m_shareDialogBG.recycle();
            m_shareDialogBG = null;
        }
        if (mSina != null) {
            mSina.clear();
            mSina = null;
        }
        if (mQzone != null) {
            mQzone.clear();
            mQzone = null;
        }
        m_ShareBmp = null;
        mWeiXin = null;
//        mCircleApi = null;
        if(mFacebook != null)
        {
            mFacebook.clear();
            mFacebook = null;
        }
        if(mTwitter != null)
        {
            mTwitter.clear();
            mTwitter = null;
        }
        if (m_videoFrame != null) {
            m_videoFrame.onClose();
            m_videoFrame = null;
        }
        if(mAdvBar != null)
        {
            mAdvBar.clean();
            mAdvBar = null;
        }
        if(m_uploadCallback != null)
        {
            m_uploadCallback.Clear();
            m_uploadCallback = null;
        }
        mAdvPostStr = null;
        mActConfigure = null;
        mChannelValue = null;
        mActChannelAdRes = null;
        TongJiUtils.onPageEnd(getContext(), TAG);
        System.gc();
    }
}

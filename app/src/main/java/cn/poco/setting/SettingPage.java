package cn.poco.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.common.friendpage.FileUtils;

import java.util.HashMap;
import java.util.Iterator;

import cn.poco.advanced.ImageUtils;
import cn.poco.blogcore.PocoBlog;
import cn.poco.blogcore.QzoneBlog2;
import cn.poco.blogcore.SinaBlog;
import cn.poco.camera.CameraConfig;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.setting.site.SettingPageSite;
import cn.poco.share.DialogView2;
import cn.poco.share.ImageButton;
import cn.poco.share.SharePage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.AppInterface;
import cn.poco.system.FolderMgr;
import cn.poco.system.SysConfig;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.FileUtil;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

public class SettingPage extends IPage {
    private static final int TAG = R.string.个人中心_设置;

    protected SettingPageSite m_site;
    private Context m_context;

    private ImageView mBtnCancel;
    private ImageButton mBtnAbout;
    private TextView mTxTopBar;
    private ScrollView mScrollView;
    private RelativeLayout mContainer;
    private TextView mTxCamera;
    private SettingGroup mSettingCamera;
    //private TextView	mTxGallery;
    //private SettingGroup mSettingGallery;
    private TextView mTxBeautify;
    private SettingGroup mSettingBeautify;
    private TextView mTxAbout;
    private SettingGroup mSettingAbout;

    protected TextView mTxWeibo;
    protected SettingGroup mSettingWeibo;

    protected SettingArrowBtn mABtnFocusMode;
    protected SettingArrowBtn mABtnFlashMode;
    protected SettingArrowBtn mABtnBrightness;
//    protected SettingArrowBtn mABtnAccount;

//    protected SettingArrowBtn mABtnSinaAccount;
    //	protected SettingArrowBtn mABtnRenrenAccount;
//	protected SettingArrowBtn mABtnQQAccount;
//    protected SettingArrowBtn mABtnQZoneAccount;
//	protected SettingArrowBtn mABtnDouBanAccount;
//	protected SettingArrowBtn mABtnFacebookAccount;
//	protected SettingArrowBtn mABtnTwitterAccount;
//	protected SettingArrowBtn mABtnTumblrAccount;

    protected SettingArrowBtn mABtnComment;
    protected SettingArrowBtn mABtnAbout;
    protected SettingArrowBtn mABtnInvite;
    protected SettingArrowBtn mABtnJieShao;
    protected SettingArrowBtn mABtnCheckUpdate;
    protected SettingArrowBtn mABtnFeedback;//意见
    protected SettingArrowBtn mABtnClearCache;
    protected SettingSliderBtn mSBtnRememberLens;
    protected SettingSliderBtn mSBtnAutoSaveSD;
    protected SettingSliderBtn mSBtnRememberBeautifyMode;
    protected SettingSliderBtn mSBtnNoSound;
    protected SettingSliderBtn mSBtnTickSound;
    protected SettingSliderBtn mSBtnFullScreen;
    //protected SettingSliderBtn mSBtnAutoUpload;
    //protected SettingSliderBtn mSBtnFSShutter;
    //protected SettingSliderBtn mSBtnJustWifi;
    protected SettingSliderBtn mSBtnAttachDate;

    protected SettingSliderBtn mSBtnActualCamera;//实时美颜
    protected SettingSliderBtn mSBtnAutoOpenCamera;
//    protected SettingSliderBtn mSBtnAutoQudou;
//    protected SettingSliderBtn mSBtnAutoQuyandai;
//    protected SettingSliderBtn mSBtnAutoLiangyan;
    protected SettingArrowBtn mABtnPhotoWatermarkSet;
    protected SettingSliderBtn mSBtnHDPhoto;
    protected SettingSliderBtn mSBtnBeautyAutoThinFace;
    //旋转镜头,重置镜头设置
    protected SettingArrowBtn mABtnCameraRotate;
    protected SettingArrowBtn mABtnReset;
    protected SettingSliderBtn mSBtnCameraFaceDetect;

//    protected SettingSliderBtn mSBtnAutoBigEye;
//    protected SettingSliderBtn mSBtnAutoThinFace;

    //智能美形
    protected SettingArrowBtn mABtnTailorMade;

    private final int str_color = 0x99333333;
    private final float str_size = 13.0f;

    protected TextView mTxVer;

    private int bindByOtherAccount = -1;    //用其他账号绑定Poco微博

    private PocoBlog mPoco;
    private SinaBlog mSina;
    private QzoneBlog2 mQzone;

    private Bitmap m_shareDialogBG;         //对话框背景截图

    public SettingPage(Context context, BaseSite site) {
        super(context, site);
        m_site = (SettingPageSite) site;
        m_context = context;
        initData();
        initUI();
        setConfigInfo();

        TongJiUtils.onPageStart(getContext(), TAG);
        MyBeautyStat.onPageStartByRes(R.string.个人中心_通用设置_主页面);
    }

    private void initData() {
        ShareData.InitData((Activity) m_context);
        SharePage.initBlogConfig(m_context);
    }

    @Override
    public void onPause()
    {
        TongJiUtils.onPagePause(getContext(), TAG);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        TongJiUtils.onPageResume(getContext(), TAG);
        super.onResume();
    }

    @Override
    public void onClose()
    {
        TongJiUtils.onPageEnd(getContext(), TAG);
        MyBeautyStat.onPageEndByRes(R.string.个人中心_通用设置_主页面);
        if(m_shareDialogBG != null && !m_shareDialogBG.isRecycled())
        {
            m_shareDialogBG.recycle();
            m_shareDialogBG = null;
        }
        mABtnComment = null;
        mABtnAbout = null;
        mABtnInvite = null;
        mABtnJieShao = null;
        mABtnCheckUpdate = null;
        mABtnFeedback = null;
        mABtnClearCache = null;

        mSBtnRememberLens = null;
        mSBtnAutoSaveSD = null;
        mSBtnRememberBeautifyMode = null;
        mSBtnNoSound = null;
        mSBtnTickSound = null;
        mSBtnFullScreen = null;
        mSBtnAttachDate = null;
        mSBtnActualCamera = null;
        mSBtnAutoOpenCamera = null;
        mSBtnHDPhoto = null;
        mABtnPhotoWatermarkSet = null;
        mSBtnCameraFaceDetect = null;
        mSBtnBeautyAutoThinFace = null;

        mABtnCameraRotate = null;
        mABtnReset = null;
        mABtnTailorMade = null;
        mSettingCamera = null;
        mSettingBeautify = null;
        mSettingAbout = null;
        System.gc();

        super.onClose();
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
    }

    private void initUI() {
        mABtnFocusMode = new SettingArrowBtn(m_context);
        mABtnFocusMode.setOnClickListener(mClickListener);
        mABtnFlashMode = new SettingArrowBtn(m_context);
        mABtnFlashMode.setOnClickListener(mClickListener);
        mABtnBrightness = new SettingArrowBtn(m_context);
        mABtnBrightness.setOnClickListener(mClickListener);
//        mABtnAccount = new SettingArrowBtn(m_context);
//        mABtnAccount.setOnClickListener(mClickListener);
//        mABtnSinaAccount = new SettingArrowBtn(m_context);
//        mABtnSinaAccount.setOnClickListener(mClickListener);
//		mABtnQQAccount = new SettingArrowBtn(m_context);
//		mABtnQQAccount.setOnClickListener(mClickListener);
//		mABtnRenrenAccount = new SettingArrowBtn(m_context);
//		mABtnRenrenAccount.setOnClickListener(mClickListener);
//        mABtnQZoneAccount = new SettingArrowBtn(m_context);
//        mABtnQZoneAccount.setOnClickListener(mClickListener);
//		mABtnDouBanAccount = new SettingArrowBtn(m_context);
//		mABtnDouBanAccount.setOnClickListener(mClickListener);
//		mABtnFacebookAccount = new SettingArrowBtn(m_context);
//		mABtnFacebookAccount.setOnClickListener(mClickListener);
//		mABtnTwitterAccount = new SettingArrowBtn(m_context);
//		mABtnTwitterAccount.setOnClickListener(mClickListener);
//		mABtnTumblrAccount = new SettingArrowBtn(m_context);
//		mABtnTumblrAccount.setOnClickListener(mClickListener);
        mABtnComment = new SettingArrowBtn(m_context);
        mABtnComment.setOnClickListener(mClickListener);
        mABtnAbout = new SettingArrowBtn(m_context);
//        mABtnAbout.setOnClickListener(mClickListener);
        mABtnInvite = new SettingArrowBtn(m_context);
        mABtnInvite.setOnClickListener(mClickListener);
        mABtnJieShao = new SettingArrowBtn(m_context);
        mABtnJieShao.setOnClickListener(mClickListener);
        mABtnCheckUpdate = new SettingArrowBtn(m_context);
        mABtnCheckUpdate.setOnClickListener(mClickListener);

//        mABtnFeedback = new SettingArrowBtn(m_context);
//        mABtnFeedback.setOnClickListener(mClickListener);

        mABtnClearCache = new SettingArrowBtn(m_context);
        mABtnClearCache.setOnClickListener(mClickListener);

        mSBtnRememberLens = new SettingSliderBtn(m_context);
        mSBtnRememberLens.setOnSwitchListener(mSwitchListener);

        mSBtnRememberBeautifyMode = new SettingSliderBtn(m_context);
        mSBtnRememberBeautifyMode.setOnSwitchListener(mSwitchListener);


        //mSBtnFSShutter = new SettingSliderBtn(m_context);
        //mSBtnFSShutter.setOnSwitchListener(mSwitchListener);
        //mSBtnAutoUpload = new SettingSliderBtn(m_context);
        //mSBtnAutoUpload.setOnSwitchListener(mSwitchListener);
        //mSBtnJustWifi = new SettingSliderBtn(m_context);
        //mSBtnJustWifi.setOnSwitchListener(mSwitchListener);
        mSBtnAttachDate = new SettingSliderBtn(m_context);
        mSBtnAttachDate.setOnSwitchListener(mSwitchListener);

        mSBtnActualCamera = new SettingSliderBtn(m_context);
        mSBtnActualCamera.setOnSwitchListener(mSwitchListener);

        mSBtnAutoOpenCamera = new SettingSliderBtn(m_context);
        mSBtnAutoOpenCamera.setOnSwitchListener(mSwitchListener);
        mSBtnAutoSaveSD = new SettingSliderBtn(m_context);
        mSBtnAutoSaveSD.setOnSwitchListener(mSwitchListener);
        mSBtnCameraFaceDetect = new SettingSliderBtn(m_context);
        mSBtnCameraFaceDetect.setOnSwitchListener(mSwitchListener);
//        mSBtnAutoBigEye = new SettingSliderBtn(m_context);
//        mSBtnAutoThinFace = new SettingSliderBtn(m_context);
//        mSBtnAutoBigEye.setOnSwitchListener(mSwitchListener);
//        mSBtnAutoThinFace.setOnSwitchListener(mSwitchListener);

        mSBtnNoSound = new SettingSliderBtn(m_context);
        mSBtnNoSound.setOnSwitchListener(mSwitchListener);
        mSBtnTickSound = new SettingSliderBtn(m_context);
        mSBtnTickSound.setOnSwitchListener(mSwitchListener);
        mSBtnFullScreen = new SettingSliderBtn(m_context);
        mSBtnFullScreen.setOnSwitchListener(mSwitchListener);
        mSBtnHDPhoto = new SettingSliderBtn(m_context);
        mSBtnHDPhoto.setOnSwitchListener(mSwitchListener);

        mABtnPhotoWatermarkSet = new SettingArrowBtn(m_context);
        mABtnPhotoWatermarkSet.setOnClickListener(mClickListener);
//        mSBtnAutoQudou = new SettingSliderBtn(m_context);
//        mSBtnAutoQudou.setOnSwitchListener(mSwitchListener);
//        mSBtnAutoQuyandai = new SettingSliderBtn(m_context);
//        mSBtnAutoQuyandai.setOnSwitchListener(mSwitchListener);
//        mSBtnAutoLiangyan = new SettingSliderBtn(m_context);
//        mSBtnAutoLiangyan.setOnSwitchListener(mSwitchListener);
        mSBtnBeautyAutoThinFace = new SettingSliderBtn(m_context);
        mSBtnBeautyAutoThinFace.setOnSwitchListener(mSwitchListener);
        //旋转镜头
        mABtnCameraRotate = new SettingArrowBtn(m_context);
        mABtnCameraRotate.setOnClickListener(mClickListener);
        mABtnReset = new SettingArrowBtn(m_context);
        mABtnReset.setOnClickListener(mClickListener);
//        mABtnTailorMade = new SettingArrowBtn(m_context);
//        mABtnTailorMade.setOnClickListener(mClickListener);

		/*BitmapDrawable bmpDraw = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.frame_bg));
        bmpDraw.setTileModeX(TileMode.REPEAT);
		bmpDraw.setTileModeY(TileMode.REPEAT);
		setBackgroundDrawable(bmpDraw);*/
        setBackgroundColor(0xffffffff);

        RelativeLayout mainFrame = new RelativeLayout(m_context);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mainFrame, fl);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
        RelativeLayout captionBar = new RelativeLayout(m_context);
        captionBar.setId(R.id.settingpage_captionbar);
//        captionBar.setBackgroundResource(R.drawable.framework_top_bar_bg);
        //防止影响下层控件
        captionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        captionBar.setGravity(Gravity.CENTER_VERTICAL);
        mainFrame.addView(captionBar, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        mBtnCancel = new ImageView(m_context);
        mBtnCancel.setImageResource(R.drawable.framework_back_btn);
        captionBar.addView(mBtnCancel, params);
        ImageUtils.AddSkin(getContext(), mBtnCancel);
        mBtnCancel.setOnTouchListener(new OnAnimationClickListener()
        {
            @Override
            public void onAnimationClick(View v)
            {
                onBack();
            }

            @Override
            public void onTouch(View v){}

            @Override
            public void onRelease(View v){}
        });

//        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        mBtnAbout = new ImageButton(m_context);
//        captionBar.addView(mBtnAbout, params);
//        mBtnAbout.setButtonImage(R.drawable.setting_aboutbtn_normal, R.drawable.setting_aboutbtn_press);
//        mBtnAbout.setOnClickListener(mClickListener);

        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTxTopBar = new TextView(m_context);
        mTxTopBar.setText(getContext().getString(R.string.setting_topbar_title));
        mTxTopBar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
        mTxTopBar.setTextColor(0xff333333);
        captionBar.addView(mTxTopBar, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = ShareData.PxToDpi_xhdpi(90);
        mScrollView = new ScrollView(m_context);
        mainFrame.addView(mScrollView, 0, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mContainer = new RelativeLayout(m_context);
        mScrollView.addView(mContainer, params);
//		mContainer.setPadding(0, ShareData.PxToDpi_hdpi(0), 0, 0);


        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mTxCamera = new TextView(m_context);
        mTxCamera.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(32), 0, ShareData.PxToDpi_xhdpi(20));
        mTxCamera.setText(getResources().getString(R.string.setting_title_camera));
        mTxCamera.setTextColor(str_color);
        mTxCamera.setTextSize(TypedValue.COMPLEX_UNIT_DIP, str_size);
        mTxCamera.setId(R.id.settingpage_text_camera);
        mContainer.addView(mTxCamera, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        params.addRule(RelativeLayout.BELOW, R.id.settingpage_text_camera);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        View line1 = new View(getContext());
        line1.setBackgroundColor(0x19000000);
        line1.setId(R.id.settingpage_line1);
        mContainer.addView(line1, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.settingpage_line1);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mSettingCamera = new SettingGroup(m_context);

//        if (support) mSettingCamera.addItem(getResources().getString(R.string.setting_camera_actual_camera), mSBtnActualCamera);
//        mSettingCamera.addItem(getResources().getString(R.string.setting_camera_tailor_made), mABtnTailorMade);
        mSettingCamera.addItem(getResources().getString(R.string.setting_camera_no_sound), mSBtnNoSound);
        mSettingCamera.addItem(getResources().getString(R.string.setting_camera_auto_open_camera), mSBtnAutoOpenCamera);
        mSettingCamera.addItem(getResources().getString(R.string.setting_camera_auto_save_sdcard), mSBtnAutoSaveSD);
		mSettingCamera.addItem(getResources().getString(R.string.setting_camera_tick_sound), mSBtnTickSound);
        mSettingCamera.addItem(getResources().getString(R.string.setting_photo_attach_date), mSBtnAttachDate);

        //mSettingCamera.addItem("全屏快门", mSBtnFSShutter);
//		boolean get_jar = First_ACT.checklocalfiles();
        //if(get_jar)
        mSettingCamera.addItem(getResources().getString(R.string.setting_camera_camera_rotate), mABtnCameraRotate);
        mSettingCamera.addItem(getResources().getString(R.string.setting_camera_reset), mABtnReset);


        //mSettingCamera.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(26), 0);
        mSettingCamera.setId(R.id.settingpage_setting_camera);
        mContainer.addView(mSettingCamera, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.BELOW, R.id.settingpage_setting_camera);
        mTxBeautify = new TextView(m_context);
        mTxBeautify.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(32), 0, ShareData.PxToDpi_xhdpi(20));
        mTxBeautify.setText(getResources().getString(R.string.setting_title_photo));
        mTxBeautify.setTextColor(str_color);
        mTxBeautify.setTextSize(TypedValue.COMPLEX_UNIT_DIP, str_size);
        mTxBeautify.setId(R.id.settingpage_text_beautify);
        mContainer.addView(mTxBeautify, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        params.addRule(RelativeLayout.BELOW, R.id.settingpage_text_beautify);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        View line2 = new View(getContext());
        line2.setBackgroundColor(0x19000000);
        line2.setId(R.id.settingpage_line2);
        mContainer.addView(line2, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.settingpage_line2);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mSettingBeautify = new SettingGroup(m_context);

        //高清模式设置
        if (SysConfig.GetPhotoSize(m_context, true) != SysConfig.GetPhotoSize(m_context, false)) {
            mSettingBeautify.addItem(getResources().getString(R.string.setting_photo_hd_photo), mSBtnHDPhoto);
        }
        //照片水印设置
        mSettingBeautify.addItem(getResources().getString(R.string.setting_photo_watermark_set), mABtnPhotoWatermarkSet);

        mSettingBeautify.addItem(getResources().getString(R.string.setting_photo_remember_beautify_mode), mSBtnRememberBeautifyMode);
        mSettingBeautify.addItem(getResources().getString(R.string.setting_beauty_auto_thinface), mSBtnBeautyAutoThinFace);
//        mSettingBeautify.addItem("自动祛痘", mSBtnAutoQudou);
//        mSettingBeautify.addItem("自动祛眼袋", mSBtnAutoQuyandai);
//        mSettingBeautify.addItem("自动亮眼", mSBtnAutoLiangyan);

//        mSettingBeautify.addItem("自动大眼", mSBtnAutoBigEye);
//        mSettingBeautify.addItem("自动瘦脸", mSBtnAutoThinFace);
//        mSettingBeautify.addItem(getResources().getString(R.string.setting_photo_attach_date), mSBtnAttachDate);


        //mSettingBeautify.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(26), 0);
        mSettingBeautify.setId(R.id.settingpage_setting_beautify);
        mContainer.addView(mSettingBeautify, params);

//        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        params.addRule(RelativeLayout.BELOW, R.id.settingpage_setting_beautify);
//        mTxWeibo = new TextView(m_context);
//        mTxWeibo.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(56), 0, ShareData.PxToDpi_xhdpi(20));
//        mTxWeibo.setText("分享");
//        mTxWeibo.setTextColor(str_color);
//        mTxWeibo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, str_size);
//        mTxWeibo.setId(R.id.settingpage_text_weibo);
//        mContainer.addView(mTxWeibo, params);
//
//        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
//        params.addRule(RelativeLayout.BELOW, R.id.settingpage_text_weibo);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        View line3 = new View(getContext());
//        line3.setBackgroundColor(0x19000000);
//        line3.setId(R.id.settingpage_line3);
//        mContainer.addView(line3, params);
//
//        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        params.addRule(RelativeLayout.BELOW, R.id.settingpage_line3);
//        mSettingWeibo = new SettingGroup(m_context);
//        mSettingWeibo.addItem("POCO账号", mABtnAccount);
//        mSettingWeibo.addItem("新浪微博", mABtnSinaAccount);
////		mSettingWeibo.addItem("腾讯微博", mABtnQQAccount);
////		mSettingWeibo.addItem("人人网", mABtnRenrenAccount);
//        mSettingWeibo.addItem("QQ", mABtnQZoneAccount);
////		mSettingWeibo.addItem("豆瓣", mABtnDouBanAccount);
////		mSettingWeibo.addItem("Facebook", mABtnFacebookAccount);
////		mSettingWeibo.addItem("Twitter", mABtnTwitterAccount);
////		mSettingWeibo.addItem("Tumblr", mABtnTumblrAccount);
//        //mSettingWeibo.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(26), 0);
//        mSettingWeibo.setId(R.id.settingpage_setting_weibo);
//        mContainer.addView(mSettingWeibo, params);

		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//		params.addRule(RelativeLayout.BELOW, R.id.settingpage_setting_weibo);
        params.addRule(RelativeLayout.BELOW, R.id.settingpage_setting_beautify);
		mTxAbout = new TextView(m_context);
		mTxAbout.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(32), 0, ShareData.PxToDpi_xhdpi(20));
		mTxAbout.setText(getResources().getString(R.string.setting_title_other));
		mTxAbout.setTextColor(str_color);
		mTxAbout.setTextSize(TypedValue.COMPLEX_UNIT_DIP, str_size);
		mTxAbout.setId(R.id.settingpage_text_other);
		mContainer.addView(mTxAbout, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        params.addRule(RelativeLayout.BELOW, R.id.settingpage_text_other);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        View line4 = new View(getContext());
        line4.setBackgroundColor(0x19000000);
        line4.setId(R.id.settingpage_line4);
        mContainer.addView(line4, params);

		params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.settingpage_line4);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.bottomMargin = ShareData.PxToDpi_xhdpi(100);
		mSettingAbout = new SettingGroup(m_context);
//		mABtnAbout.setText(Utils.getAppVersion(m_context));
//      mSettingAbout.addItem(getResources().getString(R.string.setting_other_feedback), mABtnFeedback);
        mSettingAbout.addItem(getResources().getString(R.string.setting_other_clear_cache), mABtnClearCache);
        String cache = FileUtils.getCacheSize();
        if(cache == null) cache = "0M";
        mABtnClearCache.setText(cache);
//		mSettingAbout.addItem(getResources().getString(R.string.setting_other_about), mABtnAbout);
		//mSettingAbout.addItem("给此APP评分", mABtnComment);
		//mSettingAbout.addItem("邀请朋友一起使用", mABtnInvite);
		//mSettingAbout.addItem("使用帮助", mABtnJieShao);

//		if(ConfigIni.manualCheckUpdate == true)
//		{
//			mSettingAbout.addItem("检查更新", mABtnCheckUpdate);
//		}
		//mSettingAbout.setPadding(ShareData.PxToDpi_xhdpi(32), ShareData.PxToDpi_xhdpi(20), ShareData.PxToDpi_xhdpi(26), 0);
		mSettingAbout.setId(R.id.settingpage_setting_other);
		mContainer.addView(mSettingAbout, params);
//
//		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		mTxVer = new TextView(getm_context());
//		mTxVer.setPadding(0, 0, ShareData.PxToDpi_hdpi(14), 0);
//		mContainer.addView(mTxVer, params);
    }

    private void setConfigInfo() {
//		switch(mConfigInfo.nFocusMode)
//		{
//			case FocusMode.AUTO:
//				mABtnFocusMode.setText("自动对焦");
//				break;
//			case FocusMode.MANUAL:
//				mABtnFocusMode.setText("手动对焦");
//				break;
//		}
//		switch(mConfigInfo.nFlashMode)
//		{
//			case FlashMode.AUTO:
//				mABtnFlashMode.setText("自动");
//				break;
//			case FlashMode.ON:
//				mABtnFlashMode.setText("已开启");
//				break;
//			case FlashMode.OFF:
//				mABtnFlashMode.setText("已关闭");
//				break;
//		}
//        if (SettingInfoMgr.GetSettingInfo(getContext()).GetPocoNick() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetPocoNick().length() != 0) {
//            mABtnAccount.setText(SettingInfoMgr.GetSettingInfo(getContext()).GetPocoNick());
//        } else if (SettingInfoMgr.GetSettingInfo(getContext()).GetPocoAccount() != null) {
//            mABtnAccount.setText(SettingInfoMgr.GetSettingInfo(getContext()).GetPocoAccount());
//        }
//        if (SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUserName() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUserName().length() != 0) {
//            mABtnSinaAccount.setText(SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUserName());
//        } else if (SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUid() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUid().length() != 0) {
//            mABtnSinaAccount.setText(SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUid());
//        }
//        if (SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneUserName() != null && SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneUserName().length() != 0) {
//            mABtnQZoneAccount.setText(SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneUserName());
//        }

//		mSBtnRememberLens.setSwitchStatus(mConfigInfo.boolRememberLastLens);
        mSBtnAutoSaveSD.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAutoSaveCameraPhotoState());
//		if(mConfigInfo.nfacedetect == 0)
//			mSBtnCameraFaceDetect.setSwitchStatus(false);
//		else if(mConfigInfo.nfacedetect == 1)
//			mSBtnCameraFaceDetect.setSwitchStatus(true);

//        mSBtnAutoBigEye.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAutoBigeye());
//        mSBtnAutoThinFace.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAutoThinface());

        //mSBtnAutoUpload.setSwitchStatus(mConfigInfo.boolAutoUpload);
        //mSBtnJustWifi.setSwitchStatus(mConfigInfo.boolJustWifi);
        mSBtnAttachDate.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAddDateState());

        mSBtnActualCamera.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetActualBeautyState());

        mSBtnAutoOpenCamera.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetOpenCameraState());
        mSBtnNoSound.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetCameraSoundState());
        //mSBtnFSShutter.setSwitchStatus(info.boolFSShutter);
		mSBtnTickSound.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetTickSoundState());
//		mSBtnFullScreen.setSwitchStatus(mConfigInfo.boolFullScreen);
        mSBtnRememberBeautifyMode.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetLastSaveColor());
        mSBtnHDPhoto.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetQualityState());
//        mSBtnAutoQudou.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAutoQudou());
//        mSBtnAutoQuyandai.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAutoQuyandai());
//        mSBtnAutoLiangyan.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAutoLiangyan());
        mSBtnBeautyAutoThinFace.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetBeautyAutoThinface());
    }

    protected OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (v == mBtnCancel) {
                onBack();
            }
//			else if(v == mABtnFocusMode)
//			{
//				String[] items = new String[2];
//				items[0] = "自动对焦";
//				items[1] = "手动对焦";
//				builder.setItems(items, new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						switch(which)
//						{
//							case 0:
//								mConfigInfo.nFocusMode = FocusMode.AUTO;
//								mABtnFocusMode.setText("自动对焦");
//								break;
//							case 1:
//								mConfigInfo.nFocusMode = FocusMode.MANUAL;
//								mABtnFocusMode.setText("手动对焦");
//								break;
//						}
//					}
//				});
//				AlertDialog dlg = builder.create();
//				dlg.show();
//			}
//			else if(v == mABtnFlashMode)
//			{
//				String[] items = new String[3];
//				items[0] = "开";
//				items[1] = "关";
//				items[2] = "自动";
//				builder.setItems(items, new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						switch(which)
//						{
//							case 0:
//								mConfigInfo.nFlashMode = FlashMode.ON;
//								mABtnFlashMode.setText("已开启");
//								break;
//							case 1:
//								mConfigInfo.nFlashMode = FlashMode.OFF;
//								mABtnFlashMode.setText("已关闭");
//								break;
//							case 2:
//								mConfigInfo.nFlashMode = FlashMode.AUTO;
//								mABtnFlashMode.setText("自动");
//								break;
//						}
//					}
//				});
//				AlertDialog dlg = builder.create();
//				dlg.show();
//			}
//            else if (v == mABtnAccount) {
//                if (checkPocoBindingStatus(getContext())) {
//                    AlertDialog alert = new AlertDialog.Builder(getContext()).create();
//                    alert.setTitle("提示");
//                    alert.setMessage("帐号已绑定,是否要取消绑定?");
//                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            clearPocoConfigure(getContext());
//                            mABtnAccount.setText("");
//                        }
//                    });
//                    alert.setButton(AlertDialog.BUTTON_NEGATIVE, "否", (DialogInterface.OnClickListener) null);
//                    alert.show();
//                } else {
//                    if (checkSinaBindingStatus(getContext()) || checkQzoneBindingStatus(getContext())) {
//                        bindPoco(true);
//                    } else {
//                        bindPoco(false);
//                    }
//                }
//            } else if (v == mABtnSinaAccount) {
//                if (checkSinaBindingStatus(getContext())) {
//                    AlertDialog alert = new AlertDialog.Builder(getContext()).create();
//                    alert.setTitle("提示");
//                    alert.setMessage("帐号已绑定,是否要取消绑定?");
//                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            clearSinaConfigure(getContext());
//                            mABtnSinaAccount.setText("");
//                        }
//                    });
//                    alert.setButton(AlertDialog.BUTTON_NEGATIVE, "否", (DialogInterface.OnClickListener) null);
//                    alert.show();
//                } else {
//                    bindSina();
//                }
//            } else if (v == mABtnQZoneAccount) {
//                if (checkQzoneBindingStatus(getContext())) {
//                    AlertDialog alert = new AlertDialog.Builder(getContext()).create();
//                    alert.setTitle("提示");
//                    alert.setMessage("帐号已绑定,是否要取消绑定?");
//                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            clearQzoneConfigure(getContext());
//                            mABtnQZoneAccount.setText("");
//                        }
//                    });
//                    alert.setButton(AlertDialog.BUTTON_NEGATIVE, "否", (DialogInterface.OnClickListener) null);
//                    alert.show();
//                } else {
//                    bindQzone();
//                }
//            }
            else if (v == mABtnAbout || v == mBtnAbout) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_关于我们);
                m_site.OnAbout();
            }
//			else if(v == mABtnCheckUpdate)
//			{
//				//TongJi2.add_using_count("设置/点击检查更新");
//				UpdateAPK.start(PocoCamera.main, true);
//			}
            else if (v == mABtnFeedback) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_帮助与反馈);
                MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_帮助与反馈);
                OpenQuestionPage(getContext());
//		        /**
//		         * 获取剩余内存
//		         */
//				TongJi2.AddCountByRes(getContext(), R.integer.设置_问题反馈);
//				System.gc();
////				ActivityManager am = (ActivityManager)act.getSystemService(Context.ACTIVITY_SERVICE);
////		        MemoryInfo mi = new MemoryInfo();
////		        am.getMemoryInfo(mi);
////		        String freeMemory= Formatter.formatFileSize(act.getBaseContext(), mi.availMem);
//				//把freeMemory换成了javaHeap,以前那个无参考价值;
//				Long javaHeap=Runtime.getRuntime().maxMemory();
//				String freeMemory=javaHeap/1024/1024+"MB";
//				Bundle xbundle=new Bundle();
//				xbundle.putString("appname", "poco_beautycamera_android");
//				xbundle.putString("client_ver", Utils.getAppVersion(context).trim());
//				xbundle.putString("phone_type", Build.MODEL);
//				xbundle.putString("os_ver", Build.VERSION.RELEASE);
//				xbundle.putString("memory", freeMemory);
//				String url="http://world.poco.cn/app/feedback/index.php?";
//				String loadUrl=new StringBuilder(url).append(encodeUrl(xbundle)).toString();
//				PLog.out("debug", "loadUrl:"+loadUrl);
//				/**
//				 * 默认浏览器
//				 */
//				Uri uri=Uri.parse(loadUrl);
//				Intent intent=new Intent(Intent.ACTION_VIEW, uri);
//				context.startActivity(intent);
////				Intent intent = new Intent(context, FeedbackActivity.class);
////				intent.setData(Uri.parse(url));
////				context.startActivity(intent);
            }
//			else if(v == mABtnComment)
//			{
//				Uri uri = Uri.parse(Constant.ROOT_URL+"/comment/");
//				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//				act.startActivity(intent);
//			}
//			else if(v == mABtnInvite)
//			{
//				Intent smsIntent=new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
//				smsIntent.putExtra("sms_body", Constant.STR_INVITE);
//				act.startActivity(smsIntent);
//			}
            else if(v == mABtnClearCache)
            {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_清除缓存);
                MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_清除缓存);
                final Dialog dlg = new Dialog(getContext(), R.style.notitledialog);
                final DialogView2 view2 = new DialogView2(getContext(), getGlassBackground());
                view2.setInfo(getContext().getResources().getString(R.string.setting_clear_cache_text), getContext().getResources().getString(R.string.setting_clear_cache_button), new SharePage.DialogListener()
                {
                    @Override
                    public void onClick(int view)
                    {
                        if(view == DialogView2.VIEW_BUTTON)
                        {
                            MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_清除缓存);
                            FileUtil.deleteSDFile(FolderMgr.getInstance().PATH_PLAYER_CACHE);
                            FileUtils.clearCache();
                            String cache = FileUtils.getCacheSize();
                            if(cache == null) cache = "0M";
                            mABtnClearCache.setText(cache);
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.setting_clear_cache_toast), Toast.LENGTH_SHORT).show();
                        }
                        dlg.dismiss();
                    }
                });
                dlg.setContentView(view2);
                dlg.setCanceledOnTouchOutside(false);
                Window w = dlg.getWindow();
                w.setWindowAnimations(R.style.fullDialog);
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
            else if (v == mABtnJieShao) {
                TongJi2.AddCountByRes(getContext(), R.integer.设置_新功能介绍);
                PackageManager pm = context.getPackageManager();
                if (pm != null) {
                    PackageInfo pi;
                    try {
                        pi = pm.getPackageInfo(context.getPackageName(), 0);
                        if (pi != null) {
                            Uri uri = Uri.parse("http://world.poco.cn/wo/tag_list.php?cat_id=9&tag=%cd%e6%d7%aa%c3%c0%c8%cb%cf%e0%bb%fa&from=beautycamera");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            getContext().startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (v == mABtnCameraRotate) {
                m_site.OnFixCamera();
            } else if (v == mABtnReset) {
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_初始化为默认);
                MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_初始化为默认);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle(getResources().getString(R.string.tips));
                builder2.setMessage(getResources().getString(R.string.setting_text_camera_reset));
                // 设置内容
                builder2.setPositiveButton(getResources().getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CameraConfig.getInstance().initAll(getContext());
                        CameraConfig.getInstance().resetAllData();
                        SettingInfoMgr.GetSettingInfo(getContext()).SetActualBeautyState(true);
                        SettingInfoMgr.GetSettingInfo(getContext()).SetOpenCameraState(false);
                        SettingInfoMgr.GetSettingInfo(getContext()).SetAutoSaveCameraPhotoState(true);
                        SettingInfoMgr.GetSettingInfo(getContext()).SetCameraSoundState(true);
                        SettingInfoMgr.GetSettingInfo(getContext()).setTickSoundState(false);
                        mSBtnActualCamera.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetActualBeautyState());
                        mSBtnAutoOpenCamera.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetOpenCameraState());
                        mSBtnAutoSaveSD.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetAutoSaveCameraPhotoState());
                        mSBtnNoSound.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetCameraSoundState());
                        mSBtnTickSound.setSwitchStatus(SettingInfoMgr.GetSettingInfo(getContext()).GetTickSoundState());
                        Toast.makeText(getContext(), getResources().getString(R.string.setting_reset_complete), Toast.LENGTH_SHORT).show();
                    }
                });
                builder2.setNegativeButton(getResources().getString(R.string.cancel), null);
                builder2.create().show();
            } else if (v == mABtnTailorMade){
                // 智能美形
                TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_美形定制);
                MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_美形定制);
                TagMgr.ResetTag(getContext(), Tags.CAMERA_TAILOR_MADE_FLAG);
                TagMgr.Save(getContext());
                m_site.OnTailorMadeCamera();
            } else if (v == mABtnPhotoWatermarkSet) {
                //照片水印设置
                MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_水印设置);
                m_site.OnPhotoWatermarkSet(getContext());
            }
        }
    };

//	protected void show_camera_rotate_dial()
//	{
//		SettingRotateDialog srd = new SettingRotateDialog(mcontext);
//		srd.show();
//	}

    public static String encodeUrl(Bundle parameters) {
        if (parameters == null)
            return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        String key;
        for (Iterator<String> iterator = parameters.keySet().iterator(); iterator.hasNext(); ) {
            key = (String) iterator.next();
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(new StringBuilder(String.valueOf(key)).append("=").append(parameters.getString(key)).toString());
        }
        return sb.toString();
    }

    protected SettingSliderBtn.OnSwitchListener mSwitchListener = new SettingSliderBtn.OnSwitchListener() {
        @Override
        public void onSwitch(View v, final boolean on) {
//			if(v == mSBtnRememberLens)
//			{
//				mConfigInfo.boolRememberLastLens = on;
//			}
/*			else if(v == mSBtnAutoUpload)
			{
				mConfigInfo.boolAutoUpload = on;
			}*/
			/*else if(v == mSBtnJustWifi)
			{
				mConfigInfo.boolJustWifi = on;
			}*/
            if (v == mSBtnAttachDate) {
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_照片日期水印开);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_照片日期水印开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_照片日期水印关);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_照片日期水印关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).SetAddDateState(on);
            } else if (v == mSBtnActualCamera) {
                SettingInfoMgr.GetSettingInfo(getContext()).SetActualBeautyState(on);
            } else if (v == mSBtnAutoOpenCamera) {
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_直接开启镜头开);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_直接开启镜头开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_直接开启镜头关);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_直接开启镜头关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).SetOpenCameraState(on);
            } else if (v == mSBtnAutoSaveSD) {
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_保存原图开);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_保存原图开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_保存原图关);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_保存原图关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).SetAutoSaveCameraPhotoState(on);
            }
//			else if(v == mSBtnCameraFaceDetect)
//			{
//				if(on)
//					mConfigInfo.nfacedetect = 1;
//				else
//					mConfigInfo.nfacedetect = 0;
//			}
//            else if (v == mSBtnAutoBigEye) {
//                SettingInfoMgr.GetSettingInfo(getContext()).SetAutoBigeye(on);
//            } else if (v == mSBtnAutoThinFace) {
//                SettingInfoMgr.GetSettingInfo(getContext()).SetAutoThinface(on);
//            }
			else if(v== mSBtnTickSound)
			{
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_读秒声音开);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_读秒声音开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_读秒声音关);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_读秒声音关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).setTickSoundState(on);
			}
//			else if(v== mSBtnFullScreen)
//			{
//				mConfigInfo.boolFullScreen = on;
//			}
            else if (v == mSBtnNoSound) {
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_快门声音开);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_快门声音开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_快门声音关);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_快门声音关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).SetCameraSoundState(on);
//                if (on) {
//                    //对话框之作提示用,代码不需要放进去
//                    AlertDialog.Builder builder = new AlertDialog.Builder(m_context);
//                    builder.setTitle("提示");
//                    builder.setMessage("无声拍照只对部分机型有效.");
//                    // 设置内容
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which)
//                        {
//                        }
//                    });
//
//                    builder.create().show();
//                }
            } else if (v == mSBtnRememberBeautifyMode) {
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_记住上一次美颜模式开);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_记住上一次美颜模式开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_记住上一次美颜模式关);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_记住上一次美颜模式关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).SetSaveLastColor(on);
            }
//            else if (v == mSBtnAutoQudou) {
//                SettingInfoMgr.GetSettingInfo(getContext()).SetAutoQudou(on);
//            } else if (v == mSBtnAutoQuyandai) {
//                SettingInfoMgr.GetSettingInfo(getContext()).SetAutoQuyandai(on);
//            } else if (v == mSBtnAutoLiangyan) {
//                SettingInfoMgr.GetSettingInfo(getContext()).SetAutoLiangyan(on);
//            }
            else if (v == mSBtnHDPhoto) {
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_安卓高清画质开);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_安卓高清画质开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_安卓高清画质关);
                    TongJi2.AddCountByRes(getContext(), R.integer.菜单_设置_安卓高清画质关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).SetQualityState(on);
            }
            else if (v == mSBtnBeautyAutoThinFace)
            {
                if(on)
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_美颜时自动瘦脸开);
                }
                else
                {
                    MyBeautyStat.onClickByRes(R.string.个人中心_通用设置_主页面_美颜时自动瘦脸关);
                }
                SettingInfoMgr.GetSettingInfo(getContext()).SetBeautyAutoThinface(on);
            }
        }
    };

//    private void bindPoco(boolean bindWithPartner) {
//        if (bindWithPartner) {
//            bindPocoByOtherAccount();
//        } else {
//            LoginDialog dlg = new LoginDialog(getContext(), R.style.dialog);
//            dlg.setOnLoginOkListener(new LoginDialog.OnLoginOkListener() {
//                @Override
//                public void onLoginOk(int type, String account, String psw, String id, String nick) {
//                    if (type == SharePage.POCO) {
//                        SettingInfoMgr.GetSettingInfo(getContext()).SetPocoId(id);
//                        SettingInfoMgr.GetSettingInfo(getContext()).SetPocoPassword(SharePage.md5Encryption(psw));
//                        SettingInfoMgr.GetSettingInfo(getContext()).SetPocoAccount(account);
//                        if (nick != null && nick.length() > 0) {
//                            SettingInfoMgr.GetSettingInfo(getContext()).SetPocoNick(nick);
//                            mABtnAccount.setText(nick);
//                        } else {
//                            SettingInfoMgr.GetSettingInfo(getContext()).SetPocoNick(account);
//                            mABtnAccount.setText(account);
//                        }
//                    } else {
//                        switch (type) {
//                            case SharePage.SINA:
//                                if (checkSinaBindingStatus(getContext())) {
//                                    bindPocoByOtherAccount("sina", SettingInfoMgr.GetSettingInfo(getContext()).GetSinaUid(), SettingInfoMgr.GetSettingInfo(getContext()).GetSinaAccessToken(), SettingInfoMgr.GetSettingInfo(getContext()).GetSinaAccessToken());
//                                } else {
//                                    bindByOtherAccount = type;
//                                    bindSina();
//                                }
//                                break;
//
//                            case SharePage.QZONE:
//                                if (checkQzoneBindingStatus(getContext())) {
//                                    bindPocoByOtherAccount("qzone", SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneOpenid(), SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken(), SettingInfoMgr.GetSettingInfo(getContext()).GetQzoneAccessToken());
//                                } else {
//                                    bindByOtherAccount = type;
//                                    bindQzone();
//                                }
//                                break;
//
//                            default:
//                                bindByOtherAccount = -1;
//                                break;
//                        }
//                    }
//                }
//            });
//            dlg.show();
//        }
//    }

//    private void bindPocoByOtherAccount() {
//        final ProgressDialog mProgressDialog = ProgressDialog.show(getContext(), "", "绑定中...");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//
//        new Thread() {
//            public void run() {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String account = SharePage.makeBindPocoAccount(getContext());
//                if (account != null) {
//                    params.put("psid", account);
//                } else {
//                    mProgressDialog.dismiss();
//                    return;
//                }
//                params.put("appname", "poco_beautycamera_android");
//                params.put("ver", SysConfig.GetAppVerNoSuffix(getContext()));
//                if (mPoco == null) mPoco = new PocoBlog(getContext());
//                final PocoBlog.BindPocoItem item = mPoco.getPocoBindInfo(params);
//                if (item != null) {
//                    Bitmap bmp = SharePage.downloadImage(item.iconURL);
//                    mProgressDialog.dismiss();
//                    if (bmp != null && !bmp.isRecycled()) {
//                        final Bitmap icon = SharePage.makeBindPocoDialogThumb(bmp);
//                        SettingPage.this.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                openBindPocoDialog(item, icon);
//                            }
//                        });
//                    } else {
//                        SettingPage.this.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                bindPoco(false);
//                            }
//                        });
//                    }
//                } else {
//                    mProgressDialog.dismiss();
//                    SettingPage.this.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            bindPoco(false);
//                        }
//                    });
//                }
//            }
//        }.start();
//    }

//    private void openBindPocoDialog(final PocoBlog.BindPocoItem item, Bitmap icon) {
//        BindPocoDialog dialog = new BindPocoDialog(getContext(), R.style.dialog, item.tips, icon, item.nickName, new SharePage.DialogListener() {
//            @Override
//            public void onClick(int view) {
//                switch (view) {
//                    case BindPocoDialog.ID_DETERMINE:
//                        SettingInfoMgr.GetSettingInfo(getContext()).SetPocoId(item.pocoID);
//                        SettingInfoMgr.GetSettingInfo(getContext()).SetPocoPassword(item.pocoPassword);
//                        SettingInfoMgr.GetSettingInfo(getContext()).SetPocoAccount(item.pocoID);
//                        if (item.nickName != null && item.nickName.length() > 0) {
//                            SettingInfoMgr.GetSettingInfo(getContext()).SetPocoNick(item.nickName);
//                            mABtnAccount.setText(item.nickName);
//                        } else {
//                            SettingInfoMgr.GetSettingInfo(getContext()).SetPocoNick(item.pocoID);
//                            mABtnAccount.setText(item.pocoID);
//                        }
//                        break;
//
//                    case BindPocoDialog.ID_OTHER:
//                        bindPoco(false);
//                        break;
//                }
//            }
//        });
//        dialog.show();
//    }

//    private void bindPocoByOtherAccount(final String partner, final String uid, final String token, final String password) {
//        final ProgressDialog mProgressDialog = ProgressDialog.show(getContext(), "", "绑定Poco微博中...");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//
//        new Thread() {
//            public void run() {
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("ctype", "poco_beautycamera_android");
//                params.put("partner", partner);
//                params.put("sid", uid);
//                params.put("token", token);
//                params.put("secret", password);
//                params.put("atype", "oauth");
//                if (mPoco == null) mPoco = new PocoBlog(getContext());
//                final PocoBlog.BindPocoItem item = mPoco.getPocoBindInfo2(params);
//                if (item != null) {
//                    if (mProgressDialog != null) {
//                        mProgressDialog.dismiss();
//                    }
//                    SettingPage.this.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            SettingInfoMgr.GetSettingInfo(getContext()).SetPocoId(item.pocoID);
//                            SettingInfoMgr.GetSettingInfo(getContext()).SetPocoPassword(item.pocoPassword);
//                            SettingInfoMgr.GetSettingInfo(getContext()).SetPocoAccount(item.pocoID);
//                            if (item.nickName != null && item.nickName.length() > 0) {
//                                SettingInfoMgr.GetSettingInfo(getContext()).SetPocoNick(item.nickName);
//                                mABtnAccount.setText(item.nickName);
//                            } else {
//                                SettingInfoMgr.GetSettingInfo(getContext()).SetPocoNick(item.pocoID);
//                                mABtnAccount.setText(item.pocoID);
//                            }
//                        }
//                    });
//                } else {
//                    if (mProgressDialog != null) {
//                        mProgressDialog.dismiss();
//                    }
//                    SettingPage.this.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getContext(), "绑定失败", Toast.LENGTH_LONG).show();
//                            bindPoco(false);
//                        }
//                    });
//                }
//            }
//        }.start();
//    }

//    public void bindSina() {
//        TongJi2.AddCountByRes(getContext(), R.integer.分享_新浪微博_绑定);
//
//        if (mSina == null) mSina = new SinaBlog(getContext());
//
//        mSina.bindSinaWithSSO(new SinaBlog.BindSinaCallback() {
//            @Override
//            public void success(final String accessToken, String expiresIn, String uid,
//                                String userName, String nickName) {
//                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaAccessToken(accessToken);
//                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUid(uid);
//                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaExpiresIn(expiresIn);
//                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
//                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUserName(userName);
//                SettingInfoMgr.GetSettingInfo(getContext()).SetSinaUserNick(nickName);
//                if (nickName != null) mABtnSinaAccount.setText(nickName);
//
//                if (bindByOtherAccount == SharePage.SINA) {
//                    bindByOtherAccount = -1;
//                    bindPocoByOtherAccount("sina", uid, accessToken, accessToken);
//                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSina.flowerCameraSinaWeibo(Constant.sinaUserId, accessToken);
//                    }
//                }).start();
//            }
//
//            @Override
//            public void fail() {
//                switch (mSina.LAST_ERROR) {
//                    case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
//                        SharePage.msgBox(getContext(), "还没有安装最新的新浪微博客户端，需要安装后才能绑定");
//                        break;
//
//                    default:
//                        SharePage.msgBox(getContext(), "绑定新浪微博失败");
//                        break;
//                }
//            }
//        });
//    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSina != null) mSina.onActivityResult(requestCode, resultCode, data, -1);
        if (mQzone != null) mQzone.onActivityResult(requestCode, resultCode, data);
        return super.onActivityResult(requestCode, resultCode, data);
    }

//    public void bindQzone() {
//        final boolean bindPoco = (bindByOtherAccount == SharePage.QZONE);
//        bindByOtherAccount = -1;
//
//        if (mQzone == null) mQzone = new QzoneBlog2(getContext());
//
//        mQzone.bindQzoneWithSDK(new QzoneBlog2.BindQzoneCallback() {
//            @Override
//            public void success(String accessToken, String expiresIn, String openId, String nickName) {
//                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneAccessToken(accessToken);
//                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneOpenid(openId);
//                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneExpiresIn(expiresIn);
//                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneSaveTime(String.valueOf(System.currentTimeMillis() / 1000));
//                SettingInfoMgr.GetSettingInfo(getContext()).SetQzoneUserName(nickName);
//
//                if (nickName != null) mABtnQZoneAccount.setText(nickName);
//                if (bindPoco) bindPocoByOtherAccount("qzone", openId, accessToken, accessToken);
//            }
//
//            @Override
//            public void fail() {
//                switch (mQzone.LAST_ERROR) {
//                    case WeiboInfo.BLOG_INFO_CLIENT_NO_INSTALL:
//                        AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
//                        dlg.setTitle("提示");
//                        dlg.setMessage("还没有安装最新手机QQ，需要安装后才能绑定");
//                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (DialogInterface.OnClickListener) null);
//                        dlg.show();
//                        break;
//
//                    default:
//                        SharePage.msgBox(getContext(), "绑定QQ空间失败");
//                        break;
//                }
//            }
//        });
//    }

    public static void OpenQuestionPage(Context context) {
        TongJi2.AddCountByRes(context, R.integer.设置_问题反馈);
        //把freeMemory换成了javaHeap,以前那个无参考价值;
        Long javaHeap = Runtime.getRuntime().maxMemory();
        String freeMemory = javaHeap / 1024 / 1024 + "MB";
        Bundle xbundle = new Bundle();
        xbundle.putString("appname", "beauty_camera_android");
        xbundle.putString("client_ver", SysConfig.GetAppVer(context).trim());
        xbundle.putString("phone_type", Build.MODEL);
        xbundle.putString("os_ver", Build.VERSION.RELEASE);
        xbundle.putString("memory", freeMemory);
        String phone = SettingInfoMgr.GetSettingInfo(context).GetPoco2Phone();
        if (phone != null) {
            xbundle.putString("phone", phone);
        }
        String id = SettingInfoMgr.GetSettingInfo(context).GetPoco2Id(false);
        if (id != null) {
            xbundle.putString("user_id", id);
        }
//        String url = "http://world.poco.cn/app/feedback/index.php?";
        String url = AppInterface.GetInstance(context).getQAUrl()+"&";
        String loadUrl = new StringBuilder(url).append(SettingPage.encodeUrl(xbundle)).toString();
        /**
         * 默认浏览器
         */
        CommonUtils.OpenBrowser(context, loadUrl);
		/*Uri uri = Uri.parse(loadUrl);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		context.startActivity(intent);*/
    }

    /**
     * 检测新浪微博绑定状态
     *
     * @param context context
     * @return true为已绑定，false为未绑定
     */
    public static boolean checkSinaBindingStatus(Context context) {
        if (SettingInfoMgr.GetSettingInfo(context).GetSinaAccessToken() != null &&
                SettingInfoMgr.GetSettingInfo(context).GetSinaAccessToken().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 检测QQ空间绑定状态
     *
     * @param context context
     * @return true为已绑定，false为未绑定
     */
    public static boolean checkQzoneBindingStatus(Context context) {
        if (SettingInfoMgr.GetSettingInfo(context).GetQzoneAccessToken() != null &&
                SettingInfoMgr.GetSettingInfo(context).GetQzoneAccessToken().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 解除新浪微博绑定
     *
     * @param context context
     */
    public static void clearSinaConfigure(Context context) {
        SettingInfoMgr.GetSettingInfo(context).SetSinaAccessToken(null);
        SettingInfoMgr.GetSettingInfo(context).SetSinaUid(null);
        SettingInfoMgr.GetSettingInfo(context).SetSinaExpiresIn(null);
        SettingInfoMgr.GetSettingInfo(context).SetSinaSaveTime(null);
        SettingInfoMgr.GetSettingInfo(context).SetSinaUserName(null);
        SettingInfoMgr.GetSettingInfo(context).SetSinaUserNick(null);
    }

    /**
     * 解除QQ空间绑定
     *
     * @param context context
     */
    public static void clearQzoneConfigure(Context context) {
        SettingInfoMgr.GetSettingInfo(context).SetQzoneAccessToken(null);
        SettingInfoMgr.GetSettingInfo(context).SetQzoneOpenid(null);
        SettingInfoMgr.GetSettingInfo(context).SetQzoneExpiresIn(null);
        SettingInfoMgr.GetSettingInfo(context).SetQzoneSaveTime(null);
        SettingInfoMgr.GetSettingInfo(context).SetQzoneUserName(null);
    }

    @Override
    public void onBack() {
        SettingInfoMgr.Save(getContext());
        if (m_site != null) m_site.OnBack();
    }

    public Bitmap getGlassBackground()
    {
        if(m_shareDialogBG == null || m_shareDialogBG.isRecycled())
        {
            m_shareDialogBG = SharePage.makeGlassBackground(SharePage.screenCapture(getContext(), ShareData.m_screenWidth, ShareData.m_screenHeight));
        }
        return m_shareDialogBG;
    }
}

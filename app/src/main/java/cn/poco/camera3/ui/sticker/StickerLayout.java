package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.RecomDisplayMgr;
import cn.poco.camera.CameraConfig;
import cn.poco.camera3.UnLockUIListener;
import cn.poco.camera3.cb.sticker.StickerInnerListener;
import cn.poco.camera3.cb.sticker.StickerUIListener;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.StickerImageViewConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.info.sticker.LabelInfo;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.mgr.StickerResMgr;
import cn.poco.camera3.ui.ShadowSeekBar;
import cn.poco.camera3.ui.drawable.StickerBKDrawable;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.NetworkStateChangeReceiver;
import cn.poco.resource.BaseRes;
import cn.poco.resource.LockRes;
import cn.poco.resource.VideoStickerGroupResRedDotMrg2;
import cn.poco.resource.VideoStickerRes;
import cn.poco.resource.VideoStickerResRedDotMgr2;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.utils.DrawableUtils;
import my.beautyCamera.R;

/**
 * 贴纸 + 标签
 * Created by Gxx on 2017/10/12.
 */

public class StickerLayout extends LinearLayout implements StickerInnerListener, View.OnClickListener, NetworkStateChangeReceiver.NetworkStateChangeListener
{
    private StickerBKDrawable mBKDrawable;
    private FrameLayout mStickerSoundFr;
    private ImageView mStickerFoldView;
    private ImageView mMusicIcon;
    private LabelView mLabel;
    private StickerView mSticker;
    private ShadowSeekBar mMakeupSeekbar;

    private int MSG_PARSE;
    private StickerUIListener mStickerUIListener;
    private HandlerThread mHandlerThread;
    private StickerZipParseHelper.ParseHandler mStickerHandler;
    private Handler mUiHandler;

    // unlock
    private FrameLayout mPopFrameView;
    protected RecomDisplayMgr mUnlockView;
    private boolean mShowUnlockView;
    private UnLockUIListener mUnLockUIListener;
    private RecomDisplayMgr.ExCallback mUnLockCallback;

    //network manager
    private Toast mToast;
    protected ConnectivityManager mConnectivityManager;
    protected NetworkStateChangeReceiver mNetworkStateChangeReceiver;

    public StickerLayout(Context context)
    {
        super(context);
        setOrientation(VERTICAL);

        StickerImageViewConfig.init(context);
        StickerResMgr.getInstance().init(context);
        StickerResMgr.getInstance().setInitDataCB(this);
        mBKDrawable = new StickerBKDrawable();

        initCB(context);

        initView(context);
    }

    private void initCB(Context context)
    {
        //network
        mConnectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkStateChangeReceiver = new NetworkStateChangeReceiver();
        mNetworkStateChangeReceiver.setNetworkStateChangeListener(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(mNetworkStateChangeReceiver, filter);

        mUnLockCallback = new RecomDisplayMgr.ExCallback()
        {
            @Override
            public void onWXCancel()
            {
                if (mUnLockUIListener != null)
                {
                    mUnLockUIListener.closeUnLockView();
                }
            }

            @Override
            public void UnlockSuccess(BaseRes res)
            {
                CloseUnlockView();

                if (res != null)
                {
                    if (res instanceof LockRes)
                    {
                        boolean isNew = VideoStickerResRedDotMgr2.getInstance().hasMarkFlag(getContext(), res.m_id);
                        if (isNew)
                        {
                            VideoStickerResRedDotMgr2.getInstance().markResFlag(getContext(), res.m_id);
                        }
                        //添加已解锁标记
                        TagMgr.SetTag(getContext(), Tags.CAMERA_VIDEO_FACE_UNLOCK_ID_FLAG + res.m_id);
                        StickerInfo info = StickerResMgr.getInstance().getStickerInfoByID(res.m_id);
                        if (info != null)
                        {
                            info.mStatus = StatusMgr.Type.DOWN_LOADING;
                            info.mProgress = 0;
                            StickerResMgr.getInstance().notifyPagerViewDataChange(info);
                            StickerResMgr.getInstance().DownloadRes(info);
                        }
                    }
                }
            }

            @Override
            public void OnCloseBtn()
            {
                mShowUnlockView = false;
            }

            @Override
            public void OnBtn(int state)
            {

            }

            @Override
            public void OnClose()
            {
                mShowUnlockView = false;
                if (mUnLockUIListener != null)
                {
                    mUnLockUIListener.closeUnLockView();
                }
            }

            @Override
            public void OnLogin()
            {
                if (mUnLockUIListener != null)
                {
                    mUnLockUIListener.onUserLogin();
                }
            }
        };

        mUiHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == MSG_PARSE)
                {
                    StickerZipParseHelper.ParseHandler.ParseObj obj = (StickerZipParseHelper.ParseHandler.ParseObj) msg.obj;
                    VideoStickerRes res = obj.res;
                    boolean repeat = obj.repeat;
                    boolean isTabChanged = obj.isTabChanged;
                    boolean isMakeup = obj.isMakeupRes;

                    showMakeupSeekBar(isMakeup);
                    // 先回调再缓存
                    if (mStickerUIListener != null)
                    {
                        mStickerUIListener.onSelectSticker(res, repeat, isTabChanged);
                    }
                    StickerZipParseHelper.addCache(res);
                }
            }
        };
        MSG_PARSE = StickerZipParseHelper.ParseHandler.MSG_PARSE;
        mHandlerThread = new HandlerThread("sticker_handler_thread");
        mHandlerThread.start();
        mStickerHandler = new StickerZipParseHelper.ParseHandler(mHandlerThread.getLooper(), context, mUiHandler);
    }

    private void sendStickerParseMsg(StickerZipParseHelper.ParseHandler.ParseObj obj)
    {
        if (obj == null) return;

        mStickerHandler.removeMessages(MSG_PARSE);
        mStickerHandler.setNewParse(mStickerHandler.isParse());

        Message msg = mStickerHandler.obtainMessage();
        msg.what = MSG_PARSE;
        msg.obj = obj;
        mStickerHandler.sendMessage(msg);
    }

    private void initView(Context context)
    {
        mStickerSoundFr = new FrameLayout(context);
        mStickerSoundFr.setOnClickListener(this);
        LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(mStickerSoundFr, params);
        {
            // 彩妆分类seekbar
            mMakeupSeekbar = new ShadowSeekBar(context);
            mMakeupSeekbar.setVisibility(GONE);
            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(588), FrameLayout.LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.BOTTOM;
            // 设计图 白色控制点距列表top 40px，控件默认是 60px，所以距底 14px，大概足够
            fp.bottomMargin = CameraPercentUtil.WidthPxToPercent(14);
            mStickerSoundFr.addView(mMakeupSeekbar, fp);

            //收缩素材icon
            mStickerFoldView = new ImageView(context);
            mStickerFoldView.setScaleType(ImageView.ScaleType.CENTER);
            mStickerFoldView.setOnClickListener(this);
            fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.END | Gravity.BOTTOM;
            fp.rightMargin = CameraPercentUtil.WidthPxToPercent(14);
            fp.bottomMargin = CameraPercentUtil.WidthPxToPercent(18);
            mStickerSoundFr.addView(mStickerFoldView, fp);

            //贴纸音效icon
            mMusicIcon = new ImageView(context);
            mMusicIcon.setScaleType(ImageView.ScaleType.CENTER);
            mMusicIcon.setBackgroundResource(R.drawable.sticker_sound_mute_bg);
            mMusicIcon.setImageBitmap(ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_sound_unmute)));
            mMusicIcon.setVisibility(GONE);
            mMusicIcon.setOnClickListener(this);
            fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fp.gravity = Gravity.BOTTOM;
            fp.leftMargin = CameraPercentUtil.WidthPxToPercent(26);
            fp.bottomMargin = CameraPercentUtil.WidthPxToPercent(18);
            mStickerSoundFr.addView(mMusicIcon, fp);
        }

        FrameLayout contentView = new FrameLayout(context);
        contentView.setBackgroundDrawable(mBKDrawable);
        params = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(460));
        addView(contentView, params);
        {
            mLabel = new LabelView(context);
            mLabel.setStickerDataHelper(this);
            FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(80));
            contentView.addView(mLabel, fp);

            mSticker = new StickerView(context);
            mSticker.setStickerDataHelper(this);
            fp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CameraPercentUtil.HeightPxToPercent(380));
            fp.gravity = Gravity.BOTTOM;
            contentView.addView(mSticker, fp);
        }
    }

    public void registerStickerResMgrCB()
    {
        StickerResMgr.getInstance().setInitDataCB(this);
        if (mSticker != null)
        {
            mSticker.registerStickerResMgrCB();
        }
    }

    public void setUIConfig(CameraUIConfig config)
    {
        float ratio = config.GetPreviewRatio();
        boolean showWhiteBK = (ratio == CameraConfig.PreviewRatio.Ratio_1_1 || ratio == CameraConfig.PreviewRatio.Ratio_9_16);

        showWhiteBKDrawable(showWhiteBK);
    }

    public void SetPopFrameView(FrameLayout view)
    {
        mPopFrameView = view;
    }

    public void setUnLockUIListener(UnLockUIListener listener)
    {
        mUnLockUIListener = listener;
    }

    public boolean CloseUnlockView()
    {
        if (mShowUnlockView && mUnlockView != null && mUnlockView.IsShow())
        {
            mUnlockView.OnCancel(true);
            mUnlockView.ClearAllaa();
            mUnlockView = null;
            mShowUnlockView = false;
            return true;
        }
        return false;
    }

    public void UpdateCredit()
    {
        if (mUnlockView != null)
        {
            mUnlockView.UpdateCredit();
        }
    }

    public void showMakeupSeekBar(boolean show)
    {
        if (mMakeupSeekbar != null)
        {
            mMakeupSeekbar.setVisibility(show ? VISIBLE : GONE);
        }
    }

    public boolean isShowUnlockView()
    {
        return mShowUnlockView;
    }

    public void setStickerSoundMute(boolean mute)
    {
        if (mMusicIcon != null)
        {
            if (mute)
            {
                mMusicIcon.setImageResource(R.drawable.sticker_sound_mute);
            }
            else
            {
                mMusicIcon.setImageBitmap(ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getResources(), R.drawable.sticker_sound_unmute)));
            }
        }
    }

    public void showWhiteBKDrawable(boolean show)
    {
        mBKDrawable.showWhiteBK(show);
        if (mLabel != null)
        {
            mLabel.showBlackNonDrawable(show);
        }
        if (mStickerFoldView != null)
        {
            mStickerFoldView.setImageResource(show ? R.drawable.sticker_list_fold_white : R.drawable.sticker_list_fold_gray);
        }
        StickerResMgr.getInstance().showGrayProgressBK(show);
    }

    public void setBtnRotation(int degree)
    {
        if (mMusicIcon != null)
        {
            mMusicIcon.setRotation(degree);
        }
    }

    public void setStickerUIListener(StickerUIListener listener)
    {
        mStickerUIListener = listener;
    }

    public void ClearAll()
    {
        if (mToast != null)
        {
            mToast.cancel();
            mToast = null;
        }

        if (mStickerHandler != null)
        {
            mStickerHandler.clearAll();
            mStickerHandler = null;
        }

        if (mHandlerThread != null)
        {
            mHandlerThread.quit();
            mHandlerThread = null;
        }

        StickerZipParseHelper.clearAll();

        if (mUiHandler != null)
        {
            mUiHandler.removeMessages(MSG_PARSE);
            mUiHandler = null;
        }

        mBKDrawable = null;

        mStickerUIListener = null;

        if (mLabel != null)
        {
            mLabel.ClearAll();
            mLabel = null;
        }
        if (mSticker != null)
        {
            mSticker.setStickerDataHelper(null);
            mSticker = null;
        }

        //network
        if (mNetworkStateChangeReceiver != null)
        {
            getContext().unregisterReceiver(mNetworkStateChangeReceiver);
            mNetworkStateChangeReceiver.setNetworkStateChangeListener(null);
            mNetworkStateChangeReceiver = null;
        }
        mConnectivityManager = null;
    }

    @Override
    public void onLoadStickerDataSucceed()
    {
        if (mLabel != null)
        {
            mLabel.updateData();
        }

        if (mSticker != null)
        {
            mSticker.notifyChildrenUpdateData();
        }

        if (StickerResMgr.getInstance().getShutterType() != ShutterConfig.TabType.PHOTO)
        {
            StickerInfo info = StickerResMgr.getInstance().getSelectedStickerInfo();

            if (info != null)
            {
                VideoStickerRes res = (VideoStickerRes) info.mRes;
                // 需要解压
                if (res.mStickerRes == null)
                {
                    StickerZipParseHelper.ParseHandler.ParseObj obj = new StickerZipParseHelper.ParseHandler.ParseObj();
                    obj.repeat = false;
                    obj.res = res;
                    obj.isMakeupRes = info.mIsMakeup;
                    sendStickerParseMsg(obj);
                }
                else
                {
                    showMakeupSeekBar(info.mIsMakeup);
                    if (mStickerUIListener != null)
                    {
                        mStickerUIListener.onSelectSticker(res, false);
                    }
                }

                if (StickerResMgr.getInstance().isShowStickerSelector() && mUiHandler != null)
                {
                    // 等ui 刷新
                    mUiHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onScrollStickerToCenter();
                        }
                    }, 50);
                }
            }
            else if (StickerResMgr.getInstance().isInInitDataJustGotoLabel())
            {
                if (StickerResMgr.getInstance().isShowStickerSelector() && mUiHandler != null)
                {
                    // 等ui 刷新
                    mUiHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onScrollStickerToCenter();
                        }
                    }, 50);
                }
            }
            else if (StickerResMgr.getInstance().isSpecific())
            {
                int id = StickerResMgr.getInstance().getSelectedInfo(StickerResMgr.SelectedInfoKey.AUTO_DOWN_LOAD);
                if (id != 0)
                {
                    info = StickerResMgr.getInstance().getStickerInfoByID(id);
                    if (info != null)
                    {
                        // 等ui 刷新
                        final StickerInfo finalInfo = info;
                        mUiHandler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                finalInfo.mAutoSelected = true;
                                finalInfo.mStatus = StatusMgr.Type.DOWN_LOADING;
                                finalInfo.mProgress = 0;
                                StickerResMgr.getInstance().notifyPagerViewDataChange(finalInfo);
                                // 自动下载
                                StickerResMgr.getInstance().DownloadRes(finalInfo);
                            }
                        }, 1000);
                    }
                }
            }
            else
            {
                if (mStickerUIListener != null)
                {
                    mStickerUIListener.onSelectSticker(null, false);
                }
            }
        }
        else
        {
            if (mStickerUIListener != null)
            {
                mStickerUIListener.onSelectSticker(null, false);
            }
        }
    }

    @Override
    public void onCanLoadRes()
    {
        if (mLabel != null)
        {
            mLabel.updateData();
        }

        if (mSticker != null)
        {
            mSticker.updateData();
        }

        if (StickerResMgr.getInstance().isLoadBuildIn())
        {
            onLoadStickerDataSucceed();
        }
    }

    @Override
    public void onShutterTabChange()
    {
        if (mLabel != null)
        {
            mLabel.updateData();
        }

        if (mSticker != null)
        {
            mSticker.notifyChildrenUpdateData();
        }

        if (StickerResMgr.getInstance().getShutterType() != ShutterConfig.TabType.PHOTO)
        {
            StickerInfo info = StickerResMgr.getInstance().getSelectedStickerInfo();
            if (info != null)
            {
                if (mMusicIcon != null)
                {
                    boolean show = StickerResMgr.getInstance().getShutterType() == ShutterConfig.TabType.VIDEO && info.mHasMusic;
                    mMusicIcon.setVisibility(show ? VISIBLE : GONE);
                }

                VideoStickerRes res = (VideoStickerRes) info.mRes;
                // 需要解压
                if (res.mStickerRes == null)
                {
                    StickerZipParseHelper.ParseHandler.ParseObj obj = new StickerZipParseHelper.ParseHandler.ParseObj();
                    obj.repeat = false;
                    obj.res = res;
                    obj.isTabChanged = true;
                    sendStickerParseMsg(obj);
                }
                else
                {
                    if (mStickerUIListener != null)
                    {
                        mStickerUIListener.onSelectSticker(res, false, true);
                    }
                }
            }
        }
        else
        {
            if (mMusicIcon != null)
            {
                mMusicIcon.setVisibility(GONE);
            }

            if (mStickerUIListener != null)
            {
                mStickerUIListener.onSelectSticker(null, false, true);
            }
        }
    }

    @Override
    public void onSelectedLabel(int index)
    {
        if (index < StickerResMgr.getInstance().getLabelArrValidSize() && mSticker != null)
        {
            mSticker.setCurrentItem(index);
        }
        else
        {
            if (mStickerUIListener != null)
            {
                mStickerUIListener.onOpenStickerMgrPage();
            }
        }
    }

    @Override
    public void onSelectedSticker(StickerInfo info)
    {
        if (info != null)
        {
            boolean repeat = false;

            if (info.mAutoSelected)
            {
                info.mAutoSelected = false;
                StickerResMgr.getInstance().updateSelectedInfo(StickerResMgr.SelectedInfoKey.STICKER, info.id);
            }
            else
            {
                repeat = StickerResMgr.getInstance().isHadSelected(info.id);

                // 更新选中id、ui
                if (!repeat)
                {
                    StickerResMgr.getInstance().modifyPreviousSelected(info);
                }
            }
            onShowVolumeBtn(info.mHasMusic);
            VideoStickerRes res = (VideoStickerRes) info.mRes;
            // 需要解压
            if (res.mStickerRes == null)
            {
                StickerZipParseHelper.ParseHandler.ParseObj obj = new StickerZipParseHelper.ParseHandler.ParseObj();
                obj.repeat = repeat;
                obj.res = res;
                obj.isMakeupRes = info.mIsMakeup;
                sendStickerParseMsg(obj);
            }
            else
            {
                showMakeupSeekBar(info.mIsMakeup);
                if (mStickerUIListener != null)
                {
                    mStickerUIListener.onSelectSticker((VideoStickerRes) info.mRes, repeat);
                }
            }
        }
        else // non
        {
            onShowVolumeBtn(false);
            if (mStickerUIListener != null)
            {
                mStickerUIListener.onSelectSticker(null, false);
            }
        }
    }

    @Override
    public void onStickerPageSelected(int index)
    {
        int last_selected_index = StickerResMgr.getInstance().getSelectedLabelIndex();

        LabelInfo previous_info = StickerResMgr.getInstance().getLabelInfoByIndex(last_selected_index);
        if (previous_info != null)
        {
            previous_info.isSelected = false;
        }

        LabelInfo info = StickerResMgr.getInstance().getLabelInfoByIndex(index);
        if (info != null)
        {
            info.isSelected = true;
            if (info.isShowRedPoint)
            {
                info.isShowRedPoint = false;
                VideoStickerGroupResRedDotMrg2.getInstance().markResFlag(getContext(), info.ID);
            }
            StickerResMgr.getInstance().updateSelectedInfo(StickerResMgr.SelectedInfoKey.LABEL, info.ID);
        }

        mLabel.notifyLabelDataChange(last_selected_index);
        mLabel.notifyLabelDataChange(index);
        mLabel.scrollToCenter(index);
    }

    @Override
    public void onLabelScrollToSelected(int index)
    {
        if (mLabel != null)
        {
            mLabel.scrollToCenter(index);
        }
    }

    @Override
    public void onRefreshAllData()
    {
        int selected_sticker_id = StickerResMgr.getInstance().getSelectedInfo(StickerResMgr.SelectedInfoKey.STICKER);
        if (selected_sticker_id == -1)
        {
            onSelectedSticker(null);
        }
        else
        {
            StickerInfo info = StickerResMgr.getInstance().getStickerInfoByID(selected_sticker_id);
            if (info != null)
            {
                onShowVolumeBtn(info.mHasMusic);
                VideoStickerRes res = (VideoStickerRes) info.mRes;
                // 需要解压
                if (res.mStickerRes == null)
                {
                    StickerZipParseHelper.ParseHandler.ParseObj obj = new StickerZipParseHelper.ParseHandler.ParseObj();
                    obj.repeat = false;
                    obj.res = res;
                    sendStickerParseMsg(obj);
                }
                else
                {
                    if (mStickerUIListener != null)
                    {
                        mStickerUIListener.onSelectSticker((VideoStickerRes) info.mRes, false);
                    }
                }
            }
        }

        if (mLabel != null)
        {
            mLabel.updateData();
        }

        if (mSticker != null)
        {
            mSticker.notifyChildrenUpdateData();
            mSticker.setCurrentItem(StickerResMgr.getInstance().getSelectedLabelIndex());
        }
    }

    @Override
    public void onUpdateUIByRatio(float ratio)
    {
        boolean showWhiteBK = (ratio == CameraConfig.PreviewRatio.Ratio_1_1 || ratio == CameraConfig.PreviewRatio.Ratio_9_16);
        showWhiteBKDrawable(showWhiteBK);
    }

    @Override
    public void popLockView(BaseRes res)
    {
        if (res != null)
        {
            if (mUnlockView != null && mUnlockView.IsRecycle() && !mShowUnlockView)
            {
                BaseRes baseres = mUnlockView.getData();
                if (baseres != null && baseres.m_id == res.m_id)
                {
                    mShowUnlockView = true;
                    mUnlockView.Show();
                }
                else
                {
                    if (res instanceof LockRes)
                    {
                        mShowUnlockView = true;
                        mUnlockView.SetBk(0xcc000000);
                        mUnlockView.SetDatas(res, 0);
                        mUnlockView.Show();
                    }
                }

                if (mUnLockUIListener != null)
                {
                    mUnLockUIListener.openUnLockView();
                }
            }
            else if (mPopFrameView != null && !mShowUnlockView)
            {
                mPopFrameView.setVisibility(View.VISIBLE);
                mShowUnlockView = true;
                if (res instanceof LockRes)
                {
                    mUnlockView = new RecomDisplayMgr(getContext(), mUnLockCallback);
                    mUnlockView.Create(mPopFrameView);
                    mUnlockView.SetBk(0xcc000000);
                    mUnlockView.SetDatas(res, 0);
                    mUnlockView.Show();
                }
                if (mUnLockUIListener != null)
                {
                    mUnLockUIListener.openUnLockView();
                }
            }
        }
    }

    public void onResume()
    {
        if (mUnlockView != null)
        {
            mUnlockView.onResume();
        }
    }

    public void onPause()
    {
        if (mUnlockView != null)
        {
            mUnlockView.onPause();
        }
    }

    @Override
    public void onScrollStickerToCenter()
    {
        if (mSticker != null)
        {
            int index = StickerResMgr.getInstance().getSelectedLabelIndex();
            mSticker.setCurrentItem(index);
        }
    }

    @Override
    public boolean checkNetworkAvailable()
    {
        boolean out = true;

        if (mConnectivityManager != null)
        {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable())
            {
                showToast(getContext().getString(R.string.sticker_pager_network_error));
                out = false;
            }
        }
        return out;
    }

    @Override
    public void onShowVolumeBtn(boolean show)
    {
        if (mMusicIcon != null && isSupportStickerSound())
        {
            if (mStickerUIListener != null)
            {
                setStickerSoundMute(checkAudioMute());
            }
            mMusicIcon.setVisibility(show ? VISIBLE : GONE);
        }
    }

    private void showToast(String str)
    {
        if (mToast == null)
        {
            mToast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
            LinearLayout layout = (LinearLayout) mToast.getView();
            TextView tv = (TextView) layout.findViewById(android.R.id.message);
            layout.setBackgroundDrawable(null);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundDrawable(DrawableUtils.shapeDrawable(0x3e000000, CameraPercentUtil.WidthPxToPercent(45)));
            tv.setCompoundDrawablePadding(CameraPercentUtil.WidthPxToPercent(8));
            tv.getBackground().setAlpha((int) (255 * 0.8f));
            tv.setPadding(CameraPercentUtil.WidthPxToPercent(20), CameraPercentUtil.WidthPxToPercent(10), CameraPercentUtil.WidthPxToPercent(20), CameraPercentUtil.WidthPxToPercent(10));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mToast.show();
    }

    @Override
    public void onClick(View v)
    {
        if (v == mMusicIcon)
        {
            //贴纸音效开关btn
            if (mStickerUIListener != null)
            {
                //当前是静音状态下，设置为非静音
                boolean mute = checkAudioMute();
                mStickerUIListener.onStickerSoundMute(!mute);
            }
        }
        else if (v == mStickerFoldView || v == mStickerSoundFr)
        {
            if (mStickerUIListener != null)
            {
                mStickerUIListener.onCloseStickerList();
            }
        }
    }

    /**
     * 检查是否静音
     *
     * @return
     */
    private boolean checkAudioMute()
    {
        return mStickerUIListener != null && mStickerUIListener.getAudioMute();
    }

    private boolean isSupportStickerSound()
    {
        //gif模式、萌装照不支持贴纸音效
        return StickerResMgr.getInstance().getShutterType() == ShutterConfig.TabType.VIDEO;
    }

    @Override
    public void onNetworkDisconnect()
    {
        StickerResMgr.getInstance().CancelDownload();
    }
}

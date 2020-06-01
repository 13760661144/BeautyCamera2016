package cn.poco.arWish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.graphics.ColorUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.arWish.site.ArVideoPageSite;
import cn.poco.arWish.widget.MediaControllerBar;
import cn.poco.camera3.ui.drawable.RoundRectDrawable;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.widget.ProgressView;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.FolderMgr;
import cn.poco.tianutils.NetCore2;
import cn.poco.utils.FileUtil;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.video.FileUtils;
import my.beautyCamera.BuildConfig;
import my.beautyCamera.R;

/**
 * Created by admin on 2018/1/19.
 */

public class ArVideoPage extends IPage {
    private static final String TAG = "bbb";

    public static final String KEY_VIDEO_PATH = "video_path";
    public static final String KEY_VIDEO_THUMB = "video_thumb";
    public static final String KEY_SHOW_SAVE_VIDEO_BTN = "show_save_video_btn";

    private ArVideoPageSite mBaseSite;
    private ImageView mVideoThumbView;
    private View mSaveVideoBtn;
    private View mLoadingView;
    private View mPlayBtn;
    private TextView mSaveTips;

    private boolean mShowSaveVideoBtn = true;
    private String mPlayVideoPath;
    private String mPlayVideoThumb;
    private boolean mIsSavingVideo;

    private ProgressView mProgressView;

    private ConnectivityManager mConnectivityManager;
    private boolean mCancelSave;
    private boolean mVideoIsLocal;
    private boolean mStartNeedNetTips;

    public ArVideoPage(Context context, BaseSite site) {
        super(context, site);
        MyBeautyStat.onPageStartByRes(R.string.ar祝福_找祝福_播放视频);
        mBaseSite = (ArVideoPageSite) site;
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        initScreen();
        initUI();
    }

    private void initScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = ((Activity) getContext()).getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private FrameLayout mContentView;

    private void initUI() {

        LayoutInflater m_inflater = LayoutInflater.from(getContext());
        mContentView = (FrameLayout) m_inflater.inflate(R.layout.pl_video_view, null);
        this.addView(mContentView);

        View backBtn = findViewById(R.id.pl_back);
        FrameLayout.LayoutParams params = (LayoutParams) backBtn.getLayoutParams();
        backBtn.setLayoutParams(params);

        backBtn.setOnTouchListener(mOnAnimationClickListener);
        mVideoView = findViewById(R.id.pl_video_view);

        //加载view
        mLoadingView = findViewById(R.id.pl_loadingview);
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(GONE);

        //视频微缩封面
        mVideoThumbView = findViewById(R.id.pl_coverView);
        mVideoView.setCoverView(mVideoThumbView);

        mPlayBtn = findViewById(R.id.pl_play_btn);
        mPlayBtn.setOnTouchListener(mOnAnimationClickListener);

        //点击保存视频
        mSaveVideoBtn = findViewById(R.id.pl_save_video);
        params = (LayoutParams) mSaveVideoBtn.getLayoutParams();
        mSaveVideoBtn.setLayoutParams(params);
        mSaveVideoBtn.setOnTouchListener(mOnAnimationClickListener);

        // Set some listeners
//        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnVideoFrameListener(mOnVideoFrameListener);
        mVideoView.setOnAudioFrameListener(mOnAudioFrameListener);
        mVideoView.setLooping(true);//循环播放

        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        mMediaController = new MediaControllerBar(getContext());
        mMediaController.setOnClickPauseListener(mOnClickPauseListener);
        mContentView.addView(mMediaController, params);
        mVideoView.setMediaController(mMediaController);
    }

    private PLVideoView mVideoView;
    private Toast mToast = null;
    private MediaControllerBar mMediaController;

    private boolean isPlayingOnPause = false;

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && isPlayingOnPause) {
            mPlayBtn.setVisibility(GONE);
            mVideoView.start();
        }
        // 双重判断
        if (mVideoView != null && mVideoView.isPlaying())
        {
            mPlayBtn.setVisibility(GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPlayingOnPause = mVideoView.isPlaying();
        pause();
        mToast = null;
    }

    @Override
    public void onClose() {
        super.onClose();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        mConnectivityManager = null;
        MyBeautyStat.onPageEndByRes(R.string.ar祝福_找祝福_播放视频);

        if (mNetCore2 != null) {
            mNetCore2.ClearAll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }

        mVideoView = null;
    }

    private void saveVideo() {
        if (mIsSavingVideo) {
            return;
        }
        if (!this.hasNetwork()) {
            showToastTips(getContext().getString(R.string.ar_find_preview_net_error));
        } else {

            downloadVideo();

//            if (!this.isWifi()) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("使用移动网络下载");
//                builder.setMessage("下载将消耗移动流量");
//                builder.setNegativeButton(R.string.ar_find_preview_cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        downloadVideo();
//                    }
//                });
//                builder.show();
//            } else {
//                downloadVideo();
//            }
        }
    }

    private void downloadVideo() {
        if (!TextUtils.isEmpty(mPlayVideoPath) && mPlayVideoPath.startsWith("http")) {
            mCancelSave = false;

            String path = getVideoDownloadPath(mPlayVideoPath);
            File file = new File(path);
            if (file.exists()) {
                showToastTips(getContext().getString(R.string.ar_find_preview_video_exit));
            } else if (!mIsSavingVideo) {
                path = getVideoDownloadTempPath();
                file = new File(path);
                if (file.exists())
                {
                    FileUtil.deleteFile(file, false);
                }

                mIsSavingVideo = true;
                mMediaController.setEnabled(false);
                pause();
                mPlayBtn.setVisibility(GONE);
                showProgressView();
                startToLoadVideo();
            }
        }
    }

    private void showProgressView() {
        if (mProgressView == null) {

            RoundRectDrawable bg = new RoundRectDrawable();
            bg.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(30), CameraPercentUtil.WidthPxToPercent(30));
            bg.setColor(Color.WHITE);

            LayoutParams params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(286), CameraPercentUtil.WidthPxToPercent(278));
            params.gravity = Gravity.CENTER;
            mProgressView = new ProgressView(getContext());
            mProgressView.setProgressColor(ColorUtils.setAlphaComponent(ImageUtils.GetSkinColor(), (int) (255 * 0.2f)), ImageUtils.GetSkinColor());
            mProgressView.setProgressWidth(CameraPercentUtil.WidthPxToPercent(2));
            mProgressView.setRadius(CameraPercentUtil.WidthPxToPercent(56));
            mProgressView.setText(getContext().getString(R.string.ar_find_preview_download));
            mProgressView.setTextParams(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()), 0xff333333);
            mProgressView.setBackgroundDrawable(bg);
            mContentView.addView(mProgressView, params);
        }

        mProgressView.setProgress(0);
        mProgressView.setVisibility(VISIBLE);
    }

    private void startToLoadVideo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String path = getVideoDownloadTempPath();//文件保存临时路径
                mNetCore2 = new NetCore2();
                final NetCore2.NetMsg msg = mNetCore2.HttpGet(mPlayVideoPath, null, path, mHandler);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIsSavingVideo = false;

                        if (mProgressView != null) {
                            mProgressView.setVisibility(GONE);
                        }

                        if (mPlayBtn != null && !mVideoView.isPlaying()) {
                            mPlayBtn.setVisibility(VISIBLE);
                        }

                        if (msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK) {
                            String save_path = getVideoDownloadPath(mPlayVideoPath);
                            File save_file = new File(save_path);
                            File file = new File(path);
                            if (file.exists())
                            {
                                if (file.renameTo(save_file))
                                {
                                    getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(save_file)));
                                    showSaveTips(true, getContext().getString(R.string.ar_find_preview_saved));
                                }
                            }
                            else
                            {
                                showSaveTips(true, getContext().getString(R.string.ar_find_preview_save_failed));
                            }
                        } else if (!mCancelSave) {
                            // 删除文件
                            File file = new File(path);
                            FileUtil.deleteFile(file, false);
                            showSaveTips(true, getContext().getString(R.string.ar_find_preview_save_failed));
                        }

                        if (mMediaController != null) {
                            mMediaController.setEnabled(true);
                        }
                    }
                });
            }
        }).start();
    }

    private String getVideoDownloadPath(String url) {
        String file_name = NetCore2.GetSubFileName(url);

        String dir_path = FileUtils.getVideoOutputSysDir();

        return dir_path + File.separator + file_name;
    }

    private String getVideoDownloadTempPath() {
        String file_name = "temp_ar_video.xxx";

        String dir_path = FileUtils.getVideoOutputSysDir();

        return dir_path + File.separator + file_name;
    }

    private void showSaveTips(boolean show, String msg) {
        if (show) {
            if (mSaveTips == null) {
                RoundRectDrawable bg = new RoundRectDrawable();
                bg.setColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.96f)));
                bg.setRoundRectParams(CameraPercentUtil.WidthPxToPercent(39), CameraPercentUtil.WidthPxToPercent(39));

                mSaveTips = new TextView(getContext());
                mSaveTips.setBackgroundDrawable(bg);
                mSaveTips.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                mSaveTips.setTextColor(0xff333333);
                mSaveTips.setText(msg);
                mSaveTips.setGravity(Gravity.CENTER);
                FrameLayout.LayoutParams params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(176), CameraPercentUtil.WidthPxToPercent(78));
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.topMargin = CameraPercentUtil.WidthPxToPercent(120);
                addView(mSaveTips, params);
            } else {
                mSaveTips.setText(msg);
            }

            mSaveTips.setVisibility(VISIBLE);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSaveTips(false, "");
                }
            }, 4000);
        } else if (mSaveTips != null) {
            mSaveTips.setVisibility(GONE);
        }
    }

    private void pause() {
        mPlayBtn.setVisibility(VISIBLE);
        mLoadingView.setVisibility(GONE);
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    private void setVideoPathToPlayer(String videoPath, boolean isPlay) {
        if (!TextUtils.isEmpty(videoPath) && mVideoView != null) {
            mVideoView.setVideoPath(videoPath);
            if (isPlay) mVideoView.start();
        }
        if (isPlay) {
            mPlayBtn.setVisibility(GONE);
        }
    }

    private void startPlayer(final String videoPath) {
        if (!mVideoIsLocal && mStartNeedNetTips) // 网络视频、断网
        {
            if (!hasNetwork()) {
                showToastTips(getContext().getString(R.string.ar_find_preview_net_error));
            } else if (!isWifi()) {
                //移动网络播放
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.ar_find_preview_photo_net_tips_one);
                builder.setMessage(R.string.ar_find_preview_photo_net_tips_two);
                builder.setNegativeButton(R.string.ar_find_preview_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPlayBtn.setVisibility(VISIBLE);
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(R.string.ar_find_preview_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mStartNeedNetTips = false;
                        if (!TextUtils.isEmpty(videoPath) && mVideoView != null) {
                            setVideoPathToPlayer(videoPath, true);
                            mPlayBtn.setVisibility(GONE);
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {
                mStartNeedNetTips = false;
                if (!TextUtils.isEmpty(videoPath) && mVideoView != null) {
                    setVideoPathToPlayer(videoPath, true);
                    mPlayBtn.setVisibility(GONE);
                }
//            mMediaController.show();
            }
        } else {
            if (!TextUtils.isEmpty(videoPath) && mVideoView != null) {
                mVideoView.start();
                mPlayBtn.setVisibility(GONE);
            }
//            mMediaController.show();
        }
    }

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    /**
                     * SDK will do reconnecting automatically
                     */
                    Log.e(TAG, "IO Error!");
                    return false;
                case PLMediaPlayer.ERROR_CODE_OPEN_FAILED:
//                    showToastTips("failed to open player !");
                    Log.e(TAG, "failed to open player ! ");
                    break;
                case PLMediaPlayer.ERROR_CODE_SEEK_FAILED:
//                    showToastTips("failed to seek !");
                    Log.e(TAG, "failed to seek ! ");
                    mMediaController.updateProgress();
                    setVideoPathToPlayer(mPlayVideoPath,true);
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            /*Log.d(TAG, "ArVideoPage --> onCompletion: ");*/
//            showToastTips("Play Completed !");
            mMediaController.refreshProgress();
        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
            /*Log.d(TAG, "ArVideoPage --> onBufferingUpdate: precent :" + precent);*/
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            /*Log.d(TAG, "ArVideoPage --> onVideoSizeChanged: width = " + width + ", height = " + height);*/
        }
    };

    private PLMediaPlayer.OnVideoFrameListener mOnVideoFrameListener = new PLMediaPlayer.OnVideoFrameListener() {
        @Override
        public void onVideoFrameAvailable(byte[] data, int size, int width, int height, int format, long ts) {
            /*Log.d(TAG, "ArVideoPage --> onVideoFrameAvailable:  " + size + ", " + width + " x " + height + ", " + format + ", " + ts);*/
        }
    };

    private PLMediaPlayer.OnAudioFrameListener mOnAudioFrameListener = new PLMediaPlayer.OnAudioFrameListener() {
        @Override
        public void onAudioFrameAvailable(byte[] data, int size, int samplerate, int channels, int datawidth, long ts) {
            /*Log.i(TAG, "ArVideoPage --> onAudioFrameAvailable: " + size + ", " + samplerate + ", " + channels + ", " + datawidth + ", " + ts);*/
        }
    };
    private MediaControllerBar.OnClickPauseListener mOnClickPauseListener = new MediaControllerBar.OnClickPauseListener() {

        @Override
        public void pause() {
            mLoadingView.setVisibility(GONE);
            mPlayBtn.setVisibility(VISIBLE);
        }

        @Override
        public void play() {
            mPlayBtn.setVisibility(GONE);
        }
    };

    private NetCore2 mNetCore2;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == NetCore2.MSG_PROGRESS) {
                if (mProgressView != null) {
                    int progress = msg.arg1;
                    mProgressView.setProgress((int) (progress * 360f / 100f));
                    mPlayBtn.setVisibility(GONE);
                }
            }
        }
    };

    private void showToastTips(final String tips) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params != null) {
            if (params.containsKey(KEY_VIDEO_PATH)) {
                mPlayVideoPath = (String) params.get(KEY_VIDEO_PATH);
            }
            if (params.containsKey(KEY_VIDEO_THUMB)) {
                mPlayVideoThumb = (String) params.get(KEY_VIDEO_THUMB);
            }
            if (params.containsKey(KEY_SHOW_SAVE_VIDEO_BTN)) {
                mShowSaveVideoBtn = (boolean) params.get(KEY_SHOW_SAVE_VIDEO_BTN);
            }
        }

        if (mSaveVideoBtn != null) {
            mSaveVideoBtn.setVisibility(mShowSaveVideoBtn ? VISIBLE : GONE);
        }

        if (!TextUtils.isEmpty(mPlayVideoPath)) {
            String path = getVideoDownloadPath(mPlayVideoPath);
            File file = new File(path);

            if (file.exists())// 本地视频
            {
                mVideoIsLocal = true;
                setVideoPathToPlayer(path, true);
            } else              // 网络视频
            {

                initOptions();

                if (!TextUtils.isEmpty(mPlayVideoThumb) && mVideoThumbView != null) {
                    Glide.with(getContext()).load(mPlayVideoThumb).into(mVideoThumbView);
                }

                if (this.hasNetwork() && !this.isWifi()) {
                    //移动网络播放
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.ar_find_preview_photo_net_tips_one);
                    builder.setMessage(R.string.ar_find_preview_photo_net_tips_two);
                    builder.setNegativeButton(R.string.ar_find_preview_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPlayBtn.setVisibility(VISIBLE);
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.ar_find_preview_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mStartNeedNetTips = false;
                            if (!TextUtils.isEmpty(mPlayVideoPath) && mVideoView != null) {
                                setVideoPathToPlayer(mPlayVideoPath, true);
                                mPlayBtn.setVisibility(GONE);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else if (this.hasNetwork()) {
                    setVideoPathToPlayer(mPlayVideoPath, true);
                } else {
                    setVideoPathToPlayer(mPlayVideoPath, false);
                    mPlayBtn.setVisibility(VISIBLE);
                    mStartNeedNetTips = true;
                    showToastTips(getContext().getString(R.string.ar_find_preview_net_error));
                }
            }
        } else {
            showToastTips(getContext().getString(R.string.ar_find_preview_no_video));
        }
    }

    private void initOptions() {
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 15 * 1000);//请求超时时间
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_AUTO);//硬软解码
        boolean debug = BuildConfig.DEBUG;
        options.setInteger(AVOptions.KEY_LOG_LEVEL, debug ? 5 : 0);
        options.setString(AVOptions.KEY_CACHE_DIR, FolderMgr.getInstance().PATH_PLAYER_CACHE);//缓存
        options.setInteger(AVOptions.KEY_VIDEO_DATA_CALLBACK, 1);
        options.setInteger(AVOptions.KEY_AUDIO_DATA_CALLBACK, 1);
        mVideoView.setDebugLoggingEnabled(debug);
        mVideoView.setAVOptions(options);
        mVideoView.setDrawingCacheEnabled(false);
    }

    private boolean isWifi() {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (mConnectivityManager != null) {
            NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }

        return false;
    }

    private boolean hasNetwork() {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (mConnectivityManager != null) {
            NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBack() {
        if (mIsSavingVideo) {// 正在下载
            // 停止下载
            if (mNetCore2 != null) {
                mNetCore2.ClearAll();
                mNetCore2 = null;
            }
            if (mProgressView != null) {
                mProgressView.setVisibility(GONE);
            }
            mCancelSave = true;
            showSaveTips(true, getContext().getString(R.string.ar_find_preview_cancel_save));
            return;
        }
        MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_播放视频_返回);
        mBaseSite.onBack(getContext());
    }

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v.getId() == R.id.pl_back) {
                onBack();
            } else if (v.getId() == R.id.pl_save_video) {
                MyBeautyStat.onClickByRes(R.string.AR祝福_找祝福_播放视频_保存视频);
                saveVideo();
            } else if (v.getId() == R.id.pl_play_btn) {
                startPlayer(mPlayVideoPath);
            }
        }
    };
}

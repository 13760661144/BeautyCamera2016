package cn.poco.arWish.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.circle.utils.Utils;
import com.pili.pldroid.player.IMediaController;
import com.taotie.circle.PLog;

import java.util.Locale;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.widget.BufferSeekBar;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class MediaControllerBar extends FrameLayout implements IMediaController {

    private MediaPlayerControl mPlayer;
    private Context mContext;
    private BufferSeekBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private long mDuration;
    private boolean mDragging;
    private boolean mInstantSeeking = true;
    private static int sDefaultTimeout = 3000;
    private static final int SEEK_TO_POST_DELAY_MILLIS = 200;

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private ImageView mPauseButton;

    private AudioManager mAM;
    private Runnable mLastSeekBarRunnable;
    private boolean mDisableProgress = false;
    private OnClickPauseListener mOnClickPauseListener;

    public interface OnClickPauseListener {
        void pause();

        void play();
    }

    public MediaControllerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initController(context);
    }

    public MediaControllerBar(Context context) {
        super(context);
        initController(context);
    }

    public void refreshProgress() {
        mProgress.setProgress(100);
        mCurrentTime.setText(generateTime(mDuration));
        updatePausePlay();
    }


    public void setOnClickPauseListener(OnClickPauseListener listener) {
        mOnClickPauseListener = listener;
    }


    private boolean initController(Context context) {
        mContext = context.getApplicationContext();
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        addView(makeControllerView());
        return true;
    }

    @Override
    public void onFinishInflate() {

        super.onFinishInflate();
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     */
    protected View makeControllerView() {
        RelativeLayout mControllerView = new RelativeLayout(getContext());
        mControllerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(240)));
        mControllerView.setBackgroundResource(R.drawable.ar_video_controller_bar_bg);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(46));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = ShareData.PxToDpi_xhdpi(50);
        RelativeLayout mLayout = new RelativeLayout(getContext());
        mControllerView.addView(mLayout, params);

        params = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(46),ShareData.PxToDpi_xhdpi(46));
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.leftMargin = ShareData.PxToDpi_xhdpi(40);
        mPauseButton = new ImageView(getContext());
        mPauseButton.setImageResource(R.drawable.ar_video_play_btn);
        mPauseButton.requestFocus();
        mPauseButton.setId(Utils.generateViewId());
        mPauseButton.setOnClickListener(mPauseListener);
        mLayout.addView(mPauseButton, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.RIGHT_OF, mPauseButton.getId());
        params.leftMargin = ShareData.PxToDpi_xhdpi(30);
        params.rightMargin = ShareData.PxToDpi_xhdpi(10);
        mCurrentTime = new TextView(getContext());
        mCurrentTime.setTextColor(Color.WHITE);
        mCurrentTime.setId(Utils.generateViewId());
        mCurrentTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        mLayout.addView(mCurrentTime, params);

        params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = ShareData.PxToDpi_xhdpi(48);
        params.leftMargin = ShareData.PxToDpi_xhdpi(10);
        mEndTime = new TextView(getContext());
        mEndTime.setId(Utils.generateViewId());
        mEndTime.setTextColor(Color.WHITE);
        mEndTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        mLayout.addView(mEndTime, params);

        params = new RelativeLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(384), CameraPercentUtil.WidthPxToPercent(100));
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.RIGHT_OF, mCurrentTime.getId());
        params.addRule(RelativeLayout.LEFT_OF, mEndTime.getId());
        mProgress = new BufferSeekBar(getContext());
        mProgress.setColor(ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.2f)), Color.WHITE, ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.6f)));
        mProgress.setPointParams(CameraPercentUtil.WidthPxToPercent(10), Color.WHITE);
        mProgress.setProgressWidth(CameraPercentUtil.WidthPxToPercent(2));
        mProgress.setOnSeekBarChangeListener(mBufferSeekListener);
//        mProgress.setThumbOffset(1);
//        mProgress.setMax(1000);
        mProgress.setUIEnable(!mDisableProgress);
        mLayout.addView(mProgress, params);

//        LayerDrawable layerDrawable = (LayerDrawable) mProgress.getProgressDrawable();
//        Drawable dra = layerDrawable.getDrawable(2);
//        dra.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC);
//
//        Drawable dra1 = layerDrawable.getDrawable(1);
//        dra1.setColorFilter(0xffB99482, PorterDuff.Mode.SRC);
//
//        Drawable dra2 = layerDrawable.getDrawable(1);
//        dra2.setColorFilter(0xff999999, PorterDuff.Mode.SRC);
//
//        mProgress.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//        mProgress.invalidate();

        return mControllerView;
    }

    private void disableUnsupportedButtons() {
        try {
            if (mPauseButton != null && mPlayer != null && !mPlayer.canPause())
                mPauseButton.setEnabled(false);
        } catch (IncompatibleClassChangeError ex) {
        }
    }

    public interface OnShownListener {
        public void onShown();
    }

    private OnShownListener mShownListener;

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public interface OnHiddenListener {
        void onHidden();
    }

    private OnHiddenListener mHiddenListener;

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:

//                    if (!mPlayer.isPlaying()) {
//                        return;
//                    }
//
                    pos = setProgress();
//                    if (pos == -1) {
//                        return;
//                    }

                    if (getVisibility() == VISIBLE) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    public void updateProgress() {
        setProgress();
    }

    private long setProgress() {
        if (mPlayer == null) {
            return 0;
        }

//        PLog.out("****** setProgress ******");

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                float pos = position * 1f / duration;
                mProgress.setProgress(pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setBufferProgress(percent * 1f / 100f);
        }

        mDuration = duration;

        if (mEndTime != null)
            mEndTime.setText(generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(generateTime(position));

//        PLog.out("****** setProgress mEndTime ******" + mEndTime.getText());
//        PLog.out("****** setProgress mCurrentTime ******" + mCurrentTime.getText());
        return position;
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        PLog.out("****** onTouchEvent ******");
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(mPlayer == null){
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0
                && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(sDefaultTimeout);
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            show(sDefaultTimeout);
            doPauseResume();
        }
    };

    public void updatePausePlay() {
        if (mPauseButton == null || mPlayer == null)
            return;

        if (mPlayer.isPlaying())
            mPauseButton.setImageResource(R.drawable.ar_video_pause_btn);
        else
            mPauseButton.setImageResource(R.drawable.ar_video_play_btn);
    }

    private void doPauseResume() {
        if(mPlayer == null){
            return;
        }
        if (mPlayer.isPlaying())
            mPlayer.pause();
        else
            mPlayer.start();
        updatePausePlay();

        if (mOnClickPauseListener != null) {
            if (mPlayer.isPlaying()) {
                mOnClickPauseListener.play();
            } else {
                mOnClickPauseListener.pause();
            }
        }
    }

    private BufferSeekBar.OnSeekBarChangeListener mBufferSeekListener = new BufferSeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(float percent) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        @Override
        public void onProgressChanged(float percent) {
            final long newposition = (long) (mDuration * percent);
            String time = generateTime(newposition);

            if(mPlayer != null){
//                mHandler.removeCallbacks(mLastSeekBarRunnable);
//                mLastSeekBarRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        mPlayer.seekTo(newposition);
//                    }
//                };
//                mHandler.postDelayed(mLastSeekBarRunnable, SEEK_TO_POST_DELAY_MILLIS);

                if (mCurrentTime != null)
                    mCurrentTime.setText(time);
            }
        }

        @Override
        public void onStopTrackingTouch(float percent) {
            if(mPlayer != null){
                long currentPosition = mPlayer.getCurrentPosition();
                long seekTime = (long) (mDuration * percent);
                boolean canSeek = false;
                if(currentPosition > seekTime ){
                    PLog.out("seek Backward");
                    canSeek = mPlayer.canSeekBackward();
                }else{
                    PLog.out("seek forward");
                    canSeek = mPlayer.canSeekForward();
                }

                PLog.out("canSeek ：" + canSeek);
                if(canSeek){
                    mPlayer.seekTo((long) (mDuration * percent));
                }else{
                    String time = generateTime(currentPosition);
                    if (mCurrentTime != null)
                        mCurrentTime.setText(time);
                }

                show(sDefaultTimeout);
                mHandler.removeMessages(SHOW_PROGRESS);
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
                mDragging = false;
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
            }
        }
    };

    /**
     * Set the view that acts as the anchor for the control view.
     * <p>
     * - This can for example be a VideoView, or your Activity's main view.
     * - AudioPlayer has no anchor view, so the view parameter will be null.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    @Override
    public void setAnchorView(View view) {

    }

    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller until hide() is called.
     */
    @Override
    public void show(int timeout) {
        if (getVisibility() != VISIBLE) {
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            disableUnsupportedButtons();

            setVisibility(VISIBLE);
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }
    }

    @Override
    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    @Override
    public void hide() {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mProgress != null && !mDisableProgress)
            mProgress.setUIEnable(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }
}

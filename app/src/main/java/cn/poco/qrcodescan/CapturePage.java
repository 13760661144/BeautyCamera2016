/*
package cn.poco.qrcodescan;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.circle.common.photopicker.ImageStore;
import com.circle.common.photopicker.PhotoPickerPage;
import com.circle.utils.Utils;
import com.google.zxing.Result;
import com.sina.weibo.sdk.web.view.LoadingBar;

import java.io.IOException;

import my.beautyCamera.R;

public class CapturePage extends BasePage implements Callback
{
	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;
	
	private ScanView mScanView;
	private boolean mHasSurface;
	private boolean mFlashLightOpenSave = false;
	private boolean mFlashLightOpen = false;
	private MediaPlayer mMediaPlayer;
	private boolean mPlayBeep;
	private boolean mVibrate;

	private IconButton mBtnAlbum;
	private IconButton mFlashrightBtn;
	private RelativeLayout mRelaLayout;
	private RelativeLayout mTopBar;
	//private LinearLayout mBtmBar;
	private FrameLayout mFrameLayout;
	private SurfaceView mSurfaceView;
	private CameraManager mCameraManager;
	private OnHandleDecodeResultListener mHandleDecodeResultListener;
	private DecodeHandler mDecodeHandler = new DecodeHandler(null);
	//private ProgressDialog mProgressDialog;
	private LoadingBar mLoadingBar;
	private Handler mHandler = new Handler();
	
	public CapturePage(Context context) {
		super(context);
		initialize(context);
	}
	
	private void initialize(Context context) {
		
		CameraManager.init(context);
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mFrameLayout = new FrameLayout(context);
		mFrameLayout.setLayoutParams(params);
		addView(mFrameLayout);
		
		FrameLayout.LayoutParams fparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fparams.gravity = Gravity.CENTER;
		mSurfaceView = new SurfaceView(context);
		mFrameLayout.addView(mSurfaceView, fparams);

		fparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mScanView = new ScanView(context);
		int w = Utils.getRealPixel2(484);
		int h = Utils.getRealPixel2(484);
		int x = (Utils.getScreenW()-w)/2;
		int y = Utils.getRealPixel2(286);
		mScanView.setFrameRect(new Rect(x, y, x+w, y+h));
		mFrameLayout.addView(mScanView, fparams);

		fparams = new FrameLayout.LayoutParams(Utils.getRealPixel2(484), Utils.getRealPixel2(484));
		fparams.leftMargin = x;
		fparams.topMargin = y;
		FrameLayout loading = new FrameLayout(context);
		mFrameLayout.addView(loading, fparams);

		fparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		fparams.gravity = Gravity.CENTER;
		mLoadingBar = new LoadingBar(context);
		mLoadingBar.setTextColor(0xffffffff);
		loading.addView(mLoadingBar, fparams);
		mLoadingBar.setVisibility(GONE);

		fparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fparams.gravity = Gravity.CENTER;
		mRelaLayout = new RelativeLayout(context);
		mFrameLayout.addView(mRelaLayout, fparams);

		LayoutParams rparams = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.getRealPixel2(100));
		rparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mTopBar = new RelativeLayout(context);
		mRelaLayout.addView(mTopBar, rparams);

		rparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rparams.addRule(RelativeLayout.CENTER_VERTICAL);
		rparams.rightMargin = Utils.getRealPixel2(30);
		mBtnAlbum = new IconButton(context);
		mBtnAlbum.setText("相册");
		mBtnAlbum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mBtnAlbum.setTextColor(0xffffffff, 0xffffffff);
		mBtnAlbum.setOnClickListener(mOnClickListener);
		mTopBar.addView(mBtnAlbum, rparams);

		fparams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fparams.topMargin = Utils.getRealPixel2(620);
		fparams.gravity = Gravity.CENTER_HORIZONTAL;
		mFlashrightBtn = new IconButton(context);
		mFlashrightBtn.setId(Utils.generateViewId());
		mFlashrightBtn.setOrientation(LinearLayout.VERTICAL);
		mFlashrightBtn.setText("轻点照亮");
		mFlashrightBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		mFlashrightBtn.setTextColor(0xffffffff);
        mFlashrightBtn.setSpace(Utils.getRealPixel2(14));
		mFlashrightBtn.setButtonImage(R.drawable.framework_flashlight_open_hover, R.drawable.framework_flashlight_open_hover);
		mFlashrightBtn.setOnClickListener(mOnClickListener);
		mFrameLayout.addView(mFlashrightBtn, fparams);
		mFlashrightBtn.setVisibility(GONE);

		rparams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		rparams.addRule(RelativeLayout.ABOVE, 10);
		View line = new View(context);
		line.setBackgroundColor(Color.BLACK);
		mRelaLayout.addView(line, rparams);
		
		fparams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fparams.gravity = Gravity.CENTER_HORIZONTAL;
		fparams.topMargin = Utils.getRealPixel2(823);
		TextView msg = new TextView(context);
		msg.setText("将取景框对准二维码，即可扫描。");
		msg.setTextColor(0xffffffff);
		msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		mFrameLayout.addView(msg, fparams);
		
		if(!Utils.checkPermission(context, permission.CAMERA)){
			Toast.makeText(context, "拍摄权限已被禁止,请开启后再使用扫描", Toast.LENGTH_SHORT).show();
		}
		
		mDecodeHandler.setOnDecodeCompleteListener(mOnDecodeCompleteListener);

		checkDark();
	}

	public void setOnHandleDecodeResultListener(OnHandleDecodeResultListener l){
		mHandleDecodeResultListener = l;
	}

	public void checkDark()
	{
		mHandler.postDelayed(mCheckDarkRunnable, 500);
	}

	private Runnable mCheckDarkRunnable = new Runnable()
	{
		@Override
		public void run() {
			postDelayed(mCheckDarkRunnable, 500);
			if(mDecodeHandler.isDark() == false || mFlashLightOpen) {
				mScanView.startMove();
			} else {
				mScanView.stopMove();
			}
			if(mDecodeHandler.isDark() || mFlashLightOpen)
			{
                if(mFlashrightBtn.getVisibility() != VISIBLE) {
                    mFlashrightBtn.setVisibility(VISIBLE);
                    AnimationSet animationSet = new AnimationSet(true);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                    alphaAnimation.setDuration(200);
                    animationSet.addAnimation(alphaAnimation);
                    mFlashrightBtn.startAnimation(animationSet);
                }
			}
			else
			{
                if(mFlashrightBtn.getVisibility() != GONE && mFlashLightOpen == false) {
                    AnimationSet animationSet = new AnimationSet(true);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                    alphaAnimation.setDuration(200);
                    animationSet.addAnimation(alphaAnimation);
                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mFlashrightBtn.setVisibility(GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mFlashrightBtn.startAnimation(animationSet);
                }
			}
		}
	};

	
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mFlashrightBtn) {
				if (mFlashLightOpen) {
					closeFlashLight();
				} else {
					openFlashLight();
				}
			}else if(v == mBtnAlbum){
				final PhotoPickerPage page = new PhotoPickerPage(getContext());
				page.setMode(PhotoPickerPage.MODE_SINGLE);
				page.setOnCancelListener(new OnCancelListener()
				{

					@Override
					public void onCancel(View v) {
					}
				});
				page.setOnChooseListener(new PhotoPickerPage.OnChooseImageListener()
				{
					@Override
					public void onChoose(ImageStore.ImageInfo[] imgs) {
						Main.getInstance().closePopupPage(page);
						if(imgs != null && imgs.length > 0)
						{
							mCameraManager.setPreviewCallback(null);
							mLoadingBar.setVisibility(VISIBLE);
							mLoadingBar.setText("正在识别图片中的二维码");
							mDecodeHandler.decodeImg(imgs[0].image);
						}
					}
				});
				Main.getInstance().popupPage(page);
			}
		}
	};

	//闪光灯
	private void openFlashLight() {
		if (mCameraManager != null) {
			if (mCameraManager.openFlashLight()) {
				mFlashLightOpen = true;
				mFlashrightBtn.setButtonImage(R.drawable.framework_flashlight_open_normal, R.drawable.framework_flashlight_open_normal);
				mFlashrightBtn.setText("轻点关闭");
			}
		}

	}

	private void closeFlashLight() {
		if(mCameraManager != null) {
			mFlashLightOpen = false;
			mFlashrightBtn.setButtonImage(R.drawable.framework_flashlight_open_hover, R.drawable.framework_flashlight_open_hover);
			if (mCameraManager.closeFlashLight()) {
				mFlashrightBtn.setText("轻点照亮");
			}
		}
	}
	
	//自动对焦
	private void beginAutoFocus()
	{
		postDelayed(mAutoFocusRunnable, 1000);
	}
	
	private void endAutoFocus()
	{
		removeCallbacks(mAutoFocusRunnable);
	}
	
	private Runnable mAutoFocusRunnable = new Runnable()
	{
		@Override
		public void run() {
			mCameraManager.autoFocus(null);
			postDelayed(mAutoFocusRunnable, 3000);
		}
	};
	
	//解码处理
	private PreviewCallback mPreviewCallback = new PreviewCallback()
	{
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Point point = mCameraManager.getPreviewSize();
			if(point != null)
			{
				Rect rcArea = new Rect();
				Rect rcScan = mScanView.getFrameRect();
				int w = mScanView.getWidth();
				int h = mScanView.getHeight();
				rcArea.left = point.y*rcScan.left/w;
				rcArea.top = point.x*rcScan.top/h;
				rcArea.right = point.y*rcScan.right/w;
				rcArea.bottom = point.x*rcScan.bottom/h;
				mDecodeHandler.decodeYuv(data, point.x, point.y, rcArea);
			}
		}
	};

	//扫码结束
	private DecodeHandler.OnDecodeCompleteListener mOnDecodeCompleteListener = new DecodeHandler.OnDecodeCompleteListener() {
		
		@Override
		public void onComplete(Result result) {
			playBeepSoundAndVibrate();
			String resultString = result.getText();
			mLoadingBar.setVisibility(GONE);
			if(resultString.equals("yue_err_404")){
				final AlertDialog alert = new AlertDialog(getContext());
				alert.setMessage("未在图中发现二维码");
				alert.setNegativeButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mCameraManager.setPreviewCallback(mPreviewCallback);
						alert.dismiss();
					}
				});
				alert.show();
				
//				Toast.makeText(getContext(), "未发现二维码", Toast.LENGTH_SHORT).show();
			}else{
				if (resultString.equals("")) {
					Toast.makeText(getContext(), "Scan failed!", Toast.LENGTH_SHORT).show();
				} else {
					if(mHandleDecodeResultListener!=null){
						mHandleDecodeResultListener.onHandleDecodeResult(resultString);
					}
				}
			}
		}
	};
	
	//镜头初始化
	private void initCamera(SurfaceHolder surfaceHolder) {
		mCameraManager = CameraManager.get();
		if(mCameraManager.openCamera(surfaceHolder, new Point(Utils.getScreenW(), Utils.getScreenH()-Utils.getRealPixel2(100))))
		{
			beginAutoFocus();
			mCameraManager.setPreviewCallback(mPreviewCallback);
		}
		else
		{
			Toast.makeText(getContext(), "镜头开启失败，请检查是否禁用了APP的镜头使用权限", Toast.LENGTH_LONG).show();
		}
	}

	//SurfaceView事件
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(!mHasSurface) {
			mHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasSurface = false;
	}

	//声音和震动
	private void initBeepSound() {
		if (mPlayBeep && mMediaPlayer == null) {
			((Activity)getContext()).setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(mBeepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mMediaPlayer.prepare();
			} catch (IOException e) {
				mMediaPlayer = null;
			}
		}
	}

	private void playBeepSoundAndVibrate() {
		if(mPlayBeep && mMediaPlayer != null) {
			mMediaPlayer.start();
		}
		if(mVibrate) {
			Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener mBeepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public void onResume() {
		SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
		if(mHasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		mPlayBeep = true;
		AudioManager audioService = (AudioManager)(getContext()).getSystemService(Context.AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			mPlayBeep = false;
		}
		initBeepSound();
		mVibrate = true;
		super.onResume();
	}

	@Override
	public void onPause() {
		endAutoFocus();
		CameraManager.get().closeCamera();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mFlashLightOpenSave = mFlashLightOpen;
		if(mFlashLightOpenSave){
			closeFlashLight();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		*/
/*if(mFlashLightOpenSave){
			openFlashLight();
		}*//*

	}

	@Override
	public void onClose() {
		super.onClose();
		closeFlashLight();
		endAutoFocus();
		mHandler.removeCallbacks(mCheckDarkRunnable);
		removeCallbacks(mCheckDarkRunnable);
		if (mCameraManager != null) {
			mCameraManager.stopPreview();
			mCameraManager.closeCamera();
		}
		mScanView.clear();
	}
}*/

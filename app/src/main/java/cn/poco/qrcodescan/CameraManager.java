package cn.poco.qrcodescan;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;

import java.util.List;


public final class CameraManager {
	private static CameraManager sCameraManager;
	public static CameraManager get()
	{
		return sCameraManager;
	}
	
	public static void init(Context context)
	{
		if(sCameraManager == null){
			sCameraManager = new CameraManager(context);
		}
	}
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private boolean mPreviewing = false;
	private Point mPreviewSize;
	private boolean mFocusing = false;
	private Camera mCamera = null;
	private long mStartPreviewTime = 0;
	private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
	private CameraManager(Context context)
	{
	}
	
	public boolean openCamera(SurfaceHolder holder, Point holderSize)
	{
		if(mCamera == null) {
			try {
				mCamera = Camera.open(mCameraId);
				if (mCamera == null) {
					return false;
				}
				mCamera.setPreviewDisplay(holder);
			} catch (Exception e) {
				e.printStackTrace();
				if(mCamera != null)
				{
					mCamera.release();
					mCamera = null;
				}
				return false;
			}

			initCameraParameters(mCamera, holderSize);

			startPreview();
		}
		return true;
	}
	
	public void closeCamera() {
		if(mCamera != null) {
			stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	public void autoFocus(final AutoFocusCallback callback) {
		//防止startPreview后立即调用autoFocus,有些机器可能有问题
		if (mCamera != null 
				&& mPreviewing 
				&& System.currentTimeMillis()-mStartPreviewTime > 100) {
			//正在对焦的时候不要再调用对焦，有些机器可能有问题
			if(mFocusing == false)
			{
				mFocusing = true;
				mCamera.autoFocus(new AutoFocusCallback()
				{
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						mFocusing = false;
						mHandler.removeCallbacks(mFocusTimeoutRunnable);
						if(callback != null){
							callback.onAutoFocus(success, camera);
						}
					}
				});
				mHandler.postDelayed(mFocusTimeoutRunnable, 5000);
			}
		}
	}
	
	private Runnable mFocusTimeoutRunnable = new Runnable()
	{
		@Override
		public void run() {
			mFocusing = false;
		}
	};
	
	public Point getPreviewSize()
	{
		Point ptSize = null;
		if(mCamera != null)
		{
			Size size = mCamera.getParameters().getPreviewSize();
			if(size != null)
			{
				ptSize = new Point(size.width, size.height);
			}
		}
		if(ptSize == null)
		{
			ptSize = mPreviewSize;
		}
		return ptSize;
	}
	
	public void startPreview() {
		if (mCamera != null && !mPreviewing) {
			mStartPreviewTime = System.currentTimeMillis();
			mCamera.startPreview();
			mPreviewing = true;
		}
	}

	public void stopPreview() {
		if (mCamera != null && mPreviewing) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mPreviewing = false;
		}
	}
	
	public void setPreviewCallback(PreviewCallback callback)
	{
		if (mCamera != null) {
			mCamera.setPreviewCallback(callback);
		}
	}
	
	public boolean openFlashLight() {
		try {
			Parameters parameters = mCamera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(parameters);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean closeFlashLight() {
		if (mCamera != null) {
			try {
				Parameters parameters = mCamera.getParameters();
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	private void initCameraParameters(Camera camera, Point holderSize)
	{
		Parameters parameters = camera.getParameters();
		List<Size> size  = parameters.getSupportedPreviewSizes();
		if(size != null)
		{
			Size bestSize = null;
			int holderPixels = holderSize.x*holderSize.y;
			int minPixels = holderPixels*8/10;
			float m = 10f;
			float r1 = (float)holderSize.x/(float)holderSize.y;
			for(Size s : size)
			{
				float r2 = (float)s.height/(float)s.width;
				float r3 = Math.abs(r1-r2);
				if(r3 < m && s.height*s.width > minPixels){
					m = r3;
					bestSize = s;
				}
			}
			if(bestSize == null)
			{
				int diff = Integer.MAX_VALUE;
				for(Size s : size)
				{
					int newDiff = Math.abs(s.height*s.width-holderPixels);
					if(newDiff < diff){
						diff = newDiff;
						bestSize = s;
					}
				}
			}
			if(bestSize != null){
				//Ensure that the camera resolution is a multiple of 8, as the screen may not be.
				mPreviewSize = new Point((bestSize.width >> 3) << 3, (bestSize.height >> 3) << 3);
			}
			if(mPreviewSize != null)
			{
				parameters.setPreviewSize(mPreviewSize.x, mPreviewSize.y);
			}
		}
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
	    camera.setDisplayOrientation(90);
		camera.setParameters(parameters);
	}
}

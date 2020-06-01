package cn.poco.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.display.SimplePreviewV2;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.preview.site.PreviewImgPageSite;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.video.VideoView;
import my.beautyCamera.R;

/**
 * 预览图片
 */
public class PreviewImgPage extends IPage
{
	//private static final String TAG = "预览图片";

	protected PreviewImgPageSite mSite;
	protected SimplePreviewV2 mView;
	protected VideoView mVideo;
	private ContentObserver mBrightObserver;

	private boolean isVideo;

	public PreviewImgPage(Context context, BaseSite site)
	{
		super(context, site);

		mSite = (PreviewImgPageSite)site;
		Init();

		TongJiUtils.onPageStart(getContext(), R.string.预览图片);

		this.setBackgroundColor(0xffffffff);
	}

	protected void Init()
	{
		mView = new SimplePreviewV2(getContext());
		mView.def_space_size = 0;
		this.addView(mView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mVideo = new VideoView(getContext());
		mVideo.setCallback(mVideoViewPlayCallback);
		this.addView(mVideo, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	private VideoView.OnVideoViewPlayCallback mVideoViewPlayCallback = new VideoView.OnVideoViewPlayCallback()
	{
		@Override
		public void onReady()
		{

		}

		@Override
		public void onPlayerError(ExoPlaybackException error, String errorMsg)
		{
			if (SysConfig.IsDebug())
			{
				new AlertDialog.Builder(getContext())
						.setCancelable(true)
						.setTitle(R.string.tips)
						.setMessage("player error " + (errorMsg != null ? errorMsg : ""))
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.dismiss();
								onBack();
							}
						}).create().show();
			}
		}

		@Override
		public void onVideoPlayCompleted()
		{
			if (mVideo != null)
			{
				mVideo.reset();
				mVideo.start();
			}
		}

		@Override
		public void onVideoSeekTo(long millSecond)
		{

		}

		@Override
		public void onVideoProgressStartTouch()
		{

		}

		@Override
		public void onVideoPlayPosition(long duration, long position)
		{

		}

		@Override
		public boolean isPause()
		{
			return false;
		}
	};

	@Override
	public void onClose()
	{
		TongJiUtils.onPageEnd(getContext(), R.string.预览图片);
		if (mVideo != null) {
			mVideo.release();
			mVideo = null;
		}
		if (isVideo) unregisterBrightnessObserver();
		mBrightObserver = null;
		mVideoViewPlayCallback = null;
		super.onClose();
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), R.string.预览图片);
		if(mVideo != null) mVideo.pause();
		if (isVideo) unregisterBrightnessObserver();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), R.string.预览图片);
		if(mVideo != null) mVideo.resume();
		if (isVideo) registerBrightnessObserver();
		super.onResume();
	}

	/**
	 * @param params img:String(非FASTBMP)
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		Boolean isVideo = (Boolean)params.get("isVideo");
		if(isVideo != null && isVideo)
		{
			boolean isFullVideoScreen = false;
			if (params.containsKey("isFullVideoScreen"))
			{
				isFullVideoScreen = (Boolean) params.get("isFullVideoScreen");
			}
			this.isVideo = true;
			removeView(mView);
			String video = (String) params.get("img");
			if (video == null || video.length() <= 0 || !new File(video).exists())
			{
				Toast.makeText(getContext(), R.string.preview_video_delete, Toast.LENGTH_LONG).show();
			}
			else
			{
				mVideo.setIs16_9FullScreen(true);
				mVideo.setAdaptation(!isFullVideoScreen);
				mVideo.setProgressViewShow(false);
				ArrayList<String> path = new ArrayList<>();
				path.add(video);
				mVideo.setVideoPath(path);
				mVideo.prepared();
				mVideo.start();
			}
			mVideo.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mSite != null)
					{
						mSite.OnBack(getContext());
					}
				}
			});
			mBrightObserver = new BrightObserver(new Handler());
			registerBrightnessObserver();
		}
		else
		{
			removeView(mVideo);
			mVideo.release();
			mVideo = null;

			Object img = params.get("img");
			if(img == null)
			{
				Toast.makeText(getContext(), R.string.preview_pic_delete, Toast.LENGTH_LONG).show();
			}
			else
			{
				if (img instanceof String)
				{
					if (!new File((String) img).exists())
					{
						Toast.makeText(getContext(), R.string.preview_pic_delete, Toast.LENGTH_LONG).show();
					}
				}
				mView.SetImage(img);
			}
			mView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mSite != null)
					{
						mSite.OnBack(getContext());
					}
				}
			});
		}
	}

	@Override
	public void onBack()
	{
		if (mSite != null)
		{
			mSite.OnBack(getContext());
		}
	}

	public void registerBrightnessObserver()
	{
		if (mBrightObserver != null)
		{
			getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, mBrightObserver);
		}
	}

	public void unregisterBrightnessObserver()
	{
		if (mBrightObserver != null)
		{
			getContext().getContentResolver().unregisterContentObserver(mBrightObserver);
		}
	}

	private class BrightObserver extends ContentObserver
	{

		/**
		 * Creates a content observer.
		 *
		 * @param handler The handler to run {@link #onChange} on, or null if none.
		 */
		public BrightObserver(Handler handler)
		{
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange)
		{
			super.onChange(selfChange);
			if (getContext() == null) return;
			int mode = -1;
			int brightness = 0;
			try
			{
				mode = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
				brightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
			}
			catch (Settings.SettingNotFoundException e)
			{
				e.printStackTrace();
			}
			if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL && getContext() != null)
			{
				Window window = ((Activity) getContext()).getWindow();
				WindowManager.LayoutParams wmParams = window.getAttributes();
				wmParams.screenBrightness = brightness / 255f;
				window.setAttributes(wmParams);
			}
		}
	}
}

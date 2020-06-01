package cn.poco.live.sticker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.config.StickerImageViewConfig;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StatusMgr;
import cn.poco.camera3.mgr.StickerResMgr;
import cn.poco.camera3.ui.sticker.StickerImageView;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * 直播 单个贴纸 view
 * Created by Gxx on 2017/10/12.
 */

public class StickerItemView extends FrameLayout
{
	private StickerImageView mStickerImageView;
	private FrameLayout mMixView;
	private ImageView mNewBk;
	private ImageView mMixIcon;
	private Drawable mLoadingDrawable;

	public StickerItemView(@NonNull Context context)
	{
		super(context);
		initView(context);
	}

	public void ClearAll()
	{
		if(mStickerImageView != null)
		{
			Glide.clear(mStickerImageView);
		}
	}

	private void initView(Context context)
	{
		mStickerImageView = new StickerImageView(context);
		mStickerImageView.setId(R.id.sticker_image_view);
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		addView(mStickerImageView, params);

		mMixView = new FrameLayout(context);
		mMixView.setVisibility(GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.END | Gravity.BOTTOM;
		params.rightMargin = CameraPercentUtil.WidthPxToPercent(6);
		params.bottomMargin = CameraPercentUtil.HeightPxToPercent(6);
		addView(mMixView, params);
		{
			mNewBk = new ImageView(context);
			mNewBk.setVisibility(GONE);
			mNewBk.setImageResource(R.drawable.sticker_tip_new_bg);
			params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(34), CameraPercentUtil.HeightPxToPercent(34));
			params.gravity = Gravity.CENTER;
			ImageUtils.AddSkin(context, mNewBk);
			mMixView.addView(mNewBk, params);

			mMixIcon = new ImageView(context);
			params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(34), CameraPercentUtil.HeightPxToPercent(34));
			params.gravity = Gravity.CENTER;
			mMixView.addView(mMixIcon, params);
		}
	}

	public void initImageConfig()
	{
		if(mStickerImageView != null)
		{
			mStickerImageView.init();
		}
	}

	public void setThumb(Object thumb, boolean isDownloaded)
	{
		if(mLoadingDrawable == null)
		{
			mLoadingDrawable = StickerImageViewConfig.getLoadingDrawable();
		}

		//gif格式下，已下载素材只显示第一帧，未下载素材动态显示
		if(thumb != null)
		{
			if(thumb instanceof Integer)
			{
				Glide.with(getContext()).load((Integer)thumb).placeholder(mLoadingDrawable).into(mStickerImageView);
			}
			else if(thumb instanceof String)
			{
				if (isDownloaded) {
					Glide.with(getContext()).load((String)thumb).asBitmap().placeholder(mLoadingDrawable).into(mStickerImageView);
				} else {
					Glide.with(getContext()).load((String) thumb).placeholder(mLoadingDrawable).into(mStickerImageView);
				}
			}
		}
	}

	public void setIsSelected(boolean isSelected)
	{
		if(mStickerImageView != null)
		{
			mStickerImageView.setSelected(isSelected);
		}
	}

	private void setDownloadProgress(float progress)
	{
		if(mStickerImageView != null)
		{
			mStickerImageView.showGrayProgressBKColor(false);
			mStickerImageView.setProgress(progress);
		}
	}

	public void setStickerStatus(StickerInfo info)
	{
		mMixView.setVisibility(GONE);
		mNewBk.setVisibility(GONE);

		switch(info.mStatus)
		{
			case StatusMgr.Type.NEED_DOWN_LOAD:// 未下载
			case StatusMgr.Type.NEW: // 新
			case StatusMgr.Type.LOCK: // 上锁
			case StatusMgr.Type.LIMIT: // 限时
			{
				mMixView.setVisibility(VISIBLE);
				break;
			}
		}

		setDownloadProgress(info.mProgress);
		setMixIconLogo(info.mStatus);
	}

	private void setMixIconLogo(int status)
	{
		if(mMixIcon != null)
		{
			int resID = 0;
			switch(status)
			{
				case StatusMgr.Type.NEED_DOWN_LOAD:
				{
					resID = R.drawable.sticker_download_gary;
					break;
				}

				case StatusMgr.Type.LOCK:
				{
					resID = R.drawable.sticker_lock_gary;
					break;
				}

				case StatusMgr.Type.NEW:
				{
					mNewBk.setVisibility(VISIBLE);
					resID = R.drawable.sticker_tip_new;
					break;
				}

				case StatusMgr.Type.LIMIT:
				{
					resID = R.drawable.sticker_limit;
					break;
				}
			}

			if(resID != 0)
			{
				mMixIcon.setImageResource(resID);
			}
		}
	}
}

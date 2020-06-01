package cn.poco.water;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * 水印item
 * Created by admin on 2017/12/13.
 */

public class WatermarkItemView extends FrameLayout
{
	private ImageView mContent;
	private ImageView mSelected;

	public WatermarkItemView(@NonNull Context context, int viewType)
	{
		super(context);
		initView(context, viewType);
	}

	private void initView(Context context, int viewType)
	{
		FrameLayout.LayoutParams params;

		mContent = new ImageView(context);
		if(viewType == WatermarkAdapter.ItemType.NON)
		{
			params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(140), CameraPercentUtil.WidthPxToPercent(140));
		}
		else
		{
			params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(200), CameraPercentUtil.WidthPxToPercent(181));
		}

		// 第一列 或 无
		if(viewType == WatermarkAdapter.ItemType.NON || viewType == WatermarkAdapter.ItemType.WATERMARK_FST)
		{
			params.gravity = Gravity.CENTER;
		}
		else
		{
			params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
			params.rightMargin = CameraPercentUtil.WidthPxToPercent(80);
		}
		addView(mContent, params);

		mSelected = new ImageView(context);
		mSelected.setVisibility(View.GONE);
		mSelected.setImageResource(R.drawable.watermark_page_sel_logo);
		params = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(36), CameraPercentUtil.WidthPxToPercent(36));
		params.gravity = Gravity.END;
		addView(mSelected, params);
		ImageUtils.AddSkin(context, mSelected);
	}

	public void setSelected(boolean selected)
	{
		if(mSelected != null)
		{
			mSelected.setVisibility(selected ? VISIBLE : GONE);
		}

		setBackgroundColor(selected ? 0x0DFFFFFF : Color.TRANSPARENT);
	}

	public void setThumb(Object thumb)
	{
		if(thumb != null)
		{
			if(thumb instanceof Integer)
			{
				Glide.with(getContext()).load((Integer)thumb).error(R.drawable.music_cover_default).into(mContent);
			}
			else if(thumb instanceof String)
			{
				Glide.with(getContext()).load((String)thumb).error(R.drawable.music_cover_default).into(mContent);
			}
		}
	}

	public void clearAll()
	{
		Glide.clear(mContent);
		setOnTouchListener(null);
	}
}

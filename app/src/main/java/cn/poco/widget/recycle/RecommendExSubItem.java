package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.poco.advanced.ImageUtils;
import cn.poco.recycleview.AbsAdapter;
import cn.poco.recycleview.BaseItem;
import cn.poco.tianutils.ShareData;

/**
 * Created by lgd on 2017/5/9.
 */

public class RecommendExSubItem extends BaseItem
{
	private static final String TAG = "RecommendSubItem";
	private ImageView mLogo;
	private FrameView mFrameView;

	public RecommendExSubItem(@NonNull Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		LayoutParams params;
		mLogo = new ImageView(getContext());
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mLogo, params);

		mFrameView = new FrameView(getContext());
		mFrameView.setVisibility(View.GONE);
		params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mFrameView, params);
	}

	@Override
	public void SetData(AbsAdapter.ItemInfo info, int index)
	{
		if(info instanceof RecommendExAdapter.ItemInfo)
		{
			RecommendExAdapter.ItemInfo itemInfo = (RecommendExAdapter.ItemInfo)info;
			//m_uris 第一个是组
			if(index < itemInfo.m_logos.length)
			{
				Glide.with(getContext()).load(itemInfo.m_logos[index]).into(mLogo);
			}
		}
	}

	@Override
	public void onSelected()
	{
		mFrameView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onUnSelected()
	{
		mFrameView.setVisibility(View.GONE);
	}

	@Override
	public void onClick()
	{

	}

	public static class FrameView extends View
	{
		private Paint mPaint;
		private int strokeW;
		private int color;

		public FrameView(Context context)
		{
			super(context);
			init();
		}

		private void init()
		{
			strokeW = ShareData.PxToDpi_xhdpi(4);
			color = ImageUtils.GetSkinColor(0x66e75988);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setColor(color);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(strokeW);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
			canvas.drawRect(strokeW / 2, strokeW / 2, getWidth() - strokeW / 2, getHeight() - strokeW / 2, mPaint);
		}

		public void setStrokeW(int strokeW)
		{
			this.strokeW = strokeW;
			invalidate();
		}

		public void setColor(int color)
		{
			this.color = color;
			invalidate();
		}
	}
}

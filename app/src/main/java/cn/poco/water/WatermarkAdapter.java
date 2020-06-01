package cn.poco.water;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.dynamicSticker.newSticker.MyHolder;
import cn.poco.filter4.WatermarkItem;
import cn.poco.resource.WatermarkResMgr2;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by Gxx on 2017/12/13.
 */

public class WatermarkAdapter extends RecyclerView.Adapter
{
	interface ItemType
	{
		int NON = 0;
		int WATERMARK_FST = 1;
		int WATERMARK_SEC = 2;
	}

	private ArrayList<WatermarkItem> mData;
	private int mSpanSize;
	private int mSelectedID;
	private OnAnimationClickListener mOnAnimClickListener;
	private int mNonID;

	public WatermarkAdapter(Context context, int spanSize)
	{
		mSpanSize = spanSize;
		SettingInfo info = SettingInfoMgr.GetSettingInfo(context);
		mSelectedID = info.GetPhotoWatermarkId(context.getResources().getInteger(R.integer.滤镜_水印_流星));
		mNonID = context.getResources().getInteger(R.integer.滤镜_水印_无);
		initCB();
		init(context);
	}

	private int getPositionByID(int id)
	{
		if(mData != null)
		{
			int size = mData.size();
			for(int i = 0; i < size; i++)
			{
				WatermarkItem info = mData.get(i);
				if(info != null && info.mID == id)
				{
					return i;
				}
			}
		}
		return -1;
	}

	private void initCB()
	{
		mOnAnimClickListener = new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				int position = (int)v.getTag();
				WatermarkItem info = mData.get(position);
				if(info.mID != mSelectedID)
				{
					int old_position = getPositionByID(mSelectedID);
					mSelectedID = info.mID;
					notifyItemChanged(position);
					notifyItemChanged(old_position);
				}
			}
		};
	}

	private void init(Context context)
	{
		mData = WatermarkResMgr2.getInstance().sync_GetLocalRes(context, null);
	}

	public int getSelectedID()
	{
		return mSelectedID;
	}

	@Override
	public int getItemViewType(int position)
	{
		WatermarkItem info = mData.get(position);

		if(info.mID != mNonID)
		{
			if(position % mSpanSize != 0)
			{
				return ItemType.WATERMARK_SEC;
			}

			return ItemType.WATERMARK_FST;

		}
		return ItemType.NON;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		WatermarkItemView itemView = new WatermarkItemView(parent.getContext(), viewType);
		itemView.setOnTouchListener(mOnAnimClickListener);
		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(CameraPercentUtil.WidthPxToPercent(360), CameraPercentUtil.WidthPxToPercent(200));
		itemView.setLayoutParams(params);
		return new MyHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		if(holder instanceof MyHolder)
		{
			WatermarkItemView itemView = ((MyHolder)holder).getItemView();
			itemView.setTag(position);

			WatermarkItem info = mData.get(position);

			if(info != null)
			{
				itemView.setThumb(info.thumb);
				itemView.setSelected(info.mID == mSelectedID);
			}
		}
	}

	@Override
	public int getItemCount()
	{
		return mData != null ? mData.size() : 0;
	}
}

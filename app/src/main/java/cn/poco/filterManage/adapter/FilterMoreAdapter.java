package cn.poco.filterManage.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.poco.filterManage.FilterMorePage;
import cn.poco.filterManage.model.FilterInfo;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/3/29
 */
public class FilterMoreAdapter extends RecyclerView.Adapter<FilterMoreAdapter.ViewHolder> {

	private static final int TYPE_EMPTY = 0;
	private static final int TYPE_ITEM = 1;

	private Context mContext;

	private List<FilterInfo> mItems;

	private OnItemClickListener mOnItemClickListener;

	public FilterMoreAdapter(Context context, List<FilterInfo> items) {
		mContext = context;
		mItems = items;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == TYPE_EMPTY) {
			TextView empty = new TextView(parent.getContext());
			empty.setText(R.string.material_download_none_tip);
			empty.setTextColor(0x99000000);
			empty.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f);
			empty.setGravity(Gravity.CENTER);
			RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			empty.setLayoutParams(params);
			return new ViewHolder(empty);
		}

		ItemView itemView = new ItemView(parent.getContext());
		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(374));
		itemView.setLayoutParams(params);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		if (getItemViewType(position) == TYPE_ITEM) {
			ItemView itemView = (ItemView)holder.itemView;
			FilterInfo info = mItems.get(position);
			itemView.setDy(info.getDy());
			Glide.with(mContext).load(info.image).centerCrop().into(itemView.image);
			itemView.text.setText(info.name);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onClick(v, holder.getAdapterPosition());
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		if (isEmpty()) {
			return 1;
		}

		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (isEmpty()) {
			return TYPE_EMPTY;
		}
		return TYPE_ITEM;
	}

	private boolean isEmpty() {
		return mItems == null || mItems.isEmpty();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View itemView) {
			super(itemView);
		}
	}

	public static class ItemView extends FrameLayout {

		public ImageView image;
		public TextView text;

		private float mDy = 0;

		public ItemView(@NonNull Context context) {
			super(context);

			initViews();
		}

		private void initViews() {
			LayoutParams params;
			image = new ImageView(getContext());
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(720));
			params.gravity = Gravity.CENTER;
			addView(image, params);

			View view = new View(getContext());
			view.setBackgroundColor(Color.BLACK);
			view.setAlpha(0.4f);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			addView(view, params);

			text = new TextView(getContext());
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
			text.setTextColor(Color.WHITE);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			addView(text, params);
		}

//		@Override
//		protected void dispatchDraw(Canvas canvas) {
//			canvas.save();
//			canvas.translate(0, mDy);
//			super.dispatchDraw(canvas);
//			canvas.restore();
//		}

		@Override
		protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
			if (child == image) {
				canvas.save();
				canvas.translate(0, mDy);
				boolean resule = super.drawChild(canvas, child, drawingTime);
				canvas.restore();
				return resule;
			}

			return super.drawChild(canvas, child, drawingTime);
		}

		public void setDy(float dy) {
			mDy = dy;
			if (mDy > FilterMorePage.sMaxDelta) {
				mDy = FilterMorePage.sMaxDelta;
			} else if (mDy < -FilterMorePage.sMaxDelta) {
				mDy = -FilterMorePage.sMaxDelta;
			}
			invalidate();
		}
	}

	public interface OnItemClickListener {
		void onClick(View view, int position);
	}
}

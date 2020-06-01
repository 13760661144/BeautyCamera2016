package cn.poco.filterManage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.utils.RoundCornerTransformation;
import cn.poco.filterManage.model.FilterInfo;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2017/3/30
 */
public class FilterManageAdapter extends RecyclerView.Adapter<FilterManageAdapter.ViewHolder> {

	private static final int TYPE_EMPTY = 0;
	private static final int TYPE_ITEM = 1;

	private OnItemClickListener mOnItemClickListener;
	private OnCheckChangeListener mOnCheckChangeListener;

	private Context mContext;

	private List<FilterInfo> mItems;

	public FilterManageAdapter(Context context, List<FilterInfo> items) {
		mContext = context;
		mItems = items;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == TYPE_EMPTY) {
			TextView empty = new TextView(parent.getContext());
			empty.setText(R.string.material_manage_none_tip);
			empty.setTextColor(0x99000000);
			empty.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f);
			empty.setGravity(Gravity.CENTER);
			RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			empty.setLayoutParams(params);
			return new ViewHolder(empty);
		}

		ItemView itemView = new ItemView(parent.getContext());
		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(116));
		params.leftMargin = params.rightMargin = ShareData.PxToDpi_xhdpi(22);
		itemView.setLayoutParams(params);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		if (getItemViewType(position) == TYPE_ITEM) {
			final ItemView itemView = (ItemView) holder.itemView;
			final FilterInfo info = mItems.get(position);
			Glide.with(mContext).load(info.image)
					.bitmapTransform(new CenterCrop(mContext),
									 new RoundCornerTransformation(mContext, ShareData.PxToDpi_xhdpi(10), 0))
					.into(itemView.image);

			itemView.text.setText(info.name);
			if (info.check) {
				itemView.check.setVisibility(View.VISIBLE);
				itemView.unCheck.setVisibility(View.INVISIBLE);
			} else {
				itemView.check.setVisibility(View.INVISIBLE);
				itemView.unCheck.setVisibility(View.VISIBLE);
			}

			itemView.setOnTouchListener(new OnAnimationClickListener() {
				@Override
				public void onAnimationClick(View v) {
					if (info.check) {
						itemView.unCheck.setVisibility(View.VISIBLE);
						itemView.check.setVisibility(View.INVISIBLE);
					} else {
						itemView.unCheck.setVisibility(View.INVISIBLE);
						itemView.check.setVisibility(View.VISIBLE);
					}

					final int itemPosition = holder.getAdapterPosition();

					if (mOnCheckChangeListener != null && itemPosition >= 0) {
						mOnCheckChangeListener.onCheck(holder.itemView, itemPosition, !info.check);
					}
				}

				@Override
				public void onTouch(View v) {

				}

				@Override
				public void onRelease(View v) {

				}
			});
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (isEmpty()) {
			return TYPE_EMPTY;
		}
		return TYPE_ITEM;
	}

	@Override
	public int getItemCount() {
		if (isEmpty()) {
			return 1;
		}

		return mItems.size();
	}

	private boolean isEmpty() {
		return mItems == null || mItems.isEmpty();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
		mOnCheckChangeListener = onCheckChangeListener;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View itemView) {
			super(itemView);
		}
	}

	private static class ItemView extends FrameLayout {

		private Context mContext;

		public ImageView image;
		public TextView text;
		public FrameLayout check;
		public ImageView unCheck;

		public ItemView(@NonNull Context context) {
			super(context);
			mContext = context;

			initViews();
		}

		private void initViews() {
			Drawable background = DrawableUtils.shapeDrawable(Color.WHITE, ShareData.PxToDpi_xhdpi(10));
			DrawableUtils.setBackground(this, background);

			LayoutParams params;
			image = new ImageView(mContext);
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(88), ShareData.PxToDpi_xhdpi(88));
			params.leftMargin = ShareData.PxToDpi_xhdpi(14);
			params.gravity = Gravity.CENTER_VERTICAL;
			addView(image, params);

			text = new TextView(mContext);
			text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			text.setTextColor(0xcc000000);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.leftMargin = ShareData.PxToDpi_xhdpi(128);
			params.gravity = Gravity.CENTER_VERTICAL;
			addView(text, params);

			FrameLayout checkLayout = new FrameLayout(mContext);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
			addView(checkLayout, params);
			{
				check = new FrameLayout(mContext);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				checkLayout.addView(check, params);
				{
					ImageView checkBg = new ImageView(mContext);
					checkBg.setImageResource(R.drawable.new_material4_checkbox_over_bg);
					ImageUtils.AddSkin(mContext, checkBg);
					params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					check.addView(checkBg, params);

					ImageView checkTip = new ImageView(mContext);
					checkTip.setImageResource(R.drawable.new_material4_checkbox_over);
					params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.CENTER;
					check.addView(checkTip, params);
				}
				check.setVisibility(INVISIBLE);

				unCheck = new ImageView(mContext);
				unCheck.setImageResource(R.drawable.new_material4_checkbox_out);
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				checkLayout.addView(unCheck, params);
			}
		}
	}

	public interface OnItemClickListener {
		void onClick(View view, int position);
	}

	public interface OnCheckChangeListener {
		void onCheck(View view, int position, boolean checked);
	}
}

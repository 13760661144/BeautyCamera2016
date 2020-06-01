package cn.poco.beauty.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.poco.beautify.EffectType;
import cn.poco.beauty.model.BeautyItem;
import cn.poco.beauty.view.ItemView;
import cn.poco.beauty.view.UserItemView;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;

import static cn.poco.beauty.adapter.BeautyAdapter.BeautyViewHolder;

/**
 * Created by: fwc
 * Date: 2016/12/21
 */
public class BeautyAdapter extends RecyclerView.Adapter<BeautyViewHolder> {

	private static final int TYPE_DEFAULT = 1;
	private static final int TYPE_USER = 2;

	private Context mContext;
	private List<BeautyItem> mItems;

	public OnItemClickListener mItemClickListener;

	public BeautyAdapter(Context context, List<BeautyItem> items) {
		mContext = context;
		mItems = items;
	}

	@Override
	public BeautyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View itemView;
		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
				ShareData.PxToDpi_xhdpi(146), ShareData.PxToDpi_xhdpi(187));

		if (viewType == TYPE_USER) {
			itemView = new UserItemView(mContext);
		} else {
			itemView = new ItemView(mContext);
		}

		itemView.setLayoutParams(params);
		return new BeautyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final BeautyViewHolder holder, int position) {
		holder.itemView.setOnTouchListener(mOnTouchListener);

		BeautyItem item = mItems.get(position);

		if (getItemViewType(position) == TYPE_USER) {
			((UserItemView)holder.itemView).setUserSelected(item.select);
			return;
		}
		ItemView itemView = (ItemView) holder.itemView;

		if (item.thumb != null && !item.thumb.isRecycled()) {
			itemView.image.setImageBitmap(item.thumb);
		}
		if (item.select) {
			itemView.selectedText.setText(item.title);
			itemView.text.setVisibility(View.GONE);
			itemView.selected.setVisibility(View.VISIBLE);
			if (item.type == EffectType.EFFECT_NONE) {
				itemView.selectedIcon.setVisibility(View.GONE);
				itemView.selectTip.setVisibility(View.VISIBLE);
			} else {
				itemView.selectTip.setVisibility(View.GONE);
				itemView.selectedIcon.setVisibility(View.VISIBLE);
			}
		} else {
			itemView.text.setText(item.title);
			itemView.text.setVisibility(View.VISIBLE);
			itemView.selectedIcon.setVisibility(View.GONE);
			itemView.selected.setVisibility(View.GONE);
			itemView.selectTip.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (mItems.get(position).type == EffectType.EFFECT_USER) {
			return TYPE_USER;
		}

		return TYPE_DEFAULT;
	}

	public void setOnItemCLickListener(OnItemClickListener onItemClickListener) {
		mItemClickListener = onItemClickListener;
	}

	static class BeautyViewHolder extends RecyclerView.ViewHolder {

		BeautyViewHolder(View itemView) {
			super(itemView);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	private View.OnTouchListener mOnTouchListener = new OnAnimationClickListener() {
		@Override
		public void onAnimationClick(View v) {
			if (mItemClickListener != null) {
				int position = ((RecyclerView.LayoutParams)v.getLayoutParams()).getViewLayoutPosition();
				mItemClickListener.onItemClick(v, position);
			}
		}

		@Override
		public void onTouch(View v) {

		}

		@Override
		public void onRelease(View v) {

		}
	};
}

package cn.poco.filterManage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import cn.poco.filterManage.model.FilterInfo;
import cn.poco.tianutils.ShareData;

/**
 * Created by: fwc
 * Date: 2017/3/27
 */
public class FilterDetailAdapter extends RecyclerView.Adapter<FilterDetailAdapter.ViewHolder> {

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;

	private Context mContext;

	private List<FilterInfo> mItems;

	private int mFilterThemeColor;

	public FilterDetailAdapter(Context context, List<FilterInfo> items, int filterThemeColor) {
		mContext = context;
		mItems = items;
		mFilterThemeColor = filterThemeColor;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		if (viewType == TYPE_HEADER) {
			FilterInfo item = mItems.get(0);
			FilterHeaderView headerView = new FilterHeaderView(parent.getContext());
			Glide.with(parent.getContext()).load(item.image).centerCrop().into(headerView.image.image);
			headerView.title.setText(item.name);
			headerView.description.setText(item.description);
			RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			headerView.setLayoutParams(params);
			return new ViewHolder(headerView);
		}

		FilterItemView itemView = new FilterItemView(parent.getContext());
		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(720));
		itemView.setLayoutParams(params);

		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (getItemViewType(position) == TYPE_ITEM) {
			FilterInfo info = mItems.get(position);
			final FilterItemView itemView = (FilterItemView)holder.itemView;
			itemView.label.setText(info.name);
			itemView.label.setBackgroundColor(mFilterThemeColor);
			itemView.label.setAlpha(0);
			Glide.with(mContext).load(info.image).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
				@Override
				public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
					return false;
				}

				@Override
				public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
					itemView.label.animate().alpha(1).setDuration(100);
					return false;
				}
			}).into(itemView.image);
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_HEADER;
		}

		return TYPE_ITEM;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View itemView) {
			super(itemView);
		}
	}

	private static class FilterHeaderView extends LinearLayout {

		public FilterItemView image;
		public TextView title;
		public TextView description;

		public FilterHeaderView(@NonNull Context context) {
			super(context);

			setOrientation(VERTICAL);
			setBackgroundColor(Color.WHITE);
			setPadding(0, 0, 0, ShareData.PxToDpi_xhdpi(96));

			initViews();
		}

		private void initViews() {
			LayoutParams params;

			image = new FilterItemView(getContext());
			image.label.setVisibility(GONE);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			addView(image, params);

			title = new TextView(getContext());
			title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
			title.setTextColor(0xe5000000);
			title.setIncludeFontPadding(false);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.topMargin = ShareData.PxToDpi_xhdpi(74); // 相应减少6
			addView(title, params);

			description = new TextView(getContext());
			description.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			description.setTextColor(Color.BLACK);
			description.setIncludeFontPadding(false);
			description.setGravity(Gravity.CENTER);
			description.setLineSpacing(0, 1.2f);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.topMargin = ShareData.PxToDpi_xhdpi(40); // 相应减少10
			params.leftMargin = params.rightMargin = ShareData.PxToDpi_xhdpi(60);
			addView(description, params);
		}
	}

	private static class FilterItemView extends FrameLayout {

		public ImageView image;
		public TextView label;

		public FilterItemView(@NonNull Context context) {
			super(context);

			initViews();
		}

		private void initViews() {
			LayoutParams params;

			image = new ImageView(getContext());
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			addView(image, params);

			label = new TextView(getContext());
			label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			label.setTextColor(Color.WHITE);
			label.setGravity(Gravity.CENTER);
			label.setPadding(ShareData.PxToDpi_xhdpi(22), 0, ShareData.PxToDpi_xhdpi(22), 0);
			params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ShareData.PxToDpi_xhdpi(56));
			params.gravity = Gravity.END;
			params.topMargin = params.rightMargin = ShareData.PxToDpi_xhdpi(16);
			addView(label, params);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		}
	}
}

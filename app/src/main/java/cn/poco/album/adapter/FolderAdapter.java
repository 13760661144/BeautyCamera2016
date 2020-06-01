package cn.poco.album.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.poco.album.model.FolderInfo;
import cn.poco.album.view.FolderItemView;
import cn.poco.cloudalbumlibs.utils.ImageLoader;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/23
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

	private Context mContext;

	private List<FolderInfo> mItems;

	private OnFolderItemClickListener mListener;

	public FolderAdapter(Context context, List<FolderInfo> items) {
		mContext = context;
		mItems = items;
	}

	public void setOnFolderItemClickListener(OnFolderItemClickListener listener) {
		mListener = listener;
	}

	@Override
	public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		FolderItemView folderItemView = new FolderItemView(parent.getContext());
		RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		folderItemView.setLayoutParams(params);
		return new FolderViewHolder(folderItemView);
	}

	@Override
	public void onBindViewHolder(final FolderViewHolder holder, int position) {

		FolderItemView folderItemView = (FolderItemView)holder.itemView;
		FolderInfo info = mItems.get(position);
		if (info.getCount() > 0) {
			if (info.getCover().endsWith(".gif")) {
				// asBitmap()没有渐变动画
				Glide.with(folderItemView.getContext()).load(info.getCover()).asBitmap().into(folderItemView.image);
			} else {
				ImageLoader.displayImage(mContext, info.getCover(), folderItemView.image);
			}
		} else {
			folderItemView.image.setImageResource(R.drawable.cloudalbum_default_placeholder);
		}

		folderItemView.name.setText(info.getName());
		folderItemView.number.setText(String.valueOf(info.getCount()));

		folderItemView.setOnTouchListener(new OnAnimationClickListener() {
			@Override
			public void onAnimationClick(View v) {
				if (mListener != null) {
					mListener.onItemClick(v, holder.getAdapterPosition());
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

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	static class FolderViewHolder extends RecyclerView.ViewHolder {

		FolderViewHolder(View itemView) {
			super(itemView);
		}
	}

	public interface OnFolderItemClickListener {
		void onItemClick(View view, int position);
	}
}

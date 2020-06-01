package cn.poco.album.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.album.model.PhotoInfo;
import cn.poco.album.view.PhotoView;
import cn.poco.cloudalbumlibs.utils.T;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/23
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

	private Context mContext;

	private List<PhotoInfo> mItems;

	private OnPhotoItemClickListener mListener;
	private OnLookBigListener mBigListener;

	private boolean mCloudMode = false;

	private boolean mShowEdit;

	public PhotoAdapter(Context context, List<PhotoInfo> items, boolean showEdit) {
		mContext = context;
		mItems = items;
		mShowEdit = showEdit;
	}

	public PhotoInfo getItem(int position) {
		return mItems.get(position);
	}

	public void setOnPhotoItemClickListener(OnPhotoItemClickListener listener) {
		mListener = listener;
	}

	public void setOnLookBigListener(OnLookBigListener lookBigListener) {
		mBigListener = lookBigListener;
	}

	@Override
	public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new PhotoViewHolder(new PhotoView(parent.getContext()));
	}

	@Override
	public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

		final PhotoView photoView = (PhotoView) holder.itemView;
		PhotoInfo photoInfo = mItems.get(position);
		// 去掉占位符
		String path = photoInfo.getImagePath();
		if (path.endsWith(".gif")) {
			Glide.with(mContext).load(path).asBitmap().into(photoView.image);
		} else {
			Glide.with(mContext).load(path).into(photoView.image);
		}

		photoView.setSelected(photoInfo.isSelected());
		if (mShowEdit) {
			photoView.setEditView(photoInfo.isShowEdit());
		}

		if (mCloudMode) {
			photoView.lookBig.setVisibility(View.VISIBLE);
			photoView.lookBig.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mBigListener != null) {
						mBigListener.onClick(photoView, position);
					}
				}
			});
		}

		photoView.setOnTouchListener(new OnAnimationClickListener() {

			@Override
			public void onAnimationClick(View v) {
				if (mItems != null && mItems.size() > position)
				{
					if (validatePhoto(mItems.get(position)) && mListener != null)
					{
						mListener.onItemClick(position);
					}
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

	/**
	 * 验证图片
	 * @param photoInfo 图片信息
	 * @return true: 通过验证
	 */
	private boolean validatePhoto(PhotoInfo photoInfo) {

		if (!ImageUtils.AvailableImg(photoInfo.getImagePath())) {
			T.showShort(mContext, R.string.photo_not_exist);
			return false;
		}

		return true;
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	static class PhotoViewHolder extends RecyclerView.ViewHolder {

		PhotoViewHolder(View itemView) {
			super(itemView);
		}
	}

	public void setCloudMode() {
		mCloudMode = true;
	}

	public interface OnPhotoItemClickListener {
		void onItemClick(int position);
	}

	public interface OnLookBigListener {
		void onClick(View view, int position);
	}
}

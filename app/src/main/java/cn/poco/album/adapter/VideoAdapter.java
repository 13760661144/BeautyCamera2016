package cn.poco.album.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;
import java.util.Locale;

import cn.poco.album.model.VideoInfo;
import cn.poco.album.view.VideoItemView;
import cn.poco.cloudalbumlibs.utils.T;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by: fwc
 * Date: 2016/11/23
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.PhotoViewHolder> {

	private Context mContext;

	private List<VideoInfo> mItems;

	private OnPhotoItemClickListener mListener;

	public VideoAdapter(Context context, List<VideoInfo> items) {
		mContext = context;
		mItems = items;
	}

	public void setOnPhotoItemClickListener(OnPhotoItemClickListener listener) {
		mListener = listener;
	}

	@Override
	public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new PhotoViewHolder(new VideoItemView(parent.getContext()));
	}

	@SuppressWarnings("all")
	@Override
	public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

		final VideoItemView photoView = (VideoItemView) holder.itemView;
		VideoInfo info = mItems.get(position);
		// 去掉占位符
		Glide.with(mContext).load(info.getPath()).asBitmap().into(photoView.image);

		photoView.duration.setText(getFormatTime(info.getDuration()));
		photoView.duration.setVisibility(View.VISIBLE);

		photoView.setOnTouchListener(new OnAnimationClickListener() {

			@Override
			public void onAnimationClick(View v) {
				if (validateVideo(mItems.get(position)) && mListener != null) {
					mListener.onItemClick(position);
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
	private boolean validateVideo(VideoInfo photoInfo) {

		if (!new File(photoInfo.getPath()).exists()) {
			T.showShort(mContext, R.string.video_not_exist);
			return false;
		}

		return true;
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	private String getFormatTime(long time) {
		time /= 1000;

		long minute = (time / 60) % 60;
		long second = time % 60;

		return String.format(Locale.getDefault(), "%02d:%02d", minute, second);
	}

	static class PhotoViewHolder extends RecyclerView.ViewHolder {

		PhotoViewHolder(View itemView) {
			super(itemView);
		}
	}

	public interface OnPhotoItemClickListener {
		void onItemClick(int position);
	}
}
